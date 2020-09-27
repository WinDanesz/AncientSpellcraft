package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftItems;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftPotions;
import electroblob.wizardry.spell.SpellBuff;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class MagmaStrider extends SpellBuff {

	public MagmaStrider() {
		super(AncientSpellcraft.MODID, "magma_strider", 216, 26, 11, () -> AncientSpellcraftPotions.magma_strider);
		soundValues(0.7f, 1.2f, 0.4f);
	}

	@SubscribeEvent
	public static void onLivingUpdateEvent(LivingEvent.LivingUpdateEvent event) {
		if (event.getEntityLiving().isPotionActive(AncientSpellcraftPotions.magma_strider)) {
			EntityLivingBase entity = event.getEntityLiving();
			PotionEffect effect = entity.getActivePotionEffect(AncientSpellcraftPotions.magma_strider);
			if (entity.isInLava() && !(entity instanceof EntityPlayer && (((EntityPlayer) entity).isCreative()))) {
				entity.motionX *= (1.7f + 0.03 * effect.getAmplifier());
				entity.motionZ *= (1.7f + 0.03 * effect.getAmplifier());

				if (entity.isSneaking() && entity.motionY < 0 || entity.motionY > 0) {
					entity.motionY *= (1.7f + 0.03 * effect.getAmplifier());
				}
			}
		}
	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == AncientSpellcraftItems.ancient_spellcraft_spell_book || item == AncientSpellcraftItems.ancient_spellcraft_scroll;
	}
}
