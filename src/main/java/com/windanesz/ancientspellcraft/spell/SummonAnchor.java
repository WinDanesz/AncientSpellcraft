package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftItems;
import com.windanesz.ancientspellcraft.util.ASParticles;
import electroblob.wizardry.entity.living.ISummonedCreature;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.SpellModifiers;
import electroblob.wizardry.util.WizardryUtilities;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

public class SummonAnchor extends Spell {
	public SummonAnchor() {
		super(AncientSpellcraft.MODID, "summon_anchor", EnumAction.NONE, false);
		addProperties(BLAST_RADIUS);
		addProperties(DURATION);
		addProperties(HEALTH);
		soundValues(1.0f, 1.2f, 0.2f);
	}

	@Override
	public boolean requiresPacket() {
		return false;
	}

	@Override
	public boolean cast(World world, EntityPlayer caster, EnumHand hand, int ticksInUse, SpellModifiers modifiers) {
		double radius = getProperty(BLAST_RADIUS).floatValue() * modifiers.get(WizardryItems.blast_upgrade);
		int duration = Math.round(getProperty(DURATION).floatValue() * modifiers.get(WizardryItems.duration_upgrade));
		boolean foundSummon = false;

		playSound(world, caster.posX, caster.posY, caster.posZ, 0, 0, modifiers);

		List<EntityLivingBase> targets = WizardryUtilities.getEntitiesWithinRadius(radius, caster.posX, caster.posY, caster.posZ, world);
		for (EntityLivingBase target : targets) {
			if (!(target instanceof ISummonedCreature && ((ISummonedCreature) target).getOwnerId() == caster.getUniqueID())) {
				continue;
			}
			ISummonedCreature summon = (ISummonedCreature) target;
			summon.setLifetime(summon.getLifetime() + Math.round(duration));

			if (target.getHealth() < target.getMaxHealth() && target.getHealth() > 0) {
				System.out.println("heal amount:" + getProperty(HEALTH).floatValue() * modifiers.get(SpellModifiers.POTENCY));
				target.heal(getProperty(HEALTH).floatValue() * modifiers.get(SpellModifiers.POTENCY));
			}
			foundSummon = true;

			/// test
			Vec3d origin = new Vec3d(caster.posX, caster.getEntityBoundingBox().minY + caster.getEyeHeight(), caster.posZ);
			Vec3d hookPosition = new Vec3d(target.posX, target.getEntityBoundingBox().minY + target.getEyeHeight(), target.posZ);
			Vec3d targetVec = new Vec3d(target.posX, target.getEntityBoundingBox().minY + target.height/2, target.posZ);
			Vec3d vec = targetVec.subtract(origin).normalize();
			float extensionSpeed = 5;

			// Extension
			if (world.isRemote) {
				// world.getTotalWorldTime() - ticksInUse generates a constant but unique seed each time the spell is cast
				ParticleBuilder.create(ASParticles.SOUL_CHAIN)
						.entity(caster)
						.time(30)
						.pos(0, caster.getEyeHeight() - 0.25, 0)
						.target(target)
						.seed(world.getTotalWorldTime() - ticksInUse)
						.spawn(world);

			}
			/// test
		}

		if (world.isRemote)
			spawnSpellParticles(caster, caster.world, radius);

		System.out.println("foundSummon: " + foundSummon);
		return foundSummon;
	}

	@SuppressWarnings("Duplicates")
	private void spawnSpellParticles(EntityPlayer caster, World world, double radius) {
		if (world.isRemote) {

			float particleCount = 10;
			final float r, g, b;
			r = 0;
			g = 0;
			b = 237;
			for (int i = 0; i < particleCount; i++) {
				double x = caster.posX + world.rand.nextDouble() * 2 - 1;
				double y = caster.getEntityBoundingBox().minY + caster.getEyeHeight() - 1 + world.rand.nextDouble();
				double z = caster.posZ + world.rand.nextDouble() * 2 - 1;

				ParticleBuilder.create(ParticleBuilder.Type.SPHERE)
						.pos(caster.posX, caster.getEntityBoundingBox().minY + 0.1, caster.posZ)
						.scale((float) radius * 0.8f)
						.clr(51, 0, 102)
						.spawn(world);
			}
		}
	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == AncientSpellcraftItems.ancient_spellcraft_spell_book || item == AncientSpellcraftItems.ancient_spellcraft_scroll;
	}

}