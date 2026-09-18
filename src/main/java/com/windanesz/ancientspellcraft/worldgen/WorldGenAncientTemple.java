package com.windanesz.ancientspellcraft.worldgen;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.Settings;
import com.windanesz.ancientspellcraft.integration.antiqueatlas.ASAntiqueAtlasIntegration;
import com.windanesz.ancientspellcraft.registry.ASBlocks;
import com.windanesz.ancientspellcraft.tileentity.TileSageLectern;
import electroblob.wizardry.constants.Element;
import electroblob.wizardry.item.ItemWizardArmour;
import electroblob.wizardry.registry.WizardryAdvancementTriggers;
import electroblob.wizardry.registry.WizardryBlocks;
import electroblob.wizardry.tileentity.TileEntityBookshelf;
import electroblob.wizardry.util.BlockUtils;
import electroblob.wizardry.util.GeometryUtils;
import electroblob.wizardry.worldgen.MossifierTemplateProcessor;
import electroblob.wizardry.worldgen.MultiTemplateProcessor;
import electroblob.wizardry.worldgen.WoodTypeTemplateProcessor;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockStoneBrick;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
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
public class WorldGenAncientTemple extends WorldGenSurfaceStructureAS {

	public WorldGenAncientTemple() {}

	@Override
	public String getStructureName() {
		return "ancient_temple";
	}

	@Override
	public long getRandomSeedModifier() {
		return 12342215L; // Yep, I literally typed 8 digits at random
	}

	@Override
	public boolean canGenerate(Random random, World world, int chunkX, int chunkZ) {
		return ArrayUtils.contains(Settings.worldgenSettings.battlemageKeepDimensions, world.provider.getDimension())
				&& Settings.worldgenSettings.ancientVaultRarity > 0 && random.nextInt(Settings.worldgenSettings.ancientVaultRarity) == 0;
	}

	@Override
	public ResourceLocation getStructureFile(Random random) {
		return AncientSpellcraft.settings.ancientTempleFiles[0];
	}

	@Override
	public void spawnStructure(Random random, World world, BlockPos origin, Template template, PlacementSettings settings, ResourceLocation structureFile) {
		final Element element = Element.values()[1 + random.nextInt(Element.values().length-1)];

		final EnumDyeColor colour = EnumDyeColor.values()[random.nextInt(EnumDyeColor.values().length)];
		final Biome biome = world.getBiome(origin);
		IBlockState biomeCover = biome.topBlock;
		final float mossiness = getBiomeMossiness(biome);

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
				// change ground type to biome's cover block
				(w, p, i) -> i.blockState.getBlock() == Blocks.DIRT ? new Template.BlockInfo(i.pos,
						biomeCover, i.tileentityData) : i,
				// change ground type to biome's cover block
				(w, p, i) -> i.blockState.getBlock() == Blocks.GRASS ? new Template.BlockInfo(i.pos,
						biomeCover, i.tileentityData) : i,
				// Wood type
				new WoodTypeTemplateProcessor(woodType),
				// Mossifier
				new MossifierTemplateProcessor(mossiness, 0.04f, origin.getY() + 1),
				// Stone brick smasher-upper
				(w, p, i) -> i.blockState.getBlock() == Blocks.STONEBRICK && w.rand.nextFloat() < 0.1f ?
						new Template.BlockInfo(i.pos, Blocks.STONEBRICK.getDefaultState().withProperty(
								BlockStoneBrick.VARIANT, BlockStoneBrick.EnumType.CRACKED), i.tileentityData) : i,
				// Bookshelf marker
				(w, p, i) -> {
					TileEntityBookshelf.markAsNatural(i.tileentityData);
					return i;
				},
				(w, p, i) -> {
					TileSageLectern.markAsNatural(i.tileentityData);
					return i;
				},
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
			WorldGenUtils.spawnEntityByType(world, entry.getValue(), ItemWizardArmour.ArmourClass.BATTLEMAGE, origin, vec, blocksPlaced, Element.MAGIC, false);
		}
	}

	/**
	 * Adds a second sealed layer beneath the temple. The template floor itself is deliberately left untouched so
	 * its decoration remains intact, while the two layers below prevent players from clipping into sealed rooms.
	 */
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

	@SubscribeEvent
	public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if (event.player instanceof EntityPlayerMP && event.player.ticksExisted % 20 == 0) {
			WizardryAdvancementTriggers.visit_structure.trigger((EntityPlayerMP) event.player);
		}
	}

	private static float getBiomeMossiness(Biome biome) {
		if (BiomeDictionary.hasType(biome, BiomeDictionary.Type.DENSE)) { return 0.7f; }
		if (BiomeDictionary.hasType(biome, BiomeDictionary.Type.JUNGLE)) { return 0.7f; }
		if (BiomeDictionary.hasType(biome, BiomeDictionary.Type.WET)) { return 0.5f; }
		if (BiomeDictionary.hasType(biome, BiomeDictionary.Type.SWAMP)) { return 0.5f; }
		if (BiomeDictionary.hasType(biome, BiomeDictionary.Type.FOREST)) { return 0.3f; }
		if (BiomeDictionary.hasType(biome, BiomeDictionary.Type.LUSH)) { return 0.3f; }
		if (BiomeDictionary.hasType(biome, BiomeDictionary.Type.DRY)) { return 0; }
		if (BiomeDictionary.hasType(biome, BiomeDictionary.Type.COLD)) { return 0; }
		if (BiomeDictionary.hasType(biome, BiomeDictionary.Type.DEAD)) { return 0; }
		if (BiomeDictionary.hasType(biome, BiomeDictionary.Type.WASTELAND)) { return 0; }
		if (BiomeDictionary.hasType(biome, BiomeDictionary.Type.NETHER)) { return 0; }
		return 0.1f; // Everything else (plains, etc.) has a small amount of moss
	}

	NBTTagCompound setElement(NBTTagCompound compound, Element element) {
		compound.setInteger("Element", element.ordinal());
		return compound;
	}


}
