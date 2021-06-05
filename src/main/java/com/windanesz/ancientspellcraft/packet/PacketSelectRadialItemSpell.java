package com.windanesz.ancientspellcraft.packet;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import electroblob.wizardry.item.ISpellCastingItem;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * <b>[Client -> Server]</b> This packet is for control events such as buttons in GUIs and key presses.
 * Based on {@link electroblob.wizardry.packet.PacketControlInput} (author: Electroblob)
 */
public class PacketSelectRadialItemSpell implements IMessageHandler<PacketSelectRadialItemSpell.Message, IMessage> {

	@Override
	public IMessage onMessage(Message message, MessageContext ctx) {

		// Just to make sure that the side is correct
		if (ctx.side.isServer()) {

			final EntityPlayerMP player = ctx.getServerHandler().player;

			player.getServerWorld().addScheduledTask(() -> {

				if (!(player.getHeldItemMainhand().getItem() instanceof ISpellCastingItem) && !(player.getHeldItemOffhand().getItem() instanceof ISpellCastingItem)) {
					AncientSpellcraft.logger.warn("Received a PacketSelectRadialItemSpell, but the player was not holding an ISpellCastingItem. This should not happen!");
				} else {
					if ((player.getHeldItemMainhand().getItem() instanceof ISpellCastingItem)) {
						((ISpellCastingItem) player.getHeldItemMainhand().getItem()).selectSpell(player.getHeldItemMainhand(), message.spellIndex);
					} else {
						((ISpellCastingItem) player.getHeldItemOffhand().getItem()).selectSpell(player.getHeldItemOffhand(), message.spellIndex);
					}
				}
			});
		}

		return null;
	}

	public static class Message implements IMessage {

		private int spellIndex;

		// This constructor is required otherwise you'll get errors (used somewhere in fml through reflection)
		public Message() {
		}

		public Message(int spellIndex) {
			this.spellIndex = spellIndex;
		}

		@Override
		public void fromBytes(ByteBuf buf) {
			// The order is important
			this.spellIndex = buf.readInt();
		}

		@Override
		public void toBytes(ByteBuf buf) {
			buf.writeInt(spellIndex);
		}
	}
}
