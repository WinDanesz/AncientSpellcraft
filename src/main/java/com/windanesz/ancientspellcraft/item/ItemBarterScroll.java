package com.windanesz.ancientspellcraft.item;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftSounds;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftTabs;
import com.windanesz.ancientspellcraft.entity.construct.EntityBarterConstruct;
import electroblob.wizardry.registry.WizardrySounds;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ItemBarterScroll extends Item {

	public ItemBarterScroll() {
		super();
		setCreativeTab(AncientSpellcraftTabs.ANCIENTSPELLCRAFT);
		setMaxStackSize(1);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack stack) {
		return true;
	}

	@Override
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.RARE;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, net.minecraft.client.util.ITooltipFlag flag) {
		AncientSpellcraft.proxy.addMultiLineDescription(tooltip, "item." + this.getRegistryName() + ".desc");
	}

	/**
	 * Called when the equipped item is right clicked.
	 */
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack itemstack = player.getHeldItem(hand);

		if (!player.onGround) {
			return new ActionResult<>(EnumActionResult.FAIL, itemstack);
		}

		if (!world.canSeeSky(player.getPosition()) && !world.isRemote) {
			player.sendMessage(new TextComponentTranslation("item.ancientspellcraft:bartering_scroll.must_cast_outdoors"));
			return new ActionResult<>(EnumActionResult.FAIL, itemstack);
		}

		if (!world.isRemote) {
			EntityBarterConstruct entityBarter = new EntityBarterConstruct(world);
			entityBarter.setPosition(player.posX + 0.1f, player.posY, player.posZ + 0.1f);
			entityBarter.setCaster(player);
			world.spawnEntity(entityBarter);

			player.sendMessage(new TextComponentTranslation("item.ancientspellcraft:bartering_scroll.beacon_started"));
			itemstack.shrink(1);
		}

		world.playSound(player.posX, player.posY, player.posZ, AncientSpellcraftSounds.BARTERING_SCROLL, WizardrySounds.SPELLS, 1, 1, false);

		player.setActiveHand(hand);
		player.getCooldownTracker().setCooldown(this, 120);
		return new ActionResult<>(EnumActionResult.SUCCESS, itemstack);
	}

}
