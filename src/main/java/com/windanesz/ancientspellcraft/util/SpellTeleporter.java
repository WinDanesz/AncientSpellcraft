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
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.util.ITeleporter;

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

	private static void teleportEntity(int targetDim, double x, double y, double z, EntityPlayer player) {
		teleportEntity(targetDim, x, y, z, false, player);
	}

	public static void teleportEntity(int targetDim, double x, double y, double z, boolean causeBlindness, EntityPlayer player) {
		MinecraftServer server = player.getEntityWorld().getMinecraftServer();

		if (server != null) {
			WorldServer worldServer = server.getWorld(targetDim);

			if (worldServer.getMinecraftServer() != null) {

				if (targetDim == -1) { // nether teleport
					//avoid falling through the void
					if (y <= 32) {
						y = 35;
					} else if (y >= 120) { // and TP-ing too high in the nether
						y = 120;
					}

					// avoid lava and fire
					BlockPos blockPos = (new BlockPos(x, y, z));
					IBlockState currState = worldServer.getBlockState(blockPos);
					Material material = currState.getMaterial();
					while ((material == Material.LAVA || material == Material.FIRE) && y <= 100) {
						blockPos = blockPos.add(0, 3, 0);
						currState = worldServer.getBlockState(blockPos);
						material = currState.getMaterial();
						y = y + 3;
					}

					worldServer.setBlockToAir(blockPos);
					worldServer.setBlockToAir(blockPos.up());
					for (BlockPos currPos : BlockPos.getAllInBox(blockPos.add(-2, -1, -2), blockPos.add(2, -1, 2))) {
						if (!worldServer.isSideSolid(currPos, EnumFacing.UP)) {
							worldServer.setBlockState(currPos, Blocks.NETHERRACK.getDefaultState());
						}
					}
				}

				// TP to overworld
				if (targetDim == 0) {
					BlockPos blockPos = (new BlockPos(x, y, z));
					if (!(worldServer.isAirBlock(blockPos) && worldServer.isAirBlock(blockPos.up()) && !worldServer.isAirBlock(blockPos.down()))) {
						while (worldServer.isAirBlock(blockPos.down())) {
							blockPos = blockPos.add(0, -1, 0);
							y--;
						}
					}
				}
				worldServer.getMinecraftServer().getPlayerList().transferPlayerToDimension((EntityPlayerMP) player, targetDim, new SpellTeleporter(x + 0.5, y, z + 0.5));

				// experminental
//				player.setPositionAndUpdate(x + 0.5, y, z + 0.5);
//				IMessage msg = new PacketTransportation.Message();
//				WizardryPacketHandler.net.sendToDimension(msg, player.world.provider.getDimension());
				// experminental

				if (!player.world.isRemote && causeBlindness) {
					player.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 70, 0));
				}
			}
		}
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