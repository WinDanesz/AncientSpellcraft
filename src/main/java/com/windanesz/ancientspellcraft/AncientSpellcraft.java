package com.windanesz.ancientspellcraft;

import com.windanesz.ancientspellcraft.proxy.IProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = AncientSpellcraft.MOD_ID, name = AncientSpellcraft.NAME, version = AncientSpellcraft.VERSION, acceptedMinecraftVersions = AncientSpellcraft.MC_VERSION, dependencies = "required-after:ebwizardry@[4.2.7,4.3)")
public class AncientSpellcraft {

	public static final String MOD_ID = "ancientspellcraft";
	public static final String NAME = "Ancient Spellcraft by Dan";
	public static final String VERSION = "1.0.0";
	public static final String MC_VERSION = "[1.12.2]";

	public static final String CLIENT = "com.windanesz.ancientspellcraft.proxy.ClientProxy";
	public static final String SERVER = "com.windanesz.ancientspellcraft.proxy.ServerProxy";

	@SidedProxy(clientSide = AncientSpellcraft.CLIENT, serverSide = AncientSpellcraft.SERVER)
	public static IProxy proxy;

	public static final Logger LOG = LogManager.getLogger(MOD_ID);

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		proxy.preInit(event);
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.init(event);
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		proxy.postInit(event);
	}

}
