package com.windanesz.ancientspellcraft.item;

import electroblob.wizardry.constants.Tier;
import electroblob.wizardry.item.ItemWand;
import electroblob.wizardry.registry.WizardrySounds;
import electroblob.wizardry.util.WandHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class ItemKnowledgeScroll extends ItemRareScroll {

	public ItemKnowledgeScroll() {
		super();
	}

	/**
	 * Called when the equipped item is right clicked.
	 */
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);
		EnumHand otherHand = hand == EnumHand.MAIN_HAND ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;
		ItemStack otherStack = player.getHeldItem(otherHand);

		if (otherStack.getItem() instanceof ItemWand) {

			Tier tier = ((ItemWand) otherStack.getItem()).tier;
			int currentProgression = WandHelper.getProgression(otherStack);
			int minProgressionForNextTier = tier.next().getProgression();

			if (!world.isRemote) {
				if (currentProgression <= minProgressionForNextTier && tier != Tier.MASTER) {
					int boost = ((int) ((minProgressionForNextTier - currentProgression) * 0.33));
					WandHelper.addProgression(otherStack, boost);
					consumeScroll(player, hand);
					player.playSound(WizardrySounds.MISC_DISCOVER_SPELL, 1.25F, 1.0F);
				}
			}

			return new ActionResult<>(EnumActionResult.SUCCESS, stack);
		}

		return new ActionResult<>(EnumActionResult.PASS, stack);
	}

}
