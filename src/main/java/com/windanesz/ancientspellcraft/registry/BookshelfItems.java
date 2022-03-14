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

	}

	public static void InitBookshelfItems() {

		ContainerBookshelf.registerBookItem(ASItems.ancient_spell_book);
		ContainerBookshelf.registerBookItem(ASItems.ancient_spellcraft_spell_book);
		ContainerBookshelf.registerBookItem(ASItems.ancient_spellcraft_scroll);
	}
}
