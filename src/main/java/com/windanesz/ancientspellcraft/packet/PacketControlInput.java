package com.windanesz.ancientspellcraft.packet;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.client.gui.ContainerScribingDesk;
import com.windanesz.ancientspellcraft.client.gui.ContainerSphereCognizance;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
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
				}
			});
		}

		return null;
	}

	public enum ControlType {
		APPLY_BUTTON,
		CRAFT_SPELL
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
