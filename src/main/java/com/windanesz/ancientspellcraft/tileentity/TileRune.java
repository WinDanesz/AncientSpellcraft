package com.windanesz.ancientspellcraft.tileentity;

import com.windanesz.ancientspellcraft.constants.EnumFacingIntercardinal;
import com.windanesz.ancientspellcraft.registry.ASBlocks;
import com.windanesz.ancientspellcraft.registry.Rituals;
import com.windanesz.ancientspellcraft.ritual.IRitualBlockRequirement;
import com.windanesz.ancientspellcraft.ritual.IRitualIngredient;
import com.windanesz.ancientspellcraft.ritual.Ritual;
import electroblob.wizardry.util.BlockUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.UUID;
import java.util.stream.Collectors;

// maybe draw lines between the ground between each rune to connect them
public class TileRune extends TileEntity implements ITickable {

	private Item rune = Items.AIR;
	private String RUNE_TAG = "rune";
	private String MASTER_TAG = "is_master";
	private String RITUAL_DATA = "ritual_data";

	private TileRune master;
	private boolean isMaster;

	private int casterId;
	private UUID casterUUID;

	// used for initializing master/slave tiles
	private boolean firstRun = true;
	private TileRune centerPiece;

	private List<TileRune> connectedRunes;
	private Ritual ritual;

	private int ritualCurrentLifeTime = 0;

	List<TileRune> ritualRunes;

	public void setRitualRunes(List<TileRune> ritualRunes) {
		if (isMaster) {
			this.ritualRunes = ritualRunes;
		} else {
			getMaster().ritualRunes = ritualRunes;
		}
	}

	public void setRitualData(NBTTagCompound ritualData) {
		this.ritualData = ritualData;
	}

	private NBTTagCompound ritualData;

	public void setRitual(Ritual ritual) {
		if (isMaster()) {
			this.ritual = ritual;
		} else {
			getMaster().setRitual(ritual);
		}
	}

	public TileRune() {

	}

	public boolean isMaster() {
		return isMaster;
	}

	private void incrementRitualCurrentLifeTime() {
		if (isMaster()) {
			ritualCurrentLifeTime++;
		} else {
			getMaster().ritualCurrentLifeTime++;
		}
	}

