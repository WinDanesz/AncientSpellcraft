package com.windanesz.ancientspellcraft.client.renderer.entity;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.entity.living.EntityStoneGuardian;
import net.minecraft.client.model.ModelZombie;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderStoneGuardian extends RenderBiped<EntityStoneGuardian> {
	private static final ResourceLocation TEXTURE = new ResourceLocation(AncientSpellcraft.MODID, "textures/entity/stone_guardian.png");

	public RenderStoneGuardian(RenderManager renderManagerIn) {
		super(renderManagerIn, new ModelZombie(), 0.5F);
		//this.addLayer(new LayerStoneGuardian<>(this));
		this.addLayer(new LayerBipedArmor(this) {
			protected void initArmor() {
				this.modelLeggings = new ModelZombie(0.5F, true);
				this.modelArmor = new ModelZombie(1.0F, true);
			}
		});
	}

	protected ResourceLocation getEntityTexture(EntityStoneGuardian entity) {
		return TEXTURE;
	}
}
