package com.windanesz.ancientspellcraft.potion;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.client.ClientEventHandler;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftPotions;
import electroblob.wizardry.potion.PotionMagicEffect;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

// Also see com.windanesz.ancientspellcraft.handler.ASEventHandler.onPotionApplicableEvent
@Mod.EventBusSubscriber()
public class PotionAstralProjection extends PotionMagicEffect {

	public static final ResourceLocation SHADER = new ResourceLocation(AncientSpellcraft.MODID, "shaders/post/sixth_sense.json");

	public PotionAstralProjection() {
		super(false, 0xffc800, new ResourceLocation(AncientSpellcraft.MODID, "textures/gui/potion_icon_astral_travel.png"));
		this.setPotionName("potion." + AncientSpellcraft.MODID + ":astral_travel");
		this.setBeneficial();
	}

	@Override
	public boolean isReady(int duration, int amplifier) {
		return true;
	}

	@SubscribeEvent
	public static void onPotionAddedEvent(PotionEvent.PotionAddedEvent event) {
		if (event.getEntity() instanceof EntityPlayer && event.getEntity().world.isRemote && event.getPotionEffect().getPotion() == AncientSpellcraftPotions.astral_projection) {

			ClientEventHandler.astralTravelInputTimeout = 10;
			ClientEventHandler.ASTRAL_TRAVEL_ENABLED = true;
			ClientEventHandler.x = (int) Minecraft.getMinecraft().player.posX;
			ClientEventHandler.y = (int) Minecraft.getMinecraft().player.posY;
			ClientEventHandler.z = (int) Minecraft.getMinecraft().player.posZ;
		}
	}

}


