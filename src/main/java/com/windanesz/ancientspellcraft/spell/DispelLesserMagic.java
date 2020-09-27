package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import electroblob.wizardry.entity.projectile.EntityMagicFireball;
import electroblob.wizardry.spell.SpellProjectile;

import java.util.function.Function;

//		registry.register(new SpellProjectile<>("fireball", EntityMagicFireball::new).addProperties(Spell.DAMAGE, Spell.BURN_DURATION));//new Fireball());
public class DispelLesserMagic extends SpellProjectile<EntityMagicFireball> {

	public DispelLesserMagic(String modID, String name, Function projectileFactory) {
		super(AncientSpellcraft.MODID, "dispel_lesser_magic", projectileFactory);
	}


}
