package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.entity.living.EntityAnimatedItem;
import com.windanesz.ancientspellcraft.item.ItemBattlemageSword;
import com.windanesz.ancientspellcraft.item.ItemSageTome;
import com.windanesz.ancientspellcraft.registry.ASItems;
import com.windanesz.ancientspellcraft.util.WizardArmourUtils;
import electroblob.wizardry.event.SpellCastEvent;
import electroblob.wizardry.item.ItemWizardArmour;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public interface IClassSpell {

	/**
	 * Assures spell casts fail if the required condition of wearing a matching full set is not met for class spells.
	 *
	 * @param event the SpellCastEvent
	 */
	@SubscribeEvent(priority = EventPriority.LOWEST)
	static void onSpellCastPreEvent(SpellCastEvent.Pre event) {
		if (event.getSpell() instanceof IClassSpell) {

			if (event.getCaster() instanceof EntityPlayer) {
				ItemWizardArmour.ArmourClass armourClass = ((IClassSpell) event.getSpell()).getArmourClass();
				if (!WizardArmourUtils.isWearingFullSet(event.getCaster(), null, armourClass)) {

					if (!event.getWorld().isRemote) {
						((EntityPlayer) event.getCaster()).sendStatusMessage(
								new TextComponentTranslation("message." + AncientSpellcraft.MODID + ":must_have_full_matching_set",
								new TextComponentTranslation("wizard_armour_class." + armourClass.name().toLowerCase())), false);
					}

					event.setCanceled(true);
					return;
				}

				SpellCastEvent.Source source = event.getSource();
				boolean allowedSource = source.name().equals(armourClass.name().toUpperCase() + "_ITEM");

				if (!(source == SpellCastEvent.Source.COMMAND || source == SpellCastEvent.Source.NPC || allowedSource)) {
					if (!event.getWorld().isRemote) {
						((EntityPlayer) event.getCaster()).sendStatusMessage(
								new TextComponentTranslation("message." + AncientSpellcraft.MODID + ":must_use_class_item",
										new TextComponentTranslation("wizard_armour_class." + armourClass.name().toLowerCase())), false);
					}
					event.setCanceled(true);
				}
			} else if (event.getCaster() instanceof EntityAnimatedItem) {
				// prevent casting these spells by animating an incorrect item...
				Item item = event.getCaster().getHeldItemMainhand().getItem();
				switch (((IClassSpell) event.getSpell()).getArmourClass()) {
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
						// TODO
						// case WARLOCK:
						// if (!(item instanceof ???)) {
						// 	event.setCanceled(true); return;
						// }
				}
			}
		}
	}

	default boolean applicableForItem(Item item) {
		return item == ASItems.mystic_spell_book;
	}

	ItemWizardArmour.ArmourClass getArmourClass();

	//	@SubscribeEvent(priority = EventPriority.LOWEST)
	//	static void onSpellCastPreEvent(SpellBindEvent event) {
	//		ContainerArcaneWorkbench workbench = (ContainerArcaneWorkbench) event.getContainer();
	//		for (int i = 0; i < 8; i++) {
	//			ItemStack stack = workbench.inventorySlots.get(i).getStack();
	//			if (stack.getItem() instanceof ItemSpellBook)
	//		}
	//	}


}
