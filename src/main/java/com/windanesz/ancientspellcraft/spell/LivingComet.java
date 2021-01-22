package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftItems;
import electroblob.wizardry.Wizardry;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.spell.Spell;
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

public class LivingComet  extends Spell {

	public static final String SPEED = "speed";
	public static final String ACCELERATION = "acceleration";

	private static final double Y_NUDGE_ACCELERATION = 0.075;

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

				ParticleBuilder.create(ParticleBuilder.Type.MAGIC_FIRE).entity(caster).pos(x, caster.height / 2 + y, z).time(6 ).vel(caster.world.rand.nextGaussian() / 40, caster.world.rand.nextDouble() / 40,
						caster.world.rand.nextGaussian() / 40).collide(true).
						scale(1F).spawn(caster.world);
			}
		}
		caster.fallDistance = 0.0f;

		if (ticksInUse > 40 && caster.onGround) {

			if (!world.isRemote)
				caster.world.createExplosion(caster, caster.posX, caster.posY, caster.posZ, 1.8f, true);
				caster.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, 60, 0));
			caster.stopActiveHand();
			return true;
		}

		if (!caster.isElytraFlying()) {

			float speed = 2 * getProperty(SPEED).floatValue() * modifiers.get(SpellModifiers.POTENCY);
			float acceleration = 2 *  getProperty(ACCELERATION).floatValue() * modifiers.get(SpellModifiers.POTENCY);

			if (ticksInUse < 20) {
				caster.addVelocity(0, 0.3, 0);
			}

			// The division thingy checks if the look direction is the opposite way to the velocity. If this is the
			// case then the velocity should be added regardless of the player's current speed.
			if ((ticksInUse > 20) &&  ((Math.abs(caster.motionX) < speed || caster.motionX / caster.getLookVec().x < 0))
					&& (Math.abs(caster.motionZ) < speed || caster.motionZ / caster.getLookVec().z < 0)) {
				caster.addVelocity(caster.getLookVec().x * acceleration *2, 0, caster.getLookVec().z * acceleration  *2 );
			}
			// y velocity is handled separately to stop the player from falling from the sky when they reach maximum
			// horizontal speed.
			if ((ticksInUse > 100) && (Math.abs(caster.motionY) < speed || caster.motionY / caster.getLookVec().y < 0)) {
				caster.motionY += caster.getLookVec().y * (acceleration + ticksInUse / 20)+ Y_NUDGE_ACCELERATION;
			}

			if (!Wizardry.settings.replaceVanillaFallDamage)
				caster.fallDistance = 0.0f;
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
		return item == AncientSpellcraftItems.ancient_spellcraft_spell_book || item == AncientSpellcraftItems.ancient_spellcraft_scroll;
	}
}
