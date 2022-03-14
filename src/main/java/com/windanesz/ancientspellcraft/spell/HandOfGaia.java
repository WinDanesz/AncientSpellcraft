package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.registry.ASItems;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.AllyDesignationSystem;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

import java.util.List;

public class HandOfGaia extends Spell {

	private static String MAX_AFFECTED_ENTITIES = "max_affected_entities";

	public HandOfGaia() {

		super(AncientSpellcraft.MODID, "hand_of_gaia", SpellActions.SUMMON, true);
		addProperties(EFFECT_RADIUS);
		addProperties(MAX_AFFECTED_ENTITIES);
		soundValues(1.0f, 1.1f, 0.1f);
	}

	@Override
	public boolean requiresPacket() {
		return false;
	}

	@Override
	public boolean cast(World world, EntityPlayer caster, EnumHand hand, int ticksInUse, SpellModifiers modifiers) {
		double radius = getProperty(EFFECT_RADIUS).floatValue() * modifiers.get(WizardryItems.blast_upgrade);

		playSound(world, caster.posX, caster.posY, caster.posZ, ticksInUse, 0, modifiers);

		List<EntityLivingBase> targets = EntityUtils.getEntitiesWithinRadius(radius, caster.posX, caster.posY, caster.posZ, world, EntityLivingBase.class);
		int entityCount = getProperty(MAX_AFFECTED_ENTITIES).intValue();

		for (EntityLivingBase target : targets) {
			if (entityCount <= 0) {
				return true;
			}

			if (AllyDesignationSystem.isAllied(caster, target)) {
				entityCount--;

				target.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 30, 0));
			}

			if (world.isRemote) {

				if (world.getTotalWorldTime() % 2 == 0) {

				ParticleBuilder.create(ParticleBuilder.Type.FLASH)
						.entity(target)
						.time(20)
						.pos(0, caster.getEyeHeight() - 0.25, 0)
//						.target(target)
						.clr(201, 90, 168)
						.spawn(world);
				}
//
				ParticleBuilder.create(ParticleBuilder.Type.VINE)
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
		return true;

	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == ASItems.ancient_spellcraft_spell_book || item == ASItems.ancient_spellcraft_scroll;
	}

}