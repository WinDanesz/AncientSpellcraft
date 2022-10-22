package com.windanesz.ancientspellcraft.crafting.recipe;

import com.google.gson.JsonObject;
import com.windanesz.ancientspellcraft.util.RecipeUtil;
import electroblob.wizardry.spell.Spell;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IRecipeFactory;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.oredict.ShapedOreRecipe;

import javax.annotation.Nullable;

/**
 * A shaped recipe class that creates a spell book from a crafting recipe by specifying the spell's name as nbt data
 * <p>
 *
 * @author WinDanesz
 */
@SuppressWarnings("unused")
public class ShapedSpellBookRecipe extends ShapedOreRecipe {
	public ShapedSpellBookRecipe(@Nullable final ResourceLocation group, final ItemStack result, final CraftingHelper.ShapedPrimer primer) {
		super(group, result, primer);
	}

	@Override
	public ItemStack getCraftingResult(final InventoryCrafting inv) {
		final ItemStack output = super.getCraftingResult(inv); // Get the default output

		if (!output.isEmpty()) {
			if (output.hasTagCompound()) {
				NBTTagCompound compound = output.getTagCompound();
				if (compound.hasKey("spell")) {
					String spellName = compound.getString("spell");
					Spell spell = Spell.get(spellName);
					if (spell != null) {
						output.setTagCompound(null);
						output.setItemDamage(spell.metadata());
					}
				}
			}
		}

		return output; // Return the modified output
	}

	@Override
	public String getGroup() {
		return group == null ? "" : group.toString();
	}

	public static class Factory implements IRecipeFactory {

		@Override
		public IRecipe parse(final JsonContext context, final JsonObject json) {
			final String group = JsonUtils.getString(json, "group", "");
			final CraftingHelper.ShapedPrimer primer = RecipeUtil.parseShaped(context, json);
			final ItemStack result = CraftingHelper.getItemStack(JsonUtils.getJsonObject(json, "result"), context);

			return new ShapedSpellBookRecipe(group.isEmpty() ? null : new ResourceLocation(group), result, primer);
		}
	}
}