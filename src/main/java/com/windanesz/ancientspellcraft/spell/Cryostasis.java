package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftBlocks;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftItems;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.registry.WizardryPotions;
import electroblob.wizardry.spell.SpellBuff;
import electroblob.wizardry.util.AllyDesignationSystem;
import electroblob.wizardry.util.MagicDamage;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.SpellModifiers;
import electroblob.wizardry.util.WizardryUtilities;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class Cryostasis extends SpellBuff {
	public static final String ICE_DURATION = "ice_duration";

	public Cryostasis() {
		super(AncientSpellcraft.MODID, "cryostasis", 11, 215, 222, () -> WizardryPotions.frost, () -> MobEffects.REGENERATION, () -> MobEffects.ABSORPTION);
		soundValues(1.0f, 1f, 0f);
		addProperties(ICE_DURATION, EFFECT_DURATION, EFFECT_STRENGTH);
	}

	@Override
	protected boolean applyEffects(EntityLivingBase caster, SpellModifiers modifiers) {
		super.applyEffects(caster, modifiers);

		if (!caster.world.isRemote && WizardryUtilities.canDamageBlocks(caster, caster.world)) {
			if (caster.isBurning()) {
				caster.extinguish();
			}

			caster.setPositionAndUpdate((caster.getPosition().getX() + 0.5), (int) caster.getPosition().getY(), (int) caster.getPosition().getZ() + 0.5);
			BlockPos pos = caster.getPosition();
			for (EntityLivingBase currentTarget : WizardryUtilities.getEntitiesWithinRadius(4, caster.posX, caster.posY, caster.posZ, caster.world)) {
				if (AllyDesignationSystem.isValidTarget(caster, currentTarget) && !MagicDamage.isEntityImmune(MagicDamage.DamageType.FROST, currentTarget)) {

					WizardryUtilities.applyStandardKnockback(caster, currentTarget);
					WizardryUtilities.applyStandardKnockback(caster, currentTarget);

					currentTarget.addPotionEffect(new PotionEffect(WizardryPotions.frost, (int) (getProperty(EFFECT_DURATION).floatValue() * modifiers.get(WizardryItems.duration_upgrade)),
							getProperty(EFFECT_STRENGTH).intValue() + SpellBuff.getStandardBonusAmplifier(modifiers.get(SpellModifiers.POTENCY))));
				}
			}
			for (BlockPos currPos :
					BlockPos.getAllInBox(pos.offset(EnumFacing.SOUTH).offset(EnumFacing.EAST).offset(EnumFacing.DOWN), pos.offset(EnumFacing.UP, 2).offset(EnumFacing.NORTH).offset(EnumFacing.WEST))) {

				if (currPos == caster.getPosition() || currPos == caster.getPosition().offset(EnumFacing.UP)) {
					continue;
				}
				int duration = (int) (getProperty(EFFECT_DURATION).floatValue() * modifiers.get(WizardryItems.duration_upgrade));

				if (WizardryUtilities.canBlockBeReplaced(caster.world, currPos)) {
					caster.world.setBlockState(currPos, AncientSpellcraftBlocks.HARD_FROSTED_ICE.getDefaultState());
					caster.world.scheduleUpdate(currPos.toImmutable(), AncientSpellcraftBlocks.HARD_FROSTED_ICE, duration);
				}
			}
			caster.world.setBlockToAir(caster.getPosition());
			caster.world.setBlockToAir(caster.getPosition().offset(EnumFacing.UP));

			// return some hunger points for players
			if (caster instanceof EntityPlayer && ((EntityPlayer) caster).getFoodStats().needFood()) {
				((EntityPlayer) caster).getFoodStats().addStats(5, 0.1f);
			}
		}

		return true;
	}

	@Override
	protected void spawnParticles(World world, EntityLivingBase caster, SpellModifiers modifiers) {

		//		super.spawnParticles(world, caster, modifiers);

		if (world.isRemote) {

			float particleCount = 10;
			for (int i = 0; i < 2; i++) {
				double x = caster.posX + world.rand.nextDouble() * 2 - 1;
				double y = caster.getEntityBoundingBox().minY + caster.getEyeHeight() - 1 + world.rand.nextDouble();
				double z = caster.posZ + world.rand.nextDouble() * 2 - 1;

				ParticleBuilder.create(ParticleBuilder.Type.SPHERE)
						.pos(caster.posX, caster.getEntityBoundingBox().minY + 0.1, caster.posZ)
						.scale(2f)
						.clr(117, 255, 250)
						.spawn(world);
			}
		}
	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == AncientSpellcraftItems.ancient_spellcraft_spell_book || item == AncientSpellcraftItems.ancient_spellcraft_scroll;
	}
}
