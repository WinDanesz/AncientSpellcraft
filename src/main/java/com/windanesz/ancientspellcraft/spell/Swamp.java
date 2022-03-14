package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.block.ITemporaryBlock;
import com.windanesz.ancientspellcraft.entity.construct.EntitySpellTicker;
import com.windanesz.ancientspellcraft.registry.ASBlocks;
import com.windanesz.ancientspellcraft.registry.ASItems;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.spell.SpellConstruct;
import electroblob.wizardry.util.BlockUtils;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class Swamp extends SpellConstruct<EntitySpellTicker> implements ISpellTickerConstruct {

	public Swamp() {
		super(AncientSpellcraft.MODID, "swamp", SpellActions.SUMMON, EntitySpellTicker::new, false);
		addProperties(EFFECT_RADIUS);
	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == ASItems.ancient_spellcraft_spell_book || item == ASItems.ancient_spellcraft_scroll;
	}

	@Override
	protected void addConstructExtras(EntitySpellTicker construct, EnumFacing side, @Nullable EntityLivingBase caster, SpellModifiers modifiers) {
		super.addConstructExtras(construct, side, caster, modifiers);

		// so that mushrooms don't immediately disappear when the forest spell itself ends
		int blockDuration = (int) (getProperty(DURATION).floatValue() * modifiers.get(WizardryItems.duration_upgrade));
		construct.setDuration(blockDuration);
		construct.setModifiers(modifiers);
		construct.setSpell(this);
	}

	@Override
	public void onUpdate(World world, EntitySpellTicker entity) {

		BlockPos pos;
		int radius = (int) (getProperty(Spell.EFFECT_RADIUS).doubleValue());
		int blockDuration = (int) (2 * getProperty(DURATION).floatValue() * entity.getModifiers().get(WizardryItems.duration_upgrade));

		pos = BlockUtils.findNearbyFloorSpace(world, entity.getPosition(), radius, 10);

		boolean shouldPlace = true;
		boolean placed = false;

		// null if there are no possible positions nearby at all, retries are handled by findNearbyFloorSpace
		if (pos == null)
			return;

		// TODO: FIXME - for some reason this was always returning true
		//if (entity.getCaster() != null && entity.getCaster().getPosition().getY() == pos.getY())
		//	shouldPlace = false;

		if (shouldPlace) {
			ITemporaryBlock.placeTemporaryBlock(entity.getCaster(), world, ASBlocks.QUICKSAND, pos.down(), blockDuration);
			placed = true;
		}

		BlockPos prevPos = pos;
		if (placed) {
			for (int i = 0; i < world.rand.nextInt(4); i++) {
				pos = BlockUtils.findNearbyFloorSpace(world, prevPos, 1, 3);

				if (pos == null || entity.getCaster() != null && entity.getCaster().getPosition().getY() == pos.getY())
					continue;

				ITemporaryBlock.placeTemporaryBlock(entity.getCaster(), world, ASBlocks.QUICKSAND, pos.down(), blockDuration);
				prevPos = pos;
			}
		}
	}

	@Override
	public IBlockState getBlock(World world, EntitySpellTicker entityMushroomForest) {
		return null;
	}
}
