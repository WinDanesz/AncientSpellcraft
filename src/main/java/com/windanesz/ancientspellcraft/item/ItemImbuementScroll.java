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
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.Arrays;

public class ItemImbuementScroll extends ItemRareScroll {

	public ItemImbuementScroll() {
		super();
	}

	//	@Override
	//	public int getMaxItemUseDuration(ItemStack stack) {
	//		return 60;
	//	}

	@Override
	public EnumAction getItemUseAction(ItemStack stack) {
		return SpellActions.IMBUE;
	}

	@Override
	public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
		if (world.getBlockState(pos).getBlock() == WizardryBlocks.imbuement_altar && world.getTileEntity(pos) instanceof TileEntityImbuementAltar) {
			TileEntityImbuementAltar tile = (TileEntityImbuementAltar) world.getTileEntity(pos);
			Element[] elements = getReceptacleElements(world, pos);

			if (Arrays.stream(elements).distinct().count() == 1) {
				// all matching
				Element element = elements[0];
				ItemStack oldStack = tile.getStack();
				String oldName = oldStack.getItem().getRegistryName().toString();

				if (element != null && (oldStack.getItem() instanceof ItemWand || oldStack.getItem() instanceof ItemWizardArmour)
						&& oldStack.getItem().getRegistryName().getNamespace().equals(Wizardry.MODID)) {
					if (!world.isRemote) {
						Element oldElement = oldStack.getItem() instanceof ItemWand ? ((ItemWand) oldStack.getItem()).element :
								((ItemWizardArmour) oldStack.getItem()).element;
						String oldElementName = oldStack.getItem() instanceof ItemWand ? ((ItemWand) oldStack.getItem()).element.getName() :
								((ItemWizardArmour) oldStack.getItem()).element.getName();
						if (oldName != null) {
							String name = oldName.replace(oldElementName, element.getName());
							ResourceLocation newItemName = new ResourceLocation(name);
							Item item = ForgeRegistries.ITEMS.getValue(newItemName);
							if (item != null && element != oldElement) {
								ItemStack newStack = new ItemStack(item);
								newStack.setItemDamage(oldStack.getItemDamage());
								newStack.setTagCompound(oldStack.getTagCompound());
								tile.setStack(newStack);
								if (!player.isCreative()) {
									player.getHeldItem(hand).shrink(1);
								}
								consumeReceptacleContents(world, pos);
								return EnumActionResult.SUCCESS;
							}
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
	//
	//	@Override
	//	public ItemStack onItemUseFinish(ItemStack stack, World world, EntityLivingBase entityLiving) {
	//		if (entityLiving instanceof EntityPlayer) {
	//			EntityPlayer player = (EntityPlayer) entityLiving;
	//			RayTraceResult res = RayTracer.standardBlockRayTrace(world, player, 7, false, true, false);
	//			if (res.typeOfHit == RayTraceResult.Type.BLOCK) {
	//				BlockPos pos = res.getBlockPos();
	//				if (world.getBlockState(pos).getBlock() == WizardryBlocks.imbuement_altar && world.getTileEntity(pos) instanceof TileEntityImbuementAltar) {
	//					TileEntityImbuementAltar tile = (TileEntityImbuementAltar) world.getTileEntity(pos);
	//					Element[] elements = getReceptacleElements(world, pos);
	//
	//					if (Arrays.stream(elements).distinct().count() == 1) {
	//						// all matching
	//						Element element = elements[0];
	//						ItemStack oldStack = tile.getStack();
	//						String oldName = oldStack.getItem().getRegistryName().toString();
	//
	//						if ((oldStack.getItem() instanceof ItemWand || oldStack.getItem() instanceof ItemWizardArmour)
	//								&& oldStack.getItem().getRegistryName().getNamespace().equals(Wizardry.MODID)) {
	//							String oldElementName = ((ItemWand) oldStack.getItem()).element.getName();
	//							ResourceLocation newItemName = new ResourceLocation(oldName.replace(oldElementName, element.getName()));
	//							Item item = ForgeRegistries.ITEMS.getValue(newItemName);
	//							if (item != null) {
	//								ItemStack newStack = new ItemStack(item);
	//								newStack.setItemDamage(oldStack.getItemDamage());
	//								newStack.setTagCompound(oldStack.getTagCompound());
	//								if (!world.isRemote) {
	//									tile.setStack(newStack);
	//									consumeReceptacleContents(world, pos);
	//								} else {
	//									if (world.isRemote) {
	//										int[] colours = BlockReceptacle.PARTICLE_COLOURS.get(elements[element.ordinal()]);
	//										double particleX, particleZ;
	//										Vec3d origin = new Vec3d(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);
	//										for (int i = 0; i < 40; i++) {
	//											particleX = origin.x - 1.0d + 2 * world.rand.nextDouble();
	//											particleZ = origin.z - 1.0d + 2 * world.rand.nextDouble();
	//											ParticleBuilder.create(ParticleBuilder.Type.DARK_MAGIC).pos(particleX, origin.y, particleZ)
	//													.vel(particleX - origin.x, 0, particleZ - origin.z).clr(colours[0],colours[1],colours[2]).spawn(world);
	//
	//											particleX = origin.x - 1.0d + 2 * world.rand.nextDouble();
	//											particleZ = origin.z - 1.0d + 2 * world.rand.nextDouble();
	//											ParticleBuilder.create(ParticleBuilder.Type.SPARKLE).pos(particleX, origin.y, particleZ)
	//													.vel(particleX - origin.x, 0, particleZ - origin.z).time(30).clr(colours[0],colours[1],colours[2]).spawn(world);
	//
	//											particleX = origin.x - 1.0d + 2 * world.rand.nextDouble();
	//											particleZ = origin.z - 1.0d + 2 * world.rand.nextDouble();
	//
	//											IBlockState block = world.getBlockState(new BlockPos(origin.x, origin.y - 0.5, origin.z));
	//
	//											if (block != null) {
	//												world.spawnParticle(EnumParticleTypes.BLOCK_DUST, particleX, origin.y,
	//														particleZ, particleX - origin.x, 0, particleZ - origin.z, Block.getStateId(block));
	//											}
	//										}
	//									}
	//								}
	//							}
	//						}
	//					}
	//				}
	//			}
	//		}
	//		return super.onItemUseFinish(stack, world, entityLiving);
	//	}
	//
	//	@Override
	//	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
	//		if (world.getBlockState(pos).getBlock() == WizardryBlocks.imbuement_altar && world.getTileEntity(pos) instanceof TileEntityImbuementAltar) {
	//			TileEntityImbuementAltar tile = (TileEntityImbuementAltar) world.getTileEntity(pos);
	//			Element[] elements = getReceptacleElements(world, pos);
	//
	//			if (Arrays.stream(elements).distinct().count() == 1) {
	//				// all matching
	//				Element element = elements[0];
	//				ItemStack oldStack = tile.getStack();
	//				String oldName = oldStack.getItem().getRegistryName().toString();
	//
	//				if ((oldStack.getItem() instanceof ItemWand || oldStack.getItem() instanceof ItemWizardArmour)
	//						&& oldStack.getItem().getRegistryName().getNamespace().equals(Wizardry.MODID)) {
	//					String oldElementName = ((ItemWand) oldStack.getItem()).element.getName();
	//					ResourceLocation newItemName = new ResourceLocation(oldName.replace(oldElementName, element.getName()));
	//					Item item = ForgeRegistries.ITEMS.getValue(newItemName);
	//					if (item != null) {
	//						if (world.isRemote) {
	//							ParticleBuilder.create(ParticleBuilder.Type.DUST).pos(pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5).clr(255, 255, 247).fade(0, 0, 0).spin(0.8f, 0.07f).time(20).scale(1.2f).spawn(world);
	//							ParticleBuilder.create(ParticleBuilder.Type.DUST).pos(pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5).clr(255, 255, 247).vel(0, 0.1, 0).fade(0, 0, 0).spin(0.8f, 0.07f).time(20).scale(1.2f).spawn(world);
	//						}
	//						return EnumActionResult.SUCCESS;
	//					}
	//				}
	//			}
	//		}
	//		return super.onItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ);
	//	}

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

