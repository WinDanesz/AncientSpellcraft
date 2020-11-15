package com.windanesz.ancientspellcraft.entity.living;

import com.windanesz.ancientspellcraft.registry.AncientSpellcraftItems;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftSpells;
import electroblob.wizardry.Wizardry;
import electroblob.wizardry.constants.Element;
import electroblob.wizardry.entity.living.EntityAIAttackSpell;
import electroblob.wizardry.entity.living.ISpellCaster;
import electroblob.wizardry.entity.living.ISummonedCreature;
import electroblob.wizardry.registry.Spells;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.NBTExtras;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest2;
import net.minecraft.entity.monster.AbstractSkeleton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class EntitySkeletonMageMinion extends AbstractSkeleton implements ISpellCaster, ISummonedCreature {

	// Field implementations
	private int lifetime = -1;
	private UUID casterUUID;
	private double AISpeed = 1.0;
	private Spell continuousSpell;
	private int spellCounter;
	private int particleCount = 1;
	private float particleSize = 0.7F;
	private Element element = Element.MAGIC;
	//	private ResourceLocation particle1;
	//	private ResourceLocation particle2;
	//	private ResourceLocation particle1 = ParticleBuilder.Type.MAGIC_FIRE;
	//	private ResourceLocation particle2 = ParticleBuilder.Type.FLASH;

	/**
	 * The entity selector passed into the new AI methods.
	 */

	// Data parameter for the skelly's element.
	private static final DataParameter<Integer> ELEMENT = EntityDataManager.createKey(EntitySkeletonMageMinion.class, DataSerializers.VARINT);
	//	private static final DataParameter<String> PARTICLE1 = EntityDataManager.createKey(EntitySkeletonMageMinion.class, DataSerializers.STRING);
	//	private static final DataParameter<String> PARTICLE2 = EntityDataManager.createKey(EntitySkeletonMageMinion.class, DataSerializers.STRING);

	private List<Spell> spells = new ArrayList<>(1);

	private EntityAIAttackSpell<EntitySkeletonMageMinion> spellcastingAI = new EntityAIAttackSpell<EntitySkeletonMageMinion>(this, AISpeed, 15f, 40, 0);

	// target AI task for non-healer elements. Uses ISummonedCreature target selector
	private EntityAINearestAttackableTarget<EntityLivingBase> commonTargetingAI = new EntityAINearestAttackableTarget<EntityLivingBase>(this, EntityLivingBase.class, 0, false, true, this.getTargetSelector());

	// target AI task for healers, makes them only target their owner. Its not using the ISummonedCreature target selector
	private EntityAINearestAttackableTarget<EntityPlayer> healerTargetingAI = new EntityAINearestAttackableTarget<EntityPlayer>(this, EntityPlayer.class, false, true) {
		@Override
		protected boolean isSuitableTarget(@Nullable EntityLivingBase target, boolean includeInvincibles) {
			return target instanceof EntityPlayer && ((EntityPlayer) target).getUniqueID() == getOwnerId();
		}
	};

	/**
	 * Creates a new summoned creature in the given world.
	 */
	public EntitySkeletonMageMinion(World world) {
		super(world);
		this.experienceValue = 0;
		// For some reason this can't be in initEntityAI
		this.tasks.addTask(0, this.spellcastingAI);
	}

	/**
	 * Creates a new summoned creature in the given world with a specific element.
	 */
	public EntitySkeletonMageMinion(World world, Element element) {
		super(world);
		this.experienceValue = 0;
		this.element = element;
		// For some reason this can't be in initEntityAI
		this.tasks.addTask(0, this.spellcastingAI);
		// needs to be called after element settings are initialized
		if (this.element == Element.HEALING || this.getElement() == Element.HEALING) {
			this.targetTasks.addTask(2, this.healerTargetingAI);
		} else {
			this.targetTasks.addTask(2, this.commonTargetingAI);
		}
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		this.dataManager.register(ELEMENT, 0);
	}

	@Nullable
	@Override
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
		livingdata = super.onInitialSpawn(difficulty, livingdata);
		setLeftHanded(false);
		if (element == Element.MAGIC) {
			setElement(Element.values()[rand.nextInt(Element.values().length - 1) + 1]);
		} else {
			setElement(element);
		}
		Element element = this.getElement();
		populateSpellList(element);
		return livingdata;
	}

	@Override
	protected void setEquipmentBasedOnDifficulty(DifficultyInstance difficulty) {
		this.setItemStackToSlot(EntityEquipmentSlot.HEAD, new ItemStack(AncientSpellcraftItems.wizard_hat_ancient));
		for (EntityEquipmentSlot slot : EntityEquipmentSlot.values())
			this.setDropChance(slot, 0.0f);
	}

	@Override
	protected void initEntityAI() {
		super.initEntityAI();
		this.targetTasks.taskEntries.clear();
		this.tasks.addTask(0, new EntityAISwimming(this));
		this.tasks.addTask(3, new EntityAILookIdle(this));
		this.tasks.addTask(3, new EntityAIWatchClosest2(this, EntityPlayer.class, 3.0F, 1.0F));
		this.tasks.addTask(3, new EntityAIWander(this, 0.6D));

		this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));

		this.setAIMoveSpeed((float) AISpeed);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		this.updateDelegate();
	}

	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();
		renderHandParticle();
	}

	private void renderHandParticle() {

		Vec3d height = this.getPositionVector().add(0, 0.84, 0);
		Vec3d rightHand = Vector.toRectangular(Math.toRadians(this.renderYawOffset + 90), 0).times(0.385).withY(0).toMinecraft();
		Vec3d leftHand = Vector.toRectangular(Math.toRadians(this.renderYawOffset - 90), 0).times(0.385).withY(0).toMinecraft();
		rightHand = rightHand.add(height);
		leftHand = leftHand.add(height);
		if (isSwingInProgress) {
			rightHand = (rightHand.add(this.getLookVec().x * 0.5, swingProgress * 0.6, this.getLookVec().z * 0.5)); // offset for hand swinging
		}
		spawnHandParticle(rightHand);
		spawnHandParticle(leftHand);
	}

	@SuppressWarnings("Duplicates")
	private void spawnHandParticle(Vec3d handSide) {

		if (world.isRemote) {
			for (int i = 0; i < particleCount; i++) {
				switch (getElement()) {
					case FIRE:
						ParticleBuilder.create(ParticleBuilder.Type.MAGIC_FIRE).pos(handSide).time(6 + getRandomNumberInRange(0, 4)).vel(world.rand.nextGaussian() / 40, world.rand.nextDouble() / 40,
								world.rand.nextGaussian() / 40).clr(246, 180 + getRandomNumberInRange(0, 50), 80).collide(false).
								scale(particleSize / 1.5F).spawn(world);
						ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(handSide).time(6 + getRandomNumberInRange(0, 4)).vel(world.rand.nextGaussian() / 40, world.rand.nextDouble() / 40,
								world.rand.nextGaussian() / 40).clr(255, 116, 0).collide(false).
								scale(particleSize / 1.5F).spawn(world);
						ParticleBuilder.create(ParticleBuilder.Type.MAGIC_FIRE).pos(handSide).time(6 + getRandomNumberInRange(0, 4)).vel(world.rand.nextGaussian() / 40, world.rand.nextDouble() / 40,
								world.rand.nextGaussian() / 40).clr(246, 180 + getRandomNumberInRange(0, 50), 80).collide(false).
								scale(particleSize / 2F).spawn(world);
						break;

					case ICE:
						ParticleBuilder.create(ParticleBuilder.Type.ICE).pos(handSide).time(6 + getRandomNumberInRange(0, 4)).vel(world.rand.nextGaussian() / 40, world.rand.nextDouble() / 40,
								world.rand.nextGaussian() / 40).collide(false).
								scale(particleSize / 1.5F).spawn(world);
						ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(handSide).time(6 + getRandomNumberInRange(0, 4)).vel(world.rand.nextGaussian() / 40, world.rand.nextDouble() / 40,
								world.rand.nextGaussian() / 40).clr(0, 204, 255).collide(false).
								scale(particleSize / 1.5F).spawn(world);
						break;

					case LIGHTNING:
						ParticleBuilder.create(ParticleBuilder.Type.SPARK).pos(handSide).time(6 + getRandomNumberInRange(0, 4)).vel(world.rand.nextGaussian() / 40, world.rand.nextDouble() / 40,
								world.rand.nextGaussian() / 40).collide(false).
								scale(particleSize / 1.5F).spawn(world);
						ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(handSide).time(getRandomNumberInRange(0, 2)).vel(world.rand.nextGaussian() / 40, world.rand.nextDouble() / 40,
								world.rand.nextGaussian() / 40).clr(27, 102, 125).collide(false).
								scale(particleSize / 0.9F).spawn(world);
						break;

					case EARTH:
						ParticleBuilder.create(ParticleBuilder.Type.LEAF).pos(handSide).time(6 + getRandomNumberInRange(0, 4)).vel(world.rand.nextGaussian() / 40, world.rand.nextDouble() / 40,
								world.rand.nextGaussian() / 40).collide(false).
								scale(particleSize / 1.5F).spawn(world);
						ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(handSide).time(6 + getRandomNumberInRange(0, 4)).vel(world.rand.nextGaussian() / 40, world.rand.nextDouble() / 40,
								world.rand.nextGaussian() / 40).clr(102, 51, 0).collide(false).
								scale(particleSize / 1.5F).spawn(world);
						ParticleBuilder.create(ParticleBuilder.Type.LEAF).pos(handSide).time(6 + getRandomNumberInRange(0, 4)).vel(world.rand.nextGaussian() / 40, world.rand.nextDouble() / 40,
								world.rand.nextGaussian() / 40).collide(false).
								scale(particleSize / 2F).spawn(world);
						break;

					case SORCERY:
						ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(handSide).time(6 + getRandomNumberInRange(0, 4)).vel(world.rand.nextGaussian() / 40, world.rand.nextDouble() / 40,
								world.rand.nextGaussian() / 40).clr(89, 238, 155).collide(false).
								scale(particleSize / 1.5F).spawn(world);
						break;

					case HEALING:
						ParticleBuilder.create(ParticleBuilder.Type.SPARKLE).pos(handSide).time(6 + getRandomNumberInRange(0, 4)).vel(world.rand.nextGaussian() / 40, world.rand.nextDouble() / 40,
								world.rand.nextGaussian() / 40).clr(246, 180 + getRandomNumberInRange(0, 50), 80).collide(false).
								scale(particleSize / 1.5F).spawn(world);
						ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(handSide).time(6 + getRandomNumberInRange(0, 4)).vel(world.rand.nextGaussian() / 40, world.rand.nextDouble() / 40,
								world.rand.nextGaussian() / 40).clr(255, 255, 204).collide(false).
								scale(particleSize / 1.5F).spawn(world);
						ParticleBuilder.create(ParticleBuilder.Type.SPARKLE).pos(handSide).time(6 + getRandomNumberInRange(0, 4)).vel(world.rand.nextGaussian() / 40, world.rand.nextDouble() / 40,
								world.rand.nextGaussian() / 40).clr(246, 180 + getRandomNumberInRange(0, 50), 80).collide(false).
								scale(particleSize / 2F).spawn(world);
						break;
					case NECROMANCY:
						ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(handSide).time(6 + getRandomNumberInRange(0, 4)).vel(world.rand.nextGaussian() / 40, world.rand.nextDouble() / 40,
								world.rand.nextGaussian() / 40).clr(51, 0, 26).collide(false).
								scale(particleSize / 1.5F).spawn(world);
						ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(handSide).time(6 + getRandomNumberInRange(0, 4)).vel(world.rand.nextGaussian() / 40, world.rand.nextDouble() / 40,
								world.rand.nextGaussian() / 40).clr(32 + getRandomNumberInRange(0, 25), 1, 36).collide(false).
								scale(particleSize / 2.2F).spawn(world);
						break;
				}
			}
		}
	}

	public Element getElement() {
		return Element.values()[this.dataManager.get(ELEMENT)];
	}

	public void setElement(Element element) {
		this.dataManager.set(ELEMENT, element.ordinal());
	}

	private void populateSpellList(Element element) {
		switch (element) {
			case FIRE:
				spells.add(Spells.fireball);
				break;
			case ICE:
				spells.add(Spells.iceball);
				break;
			case LIGHTNING:
				spells.add(Spells.lightning_arrow);
				break;
			case EARTH:
				spells.add(Spells.dart);
				break;
			case SORCERY:
				spells.add(Spells.force_arrow);
				break;
			case HEALING:
				spells.add(AncientSpellcraftSpells.healing_heart);
				//				spells.add(AncientSpellcraftSpells.healing_heart);
				break;
			case NECROMANCY:
				spells.add(Spells.wither);

				break;
			default:
				spells.add(Spells.magic_missile);
		}

	}
	private static int getRandomNumberInRange(int min, int max) {
		Random r = new Random();
		return r.nextInt((max - min) + 1) + min;
	}


	/**
	 * Whether this summoned creature has a ranged attack. Used to test whether it should attack flying creatures.
	 */
	private boolean hasRangedAttack() {
		return true;
	}

	@Override
	public List<Spell> getSpells() {
		return spells;
	}

	public int getLifetime() {
		return lifetime;
	}

	public void setLifetime(int lifetime) {
		this.lifetime = lifetime;
	}

	@Override
	public void setOwnerId(UUID uuid) {
		this.casterUUID = uuid;
	}

	@Override
	public UUID getOwnerId() {
		return casterUUID;
	}

	@Override
	public void onSpawn() {
		this.spawnParticleEffect();
	}

	@Override
	public void onDespawn() {
		this.spawnParticleEffect();
	}

	private void spawnParticleEffect() {
		if (this.world.isRemote) {
			for (int i = 0; i < 15; i++) {
				this.world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, this.posX + (double) this.rand.nextFloat(),
						this.posY + 1 + (double) this.rand.nextFloat(),
						this.posZ + (double) this.rand.nextFloat(), 0, 0, 0);
			}
		}
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		this.writeNBTDelegate(nbt);
		nbt.setInteger("element", this.getElement().ordinal());
		NBTExtras.storeTagSafely(nbt, "spells", NBTExtras.listToNBT(spells, spell -> new NBTTagInt(spell.metadata())));
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		this.readNBTDelegate(nbt);
		this.setElement(Element.values()[nbt.getInteger("element")]);
		this.spells = (List<Spell>) NBTExtras.NBTToList(nbt.getTagList("spells", Constants.NBT.TAG_INT),
				(NBTTagInt tag) -> Spell.byMetadata(tag.getInt()));
	}

	@Override
	public boolean hasParticleEffect() {
		return false;
	}

	@Override
	public SpellModifiers getModifiers() {
		return new SpellModifiers();
	}

	@Override
	public void setContinuousSpell(Spell spell) {
		this.continuousSpell = spell;
	}

	@Override
	public Spell getContinuousSpell() {
		return this.continuousSpell;
	}

	@Override
	public void setSpellCounter(int count) {
		spellCounter = count;
	}

	@Override
	public int getSpellCounter() {
		return spellCounter;
	}

	//	@Override
	protected SoundEvent getStepSound() {
		return SoundEvents.ENTITY_STRAY_STEP;
	}

	@Override
	protected SoundEvent getAmbientSound() { return SoundEvents.ENTITY_STRAY_AMBIENT; }

	@Override
	protected SoundEvent getHurtSound(DamageSource source) { return SoundEvents.ENTITY_STRAY_HURT; }

	@Override
	protected SoundEvent getDeathSound() { return SoundEvents.ENTITY_STRAY_DEATH; }

	@Override
	protected boolean processInteract(EntityPlayer player, EnumHand hand) {
		// In this case, the delegate method determines whether super is called.
		// Rather handily, we can make use of Java's short-circuiting method of evaluating OR statements.
		return this.interactDelegate(player, hand) || super.processInteract(player, hand);
	}

	// Recommended overrides

	@Override
	protected int getExperiencePoints(EntityPlayer player) { return 0; }

	@Override
	protected boolean canDropLoot() { return false; }

	@Override
	protected Item getDropItem() { return null; }

	@Override
	protected ResourceLocation getLootTable() { return null; }

	@Override
	public boolean canPickUpLoot() { return false; }

	// This vanilla method has nothing to do with the custom despawn() method.
	@Override
	protected boolean canDespawn() {
		return getCaster() == null && getOwnerId() == null;
	}

	@Override
	public boolean getCanSpawnHere() {
		return this.world.getDifficulty() != EnumDifficulty.PEACEFUL;
	}

	@Override
	public boolean canAttackClass(Class<? extends EntityLivingBase> entityType) {
		// Returns true unless the given entity type is a flying entity and this entity only has melee attacks.
		return !EntityFlying.class.isAssignableFrom(entityType) || this.hasRangedAttack();
	}

	@Override
	public ITextComponent getDisplayName() {
		if (getCaster() != null) {
			return new TextComponentTranslation(NAMEPLATE_TRANSLATION_KEY, getCaster().getName(),
					new TextComponentTranslation("entity." + this.getEntityString() + ".name"));
		} else {
			return super.getDisplayName();
		}
	}

	@Override
	public boolean hasCustomName() {
		// If this returns true, the renderer will show the nameplate when looking directly at the entity
		return Wizardry.settings.summonedCreatureNames && getCaster() != null;
	}

	public float getEyeHeight() {
		return 1.74F;
	}

	/**
	 * Returns the Y Offset of this entity.
	 */
	public double getYOffset() {
		return -0.6D;
	}
}
