package com.windanesz.ancientspellcraft.item;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.registry.ASSounds;
import com.windanesz.ancientspellcraft.util.ASUtils;
import electroblob.wizardry.item.ItemArtefact;
import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class ItemDiamondGoose extends ItemASArtefact implements ITickableArtefact {

	private static final String BOUND_BLOCK_KEY = "boundBlock";
	private static final int CHECK_INTERVAL = 40; // Check every 2 seconds
	private static final int DETECTION_RANGE = 8; // Range to detect blocks
	private static final Random rand = new Random();
	private static final int HELD_CHECK_INTERVAL = 20; // Check more frequently when held in hand
	private static final int DIRECTION_COOLDOWN = 200; // 10 seconds (200 ticks) cooldown for direction messages
	private static final String LAST_DIRECTION_TIME_KEY = "lastDirectionTime"; // NBT key for cooldown tracking

	public ItemDiamondGoose(EnumRarity rarity, ItemArtefact.Type type) {
		super(rarity, type);
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing,
									  float hitX, float hitY, float hitZ) {
		ItemStack stack = player.getHeldItem(hand);

		if (!world.isRemote) {
			Block block = world.getBlockState(pos).getBlock();
			String blockName = block.getRegistryName().toString();

			bindBlock(stack, blockName);

			ASUtils.sendMessage(player, "item." + AncientSpellcraft.MODID + ":charm_diamond_goose.bound", false);

			return EnumActionResult.SUCCESS;
		}

		return EnumActionResult.PASS;
	}

	@Override
	public void onWornTick(ItemStack stack, EntityLivingBase entity) {
		// Only check periodically to avoid performance impact
		if (entity.ticksExisted % CHECK_INTERVAL != 0) {
			return;
		}

		// Only works for players and on the server
		if (!(entity instanceof EntityPlayer) || entity.world.isRemote) {
			return;
		}

		EntityPlayer player = (EntityPlayer) entity;
		String boundBlockName = getBoundBlock(stack);

		// If not bound to a block, do nothing
		if (boundBlockName == null || boundBlockName.isEmpty()) {
			return;
		}

		// Check if the bound block is nearby
		if (checkForBoundBlockNearby(player, boundBlockName, null)) {
			// Found a matching block! Quack!
			quack(player);
		}
	}

	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
		super.onUpdate(stack, world, entity, itemSlot, isSelected);

		// Only process for players holding the item
		if (!(entity instanceof EntityPlayer) || world.isRemote || !isSelected) {
			return;
		}

		EntityPlayer player = (EntityPlayer) entity;

		// Check less frequently than worn tick to avoid spam
		if (player.ticksExisted % HELD_CHECK_INTERVAL != 0) {
			return;
		}

		String boundBlockName = getBoundBlock(stack);
		if (boundBlockName == null || boundBlockName.isEmpty()) {
			return;
		}

		// Check cooldown before proceeding
		long currentTime = world.getTotalWorldTime();
		long lastTime = getLastDirectionTime(stack);

		if (currentTime - lastTime < DIRECTION_COOLDOWN) {
			return; // Still on cooldown
		}

		// Temp variable to store found direction
		EnumFacing[] foundDirection = new EnumFacing[1];

		if (checkForBoundBlockNearby(player, boundBlockName, foundDirection)) {
			indicateDirection(player, foundDirection[0]);
			updateLastDirectionTime(stack, currentTime);
		}
	}

	/**
	 * Checks if the bound block is nearby and returns true if found
	 * Optionally fills the outDirection parameter with the direction to the closest found block
	 */
	private boolean checkForBoundBlockNearby(EntityPlayer player, String boundBlockName, @Nullable EnumFacing[] outDirection) {
		BlockPos playerPos = player.getPosition();
		World world = player.world;
		double closestDistance = Double.MAX_VALUE;
		BlockPos closestPos = null;

		for (int x = -DETECTION_RANGE; x <= DETECTION_RANGE; x++) {
			for (int y = -DETECTION_RANGE; y <= DETECTION_RANGE; y++) {
				for (int z = -DETECTION_RANGE; z <= DETECTION_RANGE; z++) {
					BlockPos checkPos = playerPos.add(x, y, z);

					// Skip positions too far away
					if (playerPos.distanceSq(checkPos) > DETECTION_RANGE * DETECTION_RANGE) {
						continue;
					}

					Block block = world.getBlockState(checkPos).getBlock();
					if (block.getRegistryName().toString().equals(boundBlockName)) {
						// If we don't need direction, just return true immediately
						if (outDirection == null) {
							return true;
						}

						// Track the closest matching block
						double distance = playerPos.distanceSq(checkPos);
						if (distance < closestDistance) {
							closestDistance = distance;
							closestPos = checkPos;
						}
					}
				}
			}
		}

		// If we found a block and need direction
		if (closestPos != null && outDirection != null) {
			outDirection[0] = getRelativeDirection(player, playerPos, closestPos);
			return true;
		}

		return closestPos != null;
	}

	/**
	 * Gets the approximate direction from source to target relative to the player's facing
	 */
	private EnumFacing getRelativeDirection(EntityPlayer player, BlockPos source, BlockPos target) {
		// Calculate the vector from source to target
		double dx = target.getX() + 0.5 - player.posX;
		double dy = target.getY() + 0.5 - player.posY - player.getEyeHeight();
		double dz = target.getZ() + 0.5 - player.posZ;

		// Get player's look vector (normalized)
		double lookX = -Math.sin(Math.toRadians(player.rotationYaw));
		double lookZ = Math.cos(Math.toRadians(player.rotationYaw));

		// Calculate the dot product to determine forward/backward
		double dotProductHorizontal = dx * lookX + dz * lookZ;

		// Calculate cross product for left/right
		double crossProduct = dx * lookZ - dz * lookX;

		// Find which component is dominant
		double absDot = Math.abs(dotProductHorizontal);
		double absCross = Math.abs(crossProduct);
		double absY = Math.abs(dy);

		if (absY > absDot && absY > absCross) {
			// Vertical direction is dominant
			return dy > 0 ? EnumFacing.UP : EnumFacing.DOWN;
		}
		else if (absDot > absCross) {
			// Forward/backward is dominant
			return dotProductHorizontal > 0 ? EnumFacing.NORTH : EnumFacing.SOUTH;
		}
		else {
			// Left/right is dominant - Fix the direction here
			return crossProduct > 0 ? EnumFacing.WEST : EnumFacing.EAST; // WEST for left, EAST for right
		}
	}

	/**
	 * Gets the last time a direction was indicated for this item
	 */
	private long getLastDirectionTime(ItemStack stack) {
		if (!stack.hasTagCompound() || !stack.getTagCompound().hasKey(LAST_DIRECTION_TIME_KEY)) {
			return 0;
		}
		return stack.getTagCompound().getLong(LAST_DIRECTION_TIME_KEY);
	}

	/**
	 * Updates the last direction time for this item
	 */
	private void updateLastDirectionTime(ItemStack stack, long time) {
		if (!stack.hasTagCompound()) {
			stack.setTagCompound(new NBTTagCompound());
		}
		stack.getTagCompound().setLong(LAST_DIRECTION_TIME_KEY, time);
	}

	/**
	 * Sends a direction message to the player
	 */
	private void indicateDirection(EntityPlayer player, EnumFacing direction) {
		String message;

		switch (direction) {
			case NORTH: // Using NORTH for forward
				message = "item." + AncientSpellcraft.MODID + ":charm_diamond_goose.forward";
				break;
			case SOUTH: // Using SOUTH for backward
				message = "item." + AncientSpellcraft.MODID + ":charm_diamond_goose.backward";
				break;
			case EAST: // Using EAST for right
				message = "item." + AncientSpellcraft.MODID + ":charm_diamond_goose.right";
				break;
			case WEST: // Using WEST for left
				message = "item." + AncientSpellcraft.MODID + ":charm_diamond_goose.left";
				break;
			case UP:
				message = "item." + AncientSpellcraft.MODID + ":charm_diamond_goose.up";
				break;
			case DOWN:
				message = "item." + AncientSpellcraft.MODID + ":charm_diamond_goose.down";
				break;
			default:
				message = "item." + AncientSpellcraft.MODID + ":charm_diamond_goose.nearby";
		}

		ASUtils.sendMessage(player, message, false);
	}

	private void quack(EntityPlayer player) {
		// Choose a random quack message
		String[] quackMessages = {
				"item." + AncientSpellcraft.MODID + ":charm_diamond_goose.quack1",
				"item." + AncientSpellcraft.MODID + ":charm_diamond_goose.quack2",
				"item." + AncientSpellcraft.MODID + ":charm_diamond_goose.quack3"
		};

		String message = quackMessages[rand.nextInt(quackMessages.length)];
		ASUtils.sendMessage(player, message, false);

		// You could also add a sound effect here if you wanted
		player.world.playSound(null, player.posX, player.posY, player.posZ, ASSounds.GOOSE,  SoundCategory.PLAYERS,1.0F,0.9F + rand.nextFloat() * 0.2F);
	}

	private void bindBlock(ItemStack stack, String blockName) {
		if (!stack.hasTagCompound()) {
			stack.setTagCompound(new NBTTagCompound());
		}

		stack.getTagCompound().setString(BOUND_BLOCK_KEY, blockName);
	}

	@Nullable
	private String getBoundBlock(ItemStack stack) {
		if (!stack.hasTagCompound() || !stack.getTagCompound().hasKey(BOUND_BLOCK_KEY)) {
			return null;
		}

		return stack.getTagCompound().getString(BOUND_BLOCK_KEY);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		super.addInformation(stack, worldIn, tooltip, flagIn);

		String boundBlock = getBoundBlock(stack);
		if (boundBlock != null && !boundBlock.isEmpty()) {
			Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(boundBlock));
			if (block != null) {
				tooltip.add(TextFormatting.GRAY + AncientSpellcraft.proxy.translate("item." + AncientSpellcraft.MODID + ":charm_diamond_goose.tooltip.imprinted", block.getLocalizedName()));
				tooltip.add(TextFormatting.DARK_AQUA + AncientSpellcraft.proxy.translate("item." + AncientSpellcraft.MODID + ":charm_diamond_goose.tooltip.quack"));
				tooltip.add(TextFormatting.DARK_AQUA + AncientSpellcraft.proxy.translate("item." + AncientSpellcraft.MODID + ":charm_diamond_goose.tooltip.excited"));
			}
		} else {
			tooltip.add(TextFormatting.GRAY + AncientSpellcraft.proxy.translate("item." + AncientSpellcraft.MODID + ":charm_diamond_goose.tooltip.bind"));
		}
	}
}
