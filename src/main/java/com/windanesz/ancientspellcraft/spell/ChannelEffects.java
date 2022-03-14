package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.registry.ASItems;
import electroblob.wizardry.constants.SpellType;
import electroblob.wizardry.item.IManaStoringItem;
import electroblob.wizardry.item.ISpellCastingItem;
import electroblob.wizardry.item.ItemScroll;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.spell.SpellBuff;
import electroblob.wizardry.spell.SpellRay;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.function.Predicate;

public class ChannelEffects extends SpellRay {
	public ChannelEffects() {
		super(AncientSpellcraft.MODID, "channel_effect", SpellActions.POINT, false);
	}

	// TODO predicate to match allowed type

	@Override
	public boolean cast(World world, EntityPlayer player, EnumHand hand, int ticksInUse, SpellModifiers modifiers) {

		// The other hand which should have an ISpellCastingItem item with a BUFF spell
		EnumHand otherHand = hand == EnumHand.MAIN_HAND ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;

		if (!player.getHeldItem(otherHand).isEmpty()) {

			ItemStack stack = player.getHeldItem(otherHand);

			if (stack.getItem() instanceof ISpellCastingItem) {
				Spell spell = ((ISpellCastingItem) stack.getItem()).getCurrentSpell(stack);

				if (isValidSpell().test(spell)) {

					if (stack.getItem() instanceof ItemScroll) {
						boolean status = super.cast(world, player, hand, ticksInUse, modifiers);

						// Consume one scroll if the casting was successful
						if (status && !world.isRemote)
							stack.shrink(1);

						return status;

					} else if (stack.getItem() instanceof ISpellCastingItem && stack.getItem() instanceof IManaStoringItem) {
						int spellCost = (int) modifiers.get(SpellModifiers.COST);

						// Only allow casting if the item has enough mana
						if (((IManaStoringItem) stack.getItem()).getMana(stack) >= spellCost) {
							boolean status = super.cast(world, player, hand, ticksInUse, modifiers);

							// Consume mana if the casting was successful
							if (status && !world.isRemote) {
								((IManaStoringItem) stack.getItem()).consumeMana(stack, spellCost, player);
							}

							return status;
						} else {
							if (!world.isRemote)
								player.sendStatusMessage(new TextComponentTranslation("spell." + this.getUnlocalisedName() + ".not_enough_mana"), true);

						}
					} else {
						if (!world.isRemote)
							player.sendStatusMessage(new TextComponentTranslation("spell." + this.getUnlocalisedName() + ".missing_required_item"), true);
					}
				} else {
					if (!world.isRemote)
						player.sendStatusMessage(new TextComponentTranslation("spell." + this.getUnlocalisedName() + ".invalid_spell"), true);
				}
			} else {
				if (!world.isRemote)
					player.sendStatusMessage(new TextComponentTranslation("spell." + this.getUnlocalisedName() + ".missing_required_item"), true);
			}
		} else {
			if (!world.isRemote)
				player.sendStatusMessage(new TextComponentTranslation("spell." + this.getUnlocalisedName() + ".missing_required_item"), true);
		}

		return false;
	}

	@Override
	protected boolean onEntityHit(World world, Entity target, Vec3d hit,
			@Nullable EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {
		if (!caster.getHeldItemOffhand().isEmpty() && caster.getHeldItemOffhand().getItem() instanceof ISpellCastingItem) {
			return attemptCastingForTarget(world, target, caster, modifiers, EnumHand.OFF_HAND);
		} else if (!caster.getHeldItemMainhand().isEmpty() && caster.getHeldItemMainhand().getItem() instanceof ISpellCastingItem) {
			return attemptCastingForTarget(world, target, caster, modifiers, EnumHand.MAIN_HAND);
		} else
			return false;
	}

	private boolean attemptCastingForTarget(World world, Entity target, EntityLivingBase caster, SpellModifiers modifiers, EnumHand hand) {
		ItemStack stack = caster.getHeldItem(hand);
		ISpellCastingItem item = (ISpellCastingItem) stack.getItem();
		Spell spell = item.getCurrentSpell(stack);
		if (isValidSpell().test(spell)) {
			if (target instanceof EntityPlayer) {
				return spell.cast(world, (EntityPlayer) target, EnumHand.MAIN_HAND, 0, modifiers);
			} else if (target instanceof EntityLiving) {
				EntityLiving npc = (EntityLiving) target;
				return spell.cast(world, npc, EnumHand.MAIN_HAND, 0, (EntityLiving) target, modifiers);
			}
		}
		return false;
	}

	@Override
	protected boolean onBlockHit(World world, BlockPos pos, EnumFacing side, Vec3d hit,
			@Nullable EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {
		return false;
	}

	@Override
	protected boolean onMiss(World world, @Nullable EntityLivingBase caster, Vec3d origin, Vec3d direction, int ticksInUse, SpellModifiers modifiers) {
		return false;
	}

	private static Predicate<Spell> isValidSpell() {
		return spell -> spell != null && spell.getType() == SpellType.BUFF || (spell instanceof SpellBuff);
	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == ASItems.ancient_spell_book || item == ASItems.ancient_spellcraft_scroll;
	}
}
