package com.windanesz.ancientspellcraft.potion;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftPotions;
import electroblob.wizardry.potion.Curse;
import electroblob.wizardry.potion.PotionMagicEffect;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class PotionCurseWard extends PotionMagicEffect {

	public PotionCurseWard() {
		super(false, 0xfcfde4, new ResourceLocation(AncientSpellcraft.MODID, "textures/gui/potion_icon_curse_ward.png"));
		setBeneficial();
		this.setPotionName("potion." + AncientSpellcraft.MODID + ":curse_ward");
	}

	@SubscribeEvent
	public static void onPotionApplicableEvent(PotionEvent.PotionApplicableEvent event) {

		if (event.getEntityLiving().isPotionActive(AncientSpellcraftPotions.curse_ward)) {
			if (event.getPotionEffect().getPotion() instanceof Curse) {
				PotionEffect curseWard = event.getEntityLiving().getActivePotionEffect(AncientSpellcraftPotions.curse_ward);
				int newDuration = (int) (curseWard.getDuration() / 2);
				event.getEntityLiving().removePotionEffect(AncientSpellcraftPotions.curse_ward);
				event.getEntityLiving().addPotionEffect(new PotionEffect(AncientSpellcraftPotions.curse_ward, newDuration));
				event.setResult(Event.Result.DENY);
			}
		}
	}

}
