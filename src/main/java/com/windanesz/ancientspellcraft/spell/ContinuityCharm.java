package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.registry.AncientSpellcraftPotions;

public class ContinuityCharm extends MetaSpellBuff {

	public ContinuityCharm() {
		super("continuity_charm", 255, 255, 255, () -> AncientSpellcraftPotions.continuity_charm);
	}

}
