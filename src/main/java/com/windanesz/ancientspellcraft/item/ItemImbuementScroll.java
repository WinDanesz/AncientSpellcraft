package com.windanesz.ancientspellcraft.item;

import electroblob.wizardry.Wizardry;
import electroblob.wizardry.block.BlockReceptacle;
import electroblob.wizardry.constants.Element;
import electroblob.wizardry.item.ItemWand;
import electroblob.wizardry.item.ItemWizardArmour;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.registry.WizardryBlocks;
import electroblob.wizardry.tileentity.TileEntityImbuementAltar;
import electroblob.wizardry.tileentity.TileEntityReceptacle;
import electroblob.wizardry.util.GeometryUtils;
import electroblob.wizardry.util.ParticleBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Arrays;

public class ItemImbuementScroll extends ItemRareScroll {

	public ItemImbuementScroll() {
		super();
	}

	@Override
	public EnumAction getItemUseAction(ItemStack stack) {
		return SpellActions.IMBUE;
	}

	@Override
	public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
		if (world.getBlockState(pos).getBlock() == WizardryBlocks.imbuement_altar && world.getTileEntity(pos) instanceof TileEntityImbuementAltar) {
			TileEntityImbuementAltar tile = (TileEntityImbuementAltar) world.getTileEntity(pos);
			Element[] dustElements = getReceptacleElements(world, pos);

			if (Arrays.stream(dustElements).distinct().count() == 1) {
				// all matching
				Element element = dustElements[0];
				ItemStack oldStack = tile.getStack();

				if (element != null && (oldStack.getItem() instanceof ItemWand || oldStack.getItem() instanceof ItemWizardArmour)
						&& oldStack.getItem().getRegistryName().getNamespace().equals(Wizardry.MODID)) {
					if (!world.isRemote) {

							ItemStack newStack = ItemStack.EMPTY;

							if (oldStack.getItem() instanceof ItemWand) {
								newStack = new ItemStack(ItemWand.getWand(((ItemWand) oldStack.getItem()).tier, element));
							} else if (oldStack.getItem() instanceof ItemWizardArmour) {
								newStack = new ItemStack(ItemWizardArmour.getArmour(element, ((ItemWizardArmour) oldStack.getItem()).armourClass,
										((ItemWizardArmour) oldStack.getItem()).armorType));
							}

							if (!newStack.isEmpty()) {
								newStack.setItemDamage(oldStack.getItemDamage());
								newStack.setTagCompound(oldStack.getTagCompound());
								tile.setStack(newStack);
								if (!player.isCreative()) {
									consumeScroll(player, hand);
								}
								consumeReceptacleContents(world, pos);
								return EnumActionResult.SUCCESS;
							}

					}
					if (world.isRemote) {

						int[] colours = BlockReceptacle.PARTICLE_COLOURS.get(element);

						double particleX, particleZ;
						Vec3d origin = new Vec3d(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);

						Vec3d centre = GeometryUtils.getCentre(pos);
						ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(centre).scale(0.35f).time(48).clr(colours[0]).spawn(world);
						double r = 0.12;

						for (int i = 0; i < 20; i++) {

							double x = r * (world.rand.nextDouble() * 4 - 1);
							double y = r * (world.rand.nextDouble() * 4 - 1);
							double z = r * (world.rand.nextDouble() * 4 - 1);

							ParticleBuilder.create(ParticleBuilder.Type.DUST).pos(centre.x + x, centre.y + y + 1, centre.z + z)
									.vel(x * -0.03, 0.02, z * -0.03).time(44 + world.rand.nextInt(8)).clr(colours[1]).fade(colours[2]).spawn(world);
						}

						for (int i = 0; i < 40; i++) {
							particleX = origin.x - 1.0d + 2 * world.rand.nextDouble();
							particleZ = origin.z - 1.0d + 2 * world.rand.nextDouble();
							ParticleBuilder.create(ParticleBuilder.Type.DUST).pos(particleX, origin.y, particleZ)
									.vel(particleX - origin.x, 0, particleZ - origin.z).clr(colours[0], colours[1], colours[2]).spawn(world);

							particleX = origin.x - 1.0d + 2 * world.rand.nextDouble();
							particleZ = origin.z - 1.0d + 2 * world.rand.nextDouble();
							ParticleBuilder.create(ParticleBuilder.Type.SPARKLE).pos(particleX, origin.y, particleZ)
									.vel(particleX - origin.x, 0, particleZ - origin.z).time(30).clr(colours[0], colours[1], colours[2]).spawn(world);

							particleX = origin.x - 1.0d + 2 * world.rand.nextDouble();
							particleZ = origin.z - 1.0d + 2 * world.rand.nextDouble();

							IBlockState block = world.getBlockState(new BlockPos(origin.x, origin.y - 0.5, origin.z));

							if (block != null) {
								world.spawnParticle(EnumParticleTypes.BLOCK_DUST, particleX, origin.y,
										particleZ, particleX - origin.x, 0, particleZ - origin.z, Block.getStateId(block));
							}
						}
					}
					consumeReceptacleContents(world, pos);
					return EnumActionResult.SUCCESS;
				}
			}
		}
		return super.onItemUseFirst(player, world, pos, side, hitX, hitY, hitZ, hand);
	}

	/**
	 * Returns the elements of the 4 adjacent receptacles, in SWNE order. Null means an empty or missing receptacle.
	 */
	private static Element[] getReceptacleElements(World world, BlockPos pos) {

		Element[] elements = new Element[4];

		for (EnumFacing side : EnumFacing.HORIZONTALS) {

			TileEntity tileEntity = world.getTileEntity(pos.offset(side));

			if (tileEntity instanceof TileEntityReceptacle) {
				elements[side.getHorizontalIndex()] = ((TileEntityReceptacle) tileEntity).getElement();
			} else {
				elements[side.getHorizontalIndex()] = null;
			}
		}

		return elements;
	}

	/**
	 * Empties the 4 adjacent receptacles.
	 */
	private void consumeReceptacleContents(World world, BlockPos pos) {

		for (EnumFacing side : EnumFacing.HORIZONTALS) {

			TileEntity tileEntity = world.getTileEntity(pos.offset(side));

			if (tileEntity instanceof TileEntityReceptacle) {
				((TileEntityReceptacle) tileEntity).setElement(null);
			}
		}
	}
}

