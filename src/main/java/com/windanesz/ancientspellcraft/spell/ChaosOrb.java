package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.entity.EntityChaosOrb;
import com.windanesz.ancientspellcraft.registry.ASItems;
import electroblob.wizardry.constants.Constants;
import electroblob.wizardry.entity.projectile.EntityMagicProjectile;
import electroblob.wizardry.item.ItemWizardArmour;
import electroblob.wizardry.spell.SpellProjectile;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.function.Function;

public class ChaosOrb<T extends EntityMagicProjectile> extends SpellProjectile<T> implements IClassSpell {

	public static final String MAX_SPLIT_GENERATIONS = "max_split_generations";

	public ChaosOrb(String modID, String name, Function<World, T> projectileFactory) {
		super(modID, name, projectileFactory);
		soundValues(0.8f, 0.9f, 0.2f);
		addProperties(DAMAGE, MAX_SPLIT_GENERATIONS);
	}

	@Override
	protected void addProjectileExtras(T projectile, @Nullable EntityLivingBase caster, SpellModifiers modifiers) {
		super.addProjectileExtras(projectile, caster, modifiers);
		int gen = Math.min(getProperty(MAX_SPLIT_GENERATIONS).intValue(), (int) ((modifiers.get(SpellModifiers.POTENCY) - 1) / Constants.POTENCY_INCREASE_PER_TIER + 0.01f) - 1);
		((EntityChaosOrb) projectile).setGeneration(gen);
		((EntityChaosOrb) projectile).setElement(getElementOrMagicElement(caster));

	}

	@Override
	public ItemWizardArmour.ArmourClass getArmourClass() {
		return ItemWizardArmour.ArmourClass.WARLOCK;
	}

	public boolean applicableForItem(Item item) {
		return item == ASItems.forbidden_tome;
	}


	@Override
	public boolean canBeCastBy(TileEntityDispenser dispenser) {
		return false;
	}

	@Override
	public boolean canBeCastBy(EntityLiving npc, boolean override) {
		return canBeCastByClassNPC(npc);
	}
}
