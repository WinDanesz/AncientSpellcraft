package com.windanesz.ancientspellcraft.client.gui;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.packet.ASPacketHandler;
import com.windanesz.ancientspellcraft.packet.PacketControlInput;
import com.windanesz.ancientspellcraft.tileentity.TileCrystalBallCognizance;
import electroblob.wizardry.Wizardry;
import electroblob.wizardry.client.DrawingUtils;
import electroblob.wizardry.item.ItemScroll;
import electroblob.wizardry.item.ItemSpellBook;
import electroblob.wizardry.registry.WizardrySounds;
import electroblob.wizardry.spell.Spell;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import org.lwjgl.input.Keyboard;

import java.io.IOException;
import java.util.List;

public class GuiCrystalBallCognizance extends GuiContainer {

	private GuiButton researchButton;

	int currentHintId = 0;
	int currentHintTypeId = 0;

	private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(AncientSpellcraft.MODID, "textures/gui/container/gui_sphere_cognizance.png");

	//	private boolean drawprogress = true;

	//	private int requiredCrystalAmount = 0;

	private String tooltipLangKey = "";

	private int researchCost = 1;

	private TileCrystalBallCognizance tileCrystalBall;
	private IInventory playerInv;

	public GuiCrystalBallCognizance(EntityPlayer player, IInventory playerInv, TileCrystalBallCognizance te) {
		super(new ContainerCrystalBallCognizance(player, playerInv, te));

		this.tileCrystalBall = te;
		this.playerInv = playerInv;

		this.xSize = 176;
		this.ySize = 241;
	}

	@Override
	public void initGui() {
		super.initGui();

		Keyboard.enableRepeatEvents(true);
		this.buttonList.clear();
		this.buttonList.add(this.researchButton = new GuiButtonApply(0, this.width / 2 + 57, this.height / 2 + 6));
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
		this.renderHoveredToolTip(mouseX, mouseY);

		Slot crystalSlot = this.inventorySlots.getSlot(ContainerCrystalBallCognizance.CRYSTAL_SLOT);
		Slot bookSlot = this.inventorySlots.getSlot(ContainerCrystalBallCognizance.BOOK_SLOT);

		//		System.out.println("crystalSlot info: " + (crystalSlot.getHasStack() ? crystalSlot.getStack().getItem() : "no item"));
		//		System.out.println("bookSlot info: " + (bookSlot.getHasStack() ? bookSlot.getStack().getItem() : "no item"));

		this.researchButton.enabled = false;
		if (!bookSlot.getHasStack()) { // no book to research
			this.researchButton.enabled = false;
			tooltipLangKey = "no_book";
		} else if (!crystalSlot.getHasStack()) { // no crystal
			tooltipLangKey = "no_crystal";
			this.researchButton.enabled = false;
		} else if (bookSlot.getStack().getItem() instanceof ItemSpellBook || bookSlot.getStack().getItem() instanceof ItemScroll) { // has a book and crystal
			if (tileCrystalBall.getCurrentSpell() != null && tileCrystalBall.getPlayerWizardData() != null) { // spell & player valid
				Spell spell = tileCrystalBall.getCurrentSpell();
				if (!tileCrystalBall.getPlayerWizardData().hasSpellBeenDiscovered(spell)) {
					researchCost = tileCrystalBall.getResearchCost(spell);
					if (crystalSlot.getStack().getCount() >= researchCost) {
						this.researchButton.enabled = true;
						tooltipLangKey = "research";
					} else {
						this.researchButton.enabled = false;
						tooltipLangKey = "too_few_crystals";
					}
				} else {
					this.researchButton.enabled = false;
					tooltipLangKey = "known_book";

				}

			}
			//			bookSlot.getStack()

		} else {
			this.researchButton.enabled = false;
			tooltipLangKey = "no_book";
		}

		//		this.researchButton.x = this.width / 2 + 64; /// TODO CHECKKKK!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

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
			//					drawHoveringText(I18n.format("gui.ancientspellcraft:crystal_ball_cognizance." + tooltipLangKey, researchCost), mouseX, mouseY);
			//				} else {
			////				drawHoveringText(tooltipLangKey, mouseX, mouseY);
			//				drawHoveringText(I18n.format("gui.ancientspellcraft:crystal_ball_cognizance." + tooltipLangKey), mouseX, mouseY);
			//					}
			//			}
			//this.drawCenteredString(minecraft.fontRenderer, this.displayString, this.x + this.width / 2,
			//		this.y + (this.height - 8) / 2, colour);
		}

