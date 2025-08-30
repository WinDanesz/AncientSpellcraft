package com.windanesz.ancientspellcraft.item;

import electroblob.wizardry.Wizardry;
import electroblob.wizardry.event.SpellCastEvent;
import electroblob.wizardry.item.ISpellCastingItem;
import electroblob.wizardry.item.IWorkbenchItem;
import electroblob.wizardry.item.ItemArtefact;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.packet.PacketCastSpell;
import electroblob.wizardry.packet.WizardryPacketHandler;
import electroblob.wizardry.registry.Spells;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.SpellModifiers;
import electroblob.wizardry.util.WandHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class ItemAncientWand extends ItemASArtefact implements ISpellCastingItem, IWorkbenchItem {
	public static final int BASE_SPELL_SLOTS = 5;

	public ItemAncientWand(EnumRarity enumRarity, ItemArtefact.Type type) {
		super(enumRarity, type);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, net.minecraft.client.util.ITooltipFlag advanced) {
		super.addInformation(stack, worldIn, tooltip, advanced);
		Spell currentSpell = getCurrentSpell(stack);
		if (currentSpell != Spells.none) {
			tooltip.add("Current Spell: " + currentSpell.getDisplayNameWithFormatting());
		} else {
			tooltip.add("Current Spell: None");
		}
	}

	@Override
	public EnumAction getItemUseAction(ItemStack stack) {
		return SpellActions.POINT;
	}

	public int getMaxItemUseDuration(ItemStack stack) {
		return 20000;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer player, EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);

		Spell spell = getCurrentSpell(stack);
		SpellModifiers modifiers = new SpellModifiers();
		modifiers.set("potency", 1.f, false);

		cast(stack, spell, player, hand, 0, modifiers);
		if (!player.isHandActive()) {
			player.setActiveHand(hand);
			return new ActionResult<>(EnumActionResult.SUCCESS, stack);
		}

		return new ActionResult<>(EnumActionResult.FAIL, stack);
	}

	@Override
	public boolean canContinueUsing(ItemStack oldStack, ItemStack newStack) {
		// Ignore durability changes
		if (ItemStack.areItemsEqualIgnoreDurability(oldStack, newStack)) return true;
		return super.canContinueUsing(oldStack, newStack);
	}

	@Override
	public void onUsingTick(ItemStack stack, EntityLivingBase user, int count) {
		if (user instanceof EntityPlayer) {
			if (count <= 19800) { // Stop after 10 seconds (200 ticks)
				user.stopActiveHand();
			}
		}
		super.onUsingTick(stack, user, count);
	}

	// enchantment glint
	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack stack) {
		// Removed mana check for glint
		return true;
	}

	@Override
	public boolean cast(ItemStack stack, Spell spell, EntityPlayer caster, EnumHand hand, int castingTick, SpellModifiers modifiers) {

		World world = caster.world;

		if (world.isRemote && !spell.isContinuous && spell.requiresPacket()) return false;

		if (spell.cast(world, caster, hand, castingTick, modifiers)) {

			if (castingTick == 0)
				MinecraftForge.EVENT_BUS.post(new SpellCastEvent.Post(SpellCastEvent.Source.COMMAND, spell, caster, modifiers));

			if (!world.isRemote) {

				// Continuous spells never require packets so don't rely on the requiresPacket method to specify it
				if (!spell.isContinuous && spell.requiresPacket()) {
					// Sends a packet to all players in dimension to tell them to spawn particles.
					IMessage msg = new PacketCastSpell.Message(caster.getEntityId(), hand, spell, modifiers);
					WizardryPacketHandler.net.sendToDimension(msg, world.provider.getDimension());
				}
			}

			setSpell(stack, Spells.none);
			if (caster instanceof EntityPlayer) {
				caster.getCooldownTracker().setCooldown(this, spell.getCooldown() + 40);
			}
			return true;
		}
		return false;
	}

	public void setSpell(ItemStack stack, Spell spell) {
		NBTTagCompound nbt = stack.getTagCompound();
		if (nbt == null) {
			nbt = new NBTTagCompound();
			stack.setTagCompound(nbt);
		}
		nbt.setString("Spell", spell.getRegistryName().toString());
	}

	@Override
	public Spell getCurrentSpell(ItemStack stack) {
		return WandHelper.getCurrentSpell(stack);
	}

	@Override
	public Spell getNextSpell(ItemStack stack) {
		return WandHelper.getNextSpell(stack);
	}

	@Override
	public Spell getPreviousSpell(ItemStack stack) {
		return WandHelper.getPreviousSpell(stack);
	}

	@Override
	public Spell[] getSpells(ItemStack stack) {
		return WandHelper.getSpells(stack);
	}

	@Override
	public void selectNextSpell(ItemStack stack) {
		WandHelper.selectNextSpell(stack);
	}

	@Override
	public void selectPreviousSpell(ItemStack stack) {
		WandHelper.selectPreviousSpell(stack);
	}

	@Override
	public boolean selectSpell(ItemStack stack, int index) {
		return WandHelper.selectSpell(stack, index);
	}

	@Override
	public int getCurrentCooldown(ItemStack stack) {
		return WandHelper.getCurrentCooldown(stack);
	}

	@Override
	public int getCurrentMaxCooldown(ItemStack stack) {
		return WandHelper.getCurrentMaxCooldown(stack);
	}

	@Override
	public boolean canCast(ItemStack stack, Spell spell, EntityPlayer caster, EnumHand hand, int castingTick, SpellModifiers modifiers) {
		return !caster.getCooldownTracker().hasCooldown(stack.getItem());
	}

	@Override
	public boolean showSpellHUD(EntityPlayer player, ItemStack stack) {
		return true;
	}

	@Override
	public int getSpellSlotCount(ItemStack stack) {
		return 1;
	}

	@Override
	public boolean onApplyButtonPressed(EntityPlayer player, Slot centre, Slot crystals, Slot upgrade, Slot[] spellBooks) {
		boolean changed = false; // Used for advancements

		// Reads NBT spell metadata array to variable, edits this, then writes it back to NBT.
		// Original spells are preserved; if a slot is left empty the existing spell binding will remain.
		// Accounts for spells which cannot be applied because they are above the wand's tier; these spells
		// will not bind but the existing spell in that slot will remain and other applicable spells will
		// be bound as normal, along with any upgrades and crystals.
		Spell[] spells = WandHelper.getSpells(centre.getStack());

		if (spells.length <= 0) {
			// Base value here because if the spell array doesn't exist, the wand can't possibly have attunement upgrades
			spells = new Spell[1];
		}

		for (int i = 0; i < spells.length; i++) {
			if (spellBooks[i].getStack() != ItemStack.EMPTY) {

				Spell spell = Spell.byMetadata(spellBooks[i].getStack().getItemDamage());
				// Decide if we can bind this multiple times
				if (Wizardry.settings.preventBindingSameSpellTwiceToWands && Arrays.stream(spells).anyMatch(s -> s == spell)) {
					continue;
				}

				spells[i] = spell;
				changed = true;

				// setting to consume books upon use
				if (Wizardry.settings.singleUseSpellBooks) {
					spellBooks[i].getStack().shrink(1);
				}
			}
		}

		//if (spells.length <= 0) {
			WandHelper.setSpells(centre.getStack(), spells);
		//}

		// Charges wand by appropriate amount
		if (WandHelper.rechargeManaOnApplyButtonPressed(centre, crystals)) {
			changed = true;
		}

		return changed;
	}

	@Override
	public boolean showTooltip(ItemStack stack) {
		return false;
	}


}
