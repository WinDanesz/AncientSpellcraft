package com.windanesz.ancientspellcraft.client.gui;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.item.ItemRelic;
import com.windanesz.ancientspellcraft.packet.ASPacketHandler;
import com.windanesz.ancientspellcraft.packet.PacketControlInput;
import com.windanesz.ancientspellcraft.tileentity.TileScribingDesk;
import electroblob.wizardry.Wizardry;
import electroblob.wizardry.client.DrawingUtils;
import electroblob.wizardry.registry.WizardrySounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import org.lwjgl.input.Keyboard;

import java.io.IOException;
import java.util.List;

public class GuiScribingDesk extends GuiContainer {

	private GuiButton researchButton;
	private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(AncientSpellcraft.MODID, "textures/gui/container/gui_scribing_desk.png");

	private TileScribingDesk tileSphere;

	public GuiScribingDesk(EntityPlayer player, IInventory playerInv, TileScribingDesk te) {
		super(new ContainerScribingDesk(player, playerInv, te));

		this.tileSphere = te;

		this.xSize = 176;
		this.ySize = 161;
	}

	@Override
	public void initGui() {
		super.initGui();

		Keyboard.enableRepeatEvents(true);
		this.buttonList.clear();
		this.buttonList.add(this.researchButton = new GuiButtonApply(0, this.width / 2 + 53, this.height / 2 + -30));
		this.researchButton.enabled = false;
	}

	@Override
	public void updateScreen() {
		super.updateScreen();

	}

