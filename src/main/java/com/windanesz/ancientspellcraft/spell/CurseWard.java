package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.registry.ASItems;
import com.windanesz.ancientspellcraft.registry.ASPotions;
import electroblob.wizardry.spell.SpellBuff;
import net.minecraft.item.Item;

public class CurseWard extends SpellBuff {

	public CurseWard() {
		super(AncientSpellcraft.MODID, "curse_ward", 252, 253, 228, () -> ASPotions.curse_ward);
		soundValues(1f, 1f, 0f);
	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == ASItems.ancient_spellcraft_spell_book || item == ASItems.ancient_spellcraft_scroll;
	}
}
