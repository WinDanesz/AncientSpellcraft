package com.windanesz.ancientspellcraft.item;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftTabs;
import electroblob.wizardry.Wizardry;
import electroblob.wizardry.constants.Element;
import electroblob.wizardry.data.SpellGlyphData;
import electroblob.wizardry.item.ItemSpellBook;
import electroblob.wizardry.item.ItemWizardArmour;
import electroblob.wizardry.spell.Spell;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

import java.util.List;

abstract class ItemArmourClassSpellHolder extends ItemSpellBook {

	private static final ResourceLocation SAGE_GUI = new ResourceLocation(AncientSpellcraft.MODID, "textures/gui/todo_sage_gui.png");
	private static final ResourceLocation WARLOCK_GUI =  new ResourceLocation(AncientSpellcraft.MODID, "textures/gui/todo_warlock_gui.png");

	public ItemArmourClassSpellHolder(){
		setHasSubtypes(true);
		setMaxStackSize(16);
		setCreativeTab(AncientSpellcraftTabs.CLASS_SPELLS);
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> list){

		if(tab == AncientSpellcraftTabs.CLASS_SPELLS){

			List<Spell> spells = Spell.getAllSpells();
			spells.removeIf(s -> !s.applicableForItem(this));

			for(Spell spell : spells){
				list.add(new ItemStack(this, 1, spell.metadata()));
			}
		}
	}

	public abstract ResourceLocation getGuiTexture(Spell spell);

	@Override
	public abstract ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand);

	public abstract ItemWizardArmour.ArmourClass getArmourClass();

	// overriding the display tag of Element.MAGIC from none to Arcane
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack itemstack, World world, List<String> tooltip, net.minecraft.client.util.ITooltipFlag advanced) {
		// Tooltip is left blank for wizards buying generic spell books.
		if (world != null && itemstack.getItemDamage() != OreDictionary.WILDCARD_VALUE) {

			Spell spell = Spell.byMetadata(itemstack.getItemDamage());

			boolean discovered = Wizardry.proxy.shouldDisplayDiscovered(spell, itemstack);

			// armour class name
			tooltip.add(I18n.format("gui.ancientspellcraft:" + getArmourClass().name().toLowerCase() + "_spell"));

			// Element colour is not given for undiscovered spells
			tooltip.add(discovered ? "\u00A77" + spell.getDisplayNameWithFormatting()
					: "#\u00A79" + SpellGlyphData.getGlyphName(spell, world));

			tooltip.add(spell.getTier().getDisplayNameWithFormatting());

			// Advanced tooltips display more information, mainly for searching purposes in creative
			if (discovered && advanced.isAdvanced()) { // No cheating!
				String elementTooltip = (spell.getElement() == Element.MAGIC) ? I18n.format("gui.ancientspellcraft:element.magic") : spell.getElement().getDisplayName();

				if (getArmourClass() == ItemWizardArmour.ArmourClass.SAGE) {
					tooltip.add(elementTooltip);
				}
				tooltip.add(spell.getType().getDisplayName());
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public net.minecraft.client.gui.FontRenderer getFontRenderer(ItemStack stack){
		return Wizardry.proxy.getFontRenderer(stack);
	}

}
