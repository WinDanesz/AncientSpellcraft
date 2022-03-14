package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.entity.projectile.EntityAOEProjectile;
import com.windanesz.ancientspellcraft.integration.artemislib.ASArtemisLibIntegration;
import com.windanesz.ancientspellcraft.registry.ASItems;
import electroblob.wizardry.entity.projectile.EntityMagicProjectile;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.spell.Heal;
import electroblob.wizardry.spell.SpellBuff;
import electroblob.wizardry.spell.SpellProjectile;
import electroblob.wizardry.util.MagicDamage;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class SpellProjectileAOEPotion<T extends EntityMagicProjectile> extends SpellProjectile<T> {

	/**
	 * The RGB colour values of the particles spawned when this spell is cast.
	 */
	protected final float r, g, b;

	/**
	 * An array of factories for the status effects that this spell applies to its caster. The effect factory
	 * avoids the issue of the potions being registered after the spell.
	 */
	protected final Supplier<Potion>[] effects;
	/**
	 * A set of all the different potions (status effects) that this spell applies to its caster. Loaded during
	 * init().
	 */
	private Set<Potion> potionSet;

	private MagicDamage.DamageType damageType = MagicDamage.DamageType.MAGIC;

	/**
	 * The particle type of this AoE projectile
	 */
	private ResourceLocation particle = ParticleBuilder.Type.SPARKLE;
	private int maxLevel;

	public SpellProjectileAOEPotion(String name, Function<World, T> projectileFactory, float r, float g, float b, Supplier<Potion>... effects) {
		super(AncientSpellcraft.MODID, name, projectileFactory);
		this.effects = effects;
		this.r = r;
		this.g = g;
		this.b = b;
		addProperties(EFFECT_RADIUS);

		if (!ASArtemisLibIntegration.enabled()) {
			this.setEnabled(false);
		}
	}

	@Override
	public boolean cast(World world, EntityPlayer caster, EnumHand hand, int ticksInUse, SpellModifiers modifiers) {
		if (ASArtemisLibIntegration.enabled()) {
			return super.cast(world, caster, hand, ticksInUse, modifiers);
		} else {
			if (!world.isRemote)
				caster.sendStatusMessage(new TextComponentTranslation("tooltip.ancientspellcraft:missing_artemislib.disabled_spell"), false);
			return false;
		}
	}

	@Override
	public boolean cast(World world, EntityLiving caster, EnumHand hand, int ticksInUse, EntityLivingBase target, SpellModifiers modifiers) {
		return ASArtemisLibIntegration.enabled() && super.cast(world, caster, hand, ticksInUse, target, modifiers);
	}

	@Override
	public boolean cast(World world, double x, double y, double z, EnumFacing direction, int ticksInUse, int duration, SpellModifiers modifiers) {
		return ASArtemisLibIntegration.enabled() && super.cast(world, x, y, z, direction, ticksInUse, duration, modifiers);
	}

	@Override
	public void init() {
		// Loads the potion set
		this.potionSet = Arrays.stream(effects).map(Supplier::get).collect(Collectors.toSet());

		for (Potion potion : potionSet) {
			// I don't like having this for all buff spells when some potions aren't affected by amplifiers
			// TODO: Find a way of only adding the strength key if the potion is affected by amplifiers (dynamically if possible)
			// BrewingRecipeRegistry#getOutput might be a good place to start
			addProperties(getStrengthKey(potion));
			if (!potion.isInstant())
				addProperties(getDurationKey(potion));
		}
	}

	public MagicDamage.DamageType getDamageType() { return damageType; }

	public ResourceLocation getParticle() { return particle; }

	public float getR() { return r; }

	public float getG() { return g; }

	public float getB() { return b; }

	///////////// Standard SpellBuff methods, Author: Electroblob /////////////

	// Potion-specific equivalent to defining the identifiers as constants

	protected static String getDurationKey(Potion potion) {
		return potion.getRegistryName().getPath() + "_duration";
	}

	protected static String getStrengthKey(Potion potion) {
		return potion.getRegistryName().getPath() + "_strength";
	}

	/**
	 * Actually applies the status effects to the caster. By default, this iterates through the array of effects and
	 * applies each in turn, multiplying the duration and amplifier by the appropriate modifiers. Particles are always
	 * hidden and isAmbient is always set to false. Override to do something special, like apply a non-potion buff.
	 * Returns a boolean to allow subclasses to cause the spell to fail if for some reason the effect cannot be applied
	 * (for example, {@link Heal} fails if the caster is on full health).
	 */
	public boolean applyPotionEffects(EntityLivingBase caster, SpellModifiers modifiers) {
		// This will generate 0 for novice and apprentice, and 1 for advanced and master
		// TODO: Once we've found a way of detecting if amplifiers actually affect the potion type, implement it here.
		int bonusAmplifier = getBonusAmplifier(modifiers.get(SpellModifiers.POTENCY));

		for (Potion potion : potionSet) {
			caster.addPotionEffect(new PotionEffect(potion, potion.isInstant() ? 1 :
					(int) (getProperty(getDurationKey(potion)).floatValue() * modifiers.get(WizardryItems.duration_upgrade)),
					Math.min(maxLevel + 1, (int) getProperty(getStrengthKey(potion)).floatValue() + bonusAmplifier + 1),
					false, true));
		}

		return true;
	}

	/**
	 * Returns the number to be added to the potion amplifier(s) based on the given potency modifier. Override
	 * to define custom modifier handling. Delegates to {@link SpellBuff#getStandardBonusAmplifier(float)} by
	 * default.
	 */
	protected int getBonusAmplifier(float potencyModifier) {
		return getStandardBonusAmplifier(potencyModifier);
	}

	/**
	 * Returns a number to be added to potion amplifiers based on the given potency modifier. This method uses
	 * a standard calculation which results in zero extra levels for novice and apprentice wands and one extra
	 * level for advanced and master wands (this generally seems to give about the right weight to potency
	 * modifiers). This is public static because it is useful in a variety of places.
	 */
	public static int getStandardBonusAmplifier(float potencyModifier) {
		return (int) ((potencyModifier - 1) / 0.4);
	}

	@Override
	protected void addProjectileExtras(T projectile, @Nullable EntityLivingBase caster, SpellModifiers modifiers) {
		((EntityAOEProjectile) projectile).setRelatedSpell(this);
		super.addProjectileExtras(projectile, caster, modifiers);
	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == ASItems.ancient_spell_book || item == ASItems.ancient_spellcraft_scroll;
	}
}
