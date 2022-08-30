package com.windanesz.ancientspellcraft.item;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.material.IDevoritium;
import com.windanesz.ancientspellcraft.registry.ASPotions;
import com.windanesz.ancientspellcraft.registry.ASSounds;
import electroblob.wizardry.item.ItemArtefact;
import electroblob.wizardry.util.EntityUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class ItemDevoritiumMagnet extends ItemASArtefact implements IDevoritium, ITickableArtefact {

	@SuppressWarnings("ConstantConditions")
	public ItemDevoritiumMagnet(EnumRarity rarity, ItemArtefact.Type type) {
		super(rarity, type);
	}

	@Override
	public boolean isEnchantable(ItemStack stack) {
		return false;
	}

	@Override
	public int getItemEnchantability() { return 0; }

	@Override
	public boolean hasEffect(ItemStack stack) {
		return false;
	}

	@Override
	public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
		hitEntityDelegate(attacker, target);
		return super.hitEntity(stack, target, attacker);
	}


	/**
	 * Called when the equipped item is right clicked.
	 */
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand handIn) {
		ItemStack itemstack = player.getHeldItem(handIn);

		player.getCooldownTracker().setCooldown(this, 1200);

		if (!world.isRemote) {

			int SEARCH_RADIUS = 15;
			List<EntityCreature> entities = EntityUtils.getEntitiesWithinRadius(
					SEARCH_RADIUS,
					player.posX, player.posY, player.posZ, world, EntityCreature.class);

			for (EntityCreature target : entities) {
					target.addPotionEffect(new PotionEffect(ASPotions.magical_exhaustion, 160, 4));
				}
		}

		player.swingArm(handIn);
		world.playSound(null, player.posX, player.posY, player.posZ, ASSounds.WAR_HORN, SoundCategory.PLAYERS, 1.0F, 1.0F);
		return new ActionResult<>(EnumActionResult.SUCCESS, itemstack);
	}

	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		onUpdateDelegate(stack, worldIn, entityIn, itemSlot, isSelected);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack itemstack, World world, List<String> tooltip, net.minecraft.client.util.ITooltipFlag advanced) {
		super.addInformation(itemstack, world, tooltip, advanced);

		if (GameSettings.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindSneak)) {
			AncientSpellcraft.proxy.addMultiLineDescription(tooltip, I18n.format("tooltip.ancientspellcraft:devoritium.more_info"));
		} else {
			tooltip.add(I18n.format("tooltip.ancientspellcraft:more_info"));
		}
	}

	@Override
	public void onWornTick(ItemStack stack, EntityLivingBase player) {
		onUpdateDelegate(stack, player.world, player, 0, false);
	}
}

