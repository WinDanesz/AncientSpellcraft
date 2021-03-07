package com.windanesz.ancientspellcraft.client.gui;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.item.ItemRelic;
import com.windanesz.ancientspellcraft.packet.ASPacketHandler;
import com.windanesz.ancientspellcraft.packet.PacketControlInput;
import com.windanesz.ancientspellcraft.tileentity.TileSphereCognizance;
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

public class GuiSphereCognizance extends GuiContainer {

	private GuiButton researchButton;

	int currentHintId = 0;
	int currentHintTypeId = 0;

	private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(AncientSpellcraft.MODID, "textures/gui/container/gui_sphere_cognizance.png");

	private String tooltipLangKey = "";

	private int researchCost = 1;

	private TileSphereCognizance tileSphere;
	private IInventory playerInv;

	public GuiSphereCognizance(EntityPlayer player, IInventory playerInv, TileSphereCognizance te) {
		super(new ContainerSphereCognizance(player, playerInv, te));

		this.tileSphere = te;
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
		this.renderHoveredToolTip(mouseX, mouseY);

		Slot crystalSlot = this.inventorySlots.getSlot(ContainerSphereCognizance.CRYSTAL_SLOT);
		Slot bookSlot = this.inventorySlots.getSlot(ContainerSphereCognizance.BOOK_SLOT);

		this.researchButton.enabled = false;
		if (!bookSlot.getHasStack()) { // no book to research
			this.researchButton.enabled = false;
			tooltipLangKey = "no_book";
		} else if (!crystalSlot.getHasStack()) { // no crystal
			tooltipLangKey = "no_crystal";
			this.researchButton.enabled = false;
		} else if (bookSlot.getStack().getItem() instanceof ItemSpellBook || bookSlot.getStack().getItem() instanceof ItemScroll) { // has a book and crystal
			if (tileSphere.getCurrentSpell() != null && tileSphere.getPlayerWizardData() != null) { // spell & player valid
				Spell spell = tileSphere.getCurrentSpell();
				if (!tileSphere.getPlayerWizardData().hasSpellBeenDiscovered(spell)) {
					researchCost = tileSphere.getResearchCost(spell);
					if (crystalSlot.getStack().getCount() >= researchCost) {
						this.researchButton.enabled = true;
						tooltipLangKey = "research";
					} else {
						this.researchButton.enabled = false;
						tooltipLangKey = "too_few_crystals";
					}
				} else {
					this.researchButton.enabled = false;
					tooltipLangKey = "known_item";

				}

			}

		} else if (bookSlot.getStack().getItem() instanceof ItemRelic) {
			if (!ItemRelic.isResearched(bookSlot.getStack())) {
				this.researchButton.enabled = true;
				tooltipLangKey = "research";
			} else {
				this.researchButton.enabled = false;
				tooltipLangKey = "known_book";
			}
		}
		else {
			this.researchButton.enabled = false;
			tooltipLangKey = "no_book";
		}

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
			this.drawCenteredString(this.fontRenderer, text, 87, 70 - offset + i, 16777215);
			i += 10;
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

			//			DrawingUtils.drawTexturedRect(this.x, this.y, 176, 7, this.width, this.height, 256, 256);
			DrawingUtils.drawTexturedRect(this.x, this.y, k, l, this.width, this.height, 256, 256);
		}

		/**
		 * Draw the hovering text of the button based on the current text of {@link GuiSphereCognizance#tooltipLangKey}.
		 * Also displays the researched text...
		 * Called via {@link GuiSphereCognizance#drawGuiContainerForegroundLayer(int, int)} if the button is hovered.
		 */
		@Override
		public void drawButtonForegroundLayer(int mouseX, int mouseY) {
			if (tooltipLangKey.equals("too_few_crystals")) {
				//					drawHoveringText(tooltipLangKey + "_" + requiredCrystalAmount, mouseX, mouseY);
				GuiSphereCognizance.this.drawHoveringText(I18n.format("gui.ancientspellcraft:sphere_cognizance." + tooltipLangKey, researchCost), mouseX, mouseY);
			} else {
				//				drawHoveringText(tooltipLangKey, mouseX, mouseY);
				GuiSphereCognizance.this.drawHoveringText(I18n.format("gui.ancientspellcraft:sphere_cognizance." + tooltipLangKey), mouseX, mouseY);
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
				// Packet building
				IMessage msg = new PacketControlInput.Message(PacketControlInput.ControlType.APPLY_BUTTON);
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


		try {
			this.currentHintTypeId = this.tileSphere.getField(2);
			this.currentHintId = this.tileSphere.getField(3);

			// failed attemp
			if (currentHintTypeId == 1) {
				String unlocalizedText = "gui.ancientspellcraft:sphere_cognizance.hint.failed." + currentHintId;
				drawMultilineHintText(unlocalizedText);
				// discovered spell
			} else if (currentHintTypeId == 2 && this.tileSphere.getCurrentSpell() != null) {
				String unlocalizedText = "gui.ancientspellcraft:sphere_cognizance.hint.discovered." + currentHintId;
				drawMultilineHintText(unlocalizedText, this.tileSphere.getCurrentSpell());

			} else if (currentHintTypeId != 0 && currentHintId != 0) { // every other case
				String hintTypeName = ContainerSphereCognizance.HINT_TYPES.get(currentHintTypeId);
				String unlocalizedText = "gui.ancientspellcraft:sphere_cognizance.hint." + hintTypeName + "." + currentHintId;
				drawMultilineHintText(unlocalizedText);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}

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