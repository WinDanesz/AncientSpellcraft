package com.windanesz.ancientspellcraft.mixin;

import electroblob.wizardry.block.BlockReceptacle;
import electroblob.wizardry.constants.Element;
import electroblob.wizardry.item.IManaStoringItem;
import electroblob.wizardry.item.ItemWand;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.registry.WizardrySounds;
import electroblob.wizardry.util.AllyDesignationSystem;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.WandHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ItemWand.class)
public class ItemWandMixin {


	@Shadow public Element element;

	@Inject(method = "hitEntity", at = @At("RETURN"))
	private void modifyItemList(ItemStack stack, EntityLivingBase originalTarget, EntityLivingBase wielder, CallbackInfoReturnable<Boolean> cir) {
		int level = WandHelper.getUpgradeLevel(stack, WizardryItems.melee_upgrade);
		if (level > 0) {
			World world = wielder.world;

			// Left as EntityLivingBase because why not be able to move armour stands around?
			List<EntityLivingBase> targets = EntityUtils.getEntitiesWithinRadius(2, originalTarget.posX, originalTarget.posY, originalTarget.posZ, world, EntityLivingBase.class);

			int remainingTargets = level;
			for (EntityLivingBase target : targets) {

				if(remainingTargets > 0 && target != wielder && target != null && !AllyDesignationSystem.isAllied(wielder, target)){
					remainingTargets--;
					Vec3d origin = new Vec3d(wielder.posX, wielder.posY + wielder.getEyeHeight() - 0.25, wielder.posZ);
					Vec3d vec = target.getPositionEyes(1).subtract(origin).normalize();

					if(!world.isRemote){

						float velocity = Math.max(0.4f, 0.4f * (level + ((ItemWand) (Object) this).tier.ordinal() * 0.5f));

						target.motionX = vec.x * velocity;
						target.motionY = vec.y * 0.4f;
						target.motionZ = vec.z * velocity;

						// Player motion is handled on that player's client so needs packets
						if(target instanceof EntityPlayerMP){
							((EntityPlayerMP)target).connection.sendPacket(new SPacketEntityVelocity(target));
						}

						double motionX = target.motionX;
						double motionY = target.motionY;
						double motionZ = target.motionZ;
						if (wielder instanceof EntityPlayer) {
							target.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer) wielder), level);
						} else {
							target.attackEntityFrom(DamageSource.causeMobDamage(wielder), level);
						}
						target.motionX = motionX;
						target.motionY = motionY;
						target.motionZ = motionZ;
					}

					if(world.isRemote){

						double distance = target.getDistance(origin.x, origin.y, origin.z);

						int[] colours = BlockReceptacle.PARTICLE_COLOURS.get(((ItemWand) (Object) this).element);
						for(int i = 0; i < 20 + level * 5; i++){
							double x = origin.x + world.rand.nextDouble() - 0.3 + vec.x * distance * 0.3;
							double y = origin.y + world.rand.nextDouble() - 0.3 + vec.y * distance * 0.3;
							double z = origin.z + world.rand.nextDouble() - 0.3 + vec.z * distance * 0.3;

							ParticleBuilder.create(ParticleBuilder.Type.DUST, world.rand,x, y, z, 1, false).scale(world.rand.nextFloat() * 4)
									.clr(colours[1]).fade(colours[2]).time(10).vel(vec.x * 0.8, vec.y * 0.8,vec.z * 0.8).spawn(world);

							//world.spawnParticle(EnumParticleTypes.CLOUD, x, y, z, vec.x, vec.y, vec.z);

						}
					}
				}
			}

		}
	}

	@Inject(method = "onAttackEntityEvent", at = @At("HEAD"), cancellable = true)
	private static void onAttackEntityEventMixin(AttackEntityEvent event, CallbackInfo ci) {

		EntityPlayer wielder = event.getEntityPlayer();
		ItemStack stack = wielder.getHeldItemMainhand(); // Can't melee with offhand items
		World world = wielder.world;
		if(stack.getItem() instanceof IManaStoringItem){

			// Nobody said it had to be a wand, as long as it's got a melee upgrade it counts
			int level = WandHelper.getUpgradeLevel(stack, WizardryItems.melee_upgrade);
			int mana = ((IManaStoringItem)stack.getItem()).getMana(stack);
			Element element = stack.getItem() instanceof ItemWand ? ((ItemWand) stack.getItem()).element : Element.MAGIC;
			if(level > 0 && mana > 0){

				wielder.world.playSound(wielder.posX, wielder.posY, wielder.posZ, WizardrySounds.ITEM_WAND_MELEE, SoundCategory.PLAYERS, 0.75f, 1, false);

				if(wielder.world.isRemote){

					Vec3d origin = new Vec3d(wielder.posX, wielder.posY + wielder.getEyeHeight() - 0.25, wielder.posZ);
					Vec3d vec = event.getTarget().getPositionEyes(1).subtract(origin).normalize();

					double distance = event.getTarget().getDistance(origin.x, origin.y, origin.z);

					int[] colours = BlockReceptacle.PARTICLE_COLOURS.get(element);
					for(int i = 0; i < 25; i++){
						double x = origin.x + world.rand.nextDouble() - 0.3 + vec.x * distance * 0.3;
						double y = origin.y + world.rand.nextDouble() - 0.3 + vec.y * distance * 0.3;
						double z = origin.z + world.rand.nextDouble() - 0.3 + vec.z * distance * 0.3;

						ParticleBuilder.create(ParticleBuilder.Type.DUST, world.rand,x, y, z, 1, false).scale(world.rand.nextFloat() * 4)
								.clr(colours[1]).fade(colours[2]).time(10).vel(vec.x * 0.8, vec.y * 0.8,vec.z * 0.8).spawn(world);

						//world.spawnParticle(EnumParticleTypes.CLOUD, x, y, z, vec.x, vec.y, vec.z);

					}
				}
			}
		}
		ci.cancel();
	}

}