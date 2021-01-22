package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.registry.AncientSpellcraftItems;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftPotions;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;

public class MightAndMagic extends SpellBuffAS {

	public MightAndMagic() {
		super("might_and_magic", 219, 0, 18, ()-> AncientSpellcraftPotions.magical_exhaustion, ()-> MobEffects.HEALTH_BOOST, ()-> MobEffects.STRENGTH);
	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == AncientSpellcraftItems.ancient_spell_book || item == AncientSpellcraftItems.ancient_spellcraft_scroll;
	}
}
