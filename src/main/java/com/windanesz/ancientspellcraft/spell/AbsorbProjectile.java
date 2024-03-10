package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.registry.ASItems;
import electroblob.wizardry.constants.Element;
import electroblob.wizardry.data.IStoredVariable;
import electroblob.wizardry.data.Persistence;
import electroblob.wizardry.data.WizardData;
import electroblob.wizardry.entity.EntityMeteor;
import electroblob.wizardry.entity.projectile.EntityMagicArrow;
import electroblob.wizardry.entity.projectile.EntityMagicProjectile;
import electroblob.wizardry.item.ItemWizardArmour;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.List;

public class AbsorbProjectile extends Spell implements IClassSpell {

	public static final IStoredVariable<NBTTagCompound> ABSORBED_PROJECTILES = IStoredVariable.StoredVariable.ofNBT("AbsorbedProjectiles", Persistence.ALWAYS);
	public static final IStoredVariable<Integer> ABSORBED_PROJECTILES_TIMER = IStoredVariable.StoredVariable.ofInt("AbsorbedProjectilesDuration", Persistence.ALWAYS).withTicker(AbsorbProjectile::update);

	public AbsorbProjectile() {
		super(AncientSpellcraft.MODID, "absorb_projectile", SpellActions.SUMMON, true);
		this.soundValues(1, 1, 0.4f);
		WizardData.registerStoredVariables(ABSORBED_PROJECTILES, ABSORBED_PROJECTILES_TIMER);
	}

	@Override
	public boolean cast(World world, EntityPlayer caster, EnumHand hand, int ticksInUse, SpellModifiers modifiers) {
		return doEffect(world, caster, hand, ticksInUse, null, modifiers);
	}

	@Override
	public boolean cast(World world, EntityLiving caster, EnumHand hand, int ticksInUse, EntityLivingBase target, SpellModifiers modifiers) {
		return doEffect(world, caster, hand, ticksInUse, target, modifiers);
	}

