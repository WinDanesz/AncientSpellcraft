package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.entity.living.EntityAnimatedItem;
import com.windanesz.ancientspellcraft.material.IDevoritium;
import electroblob.wizardry.item.ISpellCastingItem;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemSword;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class AnimateItem extends Animate {

	public AnimateItem() {
		super(AncientSpellcraft.MODID, "animate_item");
	}

	@Override
	public boolean cast(World world, EntityPlayer caster, EnumHand hand, int ticksInUse, SpellModifiers modifiers) {

		if (caster.getHeldItemOffhand().isEmpty()) {
			if(!world.isRemote) caster.sendStatusMessage(new TextComponentTranslation("spell." + this.getUnlocalisedName() + ".no_item"), true);
			return false;
		}

		if (caster.getHeldItemOffhand().getItem() instanceof IDevoritium) {
			if(!world.isRemote) caster.sendStatusMessage(new TextComponentTranslation("spell." + this.getUnlocalisedName() + ".devoritium_item"), true);
		}

		if (!caster.getHeldItemOffhand().isEmpty()) {
			Item item = caster.getHeldItemOffhand().getItem();

			if (item instanceof ISpellCastingItem || item instanceof ItemAxe || item instanceof ItemSword || item.getRegistryName().getPath().matches("sword|waraxe|spear|lance|battleaxe|halberd|mace|katana|dagger")) {
				if(!world.isRemote) caster.sendStatusMessage(new TextComponentTranslation("spell." + this.getUnlocalisedName() + ".spell_too_weak"), true);
				return false;
			}
		}

		return super.cast(world, caster, hand, ticksInUse, modifiers);
	}

	@Override
	public boolean cast(World world, EntityLiving caster, EnumHand hand, int ticksInUse, EntityLivingBase target, SpellModifiers modifiers) {
		return super.cast(world, caster, hand, ticksInUse, target, modifiers);
	}

	@Override
	public boolean canBeCastBy(EntityLiving npc, boolean override) {
		return true;
	}

	@Override
	public boolean canBeCastBy(TileEntityDispenser dispenser) {
		return true;
	}

	@Override
	protected void addMinionExtras(EntityAnimatedItem minion, BlockPos pos, @Nullable EntityLivingBase caster, SpellModifiers modifiers, int alreadySpawned) {
		super.addMinionExtras(minion, pos, caster, modifiers, alreadySpawned);

		if (caster instanceof EntityPlayer) {
			equipFromOffhand(minion, pos, caster, modifiers);
		} else {
			equipWith(minion, conjureItem(new SpellModifiers(), WizardryItems.spectral_pickaxe));
		}
	}
}
