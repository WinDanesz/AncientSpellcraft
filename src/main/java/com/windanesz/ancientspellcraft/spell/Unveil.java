package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.registry.ASItems;
import electroblob.wizardry.item.ItemWizardArmour;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.registry.WizardryPotions;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

import java.util.ArrayList;

public class Unveil extends Spell implements IClassSpell {

	public Unveil() {
		super(AncientSpellcraft.MODID, "unveil", SpellActions.POINT_UP, false);
		addProperties(EFFECT_RADIUS);
	}

	@Override
	public boolean cast(World world, EntityPlayer caster, EnumHand hand, int ticksInUse, SpellModifiers modifiers) {
		double radius = getProperty(EFFECT_RADIUS).floatValue() * modifiers.get(WizardryItems.blast_upgrade);

		for (EntityLivingBase entity : EntityUtils.getEntitiesWithinRadius(radius, caster.posX, caster.posY, caster.posZ, world, EntityLivingBase.class)) {
			if (entity == caster) continue;

			if (!entity.getActivePotionEffects().isEmpty()) {

				for (PotionEffect effect : new ArrayList<>(entity.getActivePotionEffects())) { // Get outta here, CMEs
					// The PotionEffect version (as opposed to Potion) does not call cleanup callbacks
					if (effect.getPotion() == MobEffects.INVISIBILITY || effect.getPotion() == WizardryPotions.mirage || effect.getPotion() == WizardryPotions.muffle) {
						entity.removePotionEffect(effect.getPotion());
					}
				}
			}
		}
		return true;
	}

	@Override
	public ItemWizardArmour.ArmourClass getArmourClass() { return ItemWizardArmour.ArmourClass.SAGE; }

	@Override
	public boolean applicableForItem(Item item) {
		return item == ASItems.mystic_spell_book;
	}
}