		/**
		 * Draw the hovering text of the button based on the current text of {@link GuiCrystalBallCognizance#tooltipLangKey}.
		 * Also displays the researched text...
		 * Called via {@link com.windanesz.ancientspellcraft.client.gui.GuiCrystalBallCognizance#drawGuiContainerForegroundLayer(int, int)} if the button is hovered.
		 */
		@Override
		public void drawButtonForegroundLayer(int mouseX, int mouseY) {
			if (tooltipLangKey.equals("too_few_crystals")) {
				//					drawHoveringText(tooltipLangKey + "_" + requiredCrystalAmount, mouseX, mouseY);
				GuiCrystalBallCognizance.this.drawHoveringText(I18n.format("gui.ancientspellcraft:crystal_ball_cognizance." + tooltipLangKey, researchCost), mouseX, mouseY);
			} else {
				//				drawHoveringText(tooltipLangKey, mouseX, mouseY);
				GuiCrystalBallCognizance.this.drawHoveringText(I18n.format("gui.ancientspellcraft:crystal_ball_cognizance." + tooltipLangKey), mouseX, mouseY);
			}
		}
	}

	/**
	 * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
	 */
	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		super.actionPerformed(button);

		//herevere sere

		if (button.id == 0) { // researchButton
			if (button.enabled) {
				//				System.out.println("button pressed");
				// Packet building
				//				IMessage msg = new PacketControlInput.Message(PacketControlInput.ControlType.APPLY_BUTTON);
				//				WizardryPacketHandler.net.sendToServer(msg);
				//				 Animationk
				//				animationTimer = 20;
				//				this.tileCrystalBall.setField(2, 1);

				//				if (button.id == 0) {
				// Packet building
				IMessage msg = new PacketControlInput.Message(PacketControlInput.ControlType.APPLY_BUTTON);
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
		for (GuiButton guibutton : this.buttonList) {
			if (guibutton.isMouseOver()) {
				guibutton.drawButtonForegroundLayer(mouseX - this.guiLeft, mouseY - this.guiTop);
				break;
			}
		}

		try {
			this.currentHintTypeId = this.tileCrystalBall.getField(2);
			this.currentHintId = this.tileCrystalBall.getField(3);

			// failed attemp
			if (currentHintTypeId == 1) {
				String unlocalizedText = "gui.ancientspellcraft:crystal_ball_cognizance.hint.failed." + currentHintId;
				drawMultilineHintText(unlocalizedText);
				// discovered spell
			} else if (currentHintTypeId == 2 && this.tileCrystalBall.getCurrentSpell() != null) {
				String unlocalizedText = "gui.ancientspellcraft:crystal_ball_cognizance.hint.discovered." + currentHintId;
				drawMultilineHintText(unlocalizedText, this.tileCrystalBall.getCurrentSpell());

			} else if (currentHintTypeId != 0 && currentHintId != 0) { // every other case
				//				System.out.println("currentHintTypeId: " + currentHintTypeId);
				//				System.out.println("currentHintId: " + currentHintId);
				String hintTypeName = ContainerCrystalBallCognizance.HINT_TYPES.get(currentHintTypeId);
				String unlocalizedText = "gui.ancientspellcraft:crystal_ball_cognizance.hint." + hintTypeName + "." + currentHintId;
				drawMultilineHintText(unlocalizedText);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		//		System.out.println("currentHintTypeId: " + currentHintTypeId);
		//		System.out.println("currentHintId: " + currentHintId);
		//		this.currentHintTypeId = this.tileCrystalBall.getField(2); // researchProgress
		//		this.currentHintId = this.tileCrystalBall.getField(3); // researchDuration

		//		this.currentHintTypeId = this.container
		////		this.currentHintId  =

		//				this.fontRenderer.drawString(I18n.format("gui.ancientspellcraft:crystal_ball_cognizance.hint.0"), 20, 20, 4210752);      //#404040
		//				String s = tileCrystalBall.getDisplayName().getUnformattedText();
		//				this.fontRenderer.drawString(s, 88 - this.fontRenderer.getStringWidth(s) / 2, 6, 4210752);            //#404040
		//		this.fontRenderer.drawString(this.playerInv.getDisplayName().getUnformattedText(), 8, 72, 4210752);      //#404040
	}

	@SubscribeEvent
	public static void onRenderGameOverLayPost(RenderGameOverlayEvent.Post event) {

	}

	private void buttonClicked() {
		this.tileCrystalBall.setField(2, 1);
	}

	private int getResearchProgressScaled(int pixels) {
		int researchProgress = this.tileCrystalBall.getField(0); // researchProgress
		int researchDuration = this.tileCrystalBall.getField(1); // researchDuration

		//		int researchDuration2 = this.tileCrystalBall.getField(2); // researchDuration

		//		researchDuration = 100;
		//		System.out.println("researchProgress: " + researchProgress );
		//		System.out.println("researchDuration : " + researchDuration  );
		//		System.out.println("researchDuration2 : " + researchDuration2);
		return researchDuration != 0 && researchProgress != 0 ? researchProgress * 76 / researchDuration : 0;
	}

}