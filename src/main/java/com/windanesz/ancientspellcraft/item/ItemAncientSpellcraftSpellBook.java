package com.windanesz.ancientspellcraft.item;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.client.gui.GuiHandlerAS;
import electroblob.wizardry.Wizardry;
import electroblob.wizardry.WizardryGuiHandler;
import electroblob.wizardry.constants.Element;
import electroblob.wizardry.data.SpellGlyphData;
import electroblob.wizardry.item.ItemSpellBook;
import electroblob.wizardry.spell.Spell;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

import java.util.List;

public class ItemAncientSpellcraftSpellBook extends ItemSpellBook {

	private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(AncientSpellcraft.MODID, "textures/gui/spell_book_ancient_spellcraft.png");

	@Override
	public ResourceLocation getGuiTexture(Spell spell) {
		return GUI_TEXTURE;
	}

	@Override
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.UNCOMMON;
	}

	// overriding the display tag of Element.MAGIC from none to Arcane
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack itemstack, World world, List<String> tooltip, net.minecraft.client.util.ITooltipFlag advanced) {
		// Tooltip is left blank for wizards buying generic spell books.
		if (world != null && itemstack.getItemDamage() != OreDictionary.WILDCARD_VALUE) {

			Spell spell = Spell.byMetadata(itemstack.getItemDamage());

			boolean discovered = Wizardry.proxy.shouldDisplayDiscovered(spell, itemstack);

			// Element colour is not given for undiscovered spells
			// Ancient spells have gold color
			String name = spell.getElement() == Element.MAGIC ? (TextFormatting.GOLD + I18n.format("spell." + spell.getUnlocalisedName()))
					: ("\u00A77" + spell.getDisplayNameWithFormatting());

			tooltip.add(discovered ? name : "#\u00A79" + SpellGlyphData.getGlyphName(spell, world));

			tooltip.add(spell.getTier().getDisplayNameWithFormatting());

			// Advanced tooltips display more information, mainly for searching purposes in creative
			if (discovered && advanced.isAdvanced()) { // No cheating!
				String elementTooltip = (spell.getElement() == Element.MAGIC) ? I18n.format("gui.ancientspellcraft:element.magic") : spell.getElement().getDisplayName();
				tooltip.add(elementTooltip);
				tooltip.add(spell.getType().getDisplayName());
			}
		}
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);
		Spell spell = Spell.byMetadata(stack.getItemDamage());
		if (spell.getElement() == Element.MAGIC) {
			player.openGui(AncientSpellcraft.instance, GuiHandlerAS.SPELL_BOOK_ANCIENT, world, 0, 0, 0);
			return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
		} else {
			player.openGui(Wizardry.instance, WizardryGuiHandler.SPELL_BOOK, world, 0, 0, 0);
		}
		return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
	}
}
