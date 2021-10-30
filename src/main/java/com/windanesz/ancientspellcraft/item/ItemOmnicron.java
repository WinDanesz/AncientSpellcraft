package com.windanesz.ancientspellcraft.item;

import com.windanesz.ancientspellcraft.registry.AncientSpellcraftSounds;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftTabs;
import electroblob.wizardry.Wizardry;
import electroblob.wizardry.client.DrawingUtils;
import electroblob.wizardry.constants.Constants;
import electroblob.wizardry.data.WizardData;
import electroblob.wizardry.event.DiscoverSpellEvent;
import electroblob.wizardry.item.IManaStoringItem;
import electroblob.wizardry.item.IWorkbenchItem;
import electroblob.wizardry.item.ItemScroll;
import electroblob.wizardry.item.ItemSpellBook;
import electroblob.wizardry.registry.WizardryRecipes;
import electroblob.wizardry.spell.Spell;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ItemOmnicron extends ItemASArtefact implements IWorkbenchItem, IManaStoringItem {

	private static final int MAX_MANA = 10000;

	public ItemOmnicron(EnumRarity rarity, Type type) {
		super(rarity, type);
		setMaxStackSize(1);
		setCreativeTab(AncientSpellcraftTabs.ANCIENTSPELLCRAFT_GEAR);
		setMaxDamage(MAX_MANA);
		WizardryRecipes.addToManaFlaskCharging(this);
	}

	@Override
	public EnumRarity getRarity(ItemStack stack){
		return EnumRarity.EPIC;
	}

	@Override
	public boolean hasEffect(ItemStack stack){
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, net.minecraft.client.util.ITooltipFlag advanced){
		tooltip.add(Wizardry.proxy.translate("item." + Wizardry.MODID + ":wand.mana", new Style().setColor(TextFormatting.BLUE),
				this.getMana(stack), this.getManaCapacity(stack)));
		//Wizardry.proxy.addMultiLineDescription(tooltip, "item." + this.getRegistryName() + ".desc");
		Wizardry.proxy.addMultiLineDescription(tooltip, "tooltip.ancientspellcraft:artefact_use.usage", new Style().setItalic(true));
		super.addInformation(stack, world, tooltip, advanced);
	}

	@Override
	public int getRGBDurabilityForDisplay(ItemStack stack){
		return DrawingUtils.mix(0xff8bfe, 0x8e2ee4, (float)getDurabilityForDisplay(stack));
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {

		ItemStack cubeStack = player.getHeldItem(hand);

		if (hand != EnumHand.MAIN_HAND) {
			if (!world.isRemote)
				player.sendMessage(new TextComponentTranslation("item." +  this.getRegistryName() + ".wrong_hand"));
			return new ActionResult<>(EnumActionResult.PASS, cubeStack);
		}

		ItemStack offhandStack = player.getHeldItemOffhand();

		if (!(offhandStack.getItem() instanceof ItemSpellBook || offhandStack.getItem() instanceof ItemScroll)) {
			if (!world.isRemote)
				player.sendMessage(new TextComponentTranslation("item." +  this.getRegistryName() + ".wrong_item"));
			return new ActionResult<>(EnumActionResult.PASS, cubeStack);
		}

		if (WizardData.get(player) != null) {

			WizardData data = WizardData.get(player);

				Spell spell = Spell.byMetadata(offhandStack.getItemDamage());
				if (!data.hasSpellBeenDiscovered(spell)) {

					int requiredMana;

					switch (spell.getTier()) {
						case NOVICE:
							requiredMana = 3000;
							break;
						case APPRENTICE:
							requiredMana = 5000;
							break;
						case ADVANCED:
							requiredMana = 7000;
							break;
						case MASTER:
							requiredMana = 10000;
							break;
						default:
							requiredMana = 5000;
					}

					if (getMana(cubeStack) < requiredMana) {
						if (!world.isRemote)
							player.sendMessage(new TextComponentTranslation("item." +  this.getRegistryName() + ".no_mana", requiredMana));
						return new ActionResult<>(EnumActionResult.PASS, cubeStack);
					}

					if (!MinecraftForge.EVENT_BUS.post(new DiscoverSpellEvent(player, spell,
							DiscoverSpellEvent.Source.OTHER))) {
						// Identification scrolls give the chat readout in creative mode, otherwise it looks like
						// nothing happens!
						data.discoverSpell(spell);
						player.playSound(AncientSpellcraftSounds.CHARM_OMNICRON, 1.25f, 1);
						if (!world.isRemote) {
							consumeMana(cubeStack, requiredMana, player);
							player.sendMessage(new TextComponentTranslation("item." + this.getRegistryName() + ".discover",
									spell.getNameForTranslationFormatted()));
						}

						return new ActionResult<>(EnumActionResult.SUCCESS, cubeStack);
					}
				}

			// If it found nothing to identify
			if (!world.isRemote)
				player.sendMessage(
						new TextComponentTranslation("item." + Wizardry.MODID + ":identification_scroll.nothing_to_identify"));
		}

		return new ActionResult<>(EnumActionResult.FAIL, cubeStack);
	}

	@Override
	public int getMana(ItemStack stack) {
		return getManaCapacity(stack) - getDamage(stack);
	}

	@Override
	public void setMana(ItemStack stack, int mana) {
		// Using super (which can only be done from in here) bypasses the above override
		super.setDamage(stack, getManaCapacity(stack) - mana);
	}

	@Override
	public int getManaCapacity(ItemStack stack) {
		return MAX_MANA;
	}

	@Override
	public int getSpellSlotCount(ItemStack stack) {
		return 0;
	}

	@Override
	public boolean onApplyButtonPressed(EntityPlayer player, Slot centre, Slot crystals, Slot upgrade, Slot[] spellBooks) {
		boolean changed = false;
		if (crystals.getStack() != ItemStack.EMPTY && !this.isManaFull(centre.getStack())) {

			int chargeDepleted = this.getManaCapacity(centre.getStack()) - this.getMana(centre.getStack());

			if (crystals.getStack().getCount() * Constants.MANA_PER_CRYSTAL < chargeDepleted) {
				// If there aren't enough crystals to fully charge the name tag
				this.rechargeMana(centre.getStack(), crystals.getStack().getCount() * Constants.MANA_PER_CRYSTAL);
				crystals.decrStackSize(crystals.getStack().getCount());

			} else {
				// If there are excess crystals (or just enough)
				this.setMana(centre.getStack(), this.getManaCapacity(centre.getStack()));
				crystals.decrStackSize((int) Math.ceil(((double) chargeDepleted) / Constants.MANA_PER_CRYSTAL));
			}

			changed = true;
		}

		return changed;
	}

	@Override
	public boolean showTooltip(ItemStack stack) {
		return false;
	}
}
