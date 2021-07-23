package com.windanesz.ancientspellcraft.client.layer;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.client.model.ModelBackpack;
import com.windanesz.ancientspellcraft.client.renderer.RenderMerchantWizard;
import com.windanesz.ancientspellcraft.entity.EntityWizardMerchant;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class LayerBackpack implements LayerRenderer<EntityWizardMerchant> {

	private final RenderMerchantWizard wizardRenderer;
	private final ModelBackpack sheepModel = new ModelBackpack();

	public LayerBackpack(RenderMerchantWizard wizardRenderer) {
		this.wizardRenderer = wizardRenderer;
	}

	public void doRenderLayer(EntityWizardMerchant entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		this.wizardRenderer.bindTexture(new ResourceLocation(AncientSpellcraft.MODID, "textures/entity/layer/backpack.png"));
		sheepModel.render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);

	}

	public boolean shouldCombineTextures() {
		return false;
	}
}