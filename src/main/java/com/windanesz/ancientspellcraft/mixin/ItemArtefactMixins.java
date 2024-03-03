package com.windanesz.ancientspellcraft.mixin;

import com.windanesz.ancientspellcraft.spell.AbsorbArtefact;
import electroblob.wizardry.data.WizardData;
import electroblob.wizardry.item.ItemArtefact;
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
		// Add your item to the list
		Optional<Item> optional = AbsorbArtefact.getArtefact(WizardData.get(player));
		optional.ifPresent(itemArtefact -> cir.getReturnValue().add(itemArtefact));
	}

	@Inject(method = "isArtefactActive", at = @At("RETURN"), cancellable = true)
	private static void modifyArtefactActiveStatus(EntityPlayer player, Item artefact, CallbackInfoReturnable<Boolean> cir) {
		// Add your item to the list
		Optional<Item> optional = AbsorbArtefact.getArtefact(WizardData.get(player));
		if (optional.isPresent() && optional.get() == artefact) {
			cir.setReturnValue(true);
		}
	}
}