	@Override
	public void update() {
		if (world.getBlockState(pos).getBlock() != ASBlocks.PLACED_RUNE) {
			world.removeTileEntity(pos);
			return;
		}

		//		prevConnectedRunes = new ArrayList<>(connectedRunes);
		if (firstRun) {
			initializeRuneMultiBlockIfNecessary();
			firstRun = false;
		}

		//		if (isMaster & world.isRemote && world.rand.nextBoolean()) {
		//			ParticleBuilder.create(ParticleBuilder.Type.SPARKLE)
		//					.pos(pos.getX() + world.rand.nextDouble(), pos.getY() + world.rand.nextDouble() / 2 + 0.5, pos.getZ() + world.rand.nextDouble()).vel(0, 0.01, 0)
		//					.time(20 + world.rand.nextInt(10)).clr(0.5f + (world.rand.nextFloat() / 2), 0.5f + (world.rand.nextFloat() / 2),
		//					0.5f + (world.rand.nextFloat() / 2)).spawn(world);
		//		}

		if (isMaster && !world.isRemote) {
			if (centerPiece != null && getMaster() != centerPiece) {
				//				for (TileRune rune : connectedRunes) {
				//					rune.setMaster(centerPiece, connectedRunes.size());
				//				}
				return;
			}

			TileRune currentRune;
			if (connectedRunes != null && ritual == null) {
				boolean matchesRitual = true;
				//				List<Ritual> matches = new ArrayList<>();
				for (TileRune rune : connectedRunes) {
					for (Map.Entry<ResourceLocation, Ritual> resourceLocationRitualEntry : Ritual.registry.getEntries()) {
						Ritual currentRitual = resourceLocationRitualEntry.getValue();

						if (currentRitual != Rituals.none) {

							if (currentRitual.getRitualProperties().centerpiece != null) {

								if (currentRitual.getRitualProperties().centerpiece == rune.rune) {

									BlockPos pos = rune.pos;

									//									matches.add(ritual);

									int width = currentRitual.getRitualProperties().width;
									int height = currentRitual.getRitualProperties().height;

									Item[][] matrix = new Item[height][width];

									int index = 0;
									for (int j = 0; j < height; j++) {
										for (int k = 0; k < width; k++) {

											if (currentRitual.getRitualProperties().pattern.get(index).getMatchingStacks().length > 0) {
												matrix[j][k] = currentRitual.getRitualProperties().pattern.get(index).getMatchingStacks()[0].getItem();
												BlockPos postest = pos.offset(EnumFacing.NORTH, j).offset(EnumFacing.WEST, k);

											} else {
												matrix[j][k] = Items.AIR;
											}

											index++;
										}
									}

									Item[][] posMatrix = new Item[height][width];
									for (int j = 0; j < height; j++) {
										for (int k = 0; k < width; k++) {
											BlockPos pos1 = pos.offset(EnumFacing.NORTH, j - (int) Math.ceil(height / 2)).offset(EnumFacing.WEST, k - (int) Math.ceil(width / 2));
											//	System.out.println("pos1:" + pos1);
											if (world.getTileEntity(pos1) instanceof TileRune) {
												TileRune tileRune = ((TileRune) world.getTileEntity(pos1));
												posMatrix[j][k] = tileRune.getRune();
											} else {
												posMatrix[j][k] = Items.AIR;
											}

										}
									}

									int row1, col1;
									row1 = matrix.length;
									col1 = matrix[0].length;

									for (int i = 0; i < 4; i++) {
										matchesRitual = true;

										for (int l = 0; l < row1; l++) {
											for (int j = 0; j < col1; j++) {
												if (matrix[l][j] != posMatrix[l][j]) {
													matchesRitual = false;
													break;
												}
											}
											if (!matchesRitual) {
												break;
											}
										}
										if (matchesRitual) {
											break;
										}
										matrix = rotMatrixBy90(matrix);
									}

									if (matchesRitual) {

										if (!rune.isMaster()) {
											List<BlockPos> blockPosList = BlockUtils.getBlockSphere(pos, (int) (width / 2) + 1).stream().filter(p -> p.getY() == pos.getY()).collect(Collectors.toList());
											List<TileRune> ritualRunes = blockPosList.stream().filter(pos1 -> world.getTileEntity(pos1) instanceof TileRune).map(pos1 -> (TileRune) world.getTileEntity(pos1)).collect(Collectors.toList());

											if (!ritualRunes.contains(rune)) {

											}
											ritualRunes.forEach(ritual -> setMaster(rune));
											ritualRunes.forEach(ritual -> connectedRunes = ritualRunes);

											for (TileRune ritualRune : ritualRunes) {
												ritualRune.setMaster(rune);

												// the new master
												if (ritualRune == rune) {
													ritualRune.connectedRunes = ritualRunes;
													ritualRune.ritualRunes = ritualRunes;
													ritualRune.ritual = currentRitual;
													ritualRune.centerPiece = rune;
												}
												ritualRune.sendUpdates();
											}
										}
									}
								}
							}
						}
					}

				}
			}
		}
		if (isMaster) {

			if (ritual != null) {

				if (ritualCurrentLifeTime == 0) {

					boolean ingredientsOk = true;
					boolean blocksOk = true;

					if (ritual instanceof IRitualIngredient) {
						List<EntityItem> actualIngredients = ((IRitualIngredient) ritual).getActualIngredients(world, centerPiece, 1);

						if (actualIngredients.isEmpty()) {
							ingredientsOk = false;
						} else {
							if (((IRitualIngredient) ritual).shouldConsumeIngredients()) {
								actualIngredients.stream().forEach(i -> i.getItem().shrink(1));
//								actualIngredients.forEach(i -> i.setDead());
							}
						}
					}
					if (ritual instanceof IRitualBlockRequirement) {
						blocksOk = ((IRitualBlockRequirement) ritual).areInitialRequirementsMet(world, centerPiece);
					}

					if (blocksOk && ingredientsOk) {
						updateRitualTick();
					}

				} else {
					updateRitualTick();
				}
			}

		}
	}

