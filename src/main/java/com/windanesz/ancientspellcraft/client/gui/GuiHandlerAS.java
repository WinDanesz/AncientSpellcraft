package com.windanesz.ancientspellcraft.client.gui;

import com.windanesz.ancientspellcraft.item.ItemASSpellBook;
import com.windanesz.ancientspellcraft.item.ItemRitualBook;
import com.windanesz.ancientspellcraft.item.ItemRunicPlate;
import com.windanesz.ancientspellcraft.item.ItemSageTome;
import com.windanesz.ancientspellcraft.tileentity.TileArcaneAnvil;
import com.windanesz.ancientspellcraft.tileentity.TileSageLectern;
import com.windanesz.ancientspellcraft.tileentity.TileScribingDesk;
import com.windanesz.ancientspellcraft.tileentity.TileSphereCognizance;
import electroblob.wizardry.item.ItemSpellBook;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
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
	public static final int SCRIBING_DESK = nextGuiId++;
	public static final int RITUAL_BOOK = nextGuiId++;
	public static final int ARCANE_ANVIL = nextGuiId++;
	public static final int SAGE_LECTERN = nextGuiId++;
	public static final int SPELL_GUI_LECTERN = nextGuiId++;
	public static final int RUNIC_PLATE = nextGuiId++;

	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		if (id == ICE_CRAFTING_TABLE) {
			return new ContainerIceWorkbench(player.inventory, world, new BlockPos(x, y, z));
		} else if (id == SPHERE_COGNIZANCE) {
			TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));
			return new ContainerSphereCognizance(player, player.inventory, (TileSphereCognizance) tileEntity);
		} else if (id == SCRIBING_DESK) {
			TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));
			return new ContainerScribingDesk(player, player.inventory, (TileScribingDesk) tileEntity);
		} else if (id == ARCANE_ANVIL) {
			TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));
			return new ContainerArcaneAnvil(player, player.inventory, (TileArcaneAnvil) tileEntity);
		} else if (id == SAGE_LECTERN) {
			TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));
			return new ContainerSageLectern(player, player.inventory, (TileSageLectern) tileEntity);
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		if (id == ICE_CRAFTING_TABLE) {
			return new GuiIceWorkbench(player.inventory, world, new BlockPos(x, y, z));
		}
		if (id == SPELL_BOOK_ANCIENT) {
			if (player.getHeldItemMainhand().getItem() instanceof ItemASSpellBook) {
				return new GuiAncientElementSpellBook(player.getHeldItemMainhand());
			} else if (player.getHeldItemOffhand().getItem() instanceof ItemASSpellBook) {
				return new GuiAncientElementSpellBook(player.getHeldItemOffhand());
			}
		}
		if (id == SPHERE_COGNIZANCE) {
			TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));
			if (tileEntity instanceof TileSphereCognizance) {
				return new GuiSphereCognizance(player, player.inventory,
						(TileSphereCognizance) tileEntity);
			}
		}
		if (id == SCRIBING_DESK) {
			TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));
			if (tileEntity instanceof TileScribingDesk) {
				return new GuiScribingDesk(player, player.inventory,
						(TileScribingDesk) tileEntity);
			}

		}
		if (id == RITUAL_BOOK) {
			if (player.getHeldItemMainhand().getItem() instanceof ItemRitualBook) {
				return new GuiRitualBook(player.getHeldItemMainhand());
			} else if (player.getHeldItemOffhand().getItem() instanceof ItemRitualBook) {
				return new GuiRitualBook(player.getHeldItemOffhand());
			}

		}

		if (id == ARCANE_ANVIL) {
			TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));
			if (tileEntity instanceof TileArcaneAnvil) {
				return new GuiArcaneAnvil(player, player.inventory,
						(TileArcaneAnvil) tileEntity);
			}
		}

		if (id == SAGE_LECTERN) {
			TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));
			if (tileEntity instanceof TileSageLectern) {
				return new GuiSageLectern(player, player.inventory,
						(TileSageLectern) tileEntity);
			}
		}

		if (id == SPELL_GUI_LECTERN) {
			TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));
			if (tileEntity instanceof TileSageLectern) {
				ItemStack book = ((TileSageLectern) tileEntity).getBookSlotItem();

				if (book.getItem() instanceof ItemASSpellBook) {
					return new GuiAncientElementSpellBook(book);
				} else if (book.getItem() instanceof ItemSpellBook) {
					return new electroblob.wizardry.client.gui.GuiSpellBook(book);
				} else if (book.getItem() instanceof ItemRitualBook) {
					return new GuiRitualBook(book);
				} else if (book.getItem() instanceof ItemSageTome) {
					return new GuiSageLectern(player, player.inventory, (TileSageLectern) tileEntity);
				}

			}
		}

		if (id == RUNIC_PLATE) {
			if (player.getHeldItemMainhand().getItem() instanceof ItemRunicPlate) {
				return new GuiRunicPlate(player.getHeldItemMainhand());
			} else if (player.getHeldItemOffhand().getItem() instanceof ItemRunicPlate) {
				return new GuiRunicPlate(player.getHeldItemOffhand());
			}
		}
		return null;
	}
}


