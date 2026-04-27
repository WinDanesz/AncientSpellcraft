package com.windanesz.ancientspellcraft.mixin.minecraft;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.functions.EnchantRandomly;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;



/**
 * Fixes enchant_randomly loot function applying enchantments that are not allowed on books
 * (e.g. temporary imbuement enchantments) to enchanted books.
 */
@Mixin(EnchantRandomly.class)
public class MixinEnchantRandomly {

	@Shadow
	@Final
	private java.util.List<Enchantment> enchantments;

	@Inject(method = "apply", at = @At("HEAD"), cancellable = true)
	private void filterDisallowedBookEnchantments(ItemStack stack, java.util.Random rand, LootContext context, CallbackInfoReturnable<ItemStack> cir) {
		if (this.enchantments.isEmpty() && stack.getItem() == Items.BOOK) {
			java.util.List<Enchantment> list = new java.util.ArrayList<>();

			for (Enchantment enchantment : Enchantment.REGISTRY) {
				if (enchantment.isAllowedOnBooks()) {
					list.add(enchantment);
				}
			}

			if (list.isEmpty()) {
				cir.setReturnValue(stack);
				return;
			}

			Enchantment enchantment = list.get(rand.nextInt(list.size()));
			int i = MathHelper.getInt(rand, enchantment.getMinLevel(), enchantment.getMaxLevel());
			stack = new ItemStack(Items.ENCHANTED_BOOK);
			net.minecraft.item.ItemEnchantedBook.addEnchantment(stack, new net.minecraft.enchantment.EnchantmentData(enchantment, i));
			cir.setReturnValue(stack);
		}
	}
}
