package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import electroblob.wizardry.spell.Spell;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public interface IOverrideSpell {

	/**
	 * DO NOT call this outside of an override spell's constructor, or this will shift the network IDs and cause world logon crashes!
	 */
	default void handleNetworkIDChange(Spell spell, int originalNetworkID) {

		if (!(spell instanceof IOverrideSpell)) {
			throw new IllegalArgumentException("Tried to call IOverrideSpell.handleNetworkIDChange from a Spell.class not implementing IOverrideSpell!");
		}

		////// Overrides //////
		AncientSpellcraft.logger.info("Overriding default Electroblobs's Wizardry spell " + spell.getRegistryName() + " to apply changes by Ancient Spellcraft.");

		ObfuscationReflectionHelper.setPrivateValue(Spell.class, spell, originalNetworkID, "id");

		// must call this or the networkIDs will be pushed, because calling super() increments the next ID again!
		int nextSpellId = ObfuscationReflectionHelper.getPrivateValue(Spell.class, spell, "nextSpellId");

		//  decrement it back...
		nextSpellId--;
		ObfuscationReflectionHelper.setPrivateValue(Spell.class, spell, nextSpellId, "nextSpellId");
	}

}
