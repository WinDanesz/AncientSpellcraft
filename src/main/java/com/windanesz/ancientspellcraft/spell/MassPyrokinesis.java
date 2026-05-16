package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.registry.ASItems;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.AllyDesignationSystem;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.MagicDamage;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.ParticleBuilder.Type;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class MassPyrokinesis extends Spell {

	public static String SLOW_DURATION = "slow_duration";

	public MassPyrokinesis() {
		super(AncientSpellcraft.MODID, "mass_pyrokinesis", SpellActions.SUMMON, true);
		addProperties(EFFECT_RADIUS, DAMAGE, BURN_DURATION, SLOW_DURATION);
		this.soundValues(0.8f, 1, 0.2f);
	}

	@Override
	public boolean canBeCastBy(EntityLiving npc, boolean override) { return true; }

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
		return pyrokinesis(world, caster, hand, ticksInUse, modifiers);
	}

	@Override
	public boolean cast(World world, EntityLiving caster, EnumHand hand, int ticksInUse, EntityLivingBase target, SpellModifiers modifiers) {
		return pyrokinesis(world, caster, hand, ticksInUse, modifiers);
	}

public boolean pyrokinesis(World world, EntityLivingBase caster, EnumHand hand, int ticksInUse, SpellModifiers modifiers) {
    boolean affected = false;

    // Affect entities as before
    for (EntityLivingBase entity : EntityUtils.getEntitiesWithinRadius(getProperty(EFFECT_RADIUS).floatValue(), caster.posX, caster.posY, caster.posZ, world, EntityLivingBase.class)) {
        if (AllyDesignationSystem.isAllied(caster, entity)) {continue;}

        if (MagicDamage.isEntityImmune(MagicDamage.DamageType.FIRE, entity)) {

        } else if (!world.isRemote && entity != caster && ticksInUse % entity.maxHurtResistantTime == 1) {
            entity.setFire((int) (getProperty(BURN_DURATION).floatValue()));
            EntityUtils.attackEntityWithoutKnockback(entity,
                MagicDamage.causeDirectMagicDamage(caster, MagicDamage.DamageType.FIRE),
                getProperty(DAMAGE).floatValue() * modifiers.get(SpellModifiers.POTENCY));

            ((EntityLivingBase) entity).addPotionEffect(new PotionEffect(MobEffects.SLOWNESS,
                (int) (getProperty(SLOW_DURATION).floatValue()), 1));
        }

        if (world.isRemote) {
            for (int i = 0; i < 10; i++) {
                double dx = (world.rand.nextDouble() * (world.rand.nextBoolean() ? 1 : -1)) * 0.1;
                double dy = (world.rand.nextDouble() * (world.rand.nextBoolean() ? 1 : -1)) * 0.1;
                double dz = (world.rand.nextDouble() * (world.rand.nextBoolean() ? 1 : -1)) * 0.1;

                ParticleBuilder.create(Type.MAGIC_FIRE)
                    .entity(entity)
                    .pos(0, entity.height / 2, 0)
                    .vel(dx, dy, dz)
                    .spawn(world);
            }
        }
        affected = true;
    }

    // Ignite nearby TNT blocks
    if (!world.isRemote) {
        int radius = (int) getProperty(EFFECT_RADIUS).floatValue();
        int x0 = (int) caster.posX;
        int y0 = (int) caster.posY;
        int z0 = (int) caster.posZ;

        // Check all blocks within radius
        for (int x = x0 - radius; x <= x0 + radius; x++) {
            for (int y = y0 - radius; y <= y0 + radius; y++) {
                for (int z = z0 - radius; z <= z0 + radius; z++) {
                    net.minecraft.util.math.BlockPos pos = new net.minecraft.util.math.BlockPos(x, y, z);

                    // Check if the block is TNT
                    if (world.getBlockState(pos).getBlock() == net.minecraft.init.Blocks.TNT) {
                        // Calculate distance from caster
                        double dist = Math.sqrt(pos.distanceSq(x0, y0, z0));
                        if (dist <= radius) {
                            // Ignite TNT
                            world.setBlockToAir(pos);
                            net.minecraft.entity.item.EntityTNTPrimed tnt = new net.minecraft.entity.item.EntityTNTPrimed(
                                world,
                                (double)pos.getX() + 0.5D,
                                (double)pos.getY() + 0.75D,
                                (double)pos.getZ() + 0.5D,
                                caster instanceof EntityLiving ? (EntityLiving)caster : null
                            );
                            world.spawnEntity(tnt);
                            world.playSound(null, pos, net.minecraft.init.SoundEvents.ENTITY_TNT_PRIMED,
                                          net.minecraft.util.SoundCategory.BLOCKS, 1.0F, 1.0F);
                            affected = true;
                        }
                    }
                }
            }
        }
    }

    return affected;
}

	@Override
	public boolean applicableForItem(Item item) {
		return item == ASItems.ancient_spellcraft_spell_book || item == ASItems.ancient_spellcraft_scroll;
	}
}
