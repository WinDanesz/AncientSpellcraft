package com.windanesz.ancientspellcraft.client.model;

import net.minecraft.client.model.ModelZombie;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelZombie2 extends ModelZombie
{
    public ModelZombie2() {
        this(0.0F, false);
    }

    public ModelZombie2(float modelSize, boolean p_i1168_2_) {
        super(modelSize, p_i1168_2_);
        this.bipedRightArm.cubeList.clear();
        this.bipedRightArm.addBox(-2.0F, -2.0F, -2.0F, 4, 12, 4, modelSize);
        this.bipedRightArm.setRotationPoint(-5.0F, 2.0F + 0.0f, 0.0F);
    }

//    public void setLivingAnimations(EntityLivingBase entity, float limbSwing, float limbSwingAmount, float partialTickTime)
//    {
//        this.rightArmPose = ModelBiped.ArmPose.EMPTY;
//        this.leftArmPose = ModelBiped.ArmPose.EMPTY;
//        ItemStack itemstack = entity.getHeldItem(EnumHand.MAIN_HAND);
//
//        if (itemstack.getItem() instanceof net.minecraft.item.ItemBow && ((EntityAnimatedItem)entity).isSwingingArms())
//        {
//            if (entity.getPrimaryHand() == EnumHandSide.RIGHT)
//            {
//                this.rightArmPose = ModelBiped.ArmPose.BOW_AND_ARROW;
//            }
//            else
//            {
//                this.leftArmPose = ModelBiped.ArmPose.BOW_AND_ARROW;
//            }
//        }
//
//        super.setLivingAnimations(entity, limbSwing, limbSwingAmount, partialTickTime);
//    }
//
//    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entity)
//    {
//        super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entity);
//        ItemStack itemstack = ((EntityLivingBase)entity).getHeldItemMainhand();
//        EntityAnimatedItem animatedItem = (EntityAnimatedItem) entity;
//
//        if (animatedItem.isSwingingArms() && (itemstack.isEmpty() || !(itemstack.getItem() instanceof net.minecraft.item.ItemBow)))
//        {
//            float f = MathHelper.sin(this.swingProgress * (float)Math.PI);
//            float f1 = MathHelper.sin((1.0F - (1.0F - this.swingProgress) * (1.0F - this.swingProgress)) * (float)Math.PI);
//            this.bipedRightArm.rotateAngleZ = 0.0F;
//            this.bipedLeftArm.rotateAngleZ = 0.0F;
//            this.bipedRightArm.rotateAngleY = -(0.1F - f * 0.6F);
//            this.bipedLeftArm.rotateAngleY = 0.1F - f * 0.6F;
//            this.bipedRightArm.rotateAngleX = -((float)Math.PI / 2F);
//            this.bipedLeftArm.rotateAngleX = -((float)Math.PI / 2F);
//            this.bipedRightArm.rotateAngleX -= f * 1.2F - f1 * 0.4F;
//            this.bipedLeftArm.rotateAngleX -= f * 1.2F - f1 * 0.4F;
//            this.bipedRightArm.rotateAngleZ += MathHelper.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
//            this.bipedLeftArm.rotateAngleZ -= MathHelper.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
//            this.bipedRightArm.rotateAngleX += MathHelper.sin(ageInTicks * 0.067F) * 0.05F;
//            this.bipedLeftArm.rotateAngleX -= MathHelper.sin(ageInTicks * 0.067F) * 0.05F;
//        }
//    }
//
//    public void postRenderArm(float scale, EnumHandSide side)
//    {
//        float f = side == EnumHandSide.RIGHT ? 1.0F : -1.0F;
//        ModelRenderer modelrenderer = this.getArmForSide(side);
//        modelrenderer.rotationPointX += f;
//        modelrenderer.postRender(scale);
//        modelrenderer.rotationPointX -= f;
//    }
}