package com.windanesz.ancientspellcraft.item;

import com.windanesz.ancientspellcraft.Settings;
import electroblob.wizardry.Wizardry;
import electroblob.wizardry.constants.Element;
import electroblob.wizardry.data.WizardData;
import electroblob.wizardry.event.SpellCastEvent;
import electroblob.wizardry.item.ISpellCastingItem;
import electroblob.wizardry.packet.PacketCastSpell;
import electroblob.wizardry.packet.WizardryPacketHandler;
import electroblob.wizardry.registry.Spells;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
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

import static electroblob.wizardry.spell.Spell.EFFECT_DURATION;

public class ItemElementalOrb extends ItemASArtefact implements ISpellCastingItem {

	/**
	 * False if this artefact has been disabled in the config, true otherwise.
	 */
	private boolean enabled = true;

	private String spell;
	private int cooldown;
	private Element element;

	public ItemElementalOrb(EnumRarity rarity, Type type, String spell, int cooldown, Element element) {
		super(rarity, type);
		this.spell = spell;
		this.cooldown = cooldown;
		this.element = element;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack stack) {
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag advanced) {
		Wizardry.proxy.addMultiLineDescription(tooltip, "item." + this.getRegistryName() + ".desc", ((Settings.generalSettings.orb_artefact_potency_bonus * 0.01) + "%"));
		Wizardry.proxy.addMultiLineDescription(tooltip, "item." + this.getRegistryName() + ".desc2", element.getColour());
		Wizardry.proxy.addMultiLineDescription(tooltip,  "set.ancientspellcraft:elemental_orbs", new Style().setColor(TextFormatting.GOLD));

		if (!enabled)
			tooltip.add(Wizardry.proxy.translate("item." + Wizardry.MODID + ":generic.disabled", new Style().setColor(TextFormatting.RED)));
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);

		Spell spell = getCurrentSpell(stack);
		SpellModifiers modifiers = new SpellModifiers();
		if (element == Element.HEALING) {
			float f = modifiers.get(EFFECT_DURATION);
			modifiers.set(EFFECT_DURATION, f / 3, true);

		}

		if (canCast(stack, spell, player, hand, 0, modifiers)) {
			// Now we can cast continuous spells with scrolls!
			if (spell.isContinuous) {
				if (!player.isHandActive()) {
					player.setActiveHand(hand);
					// Store the modifiers for use each tick (there aren't any by default but there could be, as above)
					if (WizardData.get(player) != null)
						WizardData.get(player).itemCastingModifiers = modifiers;
					return new ActionResult<>(EnumActionResult.SUCCESS, stack);
				}
			} else {
				if (cast(stack, spell, player, hand, 0, modifiers)) {

					if (element == Element.HEALING) {
						player.removePotionEffect(MobEffects.ABSORPTION);
						player.addPotionEffect(new PotionEffect(MobEffects.ABSORPTION,
								300, 1));
						spell = Spells.blinding_flash;
						modifiers = new SpellModifiers();
						if (cast(stack, spell, player, hand, 0, modifiers)) {
							return new ActionResult<>(EnumActionResult.SUCCESS, stack);
						}
					}

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

		if (world.isRemote && !spell.isContinuous && spell.requiresPacket())
			return false;

		if (spell.cast(world, caster, hand, castingTick, modifiers)) {

			if (castingTick == 0)
				MinecraftForge.EVENT_BUS.post(new SpellCastEvent.Post(SpellCastEvent.Source.OTHER, spell, caster, modifiers));

			if (!world.isRemote) {

				// Continuous spells never require packets so don't rely on the requiresPacket method to specify it
				if (!spell.isContinuous && spell.requiresPacket()) {
					// Sends a packet to all players in dimension to tell them to spawn particles.
					IMessage msg = new PacketCastSpell.Message(caster.getEntityId(), hand, spell, modifiers);
					WizardryPacketHandler.net.sendToDimension(msg, world.provider.getDimension());
				}

				caster.getCooldownTracker().setCooldown(this, cooldown);
			}

			return true;
		}

		return false;
	}

	@Nonnull
	@Override
	public Spell getCurrentSpell(ItemStack stack) {
		return Spell.get(spell);
	}

	@Override
	public boolean showSpellHUD(EntityPlayer player, ItemStack stack) {
		return false;
	}

	@Override
	public boolean canCast(ItemStack stack, Spell spell, EntityPlayer caster, EnumHand hand, int castingTick, SpellModifiers modifiers) {
		// Even neater!
		if (castingTick == 0) {
			return !MinecraftForge.EVENT_BUS.post(new SpellCastEvent.Pre(SpellCastEvent.Source.OTHER, spell, caster, modifiers));
		} else {
			return !MinecraftForge.EVENT_BUS.post(new SpellCastEvent.Tick(SpellCastEvent.Source.OTHER, spell, caster, modifiers, castingTick));
		}
	}
}
