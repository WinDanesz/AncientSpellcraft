package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.registry.ASItems;
import com.windanesz.ancientspellcraft.registry.ASPotions;
import electroblob.wizardry.spell.SpellBuff;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod.EventBusSubscriber(Side.CLIENT)
public class LavaVision extends SpellBuff {

	public LavaVision() {
		super(AncientSpellcraft.MODID, "lava_vision", 245, 70, 1, () -> ASPotions.lava_vision);
		soundValues(0.7f, 1.2f, 0.4f);
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public static void onFogDensityEvent(EntityViewRenderEvent.FogDensity event) {
		if (event.getEntity() instanceof EntityPlayer && ((EntityPlayer) event.getEntity()).isPotionActive(ASPotions.lava_vision)) {
			if (event.getState().getMaterial() == Material.LAVA) {
				GlStateManager.setFog(GlStateManager.FogMode.EXP);
				event.setDensity(0.2f);
				event.setCanceled(true);
			}
		}
	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == ASItems.ancient_spellcraft_spell_book || item == ASItems.ancient_spellcraft_scroll;
	}
}
