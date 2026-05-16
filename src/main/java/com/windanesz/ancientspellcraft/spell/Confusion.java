package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.registry.ASItems;
import electroblob.wizardry.Wizardry;
import electroblob.wizardry.constants.Element;
import electroblob.wizardry.item.ItemWizardArmour;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.spell.SpellRay;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.InventoryUtils;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.ParticleBuilder.Type;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Collections;
import java.util.List;

public class Confusion extends SpellRay implements IClassSpell {

	public Confusion() {
		super(AncientSpellcraft.MODID, "confusion", SpellActions.POINT, false);
	}

	// The following three methods serve as a good example of how to implement continuous spell sounds (hint: it's easy)

	@Override
	protected boolean onEntityHit(World world, Entity target, Vec3d hit, EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {
		if (!world.isRemote) {
			if (target instanceof EntityPlayer) {
				EntityPlayer player = (EntityPlayer) target;
				// Get the player's hotbar items
				if (!player.getHeldItemOffhand().isEmpty()) {
					ItemStack copy = player.getHeldItemMainhand().copy();
					player.setHeldItem(EnumHand.MAIN_HAND, player.getHeldItemOffhand());
					player.setHeldItem(EnumHand.OFF_HAND, copy);
				}

				List<ItemStack> hotbar = InventoryUtils.getHotbar(player);

				// Shuffle the hotbar items
				Collections.shuffle(hotbar, world.rand);
				// Update the player's hotbar with the shuffled items
				for (int i = 0; i < hotbar.size(); i++) {
					player.inventory.setInventorySlotContents(i, hotbar.get(i));
				}
				player.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 40, 3));
			} else if (target instanceof EntityCreature) {
				EntityCreature creature = (EntityCreature) target;
				List<EntityLivingBase> entities = EntityUtils.getEntitiesWithinRadius(10, target.posX, target.posY, target.posZ, world, EntityLivingBase.class);
				entities.removeIf(e -> e == caster || e == creature);
				if (!entities.isEmpty()) {
					creature.setRevengeTarget(entities.get(world.rand.nextInt(entities.size())));
				}
			}
		}
		return true;
	}

	@Override
	protected boolean onBlockHit(World world, BlockPos pos, EnumFacing side, Vec3d hit, EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {
		return false;
	}

	@Override
	protected boolean onMiss(World world, EntityLivingBase caster, Vec3d origin, Vec3d direction, int ticksInUse, SpellModifiers modifiers) {
		return true;
	}

	@Override
	protected void spawnParticleRay(World world, Vec3d origin, Vec3d direction, EntityLivingBase caster, double distance) {

		if (caster != null) {
			ParticleBuilder.create(Type.BEAM).entity(caster).pos(origin.subtract(caster.getPositionVector()))
					.length(distance).clr(WarlockElementalSpellEffects.PARTICLE_COLOURS.get(Element.MAGIC)[0])
					.scale(MathHelper.sin(caster.ticksExisted * 0.2f) * 0.1f + 1.4f).spawn(world);
		} else {
			ParticleBuilder.create(Type.BEAM).pos(origin).target(origin.add(direction.scale(distance)))
					.clr(WarlockElementalSpellEffects.PARTICLE_COLOURS.get(Element.MAGIC)[0])
					.scale(MathHelper.sin(Wizardry.proxy.getThePlayer().ticksExisted * 0.2f) * 0.1f + 1.4f).spawn(world);
		}
	}

	@Override
	public ItemWizardArmour.ArmourClass getArmourClass() {
		return ItemWizardArmour.ArmourClass.WARLOCK;
	}

	public boolean applicableForItem(Item item) {
		return item == ASItems.forbidden_tome;
	}

	@Override
	public boolean canBeCastBy(TileEntityDispenser dispenser) {
		return false;
	}

	@Override
	public boolean canBeCastBy(EntityLiving npc, boolean override) {
		return canBeCastByClassNPC(npc);
	}
}
