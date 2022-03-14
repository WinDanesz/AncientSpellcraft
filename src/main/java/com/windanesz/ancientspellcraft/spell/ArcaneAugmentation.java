package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.registry.ASPotions;

public class ArcaneAugmentation extends MetaSpellBuff {

	public ArcaneAugmentation() {
		super("arcane_augmentation", 255, 255, 255, () -> ASPotions.arcane_augmentation);
	}

}
