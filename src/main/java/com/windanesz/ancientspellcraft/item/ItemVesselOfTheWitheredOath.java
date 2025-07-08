package com.windanesz.ancientspellcraft.item;

import baubles.api.IBauble;
import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.util.ASUtils;
import electroblob.wizardry.potion.Curse;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

import java.util.List;

public class ItemVesselOfTheWitheredOath extends ItemASArtefact implements IBauble, ITickableArtefact {

	private static final String NBT_STORED_CURSE = "storedCurse";

	public ItemVesselOfTheWitheredOath(EnumRarity rarity, Type type) {
		super(rarity, type);
	}

	@Override
	public baubles.api.BaubleType getBaubleType(ItemStack itemstack) {
		return baubles.api.BaubleType.CHARM;
	}

	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int inventorySlot, boolean isSelected) {
		if (hasStoredCurse(stack)) removeStoredCurse(stack);
		super.onUpdate(stack, world, entity, inventorySlot, isSelected);
	}

	@Override
	public void onEquipped(ItemStack itemstack, EntityLivingBase player) {
		if (player instanceof EntityPlayer && !player.world.isRemote) {
			EntityPlayer entityPlayer = (EntityPlayer) player;
			// Check if the vessel already contains a curse
		}
	}

	public void storeFirstCurse(EntityLivingBase player, ItemStack stack) {
		// Find the first curse affecting the player
		PotionEffect curseToContain = findFirstCurse(player);
		if (curseToContain != null) {
			// Store the curse in the vessel
			setStoredCurse(stack, curseToContain);
			// Remove the curse from the player
			player.removePotionEffect(curseToContain.getPotion());
			// Notify the player
			ASUtils.sendMessage(player, "item.ancientspellcraft:charm_vessel_of_the_withered_oath.curse_contained", true);
		}
	}

	@Override
	public void onWornTick(ItemStack itemstack, EntityLivingBase player) {
		// This method is called every tick while the bauble is worn
		if (player instanceof EntityPlayer && player.ticksExisted % 5 == 0 && !player.world.isRemote) {
			EntityPlayer entityPlayer = (EntityPlayer) player;
			Potion storedCurse = getStoredCurse(itemstack);
			if (storedCurse != null) {
				// Check if the player has the stored curse effect and remove it
				PotionEffect activeCurse = entityPlayer.getActivePotionEffect(storedCurse);
				if (activeCurse != null) {
					entityPlayer.removePotionEffect(storedCurse);
				}
			} else {
				storeFirstCurse(player, itemstack);
			}
		}
	}

	@Override
	public void onUnequipped(ItemStack itemstack, EntityLivingBase entity) {
		if (!entity.world.isRemote) {
			// Check if the vessel contains a stored curse
			if (hasStoredCurse(itemstack)) {
				// Restore the curse to the player
				Potion storedCurse = getStoredCurse(itemstack);
				if (storedCurse != null) {
					entity.addPotionEffect(new PotionEffect(storedCurse, Integer.MAX_VALUE));
					// Clear the stored curse from the vessel
					removeStoredCurse(itemstack);
					// Notify the player
					ASUtils.sendMessage(entity,"item.ancientspellcraft:charm_vessel_of_the_withered_oath.curse_restored", true, storedCurse.getName());
				}
			}
		}
	}

	/**
	 * Finds the first curse affecting the player
	 */
	private PotionEffect findFirstCurse(EntityLivingBase entity) {
		for (PotionEffect effect : entity.getActivePotionEffects()) {
			if (effect.getPotion() instanceof Curse) {
				return effect;
			}
		}
		return null;
	}

	/**
	 * Checks if the vessel contains a stored curse
	 */
	public boolean hasStoredCurse(ItemStack itemstack) {
		return itemstack.hasTagCompound() && itemstack.getTagCompound().hasKey(NBT_STORED_CURSE);
	}

	/**
	 * Retrieves the stored curse from the vessel's NBT
	 */
	public Potion getStoredCurse(ItemStack itemstack) {
		NBTTagCompound nbt = itemstack.getTagCompound();
		if (nbt == null || !nbt.hasKey(NBT_STORED_CURSE)) {
			return null;
		}
		return Potion.getPotionFromResourceLocation(nbt.getString(NBT_STORED_CURSE));
	}

	/**
	 * Stores a curse in the vessel's NBT
	 */
	public void setStoredCurse(ItemStack itemstack, PotionEffect curse) {
		NBTTagCompound nbt = itemstack.hasTagCompound() ? itemstack.getTagCompound() : new NBTTagCompound();
		nbt.setString(NBT_STORED_CURSE, curse.getPotion().getRegistryName().toString());
		itemstack.setTagCompound(nbt);
	}

	public void removeStoredCurse(ItemStack itemstack) {
		NBTTagCompound nbt = itemstack.hasTagCompound() ? itemstack.getTagCompound() : new NBTTagCompound();
		nbt.removeTag(NBT_STORED_CURSE);
		itemstack.setTagCompound(nbt);
	}

	@Override
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, net.minecraft.client.util.ITooltipFlag flagIn) {
		super.addInformation(stack, worldIn, tooltip, flagIn);

		if (hasStoredCurse(stack)) {
			Potion storedCurse = getStoredCurse(stack);
			if (storedCurse != null) {
				tooltip.add(AncientSpellcraft.proxy.translate("item.ancientspellcraft:charm_vessel_of_the_withered_oath.contains", AncientSpellcraft.proxy.translate(storedCurse.getName())));
			}
		}
	}
}