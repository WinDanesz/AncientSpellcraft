package com.windanesz.ancientspellcraft.item;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import baubles.api.cap.BaublesCapabilities;
import com.windanesz.ancientspellcraft.registry.ASItems;
import com.windanesz.ancientspellcraft.registry.ASTabs;
import electroblob.wizardry.constants.Element;
import electroblob.wizardry.constants.Tier;
import electroblob.wizardry.event.ImbuementActivateEvent;
import electroblob.wizardry.item.ItemArtefact;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.SpellProperties;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

@Mod.EventBusSubscriber
public class ItemASArtefact extends ItemArtefact {

	public ItemASArtefact(EnumRarity rarity, Type type) {
		super(rarity, type);
		setCreativeTab(ASTabs.ANCIENTSPELLCRAFT_GEAR);
	}

	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
		return new ASArtefactBaubleProvider(this);
	}

	/**
	 * Custom bauble provider that properly implements all IBauble methods
	 * This allows subclasses to override onEquipped, onWornTick, onUnequipped, etc.
	 */
	protected static class ASArtefactBaubleProvider implements ICapabilityProvider, IBauble {
		
		private final ItemASArtefact item;
		
		public ASArtefactBaubleProvider(ItemASArtefact item) {
			this.item = item;
		}

		@Override
		public BaubleType getBaubleType(ItemStack itemstack) {
			// Map ItemArtefact.Type to BaubleType
			switch (item.getType()) {
				case RING:
					return BaubleType.RING;
				case AMULET:
					return BaubleType.AMULET;
				case CHARM:
					return BaubleType.CHARM;
				case BELT:
					return BaubleType.BELT;
				case HEAD:
					return BaubleType.HEAD;
				case BODY:
					return BaubleType.BODY;

				default:
					return BaubleType.CHARM;
			}
		}

		@Override
		public void onEquipped(ItemStack itemstack, EntityLivingBase player) {
			if (item instanceof IBauble) {
				((IBauble) item).onEquipped(itemstack, player);
			}
		}

		@Override
		public void onWornTick(ItemStack itemstack, EntityLivingBase player) {
			if (item instanceof IBauble) {
				((IBauble) item).onWornTick(itemstack, player);
			}
		}

		@Override
		public void onUnequipped(ItemStack itemstack, EntityLivingBase player) {
			if (item instanceof IBauble) {
				((IBauble) item).onUnequipped(itemstack, player);
			}
		}

		@Override
		public boolean canEquip(ItemStack itemstack, EntityLivingBase player) {
			if (item instanceof IBauble) {
				return ((IBauble) item).canEquip(itemstack, player);
			}
			return true;
		}

		@Override
		public boolean canUnequip(ItemStack itemstack, EntityLivingBase player) {
			if (item instanceof IBauble) {
				return ((IBauble) item).canUnequip(itemstack, player);
			}
			return true;
		}

		@Override
		public boolean willAutoSync(ItemStack itemstack, EntityLivingBase player) {
			if (item instanceof IBauble) {
				return ((IBauble) item).willAutoSync(itemstack, player);
			}
			return false;
		}

		@Override
		public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
			return capability == BaublesCapabilities.CAPABILITY_ITEM_BAUBLE;
		}

		@Override
		public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
			return capability == BaublesCapabilities.CAPABILITY_ITEM_BAUBLE ? (T) this : null;
		}
	}

	@SubscribeEvent
	public static void onImbuement(ImbuementActivateEvent event) {
		if (event.lastUser != null && ItemArtefact.isArtefactActive(event.lastUser, ASItems.amulet_imbued_marble)) {
			List<Element> elements = Arrays.asList(event.receptacleElements);

			Element firstElement = elements.get(0);
			if (elements.stream().anyMatch(e -> e != firstElement)) {
				// at least one dust was not matching..
				return;
			}
			// otherwise, all of them were the same, 80% chance to imbue this to the element..
			if (itemRand.nextFloat() < 0.8) {
				for (int i = 0; i < 10; i++) {
					List<Spell> randomSpell = Spell.getSpells(new Spell.TierElementFilter(Tier.ADVANCED, firstElement, SpellProperties.Context.BOOK));
					Spell spell = randomSpell.get(itemRand.nextInt(randomSpell.size() - 1));
					if (spell.applicableForItem(WizardryItems.spell_book)) {
						event.result = new ItemStack(WizardryItems.spell_book, 1, spell.metadata());
						break;
					} else if (spell.applicableForItem(ASItems.ancient_spellcraft_spell_book)) {
						event.result = new ItemStack(ASItems.ancient_spellcraft_spell_book, 1, spell.metadata());
						break;
					}
				}
			}
		}
	}
}
