package com.windanesz.ancientspellcraft.potion;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import electroblob.wizardry.item.IManaStoringItem;
import electroblob.wizardry.item.ISpellCastingItem;
import electroblob.wizardry.item.IWorkbenchItem;
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
			if (player.getHeldItemMainhand().getItem() instanceof IManaStoringItem && player.getHeldItemMainhand().getItem() instanceof ISpellCastingItem &&
					player.getHeldItemMainhand().getItem() instanceof IWorkbenchItem) {
				refillMana(player, player.getHeldItemMainhand());
			} else if (player.getHeldItemOffhand().getItem() instanceof IManaStoringItem && player.getHeldItemOffhand().getItem() instanceof ISpellCastingItem &&
					player.getHeldItemOffhand().getItem() instanceof IWorkbenchItem) {
				refillMana(player, player.getHeldItemOffhand());
			}
		}
	}

	private void refillMana(EntityPlayer player, ItemStack wand) {
		if (!player.world.isRemote && !(((IManaStoringItem) wand.getItem()).isManaFull(wand))) {
			((IManaStoringItem) wand.getItem()).rechargeMana(wand, 1);
		}
	}
}

