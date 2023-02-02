package com.windanesz.ancientspellcraft.item;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.client.gui.GuiHandlerAS;
import com.windanesz.ancientspellcraft.spell.Runeword;
import com.windanesz.ancientspellcraft.util.ASUtils;
import electroblob.wizardry.item.ItemWizardArmour;
import electroblob.wizardry.registry.Spells;
import electroblob.wizardry.spell.Spell;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import java.util.stream.Collectors;

public class ItemRunicPlate extends ItemArmourClassSpellHolder {

	private static final ResourceLocation BATTLEMAGE_GUI = new ResourceLocation(AncientSpellcraft.MODID, "textures/gui/runic_plate.png");

	@Override
	public ResourceLocation getGuiTexture(Spell spell) {
		return BATTLEMAGE_GUI;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);
		if (Spell.byMetadata(stack.getMetadata()) == Spells.none) {
			if (stack.getCount() > 1) {
				ItemStack oldStack = stack.copy();
				oldStack.shrink(1);
				ASUtils.giveStackToPlayer(player, oldStack);
			}
			stack.setItemDamage(getRandomRuneword(world));
			stack.setCount(1);
			return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
		}
		player.openGui(AncientSpellcraft.instance, GuiHandlerAS.RUNIC_PLATE, world, player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ());
		return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
	}

	@Override
	public ItemWizardArmour.ArmourClass getArmourClass() {
		return ItemWizardArmour.ArmourClass.BATTLEMAGE;
	}


	// This is accessed during loading (before we even get to the main menu) for search tree population
	// Obviously the world is always null at that point, because no world objects exist! However, outside of a world
	// there are no guarantees as to spell metadata order so we just have to give up (and we can't account for discovery)
	// TODO: Search trees seem to get reloaded when the mappings change so in theory this should work ok, why doesn't it?
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack itemstack, World world, List<String> tooltip, net.minecraft.client.util.ITooltipFlag advanced){
		if (Spell.byMetadata(itemstack.getMetadata()) != Spells.none) {
			super.addInformation(itemstack,world,tooltip, advanced);
		}

		AncientSpellcraft.proxy.addMultiLineDescription(tooltip, I18n.format("tooltip.ancientspellcraft:right_click_to_read"));

		if (GameSettings.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindSneak)) {
			AncientSpellcraft.proxy.addMultiLineDescription(tooltip, I18n.format("tooltip.ancientspellcraft:runic_plate.more_info"));
		} else {
			tooltip.add(I18n.format("tooltip.ancientspellcraft:more_info"));
		}
	}

	public static int getRandomRuneword(World world) {
		List<Integer> runewordIDs = Spell.getAllSpells().stream().filter(s -> s instanceof Runeword).map(Spell::metadata).collect(Collectors.toList());
		if (runewordIDs.size() > 0) {
			int rnd = world.rand.nextInt(runewordIDs.size());
			return runewordIDs.get(rnd);
		}
		return 0;
	}
}
