package com.windanesz.ancientspellcraft.mixin.modrefs;

import com.windanesz.ancientspellcraft.registry.ASItems;
import electroblob.wizardry.item.ItemArtefact;
import net.minecraft.entity.player.EntityPlayer;

public class HandleCatEye {
	public static boolean shouldAvoid(EntityPlayer player) {
		return ItemArtefact.isArtefactActive(player, ASItems.charm_cat_eye);
	}
}
