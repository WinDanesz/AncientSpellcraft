package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.entity.living.EntitySpiritBear;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftItems;
import electroblob.wizardry.data.IStoredVariable;
import electroblob.wizardry.data.Persistence;
import electroblob.wizardry.data.WizardData;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.spell.SpellMinion;
import electroblob.wizardry.util.BlockUtils;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.UUID;

public class SummonSpiritBear extends Spell {

	/**
	 * The string identifier for the potency attribute modifier.
	 */
	private static final String POTENCY_ATTRIBUTE_MODIFIER = "potency";

	public static final IStoredVariable<UUID> SPIRIT_BEAR_UUID = IStoredVariable.StoredVariable.ofUUID("spiritBearUUID", Persistence.ALWAYS);

	public SummonSpiritBear() {
		super(AncientSpellcraft.MODID, "summon_spirit_bear", SpellActions.SUMMON, false);
		addProperties(SpellMinion.SUMMON_RADIUS);
		soundValues(0.7f, 1.2f, 0.4f);
		WizardData.registerStoredVariables(SPIRIT_BEAR_UUID);
	}

	@Override
	public boolean requiresPacket() {
		return false;
	}

	@Override
	public boolean cast(World world, EntityPlayer caster, EnumHand hand, int ticksInUse, SpellModifiers modifiers) {

		WizardData data = WizardData.get(caster);

		if (!world.isRemote) {

			Entity oldWolf = EntityUtils.getEntityByUUID(world, data.getVariable(SPIRIT_BEAR_UUID));

			if (oldWolf != null)
				oldWolf.setDead();

			BlockPos pos = BlockUtils.findNearbyFloorSpace(caster, 2, 4);
			if (pos == null)
				return false;

			EntitySpiritBear bear = new EntitySpiritBear(world);
			bear.setPosition(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
			bear.setOwnerId(caster.getUniqueID());

			//			EntitySpiritWolf wolf = new EntitySpiritWolf(world);
			//			wolf.setPosition(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
			//			wolf.setTamed(true);
			//			wolf.setOwnerId(caster.getUniqueID());
			// Potency gives the wolf more strength AND more health
			bear.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).applyModifier(
					new AttributeModifier(POTENCY_ATTRIBUTE_MODIFIER, (modifiers.get(SpellModifiers.POTENCY)) - 1, EntityUtils.Operations.MULTIPLY_CUMULATIVE));
			bear.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).applyModifier(
					new AttributeModifier(POTENCY_ATTRIBUTE_MODIFIER, modifiers.amplified(SpellModifiers.POTENCY, 1.5f) - 1, EntityUtils.Operations.MULTIPLY_CUMULATIVE));
			bear.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).applyModifier(
					new AttributeModifier(POTENCY_ATTRIBUTE_MODIFIER, modifiers.amplified(SpellModifiers.POTENCY, 0.05f) - 1, EntityUtils.Operations.MULTIPLY_CUMULATIVE));
			bear.setHealth(bear.getMaxHealth());

			world.spawnEntity(bear);

			data.setVariable(SPIRIT_BEAR_UUID, bear.getUniqueID());
		}

		this.playSound(world, caster, ticksInUse, -1, modifiers);
		return true;
	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == AncientSpellcraftItems.ancient_spellcraft_spell_book || item == AncientSpellcraftItems.ancient_spellcraft_scroll;
	}

}
