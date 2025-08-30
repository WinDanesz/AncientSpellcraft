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
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.NBTExtras;
import electroblob.wizardry.util.SpellModifiers;
import electroblob.wizardry.worldgen.MossifierTemplateProcessor;
import electroblob.wizardry.worldgen.MultiTemplateProcessor;
import net.minecraft.block.*;
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
import net.minecraft.util.*;
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
 * A spell that summons a pocket library structure.
 * The library can be conjured and packed, persisting its state.
 * Author: WinDanesz
 */
public class PocketLibrary extends Spell implements IClassSpell {

	// NBT Keys
	public static final String FIRST_CAST = "first_cast";
	public static final String SIZE = "size";
	public static final String LIBRARY_BLOCKS = "libraryBlocks";
	public static final String CENTER_POS = "centerPos";
	public static final String SAVED_DATA = "savedData";
	public static final String LIBRARY_IS_SUMMONED = "library_is_summoned";
	public static final String PLAYER_BLOCKS = "player_blocks";
	
	// Structure constants
	private static final int STRUCTURE_HEIGHT = 15;
	private static final int STRUCTURE_WIDTH = 7;
	private static final int STRUCTURE_LENGTH = 7;
	private static final int STRUCTURE_WEST_OFFSET = 4;
	private static final int STRUCTURE_NORTH_OFFSET = 4;
	private static final double MAX_PACK_DISTANCE = 3.0d;
	private static final int PLAYER_BLOCKS_RANGE = 3; // 3x3x3 area for player-placed blocks
	
	// Stored data
	private static final IStoredVariable<NBTTagCompound> POCKET_LIBRARY_DATA_NBT = 
		IStoredVariable.StoredVariable.ofNBT("pocketLibraryData", Persistence.ALWAYS);
	private static final ResourceLocation POCKET_LIBRARY = 
		new ResourceLocation(AncientSpellcraft.MODID, "pocket_library");
	
	public PocketLibrary() {
		super(AncientSpellcraft.MODID, "pocket_library", SpellActions.POINT_DOWN, false);
		WizardData.registerStoredVariables(POCKET_LIBRARY_DATA_NBT);
		addProperties(SIZE);
	}

	/**
	 * Checks if this is the first time the player has cast the spell.
	 * @param nbt The player's spell data
	 * @return true if this is the first cast, false otherwise
	 */
	public static boolean isFirstCast(NBTTagCompound nbt) {
		return !nbt.hasKey(FIRST_CAST) || nbt.hasKey(FIRST_CAST) && nbt.getBoolean(FIRST_CAST);
	}

	/**
	 * Gets the player's pocket library data.
	 * @param player The player to get data for
	 * @return The player's pocket library data, or a new NBTTagCompound if none exists
	 */
	public static NBTTagCompound getData(EntityPlayer player) {
		WizardData data = WizardData.get(player);
		if (data != null) {
			NBTTagCompound libraryData = data.getVariable(POCKET_LIBRARY_DATA_NBT);
			if (libraryData != null) {
				return libraryData;
			}
		}
		return new NBTTagCompound();
	}

	/**
	 * Saves the player's pocket library data.
	 * @param player The player to save data for
	 * @param nbt The data to save
	 */
	public static void saveData(EntityPlayer player, NBTTagCompound nbt) {
		WizardData data = WizardData.get(player);
		if (data != null) {
			data.setVariable(POCKET_LIBRARY_DATA_NBT, nbt);
			data.sync();
		}
	}
	
	/**
	 * Calculates how mossy the library should be based on the biome.
	 * @param biome The biome to check
	 * @return A float from 0.0 to 0.7 representing the mossiness level
	 */
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

	/**
	 * Checks if the player's library is currently summoned.
	 * @param player The player to check
	 * @return true if the library is summoned, false otherwise
	 */
	public static boolean isLibrarySummoned(EntityPlayer player) {
		NBTTagCompound nbt = getData(player);
		return nbt.hasKey(LIBRARY_IS_SUMMONED) && nbt.getBoolean(LIBRARY_IS_SUMMONED);
	}

