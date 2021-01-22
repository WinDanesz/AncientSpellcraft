package com.windanesz.ancientspellcraft.registry;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.block.BlockArtefactPensive;
import com.windanesz.ancientspellcraft.block.BlockCandleLight;
import com.windanesz.ancientspellcraft.block.BlockCrystalLeaves;
import com.windanesz.ancientspellcraft.block.BlockCrystalLog;
import com.windanesz.ancientspellcraft.block.BlockCrystalMine;
import com.windanesz.ancientspellcraft.block.BlockCrystalOre;
import com.windanesz.ancientspellcraft.block.BlockDevoritium;
import com.windanesz.ancientspellcraft.block.BlockDevoritiumBars;
import com.windanesz.ancientspellcraft.block.BlockDevoritiumOre;
import com.windanesz.ancientspellcraft.block.BlockDimensionBoundary;
import com.windanesz.ancientspellcraft.block.BlockDimensionFocus;
import com.windanesz.ancientspellcraft.block.BlockHardFrostedIce;
import com.windanesz.ancientspellcraft.block.BlockHellFire;
import com.windanesz.ancientspellcraft.block.BlockIceDoor;
import com.windanesz.ancientspellcraft.block.BlockIceWorkbench;
import com.windanesz.ancientspellcraft.block.BlockMageLight;
import com.windanesz.ancientspellcraft.block.BlockRuinedImbuementAltar;
import com.windanesz.ancientspellcraft.block.BlockScribingDesk;
import com.windanesz.ancientspellcraft.block.BlockSkullWatch;
import com.windanesz.ancientspellcraft.block.BlockSnowSlab;
import com.windanesz.ancientspellcraft.block.BlockSphereCognizance;
import com.windanesz.ancientspellcraft.tileentity.TileArtefactPensive;
import com.windanesz.ancientspellcraft.tileentity.TileCandleLight;
import com.windanesz.ancientspellcraft.tileentity.TileMageLight;
import com.windanesz.ancientspellcraft.tileentity.TileScribingDesk;
import com.windanesz.ancientspellcraft.tileentity.TileSkullWatch;
import com.windanesz.ancientspellcraft.tileentity.TileSphereCognizance;
import electroblob.wizardry.constants.Element;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nonnull;

@GameRegistry.ObjectHolder(AncientSpellcraft.MODID)
@Mod.EventBusSubscriber
public class AncientSpellcraftBlocks {

	private AncientSpellcraftBlocks() {} // no instances

	@Nonnull
	@SuppressWarnings("ConstantConditions")
	private static <T> T placeholder() { return null; }

	public static final Block NETHER_FIRE = placeholder();
	public static final Block SNOW_DOUBLE_SLAB = placeholder();
	public static final Block SNOW_SLAB = placeholder();
	public static final Block ICE_DOOR = placeholder();
	public static final Block ICE_BED = placeholder();
	public static final Block ICE_CRAFTING_TABLE = placeholder();
	public static final Block HARD_FROSTED_ICE = placeholder();
	public static final Block ARTEFACT_PENSIVE = placeholder();
	public static final Block SPHERE_COGNIZANCE = placeholder();
	public static final Block MAGELIGHT = placeholder();
	public static final Block CANDLELIGHT = placeholder();
	public static final Block SKULL_WATCH = placeholder();
	public static final Block DIMENSION_BOUNDARY = placeholder();
	public static final Block DIMENSION_FOCUS = placeholder();
	public static final Block SCRIBING_DESK = placeholder();
	public static final Block IMBUEMENT_ALTAR_RUINED = placeholder();

	public static final Block CRYSTAL_ORE_FIRE = placeholder();
	public static final Block CRYSTAL_ORE_EARTH = placeholder();
	public static final Block CRYSTAL_ORE_HEALING = placeholder();
	public static final Block CRYSTAL_ORE_ICE = placeholder();
	public static final Block CRYSTAL_ORE_LIGHTNING = placeholder();
	public static final Block CRYSTAL_ORE_NECROMANCY = placeholder();
	public static final Block CRYSTAL_ORE_SORCERY = placeholder();

	public static final Block DEVORITIUM_BLOCK = placeholder();
	public static final Block DEVORITIUM_ORE = placeholder();
	public static final Block DEVORITIUM_BARS = placeholder();
	public static final Block DEVORITIUM_DOOR = placeholder();

	public static final Block LOG_CRYSTAL_TREE = placeholder();
//	public static final Block log_crystal_tree2 = placeholder();
	public static final Block LEAVES_CRYSTAL_TREE = placeholder();
	public static final Block CRYSTAL_MINE = placeholder();

