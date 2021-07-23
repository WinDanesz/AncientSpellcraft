package com.windanesz.ancientspellcraft.client;

import com.windanesz.ancientspellcraft.registry.AncientSpellcraftItems;
import electroblob.wizardry.Wizardry;
import electroblob.wizardry.client.DrawingUtils;
import electroblob.wizardry.data.WizardData;
import electroblob.wizardry.item.ItemScroll;
import electroblob.wizardry.item.ItemSpellBook;
import electroblob.wizardry.spell.Spell;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMerchant;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerMerchant;
import net.minecraft.inventory.Slot;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraftforge.client.event.GuiContainerEvent;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Unfortunately the AS spell books suffer from the same issue as the default spell books in the game, when they are being bought by wizards, having the offered
 * crystals not getting rendered in the gui. Also adds some more things..
 * Shamelessly taken from {@link electroblob.wizardry.client.gui.WizardTradeTweaksHandler} to fix the same issue
 *
 * @Author Electroblob, WinDanesz
 */

@EventBusSubscriber(Side.CLIENT)
public class ASWizardTradeTweaksHandler {

	private static int tradeIndex; // Mirrors GuiMerchant#selectedMerchantRecipe (don't want to reflect into it every frame)

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onGuiOpenEvent(GuiOpenEvent event) {
		if (event.getGui() instanceof GuiMerchant)
			tradeIndex = 0;
	}

	@SubscribeEvent
	public static void onActionPerformedPostEvent(ActionPerformedEvent.Post event) {

		if (event.getGui() instanceof GuiMerchant) {

			MerchantRecipeList recipes = ((GuiMerchant) event.getGui()).getMerchant().getRecipes(Minecraft.getMinecraft().player);

			if (recipes == null)
				return;

			if (event.getButton().id == 1) { // Next
				tradeIndex = Math.min(tradeIndex + 1, recipes.size());
			} else if (event.getButton().id == 2) { // Previous
				tradeIndex = Math.max(tradeIndex - 1, 0);
			}
		}
	}

	// Brute-force fix for crystals not showing up when a wizard is given a spell book in the trade GUI.
	@SubscribeEvent
	public static void onGuiDrawForegroundEvent(GuiContainerEvent.DrawForeground event) {

		if (event.getGuiContainer() instanceof GuiMerchant) {

			GuiMerchant gui = (GuiMerchant) event.getGuiContainer();
			// Note that gui.getMerchant() returns an NpcMerchant, not an EntityWizard.
			MerchantRecipeList trades = gui.getMerchant().getRecipes(Minecraft.getMinecraft().player);

			if (trades == null)
				return;

			// Using == the specific item rather than instanceof because that's how trades do it.
			if (gui.inventorySlots.getSlot(0).getStack().getItem() == AncientSpellcraftItems.ancient_spell_book
					|| gui.inventorySlots.getSlot(1).getStack().getItem() == AncientSpellcraftItems.ancient_spell_book
					|| gui.inventorySlots.getSlot(0).getStack().getItem() == AncientSpellcraftItems.ancient_spellcraft_spell_book
					|| gui.inventorySlots.getSlot(1).getStack().getItem() == AncientSpellcraftItems.ancient_spellcraft_spell_book
					|| gui.inventorySlots.getSlot(0).getStack().getItem() == AncientSpellcraftItems.ritual_book
					|| gui.inventorySlots.getSlot(1).getStack().getItem() == AncientSpellcraftItems.ritual_book
					|| gui.inventorySlots.getSlot(0).getStack().getItem() instanceof ItemScroll
					|| gui.inventorySlots.getSlot(1).getStack().getItem() instanceof ItemScroll
			) {

				for (MerchantRecipe trade : trades) {
					if ((trade.getItemToBuy().getItem() == AncientSpellcraftItems.ancient_spell_book || trade.getItemToBuy().getItem() == AncientSpellcraftItems.ancient_spellcraft_spell_book
							|| trade.getItemToBuy().getItem() == AncientSpellcraftItems.ritual_book || trade.getItemToBuy().getItem() instanceof ItemScroll)
							&& trade.getSecondItemToBuy().isEmpty()) {
						Slot slot = gui.inventorySlots.getSlot(2);
						// It still doesn't look quite right because the slot highlight is behind the item, but it'll do
						// until/unless I find a better solution.
						DrawingUtils.drawItemAndTooltip(gui, trade.getItemToSell(), slot.xPos, slot.yPos, event.getMouseX(), event.getMouseY(),
								gui.getSlotUnderMouse() == slot);
					}
				}
			}

			// New spell indicator
			if (gui.inventorySlots instanceof ContainerMerchant) {

				// TODO: this might just covers a syncing issue, where tradeIndex was out of bounds
				if (trades.size() <= tradeIndex) {
					return;
				}

				// Can't use getCurrentRecipe because that only gets updated when the correct items are given
				MerchantRecipe recipe = trades.get(tradeIndex);

				if (recipe != null && recipe.getItemToSell().getItem() instanceof ItemSpellBook) {

					EntityPlayer player = Minecraft.getMinecraft().player;
					Spell spell = Spell.byMetadata(recipe.getItemToSell().getMetadata());

					if (Wizardry.settings.discoveryMode && !player.isCreative() && Wizardry.proxy.shouldDisplayDiscovered(spell, recipe.getItemToSell())
							&& WizardData.get(player) != null && !WizardData.get(player).hasSpellBeenDiscovered(spell)) {
					}
				}
			}
		}
	}

}
