package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.entity.living.EntityAnimatedItem;
import com.windanesz.ancientspellcraft.entity.living.EntityEvilClassWizard;
import com.windanesz.ancientspellcraft.item.ItemBattlemageSword;
import com.windanesz.ancientspellcraft.item.ItemSageTome;
import com.windanesz.ancientspellcraft.item.ItemWarlockOrb;
import com.windanesz.ancientspellcraft.ritual.WarlockAttunement;
import com.windanesz.ancientspellcraft.util.ASUtils;
import com.windanesz.ancientspellcraft.util.WizardArmourUtils;
import electroblob.wizardry.constants.Element;
import electroblob.wizardry.event.SpellCastEvent;
import electroblob.wizardry.item.ItemWizardArmour;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Optional;

@Mod.EventBusSubscriber
public interface IClassSpell {

	/**
	 * Assures spell casts fail if the required condition of wearing a matching full set is not met for class spells.
	 *
	 * @param event the SpellCastEvent
	 */
	@SubscribeEvent(priority = EventPriority.LOWEST)
	static void onSpellCastPreEvent(SpellCastEvent.Pre event) {
		if (!(event.getSpell() instanceof IClassSpell)) {
			return;
		}

		IClassSpell spell = (IClassSpell) event.getSpell();
		EntityLivingBase caster = event.getCaster();

		if (caster instanceof EntityPlayer) {
			ItemWizardArmour.ArmourClass armourClass = spell.getArmourClass();

			if (armourClass != ItemWizardArmour.ArmourClass.WARLOCK && !WarlockAttunement.isWarlockAttuned((EntityPlayer) caster)) {
				ASUtils.sendMessage(caster, "message." + AncientSpellcraft.MODID + ":warlock_attunement_prevents_spell_cast", true);
				event.setCanceled(true);
				return;
			}
			if (!WizardArmourUtils.isWearingFullSet(caster, null, armourClass)) {
				ASUtils.sendMessage(caster, "message." + AncientSpellcraft.MODID + ":must_have_full_matching_set", false, new TextComponentTranslation("wizard_armour_class." + armourClass.name().toLowerCase()));
				event.setCanceled(true);
				return;
			}

			SpellCastEvent.Source source = event.getSource();
			boolean allowedSource = source.name().equals(armourClass.name().toUpperCase() + "_ITEM") || source == SpellCastEvent.Source.SCROLL && armourClass == ItemWizardArmour.ArmourClass.SAGE;

			if (!(source == SpellCastEvent.Source.COMMAND || source == SpellCastEvent.Source.NPC || allowedSource || spell instanceof IRunicHammerSpell)) {
				ASUtils.sendMessage(caster, "message." + AncientSpellcraft.MODID + ":must_use_class_item", false, new TextComponentTranslation("wizard_armour_class." + armourClass.name().toLowerCase()));
				event.setCanceled(true);
			}
		} else if (caster instanceof EntityAnimatedItem) {
			// prevent casting these spells by animating an incorrect item...
			Item item = event.getCaster().getHeldItemMainhand().getItem();
			switch (spell.getArmourClass()) {
				case SAGE:
					if (!(item instanceof ItemSageTome)) {
						event.setCanceled(true);
						return;
					}
				case BATTLEMAGE:
					if (!(item instanceof ItemBattlemageSword)) {
						event.setCanceled(true);
						return;
					}
				case WARLOCK:
					if (!(item instanceof ItemWarlockOrb)) {
						event.setCanceled(true);
					}
			}
		}
	}

	ItemWizardArmour.ArmourClass getArmourClass();

	default Element getElementOrMagicElement(EntityLivingBase caster) {
		Optional<Element> e = WizardArmourUtils.getFullSetElementForClassOptional(caster, getArmourClass());
		return e.orElse(Element.MAGIC);
	}

	default boolean canBeCastByClassNPC(EntityLivingBase npc) {
		return npc instanceof EntityEvilClassWizard;
	}

}
