package com.windanesz.ancientspellcraft.mixin;

import com.windanesz.ancientspellcraft.ritual.WarlockAttunement;
import com.windanesz.ancientspellcraft.spell.AbsorbArtefact;
import com.windanesz.ancientspellcraft.util.WizardArmourUtils;
import electroblob.wizardry.data.WizardData;
import electroblob.wizardry.item.ItemArtefact;
import electroblob.wizardry.item.ItemWizardArmour;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Optional;

@Mixin(ItemArtefact.class)
public class ItemArtefactMixins {

	@Inject(method = "getActiveArtefacts", at = @At("RETURN"))
	private static void modifyItemList(EntityPlayer player, ItemArtefact.Type[] types, CallbackInfoReturnable<List<Item>> cir) {
		// Add item to the list
		Optional<Item> optional = AbsorbArtefact.getArtefact(WizardData.get(player));
		if (optional.isPresent()) {
			if (WarlockAttunement.isWarlockAttuned(player) || WizardArmourUtils.isWearingFullSet(player, null, ItemWizardArmour.ArmourClass.WARLOCK)) {
				cir.getReturnValue().add(optional.get());
			}
		}
	}

	@Inject(method = "isArtefactActive", at = @At("RETURN"), cancellable = true)
	private static void modifyArtefactActiveStatus(EntityPlayer player, Item artefact, CallbackInfoReturnable<Boolean> cir) {
		Optional<Item> optional = AbsorbArtefact.getArtefact(WizardData.get(player));
		if (optional.isPresent() && optional.get() == artefact) {
			if (WarlockAttunement.isWarlockAttuned(player) || WizardArmourUtils.isWearingFullSet(player, null, ItemWizardArmour.ArmourClass.WARLOCK)) {
				cir.setReturnValue(true);
			}
		}
	}
}