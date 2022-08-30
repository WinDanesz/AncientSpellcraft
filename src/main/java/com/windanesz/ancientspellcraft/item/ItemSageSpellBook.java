package com.windanesz.ancientspellcraft.item;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import electroblob.wizardry.Wizardry;
import electroblob.wizardry.WizardryGuiHandler;
import electroblob.wizardry.item.ItemWizardArmour;
import electroblob.wizardry.spell.Spell;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class ItemSageSpellBook extends ItemArmourClassSpellHolder {

	private static final ResourceLocation guiTexture = new ResourceLocation(AncientSpellcraft.MODID, "textures/gui/spell_book_ancient_spellcraft.png");

	@Override
	public ResourceLocation getGuiTexture(Spell spell) { return guiTexture;	}


	// This is accessed during loading (before we even get to the main menu) for search tree population
	// Obviously the world is always null at that point, because no world objects exist! However, outside of a world
	// there are no guarantees as to spell metadata order so we just have to give up (and we can't account for discovery)
	// TODO: Search trees seem to get reloaded when the mappings change so in theory this should work ok, why doesn't it?
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack itemstack, World world, List<String> tooltip, net.minecraft.client.util.ITooltipFlag advanced){
		super.addInformation(itemstack,world,tooltip, advanced);

		if (GameSettings.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindSneak)) {
			AncientSpellcraft.proxy.addMultiLineDescription(tooltip, I18n.format("tooltip.ancientspellcraft:mystic_spell_book.more_info"));
		} else {
			tooltip.add(I18n.format("tooltip.ancientspellcraft:more_info"));
		}


//		if(world == null) world = Wizardry.proxy.getTheWorld(); // But... I need the world!
//
//		// Tooltip is left blank for wizards buying generic spell books.
//		if(world != null && itemstack.getItemDamage() != OreDictionary.WILDCARD_VALUE){
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
//			EntityPlayer player = Wizardry.proxy.getThePlayer();
//
//			// If the spell should *appear* discovered but isn't *actually* discovered, show a 'new spell' message
//			// A bit annoying to check this again but it's the easiest way
//			if(Wizardry.settings.discoveryMode && !player.isCreative() && discovered && WizardData.get(player) != null && !WizardData.get(player).hasSpellBeenDiscovered(spell)){
//				tooltip.add(Wizardry.proxy.translate("item." + this.getRegistryName() + ".new", new Style().setColor(TextFormatting.LIGHT_PURPLE)));
//			}
//
//			// Advanced tooltips display more information, mainly for searching purposes in creative
//			if (discovered && advanced.isAdvanced()) { // No cheating!
//				String elementTooltip = (spell.getElement() == Element.MAGIC) ? I18n.format("gui.ancientspellcraft:sage_spell") : spell.getElement().getDisplayName();
//				tooltip.add(elementTooltip);
//				tooltip.add(spell.getType().getDisplayName());
//			}
//		}
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand){
		ItemStack stack = player.getHeldItem(hand);
		player.openGui(Wizardry.instance, WizardryGuiHandler.SPELL_BOOK, world, 0, 0, 0);
		return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
	}

	@Override
	public ItemWizardArmour.ArmourClass getArmourClass() { return ItemWizardArmour.ArmourClass.SAGE; }
}
