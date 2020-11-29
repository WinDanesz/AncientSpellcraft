package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.entity.living.EntityWolfMinion;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftItems;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.BlockUtils;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class CallOfThePack extends Spell {

	public static final String MINION_LIFETIME = "minion_lifetime";
	public static final String MINION_COUNT = "minion_count";
	public static final String SUMMON_RADIUS = "summon_radius";

	/**
	 * The string identifier for the minion health spell modifier, which doubles as the identifier for the
	 * entity attribute modifier.
	 */
	public static final String HEALTH_MODIFIER = "minion_health";
	/**
	 * The string identifier for the potency attribute modifier.
	 */
	public static final String POTENCY_ATTRIBUTE_MODIFIER = "potency";

	public CallOfThePack() {
		super(AncientSpellcraft.MODID, "call_of_the_pack", SpellActions.SUMMON, false);
		addProperties(MINION_LIFETIME, MINION_COUNT, SUMMON_RADIUS);
	}

	protected void addMinionExtras(EntityWolfMinion minion, BlockPos pos, @Nullable EntityLivingBase caster, SpellModifiers modifiers, int alreadySpawned) {
		//		minion.setTamed(true);
		//		minion.setOwnerId(caster.getUniqueID());
		minion.onInitialSpawn(minion.world.getDifficultyForLocation(pos), null);
	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == AncientSpellcraftItems.ancient_spellcraft_spell_book || item == AncientSpellcraftItems.ancient_spellcraft_scroll;
	}

	@Override
	public boolean cast(World world, EntityPlayer caster, EnumHand hand, int ticksInUse, SpellModifiers modifiers) {

		if (!this.spawnMinions(world, caster, modifiers))
			return false;
		this.playSound(world, caster, ticksInUse, -1, modifiers);
		return true;
	}

	@SuppressWarnings("Duplicates")
	protected boolean spawnMinions(World world, EntityLivingBase caster, SpellModifiers modifiers) {
		if (!world.isRemote) {
			for (int i = 0; i < getProperty(MINION_COUNT).intValue(); i++) {

				int range = getProperty(SUMMON_RADIUS).intValue();

				// Try and find a nearby floor space
				BlockPos pos = BlockUtils.findNearbyFloorSpace(caster, range, range * 2);

				// If there was no floor around and the entity isn't a flying one, the spell fails.
				// As per the javadoc for findNearbyFloorSpace, there's no point trying the rest of the minions.
				if (pos == null)
					return false;

				EntityWolfMinion minion = new EntityWolfMinion(world);

				minion.setPosition(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
				//				minion.setCaster(caster);
//				minion.setTamedBy((EntityPlayer) caster);
				minion.setOwner(caster);
				// Modifier implementation
				// Attribute modifiers are pretty opaque, see https://minecraft.gamepedia.com/Attribute#Modifiers
				minion.setLifetime((int) (getProperty(MINION_LIFETIME).floatValue() * modifiers.get(WizardryItems.duration_upgrade)));
				IAttributeInstance attribute = minion.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
				attribute.applyModifier( // Apparently some things don't have an attack damage
						new AttributeModifier(POTENCY_ATTRIBUTE_MODIFIER, modifiers.get(SpellModifiers.POTENCY) - 1, EntityUtils.Operations.MULTIPLY_CUMULATIVE));
				// This is only used for artefacts, but it's a nice example of custom spell modifiers
				minion.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).applyModifier(
						new AttributeModifier(HEALTH_MODIFIER, modifiers.get(HEALTH_MODIFIER) - 1, EntityUtils.Operations.MULTIPLY_CUMULATIVE));
				minion.setHealth(minion.getMaxHealth()); // Need to set this because we may have just modified the value

				this.addMinionExtras(minion, pos, caster, modifiers, i);

				world.spawnEntity(minion);
			}
		}
		return true;
	}
}


