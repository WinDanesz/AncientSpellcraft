package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.entity.living.EntityFireAnt;
import com.windanesz.ancientspellcraft.registry.ASItems;
import electroblob.wizardry.spell.SpellMinion;
import net.minecraft.item.Item;

public class SummonFireAnt extends SpellMinion<EntityFireAnt> {

	public SummonFireAnt() {
		super(AncientSpellcraft.MODID, "summon_fire_ant", EntityFireAnt::new);

	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == ASItems.ancient_spellcraft_spell_book || item == ASItems.ancient_spellcraft_scroll;
	}
}
