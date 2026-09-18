package com.windanesz.ancientspellcraft.packet;

import electroblob.wizardry.data.WizardData;
import electroblob.wizardry.spell.Grapple;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/** Synchronizes the otherwise client-local Grapple target selected by the Vinekeeper's Ring. */
public class PacketVinekeeperTarget implements IMessageHandler<PacketVinekeeperTarget.Message, IMessage> {

	@Override
	public IMessage onMessage(Message message, MessageContext context) {
		if (context.side.isClient()) {
			Minecraft.getMinecraft().addScheduledTask(() -> {
				Entity entity = Minecraft.getMinecraft().world.getEntityByID(message.casterId);
				if (entity instanceof EntityPlayer) {
					RayTraceResult target = new RayTraceResult(RayTraceResult.Type.BLOCK, message.hitVec,
							EnumFacing.byIndex(message.face), BlockPos.fromLong(message.blockPos));
					WizardData data = WizardData.get((EntityPlayer) entity);
					if (data != null) data.setVariable(Grapple.TARGET_KEY, target);
				}
			});
		}
		return null;
	}

	public static class Message implements IMessage {
		private int casterId;
		private long blockPos;
		private int face;
		private Vec3d hitVec;

		public Message() {}

		public Message(EntityPlayer caster, RayTraceResult target) {
			casterId = caster.getEntityId();
			blockPos = target.getBlockPos().toLong();
			face = target.sideHit.getIndex();
			hitVec = target.hitVec;
		}

		@Override
		public void fromBytes(ByteBuf buffer) {
			casterId = buffer.readInt();
			blockPos = buffer.readLong();
			face = buffer.readInt();
			hitVec = new Vec3d(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
		}

		@Override
		public void toBytes(ByteBuf buffer) {
			buffer.writeInt(casterId);
			buffer.writeLong(blockPos);
			buffer.writeInt(face);
			buffer.writeDouble(hitVec.x);
			buffer.writeDouble(hitVec.y);
			buffer.writeDouble(hitVec.z);
		}
	}
}
