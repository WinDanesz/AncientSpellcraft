package com.windanesz.ancientspellcraft.potion;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.registry.ASBlocks;
import electroblob.wizardry.potion.PotionMagicEffect;
import electroblob.wizardry.util.ParticleBuilder;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

import static com.windanesz.ancientspellcraft.util.ASUtils.getRandomNumberInRange;

public class PotionCandleLight extends PotionMagicEffect {
	private float particleSize = 0.7F;

	public PotionCandleLight() {
		super(false, 0xFAFCCC, new ResourceLocation(AncientSpellcraft.MODID, "textures/gui/potion_icon_candlelight.png"));
		this.setPotionName("potion." + AncientSpellcraft.MODID + ":candlelight");
		this.setBeneficial();

	}

	@Override
	public boolean isReady(int duration, int amplifier) {
		return true; // Execute the effect every tick
	}

	@Override
	public void performEffect(EntityLivingBase entity, int strength) {
		super.performEffect(entity, strength);
		if (entity instanceof EntityPlayer) {
			if (entity.world.isRemote) {

				ParticleBuilder.create(ParticleBuilder.Type.MAGIC_FIRE).entity(entity).pos(0, 2.8, 0).time(6 + getRandomNumberInRange(0, 4)).vel(entity.world.rand.nextGaussian() / 80, entity.world.rand.nextDouble() / 80,
						entity.world.rand.nextGaussian() / 80).clr(246, 180 + getRandomNumberInRange(0, 50), 80).collide(false).
						scale(0.46F).spawn(entity.world);

			} else {
				if (entity.ticksExisted % 15 == 0 && entity instanceof EntityPlayer && !entity.world.isRemote && entity.world.isAirBlock(entity.getPosition().up())) {
					entity.world.setBlockState(entity.getPosition().up(), ASBlocks.CANDLELIGHT.getDefaultState());
				}
			}
		}
	}

}
