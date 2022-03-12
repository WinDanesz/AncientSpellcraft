package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.Settings;
import com.windanesz.ancientspellcraft.item.ItemNewArtefact;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftItems;
import electroblob.wizardry.constants.Constants;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.spell.SpellConjuration;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public class ConjurePickaxe extends SpellConjuration {
	public ConjurePickaxe(String name, Item item) {
		super(name, item);

		////// Overrides //////
		AncientSpellcraft.logger.info("Overriding default Electroblobs's Wizardry spell " + this.getRegistryName() + " to apply changes by Ancient Spellcraft.");

		// must use the original networkID of the base spell
		int id = Settings.spellCompatSettings.conjurePickaxeSpellNetworkID;
		ObfuscationReflectionHelper.setPrivateValue(Spell.class, this, id, "id");

		// most call this or the networkIDs will be pushed, because calling super() increments the next ID again!
		int nextSpellId = ObfuscationReflectionHelper.getPrivateValue(Spell.class, this, "nextSpellId");
		//  decrement it back...
		nextSpellId--;
		ObfuscationReflectionHelper.setPrivateValue(Spell.class, this, nextSpellId, "nextSpellId");
		////// Overrides //////
	}

	@Override
	protected void addItemExtras(EntityPlayer caster, ItemStack stack, SpellModifiers modifiers) {

		if (ItemNewArtefact.isNewArtefactActive(caster, AncientSpellcraftItems.head_fortune)) {

			// The maximum harvest level as determined by the potency multiplier. The + 0.5f is so that
			// weird float processing doesn't incorrectly round it down.
			// maximum level III of Fortune is allowed
			int fortuneLevel = Math.min((int) ((modifiers.get(SpellModifiers.POTENCY) - 1) / Constants.POTENCY_INCREASE_PER_TIER + 0.5f), 3);

			if (fortuneLevel > 0) {
				// would be funny otherwise, but practically useless
				stack.addEnchantment(Enchantments.FORTUNE, fortuneLevel);
			}

		}
	}
}
