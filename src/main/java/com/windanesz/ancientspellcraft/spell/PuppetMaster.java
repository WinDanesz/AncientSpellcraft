package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.registry.ASItems;
import electroblob.wizardry.entity.living.ISummonedCreature;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.spell.SpellRay;
import electroblob.wizardry.util.AllyDesignationSystem;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class PuppetMaster extends SpellRay {

	public PuppetMaster() {
		super(AncientSpellcraft.MODID, "puppet_master", SpellActions.POINT, false);
		this.soundValues(1, 1.2f, 0.2f);
	}

	@Override
	protected boolean onEntityHit(World world, Entity target, Vec3d hit,
			@Nullable EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {
		if (target instanceof EntityLivingBase) {

			boolean commanded = false;

			// can't send all the minions against an allied player
			if (target instanceof EntityPlayer && AllyDesignationSystem.isAllied(caster, (EntityPlayer) target)) {
				return false;
			}

			List<EntityLiving> minions = getAllMinionsInRadius(caster, caster.getPosition(), world, modifiers);

			for (EntityLivingBase minion : minions) {

				if (minion == target)
					continue;

				if (!world.isRemote)
					((EntityLiving) minion).setAttackTarget((EntityLivingBase) target);
				commanded = true;
			}

			return commanded;
		}
		return false;
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
	protected boolean onBlockHit(World world, BlockPos pos, EnumFacing side, Vec3d hit, EntityLivingBase caster, Vec3d origin,
			int ticksInUse, SpellModifiers modifiers) {

		boolean commanded = false;

		List<EntityLiving> minions = getAllMinionsInRadius(caster, caster.getPosition(), world, modifiers);

		boolean negativeOffset = false;

		int index = 0;


		for (EntityLivingBase minion : minions) {
			if (!world.isRemote) {

				// if sneak-commanded, minions form a line formation
				if (caster.isSneaking()) {
					pos = pos.offset(caster.getHorizontalFacing().rotateY(), +(negativeOffset ? (-1 * index) : index));

				// a bit hacky but formations are almost impossible with collision
				((EntityLiving) minion).entityCollisionReduction = 1;

				}


				((EntityLiving) minion).setAttackTarget(null);
				((EntityLiving) minion).getNavigator().clearPath();
				((EntityLiving) minion).getNavigator().tryMoveToXYZ(pos.getX(), pos.getY(), pos.getZ(), 1);

				negativeOffset = !negativeOffset;
				index++;
			}
			commanded = true;
		}
		return commanded;
	}

	@Override
	protected boolean onMiss(World world, @Nullable EntityLivingBase caster, Vec3d origin, Vec3d direction, int ticksInUse, SpellModifiers modifiers) {
		if (caster != null && caster.isSneaking()) {

			boolean commanded = false;

			List<EntityLiving> minions = getAllMinionsInRadius(caster, caster.getPosition(), world, modifiers);
			for (EntityLivingBase minion : minions) {
				if (!world.isRemote) {
					((EntityLiving) minion).setAttackTarget(null);
					((EntityLiving) minion).getNavigator().tryMoveToEntityLiving(caster, 1.1);
				}
				commanded = true;
			}
			return commanded;
		}
		return false;
	}

	private List<EntityLiving> getAllMinionsInRadius(EntityLivingBase owner, BlockPos pos, World world, SpellModifiers modifiers) {
		double radius = getProperty(RANGE).floatValue() * modifiers.get(WizardryItems.range_upgrade);
		List<EntityLiving> entities = EntityUtils.getEntitiesWithinRadius(radius, pos.getX(), pos.getY(), pos.getZ(), world, EntityLiving.class);
		List<EntityLiving> minions = new ArrayList<>();
		for (EntityLiving entity : entities) {
			if (entity instanceof ISummonedCreature && ((ISummonedCreature) entity).getOwnerId() == owner.getUniqueID()) {
				minions.add(entity);
			}

		}
		return minions;

	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == ASItems.ancient_spellcraft_spell_book || item == ASItems.ancient_spellcraft_scroll;
	}

}