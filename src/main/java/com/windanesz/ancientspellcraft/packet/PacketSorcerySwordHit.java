package com.windanesz.ancientspellcraft.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import javax.annotation.Nullable;

/**
 * <b>[Client -> Server]</b> This packet is for control events such as buttons in GUIs and key presses.
 * Based on {@link electroblob.wizardry.packet.PacketControlInput} (author: Electroblob)
 */
public class PacketSorcerySwordHit implements IMessageHandler<PacketSorcerySwordHit.Message, IMessage> {

	@Override
	public IMessage onMessage(Message message, MessageContext ctx) {

		// Just to make sure that the side is correct
		if (ctx.side.isServer()) {

			final EntityPlayerMP player = ctx.getServerHandler().player;

			player.getServerWorld().addScheduledTask(() -> {

//				Entity attacker = ctx.getServerHandler().player.world.getEntityByID(message.attackerEntityId);
				Entity target = ctx.getServerHandler().player.world.getEntityByID(message.targetEntityId);

				if (player.getEntityId() == message.attackerEntityId && target != null) {
//					Vec3d vec = EntityUtils.findSpaceForTeleport(player, GeometryUtils.getFaceCentre(target.getPosition().north(), EnumFacing.DOWN), false);
//
//					System.out.println("teleport player");
//					Vec3d projectilePos = new Vec3d(1, 0, 0);
//					projectilePos = projectilePos.rotateYaw((float) Math.toRadians(-player.rotationYaw - 90));
//					projectilePos = projectilePos.add(player.getPositionVector());
//					projectilePos = projectilePos.add(new Vec3d(0, 0, 1).rotatePitch((float) Math.toRadians(-player.rotationPitch)).rotateYaw((float) Math.toRadians(-player.rotationYawHead)));
//					projectilePos = projectilePos.add(new Vec3d(0, 0.3, 0));
					player.attackTargetEntityWithCurrentItem(target);
					//player.setPositionAndUpdate(projectilePos.x,projectilePos.y,projectilePos.z);
//					player.getWorld().spawnEntity(entityLoc, EntityType.ARMOR_STAND);
//					player.setPositionAndRotation(target.posX, target.posY + (double)target.getEyeHeight(), this.posZ, (float)this.getHorizontalFaceSpeed(), (float)this.getVerticalFaceSpeed());

				}
			});
		}

		return null;
	}

	public static class Message implements IMessage {

		public int attackerEntityId;

		public int targetEntityId;

		// This constructor is required otherwise you'll get errors (used somewhere in fml through reflection)
		public Message() {
		}

		public Message(@Nullable Entity attacker, @Nullable Entity target) {
			this.attackerEntityId = attacker == null ? -1 : attacker.getEntityId();
			this.targetEntityId = target == null ? -1 : target.getEntityId();
		}

		@Override
		public void fromBytes(ByteBuf buf) {
			// The order is important
			this.attackerEntityId = buf.readInt();
			this.targetEntityId = buf.readInt();
		}

		@Override
		public void toBytes(ByteBuf buf) {
			buf.writeInt(attackerEntityId);
			buf.writeInt(targetEntityId);
		}
	}
}
