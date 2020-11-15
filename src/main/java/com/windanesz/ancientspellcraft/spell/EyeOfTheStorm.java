package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftItems;
import electroblob.wizardry.Wizardry;
import electroblob.wizardry.entity.projectile.EntityLightningArrow;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.AllyDesignationSystem;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class EyeOfTheStorm extends Spell {

	public EyeOfTheStorm() {
		super(AncientSpellcraft.MODID, "eye_of_the_storm", SpellActions.SUMMON, true);
	}

	@Override
	public boolean cast(World world, EntityPlayer caster, EnumHand hand, int ticksInUse, SpellModifiers modifiers) {

		if (ticksInUse > 1 && ticksInUse % 60 == 0) {
			if (!world.isRemote) {

				for (EntityLivingBase entity : EntityUtils.getEntitiesWithinRadius(15, caster.posX, caster.posY, caster.posZ, world, EntityLivingBase.class)) {
					if (!AllyDesignationSystem.isAllied(caster, entity)) {

						// Release bolts
						for (int i = 0; i < 3; i++) {
							EntityLightningArrow iceshard = new EntityLightningArrow(world);
							iceshard.aim(caster, entity, 0.6F, 0F);
							iceshard.setCaster(caster);
							iceshard.damageMultiplier = modifiers.get(DAMAGE);
							world.spawnEntity(iceshard);
						}
					}
				}
			}
			return true;
		} else {

			if (!Wizardry.settings.replaceVanillaFallDamage) {
				caster.fallDistance = 0;
			}

			if (!caster.isSneaking()) {
				caster.motionY = caster.motionY < 0.01f ? caster.motionY + 0.1f : caster.motionY;
			} else {
				caster.motionY = caster.motionY < 0.00f ? 0 : caster.motionY;
			}

			int radius = 2;
			double speed = 0.02 * world.rand.nextDouble();

			if (world.isRemote) {
				for (int i = 0; i < Math.max(ticksInUse / 2, 30); i++) {

					ParticleBuilder.create(ParticleBuilder.Type.SPARK).pos(
							caster.posX + ((world.rand.nextDouble() / 5) * (ticksInUse / 10F) * (world.rand.nextBoolean() ? -1 : 1)),
							caster.posY + +(0.3 + (world.rand.nextDouble() / 5) * (ticksInUse / 10F) * (world.rand.nextBoolean() ? -1 : 1)),
							caster.posZ + (world.rand.nextDouble() / 5) * (ticksInUse / 10F) * (world.rand.nextBoolean() ? -1 : 1)).time(4).spawn(world);

					ParticleBuilder.create(ParticleBuilder.Type.FLASH).clr(140, 181, 207).pos(
							caster.posX + ((world.rand.nextDouble() / 5) * (ticksInUse / 10F) * (world.rand.nextBoolean() ? -1 : 1)),
							caster.posY,
							caster.posZ + (world.rand.nextDouble() / 5) * (ticksInUse / 10F) * (world.rand.nextBoolean() ? -1 : 1)).time(4).spawn(world);

					if (world.rand.nextBoolean()) {
						ParticleBuilder.create(ParticleBuilder.Type.FLASH).clr(140, 181, 207).spin(world.rand.nextDouble() * (radius - 0.5) + 0.5, speed).pos(
								caster.posX + ((world.rand.nextDouble() / 5) * (ticksInUse / 10F) * (world.rand.nextBoolean() ? -1 : 1)),
								caster.posY,
								caster.posZ + (world.rand.nextDouble() / 5) * (ticksInUse / 10F) * (world.rand.nextBoolean() ? -1 : 1)).time(10).spawn(world);

					} else {
						ParticleBuilder.create(ParticleBuilder.Type.FLASH).clr(39, 50, 77).spin(world.rand.nextDouble() * (radius - 0.5) + 0.5, speed).pos(
								caster.posX + ((world.rand.nextDouble() / 5) * (ticksInUse / 10F) * (world.rand.nextBoolean() ? -1 : 1)),
								caster.posY,
								caster.posZ + (world.rand.nextDouble() / 5) * (ticksInUse / 10F) * (world.rand.nextBoolean() ? -1 : 1)).time(10).spawn(world);

					}

				}
			}
		}
		if (ticksInUse > 1 && ticksInUse % 61 == 0) {
			return false;
		}
		return true;
		//		return true;
	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == AncientSpellcraftItems.ancient_spellcraft_spell_book || item == AncientSpellcraftItems.ancient_spellcraft_scroll;
	}

}
