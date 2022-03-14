package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.integration.artemislib.ASArtemisLibIntegration;
import com.windanesz.ancientspellcraft.registry.ASItems;
import com.windanesz.ancientspellcraft.registry.ASPotions;
import electroblob.wizardry.item.ItemArtefact;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import java.util.function.Supplier;

public class SpellResizeSelf extends SpellBuffAS {

	public SpellResizeSelf(String name, float r, float g, float b, Supplier<Potion>... effects) {
		super(name, r, g, b, effects);

		if (!ASArtemisLibIntegration.enabled()) {
			this.setEnabled(false);
		}
	}

	@Override
	public boolean cast(World world, EntityPlayer caster, EnumHand hand, int ticksInUse, SpellModifiers modifiers) {
		if (ASArtemisLibIntegration.enabled()) {
			return super.cast(world, caster, hand, ticksInUse, modifiers);
		} else {
			if (!world.isRemote)
				caster.sendStatusMessage(new TextComponentTranslation("tooltip.ancientspellcraft:missing_artemislib.disabled_spell"), false);
			return false;
		}
	}

	@Override
	public boolean cast(World world, EntityLiving caster, EnumHand hand, int ticksInUse, EntityLivingBase target, SpellModifiers modifiers) {
		return ASArtemisLibIntegration.enabled() && super.cast(world, caster, hand, ticksInUse, target, modifiers);
	}

	@Override
	public boolean cast(World world, double x, double y, double z, EnumFacing direction, int ticksInUse, int duration, SpellModifiers modifiers) {
		return ASArtemisLibIntegration.enabled() && super.cast(world, x, y, z, direction, ticksInUse, duration, modifiers);
	}

	@Override
	protected boolean applyEffects(EntityLivingBase caster, SpellModifiers modifiers) {

		if (caster != null) {
			int modifier = caster.isSneaking() ? 0 : 1;

			for (Potion potion : potionSet) {
				int duration = (int) (getProperty(getDurationKey(potion)).floatValue() * modifiers.get(WizardryItems.duration_upgrade));

				if (caster instanceof EntityPlayer && ((
						potion == ASPotions.shrinkage && ItemArtefact.isArtefactActive((EntityPlayer) caster, ASItems.ring_permanent_shrinkage)) ||
						potion == ASPotions.growth && ItemArtefact.isArtefactActive((EntityPlayer) caster, ASItems.ring_permanent_growth))) {
					duration = Integer.MAX_VALUE;
				}

				caster.addPotionEffect(new PotionEffect(potion, duration, modifier, false, false));
			}
		}

		return true;
	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == ASItems.ancient_spell_book || item == ASItems.ancient_spellcraft_scroll;
	}
}
