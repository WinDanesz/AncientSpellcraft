package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftItems;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftPotions;
import electroblob.wizardry.spell.SpellBuff;
import net.minecraft.item.Item;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class AquaticAgility extends SpellBuff {

	public AquaticAgility() {
		super(AncientSpellcraft.MODID, "aquatic_agility", 0f, 0.4f, 0.8f, () -> AncientSpellcraftPotions.aquatic_agility);
		soundValues(0.7f, 1.2f, 0.4f);
	}

	@SubscribeEvent
	public static void onLivingUpdateEvent(LivingEvent.LivingUpdateEvent event) {
		if (event.getEntityLiving().isPotionActive(AncientSpellcraftPotions.aquatic_agility)) {
			PotionEffect effect = event.getEntityLiving().getActivePotionEffect(AncientSpellcraftPotions.aquatic_agility);
			if (event.getEntityLiving().isInWater()) {
				event.getEntityLiving().motionX *= (1.1f + 0.025 * effect.getAmplifier());
				event.getEntityLiving().motionZ *= (1.1f + 0.025 * effect.getAmplifier());

				if (event.getEntityLiving().isSneaking() && event.getEntityLiving().motionY < 0 || event.getEntityLiving().motionY > 0) {
					event.getEntityLiving().motionY *= (1.1f + 0.025 * effect.getAmplifier());
				}
			}
		}
	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == AncientSpellcraftItems.ancient_spellcraft_spell_book || item == AncientSpellcraftItems.ancient_spellcraft_scroll;
	}
}
