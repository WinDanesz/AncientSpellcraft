package com.windanesz.ancientspellcraft.entity.projectile;

import com.windanesz.ancientspellcraft.registry.AncientSpellcraftSounds;
import com.windanesz.ancientspellcraft.spell.Contingency;
import electroblob.wizardry.entity.projectile.EntityMagicProjectile;
import electroblob.wizardry.registry.Spells;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.ParticleBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EntityContingencyProjectile extends EntityMagicProjectile {

	private int lifetime = 16;
	private Spell storedSpell = Spells.none;
	private Contingency.Type contingencyType = Contingency.Type.DAMAGE;

	public EntityContingencyProjectile(World world) {
		super(world);
		this.setSize(0.7f, 0.7f);

	}

	public void setContingencyType(Contingency.Type contingencyType) {
		this.contingencyType = contingencyType;
	}

	public void setStoredSpell(Spell storedSpell) {
		this.storedSpell = storedSpell;
	}

	@Override
	protected void onImpact(RayTraceResult rayTrace) {

		if (!world.isRemote) {

			Entity entity = rayTrace.entityHit;

			if (entity != null) {

				// magic projectile collision, it should delete the projectile entity
				if (entity instanceof EntityLivingBase) {
					if (thrower != null) {
						Contingency.setContingencyTag((EntityLivingBase) entity, thrower, contingencyType, storedSpell);
					} else {
						Contingency.setContingencyTag((EntityLivingBase) entity, (EntityLivingBase) entity, contingencyType, storedSpell);
					}
				}
			} //	else - block hit

		}

		world.playSound(this.posX, this.posY, this.posZ, AncientSpellcraftSounds.DISPEL, SoundCategory.HOSTILE, 1, 1, false);
		this.setDead();
	}

	@Override
	public void onUpdate() {

		super.onUpdate();

		if (world.isRemote) {

			for (int i = 0; i < 5; i++) {

				if (ticksExisted > 1) {

					ParticleBuilder.create(ParticleBuilder.Type.SPARKLE, rand, posX, posY, posZ, 0.03, true).clr(210, 255, 255).fade(0.2f, 0.2f, 0.2f)
							.time(20 + rand.nextInt(10)).spawn(world);

					if (this.ticksExisted > 1) { // Don't spawn particles behind where it started!
						double x = posX - motionX / 2;
						double y = posY - motionY / 2;
						double z = posZ - motionZ / 2;
						ParticleBuilder.create(ParticleBuilder.Type.FLASH, rand, x, y, z, 0.01, true).clr(210, 255, 255).fade(0.2f, 0.2f, 0.2f)
								.time(20 + rand.nextInt(10)).spawn(world);
					}
				}
			}
		}
	}

	@Override
	public boolean canBeCollidedWith() {
		return true;
	}

	@Override
	public float getCollisionBorderSize() {
		return 1.0F;
	}

	public void setLifetime(int lifetime) {
		this.lifetime = lifetime;
	}

	@Override
	public int getLifetime() {
		return lifetime;
	}

	@Override
	public boolean hasNoGravity() {
		return true;
	}

	@Override
	public boolean canRenderOnFire() {
		return false;
	}

	@Override
	public void writeSpawnData(ByteBuf buffer) {
		buffer.writeInt(lifetime);
		super.writeSpawnData(buffer);
	}

	@Override
	public void readSpawnData(ByteBuf buffer) {
		lifetime = buffer.readInt();
		super.readSpawnData(buffer);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		lifetime = nbt.getInteger("lifetime");
		storedSpell = Spell.registry.getValue(new ResourceLocation(nbt.getString("stored_spell")));
		contingencyType = Contingency.Type.fromName(nbt.getString("contingency_type"));
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		nbt.setInteger("lifetime", lifetime);
		nbt.setString("stored_spell", storedSpell.getUnlocalisedName());
		nbt.setString("contingency_type", contingencyType.spellName);
	}
}
