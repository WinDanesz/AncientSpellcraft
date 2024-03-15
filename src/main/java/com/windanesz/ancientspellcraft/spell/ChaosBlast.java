package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.registry.ASItems;
import electroblob.wizardry.Wizardry;
import electroblob.wizardry.constants.Element;
import electroblob.wizardry.entity.ICustomHitbox;
import electroblob.wizardry.item.ItemArtefact;
import electroblob.wizardry.item.ItemWizardArmour;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.spell.SpellRay;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.GeometryUtils;
import electroblob.wizardry.util.MagicDamage;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.RayTracer;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

public class ChaosBlast extends SpellRay implements IClassSpell {

	public static final String CHARGING_TIME = "charging_time";

	public ChaosBlast() {
		super(AncientSpellcraft.MODID, "chaos_blast", SpellActions.POINT, true);
		ignoreUncollidables = false;
		this.soundValues(1, 1, 0.4f);
		addProperties(DAMAGE, CHARGING_TIME);
	}

	@Override
	public boolean cast(World world, EntityLiving caster, EnumHand hand, int ticksInUse, EntityLivingBase target, SpellModifiers modifiers) {
		return doSpellTick(world, caster, hand, ticksInUse, target, modifiers);
	}

	@Override
	protected boolean onEntityHit(World world, Entity target, Vec3d hit,
			@Nullable EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {
		return false;
	}

	@Override
	public boolean cast(World world, EntityPlayer caster, EnumHand hand, int ticksInUse, SpellModifiers modifiers) {
		return doSpellTick(world, caster, hand, ticksInUse, null, modifiers);
	}

	private boolean doSpellTick(World world, EntityLivingBase caster, EnumHand hand, int ticksInUse, @Nullable EntityLivingBase target, SpellModifiers modifiers) {
		int chargeup = getProperty(CHARGING_TIME).intValue();
		Element element = getElementOrMagicElement(caster);
		if (ticksInUse < chargeup) {
			if (world.isRemote && caster.ticksExisted % 1 == 0) {
				for (int i = 0; i < 10; i++) {
					ParticleBuilder.create(ParticleBuilder.Type.FLASH).entity(caster).spin((Math.min(3, 1 / ((0.1 + ticksInUse) * 0.05f))), 0.006f)
							.scale(Math.min(1.0f, 2 / (Math.max(1.2f, ticksInUse * 0.2f) * 0.7f))).clr(WarlockElementalSpellEffects.PARTICLE_COLOURS.get(element)[world.rand.nextInt(2)]).pos(0, world.rand.nextFloat() + 0.2f, 0).time(40).spawn(world);
				}
			}
		} else if (ticksInUse == chargeup + 1) {

			Vec3d look = caster.getLookVec();
			Vec3d origin = new Vec3d(caster.posX, caster.posY + caster.getEyeHeight() - Y_OFFSET, caster.posZ);
			if (!this.isContinuous && world.isRemote && !Wizardry.proxy.isFirstPerson(caster)) {
				origin = origin.add(look.scale(1.2));
			}

			if (!shootSpell(world, origin, look, caster, ticksInUse, modifiers, element)) {return false;}

			if (casterSwingsArm(world, caster, hand, ticksInUse, modifiers)) {caster.swingArm(hand);}
			this.playSound(world, caster, ticksInUse, -1, modifiers);
		}

		return true;
	}

	protected boolean shootSpell(World world, Vec3d origin, Vec3d direction,
			@Nullable EntityLivingBase caster, int ticksInUse, SpellModifiers modifiers, Element element) {
		double range = getRange(world, origin, direction, caster, ticksInUse, modifiers);
		Vec3d endpoint = origin.add(direction.scale(range));


		List<RayTraceResult> rayTraces = rayTraceMultiple(world, origin, endpoint, 1, this.hitLiquids, this.ignoreUncollidables,
				false, false, Entity.class, this.ignoreLivingEntities ? EntityUtils::isLiving : RayTracer.ignoreEntityFilter(caster));
		Collections.reverse(rayTraces);
		boolean flag;
		if (!rayTraces.isEmpty()) {
			int i = 1;
			for (RayTraceResult rayTrace : rayTraces) {
				// Doesn't matter which way round these are, they're mutually exclusive
				if (rayTrace.typeOfHit == RayTraceResult.Type.ENTITY) {

					// Do whatever the spell does when it hits an entity
					// FIXME: Some spells (e.g. lightning web) seem to not render when aimed at item frames

					if (i == 1 || caster instanceof EntityPlayer && ItemArtefact.isArtefactActive((EntityPlayer) caster, ASItems.ring_chaos_blast_multitarget)) {
						if (i == 2) {
							modifiers.set(SpellModifiers.POTENCY, modifiers.get(SpellModifiers.POTENCY) * 0.6f, false);
						}
						flag = onEntityHit(world, rayTrace.entityHit, rayTrace.hitVec, caster, origin, ticksInUse, modifiers, element);
						if (flag) {range = origin.distanceTo(rayTrace.hitVec);}
						i++;
					}

				} else if (rayTrace.typeOfHit == RayTraceResult.Type.BLOCK) {
					// Do whatever the spell does when it hits an block
					flag = onBlockHit(world, rayTrace.getBlockPos(), rayTrace.sideHit, rayTrace.hitVec, caster, origin, ticksInUse, modifiers, element);
					// Clip the particles to the correct distance so they don't go through the block
					// Unlike with entities, this is done regardless of whether the spell succeeded, since no spells go
					// through blocks (and in fact, even the ray tracer itself doesn't do that)
					range = origin.distanceTo(rayTrace.hitVec);
				}
			}
		}

		// Change the filter depending on whether living entities are ignored or not
		RayTraceResult rayTrace = RayTracer.rayTrace(world, origin, endpoint, aimAssist, hitLiquids,
				ignoreUncollidables, false, Entity.class, ignoreLivingEntities ? EntityUtils::isLiving
						: RayTracer.ignoreEntityFilter(caster));

		flag = false;

		if(rayTrace != null){
			 if(rayTrace.typeOfHit == RayTraceResult.Type.BLOCK){
				// Do whatever the spell does when it hits an block
				flag = onBlockHit(world, rayTrace.getBlockPos(), rayTrace.sideHit, rayTrace.hitVec, caster, origin, ticksInUse, modifiers, element);
				// Clip the particles to the correct distance so they don't go through the block
				// Unlike with entities, this is done regardless of whether the spell succeeded, since no spells go
				// through blocks (and in fact, even the ray tracer itself doesn't do that)
				range = origin.distanceTo(rayTrace.hitVec);
			}
		}
		// If flag is false, either the spell missed or the relevant entity/block hit method returned false
		if (!flag && !onMiss(world, caster, origin, direction, ticksInUse, modifiers)) {return false;}

		// Particle spawning
		if (world.isRemote) {
			spawnParticleRay(world, origin, direction, caster, range, element);
		}

		return true;
	}

	protected boolean onEntityHit(World world, Entity target, Vec3d hit, EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers, Element element) {
		if (target instanceof EntityLivingBase) {
				EntityUtils.attackEntityWithoutKnockback(target, caster == null ? DamageSource.MAGIC :
								MagicDamage.causeDirectMagicDamage(caster, MagicDamage.DamageType.FIRE),
						getProperty(DAMAGE).floatValue() * modifiers.get(SpellModifiers.POTENCY));
				WarlockElementalSpellEffects.affectEntity((EntityLivingBase) target, element, caster,  modifiers, true);
		}
		return true;
	}

	@Override
	protected boolean onBlockHit(World world, BlockPos pos, EnumFacing side, Vec3d hit,
			@Nullable EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {
		return false;
	}

	protected boolean onBlockHit(World world, BlockPos pos, EnumFacing side, Vec3d hit, EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers, Element element) {

		if (world.isRemote) {

			for (int i = 0; i < 8; i++) {
				world.spawnParticle(EnumParticleTypes.LAVA, hit.x, hit.y, hit.z, 0, 0, 0);
			}
			if (world.getBlockState(pos).getMaterial().isSolid()) {
				Vec3d vec = hit.add(new Vec3d(side.getDirectionVec()).scale(GeometryUtils.ANTI_Z_FIGHTING_OFFSET));
				ParticleBuilder.create(ParticleBuilder.Type.SCORCH).scale(3f).pos(vec).face(side).clr(WarlockElementalSpellEffects.PARTICLE_COLOURS.get(element)[0]).spawn(world);
			}
		}
		return true;

	}

	@Override
	protected boolean onMiss(World world, EntityLivingBase caster, Vec3d origin, Vec3d direction, int ticksInUse, SpellModifiers modifiers) {
		return true;
	}

	@Override
	public ItemWizardArmour.ArmourClass getArmourClass() {
		return ItemWizardArmour.ArmourClass.WARLOCK;
	}

	public boolean applicableForItem(Item item) {
		return item == ASItems.forbidden_tome;
	}

	protected void spawnParticleRay(World world, Vec3d origin, Vec3d direction, @Nullable EntityLivingBase caster, double distance, Element element) {
		if (!world.isRemote) {return;}

		Vec3d velocity = direction.scale(particleVelocity);
		Vec3d endpoint = origin.add(direction.scale(distance));
		ParticleBuilder.create(ParticleBuilder.Type.BEAM).scale(7).clr(WarlockElementalSpellEffects.PARTICLE_COLOURS.get(element)[0]).fade(WarlockElementalSpellEffects.PARTICLE_COLOURS.get(element)[world.rand.nextInt(2)]).time(4).pos(origin).target(endpoint).spawn(world);

		for (double d = particleSpacing / 2; d <= distance; d += particleSpacing / 2) {
			double x = origin.x + d * direction.x + particleJitter * (world.rand.nextDouble() * 2 - 1);
			double y = origin.y + d * direction.y + particleJitter * (world.rand.nextDouble() * 2 - 1);
			double z = origin.z + d * direction.z + particleJitter * (world.rand.nextDouble() * 2 - 1);
			spawnParticle(world, x, y, z, velocity.x, velocity.y, velocity.z, element);
		}
	}

	protected void spawnParticle(World world, double x, double y, double z, double vx, double vy, double vz, Element element) {
		if (!world.isRemote) {return;}

		ParticleBuilder.create(ParticleBuilder.Type.FLASH).scale(7).clr(WarlockElementalSpellEffects.PARTICLE_COLOURS.get(element)[1]).time(20).pos(x, y, z).vel(vx, vy, vz).collide(true).spawn(world);
		ParticleBuilder.create(ParticleBuilder.Type.FLASH).clr(WarlockElementalSpellEffects.PARTICLE_COLOURS.get(element)[1]).time(20).pos(x, y, z).vel(vx, vy, vz).collide(true).spawn(world);
		ParticleBuilder.create(ParticleBuilder.Type.FLASH).clr(WarlockElementalSpellEffects.PARTICLE_COLOURS.get(element)[0]).time(10).pos(x, y, z).vel(vx, vy, vz).collide(true).spawn(world);
	}

	public List<RayTraceResult> rayTraceMultiple(World world, Vec3d origin, Vec3d endpoint, float aimAssist, boolean hitLiquids, boolean ignoreUncollidables,
			boolean returnLastUncollidable, boolean penetratesBlocks, Class<? extends Entity> entityType, Predicate<? super Entity> filter) {
		float borderSize = 1.0F + aimAssist;
		AxisAlignedBB searchVolume = (new AxisAlignedBB(origin.x, origin.y, origin.z, endpoint.x, endpoint.y, endpoint.z)).grow(borderSize, borderSize, borderSize);

		List<Entity> entities = world.getEntitiesWithinAABB(entityType, searchVolume);
		entities.removeIf(filter);
		entities.removeIf(e -> !(e instanceof EntityLivingBase));
		List<RayTraceResult> result = new ArrayList<>();
		RayTraceResult rayTraceResult = world.rayTraceBlocks(origin, endpoint, hitLiquids, ignoreUncollidables, returnLastUncollidable);
		if (rayTraceResult != null && !penetratesBlocks) {
			endpoint = rayTraceResult.hitVec;
		}
		Vec3d intercept = null;
		for (Entity entity : entities) {
			float fuzziness = EntityUtils.isLiving(entity) ? aimAssist : 0.0F;
			float currentHitDistance;
			if (entity instanceof ICustomHitbox) {
				intercept = ((ICustomHitbox) entity).calculateIntercept(origin, endpoint, fuzziness);
			} else {
				AxisAlignedBB entityBounds = entity.getEntityBoundingBox();
				currentHitDistance = entity.getCollisionBorderSize();
				if (currentHitDistance != 0.0F) {
					entityBounds = entityBounds.grow(currentHitDistance, currentHitDistance, currentHitDistance);
				}
				if (fuzziness != 0.0F) {
					entityBounds = entityBounds.grow(fuzziness, fuzziness, fuzziness);
				}
				RayTraceResult hit = entityBounds.calculateIntercept(origin, endpoint);
				if (hit != null) {
					intercept = hit.hitVec;
				}
			}
			if (intercept != null) {
				currentHitDistance = (float) intercept.distanceTo(origin);
				float closestHitDistance = (float) endpoint.distanceTo(origin);
				if (currentHitDistance < closestHitDistance) {
					RayTraceResult entityResult = new RayTraceResult(entity, intercept);
					result.add(entityResult);
				}
			}
		}
		return result;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getDescription() {
		if (Minecraft.getMinecraft().player != null) {
			EntityPlayer player = Minecraft.getMinecraft().player;
			return Wizardry.proxy.translate(getDescriptionTranslationKey(), (getProperty(CHARGING_TIME).floatValue() / 20));
		}
		return super.getDescription();
	}

	@Override
	public boolean canBeCastBy(TileEntityDispenser dispenser) {
		return false;
	}

	@Override
	public boolean canBeCastBy(EntityLiving npc, boolean override) {
		return canBeCastByClassNPC(npc);
	}
}
