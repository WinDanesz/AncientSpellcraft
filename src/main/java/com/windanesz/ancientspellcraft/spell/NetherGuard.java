package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.entity.living.EntityPigZombieMinion;
import com.windanesz.ancientspellcraft.registry.ASItems;
import electroblob.wizardry.item.ItemWizardArmour;
import electroblob.wizardry.spell.SpellMinion;
import net.minecraft.item.Item;

public class NetherGuard extends SpellMinion<EntityPigZombieMinion> implements IClassSpell {

	public NetherGuard() {
		super(AncientSpellcraft.MODID, "nether_guard", EntityPigZombieMinion::new);
	}

	@Override
	public ItemWizardArmour.ArmourClass getArmourClass() { return ItemWizardArmour.ArmourClass.SAGE; }

	@Override
	public boolean applicableForItem(Item item) {
		return item == ASItems.mystic_spell_book;
	}
}
