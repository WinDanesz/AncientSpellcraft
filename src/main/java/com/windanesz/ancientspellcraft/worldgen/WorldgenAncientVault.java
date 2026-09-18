package com.windanesz.ancientspellcraft.worldgen;

import com.google.common.collect.ImmutableMap;
import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.Settings;
import com.windanesz.ancientspellcraft.integration.antiqueatlas.ASAntiqueAtlasIntegration;
import com.windanesz.ancientspellcraft.registry.ASBlocks;
import electroblob.wizardry.constants.Element;
import electroblob.wizardry.item.ItemWizardArmour;
import electroblob.wizardry.registry.WizardryAdvancementTriggers;
import electroblob.wizardry.registry.WizardryBlocks;
import electroblob.wizardry.util.BlockUtils;
import electroblob.wizardry.util.GeometryUtils;
import electroblob.wizardry.worldgen.MossifierTemplateProcessor;
import electroblob.wizardry.worldgen.MultiTemplateProcessor;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.structure.template.ITemplateProcessor;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.apache.commons.lang3.ArrayUtils;

import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

@Mod.EventBusSubscriber
public class WorldgenAncientVault extends WorldGenSurfaceStructure {

	private final Map<BiomeDictionary.Type, IBlockState> specialWallBlocks;

	public WorldgenAncientVault() {
		// These are initialised here because it's a convenient point after the blocks are registered
		specialWallBlocks = ImmutableMap.of(
				BiomeDictionary.Type.MESA, Blocks.RED_SANDSTONE.getDefaultState(),
				BiomeDictionary.Type.MOUNTAIN, Blocks.STONEBRICK.getDefaultState(),
				BiomeDictionary.Type.NETHER, Blocks.NETHER_BRICK.getDefaultState(),
				BiomeDictionary.Type.SANDY, Blocks.SANDSTONE.getDefaultState()
		);
	}

	@Override
	public String getStructureName() {
		return "ancient_vault";
	}

	@Override
	public long getRandomSeedModifier() {
		return 32478681L; // Yep, I literally typed 8 digits at random
	}

	@Override
	public boolean canGenerate(Random random, World world, int chunkX, int chunkZ) {
		return ArrayUtils.contains(Settings.worldgenSettings.ancientVaultDimensions, world.provider.getDimension())
				&& Settings.worldgenSettings.ancientVaultRarity > 0 && random.nextInt(Settings.worldgenSettings.ancientVaultRarity) == 0;
	}

	@Override
	public ResourceLocation getStructureFile(Random random) {
		return AncientSpellcraft.settings.ancientVaultFiles[0];
	}

	@Override
	public void spawnStructure(Random random, World world, BlockPos origin, Template template, PlacementSettings settings, ResourceLocation structureFile) {
		final Element element = Element.values()[1 + random.nextInt(Element.values().length - 1)];
		final Biome biome = world.getBiome(origin);
		IBlockState biomeCover = biome.topBlock;
		final float mossiness = getBiomeMossiness(biome);

		final IBlockState wallMaterial = specialWallBlocks.keySet().stream().filter(t -> BiomeDictionary.hasType(biome, t))
				.findFirst().map(specialWallBlocks::get).orElse(Blocks.COBBLESTONE.getDefaultState());

		final BlockPlanks.EnumType woodType = BlockUtils.getBiomeWoodVariant(biome);

		final Set<BlockPos> blocksPlaced = new HashSet<>();

		ITemplateProcessor processor = new MultiTemplateProcessor(true,
				// change ground type to biome's cover block
				(w, p, i) -> i.blockState.getBlock() == Blocks.DIRT ? new Template.BlockInfo(i.pos,
						biomeCover, i.tileentityData) : i,
				// Mossifier
				new MossifierTemplateProcessor(mossiness, 0.04f, origin.getY() + 1),
				// change ground type to biome's cover block
				(w, p, i) -> i.blockState.getBlock() == Blocks.GRASS ? new Template.BlockInfo(i.pos,
						biomeCover, i.tileentityData) : i,
				// receptacle colour
				(w, p, i) -> (i.blockState.getBlock() == WizardryBlocks.receptacle ? new Template.BlockInfo(
						i.pos, i.blockState, setElement(i.tileentityData, element)) : i)
		);

		template.addBlocksToWorld(world, origin, processor, settings, 2 | 16);
		addSealedFoundation(world, origin, template, settings);

		ASAntiqueAtlasIntegration.markMysteryStructure(world, origin.getX(), origin.getZ());

		// Entity spawning
		Map<BlockPos, String> dataBlocks = template.getDataBlocks(origin, settings);
		for (Map.Entry<BlockPos, String> entry : dataBlocks.entrySet()) {
			Vec3d vec = GeometryUtils.getCentre(entry.getKey());
			WorldGenUtils.spawnEntityByType(world, entry.getValue(), ItemWizardArmour.ArmourClass.BATTLEMAGE, origin, vec, blocksPlaced,
					Element.values()[1 + random.nextInt(Element.values().length - 1)], random.nextBoolean());
		}

	}