	public boolean doEffect(World world, EntityLivingBase caster, EnumHand hand, int ticksInUse, @Nullable EntityLivingBase target, SpellModifiers modifiers) {

		if (caster instanceof EntityPlayer) {
			EntityPlayer player = ((EntityPlayer) caster);
			if (!world.isRemote && player.isSneaking() && ticksInUse % 5 == 0) {
				WizardData data = WizardData.get(player);
				NBTTagCompound projs = data.getVariable(ABSORBED_PROJECTILES);
				if (projs != null && !projs.isEmpty()) {
					if (projs.hasKey("List")) {
						NBTTagList tagList = projs.getTagList("List", Constants.NBT.TAG_COMPOUND);
						if (!tagList.isEmpty()) {
							boolean f = false;

							NBTTagCompound projectileInNBT = (NBTTagCompound) tagList.get(0);
							if (!projectileInNBT.hasKey("meteor")) {

								projectileInNBT.setUniqueId("UUID", MathHelper.getRandomUUID(world.rand));
								//	NBTTagCompound posTag = new NBTTagCompound();
								projectileInNBT.setTag("Pos", this.newDoubleNBTList(caster.posX, caster.posY + 0.5f, caster.posZ));
								if (projectileInNBT.hasKey("lifetime")) {
									projectileInNBT.setInteger("lifetime", 60);
								}

								Entity projectile = EntityList.createEntityFromNBT(projectileInNBT, world);
								if (projectile instanceof EntityMagicProjectile) {
									((EntityMagicProjectile) projectile).shoot(caster, caster.rotationPitch, caster.rotationYaw, 0.0f, 2, 1.0f);
									((EntityMagicProjectile) projectile).ignoreEntity = caster;
									world.spawnEntity(projectile);
									f = true;
								} else if (projectile instanceof EntityArrow) {
									((EntityArrow) projectile).shoot(caster, caster.rotationPitch, caster.rotationYaw, 0.0f, 3, 1.0f);
									((EntityArrow) projectile).shootingEntity = caster;
									((EntityArrow) projectile).pickupStatus = EntityArrow.PickupStatus.DISALLOWED;
									world.spawnEntity(projectile);

									f = true;
								} else if (projectile instanceof EntityMagicArrow) {
									((EntityMagicArrow) projectile).aim(caster, calculateVelocity((EntityMagicArrow) projectile, caster.getEyeHeight()
											- (float) EntityMagicArrow.LAUNCH_Y_OFFSET));
									world.spawnEntity(projectile);
									f = true;

								}
							} else {
								EntityMeteor meteor = new EntityMeteor(world, caster.posX, caster.posY + caster.getEyeHeight(), caster.posZ, 1, EntityUtils.canDamageBlocks(caster, world));

								Vec3d direction = caster.getLookVec().scale(2);
								meteor.motionX = direction.x;
								meteor.motionY = direction.y;
								meteor.motionZ = direction.z;

								world.spawnEntity(meteor);
								f = true;
							}
							if (f) {
								System.out.println("size: " + tagList.tagCount());
								tagList.removeTag(0);
								projs.setTag("List", tagList);
								data.setVariable(ABSORBED_PROJECTILES, projs);
							}
						}
					}
				}
			} else if (!caster.isSneaking()) {

				List<Entity> projectiles = EntityUtils.getEntitiesWithinRadius(4, caster.posX, caster.posY, caster.posZ, caster.world, Entity.class);
				for (Entity projectile : projectiles) {
					if (!isProjectileOnGround(projectile)) {
						if (projectile instanceof IProjectile || projectile instanceof EntityMeteor) {
							WizardData data = WizardData.get(player);
							NBTTagCompound projs = data.getVariable(ABSORBED_PROJECTILES);
							if (projs == null) {
								projs = new NBTTagCompound();
							}

							NBTTagCompound absorbedProjectile = new NBTTagCompound();
							if (projectile instanceof EntityMeteor) {
								absorbedProjectile.setBoolean("meteor", true);
							} else {
								absorbedProjectile = projectile.serializeNBT();
							}
							//	(absorbedProjectile); // Serialize the projectile entity into NBT
							// absorbedProjectile.setTag("projectile_" + projectile.getEntityId(), projs);
							//pr/ojs.setTag("projectile_" + projectile.getEntityId(), absorbedProjectile);

							NBTTagList tagList = new NBTTagList();
							if (projs.hasKey("List")) {
								tagList = projs.getTagList("List", Constants.NBT.TAG_COMPOUND);
							}
							tagList.appendTag(absorbedProjectile);
							projs.setTag("List", tagList);
							data.setVariable(ABSORBED_PROJECTILES, projs);

							if (world.isRemote) {
								Element element = getElementOrMagicElement(caster);
								for (int i = 0; i < 10; i++) {
									ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(projectile.posX, projectile.posY, projectile.posZ)
											.scale(2)
											.clr(WarlockElementalSpellEffects.PARTICLE_COLOURS.get(element)[world.rand.nextInt(2)])
											.pos(0, 0, 0)
											.time(40).spawn(world);
								}
							}

							world.removeEntity(projectile);
							System.out.println("test");
						}
					}
				}
			}
		} else {

			List<Entity> projectiles = EntityUtils.getEntitiesWithinRadius(4, caster.posX, caster.posY, caster.posZ, caster.world, Entity.class);
			for (Entity projectile : projectiles) {
				if (!isProjectileOnGround(projectile)) {
					if (projectile instanceof IProjectile || projectile instanceof EntityMeteor) {
						if (world.isRemote) {
							Element element = getElementOrMagicElement(caster);
							for (int i = 0; i < 10; i++) {
								ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(projectile.posX, projectile.posY, projectile.posZ)
										.scale(2)
										.clr(WarlockElementalSpellEffects.PARTICLE_COLOURS.get(element)[world.rand.nextInt(2)])
										.pos(0, 0, 0)
										.time(40).spawn(world);
							}
						}
						world.removeEntity(projectile);
					}
				}
			}
		}

		if (world.isRemote) {
			Element element = getElementOrMagicElement(caster);
			for (int i = 0; i < 10; i++) {
				ParticleBuilder.create(ParticleBuilder.Type.FLASH).entity(caster).spin(1, 0.006f)
						.scale(Math.max(0.5f, world.rand.nextFloat()))
						.clr(WarlockElementalSpellEffects.PARTICLE_COLOURS.get(element)[world.rand.nextInt(2)])
						.pos(0, world.rand.nextFloat() * 2f + 0.1f, 0)
						.time(40).spawn(world);
			}
		}

		return true;
	}

	protected float calculateVelocity(EntityMagicArrow projectile, float launchHeight) {
		// The required range
		float range = 20;

		if (!projectile.doGravity()) {
			// No sensible spell will do this - range is meaningless if the particle has no gravity or lifetime
			if (projectile.getLifetime() <= 0) {return 2;}
			// Speed = distance/time (trivial, I know, but I've put it here for the sake of completeness)
			return range / projectile.getLifetime();
		} else {
			// Arrows have gravity 0.05
			float g = 0.05f;
			// Assume horizontal projection
			return range / MathHelper.sqrt(2 * launchHeight / g);
		}
	}

	private boolean isProjectileOnGround(Entity projectile) {
		return projectile.world.collidesWithAnyBlock(projectile.getEntityBoundingBox().grow(0.05D));
	}

	@Override
	public ItemWizardArmour.ArmourClass getArmourClass() {
		return ItemWizardArmour.ArmourClass.WARLOCK;
	}

	public boolean applicableForItem(Item item) {
		return item == ASItems.forbidden_tome;
	}

	private static int update(EntityPlayer player, Integer duration) {
		return duration;
	}

	protected NBTTagList newDoubleNBTList(double... numbers) {
		NBTTagList nbttaglist = new NBTTagList();

		for (double d0 : numbers) {
			nbttaglist.appendTag(new NBTTagDouble(d0));
		}

		return nbttaglist;
	}

	@Override
	public boolean canBeCastBy(EntityLiving npc, boolean override) {
		return false;
	}

	@Override
	public boolean canBeCastBy(TileEntityDispenser dispenser) {
		return false;
	}
}
