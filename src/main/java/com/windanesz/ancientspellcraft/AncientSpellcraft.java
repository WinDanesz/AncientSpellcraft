package com.windanesz.ancientspellcraft;

import com.windanesz.ancientspellcraft.client.gui.GuiHandlerAS;
import com.windanesz.ancientspellcraft.command.CommandListBiomes;
import com.windanesz.ancientspellcraft.data.Knowledge;
import com.windanesz.ancientspellcraft.integration.baubles.ASBaublesIntegration;
import com.windanesz.ancientspellcraft.packet.ASPacketHandler;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftBiomes;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftBlocks;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftDimensions;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftItems;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftLoot;
import com.windanesz.ancientspellcraft.registry.BookshelfItems;
import com.windanesz.ancientspellcraft.worldgen.WorldGenCrystalShardOre;
import electroblob.wizardry.constants.Element;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.apache.logging.log4j.Logger;

import java.util.Random;

@Mod(modid = AncientSpellcraft.MODID, name = AncientSpellcraft.NAME, version = AncientSpellcraft.VERSION, acceptedMinecraftVersions = AncientSpellcraft.MC_VERSION, dependencies = "required-after:ebwizardry@[4.3,4.4)")
public class AncientSpellcraft {

	public static final String MODID = "ancientspellcraft";
	public static final String NAME = "Ancient Spellcraft by Dan";
	public static final String VERSION = "1.1.0";
	public static final String MC_VERSION = "[1.12.2]";

	public static Element RUNIC;

	public static final Random rand = new Random();

	//	public static final WorldGenAS worldGenAS = new WorldGenAS();

	//	public static final Settings settings = new Settings();

	/**
	 * Static instance of the {@link Settings} object for Wizardry.
	 */
	public static Settings settings = new Settings();

	public static Logger logger;

	// The instance of wizardry that Forge uses.
	@Mod.Instance(AncientSpellcraft.MODID)
	public static AncientSpellcraft instance;

	// Location of the proxy code, used by Forge.
	@SidedProxy(clientSide = "com.windanesz.ancientspellcraft.client.ClientProxy", serverSide = "com.windanesz.ancientspellcraft.CommonProxy")
	public static CommonProxy proxy;

	/**
	 * Static instance of the {@link electroblob.wizardry.Settings} object for Wizardry.
	 */

	//	public static final Logger LOG = LogManager.getLogger(MODID);
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		logger = event.getModLog();
		settings = new Settings();

		proxy.registerRenderers();

		AncientSpellcraftLoot.preInit();
		AncientSpellcraftBlocks.registerTileEntities();
		AncientSpellcraftBiomes.preInit();
		BookshelfItems.preInitBookShelfModelTextures();

		ASBaublesIntegration.init();

	}

	@EventHandler
	public void init(FMLInitializationEvent event) {

		GameRegistry.registerWorldGenerator(new WorldGenCrystalShardOre(), 0);
		AncientSpellcraftItems.registerDispenseBehaviours();
		MinecraftForge.EVENT_BUS.register(instance); // Since there's already an instance we might as well use it
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandlerAS());
		ASPacketHandler.initPackets();
		proxy.registerParticles();

		AncientSpellcraftDimensions.init();

		Knowledge.init();
		BookshelfItems.InitBookshelfItems();

	}

	@EventHandler
	public void serverStarting(FMLServerStartingEvent event) {
		event.registerServerCommand(new CommandListBiomes());
	}
}
