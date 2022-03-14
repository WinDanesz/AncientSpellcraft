package com.windanesz.ancientspellcraft.potion;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.registry.ASPotions;
import electroblob.wizardry.Wizardry;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

@Mod.EventBusSubscriber(Side.SERVER)
public class PotionCurseDeath extends PotionCurseAS {
	private static final ResourceLocation BACKGROUND = new ResourceLocation(Wizardry.MODID, "textures/gui/curse_background.png");

	public PotionCurseDeath() {
		super("curse_of_death", true, 0x1c0b20, new ResourceLocation(AncientSpellcraft.MODID, "textures/gui/potion_icon_curse_of_death.png"));
	}

	// re-adding the timer, without amplifier
	@Override
	@SideOnly(Side.CLIENT)
	public void renderInventoryEffect(int x, int y, PotionEffect effect, Minecraft mc) {

		mc.renderEngine.bindTexture(BACKGROUND);
		electroblob.wizardry.client.DrawingUtils.drawTexturedRect(x, y, 0, 0, 140, 32, 256, 256);

		drawIcon(x + 6, y + 7, effect, mc);

		String name = net.minecraft.client.resources.I18n.format(this.getName());

		// Amplifier 0 (which would be I) is not rendered and the tooltips only go up to X (amplifier 9)
		// The vanilla implementation uses elseifs and only goes up to 4... how lazy.
		if (effect.getAmplifier() > 0 && effect.getAmplifier() < 10) {
			name = name + " " + net.minecraft.client.resources.I18n.format("enchantment.level." + (effect.getAmplifier() + 1));
		}

		List<String> lines = mc.fontRenderer.listFormattedStringToWidth(name, 100);

		int i = 0;
		for (String line : lines) {
			int h = lines.size() == 1 ? 0 : i * (mc.fontRenderer.FONT_HEIGHT + 1);
			mc.fontRenderer.drawStringWithShadow(line, (float) (x + 10 + 18), (float) (y + 6 + h), 0xbf00ee);
			i++;
		}

		String s = Potion.getPotionDurationString(effect, 1.0F);
		int h = 10;
		mc.fontRenderer.drawStringWithShadow(s, (float) (x + 10 + 18), (float) (y + 6 + h), 8355711);
	}

	@SubscribeEvent
	public static void onPotionExpiryEvent(PotionEvent.PotionExpiryEvent event) {

		if (event.getEntity() instanceof EntityPlayer && !event.getEntity().world.isRemote) {

			EntityPlayer player = (EntityPlayer) event.getEntity();

			if (event.getPotionEffect().getPotion() == ASPotions.curse_of_death) {

				player.attackEntityFrom(DamageSource.MAGIC, Float.MAX_VALUE);
			}
		}

	}
}
