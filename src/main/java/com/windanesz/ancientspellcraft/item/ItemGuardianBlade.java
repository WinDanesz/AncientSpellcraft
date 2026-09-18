package com.windanesz.ancientspellcraft.item;

import com.windanesz.ancientspellcraft.Settings;
import com.windanesz.ancientspellcraft.entity.living.EntityAnimatedItem;
import com.windanesz.ancientspellcraft.integration.baubles.ASBaublesIntegration;
import com.windanesz.ancientspellcraft.registry.ASItems;
import electroblob.wizardry.util.BlockUtils;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

public class ItemGuardianBlade extends ItemASArtefact implements ITickableArtefact {

	public ItemGuardianBlade(EnumRarity rarity, Type type) {
		super(rarity, type);
	}

	@Override
	public void onWornTick(ItemStack stack, EntityLivingBase player) {
		if (!player.world.isRemote && player.world.getTotalWorldTime() % 20 == 0 && player.getHealth() <= player.getMaxHealth() * 0.33f) {
			if (player instanceof EntityPlayer && !((EntityPlayer) player).getCooldownTracker().hasCooldown(this)) {
				BlockPos pos = BlockUtils.findNearbyFloorSpace(player, 4, 8);
				if (pos != null && pos != BlockPos.ORIGIN) {

					EntityAnimatedItem minion = new EntityAnimatedItem(player.world);
					// In this case we don't care whether the minions can fly or not.
					minion.setPosition(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
					minion.setLifetime(300);
					minion.setCaster(player);

					IAttributeInstance healthAttr = minion.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH);
					if (healthAttr != null) {
						healthAttr.setBaseValue(Settings.generalSettings.guardian_blade_hp);
						minion.setHealth((float) Settings.generalSettings.guardian_blade_hp);
					}

					IAttributeInstance attribute = minion.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
					if (attribute != null) {
						attribute.setBaseValue(Settings.generalSettings.guardian_blade_damage);
					}

					minion.setHeldItem(EnumHand.MAIN_HAND, new ItemStack(ASItems.charm_guardian_blade));
					//AnimateWeapon.addAnimatedEntityExtras(minion, wielder.getPosition(), wielder, new SpellModifiers());
					player.world.spawnEntity(minion);
				}
				((EntityPlayer) player).getCooldownTracker().setCooldown(this, 600);
				ASBaublesIntegration.setArtefactToSlot((EntityPlayer) player, ItemStack.EMPTY, Type.CHARM);
			}
		}
	}
}
