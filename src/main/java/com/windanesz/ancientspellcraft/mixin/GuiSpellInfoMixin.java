package com.windanesz.ancientspellcraft.mixin;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.registry.ASItems;
import com.windanesz.ancientspellcraft.spell.IClassSpell;
import electroblob.wizardry.client.gui.GuiSpellInfo;
import electroblob.wizardry.constants.Element;
import electroblob.wizardry.item.ItemWizardArmour;
import electroblob.wizardry.spell.Spell;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GuiSpellInfo.class)
public abstract class GuiSpellInfoMixin {

	@Shadow
	public abstract Spell getSpell();

	@Redirect(
			method = "drawForegroundLayer",
			at = @At(value = "INVOKE",
					target = "Lelectroblob/wizardry/constants/Element;getDisplayName()Ljava/lang/String;"))
	private String redirectFormatElement(Element instance) {
		if (getSpell() instanceof IClassSpell) {
			if (((IClassSpell) getSpell()).getArmourClass() == ItemWizardArmour.ArmourClass.WARLOCK) {
				Style colour = new Style().setColor(TextFormatting.RED);
				return colour.getFormattingCode() + AncientSpellcraft.proxy.translate("gui.ancientspellcraft:element.warlock");
			} else if (((IClassSpell) getSpell()).getArmourClass() == ItemWizardArmour.ArmourClass.SAGE) {
				Style colour = new Style().setColor(TextFormatting.DARK_AQUA);
				return colour.getFormattingCode() + AncientSpellcraft.proxy.translate("gui.ancientspellcraft:element.sage");
			} else if (((IClassSpell) getSpell()).getArmourClass() == ItemWizardArmour.ArmourClass.BATTLEMAGE) {
				//Style colour = new Style().setColor(TextFormatting.DARK_AQUA);
				return AncientSpellcraft.proxy.translate("gui.ancientspellcraft:element.battlemage");
			}
		} else if (getSpell().getElement() == Element.MAGIC && getSpell().applicableForItem(ASItems.ancient_spell_book)) {
			Style colour = new Style().setColor(TextFormatting.GOLD);
			return colour.getFormattingCode() + AncientSpellcraft.proxy.translate("gui.ancientspellcraft:element.ancient");
		}
		return instance.getDisplayName();
	}
}

