package com.windanesz.ancientspellcraft.item;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.registry.ASItems;
import com.windanesz.ancientspellcraft.registry.ASTabs;
import com.windanesz.ancientspellcraft.spell.Experiment;
import com.windanesz.ancientspellcraft.util.ASUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

@Mod.EventBusSubscriber
public class ItemTheoryScroll extends Item {

	public ItemTheoryScroll() {
		super();
		setCreativeTab(ASTabs.ANCIENTSPELLCRAFT);
		setMaxStackSize(1);
	}

	@Override
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.RARE;
	}
	/**
	 * Called when the equipped item is right clicked.
	 */
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack itemstack = player.getHeldItem(hand);

		if (player.isSneaking()) {

			if (isEmpty(itemstack)) {
				int points = Experiment.getTheoryPoints(player);
				if (points < 1) {
					ASUtils.sendMessage(player, "item.ancientspellcraft:empty_theory_scroll.no_points", false);
					return new ActionResult<>(EnumActionResult.FAIL, itemstack);
				} else {
					if (!world.isRemote) {
						ASUtils.giveStackToPlayer(player, new ItemStack(ASItems.theory_scroll));
						itemstack.shrink(1);
						Experiment.consumeTheoryPoint(player, 1);
						ASUtils.sendMessage(player, "item.ancientspellcraft:theory_scroll.created", false);
						player.setActiveHand(hand);
						player.getCooldownTracker().setCooldown(ASItems.empty_theory_scroll, 60);
						player.getCooldownTracker().setCooldown(ASItems.theory_scroll, 60);
					}
				}
			} else {
				if (!world.isRemote) {
					itemstack.shrink(1);
					Experiment.addTheoryPoint(player, 1);
					ASUtils.sendMessage(player, "item.ancientspellcraft:theory_scroll.used", false);
					player.setActiveHand(hand);
					player.getCooldownTracker().setCooldown(ASItems.empty_theory_scroll, 60);
					player.getCooldownTracker().setCooldown(ASItems.theory_scroll, 60);
				}
			}
		}

		return new ActionResult<>(EnumActionResult.SUCCESS, itemstack);
	}

	public static boolean isEmpty(ItemStack scroll) {
		return scroll.getItem().getRegistryName() == ASItems.empty_theory_scroll.getRegistryName();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, net.minecraft.client.util.ITooltipFlag flag) {
		AncientSpellcraft.proxy.addMultiLineDescription(tooltip, "item." + this.getRegistryName() + ".desc");
	}
}
