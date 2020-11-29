package com.windanesz.ancientspellcraft.potion;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import electroblob.wizardry.potion.PotionMagicEffect;
import net.minecraft.client.Minecraft;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class PotionMetamagicEffect extends PotionMagicEffect {
	private static final ResourceLocation BACKGROUND = new ResourceLocation(AncientSpellcraft.MODID, "textures/gui/metamagic_background.png");

	public PotionMetamagicEffect(String name, boolean isBadEffect, int liquidColour, ResourceLocation texture) {
		super(isBadEffect, liquidColour, texture);
		// This needs to be here because registerPotionAttributeModifier doesn't like it if the potion has no name yet.
		this.setPotionName("potion." + AncientSpellcraft.MODID + ":" + name);
	}

	//	 re-adding the timer, without amplifier
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

	@Override
	@SideOnly(Side.CLIENT)
	public void renderHUDEffect(int x, int y, PotionEffect effect, net.minecraft.client.Minecraft mc, float alpha) {

		net.minecraft.client.renderer.GlStateManager.color(1, 1, 1, 1);
		mc.renderEngine.bindTexture(BACKGROUND);
		electroblob.wizardry.client.DrawingUtils.drawTexturedRect(x, y, 141, 0, 24, 24, 256, 256);
		super.renderHUDEffect(x, y, effect, mc, alpha);
	}

}
