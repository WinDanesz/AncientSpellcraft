package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.entity.living.EntitySkeletonHorseMinion;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftItems;
import electroblob.wizardry.entity.living.EntitySpiritHorse;
import electroblob.wizardry.spell.SpellMinion;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;

public class SummonSkeletonHorse extends SpellMinion<EntitySkeletonHorseMinion> {

	public SummonSkeletonHorse() {
		super(AncientSpellcraft.MODID, "summon_skeleton_horse", EntitySkeletonHorseMinion::new);

	}

	@Override
	protected void addMinionExtras(EntitySkeletonHorseMinion minion, BlockPos pos,
			@Nullable EntityLivingBase caster, SpellModifiers modifiers, int alreadySpawned) {
		super.addMinionExtras(minion, pos, caster, modifiers, alreadySpawned);

		if (caster instanceof EntityPlayer) {

			minion.setTamedBy((EntityPlayer) caster);
			minion.setHorseSaddled(true);

			minion.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).applyModifier(
					new AttributeModifier(POTENCY_ATTRIBUTE_MODIFIER, modifiers.get(SpellModifiers.POTENCY) - 1, EntityUtils.Operations.MULTIPLY_CUMULATIVE));
			// Jump strength increases ridiculously fast, so we're reducing the effect of the modifier by 75%
			minion.getEntityAttribute(EntitySpiritHorse.JUMP_STRENGTH).applyModifier(new AttributeModifier(POTENCY_ATTRIBUTE_MODIFIER,
					modifiers.amplified(SpellModifiers.POTENCY, 0.25f) - 1, EntityUtils.Operations.MULTIPLY_CUMULATIVE));
		}

	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == AncientSpellcraftItems.ancient_spellcraft_spell_book || item == AncientSpellcraftItems.ancient_spellcraft_scroll;
	}
}