	/** Adds a two-block-thick sealed foundation beneath the complete transformed vault footprint. */
	private static void addSealedFoundation(World world, BlockPos origin, Template template, PlacementSettings settings) {
		BlockPos size = template.getSize();
		BlockPos firstCorner = Template.transformedBlockPos(settings, BlockPos.ORIGIN);
		BlockPos secondCorner = Template.transformedBlockPos(settings, new BlockPos(size.getX() - 1, 0, 0));
		BlockPos thirdCorner = Template.transformedBlockPos(settings, new BlockPos(0, 0, size.getZ() - 1));
		BlockPos fourthCorner = Template.transformedBlockPos(settings, new BlockPos(size.getX() - 1, 0, size.getZ() - 1));

		int minX = Math.min(Math.min(firstCorner.getX(), secondCorner.getX()), Math.min(thirdCorner.getX(), fourthCorner.getX()));
		int maxX = Math.max(Math.max(firstCorner.getX(), secondCorner.getX()), Math.max(thirdCorner.getX(), fourthCorner.getX()));
		int minZ = Math.min(Math.min(firstCorner.getZ(), secondCorner.getZ()), Math.min(thirdCorner.getZ(), fourthCorner.getZ()));
		int maxZ = Math.max(Math.max(firstCorner.getZ(), secondCorner.getZ()), Math.max(thirdCorner.getZ(), fourthCorner.getZ()));

		for (BlockPos.MutableBlockPos pos : BlockPos.getAllInBoxMutable(
				origin.add(minX, -1, minZ), origin.add(maxX, 0, maxZ))) {
			world.setBlockState(pos, ASBlocks.sealed_stone.getDefaultState(), 2);
		}
	}

	NBTTagCompound setElement(NBTTagCompound compound, Element element) {
		compound.setInteger("Element", element.ordinal());
		return compound;
	}

	@SubscribeEvent
	public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if (event.player instanceof EntityPlayerMP && event.player.ticksExisted % 20 == 0) {
			WizardryAdvancementTriggers.visit_structure.trigger((EntityPlayerMP) event.player);
		}
	}

	private static float getBiomeMossiness(Biome biome) {
		if (BiomeDictionary.hasType(biome, BiomeDictionary.Type.DENSE)) {return 0.7f;}
		if (BiomeDictionary.hasType(biome, BiomeDictionary.Type.JUNGLE)) {return 0.7f;}
		if (BiomeDictionary.hasType(biome, BiomeDictionary.Type.WET)) {return 0.5f;}
		if (BiomeDictionary.hasType(biome, BiomeDictionary.Type.SWAMP)) {return 0.5f;}
		if (BiomeDictionary.hasType(biome, BiomeDictionary.Type.FOREST)) {return 0.3f;}
		if (BiomeDictionary.hasType(biome, BiomeDictionary.Type.LUSH)) {return 0.3f;}
		if (BiomeDictionary.hasType(biome, BiomeDictionary.Type.DRY)) {return 0;}
		if (BiomeDictionary.hasType(biome, BiomeDictionary.Type.COLD)) {return 0;}
		if (BiomeDictionary.hasType(biome, BiomeDictionary.Type.DEAD)) {return 0;}
		if (BiomeDictionary.hasType(biome, BiomeDictionary.Type.WASTELAND)) {return 0;}
		if (BiomeDictionary.hasType(biome, BiomeDictionary.Type.NETHER)) {return 0;}
		return 0.1f; // Everything else (plains, etc.) has a small amount of moss
	}
}
