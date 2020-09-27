package com.windanesz.ancientspellcraft.registry;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.block.BlockArtefactPensive;
import com.windanesz.ancientspellcraft.block.BlockCandleLight;
import com.windanesz.ancientspellcraft.block.BlockCrystalBallCognizance;
import com.windanesz.ancientspellcraft.block.BlockHardFrostedIce;
import com.windanesz.ancientspellcraft.block.BlockHellFire;
import com.windanesz.ancientspellcraft.block.BlockIceDoor;
import com.windanesz.ancientspellcraft.block.BlockIceWorkbench;
import com.windanesz.ancientspellcraft.block.BlockMageLight;
import com.windanesz.ancientspellcraft.block.BlockSkullWatch;
import com.windanesz.ancientspellcraft.block.BlockSnowSlab;
import com.windanesz.ancientspellcraft.tileentity.TileArtefactPensive;
import com.windanesz.ancientspellcraft.tileentity.TileCandleLight;
import com.windanesz.ancientspellcraft.tileentity.TileCrystalBallCognizance;
import com.windanesz.ancientspellcraft.tileentity.TileMageLight;
import com.windanesz.ancientspellcraft.tileentity.TileSkullWatch;
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
	public static final Block CRYSTAL_BALL_COGNIZANCE = placeholder();
	public static final Block MAGELIGHT = placeholder();
	public static final Block CANDLELIGHT = placeholder();
	public static final Block SKULL_WATCH = placeholder();

	public static void registerBlock(IForgeRegistry<Block> registry, String name, Block block) {
		block.setRegistryName(AncientSpellcraft.MODID, name);
		block.setTranslationKey(block.getRegistryName().toString());
		registry.register(block);
	}

	@SubscribeEvent
	public static void register(RegistryEvent.Register<Block> event) {

		IForgeRegistry<Block> registry = event.getRegistry();

		registerBlock(registry, "NETHER_FIRE", new BlockHellFire());
		registerBlock(registry, "SNOW_DOUBLE_SLAB", (new BlockSnowSlab.Double()).setSoundType(SoundType.SNOW));
		registerBlock(registry, "SNOW_SLAB", (new BlockSnowSlab.Half()).setSoundType(SoundType.SNOW));
		registerBlock(registry, "ICE_DOOR", (new BlockIceDoor(Material.PACKED_ICE)));
		registerBlock(registry, "ICE_CRAFTING_TABLE", (new BlockIceWorkbench()));
		registerBlock(registry, "HARD_FROSTED_ICE", new BlockHardFrostedIce());
		registerBlock(registry, "ARTEFACT_PENSIVE", new BlockArtefactPensive().setCreativeTab(AncientSpellcraftTabs.ANCIENTSPELLCRAFT));
		registerBlock(registry, "CRYSTAL_BALL_COGNIZANCE", new BlockCrystalBallCognizance().setCreativeTab(AncientSpellcraftTabs.ANCIENTSPELLCRAFT));
		registerBlock(registry, "SKULL_WATCH", new BlockSkullWatch().setCreativeTab(AncientSpellcraftTabs.ANCIENTSPELLCRAFT));
		registerBlock(registry, "MAGELIGHT", new BlockMageLight());
		registerBlock(registry, "CANDLELIGHT", new BlockCandleLight());
	}

	/**
	 * Called from the preInit method in the main mod class to register all the tile entities.
	 */
	public static void registerTileEntities() {
		// Nope, these still don't have their own registry...
		GameRegistry.registerTileEntity(TileArtefactPensive.class, new ResourceLocation(AncientSpellcraft.MODID, "artefact_pensive"));
		GameRegistry.registerTileEntity(TileCrystalBallCognizance.class, new ResourceLocation(AncientSpellcraft.MODID, "crystal_ball_cognizance"));
		GameRegistry.registerTileEntity(TileMageLight.class, new ResourceLocation(AncientSpellcraft.MODID, "magelight"));
		GameRegistry.registerTileEntity(TileCandleLight.class, new ResourceLocation(AncientSpellcraft.MODID, "candlelight"));
		GameRegistry.registerTileEntity(TileSkullWatch.class, new ResourceLocation(AncientSpellcraft.MODID, "skull_watch"));
	}

}
