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
	private static final ResourceLocation VOLCANO_TEXTURE = new ResourceLocation(AncientSpellcraft.MODID, "textures/entity/volcano_2.png");

	public RenderVolcano(RenderManager renderManager) {
		super(renderManager, new ModelVolcano(), 0.5F);
	}

//
//	public RenderArmorStand(RenderManager manager)
//	{
//		super(manager, new ModelVolcano(), 0.0F);
//		LayerBipedArmor layerbipedarmor = new LayerBipedArmor(this)
//		{
//			protected void initArmor()
//			{
//				this.modelLeggings = new ModelArmorStandArmor(0.5F);
//				this.modelArmor = new ModelArmorStandArmor(1.0F);
//			}
//		};
//		this.addLayer(layerbipedarmor);
//		this.addLayer(new LayerHeldItem(this));
//		this.addLayer(new LayerElytra(this));
//		this.addLayer(new LayerCustomHead(this.getMainModel().bipedHead));
//	}
//

	@Nullable
	@Override
	protected ResourceLocation getEntityTexture(EntityVolcano entity) {
		return VOLCANO_TEXTURE;
	}

}
