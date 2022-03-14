package com.windanesz.ancientspellcraft.client.renderer;

import com.windanesz.ancientspellcraft.entity.construct.EntityArcaneBarrier;
import com.windanesz.ancientspellcraft.registry.ASPotions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Mod.EventBusSubscriber(Side.CLIENT)
public class RenderArcaneBarrierEventBased {

	private static final float EXPANSION_TIME = 3;

	public static List<EntityArcaneBarrier> barrierList = new ArrayList<>();
	public static List<EntityArcaneBarrier> barrierListNext = new ArrayList<>();

	// First person
	@SubscribeEvent
	public static void onRenderWorldLastEvent(RenderWorldLastEvent event) {

		// Only render in first person
		EntityPlayer player = Minecraft.getMinecraft().player;

		if (player == null) {
			return;
		}

		// todo: this should depend on if the ritual is enabled
		if (Minecraft.getMinecraft().gameSettings.thirdPersonView == 0) {
			renderBarriers(Minecraft.getMinecraft().player, event.getPartialTicks());

			if (Minecraft.getMinecraft().player.isPotionActive(ASPotions.wizard_shield)) { // && data.currentlyCasting().getRegistryName().getPath().equals("wizard_shield")) {
				renderWizardShield(player, event.getPartialTicks());

			}
		}
	}

	// Third person
	@SubscribeEvent
	public static void onRenderPlayerEvent(RenderPlayerEvent.Pre event) {
		if ((Minecraft.getMinecraft().gameSettings.thirdPersonView != 0))
			renderBarriers(event.getEntityPlayer(), event.getPartialRenderTick());
	}

	// Third person
	@SubscribeEvent
	public static void onRenderPlayerEvent(RenderPlayerEvent.Post event) {
		if (Minecraft.getMinecraft().player.isPotionActive(ASPotions.wizard_shield)) { // && data.currentlyCasting().getRegistryName().getPath().equals("wizard_shield")) {

			renderWizardShield(event.getEntityPlayer(), event.getPartialRenderTick());
		}
	}

	//////////////////////////////////////////////////////////////////////////////////////

	@SuppressWarnings("Duplicates")
	private static void renderWizardShield(EntityPlayer player, Float partialTicks) {
		PotionEffect effect = player.getActivePotionEffect(ASPotions.wizard_shield);
		float amp = 0.3f;
		if (effect != null) {
			amp = Math.max(0.3f, Math.min(1.8f, effect.getAmplifier() * 0.1f + 0.1f));
		}

		GlStateManager.pushMatrix();

		GlStateManager.disableLighting();
		GlStateManager.enableBlend();
		GlStateManager.disableTexture2D();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240f, 240f);
		GlStateManager.translate(0, (player.isSneaking() ? player.getEyeHeight() - 0.3d : player.getEyeHeight() - 0.6f), 0);
		//		GlStateManager.translate(player.posX, player.posY + (player.isSneaking() ? player.getEyeHeight() - 0.3d : player.getEyeHeight() - 0.6f), player.posZ);
		float latStep = (float) Math.PI / 20;
		float longStep = (float) Math.PI / 20;

		float pulse = MathHelper.sin((player.ticksExisted + partialTicks) / 10f);

		float r = 0.85f, g = 0.85f + 0.05f * pulse, b = 1;

		float radius = 1.3f;

		// Draw the inside first
		drawSphere((0.1f + amp) * 0.12f + radius - 0.1f - 0.025f * pulse, latStep, longStep, true, r, g, b, Math.min(0.5f, 0.5f * amp));
		drawSphere((0.1f + amp) * 0.12f + radius + 0.3f - 0.1f - 0.025f * -pulse, latStep, longStep, false, 1, 1, 1, 0.6f * 0.5f * amp);
		drawSphere((0.1f + amp) * 0.12f + radius + 0.3f - 0.025f * -pulse, latStep, longStep, false, r, g, b, 0.4f * 0.5f * amp);

		GlStateManager.enableTexture2D();
		GlStateManager.enableLighting();
		GlStateManager.disableBlend();

