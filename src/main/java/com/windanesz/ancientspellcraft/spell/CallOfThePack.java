package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.entity.living.EntityWolfMinion;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftItems;
import electroblob.wizardry.spell.SpellMinion;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;

public class CallOfThePack extends SpellMinion<EntityWolfMinion> {

	public CallOfThePack() {
		super(AncientSpellcraft.MODID, "call_of_the_pack", EntityWolfMinion::new);
		this.soundValues(7, 0.9f, 1);
	}

	@Override
	protected void addMinionExtras(EntityWolfMinion minion, BlockPos pos, @Nullable EntityLivingBase caster, SpellModifiers modifiers, int alreadySpawned) {
		minion.setTamed(true);
		minion.setOwnerId(caster.getUniqueID());
		minion.setTamedBy((EntityPlayer) caster);
		System.out.println("minion ownerID:" + minion.getOwnerId());
		super.addMinionExtras(minion, pos, caster, modifiers, alreadySpawned);
	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == AncientSpellcraftItems.ancient_spellcraft_spell_book || item == AncientSpellcraftItems.ancient_spellcraft_scroll;
	}
}


