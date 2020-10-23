package com.windanesz.ancientspellcraft.client.gui;

import com.windanesz.ancientspellcraft.item.ItemAncientSpellcraftSpellBook;
import com.windanesz.ancientspellcraft.tileentity.TileSphereCognizance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiHandlerAS implements IGuiHandler {

	/**
	 * Incrementable index for the gui ID
	 */
	private static int nextGuiId = 0;

	public static final int ICE_CRAFTING_TABLE = nextGuiId++;
	public static final int SPELL_BOOK_ANCIENT = nextGuiId++;
	public static final int SPHERE_COGNIZANCE = nextGuiId++;

	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		if (id == ICE_CRAFTING_TABLE) {
			return new ContainerIceWorkbench(player.inventory, world, new BlockPos(x, y, z));
		} else if (id == SPHERE_COGNIZANCE) {
			TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));
			return new ContainerSphereCognizance(player, player.inventory, (TileSphereCognizance) tileEntity);
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		if (id == ICE_CRAFTING_TABLE) {
			return new GuiIceWorkbench(player.inventory, world, new BlockPos(x, y, z));
		} else if (id == SPELL_BOOK_ANCIENT) {
			if (player.getHeldItemMainhand().getItem() instanceof ItemAncientSpellcraftSpellBook) {
				return new GuiAncientSpellBook(player.getHeldItemMainhand());
			} else if (player.getHeldItemOffhand().getItem() instanceof ItemAncientSpellcraftSpellBook) {
				return new GuiAncientSpellBook(player.getHeldItemOffhand());
			}
		}
		if (id == SPHERE_COGNIZANCE) {
			TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));
			if (tileEntity instanceof TileSphereCognizance) {
				return new GuiSphereCognizance(player, player.inventory,
						(TileSphereCognizance) tileEntity);
			}

		}
		return null;
	}
}


