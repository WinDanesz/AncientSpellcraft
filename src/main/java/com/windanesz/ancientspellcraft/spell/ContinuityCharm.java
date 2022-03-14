package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.registry.ASPotions;

public class ContinuityCharm extends MetaSpellBuff {

	public ContinuityCharm() {
		super("continuity_charm", 255, 255, 255, () -> ASPotions.continuity_charm);
	}

}
