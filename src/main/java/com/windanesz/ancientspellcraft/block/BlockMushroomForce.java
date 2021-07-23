package com.windanesz.ancientspellcraft.block;

import electroblob.wizardry.item.ItemArtefact;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.MagicDamage;
import electroblob.wizardry.util.ParticleBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BlockMushroomForce extends BlockMagicMushroom {

	public BlockMushroomForce() {
		super();
	}

	@Override
	public boolean applyHarmfulEffect(World world,
			@Nullable Block block, BlockPos pos, DamageSource source, float damage, IBlockState state,
			@Nullable EntityLivingBase caster, EntityLivingBase target, float potency) {

		if (!MagicDamage.isEntityImmune(MagicDamage.DamageType.SHOCK, target)) {
			if (!(target instanceof EntityPlayer && !ItemArtefact.isArtefactActive((EntityPlayer) target, WizardryItems.amulet_anchoring))) {
				double velocityFactor = 1.0f;

				double dx = target.posX - pos.getX() + 0.5f;
				double dy = target.posY + 1 - pos.getY() + 0.5f;
				double dz = target.posZ - pos.getZ() + 0.5f;

				target.motionX = velocityFactor * dx;
				target.motionY = velocityFactor * dy;
				target.motionZ = velocityFactor * dz;

				// Player motion is handled on that player's client so needs packets
				if (target instanceof EntityPlayerMP) {
					((EntityPlayerMP) target).connection.sendPacket(new SPacketEntityVelocity(target));
				}
			}

			if (world.isRemote) {
				ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(pos.getX() + 0.5f, pos.getY() + 0.3f, pos.getZ() + 0.5f).scale(2.2f)
						.clr(0.23f, 0.89f, 0.41f).time(20).shaded(true).spawn(world);
				ParticleBuilder.create(ParticleBuilder.Type.SPHERE).pos(pos.getX() + 0.5f, pos.getY() + 0.3f, pos.getZ() + 0.5f).scale(0.3f).clr(0.23f, 0.89f, 0.41f).spawn(world);
				world.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL, pos.getX(), pos.getY() + 0.3, pos.getZ(), 0, 0, 0);
			}
			EntityUtils.attackEntityWithoutKnockback(target, source, damage);
		}
		return true;
	}
}