	/**
	 * Draws the screen and all the components in it.
	 */
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);
		drawIngredientPreviews(mouseX, mouseY, partialTicks);

		this.researchButton.enabled = this.tileSphere.getField(2) == 1;

		this.renderHoveredToolTip(mouseX, mouseY);

	}

	@Override
	protected void renderHoveredToolTip(int mouseX, int mouseY) {

		// 0 = left slot
		// 1 = middle slot
		// 2 = right slot
		// 4 = stone tablet slot

		int slotIndex = getSlotUnderMouse() != null ? getSlotUnderMouse().slotNumber : -1;

		if (slotIndex == ContainerScribingDesk.INGREDIENT_1_SLOT || slotIndex == ContainerScribingDesk.INGREDIENT_2_SLOT || slotIndex == ContainerScribingDesk.INGREDIENT_3_SLOT) {

			if (this.mc.player.inventory.getItemStack().isEmpty() && getSlotUnderMouse() != null) {
				Slot slot = this.inventorySlots.getSlot(ContainerScribingDesk.RELIC_SLOT);

				boolean hasSpell = slot != null && slot.getHasStack() && slot.getStack().getItem() instanceof ItemRelic && ItemRelic.getRelicType(slot.getStack()) == ItemRelic.RelicType.SPELL;
				List<ItemStack> componentList = ItemRelic.getSpellComponentItems(this.inventorySlots.getSlot(ContainerScribingDesk.RELIC_SLOT).getStack());

				if (hasSpell && componentList != null) {

					if (!componentList.isEmpty() && getSlotUnderMouse().slotNumber == ContainerScribingDesk.INGREDIENT_1_SLOT) {
						if (!componentList.get(0).isEmpty()) {
							this.renderToolTip(componentList.get(0), mouseX, mouseY);
						}

					} else if (componentList.size() >= 2 && getSlotUnderMouse().slotNumber == ContainerScribingDesk.INGREDIENT_2_SLOT) {
						if (!componentList.get(1).isEmpty()) {
							this.renderToolTip(componentList.get(1), mouseX, mouseY);
						}
					} else if (componentList.size() >= 3 && getSlotUnderMouse().slotNumber == ContainerScribingDesk.INGREDIENT_3_SLOT) {
						if (!componentList.get(2).isEmpty()) {
							this.renderToolTip(componentList.get(2), mouseX, mouseY);
						}
					}
				} else if (!hasSpell) {
					GuiScribingDesk.this.drawHoveringText(I18n.format("Spell Component Slot"), mouseX, mouseY);
				}
			}
		}

		if (slotIndex == ContainerScribingDesk.RELIC_SLOT) {
			Slot slot = this.inventorySlots.getSlot(ContainerScribingDesk.RELIC_SLOT);
			if (!slot.getHasStack()) {
				GuiScribingDesk.this.drawHoveringText(I18n.format("Spell Stone Tablet Slot"), mouseX, mouseY);
			}
		}
		if (slotIndex == ContainerScribingDesk.BOOK_SLOT) {
			Slot slot = this.inventorySlots.getSlot(ContainerScribingDesk.BOOK_SLOT);
			if (!slot.getHasStack()) {
				GuiScribingDesk.this.drawHoveringText(I18n.format("Empty Book Slot"), mouseX, mouseY);
			}
		}
		if (slotIndex == ContainerScribingDesk.INK_SLOT) {
			Slot slot = this.inventorySlots.getSlot(ContainerScribingDesk.INK_SLOT);
			if (!slot.getHasStack()) {
				GuiScribingDesk.this.drawHoveringText(I18n.format("Ink Slot"), mouseX, mouseY);
			}
		}
		if (slotIndex == ContainerScribingDesk.CRYSTAL_SLOT) {
			Slot slot = this.inventorySlots.getSlot(ContainerScribingDesk.CRYSTAL_SLOT);
			if (!slot.getHasStack()) {
				GuiScribingDesk.this.drawHoveringText(I18n.format("Magic Crystal Slot"), mouseX, mouseY);
			}
		} else {
			super.renderHoveredToolTip(mouseX, mouseY);
		}
	}

	public void drawIngredientPreviews(int mouseX, int mouseY, float partialTicks) {

		List<ItemStack> componentList = ItemRelic.getSpellComponentItems(this.inventorySlots.getSlot(ContainerScribingDesk.RELIC_SLOT).getStack());
		if (componentList != null && !componentList.isEmpty()) {
			for (int i = 0; i < componentList.size(); i++) {
				ItemStack stack = componentList.get(i);

				if (!this.inventorySlots.getSlot(i).getHasStack()) {
					RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
					renderItem.renderItemAndEffectIntoGUI(this.mc.player, stack, width / 2 - 34 + (26 * i), height / 2 - 32);
					Gui.drawRect(width / 2 - 34 + (26 * i), height / 2 - 32, width / 2 - 34 + (26 * i) + 16, height / 2 - 32 + 16, 822018048);
				}

				if (this.inventorySlots.getSlot(i).getHasStack()) {
					if (stack != null && !stack.isEmpty() && !ItemStack.areItemStacksEqual(this.inventorySlots.getSlot(i).getStack(), stack)) {
						Gui.drawRect(width / 2 - 34 + (26 * i), height / 2 - 32, width / 2 - 34 + (26 * i) + 16, height / 2 - 32 + 16, 822018048);
					} else if (stack != null && !stack.isEmpty() && ItemStack.areItemsEqualIgnoreDurability(this.inventorySlots.getSlot(i).getStack(), stack)) {
						Gui.drawRect(width / 2 - 34 + (26 * i), height / 2 - 32, width / 2 - 34 + (26 * i) + 16, height / 2 - 32 + 16, 820037332);
					}
				}
			}
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		/// background
		GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
		this.mc.getTextureManager().bindTexture(GUI_TEXTURE);
		this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
		int scale = this.getResearchProgressScaled(76);
		this.drawTexturedModalRect(this.guiLeft + 51, this.guiTop + 106, 176, 0, scale, 5);

	}

	private class GuiButtonApply extends GuiButton {

		public GuiButtonApply(int id, int x, int y) {
			super(id, x, y, 16, 16, I18n.format("container." + Wizardry.MODID + ":arcane_workbench.apply"));
		}

		@Override
		public void drawButton(Minecraft minecraft, int mouseX, int mouseY, float partialTicks) {

			// Whether the button is highlighted
			this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;

			int k = 176;
			int l = 7;
			//int colour = 14737632;
			boolean t = false;
			if (this.enabled) {
				if (this.hovered) {
					t = true;
					k += this.width * 2;
					//colour = 16777120;
				}
			} else {
				k += this.width;
				//colour = 10526880;
			}

			DrawingUtils.drawTexturedRect(this.x, this.y, k, l, this.width, this.height, 256, 256);
		}

		/**
		 * Draw the hovering text of the button based on the current text of GuiScribingDesk#tooltipLangKey.
		 * Also displays the researched text...
		 * Called via {@link GuiScribingDesk#drawGuiContainerForegroundLayer(int, int)} if the button is hovered.
		 */
		@Override
		public void drawButtonForegroundLayer(int mouseX, int mouseY) {
			if (buttonList.get(0).enabled) {
				//					drawHoveringText(tooltipLangKey + "_" + requiredCrystalAmount, mouseX, mouseY);
				GuiScribingDesk.this.drawHoveringText(I18n.format("tile.ancientspellcraft:scribing_desk.button.write"), mouseX, mouseY);
			} else {
				//				drawHoveringText(tooltipLangKey, mouseX, mouseY);
				GuiScribingDesk.this.drawHoveringText(I18n.format("tile.ancientspellcraft:scribing_desk.button.missing"), mouseX, mouseY);
			}
		}
	}

	/**
	 * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
	 */
	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		super.actionPerformed(button);

		if (button.id == 0) { // researchButton
			if (button.enabled) {
				// Packet building
				IMessage msg = new PacketControlInput.Message(PacketControlInput.ControlType.CRAFT_SPELL);
				ASPacketHandler.net.sendToServer(msg);
				// Sound
				Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(
						WizardrySounds.BLOCK_ARCANE_WORKBENCH_SPELLBIND, 1));
			}
		}

	}

	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of the items. In our case, the button labels should be drawn over the items.
	 */
	@Override
	@SuppressWarnings("Duplicates")
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {

		for (GuiButton guibutton : this.buttonList) {
			if (guibutton.isMouseOver()) {
				guibutton.drawButtonForegroundLayer(mouseX - this.guiLeft, mouseY - this.guiTop);
				break;
			}
		}
	}

	@SubscribeEvent
	public static void onRenderGameOverLayPost(RenderGameOverlayEvent.Post event) {

	}

	private int getResearchProgressScaled(int pixels) {
		int researchProgress = this.tileSphere.getField(0); // researchProgress
		int researchDuration = this.tileSphere.getField(1); // researchDuration

		return researchDuration != 0 && researchProgress != 0 ? researchProgress * 76 / researchDuration : 0;
	}

}