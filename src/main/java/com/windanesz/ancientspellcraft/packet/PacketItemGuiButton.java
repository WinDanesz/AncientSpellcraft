package com.windanesz.ancientspellcraft.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketItemGuiButton implements IMessageHandler<PacketItemGuiButton.Message, IMessage> {
    @Override
    public IMessage onMessage(Message message, MessageContext ctx) {
        if (ctx.side.isServer()) {
            final EntityPlayerMP player = ctx.getServerHandler().player;
            player.getServerWorld().addScheduledTask(() -> {
                // This packet is now obsolete; no action needed
            });
        }
        return null;
    }

    public static class Message implements IMessage {
        public Message() {}
        @Override
        public void fromBytes(ByteBuf buf) {}
        @Override
        public void toBytes(ByteBuf buf) {}
    }
} 