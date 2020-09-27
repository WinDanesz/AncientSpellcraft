package com.windanesz.ancientspellcraft.tileentity;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class TileArtefactPensive extends TileEntity {

	private final String AMOUNT = "amount";
	private int storedXP = 0;

	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setInteger(AMOUNT, storedXP);

		return compound;
	}

	protected void setWorldCreate(World worldIn) {
		this.setWorld(worldIn);
	}

	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		if (compound.hasKey(AMOUNT)) {
			this.storedXP = compound.getInteger(AMOUNT);
		} else {
			AncientSpellcraft.logger.error("failed to load stored XP amount from the Pensive");
			this.storedXP = 0;
		}
	}

	public int getStoredXP() {
		return storedXP;
	}

	public void setStoredXP(int storedXP) {
		this.storedXP = storedXP;
	}

	public boolean isEmpty() {
		return storedXP == 0;
	}

	/** Called to manually sync the tile entity with clients. */
	public void sync(){
		this.world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
	}

}
