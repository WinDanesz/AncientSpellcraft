package com.windanesz.ancientspellcraft.entity.living;

import com.google.common.base.Optional;
import electroblob.wizardry.Wizardry;
import electroblob.wizardry.entity.living.ISummonedCreature;
import electroblob.wizardry.item.ISpellCastingItem;
import electroblob.wizardry.registry.WizardrySounds;
import electroblob.wizardry.util.ParticleBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityPolarBear;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateFlying;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.UUID;

public class EntitySpiritBear extends EntityPolarBear implements IEntityOwnable {

	private int dispelTimer = 0;

	private UUID ownerUUID;
	protected static final DataParameter<Optional<UUID>> OWNER_UNIQUE_ID = EntityDataManager.<Optional<UUID>>createKey(EntityTameable.class, DataSerializers.OPTIONAL_UNIQUE_ID);

	private static final int DISPEL_TIME = 10;

	public EntitySpiritBear(World worldIn) {
		super(worldIn);
		this.setSize(1.3F, 1.4F);
		this.experienceValue = 0;
	}

	protected void initEntityAI() {

		this.tasks.addTask(1, new EntityAISwimming(this));
		this.tasks.addTask(4, new EntityAIAttackMelee(this, 1.0D, true));
		this.tasks.addTask(5, new SpiritBearAIFollowOwner(this, 1.0D, 10.0F, 2.0F));
		this.tasks.addTask(7, new EntityAIWander(this, 1.0D));
		this.tasks.addTask(9, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
		this.tasks.addTask(9, new EntityAILookIdle(this));
		this.targetTasks.addTask(1, new SpiritBearAIOwnerHurtByTarget(this));
		this.targetTasks.addTask(2, new SpiritBearIOwnerHurtTarget(this));
		this.targetTasks.addTask(3, new EntityAIHurtByTarget(this, true, new Class[0]));
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		this.dataManager.register(OWNER_UNIQUE_ID, Optional.absent());
	}

	@Override
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {

		// Adds Particles on spawn. Due to client/server differences this cannot be done
		// in the item.
		if (this.world.isRemote) {
			this.spawnAppearParticles();
		}

		return livingdata;
	}

	@Nullable
	public UUID getOwnerId() {
		return (UUID) ((Optional) this.dataManager.get(OWNER_UNIQUE_ID)).orNull();
	}


	@Nullable
	public EntityLivingBase getOwner()
	{
		try
		{
			UUID uuid = this.getOwnerId();
			return uuid == null ? null : this.world.getPlayerEntityByUUID(uuid);
		}
		catch (IllegalArgumentException var2)
		{
			return null;
		}
	}

	public void setOwnerId(@Nullable UUID ownerId) {
		this.dataManager.set(OWNER_UNIQUE_ID, Optional.fromNullable(ownerId));
	}

	public float getOpacity() {
		return 1 - (float) dispelTimer / DISPEL_TIME;
	}

	/**
	 * Called to update the entity's position/logic.
	 */

	@Override
	public void onUpdate() {

		super.onUpdate();

		if (dispelTimer > 0) {
			if (dispelTimer++ > DISPEL_TIME) {
				this.setDead();
			}
		}

		// Adds a dust particle effect
		if (this.world.isRemote) {
			double x = this.posX - this.width / 2 + this.rand.nextFloat() * width;
			double y = this.posY + this.height * this.rand.nextFloat() + 0.2f;
			double z = this.posZ - this.width / 2 + this.rand.nextFloat() * width;
			ParticleBuilder.create(ParticleBuilder.Type.DUST).pos(x, y, z).clr(0.8f, 0.8f, 1.0f).shaded(true).spawn(world);
		}
	}

	@Override
	public boolean processInteract(EntityPlayer player, EnumHand hand) {

		ItemStack stack = player.getHeldItem(hand);

		if (this.getOwnerId() == player.getUniqueID()) {

			// Allows the owner (but not other players) to dispel the spirit wolf using a
			// wand.
			if (stack.getItem() instanceof ISpellCastingItem && this.getOwner() == player && player.isSneaking()) {
				// Prevents accidental double clicking.
				if (this.ticksExisted > 20) {

					this.dispelTimer++;

					// TODO other sound?
					this.playSound(WizardrySounds.ENTITY_SPIRIT_WOLF_VANISH, 0.7F, rand.nextFloat() * 0.4F + 1.0F);
					// This is necessary to prevent the wand's spell being cast when performing this
					// action.
					return true;
				}
			}
		}

		return super.processInteract(player, hand);

	}

	@Override
	public EntityWolf createChild(EntityAgeable ageable) {
		return null;
	}

	@Override
	protected int getExperiencePoints(EntityPlayer player) {
		return 0;
	}

	@Override
	protected boolean canDropLoot() {
		return false;
	}

	@Override
	protected Item getDropItem() {
		return null;
	}

	@Override
	public ITextComponent getDisplayName() {
		if (getOwner() != null) {
			return new TextComponentTranslation(ISummonedCreature.NAMEPLATE_TRANSLATION_KEY, getOwner().getName(),
					new TextComponentTranslation("entity." + this.getEntityString() + ".name"));
		} else {
			return super.getDisplayName();
		}
	}

	@Override
	public boolean hasCustomName() {
		// If this returns true, the renderer will show the nameplate when looking
		// directly at the entity
		return Wizardry.settings.summonedCreatureNames && getOwner() != null;
	}

	/**
	 * Checks if the parameter is an item which this animal can be fed to breed it (wheat, carrots or seeds depending on
	 * the animal type)
	 */
	public boolean isBreedingItem(ItemStack stack) {
		return false;
	}

	protected SoundEvent getAmbientSound() {
		return SoundEvents.ENTITY_POLAR_BEAR_AMBIENT;
	}

	protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
		return SoundEvents.ENTITY_POLAR_BEAR_HURT;
	}

	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_POLAR_BEAR_DEATH;
	}

	protected void playStepSound(BlockPos pos, Block blockIn) {
		this.playSound(SoundEvents.ENTITY_POLAR_BEAR_STEP, 0.15F, 1.0F);
	}

	protected void playWarningSound() {
		super.playWarningSound();
	}

	@Override
	protected ResourceLocation getLootTable() {
		return null;
	}

	public boolean attackEntityAsMob(Entity entityIn) {
		boolean flag = entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), (float) ((int) this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue()));

		if (flag) {
			this.applyEnchantments(this, entityIn);
		}

		return flag;
	}

	private void spawnAppearParticles() {
		for (int i = 0; i < 15; i++) {
			double x = this.posX - this.width / 2 + this.rand.nextFloat() * width;
			double y = this.posY + this.height * this.rand.nextFloat() + 0.2f;
			double z = this.posZ - this.width / 2 + this.rand.nextFloat() * width;
			ParticleBuilder.create(ParticleBuilder.Type.SPARKLE).pos(x, y, z).clr(0.8f, 0.8f, 1.0f).spawn(world);
		}
	}

	class SpiritBearAIFollowOwner extends EntityAIBase {
		private final EntitySpiritBear bear;
		private EntityLivingBase owner;
		World world;
		private final double followSpeed;
		private final PathNavigate petPathfinder;
		private int timeToRecalcPath;
		float maxDist;
		float minDist;
		private float oldWaterCost;

		public SpiritBearAIFollowOwner(EntitySpiritBear bear, double followSpeedIn, float minDistIn, float maxDistIn) {
			this.bear = bear;
			this.world = bear.world;
			this.followSpeed = followSpeedIn;
			this.petPathfinder = bear.getNavigator();
			this.minDist = minDistIn;
			this.maxDist = maxDistIn;
			this.setMutexBits(3);

			if (!(bear.getNavigator() instanceof PathNavigateGround) && !(bear.getNavigator() instanceof PathNavigateFlying)) {
				throw new IllegalArgumentException("Unsupported mob type for FollowOwnerGoal");
			}
		}

		/**
		 * Returns whether the EntityAIBase should begin execution.
		 */
		public boolean shouldExecute() {
			EntityLivingBase entitylivingbase = (EntityLivingBase) this.bear.getOwner();

			if (entitylivingbase == null) {
				return false;
			} else if (entitylivingbase instanceof EntityPlayer && ((EntityPlayer) entitylivingbase).isSpectator()) {
				return false;
			} else if (this.bear.getDistanceSq(entitylivingbase) < (double) (this.minDist * this.minDist)) {
				return false;
			} else {
				this.owner = entitylivingbase;
				return true;
			}
		}

		/**
		 * Returns whether an in-progress EntityAIBase should continue executing
		 */
		public boolean shouldContinueExecuting() {
			return !this.petPathfinder.noPath() && this.bear.getDistanceSq(this.owner) > (double) (this.maxDist * this.maxDist);
		}

		/**
		 * Execute a one shot task or start executing a continuous task
		 */
		public void startExecuting() {
			this.timeToRecalcPath = 0;
			this.oldWaterCost = this.bear.getPathPriority(PathNodeType.WATER);
			this.bear.setPathPriority(PathNodeType.WATER, 0.0F);
		}

		/**
		 * Reset the task's internal state. Called when this task is interrupted by another one
		 */
		public void resetTask() {
			this.owner = null;
			this.petPathfinder.clearPath();
			this.bear.setPathPriority(PathNodeType.WATER, this.oldWaterCost);
		}

		/**
		 * Keep ticking a continuous task that has already been started
		 */
		public void updateTask() {
			this.bear.getLookHelper().setLookPositionWithEntity(this.owner, 10.0F, (float) this.bear.getVerticalFaceSpeed());

			if (--this.timeToRecalcPath <= 0) {
				this.timeToRecalcPath = 10;

				if (!this.petPathfinder.tryMoveToEntityLiving(this.owner, this.followSpeed)) {
					if (!this.bear.getLeashed() && !this.bear.isRiding()) {
						if (this.bear.getDistanceSq(this.owner) >= 144.0D) {
							int i = MathHelper.floor(this.owner.posX) - 2;
							int j = MathHelper.floor(this.owner.posZ) - 2;
							int k = MathHelper.floor(this.owner.getEntityBoundingBox().minY);

							for (int l = 0; l <= 4; ++l) {
								for (int i1 = 0; i1 <= 4; ++i1) {
									if ((l < 1 || i1 < 1 || l > 3 || i1 > 3) && this.isTeleportFriendlyBlock(i, j, k, l, i1)) {
										this.bear.setLocationAndAngles((double) ((float) (i + l) + 0.5F), (double) k, (double) ((float) (j + i1) + 0.5F), this.bear.rotationYaw, this.bear.rotationPitch);
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
			return iblockstate.getBlockFaceShape(this.world, blockpos, EnumFacing.DOWN) == BlockFaceShape.SOLID && iblockstate.canEntitySpawn(this.bear) && this.world.isAirBlock(blockpos.up()) && this.world.isAirBlock(blockpos.up(2));
		}

	}

	public class SpiritBearAIOwnerHurtByTarget extends EntityAITarget {
		EntitySpiritBear bear;
		EntityLivingBase attacker;
		private int timestamp;

		public SpiritBearAIOwnerHurtByTarget(EntitySpiritBear bear) {
			super(bear, false);
			this.bear = bear;
			this.setMutexBits(1);
		}

		/**
		 * Returns whether the EntityAIBase should begin execution.
		 */
		public boolean shouldExecute() {
			EntityLivingBase entitylivingbase = (EntityLivingBase) this.bear.getOwner();

			if (entitylivingbase == null) {
				return false;
			} else {
				this.attacker = entitylivingbase.getRevengeTarget();
				int i = entitylivingbase.getRevengeTimer();
				return i != this.timestamp && this.isSuitableTarget(this.attacker, false);
			}
		}

		/**
		 * Execute a one shot task or start executing a continuous task
		 */
		public void startExecuting() {
			this.taskOwner.setAttackTarget(this.attacker);
			EntityLivingBase entitylivingbase = (EntityLivingBase) this.bear.getOwner();

			if (entitylivingbase != null) {
				this.timestamp = entitylivingbase.getRevengeTimer();
			}

			super.startExecuting();
		}
	}

	public class SpiritBearIOwnerHurtTarget extends EntityAITarget {
		EntitySpiritBear bear;
		EntityLivingBase attacker;
		private int timestamp;

		public SpiritBearIOwnerHurtTarget(EntitySpiritBear theEntityTameableIn) {
			super(theEntityTameableIn, false);
			this.bear = theEntityTameableIn;
			this.setMutexBits(1);
		}

		/**
		 * Returns whether the EntityAIBase should begin execution.
		 */
		public boolean shouldExecute() {
			EntityLivingBase entitylivingbase = (EntityLivingBase) this.bear.getOwner();

			if (entitylivingbase == null) {
				return false;
			} else {
				this.attacker = entitylivingbase.getLastAttackedEntity();
				int i = entitylivingbase.getLastAttackedEntityTime();
				return i != this.timestamp && this.isSuitableTarget(this.attacker, false);
			}
		}

		/**
		 * Execute a one shot task or start executing a continuous task
		 */
		public void startExecuting() {
			this.taskOwner.setAttackTarget(this.attacker);
			EntityLivingBase entitylivingbase = (EntityLivingBase) this.bear.getOwner();

			if (entitylivingbase != null) {
				this.timestamp = entitylivingbase.getLastAttackedEntityTime();
			}

			super.startExecuting();
		}
	}
}