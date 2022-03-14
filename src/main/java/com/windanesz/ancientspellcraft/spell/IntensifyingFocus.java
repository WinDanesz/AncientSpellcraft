package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.registry.ASPotions;

public class IntensifyingFocus extends MetaSpellBuff {

	public IntensifyingFocus() {
		super("intensifying_focus", 255, 255, 255, () -> ASPotions.intensifying_focus);
	}
}