	private IBlockState getState() {
		return world.getBlockState(pos);
	}

	private void updateRitualTick() {
		if (this.ritualRunes == null) {
			List<BlockPos> blockPosList = BlockUtils.getBlockSphere(pos, ritual.getRitualProperties().width).stream().filter(p -> p.getY() == pos.getY()).collect(Collectors.toList());
			List<TileRune> ritualRunes = new ArrayList<>();
			for (BlockPos blockPos : blockPosList) {
				//						this.setMaster(centerPiece);
				//						centerPiece.ritual = this.ritual;
				if (world.getBlockState(blockPos).getBlock() == ASBlocks.PLACED_RUNE) {
					TileEntity tile = world.getTileEntity(blockPos);
					if (tile instanceof TileRune) {
						ritualRunes.add((TileRune) tile);
						((TileRune) tile).setMaster(centerPiece);
					}
				}
			}
			this.ritualRunes = ritualRunes;
		}

		if (centerPiece == null) {
			centerPiece = this;
		}
		incrementRitualCurrentLifeTime();
		if (getCaster() != null) {
			getRitual().effect(world, getCaster(), centerPiece);
		}
		if (getRitual().getMaxLifeTime() != -1 && getRitual().getMaxLifeTime() <= getRitualCurrentLifeTime() && getCaster() != null) {
			getRitual().onRitualFinish(world, getCaster(), centerPiece);
		}

	}

