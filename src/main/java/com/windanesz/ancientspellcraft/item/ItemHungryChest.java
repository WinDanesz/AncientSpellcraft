package com.windanesz.ancientspellcraft.item;

import electroblob.wizardry.constants.Element;
import electroblob.wizardry.constants.Tier;
import electroblob.wizardry.item.ItemSpellBook;
import electroblob.wizardry.registry.Spells;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.spell.Spell;
import net.minecraft.entity.Entity;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ItemHungryChest extends AbstractItemArtefactWithSlots {

	private static final float DROP_OFFSET = 0.5f;
	private static final int MAGIC_ELEMENT_COUNT = 7;
	private static final int MAGIC_ELEMENT_START = 1;

	public ItemHungryChest(EnumRarity rarity, Type type) {
		super(rarity, type, 1, 1, true);
	}

	public static ItemStack getScroll(ItemStack stack) {
		return AbstractItemArtefactWithSlots.getItemForSlot(stack, 0);
	}

	@Override
	public boolean isItemValid(Item item) {
		return item instanceof ItemSpellBook;
	}

	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected);
		
		if (worldIn.isRemote) {
			return;
		}

		ItemStack bookStack = getScroll(stack);
		if (bookStack.isEmpty() || !(bookStack.getItem() instanceof ItemSpellBook)) {
			return;
		}

		// Remove the book from the chest
		AbstractItemArtefactWithSlots.setItemForSlot(stack, 0, ItemStack.EMPTY);

		Spell spell = Spell.byMetadata(bookStack.getItemDamage());

		// Special case: magic missile is returned intact
		if (spell == Spells.magic_missile) {
			entityIn.entityDropItem(bookStack, DROP_OFFSET);
			return;
		}

		// Always drop a ruined spell book for non-magic missile spells
		ItemStack ruinedBook = new ItemStack(WizardryItems.ruined_spell_book);
		entityIn.entityDropItem(ruinedBook, DROP_OFFSET);

		Element element = spell.getElement();
		int dustAmount = getDustAmountForTier(spell.getTier(), worldIn);

		if (element != Element.MAGIC) {
			dropNonMagicSpellRewards(entityIn, dustAmount, element.ordinal());
		} else {
			dropMagicSpellRewards(entityIn, dustAmount, worldIn);
		}
	}

	/**
	 * Determines the amount of spectral dust to drop based on spell tier
	 */
	private int getDustAmountForTier(Tier tier, World world) {
		switch (tier) {
			case NOVICE:
				return 1 + world.rand.nextInt(3); // 1-3
			case APPRENTICE:
				return 2 + world.rand.nextInt(2); // 2-3
			case ADVANCED:
				return 2 + world.rand.nextInt(3); // 2-4
			case MASTER:
				return 4;
			default:
				return 1;
		}
	}

	/**
	 * Drops rewards for non-magic element spells
	 */
	private void dropNonMagicSpellRewards(Entity entity, int dustAmount, int elementMeta) {
		// Drop spectral dust with meta based on element
		ItemStack dustStack = new ItemStack(WizardryItems.spectral_dust, dustAmount, elementMeta);
		entity.entityDropItem(dustStack, DROP_OFFSET);
	}

	/**
	 * Drops rewards for magic element spells
	 */
	private void dropMagicSpellRewards(Entity entity, int dustAmount, World world) {
		// Create list of magic element metas (1-7) and shuffle them
		List<Integer> metas = new ArrayList<>();
		for (int m = MAGIC_ELEMENT_START; m <= MAGIC_ELEMENT_COUNT; m++) {
			metas.add(m);
		}
		Collections.shuffle(metas, world.rand);

		// Drop spectral dust, each with a unique random meta
		for (int i = 0; i < dustAmount && i < metas.size(); i++) {
			int randomMeta = metas.get(i);
			ItemStack dustStack = new ItemStack(WizardryItems.spectral_dust, 1, randomMeta);
			entity.entityDropItem(dustStack, DROP_OFFSET);
		}
	}
}
