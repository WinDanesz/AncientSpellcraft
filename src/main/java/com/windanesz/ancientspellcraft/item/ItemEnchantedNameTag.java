package com.windanesz.ancientspellcraft.item;

import com.windanesz.ancientspellcraft.registry.AncientSpellcraftTabs;
import electroblob.wizardry.Wizardry;
import electroblob.wizardry.constants.Constants;
import electroblob.wizardry.item.IManaStoringItem;
import electroblob.wizardry.item.ISpellCastingItem;
import electroblob.wizardry.item.IWorkbenchItem;
import electroblob.wizardry.spell.Spell;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemNameTag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ItemEnchantedNameTag extends ItemNameTag {
	public ItemEnchantedNameTag() {
		this.setCreativeTab(AncientSpellcraftTabs.ANCIENTSPELLCRAFT);
		this.maxStackSize = 1;
	}

	/**
	 * Returns true if the item can be used on the given entity, e.g. shears on sheep.
	 */
	public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer playerIn, EntityLivingBase target, EnumHand hand) {
		return false;
	}

	// enchantment glint only if charged
	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack stack) {
		return true;
	}

	@Override
	public boolean isEnchantable(ItemStack stack) {
		return false;
	}

	public int getItemEnchantability() {
		return 0;
	}

	@Override
	public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
		return false;
	}

	/**
	 * Return whether this item is repairable in an anvil.
	 */
	public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
		return false;
	}

	/**
	 * allows items to add custom lines of information to the mouseover description
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, net.minecraft.client.util.ITooltipFlag advanced) {
		Wizardry.proxy.addMultiLineDescription(tooltip, "item." + this.getRegistryName() + ".desc_filled");
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);
		if (hand == EnumHand.MAIN_HAND) {
			if (!world.isRemote) {
				player.sendStatusMessage(new TextComponentTranslation("item." + this.getRegistryName() + ".right_click"), false);
				player.sendMessage(new TextComponentTranslation("spell.ancientspellcraft:will_o_wisp.try_listbiomes"));
			}

			return new ActionResult<>(EnumActionResult.FAIL, stack);
		} else {
			if (player.getHeldItemMainhand().getItem() instanceof ISpellCastingItem) {
				Spell currSpell = ((ISpellCastingItem) player.getHeldItemMainhand().getItem()).getCurrentSpell(player.getHeldItemMainhand());
				if (currSpell.getUnlocalisedName().equals("ancientspellcraft:will_o_wisp")) {
					return new ActionResult<>(EnumActionResult.FAIL, stack);
				} else {
					if (!world.isRemote) {
						player.sendStatusMessage(new TextComponentTranslation("item." + this.getRegistryName() + ".right_click"), false);
						player.sendMessage(new TextComponentTranslation("spell.ancientspellcraft:will_o_wisp.try_listbiomes"));

					}

				}
			}
		}
		return new ActionResult<>(EnumActionResult.FAIL, stack);
	}
}
