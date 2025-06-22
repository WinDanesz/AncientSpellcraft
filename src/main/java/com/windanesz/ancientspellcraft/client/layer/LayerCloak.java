package com.windanesz.ancientspellcraft.client.layer;

import com.windanesz.ancientspellcraft.registry.ASItems;
import com.windanesz.ancientspellcraft.client.model.ModelCloak;
import electroblob.wizardry.item.ItemArtefact;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class LayerCloak implements LayerRenderer<AbstractClientPlayer> {

    private final RenderPlayer playerRenderer;
    private final ModelCloak cloakModel = new ModelCloak();
    private final ResourceLocation capeTexture;

    public LayerCloak(RenderPlayer playerRenderer, ResourceLocation capeTexture) {
        this.playerRenderer = playerRenderer;
        this.capeTexture = capeTexture;
    }
    
    // Default cape texture for convenience
    public LayerCloak(RenderPlayer playerRenderer) {
        this(playerRenderer, new ResourceLocation("ancientspellcraft", "textures/armour/cape.png"));
    }

    @Override
    public void doRenderLayer(AbstractClientPlayer entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        // Only render cloak if player has the wardrobe charm
        if (!ItemArtefact.isArtefactActive(entitylivingbaseIn, ASItems.charm_wardrobe)) {
            return;
        }
        
        GlStateManager.pushMatrix();
        
        // Apply player transformations
        if (entitylivingbaseIn.isSneaking()) {
            GlStateManager.translate(0.0F, 0.2F, 0.0F);
        }
        
        // Position the cloak properly at shoulder level
        GlStateManager.translate(0.0F, 0.2F, -0.05F); // Move cape down and closer to back
        
        // Disable texture binding to use solid colors
        GlStateManager.disableTexture2D();
        
        // Enable blending for better visual effect
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        
        // Enable lighting for better depth perception
        GlStateManager.enableLighting();
        
        // Apply the model's animations
        cloakModel.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entitylivingbaseIn);
        
        // Store current texture state
        GlStateManager.pushMatrix();
        
        // Enable texture binding
        GlStateManager.enableTexture2D();
        
        // Render main cloak with texture
        cloakModel.cloak.render(scale);
        
        // Reset color to default
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        
        // Restore previous state
        GlStateManager.popMatrix();
        
        GlStateManager.popMatrix();
    }

    @Override
    public boolean shouldCombineTextures() {
        return false;
    }
} 