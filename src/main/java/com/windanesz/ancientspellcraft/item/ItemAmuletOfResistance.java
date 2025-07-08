package com.windanesz.ancientspellcraft.item;

import com.windanesz.ancientspellcraft.Settings;
import com.windanesz.ancientspellcraft.util.ASUtils;
import electroblob.wizardry.Wizardry;
import electroblob.wizardry.constants.Constants;
import electroblob.wizardry.item.IManaStoringItem;
import electroblob.wizardry.item.IWorkbenchItem;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.registry.WizardrySounds;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ItemAmuletOfResistance extends ItemASArtefact implements IManaStoringItem, IWorkbenchItem, ITickableArtefact {

	private static final int MANA_COST_PER_TICK = 1; // Mana cost per tick when immunity is active
	private static final String IMBUED_POTION_TAG = "imbued_potion";
	private static final String IMMUNITY_ACTIVE_TAG = "immunity_active";

	public ItemAmuletOfResistance(EnumRarity rarity, Type type) {
		super(rarity, type);
		setMaxDamage(Settings.generalSettings.amulet_of_resistance_mana_capacity);
		setMaxStackSize(1);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack stack) {
		return hasImbuedPotion(stack) && !isManaEmpty(stack);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, net.minecraft.client.util.ITooltipFlag advanced) {
		super.addInformation(stack, world, tooltip, advanced);

		if (hasImbuedPotion(stack)) {
			Potion imbuedPotion = getImbuedPotion(stack);
			if (imbuedPotion != null) {
				tooltip.add(TextFormatting.GREEN + "Imbued with: " + new TextComponentTranslation(imbuedPotion.getName()).getFormattedText());
			}
		} else {
			tooltip.add(TextFormatting.YELLOW + "Right-click with a potion to imbue");
		}

		// Show mana information
		tooltip.add(TextFormatting.BLUE + "Mana: " + getMana(stack) + "/" + getManaCapacity(stack));

		if (!Settings.isArtefactEnabled(this)) {
			tooltip.add(Wizardry.proxy.translate("item." + Wizardry.MODID + ":generic.disabled", new Style().setColor(TextFormatting.RED)));
		}
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);

		if (!world.isRemote && Settings.isArtefactEnabled(this)) {
			// Check if player is holding a potion in the other hand
			EnumHand otherHand = hand == EnumHand.MAIN_HAND ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;
			ItemStack otherStack = player.getHeldItem(otherHand);

			if (!otherStack.isEmpty() && otherStack.getItem() instanceof net.minecraft.item.ItemPotion) {
				// Try to imbue with the potion
				if (imbueWithPotion(stack, otherStack, player)) {
					// Consume the potion
					otherStack.shrink(1);
					player.playSound(WizardrySounds.ITEM_WAND_LEVELUP, 1.0f, 1.0f);
					return new ActionResult<>(EnumActionResult.SUCCESS, stack);
				}
			} else if (hasImbuedPotion(stack) && !isManaEmpty(stack)) {
				// Toggle immunity
				boolean wasActive = isImmunityActive(stack);
				toggleImmunity(stack);
				player.playSound(WizardrySounds.ITEM_WAND_LEVELUP, 1.0f, 1.0f);
				// Show chat message for activation/deactivation
				if (!wasActive) {
					ASUtils.sendMessage(player, "item.ancientspellcraft:amulet_of_resistance.activated", false);
				} else {
					ASUtils.sendMessage(player, "item.ancientspellcraft:amulet_of_resistance.deactivated", false);
				}
				return new ActionResult<>(EnumActionResult.SUCCESS, stack);
			}
		}

		return new ActionResult<>(EnumActionResult.FAIL, stack);
	}

	@Override
	public void onWornTick(ItemStack itemstack, EntityLivingBase player) {
		if (!player.world.isRemote && hasImbuedPotion(itemstack) && isImmunityActive(itemstack)) {
			Potion imbuedPotion = getImbuedPotion(itemstack);
			if (imbuedPotion != null) {
				if (player.ticksExisted % 20 == 0) { // Check every second
					consumeMana(itemstack, MANA_COST_PER_TICK, player);
				}
				// Check if player has the imbued potion effect
				if (player.isPotionActive(imbuedPotion)) {
					// Remove the effect and consume mana
					player.removePotionEffect(imbuedPotion);

					// If out of mana, deactivate immunity
					if (isManaEmpty(itemstack)) {
						setImmunityActive(itemstack, false);
					}
				}
			}
		}
	}

	private boolean imbueWithPotion(ItemStack amulet, ItemStack potionStack, EntityPlayer player) {
		// Get the potion effect from the potion item
		List<PotionEffect> effects = net.minecraft.potion.PotionUtils.getEffectsFromStack(potionStack);
		if (!effects.isEmpty()) {
			Potion potion = effects.get(0).getPotion();
			setImbuedPotion(amulet, potion);
			player.sendMessage(new TextComponentTranslation("item.ancientspellcraft:amulet_of_resistance.imbued", new TextComponentTranslation(potion.getName()).getFormattedText()));
			return true;
		}
		return false;
	}

	private void toggleImmunity(ItemStack stack) {
		boolean currentlyActive = isImmunityActive(stack);
		setImmunityActive(stack, !currentlyActive);
	}

	private boolean hasImbuedPotion(ItemStack stack) {
		return stack.hasTagCompound() && stack.getTagCompound().hasKey(IMBUED_POTION_TAG);
	}

	private Potion getImbuedPotion(ItemStack stack) {
		if (hasImbuedPotion(stack)) {
			String potionName = stack.getTagCompound().getString(IMBUED_POTION_TAG);
			return ForgeRegistries.POTIONS.getValue(new ResourceLocation(potionName));
		}
		return null;
	}

	private void setImbuedPotion(ItemStack stack, Potion potion) {
		if (!stack.hasTagCompound()) {
			stack.setTagCompound(new NBTTagCompound());
		}
		stack.getTagCompound().setString(IMBUED_POTION_TAG, potion.getRegistryName().toString());
	}

	private boolean isImmunityActive(ItemStack stack) {
		return stack.hasTagCompound() && stack.getTagCompound().getBoolean(IMMUNITY_ACTIVE_TAG);
	}

	private void setImmunityActive(ItemStack stack, boolean active) {
		if (!stack.hasTagCompound()) {
			stack.setTagCompound(new NBTTagCompound());
		}
		stack.getTagCompound().setBoolean(IMMUNITY_ACTIVE_TAG, active);
	}

	// IManaStoringItem implementation
	@Override
	public int getMana(ItemStack stack) {
		return getManaCapacity(stack) - getDamage(stack);
	}

	@Override
	public void setMana(ItemStack stack, int mana) {
		super.setDamage(stack, getManaCapacity(stack) - mana);
	}

	@Override
	public int getManaCapacity(ItemStack stack) {
		return this.getMaxDamage(stack);
	}

	@Override
	public boolean showManaInWorkbench(EntityPlayer player, ItemStack stack) {
		return true;
	}

	@Override
	public void consumeMana(ItemStack stack, int mana, @Nullable EntityLivingBase wielder) {
		IManaStoringItem.super.consumeMana(stack, mana, wielder);
	}

	@Override
	public void rechargeMana(ItemStack stack, int mana) {
		IManaStoringItem.super.rechargeMana(stack, mana);
	}

	@Override
	public boolean isManaFull(ItemStack stack) {
		return IManaStoringItem.super.isManaFull(stack);
	}

	@Override
	public boolean isManaEmpty(ItemStack stack) {
		return IManaStoringItem.super.isManaEmpty(stack);
	}

	@Override
	public float getFullness(ItemStack stack) {
		return IManaStoringItem.super.getFullness(stack);
	}

	// IWorkbenchItem implementation
	@Override
	public int getSpellSlotCount(ItemStack stack) {
		return 0;
	}

	@Override
	public boolean showTooltip(ItemStack stack) {
		return true;
	}

	@Override
	public boolean onApplyButtonPressed(EntityPlayer player, Slot centre, Slot crystals, Slot upgrade, Slot[] spellBooks) {
		boolean changed = false;

		// Handle mana charging with crystals
		if (crystals.getStack() != ItemStack.EMPTY && !this.isManaFull(centre.getStack())) {
			int chargeDepleted = this.getManaCapacity(centre.getStack()) - this.getMana(centre.getStack());

			int manaPerItem = Constants.MANA_PER_CRYSTAL;
			if (crystals.getStack().getItem() == WizardryItems.crystal_shard) {
				manaPerItem = Constants.MANA_PER_SHARD;
			}
			if (crystals.getStack().getItem() == WizardryItems.grand_crystal) {
				manaPerItem = Constants.GRAND_CRYSTAL_MANA;
			}

			if (crystals.getStack().getCount() * manaPerItem < chargeDepleted) {
				// If there aren't enough crystals to fully charge
				this.rechargeMana(centre.getStack(), crystals.getStack().getCount() * manaPerItem);
				crystals.decrStackSize(crystals.getStack().getCount());
			} else {
				// If there are excess crystals (or just enough)
				this.setMana(centre.getStack(), this.getManaCapacity(centre.getStack()));
				crystals.decrStackSize((int) Math.ceil(((double) chargeDepleted) / manaPerItem));
			}

			changed = true;
		}

		return changed;
	}
} 