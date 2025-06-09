package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.registry.ASItems;
import electroblob.wizardry.Wizardry;
import electroblob.wizardry.item.ItemArtefact;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class LivingComet extends Spell {

	public static final String SPEED = "speed";
	public static final String ACCELERATION = "acceleration";

	private static final double Y_NUDGE_ACCELERATION = 0.12; // Increased from 0.075 for better altitude control

	public LivingComet() {
		super(AncientSpellcraft.MODID, "living_comet", SpellActions.POINT, true);
		addProperties(SPEED, ACCELERATION);
	}

	@Override
	public boolean cast(World world, EntityPlayer caster, EnumHand hand, int ticksInUse, SpellModifiers modifiers) {
		if (caster.world.isRemote) {

			for (int i = 0; i < 5; i++) {
				float x = caster.world.rand.nextFloat();
				x = caster.world.rand.nextBoolean() ? x : x * -1;
				float y = caster.world.rand.nextFloat();
				y = caster.world.rand.nextBoolean() ? y : y * -1;
				float z = caster.world.rand.nextFloat();
				z = caster.world.rand.nextBoolean() ? z : z * -1;


				ParticleBuilder.create(ParticleBuilder.Type.MAGIC_FIRE).entity(caster).pos(0, caster.height, 0).time(6).vel(caster.world.rand.nextGaussian() / 40, caster.world.rand.nextDouble() / 40,
						caster.world.rand.nextGaussian() / 40).collide(true).scale(3F).spawn(caster.world);

				ParticleBuilder.create(ParticleBuilder.Type.MAGIC_FIRE).pos(caster.posX, caster.posY + 1.5f, caster.posZ).time(6).vel(caster.world.rand.nextGaussian() / 40, caster.world.rand.nextDouble() / 40,
						caster.world.rand.nextGaussian() / 40).collide(true).scale(2F).time(100).spawn(caster.world);

				ParticleBuilder.create(ParticleBuilder.Type.MAGIC_FIRE).entity(caster).pos(x, caster.height / 2 + y, z).time(6).vel(caster.world.rand.nextGaussian() / 40, caster.world.rand.nextDouble() / 40,
								caster.world.rand.nextGaussian() / 40).collide(true).
						scale(1F).spawn(caster.world);
			}
		}
		caster.fallDistance = 0.0f;

		if (ItemArtefact.isArtefactActive(caster, ASItems.charm_meteorite_stone)) {
			if (ticksInUse > 40 && caster.onGround) {

				if (!world.isRemote) {
					// Only cause terrain damage if the player is sneaking
					boolean terrainDamage = caster.isSneaking() && EntityUtils.canDamageBlocks(caster, world);
					caster.world.createExplosion(caster, caster.posX, caster.posY, caster.posZ, 1.8f, terrainDamage);
					caster.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, 60, 0));
				}
				caster.stopActiveHand();
				return true;
			}


			if (!caster.isElytraFlying()) {

				float speed = 2.2f * getProperty(SPEED).floatValue() * modifiers.get(SpellModifiers.POTENCY); // Reduced from 2.5f
				float acceleration = 2.0f * getProperty(ACCELERATION).floatValue() * modifiers.get(SpellModifiers.POTENCY); // Reduced from 2.5f

				// Initial lift phase - more immediate and stronger
				if (ticksInUse < 10) { // Reduced from 20 to make liftoff quicker
					caster.addVelocity(0, 0.4, 0); // Increased from 0.3 for faster initial ascent
				}

				// Horizontal movement - simplified conditions and made available earlier
				if (ticksInUse >= 10) { // Changed from > 20 to >= 10 for earlier control
					// Reduced multiplier from 3 to 2 to make horizontal movement less overpowered
					caster.addVelocity(
							caster.getLookVec().x * acceleration * 2,
							0,
							caster.getLookVec().z * acceleration * 2
					);
				}

				// Vertical control - available much sooner (40 ticks instead of 100)
				if (ticksInUse >= 40) { // Changed from > 100 to >= 40 (2 seconds instead of 5)
					// Simplified condition for more consistent vertical control
					float verticalFactor = acceleration * 0.8f; // Slightly reduced from horizontal for better control

					// If looking very up or down, apply stronger vertical movement
					if (Math.abs(caster.getLookVec().y) > 0.7) {
						verticalFactor *= 1.5f;
					}

					// Apply vertical velocity based on look direction, with less nudge to allow gradual descent
					// Reduced Y_NUDGE_ACCELERATION so player will eventually go down
					double nudgeFactor = 0.05; // Reduced from 0.12 to allow descent
					caster.motionY += caster.getLookVec().y * verticalFactor + nudgeFactor;

					// Only limit extreme falling speed for safety
					if (caster.motionY < -1.2) {
						caster.motionY = -1.2;
					}
				}

				if (!Wizardry.settings.replaceVanillaFallDamage)
					caster.fallDistance = 0.0f;
			}
		} else {
			caster.fallDistance = 0.0f;

			if (ticksInUse > 40 && caster.onGround) {

				if (!world.isRemote) {
					boolean terrainDamage = EntityUtils.canDamageBlocks(caster, world);
					caster.world.createExplosion(caster, caster.posX, caster.posY, caster.posZ, 1.8f, terrainDamage);
					caster.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, 60, 0));
				}
				caster.stopActiveHand();
				return true;
			}

			if (!caster.isElytraFlying()) {

				float speed = 2 * getProperty(SPEED).floatValue() * modifiers.get(SpellModifiers.POTENCY);
				float acceleration = 2 * getProperty(ACCELERATION).floatValue() * modifiers.get(SpellModifiers.POTENCY);

				if (ticksInUse < 20) {
					caster.addVelocity(0, 0.3, 0);
				}

				// The division thingy checks if the look direction is the opposite way to the velocity. If this is the
				// case then the velocity should be added regardless of the player's current speed.
				if ((ticksInUse > 20) && ((Math.abs(caster.motionX) < speed || caster.motionX / caster.getLookVec().x < 0))
						&& (Math.abs(caster.motionZ) < speed || caster.motionZ / caster.getLookVec().z < 0)) {
					caster.addVelocity(caster.getLookVec().x * acceleration * 2, 0, caster.getLookVec().z * acceleration * 2);
				}
				// y velocity is handled separately to stop the player from falling from the sky when they reach maximum
				// horizontal speed.
				if ((ticksInUse > 100) && (Math.abs(caster.motionY) < speed || caster.motionY / caster.getLookVec().y < 0)) {
					caster.motionY += caster.getLookVec().y * (acceleration + ticksInUse / 20) + Y_NUDGE_ACCELERATION;
				}

				if (!Wizardry.settings.replaceVanillaFallDamage)
					caster.fallDistance = 0.0f;
			}
		}


		if (world.isRemote) {
			double x = caster.posX - 1 + world.rand.nextDouble() * 2;
			double y = caster.posY + caster.getEyeHeight() - 0.5 + world.rand.nextDouble();
			double z = caster.posZ - 1 + world.rand.nextDouble() * 2;
			ParticleBuilder.create(ParticleBuilder.Type.MAGIC_FIRE).pos(x, y, z).vel(0, -0.1, 0).time(15).spawn(world);
			x = caster.posX - 1 + world.rand.nextDouble() * 2;
			y = caster.posY + caster.getEyeHeight() - 0.5 + world.rand.nextDouble();
			z = caster.posZ - 1 + world.rand.nextDouble() * 2;
			ParticleBuilder.create(ParticleBuilder.Type.MAGIC_FIRE).pos(x, y, z).vel(0, -0.1, 0).time(15).spawn(world);
		}

		if (ticksInUse % 24 == 0)
			playSound(world, caster, ticksInUse, -1, modifiers);

		return true;
	}

	@Override
	public void finishCasting(World world,
							  @Nullable EntityLivingBase caster, double x, double y, double z, @Nullable EnumFacing direction, int duration, SpellModifiers modifiers) {
		if (!caster.isPotionActive(MobEffects.INVISIBILITY)) {
//			caster.setInvisible(false);
		}
	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == ASItems.ancient_spellcraft_spell_book || item == ASItems.ancient_spellcraft_scroll;
	}
}
