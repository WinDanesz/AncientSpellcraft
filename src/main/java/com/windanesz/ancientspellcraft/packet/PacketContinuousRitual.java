package com.windanesz.ancientspellcraft.packet;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.ritual.Ritual;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.Item;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * <b>[Server -> Client]</b> This packet is sent when a spell is cast by a player and returns true, and is sent to other
 * clients so they can spawn the particles themselves. What sending this packet effectively does is make the
 * {@link Item#onItemRightClick} method client-consistent just for the item that sends it. Interestingly,
 * {@link Item#onUsingTick} is client-consistent already, so continuous spells don't need to send packets from there
 * (this is probably something to do with eating particles or usage actions).
 */
public class PacketContinuousRitual implements IMessageHandler<PacketContinuousRitual.Message, IMessage> {

	@Override
	public IMessage onMessage(PacketContinuousRitual.Message message, MessageContext ctx){

		// Just to make sure that the side is correct
		if(ctx.side.isClient()){
			// Using a fully qualified name is a good course of action here; we don't really want to clutter the proxy
			// methods any more than necessary.
			net.minecraft.client.Minecraft.getMinecraft().addScheduledTask(() -> AncientSpellcraft.proxy.handleContinuousRitualPacket(message));
		}

		return null;
	}

	public static class Message implements IMessage {

		/** EntityID of the caster */
		public int casterID;
		/** ID of the ritual being cast */
		public int ritualID;
//		/** SpellModifiers for the spell */
//		public SpellModifiers modifiers;
		/** The hand that is holding the itemstack used to cast the spell. Defaults to MAIN_HAND. */
		public EnumHand hand;
		public int x;
		public int y;
		public int z;

		// This constructor is required otherwise you'll get errors (used somewhere in fml through reflection)
		public Message(){
		}

		public Message(int casterID, Ritual ritual, int x, int y, int z){

			this.casterID = casterID;
			this.ritualID = ritual.networkID();
			this.x = x;
			this.y = y;
			this.z = z;
		}

		@Override
		public void fromBytes(ByteBuf buf){

			// The order is important
			this.casterID = buf.readInt();
			this.ritualID = buf.readInt();
			this.x = buf.readInt();
			this.y = buf.readInt();
			this.z = buf.readInt();
		}

		@Override
		public void toBytes(ByteBuf buf){

			buf.writeInt(casterID);
			buf.writeInt(ritualID);
			buf.writeInt(x);
			buf.writeInt(y);
			buf.writeInt(z);
		}
	}
}
