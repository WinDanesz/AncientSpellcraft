package com.windanesz.ancientspellcraft.entity.living;

import electroblob.wizardry.Wizardry;
import electroblob.wizardry.block.BlockReceptacle;
import electroblob.wizardry.constants.Element;
import electroblob.wizardry.entity.living.EntityZombieMinion;
import electroblob.wizardry.entity.living.ISummonedCreature;
import electroblob.wizardry.registry.WizardrySounds;
import electroblob.wizardry.util.ParticleBuilder;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.UUID;

public class EntityRemnantMinion extends EntityCreature implements ISummonedCreature, IEntityOwnable {

	/** Data parameter for the remnant's element. */
	private static final DataParameter<Integer> ELEMENT = EntityDataManager.createKey(EntityRemnantMinion.class, DataSerializers.VARINT);
	/** Data parameter that tracks whether the remnant is currently attacking (charging). */
	private static final DataParameter<Boolean> ATTACKING = EntityDataManager.createKey(EntityRemnantMinion.class, DataSerializers.BOOLEAN);


	private static final DataParameter<Boolean> SPAWN_PARTICLES = EntityDataManager.createKey(EntityZombieMinion.class, DataSerializers.BOOLEAN);

	// Field implementations
	private int lifetime = -1;
	private UUID casterUUID;

	// Setter + getter implementations
	@Override public int getLifetime(){ return lifetime; }
	@Override public void setLifetime(int lifetime){ this.lifetime = lifetime; }
	@Override public UUID getOwnerId(){ return casterUUID; }
	@Override public void setOwnerId(UUID uuid){ this.casterUUID = uuid; }


	private ResourceLocation lootTable;

	@Nullable
	private BlockPos boundOrigin;

	public EntityRemnantMinion(World world){
		super(world);
		this.setSize(0.8f, 0.8f);
		this.moveHelper = new EntityRemnantMinion.AIMoveControl(this);
		this.experienceValue = 0;
	}

	@Override
	protected void entityInit(){
		super.entityInit();
		this.dataManager.register(ELEMENT, 1); // Default to fire
		this.dataManager.register(ATTACKING, false); // Default to fire
		this.dataManager.register(SPAWN_PARTICLES, true);
	}

	@Override
	protected void initEntityAI(){
		super.initEntityAI();
		this.tasks.addTask(0, new EntityAISwimming(this));
		this.tasks.addTask(4, new EntityRemnantMinion.AIChargeAttack());
		this.tasks.addTask(8, new EntityRemnantMinion.AIMoveRandom());
		this.tasks.addTask(9, new EntityAIWatchClosest(this, EntityPlayer.class, 3.0F, 1.0F));
		this.tasks.addTask(10, new EntityAIWatchClosest(this, EntityLiving.class, 8.0F));
		this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true, EntityRemnantMinion.class));
		this.targetTasks.addTask(3, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, true));
	}

	@Override
	protected void applyEntityAttributes(){
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(16);
		this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(4);
	}

	@Nullable
	@Override
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata){
		this.setElement(Element.values()[1 + rand.nextInt(Element.values().length - 1)]); // Exclude MAGIC
		this.setBoundOrigin(new BlockPos(this));
		return super.onInitialSpawn(difficulty, livingdata);
	}

	public Element getElement(){
		return Element.values()[this.dataManager.get(ELEMENT)];
	}

	public void setElement(Element element){
		this.dataManager.set(ELEMENT, element.ordinal());
		this.lootTable = new ResourceLocation(Wizardry.MODID, "entities/remnant/" + element.getName());
	}

	public boolean isAttacking(){
		return this.dataManager.get(ATTACKING);
	}

	public void setAttacking(boolean attacking){
		this.dataManager.set(ATTACKING, attacking);
	}

	@Nullable
	public BlockPos getBoundOrigin(){
		return this.boundOrigin;
	}

	public void setBoundOrigin(@Nullable BlockPos boundOriginIn){
		this.boundOrigin = boundOriginIn;
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

		// Use the same trick as EntityVex to fly through stuff
		this.noClip = true;
		super.onUpdate();
		this.noClip = false;

		this.setNoGravity(true);

		if(world.isRemote){

			Vec3d centre = this.getPositionVector().add(0, height/2, 0);

			int[] colours = BlockReceptacle.PARTICLE_COLOURS.get(this.getElement());

			if(rand.nextInt(10) == 0){
				ParticleBuilder.create(ParticleBuilder.Type.FLASH).entity(this).pos(0, height/2, 0).scale(width).time(48).clr(colours[0]).spawn(world);
			}

			double r = width/3;

			double x = r * (rand.nextDouble() * 2 - 1);
			double y = r * (rand.nextDouble() * 2 - 1);
			double z = r * (rand.nextDouble() * 2 - 1);

			if(this.deathTime > 0){
				// Spew out particles on death
				for(int i = 0; i < 8; i++){
					ParticleBuilder.create(ParticleBuilder.Type.DUST, rand, centre.x + x, centre.y + y, centre.z + z, 0.1, true)
							.time(12).clr(colours[1]).fade(colours[2]).spawn(world);
				}
			}else{
				ParticleBuilder.create(ParticleBuilder.Type.DUST).pos(centre.x + x, centre.y + y, centre.z + z)
						.vel(x * -0.03, 0.02, z * -0.03).time(24 + rand.nextInt(8)).clr(colours[1]).fade(colours[2]).spawn(world);
			}
		}

	}

	@Override
	protected SoundEvent getAmbientSound(){
		return WizardrySounds.ENTITY_REMNANT_AMBIENT;
	}

	@Override
	protected SoundEvent getDeathSound(){
		return WizardrySounds.ENTITY_REMNANT_DEATH;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source){
		return WizardrySounds.ENTITY_REMNANT_HURT;
	}

	@Nullable
	@Override
	protected ResourceLocation getLootTable(){
		return lootTable;
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt){
		super.readEntityFromNBT(nbt);
		this.setElement(Element.values()[nbt.getInteger("Element")]);
		if(nbt.hasKey("BoundOrigin")) boundOrigin = NBTUtil.getPosFromTag(nbt.getCompoundTag("BoundOrigin"));
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt){
		super.writeEntityToNBT(nbt);
		nbt.setInteger("Element", this.getElement().ordinal());
		if(boundOrigin != null) nbt.setTag("BoundOrigin", NBTUtil.createPosTag(boundOrigin));
	}

	@Override
	public void onSpawn() {

	}

	@Override
	public void onDespawn() {

	}

	@Override
	public boolean hasParticleEffect() {
		return false;
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
//			EntityRemnant.this.playSound(SoundEvents.ENTITY_VEX_CHARGE, 1.0F, 1.0F);
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
