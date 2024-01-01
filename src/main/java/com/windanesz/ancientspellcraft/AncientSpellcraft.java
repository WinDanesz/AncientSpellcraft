package com.windanesz.ancientspellcraft;

import com.windanesz.ancientspellcraft.capability.DeathMarkCapability;
import com.windanesz.ancientspellcraft.client.gui.GuiHandlerAS;
import com.windanesz.ancientspellcraft.command.CommandListBiomes;
import com.windanesz.ancientspellcraft.data.RitualDiscoveryData;
import com.windanesz.ancientspellcraft.data.SpellCategorization;
import com.windanesz.ancientspellcraft.integration.antiqueatlas.ASAntiqueAtlasIntegration;
import com.windanesz.ancientspellcraft.integration.artemislib.ASArtemisLibIntegration;
import com.windanesz.ancientspellcraft.integration.baubles.ASBaublesIntegration;
import com.windanesz.ancientspellcraft.item.ItemRelic;
import com.windanesz.ancientspellcraft.item.ItemWandUpgradeAS;
import com.windanesz.ancientspellcraft.misc.ASForfeits;
import com.windanesz.ancientspellcraft.packet.ASPacketHandler;
import com.windanesz.ancientspellcraft.registry.ASBiomes;
import com.windanesz.ancientspellcraft.registry.ASBlocks;
import com.windanesz.ancientspellcraft.registry.ASDimensions;
import com.windanesz.ancientspellcraft.registry.ASItems;
import com.windanesz.ancientspellcraft.registry.ASLoot;
import com.windanesz.ancientspellcraft.registry.BookshelfItems;
import com.windanesz.ancientspellcraft.ritual.Ritual;
import com.windanesz.ancientspellcraft.util.RitualProperties;
import com.windanesz.ancientspellcraft.worldgen.WorldGenAncientTemple;
import com.windanesz.ancientspellcraft.worldgen.WorldGenAstralDiamondOre;
import com.windanesz.ancientspellcraft.worldgen.WorldGenBattlemageCamp;
import com.windanesz.ancientspellcraft.worldgen.WorldGenBattlemageKeep;
import com.windanesz.ancientspellcraft.worldgen.WorldGenCrystalShardOre;
import com.windanesz.ancientspellcraft.worldgen.WorldGenCrystalSilverOre;
import com.windanesz.ancientspellcraft.worldgen.WorldGenDevoritiumOre;
import com.windanesz.ancientspellcraft.worldgen.WorldGenSageHill;
import com.windanesz.ancientspellcraft.worldgen.WorldgenAncientVault;
import electroblob.wizardry.event.SpellCastEvent;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.apache.logging.log4j.Logger;

import java.util.Random;

@Mod(modid = AncientSpellcraft.MODID, name = AncientSpellcraft.NAME, version = "@VERSION@", acceptedMinecraftVersions = "1.12.2",
		dependencies = "required-after:ebwizardry@[@WIZARDRY_VERSION@,4.4);after:jei@[4.15.0,);after:artemislib")
public class AncientSpellcraft {

	public static final String MODID = "ancientspellcraft";
	public static final String NAME = "Ancient Spellcraft by Dan";

	public static final Random rand = new Random();
	public static final Material DEVORITIUM = (new Material(MapColor.BLACK));
	public static final Item.ToolMaterial DEVORITIUM_TOOL_MATERIAL = EnumHelper.addToolMaterial("devoritium", 2, 250, 6.0F, 2.0F, 0);
	public static final ItemArmor.ArmorMaterial DEVORITIUM_ARMOR_MATERIAL = EnumHelper.addArmorMaterial(
			AncientSpellcraft.MODID + ":" + "devoritium_armor",
			AncientSpellcraft.MODID + ":devoritium_armor",
			16,
			new int[] {2, 5, 6, 3}, // reductionAmounts
			0,
			SoundEvents.ITEM_ARMOR_EQUIP_GENERIC,
			1);

	public static final SpellCastEvent.Source BATTLEMAGE_ITEM = EnumHelper.addEnum(SpellCastEvent.Source.class, "BATTLEMAGE_ITEM", new Class[]{});
	public static final SpellCastEvent.Source SAGE_ITEM = EnumHelper.addEnum(SpellCastEvent.Source.class, "SAGE_ITEM", new Class[]{});
	public static final SpellCastEvent.Source WARLOCK_ITEM = EnumHelper.addEnum(SpellCastEvent.Source.class, "WARLOCK_ITEM", new Class[]{});
	
	/**
	 * Static instance of the {@link Settings} object for AncientSpellcraft.
	 */
	public static Settings settings = new Settings();
	public static Logger logger;
	// The instance of wizardry that Forge uses.
	@Mod.Instance(AncientSpellcraft.MODID)
	public static AncientSpellcraft instance;
	// Location of the proxy code, used by Forge.
	@SidedProxy(clientSide = "com.windanesz.ancientspellcraft.client.ClientProxy", serverSide = "com.windanesz.ancientspellcraft.CommonProxy")
	public static CommonProxy proxy;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		logger = event.getModLog();
		settings = new Settings();

		proxy.registerRenderers();
		proxy.registerExtraHandbookContent();
		FMLInterModComms.sendMessage("ebwizardry", "addon_content", "I have stuff");
		ASLoot.preInit();
		ASBlocks.registerTileEntities();
		ASBiomes.preInit();
		BookshelfItems.preInitBookShelfModelTextures();

		// Capabilities
		DeathMarkCapability.register();

		ASBaublesIntegration.init();
		ASArtemisLibIntegration.init();
		ASAntiqueAtlasIntegration.init();
		RitualDiscoveryData.init();
		ASForfeits.register();

	}

	@EventHandler
	public void init(FMLInitializationEvent event) {

		// World generators
		GameRegistry.registerWorldGenerator(new WorldGenCrystalShardOre(), 0);
		GameRegistry.registerWorldGenerator(new WorldGenDevoritiumOre(), 0);
		GameRegistry.registerWorldGenerator(new WorldGenCrystalSilverOre(), 0);
		GameRegistry.registerWorldGenerator(new WorldGenAstralDiamondOre(), 0);

		// Weight is a misnomer, it's actually the priority (where lower numbers get generated first)
		GameRegistry.registerWorldGenerator(new WorldGenBattlemageCamp(), 20);
		GameRegistry.registerWorldGenerator(new WorldGenBattlemageKeep(), 20);
		GameRegistry.registerWorldGenerator(new WorldGenSageHill(), 20);
		GameRegistry.registerWorldGenerator(new WorldgenAncientVault(), 20);
		GameRegistry.registerWorldGenerator(new WorldGenAncientTemple(), 20);

		ASItems.registerDispenseBehaviours();
		MinecraftForge.EVENT_BUS.register(instance); // Since there's already an instance we might as well use it
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandlerAS());
		ASPacketHandler.initPackets();
		proxy.registerParticles();

		ASDimensions.init();


		RitualProperties.init();

		ItemWandUpgradeAS.init();

		proxy.init();

		BookshelfItems.InitBookshelfItems();
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {

		ItemRelic.initEffects();
		proxy.initialiseLayers();
		SpellCategorization.init();
		proxy.checkTranslationKeys();
		Ritual.registry.forEach(Ritual::init);
	}

	@EventHandler
	public void serverStarting(FMLServerStartingEvent event) {
		event.registerServerCommand(new CommandListBiomes());
	}

}
