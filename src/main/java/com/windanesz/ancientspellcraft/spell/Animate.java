package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.entity.living.EntityAnimatedItem;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftItems;
import electroblob.wizardry.item.IConjuredItem;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.spell.SpellMinion;
import electroblob.wizardry.util.BlockUtils;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public abstract class Animate extends SpellMinion<EntityAnimatedItem> {

	public Animate(String modid, String name) {
		super(modid, name, EntityAnimatedItem::new);
	}

	@Override
	public boolean cast(World world, EntityPlayer caster, EnumHand hand, int ticksInUse, SpellModifiers modifiers){

		if(!this.spawnMinions(world, caster, modifiers, true)) return false;
		this.playSound(world, caster, ticksInUse, -1, modifiers);
		return true;
	}

	@Override
	public boolean cast(World world, EntityLiving caster, EnumHand hand, int ticksInUse, EntityLivingBase target,
			SpellModifiers modifiers){

		if(!this.spawnMinions(world, caster, modifiers, true)) return false;
		this.playSound(world, caster, ticksInUse, -1, modifiers);
		return true;
	}

	public static void equipFromOffhand(EntityAnimatedItem minion, BlockPos pos,
			@Nullable EntityLivingBase caster, SpellModifiers modifiers) {
		if (caster != null && !caster.getHeldItemOffhand().isEmpty()) {
			ItemStack stack = caster.getHeldItemOffhand().copy();
			stack.setCount(1);

			// remove from caster
			caster.getHeldItemOffhand().shrink(1);

			// give to minion
			minion.setHeldItem(EnumHand.MAIN_HAND, stack);
			minion.setItemType(stack.getItem().getRegistryName().toString());
			minion.setHasArmour(false);
		}
	}

	public static void equipWith(EntityAnimatedItem minion, ItemStack stack) {
		minion.setHeldItem(EnumHand.MAIN_HAND, stack);
		minion.setItemType(stack.getItem().getRegistryName().toString());
		minion.setHasArmour(false);
	}

	public static ItemStack conjureItem(SpellModifiers modifiers, Item item) {

		if (item instanceof IConjuredItem) {

			ItemStack stack = new ItemStack(item);
			IConjuredItem.setDurationMultiplier(stack, modifiers.get(WizardryItems.duration_upgrade));
			IConjuredItem.setDamageMultiplier(stack, modifiers.get(SpellModifiers.POTENCY));

			return stack;
		}
		return ItemStack.EMPTY;
	}

	public static ItemStack findAmmo(EntityPlayer player) {
		if (isArrow(player.getHeldItem(EnumHand.OFF_HAND))) {
			return player.getHeldItem(EnumHand.OFF_HAND);
		} else if (isArrow(player.getHeldItem(EnumHand.MAIN_HAND))) {
			return player.getHeldItem(EnumHand.MAIN_HAND);
		} else {
			for (int i = 0; i < player.inventory.getSizeInventory(); ++i) {
				ItemStack itemstack = player.inventory.getStackInSlot(i);

				if (isArrow(itemstack)) {
					return itemstack;
				}
			}

			return ItemStack.EMPTY;
		}
	}

	protected static boolean isArrow(ItemStack stack) {
		return stack.getItem() instanceof ItemArrow;
	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == AncientSpellcraftItems.ancient_spellcraft_spell_book || item == AncientSpellcraftItems.ancient_spellcraft_scroll;
	}

	public boolean spawnMinions(World world, EntityLivingBase caster, SpellModifiers modifiers, boolean damageScalesWithPotency) {
		if (!world.isRemote) {
			for (int i = 0; i < getProperty(MINION_COUNT).intValue(); i++) {

				int range = getProperty(SUMMON_RADIUS).intValue();

				// Try and find a nearby floor space
				BlockPos pos = BlockUtils.findNearbyFloorSpace(caster, range, range * 2);

				if (flying) {
					if (pos != null) {
						// Make sure the flying entity spawns above the ground
						pos = pos.up(2); // Adding 2 will suffice, it's not exactly a game-changer...
					} else {
						// If there was no floor around to spawn them on, just pick any spot in mid-air
						pos = caster.getPosition().north(world.rand.nextInt(range * 2) - range)
								.east(world.rand.nextInt(range * 2) - range);
					}
				} else {
					// If there was no floor around and the entity isn't a flying one, the spell fails.
					// As per the javadoc for findNearbyFloorSpace, there's no point trying the rest of the minions.
					if (pos == null) { return false; }
				}

				EntityAnimatedItem minion = createMinion(world, caster, modifiers);

				minion.setPosition(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
				minion.setCaster(caster);
				// Modifier implementation
				// Attribute modifiers are pretty opaque, see https://minecraft.gamepedia.com/Attribute#Modifiers
				minion.setLifetime((int) (getProperty(MINION_LIFETIME).floatValue() * modifiers.get(WizardryItems.duration_upgrade)));

				////////////////////////////////////////////
				if (damageScalesWithPotency) {

					IAttributeInstance attribute = minion.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
					if (attribute != null) {
						attribute.applyModifier( // Apparently some things don't have an attack damage
								new AttributeModifier(POTENCY_ATTRIBUTE_MODIFIER, modifiers.get(SpellModifiers.POTENCY) - 1, EntityUtils.Operations.MULTIPLY_CUMULATIVE));
					}
				}
				////////////////////////////////////////////

				// This is only used for artefacts, but it's a nice example of custom spell modifiers
				minion.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).applyModifier(
						new AttributeModifier(HEALTH_MODIFIER, modifiers.get(HEALTH_MODIFIER) - 1, EntityUtils.Operations.MULTIPLY_CUMULATIVE));
				minion.setHealth(minion.getMaxHealth()); // Need to set this because we may have just modified the value

				this.addMinionExtras(minion, pos, caster, modifiers, i);

				world.spawnEntity(minion);
			}
		}

		return true;
	}
}
