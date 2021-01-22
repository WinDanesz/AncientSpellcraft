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
