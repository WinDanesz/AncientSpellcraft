package com.windanesz.ancientspellcraft.client.renderer;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftItems;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftSpells;
import electroblob.wizardry.Wizardry;
import electroblob.wizardry.data.WizardData;
import electroblob.wizardry.item.ISpellCastingItem;
import electroblob.wizardry.item.ItemArtefact;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.spell.Transportation;
import electroblob.wizardry.util.Location;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DimensionType;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.opengl.GL11;

import java.util.List;

import static electroblob.wizardry.util.GeometryUtils.getCentre;

/**
 * Author: WinDanesz
 * Adapted from: Electroblob's RenderTransportationUI
 */
@Mod.EventBusSubscriber(Side.CLIENT)
public class RenderTransportationPortalUI {

	private static final ResourceLocation TEXTURE = new ResourceLocation(Wizardry.MODID, "textures/gui/transportation_marker.png");
	private static final ResourceLocation TEXTURE_OTHER_DIM = new ResourceLocation(AncientSpellcraft.MODID, "textures/gui/transportation_marker_otherdim.png");

	// I can't get rid of the view bobbing with RenderWorldLastEvent, is there an alternative?
	@SubscribeEvent
	public static void onRenderWorldLastEvent(RenderWorldLastEvent event) {

		// Only render in first person
		if (Minecraft.getMinecraft().gameSettings.thirdPersonView != 0)
			return;

		EntityPlayer player = Minecraft.getMinecraft().player;

		ItemStack stack = player.getHeldItemMainhand();
		if (!(stack.getItem() instanceof ISpellCastingItem)) {
			stack = player.getHeldItemOffhand();
			if (!(stack.getItem() instanceof ISpellCastingItem))
				return;
		}

		if (((ISpellCastingItem) stack.getItem()).getCurrentSpell(stack) == AncientSpellcraftSpells.transportation_portal
				&& ItemArtefact.isArtefactActive(player, WizardryItems.charm_transportation)) {

			WizardData data = WizardData.get(player);
			if (data == null)
				return;

			List<Location> locations = data.getVariable(Transportation.LOCATIONS_KEY);

			if (locations == null)
				return;

			GlStateManager.pushMatrix();

			Vec3d origin = player.getPositionEyes(event.getPartialTicks());
			GlStateManager.translate(0, origin.y - Minecraft.getMinecraft().getRenderManager().viewerPosY, 0);

			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder buffer = tessellator.getBuffer();

			Location target = Transportation.getLocationAimedAt(player, locations, event.getPartialTicks());
			boolean rift_active = ItemArtefact.isArtefactActive(player, AncientSpellcraftItems.charm_rift_bottle);

			for (Location location : locations) {
				boolean otherdim = false;

				if (location.dimension != player.dimension) {
					otherdim = true;
					if (!rift_active) {
						continue;
					}
				}

				GlStateManager.pushMatrix();
				GlStateManager.enableBlend();
				GlStateManager.disableLighting();
				GlStateManager.disableDepth();
				OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240, 240);
				GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
				GlStateManager.color(1, 1, 1, 1);

				if (otherdim) {
					Minecraft.getMinecraft().renderEngine.bindTexture(TEXTURE_OTHER_DIM);
				} else {
					Minecraft.getMinecraft().renderEngine.bindTexture(TEXTURE);
				}

				buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);

				Vec3d position = getCentre(location.pos).subtract(origin);
				double distance = position.length();
				// The icon lines up perfectly if you render it at actual distance, otherwise view bobbing messes things up
				// However, if that's outside the render distance it won't render at all! To fudge our way around this
				// problem, we're capping the distance to just below the render distance and adjusting the scale accordingly
				double distanceCap = Minecraft.getMinecraft().gameSettings.renderDistanceChunks * 16 - 8;
				double displayDist = distance > distanceCap ? distanceCap : distance;
				double factor = displayDist / distance;

				GlStateManager.translate(position.x * factor, position.y * factor, position.z * factor);

				GlStateManager.rotate(-Minecraft.getMinecraft().getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
				GlStateManager.rotate(Minecraft.getMinecraft().getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);

				// Get the angle between the player's look vector and the direction of the stone circle
				double angle = Transportation.getLookDeviationAngle(player, location.pos, event.getPartialTicks());
				double iconSize = Transportation.getIconSize(distance);

				// Now apply a fancy formula to make it enlarge with a nice smooth animation:
				double proximityFactor = Math.max(0, Math.pow(1 - angle / iconSize * angle / iconSize, 3));
				iconSize *= 1 + 0.3 * proximityFactor;
				iconSize *= displayDist; // Adjust the icon size for perspective

				float f = location == target ? 1 : 0.5f; // Makes it obvious which one is being aimed at

				buffer.pos(-iconSize, iconSize, 0).tex(0, 0).color(f, 1, f, f).endVertex();
				buffer.pos(iconSize, iconSize, 0).tex(1, 0).color(f, 1, f, f).endVertex();
				buffer.pos(iconSize, -iconSize, 0).tex(1, 1).color(f, 1, f, f).endVertex();
				buffer.pos(-iconSize, -iconSize, 0).tex(0, 1).color(f, 1, f, f).endVertex();

				tessellator.draw();

				GlStateManager.popMatrix();

				if (location == target) {
					String label;
					if (otherdim) {
						String name = I18n.format("misc." + AncientSpellcraft.MODID +  ":" + DimensionType.getById(location.dimension).getName());
						label = name + " - " + location.pos.getX() + ", " + location.pos.getY() + ", " + location.pos.getZ();
					} else {
						label = location.pos.getX() + ", " + location.pos.getY() + ", " + location.pos.getZ();
					}
					drawLabel(Minecraft.getMinecraft().fontRenderer, label, (float) (position.x * factor),
							(float) (position.y * factor + iconSize * 1.5f), (float) (position.z * factor), (float) displayDist * 0.2f, 0,
							Minecraft.getMinecraft().getRenderManager().playerViewY, Minecraft.getMinecraft().getRenderManager().playerViewX);
				}
			}

			GlStateManager.disableBlend();
			GlStateManager.enableTexture2D();
			GlStateManager.enableLighting();
			GlStateManager.enableDepth();
			GlStateManager.disableRescaleNormal();
			GlStateManager.popMatrix();
		}
	}

	// Copied from EntityRenderer#drawNameplate and tweaked a bit
	private static void drawLabel(FontRenderer fontRendererIn, String str, float x, float y, float z, float scale, int verticalShift, float viewerYaw, float viewerPitch) {

		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);
		GlStateManager.glNormal3f(0.0F, 1.0F, 0.0F);
		GlStateManager.rotate(-viewerYaw, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate(viewerPitch, 1.0F, 0.0F, 0.0F);
		GlStateManager.scale(-0.025F, -0.025F, 0.025F);
		GlStateManager.scale(scale, scale, scale);
		GlStateManager.disableLighting();
		GlStateManager.depthMask(false);

		GlStateManager.disableDepth();

		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		int i = fontRendererIn.getStringWidth(str) / 2;
		GlStateManager.disableTexture2D();
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
		bufferbuilder.pos((double) (-i - 1), (double) (-1 + verticalShift), 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
		bufferbuilder.pos((double) (-i - 1), (double) (8 + verticalShift), 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
		bufferbuilder.pos((double) (i + 1), (double) (8 + verticalShift), 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
		bufferbuilder.pos((double) (i + 1), (double) (-1 + verticalShift), 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
		tessellator.draw();
		GlStateManager.enableTexture2D();

		fontRendererIn.drawString(str, -fontRendererIn.getStringWidth(str) / 2, verticalShift, 0x86ff65);
		GlStateManager.enableDepth();

		GlStateManager.depthMask(true);
		fontRendererIn.drawString(str, -fontRendererIn.getStringWidth(str) / 2, verticalShift, 0x86ff65);
		GlStateManager.enableLighting();
		GlStateManager.disableBlend();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.popMatrix();
	}
}
