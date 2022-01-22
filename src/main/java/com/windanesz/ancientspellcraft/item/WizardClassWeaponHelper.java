package com.windanesz.ancientspellcraft.item;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftItems;
import electroblob.wizardry.constants.Element;
import electroblob.wizardry.constants.Tier;
import electroblob.wizardry.data.WizardData;
import electroblob.wizardry.entity.living.ISummonedCreature;
import electroblob.wizardry.item.IManaStoringItem;
import electroblob.wizardry.item.ItemWand;
import electroblob.wizardry.item.ItemWizardArmour;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.RayTracer;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import java.util.Optional;

public class WizardClassWeaponHelper {

	/**
	 * Common NBT tag used to determine the element of an item's ItemStack.
	 */
	public static final String ELEMENT_TAG = "element";
	public static final String CHARGE_PROGRESS = "charge_progress";

	private WizardClassWeaponHelper() { }

	/**
	 * Gets the class weapon for the given class and tier.
	 * @param tier to lookup
	 * @param armourClass to lookup. ArmourClass.Wizard returns the default wands.
	 * @return the class weapon for the given class & tier.
	 */
	public static Item getClassItemForTier(Tier tier, ItemWizardArmour.ArmourClass armourClass) {
		return getClassItemForTier(tier, armourClass, Element.MAGIC);
	}

	public static Item getClassItemForTier(Tier tier, ItemWizardArmour.ArmourClass armourClass, Element element) {
		if (tier == null) { throw new NullPointerException("The given tier cannot be null."); }
		if (armourClass == null) { throw new NullPointerException("The given armourClass cannot be null."); }

		switch (armourClass) {

			case WIZARD:
				return ItemWand.getWand(tier, Element.MAGIC);

			case BATTLEMAGE:
				if (tier == Tier.NOVICE) {
					return AncientSpellcraftItems.battlemage_sword_novice;
				} else if (tier == Tier.APPRENTICE) {
					return AncientSpellcraftItems.battlemage_sword_apprentice;
				} else if (tier == Tier.ADVANCED) {
					return AncientSpellcraftItems.battlemage_sword_advanced;
				}
				return AncientSpellcraftItems.battlemage_sword_master;

			case SAGE:
				String registryName = "sage_tome_" +  (tier == Tier.NOVICE && element == Element.MAGIC ? "magic" : tier.getUnlocalisedName());
				if(element != Element.MAGIC) registryName = registryName + "_" + element.getName();
				return Item.REGISTRY.getObject(new ResourceLocation(AncientSpellcraft.MODID,  registryName));

			// well, we don't have these yet..
			case WARLOCK:
			default:
				return Items.STICK;
		}
	}

	/**
	 * Gets the IWizardClassWeapon's element, returns Element.MAGIC if it has no tag.
	 *
	 * @param stack the wizardry armour class item's ItemStack
	 * @return the element
	 */
	public static Element getElement(ItemStack stack) {
		//noinspection ConstantConditions
		if (stack.hasTagCompound() && stack.getTagCompound().hasKey(ELEMENT_TAG)) {
			return Element.fromName(stack.getTagCompound().getString(ELEMENT_TAG).toLowerCase());
		}

		return Element.MAGIC;
	}

	public static boolean isChargeFull(ItemStack stack) {
		if (stack.getItem() instanceof IWizardClassWeapon && stack.hasTagCompound()) {
			//noinspection ConstantConditions
			return stack.getTagCompound().hasKey(CHARGE_PROGRESS) && stack.getTagCompound().getInteger(CHARGE_PROGRESS) >= 100;
		}
		return false;
	}

	public static int getChargeProgress(ItemStack stack) {
		if (stack.getItem() instanceof IWizardClassWeapon && stack.hasTagCompound()) {
			//noinspection ConstantConditions
			return stack.getTagCompound().hasKey(CHARGE_PROGRESS) ? stack.getTagCompound().getInteger(CHARGE_PROGRESS) : 0;
		}
		return 0;
	}

	public static void addChargeProgress(ItemStack stack, int amount) {
		int progress = getChargeProgress(stack);

		if (stack.getItem() instanceof IWizardClassWeapon) {
			NBTTagCompound compound = stack.getTagCompound();
			if (compound == null) { compound = new NBTTagCompound(); }

			compound.setInteger(CHARGE_PROGRESS, Math.min(progress + amount, 100));
			stack.setTagCompound(compound);
		}
	}

