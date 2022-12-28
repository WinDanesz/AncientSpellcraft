package com.windanesz.ancientspellcraft.entity.living;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.Settings;
import com.windanesz.ancientspellcraft.entity.ai.EntityAIAttackSpellImproved;
import com.windanesz.ancientspellcraft.registry.ASItems;
import electroblob.wizardry.constants.Element;
import electroblob.wizardry.entity.living.EntityRemnant;
import electroblob.wizardry.entity.living.ISpellCaster;
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
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class EntitySkeletonMage extends AbstractSkeleton implements ISpellCaster {

	private double AISpeed = 1.0;
	private Spell continuousSpell;
	private int spellCounter;
	private int particleCount = 1;
	private float particleSize = 0.7F;

	/**
	 * The resource location for the loot table.
	 */
	private ResourceLocation lootTableRegular = new ResourceLocation(AncientSpellcraft.MODID, "entities/skeleton_mage");

	private ResourceLocation lootTableRare = new ResourceLocation(AncientSpellcraft.MODID, "entities/skeleton_mage_rare");

	/**
	 * The entity selector passed into the new AI methods.
	 */
	// Data parameter for the skelly's element.
	protected static final DataParameter<Integer> ELEMENT = EntityDataManager.createKey(EntitySkeletonMage.class, DataSerializers.VARINT);

	// Data parameter for the skelly's rarity.
	protected static final DataParameter<Boolean> IS_RARE = EntityDataManager.createKey(EntitySkeletonMage.class, DataSerializers.BOOLEAN);

	private List<Spell> spells = new ArrayList<>(1);
	private EntityAIAttackSpellImproved<EntitySkeletonMage> spellcastingAI = new EntityAIAttackSpellImproved<EntitySkeletonMage>(this, AISpeed, 15f, 25, 40);

	/**
	 * Creates a new summoned creature in the given world.
	 */
	public EntitySkeletonMage(World world) {
		super(world);
		this.experienceValue = isRare() ? 8 : 5;
		// For some reason this can't be in initEntityAI
		this.tasks.addTask(0, this.spellcastingAI);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		this.dataManager.register(ELEMENT, 0);
		this.dataManager.register(IS_RARE, false);
	}

	@Nullable
	@Override
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
		livingdata = super.onInitialSpawn(difficulty, livingdata);
		setLeftHanded(false);
		this.setRare(rand.nextFloat() < 0.15f);
		setElement(pickElementForBiome());
		Element element = this.getElement();
		populateSpellList(element, null);
		return livingdata;
	}

	public Element pickElementForBiome() {
		if (Settings.generalSettings.use_biomes_for_mage_elements < rand.nextFloat()) {
			return Element.values()[rand.nextInt(Element.values().length - 1) + 1];
		} else {
			Biome biome = world.getBiome(this.getPosition());
			String name = biome.getRegistryName().getPath();
			if (Arrays.stream(Settings.generalSettings.fire_skeleton_and_ghost_biome_whitelist).anyMatch(b -> b.equals(name))) {
				return Element.FIRE;
			}
			if (Arrays.stream(Settings.generalSettings.earth_skeleton_and_ghost_biome_whitelist).anyMatch(b -> b.equals(name))) {
				return Element.EARTH;
			}
			if (Arrays.stream(Settings.generalSettings.sorcery_skeleton_and_ghost_biome_whitelist).anyMatch(b -> b.equals(name))) {
				return Element.SORCERY;
			}
			if (Arrays.stream(Settings.generalSettings.healing_skeleton_and_ghost_biome_whitelist).anyMatch(b -> b.equals(name))) {
				return Element.HEALING;
			}
			if (Arrays.stream(Settings.generalSettings.lightning_skeleton_and_ghost_biome_whitelist).anyMatch(b -> b.equals(name))) {
				return Element.LIGHTNING;
			}
			if (Arrays.stream(Settings.generalSettings.ice_skeleton_and_ghost_biome_whitelist).anyMatch(b -> b.equals(name))) {
				return Element.ICE;
			}
			if (Arrays.stream(Settings.generalSettings.necromancy_skeleton_and_ghost_biome_whitelist).anyMatch(b -> b.equals(name))) {
				return Element.NECROMANCY;
			}
		}
		return Element.values()[rand.nextInt(Element.values().length - 1) + 1];
	}

	@Override
	protected void setEquipmentBasedOnDifficulty(DifficultyInstance difficulty) {
		this.setItemStackToSlot(EntityEquipmentSlot.HEAD, new ItemStack(ASItems.wizard_hat_ancient));
		for (EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {this.setDropChance(slot, 0.0f);}
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
		this.targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, true));

		this.setAIMoveSpeed((float) AISpeed);
	}

	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();
		renderHandParticle();

		// Adds a dust particle effect
		if (isRare() && this.world.isRemote) {
			double x = this.posX - this.width / 2 + this.rand.nextFloat() * width;
			double y = this.posY + this.height * this.rand.nextFloat() + 0.2f;
			double z = this.posZ - this.width / 2 + this.rand.nextFloat() * width;
			ParticleBuilder.create(ParticleBuilder.Type.DUST).pos(x, y, z).clr(0.6f, 1, 0.6f).shaded(true).spawn(world);
		}
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
		this.lootTableRare = new ResourceLocation(AncientSpellcraft.MODID, "entities/skeleton_mage/" + element.getName() + "_rare");
		this.lootTableRegular = new ResourceLocation(AncientSpellcraft.MODID, "entities/skeleton_mage/" + element.getName());
	}

	public void setRare(boolean rare) {
		this.dataManager.set(IS_RARE, rare);
	}

	public void populateSpellList(Element element, Spell spell) {
		spells = new ArrayList<>(1);

		if (spell != null) {
			spells.add(spell);
			return;
		}

		switch (element) {
			case FIRE:
				if (this.isRare()) {
					spells.add(Spells.fire_breath);
				} else {
					spells.add(Spells.fireball);
				}
				break;
			case ICE:
				if (this.isRare()) {
					spells.add(Spells.ice_lance);
				} else {
					spells.add(Spells.iceball);
				}
				break;
			case LIGHTNING:
				if (this.isRare()) {
					spells.add(Spells.lightning_disc);
				} else {
					spells.add(Spells.lightning_arrow);
				}
				break;
			case EARTH:
				if (this.isRare()) {
					spells.add(Spells.fangs);
				} else {
					spells.add(Spells.dart);
				}
				break;
			case SORCERY:
				if (this.isRare()) {
					spells.add(Spells.force_orb);
				} else {
					spells.add(Spells.force_arrow);
				}
				break;
			case HEALING:
				if (this.isRare()) {
					spells.add(Spells.ray_of_purification);
				} else {
					spells.add(Spells.arcane_jammer);
				}
				break;
			case NECROMANCY:
				if (this.isRare()) {
					spells.add(Spells.summon_wither_skeleton);
				} else {
					spells.add(Spells.summon_zombie);
				}
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

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		nbt.setInteger("element", this.getElement().ordinal());
		nbt.setBoolean("rare", this.isRare());
		NBTExtras.storeTagSafely(nbt, "spells", NBTExtras.listToNBT(spells, spell -> new NBTTagInt(spell.metadata())));
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		this.setElement(Element.values()[nbt.getInteger("element")]);
		this.setRare(nbt.getBoolean("rare"));
		this.spells = (List<Spell>) NBTExtras.NBTToList(nbt.getTagList("spells", Constants.NBT.TAG_INT),
				(NBTTagInt tag) -> Spell.byMetadata(tag.getInt()));
	}

	public float getOpacity() {return isRare() ? 0.55f : 1f;}

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
	protected SoundEvent getAmbientSound() {return SoundEvents.ENTITY_STRAY_AMBIENT;}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {return SoundEvents.ENTITY_STRAY_HURT;}

	@Override
	protected SoundEvent getDeathSound() {return SoundEvents.ENTITY_STRAY_DEATH;}

	@Override
	protected ResourceLocation getLootTable() {return isRare() ? lootTableRare : lootTableRegular;}

	@Override
	public boolean getCanSpawnHere() {
		for (int i : Settings.generalSettings.skeleton_mage_dimension_whitelist) {
			if (super.getCanSpawnHere() && this.dimension == i) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void onDeath(DamageSource cause) {
		if (isRare() && rand.nextFloat() < 0.15f) {
			if (!world.isRemote) {
				for (int i = 0; i < 2; i++) {
					EntityRemnant remnant = new EntityRemnant(world);
					remnant.setPosition(posX, posY, posZ);
					remnant.setElement(getElement());
					world.spawnEntity(remnant);
				}
			}
		}
		super.onDeath(cause);
	}

	@Override
	public boolean canAttackClass(Class<? extends EntityLivingBase> entityType) {
		// Returns true unless the given entity type is a flying entity and this entity only has melee attacks.
		return !EntityFlying.class.isAssignableFrom(entityType) || this.hasRangedAttack();
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

	public boolean isRare() {
		return this.getDataManager().get(IS_RARE);
	}

	@Override
	public ITextComponent getDisplayName() {
		if (isRare()) {
			return new TextComponentTranslation("entity.ancientspellcraft:ghost_mage.name");
		} else {
			return super.getDisplayName();
		}
	}
}
