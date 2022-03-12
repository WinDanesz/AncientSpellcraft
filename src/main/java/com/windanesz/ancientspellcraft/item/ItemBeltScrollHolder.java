package com.windanesz.ancientspellcraft.item;

import com.windanesz.ancientspellcraft.registry.AncientSpellcraftItems;
import com.windanesz.ancientspellcraft.util.ASUtils;
import electroblob.wizardry.Wizardry;
import electroblob.wizardry.item.IManaStoringItem;
import electroblob.wizardry.item.ItemWandUpgrade;
import electroblob.wizardry.registry.WizardryItems;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ItemBeltScrollHolder extends ItemNewArtefact implements ITickableArtefact {

	public static final String SCROLL_TAG = "scroll";

	public ItemBeltScrollHolder(EnumRarity rarity, AdditionalType type) {
		super(rarity, type);
	}

	public static ItemStack getScroll(ItemStack stack) {
		if (stack.getItem() instanceof ItemBeltScrollHolder && stack.hasTagCompound() && stack.getTagCompound().hasKey(SCROLL_TAG)) {
			Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(stack.getTagCompound().getString(SCROLL_TAG)));
			if (item != null) {
				return new ItemStack(item);
			}
		}
		return ItemStack.EMPTY;
	}

	public static boolean setScroll(ItemStack holder, @Nullable ItemStack scroll) {
		if (getScroll(holder) != ItemStack.EMPTY && scroll != null) {
			// has a scroll
			return false;
		} else {
			NBTTagCompound nbt = holder.getTagCompound();
			if (nbt == null) { nbt = new NBTTagCompound(); }

			if (scroll != null && scroll.getItem() instanceof ItemWandUpgrade
					&& scroll.getItem() != AncientSpellcraftItems.soulbound_upgrade
					&& scroll.getItem() != WizardryItems.melee_upgrade
					&& scroll.getItem() != WizardryItems.storage_upgrade
					&& scroll.getItem() != WizardryItems.siphon_upgrade
					&& scroll.getItem() != WizardryItems.attunement_upgrade) {
				nbt.setString(SCROLL_TAG, scroll.getItem().getRegistryName().toString());
			} else if (nbt.hasKey(SCROLL_TAG)) {
				nbt.removeTag(SCROLL_TAG);
			}
			holder.setTagCompound(nbt);
			return true;
		}
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer player, EnumHand hand) {
		use(player, hand);
		return new ActionResult<>(EnumActionResult.PASS, player.getHeldItem(hand));
	}

	public boolean use(EntityPlayer player, EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);
		ItemStack otherStack = player.getHeldItem(hand == EnumHand.MAIN_HAND ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND);

		// remove scroll
		if (otherStack.isEmpty()) {
			ItemStack scroll = getScroll(stack);
			if (scroll != ItemStack.EMPTY) {
				if (!player.world.isRemote) {
					setScroll(stack, null);
					ASUtils.giveStackToPlayer(player, scroll);
					player.sendStatusMessage(new TextComponentTranslation("item.ancientspellcraft:belt_scroll_holder.removed_scroll", scroll.getItem().getItemStackDisplayName(scroll)), false);
				}
				return true;
			}
		}

		if (otherStack.getItem() instanceof ItemWandUpgrade) {
			// put scroll to empty holder
			if (!player.world.isRemote) {
				setScroll(stack, otherStack);
				player.sendStatusMessage(new TextComponentTranslation("item.ancientspellcraft:belt_scroll_holder.added_scroll", otherStack.getItem().getItemStackDisplayName(otherStack)), false);
				otherStack.shrink(1);
			}
			return true;
		}

		return false;
	}

	@Override
	public void onWornTick(ItemStack itemstack, EntityLivingBase player) {
		if (!player.world.isRemote && itemstack.getItem() instanceof IManaStoringItem &&  !((IManaStoringItem)itemstack.getItem()).isManaFull(itemstack)
				&& player.world.getTotalWorldTime() % 50L == 0L) {
			((IManaStoringItem)itemstack.getItem()).rechargeMana(itemstack, 1);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, net.minecraft.client.util.ITooltipFlag advanced) {
		super.addInformation(stack, world, tooltip, advanced);
		Wizardry.proxy.addMultiLineDescription(tooltip, "item.ancientspellcraft:belt_scroll_holder.usage_1");
		Wizardry.proxy.addMultiLineDescription(tooltip, "item.ancientspellcraft:belt_scroll_holder.usage_2");
		ItemStack scroll = getScroll(stack);
		if (scroll != ItemStack.EMPTY) {
			Wizardry.proxy.addMultiLineDescription(tooltip, "item.ancientspellcraft:belt_scroll_holder.current_scroll", scroll.getItem().getItemStackDisplayName(scroll));
		} else {
			Wizardry.proxy.addMultiLineDescription(tooltip, "item.ancientspellcraft:belt_scroll_holder.empty");
		}
	}
}
