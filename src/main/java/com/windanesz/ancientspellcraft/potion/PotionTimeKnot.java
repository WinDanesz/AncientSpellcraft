package com.windanesz.ancientspellcraft.potion;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.entity.living.Vector;
import electroblob.wizardry.potion.PotionMagicEffect;
import electroblob.wizardry.util.ParticleBuilder;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;

import static com.windanesz.ancientspellcraft.util.ASParticles.TIME_KNOT;

public class PotionTimeKnot extends PotionMagicEffect {

	private float particleSize = 0.7F;

	public PotionTimeKnot() {
		super(false, 0xD81A0B, new ResourceLocation(AncientSpellcraft.MODID, "textures/gui/potion_icon_time_knot.png"));
		setBeneficial();
		this.setPotionName("potion." + AncientSpellcraft.MODID + ":time_knot");
	}

	@Override
	public boolean isReady(int duration, int amplifier) {
		return true; // Execute performEffect every tick
	}

	@Override
	public void performEffect(EntityLivingBase entity, int strength) {
		super.performEffect(entity, strength);
		if (entity instanceof EntityPlayer) {

			if (entity.world.isRemote) {

				Vec3d height = entity.getPositionVector().add(0, 0.84, 0);
				Vec3d leftHand = Vector.toRectangular(Math.toRadians(entity.renderYawOffset - 90), 0).times(0.385).withY(0).toMinecraft();
				leftHand = leftHand.add(height);

				//				if (entity.isSwingInProgress) {
				//					leftHand = (leftHand.add(entity.getLookVec().x * 0.5, entity.swingProgress * 0.6, entity.getLookVec().z * 0.5)); // offset for hand swinging
				//				}
				ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(leftHand).time(4).vel(entity.world.rand.nextGaussian() / 40, entity.world.rand.nextDouble() / 40,
						entity.world.rand.nextGaussian() / 40).clr(89, 238, 155).collide(false).
						scale(particleSize / 1.5F).spawn(entity.world);

				ParticleBuilder.create(TIME_KNOT).pos(leftHand).face(EnumFacing.UP).time(4).clr(89, 238, 155).collide(false).
						scale(particleSize / 1.0F).spawn(entity.world);

			}
		}
	}

}
