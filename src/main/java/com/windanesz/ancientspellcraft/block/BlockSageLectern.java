package com.windanesz.ancientspellcraft.block;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.Settings;
import com.windanesz.ancientspellcraft.client.gui.GuiHandlerAS;
import com.windanesz.ancientspellcraft.item.ItemRitualBook;
import com.windanesz.ancientspellcraft.item.ItemSageTome;
import com.windanesz.ancientspellcraft.registry.ASTabs;
import com.windanesz.ancientspellcraft.spell.SpellLecternInteract;
import com.windanesz.ancientspellcraft.tileentity.TileSageLectern;
import com.windanesz.ancientspellcraft.util.ASUtils;
import electroblob.wizardry.block.BlockReceptacle;
import electroblob.wizardry.item.ItemSpellBook;
import electroblob.wizardry.util.ParticleBuilder;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemWrittenBook;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Random;

public class BlockSageLectern extends BlockHorizontal implements ITileEntityProvider {

	public BlockSageLectern() {
		super(Material.ROCK);
		this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
		this.setCreativeTab(ASTabs.ANCIENTSPELLCRAFT);
		this.setHardness(2.0F);
		this.setResistance(5.0F);
		this.setSoundType(SoundType.STONE);
	}

	public static boolean isBookOpen(IBlockState state, World world, BlockPos pos) {
		BlockPos lookUpPos = new BlockPos(pos.getX(), pos.getY(), pos.getZ());
		EnumFacing offset = state.getValue(BlockHorizontal.FACING);
		if (offset != null) {
			lookUpPos = lookUpPos.offset(offset);
		}
		EntityPlayer player = world.getClosestPlayer(lookUpPos.getX() + 0.5, lookUpPos.getY() + 0.5, lookUpPos.getZ() + 0.5,
				TileSageLectern.BOOK_OPEN_DISTANCE, false);

		TileEntity tile = world.getTileEntity(pos);

		return tile instanceof TileSageLectern && player != null && ((TileSageLectern) tile).shouldBookOpen(player);
	}

