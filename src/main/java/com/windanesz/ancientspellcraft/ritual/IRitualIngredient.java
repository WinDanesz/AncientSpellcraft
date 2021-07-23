package com.windanesz.ancientspellcraft.ritual;

import com.windanesz.ancientspellcraft.tileentity.TileRune;
import electroblob.wizardry.util.EntityUtils;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public interface IRitualIngredient {

	List<List<ItemStack>> getRequiredIngredients();

	default List<EntityItem> getActualIngredients(World world, TileRune centerPiece, int searchRadius) {
		List<EntityItem> ingredients = new ArrayList<>();
		List<EntityItem> entityItemList = EntityUtils.getEntitiesWithinRadius(searchRadius, centerPiece.getPos().getX(), centerPiece.getPos().getY(), centerPiece.getPos().getZ(), world, EntityItem.class);

		if (!entityItemList.isEmpty()) {
			List<List<ItemStack>> ingredientList = getRequiredIngredients();

			for (EntityItem entityItem : entityItemList) {
				boolean matches = false;
				for (List<ItemStack> ingredient : ingredientList) {
					for (ItemStack stack : ingredient) {
						if (entityItem.getItem().isItemEqualIgnoreDurability(stack)) {
							matches = true;
							ingredients.add(entityItem);
							break;
						}
					}
				}
				if (!matches) {
					return new ArrayList<>();
				}
			}
		}

		return ingredients;
	}

	default boolean shouldConsumeIngredients() {
		return false;
	}

	//	default boolean areIngredientsPresent(World world, EntityPlayer caster, TileRune centerPiece) {
	//		List<EntityItem> itemList = EntityUtils.getEntitiesWithinRadius(1, centerPiece.getPos().getX(), centerPiece.getPos().getY(), centerPiece.getPos().getZ(), centerPiece.getWorld(), EntityItem.class);
	//		if (!itemList.isEmpty()) {
	//			NBTTagCompound compound = new NBTTagCompound();
	//			for (EntityItem entityItem : itemList) {
	//				//				Item item = entityItem.getItem().getItem();
	//
	//				if (entityItem.getItem().getItem() == Item.getItemFromBlock(Blocks.SAPLING)) {
	//					compound.setInteger("wood_type", entityItem.getItem().getMetadata());
	//					centerPiece.setRitualData(compound);
	//					centerPiece.markDirty();
	//				}
	//			}
	//		}
	//	}
}



