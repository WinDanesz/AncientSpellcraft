package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftItems;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.AllyDesignationSystem;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class Forcefend extends Spell {

	public Forcefend() {
		super(AncientSpellcraft.MODID, "forcefend", SpellActions.SUMMON, true);
		addProperties(EFFECT_RADIUS);
	}

	@Override
	public boolean canBeCastBy(EntityLiving npc, boolean override) {
		return true;
	}

	@Override
	public boolean canBeCastBy(TileEntityDispenser dispenser) {
		return false;
	}

	@Override
	protected SoundEvent[] createSounds() {
		return createContinuousSpellSounds();
	}

	@Override
	protected void playSound(World world, EntityLivingBase entity, int ticksInUse, int duration, SpellModifiers modifiers, String... sounds) {
		this.playSoundLoop(world, entity, ticksInUse);
	}

	@Override
	protected void playSound(World world, double x, double y, double z, int ticksInUse, int duration, SpellModifiers modifiers, String... sounds) {
		this.playSoundLoop(world, x, y, z, ticksInUse, duration);
	}

	@Override
	public boolean cast(World world, EntityPlayer caster, EnumHand hand, int ticksInUse, SpellModifiers modifiers) {
		performEffect(world, caster.getPositionEyes(0).subtract(0, 0.1, 0), caster, modifiers, ticksInUse);
		this.playSound(world, caster, ticksInUse, -1, modifiers);
		return true;
	}

	@Override
	public boolean cast(World world, EntityLiving caster, EnumHand hand, int ticksInUse, EntityLivingBase target, SpellModifiers modifiers) {
		performEffect(world, caster.getPositionEyes(0).subtract(0, 0.1, 0), caster, modifiers, ticksInUse);
		this.playSound(world, caster, ticksInUse, -1, modifiers);
		return true;
	}

	public boolean cast(World world, double x, double y, double z, EnumFacing direction, int ticksInUse, int duration, SpellModifiers modifiers) {
		performEffect(world, new Vec3d(x, y, z), null, modifiers, ticksInUse);
		this.playSound(world, x - direction.getXOffset(), y - direction.getYOffset(), z - direction.getZOffset(), ticksInUse, duration, modifiers);
		return true;
	}

	private void performEffect(World world, Vec3d centre, @Nullable EntityLivingBase caster, SpellModifiers modifiers, int ticksInUse) {

		if (caster != null) {

			List<Entity> projectiles = EntityUtils.getEntitiesWithinRadius(3, caster.posX, caster.posY, caster.posZ, caster.world, Entity.class);
			for (Entity projectile : projectiles) {
				if (!isProjectileOnGround(projectile)) {

					if (projectile instanceof IProjectile) {

						List<EntityLivingBase> entites = EntityUtils.getEntitiesWithinRadius(15, caster.posX, caster.posY, caster.posZ, world, EntityLivingBase.class);
						if (entites.isEmpty()) {
							deflectProjectile(caster, projectile);
							continue;
						}
						for (EntityLivingBase entity : entites) {
							if (entity == caster) {
								continue;
							}
							if (!AllyDesignationSystem.isAllied((EntityPlayer) caster, entity)) {
								aimArrow(entity, projectile, (IProjectile) projectile);
								return;

							}
						}
					}
				}
			}

			if (world.isRemote && ticksInUse % 2 == 0) {

				double speed = (world.rand.nextBoolean() ? 1 : 1) * 0.1;// + 0.01 * rand.nextDouble();
				double radius = world.rand.nextDouble() * 2.0;
				float angle = world.rand.nextFloat() * (float) Math.PI * 2;
				ParticleBuilder.create(ParticleBuilder.Type.FLASH)
						.entity(caster)
//						.pos(1 * MathHelper.cos(angle), 0, 1  * MathHelper.sin(angle))
						.vel(0, 0.05, 0)
						.scale(0.7F)
						.time(48 + world.rand.nextInt(12))
						.spin(1, speed)
						.clr(1f, 1f, 1f)
						.spawn(world);

//				ParticleBuilder.create(ParticleBuilder.Type.FLASH)
//						.entity(caster)
////						.pos(1 * MathHelper.cos(angle), 0, 1  * MathHelper.sin(angle))
//						.vel(0, 0.05, 0)
//						.scale(0.7F)
//						.time(48 + world.rand.nextInt(12))
//						.spin(1, speed)
//						.clr(1f, 1f, 1f)
//						.collide(true)
//						.spawn(world);
			}
		}
		//		if (world.isRemote) {
		//			//			if (ticksInUse % 5 == 0) {
		//			ParticleBuilder.create(Type.FLASH).entity(caster).clr(255, 255, 235).scale(6).time(5).
		//					entity(caster).
		//					pos(caster.getLookVec().x * 1.3, caster.getEyeHeight(), caster.getLookVec().z * 1.3)
		//					//					pos(caster.posX + caster.getLookVec().x * 0.3,
		//					//							caster.posY + 1 + caster.getLookVec().y * 0.3,
		//					//							caster.posZ + caster.getLookVec().z * 0.3)
		//					.face(caster.rotationYawHead,
		//							caster.rotationPitch)
		//					.spawn(world);
		//			//				ParticleBuilder.create(Type.FLASH).entity(caster).clr(255, 255, 235).scale(4).time(5)
		//			//						.pos(caster != null ? centre.subtract(caster.getPositionVector()) : centre).spawn(world);
		//			//			}
		//		}
	}

	@SideOnly(Side.CLIENT)
	public String getDisplayNameWithFormatting() {
		return TextFormatting.GOLD + net.minecraft.client.resources.I18n.format(getTranslationKey());
	}

	public void aimArrow(EntityLivingBase target, Entity entity, IProjectile projectile) {
		double d0 = target.posX - entity.posX;
		double d1 = target.getEntityBoundingBox().minY + (double) (target.height / 3.0F) - entity.posY;
		double d2 = target.posZ - entity.posZ;
		double d3 = (double) MathHelper.sqrt(d0 * d0 + d2 * d2);
		projectile.shoot(d0, d1 + d3 * 0.20000000298023224D, d2, 1.3F, (float) (14 - target.world.getDifficulty().getId() * 4));
	}

	private boolean isProjectileOnGround(Entity projectile) {
		return projectile.world.collidesWithAnyBlock(projectile.getEntityBoundingBox().grow(0.05D));
	}

	private void deflectProjectile(EntityLivingBase entity, Entity projectile) {
		Vec3d centre = entity.getPositionEyes(0).subtract(0, 0.1, 0);
		Vec3d vec = projectile.getPositionVector().subtract(centre).normalize().scale(0.6);
		projectile.addVelocity(vec.x, vec.y, vec.z);
	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == AncientSpellcraftItems.ancient_spell_book || item == AncientSpellcraftItems.ancient_spellcraft_scroll;
	}
}