package com.windanesz.ancientspellcraft.util;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.network.play.server.SPacketEntityEffect;
import net.minecraft.network.play.server.SPacketPlayerAbilities;
import net.minecraft.network.play.server.SPacketRespawn;
import net.minecraft.network.play.server.SPacketSetExperience;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.util.ITeleporter;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class SpellTeleporter implements ITeleporter {

	private double x, y, z;

	public SpellTeleporter(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public void placeEntity(World world, Entity entity, float yaw) {
		entity.setPosition(x, y, z);
		entity.motionX = 0;
		entity.motionY = 0;
		entity.motionZ = 0;
	}

	public static void teleportEntity(int targetDim, double x, double y, double z, boolean causeBlindness, EntityPlayer entity) {

		if (!DimensionManager.isDimensionRegistered(targetDim) || entity == null || entity.isBeingRidden() || entity.isRiding()) {
			return;
		}

		EntityPlayerMP player = (entity instanceof EntityPlayerMP) ? (EntityPlayerMP) entity : null;

		boolean sameDim = (player.dimension == targetDim);

		if (!player.world.isRemote && causeBlindness) {
			player.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 70, 0));

			if (targetDim == -1) { // nether teleport
				//avoid falling through the void
				if (y <= 32) {
					y = 35;
				} else if (y >= 120) { // and TP-ing too high in the nether
					y = 120;
				}
			}

			if (!sameDim) {
				if (ForgeHooks.onTravelToDimension(player, targetDim)) {
					teleportPlayerToDimension(player, targetDim, new BlockPos(x, y, z), player.cameraYaw);
				} else {
					// cancelled TP
				}
			} else {
				player.connection.setPlayerLocation(
						x + 0.5d,
						y,
						z + 0.5d,
						player.cameraYaw,
						player.rotationPitch);
			}

			if (player.dimension == -1) { // nether teleport

				// avoid lava and fire
				BlockPos blockPos = (new BlockPos(player.posX, player.posY, player.posZ));
				IBlockState currState = player.world.getBlockState(blockPos);
				Material material = currState.getMaterial();
				while ((material == Material.LAVA || material == Material.FIRE) && y <= 100) {
					blockPos = blockPos.add(0, 3, 0);
					currState = player.world.getBlockState(blockPos);
					material = currState.getMaterial();
					y = y + 3;
				}

				for (BlockPos currPos : BlockPos.getAllInBox(blockPos.add(-2, -1, -2), blockPos.add(2, -1, 2))) {
					if (player.world.isAirBlock(currPos) || player.world.getBlockState(currPos).getBlock() == Blocks.LAVA || player.world.getBlockState(currPos).getBlock() == Blocks.FLOWING_LAVA) {
						player.world.setBlockState(currPos, Blocks.NETHERRACK.getDefaultState());
					}
				}

				if (player.world.isAreaLoaded(player.getPosition(), 1)) {
					player.world.setBlockToAir(player.getPosition());
					player.world.setBlockToAir(player.getPosition().up());
				}

			}

			// TP to overworld
			if (targetDim == 0) {
				BlockPos blockPos = (new BlockPos(x, y, z));
				if (!(player.world.isAirBlock(blockPos) && player.world.isAirBlock(blockPos.up()) && !player.world.isAirBlock(blockPos.down()))) {
					while (player.world.isAirBlock(blockPos.down())) {
						blockPos = blockPos.add(0, -1, 0);
						y--;
					}
				}
			}

			//		player.world.getMinecraftServer().getPlayerList().transferPlayerToDimension((EntityPlayerMP) player, targetDim, new SpellTeleporter(x + 0.5, y, z + 0.5));

			// experminental
			//				player.setPositionAndUpdate(x + 0.5, y, z + 0.5);
			//				IMessage msg = new PacketTransportation.Message();
			//				WizardryPacketHandler.net.sendToDimension(msg, player.world.provider.getDimension());
			// experminental

		}
	}

	/**
	 * This method is based on the work of Zarathul and licensed under the MIT license
	 * Author: Zarathul
	 * License: MIT License - https://github.com/Zarathul/simpleportals/blob/1.12.2/LICENSE.md
	 * https://github.com/Zarathul/simpleportals/blob/1.12.2/src/main/java/net/zarathul/simpleportals/common/Utils.java
	 *
	 * @param player      The player to teleport.
	 * @param dimension   The dimension to port to.
	 * @param destination The position to port to.
	 * @param yaw         The rotation yaw the entity should have after porting.
	 */
	private static void teleportPlayerToDimension(EntityPlayerMP player, int dimension, BlockPos destination, float yaw) {
		int startDimension = player.dimension;
		MinecraftServer server = player.getServer();
		PlayerList playerList = server.getPlayerList();
		WorldServer startWorld = server.getWorld(startDimension);
		WorldServer destinationWorld = server.getWorld(dimension);

		player.dimension = dimension;
		player.connection.sendPacket(new SPacketRespawn(
				dimension,
				destinationWorld.getDifficulty(),
				destinationWorld.getWorldInfo().getTerrainType(),
				player.interactionManager.getGameType()));

		playerList.updatePermissionLevel(player);
		startWorld.removeEntityDangerously(player);
		player.isDead = false;

		player.setLocationAndAngles(
				destination.getX() + 0.5d,
				destination.getY(),
				destination.getZ() + 0.5d,
				yaw,
				player.rotationPitch);

		destinationWorld.spawnEntity(player);
		destinationWorld.updateEntityWithOptionalForce(player, false);
		player.setWorld(destinationWorld);

		playerList.preparePlayer(player, startWorld);
		player.connection.setPlayerLocation(
				destination.getX() + 0.5d,
				destination.getY(),
				destination.getZ() + 0.5d,
				yaw,
				player.rotationPitch);

		player.interactionManager.setWorld(destinationWorld);
		player.connection.sendPacket(new SPacketPlayerAbilities(player.capabilities));
		playerList.updateTimeAndWeatherForPlayer(player, destinationWorld);
		playerList.syncPlayerInventory(player);

		// Reapply potion effects

		for (PotionEffect potionEffect : player.getActivePotionEffects()) {
			player.connection.sendPacket(new SPacketEntityEffect(player.getEntityId(), potionEffect));
		}

		// Resend player XP otherwise the XP bar won't show up until XP is either gained or lost

		player.connection.sendPacket(new SPacketSetExperience(player.experience, player.experienceTotal, player.experienceLevel));

		// Remove the ender dragon hp bar when porting out of the End, otherwise if the dragon is still alive
		// the hp bar won't go away and if you then reenter the End, you will have multiple boss hp bars.

		FMLCommonHandler.instance().firePlayerChangedDimensionEvent(player, startDimension, dimension);
	}

	public static void teleportEntity(EntityLivingBase entityLivingBase, int targetDim, double x, double y, double z) {
		MinecraftServer server = entityLivingBase.getEntityWorld().getMinecraftServer();
		if (server != null) {
			WorldServer worldServer = server.getWorld(targetDim);
			BlockPos pos = new BlockPos(x, y, z);
			if (!worldServer.isAreaLoaded(pos, 1)) {
				ForgeChunkManager.Ticket tk = ForgeChunkManager.requestTicket(AncientSpellcraft.MODID, worldServer, ForgeChunkManager.Type.ENTITY);
				ForgeChunkManager.forceChunk(tk, new ChunkPos(pos));
			}
			entityLivingBase.setPositionAndUpdate(x, y, z);
		}
	}

	@Override
	public boolean isVanilla() {
		return false;
	}

}