package com.windanesz.ancientspellcraft.item;

import electroblob.wizardry.data.IStoredVariable;
import electroblob.wizardry.data.Persistence;
import electroblob.wizardry.data.WizardData;
import electroblob.wizardry.item.ItemArtefact;
import net.minecraft.item.EnumRarity;

public class ItemRingEternalServitude extends ItemASArtefact {

	/**
	 * WizardData key storing the UUID (as a String) of the player's current eternal minion.
	 * Null when no eternal minion is active.
	 */
	public static final IStoredVariable<String> ETERNAL_MINION_UUID =
			IStoredVariable.StoredVariable.ofString("eternalServitudeMinion", Persistence.ALWAYS).setSynced();

	static {
		WizardData.registerStoredVariables(ETERNAL_MINION_UUID);
	}

	public ItemRingEternalServitude() {
		super(EnumRarity.EPIC, ItemArtefact.Type.RING);
	}
}
