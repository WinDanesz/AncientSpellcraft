package com.windanesz.ancientspellcraft.item;

import com.windanesz.ancientspellcraft.Settings;
import com.windanesz.ancientspellcraft.registry.ASBlocks;
import com.windanesz.ancientspellcraft.registry.ASDimensions;
import com.windanesz.ancientspellcraft.spell.PocketDimension;
import com.windanesz.ancientspellcraft.util.SpellTeleporter;
import electroblob.wizardry.Wizardry;
import electroblob.wizardry.data.WizardData;
import electroblob.wizardry.util.Location;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ItemDimensionalCube extends ItemASArtefact {

	/** NBT key stored on the ItemStack itself (one pocket per cube item). */
	public static final String CUBE_LOCATION_KEY = "DimensionalCubeLocation";

	/** Half-width of the walkable interior (interior is 5x5 = from -2 to +2 in X and Z). */
	private static final int INNER_RADIUS = 2;
	/** Walkable height of the interior (5 blocks tall). */
	private static final int INNER_HEIGHT = 5;
	/** Wall, floor and ceiling thickness in blocks. */
	private static final int WALL_THICKNESS = 3;
	/** Outer edge of walls: inner radius + wall thickness. */
	private static final int OUTER_RADIUS = INNER_RADIUS + WALL_THICKNESS;
	/** Teleport cooldown in ticks after using the cube. */
	private static final int COOLDOWN_TICKS = 20;

	public ItemDimensionalCube(EnumRarity rarity, Type type) {
		super(rarity, type);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag advanced) {
		Wizardry.proxy.addMultiLineDescription(tooltip, "item." + this.getRegistryName() + ".desc");
		if (!Settings.isArtefactEnabled(this)) {
			tooltip.add(Wizardry.proxy.translate("item." + Wizardry.MODID + ":generic.disabled", new Style().setColor(TextFormatting.RED)));
		}
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);

		if (!Settings.isArtefactEnabled(this)) {
			return new ActionResult<>(EnumActionResult.FAIL, stack);
		}

		if (!world.isRemote) {
			WizardData data = WizardData.get(player);
			if (data == null) return new ActionResult<>(EnumActionResult.FAIL, stack);

			// If the player is already inside the pocket dimension, teleport them back
			if (player.dimension == ASDimensions.POCKET_DIM_ID) {
				NBTTagCompound prevNBT = data.getVariable(PocketDimension.POCKET_DIM_PREVIOUS_LOCATION);
				if (prevNBT != null) {
					Location previousPos = Location.fromNBT(prevNBT);
					SpellTeleporter.teleportEntity(previousPos.dimension,
							previousPos.pos.getX(), previousPos.pos.getY() + 1, previousPos.pos.getZ(),
							true, player);
					player.getCooldownTracker().setCooldown(this, COOLDOWN_TICKS);
					return new ActionResult<>(EnumActionResult.SUCCESS, stack);
				}
				return new ActionResult<>(EnumActionResult.FAIL, stack);
			}

			// Ensure this cube has a pocket; create one if not
			WorldServer pocketWorld = player.getServer().getWorld(ASDimensions.POCKET_DIM_ID);
			checkOrInitPocket(stack, pocketWorld);

			// Read pocket location from the item's own NBT
			if (stack.hasTagCompound() && stack.getTagCompound().hasKey(CUBE_LOCATION_KEY)) {
				BlockPos pocketPos = NBTUtil.getPosFromTag(stack.getTagCompound().getCompoundTag(CUBE_LOCATION_KEY));
				data.setVariable(PocketDimension.POCKET_DIM_PREVIOUS_LOCATION,
						new Location(player.getPosition(), player.dimension).toNBT());
				data.sync();
				SpellTeleporter.teleportEntity(ASDimensions.POCKET_DIM_ID,
						pocketPos.getX(), pocketPos.getY(), pocketPos.getZ(),
						true, player);
				player.getCooldownTracker().setCooldown(this, COOLDOWN_TICKS);
				return new ActionResult<>(EnumActionResult.SUCCESS, stack);
			}
		}

		return new ActionResult<>(EnumActionResult.PASS, stack);
	}

	private static void checkOrInitPocket(ItemStack stack, World pocketWorld) {
		NBTTagCompound nbt = stack.getTagCompound();
		if (nbt == null) { nbt = new NBTTagCompound(); }

		if (!nbt.hasKey(CUBE_LOCATION_KEY)) {
			BlockPos pocketLocation = PocketDimension.findSuitablePocketPos(pocketWorld);
			createPocket(pocketLocation, pocketWorld);
			// Store spawn point one block above the floor origin so the player stands on it
			nbt.setTag(CUBE_LOCATION_KEY, NBTUtil.createPosTag(pocketLocation.offset(EnumFacing.UP)));
			stack.setTagCompound(nbt);
		}
	}

	/**
	 * Builds a 5x5x5 interior pocket at {@code pos} (floor origin) using
	 * {@code dimension_boundary} blocks with 3-block-thick walls, floor and ceiling.
	 */
	public static void createPocket(BlockPos pos, World pocketWorld) {
		IBlockState wallBlock = ASBlocks.DIMENSION_BOUNDARY.getDefaultState();
		IBlockState focusBlock = ASBlocks.DIMENSION_FOCUS.getDefaultState();

		// Floor: WALL_THICKNESS layers going downward (includes area under walls)
		for (int i = 0; i < WALL_THICKNESS; i++) {
			createPlatform(pos.offset(EnumFacing.DOWN, i), pocketWorld, OUTER_RADIUS, wallBlock);
		}

		// Walls: each of the WALL_THICKNESS shell layers from inner face outward
		for (int w = INNER_RADIUS + 1; w <= OUTER_RADIUS; w++) {
			createWalls(pos, pocketWorld, w, INNER_HEIGHT + 1, wallBlock);
		}

		// Ceiling: WALL_THICKNESS layers above the walkable interior
		for (int i = 1; i <= WALL_THICKNESS; i++) {
			createPlatform(pos.offset(EnumFacing.UP, INNER_HEIGHT + i), pocketWorld, OUTER_RADIUS, wallBlock);
		}

		// Dimension focus block at the centre of the floor
		pocketWorld.setBlockState(pos, focusBlock);
	}

	private static void createPlatform(BlockPos pos, World pocketWorld, int size, IBlockState block) {
		for (BlockPos currPos : BlockPos.getAllInBox(
				pos.offset(EnumFacing.SOUTH, size).offset(EnumFacing.WEST, size),
				pos.offset(EnumFacing.NORTH, size).offset(EnumFacing.EAST, size))) {
			pocketWorld.setBlockState(currPos, block);
		}
	}

	private static void createWalls(BlockPos center, World pocketWorld, int width, int height, IBlockState block) {
		// West face
		for (BlockPos currPos : BlockPos.getAllInBox(
				center.offset(EnumFacing.SOUTH, width).offset(EnumFacing.WEST, width),
				center.offset(EnumFacing.NORTH, width).offset(EnumFacing.WEST, width).offset(EnumFacing.UP, height))) {
			pocketWorld.setBlockState(currPos, block);
		}
		// East face
		for (BlockPos currPos : BlockPos.getAllInBox(
				center.offset(EnumFacing.SOUTH, width).offset(EnumFacing.EAST, width),
				center.offset(EnumFacing.NORTH, width).offset(EnumFacing.EAST, width).offset(EnumFacing.UP, height))) {
			pocketWorld.setBlockState(currPos, block);
		}
		// South face
		for (BlockPos currPos : BlockPos.getAllInBox(
				center.offset(EnumFacing.SOUTH, width).offset(EnumFacing.EAST, -width),
				center.offset(EnumFacing.SOUTH, width).offset(EnumFacing.EAST, width).offset(EnumFacing.UP, height))) {
			pocketWorld.setBlockState(currPos, block);
		}
		// North face
		for (BlockPos currPos : BlockPos.getAllInBox(
				center.offset(EnumFacing.NORTH, width).offset(EnumFacing.WEST, -width),
				center.offset(EnumFacing.NORTH, width).offset(EnumFacing.WEST, width).offset(EnumFacing.UP, height))) {
			pocketWorld.setBlockState(currPos, block);
		}
	}
}
