package com.windanesz.ancientspellcraft.entity.living;

import com.google.common.base.Optional;
import electroblob.wizardry.Wizardry;
import electroblob.wizardry.entity.living.ISummonedCreature;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.ParticleBuilder.Type;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIFollowOwner;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILeapAtTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIOwnerHurtByTarget;
import net.minecraft.entity.ai.EntityAIOwnerHurtTarget;
import net.minecraft.entity.ai.EntityAISit;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;

import java.util.UUID;

/**
 * Does not implement ISummonedCreature because it has different despawning rules and because EntityWolf already has an
 * owner system.
 */
public class EntityWolfMinion extends EntityWolf implements ISummonedCreature {

	// Field implementations
	private int lifetime = -1;
	private UUID casterUUID;

	public EntityWolfMinion(World world) {
		super(world);
		this.experienceValue = 0;
	}

	// Setter + getter implementations
	@Override
	public int getLifetime() { return lifetime; }

	@Override
	public void setLifetime(int lifetime) { this.lifetime = lifetime; }
	//	@Override public UUID getOwnerId(){ return casterUUID; }
	//	@Override public void setOwnerId(UUID uuid){ this.casterUUID = uuid; }

	public void setOwner(EntityLivingBase owner) {
		this.casterUUID = owner.getUniqueID();
		this.dataManager.set(OWNER_UNIQUE_ID, Optional.fromNullable(owner.getUniqueID()));
	}

	@Override
	public void readSpawnData(ByteBuf buffer) {
		int id = buffer.readInt();
		// We're on the client side here, so we can safely use Minecraft.getMinecraft().world via proxies.
		if (id > -1) {
			Entity entity = Wizardry.proxy.getTheWorld().getEntityByID(id);
			if (entity instanceof EntityLivingBase)
				setOwner((EntityLivingBase) entity);
			else
				Wizardry.logger.warn("Received a spawn packet for entity {}, but no living entity matched the supplied ID", this);
		}
		setLifetime(buffer.readInt());
	}

	@Override
	public void onSpawn() {
		this.spawnParticleEffect();
	}

	@Override
	public void onDespawn() {
		this.spawnParticleEffect();
	}

	private void spawnParticleEffect() {
		if (this.world.isRemote) {
			for (int i = 0; i < 15; i++) {
				this.world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, this.posX + (double) this.rand.nextFloat(),
						this.posY + 1 + (double) this.rand.nextFloat(),
						this.posZ + (double) this.rand.nextFloat(), 0, 0, 0);
			}
		}
	}

	@Override
	public boolean hasParticleEffect() {
		return false;
	}

	@Override
	protected void initEntityAI() {

		this.aiSit = new EntityAISit(this);
		this.tasks.addTask(2, this.aiSit);
		this.tasks.addTask(3, new EntityAILeapAtTarget(this, 0.4F));
		this.tasks.addTask(4, new EntityAIAttackMelee(this, 1.0D, true));
		this.tasks.addTask(5, new EntityAIFollowOwner(this, 1.0D, 10.0F, 2.0F));
		this.tasks.addTask(7, new EntityAIWander(this, 1.0D));
		this.tasks.addTask(9, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
		this.tasks.addTask(9, new EntityAILookIdle(this));
		this.targetTasks.addTask(1, new EntityAIOwnerHurtByTarget(this));
		this.targetTasks.addTask(2, new EntityAIOwnerHurtTarget(this));
		this.targetTasks.addTask(3, new EntityAIHurtByTarget(this, true, new Class[0]));
		this.targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityLivingBase.class,
				0, false, true, this.getTargetSelector()));
	}

	@Override
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {

		// Adds Particles on spawn. Due to client/server differences this cannot be done
		// in the item.
		if (this.world.isRemote) {
			this.spawnAppearParticles();
		}
		if (this.getHealth() < this.getMaxHealth()) {
			this.setHealth(this.getMaxHealth());
		}

		return livingdata;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		this.updateDelegate();
	}

	@Override
	public boolean processInteract(EntityPlayer player, EnumHand hand) {
		// In this case, the delegate method determines whether super is called.
		// Rather handily, we can make use of Java's short-circuiting method of evaluating OR statements.
		return this.interactDelegate(player, hand) || super.processInteract(player, hand);
	}

	private void spawnAppearParticles() {
		for (int i = 0; i < 15; i++) {
			double x = this.posX - this.width / 2 + this.rand.nextFloat() * width;
			double y = this.posY + this.height * this.rand.nextFloat() + 0.2f;
			double z = this.posZ - this.width / 2 + this.rand.nextFloat() * width;
			ParticleBuilder.create(Type.SPARKLE).pos(x, y, z).clr(0, 67, 0).spawn(world);
		}
	}

	@Override
	public EntityWolf createChild(EntityAgeable par1EntityAgeable) {
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
	protected ResourceLocation getLootTable() {
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

	@Override
	public void setTamedBy(EntityPlayer player) {
		super.setTamedBy(player);
		setCaster(getOwner());
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound compound) {
		super.readEntityFromNBT(compound);
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);
	}
}
