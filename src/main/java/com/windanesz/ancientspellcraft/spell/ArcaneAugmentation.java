package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.registry.AncientSpellcraftPotions;

public class ArcaneAugmentation extends MetaSpellBuff {

	public ArcaneAugmentation() {
		super("arcane_augmentation", 255, 255, 255, () -> AncientSpellcraftPotions.arcane_augmentation);
	}

}
