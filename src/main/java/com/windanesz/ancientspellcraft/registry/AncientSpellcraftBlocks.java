package com.windanesz.ancientspellcraft.registry;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.block.BlockArcaneAnvil;
import com.windanesz.ancientspellcraft.block.BlockArtefactPensive;
import com.windanesz.ancientspellcraft.block.BlockCandleLight;
import com.windanesz.ancientspellcraft.block.BlockConjuredDirt;
import com.windanesz.ancientspellcraft.block.BlockConjuredMagma;
import com.windanesz.ancientspellcraft.block.BlockCrystalLeaves;
import com.windanesz.ancientspellcraft.block.BlockCrystalLog;
import com.windanesz.ancientspellcraft.block.BlockCrystalMine;
import com.windanesz.ancientspellcraft.block.BlockCrystalOre;
import com.windanesz.ancientspellcraft.block.BlockDevoritium;
import com.windanesz.ancientspellcraft.block.BlockDevoritiumBars;
import com.windanesz.ancientspellcraft.block.BlockDevoritiumDoor;
import com.windanesz.ancientspellcraft.block.BlockDevoritiumOre;
import com.windanesz.ancientspellcraft.block.BlockDimensionBoundary;
import com.windanesz.ancientspellcraft.block.BlockDimensionFocus;
import com.windanesz.ancientspellcraft.block.BlockHardFrostedIce;
import com.windanesz.ancientspellcraft.block.BlockHellFire;
import com.windanesz.ancientspellcraft.block.BlockIceDoor;
import com.windanesz.ancientspellcraft.block.BlockIceWorkbench;
import com.windanesz.ancientspellcraft.block.BlockMageLight;
import com.windanesz.ancientspellcraft.block.BlockMushroomCleansing;
import com.windanesz.ancientspellcraft.block.BlockMushroomEmpowering;
import com.windanesz.ancientspellcraft.block.BlockMushroomExplosive;
import com.windanesz.ancientspellcraft.block.BlockMushroomFire;
import com.windanesz.ancientspellcraft.block.BlockMushroomForce;
import com.windanesz.ancientspellcraft.block.BlockMushroomHeal;
import com.windanesz.ancientspellcraft.block.BlockMushroomIce;
import com.windanesz.ancientspellcraft.block.BlockMushroomMind;
import com.windanesz.ancientspellcraft.block.BlockMushroomPoison;
import com.windanesz.ancientspellcraft.block.BlockMushroomShock;
import com.windanesz.ancientspellcraft.block.BlockMushroomWither;
import com.windanesz.ancientspellcraft.block.BlockPlacedRune;
import com.windanesz.ancientspellcraft.block.BlockQuickSand;
import com.windanesz.ancientspellcraft.block.BlockRuinedImbuementAltar;
import com.windanesz.ancientspellcraft.block.BlockScribingDesk;
import com.windanesz.ancientspellcraft.block.BlockSentinel;
import com.windanesz.ancientspellcraft.block.BlockSkullWatch;
import com.windanesz.ancientspellcraft.block.BlockSnowSlab;
import com.windanesz.ancientspellcraft.block.BlockSphereCognizance;
import com.windanesz.ancientspellcraft.block.BlockUsedRune;
import com.windanesz.ancientspellcraft.tileentity.TileArcaneAnvil;
import com.windanesz.ancientspellcraft.tileentity.TileArtefactPensive;
import com.windanesz.ancientspellcraft.tileentity.TileCandleLight;
import com.windanesz.ancientspellcraft.tileentity.TileEntityMagicMushroom;
import com.windanesz.ancientspellcraft.tileentity.TileEntityRevertingBlock;
import com.windanesz.ancientspellcraft.tileentity.TileMageLight;
import com.windanesz.ancientspellcraft.tileentity.TileRune;
import com.windanesz.ancientspellcraft.tileentity.TileScribingDesk;
import com.windanesz.ancientspellcraft.tileentity.TileSentinel;
import com.windanesz.ancientspellcraft.tileentity.TileSkullWatch;
import com.windanesz.ancientspellcraft.tileentity.TileSphereCognizance;
import electroblob.wizardry.constants.Element;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
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
	public static final Block SENTINEL_BLOCK_IRON = placeholder();
	public static final Block SENTINEL_BLOCK_GOLD = placeholder();
	public static final Block SENTINEL_BLOCK_DIAMOND = placeholder();
	public static final Block SENTINEL_BLOCK_LARGE_IRON = placeholder();
	public static final Block SENTINEL_BLOCK_LARGE_GOLD = placeholder();
	public static final Block SENTINEL_BLOCK_LARGE_DIAMOND = placeholder();
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
	//	public static final Block ANCIENT_DIMENSION_BOUNDARY = placeholder();
	public static final Block PLACED_RUNE = placeholder();
	public static final Block RUNE_USED = placeholder();

	// "elemental" mushrooms
	public static final Block MUSHROOM_POISON = placeholder();
	public static final Block MUSHROOM_ICE = placeholder();
	public static final Block MUSHROOM_FIRE = placeholder();
	public static final Block MUSHROOM_WITHER = placeholder();
	public static final Block MUSHROOM_FORCE = placeholder();
	public static final Block MUSHROOM_HEALING = placeholder();
	public static final Block MUSHROOM_SHOCKING = placeholder();

	// special mushrooms
	public static final Block MUSHROOM_MIND = placeholder();
	public static final Block MUSHROOM_CLEANSING = placeholder();
	public static final Block MUSHROOM_EXPLOSIVE = placeholder();
	public static final Block MUSHROOM_EMPOWERING = placeholder();


	public static final Block QUICKSAND = placeholder();
	public static final Block CONJURED_MAGMA = placeholder();
	public static final Block CONJURED_DIRT = placeholder();
	public static final Block CONJURED_SNOW = placeholder();
	public static final Block ARCANE_ANVIL = placeholder();


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
		registerBlock(registry, "sentinel_block_iron", new BlockSentinel(5, false).setCreativeTab(AncientSpellcraftTabs.ANCIENTSPELLCRAFT));
		registerBlock(registry, "sentinel_block_gold", new BlockSentinel(10, false).setCreativeTab(AncientSpellcraftTabs.ANCIENTSPELLCRAFT));
		registerBlock(registry, "sentinel_block_diamond", new BlockSentinel(20, false).setCreativeTab(AncientSpellcraftTabs.ANCIENTSPELLCRAFT));
		registerBlock(registry, "sentinel_block_large_iron", new BlockSentinel(15, true).setCreativeTab(AncientSpellcraftTabs.ANCIENTSPELLCRAFT));
		registerBlock(registry, "sentinel_block_large_gold", new BlockSentinel(30, true).setCreativeTab(AncientSpellcraftTabs.ANCIENTSPELLCRAFT));
		registerBlock(registry, "sentinel_block_large_diamond", new BlockSentinel(60, true).setCreativeTab(AncientSpellcraftTabs.ANCIENTSPELLCRAFT));
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
		registerBlock(registry, "devoritium_door", new BlockDevoritiumDoor());
		registerBlock(registry, "devoritium_ore", new BlockDevoritiumOre());
		registerBlock(registry, "devoritium_bars", new BlockDevoritiumBars());
		registerBlock(registry, "crystal_mine", new BlockCrystalMine());

		registerBlock(registry, "log_crystal_tree", new BlockCrystalLog());
		//		registerBlock(registry, "log_crystal_tree2", new Block(Material.WOOD).setCreativeTab(AncientSpellcraftTabs.ANCIENTSPELLCRAFT));
		registerBlock(registry, "leaves_crystal_tree", new BlockCrystalLeaves());
		registerBlock(registry, "imbuement_altar_ruined", new BlockRuinedImbuementAltar(Material.ROCK));
		//		registerBlock(registry, "ancient_dimension_boundary", new BlockDimensionBoundary());
		registerBlock(registry, "placed_rune", new BlockPlacedRune());
		registerBlock(registry, "rune_used", new BlockUsedRune(Material.ROCK, MapColor.STONE));

		registerBlock(registry, "mushroom_fire", new BlockMushroomFire());
		registerBlock(registry, "mushroom_poison", new BlockMushroomPoison());
		registerBlock(registry, "mushroom_ice", new BlockMushroomIce());
		registerBlock(registry, "mushroom_wither", new BlockMushroomWither());
		registerBlock(registry, "mushroom_force", new BlockMushroomForce());
		registerBlock(registry, "mushroom_healing", new BlockMushroomHeal());
		registerBlock(registry, "mushroom_shocking", new BlockMushroomShock());
		registerBlock(registry, "mushroom_mind", new BlockMushroomMind());
		registerBlock(registry, "mushroom_cleansing", new BlockMushroomCleansing());
		registerBlock(registry, "mushroom_explosive", new BlockMushroomExplosive());
		registerBlock(registry, "mushroom_empowering", new BlockMushroomEmpowering());
		registerBlock(registry, "arcane_anvil", new BlockArcaneAnvil());


		registerBlock(registry, "quicksand", new BlockQuickSand());
		registerBlock(registry, "conjured_magma", new BlockConjuredMagma());
		registerBlock(registry, "conjured_dirt", new BlockConjuredMagma());
		registerBlock(registry, "conjured_snow", new BlockConjuredDirt());
	}

	/**
	 * Called from the preInit method in the main mod class to register all the tile entities.
	 */
	public static void registerTileEntities() {
		// Nope, these still don't have their own registry...
		GameRegistry.registerTileEntity(TileArtefactPensive.class, new ResourceLocation(AncientSpellcraft.MODID, "artefact_pensive"));
		GameRegistry.registerTileEntity(TileSphereCognizance.class, new ResourceLocation(AncientSpellcraft.MODID, "sphere_cognizance"));
		GameRegistry.registerTileEntity(TileSentinel.class, new ResourceLocation(AncientSpellcraft.MODID, "sentinel_block"));

		GameRegistry.registerTileEntity(TileMageLight.class, new ResourceLocation(AncientSpellcraft.MODID, "magelight"));
		GameRegistry.registerTileEntity(TileCandleLight.class, new ResourceLocation(AncientSpellcraft.MODID, "candlelight"));
		GameRegistry.registerTileEntity(TileSkullWatch.class, new ResourceLocation(AncientSpellcraft.MODID, "skull_watch"));
		GameRegistry.registerTileEntity(TileScribingDesk.class, new ResourceLocation(AncientSpellcraft.MODID, "scribing_desk"));
		GameRegistry.registerTileEntity(TileRune.class, new ResourceLocation(AncientSpellcraft.MODID, "placed_rune"));
		GameRegistry.registerTileEntity(TileEntityMagicMushroom.class, new ResourceLocation(AncientSpellcraft.MODID, "magic_mushroom_tile"));

		GameRegistry.registerTileEntity(TileEntityRevertingBlock.class, new ResourceLocation(AncientSpellcraft.MODID, "reverting_tile"));
		GameRegistry.registerTileEntity(TileArcaneAnvil.class, new ResourceLocation(AncientSpellcraft.MODID, "arcane_anvil"));
	}

}
