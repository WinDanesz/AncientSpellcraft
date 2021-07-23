package com.windanesz.ancientspellcraft.item;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftItems;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftSpells;
import electroblob.wizardry.item.IConjuredItem;
import electroblob.wizardry.util.InventoryUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static com.windanesz.ancientspellcraft.spell.IceCream.*;

public class ItemIceCream extends ItemFood implements IConjuredItem {

	public ItemIceCream() {
		super(1, 0.6F, false); // same amount as a beetroot
		setMaxDamage(400);

		//noinspection ConstantConditions
		setCreativeTab(null);
		setAlwaysEdible();
	}

	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack stack) {
		return false;
	}

	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.RARE;
	}

	@Override
	public int getMaxDamage(ItemStack stack) {
		return this.getMaxDamageFromNBT(stack, AncientSpellcraftSpells.ice_cream);
	}

	@Override
	public int getRGBDurabilityForDisplay(ItemStack stack) {
		return IConjuredItem.getTimerBarColour(stack);
	}

	/**
	 * Called for extra effects. The default food behaviour is handled elsewhere.
	 */
	protected void onFoodEaten(ItemStack stack, World worldIn, EntityPlayer player) {

		if (!worldIn.isRemote) {
			player.heal(getHealingAmount(stack));

			if (stack.hasTagCompound() && stack.getTagCompound().hasKey("potion")) {
				String potionString = stack.getTagCompound().getString("potion");
				Potion potion = ForgeRegistries.POTIONS.getValue(new ResourceLocation(potionString));
				if (potion != null) {
					player.addPotionEffect(new PotionEffect(potion, 240));
				}
			}
			//			if (stack.getMetadata() > 0) {
			//				player.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 400, 1));
			//				player.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 6000, 0));
			//				player.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, 6000, 0));
			//				player.addPotionEffect(new PotionEffect(MobEffects.ABSORPTION, 2400, 3));
			//			} else {
			//				player.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 100, 1));
			//				player.addPotionEffect(new PotionEffect(MobEffects.ABSORPTION, 2400, 0));
			//			}
		}
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		return AncientSpellcraft.proxy.getIceCreamDisplayName(stack);
	}

	// a bit confusing naming from vanilla, but this means the food chunk this refills
	@Override
	public int getHealAmount(ItemStack stack) {
		if (stack.hasTagCompound() && stack.getTagCompound().hasKey(HUNGER_RESTORE_AMOUNT)) {
			return stack.getTagCompound().getInteger(HUNGER_RESTORE_AMOUNT);
		}
		return super.getHealAmount(stack);
	}

	public float getHealingAmount(ItemStack stack) {
		if (stack.hasTagCompound() && stack.getTagCompound().hasKey(HEALING_AMOUNT)) {
			return stack.getTagCompound().getInteger(HEALING_AMOUNT);
		}
		return 2;
	}

	@Override
	public float getSaturationModifier(ItemStack stack) {
		if (stack.hasTagCompound() && stack.getTagCompound().hasKey(SATURATION_AMOUNT)) {
			return stack.getTagCompound().getInteger(SATURATION_AMOUNT);
		}
		return super.getSaturationModifier(stack);
	}

	// standard overrides

	@Override
	// This method allows the code for the item's timer to be greatly simplified by damaging it directly from
	// onUpdate() and removing the workaround that involved WizardData and all sorts of crazy stuff.
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {

		if (!oldStack.isEmpty() || !newStack.isEmpty()) {
			// We only care about the situation where we specifically want the animation NOT to play.
			if (oldStack.getItem() == newStack.getItem() && !slotChanged)
				// This code should only run on the client side, so using Minecraft is ok.
				// Why the heck was this here?
				//&& !net.minecraft.client.Minecraft.getMinecraft().player.isHandActive())
				return false;
		}

		return super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged);
	}

	@Override
	public boolean canContinueUsing(ItemStack oldStack, ItemStack newStack) {
		// Ignore durability changes
		if (ItemStack.areItemsEqualIgnoreDurability(oldStack, newStack))
			return true;
		return super.canContinueUsing(oldStack, newStack);
	}

	@Override
	public boolean shouldCauseBlockBreakReset(ItemStack oldStack, ItemStack newStack) {
		// Ignore durability changes
		if (ItemStack.areItemsEqualIgnoreDurability(oldStack, newStack))
			return false;
		return super.shouldCauseBlockBreakReset(oldStack, newStack);
	}

	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
		int damage = stack.getItemDamage();
		if (damage > stack.getMaxDamage())
			InventoryUtils.replaceItemInInventory(entity, slot, stack, ItemStack.EMPTY);
		stack.setItemDamage(damage + 1);
	}

	@Override
	public boolean onDroppedByPlayer(ItemStack item, EntityPlayer player) {
		return false;
	}
}