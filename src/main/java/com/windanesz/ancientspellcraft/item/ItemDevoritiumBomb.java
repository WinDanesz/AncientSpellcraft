package com.windanesz.ancientspellcraft.item;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.entity.projectile.EntityDevoritiumBomb;
import com.windanesz.ancientspellcraft.material.IDevoritium;
import com.windanesz.ancientspellcraft.registry.ASTabs;
import electroblob.wizardry.registry.WizardrySounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class ItemDevoritiumBomb extends Item implements IDevoritium {

	public ItemDevoritiumBomb() {
		setMaxStackSize(16);
		setCreativeTab(ASTabs.ANCIENTSPELLCRAFT);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {

		ItemStack stack = player.getHeldItem(hand);

		if (!player.isCreative()) {
			stack.shrink(1);
		}

		player.playSound(WizardrySounds.ENTITY_SMOKE_BOMB_THROW, 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));

		player.getCooldownTracker().setCooldown(this, 20);

		if (!world.isRemote) {
			EntityDevoritiumBomb devoritiumBombBomb = new EntityDevoritiumBomb(world);
			devoritiumBombBomb.aim(player, 1);
			world.spawnEntity(devoritiumBombBomb);
		}

		return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
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
}