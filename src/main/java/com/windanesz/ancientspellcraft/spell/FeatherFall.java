package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.registry.AncientSpellcraftItems;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftPotions;
import net.minecraft.item.Item;

public class FeatherFall extends SpellBuffAS {

	public FeatherFall() {
		super("feather_fall", 230, 230, 255, () -> AncientSpellcraftPotions.feather_fall);
	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == AncientSpellcraftItems.ancient_spellcraft_spell_book || item == AncientSpellcraftItems.ancient_spellcraft_scroll;
	}
}
