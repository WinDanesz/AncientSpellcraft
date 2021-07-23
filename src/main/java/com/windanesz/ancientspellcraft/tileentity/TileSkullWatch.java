package com.windanesz.ancientspellcraft.tileentity;

import com.windanesz.ancientspellcraft.registry.AncientSpellcraftBlocks;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftSounds;
import electroblob.wizardry.tileentity.TileEntityPlayerSave;
import electroblob.wizardry.util.AllyDesignationSystem;
import electroblob.wizardry.util.EntityUtils;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import java.util.Random;

@SuppressWarnings("Duplicates")
public class TileSkullWatch extends TileEntityPlayerSave implements ITickable {

	private static final Random rand = new Random();

	private final String OWNER_TAG = "OwnerUUID";

	private final double DETECT_BASE_RADIUS = 15D;

	public int tickCount;
	public float pageFlip;
	public float pageFlipPrev;
	public float flipT;
	public float flipA;
	public float bookSpread;
	public float bookSpreadPrev;
	public float skullRotation;
	public float skullRotationPrev;
	public float tRot;

	public TileSkullWatch() {
	}

	@SideOnly(Side.CLIENT)
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		AxisAlignedBB bb = INFINITE_EXTENT_AABB;
		Block type = getBlockType();
		if (type == AncientSpellcraftBlocks.SKULL_WATCH) {
			bb = new AxisAlignedBB(pos, pos.add(1, 1, 1));
		} else if (type != null) {
			AxisAlignedBB cbb = this.getWorld().getBlockState(pos).getBoundingBox(world, pos);
			if (cbb != null) {
				bb = cbb;
			}
		}
		return bb;
	}

	@SuppressWarnings("Duplicates")
	public void update() {
		EntityLivingBase target = null;

		this.bookSpreadPrev = this.bookSpread;
		this.skullRotationPrev = this.skullRotation;

		List<EntityLivingBase> entities = EntityUtils.getEntitiesWithinRadius(DETECT_BASE_RADIUS, this.pos.getX() + 0.5F, this.pos.getY() + 0.5F, this.pos.getZ() + 0.5F, world, EntityLivingBase.class);

		if (!entities.isEmpty()) {

			for (EntityLivingBase entity : entities) {

				// doesn't scream for its own owner or allies of the owner
				if (entity instanceof EntityAnimal || entity instanceof EntityArmorStand || getCaster() != null && (entity.getUniqueID() == getCaster().getUniqueID() || AllyDesignationSystem.isAllied(getCaster(), entity))) {
					continue;
				}

				// returns true if the entity provided in the argument can be seen. (Raytrace)
				if ((world.rayTraceBlocks(new Vec3d(this.pos.getX(), this.pos.getY() + 1, this.pos.getZ()), new Vec3d(entity.posX, entity.posY + (double) entity.getEyeHeight(), entity.posZ), false, true, false) == null)) {
					target = entity;
					break;
				}
			}

		}

		if (target != null) {
			if (world.getTotalWorldTime() % 50 == 0) {

				world.playSound((double) pos.getX(), (double) pos.getY(), (double) pos.getZ(), AncientSpellcraftSounds.SKULL_WATCH_SCREAM, SoundCategory.BLOCKS, 1F, 1F, false);
				world.playSound((double) target.getPosition().getX(), (double) target.getPosition().getY(), (double) target.getPosition().getZ(), AncientSpellcraftSounds.SKULL_WATCH_SCREAM, SoundCategory.PLAYERS, 0.3F, 1F, false);

				if (getCaster() != null && getCaster() instanceof EntityPlayer) {
					world.playSound((EntityPlayer) getCaster(), getCaster().getPosition(), AncientSpellcraftSounds.SKULL_WATCH_ALARM, SoundCategory.PLAYERS, 0.3F, 1F);
				}


			}

			double d0 = target.posX - (double) ((float) this.pos.getX() + 0.5F);
			double d1 = target.posZ - (double) ((float) this.pos.getZ() + 0.5F);
			this.tRot = (float) MathHelper.atan2(d1, d0);
			this.bookSpread += 0.1F;

			if (this.bookSpread < 0.5F || rand.nextInt(40) == 0) {
				float f1 = this.flipT;

				while (true) {
					this.flipT += (float) (rand.nextInt(4) - rand.nextInt(4));

					if (f1 != this.flipT) {
						break;
					}
				}
			}
		} else {
			this.tRot += 0.02F;
			this.bookSpread -= 0.1F;
		}

		while (this.skullRotation >= (float) Math.PI) {
			this.skullRotation -= ((float) Math.PI * 2F);
		}

		while (this.skullRotation < -(float) Math.PI) {
			this.skullRotation += ((float) Math.PI * 2F);
		}

		while (this.tRot >= (float) Math.PI) {
			this.tRot -= ((float) Math.PI * 2F);
		}

		while (this.tRot < -(float) Math.PI) {
			this.tRot += ((float) Math.PI * 2F);
		}

		float f2;

		for (f2 = this.tRot - this.skullRotation; f2 >= (float) Math.PI; f2 -= ((float) Math.PI * 2F)) {
			;
		}

		while (f2 < -(float) Math.PI) {
			f2 += ((float) Math.PI * 2F);
		}

		this.skullRotation += f2 * 0.4F;
		this.bookSpread = MathHelper.clamp(this.bookSpread, 0.0F, 1.0F);
		++this.tickCount;
		this.pageFlipPrev = this.pageFlip;
		float f = (this.flipT - this.pageFlip) * 0.4F;
		float f3 = 0.2F;
		f = MathHelper.clamp(f, -0.2F, 0.2F);
		this.flipA += (f - this.flipA) * 0.9F;
		this.pageFlip += this.flipA;
	}
}
