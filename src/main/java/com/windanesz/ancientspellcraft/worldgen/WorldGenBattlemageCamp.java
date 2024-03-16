package com.windanesz.ancientspellcraft.worldgen;

import com.google.common.collect.ImmutableMap;
import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.Settings;
import com.windanesz.ancientspellcraft.entity.living.EntityEvilClassWizard;
import com.windanesz.ancientspellcraft.integration.antiqueatlas.ASAntiqueAtlasIntegration;
import electroblob.wizardry.Wizardry;
import electroblob.wizardry.entity.living.EntityWizard;
import electroblob.wizardry.registry.WizardryAdvancementTriggers;
import electroblob.wizardry.util.BlockUtils;
import electroblob.wizardry.util.GeometryUtils;
import electroblob.wizardry.worldgen.MultiTemplateProcessor;
import electroblob.wizardry.worldgen.WoodTypeTemplateProcessor;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
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
public class WorldGenBattlemageCamp extends WorldGenSurfaceStructureAS {

	// TODO: Add wizard towers to the /locate command
	// This requires some careful manipulation of Random objects to replicate the positions exactly for the current
	// world. See the end of ChunkGeneratorOverworld for the relevant methods.

	private static final String WIZARD_DATA_BLOCK_TAG = "wizard";
	private static final String EVIL_WIZARD_DATA_BLOCK_TAG = "evil_wizard";

	private final Map<BiomeDictionary.Type, IBlockState> specialWallBlocks;

	public WorldGenBattlemageCamp() {
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
		return "battlemage_camp";
	}

	@Override
	public long getRandomSeedModifier() {
		return 98416541L; // Yep, I literally typed 8 digits at random
	}

	@Override
	public boolean canGenerate(Random random, World world, int chunkX, int chunkZ) {
		return ArrayUtils.contains(Settings.worldgenSettings.battlemageCampDimensions, world.provider.getDimension())
				&& Settings.worldgenSettings.battlemageCampRarity > 0 && random.nextInt(Settings.worldgenSettings.battlemageCampRarity) == 0;
	}

	@Override
	public ResourceLocation getStructureFile(Random random) {
		return AncientSpellcraft.settings.battlemageCampWithChestFiles[0];
		//		return new ResourceLocation(AncientSpellcraft.MODID, "battlemage_camp_chest_0");
	}

	@Override
	public void spawnStructure(Random random, World world, BlockPos origin, Template template, PlacementSettings settings, ResourceLocation structureFile) {

		final EnumDyeColor colour = EnumDyeColor.values()[random.nextInt(EnumDyeColor.values().length)];
		final Biome biome = world.getBiome(origin);
		origin = origin.up();
		final IBlockState wallMaterial = specialWallBlocks.keySet().stream().filter(t -> BiomeDictionary.hasType(biome, t))
				.findFirst().map(specialWallBlocks::get).orElse(Blocks.COBBLESTONE.getDefaultState());

		final BlockPlanks.EnumType woodType = BlockUtils.getBiomeWoodVariant(biome);

		final Set<BlockPos> blocksPlaced = new HashSet<>();

		ITemplateProcessor processor = new MultiTemplateProcessor(true,
				// wool colour
				(w, p, i) -> i.blockState.getBlock() == Blocks.WOOL ? new Template.BlockInfo(
						i.pos, Blocks.WOOL.getStateFromMeta(colour.getMetadata()), i.tileentityData) : i,
				// carpet colour
				(w, p, i) -> i.blockState.getBlock() == Blocks.CARPET ? new Template.BlockInfo(
						i.pos, Blocks.CARPET.getStateFromMeta(colour.getMetadata()), i.tileentityData) : i,
				// banner colour
				(w, p, i) -> i.blockState.getBlock() == Blocks.WALL_BANNER ? new Template.BlockInfo(
						i.pos, Blocks.CARPET.getStateFromMeta(colour.getMetadata()), i.tileentityData) : i,
				// Wall material
				(w, p, i) -> i.blockState.getBlock() == Blocks.COBBLESTONE ? new Template.BlockInfo(i.pos,
						wallMaterial, i.tileentityData) : i,

				// Wood type
				new WoodTypeTemplateProcessor(woodType)
				//				// Mossifier
				//				new MossifierTemplateProcessor(mossiness, 0.04f, origin.getY() + 1),
				//				// Block recording (the process() method doesn't get called for structure voids)
				//				(w, p, i) -> {if(i.blockState.getBlock() != Blocks.AIR) blocksPlaced.add(p); return i;}
		);

		template.addBlocksToWorld(world, origin, processor, settings, 2 | 16);

		ASAntiqueAtlasIntegration.markBattlemageCamp(world, origin.getX(), origin.getZ());

		// Wizard spawning
		Map<BlockPos, String> dataBlocks = template.getDataBlocks(origin, settings);

		for (Map.Entry<BlockPos, String> entry : dataBlocks.entrySet()) {

			Vec3d vec = GeometryUtils.getCentre(entry.getKey());

			if (entry.getValue().equals(WIZARD_DATA_BLOCK_TAG)) {

				EntityWizard wizard = new EntityWizard(world);
				wizard.setLocationAndAngles(vec.x, vec.y, vec.z, 0, 0);
				wizard.onInitialSpawn(world.getDifficultyForLocation(origin), null);
				wizard.setTowerBlocks(blocksPlaced);
				world.spawnEntity(wizard);

			} else if (entry.getValue().equals(EVIL_WIZARD_DATA_BLOCK_TAG)) {

				EntityEvilClassWizard wizard = new EntityEvilClassWizard(world);
				wizard.setLocationAndAngles(vec.x, vec.y, vec.z, 0, 0);
				wizard.hasStructure = true; // Stops it despawning
				wizard.onInitialSpawn(world.getDifficultyForLocation(origin), null);
				world.spawnEntity(wizard);
			} else {
				// This probably shouldn't happen...
				Wizardry.logger.info("Unrecognised data block value {} in structure {}", entry.getValue(), structureFile);
			}


		}
	}

	@SubscribeEvent
	public static void onPlayerTick(TickEvent.PlayerTickEvent event){
		if(event.player instanceof EntityPlayerMP && event.player.ticksExisted % 20 == 0){
			WizardryAdvancementTriggers.visit_structure.trigger((EntityPlayerMP)event.player);
		}
	}
}
