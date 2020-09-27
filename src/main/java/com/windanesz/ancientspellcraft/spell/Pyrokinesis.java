package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftItems;
import electroblob.wizardry.spell.SpellRay;
import electroblob.wizardry.util.MagicDamage;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.ParticleBuilder.Type;
import electroblob.wizardry.util.SpellModifiers;
import electroblob.wizardry.util.WizardryUtilities;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

public class Pyrokinesis extends SpellRay {

	public static String SLOW_DURATION = "slow_duration";

	public Pyrokinesis() {
		super(AncientSpellcraft.MODID, "pyrokinesis", true, EnumAction.BOW);
		this.aimAssist(0.4f);
		this.particleSpacing(1);
		this.particleJitter(0.05);
		this.particleVelocity(0.3);
		addProperties(DAMAGE, BURN_DURATION, SLOW_DURATION);
		this.soundValues(0.8f, 1, 0.2f);
	}

	@Override
	public boolean canBeCastBy(EntityLiving npc, boolean override) { return true; }

	@Override
	public boolean canBeCastBy(TileEntityDispenser dispenser) { return true; }

	@Override
	protected SoundEvent[] createSounds() {
		return this.createContinuousSpellSounds();
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
	protected boolean onEntityHit(World world, Entity target, Vec3d hit, EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {

		// Can't be cast by dispensers so we know caster isn't null, but just in case...
		if (caster != null && (target instanceof EntityLivingBase)) {

			if (MagicDamage.isEntityImmune(MagicDamage.DamageType.FIRE, target)) {
				if (!world.isRemote && ticksInUse == 1 && caster instanceof EntityPlayer)
					((EntityPlayer) caster)
							.sendStatusMessage(new TextComponentTranslation("spell.resist", target.getName(),
									this.getNameForTranslationFormatted()), true);
				// This now only damages in line with the maxHurtResistantTime. Some mods don't play nicely and fiddle
				// with this mechanic for their own purposes, so this line makes sure that doesn't affect wizardry.
			} else if (ticksInUse % ((EntityLivingBase) target).maxHurtResistantTime == 1) {
				target.setFire((int) (getProperty(BURN_DURATION).floatValue()));
				WizardryUtilities.attackEntityWithoutKnockback(target,
						MagicDamage.causeDirectMagicDamage(caster, MagicDamage.DamageType.FIRE),
						getProperty(DAMAGE).floatValue() * modifiers.get(SpellModifiers.POTENCY));

				((EntityLivingBase) target).addPotionEffect(new PotionEffect(MobEffects.SLOWNESS,
						(int) (getProperty(SLOW_DURATION).floatValue()), 1));
			}


			if (world.isRemote) {

				for (int i = 0; i < 10; i++) {
					double dx = (world.rand.nextDouble() * (world.rand.nextBoolean() ? 1 : -1)) * 0.1;
					double dy = (world.rand.nextDouble() * (world.rand.nextBoolean() ? 1 : -1)) * 0.1;
					double dz = (world.rand.nextDouble() * (world.rand.nextBoolean() ? 1 : -1)) * 0.1;

					ParticleBuilder.create(Type.MAGIC_FIRE)
							.entity(target)
							.pos(0,target.height / 2,0)
							.vel(dx, dy, dz)
							.spawn(world);

				}
				////					 Sparks
				//					if(world.rand.nextInt(6) == 0){
				//						ParticleBuilder.create(Type.FLASH).entity(caster).clr(0xffd8fa).scale(2)
				//								.pos(caster != null ? centre.subtract(caster.getPositionVector()) : centre).spawn(world);
				//						ParticleBuilder.create(Type.LIGHTNING).entity(caster).clr(0xff4384).scale(0.5f)
				//								.pos(caster != null ? centre.subtract(caster.getPositionVector()) : centre)
				//								.target(centre.add(world.rand.nextDouble() * 2 - 1, world.rand.nextDouble() * 2 - 1,
				//										world.rand.nextDouble() * 2 - 1)).spawn(world);
				//					}

				//
				//				ParticleBuilder.create(Type.SPARKLE, target).vel(0, 0.05, 0).time(15).scale(0.6f).clr(0.2f, 0.6f, 1)
				//						.fade(1f, 1f, 1f).spawn(world);
			}

			return true;
		}

		return false;
	}

	@Override
	protected boolean onBlockHit(World world, BlockPos pos, EnumFacing side, Vec3d hit, EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {
		return false;
	}

	@Override
	protected boolean onMiss(World world, EntityLivingBase caster, Vec3d origin, Vec3d direction, int ticksInUse, SpellModifiers modifiers) {
		return false;
	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == AncientSpellcraftItems.ancient_spellcraft_spell_book || item == AncientSpellcraftItems.ancient_spellcraft_scroll;
	}
}
