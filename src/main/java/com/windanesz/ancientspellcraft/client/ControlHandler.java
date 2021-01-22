package com.windanesz.ancientspellcraft.client;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.integration.baubles.ASBaublesIntegration;
import com.windanesz.ancientspellcraft.modules.freecam.FreeCam;
import com.windanesz.ancientspellcraft.packet.ASPacketHandler;
import com.windanesz.ancientspellcraft.packet.PacketActivateBauble;
import electroblob.wizardry.item.ItemArtefact;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;

import java.util.List;

import static com.windanesz.ancientspellcraft.client.ClientProxy.*;

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

	@SubscribeEvent
	public void KeyInputEvent(InputEvent.KeyInputEvent event) {
		if (ClientEventHandler.timeout == 0 && GameSettings.isKeyDown(switc)) {
			ClientEventHandler.timeout = 10;
			if (ClientEventHandler.enabled)
				ClientEventHandler.enabled = false;
			else
				ClientEventHandler.enabled = true;
			if (ClientEventHandler.enabled) {
				ClientEventHandler.x = (int) Minecraft.getMinecraft().player.posX;
				ClientEventHandler.y = (int) Minecraft.getMinecraft().player.posY;
				ClientEventHandler.z = (int) Minecraft.getMinecraft().player.posZ;
			}
		}
		if (ClientEventHandler.timeout == 0 && GameSettings.isKeyDown(reset) && ClientEventHandler.enabled) {
			ClientEventHandler.timeout = 10;
			ClientEventHandler.x = (int) Minecraft.getMinecraft().player.posX;
			ClientEventHandler.y = (int) Minecraft.getMinecraft().player.posY;
			ClientEventHandler.z = (int) Minecraft.getMinecraft().player.posZ;
		}
		if (ClientEventHandler.timeout == 0 && GameSettings.isKeyDown(yt) && ClientEventHandler.enabled) {
			ClientEventHandler.timeout = 2;
			ClientEventHandler.y++;
		}
		if (ClientEventHandler.timeout == 0 && GameSettings.isKeyDown(yt2) && ClientEventHandler.enabled) {
			ClientEventHandler.timeout = 2;
			ClientEventHandler.y--;
		}
		if (ClientEventHandler.timeout == 0 && GameSettings.isKeyDown(zt) && ClientEventHandler.enabled) {
			ClientEventHandler.timeout = 2;
			ClientEventHandler.z++;
		}
		if (ClientEventHandler.timeout == 0 && GameSettings.isKeyDown(zt2) && ClientEventHandler.enabled) {
			ClientEventHandler.timeout = 2;
			ClientEventHandler.z--;
		}
		if (ClientEventHandler.timeout == 0 && GameSettings.isKeyDown(xt) && ClientEventHandler.enabled) {
			ClientEventHandler.timeout = 2;
			ClientEventHandler.x++;
		}
		if (ClientEventHandler.timeout == 0 && GameSettings.isKeyDown(xt2) && ClientEventHandler.enabled) {
			ClientEventHandler.timeout = 2;
			ClientEventHandler.x--;
		}
	}

}
