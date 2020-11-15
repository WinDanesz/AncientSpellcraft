package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftItems;
import electroblob.wizardry.constants.Constants;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.spell.SpellRay;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Author: WinDanesz
 * Based on {@link electroblob.wizardry.spell.Reversal} - Author: Electroblob
 */
public class Transference extends SpellRay {

	public static final String TRANSFERRED_EFFECTS = "transferred_effects";

	public Transference() {
		super(AncientSpellcraft.MODID, "transference", SpellActions.POINT, false);
		addProperties(TRANSFERRED_EFFECTS);
	}

	@Override
	public boolean canBeCastBy(TileEntityDispenser dispenser) {
		return false;
	}

	// based on Transference onEntityHit - author: Electroblob
	@Override
	protected boolean onEntityHit(World world, Entity target, Vec3d hit, @Nullable
			EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {

		// Naturally, this spell won't work unless it has a living caster and target
		if (caster != null && target instanceof EntityLivingBase) {

			List<PotionEffect> positivePotions = new ArrayList<>(caster.getActivePotionEffects());
			positivePotions.removeIf(p -> p.getPotion().isBadEffect());
			positivePotions.removeIf(p -> p.getDuration() > 100000);

			if (!world.isRemote) {

				if (positivePotions.isEmpty())
					return false; // Needs potion effects to reverse!

				// 1 effect for non-healing wands, 2 for apprentice healing wands, 3 for advanced and 4 for master
				int bonusEffects = (int) ((modifiers.get(SpellModifiers.POTENCY) - 1) / Constants.POTENCY_INCREASE_PER_TIER + 0.5f) - 1;
				int n = getProperty(TRANSFERRED_EFFECTS).intValue() + bonusEffects;

				if (n <= 1 )
					n = 1;
				// Chooses n random positive potion effects, where n is the potency level
				Collections.shuffle(positivePotions);
				positivePotions = positivePotions.subList(0, positivePotions.size() < n ? positivePotions.size() : n);

				positivePotions.forEach(p -> caster.removePotionEffect(p.getPotion()));
				positivePotions.forEach(((EntityLivingBase) target)::addPotionEffect);

			} else {
				ParticleBuilder.create(ParticleBuilder.Type.BUFF).entity(caster).clr(1, 1, 0.3f).spawn(world);
			}
		}

		return true;
	}

	@Override
	protected boolean onBlockHit(World world, BlockPos pos, EnumFacing side, Vec3d hit,
			@Nullable EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {
		return false;
	}

	@Override
	protected boolean onMiss(World world, @Nullable EntityLivingBase caster, Vec3d origin, Vec3d direction, int ticksInUse, SpellModifiers modifiers) {
		return true;
	}

	@Override
	protected void spawnParticle(World world, double x, double y, double z, double vx, double vy, double vz) {
		ParticleBuilder.create(ParticleBuilder.Type.BUFF).pos(x, y, z).clr(0.1f, 0, 0).spawn(world);
		ParticleBuilder.create(ParticleBuilder.Type.SPARKLE).pos(x, y, z).time(12 + world.rand.nextInt(8)).clr(0.9f, 0.9f, 0.85f).spawn(world);
	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == AncientSpellcraftItems.ancient_spellcraft_spell_book || item == AncientSpellcraftItems.ancient_spellcraft_scroll;
	}
}
