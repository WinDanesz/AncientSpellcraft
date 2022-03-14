package com.windanesz.ancientspellcraft.potion;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.client.ClientEventHandler;
import com.windanesz.ancientspellcraft.registry.ASPotions;
import electroblob.wizardry.Wizardry;
import electroblob.wizardry.potion.PotionMagicEffect;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

// Also see com.windanesz.ancientspellcraft.handler.ASEventHandler.onPotionApplicableEvent
@Mod.EventBusSubscriber()
public class PotionEagleEye extends PotionMagicEffect {

	public PotionEagleEye() {
		super(false, 0xffc800, new ResourceLocation(AncientSpellcraft.MODID, "textures/gui/potion_icon_eagle_eye.png"));
		this.setPotionName("potion." + AncientSpellcraft.MODID + ":eagle_eye");
		this.setBeneficial();
	}

	@Override
	public boolean isReady(int duration, int amplifier) {
		return true;
	}

	@SubscribeEvent
	public static void onPotionAddedEvent(PotionEvent.PotionAddedEvent event) {
		if (event.getEntity() instanceof EntityPlayer && event.getEntity().world.isRemote && event.getPotionEffect().getPotion() == ASPotions.eagle_eye) {
			Wizardry.proxy.playBlinkEffect((EntityPlayer) event.getEntity());

			ClientEventHandler.astralTravelInputTimeout = 10;
			ClientEventHandler.EAGLE_EYE_ENABLED = true;
			ClientEventHandler.x = (int) Minecraft.getMinecraft().player.posX;
			ClientEventHandler.y = (int) Minecraft.getMinecraft().player.posY + 50;
			ClientEventHandler.z = (int) Minecraft.getMinecraft().player.posZ;
		}
	}

}


