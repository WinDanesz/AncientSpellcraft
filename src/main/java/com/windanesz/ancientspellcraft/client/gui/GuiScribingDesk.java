package com.windanesz.ancientspellcraft.client.gui;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.item.ItemRelic;
import com.windanesz.ancientspellcraft.packet.ASPacketHandler;
import com.windanesz.ancientspellcraft.packet.PacketControlInput;
import com.windanesz.ancientspellcraft.tileentity.TileScribingDesk;
import electroblob.wizardry.Wizardry;
import electroblob.wizardry.client.DrawingUtils;
import electroblob.wizardry.registry.WizardrySounds;
import electroblob.wizardry.spell.Spell;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
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

	int currentHintId = 0;
	int currentHintTypeId = 0;

	private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(AncientSpellcraft.MODID, "textures/gui/container/gui_scribing_desk.png");

	//	private boolean drawprogress = true;

	//	private int requiredCrystalAmount = 0;

	private String tooltipLangKey = "";

	private int researchCost = 1;

	private TileScribingDesk tileSphere;
	private IInventory playerInv;

	public GuiScribingDesk(EntityPlayer player, IInventory playerInv, TileScribingDesk te) {
		super(new ContainerScribingDesk(player, playerInv, te));

		this.tileSphere = te;
		this.playerInv = playerInv;

		this.xSize = 176;
		this.ySize = 185;
	}

	@Override
	public void initGui() {
		super.initGui();

		Keyboard.enableRepeatEvents(true);
		this.buttonList.clear();
		this.buttonList.add(this.researchButton = new GuiButtonApply(0, this.width / 2 + 53, this.height / 2 + -30));
		this.researchButton.enabled = false;
		//		this.buttonDone = this.addButton(new GuiButton(0, this.width / 2 -30, this.height / 2 - 20 , 60, 20, I18n.format("gui.ancientspellcraft:research")));

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

		//		if (stack != null && this.mc.currentScreen != null)
		//		{
		//			this.mc.currentScreen.drawHoveringText(this.mc.currentScreen.getItemToolTip(stack), 50, 50);
		//		}

		Slot crystalSlot = this.inventorySlots.getSlot(ContainerScribingDesk.CRYSTAL_SLOT);
		Slot bookSlot = this.inventorySlots.getSlot(ContainerScribingDesk.BOOK_SLOT);

		//		System.out.println("crystalSlot info: " + (crystalSlot.getHasStack() ? crystalSlot.getStack().getItem() : "no item"));
		//		System.out.println("bookSlot info: " + (bookSlot.getHasStack() ? bookSlot.getStack().getItem() : "no item"));

		if (this.tileSphere.getField(2) == 1) {
			this.researchButton.enabled = true;
		} else {
			this.researchButton.enabled = false;
		}

		int ready = this.tileSphere.getField(2); // researchDuration


		//		this.researchButton.enabled = false;
//		if (!bookSlot.getHasStack()) { // no book to research
//			this.researchButton.enabled = false;
//			tooltipLangKey = "no_book";
//		} else if (!crystalSlot.getHasStack()) { // no crystal
//			tooltipLangKey = "no_crystal";
//			this.researchButton.enabled = false;
//		} else if (bookSlot.getStack().getItem() instanceof ItemSpellBook || bookSlot.getStack().getItem() instanceof ItemScroll) { // has a book and crystal
//			if (tileSphere.getCurrentSpell() != null && tileSphere.getPlayerWizardData() != null) { // spell & player valid
//				Spell spell = tileSphere.getCurrentSpell();
//				if (!tileSphere.getPlayerWizardData().hasSpellBeenDiscovered(spell)) {
//					researchCost = tileSphere.getResearchCost(spell);
//					if (crystalSlot.getStack().getCount() >= researchCost) {
//						this.researchButton.enabled = true;
//						tooltipLangKey = "research";
//					} else {
//						this.researchButton.enabled = false;
//						tooltipLangKey = "too_few_crystals";
//					}
//				} else {
//					this.researchButton.enabled = false;
//					tooltipLangKey = "known_book";
//
//				}
//
//			}
//			//			bookSlot.getStack()
//
//		} else if (bookSlot.getStack().getItem() instanceof ItemRelic) {
//			if (!ItemRelic.isResearched(bookSlot.getStack())) {
//				this.researchButton.enabled = true;
//				tooltipLangKey = "research";
//			} else {
//				this.researchButton.enabled = false;
//				tooltipLangKey = "known_book";
//			}
//		} else {
//			this.researchButton.enabled = false;
//			tooltipLangKey = "no_book";
//		}

		//		this.researchButton.x = this.width / 2 + 64; /// TODO CHECKKKK!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		//		this.renderHoveredToolTip(mouseX, mouseY);
		this.renderHoveredToolTip(mouseX, mouseY);

	}

	private void drawMultilineHintText(String unlocalizedText) {
		drawMultilineHintText(unlocalizedText, null);
	}

	private void drawMultilineHintText(String unlocalizedText, Spell spell) {
		List<String> localizedTextList;
		if (spell == null) {
			localizedTextList = this.fontRenderer.listFormattedStringToWidth(I18n.format(unlocalizedText), 104);
		} else {
			localizedTextList = this.fontRenderer.listFormattedStringToWidth(I18n.format(unlocalizedText, spell.getDisplayName()), 104);
		}
		//				text.addAll(this.fontRenderer.listFormattedStringToWidth(description, TOOLTIP_WRAP_WIDTH));
		int lineCount = localizedTextList.size();
		int offset = (lineCount - 1) * 7;
		int i = 0;
		for (String text : localizedTextList) {
			//			this.drawCenteredString(this.fontRenderer, text, this.guiLeft + 87, this.guiTop + 70 - offset + i, 16777215);
			this.drawCenteredString(this.fontRenderer, text, 87, 70 - offset + i, 16777215);
			//			fontRenderer.drawString(text, (float) (this.guiLeft + 87 - fontRenderer.getStringWidth(text) / 2), (float) (this.guiTop + 70 - offset + i), 14737632, true);
			i += 10;
		}

	}

	public void drawIngredientPreviews(int mouseX, int mouseY, float partialTicks) {

		List<ItemStack> componentList = ItemRelic.getSpellComponentItems(this.inventorySlots.getSlot(ContainerScribingDesk.RELIC_SLOT).getStack());
		if (componentList != null && !componentList.isEmpty()) {
			for (int i = 0; i < componentList.size(); i++) {
				if (!this.inventorySlots.getSlot(i).getHasStack()) {
					ItemStack stack = componentList.get(i);
					RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
					renderItem.renderItemAndEffectIntoGUI(this.mc.player, stack, width / 2 - 34 + (26 * i), height / 2 - 32);

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

		/// progress bar
		//		if (drawprogress) {

		int scale = this.getResearchProgressScaled(76);
		//			this.drawTexturedModalRect(i + 79, j + 34, 176, 14, scale + 1, 16);

		//			this.drawTexturedModalRect(this.guiLeft + 51, this.guiTop + 106, 171, 0, scale + 1, 5);
		this.drawTexturedModalRect(this.guiLeft + 51, this.guiTop + 106, 176, 0, scale, 5);
		//		this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
		//		}

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

			//			DrawingUtils.drawTexturedRect(this.x, this.y, 176, 7, this.width, this.height, 256, 256);
			DrawingUtils.drawTexturedRect(this.x, this.y, k, l, this.width, this.height, 256, 256);
			//			if (this.hovered) {
			//				if (tooltipLangKey.equals("too_few_crystals")) {
			////					drawHoveringText(tooltipLangKey + "_" + requiredCrystalAmount, mouseX, mouseY);
			//					drawHoveringText(I18n.format("gui.ancientspellcraft:sphere_cognizance." + tooltipLangKey, researchCost), mouseX, mouseY);
			//				} else {
			////				drawHoveringText(tooltipLangKey, mouseX, mouseY);
			//				drawHoveringText(I18n.format("gui.ancientspellcraft:sphere_cognizance." + tooltipLangKey), mouseX, mouseY);
			//					}
			//			}
			//this.drawCenteredString(minecraft.fontRenderer, this.displayString, this.x + this.width / 2,
			//		this.y + (this.height - 8) / 2, colour);
		}

		/**
		 * Draw the hovering text of the button based on the current text of {@link GuiScribingDesk#tooltipLangKey}.
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
				// Animation
				//				}

			}
		}

	}

	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of the items. In our case, the button labels should be drawn over the items.
	 */
	@Override
	@SuppressWarnings("Duplicates")
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {

//		try {
//			this.currentHintTypeId = this.tileSphere.getField(2);
//			this.currentHintId = this.tileSphere.getField(3);
//
//			// failed attemp
//			if (currentHintTypeId == 1) {
//				String unlocalizedText = "gui.ancientspellcraft:sphere_cognizance.hint.failed." + currentHintId;
//				drawMultilineHintText(unlocalizedText);
//				// discovered spell
//			} else if (currentHintTypeId == 2 && this.tileSphere.getCurrentSpell() != null) {
//				String unlocalizedText = "gui.ancientspellcraft:sphere_cognizance.hint.discovered." + currentHintId;
//				drawMultilineHintText(unlocalizedText, this.tileSphere.getCurrentSpell());
//
//			} else if (currentHintTypeId != 0 && currentHintId != 0) { // every other case
//				//				System.out.println("currentHintTypeId: " + currentHintTypeId);
//				//				System.out.println("currentHintId: " + currentHintId);
//				String hintTypeName = ContainerScribingDesk.HINT_TYPES.get(currentHintTypeId);
//				String unlocalizedText = "gui.ancientspellcraft:sphere_cognizance.hint." + hintTypeName + "." + currentHintId;
//				drawMultilineHintText(unlocalizedText);
//			}
//		}
//		catch (Exception e) {
//			e.printStackTrace();
//		}

		for (GuiButton guibutton : this.buttonList) {
			if (guibutton.isMouseOver()) {
				guibutton.drawButtonForegroundLayer(mouseX - this.guiLeft, mouseY - this.guiTop);
				break;
			}
		}
		//		System.out.println("currentHintTypeId: " + currentHintTypeId);
		//		System.out.println("currentHintId: " + currentHintId);
		//		this.currentHintTypeId = this.tileSphere.getField(2); // researchProgress
		//		this.currentHintId = this.tileSphere.getField(3); // researchDuration

		//		this.currentHintTypeId = this.container
		////		this.currentHintId  =

		//				this.fontRenderer.drawString(I18n.format("gui.ancientspellcraft:sphere_cognizance.hint.0"), 20, 20, 4210752);      //#404040
		//				String s = tileSphere.getDisplayName().getUnformattedText();
		//				this.fontRenderer.drawString(s, 88 - this.fontRenderer.getStringWidth(s) / 2, 6, 4210752);            //#404040
		//		this.fontRenderer.drawString(this.playerInv.getDisplayName().getUnformattedText(), 8, 72, 4210752);      //#404040
	}

	@SubscribeEvent
	public static void onRenderGameOverLayPost(RenderGameOverlayEvent.Post event) {

	}

	private void buttonClicked() {
		this.tileSphere.setField(2, 1);
	}

	private int getResearchProgressScaled(int pixels) {
		int researchProgress = this.tileSphere.getField(0); // researchProgress
		int researchDuration = this.tileSphere.getField(1); // researchDuration

		//		int researchDuration2 = this.tileSphere.getField(2); // researchDuration

		//		researchDuration = 100;
		//		System.out.println("researchProgress: " + researchProgress );
		//		System.out.println("researchDuration : " + researchDuration  );
		//		System.out.println("researchDuration2 : " + researchDuration2);
		return researchDuration != 0 && researchProgress != 0 ? researchProgress * 76 / researchDuration : 0;
	}

}