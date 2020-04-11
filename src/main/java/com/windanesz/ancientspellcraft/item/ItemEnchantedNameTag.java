package com.windanesz.ancientspellcraft.item;

import electroblob.wizardry.Wizardry;
import electroblob.wizardry.constants.Constants;
import electroblob.wizardry.item.IManaStoringItem;
import electroblob.wizardry.item.ISpellCastingItem;
import electroblob.wizardry.item.IWorkbenchItem;
import electroblob.wizardry.registry.WizardryTabs;
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

public class ItemEnchantedNameTag extends ItemNameTag implements IWorkbenchItem, IManaStoringItem {
	public ItemEnchantedNameTag() {
		this.setCreativeTab(WizardryTabs.WIZARDRY);
		this.setMaxDamage(300);
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
	public boolean hasEffect(ItemStack stack) {
		return !(isManaEmpty(stack));
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

	@Override
	public int getSpellSlotCount(ItemStack stack) {
		return 0;
	}

	@Override
	public boolean onApplyButtonPressed(EntityPlayer player, Slot centre, Slot crystals, Slot upgrade, Slot[] spellBooks) {
		boolean changed = false;
		if (crystals.getStack() != ItemStack.EMPTY && !this.isManaFull(centre.getStack())) {

			int chargeDepleted = this.getManaCapacity(centre.getStack()) - this.getMana(centre.getStack());

			if (crystals.getStack().getCount() * Constants.MANA_PER_CRYSTAL < chargeDepleted) {
				// If there aren't enough crystals to fully charge the name tag
				this.rechargeMana(centre.getStack(), crystals.getStack().getCount() * Constants.MANA_PER_CRYSTAL);
				crystals.decrStackSize(crystals.getStack().getCount());

			} else {
				// If there are excess crystals (or just enough)
				this.setMana(centre.getStack(), this.getManaCapacity(centre.getStack()));
				crystals.decrStackSize((int) Math.ceil(((double) chargeDepleted) / Constants.MANA_PER_CRYSTAL));
			}

			changed = true;
		}

		return changed;
	}

	/**
	 * Returns whether the tooltip (dark grey box) should be drawn when this item is in an arcane workbench. Only
	 * called client-side.
	 *
	 * @param stack The itemstack to query.
	 * @return True if the workbench tooltip should be shown, false if not.
	 */
	@Override
	public boolean showTooltip(ItemStack stack) {
		return true;
	}

	// can place in the workbench middle slot
	@Override
	public boolean canPlace(ItemStack stack) {
		return true;
	}

	/**
	 * allows items to add custom lines of information to the mouseover description
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, net.minecraft.client.util.ITooltipFlag advanced) {
		if ((isManaEmpty(stack))) { // no mana tooltip
			Wizardry.proxy.addMultiLineDescription(tooltip, "item." + this.getRegistryName() + ".desc_empty");
		} else { // has mana tooltip
			Wizardry.proxy.addMultiLineDescription(tooltip, "item." + this.getRegistryName() + ".desc_filled");
		}
		// TODO: if it is renamed already, display a different tag

		//				We don't want a mana tooltip for this item.
		if (advanced.isAdvanced()) {
			// Advanced tooltips for debugging
			// current mana
			tooltip.add("\u00A79" + net.minecraft.client.resources.I18n.format("item." + Wizardry.MODID + ":wand.mana",
					this.getMana(stack), this.getManaCapacity(stack)));

		}
	}

	@Override
	public int getMana(ItemStack stack) {
		return getManaCapacity(stack) - getDamage(stack);
	}

	@Override
	public void setDamage(ItemStack stack, int damage) {
		// Overridden to do nothing to stop repair things from 'repairing' the mana in a wand
	}

	@Override
	public void setMana(ItemStack stack, int mana) {
		// Using super (which can only be done from in here) bypasses the above override
		super.setDamage(stack, getManaCapacity(stack) - mana);
	}

	/**
	 * Convenience method that decreases the amount of mana contained in the given item stack by the given value. This
	 * method automatically limits the mana to a minimum of 0 and performs the relevant checks for creative mode, etc.
	 */
	@Override
	public void consumeMana(ItemStack stack, int mana, EntityLivingBase wielder) {
		if (wielder instanceof EntityPlayer && ((EntityPlayer) wielder).isCreative())
			return; // Mana isn't consumed in creative
		setMana(stack, Math.max(getMana(stack) - mana, 0));
		if (getMana(stack) <= 0) {

		}

	}

	@Override
	public int getManaCapacity(ItemStack stack) {
		return this.getMaxDamage(stack);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);
		if (hand == EnumHand.MAIN_HAND) {
			if (!world.isRemote) {
				player.sendStatusMessage(new TextComponentTranslation("item." + this.getRegistryName() + ".right_click"), true);
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
						player.sendStatusMessage(new TextComponentTranslation("item." + this.getRegistryName() + ".right_click"), true);
						player.sendMessage(new TextComponentTranslation("spell.ancientspellcraft:will_o_wisp.try_listbiomes"));

					}

				}
			}
		}
		return new ActionResult<>(EnumActionResult.FAIL, stack);
	}
}
