package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.registry.WizardryPotions;
import electroblob.wizardry.spell.Intimidate;
import electroblob.wizardry.spell.SpellBuff;
import electroblob.wizardry.spell.SpellRay;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.INpc;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class TurnUndead extends SpellRay {

	public TurnUndead() {
		super(AncientSpellcraft.MODID, "turn_undead", SpellActions.POINT_UP, false);
		addProperties(EFFECT_DURATION, EFFECT_STRENGTH);
	}

	@Override
	protected boolean onEntityHit(World world, Entity target, Vec3d hit,
			@Nullable EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {

		if (!(target instanceof EntityLivingBase)) { return false; }

		EntityLivingBase livingTarget = (EntityLivingBase) target;
		if ((livingTarget.isEntityUndead() || ((EntityLivingBase) target).isPotionActive(WizardryPotions.curse_of_undeath)) &&
				livingTarget.isNonBoss() && !(target instanceof INpc)) {

			EntityLivingBase targetEntity = (EntityLivingBase) target;
			int bonusAmplifier = SpellBuff.getStandardBonusAmplifier(modifiers.get(SpellModifiers.POTENCY));

			NBTTagCompound entityNBT = targetEntity.getEntityData();
			if (entityNBT != null) { entityNBT.setUniqueId(Intimidate.NBT_KEY, caster.getUniqueID()); }

			targetEntity.addPotionEffect(new PotionEffect(WizardryPotions.fear,
					(int) (getProperty(EFFECT_DURATION).floatValue() * modifiers.get(WizardryItems.duration_upgrade)),
					getProperty(EFFECT_STRENGTH).intValue() + bonusAmplifier));
			return true;
		}
		return false;
	}

	@Override
	protected boolean onBlockHit(World world, BlockPos pos, EnumFacing side, Vec3d hit,
			@Nullable EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {
		return false;
	}

	@Override
	protected boolean onMiss(World world, @Nullable EntityLivingBase caster, Vec3d origin, Vec3d direction, int ticksInUse, SpellModifiers modifiers) {
		return false;
	}

	@Override
	public boolean canBeCastBy(EntityLiving npc, boolean override) { return true; }

	@Override
	public boolean canBeCastBy(TileEntityDispenser dispenser) { return true; }

	@Override
	protected void spawnParticleRay(World world, Vec3d origin, Vec3d direction, @Nullable EntityLivingBase caster, double distance) {
		if (caster != null) { origin = caster.getPositionEyes(1); }
		for (int i = 0; i < 30; i++) {
			double x = origin.x - 1 + world.rand.nextDouble() * 2;
			double y = origin.y - 0.25 + world.rand.nextDouble() * 0.5;
			double z = origin.z - 1 + world.rand.nextDouble() * 2;
			ParticleBuilder.create(ParticleBuilder.Type.SPARKLE).pos(x, y, z).clr(1, 1, 0.3f).spawn(world);
		}
	}
}
