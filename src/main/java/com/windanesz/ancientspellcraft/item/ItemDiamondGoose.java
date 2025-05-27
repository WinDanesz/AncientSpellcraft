package com.windanesz.ancientspellcraft.item;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.Settings;
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

	// NBT Keys
	private static final String NBT_BOUND_BLOCK = "boundBlock";
	private static final String NBT_LAST_DIRECTION_TIME = "lastDirectionTime";
	private static final String NBT_LAST_MID_RANGE_QUACK = "lastMidRangeQuackState";
	
	// Configuration constants
	private static final int CHECK_INTERVAL = 40; // Check every 2 seconds
	// Detection range is now from config
	private static final int HELD_CHECK_INTERVAL = 40; // Check more frequently when held in hand
	private static final int DIRECTION_COOLDOWN = 200; // 10 seconds cooldown
	
	// Range definitions - now relative to config
	private static final int CLOSE_RANGE_MAX = 4; // 0-4 blocks
	private static final int MID_RANGE_MIN = 5;   // 5-8 blocks
	
	private static final Random rand = new Random();
	
	/**
	 * Detection range classifications for bound blocks
	 */
	private enum DetectionRange {
		NONE,       // No block detected
		CLOSE,      // 0-4 blocks away
		MEDIUM,     // 5-8 blocks away
		FAR         // Beyond detection range
	}
	
	public ItemDiamondGoose(EnumRarity rarity, ItemArtefact.Type type) {
		super(rarity, type);
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing,
									  float hitX, float hitY, float hitZ) {
		ItemStack stack = player.getHeldItem(hand);

		if (!world.isRemote) {
			// Check if already bound to a block
			String existingBlock = getNBTString(stack);
			if (existingBlock != null && !existingBlock.isEmpty()) {
				// Already bound, can't bind to a new block
				ASUtils.sendMessage(player, getTranslationKey() + ".already_bound", false);
				return EnumActionResult.FAIL;
			}
			
			Block block = world.getBlockState(pos).getBlock();
			String blockName = block.getRegistryName().toString();

			setNBTString(stack, blockName);
			ASUtils.sendMessage(player, getTranslationKey() + ".bound", false);

			return EnumActionResult.SUCCESS;
		}

		return EnumActionResult.PASS;
	}

	@Override
	public void onWornTick(ItemStack stack, EntityLivingBase entity) {
		// Only check periodically to reduce performance impact
		if (entity.ticksExisted % (CHECK_INTERVAL * 2) != 0) {
			return;
		}

		// Only works for players and on the server
		if (!(entity instanceof EntityPlayer) || entity.world.isRemote) {
			return;
		}

		EntityPlayer player = (EntityPlayer) entity;
		String boundBlockName = getNBTString(stack);

		// If not bound to a block, do nothing
		if (boundBlockName == null || boundBlockName.isEmpty()) {
			return;
		}

		// Check for bound block and determine the detection range
		BlockDetectionResult result = findNearestBoundBlock(player, boundBlockName);
		
		// Handle quacking based on distance
		handleQuackingByDistance(stack, player, result.getDistance());
	}
	
	/**
	 * Finds the nearest bound block and returns detection information
	 */
	private BlockDetectionResult findNearestBoundBlock(EntityPlayer player, String boundBlockName) {
		BlockPos playerPos = player.getPosition();
		World world = player.world;
		double closestDistance = Double.MAX_VALUE;
		BlockPos closestPos = null;
		
		// Get detection range from config
		int detectionRange = Settings.generalSettings.diamondGooseDetectionRange;

		// Search in a cube around the player
		for (int x = -detectionRange; x <= detectionRange; x++) {
			for (int y = -detectionRange; y <= detectionRange; y++) {
				for (int z = -detectionRange; z <= detectionRange; z++) {
					BlockPos checkPos = playerPos.add(x, y, z);

					// Skip positions too far away (optimization)
					if (playerPos.distanceSq(checkPos) > detectionRange * detectionRange) {
						continue;
					}

					Block block = world.getBlockState(checkPos).getBlock();
					if (block.getRegistryName().toString().equals(boundBlockName)) {
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

		// If block found, calculate direction and distance
		if (closestPos != null) {
			EnumFacing direction = calculateRelativeDirection(player, closestPos);
			int distance = (int)Math.sqrt(closestDistance);
			return new BlockDetectionResult(true, distance, direction);
		}
		
		return new BlockDetectionResult(false, 0, null);
	}
	
	/**
	 * Determines whether to quack based on the distance to the nearest bound block
	 */
	private void handleQuackingByDistance(ItemStack stack, EntityPlayer player, int distance) {
		if (distance > 0 && distance <= CLOSE_RANGE_MAX) {
			// Close range: always quack
			quack(player);
		} else if (distance >= MID_RANGE_MIN && distance <= Settings.generalSettings.diamondGooseDetectionRange) {
			// Mid-range: quack every other time
			boolean lastQuackState = getNBTBoolean(stack, false);
			if (!lastQuackState) {
				quack(player);
			}
			// Toggle the state for next time
			setNBTBoolean(stack, NBT_LAST_MID_RANGE_QUACK, !lastQuackState);
		}
	}
	
	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
		super.onUpdate(stack, world, entity, itemSlot, isSelected);

		// Only process for players holding the item
		if (!(entity instanceof EntityPlayer) || world.isRemote) {
			return;
		}

		EntityPlayer player = (EntityPlayer) entity;

		// Check less frequently to avoid spam
		if (player.ticksExisted % HELD_CHECK_INTERVAL != 0) {
			return;
		}

		String boundBlockName = getNBTString(stack);
		if (boundBlockName == null || boundBlockName.isEmpty()) {
			return;
		}

		// Check cooldown before proceeding
		long currentTime = world.getTotalWorldTime();
		long lastTime = getNBTLong(stack, NBT_LAST_DIRECTION_TIME, 0);

		if (currentTime - lastTime < DIRECTION_COOLDOWN) {
			return; // Still on cooldown
		}

		// Find nearest bound block and indicate direction if found
		BlockDetectionResult result = findNearestBoundBlock(player, boundBlockName);
		if (result.isBlockFound()) {
			indicateDirection(player, result.getDirection());
			setNBTLong(stack, NBT_LAST_DIRECTION_TIME, currentTime);
		}
	}

	/**
	 * Container class for block detection results
	 */
	private static class BlockDetectionResult {
		private final boolean blockFound;
		private final int distance;
		private final EnumFacing direction;
		
		public BlockDetectionResult(boolean blockFound, int distance, EnumFacing direction) {
			this.blockFound = blockFound;
			this.distance = distance;
			this.direction = direction;
		}
		
		public boolean isBlockFound() { return blockFound; }
		public int getDistance() { return distance; }
		public EnumFacing getDirection() { return direction; }
	}

	/**
	 * Calculates the relative direction from player to target position
	 */
	private EnumFacing calculateRelativeDirection(EntityPlayer player, BlockPos target) {
		// Calculate the vector from player to target
		double dx = target.getX() + 0.5 - player.posX;
		double dy = target.getY() + 0.5 - player.posY - player.getEyeHeight();
		double dz = target.getZ() + 0.5 - player.posZ;

		// Get player's look vector (normalized)
		double lookX = -Math.sin(Math.toRadians(player.rotationYaw));
		double lookZ = Math.cos(Math.toRadians(player.rotationYaw));

		// Calculate dot and cross products for direction
		double dotProductHorizontal = dx * lookX + dz * lookZ;
		double crossProduct = dx * lookZ - dz * lookX;

		// Find which component is dominant
		double absDot = Math.abs(dotProductHorizontal);
		double absCross = Math.abs(crossProduct);
		double absY = Math.abs(dy);

		// Return the dominant direction
		if (absY > absDot && absY > absCross) {
			return dy > 0 ? EnumFacing.UP : EnumFacing.DOWN;
		}
		else if (absDot > absCross) {
			return dotProductHorizontal > 0 ? EnumFacing.NORTH : EnumFacing.SOUTH;
		}
		else {
			return crossProduct > 0 ? EnumFacing.WEST : EnumFacing.EAST;
		}
	}

	/**
	 * Sends a direction message to the player
	 */
	private void indicateDirection(EntityPlayer player, EnumFacing direction) {
		String translationKey = getTranslationKey() + ".";
		
		switch (direction) {
			case NORTH: translationKey += "forward"; break;
			case SOUTH: translationKey += "backward"; break;
			case EAST: translationKey += "right"; break;
			case WEST: translationKey += "left"; break;
			case UP: translationKey += "up"; break;
			case DOWN: translationKey += "down"; break;
			default: translationKey += "nearby";
		}

		ASUtils.sendMessage(player, translationKey, false);
	}

	/**
	 * Makes the goose quack with a chance to display a message
	 */
	private void quack(EntityPlayer player) {
		// 15% chance to display a quack message
		if (rand.nextFloat() < 0.15f) {
			String[] quackMessages = {
					getTranslationKey() + ".quack1",
					getTranslationKey() + ".quack2",
					getTranslationKey() + ".quack3"
			};
			String message = quackMessages[rand.nextInt(quackMessages.length)];
			ASUtils.sendMessage(player, message, false);
		}

		// Play quack sound
		player.world.playSound(null, player.posX, player.posY, player.posZ, 
				ASSounds.GOOSE, SoundCategory.PLAYERS, 1.0F, 0.9F + rand.nextFloat() * 0.2F);
	}

	private String getNBTString(ItemStack stack) {
		if (!stack.hasTagCompound() || !stack.getTagCompound().hasKey(ItemDiamondGoose.NBT_BOUND_BLOCK)) {
			return null;
		}
		return stack.getTagCompound().getString(ItemDiamondGoose.NBT_BOUND_BLOCK);
	}
	
	private void setNBTString(ItemStack stack, String value) {
		if (!stack.hasTagCompound()) {
			stack.setTagCompound(new NBTTagCompound());
		}
		stack.getTagCompound().setString(ItemDiamondGoose.NBT_BOUND_BLOCK, value);
	}
	
	private boolean getNBTBoolean(ItemStack stack, boolean defaultValue) {
		if (!stack.hasTagCompound() || !stack.getTagCompound().hasKey(ItemDiamondGoose.NBT_LAST_MID_RANGE_QUACK)) {
			return defaultValue;
		}
		return stack.getTagCompound().getBoolean(ItemDiamondGoose.NBT_LAST_MID_RANGE_QUACK);
	}
	
	private void setNBTBoolean(ItemStack stack, String key, boolean value) {
		if (!stack.hasTagCompound()) {
			stack.setTagCompound(new NBTTagCompound());
		}
		stack.getTagCompound().setBoolean(key, value);
	}
	
	private long getNBTLong(ItemStack stack, String key, long defaultValue) {
		if (!stack.hasTagCompound() || !stack.getTagCompound().hasKey(key)) {
			return defaultValue;
		}
		return stack.getTagCompound().getLong(key);
	}
	
	private void setNBTLong(ItemStack stack, String key, long value) {
		if (!stack.hasTagCompound()) {
			stack.setTagCompound(new NBTTagCompound());
		}
		stack.getTagCompound().setLong(key, value);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		super.addInformation(stack, worldIn, tooltip, flagIn);

		String boundBlock = getNBTString(stack);
		if (boundBlock != null && !boundBlock.isEmpty()) {
			Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(boundBlock));
			if (block != null) {
				String baseKey = getTranslationKey() + ".tooltip.";
				tooltip.add(TextFormatting.GRAY + AncientSpellcraft.proxy.translate(baseKey + "imprinted", block.getLocalizedName()));
				tooltip.add(TextFormatting.DARK_AQUA + AncientSpellcraft.proxy.translate(baseKey + "quack"));
				tooltip.add(TextFormatting.DARK_AQUA + AncientSpellcraft.proxy.translate(baseKey + "excited"));
			}
		} else {
			tooltip.add(TextFormatting.GRAY + AncientSpellcraft.proxy.translate(getTranslationKey() + ".tooltip.bind"));
		}
	}
}
