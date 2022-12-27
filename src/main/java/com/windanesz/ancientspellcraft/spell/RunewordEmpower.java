package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.item.WizardClassWeaponHelper;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

import java.util.Set;

public class RunewordEmpower extends Runeword {

	protected Set<Potion> potionSet;
	/** The RGB colour values of the particles spawned when this spell is cast. */
	protected final float r, g, b;

	/** The number of sparkle particles spawned when this spell is cast. Defaults to 10. */
	protected float particleCount = 10;

	public RunewordEmpower() {
		super("runeword_empower", SpellActions.POINT_UP, false);
		r = 255;
		g = 255;
		b = 255;
	}

	@Override
	public boolean cast(World world, EntityPlayer caster, EnumHand hand, int ticksInUse, SpellModifiers modifiers) {
		if (holdsSword(caster, hand)) {
			if (!world.isRemote) {
				WizardClassWeaponHelper.addChargeProgress(caster.getHeldItem(hand), 100);
			}
			if(world.isRemote) this.spawnParticles(world, caster, modifiers);
			return true;
		}
		return false;
	}

	/** Spawns buff particles around the caster. Override to add a custom particle effect. Only called client-side. */
	protected void spawnParticles(World world, EntityLivingBase caster, SpellModifiers modifiers){

		for(int i = 0; i < particleCount; i++){
			double x = caster.posX + world.rand.nextDouble() * 2 - 1;
			double y = caster.posY + caster.getEyeHeight() - 0.5 + world.rand.nextDouble();
			double z = caster.posZ + world.rand.nextDouble() * 2 - 1;
			ParticleBuilder.create(ParticleBuilder.Type.SPARKLE).pos(x, y, z).vel(0, 0.1, 0).clr(r, g, b).spawn(world);
		}

		ParticleBuilder.create(ParticleBuilder.Type.BUFF).entity(caster).clr(r, g, b).spawn(world);
	}
}
