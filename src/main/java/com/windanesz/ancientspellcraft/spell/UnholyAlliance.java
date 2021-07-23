package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.entity.living.EntitySkeletonMageMinion;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftItems;
import electroblob.wizardry.constants.Element;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.spell.SpellMinion;
import electroblob.wizardry.util.BlockUtils;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class UnholyAlliance extends SpellMinion<EntitySkeletonMageMinion> {
	private static final String POTENCY_ATTRIBUTE_MODIFIER = "potency";

	public UnholyAlliance(String modID, String name, Function<World, EntitySkeletonMageMinion> minionFactory) {
		super(modID, name, minionFactory);
		this.soundValues(7, 1f, 0);
	}

	// cast by dispenser
	public boolean cast(World world, double x, double y, double z, EnumFacing direction, int ticksInUse, int duration, SpellModifiers modifiers) {
		return false;
	}

	@Override
	protected boolean spawnMinions(World world, EntityLivingBase caster, SpellModifiers modifiers) {
		if (!world.isRemote) {
			int i = 0;

			List<BlockPos> locations = new ArrayList<BlockPos>();
			locations.add(caster.getPosition().offset(EnumFacing.NORTH, 2));
			locations.add(caster.getPosition().offset(EnumFacing.SOUTH, 2));
			locations.add(caster.getPosition().offset(EnumFacing.WEST, 2));
			locations.add(caster.getPosition().offset(EnumFacing.EAST, 2));

			locations.add(caster.getPosition().offset(EnumFacing.NORTH, 2).offset(EnumFacing.EAST, 2));
			locations.add(caster.getPosition().offset(EnumFacing.NORTH, 2).offset(EnumFacing.WEST, 2));
			locations.add(caster.getPosition().offset(EnumFacing.SOUTH, 2).offset(EnumFacing.WEST, 2));

			for (Element element : Element.values()) {
				//skip MAGIC element
				if (element == Element.MAGIC)
					continue;
				int range = getProperty(SUMMON_RADIUS).intValue();

				BlockPos pos;
				if (world.isAirBlock(locations.get(i)) && world.isAirBlock(locations.get(i).up())) {
					pos = locations.get(i);
				} else {
					pos = BlockUtils.findNearbyFloorSpace(caster, range, range * 2);
				}
				i++;

				// If there was no floor around and the entity isn't a flying one, the spell fails.
				// As per the javadoc for findNearbyFloorSpace, there's no point trying the rest of the minions.
				if (pos == null)
					return false;

				EntitySkeletonMageMinion minion = createMinion(world, caster, modifiers, element);

				minion.setPosition(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
				minion.setCaster(caster);
				// Modifier implementation
				// Attribute modifiers are pretty opaque, see https://minecraft.gamepedia.com/Attribute#Modifiers
				minion.setLifetime((int) (getProperty(MINION_LIFETIME).floatValue() * modifiers.get(WizardryItems.duration_upgrade)));
				IAttributeInstance attribute = minion.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
				if (attribute != null)
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

	private EntitySkeletonMageMinion createMinion(World world, EntityLivingBase caster, SpellModifiers modifiers, Element element) {
		return new EntitySkeletonMageMinion(world, element);
	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == AncientSpellcraftItems.ancient_spellcraft_spell_book || item == AncientSpellcraftItems.ancient_spellcraft_scroll;
	}
}
