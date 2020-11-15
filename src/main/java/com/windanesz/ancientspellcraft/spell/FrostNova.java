package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftItems;
import electroblob.wizardry.entity.projectile.EntityIceShard;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.registry.Spells;
import electroblob.wizardry.registry.WizardryPotions;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.AllyDesignationSystem;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.MagicDamage;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.ParticleBuilder.Type;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class FrostNova extends Spell {
	private static final String SHARD_COUNT = "shard_count";
	private static final String FREEZE_RADIUS = "freeze_radius";

	public FrostNova() {

		super(AncientSpellcraft.MODID, "frost_nova", SpellActions.SUMMON, true);
		soundValues(1.0f, 1.2f, 0.2f);
		addProperties(SHARD_COUNT, FREEZE_RADIUS);
	}

	@Override
	public boolean canBeCastBy(TileEntityDispenser dispenser) {
		return false;
	}

	@Override
	public boolean canBeCastBy(EntityLiving npc, boolean override) {
		return false;
	}

	@Override
	public boolean cast(World world, EntityPlayer caster, EnumHand hand, int ticksInUse, SpellModifiers modifiers) {

		if (ticksInUse < 40) {
			if (world.isRemote)
				this.spawnParticles(world, caster, modifiers);
			return true;
		}

		if (ticksInUse > 40) {
			return false;
		}


		if (!world.isRemote) {

			caster.hurtResistantTime = 10;

			for (int i = 0; i < getProperty(SHARD_COUNT).intValue(); i++) {
				double dx = world.rand.nextDouble() - 0.5;
				//				double dy = world.rand.nextDouble() - 0.5;
				double dz = world.rand.nextDouble() - 0.5;
				EntityIceShard iceshard = new EntityIceShard(world);
				iceshard.setPosition(caster.posX + dx, caster.posY + 1, caster.posZ + dz);
				iceshard.motionX = dx * 2.5;
				iceshard.motionY = 0.2;
				iceshard.motionZ = dz * 2.5;
				iceshard.setCaster(caster);
				world.spawnEntity(iceshard);
			}

			for (EntityLivingBase entity : EntityUtils.getEntitiesWithinRadius(getProperty(FREEZE_RADIUS).intValue(), caster.posX, caster.posY, caster.posZ, caster.world, EntityLivingBase.class)) {
				if (entity == caster) {
					continue;
				}
				if (!AllyDesignationSystem.isAllied(caster, entity) && !(MagicDamage.isEntityImmune(MagicDamage.DamageType.FROST, entity))) {
					entity.addPotionEffect(new PotionEffect(WizardryPotions.frost,
							Spells.ice_shard.getProperty(Spell.EFFECT_DURATION).intValue(),
							Spells.ice_shard.getProperty(Spell.EFFECT_STRENGTH).intValue()));
				}
			}

			if (caster.isHandActive())
				caster.stopActiveHand();
		}
		this.playSound(world, caster, ticksInUse, -1, modifiers);
		return true;
	}

	protected void spawnParticles(World world, EntityLivingBase caster, SpellModifiers modifiers) {

		for (int i = 0; i < 10; i++) {
			double dx = (world.rand.nextDouble() * (world.rand.nextBoolean() ? 1 : -1)) * 0.1;
			double dy = (world.rand.nextDouble() * (world.rand.nextBoolean() ? 1 : -1)) * 0.1;
			double dz = (world.rand.nextDouble() * (world.rand.nextBoolean() ? 1 : -1)) * 0.1;

			ParticleBuilder.create(Type.ICE)
					.entity(caster)
					.pos(0, caster.height / 2, 0)
					.vel(dx * 0.3, dy * 0.3, dz * 0.3)
					.spawn(world);
		}


	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == AncientSpellcraftItems.ancient_spellcraft_spell_book || item == AncientSpellcraftItems.ancient_spellcraft_scroll;
	}
}
