package com.windanesz.ancientspellcraft.item;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.registry.ASItems;
import com.windanesz.ancientspellcraft.registry.ASTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ItemRareScroll extends Item {

	public ItemRareScroll() {
		super();
		setCreativeTab(ASTabs.ANCIENTSPELLCRAFT);
		setMaxStackSize(1);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack stack) {
		return true;
	}

	@Override
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.RARE;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, net.minecraft.client.util.ITooltipFlag flag) {
		AncientSpellcraft.proxy.addMultiLineDescription(tooltip, "item.ancientspellcraft:rare_scroll_tooltip");
		AncientSpellcraft.proxy.addMultiLineDescription(tooltip, "item." + this.getRegistryName() + ".desc");
	}

	public static void consumeScroll(EntityPlayer player, ItemStack stack) {
		if (!(ItemManaArtefact.isArtefactActive(player, ASItems.ring_rare_scroll) && player.world.rand.nextFloat() <= 0.15f)) {
			stack.shrink(1);
		}
	}

	public static void consumeScroll(EntityPlayer player, EnumHand hand) {
		if (!(ItemManaArtefact.isArtefactActive(player, ASItems.ring_rare_scroll) && player.world.rand.nextFloat() <= 0.15f)) {
			player.getHeldItem(hand).shrink(1);
		}
	}
}
