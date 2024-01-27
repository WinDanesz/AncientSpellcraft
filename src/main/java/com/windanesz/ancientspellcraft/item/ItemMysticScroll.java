package com.windanesz.ancientspellcraft.item;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.registry.ASTabs;
import electroblob.wizardry.Wizardry;
import electroblob.wizardry.item.ItemScroll;
import electroblob.wizardry.spell.Spell;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Iterator;
import java.util.List;

public class ItemMysticScroll extends ItemScroll implements IWizardClassWeapon {

	public ItemMysticScroll() {
		super();
		setCreativeTab(ASTabs.CLASS_SPELLS);
	}

	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack itemstack, World world, List<String> tooltip, ITooltipFlag advanced) {
		if (world != null) {
			Spell spell = Spell.byMetadata(itemstack.getItemDamage());
			boolean discovered = Wizardry.proxy.shouldDisplayDiscovered(spell, itemstack);
			if (discovered && advanced.isAdvanced()) {
				tooltip.add(spell.getTier().getDisplayName());
				tooltip.add(AncientSpellcraft.proxy.translate("gui.ancientspellcraft:element.sage"));
				tooltip.add(spell.getType().getDisplayName());
			}

			if (advanced.isAdvanced() && this.getRegistryName().toString().equals("ebwizardry:scroll") && !spell.getRegistryName().getNamespace().equals("ebwizardry")) {
				String modId = spell.getRegistryName().getNamespace();
				String name = (new Style()).setColor(TextFormatting.BLUE).setItalic(true).getFormattingCode() + ((ModContainer) Loader.instance().getIndexedModList().get(modId)).getMetadata().name;
				tooltip.add(name);
			}
		}

	}


	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> list) {
		if (tab == ASTabs.CLASS_SPELLS) {
			List<Spell> spells = Spell.getAllSpells();
			spells.removeIf((s) -> {
				return !s.applicableForItem(this);
			});
			Iterator var4 = spells.iterator();

			while(var4.hasNext()) {
				Spell spell = (Spell)var4.next();
				list.add(new ItemStack(this, 1, spell.metadata()));
			}
		}

	}

}