	public static void registerBlock(IForgeRegistry<Block> registry, String name, Block block) {
		block.setRegistryName(AncientSpellcraft.MODID, name);
		block.setTranslationKey(block.getRegistryName().toString());
		registry.register(block);
	}

	@SubscribeEvent
	public static void register(RegistryEvent.Register<Block> event) {

		IForgeRegistry<Block> registry = event.getRegistry();

		registerBlock(registry, "nether_fire", new BlockHellFire());
		registerBlock(registry, "snow_double_slab", (new BlockSnowSlab.Double()).setSoundType(SoundType.SNOW));
		registerBlock(registry, "snow_slab", (new BlockSnowSlab.Half()).setSoundType(SoundType.SNOW));
		registerBlock(registry, "ice_door", (new BlockIceDoor(Material.PACKED_ICE)));
		registerBlock(registry, "ice_crafting_table", (new BlockIceWorkbench()));
		registerBlock(registry, "hard_frosted_ice", new BlockHardFrostedIce());
		registerBlock(registry, "artefact_pensive", new BlockArtefactPensive().setCreativeTab(AncientSpellcraftTabs.ANCIENTSPELLCRAFT_GEAR));
		registerBlock(registry, "sphere_cognizance", new BlockSphereCognizance().setCreativeTab(AncientSpellcraftTabs.ANCIENTSPELLCRAFT));
		registerBlock(registry, "skull_watch", new BlockSkullWatch());
		registerBlock(registry, "magelight", new BlockMageLight());
		registerBlock(registry, "candlelight", new BlockCandleLight());
		registerBlock(registry, "dimension_boundary", new BlockDimensionBoundary());
		registerBlock(registry, "dimension_focus", new BlockDimensionFocus());
		registerBlock(registry, "scribing_desk", new BlockScribingDesk());

		registerBlock(registry, "crystal_ore_fire", new BlockCrystalOre(Element.FIRE));
		registerBlock(registry, "crystal_ore_earth", new BlockCrystalOre(Element.EARTH));
		registerBlock(registry, "crystal_ore_healing", new BlockCrystalOre(Element.HEALING));
		registerBlock(registry, "crystal_ore_ice", new BlockCrystalOre(Element.ICE));
		registerBlock(registry, "crystal_ore_lightning", new BlockCrystalOre(Element.LIGHTNING));
		registerBlock(registry, "crystal_ore_necromancy", new BlockCrystalOre(Element.NECROMANCY));
		registerBlock(registry, "crystal_ore_sorcery", new BlockCrystalOre(Element.SORCERY));

		registerBlock(registry, "devoritium_block", new BlockDevoritium());
		registerBlock(registry, "devoritium_ore", new BlockDevoritiumOre());
		registerBlock(registry, "devoritium_bars", new BlockDevoritiumBars());
		registerBlock(registry, "crystal_mine", new BlockCrystalMine());


		registerBlock(registry, "log_crystal_tree", new BlockCrystalLog());
//		registerBlock(registry, "log_crystal_tree2", new Block(Material.WOOD).setCreativeTab(AncientSpellcraftTabs.ANCIENTSPELLCRAFT));
		registerBlock(registry, "leaves_crystal_tree", new BlockCrystalLeaves());
		registerBlock(registry, "imbuement_altar_ruined", new BlockRuinedImbuementAltar(Material.ROCK));
	}

	/**
	 * Called from the preInit method in the main mod class to register all the tile entities.
	 */
	public static void registerTileEntities() {
		// Nope, these still don't have their own registry...
		GameRegistry.registerTileEntity(TileArtefactPensive.class, new ResourceLocation(AncientSpellcraft.MODID, "artefact_pensive"));
		GameRegistry.registerTileEntity(TileSphereCognizance.class, new ResourceLocation(AncientSpellcraft.MODID, "sphere_cognizance"));
		GameRegistry.registerTileEntity(TileMageLight.class, new ResourceLocation(AncientSpellcraft.MODID, "magelight"));
		GameRegistry.registerTileEntity(TileCandleLight.class, new ResourceLocation(AncientSpellcraft.MODID, "candlelight"));
		GameRegistry.registerTileEntity(TileSkullWatch.class, new ResourceLocation(AncientSpellcraft.MODID, "skull_watch"));
		GameRegistry.registerTileEntity(TileScribingDesk.class, new ResourceLocation(AncientSpellcraft.MODID, "scribing_desk"));
	}

}
