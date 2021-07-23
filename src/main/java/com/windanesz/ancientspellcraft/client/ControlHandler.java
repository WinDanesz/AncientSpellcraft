package com.windanesz.ancientspellcraft.client;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.integration.baubles.ASBaublesIntegration;
import com.windanesz.ancientspellcraft.packet.ASPacketHandler;
import com.windanesz.ancientspellcraft.packet.PacketActivateBauble;
import electroblob.wizardry.item.ItemArtefact;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;

import java.util.List;

//@SideOnly(Side.CLIENT)
@Mod.EventBusSubscriber(Side.CLIENT)
public class ControlHandler {

	static boolean charmKeyPressed = false;

	@SubscribeEvent
	public static void onTickEvent(TickEvent.ClientTickEvent event) {

		if (event.phase == TickEvent.Phase.END)
			return; // Only really needs to be once per tick

		if (AncientSpellcraft.proxy instanceof com.windanesz.ancientspellcraft.client.ClientProxy) {

			EntityPlayer player = Minecraft.getMinecraft().player;

			if (player != null) {

				if (ASBaublesIntegration.enabled()) {
					if (com.windanesz.ancientspellcraft.client.ClientProxy.KEY_ACTIVATE_CHARM_BAUBLE.isKeyDown() && Minecraft.getMinecraft().inGameHasFocus) {
						if (!charmKeyPressed) {
							charmKeyPressed = true;
							activateBauble(ItemArtefact.Type.CHARM, player);
						}
					} else {
						charmKeyPressed = false;
					}
				}

				boolean resetTimeout = false;
				// Astral Travel movement logic
				if (ClientEventHandler.ASTRAL_TRAVEL_ENABLED && Minecraft.getMinecraft().player != null && ClientEventHandler.astralTravelInputTimeout == 0 &&
						GameSettings.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindForward)) {
					resetTimeout = true;
					if (Minecraft.getMinecraft().player.getHorizontalFacing() == EnumFacing.SOUTH) {
						ClientEventHandler.z++;
					} else if (Minecraft.getMinecraft().player.getHorizontalFacing() == EnumFacing.NORTH) {
						ClientEventHandler.z--;
					} else if (Minecraft.getMinecraft().player.getHorizontalFacing() == EnumFacing.EAST) {
						ClientEventHandler.x++;
					} else if (Minecraft.getMinecraft().player.getHorizontalFacing() == EnumFacing.WEST) {
						ClientEventHandler.x--;
					}
				}
				if (ClientEventHandler.ASTRAL_TRAVEL_ENABLED && Minecraft.getMinecraft().player != null && ClientEventHandler.astralTravelInputTimeout == 0 &&
						GameSettings.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindJump)) {
					resetTimeout = true;
					ClientEventHandler.y++;
				} else if ((ClientEventHandler.ASTRAL_TRAVEL_ENABLED && Minecraft.getMinecraft().player != null && ClientEventHandler.astralTravelInputTimeout == 0 &&
						GameSettings.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindSneak))) {
					resetTimeout = true;
					ClientEventHandler.y--;

				}
				if (resetTimeout) {
					ClientEventHandler.astralTravelInputTimeout = 1;
				}
			}
		}
	}

	private static void activateBauble(ItemArtefact.Type type, EntityPlayer player) {

		List<ItemStack> stacks = ASBaublesIntegration.getEquippedArtefactStacks(player, type);
		if (!stacks.isEmpty() && player.getHeldItemOffhand().isEmpty()) {
			if (type == ItemArtefact.Type.CHARM) {
				IMessage msg = new PacketActivateBauble.Message(PacketActivateBauble.ControlType.CHARM);
				ASPacketHandler.net.sendToServer(msg);
			}
		}
	}
}
