package com.windanesz.ancientspellcraft.mixin.ebwizardry;

import com.windanesz.ancientspellcraft.Settings;
import com.windanesz.ancientspellcraft.registry.ASItems;
import electroblob.wizardry.constants.Constants;
import electroblob.wizardry.item.ItemArtefact;
import electroblob.wizardry.registry.Spells;
import electroblob.wizardry.spell.SpellConjuration;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Adds Fortune artefact ({@link ASItems#head_fortune}) compatibility to the Conjure Pickaxe spell specifically.
 * {@code addItemExtras} is a shared no-op hook on {@link SpellConjuration}, used by many different conjuration
 * spells (swords, pickaxes, armour...), so this only applies its behaviour when {@code this} is actually the
 * conjure_pickaxe spell instance - it does nothing for any other conjured item.
 * <p></p>
 * Previously this lived in a dedicated {@code ConjurePickaxe extends SpellConjuration} class that got registered
 * under the same "conjure_pickaxe" name as the plain {@code SpellConjuration} instance Wizardry itself registers,
 * with the networkID hazard that came with that (see {@link MixinMine}'s javadoc for the full story).
 */
@Mixin(SpellConjuration.class)
public class MixinSpellConjuration {

	@Inject(method = "addItemExtras", at = @At("HEAD"), remap = false)
	private void addFortuneToConjuredPickaxe(EntityPlayer caster, ItemStack stack, SpellModifiers modifiers, CallbackInfo ci){

		if((SpellConjuration)(Object)this != Spells.conjure_pickaxe) return;
		if(!Settings.spellCompatSettings.conjurePickaxeSpellOverride) return;
		if(!ItemArtefact.isArtefactActive(caster, ASItems.head_fortune)) return;

		// The maximum harvest level as determined by the potency multiplier. The + 0.5f is so that
		// weird float processing doesn't incorrectly round it down.
		// maximum level III of Fortune is allowed
		int fortuneLevel = Math.min((int)((modifiers.get(SpellModifiers.POTENCY) - 1) / Constants.POTENCY_INCREASE_PER_TIER + 0.5f), 3);

		if(fortuneLevel > 0){
			// would be funny otherwise, but practically useless
			stack.addEnchantment(Enchantments.FORTUNE, fortuneLevel);
		}
	}
}
