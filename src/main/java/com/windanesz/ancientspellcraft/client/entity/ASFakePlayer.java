package com.windanesz.ancientspellcraft.client.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

public class ASFakePlayer extends EntityLivingBase {

	private ASFakePlayer(World world) {
		super(world);
	}

	public static ASFakePlayer FAKE_PLAYER = new ASFakePlayer(Minecraft.getMinecraft().player.world);

	@Override
	public EnumHandSide getPrimaryHand() { return EnumHandSide.RIGHT; }

	@Override
	public Iterable<ItemStack> getArmorInventoryList() {
		return NonNullList.withSize(4, ItemStack.EMPTY);
	}

	// this can be called by various classes like EntityRenderer.updateFogColor so passing null would cause a crash
	@Override
	public ItemStack getItemStackFromSlot(EntityEquipmentSlot slotIn) { return ItemStack.EMPTY; }

	@Override
	public void setItemStackToSlot(EntityEquipmentSlot slotIn, ItemStack stack) {}

}