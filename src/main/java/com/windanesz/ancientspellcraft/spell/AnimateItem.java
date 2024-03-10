package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.entity.living.EntityAnimatedItem;
import com.windanesz.ancientspellcraft.material.IDevoritium;
import com.windanesz.ancientspellcraft.registry.ASItems;
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

			if (item instanceof ISpellCastingItem || item instanceof ItemAxe || item instanceof ItemSword || item.getRegistryName().getPath().matches("sword|cestus|bow|waraxe|spear|lance|battleaxe|blade|knife|axe|javelin|rapier|saber|pike|glaive|club|halberd|mace|katana|dagger")) {
				if(!world.isRemote) caster.sendStatusMessage(new TextComponentTranslation("spell." + this.getUnlocalisedName() + ".spell_too_weak"), true);
				return false;
			}
		}

		// same as parent, without potency scaling
		if(!this.spawnMinions(world, caster, modifiers, false)) return false;
		this.playSound(world, caster, ticksInUse, -1, modifiers);
		return true;
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
			int choice = minion.world.rand.nextInt(4);

			switch (choice) {
				case 0:
					equipWith(minion, conjureItem(new SpellModifiers(), WizardryItems.spectral_sword));
					break;
				case 1:
					equipWith(minion, conjureItem(new SpellModifiers(), WizardryItems.spectral_bow));
					break;
				case 2:
					equipWith(minion, conjureItem(new SpellModifiers(), ASItems.spectral_shield));
					break;
				case 3:
					equipWith(minion, conjureItem(new SpellModifiers(), ASItems.spectral_shovel));
					break;
				default:
					equipWith(minion, conjureItem(new SpellModifiers(), WizardryItems.spectral_pickaxe));
					break;
			}
		}
	}
}
