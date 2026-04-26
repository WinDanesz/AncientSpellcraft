package com.windanesz.ancientspellcraft.mixin.minecraft;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Items;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Fixes villager librarian trades offering enchanted books for enchantments that are not allowed on books
 * (e.g. temporary imbuement enchantments).
 */
@Mixin(EntityVillager.ListEnchantedBookForEmeralds.class)
public class MixinListEnchantedBookForEmeralds {

	@Inject(method = "addMerchantRecipe", at = @At("HEAD"), cancellable = true)
	private void filterDisallowedBookEnchantments(IMerchant merchant, MerchantRecipeList recipeList, Random random, CallbackInfo ci) {
		List<Enchantment> validEnchantments = new ArrayList<>();

		for (Enchantment enchantment : Enchantment.REGISTRY) {
			if (enchantment.isAllowedOnBooks()) {
				validEnchantments.add(enchantment);
			}
		}

		if (validEnchantments.isEmpty()) {
			ci.cancel();
			return;
		}

		Enchantment enchantment = validEnchantments.get(random.nextInt(validEnchantments.size()));
		int i = MathHelper.getInt(random, enchantment.getMinLevel(), enchantment.getMaxLevel());
		ItemStack itemstack = ItemEnchantedBook.getEnchantedItemStack(new EnchantmentData(enchantment, i));
		int j = 2 + random.nextInt(5 + i * 10) + 3 * i;

		if (enchantment.isTreasureEnchantment()) {
			j *= 2;
		}

		if (j > 64) {
			j = 64;
		}

		recipeList.add(new MerchantRecipe(new ItemStack(Items.BOOK), new ItemStack(Items.EMERALD, j), itemstack));
		ci.cancel();
	}
}
