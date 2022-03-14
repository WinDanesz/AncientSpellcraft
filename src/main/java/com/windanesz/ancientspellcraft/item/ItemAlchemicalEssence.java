package com.windanesz.ancientspellcraft.item;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.registry.ASTabs;
import com.windanesz.ancientspellcraft.util.ASUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.common.IRarity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ItemAlchemicalEssence extends Item {

	public ItemAlchemicalEssence() {
		setCreativeTab(ASTabs.ANCIENTSPELLCRAFT);
		setMaxStackSize(16);
	}

	@Override
	public IRarity getForgeRarity(ItemStack stack) {
		return EnumRarity.RARE;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer player, EnumHand handIn) {

		if (!worldIn.isRemote) {
			ItemStack essenceStack = player.getHeldItem(handIn);

			if (player.getHeldItemOffhand().getItem() == Items.IRON_INGOT) {
				int maxCount = player.getHeldItemOffhand().getCount();

				int transmutedCount = ASUtils.randIntBetween(2, Math.min(5, maxCount));

				essenceStack.shrink(1);

				// the iron bars
				player.getHeldItemOffhand().shrink(transmutedCount);

				ItemStack goldIngotStack = new ItemStack(Items.GOLD_INGOT, transmutedCount);
				if (!player.inventory.addItemStackToInventory(goldIngotStack)) {
					player.dropItem(goldIngotStack, false);
				}
			}
		}

		return new ActionResult<ItemStack>(EnumActionResult.PASS, player.getHeldItem(handIn));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, net.minecraft.client.util.ITooltipFlag advanced) {
		AncientSpellcraft.proxy.addMultiLineDescription(tooltip, "item." + this.getRegistryName() + ".desc");
	}

}