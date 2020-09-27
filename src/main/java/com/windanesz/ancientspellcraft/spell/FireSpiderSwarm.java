package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.entity.living.EntityFireAnt;
import electroblob.wizardry.spell.SpellMinion;
import electroblob.wizardry.util.SpellModifiers;
import electroblob.wizardry.util.WizardryUtilities;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;

public class FireSpiderSwarm extends SpellMinion<EntityFireAnt> {

	public FireSpiderSwarm() {
		super(AncientSpellcraft.MODID, "fire_spider_swarm", EntityFireAnt::new);
	}

	@Override
	protected void addMinionExtras(EntityFireAnt minion, BlockPos pos, @Nullable EntityLivingBase caster, SpellModifiers modifiers, int alreadySpawned) {
		super.addMinionExtras(minion, pos, caster, modifiers, alreadySpawned);

		// Attribute modifiers are pretty opaque, see https://minecraft.gamepedia.com/Attribute#Modifiers
		IAttributeInstance attribute = minion.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);

		if (attribute != null)
			attribute.applyModifier( // Apparently some things don't have an attack damage
					new AttributeModifier("potency", modifiers.get(SpellModifiers.POTENCY) - 1 / 2, WizardryUtilities.Operations.MULTIPLY_CUMULATIVE));
	}
}
