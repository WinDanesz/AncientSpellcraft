package com.windanesz.ancientspellcraft.entity.living;

import com.google.common.base.Predicate;
import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.block.BlockSealedStone;
import com.windanesz.ancientspellcraft.entity.projectile.EntityStoneGuardianShard;
import com.windanesz.ancientspellcraft.registry.ASSounds;
import electroblob.wizardry.util.ParticleBuilder;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveToBlock;
import net.minecraft.entity.ai.EntityAIMoveTowardsTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class EntityStoneGuardian extends EntityCreature {

	/**
	 * The resource location for the evil wizard's loot table.
	 */
	private static final ResourceLocation LOOT_TABLE = new ResourceLocation(AncientSpellcraft.MODID, "entities/stone_guardian");

	public boolean isDropLoot() {
		return dropLoot;
	}

	public void setDropLoot(boolean dropLoot) {
		this.dropLoot = dropLoot;
	}

	private boolean dropLoot = true;

	public EntityStoneGuardian(World world) {
		super(world);
		this.setSize(0.6F, 1.95F);
		this.isImmuneToFire = true;
		this.experienceValue = 5;
	}

	protected void entityInit() {
		super.entityInit();
	}

	protected void initEntityAI() {
		System.out.println("hel");
		this.tasks.addTask(2, new EntityAIAttackMelee(this, 0.9D, true));
		this.tasks.addTask(2, new EntityAIMoveTowardsTarget(this, 0.8D, 10.0F));
		this.tasks.addTask(7, new EntityAIMoveToBlock(this, 0.8D, 16) {
			@Override
			protected boolean shouldMoveTo(World worldIn, BlockPos pos) {
				return !isWithinHomeDistanceCurrentPosition();
			}
		});

		this.tasks.addTask(8, new EntityAILookIdle(this));

		this.applyEntityAI();
	}

	@Nullable
	@Override
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
		this.setHomePosAndDistance(this.getPosition(), 6);
		return super.onInitialSpawn(difficulty, livingdata);
	}

	@Override
	protected int getExperiencePoints(EntityPlayer player) {
		return isDropLoot() ? this.experienceValue : 0;
	}

	protected void applyEntityAI() {
		this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
		this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, true));
		this.targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityLivingBase.class,
				0, true, true, this.getTargetSelector()));
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		if (source.isProjectile()) {
			return super.attackEntityFrom(source, (float) (amount * 0.4));
		}
		if (source.isMagicDamage()) {
			return super.attackEntityFrom(source, (float) (amount * 0.1));
		} else {
			return super.attackEntityFrom(source, amount);
		}
	}

	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(15.0D);
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.23000000417232513D);
		this.getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(8.0D);
		this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(20.0D);
		this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.8D);
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(40);
	}

	@Override
	public boolean isPotionApplicable(PotionEffect potioneffectIn) {
		return false;
	}

	public Predicate<EntityLivingBase> getTargetSelector() {
		return entity -> !entity.isInvisible() && !(entity instanceof EntityStoneGuardian);
	}

	@Override
	public boolean isChild() { return false; }

	@Override
	protected void setEquipmentBasedOnDifficulty(DifficultyInstance difficulty) {
		this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(Items.GOLDEN_SWORD));
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
	}

	public void writeEntityToNBT(NBTTagCompound nbttagcompound) {
		super.writeEntityToNBT(nbttagcompound);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbttagcompound) {
		super.readEntityFromNBT(nbttagcompound);
	}

	@Override
	protected Item getDropItem() { return null; }

	@Override
	protected ResourceLocation getLootTable() { return isDropLoot() ? LOOT_TABLE : null; }

	@Override
	public boolean canPickUpLoot() { return false; }

	// This vanilla method has nothing to do with the custom despawn() method.
	@Override
	protected boolean canDespawn() {
		return false;
	}

	@Override
	public boolean getCanSpawnHere() {
		return this.world.getDifficulty() != EnumDifficulty.PEACEFUL;
	}

	@Override
	public boolean canAttackClass(Class<? extends EntityLivingBase> entityType) {
		// Returns true unless the given entity type is a flying entity.
		return !EntityFlying.class.isAssignableFrom(entityType);
	}

	public void onLivingUpdate() {
		this.updateArmSwingProgress();
		float f = this.getBrightness();

		if (f > 0.5F) {
			this.idleTime += 2;
		}

		if (this.ticksExisted % 30 == 0 && this.getHealth() < this.getMaxHealth()) {
			if (this.world.getBlockState(this.getPosition().down()).getBlock() instanceof BlockSealedStone) {
				if (!this.world.isRemote) {this.heal(0.5f);} else {
					// horizontal particle on the floor, always visible
					ParticleBuilder.create(ParticleBuilder.Type.FLASH)
							.pos(this.posX, this.posY + 0.101, this.posZ)
							.face(EnumFacing.UP)
							.clr(0xa100e6)
							.collide(false)
							.scale(2.3F)
							.time(20)
							.spawn(world);
				}
			}

		}
		super.onLivingUpdate();
	}

	public SoundCategory getSoundCategory() {
		return SoundCategory.HOSTILE;
	}

	@Nullable
	@Override
	protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
		return ASSounds.ENTITY_STONE_GUARDIAN_HURT;
	}

	public boolean attackEntityAsMob(Entity entityIn) {

		if (entityIn instanceof EntityPlayer) {
			EntityPlayer entityplayer = (EntityPlayer) entityIn;

			ItemStack itemstack1 = entityplayer.isHandActive() ? entityplayer.getActiveItemStack() : ItemStack.EMPTY;

			if (itemstack1.getItem().isShield(itemstack1, entityplayer)) {
				if (entityplayer.isHandActive()) {entityplayer.stopActiveHand();}
				entityplayer.getCooldownTracker().setCooldown(itemstack1.getItem(), 100);
				this.world.setEntityState(entityplayer, (byte) 30);
			}
		}

		float f = (float) this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
		int i = 0;

		if (entityIn instanceof EntityLivingBase) {
			f += EnchantmentHelper.getModifierForCreature(this.getHeldItemMainhand(), ((EntityLivingBase) entityIn).getCreatureAttribute());
			i += EnchantmentHelper.getKnockbackModifier(this);
		}

		boolean flag = entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), f);

		if (flag) {
			if (i > 0 && entityIn instanceof EntityLivingBase) {
				((EntityLivingBase) entityIn).knockBack(this, (float) i * 0.5F, (double) MathHelper.sin(this.rotationYaw * 0.017453292F), (double) (-MathHelper.cos(this.rotationYaw * 0.017453292F)));
				this.motionX *= 0.6D;
				this.motionZ *= 0.6D;
			}

			int j = EnchantmentHelper.getFireAspectModifier(this);

			if (j > 0) {
				entityIn.setFire(j * 4);
			}

			this.applyEnchantments(this, entityIn);
		}

		return flag;
	}

	@Override
	public void onDeath(DamageSource cause) {
		if (!this.world.isRemote && this.world.getBlockState(this.getPosition().down()).getBlock() instanceof BlockSealedStone) {

			for(int i = 0; i < 15; i++){
				EntityStoneGuardianShard ember = new EntityStoneGuardianShard(world);
				double x = (world.rand.nextDouble() - 0.5) * this.width;
				double y = world.rand.nextDouble() * this.height;
				double z = (world.rand.nextDouble() - 0.5) * this.width;
				ember.setPosition(this.posX + x, this.posY + y, this.posZ + z);
				ember.ticksExisted = world.rand.nextInt(20);
				float speed = 0.1f;
				ember.motionX = x * speed;
				ember.motionY = y * 0.5f * speed;
				ember.motionZ = z * speed;
				world.spawnEntity(ember);
			}
		}

		super.onDeath(cause);
	}

	@Override
	public void setHomePosAndDistance(BlockPos pos, int distance) {
		super.setHomePosAndDistance(pos, distance);
	}
}
