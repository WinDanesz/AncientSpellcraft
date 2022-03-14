package com.windanesz.ancientspellcraft.item;

import com.windanesz.ancientspellcraft.registry.ASTabs;
import electroblob.wizardry.Wizardry;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Everything what won't fit as a charm, ring or amulet and doesn't needs to be worn!
 * These items/blocks doesn't have checks like electroblob.wizardry.item.ItemArtefact#isArtefactActive, and have special conditions to work
 */
public class ItemNonBaubleArtefact extends Item {
	private final EnumRarity rarity;

	public ItemNonBaubleArtefact(EnumRarity rarity) {
		this.rarity = rarity;
		setCreativeTab(ASTabs.ANCIENTSPELLCRAFT_GEAR);
	}

	@Override
	public EnumRarity getRarity(ItemStack stack){
		return rarity;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack stack){
		return rarity == EnumRarity.EPIC;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, net.minecraft.client.util.ITooltipFlag flagIn){
		Wizardry.proxy.addMultiLineDescription(tooltip, "item." + this.getRegistryName() + ".desc");
	}

}
