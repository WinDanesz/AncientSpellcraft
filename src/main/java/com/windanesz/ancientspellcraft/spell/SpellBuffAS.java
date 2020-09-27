package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftItems;
import electroblob.wizardry.spell.SpellBuff;
import net.minecraft.item.Item;
import net.minecraft.potion.Potion;

import java.util.function.Supplier;

public class SpellBuffAS extends SpellBuff {

	public SpellBuffAS(String name, float r, float g, float b, Supplier<Potion>... effects) {
		super(AncientSpellcraft.MODID, name, r, g, b, effects);
	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == AncientSpellcraftItems.ancient_spellcraft_spell_book || item == AncientSpellcraftItems.ancient_spellcraft_scroll;
	}
}
