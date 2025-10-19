package com.windanesz.ancientspellcraft.potion;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import electroblob.wizardry.potion.Curse;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PotionCurseOfEternalTempest extends Curse {

	public PotionCurseOfEternalTempest() {
		super(true, 0xADD8E6, new ResourceLocation(AncientSpellcraft.MODID, "textures/gui/potion_icon_curse_of_eternal_tempest.png"));
		this.setPotionName("potion." + AncientSpellcraft.MODID + ":curse_of_eternal_tempest");
	}

	@Override
	public void performEffect(EntityLivingBase entity, int strength) {
		World world = entity.world;

		if (world.isRemote && entity.ticksExisted % 20 == 0) {
			// Cloud particles are spawned on both client and server by ParticleBuilder, so no isRemote check needed here for that.
			for (int i = 0; i < 3; i++) {
				ParticleBuilder.create(ParticleBuilder.Type.CLOUD).entity(entity)
						.time(100)
						.scale(0.5f)
						.pos((world.rand.nextDouble() - 0.5) * 1.2,
						entity.getEyeHeight() + 2,
						(world.rand.nextDouble() - 0.5) * 1.2)
						.clr(0.3f, 0.3f, 0.3f).shaded(true).spawn(world);
			}
		}

		// Lightning strike logic should only run on the server
		if (!world.isRemote) {
			// Average of once every 5 minutes (6000 ticks)
			if (world.canSeeSky(new BlockPos(entity).up()) && world.rand.nextInt(600) == 0) {
				EntityLightningBolt lightning = new EntityLightningBolt(world, entity.posX, entity.posY, entity.posZ, false);
				world.addWeatherEffect(lightning);

				world.addWeatherEffect(lightning);
			}
		}
	}

	@Override
	public boolean isReady(int duration, int amplifier) {
		// Run every 20 ticks (1 second)
		return true;
	}
}
