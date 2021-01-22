package com.windanesz.ancientspellcraft.potion;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import electroblob.wizardry.item.ItemWand;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class PotionManaRegeneration extends PotionMagicEffectAS {

	public PotionManaRegeneration() {
		super("mana_regeneration", false, 0xc558d6, new ResourceLocation(AncientSpellcraft.MODID, "textures/gui/potion_icon_mana_regeneration.png"));
	}

	@Override
	public boolean isReady(int duration, int amplifier) {
		int k = 60 >> amplifier;

		if (k > 0) {
			return duration % k == 0;
		} else {
			return true;
		}
	}

	@Override
	public void performEffect(EntityLivingBase entitylivingbase, int strength) {
		if (entitylivingbase instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) entitylivingbase;
			if (player.getHeldItemMainhand().getItem() instanceof ItemWand) {
				refillMana(player, player.getHeldItemMainhand());
			} else if (player.getHeldItemOffhand().getItem() instanceof ItemWand) {
				refillMana(player, player.getHeldItemOffhand());
			}
		}
	}

	private void refillMana(EntityPlayer player, ItemStack wand) {
		if (!player.world.isRemote && !(((ItemWand) wand.getItem()).isManaFull(wand))) {
			((ItemWand) wand.getItem()).rechargeMana(wand, 1);
		}
	}
}

