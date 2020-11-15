package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.registry.AncientSpellcraftPotions;

public class IntensifyingFocus extends MetaSpellBuff {

	public IntensifyingFocus() {
		super("intensifying_focus", 255, 255, 255, () -> AncientSpellcraftPotions.intensifying_focus);
	}
}
