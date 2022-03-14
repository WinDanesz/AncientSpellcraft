package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.registry.ASItems;
import com.windanesz.ancientspellcraft.registry.ASPotions;
import electroblob.wizardry.spell.SpellBuff;
import net.minecraft.item.Item;

public class AquaticAgility extends SpellBuff {

	public AquaticAgility() {
		super(AncientSpellcraft.MODID, "aquatic_agility", 0f, 0.4f, 0.8f, () -> ASPotions.aquatic_agility);
		soundValues(0.7f, 1.2f, 0.4f);
	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == ASItems.ancient_spellcraft_spell_book || item == ASItems.ancient_spellcraft_scroll;
	}
}
