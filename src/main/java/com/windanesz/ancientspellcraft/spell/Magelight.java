package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.registry.ASBlocks;
import com.windanesz.ancientspellcraft.registry.ASItems;
import com.windanesz.ancientspellcraft.registry.ASPotions;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.spell.SpellBuff;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class Magelight extends SpellBuff {

	public Magelight() {
		super(AncientSpellcraft.MODID, "magelight", 216, 26, 11, () -> ASPotions.magelight);
		soundValues(0.7f, 1.2f, 0.4f);
	}

	/**
	 * <b>Overriding as we don't want to spawn particles.</b>
	 */
	protected boolean applyEffects(EntityLivingBase caster, SpellModifiers modifiers) {
		// magelight and candlelight is mutually exclusive
		if (caster.isPotionActive(ASPotions.candlelight)) {
			caster.removePotionEffect(ASPotions.candlelight);
		}

		for (Potion potion : potionSet) {
			caster.addPotionEffect(new PotionEffect(potion, potion.isInstant() ? 1 :
					(int) (getProperty(getDurationKey(potion)).floatValue() * modifiers.get(WizardryItems.duration_upgrade)),
					(int) getProperty(getStrengthKey(potion)).floatValue(),
					false, false));
		}

		if (caster instanceof EntityPlayer && !caster.world.isRemote && caster.world.isAirBlock(caster.getPosition().up())) {
			caster.world.setBlockState(caster.getPosition().up(), ASBlocks.MAGELIGHT.getDefaultState());
		}
		return true;
	}

	@Override
	protected void spawnParticles(World world, EntityLivingBase entity, SpellModifiers modifiers) {
		//noop
	}

	/**
	 * Returns the number to be added to the potion amplifier(s) based on the given potency modifier. Override
	 * to define custom modifier handling. Delegates to {@link SpellBuff#getStandardBonusAmplifier(float)} by
	 * default.
	 * <b>Maximum level of this buff is 0</>
	 */
	protected int getBonusAmplifier(float potencyModifier) {
		return 0;
	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == ASItems.ancient_spell_book || item == ASItems.ancient_spellcraft_scroll;
	}
}