	private Item[][] rotMatrixBy90(Item[][] matrix) {
		int N = matrix.length;
		// Transpose the matrix
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < i; j++) {
				Item temp = matrix[i][j];
				matrix[i][j] = matrix[j][i];
				matrix[j][i] = temp;
			}
		}

		// swap columns
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N / 2; j++) {
				Item temp = matrix[i][j];
				matrix[i][j] = matrix[i][N - j - 1];
				matrix[i][N - j - 1] = temp;
			}
		}

		return matrix;
	}

	@Override
	public void invalidate() {
		super.invalidate();
		for (EnumFacingIntercardinal d : EnumFacingIntercardinal.HORIZONTALS) {
			TileEntity te = world.getTileEntity(new BlockPos(pos.getX() + d.getXOffset(), pos.getY() + d.getYOffset(), pos.getZ() + d.getZOffset()));
			if (te instanceof TileRune) {
				((TileRune) te).master = null;
				((TileRune) te).initializeRuneMultiBlockIfNecessary();
			}
		}
	}

	private void initializeRuneMultiBlockIfNecessary() {
		if (master == null || master.isInvalid()) {
			List<TileRune> connectedRunes = new ArrayList<TileRune>();
			Stack<TileRune> traversingRunes = new Stack<TileRune>();
			TileRune master = this;
			traversingRunes.add(this);
			while (!traversingRunes.isEmpty()) {
				TileRune rune = traversingRunes.pop();
				if (rune.isMaster()) {
					master = rune;
				}
				if (!connectedRunes.contains(rune)) {
					connectedRunes.add(rune);
				}
				for (EnumFacingIntercardinal d : EnumFacingIntercardinal.HORIZONTALS) {
					TileEntity te = world.getTileEntity(new BlockPos(rune.pos.getX() + d.getXOffset(), rune.pos.getY() + d.getYOffset(), rune.pos.getZ() + d.getZOffset()));
					if (te instanceof TileRune && !connectedRunes.contains(te)) {
						traversingRunes.add((TileRune) te);
					}
				}
			}

			for (TileRune rune : connectedRunes) {
				rune.setMaster(master);
				rune.connectedRunes = master.getConnectedRunes();
			}
			getMaster().connectedRunes = connectedRunes;
			this.markDirty();
		}
	}

	// ============================================ Tile data methods ==============================================

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		rune = ForgeRegistries.ITEMS.getValue(new ResourceLocation(compound.getString(RUNE_TAG)));
		isMaster = compound.getBoolean(MASTER_TAG);

		if (isMaster) {
			centerPiece = this;
			if (compound.hasKey(RITUAL_DATA)) {
				ritualData = compound.getCompoundTag(RITUAL_DATA);
			}
			if (compound.hasKey("ritual_lifetime")) {
				ritualCurrentLifeTime = compound.getInteger("ritual_lifetime");
			}
			if (compound.hasKey("ritual")) {
				ritual = Ritual.registry.getValue(new ResourceLocation(compound.getString("ritual")));
			}
		}
		if (compound.hasKey("caster_id")) {
			casterId = compound.getInteger("caster_id");
		}
		if (compound.hasKey("caster_uuid")) {
			casterUUID = compound.getUniqueId("caster_uuid");
			casterUUID = NBTUtil.getUUIDFromTag((NBTTagCompound) compound.getTag("caster_uuid"));
		}

	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setString(RUNE_TAG, rune.getRegistryName().toString());
		compound.setBoolean(MASTER_TAG, isMaster);

		if (isMaster) {
			if (ritualData != null) {
				compound.setTag(RITUAL_DATA, ritualData);
			}
			if (ritual != null) {
				compound.setString("ritual", ritual.getRegistryName().toString());
			}
			compound.setInteger("ritual_lifetime", ritualCurrentLifeTime);
		}
		compound.setInteger("caster_id", casterId);
		//		if (getCaster() != null) {
		//			compound.setUniqueId("caster_uuid", casterUUID);
		//		}
		compound.setTag("caster_uuid", NBTUtil.createUUIDTag(casterUUID));
		return compound;
	}

	public void sendUpdates() {
		world.markBlockRangeForRenderUpdate(pos, pos);
		world.notifyBlockUpdate(pos, getState(), getState(), 3);
		world.scheduleBlockUpdate(pos, this.getBlockType(), 0, 0);
		markDirty();
	}

	@Override
	public NBTTagCompound getUpdateTag() { return this.writeToNBT(new NBTTagCompound()); }

	@Nullable
	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		return new SPacketUpdateTileEntity(pos, 0, this.getUpdateTag());
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		readFromNBT(pkt.getNbtCompound());
	}

	// ============================================ Getter methods ==============================================

	public Item getRune() { return rune; }

	public int getX() { return getPos().getX(); }

	public int getY() { return getPos().getY(); }

	public int getZ() { return getPos().getZ(); }

	public float getXCenter() { return getPos().getX() + 0.5F; }

	public float getYCenter() { return getPos().getY() + 0.5F; }

	public float getZCenter() { return getPos().getZ() + 0.5F; }

	public Ritual getRitual() {
		if (isMaster()) {
			return ritual == null ? Rituals.none : ritual;
		} else {
			if (getMaster() != null) {
				return getMaster().getRitual();
			}
		}
		return Rituals.none;
	}

	public NBTTagCompound getRitualData() {
		NBTTagCompound compound = isMaster() ? ritualData : getMaster().ritualData;
		if (compound != null) {
			return compound;
		} else {
			return new NBTTagCompound();
		}
	}

	public List<TileRune> getRitualRunes() { return isMaster() ? ritualRunes : getMaster().ritualRunes; }

	public int getRitualCurrentLifeTime() { return isMaster() ? ritualCurrentLifeTime : getMaster().getRitualCurrentLifeTime(); }

	public List<TileRune> getConnectedRunes() { return isMaster() ? connectedRunes : getMaster().connectedRunes; }

	@Nullable
	public EntityPlayer getCaster() {
		if (casterUUID == null) {
			return null;
		}
		EntityPlayer entity = world.getPlayerEntityByUUID(casterUUID);
		//		Entity entity = world.getEntityByID(casterId);
		if (entity != null) {
			return entity;
		}
		return null;
	}

	public TileRune getMaster() {
		if (master == null)
			initializeRuneMultiBlockIfNecessary();
		if (master != null && master == this && !isMaster) {this.isMaster = true;}
		return master;
	}

	// ============================================ Setter methods ==============================================

	private void setMaster(TileRune master) {
		this.master = master;
		isMaster = master == this;
	}

	public void setRune(Item rune) { this.rune = rune; }

	public void setPlacer(EntityPlayer placer) {
		casterId = placer.getEntityId();
		casterUUID = placer.getUniqueID();
	}
}