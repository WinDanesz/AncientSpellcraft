package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.registry.ASItems;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.spell.SpellRay;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.MagicDamage;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.ParticleBuilder.Type;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
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
		super(AncientSpellcraft.MODID, "pyrokinesis", SpellActions.SUMMON, true);
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

            if (!world.isRemote) {
                if (MagicDamage.isEntityImmune(MagicDamage.DamageType.FIRE, target)) {
                    if (ticksInUse == 1 && caster instanceof EntityPlayer)
                        ((EntityPlayer) caster)
                                .sendStatusMessage(new TextComponentTranslation("spell.resist", target.getName(),
                                        this.getNameForTranslationFormatted()), true);
                    // This now only damages in line with the maxHurtResistantTime. Some mods don't play nicely and fiddle
                    // with this mechanic for their own purposes, so this line makes sure that doesn't affect wizardry.
                } else if (ticksInUse % ((EntityLivingBase) target).maxHurtResistantTime == 1) {
                    target.setFire((int) (getProperty(BURN_DURATION).floatValue()));
                    EntityUtils.attackEntityWithoutKnockback(target,
                            MagicDamage.causeDirectMagicDamage(caster, MagicDamage.DamageType.FIRE),
                            getProperty(DAMAGE).floatValue() * modifiers.get(SpellModifiers.POTENCY));

                    ((EntityLivingBase) target).addPotionEffect(new PotionEffect(MobEffects.SLOWNESS,
                            (int) (getProperty(SLOW_DURATION).floatValue()), 1));
                }
            }

			if (world.isRemote) {

				for (int i = 0; i < 10; i++) {
					double dx = (world.rand.nextDouble() * (world.rand.nextBoolean() ? 1 : -1)) * 0.1;
					double dy = (world.rand.nextDouble() * (world.rand.nextBoolean() ? 1 : -1)) * 0.1;
					double dz = (world.rand.nextDouble() * (world.rand.nextBoolean() ? 1 : -1)) * 0.1;

					ParticleBuilder.create(Type.MAGIC_FIRE)
							.entity(target)
							.pos(0, target.height / 2, 0)
							.vel(dx, dy, dz)
							.spawn(world);

				}
			}
			return true;
		}

		return false;
	}

@Override
protected boolean onBlockHit(World world, BlockPos pos, EnumFacing side, Vec3d hit, EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {
    // Check if the block is TNT
    if (world.getBlockState(pos).getBlock() == net.minecraft.init.Blocks.TNT) {
        // Ignite TNT
        if (!world.isRemote) {
            world.setBlockToAir(pos);
            net.minecraft.entity.item.EntityTNTPrimed entityTNTPrimed = new net.minecraft.entity.item.EntityTNTPrimed(world,
                    (double)pos.getX() + 0.5D,
                    (double)pos.getY() + 0.75D,
                    (double)pos.getZ() + 0.5D,
                    caster instanceof EntityLiving ? (EntityLiving)caster : null);
            world.spawnEntity(entityTNTPrimed);
            world.playSound(null, pos, net.minecraft.init.SoundEvents.ENTITY_TNT_PRIMED, net.minecraft.util.SoundCategory.BLOCKS, 1.0F, 1.0F);
        }
        return true;
    }
    return false;
}

	@Override
	protected boolean onMiss(World world, EntityLivingBase caster, Vec3d origin, Vec3d direction, int ticksInUse, SpellModifiers modifiers) {
		return false;
	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == ASItems.ancient_spellcraft_spell_book || item == ASItems.ancient_spellcraft_scroll;
	}
}
