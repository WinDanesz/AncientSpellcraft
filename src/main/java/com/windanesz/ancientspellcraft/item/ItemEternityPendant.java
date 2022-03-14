package com.windanesz.ancientspellcraft.item;

import com.windanesz.ancientspellcraft.registry.ASItems;
import com.windanesz.ancientspellcraft.registry.ASSpells;
import com.windanesz.ancientspellcraft.spell.MetaSpellBuff;
import electroblob.wizardry.Wizardry;
import electroblob.wizardry.constants.SpellType;
import electroblob.wizardry.data.SpellGlyphData;
import electroblob.wizardry.item.IWorkbenchItem;
import electroblob.wizardry.registry.Spells;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.spell.SpellBuff;
import electroblob.wizardry.util.SpellProperties;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class ItemEternityPendant extends ItemASArtefact implements IWorkbenchItem {

	public ItemEternityPendant(EnumRarity rarity, Type type) {
		super(rarity, type);
	}

	@Override
	public int getSpellSlotCount(ItemStack stack) {
		return 1;
	}

	@Override
	public boolean onApplyButtonPressed(EntityPlayer player, Slot centre, Slot crystals, Slot upgrade, Slot[] spellBooks) {

		if (!spellBooks[0].getStack().isEmpty()) {

			Spell spell = Spell.byMetadata(spellBooks[0].getStack().getItemDamage());

			// only buff type spells can be applied to the pendant
			if ((spell.getType() == SpellType.BUFF || spell instanceof SpellBuff) && !(spell instanceof MetaSpellBuff) && spell != ASSpells.time_knot) {

				// Spells can only be bound to the artefact if the player has already cast them
				// This restriction does not apply in creative mode
				if (spell != Spells.none && player.isCreative() || (spell.isEnabled(SpellProperties.Context.WANDS))) {
					centre.putStack(new ItemStack(ASItems.amulet_pendant_of_eternity, 1, spell.metadata()));

					// consumes the book
					spellBooks[0].putStack(ItemStack.EMPTY);
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean showTooltip(ItemStack stack) {
		return true;
	}

	@Override
	public boolean canPlace(ItemStack stack) {
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack itemstack, World world, List<String> tooltip, net.minecraft.client.util.ITooltipFlag advanced) {
		Wizardry.proxy.addMultiLineDescription(tooltip, "item." + this.getRegistryName() + ".desc");

		if (world != null) {

			Spell spell = Spell.byMetadata(itemstack.getItemDamage());

			boolean discovered = Wizardry.proxy.shouldDisplayDiscovered(spell, itemstack);

			if (spell == Spells.none) {

//				tooltip.add("\u00A7d" + net.minecraft.client.resources.I18n.format("item." + Wizardry.MODID + ":wizard_armour.legendary"));

				tooltip.add(net.minecraft.client.resources.I18n.format("item." + this.getRegistryName() + ".bound_spell.none"));
			} else {
				// Advanced tooltips display more information, mainly for searching purposes in creative
				if (discovered) { // No cheating!
					String displayName = "\u00A77" + spell.getDisplayNameWithFormatting();
					tooltip.add(net.minecraft.client.resources.I18n.format("item." + this.getRegistryName() + ".bound_spell.present"));
					tooltip.add(net.minecraft.client.resources.I18n.format("item." + this.getRegistryName() + ".bound_spell_info", displayName));
				} else {
					String displayName = "#\u00A79" + SpellGlyphData.getGlyphName(spell, world);
					tooltip.add(net.minecraft.client.resources.I18n.format("item." + this.getRegistryName() + ".bound_spell_info", displayName));
				}
			}
		}
	}

	//	@Override
	//	@SideOnly(Side.CLIENT)
	//	public void addInformation(ItemStack itemstack, World world, List<String> tooltip, net.minecraft.client.util.ITooltipFlag advanced) {
	//		// Tooltip is left blank for wizards buying generic spell books.
	//		if (world != null && itemstack.getItemDamage() != OreDictionary.WILDCARD_VALUE) {
	//
	//			Spell spell = Spell.byMetadata(itemstack.getItemDamage());
	//
	//			boolean discovered = Wizardry.proxy.shouldDisplayDiscovered(spell, itemstack);
	//
	//			// Element colour is not given for undiscovered spells
	//			tooltip.add(discovered ? "\u00A77" + spell.getDisplayNameWithFormatting()
	//					: "#\u00A79" + SpellGlyphData.getGlyphName(spell, world));
	//
	//			tooltip.add(spell.getTier().getDisplayNameWithFormatting());
	//
	//			// Advanced tooltips display more information, mainly for searching purposes in creative
	//			if (discovered && advanced.isAdvanced()) { // No cheating!
	//				tooltip.add(spell.getElement().getDisplayName());
	//				tooltip.add(spell.getType().getDisplayName());
	//			}
	//		}
	//	}
}
