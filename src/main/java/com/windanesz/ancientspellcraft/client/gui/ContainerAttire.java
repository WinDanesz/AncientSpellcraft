package com.windanesz.ancientspellcraft.client.gui;

import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerAttire extends InventoryBasic {
	public ContainerAttire(String inventoryTitle, int slotCount) {
		super(inventoryTitle, false, slotCount);
	}

	@SideOnly(Side.CLIENT)
	public ContainerAttire(ITextComponent inventoryTitle, int slotCount) {
		super(inventoryTitle, slotCount);
	}

	public void loadInventoryFromNBT(NBTTagList tagList)
	{
		for (int i = 0; i < this.getSizeInventory(); ++i)
		{
			this.setInventorySlotContents(i, ItemStack.EMPTY);
		}

		for (int k = 0; k < tagList.tagCount(); ++k)
		{
			NBTTagCompound nbttagcompound = tagList.getCompoundTagAt(k);
			int j = nbttagcompound.getByte("Slot") & 255;

			if (j >= 0 && j < this.getSizeInventory())
			{
				this.setInventorySlotContents(j, new ItemStack(nbttagcompound));
			}
		}
	}

	public NBTTagList saveInventoryToNBT()
	{
		NBTTagList nbttaglist = new NBTTagList();

		for (int i = 0; i < this.getSizeInventory(); ++i)
		{
			ItemStack itemstack = this.getStackInSlot(i);

			if (!itemstack.isEmpty())
			{
				NBTTagCompound nbttagcompound = new NBTTagCompound();
				nbttagcompound.setByte("Slot", (byte)i);
				itemstack.writeToNBT(nbttagcompound);
				nbttaglist.appendTag(nbttagcompound);
			}
		}

		return nbttaglist;
	}
}