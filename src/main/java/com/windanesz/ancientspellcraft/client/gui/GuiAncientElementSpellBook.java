package com.windanesz.ancientspellcraft.client.gui;

import com.windanesz.ancientspellcraft.spell.MetaSpellBuff;
import electroblob.wizardry.Wizardry;
import electroblob.wizardry.client.gui.GuiSpellInfo;
import electroblob.wizardry.constants.Tier;
import electroblob.wizardry.data.SpellGlyphData;
import electroblob.wizardry.item.ItemSpellBook;
import electroblob.wizardry.spell.Spell;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

public class GuiAncientElementSpellBook extends GuiSpellInfo {

	private int xSize, ySize;
	private ItemSpellBook book;
	private Spell spell;

	public GuiAncientElementSpellBook(ItemStack stack) {
		super(288, 180);
		System.out.println("test GuiAncientSpellBook");
//		xSize = 288;
//		ySize = 180;
		if (!(stack.getItem() instanceof ItemSpellBook)) {
			throw new ClassCastException("Cannot create spell book GUI for item that does not extend ItemSpellBook!");
		}
		this.book = (ItemSpellBook)stack.getItem();
		this.spell = (Spell.byMetadata(stack.getItemDamage()));
	}

	@Override
	protected void drawForegroundLayer(int left, int top, int mouseX, int mouseY){

		boolean discovered = Wizardry.proxy.shouldDisplayDiscovered(getSpell(), null);

		if(discovered){
			this.fontRenderer.drawString(getSpell().getDisplayName(), left + 17, top + 15, 0);
			String spellType = spell instanceof MetaSpellBuff  ? I18n.format("spelltype.metamagic") : spell.getType().getDisplayName();
			this.fontRenderer.drawString(spellType, left + 17, top + 26, 0x777777);		}else{
			this.mc.standardGalacticFontRenderer.drawString(SpellGlyphData.getGlyphName(getSpell(), mc.world), left + 17,
					top + 15, 0);
			this.mc.standardGalacticFontRenderer.drawString(getSpell().getType().getDisplayName(), left + 17, top + 26,
					0x777777);
		}

		// Novice is usually white but this doesn't show up
		String tier = I18n.format(TRANSLATION_KEY_PREFIX + ".tier", getSpell().getTier() == Tier.NOVICE ?
				"\u00A77" + getSpell().getTier().getDisplayName() : getSpell().getTier().getDisplayNameWithFormatting());
		this.fontRenderer.drawString(tier, left + 17, top + 45, 0);

		String element = I18n.format("gui.ebwizardry:spell_book.element", TextFormatting.GOLD + I18n.format("element.ancient"));
		if(!discovered) element = I18n.format(TRANSLATION_KEY_PREFIX + ".element_undiscovered");
		this.fontRenderer.drawString(element, left + 17, top + 57, 0);

		String manaCost = I18n.format(TRANSLATION_KEY_PREFIX + ".mana_cost", getSpell().getCost());
		if(getSpell().isContinuous) manaCost = I18n.format(TRANSLATION_KEY_PREFIX + ".mana_cost_continuous", getSpell().getCost());
		if(!discovered) manaCost = I18n.format(TRANSLATION_KEY_PREFIX + ".mana_cost_undiscovered");
		this.fontRenderer.drawString(manaCost, left + 17, top + 69, 0);

		if(discovered){
			this.fontRenderer.drawSplitString(getSpell().getDescription(), left + 17, top + 83, 118, 0);
		}else{
			this.mc.standardGalacticFontRenderer.drawSplitString(
					SpellGlyphData.getGlyphDescription(getSpell(), mc.world), left + 17, top + 83, 118, 0);
		}
	}

//		@Override
//	public void drawScreen(int par1, int par2, float par3) {
//
//		int xPos = this.width / 2 - xSize / 2;
//		int yPos = this.height / 2 - this.ySize / 2;
//
//		EntityPlayer player = Minecraft.getMinecraft().player;
//
//		boolean discovered = true;
//		if (Wizardry.settings.discoveryMode && !player.isCreative() && WizardData.get(player) != null
//				&& !WizardData.get(player).hasSpellBeenDiscovered(spell)) {
//			discovered = false;
//		}
//
//		GlStateManager.color(1, 1, 1, 1); // Just in case
//
//		// Draws spell illustration on opposite page, underneath the book so it shows through the hole.
//		Minecraft.getMinecraft().renderEngine.bindTexture(discovered ? spell.getIcon() : Spells.none.getIcon());
//		DrawingUtils.drawTexturedRect(xPos + 146, yPos + 20, 0, 0, 128, 128, 128, 128);
//
//		Minecraft.getMinecraft().renderEngine.bindTexture(book.getGuiTexture(spell));
//		DrawingUtils.drawTexturedRect(xPos, yPos, 0, 0, xSize, ySize, xSize, 256);
//
//		super.drawScreen(par1, par2, par3);
//
////		boolean m = spell.getEffect().matches("metamagic");
////		System.out.println("spell.getEffect():" + spell.getEffect());
////
////		System.out.println("matches: " + m);
//
//		// a bit hacky... but metamagic type spells have their own custom category displayed, based on spell unlocalized name metamagic_ prefic
//		// TODO: make this nicer once spell types can be properly registered
//		String spellType = spell instanceof MetaSpellBuff  ? I18n.format("spelltype.metamagic") : spell.getType().getDisplayName();
//
//		if (discovered) {
//			this.fontRenderer.drawString(spell.getDisplayName(), xPos + 17, yPos + 15, 0);
//			this.fontRenderer.drawString(spellType, xPos + 17, yPos + 26, 0x777777);
//		} else {
//			this.mc.standardGalacticFontRenderer.drawString(SpellGlyphData.getGlyphName(spell, player.world), xPos + 17,
//					yPos + 15, 0);
//			this.mc.standardGalacticFontRenderer.drawString(spell.getType().getDisplayName(), xPos + 17, yPos + 26,
//					0x777777);
//		}
//
//		// Novice is usually white but this doesn't show up
//		String tier = I18n.format("gui.ebwizardry:spell_book.tier", spell.getTier() == Tier.NOVICE ?
//				"\u00A77" + spell.getTier().getDisplayName() : spell.getTier().getDisplayNameWithFormatting());
//		this.fontRenderer.drawString(tier, xPos + 17, yPos + 45, 0);
//
//		//		String element = I18n.format("gui.ancientspellcraft:element.magic");
//		String element = I18n.format("gui.ebwizardry:spell_book.element", TextFormatting.GOLD + I18n.format("element.ancient"));
//
////		String element = I18n.format("element.ancient", spell.getElement().getFormattingCode() + spell.getElement().getDisplayName());
////		if (!discovered)
////			element = I18n.format("gui.ebwizardry:spell_book.element_undiscovered");
//		this.fontRenderer.drawString(element, xPos + 17, yPos + 57, 0);
//
//		String manaCost = I18n.format("gui.ebwizardry:spell_book.mana_cost", spell.getCost());
//		if (spell.isContinuous)
//			manaCost = I18n.format("gui.ebwizardry:spell_book.mana_cost_continuous", spell.getCost());
//		if (!discovered)
//			manaCost = I18n.format("gui.ebwizardry:spell_book.mana_cost_undiscovered");
//		this.fontRenderer.drawString(manaCost, xPos + 17, yPos + 69, 0);
//
//		if (discovered) {
//			this.fontRenderer.drawSplitString(spell.getDescription(), xPos + 17, yPos + 83, 118, 0);
//		} else {
//			this.mc.standardGalacticFontRenderer.drawSplitString(
//					SpellGlyphData.getGlyphDescription(spell, player.world), xPos + 17, yPos + 83, 118, 0);
//		}
//	}

//	@Override
//	public void initGui() {
//		super.initGui();
//		Keyboard.enableRepeatEvents(true);
//		this.buttonList.clear();
//
//		this.mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(WizardrySounds.MISC_BOOK_OPEN, 1));
//	}
//
//	@Override
//	public void onGuiClosed() {
//		super.onGuiClosed();
//		Keyboard.enableRepeatEvents(false);
//	}

	@Override
	public boolean doesGuiPauseGame() {
		return Wizardry.settings.booksPauseGame;
	}


	@Override
	public Spell getSpell(){
		return spell;
	}

	@Override
	public ResourceLocation getTexture(){
		return book.getGuiTexture(spell);
	}


}
