package com.windanesz.ancientspellcraft.entity.living;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.entity.ai.EntityAIAttackRangedBowNoStrafing;
import com.windanesz.ancientspellcraft.entity.ai.EntityAIAttackSpellWithCost;
import com.windanesz.ancientspellcraft.entity.ai.EntitySummonAIFollowOwner;
import com.windanesz.ancientspellcraft.item.ItemBattlemageSword;
import com.windanesz.ancientspellcraft.item.ItemIceShield;
import com.windanesz.ancientspellcraft.item.ItemSageTome;
import com.windanesz.ancientspellcraft.registry.ASItems;
import com.windanesz.ancientspellcraft.spell.AwakenTome;
import com.windanesz.ancientspellcraft.util.ASUtils;
import electroblob.wizardry.Wizardry;
import electroblob.wizardry.entity.living.EntitySummonedCreature;
import electroblob.wizardry.entity.living.ISpellCaster;
import electroblob.wizardry.entity.projectile.EntityFlamecatcherArrow;
import electroblob.wizardry.item.IConjuredItem;
import electroblob.wizardry.item.ISpellCastingItem;
import electroblob.wizardry.item.ItemArtefact;
import electroblob.wizardry.item.ItemFlamecatcher;
import electroblob.wizardry.item.ItemFlamingAxe;
import electroblob.wizardry.item.ItemFrostAxe;
import electroblob.wizardry.item.ItemSpectralBow;
import electroblob.wizardry.item.ItemSpectralSword;
import electroblob.wizardry.item.ItemWand;
import electroblob.wizardry.registry.Spells;
import electroblob.wizardry.registry.WizardryEnchantments;
import electroblob.wizardry.registry.WizardryPotions;
import electroblob.wizardry.registry.WizardrySounds;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.MagicDamage;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.ParticleBuilder.Type;
import electroblob.wizardry.util.SpellModifiers;
import electroblob.wizardry.util.WandHelper;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.PotionTypes;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static electroblob.wizardry.spell.SpellMinion.HEALTH_MODIFIER;

public class EntityAnimatedItem extends EntitySummonedCreature implements ISpellCaster, IRangedAttackMob {

	public static final String ANIMATED_ITEM_TRANSLATION_KEY = "entity." + AncientSpellcraft.MODID + ":animated_item.nameplate";
	/* Used by the display name - to decide if the item's name should be in the nameplate, or just "Animated Armour"*/
	private static final DataParameter<Boolean> HAS_ARMOUR = EntityDataManager.createKey(EntityAnimatedItem.class, DataSerializers.BOOLEAN);
	/* These must use the datamanager otherwise electroblob.wizardry.WizardryEventHandler.onLivingUpdateEvent will create an infinite loop of client-side only
	 spellcasting loop if the target dies during the continuous spell cast. */
	private static final DataParameter<String> CONTINUOUS_SPELL = EntityDataManager.createKey(EntityAnimatedItem.class, DataSerializers.STRING);
	private static final DataParameter<Integer> SPELL_COUNTER = EntityDataManager.createKey(EntityAnimatedItem.class, DataSerializers.VARINT);
	private final double AISpeed = 1.0;
	// AI for wands or scrolls only
	private final EntityAIAttackSpellWithCost<EntityAnimatedItem> spellAttackAI = new EntityAIAttackSpellWithCost<>(this, 0.1f, 15f, 30, 50, false);
	// Tome AI
	private final EntityAIAttackSpellWithCost<EntityAnimatedItem> tomeAttackAI = new EntityAIAttackSpellWithCost<>(this, 0.1f, 15f, 30, 50, true);
	// AI task for ranged (bow)
	private final EntityAIAttackRangedBowNoStrafing<EntityAnimatedItem> bowAttackAI = new EntityAIAttackRangedBowNoStrafing<>(this, 0.1D, 40, 25.0F);
	// melee AI
	private final EntityAIAttackMelee meleeAI = new EntityAIAttackMelee(this, AISpeed, false);
	/* Used by animated bows which are not conjured or Infinity enchanted */
	private ItemStack arrows = ItemStack.EMPTY;

