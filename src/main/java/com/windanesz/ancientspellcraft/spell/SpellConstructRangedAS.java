package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.registry.ASItems;
import electroblob.wizardry.entity.construct.EntityMagicConstruct;
import electroblob.wizardry.spell.SpellConstructRanged;
import net.minecraft.item.Item;
import net.minecraft.world.World;

import java.util.function.Function;

public class SpellConstructRangedAS<T extends EntityMagicConstruct> extends SpellConstructRanged<T> {

	public SpellConstructRangedAS(String name, Function<World, T> constructFactory, boolean permanent) {
		super(AncientSpellcraft.MODID, name, constructFactory, permanent);
	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == ASItems.ancient_spellcraft_spell_book || item == ASItems.ancient_spellcraft_scroll;
	}
}
