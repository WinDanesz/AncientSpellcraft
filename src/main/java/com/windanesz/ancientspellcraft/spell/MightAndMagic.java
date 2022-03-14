package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.registry.ASItems;
import com.windanesz.ancientspellcraft.registry.ASPotions;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;

public class MightAndMagic extends SpellBuffAS {

	public MightAndMagic() {
		super("might_and_magic", 219, 0, 18, ()-> ASPotions.magical_exhaustion, ()-> MobEffects.HEALTH_BOOST, ()-> MobEffects.STRENGTH);
	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == ASItems.ancient_spell_book || item == ASItems.ancient_spellcraft_scroll;
	}
}
