package com.windanesz.ancientspellcraft.mixin;

import com.windanesz.ancientspellcraft.item.ItemSageTome;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public class MixinItemRenderer {

	@Inject(at = @At("HEAD"), method = "renderItem", cancellable = true)
	private void ancientspellcraft$renderItem(EntityLivingBase entity, ItemStack stack, ItemCameraTransforms.TransformType type, CallbackInfo ci) {
		if (stack.getItem() instanceof ItemSageTome) {
			GlStateManager.pushMatrix();

			IBakedModel bakedModel = Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getModelManager().getModel(((ItemSageTome) stack.getItem()).getSpecialModel());
			Minecraft.getMinecraft().getRenderItem().renderItem(stack, bakedModel);

			GlStateManager.popMatrix();
			ci.cancel();
		}
	}

	@Inject(at = @At("HEAD"), method = "renderItemSide", cancellable = true)
	private void ancientspellcraft$renderItemSide(EntityLivingBase entity, ItemStack stack, ItemCameraTransforms.TransformType type, boolean leftHanded, CallbackInfo ci) {
		if (stack.getItem() instanceof ItemSageTome) {
			GlStateManager.pushMatrix();

			IBakedModel bakedModel = Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getModelManager().getModel(((ItemSageTome) stack.getItem()).getSpecialModel());
			Minecraft.getMinecraft().getRenderItem().renderItemModel(stack, bakedModel, type, leftHanded);

			GlStateManager.popMatrix();
			ci.cancel();
		}
	}

}