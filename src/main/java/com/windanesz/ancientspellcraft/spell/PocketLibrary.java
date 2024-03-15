package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.registry.ASItems;
import com.windanesz.ancientspellcraft.util.ASUtils;
import com.windanesz.ancientspellcraft.util.WizardArmourUtils;
import electroblob.wizardry.Wizardry;
import electroblob.wizardry.data.IStoredVariable;
import electroblob.wizardry.data.Persistence;
import electroblob.wizardry.data.WizardData;
import electroblob.wizardry.item.ItemWizardArmour;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.tileentity.TileEntityBookshelf;
import electroblob.wizardry.util.NBTExtras;
import electroblob.wizardry.util.SpellModifiers;
import electroblob.wizardry.worldgen.MossifierTemplateProcessor;
import electroblob.wizardry.worldgen.MultiTemplateProcessor;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockStainedHardenedClay;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.structure.template.ITemplateProcessor;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.util.Constants;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Author: WinDanesz
 */
public class PocketLibrary extends Spell implements IClassSpell {

	public static final String FIRST_CAST = "first_cast";
	public static final String SIZE = "size";
	public static final String LIBRARY_BLOCKS = "libraryBlocks";
	public static final String CENTER_POS = "centerPos";
	public static final String SAVED_DATA = "savedData";
	private static final IStoredVariable<NBTTagCompound> POCKET_LIBRARY_DATA_NBT = IStoredVariable.StoredVariable.ofNBT("pocketLibraryData", Persistence.ALWAYS);
	private static final ResourceLocation POCKET_LIBRARY = new ResourceLocation(AncientSpellcraft.MODID, "pocket_library");
	public static final String LIBRARY_IS_SUMMONED = "library_is_summoned";

	public PocketLibrary() {
		super(AncientSpellcraft.MODID, "pocket_library", SpellActions.POINT_DOWN, false);
		WizardData.registerStoredVariables(POCKET_LIBRARY_DATA_NBT);
		addProperties(SIZE);
	}

	public static boolean isFirstCast(NBTTagCompound nbt) {
		return !nbt.hasKey(FIRST_CAST) || nbt.hasKey(FIRST_CAST) && nbt.getBoolean(FIRST_CAST);
	}

	public static NBTTagCompound getData(EntityPlayer player) {
		WizardData data = WizardData.get(player);
		if (data != null) {
			NBTTagCompound experimentData = data.getVariable(POCKET_LIBRARY_DATA_NBT);

			if (experimentData != null) {
				return experimentData;
			}

		}
		return new NBTTagCompound();
	}

