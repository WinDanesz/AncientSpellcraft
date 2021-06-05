package com.windanesz.ancientspellcraft.spell;

import com.google.common.base.Predicate;
import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.item.ItemNewArtefact;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftItems;
import electroblob.wizardry.data.IStoredVariable;
import electroblob.wizardry.data.Persistence;
import electroblob.wizardry.data.WizardData;
import electroblob.wizardry.entity.living.EntitySpiritHorse;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.BlockUtils;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = AncientSpellcraft.MODID)
public class HorseWhistle extends Spell {

	public static final IStoredVariable<UUID> LAST_HORSE_UUID_KEY = IStoredVariable.StoredVariable.ofUUID("lastHorseUUID", Persistence.ALWAYS);

	private static final Predicate<Entity> HORSE = new Predicate<Entity>() {
		public boolean apply(@Nullable Entity entity) {
			return entity instanceof AbstractHorse && !entity.isBeingRidden();
		}
	};

	public HorseWhistle() {
		super(AncientSpellcraft.MODID, "horse_whistle", SpellActions.POINT_UP, false);
		this.soundValues(1, 1.4f, 0.4f);
		addProperties(RANGE);
		WizardData.registerStoredVariables(LAST_HORSE_UUID_KEY);

	}

	@Override
	public boolean cast(World world, EntityPlayer caster, EnumHand hand, int ticksInUse, SpellModifiers modifiers) {

		if (caster.isRiding() && caster.getRidingEntity() instanceof EntityHorse && ItemNewArtefact.isNewArtefactActive(caster, AncientSpellcraftItems.belt_horse)) {

			EntityHorse horse = (EntityHorse) caster.getRidingEntity();
			horse.addPotionEffect(new PotionEffect(MobEffects.SPEED, 600, 0));

			if (world.isRemote) {
				for (int i = 0; i < 4; i++) {
					double x = horse.posX + world.rand.nextDouble() * 2 - 1;
					double y = horse.posY + caster.getEyeHeight() - 0.5 + world.rand.nextDouble();
					double z = horse.posZ + world.rand.nextDouble() * 2 - 1;
					ParticleBuilder.create(ParticleBuilder.Type.SPARKLE).pos(x, y, z).vel(0, 0.1, 0).clr(MobEffects.SPEED.getLiquidColor()).spawn(world);
				}

				ParticleBuilder.create(ParticleBuilder.Type.BUFF).entity(horse).clr(MobEffects.SPEED.getLiquidColor()).spawn(world);
			}

			return true;

		} else {
			this.playSound(world, caster, ticksInUse, -1, modifiers);

			if (!world.isRemote) {

				WizardData data = WizardData.get(caster);

				Entity oldHorse = EntityUtils.getEntityByUUID(world, data.getVariable(LAST_HORSE_UUID_KEY));

				if (oldHorse != null) {
					callHorse(caster, (EntityHorse) oldHorse);
					return true;
				} else {

					AbstractHorse abstracthorse = this.getClosestHorse(caster, world, 100);

					//				if (abstracthorse != null && abstracthorse.getOwnerUniqueId() == caster.getUniqueID()) {
					//					callHorse(caster, (EntityHorse) abstracthorse);
					//					return true;
					//				}

					double radius = 100;

					List<AbstractHorse> horses = EntityUtils.getEntitiesWithinRadius(radius, caster.posX, caster.posY, caster.posZ, world, AbstractHorse.class);
					for (AbstractHorse horse : horses) {

						if (horse.getOwnerUniqueId() == caster.getUniqueID()) {
							callHorse(caster, (EntityHorse) horse);

							data.setVariable(LAST_HORSE_UUID_KEY, horse.getUniqueID());
							return true;
						}
					}
				}
			}

			return true;
		}
	}

	private void callHorse(EntityPlayer caster, EntityHorse horse) {
		if (!horse.isBeingRidden()) {
			if (horse.getDistance(caster) > 20) {
				tryTeleportToOwner(horse, caster);
			}
				horse.getNavigator().clearPath();
				horse.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(100.0D);
				horse.getNavigator().tryMoveToXYZ(caster.posX, caster.posY, caster.posZ, 1.7);
		}
	}

	@Nullable
	protected AbstractHorse getClosestHorse(Entity entityIn, World world, double distance) {
		double d0 = Double.MAX_VALUE;
		Entity entity = null;

		for (Entity entity1 : world.getEntitiesInAABBexcluding(entityIn, entityIn.getEntityBoundingBox().expand(distance, distance, distance), HORSE)) {
			double d1 = entity1.getDistanceSq(entityIn.posX, entityIn.posY, entityIn.posZ);

			if (d1 < d0) {
				entity = entity1;
				d0 = d1;
			}
		}

		return (AbstractHorse) entity;
	}

	@Override
	public boolean canBeCastBy(EntityLiving npc, boolean override) {
		return false;
	}

	@Override
	public boolean canBeCastBy(TileEntityDispenser dispenser) {
		return false;
	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == AncientSpellcraftItems.ancient_spellcraft_spell_book || item == AncientSpellcraftItems.ancient_spellcraft_scroll;
	}

	@SubscribeEvent
	public static void onEntityMountEvent(EntityMountEvent event) {
		if (event.getEntityMounting() instanceof EntityPlayer && (event.getEntityBeingMounted() instanceof EntityHorse && !(event.getEntityBeingMounted() instanceof EntitySpiritHorse))) {

			EntityPlayer player = (EntityPlayer) event.getEntityMounting();

			WizardData data = WizardData.get(player);

			if (!player.world.isRemote) {
				data.setVariable(LAST_HORSE_UUID_KEY, event.getEntityBeingMounted().getUniqueID());
			}
		}
	}

	private static void tryTeleportToOwner(EntityHorse horse, EntityPlayer owner) {
		if (!horse.getLeashed() && !horse.isRiding()) {
			BlockPos randomNearbyPos = BlockUtils.findNearbyFloorSpace(owner, 10, 3);
			int i = MathHelper.floor(randomNearbyPos.getX()) - 2;
			int j = MathHelper.floor(randomNearbyPos.getZ()) - 2;
			int k = MathHelper.floor(randomNearbyPos.getY());
			for (int l = 0; l <= 4; ++l) {
				for (int i1 = 0; i1 <= 4; ++i1) {
					if ((l < 1 || i1 < 1 || l > 3 || i1 > 3) && isTeleportFriendlyBlock(i, j, k, l, i1, horse)) {
						horse.setLocationAndAngles((double) ((float) (i + l) + 0.5F), (double) k, (double) ((float) (j + i1) + 0.5F), horse.rotationYaw, horse.rotationPitch);
					}
				}
			}
		}
	}

	private static boolean isTeleportFriendlyBlock(int x, int z, int y, int xOffset, int zOffset, EntityHorse horse) {
		BlockPos blockpos = new BlockPos(x + xOffset, y - 1, z + zOffset);
		IBlockState iblockstate = horse.world.getBlockState(blockpos);
		return iblockstate.getBlockFaceShape(horse.world, blockpos, EnumFacing.DOWN) == BlockFaceShape.SOLID && iblockstate.canEntitySpawn(horse) && horse.world.isAirBlock(blockpos.up()) && horse.world.isAirBlock(blockpos.up(2));
	}

}
