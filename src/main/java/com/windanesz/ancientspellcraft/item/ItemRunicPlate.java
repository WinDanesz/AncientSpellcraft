package com.windanesz.ancientspellcraft.item;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.client.gui.GuiHandlerAS;
import electroblob.wizardry.item.ItemWizardArmour;
import electroblob.wizardry.spell.Spell;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class ItemRunicPlate extends ItemArmourClassSpellHolder {

	private static final ResourceLocation BATTLEMAGE_GUI = new ResourceLocation(AncientSpellcraft.MODID, "textures/gui/runic_plate.png");

	@Override
	public ResourceLocation getGuiTexture(Spell spell) {
		return BATTLEMAGE_GUI;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);
		player.openGui(AncientSpellcraft.instance, GuiHandlerAS.RUNIC_PLATE, world, player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ());
		return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
	}

	@Override
	public ItemWizardArmour.ArmourClass getArmourClass() {
		return ItemWizardArmour.ArmourClass.BATTLEMAGE;
	}

}
