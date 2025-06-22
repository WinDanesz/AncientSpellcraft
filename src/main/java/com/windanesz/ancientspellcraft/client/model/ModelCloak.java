package com.windanesz.ancientspellcraft.client.model;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

public class ModelCloak extends ModelBiped {
    
    public ModelRenderer cloak;
    
    // Store previous rotation values for smooth transitions
    private float prevCloakRotX = 0.0F;
    private float prevCloakRotY = 0.0F;
    private float prevCloakRotZ = 0.0F;
    
    public ModelCloak() {
        super(0.0F, 0.0F, 64, 64);
        
        // Main cloak body - positioned behind the player at shoulder level
        cloak = new ModelRenderer(this, 0, 32);
        cloak.addBox(-6.0F, 0.0F, 0.0F, 12, 20, 1, 0.0F);
        cloak.setRotationPoint(0.0F, -3.0F, 1.5F); // Positioned very close to player's back
        
        // Don't add as children to bipedBody, we'll render them separately
        // This allows for independent animation
    }
    
    @Override
    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        // Set up the model for rendering
        setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entityIn);
        
        // Render the cloak
        cloak.render(scale);
    }
    
    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
        super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);
        
        // Calculate target rotation values
        float targetCloakRotX = 0.0F;
        float targetCloakRotY = 0.0F;
        float targetCloakRotZ = 0.0F;
        
        if (entityIn instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entityIn;
            
            // Get player's movement direction
            float moveForward = player.moveForward;
            float moveStrafing = player.moveStrafing;
            
            // Base movement intensity
            float baseIntensity = limbSwingAmount > 0.01F ? 0.2F : 0.05F;
            
            // Forward/backward movement - cloak should sway backward when moving forward
            if (Math.abs(moveForward) > 0.1F) {
                float forwardSway;
                
                if (moveForward > 0.1F) {
                    // Moving forward - cloak sways backward (positive X rotation)
                    forwardSway = moveForward * baseIntensity * 1.2F;
                    
                    // Check if player is sprinting and add extra backward lean
                    if (player.isSprinting()) {
                        forwardSway += 0.3F; // Extra backward lean when sprinting
                    }
                    
                    // Add light oscillation during movement
                    if (limbSwingAmount > 0.1F) {
                        // Light oscillation that increases with movement speed
                        float oscillation = (float) Math.sin(ageInTicks * 0.15F) * limbSwingAmount * 0.15F;
                        forwardSway += oscillation;
                    }
                } else {
                    // Moving backward - cloak sways forward slightly (negative X rotation)
                    forwardSway = moveForward * baseIntensity * 0.4F;
                }
                
                targetCloakRotX = forwardSway;
            } else {
                // Very gentle wind effect when standing still
                float windEffect = (float) Math.sin(ageInTicks * 0.06F) * 0.02F;
                targetCloakRotX = windEffect;
            }
            
            // Side-to-side movement based on strafing
            if (Math.abs(moveStrafing) > 0.1F) {
                float sideSway = moveStrafing * baseIntensity * 0.3F;
                targetCloakRotZ = sideSway;
            } else {
                // Very gentle side sway when not strafing
                float gentleSway = (float) Math.sin(ageInTicks * 0.08F) * baseIntensity * 0.15F;
                targetCloakRotZ = gentleSway;
            }
            
            // Minimal rotation based on movement
            if (limbSwingAmount > 0.01F) {
                float rotationSway = (float) Math.sin(ageInTicks * 0.1F) * baseIntensity * 0.08F;
                targetCloakRotY = rotationSway;
            }
        }
        
        // Smooth interpolation between previous and target values
        float interpolationSpeed = 0.15F; // Adjust this for faster/slower transitions
        
        cloak.rotateAngleX = prevCloakRotX + (targetCloakRotX - prevCloakRotX) * interpolationSpeed;
        cloak.rotateAngleY = prevCloakRotY + (targetCloakRotY - prevCloakRotY) * interpolationSpeed;
        cloak.rotateAngleZ = prevCloakRotZ + (targetCloakRotZ - prevCloakRotZ) * interpolationSpeed;
        
        // Store current values for next frame
        prevCloakRotX = cloak.rotateAngleX;
        prevCloakRotY = cloak.rotateAngleY;
        prevCloakRotZ = cloak.rotateAngleZ;
    }
} 