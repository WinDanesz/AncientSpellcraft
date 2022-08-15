package com.windanesz.ancientspellcraft.potion;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import electroblob.wizardry.potion.Curse;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;

public class PotionCurseGills extends Curse {

	public PotionCurseGills() {
		super(true, 0x4287f5, new ResourceLocation(AncientSpellcraft.MODID, "textures/gui/potion_icon_curse_of_gills.png"));
		// This needs to be here because registerPotionAttributeModifier doesn't like it if the potion has no name yet.
		this.setPotionName("potion." + AncientSpellcraft.MODID + ":curse_of_gills");
	}

	@Override
	public boolean isReady(int duration, int amplifier) {
		return true;
	}

	@Override
	public void performEffect(EntityLivingBase entitylivingbase, int strength) {
		if (!entitylivingbase.isWet() && entitylivingbase.ticksExisted % 20 == 0) {
			entitylivingbase.attackEntityFrom(DamageSource.DROWN, 0.5F);
		}

		if (entitylivingbase.isWet()) {
			entitylivingbase.setAir(300);
		}
	}
}
