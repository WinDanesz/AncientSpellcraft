package com.windanesz.ancientspellcraft.item;

import com.windanesz.ancientspellcraft.Settings;
import com.windanesz.ancientspellcraft.entity.living.EntityEvilClassWizard;
import com.windanesz.ancientspellcraft.util.ASUtils;
import electroblob.wizardry.util.BlockUtils;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

public class ItemAmuletCursedPendant extends ItemASArtefact implements ITickableArtefact {

	public ItemAmuletCursedPendant(EnumRarity rarity, Type type) {
		super(rarity, type);
	}

	@Override
	public void onWornTick(ItemStack stack, EntityLivingBase entity) {
		// 18000 = midnight
		if (entity instanceof EntityPlayer && !entity.world.isRemote && entity.world.getWorldTime() % 24000 == 18000 &&
				entity.world.rand.nextFloat() <= Settings.generalSettings.cursed_pendant_summon_chance
				&& !((EntityPlayer) entity).getCooldownTracker().hasCooldown(this)) {
			BlockPos pos = BlockUtils.findNearbyFloorSpace(entity, 8, 8);
			if (pos != null) {
				ASUtils.sendMessage(entity, "item.ancientspellcraft:amulet_cursed_pendant.summoned_wizard", false);
				EntityEvilClassWizard wizard = new EntityEvilClassWizard(entity.world);
				wizard.setPosition(pos.getX() + 0.5f, pos.getY(), pos.getZ() + 0.5f);
				wizard.onInitialSpawn(entity.world.getDifficultyForLocation(BlockPos.ORIGIN), null);
				wizard.setRevengeTarget(entity);
				entity.world.spawnEntity(wizard);
				((EntityPlayer) entity).getCooldownTracker().setCooldown(this, 18000);
			}
		}
	}
}
