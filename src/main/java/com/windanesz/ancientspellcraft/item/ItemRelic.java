package com.windanesz.ancientspellcraft.item;

import com.windanesz.ancientspellcraft.data.Knowledge;
import com.windanesz.ancientspellcraft.data.SpellComponentList;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftTabs;
import electroblob.wizardry.constants.Tier;
import electroblob.wizardry.spell.Spell;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.IRarity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

import static com.windanesz.ancientspellcraft.data.SpellComponentList.lookupBySpell;

public class ItemRelic extends Item {

	private final EnumRarity rarity;

	public ItemRelic(String name, EnumRarity rarity) {
		this.maxStackSize = 1;
		this.setCreativeTab(AncientSpellcraftTabs.ANCIENTSPELLCRAFT);
		this.rarity = rarity;

	}

	@Override
	public EnumRarity getRarity(ItemStack stack) {
		return rarity;
	}

	public static Tier getTier(ItemStack stack) {

		switch (stack.getItem().getRarity(stack)) {
			case COMMON:
				return Tier.NOVICE;
			case UNCOMMON:
				return Tier.APPRENTICE;
			case RARE:
				return Tier.ADVANCED;
			case EPIC:
				return Tier.MASTER;
			default:
				return Tier.NOVICE;
		}
	}

	@Override
	public IRarity getForgeRarity(ItemStack stack) {
		return rarity;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, net.minecraft.client.util.ITooltipFlag advanced) {
//		Wizardry.proxy.addMultiLineDescription(tooltip, "item." + this.getRegistryName() + ".desc");

		if (advanced.isAdvanced()) {
			// Advanced tooltips for debugging
			// current mana
			tooltip.add(TextFormatting.GOLD  + "Ancient Relic");
		}
		if (isResearched(stack)) {
			tooltip.add((TextFormatting.GRAY + I18n.format("item.ancientspellcraft:relic_researched")));
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack stack) {
		return rarity == EnumRarity.EPIC;
	}

	@Override
	public boolean isEnchantable(ItemStack stack) {
		return false;
	}

	public static void setResearched(ItemStack stack) {
		if (stack.getTagCompound() != null && stack.getTagCompound().hasKey("researched") && stack.getTagCompound().getBoolean("researched"))
			return;
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setBoolean("researched", true);
		stack.setTagCompound(nbt);
	}

	public static void setRelicContent(ItemStack stack, EntityPlayer player) {
		if (stack.getTagCompound() != null && stack.getTagCompound().hasKey("researched") && stack.getTagCompound().getBoolean("spell"))
			return;
		NBTTagCompound nbt;
		if (stack.hasTagCompound()) {
			nbt = stack.getTagCompound();
		} else {
			nbt = new NBTTagCompound();
		}
		nbt.setString("spell", Knowledge.getRandomNextSpell(player, getTier(stack)).getRegistryName().toString());
		stack.setTagCompound(nbt);
	}

	public static boolean isResearched(ItemStack stack) {
		if (stack.getTagCompound() != null && stack.getTagCompound().hasKey("researched")) {
			return stack.getTagCompound().getBoolean("researched");
		}
		return false;
	}

	@Nullable
	public static List<ItemStack> getSpellComponentItems(ItemStack stack) {
		if (stack.getItem() instanceof ItemRelic) {
			if ((stack.getTagCompound() != null && stack.getTagCompound().hasKey("spell"))) {
				Spell spell = Spell.get(stack.getTagCompound().getString("spell"));
				if (spell != null && SpellComponentList.containsSpell(spell)) {
					SpellComponentList componentList = lookupBySpell(spell);
					return Arrays.asList(componentList.getComponents());
				}
			}
		}
		return null;
	}

	@Nullable
	public static Spell getSpell(ItemStack stack) {
		if (stack.getItem() instanceof ItemRelic) {
			if ((stack.getTagCompound() != null && stack.getTagCompound().hasKey("spell"))) {
				Spell spell = Spell.get(stack.getTagCompound().getString("spell"));
				return spell;
			}
		}
		return null;
	}

}

