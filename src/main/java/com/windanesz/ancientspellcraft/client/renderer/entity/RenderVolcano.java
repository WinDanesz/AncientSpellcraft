package com.windanesz.ancientspellcraft.client.renderer.entity;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.client.model.ModelVolcano;
import com.windanesz.ancientspellcraft.entity.EntityVolcano;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

@SideOnly(Side.CLIENT)
public class RenderVolcano extends RenderLivingBase<EntityVolcano> {
	private static final ResourceLocation VOLCANO_TEXTURE = new ResourceLocation(AncientSpellcraft.MODID, "textures/entity/volcano.png");

	public RenderVolcano(RenderManager renderManager) {
		super(renderManager, new ModelVolcano(), 0.5F);
	}

	@Nullable
	@Override
	protected ResourceLocation getEntityTexture(EntityVolcano entity) {
		return VOLCANO_TEXTURE;
	}

}
