package com.windanesz.ancientspellcraft.ritual;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.tileentity.TileRune;
import electroblob.wizardry.data.IStoredVariable;
import electroblob.wizardry.data.Persistence;
import electroblob.wizardry.data.WizardData;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.util.BlockUtils;
import electroblob.wizardry.util.ParticleBuilder;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class Forest extends Ritual implements IRitualIngredient {

	public static final IStoredVariable<BlockPos> RITUAL_MARK_LOCATION = IStoredVariable.StoredVariable.ofBlockPos("ritual_mark_location", Persistence.ALWAYS).setSynced();

	public Forest() {
		super(AncientSpellcraft.MODID, "forest", SpellActions.SUMMON, false);
		WizardData.registerStoredVariables(RITUAL_MARK_LOCATION);
	}

	@Override
	public void initialEffect(World world, EntityPlayer caster, TileRune centerPiece) {
		ruinNonCenterPieceRunes(centerPiece, world);
	}

	@Override
	public void effect(World world, EntityPlayer caster, TileRune centerPiece) {
		super.effect(world, caster, centerPiece);
		if (!world.isRemote) {
			if (centerPiece.getRitualData() == null || (!centerPiece.getRitualData().hasKey("wood_type"))) {

				List<EntityItem> actualIngredients = getActualIngredients(world, centerPiece, 1);

				if (!actualIngredients.isEmpty()) {

					NBTTagCompound compound = new NBTTagCompound();

					if (actualIngredients.get(0).getItem().getItem() == Item.getItemFromBlock(Blocks.SAPLING)) {
						compound.setInteger("wood_type", actualIngredients.get(0).getItem().getMetadata());
						centerPiece.setRitualData(compound);
						centerPiece.markDirty();
						actualIngredients.get(0).getItem().shrink(1);
					}
				}

			} else if (world.getTotalWorldTime() % 40 == 0) {
				int woodType = centerPiece.getRitualData().getInteger("wood_type");

				BlockPos centerPos = centerPiece.getPos();

				int x = centerPos.getX();
				int y = centerPos.getY();
				int z = centerPos.getZ();

				x = x + Math.max(world.rand.nextInt(30), 3) * (world.rand.nextBoolean() ? 1 : -1);
				z = z + Math.max(world.rand.nextInt(30), 3) * (world.rand.nextBoolean() ? 1 : -1);
				//			y = getTopYPos(world, new BlockPos(x, y, z));

				boolean test = false;
				for (int i = 0; i < 20 && !test; i++) {
					int j = i;
					if (i > 10) {
						j = (-1 * j) - 10;
					}
					y = y + j;
					BlockPos testPos = new BlockPos(x, y, z);
					IBlockState soil = world.getBlockState(testPos.down());
					if (soil.getBlock().canSustainPlant(soil, world, testPos.down(), net.minecraft.util.EnumFacing.UP, (BlockSapling) Blocks.SAPLING)) {

						BlockPos treePos = new BlockPos(x, y, z);

						if (BlockUtils.canPlaceBlock(caster, world, treePos)) {
							world.setBlockState(treePos, Blocks.SAPLING.getDefaultState().withProperty(BlockSapling.TYPE, BlockPlanks.EnumType.byMetadata(woodType)));

							if (BlockPlanks.EnumType.byMetadata(woodType) == BlockPlanks.EnumType.DARK_OAK) {

								//							if (BlockUtils.canPlaceBlock(caster, world, treePos.west()) &&
								//									(soil.getBlock().canSustainPlant(soil, world, treePos.west().down(), net.minecraft.util.EnumFacing.UP, (BlockSapling) Blocks.SAPLING)) ) {
								//							}
							}
						}

						if (world.getBlockState(treePos).getBlock() == Blocks.SAPLING) {
							((BlockSapling) world.getBlockState(treePos).getBlock()).generateTree(world, treePos, world.getBlockState(treePos), world.rand);
							test = true;
						}
					}

				}
			}
		}

		if (world.isRemote) {

			ParticleBuilder.create(ParticleBuilder.Type.FLASH).clr(83, 173, 23).pos(centerPiece.getX() + 0.5f, centerPiece.getY() + 0.5f, centerPiece.getZ() + 0.5f).scale(2f).spawn(world);

			for (int i = 0; i < 4; i++) {
				double dx = (world.rand.nextDouble() * (world.rand.nextBoolean() ? 1 : -1)) * 0.1;
				double dy = (world.rand.nextDouble() * (world.rand.nextBoolean() ? 1 : -1)) * 0.1;
				double dz = (world.rand.nextDouble() * (world.rand.nextBoolean() ? 1 : -1)) * 0.1;

				ParticleBuilder.create(ParticleBuilder.Type.LEAF)
						.clr(50, 168, 72)
						.pos(centerPiece.getXCenter(), centerPiece.getYCenter(), centerPiece.getZCenter())
						.vel(dx, dy, dz)
						.time(60)
						.spawn(world);
			}
		}
	}

	@Override
	public List<List<ItemStack>> getRequiredIngredients() {
		List<List<ItemStack>> ingredients = new ArrayList<>();
		List<ItemStack> woods = new ArrayList<>();
		for (BlockPlanks.EnumType value : BlockPlanks.EnumType.values()) {
			woods.add(new ItemStack(Blocks.SAPLING, 1, value.getMetadata()));
		}
		ingredients.add(woods);
		return ingredients;
	}
}