	/**
	 * Updates the summoned status of the player's library.
	 * @param player The player whose library to update
	 * @param nbt The player's data
	 * @param isSummoned Whether the library is summoned
	 */
	public static void changeLibrarySummonedStatus(EntityPlayer player, NBTTagCompound nbt, boolean isSummoned) {
		nbt.setBoolean(LIBRARY_IS_SUMMONED, isSummoned);
		saveData(player, nbt);
	}
	
	@Override
	public boolean cast(World world, EntityPlayer caster, EnumHand hand, int ticksInUse, SpellModifiers modifiers) {
		// Check if this is the first time the player has cast the spell
		if (isFirstCast(getData(caster))) {
			return spawnStructureInitially(caster, world);
		}

		// Handle re-casting the spell
		if (isLibrarySummoned(caster)) {
			// If the library is already summoned, check if the player is close enough to pack it
			BlockPos libraryPosition = NBTUtil.getPosFromTag(getData(caster).getCompoundTag(CENTER_POS));
			double distance = Math.sqrt(caster.getPosition().distanceSq(libraryPosition));
			
			if (distance > MAX_PACK_DISTANCE) {
				ASUtils.sendMessage(caster, "spell.ancientspellcraft:pocket_library.too_far_away", true);
				return false;
			}
			
			// Pack the library
			Wizardry.proxy.playBlinkEffect(caster);
			return packStructure(caster, world);
		} else {
			// Re-summon the library
			Wizardry.proxy.playBlinkEffect(caster);
			boolean success = spawnStructure(caster, world);
			
			if (!world.isRemote && success) {
				// Center the player inside the library
				caster.setPositionAndUpdate(
					caster.getPosition().getX() + 0.5, 
					caster.posY + 1, 
					caster.getPosition().getZ() + 0.5
				);
			}
			
			return success;
		}
	}

	@Override
	public ItemWizardArmour.ArmourClass getArmourClass() { 
		return ItemWizardArmour.ArmourClass.SAGE; 
	}

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
			// Get player data
			NBTTagCompound data = getData(caster);

			// Load the structure template
			Template template = world.getSaveHandler().getStructureTemplateManager().getTemplate(
					world.getMinecraftServer(), POCKET_LIBRARY);

			// Check if the template is valid
			BlockPos size = template.getSize();
			if (size.getX() == 0 || size.getY() == 0 || size.getZ() == 0) {
				AncientSpellcraft.logger.warn("Structure template file {} is missing or empty!", POCKET_LIBRARY);
				return false;
			}

			// Calculate the origin position for the structure
			BlockPos origin = caster.getPosition()
								.offset(EnumFacing.WEST, STRUCTURE_WEST_OFFSET)
								.offset(EnumFacing.NORTH, STRUCTURE_NORTH_OFFSET);
			
			// Validate that there's enough space for the structure
			if (!validateSpaceForLibrary(world, caster, origin)) {
				return false;
			}

			// Set up placement settings
			final Set<BlockPos> blocksPlaced = new HashSet<>();
			Rotation rotation = Rotation.NONE;
			PlacementSettings settings = new PlacementSettings().setRotation(rotation);
			
			// Create the template processor
			ITemplateProcessor processor = createTemplateProcessor(world, caster, blocksPlaced);

			// Place the structure in the world
			template.addBlocksToWorld(world, origin, processor, settings, 2 | 16);

			// Save data about the placed blocks
			if (blocksPlaced != null && blocksPlaced.size() > 0) {
				// Store the blocks in NBT
				NBTTagCompound blocks = new NBTTagCompound();
				NBTExtras.storeTagSafely(blocks, LIBRARY_BLOCKS, NBTExtras.listToNBT(blocksPlaced, NBTUtil::createPosTag));
				data.setTag(LIBRARY_BLOCKS, blocks);
				
				// Store the center position
				NBTTagCompound centerPos = NBTUtil.createPosTag(caster.getPosition());
				data.setTag(CENTER_POS, centerPos);
				
				// Update flags
				data.setBoolean(FIRST_CAST, false);
				changeLibrarySummonedStatus(caster, data, true);
				saveData(caster, data);
			}

