package com.windanesz.ancientspellcraft.potion;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;

import java.util.UUID;

public class PotionDegradedArmor extends PotionMagicEffectAS {

	public static final UUID additiveUUID = UUID.fromString("5cd37f8f-569c-4e85-8466-0d028f1e9aa4");

	public PotionDegradedArmor() {
		super("degraded_armor", false, 0x000000, new ResourceLocation(AncientSpellcraft.MODID, "textures/gui/potion_icon_degraded_armor.png"));
		this.registerPotionAttributeModifier(SharedMonsterAttributes.ARMOR, additiveUUID.toString(), -2D, Constants.AttributeModifierOperation.ADD);
	}

	@Override
	public boolean isReady(int duration, int amplifier) {
		return true;
	}

}