	public EntityAnimatedItem(World world) {
		super(world);
		setLeftHanded(false);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		this.dataManager.register(HAS_ARMOUR, false);
		this.dataManager.register(CONTINUOUS_SPELL, "ebwizardry:none");
		this.dataManager.register(SPELL_COUNTER, 0);
	}

	@Override
	protected void initEntityAI() {
		this.tasks.addTask(2, new EntityAIWander(this, AISpeed));
		this.targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityLivingBase.class,
				0, true, true, this.getTargetSelector()));
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		// Animated items die if their item is removed or break or their caster is not present
		if (this.ticksExisted > 20 && (this.getCaster() == null || this.getCaster().isDead || (!this.hasArmour() && this.getHeldItemMainhand().isEmpty()) || (this.hasArmour() && lostArmour()))) {
			this.setDead();
			this.onDespawn();
		}

		// conjured items receive a durability damage each tick
		for (EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
			if (!getItemStackFromSlot(slot).isEmpty() && (getItemStackFromSlot(slot).getItem() instanceof IConjuredItem)) {
				getItemStackFromSlot(slot).damageItem(1, this);
			}
		}

		// TNT specific logic - animated TNT explodes immediately from fire
		if (this.isBurning()) {
			if (this.getHeldItemMainhand().getItem() == ItemSpectralSword.getItemFromBlock(Blocks.TNT)) {
				if (!this.world.isRemote) {
					EntityTNTPrimed tnt = new EntityTNTPrimed(this.world, this.posX, this.posY, this.posZ, this);
					tnt.setFuse(20);
					this.world.spawnEntity(tnt);
					this.world.playSound((EntityPlayer) null, tnt.posX, tnt.posY, tnt.posZ, SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 1.0F, 1.0F);
					this.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);
					this.setDead();
				}
			}
		}

		// Wands consume mana if they are animated
		if (this.getHeldItemMainhand().getItem() instanceof ItemWand) {

			if (this.ticksExisted % 20 == 0) {
				((ItemWand) this.getHeldItemMainhand().getItem()).consumeMana(this.getHeldItemMainhand(), 3, this);
			}

			// .. and revert to item form if they run out of mana
			if (((ItemWand) this.getHeldItemMainhand().getItem()).isManaEmpty(this.getHeldItemMainhand())) {
				this.world.playSound((EntityPlayer) null, this.posX, this.posY, this.posZ, WizardrySounds.ENTITY_LIGHTNING_ARROW_HIT, SoundCategory.BLOCKS, 1.0F, 1.0F);
				this.setDead();
				this.onDespawn();
			}
		}

		// Sorcery particles!
		if (world.isRemote) {
			Vec3d height = this.getPositionVector().add(0, 1.3, 0);

			if (!hasArmour()) {
				Vec3d offset = this.getLookVec().normalize().scale(0.5).add(0.5f * (rand.nextDouble() - 0.5f), 0.5f * (rand.nextDouble() - 0.5f), 0.5f * (rand.nextDouble() - 0.5f));
				height = height.add(offset);

				Vec3d rightHand = Vector.toRectangular(Math.toRadians(this.renderYawOffset + 90), 0).times(0.385).withY(0).toMinecraft();
				rightHand = rightHand.add(height);
				for (int i = 0; i < 20; i++) {
					ParticleBuilder.create(Type.DUST)
							.pos(rightHand)
							.time(40)
							.clr(0.2f, 1.0f, 0.8f)
							.spawn(world);
				}

			} else {
				for (int i = 0; i < 10; i++) {
					ParticleBuilder.create(Type.DUST)
							.pos(0.7f * (rand.nextDouble() - 0.5f) + this.posX, 1.5f * (rand.nextDouble() - 0.5f) + this.posY + getEyeHeight() - 0.3, 0.7f * (rand.nextDouble() - 0.5f) + this.posZ)
							.time(20)
							.clr(0.2f, 1.0f, 0.8f)
							.spawn(world);
				}

			}
		}
	}

	// Adapted from EntityLiving
	@Override
	public void onLivingUpdate() {
		this.updateArmSwingProgress();
		float f = this.getBrightness();

		if (f > 0.5F) {
			this.idleTime += 2;
		}

		super.onLivingUpdate();
	}

	@Override
	protected boolean processInteract(EntityPlayer player, EnumHand hand) {
		boolean taken = false;

		// The caster can remove the item by right clicking it
		if (!player.world.isRemote && player == this.getCaster()) {

			if (this.getHeldItemMainhand().getItem() instanceof ItemBow) {
				this.entityDropItem(arrows, 1);
				this.setArrows(ItemStack.EMPTY);
			}

			for (EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
				if (player.getItemStackFromSlot(slot).isEmpty() && !this.getItemStackFromSlot(slot).isEmpty()) {
					ItemStack armourToTake = this.getItemStackFromSlot(slot).copy();
					player.setItemStackToSlot(slot, armourToTake);
					this.setItemStackToSlot(slot, ItemStack.EMPTY);
					taken = true;
				}
			}
		}

		if (taken) { return true; }

		// In this case, the delegate method determines whether super is called.
		// Rather handily, we can make use of Java's short-circuiting method of evaluating OR statements.
		return this.interactDelegate(player, hand) || super.processInteract(player, hand);
	}

	public boolean lostArmour() {
		// return true if any of this item's armour is still present!
		for (EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
			if (!this.getItemStackFromSlot(slot).isEmpty()) {
				return false;
			}
		}
		return true;
	}

	public boolean hasArmour() {
		return this.dataManager.get(HAS_ARMOUR);
	}

	public void setHasArmour(boolean hasArmour) {
		this.dataManager.set(HAS_ARMOUR, hasArmour);
	}

	@Override
	public void setCaster(EntityLivingBase caster) {
		super.setCaster(caster);
	}

	/* This animation of conjured entities would look really weird for the animated items */
	@Override
	public boolean hasAnimation() {
		return false;
	}

	@Override
	public void onDespawn() {
		revertToItemForm();
		super.onDespawn();

		if (world.isRemote) {
			for (int i = 0; i < 20; i++) {
				ParticleBuilder.create(Type.DUST)
						.pos(this.posX + (this.rand.nextDouble() - 0.5) * this.width, this.posY
								+ this.rand.nextDouble() * this.height, this.posZ + (this.rand.nextDouble() - 0.5) * this.width)
						.time(0)
						.clr(0.2f, 1.0f, 0.8f)
						.shaded(true)
						.spawn(world);
			}
		}
	}

	@Nonnull
	@Override
	public List<Spell> getSpells() {
		if (this.getHeldItemMainhand().getItem() instanceof ISpellCastingItem) {
			Spell spell = (((ISpellCastingItem) this.getHeldItemMainhand().getItem()).getCurrentSpell(this.getHeldItemMainhand()));

			// normally, only allow up to apprentice spells
			int maxTier = 1;

			if (getCaster() instanceof EntityPlayer && ItemArtefact.isArtefactActive((EntityPlayer) getCaster(), ASItems.charm_spectral_tome)) {
				// allow advanced tier spells with the artefact
				maxTier += 1;
			}

			// adding this variable to stop java from complaining about effectively final variables..
			int finalMaxTier = maxTier;

			return Arrays.stream((((ISpellCastingItem) this.getHeldItemMainhand().getItem()).getSpells(this.getHeldItemMainhand())))
					.filter(s -> s.getTier().level <= finalMaxTier).collect(Collectors.toList());
		}
		return Collections.singletonList(Spells.none);
	}

	@Override
	public SpellModifiers getModifiers() {
		return new SpellModifiers();
	}

	@Override
	public Spell getContinuousSpell() {
		return Spell.get(this.dataManager.get(CONTINUOUS_SPELL));
	}

	@Override
	public void setContinuousSpell(Spell spell) {
		//noinspection ConstantConditions
		this.dataManager.set(CONTINUOUS_SPELL, spell.getRegistryName().toString());
	}

	@Override
	public int getSpellCounter() {
		return this.dataManager.get(SPELL_COUNTER);
	}

	@Override
	public void setSpellCounter(int count) {
		this.dataManager.set(SPELL_COUNTER, count);
	}

	@Override
	public boolean isPotionApplicable(PotionEffect potioneffectIn) {
		if (potioneffectIn.getPotion() == WizardryPotions.frost
				|| potioneffectIn.getPotion() == MobEffects.GLOWING
				|| potioneffectIn.getPotion() == WizardryPotions.mind_control) {
			return false;
		}
		return super.isPotionApplicable(potioneffectIn);
	}

	@Override
	public ITextComponent getDisplayName() {
		if (getCaster() != null) {
			if (!this.getHeldItemMainhand().isEmpty() && !hasArmour()) {
				// Conjured items display their item's name
				return new TextComponentTranslation(ANIMATED_ITEM_TRANSLATION_KEY, getCaster().getName(), this.getHeldItemMainhand().getDisplayName());
			}
			return new TextComponentTranslation(ANIMATED_ITEM_TRANSLATION_KEY, getCaster().getName(), super.getDisplayName());
		} else {
			return super.getDisplayName();
		}
	}

	@Override
	public boolean hasCustomName() {
		// If this returns true, the renderer will show the nameplate when looking directly at the entity
		return Wizardry.settings.summonedCreatureNames && getCaster() != null;
	}

	/**
	 * Called when this entity is <b>being hurt</b> by someone. Damages the animated item
	 */
	public boolean attackEntityFrom(DamageSource source, float amount) {
		if (this.isEntityInvulnerable(source)) { return false; }

		// Damages the item. Armor damage is handled in damageArmor(float damage).
		// Tbd: should mana be drained from wands?
		damageItem(amount);

		return super.attackEntityFrom(source, amount);
	}

	@Override
	protected void damageArmor(float damage) {
		damage = damage / 4.0F;

		// deal at least 1 damage
		damage = Math.max(damage, 1.0F);

		for (EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {

			if (!this.getItemStackFromSlot(slot).isEmpty()) {
				ItemStack stack = this.getItemStackFromSlot(slot);

				if (stack.getItem() instanceof ItemArmor) {
					stack.damageItem((int) damage, this);
				}
			}
		}
	}

	protected void damageItem(float damage) {
		// deal 1/2 of the damage to this item
		damage = damage / 2.0F;

		// deal at least 1 damage
		damage = Math.max(damage, 1.0F);

		if (!this.getHeldItemMainhand().isEmpty()) {
			this.getHeldItemMainhand().damageItem((int) damage, this);
		}
	}

	@Override
	public void onDeath(DamageSource cause) {
		Item heldItem = this.getHeldItemMainhand().getItem();

		// TNT specific logic
		if (heldItem == Item.getItemFromBlock(Blocks.TNT)) {
			if (!this.world.isRemote) {
				EntityTNTPrimed tnt = new EntityTNTPrimed(this.world, this.posX, this.posY, this.posZ, this);
				tnt.setFuse(0);
				this.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);
				this.world.spawnEntity(tnt);
				this.world.playSound((EntityPlayer) null, tnt.posX, tnt.posY, tnt.posZ, SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 1.0F, 1.0F);
			}
			// ender pearl specific logic
		} else if (heldItem == Items.ENDER_PEARL) {
			if (!this.world.isRemote && this.getCaster() instanceof EntityPlayer) {
				EntityEnderPearl entityenderpearl = new EntityEnderPearl(this.getCaster().world, (EntityPlayer) this.getCaster());
				entityenderpearl.setPosition(this.posX, this.posY + 1.5f, this.posZ);
				this.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);
				this.world.spawnEntity(entityenderpearl);
			}
			// potion specific logic
		} else if (heldItem instanceof ItemPotion && PotionUtils.getPotionFromItem(this.getHeldItemMainhand()) != PotionTypes.EMPTY) {
			if (!this.world.isRemote) {
				EntityPotion entitypotion = new EntityPotion(this.world, this, PotionUtils.addPotionToItemStack(new ItemStack(Items.SPLASH_POTION),
						PotionUtils.getPotionFromItem(this.getHeldItemMainhand())));
				entitypotion.setPosition(this.posX, this.posY + 1.5f, this.posZ);
				this.world.playSound((EntityPlayer) null, this.posX, this.posY, this.posZ, SoundEvents.ENTITY_WITCH_THROW, this.getSoundCategory(), 1.0F, 0.8F + this.rand.nextFloat() * 0.4F);
				this.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);
				this.world.spawnEntity(entitypotion);
			}
			// ice shield specific logic
		} else if (this.getHeldItemMainhand().getItem() instanceof ItemIceShield) {
			ItemIceShield.explodeShield(world, this, this.getHeldItemMainhand());
		} else {
			// Usual reverting logic
			revertToItemForm();
		}
		super.onDeath(cause);
	}

	/**
	 * Called when this entity is killed or if the item is recalled by the caster, causes it to drop all non-conjured.
	 */
	public void revertToItemForm() {
		if (!world.isRemote) {

			// sage tome
			if (this.getHeldItemMainhand().getItem() instanceof ItemSageTome) {
				if (this.getCaster() instanceof EntityPlayer) {
					AwakenTome.removeController((EntityPlayer) this.getCaster());
					ASUtils.giveStackToPlayer((EntityPlayer) this.getCaster(), this.getHeldItemMainhand().copy());
					return;
				} else {
					this.entityDropItem(this.getHeldItemMainhand(), 1);
				}
			}

			// mainhand, offhand and armor slots
			for (EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
				boolean shouldDropItem = true;

				// soulbound items attempt to return to their owner's inventory
				if (this.getCaster() instanceof EntityPlayer && !getItemStackFromSlot(slot).isEmpty()
						&& WandHelper.getUpgradeLevel(getItemStackFromSlot(slot), ASItems.soulbound_upgrade) > 0) {

					shouldDropItem = !ASUtils.giveStackToPlayer((EntityPlayer) this.getCaster(), getItemStackFromSlot(slot));

				}

				// conjured items just disappear
				if (shouldDropItem && !getItemStackFromSlot(slot).isEmpty() && !(getItemStackFromSlot(slot).getItem() instanceof IConjuredItem)) {
					this.entityDropItem(getItemStackFromSlot(slot), 1);
				}

				this.setItemStackToSlot(slot, ItemStack.EMPTY);
			}

			// drops arrows
			if (this.getHeldItemMainhand().getItem() instanceof ItemBow) {
				this.entityDropItem(arrows, 1);
				this.setArrows(ItemStack.EMPTY);
			}
		}
	}

	/**
	 * Mostly the same as net.minecraft.entity.monster.EntityMob with some extra additions to support Enchantments
	 * This is called when <b>this entity hurts someone else</b>.
	 */
	public boolean attackEntityAsMob(Entity entityIn) {
		float f = (float) this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
		int i = 0;
		ItemStack stack = this.getHeldItemMainhand();
		Item item = this.getHeldItemMainhand().getItem();

		if (entityIn instanceof EntityLivingBase) {
			f += EnchantmentHelper.getModifierForCreature(this.getHeldItemMainhand(), ((EntityLivingBase) entityIn).getCreatureAttribute());
			i += EnchantmentHelper.getKnockbackModifier(this);
		}

		boolean flag = entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), f);

		if (i > 0 && entityIn instanceof EntityLivingBase) {
			((EntityLivingBase) entityIn).knockBack(this, (float) i * 0.5F, (double) MathHelper.sin(this.rotationYaw * 0.017453292F), (double) (-MathHelper.cos(this.rotationYaw * 0.017453292F)));
			this.motionX *= 0.6D;
			this.motionZ *= 0.6D;
		}

		if (!stack.isEmpty() && entityIn instanceof EntityLivingBase) {
			EntityLivingBase livingTarget = (EntityLivingBase) entityIn;

			// fire aspect
			int j = EnchantmentHelper.getFireAspectModifier(this);
			if (j > 0) {
				entityIn.setFire(j * 4);
			}

			// flaming axe and enchant
			int flamingLevel = EnchantmentHelper.getEnchantmentLevel(WizardryEnchantments.flaming_weapon, stack);
			if (item instanceof ItemFlamingAxe) {
				flamingLevel++;
			}

			if (flamingLevel > 0 && !MagicDamage.isEntityImmune(MagicDamage.DamageType.FIRE, livingTarget)) {
				livingTarget.setFire(flamingLevel * 4);
			}

			// ice axe and enchant
			int iceLevel = EnchantmentHelper.getEnchantmentLevel(WizardryEnchantments.freezing_weapon, stack);
			if (item instanceof ItemFrostAxe) {
				iceLevel++;
			}

			if (iceLevel > 0 && !MagicDamage.isEntityImmune(MagicDamage.DamageType.FROST, livingTarget)) {
				livingTarget.addPotionEffect(new PotionEffect(WizardryPotions.frost, iceLevel * 200, 0));
			}

		}

		this.applyEnchantments(this, entityIn);

		// damage the item
		if (!this.getHeldItemMainhand().isEmpty()) {
			this.getHeldItemMainhand().damageItem(1, this);
		}

		return flag;
	}

	@Override
	public int getAnimationColour(float animationProgress) {
		return 0xffc600;
	}

	@Override
	public boolean isSneaking() {
		return false;
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(1.0D);
		this.getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_SPEED).setBaseValue(6.0D);
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.5f);
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20.0D);
		this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(16.0D);
	}

	@Override
	public boolean hasRangedAttack() {
		return false;
	}

	public void setItemType(String itemType) {
	}

	/**
	 * Sets the current AI task, based on the held item.
	 */
	private void setCombatTask() {
		this.tasks.removeTask(meleeAI);
		this.tasks.removeTask(spellAttackAI);
		this.tasks.removeTask(bowAttackAI);

		if ((this.getHeldItemMainhand().getItem() instanceof ISpellCastingItem) && !(this.getHeldItemMainhand().getItem() instanceof ItemBattlemageSword)) {

			// Tome logic
			if (this.getHeldItemMainhand().getItem() instanceof ItemSageTome) {

				int sentienceUpgrades = WandHelper.getUpgradeLevel(this.getHeldItemMainhand(), ASItems.sentience_upgrade);
				if (sentienceUpgrades > 0) {
					// tomes should follow the caster
					EntitySummonAIFollowOwner task = new EntitySummonAIFollowOwner(this, 1.0D, 10.0F, 2.0F);
					this.tasks.addTask(5, task);
				}
				if (sentienceUpgrades > 1) {
					// should proxy buff spells
					this.tasks.addTask(1, tomeAttackAI);
				} else {
					this.tasks.addTask(1, spellAttackAI);
				}
			} else {
				// Wand / other logic
				this.tasks.addTask(1, spellAttackAI);
			}
		} else if (this.getHeldItemMainhand().getItem() instanceof ItemBow
				&& ((EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, this.getHeldItemMainhand()) > 0 || this.getHeldItemMainhand().getItem() instanceof ItemSpectralBow)
				|| !this.arrows.isEmpty() && this.arrows.getItem() instanceof ItemArrow || this.getHeldItemMainhand().getItem() instanceof ItemFlamecatcher)) {
			this.tasks.addTask(1, bowAttackAI);
		} else {

			if (this.getHeldItemMainhand().getItem() instanceof ItemPotion) {
				this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).applyModifier(new AttributeModifier(HEALTH_MODIFIER, -0.9f, EntityUtils.Operations.MULTIPLY_CUMULATIVE));
				this.setHealth(this.getMaxHealth()); // Need to set this because we may have just modified the value
			}
			this.tasks.addTask(1, meleeAI);
		}
	}

	/**
	 * This override handles the AI task switching, based on the currently held item
	 */
	@Override
	public void setItemStackToSlot(EntityEquipmentSlot slotIn, ItemStack stack) {
		super.setItemStackToSlot(slotIn, stack);

		if (!this.world.isRemote && slotIn == EntityEquipmentSlot.MAINHAND) {
			this.setCombatTask();
		}
	}

	/**
	 * Mostly the same as AbstractSkeleton.attackEntityWithRangedAttack
	 */
	public void attackEntityWithRangedAttack(EntityLivingBase target, float distanceFactor) {

		// flamecatcher
		if (this.getHeldItemMainhand().getItem() instanceof ItemFlamecatcher) {
			float velocity = 0.8f;
			velocity = (velocity * velocity + velocity * 2) / 3;

			EntityFlamecatcherArrow arrow = new EntityFlamecatcherArrow(world);
			arrow.aim(this, EntityFlamecatcherArrow.SPEED * velocity);
			this.world.spawnEntity(arrow);
			this.getHeldItemMainhand().damageItem((int) (this.getHeldItemMainhand().getMaxDamage() / 4), this);
			return;
		}

		Optional<EntityArrow> arrowOptional = this.getArrow(distanceFactor);

		if (arrowOptional.isPresent()) {
			EntityArrow entityArrow = arrowOptional.get();

			// bow enchants
			if (this.getHeldItemMainhand().getItem() instanceof ItemBow) {
				entityArrow = (((ItemBow) this.getHeldItemMainhand().getItem()).customizeArrow(entityArrow));
			}

			// set arrow positioning
			double d0 = target.posX - this.posX;
			double d1 = target.getEntityBoundingBox().minY + (double) (target.height / 3.0F) - arrowOptional.get().posY;
			double d2 = target.posZ - this.posZ;
			double d3 = MathHelper.sqrt(d0 * d0 + d2 * d2);
			entityArrow.shoot(d0, d1 + d3 * 0.20000000298023224D, d2, 1.6F, (float) (14 - this.world.getDifficulty().getId() * 4));

			this.playSound(SoundEvents.ENTITY_SKELETON_SHOOT, 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));

			// spawn arrow
			this.world.spawnEntity(entityArrow);
		}
	}

	/**
	 * Gets the arrow (with "tips"), or nothing (Optional.empty()) if there are no arrows and resets the AI task.
	 */
	protected Optional<EntityArrow> getArrow(float distanceFactor) {
		ItemStack arrow;

		// Spectral Bows and bows with Infinity enchantment get free arrows
		if (EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, this.getHeldItemMainhand()) > 0 || this.getHeldItemMainhand().getItem() instanceof ItemSpectralBow) {
			EntityArrow entityArrow = ((ItemArrow) Items.ARROW).createArrow(this.world, new ItemStack(Items.ARROW), this);
			entityArrow.setEnchantmentEffectsFromEntity(this, distanceFactor);
			return Optional.of(entityArrow);
		}

		if (this.arrows.getItem() instanceof ItemArrow) {
			arrow = this.arrows.copy();
			arrow.setCount(1);
			// remove one arrow from the arrows stack of this entity
			arrows.shrink(1);
		} else {
			// No arrows, we shouldn't continue using the bow AI
			setCombatTask();
			return Optional.empty();
		}

		EntityArrow entityArrow = ((ItemArrow) arrow.getItem()).createArrow(this.world, arrow, this);
		entityArrow.setEnchantmentEffectsFromEntity(this, distanceFactor);

		if (arrow.getItem() instanceof ItemArrow) {
			this.arrows.shrink(1);
		}

		return Optional.of(entityArrow);
	}

	public void setArrows(ItemStack arrows) {
		this.arrows = arrows;
	}

	@SideOnly(Side.CLIENT)
	public boolean isSwingingArms() {
		return false;
	}

	public void setSwingingArms(boolean swingingArms) {
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		// saving the arrow stack
		NBTTagCompound arrowTag = new NBTTagCompound();
		this.arrows.writeToNBT(arrowTag);
		nbt.setTag("arrows", arrowTag);
		super.writeEntityToNBT(nbt);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		// loading the arrow stack
		if (nbt.hasKey("arrows")) {
			this.arrows = new ItemStack(nbt.getCompoundTag("arrows"));
		}

		super.readEntityFromNBT(nbt);
	}
}
