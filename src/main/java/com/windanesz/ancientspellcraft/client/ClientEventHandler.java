package com.windanesz.ancientspellcraft.client;

import com.windanesz.ancientspellcraft.client.entity.ASFakePlayer;
import com.windanesz.ancientspellcraft.registry.ASPotions;
import com.windanesz.ancientspellcraft.spell.ScryingOrb;
import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.client.layer.LayerCloak;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod.EventBusSubscriber(modid = AncientSpellcraft.MODID, value = Side.CLIENT)
@SideOnly(Side.CLIENT)
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

	private static boolean layersInitialized = false;

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
				World world = Minecraft.getMinecraft().world;
				ASFakePlayer fakePlayer = ASFakePlayer.get(world);
 //else {
					fakePlayer.setLocationAndAngles(x, y, z, Minecraft.getMinecraft().player.rotationYaw,
							Minecraft.getMinecraft().player.rotationPitch);
				//}


				fakePlayer.prevRotationPitch = previousPitch;
				fakePlayer.prevRotationYaw = pirevousYaw;
				fakePlayer.rotationYawHead = Minecraft.getMinecraft().player.rotationYawHead;
				fakePlayer.prevPosX = previousX;
				fakePlayer.prevPosY = previousY;
				fakePlayer.prevPosZ = previousZ;
				Minecraft.getMinecraft().setRenderViewEntity(fakePlayer);

				previousX = x;
				previousY = y;
				previousZ = z;
				previousPitch = Minecraft.getMinecraft().player.rotationPitch;
				pirevousYaw = Minecraft.getMinecraft().player.rotationYaw;
				if (ScryingOrb.isScrying(event.player)) {
					BlockPos pos = ScryingOrb.getBlockPos(event.player);
					fakePlayer.setLocationAndAngles(pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f, Minecraft.getMinecraft().player.rotationYaw,
							Minecraft.getMinecraft().player.rotationPitch);

				}
			} else if (Minecraft.getMinecraft().getRenderViewEntity() instanceof ASFakePlayer) {

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

	@SubscribeEvent
	public static void onPlayerRender(RenderPlayerEvent.Pre event) {
		//if (!layersInitialized) {
		//	initializeCloakLayers();
		//	layersInitialized = true;
		//}
	}

	private static void initializeCloakLayers() {
		RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
		
		// Add cloak layer to all player renderers
		if (renderManager.getSkinMap().containsKey("default")) {
			RenderPlayer defaultRenderer = renderManager.getSkinMap().get("default");
			if (!hasCloakLayer(defaultRenderer)) {
				defaultRenderer.addLayer(new LayerCloak(defaultRenderer));
			}
		}
		
		if (renderManager.getSkinMap().containsKey("slim")) {
			RenderPlayer slimRenderer = renderManager.getSkinMap().get("slim");
			if (!hasCloakLayer(slimRenderer)) {
				slimRenderer.addLayer(new LayerCloak(slimRenderer));
			}
		}
	}

	private static boolean hasCloakLayer(RenderPlayer renderer) {
		// Since layerRenderers is not accessible, we'll just add the layer
		// and let it handle duplicates internally
		return false; // Always return false to ensure layer is added
	}
}