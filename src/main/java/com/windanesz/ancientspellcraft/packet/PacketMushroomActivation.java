package com.windanesz.ancientspellcraft.packet;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import javax.annotation.Nullable;

/**
 * <b>[Server -> Client]</b> This packet is sent when a mushroom was triggered by BlockMagicMushroom#onEntityCollision(...) to spawn  the particles and visual effects
 */
public class PacketMushroomActivation implements IMessageHandler<PacketMushroomActivation.Message, IMessage> {

	@Override
	public IMessage onMessage(Message message, MessageContext ctx) {
		// Just to make sure that the side is correct
		if (ctx.side.isClient()) {
			// Using a fully qualified name is a good course of action here; we don't really want to clutter the proxy
			// methods any more than necessary.
			net.minecraft.client.Minecraft.getMinecraft().addScheduledTask(() -> AncientSpellcraft.proxy.handleMushroomActivationPacket(message));
		}

		return null;
	}

	public static class Message implements IMessage {

		/**
		 * The entity triggering the mushroom
		 */
		public int activatorEntityID;
		/**
		 * Coordinates of the activator entity position
		 */
		public double x, y, z;

		/**
		 * BlockPos of the mushroom
		 */
		public BlockPos pos;


		// This constructor is required otherwise you'll get errors (used somewhere in fml through reflection)
		public Message() {}

		public Message(double x, double y, double z, BlockPos pos, @Nullable Entity activator) {

			this.x = x;
			this.y = y;
			this.z = z;
			this.pos = pos;
			this.activatorEntityID = activator == null ? -1 : activator.getEntityId();

		}

		@Override
		public void fromBytes(ByteBuf buf) {

			// The order is important
			this.x = buf.readDouble();
			this.y = buf.readDouble();
			this.z = buf.readDouble();
			this.pos = BlockPos.fromLong(buf.readLong());
			this.activatorEntityID = buf.readInt();

		}

		@Override
		public void toBytes(ByteBuf buf) {

			// The order is important
			buf.writeDouble(x);
			buf.writeDouble(y);
			buf.writeDouble(z);
			buf.writeLong(pos.toLong());
			buf.writeInt(activatorEntityID);
		}

	}
}
