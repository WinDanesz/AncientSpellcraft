package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.registry.ASItems;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class MetabolismOverdrive extends Spell {

	public static String CONVERSION_RATIO = "conversion_ratio";

	public MetabolismOverdrive() {
		super(AncientSpellcraft.MODID, "metabolism_overdrive", SpellActions.POINT_UP, true);
	}

	@Override
	public boolean canBeCastBy(EntityLiving npc, boolean override) {
		return true;
	}

	@Override
	public boolean canBeCastBy(TileEntityDispenser dispenser) {
		return false;
	}

	@Override
	public boolean cast(World world, EntityPlayer caster, EnumHand hand, int ticksInUse, SpellModifiers modifiers) {
		if (caster.getHealth() < caster.getMaxHealth() && caster.getFoodStats().getFoodLevel() > 1f) {
			overdrive(caster, ticksInUse);
			return true;
		}
		return false;
	}

	private void overdrive(EntityLivingBase caster, int ticksInUse) {
			if (caster instanceof EntityPlayer) {
				float delta = 0.2F;
				EntityPlayer player = (EntityPlayer) caster;
				if (!caster.world.isRemote) {
					player.addExhaustion(delta * 3.5f);
					player.heal(delta);
				}
			} else {
				caster.heal(0.1f);
			}
	}

	@Override
	public boolean cast(World world, EntityLiving caster, EnumHand hand, int ticksInUse, EntityLivingBase target, SpellModifiers modifiers) {
		overdrive(caster, ticksInUse);
		return true;
	}

	@Override
	public boolean cast(World world, double x, double y, double z, EnumFacing direction, int ticksInUse, int duration, SpellModifiers modifiers) {
		return false;
	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == ASItems.ancient_spellcraft_spell_book || item == ASItems.ancient_spellcraft_scroll;
	}
}