package com.windanesz.ancientspellcraft.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.UUID;

//adapted from EntityEnderEye
public class EntityMageLight extends Entity {

	private int despawnTimer;
	private boolean dropNameTag;
	private UUID caster;
	private NBTTagCompound nameTag;
	private int nameTagMana;

	public EntityMageLight(World worldIn) {
		super(worldIn);
		this.setSize(0.25F, 0.25F);
	}

	protected void entityInit() {
	}

	public void setPlayer(EntityPlayer player) {
		caster = player.getUniqueID();
	}

	/**
	 * Checks if the entity is in range to render.
	 */
	@SideOnly(Side.CLIENT)
	public boolean isInRangeToRenderDist(double distance) {
		double d0 = this.getEntityBoundingBox().getAverageEdgeLength() * 4.0D;

		if (Double.isNaN(d0)) {
			d0 = 4.0D;
		}

		d0 = d0 * 64.0D;
		return distance < d0 * d0;
	}

	public EntityMageLight(World worldIn, double x, double y, double z, EntityPlayer caster) {
		super(worldIn);
		this.caster = caster.getUniqueID();
		this.despawnTimer = 0;
		this.setSize(0.25F, 0.25F);
		this.setPosition(x, y, z);
	}

	public void setEnchantedNameTag(NBTTagCompound stack, int nameTagMana) {
		this.nameTag = stack;
		this.nameTagMana = nameTagMana;
	}

	public void writeEntityToNBT(NBTTagCompound compound) {
		compound.setUniqueId("caster", caster);
	}

	/**
	 * (abstract) Protected helper method to read subclass entity data from NBT.
	 */
	public void readEntityFromNBT(NBTTagCompound compound) {
		caster = compound.getUniqueId("caster");

	}

	@Override
	public void onUpdate() {
		if (!world.isRemote) {
			if (world.getPlayerEntityByUUID(caster) != null) {
				BlockPos casterPos = world.getPlayerEntityByUUID(caster).getPosition();
			} else {
				System.out.println("setting dead");
				this.setDead();
			}

		}
	}

	public float getBrightness() {
		return 1.0F;
	}

	@SideOnly(Side.CLIENT)
	public int getBrightnessForRender() {
		return 15728880;
	}

	/**
	 * Returns true if it's possible to attack this entity with an item.
	 */
	public boolean canBeAttackedWithItem() {
		return false;
	}
}