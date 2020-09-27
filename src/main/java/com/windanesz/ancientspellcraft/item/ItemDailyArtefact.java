package com.windanesz.ancientspellcraft.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

public abstract class ItemDailyArtefact extends ItemASArtefact {
	private static final String LAST_OPEN_TIME_TAG = "last_open_time";

	public ItemDailyArtefact(EnumRarity rarity, Type type) {
		super(rarity, type);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);
		long currentWorldTime = player.world.getTotalWorldTime();

		if (stack.hasTagCompound() && stack.getTagCompound().hasKey(LAST_OPEN_TIME_TAG)) {
			long lastAccess = stack.getTagCompound().getLong(LAST_OPEN_TIME_TAG);
			if (isFullDayBetween(lastAccess, currentWorldTime)) {
				System.out.println("fullday: diff: " + (currentWorldTime - lastAccess));
				System.out.println("giving gold");
				performAction(player);
				setLastOpenTimeCurrent(stack, currentWorldTime);
				return new ActionResult<>(EnumActionResult.SUCCESS, stack);
			} else {
				if (!player.world.isRemote)
					player.sendStatusMessage(new TextComponentTranslation("item." + this.getRegistryName() + ".empty"), true);
				return new ActionResult<>(EnumActionResult.FAIL, stack);
			}
		} else {
			System.out.println("first open time");
			performAction(player);
			setLastOpenTimeCurrent(stack, currentWorldTime);
			return new ActionResult<>(EnumActionResult.SUCCESS, stack);
		}
	}

	public static boolean isFullDayBetween(long startTime, long endTime) {
		//		long fullDay = 24000;
		long fullDay = 24000;
		System.out.println("diff: " + (endTime - startTime));
		return (endTime - startTime) >= fullDay;
	}

	public abstract void performAction(EntityPlayer player);

	public static void setLastOpenTimeCurrent(ItemStack stack, long currentTime) {
		if (stack.hasTagCompound()) {
			NBTTagCompound nbt = stack.getTagCompound();
			nbt.setLong(LAST_OPEN_TIME_TAG, currentTime);
			stack.setTagCompound(nbt);
		} else {
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setLong(LAST_OPEN_TIME_TAG, currentTime);
			stack.setTagCompound(nbt);
		}

	}
}
