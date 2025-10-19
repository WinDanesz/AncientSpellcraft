package com.windanesz.ancientspellcraft.potion;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import electroblob.wizardry.potion.Curse;
import electroblob.wizardry.registry.WizardryPotions;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;

public class PotionCurseOfEternalFrost extends Curse {

	public PotionCurseOfEternalFrost() {
		super(true, 0xFF4500, new ResourceLocation(AncientSpellcraft.MODID, "textures/gui/potion_icon_curse_of_eternal_frost.png"));
		this.setPotionName("potion." + AncientSpellcraft.MODID + ":curse_of_eternal_frost");
	}

	@Override
	public boolean isReady(int duration, int amplifier) {
		// Every second
		return true;
	}

	@Override
	public void performEffect(EntityLivingBase entitylivingbase, int strength) {
		if (!entitylivingbase.world.isRemote) {
			// Average of 45 seconds.
			if (entitylivingbase.world.rand.nextInt(400) == 0) {
				int duration = 60 + entitylivingbase.world.rand.nextInt(80);
				entitylivingbase.addPotionEffect(new PotionEffect(WizardryPotions.frost, duration, 0));
			}
		}
	}
}
