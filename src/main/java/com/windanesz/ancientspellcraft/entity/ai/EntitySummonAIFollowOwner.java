package com.windanesz.ancientspellcraft.entity.ai;

import electroblob.wizardry.entity.living.ISummonedCreature;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateFlying;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

/**
 * AI class based on {@link net.minecraft.entity.ai.EntityAIBase}, which makes summons follow their owners
 */
public class EntitySummonAIFollowOwner extends EntityAIBase {
	private final EntityCreature creature;
	private final ISummonedCreature summon;
	private EntityLivingBase owner;
	World world;
	private final double followSpeed;
	private final PathNavigate petPathfinder;
	private int timeToRecalcPath;
	float maxDist;
	float minDist;
	private float oldWaterCost;

	public EntitySummonAIFollowOwner(EntityCreature summonedCreature, double followSpeedIn, float minDistIn, float maxDistIn) {
		this.creature = summonedCreature;
		this.world = summonedCreature.world;
		this.followSpeed = followSpeedIn;
		this.petPathfinder = summonedCreature.getNavigator();
		this.minDist = minDistIn;
		this.maxDist = maxDistIn;
		this.setMutexBits(3);

		if (!(summonedCreature instanceof ISummonedCreature)) {
			throw new IllegalArgumentException("Entities with EntitySummonAIFollowOwner must implement ISummonedCreature!");
		}

		this.summon = (ISummonedCreature) summonedCreature;

		if (!(summonedCreature.getNavigator() instanceof PathNavigateGround) && !(summonedCreature.getNavigator() instanceof PathNavigateFlying)) {
			throw new IllegalArgumentException("Unsupported mob type for FollowOwnerGoal");
		}
	}

	/**
	 * Returns whether the EntityAIBase should begin execution.
	 */
	public boolean shouldExecute() {
		EntityLivingBase ownerCaster = this.summon.getCaster();

		if (ownerCaster == null) {
			return false;
		} else if (ownerCaster instanceof EntityPlayer && ((EntityPlayer) ownerCaster).isSpectator()) {
			return false;
		} else if (this.creature.getDistanceSq(ownerCaster) < (double) (this.minDist * this.minDist)) {
			return false;
		} else {
			this.owner = ownerCaster;
			return true;
		}
	}

	/**
	 * Returns whether an in-progress EntityAIBase should continue executing
	 */
	public boolean shouldContinueExecuting() {
		return !this.petPathfinder.noPath() && this.creature.getDistanceSq(this.owner) > (double) (this.maxDist * this.maxDist);
	}

	/**
	 * Execute a one shot task or start executing a continuous task
	 */
	public void startExecuting() {
		this.timeToRecalcPath = 0;
		this.oldWaterCost = this.creature.getPathPriority(PathNodeType.WATER);
		this.creature.setPathPriority(PathNodeType.WATER, 0.0F);
	}

	/**
	 * Reset the task's internal state. Called when this task is interrupted by another one
	 */
	public void resetTask() {
		this.owner = null;
		this.petPathfinder.clearPath();
		this.creature.setPathPriority(PathNodeType.WATER, this.oldWaterCost);
	}

	/**
	 * Keep ticking a continuous task that has already been started
	 */
	public void updateTask() {
		this.creature.getLookHelper().setLookPositionWithEntity(this.owner, 10.0F, (float) this.creature.getVerticalFaceSpeed());

		if (--this.timeToRecalcPath <= 0) {
			this.timeToRecalcPath = 10;

			if (!this.petPathfinder.tryMoveToEntityLiving(this.owner, this.followSpeed)) {
				if (!this.creature.getLeashed() && !this.creature.isRiding()) {
					if (this.creature.getDistanceSq(this.owner) >= 144.0D) {
						int i = MathHelper.floor(this.owner.posX) - 2;
						int j = MathHelper.floor(this.owner.posZ) - 2;
						int k = MathHelper.floor(this.owner.getEntityBoundingBox().minY);

						for (int l = 0; l <= 4; ++l) {
							for (int i1 = 0; i1 <= 4; ++i1) {
								if ((l < 1 || i1 < 1 || l > 3 || i1 > 3) && this.isTeleportFriendlyBlock(i, j, k, l, i1)) {
									this.creature.setLocationAndAngles((double) ((float) (i + l) + 0.5F), (double) k, (double) ((float) (j + i1) + 0.5F), this.creature.rotationYaw, this.creature.rotationPitch);
									this.petPathfinder.clearPath();
									return;
								}
							}
						}
					}
				}
			}
		}
	}

	protected boolean isTeleportFriendlyBlock(int x, int z, int y, int xOffset, int zOffset) {
		BlockPos blockpos = new BlockPos(x + xOffset, y - 1, z + zOffset);
		IBlockState iblockstate = this.world.getBlockState(blockpos);
		return iblockstate.getBlockFaceShape(this.world, blockpos, EnumFacing.DOWN) == BlockFaceShape.SOLID && iblockstate.canEntitySpawn(this.creature) && this.world.isAirBlock(blockpos.up()) && this.world.isAirBlock(blockpos.up(2));
	}
}