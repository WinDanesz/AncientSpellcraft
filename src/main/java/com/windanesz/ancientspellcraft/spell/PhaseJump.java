package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.registry.ASItems;
import electroblob.wizardry.item.ItemWizardArmour;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.registry.Spells;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.spell.Banish;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class PhaseJump extends Spell implements IClassSpell {

	public static final String MINIMUM_TELEPORT_DISTANCE = "minimum_teleport_distance";
	public static final String MAXIMUM_TELEPORT_DISTANCE = "maximum_teleport_distance";
	public static final String EXTRA_DISTANCE_PER_SECOND = "extra_distance_per_second";

	public PhaseJump() {
		super(AncientSpellcraft.MODID, "phase_jump", SpellActions.POINT_UP, true);
		this.addProperties(MINIMUM_TELEPORT_DISTANCE, MAXIMUM_TELEPORT_DISTANCE, EXTRA_DISTANCE_PER_SECOND);
	}

	@Override
	public boolean cast(World world, EntityPlayer caster, EnumHand hand, int ticksInUse, SpellModifiers modifiers) {


		return true;
	}

	@Override
	public void finishCasting(World world,
			@Nullable EntityLivingBase caster, double x, double y, double z, @Nullable EnumFacing direction, int duration, SpellModifiers modifiers) {
		if (caster != null) {
			double minRadius = getProperty(MINIMUM_TELEPORT_DISTANCE).doubleValue();
			double maxRadius = getProperty(MAXIMUM_TELEPORT_DISTANCE).doubleValue();
			double bonus = (duration / 20f) * getProperty(EXTRA_DISTANCE_PER_SECOND).doubleValue();
			double radius = ((minRadius + world.rand.nextDouble() * (maxRadius-minRadius)) * modifiers.get(WizardryItems.blast_upgrade) + bonus);
			((Banish) Spells.banish).teleport(caster, world, radius);
		}
	}

	@Override
	public boolean cast(World world, EntityLiving caster, EnumHand hand, int ticksInUse, EntityLivingBase target, SpellModifiers modifiers) {
		return super.cast(world, caster, hand, ticksInUse, target, modifiers);
	}

	@Override
	public ItemWizardArmour.ArmourClass getArmourClass() { return ItemWizardArmour.ArmourClass.SAGE; }

	@Override
	public boolean applicableForItem(Item item) {
		return item == ASItems.mystic_spell_book || item == ASItems.mystic_scroll;
	}

}
