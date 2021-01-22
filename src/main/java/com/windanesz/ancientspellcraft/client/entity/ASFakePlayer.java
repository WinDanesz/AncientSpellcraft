package com.windanesz.ancientspellcraft.client.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import net.minecraft.world.World;

public class ASFakePlayer extends EntityLivingBase {

	private ASFakePlayer(World world) {
		super(world);
	}

	public static ASFakePlayer FAKE_PLAYER = new ASFakePlayer(Minecraft.getMinecraft().player.world);

	@Override
	public EnumHandSide getPrimaryHand() { return null; }

	@Override
	public Iterable<ItemStack> getArmorInventoryList() { return null; }

	@Override
	public ItemStack getItemStackFromSlot(EntityEquipmentSlot slotIn) { return null; }

	@Override
	public void setItemStackToSlot(EntityEquipmentSlot slotIn, ItemStack stack) { }

}