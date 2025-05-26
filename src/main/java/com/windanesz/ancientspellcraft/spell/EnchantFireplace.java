package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.registry.ASBlocks;
import com.windanesz.ancientspellcraft.tileentity.TileEntityTeleportationFlame;
import com.windanesz.ancientspellcraft.util.ASUtils;
import electroblob.wizardry.data.IStoredVariable;
import electroblob.wizardry.data.Persistence;
import electroblob.wizardry.data.WizardData;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.util.BlockUtils;
import electroblob.wizardry.util.Location;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * A spell that places a teleportation flame block where the player targets.
 * <p>
 * This spell uses the {@link com.windanesz.ancientspellcraft.block.BlockTeleportationFlame} and its tile entity to create
 * magical blue flames that can teleport entities to another teleportation flame.
 * <p>
 * Right-click with the spell to place a teleportation flame.
 * Sneak and right-click a teleportation flame to link it to the last one you placed.
 */
public class EnchantFireplace extends SpellRayAS {

	public static final IStoredVariable<BlockPos> LAST_FIRE = IStoredVariable.StoredVariable.ofBlockPos("lastEnchantedFireLocation", Persistence.ALWAYS).setSynced();

	// Constants
	private static final float RAY_TRACE_RANGE = 5.0f;
	private static final int MIN_FIREPLACE_SIDES = 3;

	// Translation keys
	private static final String CANNOT_LINK_TO_ITSELF_KEY = "spell.ancientspellcraft:teleportation_flame.cannot_link_to_itself";
	private static final String LINK_SUCCESS_KEY = "spell.ancientspellcraft:teleportation_flame.link_success";
	private static final String FLAME_STORED_KEY = "spell.ancientspellcraft:teleportation_flame.stored";
	private static final String NOT_LOOKING_AT_FLAME_KEY = "spell.ancientspellcraft:teleportation_flame.not_looking_at_flame";
	private static final String NO_FIREPLACE_KEY = "spell.ancientspellcraft:teleportation_flame.no_fireplace";

	public EnchantFireplace() {
		super("enchant_fireplace", SpellActions.SUMMON, false);
		this.soundValues(1.0f, 1.2f, 0.4f);
		addProperties(DURATION);
		WizardData.registerStoredVariables(LAST_FIRE);
	}

	@Override
	public boolean cast(World world, EntityPlayer caster, EnumHand hand, int ticksInUse, SpellModifiers modifiers) {
		// Get the target block
		RayTraceResult rayTrace = performRayTrace(world, caster);
		
		// Check if we're targeting a teleportation flame
		if (rayTrace != null && rayTrace.typeOfHit == RayTraceResult.Type.BLOCK) {
			BlockPos pos = rayTrace.getBlockPos();
			if (world.getBlockState(pos).getBlock() == ASBlocks.TELEPORTATION_FLAME) {
				// We hit a teleportation flame block
				if (caster.isSneaking()) {
					// Sneaking - store location
					return handleStoreFlameLocation(world, caster, pos);
				} else {
					// Not sneaking - try to set target destination
					return handleSetFlameTarget(world, caster, pos);
				}
			}
		}
		
		// If not targeting a flame, cast normally using the ray to create a new flame
		return super.cast(world, caster, hand, ticksInUse, modifiers);
	}
	
	/**
	 * Handles storing a flame location when sneak-clicking on a flame
	 * 
	 * @param world The world
	 * @param caster The player casting the spell
	 * @param pos The position of the flame
	 * @return true if the location was successfully stored
	 */
	private boolean handleStoreFlameLocation(World world, EntityPlayer caster, BlockPos pos) {
		if (world.isRemote) return false;
		
		WizardData data = WizardData.get(caster);
		if (data == null) return false;
		
		// Always store this flame's location when sneaking
		data.setVariable(LAST_FIRE, pos);
		ASUtils.sendMessage(caster, FLAME_STORED_KEY, true);
		return true;
	}
	
	/**
	 * Handles setting a flame's target when clicking on it (non-sneaking)
	 * 
	 * @param world The world
	 * @param caster The player casting the spell
	 * @param pos The position of the flame
	 * @return true if the target was successfully set
	 */
	private boolean handleSetFlameTarget(World world, EntityPlayer caster, BlockPos pos) {
		if (world.isRemote) return false;
		
		TileEntity tileEntity = world.getTileEntity(pos);
		if (!(tileEntity instanceof TileEntityTeleportationFlame)) return false;
		
		TileEntityTeleportationFlame flame = (TileEntityTeleportationFlame) tileEntity;
		WizardData data = WizardData.get(caster);
		if (data == null) return false;
		
		BlockPos storedLocation = data.getVariable(LAST_FIRE);
		if (storedLocation == null) {
			// No stored location
			ASUtils.sendMessage(caster, "spell.ancientspellcraft:teleportation_flame.no_stored_location", true);
			return false;
		}
		
		// Check if trying to link to itself
		if (isSamePosition(pos, storedLocation) && caster.dimension == world.provider.getDimension()) {
			ASUtils.sendMessage(caster, CANNOT_LINK_TO_ITSELF_KEY, true);
			return false;
		}
		
		// Set the target location of the flame
		flame.setTargetLocation(new Location(storedLocation, world.provider.getDimension()));
		ASUtils.sendMessage(caster, LINK_SUCCESS_KEY, true);
		return true;
	}

