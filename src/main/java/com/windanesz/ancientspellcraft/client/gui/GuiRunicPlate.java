package com.windanesz.ancientspellcraft.client.gui;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.item.ItemRunicPlate;
import com.windanesz.ancientspellcraft.spell.Runeword;
import electroblob.wizardry.Wizardry;
import electroblob.wizardry.client.DrawingUtils;
import electroblob.wizardry.constants.Tier;
import electroblob.wizardry.data.SpellGlyphData;
import electroblob.wizardry.spell.Spell;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class GuiRunicPlate extends GuiScreen {

	protected static final String TRANSLATION_KEY_PREFIX = "gui." + Wizardry.MODID + ":spell_book";

	protected final int xSize, ySize;
	private ItemRunicPlate plate;
	private Spell spell;

	protected int textureWidth = 512;
	protected int textureHeight = 256;

	public GuiRunicPlate(ItemStack stack) {
		super();
		this.xSize = 150;
		this.ySize = 180;
		if (!(stack.getItem() instanceof ItemRunicPlate)) {
			throw new ClassCastException("Cannot create runic plate GUI for item that does not extend ItemRunicPlate!");
		}
		this.plate = (ItemRunicPlate) stack.getItem();
		this.spell = (Spell.byMetadata(stack.getItemDamage()));
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks){
		int left = this.width/2 - xSize/2;
		int top = this.height/2 - this.ySize/2;
		this.drawDefaultBackground();
		this.drawBackgroundLayer(left, top, mouseX, mouseY);
		super.drawScreen(mouseX, mouseY, partialTicks); // Just draws the buttons
		this.drawForegroundLayer(left, top, mouseX, mouseY);
	}

	protected void drawForegroundLayer(int left, int top, int mouseX, int mouseY) {

		boolean discovered = Wizardry.proxy.shouldDisplayDiscovered(getSpell(), null);

		if (discovered) {
			this.fontRenderer.drawString(getSpell().getDisplayName(), left + 24, top + 30, 0x3c2c44);

			if (getSpell().hasProperty(Runeword.CHARGES)) {
				int charges = getSpell().getProperty(Runeword.CHARGES).intValue();
				String chargesString = I18n.format("gui.ancientspellcraft:runic_plate.charges", charges);
				this.fontRenderer.drawString(chargesString, left + 24, top + 57, 0x3c2c44);
			} else if (((Runeword) getSpell()).isPassive()) {
				this.fontRenderer.drawString(I18n.format("gui.ancientspellcraft:runic_plate.passive"), left + 24, top + 57, 0x3c2c44);
			} else {}

		} else {
			this.mc.standardGalacticFontRenderer.drawString(SpellGlyphData.getGlyphName(getSpell(), mc.world), left + 24, top + 30, 0);
			this.mc.standardGalacticFontRenderer.drawString(getSpell().getType().getDisplayName(), left + 24, top + 26, 0x3c2c44);
		}


		// Novice is usually white but this doesn't show up
		String tier = I18n.format(TRANSLATION_KEY_PREFIX + ".tier", getSpell().getTier() == Tier.NOVICE ?
				"\u00A77" + getSpell().getTier().getDisplayName() : getSpell().getTier().getDisplayNameWithFormatting());
		this.fontRenderer.drawString(tier, left + 24, top + 45, 0x3c2c44);

		String manaCost = I18n.format(TRANSLATION_KEY_PREFIX + ".mana_cost", getSpell().getCost());

		if (getSpell().isContinuous) { manaCost = I18n.format(TRANSLATION_KEY_PREFIX + ".mana_cost_continuous", getSpell().getCost()); }
		if (!discovered) { manaCost = I18n.format(TRANSLATION_KEY_PREFIX + ".mana_cost_undiscovered"); }
		this.fontRenderer.drawString(manaCost, left + 24, top + 69, 0x3c2c44);

		if (discovered) {
			this.fontRenderer.drawSplitString(getSpell().getDescription(), left + 24, top + 83, 110, 0x3c2c44);
		} else {
			this.mc.standardGalacticFontRenderer.drawSplitString(
					SpellGlyphData.getGlyphDescription(getSpell(), mc.world), left + 24, top + 83, 110, 0x3c2c44);
		}
	}

	/**
	 * Draws the background of the spell info GUI. This is called before buttons are drawn.
	 * @param left The x-coordinate of the left-hand edge of the GUI
	 * @param top The y-coordinate of the top edge of the GUI
	 * @param mouseX The current x position of the mouse pointer
	 * @param mouseY The current y position of the mouse pointer
	 */
	protected void drawBackgroundLayer(int left, int top, int mouseX, int mouseY){

		boolean discovered = Wizardry.proxy.shouldDisplayDiscovered(getSpell(), null);

		GlStateManager.color(1, 1, 1, 1); // Just in case

//		// Draws spell illustration on opposite page, underneath the book so it shows through the hole.
//		Minecraft.getMinecraft().renderEngine.bindTexture(discovered ? getSpell().getIcon() : Spells.none.getIcon());
//		DrawingUtils.drawTexturedRect(left + 146, top + 20, 0, 0, 128, 128, 128, 128);

		Minecraft.getMinecraft().renderEngine.bindTexture(getTexture());
		DrawingUtils.drawTexturedRect(left, top, 0, 0, xSize, ySize, textureWidth, textureHeight);
	}

	@Override
	public boolean doesGuiPauseGame() {
		return Wizardry.settings.booksPauseGame;
	}

	public Spell getSpell() {
		return spell;
	}

	public ResourceLocation getTexture() {
		return new ResourceLocation(AncientSpellcraft.MODID, "textures/gui/runic_plate.png");
	}

}
