package com.windanesz.ancientspellcraft.item;

import com.windanesz.ancientspellcraft.entity.construct.EntityBarterConstruct;
import com.windanesz.ancientspellcraft.registry.ASItems;
import com.windanesz.ancientspellcraft.registry.ASSounds;
import electroblob.wizardry.item.ItemArtefact;
import electroblob.wizardry.registry.WizardrySounds;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

public class ItemBarterScroll extends ItemRareScroll {

	public ItemBarterScroll() {
		super();
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
			if (ItemArtefact.isArtefactActive(player, ASItems.charm_wizard_ale)) {
				entityBarter.wizardAleArtefact = true;
			}
			world.spawnEntity(entityBarter);

			player.sendMessage(new TextComponentTranslation("item.ancientspellcraft:bartering_scroll.beacon_started"));
			consumeScroll(player, hand);
		}

		world.playSound(player.posX, player.posY, player.posZ, ASSounds.BARTERING_SCROLL, WizardrySounds.SPELLS, 1, 1, false);

		player.setActiveHand(hand);
		player.getCooldownTracker().setCooldown(this, 120);
		return new ActionResult<>(EnumActionResult.SUCCESS, itemstack);
	}

}
