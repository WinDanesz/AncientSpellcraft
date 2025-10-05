package com.windanesz.ancientspellcraft.entity.living;

import electroblob.wizardry.block.BlockReceptacle;
import electroblob.wizardry.constants.Element;
import electroblob.wizardry.entity.living.EntityRemnant;
import electroblob.wizardry.entity.living.ISummonedCreature;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.ParticleBuilder;
import com.windanesz.wizardryutils.entity.ai.EntityAIMinionOwnerHurtByTarget;
import com.windanesz.wizardryutils.entity.ai.EntityAIMinionOwnerHurtTarget;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateFlying;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;

import com.google.common.base.Predicate;
import javax.annotation.Nullable;

import java.util.List;
import java.util.UUID;

public class EntityRemnantMinion extends EntityRemnant implements ISummonedCreature {

	private static final DataParameter<Boolean> SPAWN_PARTICLES = EntityDataManager.createKey(EntityRemnantMinion.class, DataSerializers.BOOLEAN);

	// Field implementations
	private int lifetime = -1;
	private UUID casterUUID;

	// Setter + getter implementations
	@Override public int getLifetime(){ return lifetime; }
	@Override public void setLifetime(int lifetime){ this.lifetime = lifetime; }
	@Override public UUID getOwnerId(){ return casterUUID; }
	@Override public void setOwnerId(UUID uuid){ this.casterUUID = uuid; }

	@Override
	public EntityLivingBase getCaster(){
		return casterUUID == null ? null : EntityUtils.getEntityByUUID(world, casterUUID) instanceof EntityLivingBase ? (EntityLivingBase) EntityUtils.getEntityByUUID(world, casterUUID) : null;
	}

	@Override
	public void setRevengeTarget(EntityLivingBase entity) {
		if (this.shouldRevengeTarget(entity)) { super.setRevengeTarget(entity); }
	}


	public EntityRemnantMinion(World world){
		super(world);
		this.moveHelper = new EntityRemnantMinion.AIMoveControl(this);
		this.experienceValue = 0;
	}

	@Override protected boolean canDropLoot(){ return false; }
	@Override protected Item getDropItem(){ return null; }
	@Override protected ResourceLocation getLootTable(){ return null; }
	@Override public boolean canPickUpLoot(){ return false; }

	protected PathNavigate createNavigator(World worldIn)
	{
		PathNavigateFlying pathnavigateflying = new PathNavigateFlying(this, worldIn);
		pathnavigateflying.setCanOpenDoors(false);
		pathnavigateflying.setCanFloat(true);
		pathnavigateflying.setCanEnterDoors(true);
		return pathnavigateflying;
	}

	@Override
	protected void entityInit(){
		super.entityInit();
		this.dataManager.register(SPAWN_PARTICLES, true);
	}

