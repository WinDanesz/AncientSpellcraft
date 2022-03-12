package com.windanesz.ancientspellcraft.tileentity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
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

	private int lifeTime = 0;
	private boolean extended = false;

	public EntityPlayer player = null;

	public EntityPlayer getPlayer() {
		return player;
	}

	public void setPlayer(EntityPlayer player) {
		this.player = player;
	}
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

//			if (event.player.isPotionActive(AncientSpellcraftPotions.candlelight)) {
//				light(0, event.player);
//				return;
//			}
//			if (event.player.isPotionActive(AncientSpellcraftPotions.magelight) ||
//					(ItemArtefact.isArtefactActive(event.player, AncientSpellcraftItems.charm_magic_light))) {
//				light(1, event.player);
//				return;
//			}
//
//			if (ItemArtefact.isArtefactActive(event.player, AncientSpellcraftItems.charm_glyph_illumination)
//					&& event.player.getHeldItemMainhand().getItem() instanceof ItemBattlemageSword) {
//				light(2, event.player);
//			}
		}
	}

	public static void light(int source, EntityPlayer player) {

		int blockX = MathHelper.floor(player.posX);
		int blockY = MathHelper.floor(player.posY - 0.2D - player.getYOffset());
		int blockZ = MathHelper.floor(player.posZ);

		BlockPos blockLocation = new BlockPos(blockX, blockY, blockZ).up();

//		if (player.world.getBlockState(blockLocation).getBlock() == Blocks.AIR) {
//			if (source == 1) {
//				player.world.setBlockState(blockLocation, AncientSpellcraftBlocks.MAGELIGHT.getDefaultState());
//			} else {
//				player.world.setBlockState(blockLocation, AncientSpellcraftBlocks.CANDLELIGHT.getDefaultState());
//			}
//		}
//		else if (player.world.getBlockState(blockLocation.add(player.getLookVec().x, player.getLookVec().y, player.getLookVec().z)).getBlock() == Blocks.AIR) {
//			if (source == 1) {
//				player.world.setBlockState(blockLocation.add(player.getLookVec().x, player.getLookVec().y, player.getLookVec().z),
//						AncientSpellcraftBlocks.MAGELIGHT.getDefaultState());
//			} else {
//				player.world.setBlockState(blockLocation.add(player.getLookVec().x, player.getLookVec().y, player.getLookVec().z),
//						AncientSpellcraftBlocks.CANDLELIGHT.getDefaultState());
//			}
//		}
	}

	@Override
	public void update() {

		if (lifeTime > 0) {
			lifeTime--;
		} else {

			if (world.getTileEntity(pos.down()) instanceof TileRune) {
				return;
			}

			if (this.player == null) {
				// check if player has moved away from the tile entity
				//world.setBlockToAir(this.getPos());
//				player.getDistanceSq()
//				EntityPlayer thePlayer = world.getClosestPlayer(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5, 2.0D, false);
			}
//			else {
//
//			}
//
//			if (thePlayer == null) {
//				if (world.getBlockState(getPos()).getBlock() == AncientSpellcraftBlocks.MAGELIGHT) {
//					System.out.println("setair");
//					world.setBlockToAir(this.getPos());
//				}
//			} else {
//				boolean artefact = ItemNewArtefact.isNewArtefactActive(thePlayer, AncientSpellcraftItems.head_magelight);
//				if (artefact && !extended) {
//					extended = true;
//					lifeTime = 600;
//				}
				//if (!(thePlayer.isPotionActive(AncientSpellcraftPotions.magelight) || ItemArtefact.isArtefactActive(thePlayer, AncientSpellcraftItems.charm_magic_light))) {


//					if (world.getBlockState(getPos()).getBlock() == AncientSpellcraftBlocks.MAGELIGHT) {
//						System.out.println("setair");
//						world.setBlockToAir(getPos());
//					}
			//	}
//			}
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setInteger("lifetime", lifeTime);
		compound.setBoolean("extended", extended);
		return super.writeToNBT(compound);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		lifeTime = compound.getInteger("lifetime");
		extended = compound.getBoolean("extended");
		super.readFromNBT(compound);
	}

	public void setLifeTime(int i) {
		lifeTime = i;
	}
}
	