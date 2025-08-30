package com.windanesz.ancientspellcraft.tileentity;

import electroblob.wizardry.util.MagicDamage;
import electroblob.wizardry.util.ParticleBuilder;
import net.minecraft.util.EnumParticleTypes;

/**
 * Tile entity for the arcane flame block.
 * Arcane flames have a purple color and deal magic damage.
 */
public class TileEntityArcaneFlame extends AbstractMagicFlameTileEntity {
    
    @Override
    protected void spawnParticles() {
        for (int i = 0; i < 3; i++) {
            double x = pos.getX() + 0.2 + world.rand.nextDouble() * 0.6;
            double y = pos.getY() + 0.2 + world.rand.nextDouble() * 0.6;
            double z = pos.getZ() + 0.2 + world.rand.nextDouble() * 0.6;

            // Arcane (purple) flame particles
            ParticleBuilder.create(ParticleBuilder.Type.SPARKLE)
                    .pos(x, y, z)
                    .vel(0, world.rand.nextDouble() * 0.05, 0)
                    .time(10 + world.rand.nextInt(10))
                    .clr(0.6f, 0.2f, 0.8f + world.rand.nextFloat() * 0.2f)
                    .spawn(world);

            // Regular fire particles for all types (less frequent)
            if (world.rand.nextInt(3) == 0) {
                world.spawnParticle(EnumParticleTypes.FLAME, x, y, z, 0, 0.02, 0);
            }
        }
    }
    
    @Override
    public MagicDamage.DamageType getDamageType() {
        return MagicDamage.DamageType.MAGIC;
    }
}