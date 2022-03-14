package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.registry.ASItems;
import electroblob.wizardry.spell.SpellRay;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;

public abstract class SpellRayAS extends SpellRay {

	public SpellRayAS(String name, EnumAction action, boolean isContinuous) {
		super(AncientSpellcraft.MODID, name, action, isContinuous);
	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == ASItems.ancient_spellcraft_spell_book || item == ASItems.ancient_spellcraft_scroll;
	}
}
