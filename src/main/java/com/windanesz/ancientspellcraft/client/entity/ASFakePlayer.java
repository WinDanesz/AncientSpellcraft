package com.windanesz.ancientspellcraft.client.entity;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

import java.util.Map;
import java.util.WeakHashMap;

public class ASFakePlayer extends EntityLivingBase {

	private static final Map<World, ASFakePlayer> FAKE_PLAYERS = new WeakHashMap<>();

	private ASFakePlayer(World world) {
		super(world);
	}

	public static ASFakePlayer get(World world) {
		return FAKE_PLAYERS.computeIfAbsent(world, ASFakePlayer::new);
	}

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
