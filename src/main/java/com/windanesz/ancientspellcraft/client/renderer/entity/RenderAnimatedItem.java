package com.windanesz.ancientspellcraft.client.renderer.entity;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.client.model.ModelZombie2;
import com.windanesz.ancientspellcraft.entity.living.EntityAnimatedItem;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderAnimatedItem extends RenderBiped<EntityAnimatedItem> {
	static final ResourceLocation TEXTURE = new ResourceLocation(AncientSpellcraft.MODID, "textures/entity/animated_item.png");

	public RenderAnimatedItem(RenderManager renderManagerIn) {
		super(renderManagerIn, new ModelZombie2(), 0.0F);
		LayerBipedArmor layerbipedarmor = new LayerBipedArmor(this) {
			protected void initArmor() {
				this.modelLeggings = new ModelZombie2(0.5F, true);
				this.modelArmor = new ModelZombie2(1.0F, true);
			}
		};
		this.addLayer(layerbipedarmor);
	}

	protected ResourceLocation getEntityTexture(EntityAnimatedItem entity) {
		return TEXTURE;
	}

}

