package com.windanesz.ancientspellcraft.entity;

import com.windanesz.ancientspellcraft.registry.AncientSpellcraftItems;
import electroblob.wizardry.util.ParticleBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

//adapted from EntityEnderEye
public class EntityWisp extends Entity {
	/**
	 * 'x' location the eye should float towards.
	 */
	private double targetX;
	/**
	 * 'y' location the eye should float towards.
	 */
	private double targetY;
	/**
	 * 'z' location the eye should float towards.
	 */
	private double targetZ;
	private int despawnTimer;
	private boolean dropNameTag;
	private EntityPlayer caster;
	private NBTTagCompound nameTag;
	private int nameTagMana;

	public EntityWisp(World worldIn) {
		super(worldIn);
		this.setSize(0.25F, 0.25F);
	}

	protected void entityInit() {
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

	public EntityWisp(World worldIn, double x, double y, double z) {
		super(worldIn);
		this.despawnTimer = 0;
		this.setSize(0.25F, 0.25F);
		this.setPosition(x, y, z);
	}

	public void setEnchantedNameTag(NBTTagCompound stack, int nameTagMana) {
		this.nameTag = stack;
		this.nameTagMana = nameTagMana;
	}

	public void moveTowards(BlockPos pos, EntityPlayer caster) {
		this.caster = caster;
		double targetX = (double) pos.getX(); // target x pos
		int i = pos.getY(); // target y pos
		double d1 = (double) pos.getZ(); // target z pos
		double d2 = targetX - this.posX; // targetXpos - currXpos = X distance from target ?
		double d3 = d1 - this.posZ; // targetZpos - currZpos = Z distance from target ?
		float f = MathHelper.sqrt(d2 * d2 + d3 * d3);

		if (f > 12.0F) { // from 12 to 32   wait for the player, only move if closer than 5 blocks
			this.targetX = this.posX + d2 / (double) f * 30.0D; // from 12 to 120
			this.targetZ = this.posZ + d3 / (double) f * 30.0D;
			this.targetY = this.posY + 1.0D; // changed from 8 to 2 to 1
		} else {
			this.targetX = targetX;
			this.targetY = (double) i;
			this.targetZ = d1;
		}

		this.despawnTimer = 0;
		this.dropNameTag = this.rand.nextInt(5) > 0;
		this.dropNameTag = Math.random() < 0.75;

	}

	/**
	 * Updates the entity motion clientside, called by packets from the server
	 */
	@SideOnly(Side.CLIENT)
	public void setVelocity(double x, double y, double z) {
		this.motionX = x;
		this.motionY = y;
		this.motionZ = z;

		if (this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F) {
			float f = MathHelper.sqrt(x * x + z * z);
			this.rotationYaw = (float) (MathHelper.atan2(x, z) * (180D / Math.PI));
			this.rotationPitch = (float) (MathHelper.atan2(y, (double) f) * (180D / Math.PI));
			this.prevRotationYaw = this.rotationYaw;
			this.prevRotationPitch = this.rotationPitch;
		}
	}

	/**
	 * Called to update the entity's position/logic.
	 */
	public void onUpdate() {
		this.lastTickPosX = this.posX;
		this.lastTickPosY = this.posY;
		this.lastTickPosZ = this.posZ;
		super.onUpdate();
		this.posX += this.motionX;
		this.posY += this.motionY;
		this.posZ += this.motionZ;
		float f = MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
		this.rotationYaw = (float) (MathHelper.atan2(this.motionX, this.motionZ) * (180D / Math.PI));

		for (this.rotationPitch = (float) (MathHelper.atan2(this.motionY, (double) f) * (180D / Math.PI)); this.rotationPitch - this.prevRotationPitch < -180.0F; this.prevRotationPitch -= 360.0F) {
			;
		}

		while (this.rotationPitch - this.prevRotationPitch >= 180.0F) {
			this.prevRotationPitch += 360.0F;
		}

		while (this.rotationYaw - this.prevRotationYaw < -180.0F) {
			this.prevRotationYaw -= 360.0F;
		}

		while (this.rotationYaw - this.prevRotationYaw >= 180.0F) {
			this.prevRotationYaw += 360.0F;
		}

		this.rotationPitch = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * 0.2F;
		this.rotationYaw = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * 0.2F;

		if (!this.world.isRemote) {
			double d0 = this.targetX - this.posX;
			double d1 = this.targetZ - this.posZ;
			float f1 = (float) Math.sqrt(d0 * d0 + d1 * d1);
			float f2 = (float) MathHelper.atan2(d1, d0);
			double d2 = (double) f + (double) (f1 - f) * 0.0025D;

			if (f1 < 1.0F) {
				d2 *= 0.8D;
				this.motionY *= 0.8D;
			}

			this.motionX = Math.cos((double) f2) * d2 * 0.7;
			this.motionZ = Math.sin((double) f2) * d2 * 0.7;
		}

		float f3 = 0.25F;
		// under water
		if (this.isInWater()) {
			for (int i = 0; i < 4; ++i) {
				this.world.spawnParticle(EnumParticleTypes.WATER_BUBBLE, this.posX - this.motionX * 0.25D, this.posY - this.motionY * 0.25D, this.posZ - this.motionZ * 0.25D, this.motionX, this.motionY, this.motionZ);
			}
		} else {
			if (this.world.isRemote) { // client side only
				ParticleBuilder.create(ParticleBuilder.Type.SPARKLE, rand, posX, posY, posZ, 0.03, true).clr(1, 1, 0.65f).fade(0.7f, 0, 1)
						.time(20 + rand.nextInt(10)).spawn(world);
			}
		}

		if (!this.world.isRemote) {
			this.setPosition(this.posX, this.posY, this.posZ);
			++this.despawnTimer;

			if (this.despawnTimer > 80 && !this.world.isRemote) {
				this.playSound(SoundEvents.ENTITY_GHAST_SHOOT, 1.0F, 1.0F);
				this.setDead();

				ItemStack stack = new ItemStack(AncientSpellcraftItems.enchanted_name_tag, 1, nameTagMana);
				stack.setTagCompound(nameTag);
				this.world.spawnEntity(new EntityItem(this.world, this.posX, this.posY, this.posZ, stack));
			}
		}
	}

	/**
	 * (abstract) Protected helper method to write subclass entity data to NBT.
	 */
	public void writeEntityToNBT(NBTTagCompound compound) {
		NBTTagCompound nameTagTagCompound = this.nameTag;
		NBTTagList nbttaglist1 = new NBTTagList();

		if (nameTagTagCompound != null) {
			nbttaglist1.appendTag(nameTag);
		}

		compound.setInteger("EnchantedNameTagMana", nameTagMana);
		compound.setTag("EnchantedNameTagTags", nbttaglist1);
	}

	/**
	 * (abstract) Protected helper method to read subclass entity data from NBT.
	 */
	public void readEntityFromNBT(NBTTagCompound compound) {
		if (compound.hasKey("EnchantedNameTagTags")) {
			nameTag = compound.getCompoundTag("EnchantedNameTagTags");
			nameTagMana = compound.getInteger("EnchantedNameTagMana");
		}

	}

	/**
	 * Gets how bright this entity is.
	 */
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