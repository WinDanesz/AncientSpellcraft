package com.windanesz.ancientspellcraft.mixin;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.Settings;
import electroblob.wizardry.item.ItemWizardArmour;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ItemWizardArmour.class)
public class ItemWizardArmourMixin {

	@Inject(method = "addInformation", at = @At("RETURN"))
	private void modifyTooltip(ItemStack stack, World world, List<String> tooltip, ITooltipFlag advanced, CallbackInfo ci) {
		if (Settings.generalSettings.warlock_bonus_override) {
			if (((ItemWizardArmour) (Object) this).armourClass == ItemWizardArmour.ArmourClass.WARLOCK) {
				// Replace the last line of tooltip with the new content
				tooltip.set(tooltip.size() - 1, AncientSpellcraft.proxy.translate("item." + AncientSpellcraft.MODID + ":warlock_armour.full_set_bonus", new Style().setColor(TextFormatting.AQUA), (int) (Settings.generalSettings.warlock_bonus_potency_amount * 100)));
			}
		}
	}

	@Inject(method = "onLivingEquipmentChangeEvent", at = @At("HEAD"), cancellable = true)
	private static void onLivingEquipmentChangeEvent(LivingEquipmentChangeEvent event, CallbackInfo info) {
		if (Settings.generalSettings.warlock_bonus_override) {
			info.cancel();
		}
	}

	@Inject(method = "applySpellModifiers", at = @At("TAIL"))
	private void onApplySpellModifiers(EntityLivingBase caster, Spell spell, SpellModifiers modifiers, CallbackInfo ci) {
		if (Settings.generalSettings.warlock_bonus_override) {
			// Full set bonuses
			ItemWizardArmour thiz = ((ItemWizardArmour) (Object) this);
			if (thiz.armorType == EntityEquipmentSlot.HEAD && ItemWizardArmour.isWearingFullSet(caster, thiz.element, thiz.armourClass) && ItemWizardArmour.doAllArmourPiecesHaveMana(caster)) {
				if (thiz.armourClass == ItemWizardArmour.ArmourClass.WARLOCK && spell.getElement() != thiz.element) {
					modifiers.set(SpellModifiers.POTENCY, modifiers.get(SpellModifiers.POTENCY) + Settings.generalSettings.warlock_bonus_potency_amount, false);
				}
			}
		}
	}

}
