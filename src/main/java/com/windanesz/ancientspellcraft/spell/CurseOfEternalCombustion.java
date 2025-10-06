package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.registry.ASPotions;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.spell.SpellBuff;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class CurseOfEternalCombustion extends SpellRayAS {

    public CurseOfEternalCombustion() {
        super("curse_of_eternal_combustion", SpellActions.POINT, false);
        this.soundValues(1, 1.1f, 0.2f);
        addProperties(EFFECT_STRENGTH);
    }

    @Override
    protected boolean onEntityHit(World world, Entity target, Vec3d hit, EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {

        if (EntityUtils.isLiving(target)) {

            if (!world.isRemote) {
                ((EntityLivingBase) target).addPotionEffect(new PotionEffect(ASPotions.curse_of_eternal_combustion, Integer.MAX_VALUE,
                        getProperty(EFFECT_STRENGTH).intValue() + SpellBuff.getStandardBonusAmplifier(modifiers.get(SpellModifiers.POTENCY))));
            }
        }

        return true;
    }

    @Override
    protected boolean onBlockHit(World world, BlockPos pos, EnumFacing side, Vec3d hit, EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {
        return false;
    }

    @Override
    protected boolean onMiss(World world, EntityLivingBase caster, Vec3d origin, Vec3d direction, int ticksInUse, SpellModifiers modifiers) {
        return true;
    }

    @Override
    protected void spawnParticle(World world, double x, double y, double z, double vx, double vy, double vz) {
        ParticleBuilder.create(ParticleBuilder.Type.FLAME).pos(x, y, z).spawn(world);
        ParticleBuilder.create(ParticleBuilder.Type.SMOKE).pos(x, y, z).spawn(world);
    }
}
