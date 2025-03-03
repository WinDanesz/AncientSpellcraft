package com.windanesz.ancientspellcraft.mixin.minecraft;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityLivingBase.class)
public abstract class MixinIsEntityUndead {

	@Shadow
	public abstract boolean isEntityUndead();

	@Unique
	private static ResourceLocation CURSE_OF_UNDEATH = new ResourceLocation("ebwizardry:curse_of_undeath");

	@Inject(method = "isEntityUndead", at = @At("RETURN"), cancellable = true)
	public void modifyUndeadStatus(CallbackInfoReturnable<Boolean> cir) {
		Potion potion = ForgeRegistries.POTIONS.getValue(CURSE_OF_UNDEATH);
		if (((EntityLivingBase) (Object) this).ticksExisted != 0 && potion != null) {
			//  net.minecraft.entity.EntityLivingBase.activePotionsMap is not yet initialized when the entity is not yet constructed
			cir.setReturnValue(((EntityLivingBase) (Object) this).isPotionActive(potion) || cir.getReturnValue());
		}
	}
}