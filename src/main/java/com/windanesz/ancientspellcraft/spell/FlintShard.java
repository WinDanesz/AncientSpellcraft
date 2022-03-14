package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.entity.projectile.EntityFlint;
import com.windanesz.ancientspellcraft.registry.ASItems;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.spell.SpellArrow;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class FlintShard extends SpellArrow<EntityFlint> {
	public FlintShard() {
		super(AncientSpellcraft.MODID, "flint_shard", EntityFlint::new);
		this.addProperties(Spell.DAMAGE);
	}


	@Override
	public boolean cast(World world, EntityPlayer caster, EnumHand hand, int ticksInUse, SpellModifiers modifiers){

		// reduce the cost to by 80% to a minimum of 1 mana when the player stands on gravel
		if (caster.onGround && world.getBlockState(caster.getPosition().down()).getBlock() == Blocks.GRAVEL) {
			float cost = modifiers.get(SpellModifiers.COST);
			float newCost = Math.max(cost * 0.2f, 0.1f);
			modifiers.set(SpellModifiers.COST, newCost, false);
		}
		super.cast(world, caster, hand, ticksInUse, modifiers);

		return true;
	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == ASItems.ancient_spellcraft_spell_book || item == ASItems.ancient_spellcraft_scroll;
	}
}