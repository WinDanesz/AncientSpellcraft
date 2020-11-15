package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftItems;
import electroblob.wizardry.Wizardry;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.spell.SpellAreaEffect;
import electroblob.wizardry.util.AllyDesignationSystem;
import electroblob.wizardry.util.BlockUtils;
import electroblob.wizardry.util.MagicDamage;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class Conflagration extends SpellAreaEffect {

	public static final String BURN_DURATION = "burn_duration";
	public static final String MAX_DAMAGE = "max_damage";

	public Conflagration() {
		super(AncientSpellcraft.MODID, "conflagration", SpellActions.SUMMON, false);
		addProperties(BURN_DURATION, MAX_DAMAGE);
	}

	@Override
	public boolean cast(World world, EntityPlayer caster, EnumHand hand, int ticksInUse, SpellModifiers modifiers) {
		boolean f = super.cast(world, caster, hand, ticksInUse, modifiers);

		Wizardry.proxy.shakeScreen(caster, 8);

		if (!world.isRemote) {

			float radius = getProperty(EFFECT_RADIUS).floatValue() * modifiers.get(WizardryItems.blast_upgrade);
			List<BlockPos> sphere = BlockUtils.getBlockSphere(caster.getPosition(), radius);

			for (BlockPos currPos : sphere) {
				if (currPos.distanceSq(caster.posX, caster.posY, caster.posZ) < 2) {
					continue;
				}
				if (world.rand.nextInt(4) <= 2) {
					if (world.isAirBlock(currPos.offset(EnumFacing.UP))) {
						world.setBlockState(currPos.offset(EnumFacing.UP), Blocks.FIRE.getDefaultState());
					}
				}
			}
		} else {
			world.spawnParticle(EnumParticleTypes.EXPLOSION_HUGE, caster.posX + 0.5, caster.posY + 0.5, caster.posZ + 0.5, 0, 0, 0);

		}

		return f;
	}

	@Override
	protected boolean affectEntity(World world, Vec3d origin,
			@Nullable EntityLivingBase caster, EntityLivingBase target, int targetCount, int ticksInUse, SpellModifiers modifiers) {
		if (caster instanceof EntityPlayer && AllyDesignationSystem.isAllied((EntityPlayer) caster, target))
			return false;

		if (target instanceof EntityPlayer) {
			Wizardry.proxy.shakeScreen((EntityPlayer) target, 8);
		}

		target.attackEntityFrom(MagicDamage.causeDirectMagicDamage(caster, MagicDamage.DamageType.BLAST),
				// Damage decreases with distance but cannot be less than 0, naturally.
				Math.max(getProperty(MAX_DAMAGE).floatValue() - (float) target.getDistance(caster.posX + 0.5,
						caster.posY + 0.5, caster.posZ + 0.5) * 4, 0) * modifiers.get(SpellModifiers.POTENCY));

		target.setFire(getProperty(BURN_DURATION).intValue());

		double dx = target.posX - caster.posX;
		double dy = target.getEntityBoundingBox().minY + 1 - caster.posY;
		double dz = target.posZ - caster.posZ;

		target.motionX = dx * 0.2;
		target.motionY = dy * 0.1;
		target.motionZ = dz * 0.2;
		return true;
	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == AncientSpellcraftItems.ancient_spellcraft_spell_book || item == AncientSpellcraftItems.ancient_spellcraft_scroll;
	}
}
