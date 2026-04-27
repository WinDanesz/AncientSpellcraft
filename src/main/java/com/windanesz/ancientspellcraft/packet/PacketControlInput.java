package com.windanesz.ancientspellcraft.packet;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.client.gui.ContainerScribingDesk;
import com.windanesz.ancientspellcraft.client.gui.ContainerSphereCognizance;
import com.windanesz.ancientspellcraft.item.ItemCloakOfLevitation;
import com.windanesz.ancientspellcraft.registry.ASItems;
import electroblob.wizardry.item.ItemArtefact;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * <b>[Client -> Server]</b> This packet is for control events such as buttons in GUIs and key presses.
 * Based on {@link electroblob.wizardry.packet.PacketControlInput} (author: Electroblob)
 */
public class PacketControlInput implements IMessageHandler<PacketControlInput.Message, IMessage> {

	@Override
	public IMessage onMessage(Message message, MessageContext ctx) {
		if (ctx.side.isClient()) {
			Minecraft.getMinecraft().addScheduledTask(() -> {
				if (message.controlType == ControlType.DOUBLE_JUMP && Minecraft.getMinecraft().player != null) {
					Minecraft.getMinecraft().player.motionY = 0.75;
					Minecraft.getMinecraft().player.fallDistance = 0;
					spawnVaultDiskParticlesClient();
				}
			});
			return null;
		}

		// Just to make sure that the side is correct
		if (ctx.side.isServer()) {

			final EntityPlayerMP player = ctx.getServerHandler().player;

			player.getServerWorld().addScheduledTask(() -> {

				switch (message.controlType) {

					case APPLY_BUTTON:

						if (!(player.openContainer instanceof ContainerSphereCognizance)) {
							AncientSpellcraft.logger.warn("Received a PacketControlInput, but the player that sent it was not " +
									"currently using a crystal ball of cognizance. This should not happen!");
						} else {
							ContainerSphereCognizance container = (ContainerSphereCognizance) player.openContainer;
							container.onApplyButtonPressed();

						}

						break;

					case CRAFT_SPELL:

						if (!(player.openContainer instanceof ContainerScribingDesk)) {
							AncientSpellcraft.logger.warn("Received a PacketControlInput, but the player that sent it was not " +
									"currently using a scribing desc. This should not happen!");
						} else {
							ContainerScribingDesk container = (ContainerScribingDesk) player.openContainer;
							container.onApplyButtonPressed();

						}

						break;

					case LEVITATION_TOGGLE:
						ItemCloakOfLevitation.toggleLevitation(player);
						break;

					case DOUBLE_JUMP:
						if (ItemArtefact.isArtefactActive(player, ASItems.charm_vaulting_boots)
								&& !player.getCooldownTracker().hasCooldown(ASItems.charm_vaulting_boots)) {
							player.getCooldownTracker().setCooldown(ASItems.charm_vaulting_boots, 20);
							// Authorize the vault and send it back to this client only.
							ASPacketHandler.net.sendTo(new PacketControlInput.Message(ControlType.DOUBLE_JUMP), player);
							player.fallDistance = 0;
							spawnVaultDiskParticles(player);
						}
						break;
				}
			});
		}

		return null;
	}

	public enum ControlType {
		APPLY_BUTTON,
		CRAFT_SPELL,
		LEVITATION_TOGGLE,
		DOUBLE_JUMP
	}

	private static void spawnVaultDiskParticles(EntityPlayerMP player) {
		double px = player.posX;
		double py = player.posY + 0.05;
		double pz = player.posZ;

		if (player.world instanceof WorldServer) {
			WorldServer world = (WorldServer) player.world;
			// Flat orange magic disk under the player's feet.
			for (int ring = 1; ring <= 5; ring++) {
				double radius = ring * 0.18;
				int count = 8 + ring * 6;
				for (int i = 0; i < count; i++) {
					double angle = 2.0 * Math.PI * i / count;
					double dx = Math.cos(angle) * radius;
					double dz = Math.sin(angle) * radius;
					world.spawnParticle(EnumParticleTypes.SPELL_MOB,
							px + dx, py, pz + dz,
							1, 1.0, 0.45, 0.05, 0.0);
				}
			}
		}
	}

	private static void spawnVaultDiskParticlesClient() {
		if (Minecraft.getMinecraft().player == null || Minecraft.getMinecraft().world == null) return;
		double px = Minecraft.getMinecraft().player.posX;
		double py = Minecraft.getMinecraft().player.posY + 0.05;
		double pz = Minecraft.getMinecraft().player.posZ;

		for (int ring = 1; ring <= 5; ring++) {
			double radius = ring * 0.18;
			int count = 8 + ring * 6;
			for (int i = 0; i < count; i++) {
				double angle = 2.0 * Math.PI * i / count;
				double dx = Math.cos(angle) * radius;
				double dz = Math.sin(angle) * radius;
				Minecraft.getMinecraft().world.spawnParticle(EnumParticleTypes.SPELL_MOB,
						px + dx, py, pz + dz,
						1.0, 0.45, 0.05);
			}
		}
	}

	public static class Message implements IMessage {

		private ControlType controlType;

		// This constructor is required otherwise you'll get errors (used somewhere in fml through reflection)
		public Message() {
		}

		public Message(ControlType type) {
			this.controlType = type;
		}

		@Override
		public void fromBytes(ByteBuf buf) {
			// The order is important
			this.controlType = ControlType.values()[buf.readInt()];
		}

		@Override
		public void toBytes(ByteBuf buf) {
			buf.writeInt(controlType.ordinal());
		}
	}
}
