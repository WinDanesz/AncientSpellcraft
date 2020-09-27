package com.windanesz.ancientspellcraft.tileentity;

import com.windanesz.ancientspellcraft.registry.AncientSpellcraftBlocks;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftItems;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftPotions;
import electroblob.wizardry.item.ItemArtefact;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

@Mod.EventBusSubscriber
public class TileMageLight extends TileEntity implements ITickable {
	public TileMageLight() {}

	/**
	 * This controls whether the tile entity gets replaced whenever the block state
	 * is changed. Normally only want this when block actually is replaced.
	 */
	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
		return (oldState.getBlock() != newSate.getBlock());
	}

	@SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = true)
	public static void onPlayerTickEvent(TickEvent.PlayerTickEvent event) {
		if (event.phase == TickEvent.Phase.START && !event.player.world.isRemote) {

			boolean candleLight = event.player.isPotionActive(AncientSpellcraftPotions.candlelight);
			boolean mageLight = event.player.isPotionActive(AncientSpellcraftPotions.magelight) ||
					(ItemArtefact.isArtefactActive(event.player, AncientSpellcraftItems.charm_magic_light));

			if (mageLight || candleLight) {
				EntityPlayer player = event.player;

				int blockX = MathHelper.floor(player.posX);
				int blockY = MathHelper.floor(player.posY - 0.2D - player.getYOffset());
				int blockZ = MathHelper.floor(player.posZ);

				BlockPos blockLocation = new BlockPos(blockX, blockY, blockZ).up();

				if (player.world.getBlockState(blockLocation).getBlock() == Blocks.AIR) {
					if (mageLight) {
						player.world.setBlockState(blockLocation, AncientSpellcraftBlocks.MAGELIGHT.getDefaultState());
					} else {
						player.world.setBlockState(blockLocation, AncientSpellcraftBlocks.CANDLELIGHT.getDefaultState());
					}
				} else if (player.world.getBlockState(blockLocation.add(player.getLookVec().x, player.getLookVec().y, player.getLookVec().z)).getBlock() == Blocks.AIR) {
					if (mageLight) {
						player.world.setBlockState(blockLocation.add(player.getLookVec().x, player.getLookVec().y, player.getLookVec().z),
								AncientSpellcraftBlocks.MAGELIGHT.getDefaultState());
					} else {
						player.world.setBlockState(blockLocation.add(player.getLookVec().x, player.getLookVec().y, player.getLookVec().z),
								AncientSpellcraftBlocks.CANDLELIGHT.getDefaultState());
					}
				}
			}
		}
	}

	//	@SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = true)
	//	public static void onPlayerTickEvent(TickEvent.PlayerTickEvent event) {
	//		//		System.out.println("method onPlayerTickEvent called");
	//		if (event.phase == TickEvent.Phase.START && !event.player.world.isRemote) {
	//			//			System.out.println("step 1");
	//
	//			if (event.player.isPotionActive(AncientSpellcraftPotions.magelight)) {
	//				//				System.out.println("step 2");
	//
	//				int blockX = MathHelper.floor(event.player.posX);
	//				int blockY = MathHelper.floor(event.player.posY - 0.2D - event.player.getYOffset());
	//				//				int blockY = MathHelper.floor(event.player.posY + 1.2D - event.player.getYOffset());
	//				//				event.player.getYOffset());
	//				int blockZ = MathHelper.floor(event.player.posZ);
	//				// place light at head level
	//				BlockPos blockLocation = new BlockPos(blockX, blockY, blockZ).up();
	//				if (event.player.world.getBlockState(blockLocation).getBlock() == Blocks.AIR) {
	//					//					System.out.println("step 3A");
	//					event.player.world.setBlockState(
	//							blockLocation,
	//							AncientSpellcraftBlocks.MAGELIGHT.getDefaultState());
	//				} else if (event.player.world.getBlockState(
	//						blockLocation.add(
	//								event.player.getLookVec().x,
	//								event.player.getLookVec().y,
	//								event.player.getLookVec().z)).getBlock() == Blocks.AIR) {
	//					event.player.world.setBlockState(
	//							blockLocation.add(
	//									event.player.getLookVec().x,
	//									event.player.getLookVec().y,
	//									event.player.getLookVec().z),
	//							AncientSpellcraftBlocks.MAGELIGHT.getDefaultState());
	//					//					System.out.println("step 3B");
	//				}
	//			}
	//		}
	//	}

	@Override
	public void update() {
		// check if player has moved away from the tile entity
		EntityPlayer thePlayer = world.getClosestPlayer(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5, 2.0D, false);

		if (thePlayer == null) {
			if (world.getBlockState(getPos()).getBlock() ==
					AncientSpellcraftBlocks.MAGELIGHT) {
				world.setBlockToAir(this.getPos());
			}
		} else if (!(thePlayer.isPotionActive(AncientSpellcraftPotions.magelight) || ItemArtefact.isArtefactActive(thePlayer, AncientSpellcraftItems.charm_magic_light))) {
			if (world.getBlockState(getPos()).getBlock() == AncientSpellcraftBlocks.MAGELIGHT) {
				world.setBlockToAir(getPos());
			}
		}
	}
}
	