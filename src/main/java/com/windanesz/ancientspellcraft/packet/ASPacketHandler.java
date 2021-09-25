package com.windanesz.ancientspellcraft.packet;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class ASPacketHandler {

	public static SimpleNetworkWrapper net;

	public static void initPackets(){
		net = NetworkRegistry.INSTANCE.newSimpleChannel(AncientSpellcraft.MODID.toUpperCase());
		registerMessage(PacketControlInput.class, PacketControlInput.Message.class);
		registerMessage(PacketActivateBauble.class, PacketActivateBauble.Message.class);
		registerMessage(PacketSelectRadialItemSpell.class, PacketSelectRadialItemSpell.Message.class);
		registerMessage(PacketStartRitual.class, PacketStartRitual.Message.class);
		registerMessage(PacketContinuousRitual.class, PacketContinuousRitual.Message.class);
		registerMessage(PacketMushroomActivation.class, PacketMushroomActivation.Message.class);
		registerMessage(PacketSorcerySwordHit.class, PacketSorcerySwordHit.Message.class);
	}

	private static int nextPacketId = 0;

	private static <REQ extends IMessage, REPLY extends IMessage> void registerMessage(
			Class<? extends IMessageHandler<REQ, REPLY>> packet, Class<REQ> message){
		net.registerMessage(packet, message, nextPacketId, Side.CLIENT);
		net.registerMessage(packet, message, nextPacketId, Side.SERVER);
		nextPacketId++;
	}

}
