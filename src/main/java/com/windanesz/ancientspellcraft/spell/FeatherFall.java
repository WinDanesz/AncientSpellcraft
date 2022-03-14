package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.registry.ASItems;
import com.windanesz.ancientspellcraft.registry.ASPotions;
import net.minecraft.item.Item;

public class FeatherFall extends SpellBuffAS {

	public FeatherFall() {
		super("feather_fall", 230, 230, 255, () -> ASPotions.feather_fall);
	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == ASItems.ancient_spellcraft_spell_book || item == ASItems.ancient_spellcraft_scroll;
	}
}