	public static ItemStack getItemOnLectern(World world, BlockPos pos) {
		TileEntity tileentity = world.getTileEntity(pos);

		if (tileentity instanceof TileSageLectern) {

			TileSageLectern lectern = (TileSageLectern) tileentity;

			return lectern.getBookSlotItem();
		}

		return ItemStack.EMPTY;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer.Builder(this).add(FACING).build();
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		EnumFacing enumfacing = EnumFacing.byIndex(meta);
		if (enumfacing.getAxis() == EnumFacing.Axis.Y) { enumfacing = EnumFacing.NORTH; }
		return this.getDefaultState().withProperty(FACING, enumfacing);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(FACING).getIndex();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {
		if (isBookOpen(state, world, pos)) {
			int[] colours = BlockReceptacle.PARTICLE_COLOURS.get(((TileSageLectern) world.getTileEntity(pos)).getBookElement());

			ParticleBuilder.create(ParticleBuilder.Type.DUST).pos(pos.getX() + rand.nextFloat(), pos.getY() + 1, pos.getZ() + rand.nextFloat())
					.vel(0, 0.03, 0).clr(colours[1]).fade(colours[2]).time(40).shaded(false).spawn(world);
		}
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullBlock(IBlockState state) {
		return false;
	}

	@Override
	public boolean isNormalCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean canPlaceTorchOnTop(IBlockState state, IBlockAccess world, BlockPos pos) {
		return false;
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing face) {
		return face == EnumFacing.DOWN ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
	}

	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
	}

	@Override
	public IBlockState withRotation(IBlockState state, Rotation rotation) {
		return state.withProperty(FACING, rotation.rotate(state.getValue(FACING)));
	}

	@Override
	public IBlockState withMirror(IBlockState state, Mirror mirror) {
		return state.withRotation(mirror.toRotation(state.getValue(FACING)));
	}

	@Nullable
	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileSageLectern();
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState block, EntityPlayer player, EnumHand hand,
			EnumFacing side, float hitX, float hitY, float hitZ) {

		// do not intercept spell casts with lectern interacting spells
		if (player.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemSageTome
				&& ((ItemSageTome) player.getHeldItem(EnumHand.MAIN_HAND).getItem()).getCurrentSpell(player.getHeldItem(EnumHand.MAIN_HAND)) instanceof SpellLecternInteract) {
			return false;
		}

		TileEntity tileEntity = world.getTileEntity(pos);

		if (tileEntity == null) {
			return false;
		}

		if (tileEntity instanceof TileSageLectern) {
			TileSageLectern lectern = (TileSageLectern) tileEntity;
			boolean lecternHasItem = lectern.hasItem();
			ItemStack heldStack = player.getHeldItem(hand);

			// Item removal / addition
			if (player.isSneaking()) {

				// Remove item
				if (lecternHasItem && heldStack.isEmpty()) {
					ASUtils.giveStackToPlayer(player, lectern.getBookSlotItem().copy());
					lectern.setInventorySlotContents(TileSageLectern.BOOK_SLOT, ItemStack.EMPTY);
				}
				return true;
			}

			String heldStackRegistryName = heldStack.getItem().getRegistryName().toString();

			// Put item
			if (!lecternHasItem && (heldStack.getItem() instanceof ItemSpellBook || heldStack.getItem() instanceof ItemSageTome
					|| Arrays.asList(Settings.generalSettings.sage_lectern_item_whitelist).contains(heldStackRegistryName))) {

				// shrink the player's held stack and put it to the lectern in a single step with splitStack
				lectern.setInventorySlotContents(TileSageLectern.BOOK_SLOT, player.getHeldItem(hand).splitStack(1));
				return true;
			}

			// GUI interactions
			if (!player.isSneaking()) {

				lectern.getSpellEffects().forEach(effect -> effect.persistentEffectOnLecternClick(lectern, player));

				// Sage books GUI
				if (!player.isSneaking() && lectern.getBookSlotItem().getItem() instanceof ItemSageTome) {
					if (!lectern.shouldBookOpen(player)) {
						player.sendStatusMessage(new TextComponentTranslation("tile.ancientspellcraft:sage_lectern.failed_attempt_to_read_tome"), true);
						return false;
					}

					player.openGui(AncientSpellcraft.instance, GuiHandlerAS.SAGE_LECTERN, world, pos.getX(), pos.getY(), pos.getZ());
					return true;
				}

				// Reading mode for items that are not sage books
				if (lecternHasItem && !(lectern.getBookSlotItem().getItem() instanceof ItemSageTome)) {

					// Spell Books, Ritual Books are handled here
					if (lectern.getBookSlotItem().getItem() instanceof ItemSpellBook || lectern.getBookSlotItem().getItem() instanceof ItemRitualBook) {
						player.openGui(AncientSpellcraft.instance, GuiHandlerAS.SPELL_GUI_LECTERN, world, pos.getX(), pos.getY(), pos.getZ());
						return true;
					} else

					if (lectern.getBookSlotItem().getItem() instanceof ItemWrittenBook) {
						AncientSpellcraft.proxy.openBookGUI(player, lectern.getBookSlotItem());
						player.addStat(StatList.getObjectUseStats(lectern.getBookSlotItem().getItem()));
					} else {
						lectern.getBookSlotItem().getItem().onItemRightClick(world, player, EnumHand.MAIN_HAND);

					}

					// everything else
					//if (lectern.getBookSlotItem().getItem().onItemUse(player, world, pos, hand, side, hitX, hitY, hitZ) == EnumActionResult.FAIL) {
					//}
					return true;
				}
			}
		}

		return true;
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState block) {

		TileEntity tileentity = world.getTileEntity(pos);

		if (tileentity instanceof TileSageLectern) {

			TileSageLectern lectern = (TileSageLectern) tileentity;

			// does not drop the result stack!
			for (int i = 0; i < 2; ++i) {
				ItemStack itemstack = lectern.getStackInSlot(i);

				if (!itemstack.isEmpty()) {
					InventoryHelper.spawnItemStack(world, lectern.getPos().getX(), lectern.getPos().getY(), lectern.getPos().getZ(), itemstack);
				}
			}
		}

		super.breakBlock(world, pos, block);
	}
}