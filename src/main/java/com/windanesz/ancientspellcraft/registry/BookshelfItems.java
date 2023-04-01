package com.windanesz.ancientspellcraft.registry;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import electroblob.wizardry.Wizardry;
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
		registerBookModelTexture(() -> ASItems.transmutation_scroll, new ResourceLocation(AncientSpellcraft.MODID, "blocks/scrolls_transmutation_scroll"));
		registerBookModelTexture(() -> ASItems.bartering_scroll, new ResourceLocation(AncientSpellcraft.MODID, "blocks/scrolls_bartering_scroll"));
		registerBookModelTexture(() -> ASItems.knowledge_scroll, new ResourceLocation(AncientSpellcraft.MODID, "blocks/scrolls_knowledge_scroll"));
		registerBookModelTexture(() -> ASItems.duplication_scroll, new ResourceLocation(AncientSpellcraft.MODID, "blocks/scrolls_duplication_scroll"));
		registerBookModelTexture(() -> ASItems.unsealing_scroll, new ResourceLocation(AncientSpellcraft.MODID, "blocks/scrolls_unsealing_scroll"));
		registerBookModelTexture(() -> ASItems.imbuement_scroll, new ResourceLocation(AncientSpellcraft.MODID, "blocks/scrolls_imbuement_scroll"));
		registerBookModelTexture(() -> ASItems.amnesia_scroll, new ResourceLocation(AncientSpellcraft.MODID, "blocks/scrolls_amnesia_scroll"));
		registerBookModelTexture(() -> ASItems.ritual_book, new ResourceLocation(AncientSpellcraft.MODID, "blocks/books_ritual_book"));
		registerBookModelTexture(() -> ASItems.soulbound_upgrade, new ResourceLocation(Wizardry.MODID, "blocks/scrolls_wooden"));
		registerBookModelTexture(() -> ASItems.sentience_upgrade, new ResourceLocation(Wizardry.MODID, "blocks/scrolls_wooden"));
		registerBookModelTexture(() -> ASItems.empowerment_upgrade, new ResourceLocation(Wizardry.MODID, "blocks/scrolls_wooden"));

	}

	public static void InitBookshelfItems() {

		ContainerBookshelf.registerBookItem(ASItems.ancient_spell_book);
		ContainerBookshelf.registerBookItem(ASItems.ancient_spellcraft_spell_book);
		ContainerBookshelf.registerBookItem(ASItems.ancient_spellcraft_scroll);
		ContainerBookshelf.registerBookItem(ASItems.mystic_spell_book);
		ContainerBookshelf.registerBookItem(ASItems.empty_theory_scroll);
		ContainerBookshelf.registerBookItem(ASItems.theory_scroll);
		ContainerBookshelf.registerBookItem(ASItems.transmutation_scroll);
		ContainerBookshelf.registerBookItem(ASItems.bartering_scroll);
		ContainerBookshelf.registerBookItem(ASItems.knowledge_scroll);
		ContainerBookshelf.registerBookItem(ASItems.duplication_scroll);
		ContainerBookshelf.registerBookItem(ASItems.unsealing_scroll);
		ContainerBookshelf.registerBookItem(ASItems.imbuement_scroll);
		ContainerBookshelf.registerBookItem(ASItems.amnesia_scroll);
		ContainerBookshelf.registerBookItem(ASItems.ritual_book);
		ContainerBookshelf.registerBookItem(ASItems.soulbound_upgrade);
		ContainerBookshelf.registerBookItem(ASItems.sentience_upgrade);
		ContainerBookshelf.registerBookItem(ASItems.empowerment_upgrade);
	}
}