	@Override
	protected void initEntityAI(){
		this.tasks.addTask(0, new EntityAISwimming(this));
		this.tasks.addTask(4, new EntityRemnantMinion.AIChargeAttack());
		this.tasks.addTask(8, new EntityRemnantMinion.AIMoveRandom());
		this.tasks.addTask(9, new EntityAIWatchClosest(this, EntityPlayer.class, 3.0F, 1.0F));
		this.tasks.addTask(10, new EntityAIWatchClosest(this, EntityLiving.class, 8.0F));

		// Enhanced minion AI tasks from wizardryutils
		this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true, EntityRemnantMinion.class));
		this.targetTasks.addTask(2, new EntityAIMinionOwnerHurtByTarget(this));
		this.targetTasks.addTask(3, new EntityAIMinionOwnerHurtTarget(this));

		// Add the automatic target finding task - directly in initEntityAI like other minions
		this.targetTasks.addTask(4, new EntityAINearestAttackableTarget<EntityLivingBase>(this, EntityLivingBase.class,
				0, false, true, this.getTargetSelector()));
	}

	@Nullable
	@Override
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata){
		this.setElement(Element.values()[1 + rand.nextInt(Element.values().length - 1)]); // Exclude MAGIC
		this.setBoundOrigin(new BlockPos(this));
		return super.onInitialSpawn(difficulty, livingdata);
	}

	@Override
	public BlockPos getBoundOrigin(){
		if (this.getCaster() != null) {
			return new BlockPos(this.getCaster());
		}
		return super.getBoundOrigin();
	}

	@Override
	public float getBlockPathWeight(BlockPos pos){
		// Goddamnit Minecraft, stop imposing obscure spawning restrictions
		return 1; // This won't affect pathfinding since remnants are flying mobs anyway
	}

	@Override
	protected float applyPotionDamageCalculations(DamageSource source, float damage){
		damage = super.applyPotionDamageCalculations(source, damage);
		if(source.isMagicDamage()) damage *= 0.25f; // Remnants are 75% resistant to magic damage
		return damage;
	}

	@Override
	public void onUpdate(){
		super.onUpdate();
		this.updateDelegate();
	}


	@Override
	public void onSpawn() {
	}

	@Override
	public void onDespawn() {
		this.spawnParticleEffect();
	}

	private void spawnParticleEffect() {
		if (this.world.isRemote) {
			for (int i = 0; i < 15; i++) {
				this.world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, this.posX + this.rand.nextFloat() - 0.5f,
						this.posY + this.rand.nextFloat() * 2, this.posZ + this.rand.nextFloat() - 0.5f, 0, 0, 0);
			}
		}
	}

	@Override
	public boolean processInteract(EntityPlayer player, EnumHand hand) {
		// In this case, the delegate method determines whether super is called.
		// Rather handily, we can make use of Java's short-circuiting method of evaluating OR statements.
		return this.interactDelegate(player, hand) || super.processInteract(player, hand);
	}

	@Override
	public boolean hasParticleEffect() {
		return true;
	}

	@Override
	public boolean hasAnimation() {
		return this.dataManager.get(SPAWN_PARTICLES) || this.ticksExisted > 20;
	}

	public void hideParticles() {
		this.dataManager.set(SPAWN_PARTICLES, false);
	}

	// AI classes (copied from EntityVex)

	class AIChargeAttack extends EntityAIBase {

		public AIChargeAttack(){
			this.setMutexBits(1);
		}

		@Override
		public boolean shouldExecute(){
			if(EntityRemnantMinion.this.getAttackTarget() != null && !EntityRemnantMinion.this.getMoveHelper().isUpdating() && EntityRemnantMinion.this.rand.nextInt(7) == 0){
				return EntityRemnantMinion.this.getDistanceSq(EntityRemnantMinion.this.getAttackTarget()) > 4.0D;
			}else{
				return false;
			}
		}

		@Override
		public boolean shouldContinueExecuting(){
			return EntityRemnantMinion.this.getMoveHelper().isUpdating() && EntityRemnantMinion.this.isAttacking() && EntityRemnantMinion.this.getAttackTarget() != null && EntityRemnantMinion.this.getAttackTarget().isEntityAlive();
		}

		@Override
		public void startExecuting(){
			EntityLivingBase entitylivingbase = EntityRemnantMinion.this.getAttackTarget();
			if(entitylivingbase == null) return;
			Vec3d vec3d = entitylivingbase.getPositionEyes(1.0F);
			EntityRemnantMinion.this.moveHelper.setMoveTo(vec3d.x, vec3d.y, vec3d.z, 1.0D);
			EntityRemnantMinion.this.setAttacking(true);
//			EntityRemnantMinion.this.playSound(SoundEvents.ENTITY_VEX_CHARGE, 1.0F, 1.0F);
		}

		@Override
		public void resetTask(){
			EntityRemnantMinion.this.setAttacking(false);
		}

		@Override
		public void updateTask(){

			EntityLivingBase entitylivingbase = EntityRemnantMinion.this.getAttackTarget();

			if(entitylivingbase == null) return;

			if(EntityRemnantMinion.this.getEntityBoundingBox().intersects(entitylivingbase.getEntityBoundingBox())){
				EntityRemnantMinion.this.attackEntityAsMob(entitylivingbase);
				EntityRemnantMinion.this.setAttacking(false);
			}else{
				double d0 = EntityRemnantMinion.this.getDistanceSq(entitylivingbase);

				if(d0 < 9.0D){
					Vec3d vec3d = entitylivingbase.getPositionEyes(1.0F);
					EntityRemnantMinion.this.moveHelper.setMoveTo(vec3d.x, vec3d.y, vec3d.z, 1.0D);
				}
			}
		}
	}

	class AIMoveControl extends EntityMoveHelper {

		public AIMoveControl(EntityRemnantMinion host){
			super(host);
		}

		@Override
		public void onUpdateMoveHelper(){

			if(this.action == Action.MOVE_TO){

				double d0 = this.posX - EntityRemnantMinion.this.posX;
				double d1 = this.posY - EntityRemnantMinion.this.posY;
				double d2 = this.posZ - EntityRemnantMinion.this.posZ;
				double d3 = d0 * d0 + d1 * d1 + d2 * d2;

				d3 = MathHelper.sqrt(d3);

				if(d3 < EntityRemnantMinion.this.getEntityBoundingBox().getAverageEdgeLength()){

					this.action = Action.WAIT;
					EntityRemnantMinion.this.motionX *= 0.5D;
					EntityRemnantMinion.this.motionY *= 0.5D;
					EntityRemnantMinion.this.motionZ *= 0.5D;

				}else{

					EntityRemnantMinion.this.motionX += d0 / d3 * 0.05D * this.speed;
					EntityRemnantMinion.this.motionY += d1 / d3 * 0.05D * this.speed;
					EntityRemnantMinion.this.motionZ += d2 / d3 * 0.05D * this.speed;

					if(EntityRemnantMinion.this.getAttackTarget() == null){
						EntityRemnantMinion.this.rotationYaw = -((float)MathHelper.atan2(EntityRemnantMinion.this.motionX, EntityRemnantMinion.this.motionZ)) * (180F / (float)Math.PI);
					}else{
						double d4 = EntityRemnantMinion.this.getAttackTarget().posX - EntityRemnantMinion.this.posX;
						double d5 = EntityRemnantMinion.this.getAttackTarget().posZ - EntityRemnantMinion.this.posZ;
						EntityRemnantMinion.this.rotationYaw = -((float)MathHelper.atan2(d4, d5)) * (180F / (float)Math.PI);
					}

					EntityRemnantMinion.this.renderYawOffset = EntityRemnantMinion.this.rotationYaw;
				}
			}
		}
	}

	class AIMoveRandom extends EntityAIBase {

		public AIMoveRandom(){
			this.setMutexBits(1);
		}

		@Override
		public boolean shouldExecute(){
			return !EntityRemnantMinion.this.getMoveHelper().isUpdating() && EntityRemnantMinion.this.rand.nextInt(7) == 0;
		}

		@Override
		public boolean shouldContinueExecuting(){
			return false;
		}

		@Override
		public void updateTask(){

			BlockPos blockpos = EntityRemnantMinion.this.getBoundOrigin();

			if(blockpos == null){
				blockpos = new BlockPos(EntityRemnantMinion.this);
			}

			for(int i = 0; i < 3; ++i){
				BlockPos blockpos1 = blockpos.add(EntityRemnantMinion.this.rand.nextInt(15) - 7, EntityRemnantMinion.this.rand.nextInt(11) - 5, EntityRemnantMinion.this.rand.nextInt(15) - 7);

				if(EntityRemnantMinion.this.world.isAirBlock(blockpos1)){
					EntityRemnantMinion.this.moveHelper.setMoveTo((double)blockpos1.getX() + 0.5D, (double)blockpos1.getY() + 0.5D, (double)blockpos1.getZ() + 0.5D, 0.25D);

					if(EntityRemnantMinion.this.getAttackTarget() == null){
						EntityRemnantMinion.this.getLookHelper().setLookPosition((double)blockpos1.getX() + 0.5D, (double)blockpos1.getY() + 0.5D, (double)blockpos1.getZ() + 0.5D, 180.0F, 20.0F);
					}

					break;
				}
			}
		}
	}

}
