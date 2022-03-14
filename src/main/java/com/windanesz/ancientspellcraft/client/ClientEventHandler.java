package com.windanesz.ancientspellcraft.client;

import com.windanesz.ancientspellcraft.client.entity.ASFakePlayer;
import com.windanesz.ancientspellcraft.registry.ASPotions;
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

	// True when the Eagle Eye potion is active
	public static boolean EAGLE_EYE_ENABLED = false;

	// True when the Astral Travel potion is active
	public static boolean ASTRAL_TRAVEL_ENABLED = false;

	private static int previousX, previousY, previousZ;
	private static float pirevousYaw, previousPitch;

	// timeout variable to limit keyboard input for movement
	public static int astralTravelInputTimeout = 0;

	// True when the continuous spell Farsight is being casted
	public static boolean FARSIGHT_ACTIVE = false;

	@SubscribeEvent
	public static void PlayerTick(TickEvent.PlayerTickEvent event) {
		if (event.player instanceof EntityPlayerSP) {
			if (ClientEventHandler.astralTravelInputTimeout > 0)
				ClientEventHandler.astralTravelInputTimeout--;

			// just to be sure..
			if (ClientEventHandler.EAGLE_EYE_ENABLED && !event.player.isPotionActive(ASPotions.eagle_eye)) {
				ClientEventHandler.EAGLE_EYE_ENABLED = false;
			}

			// just to be sure..
			if (ClientEventHandler.ASTRAL_TRAVEL_ENABLED && !event.player.isPotionActive(ASPotions.astral_projection)) {
				ClientEventHandler.ASTRAL_TRAVEL_ENABLED = false;
			}

			if (EAGLE_EYE_ENABLED || ASTRAL_TRAVEL_ENABLED) {

				ASFakePlayer.FAKE_PLAYER.setLocationAndAngles(x, y, z, Minecraft.getMinecraft().player.rotationYaw,
						Minecraft.getMinecraft().player.rotationPitch);

				ASFakePlayer.FAKE_PLAYER.prevRotationPitch = previousPitch;
				ASFakePlayer.FAKE_PLAYER.prevRotationYaw = pirevousYaw;
				ASFakePlayer.FAKE_PLAYER.rotationYawHead = Minecraft.getMinecraft().player.rotationYawHead;
				ASFakePlayer.FAKE_PLAYER.prevPosX = previousX;
				ASFakePlayer.FAKE_PLAYER.prevPosY = previousY;
				ASFakePlayer.FAKE_PLAYER.prevPosZ = previousZ;
				Minecraft.getMinecraft().setRenderViewEntity(ASFakePlayer.FAKE_PLAYER);

				previousX = x;
				previousY = y;
				previousZ = z;
				previousPitch = Minecraft.getMinecraft().player.rotationPitch;
				pirevousYaw = Minecraft.getMinecraft().player.rotationYaw;
			} else {
				Minecraft.getMinecraft().setRenderViewEntity(Minecraft.getMinecraft().player);
			}

		}
	}

	@SubscribeEvent
	public static void onFOVUpdate(FOVUpdateEvent event) {
		if (FARSIGHT_ACTIVE && event.getEntity() instanceof EntityPlayerSP && event.getEntity().isHandActive()) {
			event.setNewfov(0.1F);
		} else {
			FARSIGHT_ACTIVE = false;
		}
	}
}
