package com.windanesz.ancientspellcraft.mixin.ebwizardry;

import electroblob.wizardry.item.ItemArtefact;
import electroblob.wizardry.registry.WizardryItems;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraftforge.registries.IForgeRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static electroblob.wizardry.registry.WizardryItems.registerItem;

@Mixin(WizardryItems.class)
public class MixinWizardryItems {


	@Inject(method = "registerItem(Lnet/minecraftforge/registries/IForgeRegistry;Ljava/lang/String;Lnet/minecraft/item/Item;)V", at = @At("HEAD"), remap = false, cancellable = true)
	private static void replaceCharmUndeadHelmets(IForgeRegistry<Item> registry, String name, Item item, CallbackInfo ci) {
		if (name.equals("charm_undead_helmets")) {
			// Cancel the original registration
			ci.cancel();
			// Register our replacement
			registerItem(registry, name, new ItemArtefact(EnumRarity.RARE, ItemArtefact.Type.HEAD), false);
		}
	}
}