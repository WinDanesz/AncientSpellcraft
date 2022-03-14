package com.windanesz.ancientspellcraft.item;

import com.windanesz.ancientspellcraft.entity.projectile.EntityMasterBolt;
import com.windanesz.ancientspellcraft.registry.ASBlocks;
import electroblob.wizardry.Wizardry;
import electroblob.wizardry.registry.WizardrySounds;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ItemMasterBolt extends ItemBlock {

	@SuppressWarnings("ConstantConditions")
	public ItemMasterBolt() {
		super(ASBlocks.master_bolt);
		setCreativeTab(null);
		setMaxDamage(0);
		setMaxStackSize(1);
	}

	@Override
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.EPIC;
	}

	@Override
	public boolean isEnchantable(ItemStack stack) { return false; }

	@Override
	public int getItemEnchantability() { return 0; }

	@Override
	public void setDamage(ItemStack stack, int damage) {}

	@Override
	public boolean isRepairable() { return false; }

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, net.minecraft.client.util.ITooltipFlag advanced) {
		Wizardry.proxy.addMultiLineDescription(tooltip, "item." + this.getRegistryName() + ".desc");
	}


	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand){

		ItemStack stack = player.getHeldItem(hand);

		if(!player.isCreative()){
			stack.shrink(1);
		}

		player.playSound(WizardrySounds.ENTITY_SPARK_BOMB_THROW, 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));

		player.getCooldownTracker().setCooldown(this, 20);

		if(!world.isRemote){
			EntityMasterBolt masterBolt = new EntityMasterBolt(world);
			masterBolt.aim(player, 1.5f);
			world.spawnEntity(masterBolt);
		}

		return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
	}
}