		GlStateManager.popMatrix();
	}

	private static void renderBarriers(EntityPlayer player, float partialTicks) {

		// ugly hack but does the job.. only has an effect if the player closes a world and reloads
		if (player.ticksExisted < 40) {
			barrierList = barrierListNext = new ArrayList<>();
		}

		for (Iterator<EntityArcaneBarrier> iterator = barrierListNext.iterator(); iterator.hasNext(); ) {

			EntityArcaneBarrier barrier = iterator.next();

			if (barrier != null && barrier.dimension == player.dimension) {

				double x = barrier.posX - player.posX;
				double y = barrier.posY - player.posY;
				double z = barrier.posZ - player.posZ;

				doRender(barrier, x, y, z, barrier.rotationYaw, partialTicks);
			}
		}

		barrierListNext = new ArrayList<>(barrierList);

	}

	@SuppressWarnings("Duplicates")
	private static void doRender(EntityArcaneBarrier entity, double x, double y, double z, float yaw, float partialTicks) {
		// For now we're just using a UV sphere

		GlStateManager.pushMatrix();
		GlStateManager.pushMatrix();

		GlStateManager.disableLighting();
		GlStateManager.enableBlend();
		GlStateManager.disableTexture2D();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240f, 240f);

		GlStateManager.translate(x, y, z);

		float latStep = (float) Math.PI / 20;
		float longStep = (float) Math.PI / 20;

		float pulse = MathHelper.sin((entity.ticksExisted + partialTicks) / 10f);

		float r = entity.getColour().getR(), g = entity.getColour().getG(), b = entity.getColour().getB();
		//		float r = 0.67f, g = 0.23f + 0.05f, b = 0.85f;

		float radius = entity.getRadius();
		float a = 0.5f;

		if (entity.ticksExisted > entity.lifetime - EXPANSION_TIME) {
			radius *= 1 + 0.2f * (entity.ticksExisted + partialTicks - (entity.lifetime - EXPANSION_TIME)) / EXPANSION_TIME;
			a *= Math.max(0, 1 - (entity.ticksExisted + partialTicks - (entity.lifetime - EXPANSION_TIME)) / EXPANSION_TIME);
		} else if (entity.ticksExisted < EXPANSION_TIME) {
			radius *= 1 - (EXPANSION_TIME - entity.ticksExisted - partialTicks) / EXPANSION_TIME;
			a *= 1 - (EXPANSION_TIME - entity.ticksExisted - partialTicks) / EXPANSION_TIME;
		}

		// Draw the inside first
		drawSphere(radius - 0.1f - 0.025f * pulse, latStep, longStep, true, r, g, b, a);
		drawSphere(radius - 0.1f - 0.025f * pulse, latStep, longStep, false, 1, 1, 1, a);
		drawSphere(radius, latStep, longStep, false, r, g, b, 0.7f * a);

		GlStateManager.enableTexture2D();
		GlStateManager.enableLighting();
		GlStateManager.disableBlend();

		GlStateManager.popMatrix();
		GlStateManager.popMatrix();
	}

	/**
	 * Draws a sphere (using lat/long triangles) with the given parameters.
	 *
	 * @param radius   The radius of the sphere.
	 * @param latStep  The latitude step; smaller is smoother but increases performance cost.
	 * @param longStep The longitude step; smaller is smoother but increases performance cost.
	 * @param inside   Whether to draw the outside or the inside of the sphere.
	 * @param r        The red component of the sphere colour.
	 * @param g        The green component of the sphere colour.
	 * @param b        The blue component of the sphere colour.
	 * @param a        The alpha component of the sphere colour.
	 */
	@SuppressWarnings("Duplicates")
	private static void drawSphere(float radius, float latStep, float longStep, boolean inside, float r, float g, float b, float a) {

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();

		buffer.begin(GL11.GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION_COLOR);

		boolean goingUp = inside;

		buffer.pos(0, goingUp ? -radius : radius, 0).color(r, g, b, a).endVertex(); // Start at the north pole

		for (float longitude = -(float) Math.PI; longitude <= (float) Math.PI; longitude += longStep) {

			// Leave the poles out since they only have a single point per stack instead of two
			for (float theta = (float) Math.PI / 2 - latStep; theta >= -(float) Math.PI / 2 + latStep; theta -= latStep) {

				float latitude = goingUp ? -theta : theta;

				float hRadius = radius * MathHelper.cos(latitude);
				float vy = radius * MathHelper.sin(latitude);
				float vx = hRadius * MathHelper.sin(longitude);
				float vz = hRadius * MathHelper.cos(longitude);

				buffer.pos(vx, vy, vz).color(r, g, b, a).endVertex();

				vx = hRadius * MathHelper.sin(longitude + longStep);
				vz = hRadius * MathHelper.cos(longitude + longStep);

				buffer.pos(vx, vy, vz).color(r, g, b, a).endVertex();
			}

			// The next pole
			buffer.pos(0, goingUp ? radius : -radius, 0).color(r, g, b, a).endVertex();

			goingUp = !goingUp;
		}

		tessellator.draw();
	}

}
