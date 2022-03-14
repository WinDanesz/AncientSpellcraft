package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.registry.ASItems;
import com.windanesz.ancientspellcraft.registry.ASPotions;
import electroblob.wizardry.spell.SpellBuff;
import net.minecraft.item.Item;

public class AspectHunter extends SpellBuff {

	public AspectHunter() {
		super(AncientSpellcraft.MODID, "aspect_hunter", 22, 102, 48, () -> ASPotions.fortified_archery);
	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == ASItems.ancient_spell_book || item == ASItems.ancient_spellcraft_scroll;
	}
}
