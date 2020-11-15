package com.windanesz.ancientspellcraft.data;

import com.windanesz.ancientspellcraft.util.ASUtils;
import electroblob.wizardry.constants.Tier;
import electroblob.wizardry.data.IStoredVariable;
import electroblob.wizardry.data.Persistence;
import electroblob.wizardry.data.WizardData;
import electroblob.wizardry.spell.Spell;
import net.minecraft.entity.player.EntityPlayer;

import javax.annotation.Nullable;
import java.util.List;

public class Knowledge {

	/**
	 * The total amount of knowledge the player has. This also includes the amount of knowledge within their Ancient Knowledge Bar.
	 */
	public static final IStoredVariable<Integer> ANCIENT_KNOWLEDGE = IStoredVariable.StoredVariable.ofInt("ancient_knowledge_total", Persistence.ALWAYS).setSynced();

	public Knowledge() {}

	/**
	 * Called from {@link com.windanesz.ancientspellcraft.AncientSpellcraft#init(net.minecraftforge.fml.common.event.FMLInitializationEvent)}
	 * Registers the player-specific WizardData attributes.
	 */
	public static void init() {
		WizardData.registerStoredVariables(ANCIENT_KNOWLEDGE);

	}

	public static void addKnowledge(EntityPlayer player, int amount) {
		if (player != null) {
			WizardData data = WizardData.get(player);
			if (data != null) {
				Integer total = data.getVariable(ANCIENT_KNOWLEDGE);
				if (total == null || total < 0) {
					total = 0;
				}
				total += amount;
				data.setVariable(ANCIENT_KNOWLEDGE, total);
			}
		}
	}

	public static int getKnowledge(EntityPlayer player) {
		if (player != null) {
			WizardData data = WizardData.get(player);
			if (data != null) {
				Integer total = data.getVariable(ANCIENT_KNOWLEDGE);
				if (total == null || total < 0) {
					total = 0;
				}
				return total;
			}
		}
		return 0;
	}

	@Nullable
	public static Spell getRandomNextSpell(EntityPlayer player, Tier tier) {
		if (player != null) {
			WizardData data = WizardData.get(player);
			if (data != null) {
				List<Spell> list = SpellComponentList.getSpellListByTier(tier);

				Spell current = ASUtils.getRandomListItem(list);

				return current;
			}
		}
		return null;
	}
}
