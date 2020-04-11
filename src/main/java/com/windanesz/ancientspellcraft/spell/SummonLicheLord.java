package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.registry.AncientSpellcraftItems;
import electroblob.wizardry.entity.living.EntitySkeletonMinion;
import electroblob.wizardry.entity.living.EntityStrayMinion;
import electroblob.wizardry.item.ItemArtefact;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.spell.SpellMinion;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.function.Function;

public class SummonLicheLord extends SpellMinion<EntitySkeletonMinion> {

	public SummonLicheLord(String modID, String name, Function<World, EntitySkeletonMinion> minionFactory) {
		super(modID, name, minionFactory);
		this.soundValues(7, 0.6f, 0);
	}

	@Override
	protected EntitySkeletonMinion createMinion(World world, EntityLivingBase caster, SpellModifiers modifiers) {
		if (caster instanceof EntityPlayer && ItemArtefact.isArtefactActive((EntityPlayer) caster, WizardryItems.charm_minion_variants)) {
			return new EntityStrayMinion(world);
		} else {
			return super.createMinion(world, caster, modifiers);
		}
	}

	@Override
	protected void addMinionExtras(EntitySkeletonMinion minion, BlockPos pos, EntityLivingBase caster, SpellModifiers modifiers, int alreadySpawned) {
		minion.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
		minion.setDropChance(EntityEquipmentSlot.MAINHAND, 0.0f);
	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == AncientSpellcraftItems.ancient_spellcraft_spell_book || item == AncientSpellcraftItems.ancient_spellcraft_scroll;
	}
}
