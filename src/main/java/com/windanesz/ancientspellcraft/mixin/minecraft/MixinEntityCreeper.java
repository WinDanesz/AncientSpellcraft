package com.windanesz.ancientspellcraft.mixin.minecraft;

import com.windanesz.ancientspellcraft.mixin.modrefs.HandleCatEye;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityCreeper.class)
public abstract class MixinEntityCreeper {


	@Inject(method = "initEntityAI", at = @At("TAIL"))
	private void addFearCatEyeCharm(CallbackInfo ci) {
		((EntityCreeper) (Object) this).tasks.addTask(3, new EntityAIAvoidEntity<>((EntityCreeper) (Object) this,
				EntityPlayer.class,
				HandleCatEye::shouldAvoid,
				7.0F, 1.0D, 1.2D));
		((EntityCreeper) (Object) this).targetTasks.taskEntries.removeIf(entityAITaskEntry -> entityAITaskEntry.action instanceof EntityAINearestAttackableTarget);
		((EntityCreeper) (Object) this).targetTasks.addTask(1,
				new EntityAINearestAttackableTarget<>((EntityCreeper) (Object) this, EntityPlayer.class, 1, true,
						true, HandleCatEye::shouldAvoid));
	}
}