			// Center the player inside the library
			caster.setPositionAndUpdate(caster.getPosition().getX() + 0.5, caster.posY + 1, caster.getPosition().getZ() + 0.5);
		}

		// Client-side effects
		if (world.isRemote) {
			// Spawn portal particles
			spawnPortalParticles(world, caster);
			Wizardry.proxy.playBlinkEffect(caster);
		}

		return true;
	}
	
	/**
	 * Spawns portal particles around the player.
	 * 
	 * @param world The world to spawn particles in
	 * @param player The player to spawn particles around
	 */
	private void spawnPortalParticles(World world, EntityPlayer player) {
		for (int i = 0; i < 10; i++) {
			double dx = player.posX;
			double dy = player.posY + 2 * world.rand.nextFloat();
			double dz = player.posZ;
			// For portal particles, velocity is not velocity but the offset where they start, then drift to
			// the actual position given.
			world.spawnParticle(EnumParticleTypes.PORTAL, dx, dy, dz, 
				world.rand.nextDouble() - 0.5,
				world.rand.nextDouble() - 0.5, 
				world.rand.nextDouble() - 0.5);
		}
	}

	/**
	 * Packs an existing structure by storing its data and removing it from the world.
	 * 
	 * @param caster The player casting the spell
	 * @param world The world to pack the structure from
	 * @return true if the structure was successfully packed
	 */
	private boolean packStructure(EntityPlayer caster, World world) {
		if (!world.isRemote) {
			NBTTagCompound nbtData = getData(caster);
			
			// Check if there are library blocks to pack
			if (nbtData.hasKey(LIBRARY_BLOCKS)) {
				// Get the center position of the library
				BlockPos centerPos = NBTUtil.getPosFromTag(nbtData.getCompoundTag(CENTER_POS));
				
				// Get the placed blocks from NBT
				NBTTagCompound blocksCompound = nbtData.getCompoundTag(LIBRARY_BLOCKS);
				NBTTagList blocksList = blocksCompound.getTagList(LIBRARY_BLOCKS, Constants.NBT.TAG_COMPOUND);
						if (!blocksList.isEmpty()) {
					// Convert NBT to a set of positions
					Set<BlockPos> blocksPlaced = new HashSet<>(NBTExtras.NBTToList(blocksList, NBTUtil::getPosFromTag));
					
					// Find player-placed blocks within range of the center

					// Add player blocks to the collection of all blocks
					Set<BlockPos> allBlocks = new HashSet<>(blocksPlaced);
					allBlocks.add(centerPos);
					// Add a 3x2x3 bounding box around the center position, 2 blocks up (from centerPos+0,+1,+0 to centerPos+0,+2,+0), and 1 block to every side
					BlockPos min = centerPos.add(-1, 1, -1);
					BlockPos max = centerPos.add(1, 4, 1);
					for (BlockPos pos : BlockPos.getAllInBox(min, max)) {
						allBlocks.add(pos);
					}
					
					// Prepare storage for tile entity data
					List<TileEntity> tileEntityList = new ArrayList<>();
					NBTTagList tileEntityDataList = new NBTTagList();
					NBTTagList blockDataList = new NBTTagList();
					NBTTagList playerBlocksList = new NBTTagList();
							// First pass: collect tile entity data
					collectTileEntityData(world, allBlocks, tileEntityList);
					
					// Second pass: collect block state data
					collectBlockStateData(world, allBlocks, centerPos, blockDataList);
					
					// Third pass: collect player-placed block data

					// Store the data in NBT
					NBTTagCompound savedData = new NBTTagCompound();
					savedData.setTag("Blocks", blockDataList);
					savedData.setTag("PlayerBlocks", playerBlocksList);
					
					// Store tile entity data
					for (TileEntity tileEntity : tileEntityList) {
						if (isAllowedTile(tileEntity)) {
							try {
								NBTTagCompound tileNBT = tileEntity.writeToNBT(new NBTTagCompound());
								// Make position relative to center
								BlockPos relativePos = tileEntity.getPos().subtract(centerPos);
								tileNBT.setInteger("x", relativePos.getX());
								tileNBT.setInteger("y", relativePos.getY());
								tileNBT.setInteger("z", relativePos.getZ());
								tileEntityDataList.appendTag(tileNBT);
							} catch (Exception e) {
								net.minecraftforge.fml.common.FMLLog.log.error(
									"A TileEntity type {} has thrown an exception trying to write state. It will not persist. Report this to the mod author",
									tileEntity.getClass().getName(), e);
							}
						}
					}
					
					savedData.setTag("TileEntities", tileEntityDataList);
					nbtData.setTag(SAVED_DATA, savedData);
							// Update status
					changeLibrarySummonedStatus(caster, nbtData, false);
					saveData(caster, nbtData);
					
					// Remove blocks from the world
					removeLibraryBlocks(world, allBlocks);
				}
			}
		}
		return true;
	}
	
	/**
	 * Collects data from tile entities in the library.
	 * 
	 * @param world The world to collect from
	 * @param blocksPlaced The set of block positions
	 * @param tileEntityList The list to add tile entities to
	 */
	private void collectTileEntityData(World world, Set<BlockPos> blocksPlaced, List<TileEntity> tileEntityList) {
		for (BlockPos pos : blocksPlaced) {
			TileEntity tile = world.getTileEntity(pos);
			if (isAllowedTile(tile)) {
				tileEntityList.add(tile);
			}
		}
	}
	
	/**
	 * Collects block state data from the library blocks.
	 * 
	 * @param world The world to collect from
	 * @param blocksPlaced The set of block positions
	 * @param centerPos The center position of the library
	 * @param blockDataList The NBT list to add block data to
	 */
	private void collectBlockStateData(World world, Set<BlockPos> blocksPlaced, BlockPos centerPos, NBTTagList blockDataList) {
		for (BlockPos pos : blocksPlaced) {
			NBTTagCompound blockData = new NBTTagCompound();
			NBTTagCompound state = new NBTTagCompound();
			
			// Get the tile entity at this position
			TileEntity tileEntity = world.getTileEntity(pos);
			
			// Store block state or air if it's not an allowed tile
			if (tileEntity == null || isAllowedTile(tileEntity)) {
				NBTUtil.writeBlockState(state, world.getBlockState(pos));
			} else {
				NBTUtil.writeBlockState(state, Blocks.AIR.getDefaultState());
			}
			
			// Store the block state and relative position
			blockData.setTag("state", state);
			blockData.setTag("pos", NBTUtil.createPosTag(pos.subtract(centerPos)));
			blockDataList.appendTag(blockData);
		}
	}
		/**
	 * Removes all blocks of the library from the world.
	 * 
	 * @param world The world to remove blocks from
	 * @param blocksPlaced The set of block positions to remove
	 */
	private void removeLibraryBlocks(World world, Set<BlockPos> blocksPlaced) {
		if (!blocksPlaced.isEmpty()) {
			// First pass: remove tile entities
			for (BlockPos pos : blocksPlaced) {
				TileEntity tileEntity = world.getTileEntity(pos);
				if (tileEntity != null && isAllowedTile(tileEntity)) {
					world.removeTileEntity(pos);
				}
			}
			
			// Second pass: remove torches
			for (BlockPos pos : blocksPlaced) {
				if (world.getBlockState(pos).getBlock() instanceof BlockTorch) {
					world.setBlockToAir(pos);
				}
			}
			
			// Third pass: remove door bottoms
			for (BlockPos pos : blocksPlaced) {
				if (world.getBlockState(pos).getBlock() instanceof BlockDoor && 
					world.getBlockState(pos).getValue(BlockDoor.HALF) == BlockDoor.EnumDoorHalf.LOWER) {
					world.setBlockToAir(pos);
				}
			}
			
			// Final pass: remove all remaining blocks
			for (BlockPos pos : blocksPlaced) {
				TileEntity tileEntity = world.getTileEntity(pos);
				if (tileEntity == null || isAllowedTile(tileEntity)) {
					world.setBlockToAir(pos);
				}
			}
		}
	}
	
	/**
	 * Spawns an existing structrue from player data. Not used for the initial cast!
	 */
	private boolean spawnStructure(EntityPlayer caster, World world) {
		// summon the existing structure
		if (!world.isRemote) {
			// Calculate the origin position for the structure
			BlockPos origin = caster.getPosition()
								.offset(EnumFacing.WEST, STRUCTURE_WEST_OFFSET)
								.offset(EnumFacing.NORTH, STRUCTURE_NORTH_OFFSET);
			
			// Validate that there's enough space for the structure
			if (!validateSpaceForLibrary(world, caster, origin)) {
				return false;
			}

			NBTTagCompound nbtData = getData(caster);

			if (nbtData.hasKey(SAVED_DATA)) {
				final Set<BlockPos> blocksPlaced = new HashSet<>();
				BlockPos centerPos = caster.getPosition();				NBTTagCompound savedData = nbtData.getCompoundTag(SAVED_DATA);
				NBTTagList blockTagList = savedData.getTagList("Blocks", Constants.NBT.TAG_COMPOUND);
				NBTTagList playerBlockTagList = savedData.getTagList("PlayerBlocks", Constants.NBT.TAG_COMPOUND);

				// Parse blocks from NBT
				List<NBTTagCompound> blockList = new ArrayList<>();
				blockTagList.forEach(entry -> { 
					if (entry instanceof NBTTagCompound) { 
						blockList.add((NBTTagCompound) entry); 
					}
				});

				// Parse player-placed blocks from NBT
				List<NBTTagCompound> playerBlockList = new ArrayList<>();
				playerBlockTagList.forEach(entry -> { 
					if (entry instanceof NBTTagCompound) { 
						playerBlockList.add((NBTTagCompound) entry); 
					}
				});				List<NBTTagCompound> remainingBlockList = new ArrayList<>(blockList);

				// ---------- Restoring blocks - First pass ----------
				// Regular blocks (excluding those that need support)
				for (NBTTagCompound compound : blockList) {
					IBlockState oldState = NBTUtil.readBlockState(compound.getCompoundTag("state"));
					
					// Skip blocks that require support on first pass
					if (!(oldState.getBlock() instanceof BlockTorch) && !(oldState.getBlock() instanceof BlockDoor)) {
						BlockPos relativePos = NBTUtil.getPosFromTag(compound.getCompoundTag("pos"));
						BlockPos currPos = relativePos.add(centerPos);
						
						// Only place the block if it's safe to replace what's there
						if (canSafelyReplace(world, currPos)) {
							world.setBlockState(currPos, oldState);
							blocksPlaced.add(currPos);
						}
						remainingBlockList.remove(compound);
					}
				}

				// ---------- Restoring blocks - Second pass ----------
				// Place blocks that need support (torches, doors, etc.)
				for (NBTTagCompound compound : remainingBlockList) {
					IBlockState oldState = NBTUtil.readBlockState(compound.getCompoundTag("state"));
					BlockPos relativePos = NBTUtil.getPosFromTag(compound.getCompoundTag("pos"));
					BlockPos currPos = relativePos.add(centerPos);
					
					// Only place the block if it's safe to replace what's there
					if (canSafelyReplace(world, currPos)) {
						world.setBlockState(currPos, oldState);
						blocksPlaced.add(currPos);
					}
				}
				
				// ---------- Restoring player-placed blocks ----------
				// Place player-placed blocks
				for (NBTTagCompound compound : playerBlockList) {
					IBlockState oldState = NBTUtil.readBlockState(compound.getCompoundTag("state"));
					BlockPos relativePos = NBTUtil.getPosFromTag(compound.getCompoundTag("pos"));
					BlockPos currPos = relativePos.add(centerPos);
					
					// Only place the block if it's safe to replace what's there
					if (canSafelyReplace(world, currPos)) {
						world.setBlockState(currPos, oldState);
						blocksPlaced.add(currPos);
					}
				}

				// ---------- Restoring tile entities ----------
				NBTTagList tileTagList = savedData.getTagList("TileEntities", Constants.NBT.TAG_COMPOUND);
				for (NBTBase nbtBase : tileTagList) {
					if (nbtBase instanceof NBTTagCompound) {
						NBTTagCompound tileNBT = (NBTTagCompound) nbtBase;
						BlockPos relativePos = new BlockPos(
							tileNBT.getInteger("x"), 
							tileNBT.getInteger("y"), 
							tileNBT.getInteger("z")
						);
						BlockPos currPos = relativePos.add(centerPos);
						world.setTileEntity(currPos, TileEntity.create(world, tileNBT));
					}
				}

				// ---------- Saving placement data ----------
				NBTTagCompound blocks = new NBTTagCompound();
				if (blocksPlaced != null && blocksPlaced.size() > 0) {
					// Store the blocks in NBT
					NBTExtras.storeTagSafely(blocks, LIBRARY_BLOCKS, NBTExtras.listToNBT(blocksPlaced, NBTUtil::createPosTag));
					nbtData.setTag(LIBRARY_BLOCKS, blocks);
					
					// Store the center position
					NBTTagCompound centerPosNBT = NBTUtil.createPosTag(caster.getPosition());
					nbtData.setTag(CENTER_POS, centerPosNBT);
					
					// Update flags
					nbtData.setBoolean(FIRST_CAST, false);
					changeLibrarySummonedStatus(caster, nbtData, true);
					saveData(caster, nbtData);
				}
			}
		}
		return true;
	}

	/**
	 * Checks if a tile entity is allowed in the library structure.
	 * 
	 * @param tile The tile entity to check
	 * @return true if the tile entity is allowed, false otherwise
	 */
	private boolean isAllowedTile(TileEntity tile) {
		return tile != null; // Allow all non-null tile entities
	}
	
	/**
	 * Gets the radius of the structure.
	 * 
	 * @return The radius of the structure
	 */
	private int getStructureRadius() {
		return Math.max(STRUCTURE_WIDTH, STRUCTURE_LENGTH) + 2;
	}
	
	/**
	 * Checks if the block at the given position can be safely replaced by the library structure.
	 * Only allows replacing air, grass, tall grass, flowers, and other non-solid plants.
	 * 
	 * @param world The world to check in
	 * @param pos The position to check
	 * @return true if the block can be safely replaced, false otherwise
	 */
	private boolean canSafelyReplace(World world, BlockPos pos) {
		if (world.isAirBlock(pos)) {
			return true;
		}
		
		Block block = world.getBlockState(pos).getBlock();
		return block instanceof BlockGrass ||
			   block instanceof BlockTallGrass ||
			   block instanceof BlockBush;
	}
	
	/**
	 * Validates if there is enough space to spawn the library.
	 * Checks for sky access and that all blocks can be safely replaced.
	 * 
	 * @param world The world to check in
	 * @param player The player casting the spell
	 * @param origin The origin position for the structure
	 * @return true if there is enough space, false otherwise
	 */
	private boolean validateSpaceForLibrary(World world, EntityPlayer player, BlockPos origin) {
		// Calculate the bounding box of the structure
		BlockPos endCorner = origin.offset(EnumFacing.UP, STRUCTURE_HEIGHT)
								  .offset(EnumFacing.SOUTH, STRUCTURE_WIDTH)
								  .offset(EnumFacing.EAST, STRUCTURE_LENGTH);
		
		// Check all blocks in the area
		for (BlockPos currTestPos : BlockPos.getAllInBox(origin, endCorner)) {
			// Check for sky access
			if (!world.canSeeSky(currTestPos)) {
				player.sendStatusMessage(new TextComponentTranslation("spell.ancientspellcraft:pocket_library.no_room"), true);
				return false;
			}
			
			// Check if the block can be safely replaced
			if (!canSafelyReplace(world, currTestPos)) {
				player.sendStatusMessage(new TextComponentTranslation("spell.ancientspellcraft:pocket_library.cannot_replace"), true);
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Gets the template processor for the library structure.
	 * 
	 * @param world The world to spawn in
	 * @param player The player casting the spell
	 * @param blocksPlaced Set to track placed blocks
	 * @return The template processor
	 */
	private ITemplateProcessor createTemplateProcessor(World world, EntityPlayer player, Set<BlockPos> blocksPlaced) {
		// Determine the roof color based on the player's element
		EnumDyeColor color = determineRoofColor(player);
		
		// Calculate mossiness based on biome
		final Biome biome = world.getBiome(player.getPosition());
		final float mossiness = getBiomeMossiness(biome);
		
		// Create the multi-processor to handle all aspects of structure placement
		return new MultiTemplateProcessor(true,
			// Roof colour processor
			(w, p, i) -> i.blockState.getBlock() instanceof BlockStainedHardenedClay ? 
						new Template.BlockInfo(i.pos, i.blockState.withProperty(BlockStainedHardenedClay.COLOR, color), i.tileentityData) : i,
			// Mossifier processor
			new MossifierTemplateProcessor(mossiness, 0.04f, player.getPosition().getY() + 1),
			// Block recording processor (the process() method doesn't get called for structure voids)
			(w, p, i) -> {
				if (i.blockState.getBlock() != Blocks.AIR) { 
					blocksPlaced.add(p); 
				}
				return i;
			},
			// Safety check processor for existing blocks
			(w, p, i) -> {
				// Skip air blocks since they don't replace anything
				if (i.blockState.getBlock() == Blocks.AIR) return i;
				
				// Only replace if it's a replaceable block
				if (canSafelyReplace(w, p)) {
					return i;
				} else {
					// Return air if we can't replace the block
					return new Template.BlockInfo(i.pos, Blocks.AIR.getDefaultState(), i.tileentityData);
				}
			}
		);
	}
	
	/**
	 * Determines the roof color based on the player's element.
	 * 
	 * @param player The player casting the spell
	 * @return The color to use for the roof
	 */
	private EnumDyeColor determineRoofColor(EntityPlayer player) {
		// Default to a random color
		EnumDyeColor color = EnumDyeColor.values()[player.world.rand.nextInt(EnumDyeColor.values().length)];
		
		// Override with element-specific color if the player has a full set
		switch (WizardArmourUtils.getFullSetElementForClass(player, ItemWizardArmour.ArmourClass.SAGE)) {
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
		
		return color;
	}

	/**
	 * Finds blocks placed by the player within a specified range of the center.
	 * 
	 * @param world The world to check
	 * @param centerPos The center position to check from
	 * @param range The range to check around the center
	 * @param existingBlocks The set of existing library blocks to exclude
	 * @return A set of positions containing player-placed blocks
	 */
	private Set<BlockPos> findPlayerPlacedBlocks(World world, BlockPos centerPos, int range, Set<BlockPos> existingBlocks) {
		Set<BlockPos> playerBlocks = new HashSet<>();
		
		// Define the bounds for our search area (a cube with sides of length 2*range+1)
		BlockPos minPos = centerPos.add(-range, -range, -range);
		BlockPos maxPos = centerPos.add(range, range, range);
		
		// Iterate through all positions in the search area
		for (BlockPos pos : BlockPos.getAllInBox(minPos, maxPos)) {
			// Skip air blocks and blocks that are already part of the library structure
			if (world.isAirBlock(pos) || existingBlocks.contains(pos)) {
				continue;
			}
			
			// Skip blocks that shouldn't be saved (like leaves, tall grass, etc.)
			Block block = world.getBlockState(pos).getBlock();
			if (block instanceof BlockLeaves || block instanceof BlockGrass || 
				block instanceof BlockTallGrass || block instanceof BlockBush) {
				continue;
			}
			
			// Add this block to the player-placed blocks set
			playerBlocks.add(pos);
		}
		
		return playerBlocks;
	}
}
