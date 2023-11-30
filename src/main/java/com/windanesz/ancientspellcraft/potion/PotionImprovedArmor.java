package com.windanesz.ancientspellcraft.potion;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;

import java.util.UUID;

public class PotionImprovedArmor extends PotionMagicEffectAS {

	public static final UUID additiveUUID = UUID.fromString("241b76e9-fa4d-44af-a240-e4a9131c9bbe");

	public PotionImprovedArmor() {
		super("improved_armor", false, 0x000000, new ResourceLocation(AncientSpellcraft.MODID, "textures/gui/potion_icon_improved_armor.png"));
		this.registerPotionAttributeModifier(SharedMonsterAttributes.ARMOR, additiveUUID.toString(), 2D, Constants.AttributeModifierOperation.ADD);
	}

	@Override
	public boolean isReady(int duration, int amplifier) {
		return true;
	}

}