	/**
	 * Checks if two positions are the same
	 */
	private boolean isSamePosition(BlockPos pos1, BlockPos pos2) {
		return pos1.getX() == pos2.getX() &&
				pos1.getY() == pos2.getY() &&
				pos1.getZ() == pos2.getZ();
	}

	/**
	 * Performs a ray trace to find what the caster is looking at
	 */
	private RayTraceResult performRayTrace(World world, EntityPlayer caster) {
		Vec3d look = caster.getLookVec();
		Vec3d start = new Vec3d(caster.posX, caster.posY + caster.getEyeHeight(), caster.posZ);
		Vec3d end = start.add(
				look.x * RAY_TRACE_RANGE,
				look.y * RAY_TRACE_RANGE,
				look.z * RAY_TRACE_RANGE
		);

		return world.rayTraceBlocks(start, end, false);
	}

	@Override
	protected boolean onEntityHit(World world, Entity target, Vec3d hit,
								  @Nullable EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {
		// We only want to place blocks when hitting a block, not an entity
		return false;
	}

	@Override
	protected boolean onBlockHit(World world, BlockPos pos, EnumFacing side, Vec3d hit,
								 @Nullable EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {

		// Don't do anything if we're on the client side
		if (world.isRemote) return false;

		// Position where we'll place the teleportation flame (adjacent to the hit block)
		BlockPos placePos = pos.offset(side);

		// Check if we can place a block at the position
		if (BlockUtils.canBlockBeReplaced(world, placePos)) {
			// Check if there's a valid fireplace structure at the placement position
			if (!isValidFireplaceLocation(world, placePos) && caster instanceof EntityPlayer) {
				// Let the player know a fireplace is required
				ASUtils.sendMessage(caster, NO_FIREPLACE_KEY, true);
				return false;
			}

			// Place the teleportation flame block
			if (world.setBlockState(placePos, ASBlocks.TELEPORTATION_FLAME.getDefaultState())) {
				configureFlameBlock(world, placePos, caster, getProperty(DURATION).intValue());
				return true;
			}
		}

		return false;
	}

	/**
	 * Configures the placed teleportation flame block
	 */
	private void configureFlameBlock(World world, BlockPos pos, @Nullable EntityLivingBase caster, int duration) {
		TileEntity tileEntity = world.getTileEntity(pos);
		if (tileEntity instanceof TileEntityTeleportationFlame) {
			TileEntityTeleportationFlame teleportationFlame = (TileEntityTeleportationFlame) tileEntity;
			teleportationFlame.setLifetime(duration);

			// If caster exists, save it to the tile entity
			if (caster != null) {
				teleportationFlame.setCaster(caster);
			}
		}
	}

	@Override
	protected boolean onMiss(World world, @Nullable EntityLivingBase caster, Vec3d origin, Vec3d direction, int ticksInUse, SpellModifiers modifiers) {
		return false;
	}

	/**
	 * Checks if the given position has a valid fireplace structure for the teleportation flame
	 *
	 * @param world The world
	 * @param pos   The position to check
	 * @return True if the position has a valid fireplace structure
	 */
	public static boolean isValidFireplaceLocation(World world, BlockPos pos) {
		// The blocks that are valid for the fireplace structure
		Block[] validBlocks = getValidFireplaceBlocks();

		// Check if the block below is a valid block (base of fireplace)
		BlockPos belowPos = pos.down();
		if (!isValidStructureBlock(world.getBlockState(belowPos).getBlock(), validBlocks)) {
			return false;
		}

		// Check if at least MIN_FIREPLACE_SIDES sides are made of valid blocks (U shape)
		int validSides = countValidFireplaceSides(world, pos, validBlocks);
		return validSides >= MIN_FIREPLACE_SIDES;
	}

	/**
	 * Returns the blocks that are valid for creating a fireplace
	 */
	private static Block[] getValidFireplaceBlocks() {
		return new Block[]{
				Blocks.BRICK_BLOCK,  // Regular brick block
				Blocks.STONEBRICK,   // Stone brick
				Blocks.NETHER_BRICK  // Nether brick for a demonic look
		};
	}

	/**
	 * Counts how many sides of the fireplace are made of valid blocks
	 */
	private static int countValidFireplaceSides(World world, BlockPos pos, Block[] validBlocks) {
		int validSides = 0;

		// Check in all four horizontal directions
		for (EnumFacing facing : EnumFacing.HORIZONTALS) {
			BlockPos sidePos = pos.offset(facing);
			if (isValidStructureBlock(world.getBlockState(sidePos).getBlock(), validBlocks)) {
				validSides++;
			}
		}

		return validSides;
	}

	/**
	 * Checks if a block is one of the valid blocks for a fireplace structure
	 */
	private static boolean isValidStructureBlock(Block block, Block[] validBlocks) {
		for (Block validBlock : validBlocks) {
			if (block == validBlock) {
				return true;
			}
		}
		return false;
	}
}
