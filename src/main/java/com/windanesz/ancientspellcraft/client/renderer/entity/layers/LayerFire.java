package com.windanesz.ancientspellcraft.client.renderer.entity.layers;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftSpells;
import electroblob.wizardry.client.renderer.entity.layers.LayerTiledOverlay;
import electroblob.wizardry.item.ItemWand;
import electroblob.wizardry.registry.WizardryPotions;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;

/**
 * Layer used to render the mind control overlay on creatures with the mind control effect.
 *
 * @author Electroblob
 * @since Wizardry 4.3
 */
public class LayerFire extends LayerTiledOverlay<EntityLivingBase> {

	private static final ResourceLocation TEXTURE = new ResourceLocation(AncientSpellcraft.MODID, "textures/entity/fire_overlay.png");

	public LayerFire(RenderLivingBase<?> renderer) {
		super(renderer);
	}

	@Override
	public boolean shouldRender(EntityLivingBase entity, float partialTicks) {
		return !entity.isInvisible() && entity instanceof EntityPlayer && entity.isHandActive() && entity.getHeldItem(entity.getActiveHand()).getItem() instanceof ItemWand &&
				((ItemWand) entity.getHeldItem(entity.getActiveHand()).getItem()).getCurrentSpell(entity.getHeldItem(entity.getActiveHand())) == AncientSpellcraftSpells.living_comet;
	}

	@Override
	public ResourceLocation getTexture(EntityLivingBase entity, float partialTicks) {
		return TEXTURE;
	}

	@Override
	public void doRenderLayer(EntityLivingBase entity, float limbSwing, float limbSwingAmount, float partialTicks,
			float ageInTicks, float netHeadYaw, float headPitch, float scale) {

		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);

		PotionEffect effect = entity.getActivePotionEffect(WizardryPotions.mind_control);
		if (effect != null) {
			GlStateManager.color(1, 1, 1, Math.min(1, (effect.getDuration() - partialTicks) / 20));
		}

		super.doRenderLayer(entity, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale);

		GlStateManager.disableBlend();
	}

	@Override
	protected void applyTextureSpaceTransformations(EntityLivingBase entity, float partialTicks) {
		float f = entity.ticksExisted + partialTicks;
		GlStateManager.translate(f * 0.003f, f * 0.003f, 0);
	}
}