	public static void resetChargeProgress(ItemStack stack) {
		if (stack.getItem() instanceof IWizardClassWeapon && stack.hasTagCompound()) {
			NBTTagCompound compound = stack.getTagCompound();
			if (compound == null) { compound = new NBTTagCompound(); }

			compound.setInteger(CHARGE_PROGRESS, 0);
			stack.setTagCompound(compound);
		}
	}

	/**
	 * Same as ItemWand#selectMinionTarget().
	 * Used for commanding around minions by clicking them.
	 * @author Electroblob
	 * @param player who commands the minion
	 * @param world player's world
	 * @return true if a minion was selected, false otherwise.
	 */
	static boolean selectMinionTarget(EntityPlayer player, World world) {

		RayTraceResult rayTrace = RayTracer.standardEntityRayTrace(world, player, 16, false);

		if (rayTrace != null && EntityUtils.isLiving(rayTrace.entityHit)) {

			EntityLivingBase entity = (EntityLivingBase) rayTrace.entityHit;

			// Sets the selected minion's target to the right-clicked entity
			if (player.isSneaking() && WizardData.get(player) != null && WizardData.get(player).selectedMinion != null) {

				ISummonedCreature minion = WizardData.get(player).selectedMinion.get();

				if (minion instanceof EntityLiving && minion != entity) {
					// There is now only the new AI! (which greatly improves things)
					((EntityLiving) minion).setAttackTarget(entity);
					// Deselects the selected minion
					WizardData.get(player).selectedMinion = null;
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Returns an optional itemstack of the other hand of the player, if that stack has mana available.
	 * Offhand first, mainhand last.
	 *
	 * @param weapon which needs mana
	 * @param entity who holds the weapon
	 * @return the stack in the other hand, only if it has mana. Returns Optional.empty() otherwise
	 */
	public static Optional<ItemStack> getManaSourceFromOtherHand(ItemStack weapon, EntityLivingBase entity) {

		if (entity != null) {

			// check the offhand
			if (!entity.getHeldItemMainhand().isEmpty() && entity.getHeldItemMainhand().getItem() == weapon.getItem()) {
				if (!entity.getHeldItemOffhand().isEmpty() && entity.getHeldItemOffhand().getItem() instanceof IManaStoringItem) {

					ItemStack heldItemOffhand = entity.getHeldItemOffhand();
					if (!((IManaStoringItem) heldItemOffhand.getItem()).isManaEmpty(heldItemOffhand)) {
						return Optional.of(heldItemOffhand);
					}
				}
			}
			// check the mainhand
			else if (!entity.getHeldItemOffhand().isEmpty() && entity.getHeldItemOffhand().getItem() == weapon.getItem()) {
				if (!entity.getHeldItemMainhand().isEmpty() && entity.getHeldItemMainhand().getItem() instanceof IManaStoringItem) {

					ItemStack heldItemMainhand = entity.getHeldItemMainhand();
					if (!((IManaStoringItem) heldItemMainhand.getItem()).isManaEmpty(heldItemMainhand)) {
						return Optional.of(heldItemMainhand);
					}
				}
			}
		}
		return Optional.empty();
	}

	/**
	 * Distributes the given cost (which should be the per-second cost of a continuous spell) over a second and
	 * returns the appropriate cost to be applied for the given tick. Currently the cost is distributed over 2
	 * intervals per second, meaning the returned value is 0 unless {@code castingTick} is a multiple of 10.
	 */
	public static int getDistributedCost(int cost, int castingTick) {

		int partialCost;

		if (castingTick % 20 == 0) { // Whole number of seconds has elapsed
			partialCost = cost / 2 + cost % 2; // Make sure cost adds up to the correct value by adding the remainder here
		} else if (castingTick % 10 == 0) { // Something-and-a-half seconds has elapsed
			partialCost = cost / 2;
		} else { // Some other number of ticks has elapsed
			partialCost = 0; // Wands aren't damaged within half-seconds
		}

		return partialCost;
	}
}
