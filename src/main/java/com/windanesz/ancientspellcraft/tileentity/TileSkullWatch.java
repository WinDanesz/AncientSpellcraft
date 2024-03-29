package com.windanesz.ancientspellcraft.tileentity;

import com.windanesz.ancientspellcraft.registry.ASBlocks;
import com.windanesz.ancientspellcraft.registry.ASSounds;
import electroblob.wizardry.entity.living.EntitySkeletonMinion;
import electroblob.wizardry.tileentity.TileEntityPlayerSave;
import electroblob.wizardry.util.AllyDesignationSystem;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.ParticleBuilder;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import java.util.Random;

@SuppressWarnings("Duplicates")
public class TileSkullWatch extends TileEntityPlayerSave implements ITickable {

	private static final Random rand = new Random();
	public static final String MARK_ENTITIES_TAG = "MarkEntitiesWithGlowing";
	public static final String SUMMON_SKELETON_TAG = "SummonSkeleton";

	private final String OWNER_TAG = "OwnerUUID";

	private final double DETECT_BASE_RADIUS = 15D;

	private int currentSummonCooldown;
	private int maxSummonCooldown = 1200;
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

	public boolean isTriggered() {
		return triggered;
	}

	private boolean triggered = false;
	private boolean recentlyTriggered = false;

	private boolean markEntities = false;
	private boolean summonSkeleton = false;

	public TileSkullWatch() {
	}

	@SideOnly(Side.CLIENT)
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		AxisAlignedBB bb = INFINITE_EXTENT_AABB;
		Block type = getBlockType();
		if (type == ASBlocks.SKULL_WATCH) {
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
		if (!world.isRemote && summonSkeleton && currentSummonCooldown > 0) {
			currentSummonCooldown--;
		}

		if (recentlyTriggered) {
			world.notifyNeighborsOfStateChange(pos, ASBlocks.SKULL_WATCH, true);
			recentlyTriggered = false;
		}

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
			// Domus Amulet effect
			if (summonSkeleton && currentSummonCooldown == 0) {
				// summon a skeleton
				BlockPos summonPos = this.pos; //BlockUtils.findNearbyFloorSpace(world, this.pos, 3, 3);
				if (summonPos != null) {
					if (!world.isRemote) {
						EntitySkeletonMinion skeletonMinion = new EntitySkeletonMinion(world);
						skeletonMinion.setPosition(summonPos.getX() + 0.5, summonPos.getY(), summonPos.getZ() + 0.5);
						skeletonMinion.setCaster(this.getCaster());

						skeletonMinion.setHeldItem(EnumHand.MAIN_HAND, new ItemStack(Items.WOODEN_SWORD));
						skeletonMinion.setHeldItem(EnumHand.OFF_HAND, new ItemStack(Items.SHIELD));
						skeletonMinion.setItemStackToSlot(EntityEquipmentSlot.HEAD, new ItemStack(Items.LEATHER_HELMET));
						skeletonMinion.setItemStackToSlot(EntityEquipmentSlot.CHEST, new ItemStack(Items.LEATHER_CHESTPLATE));

						skeletonMinion.setDropChance(EntityEquipmentSlot.MAINHAND, 0.0f);
						skeletonMinion.setDropChance(EntityEquipmentSlot.OFFHAND, 0.0f);
						skeletonMinion.setDropChance(EntityEquipmentSlot.HEAD, 0.0f);
						skeletonMinion.setDropChance(EntityEquipmentSlot.CHEST, 0.0f);

						world.spawnEntity(skeletonMinion);
						// reset cooldown timer
						currentSummonCooldown = maxSummonCooldown;
					} else {
						for (int i = 0; i < 10; i++) {
							double x = pos.getX() + world.rand.nextDouble() * 2 - 1;
							double y = pos.getY() + 0.5 + world.rand.nextDouble();
							double z = pos.getZ() + world.rand.nextDouble() * 2 - 1;
							ParticleBuilder.create(ParticleBuilder.Type.SPARKLE).pos(x, y, z).clr(0.6f, 0.6f, 1).spawn(world);
						}
						currentSummonCooldown = maxSummonCooldown;
					}
				}
			}

			// Sentinel Eye Charm effect
			// mark entities if the Sentinel Eye artefact was used by the owner
			if (!world.isRemote && world.getTotalWorldTime() % 20 == 0 && markEntities) {
				target.addPotionEffect(new PotionEffect(MobEffects.GLOWING, 2400));
			}
			// ------------------

			if (world.getTotalWorldTime() % 50 == 0) {

				world.playSound((double) pos.getX(), (double) pos.getY(), (double) pos.getZ(), ASSounds.SKULL_WATCH_SCREAM, SoundCategory.BLOCKS, 1F, 1F, false);
				world.playSound((double) target.getPosition().getX(), (double) target.getPosition().getY(), (double) target.getPosition().getZ(), ASSounds.SKULL_WATCH_SCREAM, SoundCategory.PLAYERS, 0.3F, 1F, false);

				if (getCaster() != null && getCaster() instanceof EntityPlayer) {
					world.playSound((EntityPlayer) getCaster(), getCaster().getPosition(), ASSounds.SKULL_WATCH_ALARM, SoundCategory.PLAYERS, 0.3F, 1F);
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
			if (!triggered) {
				recentlyTriggered = true;
			}
			triggered = true;
		} else {
			this.tRot += 0.02F;
			this.bookSpread -= 0.1F;
			if (triggered) {
				recentlyTriggered = true;
			}
			triggered = false;
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

	public void setMarkEntities(boolean markEntities) {this.markEntities = markEntities;}

	public void setSummonSkeleton(boolean summonSkeleton) {this.summonSkeleton = summonSkeleton;}

	@Override
	public void readFromNBT(NBTTagCompound tagCompound) {
		super.readFromNBT(tagCompound);
		if (tagCompound.hasKey(MARK_ENTITIES_TAG)) {
			markEntities = tagCompound.getBoolean(MARK_ENTITIES_TAG);
		}
		if (tagCompound.hasKey(SUMMON_SKELETON_TAG)) {
			summonSkeleton = tagCompound.getBoolean(SUMMON_SKELETON_TAG);
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tagCompound) {
		tagCompound.setBoolean(MARK_ENTITIES_TAG, markEntities);
		tagCompound.setBoolean(SUMMON_SKELETON_TAG, summonSkeleton);
		return super.writeToNBT(tagCompound);
	}
}
