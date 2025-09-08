package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.entity.living.EntityRemnantMinion;
import com.windanesz.ancientspellcraft.registry.ASItems;
import electroblob.wizardry.spell.SpellMinion;
import net.minecraft.item.Item;

public class SummonRemnant extends SpellMinion<EntityRemnantMinion> {

	public SummonRemnant() {
		super(AncientSpellcraft.MODID, "summon_remnant", EntityRemnantMinion::new);
	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == ASItems.ancient_spell_book || item == ASItems.ancient_spellcraft_scroll;
	}
}
