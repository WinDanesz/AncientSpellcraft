package com.windanesz.ancientspellcraft.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface ITransmutableItem {
    /**
     * Called when the transmute button is pressed in the GUI. Return true if successful.
     */
    boolean onTransmuteButton(EntityPlayer player, ItemStack itemStack, World world);
} 