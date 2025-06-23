package com.windanesz.ancientspellcraft.item;

import com.windanesz.ancientspellcraft.Settings;
import com.windanesz.ancientspellcraft.util.ASUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import java.util.HashMap;
import java.util.Map;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraft.util.ResourceLocation;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;

public class ItemVerdantCrucible extends ItemDailyArtefact {
    // New structure for ingredient entries
    public static class IngredientEntry {
        public final String itemName;
        public final int meta;
        public final NBTTagCompound nbt;
        public final int min;
        public final int max;
        public IngredientEntry(String itemName, int meta, NBTTagCompound nbt, int min, int max) {
            this.itemName = itemName;
            this.meta = meta;
            this.nbt = nbt;
            this.min = min;
            this.max = max;
        }
    }
    public static final java.util.List<IngredientEntry> ingredientEntries = new java.util.ArrayList<>();

    static {
        ingredientEntries.clear();
        String[] configList = Settings.generalSettings.verdant_crucible_ingredients;
        if (configList != null) {
            for (String entry : configList) {
                String[] parts = entry.split("\\|");
                if (parts.length >= 5) {
                    String itemName = parts[0];
                    try {
                        int meta = Integer.parseInt(parts[1]);
                        NBTTagCompound nbt = null;
                        if (!parts[2].isEmpty()) {
                            try {
                                nbt = JsonToNBT.getTagFromJson(parts[2]);
                            } catch (NBTException ignored) {}
                        }
                        int min = Integer.parseInt(parts[3]);
                        int max = Integer.parseInt(parts[4]);
                        ingredientEntries.add(new IngredientEntry(itemName, meta, nbt, min, max));
                    } catch (NumberFormatException ignored) {}
                }
            }
        }
    }

    public ItemVerdantCrucible(EnumRarity rarity) {
        super(rarity);
        addReadinessPropertyOverride();
    }

    @Override
    public void performAction(EntityPlayer player) {
        if (!player.world.isRemote) {
            for (IngredientEntry entry : ingredientEntries) {
                int count = ASUtils.randIntBetween(entry.min, entry.max);
                Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(entry.itemName));
                if (item != null) {
                    ItemStack stack = new ItemStack(item, count, entry.meta);
                    if (entry.nbt != null) stack.setTagCompound(entry.nbt.copy());
                    if (!player.inventory.addItemStackToInventory(stack)) {
                        player.dropItem(stack, false);
                    }
                }
            }
        }
    }
} 