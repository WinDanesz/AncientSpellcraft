package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.registry.ASItems;
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
		return item == ASItems.ancient_spellcraft_spell_book || item == ASItems.ancient_spellcraft_scroll;
	}
}
