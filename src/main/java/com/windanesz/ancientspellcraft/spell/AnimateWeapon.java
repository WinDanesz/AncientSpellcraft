package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.entity.living.EntityAnimatedItem;
import com.windanesz.ancientspellcraft.material.IDevoritium;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftItems;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftSpells;
import com.windanesz.ancientspellcraft.util.ASUtils;
import electroblob.wizardry.item.ISpellCastingItem;
import electroblob.wizardry.item.ItemArtefact;
import electroblob.wizardry.item.ItemScroll;
import electroblob.wizardry.item.ItemSpectralBow;
import electroblob.wizardry.registry.Spells;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.spell.SpellConjuration;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.lang.reflect.Field;

public class AnimateWeapon extends Animate {

	public AnimateWeapon() {
		super(AncientSpellcraft.MODID, "animate_weapon");
	}

	@Override
	public boolean cast(World world, EntityPlayer caster, EnumHand hand, int ticksInUse, SpellModifiers modifiers) {

		if (caster.getHeldItemOffhand().isEmpty()) {
			if (!world.isRemote) { caster.sendStatusMessage(new TextComponentTranslation("spell." + this.getUnlocalisedName() + ".no_item"), true); }
			return false;
		}

		if (caster.getHeldItemOffhand().getItem() instanceof IDevoritium) {
			if (!world.isRemote) { caster.sendStatusMessage(new TextComponentTranslation("spell." + this.getUnlocalisedName() + ".devoritium_item"), true); }
		}

		return super.cast(world, caster, hand, ticksInUse, modifiers);
	}

	@Override
	public boolean cast(World world, EntityLiving caster, EnumHand hand, int ticksInUse, EntityLivingBase target, SpellModifiers modifiers) {
		return super.cast(world, caster, hand, ticksInUse, target, modifiers);
	}

	@Override
	public boolean canBeCastBy(EntityLiving npc, boolean override) {
		return false;
	}

	@Override
	public boolean canBeCastBy(TileEntityDispenser dispenser) {
		return false;
	}

	@Override
	protected void addMinionExtras(EntityAnimatedItem minion, BlockPos pos, @Nullable EntityLivingBase caster, SpellModifiers modifiers, int alreadySpawned) {
		super.addMinionExtras(minion, pos, caster, modifiers, alreadySpawned);

		addAnimatedEntityExtras(minion,pos, caster, modifiers);
	}

	public static void addAnimatedEntityExtras(EntityAnimatedItem minion, BlockPos pos, @Nullable EntityLivingBase caster, SpellModifiers modifiers) {
		if (caster instanceof EntityPlayer) {

			// undoing the damage boost

			if (caster.getHeldItemOffhand().getItem() instanceof ISpellCastingItem) {
				Spell spell = ((ISpellCastingItem) caster.getHeldItemOffhand().getItem()).getCurrentSpell(caster.getHeldItemOffhand());
				if (spell instanceof SpellConjuration) {
					if (spell != Spells.conjure_armour) {

						try {
							SpellConjuration spellConjuration = (SpellConjuration) spell;
							Field field = ASUtils.ReflectionUtil.getField(spellConjuration.getClass(), "item");
							ASUtils.ReflectionUtil.makeAccessible(field);
							Item item = (Item) field.get(spellConjuration);
							equipWith(minion, conjureItem(new SpellModifiers(), item));

							// consume scroll
							if (caster.getHeldItemOffhand().getItem() instanceof ItemScroll) {
								caster.getHeldItemOffhand().shrink(1);
							} else {
								// increase spell cost, since we summoned an item for free
								modifiers.set(SpellModifiers.COST, modifiers.get(SpellModifiers.COST) * 1.25f, false);
							}
							return;
						}
						catch (Exception e) {
							AncientSpellcraft.logger.error("Failed to get a conjuration spell's item from the offhand:");
							e.printStackTrace();
						}
					} else {
						// Spectral Armor spell

						minion.setItemStackToSlot(EntityEquipmentSlot.HEAD, conjureItem(modifiers, WizardryItems.spectral_helmet));
						minion.setItemStackToSlot(EntityEquipmentSlot.CHEST, conjureItem(modifiers, WizardryItems.spectral_chestplate));
						minion.setItemStackToSlot(EntityEquipmentSlot.LEGS, conjureItem(modifiers, WizardryItems.spectral_leggings));
						minion.setItemStackToSlot(EntityEquipmentSlot.FEET, conjureItem(modifiers, WizardryItems.spectral_boots));


						// this is just here to trigger the AI task
						minion.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, conjureItem(modifiers, Items.AIR));
						minion.setHasArmour(true);

						// artefact effect, gives sword and shield to the conjured mob
						if (ItemArtefact.isArtefactActive((EntityPlayer) caster, AncientSpellcraftItems.charm_spectral_army)) {
							minion.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, conjureItem(modifiers, WizardryItems.spectral_sword));
							minion.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, conjureItem(modifiers, AncientSpellcraftItems.spectral_shield));
						}

						// nerf speed
						IAttributeInstance speed = minion.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
						if (speed != null) {
							speed.applyModifier( // Apparently some things don't have an attack damage
									new AttributeModifier("speed_modifier", -0.5f, EntityUtils.Operations.MULTIPLY_FLAT));
						}

						// nerf speed
						IAttributeInstance attack_speed = minion.getEntityAttribute(SharedMonsterAttributes.ATTACK_SPEED);
						if (attack_speed != null) {
							attack_speed.applyModifier( // Apparently some things don't have an attack damage
									new AttributeModifier("attack_speed_modifier", 0.5f, EntityUtils.Operations.MULTIPLY_FLAT));
						}

						minion.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).applyModifier(new AttributeModifier(HEALTH_MODIFIER, 0.5 * modifiers.get(HEALTH_MODIFIER) - 1, EntityUtils.Operations.MULTIPLY_CUMULATIVE));
						minion.setHealth(minion.getMaxHealth()); // Need to set this because we may have just modified the value
					}

					// consume scroll
					if (caster.getHeldItemOffhand().getItem() instanceof ItemScroll) {
						caster.getHeldItemOffhand().shrink(1);
					} else {
						// increase spell cost, since we summoned an item for free
						modifiers.set(SpellModifiers.COST, modifiers.get(SpellModifiers.COST) * 1.25f, false);
					}

					return;
				}
			}
		}

		// bow
		if (caster instanceof EntityPlayer && caster.getHeldItemOffhand().getItem() instanceof ItemBow) {

			if (!(EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, caster.getHeldItemOffhand()) > 0 || caster.getHeldItemOffhand().getItem() instanceof ItemSpectralBow)) {

				ItemStack arrows = findAmmo((EntityPlayer) caster);
				if (arrows != null && !arrows.isEmpty()) {
					ItemStack arrowsToGive = arrows.copy();
					arrows.shrink(arrowsToGive.getCount());
					minion.setArrows(arrowsToGive);
				} else {
					if (!caster.world.isRemote) {
						((EntityPlayer) caster).sendStatusMessage(new TextComponentTranslation("spell." + AncientSpellcraftSpells.animate_weapon.getUnlocalisedName() + ".no_arrow"), true);
					}
				}
			}
		}
		// regular scenario when a conjuration spell is not used.
		equipFromOffhand(minion, pos, caster, modifiers);
	}
}