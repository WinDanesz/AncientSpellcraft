package com.windanesz.ancientspellcraft.potion;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import electroblob.wizardry.potion.Curse;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;

public class PotionCurseUmbra extends Curse {

	public PotionCurseUmbra() {
		super(true, 0x000000, new ResourceLocation(AncientSpellcraft.MODID, "textures/gui/potion_icon_curse_of_umbra.png"));
		// This needs to be here because registerPotionAttributeModifier doesn't like it if the potion has no name yet.
		this.setPotionName("potion." + AncientSpellcraft.MODID + ":curse_of_umbra");
	}

	@Override
	public boolean isReady(int duration, int amplifier) {
		return true;
	}

	@Override
	public void performEffect(EntityLivingBase entitylivingbase, int strength) {

		if (!entitylivingbase.world.isRemote) {

			float f = entitylivingbase.getBrightness();
			if (entitylivingbase.ticksExisted % 5 == 0) {
				if (f < 0.25F && entitylivingbase.world.rand.nextFloat() * 30.0F < (f + 0.4F) * 4.0F) {
					entitylivingbase.attackEntityFrom(DamageSource.MAGIC, 1.0F);
				}
			}
		}
	}
}
