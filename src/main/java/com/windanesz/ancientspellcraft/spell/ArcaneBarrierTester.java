package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.entity.EntityArcaneBarrier;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.spell.SpellConstruct;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;

public class ArcaneBarrierTester extends SpellConstruct<EntityArcaneBarrier> {

	public ArcaneBarrierTester(){
		super(AncientSpellcraft.MODID, "arcane_barrier_tester", SpellActions.THRUST, EntityArcaneBarrier::new, false);
		addProperties(Spell.EFFECT_RADIUS);
	}

	@Override
	protected void addConstructExtras(EntityArcaneBarrier construct, EnumFacing side, EntityLivingBase caster, SpellModifiers modifiers){
		construct.setRadius(getProperty(EFFECT_RADIUS).floatValue() * modifiers.get(WizardryItems.blast_upgrade) * 10);
	}
}
