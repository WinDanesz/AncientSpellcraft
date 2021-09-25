package com.windanesz.ancientspellcraft.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.item.ItemBow;
import net.minecraft.util.EnumHand;

/**
 * Same as EntityAIAttackRangedBow, but without the strafing logic.
 */
public class EntityAIAttackRangedBowNoStrafing<T extends EntityCreature & IRangedAttackMob> extends EntityAIBase {
	private final T entity;
	private final double moveSpeedAmp;
	private int attackCooldown;
	private final float maxAttackDistance;
	private int attackTime = -1;
	private int seeTime;

	public EntityAIAttackRangedBowNoStrafing(T mob, double moveSpeedAmpIn, int attackCooldownIn, float maxAttackDistanceIn) {
		this.entity = mob;
		this.moveSpeedAmp = moveSpeedAmpIn;
		this.attackCooldown = attackCooldownIn;
		this.maxAttackDistance = maxAttackDistanceIn * maxAttackDistanceIn;
		this.setMutexBits(3);
	}

	public void setAttackCooldown(int p_189428_1_) {
		this.attackCooldown = p_189428_1_;
	}

	public boolean shouldExecute() {
		return this.entity.getAttackTarget() == null ? false : this.isBowInMainhand();
	}

	protected boolean isBowInMainhand() {
		return !this.entity.getHeldItemMainhand().isEmpty() && this.entity.getHeldItemMainhand().getItem() instanceof ItemBow;
	}

	public boolean shouldContinueExecuting() {
		return (this.shouldExecute() || !this.entity.getNavigator().noPath()) && this.isBowInMainhand();
	}

	public void startExecuting() {
		super.startExecuting();
		((IRangedAttackMob) this.entity).setSwingingArms(true);
	}

	public void resetTask() {
		super.resetTask();
		((IRangedAttackMob) this.entity).setSwingingArms(false);
		this.seeTime = 0;
		this.attackTime = -1;
		this.entity.resetActiveHand();
	}

	public void updateTask() {
		EntityLivingBase entitylivingbase = this.entity.getAttackTarget();

		if (entitylivingbase != null) {
			double d0 = this.entity.getDistanceSq(entitylivingbase.posX, entitylivingbase.getEntityBoundingBox().minY, entitylivingbase.posZ);
			boolean flag = this.entity.getEntitySenses().canSee(entitylivingbase);
			boolean flag1 = this.seeTime > 0;

			if (flag != flag1) {
				this.seeTime = 0;
			}

			if (flag) {
				++this.seeTime;
			} else {
				--this.seeTime;
			}

			if (d0 <= (double) this.maxAttackDistance && this.seeTime >= 20) {
				this.entity.getNavigator().clearPath();
			} else {
				this.entity.getNavigator().tryMoveToEntityLiving(entitylivingbase, this.moveSpeedAmp);
			}

				this.entity.getLookHelper().setLookPositionWithEntity(entitylivingbase, 30.0F, 30.0F);

			if (this.entity.isHandActive()) {
				if (!flag && this.seeTime < -60) {
					this.entity.resetActiveHand();
				} else if (flag) {
					int i = this.entity.getItemInUseMaxCount();

					if (i >= 20) {
						this.entity.resetActiveHand();
						((IRangedAttackMob) this.entity).attackEntityWithRangedAttack(entitylivingbase, ItemBow.getArrowVelocity(i));
						this.attackTime = this.attackCooldown;
					}
				}
			} else if (--this.attackTime <= 0 && this.seeTime >= -60) {
				this.entity.setActiveHand(EnumHand.MAIN_HAND);
			}
		}
	}
}
