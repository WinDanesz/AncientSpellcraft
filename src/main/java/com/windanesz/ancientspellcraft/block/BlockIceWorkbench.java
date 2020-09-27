package com.windanesz.ancientspellcraft.block;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.client.gui.ContainerIceWorkbench;
import com.windanesz.ancientspellcraft.client.gui.GuiHandlerAS;
import net.minecraft.block.BlockWorkbench;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IInteractionObject;
import net.minecraft.world.World;

import java.util.Random;

public class BlockIceWorkbench extends BlockWorkbench {

	public BlockIceWorkbench() {
		this.setSoundType(SoundType.GLASS);
		this.setHardness(1.0F);
	}

	@Override
	public Material getMaterial(IBlockState state) {
		return Material.PACKED_ICE;
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (world.isRemote) {
			return true;
		} else {
			player.openGui(AncientSpellcraft.instance, GuiHandlerAS.ICE_CRAFTING_TABLE, world, pos.getX(), pos.getY(), pos.getZ());
//			player.addStat(StatList.CRAFTING_TABLE_INTERACTION);
			return true;
		}
	}

	public static class InterfaceCraftingTable implements IInteractionObject {
		private final World world;
		private final BlockPos position;

		public InterfaceCraftingTable(World worldIn, BlockPos pos) {
			this.world = worldIn;
			this.position = pos;
		}

		public String getName() {
			return "crafting_table";
		}

		public boolean hasCustomName() {
			return false;
		}

		public ITextComponent getDisplayName() {
			return new TextComponentTranslation(Blocks.CRAFTING_TABLE.getTranslationKey() + ".name", new Object[0]);
		}

		public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
			return new ContainerIceWorkbench(playerInventory, this.world, this.position);
		}

		public String getGuiID() {
			return "ancientspellcraft:crafting_table";
		}
	}

	@Override
	public int quantityDropped(Random par1Random){
		return 0;
	}

}
