package com.windanesz.ancientspellcraft.entity.living;

import electroblob.wizardry.Wizardry;
import electroblob.wizardry.entity.living.EntitySpiderMinion;
import electroblob.wizardry.util.WizardryUtilities;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.UUID;

public class EntityFireAnt extends EntitySpiderMinion {

	private UUID origCasterUUID;

	public EntityFireAnt(World world) {
		super(world);
		this.isImmuneToFire = true;
		this.setSize(0.35F, 0.25F);
	}

	private void explode()
	{
		if (!this.world.isRemote)
		{
			boolean flag = net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.world, this);
			this.dead = true;
			this.world.createExplosion(this, this.posX, this.posY, this.posZ, (float) 1, flag);
			this.setDead();
		}
	}

	@Override
	public void onUpdate() {

		if (getCaster() == null) {
			explode();
		}
		//		for (int i = 0; i < 2; i++) {
		this.world.spawnParticle(EnumParticleTypes.FLAME,
				this.posX + (this.rand.nextDouble() - 0.5D) * (double) this.width,
				this.posY + this.height / 2 + this.rand.nextDouble() * (double) this.height / 2,
				this.posZ + (this.rand.nextDouble() - 0.5D) * (double) this.width, 0.0D, -0.05D, 0.0D);
		//		}

		super.onUpdate();
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound tagcompound){
		super.writeEntityToNBT(tagcompound);

		if(this.getOrigCaster() != null){
			tagcompound.setUniqueId("origCasterUUID", this.getOrigCaster().getUniqueID());
		}

		this.writeNBTDelegate(tagcompound);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbttagcompound){
		super.readEntityFromNBT(nbttagcompound);
		this.setOrigCasterUUID(nbttagcompound.getUniqueId("origCasterUUID"));
		this.readNBTDelegate(nbttagcompound);
	}

	@Override
	public void onSuccessfulAttack(EntityLivingBase target) {
		target.setFire(3);
	}


	@Override
	public boolean isValidTarget(Entity target) {

//		System.out.println("target:" + target);
//		System.out.println("this.getCaster():" + this.getCaster());
//		System.out.println("this.getCaster() instanceof ISummonedCreature:" +);
//		if (this.getCaster() != null && this.getCaster() instanceof ISummonedCreature && ((ISummonedCreature) this.getCaster()).getCaster() == target) {
//			return false;
//		}
		if (target == getOrigCaster()) {
			return false;
		}
		return super.isValidTarget(target);
	}

	public void setOrigCaster(@Nullable EntityLivingBase caster){
		setOrigCasterUUID(caster == null ? null : caster.getUniqueID());
	}

	public UUID getOrigCasterUUID() {
		return origCasterUUID;
	}

	public void setOrigCasterUUID(UUID origCasterUUID) {
		this.origCasterUUID = origCasterUUID;
	}

	@Nullable
	public EntityLivingBase getOrigCaster() { // Kept despite the above method because it returns an EntityLivingBase

		Entity entity = WizardryUtilities.getEntityByUUID(((Entity) this).world, getOrigCasterUUID());

		if (entity != null && !(entity instanceof EntityLivingBase)) { // Should never happen
			Wizardry.logger.warn("{} has a non-living owner!", this);
			return null;
		}

		return (EntityLivingBase) entity;
	}

}

