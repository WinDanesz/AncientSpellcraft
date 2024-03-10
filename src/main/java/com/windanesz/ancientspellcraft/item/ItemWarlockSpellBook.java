package com.windanesz.ancientspellcraft.item;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.client.gui.GuiHandlerAS;
import electroblob.wizardry.data.WizardData;
import electroblob.wizardry.item.ItemWizardArmour;
import electroblob.wizardry.spell.Spell;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ItemWarlockSpellBook extends ItemArmourClassSpellHolder {

	private static final ResourceLocation guiTexture = new ResourceLocation(AncientSpellcraft.MODID, "textures/gui/spell_book_warlock.png");

	public ItemWarlockSpellBook() {
		super();
		this.addPropertyOverride(new ResourceLocation("tier"), new IItemPropertyGetter() {
			@SideOnly(Side.CLIENT)
			public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
				return entityIn instanceof EntityPlayer &&
						Spell.byMetadata(stack.getMetadata()) != null ?
						WizardData.get((EntityPlayer) entityIn).hasSpellBeenDiscovered(Spell.byMetadata(stack.getMetadata())) ? (Spell.byMetadata(stack.getMetadata()).getTier().ordinal() + 1) * 0.1f : 0 : 0;
			}
		});
	}

	private float getSwirlProgress(ItemStack stack) {
		return ((float) stack.getItemDamage() / 10);
	}
	@Override
	public ResourceLocation getGuiTexture(Spell spell) { return guiTexture;	}


	// This is accessed during loading (before we even get to the main menu) for search tree population
	// Obviously the world is always null at that point, because no world objects exist! However, outside of a world
	// there are no guarantees as to spell metadata order so we just have to give up (and we can't account for discovery)
	// TODO: Search trees seem to get reloaded when the mappings change so in theory this should work ok, why doesn't it?
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack itemstack, World world, List<String> tooltip, net.minecraft.client.util.ITooltipFlag advanced){
		super.addInformation(itemstack,world,tooltip, advanced);

		if (GameSettings.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindSneak)) {
			AncientSpellcraft.proxy.addMultiLineDescription(tooltip, I18n.format("tooltip.ancientspellcraft:forbidden_tome.more_info"));
		} else {
			tooltip.add(I18n.format("tooltip.ancientspellcraft:more_info"));
		}
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand){
		ItemStack stack = player.getHeldItem(hand);
		player.openGui(AncientSpellcraft.instance, GuiHandlerAS.SPELL_BOOK_WARLOCK, world, 0, 0, 0);
		return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
	}

	@Override
	public ItemWizardArmour.ArmourClass getArmourClass() { return ItemWizardArmour.ArmourClass.WARLOCK; }
}
