package com.windanesz.ancientspellcraft.item;

import com.google.common.collect.Multimap;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftSounds;
import electroblob.wizardry.client.DrawingUtils;
import electroblob.wizardry.item.IConjuredItem;
import electroblob.wizardry.util.EntityUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.UUID;

public class ItemStoneFist extends ItemSword implements IConjuredItem {

	private static UUID SLOWNESS_MODIFIER = UUID.fromString("53df16b3-22c3-4da8-81a7-1102105b70f5");

	public ItemStoneFist() {
		super(ToolMaterial.STONE);
		this.setMaxDamage(1);
		this.setCreativeTab(null);
		setNoRepair();
	}

	@Override
	public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {

		Multimap<String, AttributeModifier> multimap = super.getItemAttributeModifiers(slot);

		if (slot == EntityEquipmentSlot.MAINHAND) {
			multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(POTENCY_MODIFIER,
					"Potency modifier", IConjuredItem.getDamageMultiplier(stack) - 1, EntityUtils.Operations.MULTIPLY_CUMULATIVE));
		}
		if (slot == EntityEquipmentSlot.MAINHAND) {
			multimap.put(SharedMonsterAttributes.MOVEMENT_SPEED.getName(), new AttributeModifier(SLOWNESS_MODIFIER,
					"Slowness modifier modifier", -0.2, EntityUtils.Operations.MULTIPLY_CUMULATIVE));
		}
		if (slot == EntityEquipmentSlot.MAINHAND) {
			multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", -5.4000000953674316D, 0));

		}

		return multimap;
	}

	/**
	 * Called each tick as long the item is on a player inventory. Uses by maps to check if is on a player hand and
	 * update it's contents.
	 * Removes the item from inventory if the player changes it from the mainhand
	 */
	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected);

		if (entityIn instanceof EntityPlayer) {
			if (!(((EntityPlayer) entityIn).getHeldItemMainhand().getItem() instanceof ItemStoneFist)) {
				((EntityPlayer) entityIn).inventory.deleteStack(stack);
			}
		}
	}

	@Override
	public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase wielder) {
		EntityUtils.applyStandardKnockback(wielder, target, 2.5f);
		if (wielder.world.isRemote)
			wielder.world.playSound(wielder.posX, wielder.posY, wielder.posZ, AncientSpellcraftSounds.STONE_FIST, SoundCategory.PLAYERS, 1, 1, false);
		stack.damageItem(2, wielder);
		return true;
	}

	@Override
	public int getRGBDurabilityForDisplay(ItemStack stack) {
		return DrawingUtils.mix(0x737a7d, 0xadb5b8, (float) getDurabilityForDisplay(stack));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack stack) {
		return false;
	}

	@Override
	public boolean getIsRepairable(ItemStack stack, ItemStack par2ItemStack) {
		return false;
	}

	@Override
	public int getItemEnchantability() {
		return 0;
	}

	@Override
	public boolean isEnchantable(ItemStack stack) {
		return false;
	}

	@Override
	public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
		return false;
	}

	// Cannot be dropped
	@Override
	public boolean onDroppedByPlayer(ItemStack item, EntityPlayer player) {
		return false;
	}
}






































