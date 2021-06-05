package com.windanesz.ancientspellcraft.entity.living;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.tileentity.TileSentinel;
import electroblob.wizardry.Wizardry;
import electroblob.wizardry.entity.living.ISpellCaster;
import electroblob.wizardry.registry.WizardryPotions;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.AllyDesignationSystem;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.MagicDamage;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EntitySpellCaster extends EntityCreature implements ISpellCaster, IEntityOwnable {

	private List<Spell> spells = new ArrayList<Spell>(1);
	private Spell continuousSpell;
	private SpellModifiers modifiers = new SpellModifiers();

	private UUID ownerUUID;

	static {
		MagicDamage.addEntityImmunity(EntitySpellCaster.class, MagicDamage.DamageType.WITHER);
	}

	public EntitySpellCaster(World worldIn) {
		super(worldIn);
		setNoAI(true);

		this.height = 0.7f;
		this.width = 0.4f;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if (!world.isRemote && this.ticksExisted > 10) {
			TileEntity tile = world.getTileEntity(this.getPosition());
			if (!(tile instanceof TileSentinel && ((TileSentinel) tile).getSpellCasterEntity() == this)) {
				this.setDead();
			}
		}

	}

	@Nullable
	protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
		return SoundEvents.BLOCK_GLASS_HIT;
	}

	@Nullable
	protected SoundEvent getDeathSound() {
		return SoundEvents.BLOCK_METAL_BREAK;
	}

	@Override
	public boolean isPotionApplicable(PotionEffect effect) {
		return effect.getPotion() == WizardryPotions.arcane_jammer;
	}

	/**
	 * Called when a lightning bolt hits the entity.
	 */
	public void onStruckByLightning(EntityLightningBolt lightningBolt) {}

	/**
	 * Returns false if the entity is an armor stand. Returns true for all other entity living bases.
	 */
	public boolean canBeHitWithPotion() {
		return false;
	}

	protected void collideWithEntity(Entity entityIn) { }

	@Override
	public boolean canBePushed() {
		return false;
	}

	@Override
	public EnumPushReaction getPushReaction() {
		return EnumPushReaction.IGNORE;
	}

	public void onKillCommand() {
		this.setDead();
	}

	@Nonnull
	@Override
	public List<Spell> getSpells() { return spells; }

	@Nonnull
	@Override
	public SpellModifiers getModifiers() { return modifiers; }

	public void setModifiers(SpellModifiers modifiers) { this.modifiers = modifiers; }

	@Nonnull
	@Override
	public Spell getContinuousSpell() { return continuousSpell; }

	@Override
	public void setContinuousSpell(Spell spell) {
		this.continuousSpell = spell;
	}

	@Override
	public int getSpellCounter() {
		return 1;
	}

	@Override
	public void setSpellCounter(int count) {}

	@Override
	public int getAimingError(EnumDifficulty difficulty) {

		switch (difficulty) {
			case EASY:
				return 7;
			case NORMAL:
				return 4;
			case HARD:
				return 1;
			default:
				return 7; // Peaceful counts as easy
		}
	}

	@Override
	protected boolean canDespawn() {
		return false;
	}

	public void setOwnerId(UUID ownerUUID) {
		this.ownerUUID = ownerUUID;
	}

	@Nullable
	@Override
	public UUID getOwnerId() {
		return ownerUUID;
	}

	@Nullable
	@Override
	public Entity getOwner() {
		Entity entity = EntityUtils.getEntityByUUID(world, getOwnerId());

		if (entity != null && !(entity instanceof EntityLivingBase)) { // Should never happen
			AncientSpellcraft.logger.warn("{} has a non-living owner!", this);
			entity = null;
		}

		return (EntityLivingBase) entity;
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		if (source.getTrueSource() instanceof EntityLivingBase && ownerUUID != null) {
			EntityPlayer possibleOwner = world.getPlayerEntityByUUID(ownerUUID);
			if (possibleOwner != null) {
				if ((Wizardry.settings.friendlyFire == AllyDesignationSystem.FriendlyFire.ONLY_OWNED || Wizardry.settings.friendlyFire == AllyDesignationSystem.FriendlyFire.NONE)
						&& AllyDesignationSystem.isAllied(possibleOwner, (EntityLivingBase) source.getTrueSource())) {
					return false;
				}
			}
		}
		return super.attackEntityFrom(source, amount);
	}
}
