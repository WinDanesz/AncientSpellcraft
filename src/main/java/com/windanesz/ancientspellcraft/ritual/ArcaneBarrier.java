package com.windanesz.ancientspellcraft.ritual;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.entity.EntityArcaneBarrier;
import com.windanesz.ancientspellcraft.misc.DonorPerks;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftBlocks;
import com.windanesz.ancientspellcraft.tileentity.TileRune;
import com.windanesz.ancientspellcraft.util.ASParticles;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.registry.WizardryBlocks;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.ParticleBuilder;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ArcaneBarrier extends Ritual implements IRitualIngredient, IRitualBlockRequirement {

	private ItemStack stack = ItemStack.EMPTY;
	private String BLOCK_TYPE_TAG = "block_type";

	// throw in an astral diamond to make permanent?
	public ArcaneBarrier() {
		super(AncientSpellcraft.MODID, "arcane_barrier", SpellActions.SUMMON, false);
	}

	@Override
	public void initialEffect(World world, EntityPlayer caster, TileRune centerPiece) {
		if (caster != null && !world.isRemote) {
			EntityArcaneBarrier barrier = new EntityArcaneBarrier(world);
			barrier.setPosition(centerPiece.getXCenter(), centerPiece.getY(), centerPiece.getZCenter());
			barrier.setCaster(caster);
			barrier.lifetime = 40;
			barrier.setRadius(1);
			world.spawnEntity(barrier);

		}
	}

	@Override
	public void onRitualFinish(World world, EntityPlayer caster, TileRune centerPiece) {

		super.onRitualFinish(world, caster, centerPiece);
	}

	// todo - determine barrier size by the supporter blocks under it ( e.g. emerald, diamond etc)

	@Override
	public boolean areContinuousRequirementsMet(World world, TileRune centerPiece) {
		NBTTagCompound ritualData = centerPiece.getRitualData();

		if (ritualData.hasKey(BLOCK_TYPE_TAG)) {
			String blockName = ritualData.getString(BLOCK_TYPE_TAG);
			for (BlockPos pos : BlockPos.getAllInBox(centerPiece.getPos().west(2).north(2).down(), centerPiece.getPos().east(2).south(2).down())) {
				if (!world.getBlockState(pos).getBlock().getRegistryName().getPath().equals(blockName)) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	@Override
	public void effect(World world, EntityPlayer caster, TileRune centerPiece) {
		super.effect(world, caster, centerPiece);

		// every 5 seconds
		if (world.getTotalWorldTime() % 100L == 0) {

			// check for the integrity of the supporting blocks
			if (!areContinuousRequirementsMet(world, centerPiece)) {
				if (world.getBlockState(centerPiece.getPos()).getBlock() == AncientSpellcraftBlocks.PLACED_RUNE) {
					world.removeTileEntity(centerPiece.getPos());
					world.setBlockState(centerPiece.getPos(), AncientSpellcraftBlocks.RUNE_USED.getDefaultState());
				}
			}

			// donor perk to set the colour
			List<EntityItem> entityItemList = EntityUtils.getEntitiesWithinRadius(1, centerPiece.getPos().getX(), centerPiece.getPos().getY(), centerPiece.getPos().getZ(), world, EntityItem.class);
			if (!entityItemList.isEmpty()) {
				EntityItem dye = entityItemList.get(0);

				if (dye.getItem().getItem() == Items.DYE) {
					int index = dye.getItem().getMetadata();
					List<EntityArcaneBarrier> barriers = EntityUtils.getEntitiesWithinRadius(1, centerPiece.getX(), centerPiece.getY(), centerPiece.getZ(), world, EntityArcaneBarrier.class);

					if (!barriers.isEmpty()) {
						EntityArcaneBarrier barrier = barriers.get(0);
						if (barrier.getOwnerId() != null && !DonorPerks.isDonor(barrier.getOwnerId())) {
							barrier.setColour(index);
							dye.getItem().shrink(1);
						}
					}
				}
			}
		}

		List<EntityArcaneBarrier> barriers = EntityUtils.getEntitiesWithinRadius(1, centerPiece.getX(), centerPiece.getY(), centerPiece.getZ(), world, EntityArcaneBarrier.class);
		EntityArcaneBarrier barrier;

		if (!barriers.isEmpty()) {
			barrier = barriers.get(0);
		} else {
			return;
		}

		if (barrier != null) {

			float currentRadius = barrier.getRadius();
			barriers.get(0).lifetime += 1;

			if (currentRadius < getSizeLimit(centerPiece) && !world.isRemote) {
				barriers.get(0).setRadius(currentRadius * 1.001f);
			}

			if (world.isRemote) {

				float r = barrier.getColour().getR(), g = barrier.getColour().getG(), b = barrier.getColour().getB();
				//				float r = 0.67f, g = 0.23f + 0.05f, b = 0.85f;

				double posX = centerPiece.getXCenter();
				double posY = centerPiece.getY();
				double posZ = centerPiece.getZCenter();
				Random rand = world.rand;

				if (!barriers.isEmpty()) {

					float radius = barriers.get(0).getRadius();

					if (world.getTotalWorldTime() % 3 == 0) {
						ParticleBuilder.create(ParticleBuilder.Type.SPARKLE, rand, posX + rand.nextDouble() * 0.5d * (rand.nextBoolean() ? 1 : -1), posY,
								posZ + rand.nextDouble() * 0.5d * (rand.nextBoolean() ? 1 : -1), 0.03, true).vel(0, 0.3, 0).clr(r, g, b)
								.time(20 + rand.nextInt((int) radius)).spawn(world);

						ParticleBuilder.create(ParticleBuilder.Type.SPARKLE, rand, posX + rand.nextDouble() * 0.2d * (rand.nextBoolean() ? 1 : -1), posY,
								posZ + rand.nextDouble() * 0.2d * (rand.nextBoolean() ? 1 : -1), 0.03, true).spin(0.7, 0.05).vel(0, 0.3, 0).clr(r, g, b).fade(230, 230, 230)
								.time(20 + rand.nextInt((int) radius)).spawn(world);
					}

					ParticleBuilder.create(ParticleBuilder.Type.FLASH).clr(r, g, b).face(EnumFacing.UP).pos(posX, posY + 0.01f, posZ).scale(2f).spawn(world);

					ParticleBuilder.create(ParticleBuilder.Type.FLASH).clr(r, g, b).pos(posX, posY + 0.01f, posZ).scale(0.9f).spawn(world);

					ParticleBuilder.create(ParticleBuilder.Type.FLASH).clr(r, g, b).face(EnumFacing.UP).pos(posX, centerPiece.getY() + radius - 0.4f, posZ).scale(Math.min(4, radius * 0.3f)).spawn(world);
					Vec3d target = new Vec3d(posX, centerPiece.getY() + radius - 0.4f, posZ);
					ParticleBuilder.create(ASParticles.CONSTANT_BEAM).clr(r, g, b).pos(posX, posY, posZ).target(target).scale(1.5f).shaded(true).time(2).spawn(world);
				}

			}
		}

	}

	@Override
	public boolean areInitialRequirementsMet(World world, TileRune centerPiece) {
		List<Block> blockList = new ArrayList<>();

		blockList.add(Blocks.LAPIS_BLOCK);
		blockList.add(WizardryBlocks.crystal_block);
		blockList.add(Blocks.GOLD_BLOCK);
		blockList.add(Blocks.EMERALD_BLOCK);
		blockList.add(Blocks.DIAMOND_BLOCK);

		for (Block block : blockList) {
			boolean allMatches = true;

			Iterable<BlockPos> posIterable = BlockPos.getAllInBox(centerPiece.getPos().west(2).north(2).down(), centerPiece.getPos().east(2).south(2).down());
			for (BlockPos pos : posIterable) {
				if (world.getBlockState(pos).getBlock() != block) {
					allMatches = false;
					break;
				}
			}

			if (allMatches = true) {
				NBTTagCompound ritualData = centerPiece.getRitualData();
				Block currBlock = world.getBlockState(centerPiece.getPos().down()).getBlock();
				if (currBlock.getRegistryName() != null) {

					ritualData.setString(BLOCK_TYPE_TAG, currBlock.getRegistryName().getPath());
					centerPiece.setRitualData(ritualData);
					centerPiece.sendUpdates();
					return true;
				}
			}

		}
		return false;
	}

	private int getSizeLimit(TileRune centerPiece) {
		NBTTagCompound ritualData = centerPiece.getRitualData();
		if (ritualData.hasKey(BLOCK_TYPE_TAG)) {
			String blockName = ritualData.getString(BLOCK_TYPE_TAG);
			return SizeLimits.getRadiusLimit(blockName);
		}

		return SizeLimits.LAPIS_BLOCK.radiusLimit;
	}

	@Override
	public List<List<ItemStack>> getRequiredIngredients() {
		List<List<ItemStack>> ingredients = new ArrayList<>();
		List<ItemStack> grandCrystal = new ArrayList<>();

		grandCrystal.add(new ItemStack(WizardryItems.grand_crystal));

		ingredients.add(grandCrystal);
		return ingredients;
	}

	private enum SizeLimits {
		LAPIS_BLOCK("lapis_block", 10),
		MAGIC_CRYSTAL("CRYSTAL_BLOCK", 20),
		EMERALD_BLOCK("emerald_block", 25),
		GOLD_BLOCK("gold_block", 30),
		DIAMOND_BLOCK("diamond_block", 35),
		;

		private String block;
		private int radiusLimit;

		SizeLimits(String block, int radiusLimit) {
			this.block = block;
			this.radiusLimit = radiusLimit;
		}

		public static int getRadiusLimit(String checkBlock) {
			for (int i = 0; i < SizeLimits.values().length; i++) {
				if (values()[i].block.equals(checkBlock.toLowerCase())) {
					return values()[i].radiusLimit;
				}
			}
			return 15;
		}
	}

}
