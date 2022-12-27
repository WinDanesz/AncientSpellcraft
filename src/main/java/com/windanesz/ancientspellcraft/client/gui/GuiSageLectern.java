package com.windanesz.ancientspellcraft.client.gui;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.tileentity.TileSageLectern;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiSageLectern extends GuiContainer {
	private static final ResourceLocation SAGE_LECTERN_RESOURCE = new ResourceLocation(AncientSpellcraft.MODID, "textures/gui/container/gui_sage_lectern.png");
	private final TileSageLectern lectern;

	public GuiSageLectern(EntityPlayer player, IInventory playerInv, TileSageLectern te) {
		super(new ContainerSageLectern(player, playerInv, te));
		this.lectern = te;
		this.xSize = 176;
		this.ySize = 241;
	}

	public void onGuiClosed() {
		super.onGuiClosed();
	}

	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		GlStateManager.disableLighting();
		GlStateManager.disableBlend();
		this.fontRenderer.drawString(I18n.format("tile.ancientspellcraft:sage_lectern.name"), 55, 6, 4210752);

		int requiredPages = lectern.getRequiredEnchantedPageCount();
		if (requiredPages > 0) {
			this.fontRenderer.drawString(I18n.format("tile.ancientspellcraft:sage_lectern.required_pages", requiredPages), 19, 50, 4210752);
		}
		GlStateManager.enableLighting();
	}

	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);
		this.renderHoveredToolTip(mouseX, mouseY);
		GlStateManager.disableLighting();
		GlStateManager.disableBlend();
	}

	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(SAGE_LECTERN_RESOURCE);
		int i = (this.width - this.xSize) / 2;
		int j = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
//		this.drawTexturedModalRect(i + 59, j + 20, 0, this.ySize + (this.anvil.getSlot(0).getHasStack() ? 0 : 16), 110, 16);

//		if ((this.anvil.getStackInSlot(0).isEmpty() || this.anvil.getStackInSlot(1).isEmpty()) && !this.anvil.getSlot(2).getHasStack()) {
//			this.drawTexturedModalRect(i + 99, j + 45, this.xSize, 0, 28, 21);
//		}
	}
}
