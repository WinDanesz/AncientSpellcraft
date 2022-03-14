package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.entity.living.EntityVolcano;
import com.windanesz.ancientspellcraft.registry.ASItems;
import electroblob.wizardry.spell.SpellMinion;
import net.minecraft.item.Item;

public class SummonVolcano extends SpellMinion<EntityVolcano> {

	public SummonVolcano() {
		super(AncientSpellcraft.MODID, "fire_ant_swarm", EntityVolcano::new);
	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == ASItems.ancient_spellcraft_spell_book || item == ASItems.ancient_spellcraft_scroll;
	}
}
