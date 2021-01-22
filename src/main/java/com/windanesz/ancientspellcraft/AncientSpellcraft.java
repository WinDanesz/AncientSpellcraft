package com.windanesz.ancientspellcraft;

import com.windanesz.ancientspellcraft.client.gui.GuiHandlerAS;
import com.windanesz.ancientspellcraft.command.CommandListBiomes;
import com.windanesz.ancientspellcraft.integration.baubles.ASBaublesIntegration;
import com.windanesz.ancientspellcraft.item.ItemRelic;
import com.windanesz.ancientspellcraft.packet.ASPacketHandler;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftBiomes;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftBlocks;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftDimensions;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftItems;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftLoot;
import com.windanesz.ancientspellcraft.registry.BookshelfItems;
import com.windanesz.ancientspellcraft.worldgen.WorldGenCrystalShardOre;
import com.windanesz.ancientspellcraft.worldgen.WorldGenDevoritiumOre;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.apache.logging.log4j.Logger;

import java.util.Random;

@Mod(modid = AncientSpellcraft.MODID, name = AncientSpellcraft.NAME, version = AncientSpellcraft.VERSION, acceptedMinecraftVersions = AncientSpellcraft.MC_VERSION, dependencies = "required-after:ebwizardry@[4.3,4.4);after:jei@[4.15.0,)")
public class AncientSpellcraft {

	public static final String MODID = "ancientspellcraft";
	public static final String NAME = "Ancient Spellcraft by Dan";
	public static final String VERSION = "1.1.2";
	public static final String MC_VERSION = "[1.12.2]";

	public static final Random rand = new Random();

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

	public static final Material DEVORITIUM = (new Material(MapColor.BLACK));
	public static final Item.ToolMaterial DEVORITIUM_TOOL_MATERIAL = EnumHelper.addToolMaterial("devoritium", 2, 250, 6.0F, 2.0F, 0);

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
		GameRegistry.registerWorldGenerator(new WorldGenDevoritiumOre(), 0);
		AncientSpellcraftItems.registerDispenseBehaviours();
		MinecraftForge.EVENT_BUS.register(instance); // Since there's already an instance we might as well use it
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandlerAS());
		ASPacketHandler.initPackets();
		proxy.registerParticles();

		AncientSpellcraftDimensions.init();

		proxy.init();

		BookshelfItems.InitBookshelfItems();

	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {

		ItemRelic.initEffects();
		proxy.initialiseLayers();
	}

	@EventHandler
	public void serverStarting(FMLServerStartingEvent event) {
		event.registerServerCommand(new CommandListBiomes());
	}
}
