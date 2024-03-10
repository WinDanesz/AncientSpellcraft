package com.windanesz.ancientspellcraft.mixin;

import electroblob.wizardry.registry.WizardryPotions;
import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityLivingBase.class)
public abstract class IsEntityUndeadMixin {

	@Shadow public abstract boolean isEntityUndead();

	@Inject(method = "isEntityUndead", at = @At("RETURN"), cancellable = true)
	public void modifyUndeadStatus(CallbackInfoReturnable<Boolean> cir) {
		cir.setReturnValue(((EntityLivingBase) (Object) this).isPotionActive(WizardryPotions.curse_of_undeath) || cir.getReturnValue());
	}
}