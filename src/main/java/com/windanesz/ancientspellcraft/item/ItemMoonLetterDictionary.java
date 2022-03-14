package com.windanesz.ancientspellcraft.item;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public class ItemMoonLetterDictionary extends ItemASArtefact {

	public ItemMoonLetterDictionary(EnumRarity rarity, Type type) {
		super(rarity, type);
		addReadinessPropertyOverride();
	}

	public void addReadinessPropertyOverride() {
		this.addPropertyOverride(new ResourceLocation("fullmoon"), new IItemPropertyGetter() {
			@SideOnly(Side.CLIENT)
			public float apply(ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entity) {
				if (world != null && entity != null) {
					return isFullMoon(world) ? 1f : 0f;
				} else {
					return 0f;
				}
			}
		});
	}

	public static boolean isFullMoon(World world) {
		return world.provider.getMoonPhase(world.getWorldTime()) == 0;
	}
}
