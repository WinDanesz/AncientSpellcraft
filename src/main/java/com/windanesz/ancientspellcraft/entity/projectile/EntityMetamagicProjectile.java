package com.windanesz.ancientspellcraft.entity.projectile;

import com.windanesz.ancientspellcraft.registry.ASSounds;
import com.windanesz.ancientspellcraft.util.SpellcastUtils;
import electroblob.wizardry.entity.construct.EntityMagicConstruct;
import electroblob.wizardry.entity.living.ISummonedCreature;
import electroblob.wizardry.entity.projectile.EntityMagicProjectile;
import electroblob.wizardry.registry.Spells;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.SpellModifiers;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import java.util.List;

public class EntityMetamagicProjectile extends EntityMagicProjectile {

	private int lifetime = 16;
	private Spell storedSpell = Spells.none;

	public EntityMetamagicProjectile(World world) {
		super(world);
		this.setSize(0.7f, 0.7f);

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

				}
			} else {
				SpellcastUtils.tryCastSpellAtLocation(world, rayTrace.getBlockPos().up(), rayTrace.sideHit, storedSpell, new SpellModifiers());
			}
		}

		// fixing ownership
		if (thrower == null)
			return;

		if (rayTrace.typeOfHit == RayTraceResult.Type.BLOCK && rayTrace.getBlockPos() != null) {

			BlockPos pos = rayTrace.getBlockPos().up();
			List<EntityLivingBase> entities = EntityUtils.getEntitiesWithinRadius(1.5, pos.getX(), pos.getY(), pos.getZ(), world, EntityLivingBase.class);

			if (!entities.isEmpty()) {
				for (EntityLivingBase entityLivingBase : entities) {
					if (entityLivingBase instanceof ISummonedCreature) {
						if (((ISummonedCreature) entityLivingBase).getOwnerId() == null && entityLivingBase.ticksExisted < 40) {
							((ISummonedCreature) entityLivingBase).setOwnerId(thrower.getUniqueID());
							((ISummonedCreature) entityLivingBase).setCaster(thrower);
						}
					}
				}
			}

			List<EntityMagicConstruct> entityList = EntityUtils.getEntitiesWithinRadius(3, pos.getX(), pos.getY(), pos.getZ(), world, EntityMagicConstruct.class);

			if (!entityList.isEmpty()) {
				for (EntityMagicConstruct magicConstruct : entityList) {
					if (magicConstruct.ticksExisted < 40 && magicConstruct.getOwnerId() == null) {
						magicConstruct.setCaster(thrower);
					}
				}
			}

			world.playSound(this.posX, this.posY, this.posZ, ASSounds.DISPEL, SoundCategory.HOSTILE, 1, 1, false);
			this.setDead();
		}

	}

	@Override
	public void onUpdate() {

		super.onUpdate();

		if (world.isRemote) {

			for (int i = 0; i < 5; i++) {

				double dx = (rand.nextDouble() - 0.5) * width;
				double dy = (rand.nextDouble() - 0.5) * height + this.height / 2 - 0.1; // -0.1 because flames aren't centred
				double dz = (rand.nextDouble() - 0.5) * width;
				double v = 0.06;
				// TODO add colors from element
				ParticleBuilder.create(ParticleBuilder.Type.DUST).clr(255, 255, 255)
						.pos(this.getPositionVector().add(dx - this.motionX / 2, dy, dz - this.motionZ / 2))
						.vel(-v * dx, -v * dy, -v * dz).scale(width * 2).time(10).spawn(world);

				if (ticksExisted > 1) {

					ParticleBuilder.create(ParticleBuilder.Type.DUST, rand, posX, posY, posZ, 0.03, true).clr(255, 255, 255).fade(0, 0, 0)
							.time(20 + rand.nextInt(10)).spawn(world);

					if (this.ticksExisted > 1) { // Don't spawn particles behind where it started!
						double x = posX - motionX / 2;
						double y = posY - motionY / 2;
						double z = posZ - motionZ / 2;
						ParticleBuilder.create(ParticleBuilder.Type.FLASH, rand, x, y, z, 0.03, true).clr(255, 255, 255).fade(0, 0, 0)
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
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		nbt.setInteger("lifetime", lifetime);
		nbt.setString("stored_spell", storedSpell.getUnlocalisedName());
	}
}
