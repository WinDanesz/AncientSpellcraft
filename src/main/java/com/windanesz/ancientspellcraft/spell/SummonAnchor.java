package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftItems;
import com.windanesz.ancientspellcraft.util.ASParticles;
import electroblob.wizardry.entity.living.ISummonedCreature;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

import java.util.List;

public class SummonAnchor extends Spell {

	private static String MAX_AFFECTED_ENTITIES = "max_affected_entities";

	public SummonAnchor() {

		super(AncientSpellcraft.MODID, "summon_anchor", SpellActions.SUMMON, false);
		addProperties(BLAST_RADIUS);
		addProperties(DURATION);
		addProperties(HEALTH);
		addProperties(MAX_AFFECTED_ENTITIES);
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

		List<EntityLivingBase> targets = EntityUtils.getEntitiesWithinRadius(radius, caster.posX, caster.posY, caster.posZ, world, EntityLivingBase.class);
		int entityCount = getProperty(MAX_AFFECTED_ENTITIES).intValue();

		for (EntityLivingBase target : targets) {

			if (!(target instanceof ISummonedCreature && ((ISummonedCreature) target).getOwnerId() == caster.getUniqueID())) {
				continue;
			}

			entityCount--;
			if (entityCount <= 0) {
				return false;
			}

			ISummonedCreature summon = (ISummonedCreature) target;
			summon.setLifetime(summon.getLifetime() + Math.round(duration));

			if (target.getHealth() < target.getMaxHealth() && target.getHealth() > 0) {
				target.heal(getProperty(HEALTH).floatValue() * modifiers.get(SpellModifiers.POTENCY));
			}
			foundSummon = true;

			if (world.isRemote) {
				// world.getTotalWorldTime() - ticksInUse generates a constant but unique seed each time the spell is cast
				ParticleBuilder.create(ASParticles.SOUL_CHAIN)
						.entity(caster)
						.time(60)
						.pos(0, caster.getEyeHeight() - 0.25, 0)
						.target(target)
						.seed(world.getTotalWorldTime() - ticksInUse)
						.spawn(world);

				ParticleBuilder.create(ParticleBuilder.Type.GUARDIAN_BEAM)
						.entity(caster)
						.time(60)
						.pos(0, caster.getEyeHeight() - 0.25, 0)
						.target(target)
						.clr(139, 8, 168)
						.spawn(world);

			}
		}

		if (world.isRemote)
			spawnSpellParticles(caster, caster.world, radius);

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