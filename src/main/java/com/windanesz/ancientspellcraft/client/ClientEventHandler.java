package com.windanesz.ancientspellcraft.client;

import com.windanesz.ancientspellcraft.client.entity.ASFakePlayer;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftPotions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(Side.CLIENT)
public class ClientEventHandler {
	public static int x, y, z;
	public static int prevx, prevy, prevz;
	public static float prevyaw, prevpitch;
	public static boolean enabled = false;
	public static int timeout = 0;

	public static boolean farsightActive = false;

	@SubscribeEvent
	public static void PlayerTick(TickEvent.PlayerTickEvent event) {
		if (event.player instanceof EntityPlayerSP) {
			if (ClientEventHandler.timeout > 0)
				ClientEventHandler.timeout--;

			if (ClientEventHandler.enabled && !event.player.isPotionActive(AncientSpellcraftPotions.eagle_eye)) {
				ClientEventHandler.enabled = false;
			}

			if (enabled) {

				ASFakePlayer.FAKE_PLAYER.setLocationAndAngles(x, y, z, Minecraft.getMinecraft().player.rotationYaw,
						Minecraft.getMinecraft().player.rotationPitch);

				ASFakePlayer.FAKE_PLAYER.prevRotationPitch = prevpitch;
				ASFakePlayer.FAKE_PLAYER.prevRotationYaw = prevyaw;
				ASFakePlayer.FAKE_PLAYER.rotationYawHead = Minecraft.getMinecraft().player.rotationYawHead;
				ASFakePlayer.FAKE_PLAYER.prevPosX = prevx;
				ASFakePlayer.FAKE_PLAYER.prevPosY = prevy;
				ASFakePlayer.FAKE_PLAYER.prevPosZ = prevz;
				Minecraft.getMinecraft().setRenderViewEntity(ASFakePlayer.FAKE_PLAYER);

				prevx = x;
				prevy = y;
				prevz = z;
				prevpitch = Minecraft.getMinecraft().player.rotationPitch;
				prevyaw = Minecraft.getMinecraft().player.rotationYaw;
			} else {
				Minecraft.getMinecraft().setRenderViewEntity(Minecraft.getMinecraft().player);
			}

		}
	}

	@SubscribeEvent
	public static void onFOVUpdate(FOVUpdateEvent event) {
		if (farsightActive && event.getEntity() instanceof EntityPlayerSP && event.getEntity().isHandActive()) {
			event.setNewfov(0.1F);
		} else {
			farsightActive = false;
		}
	}
}
