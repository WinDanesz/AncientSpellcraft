package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.entity.construct.EntityAntiMagicField;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftItems;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.spell.SpellConstructRanged;
import net.minecraft.item.Item;

public class AntiMagicField extends SpellConstructRanged<EntityAntiMagicField> {

	public AntiMagicField() {
		super(AncientSpellcraft.MODID, "antimagic_field", EntityAntiMagicField::new, false);
		addProperties(Spell.EFFECT_RADIUS);
	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == AncientSpellcraftItems.ancient_spell_book || item == AncientSpellcraftItems.ancient_spellcraft_scroll;
	}
}
