package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.block.ITemporaryBlock;
import com.windanesz.ancientspellcraft.entity.living.EntityEvilClassWizard;
import com.windanesz.ancientspellcraft.registry.ASBlocks;
import com.windanesz.ancientspellcraft.registry.ASItems;
import electroblob.wizardry.client.DrawingUtils;
import electroblob.wizardry.entity.construct.EntityBlizzard;
import electroblob.wizardry.entity.construct.EntityStormcloud;
import electroblob.wizardry.item.ItemWizardArmour;
import electroblob.wizardry.registry.WizardryBlocks;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.spell.SpellConstructRanged;
import electroblob.wizardry.util.BlockUtils;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

public class TernaryStorm extends SpellConstructRanged<EntityStormcloud> implements IClassSpell {

	public TernaryStorm() {
		super(AncientSpellcraft.MODID, "ternary_storm", EntityStormcloud::new, false);
		this.addProperties(DAMAGE, EFFECT_RADIUS);
		this.floor(true);
	}

	@Override
	public boolean cast(World world, EntityPlayer caster, EnumHand hand, int ticksInUse, SpellModifiers modifiers) {
		boolean f = super.cast(world, caster, hand, ticksInUse, modifiers);
		//if (!requiresFloor) {
			Vec3d look = caster.getLookVec();
			double range = getProperty(RANGE).doubleValue() * modifiers.get(WizardryItems.range_upgrade);

			double x = caster.posX + look.x * range;
			double y = caster.posY + caster.getEyeHeight() + look.y * range + 2;
			double z = caster.posZ + look.z * range;

			if (f && world.isRemote) {

				for (int i = 0; i < 100; i++) {
					float r = world.rand.nextFloat();
					double speed = 0.02 / r * (1 + world.rand.nextDouble());//(world.rand.nextBoolean() ? 1 : -1) * (0.05 + 0.02 * world.rand.nextDouble());
					ParticleBuilder.create(ParticleBuilder.Type.MAGIC_FIRE)
							.pos(x, y + world.rand.nextDouble() * 3, z)
							.vel(0, 0, 0)
							.scale(2)
							.time(40 + world.rand.nextInt(10))
							.spin(world.rand.nextDouble() * (4 - 0.5) + 0.5, speed)
							.spawn(world);
				}

				for (int i = 0; i < 60; i++) {
					float r = world.rand.nextFloat();
					double speed = 0.02 / r * (1 + world.rand.nextDouble());//(world.rand.nextBoolean() ? 1 : -1) * (0.05 + 0.02 * world.rand.nextDouble());
					ParticleBuilder.create(ParticleBuilder.Type.CLOUD)
							.pos(x, y + world.rand.nextDouble() * 2.5, z)
							.clr(DrawingUtils.mix(DrawingUtils.mix(0xffbe00, 0xff3600, r / 0.6f), 0x222222, (r - 0.6f) / 0.4f))
							.spin(r * (4 - 1) + 0.5, speed)
							.spawn(world);
				}

			}

		//}
		return f;
	}

	@Override
	protected void addConstructExtras(EntityStormcloud construct, EnumFacing side, @Nullable EntityLivingBase caster, SpellModifiers modifiers) {
		EntityBlizzard blizzard = new EntityBlizzard(construct.world);
		if (caster != null) { blizzard.setCaster(caster); }
		blizzard.setPosition(construct.posX, construct.posY, construct.posZ);
		blizzard.lifetime = 40;
		blizzard.setSizeMultiplier(modifiers.get(WizardryItems.blast_upgrade));
		construct.world.spawnEntity(blizzard);

		BlockPos pos = construct.getPosition();
		World world = construct.world;

		BlockPos finalPos = pos.down();
		List<BlockPos> list = BlockUtils.getBlockSphere(pos, modifiers.get(WizardryItems.blast_upgrade) * getProperty(EFFECT_RADIUS).intValue()).stream().filter(i -> !world.isAirBlock(i)).filter(i -> i.getY() == finalPos.getY()).collect(Collectors.toList());
		int blockLifetime = getProperty(DURATION).intValue();

		for (BlockPos currPos : list) {
			boolean magma = world.rand.nextBoolean();

			if (magma) {
				if (world.rand.nextBoolean() && world.isAirBlock(currPos.up())) {
					world.setBlockState(currPos.up(), Blocks.FIRE.getDefaultState(), 11);
				}
				ITemporaryBlock.placeTemporaryBlock(caster, world, ASBlocks.CONJURED_MAGMA, currPos, blockLifetime);
			} else {
				currPos = currPos.up();
				if (world.getBlockState(currPos.down()).isSideSolid(world, currPos.down(), EnumFacing.UP) && BlockUtils.canBlockBeReplaced(world, currPos)) {
					if (BlockUtils.canPlaceBlock(caster, world, currPos)) {
						world.setBlockState(currPos, WizardryBlocks.permafrost.getDefaultState());
						world.scheduleUpdate(currPos.toImmutable(), WizardryBlocks.permafrost, blockLifetime);
					}
				}
			}
		}

		construct.posY += 5;
	}

	@Override
	public boolean canBeCastBy(EntityLiving npc, boolean override) {
		return npc instanceof EntityEvilClassWizard && ((EntityEvilClassWizard) npc).getArmourClass() == ItemWizardArmour.ArmourClass.SAGE;
	}

	@Override
	public ItemWizardArmour.ArmourClass getArmourClass() { return ItemWizardArmour.ArmourClass.SAGE; }

	@Override
	public boolean applicableForItem(Item item) {
		return item == ASItems.mystic_spell_book || item == WizardryItems.scroll;
	}
}
