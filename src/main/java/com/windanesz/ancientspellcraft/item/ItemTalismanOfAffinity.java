package com.windanesz.ancientspellcraft.item;

import electroblob.wizardry.item.ItemCrystal;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

/** A crystal-configurable item which biases looted spell books toward that crystal's element. */
public class ItemTalismanOfAffinity extends AbstractItemArtefactWithSlots {

	public ItemTalismanOfAffinity(EnumRarity rarity, Type type) {
		super(rarity, type, 1, 1, true);

		this.addPropertyOverride(new ResourceLocation("element"), new IItemPropertyGetter() {
			@Override
			@SideOnly(Side.CLIENT)
			public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
				ItemStack crystal = getItemForSlot(stack, 0);
				return crystal.getItem() instanceof ItemCrystal ? crystal.getMetadata() * 0.1f : 0;
			}
		});
	}

	@Override
	public boolean isItemStackValid(ItemStack stack) {
		return stack.getItem() instanceof ItemCrystal && stack.getMetadata() != 0;
	}

	@Override
	public boolean isItemValid(Item item) {
		return item instanceof ItemCrystal;
	}
}
