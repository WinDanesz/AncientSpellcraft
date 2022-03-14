package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.registry.ASItems;
import electroblob.wizardry.item.ItemArtefact;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.spell.SpellRay;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.MagicDamage;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.RayTracer;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.function.Predicate;

import static electroblob.wizardry.util.EntityUtils.attackEntityWithoutKnockback;

public class ArcaneBeam extends SpellRay {

	public ArcaneBeam() {
		super(AncientSpellcraft.MODID, "arcane_beam", SpellActions.POINT, true);
		addProperties(DAMAGE);
		this.aimAssist(0.6f);
		this.particleSpacing = 0.3;
	}

	@Override
	protected boolean onEntityHit(World world, Entity target, Vec3d hit,
			@Nullable EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {
		if (target instanceof EntityLivingBase) {

			if (MagicDamage.isEntityImmune(MagicDamage.DamageType.MAGIC, target)) {
				if (!world.isRemote && ticksInUse == 1 && caster instanceof EntityPlayer)
					((EntityPlayer) caster)
							.sendStatusMessage(new TextComponentTranslation("spell.resist", target.getName(),
									this.getNameForTranslationFormatted()), true);
				// This now only damages in line with the maxHurtResistantTime. Some mods don't play nicely and fiddle
				// with this mechanic for their own purposes, so this line makes sure that doesn't affect wizardry.
				//			} else if
				////			(ticksInUse % ((EntityLivingBase) target).maxHurtResistantTime == 1)
			} else {
				attackEntityWithoutKnockback(target,
						MagicDamage.causeDirectMagicDamage(caster, MagicDamage.DamageType.MAGIC),
						getProperty(DAMAGE).floatValue() * modifiers.get(SpellModifiers.POTENCY));
			}
		}
		return true;
	}

	public static Predicate<Entity> ignoreEntityFilterWithCaster(Entity entity, Entity caster) {
		return e -> e == entity || (e instanceof EntityLivingBase && ((EntityLivingBase) e).getHealth() <= 0) || e == caster;
	}

	@Override
	protected SoundEvent[] createSounds(){
		return this.createContinuousSpellSounds();
	}

	@Override
	protected void playSound(World world, EntityLivingBase entity, int ticksInUse, int duration, SpellModifiers modifiers, String... sounds){
		this.playSoundLoop(world, entity, ticksInUse);
	}

	@Override
	protected void playSound(World world, double x, double y, double z, int ticksInUse, int duration, SpellModifiers modifiers, String... sounds){
		this.playSoundLoop(world, x, y, z, ticksInUse, duration);
	}


	/**
	 * Player and dispenser casting are almost identical so this takes care of the shared stuff. This is mainly for internal use.
	 */
	@Override
	protected boolean shootSpell(World world, Vec3d origin, Vec3d direction, @Nullable EntityLivingBase caster, int ticksInUse, SpellModifiers modifiers) {

		if (!(caster instanceof EntityPlayer)) {
			return false;
		}

		double range = getRange(world, origin, direction, caster, ticksInUse, modifiers);
		Vec3d endpoint = origin.add(direction.scale(range));

		// Change the filter depending on whether living entities are ignored or not
		RayTraceResult primaryTarget = RayTracer.rayTrace(world, origin, endpoint, aimAssist, hitLiquids,
				ignoreUncollidables, false, Entity.class, ignoreLivingEntities ? EntityUtils::isLiving
						: RayTracer.ignoreEntityFilter(caster));

		boolean flag = false;

		if (primaryTarget != null) {
			// Doesn't matter which way round these are, they're mutually exclusive
			if (primaryTarget.typeOfHit == RayTraceResult.Type.ENTITY) {

				flag = onEntityHit(world, primaryTarget.entityHit, primaryTarget.hitVec, caster, origin, ticksInUse, modifiers);
				range = origin.distanceTo(primaryTarget.hitVec);

				if (ItemArtefact.isArtefactActive((EntityPlayer)caster, ASItems.ring_focus_crystal)) {

					RayTraceResult secondaryTarget = RayTracer.rayTrace(world, origin, endpoint, aimAssist, hitLiquids,
							ignoreUncollidables, false, Entity.class, ignoreLivingEntities ? EntityUtils::isLiving
									: ignoreEntityFilterWithCaster(primaryTarget.entityHit, caster));

					if (secondaryTarget != null && secondaryTarget.typeOfHit == RayTraceResult.Type.ENTITY) {
						flag = onEntityHit(world, secondaryTarget.entityHit, secondaryTarget.hitVec, caster, origin, ticksInUse, modifiers);
						range = origin.distanceTo(secondaryTarget.hitVec);
					}

				}

			} else if (primaryTarget.typeOfHit == RayTraceResult.Type.BLOCK) {
				// Do whatever the spell does when it hits an block
				flag = onBlockHit(world, primaryTarget.getBlockPos(), primaryTarget.sideHit, primaryTarget.hitVec, caster, origin, ticksInUse, modifiers);
				// Clip the particles to the correct distance so they don't go through the block
				// Unlike with entities, this is done regardless of whether the spell succeeded, since no spells go
				// through blocks (and in fact, even the ray tracer itself doesn't do that)
				range = origin.distanceTo(primaryTarget.hitVec);
			}
		}

		// If flag is false, either the spell missed or the relevant entity/block hit method returned false
		if (!flag && !onMiss(world, caster, origin, direction, ticksInUse, modifiers))
			return false;

		// Particle spawning
		if (world.isRemote) {
			spawnParticleRay(world, origin, direction, caster, range);
		}

		return true;
	}

	@Override
	protected boolean onBlockHit(World world, BlockPos pos, EnumFacing side, Vec3d hit,
			@Nullable EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {
		return true;
	}

	@Override
	protected boolean onMiss(World world, @Nullable EntityLivingBase caster, Vec3d origin, Vec3d direction, int ticksInUse, SpellModifiers modifiers) {
		return true;
	}

	@Override
	protected void spawnParticle(World world, double x, double y, double z, double vx, double vy, double vz) {
		ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(x, y, z).vel(vx, vy, vz).collide(true).scale(0.3F).spawn(world);
		ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(x, y, z).clr(187, 87, 205).vel(vx, vy, vz).collide(true).spawn(world);
	}

	@SideOnly(Side.CLIENT)
	public String getDisplayNameWithFormatting() {
		return TextFormatting.GOLD + net.minecraft.client.resources.I18n.format(getTranslationKey());
	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == ASItems.ancient_spell_book || item == ASItems.ancient_spellcraft_scroll;
	}
}
