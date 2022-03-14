package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.block.BlockMagicMushroom;
import com.windanesz.ancientspellcraft.entity.construct.EntitySpellTicker;
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

public class MushroomForest extends SpellConstruct<EntitySpellTicker> implements ISpellTickerConstruct {

	public MushroomForest() {
		super(AncientSpellcraft.MODID, "mushroom_forest", SpellActions.SUMMON, EntitySpellTicker::new, false);
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
		int individualMushroomDuration = (int) (2 * getProperty(DURATION).floatValue() * modifiers.get(WizardryItems.duration_upgrade));
		construct.setDuration(individualMushroomDuration);
		construct.setModifiers(modifiers);
		construct.setSpell(this);
	}

	@Override
	public void onUpdate(World world, EntitySpellTicker entity) {

		// findNearbyFloorSpace handles the retrying logic and only return false if placing this block is impossible, so there is no point wrapping this
		// into multiple attempts.. however the block on top of it might not be suitable, hence the retries
		BlockMagicMushroom mushroom = BlockMagicMushroom.getRandomMushroom(0.05f, 0.03f);
		BlockPos pos;
		int radius = (int) (getProperty(Spell.EFFECT_RADIUS).doubleValue());

		int i = 10;
		do {
			pos = BlockUtils.findNearbyFloorSpace(world, entity.getPosition(), radius, 10);

			if (pos == null)
				break;

			if (BlockMagicMushroom.tryPlaceMushroom(world, pos, entity.getCaster(), mushroom, entity.getDuration(), entity.getModifiers())) {
				break;
			}

			i--;
		} while (i > 0);

	}

	@Override
	public IBlockState getBlock(World world, EntitySpellTicker entityMushroomForest) {
		return null;
	}
}
