package com.windanesz.ancientspellcraft.item;

import com.windanesz.ancientspellcraft.Settings;
import electroblob.wizardry.Wizardry;
import electroblob.wizardry.event.SpellCastEvent;
import electroblob.wizardry.item.IManaStoringItem;
import electroblob.wizardry.item.ISpellCastingItem;
import electroblob.wizardry.packet.PacketCastSpell;
import electroblob.wizardry.packet.WizardryPacketHandler;
import electroblob.wizardry.registry.Spells;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ItemCubePhasing extends ItemASArtefact implements ISpellCastingItem, IManaStoringItem, ITickableArtefact {

	public ItemCubePhasing(EnumRarity rarity, Type type) {
		super(rarity, type);
		this.setMaxDamage(1000);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack stack) {
		return !isManaEmpty(stack);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag advanced) {
		Wizardry.proxy.addMultiLineDescription(tooltip, "item." + this.getRegistryName() + ".desc", Settings.generalSettings.orb_artefact_potency_bonus);

		if (!Settings.isArtefactEnabled(this)) {
			tooltip.add(Wizardry.proxy.translate("item." + Wizardry.MODID + ":generic.disabled", new Style().setColor(TextFormatting.RED)));
		}
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);

		if (Settings.isArtefactEnabled(this)) {

			Spell spell = getSpell(player, world);
			SpellModifiers modifiers = new SpellModifiers();

			if (canCast(stack, spell, player, hand, 0, modifiers)) {
				// Now we can cast continuous spells with scrolls!
				if (cast(stack, spell, player, hand, 0, modifiers)) {
					return new ActionResult<>(EnumActionResult.SUCCESS, stack);
				}
			}
		}

		return new ActionResult<>(EnumActionResult.FAIL, stack);
	}

	@Override
	public void onPlayerStoppedUsing(ItemStack stack, World world, EntityLivingBase user, int timeLeft) {
		// Casting has stopped before the full time has elapsed
		finishCasting(stack, user, timeLeft);
	}

	@Override
	public ItemStack onItemUseFinish(ItemStack stack, World world, EntityLivingBase user) {
		// Full casting time has elapsed
		finishCasting(stack, user, 0);
		return stack;
	}

	private void finishCasting(ItemStack stack, EntityLivingBase user, int timeLeft) {

		if (Spell.byMetadata(stack.getItemDamage()).isContinuous) {

			Spell spell = Spell.byMetadata(stack.getItemDamage());
			SpellModifiers modifiers = new SpellModifiers();
			int castingTick = stack.getMaxItemUseDuration() - timeLeft;

			MinecraftForge.EVENT_BUS.post(new SpellCastEvent.Finish(SpellCastEvent.Source.SCROLL, spell, user, modifiers, castingTick));
			spell.finishCasting(user.world, user, Double.NaN, Double.NaN, Double.NaN, null, castingTick, modifiers);

			if (user instanceof EntityPlayer) {
				((EntityPlayer) user).getCooldownTracker().setCooldown(this, spell.getCooldown());
			}
		}
	}

	@Override
	public boolean cast(ItemStack stack, Spell spell, EntityPlayer caster, EnumHand hand, int castingTick, SpellModifiers modifiers) {

		World world = caster.world;

		if (world.isRemote && !spell.isContinuous && spell.requiresPacket()) { return false; }

		if (spell.cast(world, caster, hand, castingTick, modifiers)) {

			// no Post event
			// if (castingTick == 0) { MinecraftForge.EVENT_BUS.post(new SpellCastEvent.Post(SpellCastEvent.Source.OTHER, spell, caster, modifiers)); }

			if (!world.isRemote) {

				// Continuous spells never require packets so don't rely on the requiresPacket method to specify it
				if (!spell.isContinuous && spell.requiresPacket()) {
					// Sends a packet to all players in dimension to tell them to spawn particles.
					IMessage msg = new PacketCastSpell.Message(caster.getEntityId(), hand, spell, modifiers);
					WizardryPacketHandler.net.sendToDimension(msg, world.provider.getDimension());
				}

				caster.getCooldownTracker().setCooldown(this, 10);
				consumeMana(stack,getManaCost(spell),caster);
			}



			return true;
		}

		return false;
	}

	@Nonnull
	@Override
	public Spell getCurrentSpell(ItemStack stack) {
		return Spells.none;
	}

	@Override
	public boolean showSpellHUD(EntityPlayer player, ItemStack stack) {
		return false;
	}

	@Override
	public boolean canCast(ItemStack stack, Spell spell, EntityPlayer caster, EnumHand hand, int castingTick, SpellModifiers modifiers) {
		// Even neater!
		if (getMana(stack) < getManaCost(spell)) {
			return false;
		}

		if (castingTick == 0) {
			return !MinecraftForge.EVENT_BUS.post(new SpellCastEvent.Pre(SpellCastEvent.Source.OTHER, spell, caster, modifiers));
		} else {
			return !spell.isContinuous;
		}
	}

	@Override
	public void setDamage(ItemStack stack, int damage) {
		// Overridden to do nothing to stop repair things from 'repairing' the mana in a wand
	}

	@Override
	public void setMana(ItemStack stack, int mana) {
		// Using super (which can only be done from in here) bypasses the above override
		super.setDamage(stack, getManaCapacity(stack) - mana);
	}

	@Override
	public int getMana(ItemStack stack) {
		return getManaCapacity(stack) - getDamage(stack);
	}

	@Override
	public int getManaCapacity(ItemStack stack) {
		return this.getMaxDamage(stack);
	}

	public void addCharge(ItemStack stack) {
		setMana(stack, getMana(stack) + 20);
	}

	private int getManaCost(Spell spell) {
		return spell == Spells.blink ? 200 : 400;
	}

	private Spell getSpell(EntityPlayer caster, World world) {
		return caster.isSneaking() ? Spells.phase_step : Spells.blink;

	}

	@Override
	public void onWornTick(ItemStack stack, EntityLivingBase player) {
		if (player.world.getTotalWorldTime() % 20 == 0 && !isManaFull(stack)) {
			addCharge(stack);
		}
	}
}
