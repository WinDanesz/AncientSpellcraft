package com.windanesz.ancientspellcraft.packet;

import com.windanesz.ancientspellcraft.integration.baubles.ASBaublesIntegration;
import electroblob.wizardry.item.ItemArtefact;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.List;

/**
 * <b>[Client -> Server]</b> This packet is for handling the bauble-slot item casting buttons.
 *
 */
public class PacketActivateBauble implements IMessageHandler<PacketActivateBauble.Message, IMessage> {

	@Override
	public IMessage onMessage(Message message, MessageContext ctx) {

		// Just to make sure that the side is correct
		if (ctx.side.isServer()) {

			final EntityPlayerMP player = ctx.getServerHandler().player;

			player.getServerWorld().addScheduledTask(() -> {

				switch (message.controlType) {

					case CHARM:
						castItem(ItemArtefact.Type.CHARM, player, 0);
						break;

					case RING_1:
						castItem(ItemArtefact.Type.RING, player, 0);
						break;

					case RING_2:
						castItem(ItemArtefact.Type.RING, player, 1);
						break;
				}
			});
		}

		return null;
	}

	private void castItem(ItemArtefact.Type type, EntityPlayerMP player, int slotIndex) {

		List<ItemStack> stacks = ASBaublesIntegration.getEquippedArtefactStacks(player, type);

		if (!stacks.isEmpty() && player.getHeldItemOffhand().isEmpty()) {

			// get artefact
			ItemStack stack = stacks.get(slotIndex);

			// set it to offhand
			player.setHeldItem(EnumHand.OFF_HAND, stack);
			ASBaublesIntegration.setArtefactToSlot(player, ItemStack.EMPTY, type);
			Minecraft mc = Minecraft.getMinecraft();

			// activate in the offhand
			mc.playerController.processRightClick(player, player.world, EnumHand.OFF_HAND);
			player.swingArm(EnumHand.OFF_HAND);

			// move it back to bauble slot
			ASBaublesIntegration.setArtefactToSlot(player, stack, type);
			player.setHeldItem(EnumHand.OFF_HAND, ItemStack.EMPTY);
		}
	}

	public enum ControlType {
		CHARM,
		RING_1,
		RING_2
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
