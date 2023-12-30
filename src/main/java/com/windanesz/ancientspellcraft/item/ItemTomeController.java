package com.windanesz.ancientspellcraft.item;

import com.windanesz.ancientspellcraft.entity.living.EntityAnimatedItem;
import com.windanesz.ancientspellcraft.registry.ASSpells;
import com.windanesz.ancientspellcraft.spell.AwakenTome;
import com.windanesz.ancientspellcraft.spell.TomeWarp;
import com.windanesz.ancientspellcraft.spell.Transplace;
import electroblob.wizardry.Wizardry;
import electroblob.wizardry.client.DrawingUtils;
import electroblob.wizardry.item.IManaStoringItem;
import electroblob.wizardry.item.ISpellCastingItem;
import electroblob.wizardry.util.WandHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Arrays;

public class ItemTomeController extends Item {

	public ItemTomeController() {
		super();
		this.setCreativeTab(null);
		this.setMaxStackSize(1);
		setMaxDamage(1200);
		setNoRepair();
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);
		EntityAnimatedItem tome = AwakenTome.getTome(player);
		if (tome != null && player.isSneaking() && (tome.getHeldItemMainhand().getItem() instanceof ItemSageTome &&
				Arrays.stream(WandHelper.getSpells(tome.getHeldItemMainhand())).anyMatch(s -> s instanceof TomeWarp))) {
			Wizardry.proxy.playBlinkEffect(player);
			Transplace.swapPlace(world, tome, player);
			player.getCooldownTracker().setCooldown(stack.getItem(), ASSpells.tome_warp.getCooldown());
			if (tome.getHeldItemMainhand().getItem() instanceof IManaStoringItem) {
				((IManaStoringItem) tome.getHeldItemMainhand().getItem()).consumeMana(tome.getHeldItemMainhand(), ASSpells.tome_warp.getCost(), tome);
			}
			return new ActionResult<>(EnumActionResult.PASS, stack);
		}

		if (tome != null) {
			AwakenTome.recallTome(tome);
			stack = player.getHeldItem(hand);
		}
		return new ActionResult<>(EnumActionResult.PASS, stack);
	}

	@Override
	/**
	 * Changes the attack target of the tome
	 */
	public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase wielder) {
		if (wielder instanceof EntityPlayer) {
			EntityPlayer caster = (EntityPlayer) wielder;

			EntityAnimatedItem tome = AwakenTome.getTome(caster);
			if (tome != null) {
				tome.setAttackTarget(target);
			}
		}
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
