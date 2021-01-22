package com.windanesz.ancientspellcraft.entity.living;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import electroblob.wizardry.entity.construct.EntityBlackHole;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAICreeperSwell;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public class EntityVoidCreeper extends EntityCreeper {

	private static final ResourceLocation LOOT_TABLE = new ResourceLocation(AncientSpellcraft.MODID, "entities/void_creeper");

	private static final DataParameter<Integer> STATE = EntityDataManager.createKey(EntityVoidCreeper.class, DataSerializers.VARINT);
	private static final DataParameter<Boolean> POWERED = EntityDataManager.createKey(EntityVoidCreeper.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> IGNITED = EntityDataManager.createKey(EntityVoidCreeper.class, DataSerializers.BOOLEAN);
	/**
	 * Time when this creeper was last in an active state (Messed up code here, probably causes creeper animation to go
	 * weird)
	 */
	private int lastActiveTime;
	/**
	 * The amount of time since the creeper was close enough to the player to ignite
	 */
	private int timeSinceIgnited;
	private int fuseTime = 30;
	/**
	 * Explosion radius for this creeper.
	 */
	private int explosionRadius = 3;

	public EntityVoidCreeper(World worldIn) {
		super(worldIn);
		this.setSize(0.6F, 1.7F);

	}

	protected void initEntityAI() {
		this.tasks.addTask(1, new EntityAISwimming(this));
		this.tasks.addTask(2, new EntityAICreeperSwell(this));
		this.tasks.addTask(3, new EntityAIAvoidEntity<>(this, EntityOcelot.class, 6.0F, 1.0D, 1.2D));
		this.tasks.addTask(4, new EntityAIAttackMelee(this, 1.0D, false));
		this.tasks.addTask(5, new EntityAIWanderAvoidWater(this, 0.8D));
		this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
		this.tasks.addTask(6, new EntityAILookIdle(this));
		this.targetTasks.addTask(1, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, true));
		this.targetTasks.addTask(2, new EntityAIHurtByTarget(this, false));
	}

	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
	}

	/**
	 * The maximum height from where the entity is alowed to jump (used in pathfinder)
	 */
	public int getMaxFallHeight() {
		return this.getAttackTarget() == null ? 3 : 3 + (int) (this.getHealth() - 1.0F);
	}

	public void fall(float distance, float damageMultiplier) {
		super.fall(distance, damageMultiplier);
		this.timeSinceIgnited = (int) ((float) this.timeSinceIgnited + distance * 1.5F);

		if (this.timeSinceIgnited > this.fuseTime - 5) {
			this.timeSinceIgnited = this.fuseTime - 5;
		}
	}

	protected void entityInit() {
		super.entityInit();
		this.dataManager.register(STATE, -1);
		this.dataManager.register(POWERED, Boolean.FALSE);
		this.dataManager.register(IGNITED, Boolean.FALSE);
	}

	/**
	 * (abstract) Protected helper method to write subclass entity data to NBT.
	 */
	public void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);

		if (this.dataManager.get(POWERED)) {
			compound.setBoolean("powered", true);
		}

		compound.setShort("Fuse", (short) this.fuseTime);
		compound.setByte("ExplosionRadius", (byte) this.explosionRadius);
		compound.setBoolean("ignited", this.hasIgnited());
	}

	/**
	 * (abstract) Protected helper method to read subclass entity data from NBT.
	 */
	public void readEntityFromNBT(NBTTagCompound compound) {
		super.readEntityFromNBT(compound);
		this.dataManager.set(POWERED, compound.getBoolean("powered"));

		if (compound.hasKey("Fuse", 99)) {
			this.fuseTime = compound.getShort("Fuse");
		}

		if (compound.hasKey("ExplosionRadius", 99)) {
			this.explosionRadius = compound.getByte("ExplosionRadius");
		}

		if (compound.getBoolean("ignited")) {
			this.ignite();
		}
	}

	/**
	 * Called to update the entity's position/logic.
	 */
	public void onUpdate() {
		if (this.isEntityAlive()) {
			this.lastActiveTime = this.timeSinceIgnited;

			if (this.hasIgnited()) {
				this.setCreeperState(1);
			}

			int i = this.getCreeperState();

			if (i > 0 && this.timeSinceIgnited == 0) {
				this.playSound(SoundEvents.ENTITY_CREEPER_PRIMED, 1.0F, 0.5F);
			}

			this.timeSinceIgnited += i;

			if (this.timeSinceIgnited < 0) {
				this.timeSinceIgnited = 0;
			}

			if (this.timeSinceIgnited >= this.fuseTime) {
				this.timeSinceIgnited = this.fuseTime;
				this.explode();
			}
		}

		super.onUpdate();
	}

	protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
		return SoundEvents.ENTITY_CREEPER_HURT;
	}

	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_CREEPER_DEATH;
	}

	/**
	 * Called when the mob's health reaches 0.
	 * Vanilla method from EntityLivingBase to bypass EntityCreeper override
	 */
	public void onDeath(DamageSource cause) {
		if (net.minecraftforge.common.ForgeHooks.onLivingDeath(this, cause))
			return;
		if (!this.dead) {
			Entity entity = cause.getTrueSource();
			EntityLivingBase entitylivingbase = this.getAttackingEntity();

			if (this.scoreValue >= 0 && entitylivingbase != null) {
				entitylivingbase.awardKillScore(this, this.scoreValue, cause);
			}

			if (entity != null) {
				entity.onKillEntity(this);
			}

			this.dead = true;
			this.getCombatTracker().reset();

			if (!this.world.isRemote) {
				int i = net.minecraftforge.common.ForgeHooks.getLootingLevel(this, entity, cause);

				captureDrops = true;
				capturedDrops.clear();

				if (this.canDropLoot() && this.world.getGameRules().getBoolean("doMobLoot")) {
					boolean flag = this.recentlyHit > 0;
					this.dropLoot(flag, i, cause);
				}

				captureDrops = false;

				if (!net.minecraftforge.common.ForgeHooks.onLivingDrops(this, cause, capturedDrops, i, recentlyHit > 0)) {
					for (EntityItem item : capturedDrops) {
						world.spawnEntity(item);
					}
				}
			}

			this.world.setEntityState(this, (byte) 3);
		}
	}

	public boolean attackEntityAsMob(Entity entityIn) {
		return true;
	}

	/**
	 * Returns true if the creeper is powered.
	 */
	public boolean getPowered() {
		return this.dataManager.get(POWERED);
	}

	/**
	 * Params: (Float)Render tick. Returns the intensity of the creeper's flash when it is ignited.
	 */
	@SideOnly(Side.CLIENT)
	public float getCreeperFlashIntensity(float p_70831_1_) {
		return ((float) this.lastActiveTime + (float) (this.timeSinceIgnited - this.lastActiveTime) * p_70831_1_) / (float) (this.fuseTime - 2);
	}

	@Override
	protected ResourceLocation getLootTable() {
		return LOOT_TABLE;
	}

	/**
	 * Returns the current state of creeper, -1 is idle, 1 is 'in fuse'
	 */
	public int getCreeperState() {
		return this.dataManager.get(STATE);
	}

	/**
	 * Sets the state of creeper, -1 to idle and 1 to be 'in fuse'
	 */
	public void setCreeperState(int state) {
		this.dataManager.set(STATE, state);
	}

	/**
	 * Called when a lightning bolt hits the entity.
	 */
	@Override
	public void onStruckByLightning(EntityLightningBolt lightningBolt) {
		super.onStruckByLightning(lightningBolt);
		this.dataManager.set(POWERED, Boolean.TRUE);
	}

	@Override
	protected boolean processInteract(EntityPlayer player, EnumHand hand) {
		return false;
	}

	private void explode() {
		if (!this.world.isRemote) {
			boolean flag = net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.world, this);
			float f = this.getPowered() ? 2.0F : 1.0F; // will be used for block destroying black holes
			this.dead = true;
			SpellModifiers modifier = new SpellModifiers();
			modifier.set("potency", 0.45f, true);
			createBlackHole(world, this.posX, this.posY + 1, this.posZ, this, modifier);
			this.setDead();
		}
	}

	public boolean hasIgnited() {
		return this.dataManager.get(IGNITED);
	}

	public void ignite() {
		this.dataManager.set(IGNITED, Boolean.TRUE);
	}

	public void incrementDroppedSkulls() {
	}

	private void createBlackHole(World world, double x, double y, double z,
			@Nullable EntityLivingBase caster, SpellModifiers modifiers) {
		if (!world.isRemote) {
			// Creates a new black hole using the supplied factory
			EntityBlackHole construct = new EntityBlackHole(world);
			// Sets the position of the construct (and initialises its bounding box)
			construct.setPosition(x, y, z);
			// Sets the various parameters
			construct.setCaster(caster);
			construct.lifetime = 140;
			construct.damageMultiplier = modifiers.get(SpellModifiers.POTENCY);
			world.spawnEntity(construct);
		}

	}

	@Override
	protected float applyPotionDamageCalculations(DamageSource source, float damage){
		damage = super.applyPotionDamageCalculations(source, damage);
		if(source.isMagicDamage()) damage *= 0.25f;
		return damage;
	}

	@Override
	public boolean getCanSpawnHere(){
		return super.getCanSpawnHere() && this.dimension == 0;
	}

}
