package com.windanesz.ancientspellcraft.entity.projectile;

import com.windanesz.ancientspellcraft.registry.AncientSpellcraftSpells;
import electroblob.wizardry.entity.projectile.EntityMagicArrow;
import electroblob.wizardry.registry.Spells;
import electroblob.wizardry.registry.WizardrySounds;
import electroblob.wizardry.spell.Spell;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EntityFlint extends EntityMagicArrow {

	/**
	 * Creates a new dart in the given world.
	 */
	public EntityFlint(World world) {
		super(world);
	}

	@Override
	public double getDamage() { return AncientSpellcraftSpells.flint_shard.getProperty(Spell.DAMAGE).doubleValue(); }

	@Override
	public boolean doGravity() { return true; }

	@Override
	public boolean doDeceleration() { return true; }

	@Override
	public void onEntityHit(EntityLivingBase entityHit) {
		// Adds a weakness effect to the target.
		entityHit.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, Spells.dart.getProperty(Spell.EFFECT_DURATION).intValue(),
				Spells.dart.getProperty(Spell.EFFECT_STRENGTH).intValue(), false, false));
		this.playSound(WizardrySounds.ENTITY_DART_HIT, 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
	}

	@Override
	public void onBlockHit(RayTraceResult hit) {
		this.playSound(WizardrySounds.ENTITY_DART_HIT_BLOCK, 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
	}

	@Override
	protected void entityInit() {}

	@Override
	public int getLifetime() {
		return -1;
	}
}
