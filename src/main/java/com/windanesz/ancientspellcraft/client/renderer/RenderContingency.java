package com.windanesz.ancientspellcraft.client.renderer;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.Settings;
import com.windanesz.ancientspellcraft.spell.Contingency;
import electroblob.wizardry.client.DrawingUtils;
import electroblob.wizardry.data.WizardData;
import electroblob.wizardry.spell.Spell;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.util.LinkedHashMap;
import java.util.Map;

import static net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType.HOTBAR;

/**
 * Author: WinDanesz
 */
@Mod.EventBusSubscriber(Side.CLIENT)
public class RenderContingency {

	private static final int SPELL_ICON_SIZE = 16;

	private static final ResourceLocation CONTINGENCY_ARROW = new ResourceLocation(AncientSpellcraft.MODID, "textures/gui/contingency_arrow.png");

	@SubscribeEvent
	public static void draw(RenderGameOverlayEvent.Post event) {
		if (Settings.clientSettings.show_contingency_hud) {

			if (event.getType() == HOTBAR) {

				int x = 20;
				if (!Settings.clientSettings.contingency_hud_left_side_position) {
					x = event.getResolution().getScaledWidth() - (x + 60);
				}

				EntityPlayer player = Minecraft.getMinecraft().player;
				LinkedHashMap<Spell, Spell> contingencyMap = new LinkedHashMap<>();

				WizardData data = WizardData.get(player);
				if (data != null) {

					NBTTagCompound activeContingencies = data.getVariable(Contingency.ACTIVE_CONTINGENCIES);
					if (activeContingencies != null) {

						for (String key : activeContingencies.getKeySet()) {

							Spell contingency = Spell.registry.getValue(new ResourceLocation(key));
							Spell storedSpell = Spell.registry.getValue(new ResourceLocation(activeContingencies.getString(key)));
							contingencyMap.putIfAbsent(contingency, storedSpell);
						}
					}
				}

				int i = 0;
				for (Map.Entry<Spell, Spell> entry : contingencyMap.entrySet()) {

					Minecraft mc = Minecraft.getMinecraft();
					int y = 40 + 18 * i;

					// contingency spell
					renderRect(mc, x, y, 0, 0, SPELL_ICON_SIZE, SPELL_ICON_SIZE, SPELL_ICON_SIZE, SPELL_ICON_SIZE, entry.getKey().getIcon());

					// arrow
					renderRect(mc, x + 16, y, 0, 0, SPELL_ICON_SIZE, SPELL_ICON_SIZE, SPELL_ICON_SIZE, SPELL_ICON_SIZE, CONTINGENCY_ARROW);

					// stored spell
					renderRect(mc, x + 32, y, 0, 0, SPELL_ICON_SIZE, SPELL_ICON_SIZE, SPELL_ICON_SIZE, SPELL_ICON_SIZE, entry.getValue().getIcon());

					i++;
				}
			}
		}
	}

	private static void renderRect(Minecraft mc, int x, int y, int u, int v, int width, int height, int textureWidth, int textureHeight, ResourceLocation texture) {
		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.color(1, 1, 1);

		mc.renderEngine.bindTexture(texture);

		DrawingUtils.drawTexturedRect(x, y, u, v, width, height, textureWidth, textureHeight);
		GlStateManager.popMatrix();
	}
}