	public static void saveData(EntityPlayer player, NBTTagCompound nbt) {
		WizardData data = WizardData.get(player);
		if (data != null) {
			data.setVariable(POCKET_LIBRARY_DATA_NBT, nbt);
			data.sync();
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

	public static boolean isLibrarySummoned(EntityPlayer player) {
		NBTTagCompound nbt = getData(player);
		return nbt.hasKey(LIBRARY_IS_SUMMONED) && nbt.getBoolean(LIBRARY_IS_SUMMONED);
	}

	public static void changeLibrarySummonedStatus(EntityPlayer player, NBTTagCompound nbt, boolean isSummoned) {
		nbt.setBoolean(LIBRARY_IS_SUMMONED, isSummoned);
		saveData(player, nbt);
	}

	@Override
	public boolean cast(World world, EntityPlayer caster, EnumHand hand, int ticksInUse, SpellModifiers modifiers) {

		//		debug
		//		if (caster.isSneaking()) {
		//
		//			if (!world.isRemote) {
		//				WizardData data = WizardData.get(caster);
		//				data.setVariable(POCKET_LIBRARY_DATA_NBT, new NBTTagCompound());
		//				data.sync();
		//			}
		//			System.out.println("clearing all data");
		//			if (true) { return false; }
		//		}

		if (isFirstCast(getData(caster))) {
			return spawnStructureInitially(caster, world);
		}

		if (isLibrarySummoned(caster)) {
			BlockPos libraryPosition = NBTUtil.getPosFromTag(getData(caster).getCompoundTag(CENTER_POS));
			double distance = Math.sqrt(caster.getPosition().distanceSq(libraryPosition));
			if (distance > 3.0d) {
				ASUtils.sendMessage(caster, "spell.ancientspellcraft:pocket_library.too_far_away", true);
				return false;
			}
			Wizardry.proxy.playBlinkEffect(caster);
			return packStructure(caster, world);
		} else {
			Wizardry.proxy.playBlinkEffect(caster);
			boolean f = spawnStructure(caster, world);
			if (!world.isRemote) {
				caster.setPositionAndUpdate(caster.getPosition().getX() + 0.5, caster.posY + 1, caster.getPosition().getZ() + 0.5);
			}
			return f;
		}
	}

	@Override
	public ItemWizardArmour.ArmourClass getArmourClass() { return ItemWizardArmour.ArmourClass.SAGE; }

	@Override
	public boolean applicableForItem(Item item) {
		return item == ASItems.mystic_spell_book;
	}

	/**
	 * Should only ever be called once for each player. Spawns the predefined structure file from the structure .nbt, then stores its location and all block
	 * positions where a block was added.
	 */
	private boolean spawnStructureInitially(EntityPlayer caster, World world) {


		if (!world.isRemote) {
			NBTTagCompound data = getData(caster);

			Template template = world.getSaveHandler().getStructureTemplateManager().getTemplate(
					world.getMinecraftServer(), POCKET_LIBRARY);

			BlockPos size = template.getSize();

			if (size.getX() == 0 || size.getY() == 0 || size.getZ() == 0) {
				AncientSpellcraft.logger.warn("Structure template file {} is missing or empty!", POCKET_LIBRARY);
			}

			int triesLeft = 10;

			BlockPos origin;
			EnumDyeColor color = EnumDyeColor.values()[world.rand.nextInt(EnumDyeColor.values().length)];
			switch (WizardArmourUtils.getFullSetElementForClass(caster, ItemWizardArmour.ArmourClass.SAGE)) {
				case MAGIC:
					color = EnumDyeColor.GRAY;
					break;
				case FIRE:
					color = EnumDyeColor.ORANGE;
					break;
				case ICE:
					color = EnumDyeColor.LIGHT_BLUE;
					break;
				case LIGHTNING:
					color = EnumDyeColor.BLUE;
					break;
				case NECROMANCY:
					color = EnumDyeColor.PURPLE;
					break;
				case EARTH:
					color = EnumDyeColor.BROWN;
					break;
				case SORCERY:
					color = EnumDyeColor.CYAN;
					break;
				case HEALING:
					color = EnumDyeColor.YELLOW;
					break;
			}

			final EnumDyeColor colour = color;
			final Biome biome = world.getBiome(caster.getPosition());
			final float mossiness = getBiomeMossiness(biome);
			final Set<BlockPos> blocksPlaced = new HashSet<>();
			Rotation rotation = Rotation.NONE;

			PlacementSettings settings = new PlacementSettings().setRotation(rotation);

			ITemplateProcessor processor = new MultiTemplateProcessor(true,
					// Roof colour
					(w, p, i) -> i.blockState.getBlock() instanceof BlockStainedHardenedClay ? new Template.BlockInfo(
							i.pos, i.blockState.withProperty(BlockStainedHardenedClay.COLOR, colour), i.tileentityData) : i,
					// Mossifier
					new MossifierTemplateProcessor(mossiness, 0.04f, caster.getPosition().getY() + 1),
					// Block recording (the process() method doesn't get called for structure voids)
					(w, p, i) -> {
						if (i.blockState.getBlock() != Blocks.AIR) { blocksPlaced.add(p); }
						return i;
					}
			);

			BlockPos pos = caster.getPosition().offset(EnumFacing.WEST, 4).offset(EnumFacing.NORTH, 4);

			for (BlockPos currTestPos : BlockPos.getAllInBox(pos,
					pos.offset(EnumFacing.UP, 15).offset(EnumFacing.SOUTH, 7).offset(EnumFacing.EAST, 7))) {
				if (!world.canSeeSky(currTestPos)) {
					caster.sendStatusMessage(new TextComponentTranslation("spell.ancientspellcraft:ice_tower.no_room"), true);
					return false;
				}
			}


			//template.addBlocksToWorld(world, caster.getPosition().offset(caster.getHorizontalFacing(), 4), processor, settings, 2 | 16);
			template.addBlocksToWorld(world, caster.getPosition().offset(EnumFacing.WEST, 4).offset(EnumFacing.NORTH, 4), processor, settings, 2 | 16);

			NBTTagCompound blocks = new NBTTagCompound();
			if (blocksPlaced != null && blocksPlaced.size() > 0) {
				NBTExtras.storeTagSafely(blocks, LIBRARY_BLOCKS, NBTExtras.listToNBT(blocksPlaced, NBTUtil::createPosTag));
				data.setTag(LIBRARY_BLOCKS, blocks);
				NBTTagCompound centerPos = NBTUtil.createPosTag(caster.getPosition());
				data.setTag(CENTER_POS, centerPos);
				data.setBoolean(FIRST_CAST, false);
				changeLibrarySummonedStatus(caster, data, true);
				saveData(caster, data);
				//WizardData wizardData = WizardData.get(caster);
				//wizardData.setVariable(POCKET_LIBRARY_DATA_NBT, data);
			}

			caster.setPositionAndUpdate(caster.getPosition().getX() + 0.5, caster.posY + 1, caster.getPosition().getZ() + 0.5);
		}

		if (world.isRemote) {
			for (int i = 0; i < 10; i++) {
				double dx = caster.posX;
				double dy = caster.posY + 2 * world.rand.nextFloat();
				double dz = caster.posZ;
				// For portal particles, velocity is not velocity but the offset where they start, then drift to
				// the actual position given.
				world.spawnParticle(EnumParticleTypes.PORTAL, dx, dy, dz, world.rand.nextDouble() - 0.5,
						world.rand.nextDouble() - 0.5, world.rand.nextDouble() - 0.5);
			}

			Wizardry.proxy.playBlinkEffect(caster);
		}

		return true;
	}

	/**
	 * Packs an existing structure by storing its data and removing it from the world.
	 */
	private boolean packStructure(EntityPlayer caster, World world) {
		// it needs to be packed
		if (!world.isRemote) {
			NBTTagCompound data = getData(caster);

			// System.out.println("packing existing library");

			NBTTagCompound nbtData = getData(caster);
			if (nbtData.hasKey(LIBRARY_BLOCKS)) {

				// Save the blocks
				BlockPos centerPos = NBTUtil.getPosFromTag(nbtData.getCompoundTag(CENTER_POS));

				NBTTagCompound compound = nbtData.getCompoundTag(LIBRARY_BLOCKS);
				NBTTagList tagList = compound.getTagList(LIBRARY_BLOCKS, Constants.NBT.TAG_COMPOUND);

				if (!tagList.isEmpty()) {
					Set<BlockPos> blocksPlaced = new HashSet<>(NBTExtras.NBTToList(tagList, NBTUtil::getPosFromTag));

					NBTTagCompound compound1 = new NBTTagCompound();
					NBTTagList nbttaglist2 = new NBTTagList();

					List<TileEntity> tileEntityList = new ArrayList<>();
					List<NBTTagCompound> blockStateList = new ArrayList<>();
					NBTTagList blockDataList = new NBTTagList();

					blocksPlaced.forEach(b -> {
						TileEntity tile = world.getTileEntity(b);

						if (isAllowedTile(tile)) {
							tileEntityList.add(tile);
						}
					});

					blocksPlaced.forEach(b -> {
						if (world.getTileEntity(b) == null || isAllowedTile(world.getTileEntity(b))) {

							NBTTagCompound blockData = new NBTTagCompound();
							NBTTagCompound state = new NBTTagCompound();
							NBTUtil.writeBlockState(state, world.getBlockState(b));

							blockData.setTag("state", state);
							blockData.setTag("pos", NBTUtil.createPosTag(b.subtract(centerPos)));
							blockDataList.appendTag(blockData);
						} else {
							NBTTagCompound blockData = new NBTTagCompound();
							NBTTagCompound state = new NBTTagCompound();
							NBTUtil.writeBlockState(state, Blocks.AIR.getDefaultState());

							blockData.setTag("state", state);
							blockData.setTag("pos", NBTUtil.createPosTag(b.subtract(centerPos)));
							blockDataList.appendTag(blockData);
						}
					});

					compound1.setTag("Blocks", blockDataList);

					for (TileEntity tileentity : tileEntityList) {

						if (isAllowedTile(tileentity)) {
							try {
								NBTTagCompound nbttagcompound3 = tileentity.writeToNBT(new NBTTagCompound());
								nbttagcompound3.setInteger("x", tileentity.getPos().subtract(centerPos).getX());
								nbttagcompound3.setInteger("y", tileentity.getPos().subtract(centerPos).getY());
								nbttagcompound3.setInteger("z", tileentity.getPos().subtract(centerPos).getZ());
								nbttaglist2.appendTag(nbttagcompound3);
							}
							catch (Exception e) {
								net.minecraftforge.fml.common.FMLLog.log.error("A TileEntity type {} has throw an exception trying to write state. It will not persist. Report this to the mod author",
										tileentity.getClass().getName(), e);
							}
						}
					}

					compound1.setTag("TileEntities", nbttaglist2);

					nbtData.setTag(SAVED_DATA, compound1);
					changeLibrarySummonedStatus(caster, data, false);
					saveData(caster, data);

					// Clean up the blocks
					if (!blocksPlaced.isEmpty()) {

						// clear tiles first or they'll drop their content
						blocksPlaced.forEach(b -> {
							if (isAllowedTile(world.getTileEntity(b))) {
								world.removeTileEntity(b);
							}
						});

						blocksPlaced.forEach(b -> {
							if (world.getBlockState(b).getBlock() instanceof BlockTorch) {
								world.setBlockToAir(b);
							}
						});
						blocksPlaced.forEach(b -> {
							if (world.getBlockState(b).getBlock() instanceof BlockDoor && world.getBlockState(b).getValue(BlockDoor.HALF) == BlockDoor.EnumDoorHalf.LOWER) {
								world.setBlockToAir(b);
							}
						});

						blocksPlaced.forEach(b -> {
							if (world.getTileEntity(b) == null || isAllowedTile(world.getTileEntity(b))) {
								world.setBlockToAir(b);
							}
						});
					}
				}
			}
			// System.out.println("packing existing library done");
		}
		return true;
	}

	/**
	 * Spawns an existing structrue from player data. Not used for the initial cast!
	 */
	private boolean spawnStructure(EntityPlayer caster, World world) {
		// summon the existing structure

		if (!world.isRemote) {
			NBTTagCompound data = getData(caster);

			BlockPos pos = caster.getPosition().offset(EnumFacing.WEST, 4).offset(EnumFacing.NORTH, 4);

			for (BlockPos currTestPos : BlockPos.getAllInBox(pos,
					pos.offset(EnumFacing.UP, 15).offset(EnumFacing.SOUTH, 7).offset(EnumFacing.EAST, 7))) {
				if (!world.canSeeSky(currTestPos)) {
					caster.sendStatusMessage(new TextComponentTranslation("spell.ancientspellcraft:pocket_library.no_room"), true);
					return false;
				}
			}

			//System.out.println("summoning the library");

			NBTTagCompound nbtData = getData(caster);

			if (data.hasKey(SAVED_DATA)) {

				final Set<BlockPos> blocksPlaced = new HashSet<>();

				BlockPos centerPos = caster.getPosition();

				NBTTagCompound savedData = nbtData.getCompoundTag(SAVED_DATA);
				NBTTagList blockTagList = savedData.getTagList("Blocks", Constants.NBT.TAG_COMPOUND);

				List<NBTTagCompound> blockList = new ArrayList<>();
				blockTagList.forEach(entry -> { if (entry instanceof NBTTagCompound) { blockList.add((NBTTagCompound) entry); }});

				List<NBTTagCompound> remainingBlockList = new ArrayList<>(blockList);

				// ---------- restoring blocks, first pass ----------
				// regular blocks
				for (NBTTagCompound compound : blockList) {
					IBlockState oldState = NBTUtil.readBlockState(compound.getCompoundTag("state"));

					if (!(oldState.getBlock() instanceof BlockTorch) && !(oldState.getBlock() instanceof BlockDoor)) {
						BlockPos relativePos = NBTUtil.getPosFromTag(compound.getCompoundTag("pos"));
						BlockPos currPos = relativePos.add(centerPos);
						world.setBlockState(currPos, oldState);
						blocksPlaced.add(currPos);
						remainingBlockList.remove(compound);
					}
				}

				// ---------- restoring blocks, second pass ----------
				// any "soft" blocks which need something to support them
				for (NBTTagCompound compound : remainingBlockList) {
					IBlockState oldState = NBTUtil.readBlockState(compound.getCompoundTag("state"));
					BlockPos relativePos = NBTUtil.getPosFromTag(compound.getCompoundTag("pos"));
					BlockPos currPos = relativePos.add(centerPos);
					world.setBlockState(currPos, oldState);
					blocksPlaced.add(currPos);
				}

				// ---------- restoring tileentities ----------
				NBTTagList tileTagList = savedData.getTagList("TileEntities", Constants.NBT.TAG_COMPOUND);
				for (NBTBase nbtBase : tileTagList) {
					if (nbtBase instanceof NBTTagCompound) {
						NBTTagCompound tileNBT = (NBTTagCompound) nbtBase;
						BlockPos relativePos = new BlockPos(tileNBT.getInteger("x"), tileNBT.getInteger("y"), tileNBT.getInteger("z"));
						BlockPos currPos = relativePos.add(centerPos);
						world.setTileEntity(currPos, TileEntity.create(world, tileNBT));
					}
				}

				// ---------- saving data ----------
				NBTTagCompound blocks = new NBTTagCompound();
				if (blocksPlaced != null && blocksPlaced.size() > 0) {
					NBTExtras.storeTagSafely(blocks, LIBRARY_BLOCKS, NBTExtras.listToNBT(blocksPlaced, NBTUtil::createPosTag));
					nbtData.setTag(LIBRARY_BLOCKS, blocks);
					NBTTagCompound centerPosNBT = NBTUtil.createPosTag(caster.getPosition());
					nbtData.setTag(CENTER_POS, centerPosNBT);
					nbtData.setBoolean(FIRST_CAST, false);
					changeLibrarySummonedStatus(caster, nbtData, true);
					saveData(caster, nbtData);
				}
			}

			//System.out.println("summoning the library done");
		}
		return true;
	}

	public boolean isAllowedTile(TileEntity tile) {
		return tile instanceof TileEntityBookshelf;
	}

	public int getStructureRadius() {
		return 13;
	}
}
