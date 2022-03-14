package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.entity.living.EntityOrdinarySpiderMinion;
import com.windanesz.ancientspellcraft.registry.ASItems;
import electroblob.wizardry.spell.SpellMinion;
import net.minecraft.item.Item;

public class SummonSpider extends SpellMinion<EntityOrdinarySpiderMinion> {

	public SummonSpider() {
		super(AncientSpellcraft.MODID, "summon_spider", EntityOrdinarySpiderMinion::new);

	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == ASItems.ancient_spellcraft_spell_book || item == ASItems.ancient_spellcraft_scroll;
	}
}
