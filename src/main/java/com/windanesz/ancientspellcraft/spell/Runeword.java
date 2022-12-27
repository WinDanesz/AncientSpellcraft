package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.item.ItemBattlemageSword;
import com.windanesz.ancientspellcraft.registry.ASItems;
import electroblob.wizardry.item.ItemWizardArmour;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

import java.util.Arrays;

import static electroblob.wizardry.util.WandHelper.SPELL_ARRAY_KEY;

public class Runeword extends Spell implements IClassSpell {

	public static final String POTENCY_ATTRIBUTE_MODIFIER = "potency";
	public static final String EFFECT_TRIGGER_CHANCE = "effect_trigger_chance";
	public static final String CHARGES = "charges";
	public static final String DAMAGE_MULTIPLIER = "damage_multiplier";

	private boolean passive = false;

	// This is true for any runeword that has an effect on attribute modifiers. Added to save some performance with a quicker bool check
	private boolean affectsAttributes = false;

	// This is true for any runeword that has an effect on damage dealt directly. Added to save some performance with a quicker bool check
	private boolean affectsDamage = false;

	// True, if this runeword needs to be updated each tick
	private boolean hasTickEffect = false;

	public Runeword(String name, EnumAction action, boolean isContinuous) {
		super(AncientSpellcraft.MODID, name, action, isContinuous);
	}

	public Runeword setPassive() {
		this.passive = true;
		return this;
	}

	public Runeword affectsAttributes() {
		this.affectsAttributes = true;
		return this;
	}

	public Runeword affectsDamage() {
		this.affectsDamage = true;
		return this;
	}

	public Runeword enableTickEffect() {
		this.hasTickEffect = true;
		return this;
	}

	public boolean isTicking() {
		return hasTickEffect;
	}

	public boolean isAffectingAttributes() { return affectsAttributes; }

	public boolean isAffectingDamageDirectly() { return affectsDamage; }

	public boolean isPassive() {
		return passive;
	}

	public void tick(EntityLivingBase wielder, ItemStack sword) {}

	@Override
	public boolean cast(World world, EntityPlayer caster, EnumHand hand, int ticksInUse, SpellModifiers modifiers) {
		// This should cover most of the use cases for Runewords with charges
		if (!isPassive() && hasProperty(CHARGES)) {
			if (holdsSword(caster, hand)) {
				ItemBattlemageSword.setActiveRuneword(caster.getHeldItem(hand), this, getProperty(CHARGES).intValue());
				return true;
			}
		}
		return false;
	}


	public static boolean holdsSword(EntityLivingBase entityLivingBase) {
		return holdsSword(entityLivingBase, EnumHand.MAIN_HAND);
	}

	public static boolean holdsSword(EntityLivingBase entityLivingBase, EnumHand hand) {
		return entityLivingBase.getHeldItem(hand).getItem() instanceof ItemBattlemageSword;
	}

	@Override
	public boolean cast(World world, double x, double y, double z, EnumFacing direction, int ticksInUse, int duration, SpellModifiers modifiers) {
		return false;
	}

	@Override
	public boolean cast(World world, EntityLiving caster, EnumHand hand, int ticksInUse, EntityLivingBase target, SpellModifiers modifiers) {
		return false;
	}

	public static boolean castSuccessful(Runeword runeword, World world, EntityLivingBase entity, EnumHand hand, ItemStack sword, SpellModifiers modifiers) {
		if (sword.getItem() instanceof ItemBattlemageSword) {
			return canCastRuneword(runeword, sword, entity, hand, modifiers);
		}
		return false;
	}

	public static boolean canCastRuneword(Runeword runeword, ItemStack stack, EntityLivingBase entity, EnumHand hand, SpellModifiers modifiers) {
		return entity instanceof EntityPlayer && stack.getItem() instanceof ItemBattlemageSword
				&& ((ItemBattlemageSword) stack.getItem()).cast(stack, runeword, (EntityPlayer) entity, hand, 0, modifiers);
	}

	/**
	 * Called when the target is being hit, but before dealing damage.
	 */
	public boolean onAboutToHitEntity(World world, EntityLivingBase caster, EntityLivingBase target, EnumHand hand, ItemStack sword, SpellModifiers modifiers, boolean charged) {
		return false;
	}

	public float affectDamage(DamageSource source, float damage, EntityPlayer player, EntityLivingBase target, ItemStack sword) { return damage; }

	public boolean onDamageTaken(World world, EntityLivingBase caster, EntityLivingBase target, EnumHand hand, SpellModifiers modifiers) {
		return false;
	}

	public boolean onShieldRaised(World world, EntityLivingBase caster, EntityLivingBase target, EnumHand hand, SpellModifiers modifiers) {
		return false;
	}

	public boolean onShieldBlock(World world, EntityLivingBase caster, EntityLivingBase target, EnumHand hand, SpellModifiers modifiers) {
		return false;
	}

	@Override
	public boolean applicableForItem(Item item) { return item == ASItems.runic_plate; }

	//	public enum Context {
	//		ON_ANY_HIT,
	//		ON_GREATER_POWER_HIT,
	//		ON_SHIELD_BLOCK,
	//		ON_CAST
	//
	//		// Some more ideas to consider
	//		//ON_KILL,
	//	}


	/**
	 * Returns an array containing the runewords currently bound to the given sword. If the sword has no spell data,
	 * returns an array of length 0. This method ignores non-runeword spells.
	 */
	public static Runeword[] getRunewords(ItemStack sword){

		Runeword[] runewords = new Runeword[0];

		if(sword.getTagCompound() != null){

			int[] spellIDs = sword.getTagCompound().getIntArray(SPELL_ARRAY_KEY);
			int[] runewordIDs = Arrays.stream(spellIDs).filter(i -> Spell.byMetadata(i) instanceof Runeword).toArray();

			for(int i = 0; i < spellIDs.length; i++){
				runewords[i] = (Runeword) Spell.byMetadata(runewordIDs[i]);
			}
		}

		return runewords;
	}

	public void spendCharge(ItemStack sword) {
		ItemBattlemageSword.spendCharge(sword, this, 1);
	}

	@Override
	public ItemWizardArmour.ArmourClass getArmourClass() { return ItemWizardArmour.ArmourClass.BATTLEMAGE; }

	public static EnumHand getOtherHand(EnumHand hand) {
		return hand == EnumHand.MAIN_HAND? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;
	}
}


