package com.windanesz.ancientspellcraft.registry;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import electroblob.wizardry.inventory.ContainerBookshelf;
import net.minecraft.util.ResourceLocation;

import static electroblob.wizardry.block.BlockBookshelf.registerBookModelTexture;

public class BookshelfItems {


	public static void preInitBookShelfModelTextures() {
		registerBookModelTexture(() -> ASItems.ancient_spell_book, new ResourceLocation(AncientSpellcraft.MODID, "blocks/books_ancient_spell_book"));
		registerBookModelTexture(() -> ASItems.ancient_spellcraft_spell_book, new ResourceLocation(AncientSpellcraft.MODID, "blocks/ancient_spellcraft_spell_book"));
		registerBookModelTexture(() -> ASItems.ancient_spellcraft_scroll, new ResourceLocation(AncientSpellcraft.MODID, "blocks/scrolls_ancient_spellcraft"));
		registerBookModelTexture(() -> ASItems.mystic_spell_book, new ResourceLocation(AncientSpellcraft.MODID, "blocks/books_mystic_spell_book"));
		registerBookModelTexture(() -> ASItems.empty_theory_scroll, new ResourceLocation(AncientSpellcraft.MODID, "blocks/books_mystic_spell_book"));
		registerBookModelTexture(() -> ASItems.theory_scroll, new ResourceLocation(AncientSpellcraft.MODID, "blocks/scrolls_theory_scroll"));
		registerBookModelTexture(() -> ASItems.ritual_book, new ResourceLocation(AncientSpellcraft.MODID, "blocks/books_ritual_book"));

	}

	public static void InitBookshelfItems() {

		ContainerBookshelf.registerBookItem(ASItems.ancient_spell_book);
		ContainerBookshelf.registerBookItem(ASItems.ancient_spellcraft_spell_book);
		ContainerBookshelf.registerBookItem(ASItems.ancient_spellcraft_scroll);
		ContainerBookshelf.registerBookItem(ASItems.mystic_spell_book);
		ContainerBookshelf.registerBookItem(ASItems.empty_theory_scroll);
		ContainerBookshelf.registerBookItem(ASItems.theory_scroll);
		ContainerBookshelf.registerBookItem(ASItems.ritual_book);
	}
}
