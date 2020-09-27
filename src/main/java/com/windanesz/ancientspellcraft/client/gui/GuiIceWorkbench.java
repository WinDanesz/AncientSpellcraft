package com.windanesz.ancientspellcraft.client.gui;

import net.minecraft.client.gui.GuiButtonImage;
import net.minecraft.client.gui.inventory.GuiCrafting;
import net.minecraft.client.gui.recipebook.GuiRecipeBook;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class GuiIceWorkbench extends GuiCrafting {
	private static final ResourceLocation ICE_CRAFTING_TABLE_GUI_TEXTURES = new ResourceLocation("ancientspellcraft:textures/gui/container/ice_crafting_table.png");
	private GuiButtonImage recipeButton;
	private final GuiRecipeBook recipeBookGui;
	private boolean widthTooNarrow;

	public GuiIceWorkbench(InventoryPlayer playerInv, World worldIn) {
		this(playerInv, worldIn, BlockPos.ORIGIN);
	}

	public GuiIceWorkbench(InventoryPlayer playerInv, World world, BlockPos pos) {
		super(playerInv, world, pos);
		this.recipeBookGui = new GuiRecipeBook();
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
	{
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(ICE_CRAFTING_TABLE_GUI_TEXTURES);
		int i = this.guiLeft;
		int j = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
	}
}
