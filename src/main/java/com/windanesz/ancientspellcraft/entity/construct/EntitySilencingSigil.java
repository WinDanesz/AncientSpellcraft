package com.windanesz.ancientspellcraft.entity.construct;

import electroblob.wizardry.entity.construct.EntityMagicConstruct;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.WizardryUtilities;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.ITickableSound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

@Mod.EventBusSubscriber
public class EntitySilencingSigil extends EntityMagicConstruct {

	public EntitySilencingSigil(World world) {
		super(world);
		this.height = 0.2f;
		this.width = 1.0f;
	}

	@Override
	public void onUpdate() {

		super.onUpdate();

		if (this.world.isRemote && this.rand.nextInt(15) == 0) {
			double radius = 0.5 + rand.nextDouble() * 0.3;
			float angle = rand.nextFloat() * (float) Math.PI * 2;
			;

			ParticleBuilder.create(ParticleBuilder.Type.SPARKLE, rand, this.posX + radius * MathHelper.cos(angle), this.posY + 0.1,
					this.posZ + radius * MathHelper.sin(angle), 0.03, true).clr(0.15f, 0.7f, 0.15f).spawn(world);
		}
	}

	@Override
	protected void entityInit() {}

	@Override
	public boolean canRenderOnFire() {
		return false;
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public static void silenceSound(PlaySoundEvent event) {
		if (Minecraft.getMinecraft().world == null || event.getSound() instanceof ITickableSound) {
			return;
		}
		ISound sound = event.getSound();

		List<EntitySilencingSigil> sigilList = WizardryUtilities.getEntitiesWithinRadius(10, sound.getXPosF(), sound.getYPosF(), sound.getZPosF(), Minecraft.getMinecraft().world, EntitySilencingSigil.class);
		{
			if (!sigilList.isEmpty()) {
				event.setResultSound(null);
			}
		}
	}


}
