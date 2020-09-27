package com.windanesz.ancientspellcraft.client.gui;

import com.windanesz.ancientspellcraft.registry.AncientSpellcraftBlocks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ContainerIceWorkbench extends ContainerWorkbench {
	private World world;
	private BlockPos pos;

	public ContainerIceWorkbench(InventoryPlayer inventory, World worldIn, BlockPos pos) {
		super(inventory, worldIn, pos);
		world = worldIn;
		this.pos = pos;
	}

	public boolean canInteractWith(EntityPlayer playerIn) {
		if (this.world.getBlockState(this.pos).getBlock() != AncientSpellcraftBlocks.ICE_CRAFTING_TABLE) {
			return false;
		} else {
			return playerIn.getDistanceSq((double) this.pos.getX() + 0.5D, (double) this.pos.getY() + 0.5D, (double) this.pos.getZ() + 0.5D) <= 64.0D;
		}
	}
}
