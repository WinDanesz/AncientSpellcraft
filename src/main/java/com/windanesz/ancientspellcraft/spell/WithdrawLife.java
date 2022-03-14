package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftItems;
import electroblob.wizardry.entity.living.ISummonedCreature;
import electroblob.wizardry.item.ItemArtefact;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import static com.windanesz.ancientspellcraft.spell.PrismaticSpray.Y_OFFSET;

public class WithdrawLife extends Spell {

	public static final String PERCENT_PER_MINION = "percent_per_minion";

	public WithdrawLife() {
		super(AncientSpellcraft.MODID, "withdraw_life", SpellActions.POINT_UP, false);
		addProperties(EFFECT_RADIUS, PERCENT_PER_MINION);
	}

	@Override
	public boolean cast(World world, EntityPlayer caster, EnumHand hand, int ticksInUse, SpellModifiers modifiers) {
		float percentPerMinion = getProperty(PERCENT_PER_MINION).floatValue();

		int minions = 0;
		for (EntityLivingBase entity : EntityUtils.getEntitiesWithinRadius(getProperty(EFFECT_RADIUS).floatValue(), caster.posX, caster.posY, caster.posZ, world, EntityLivingBase.class)) {
			if (entity == caster) { continue; }

			if (entity instanceof ISummonedCreature && ((ISummonedCreature) entity).getCaster() != null && ((ISummonedCreature) entity).getCaster() == caster) {
				minions++;
				float newHP = entity.getHealth() - (entity.getMaxHealth() * (percentPerMinion * 1.5f));
				if (!world.isRemote) {
					if (newHP <= 0) {
						entity.setDead();
					} else {
						entity.setHealth(newHP);
					}
				}
			} else { continue; }

			if (world.isRemote) {
				Vec3d origin = new Vec3d(entity.posX, entity.posY + entity.getEyeHeight() - Y_OFFSET, entity.posZ);

				ParticleBuilder.create(ParticleBuilder.Type.LIGHTNING).entity(entity).time(10).fade(0).clr(189, 0, 19)
						.pos(caster != null ? origin.subtract(entity.getPositionVector()) : origin).target(caster).spawn(world);
				ParticleBuilder.create(ParticleBuilder.Type.BUFF).entity(caster).clr(189, 0, 19).spawn(world);
			}
			if (minions > 4) { break; }
		}

		if (minions > 0) {
			if (!world.isRemote) {
				float heal = (caster.getMaxHealth() * percentPerMinion) * minions;
				float excess = (caster.getHealth() + heal) - caster.getMaxHealth();
				if (excess > 0 && ItemArtefact.isArtefactActive(caster, AncientSpellcraftItems.ring_withdraw_life)) {
					int amplifier = Math.max(0, (int) (excess / 3 - 1));
					caster.addPotionEffect(new PotionEffect(MobEffects.ABSORPTION, 160, amplifier));
				}
				caster.heal(heal);
			}

			this.playSound(world, caster, ticksInUse, -1, modifiers);
			return true;
		}

		return false;
	}

	@Override
	public boolean canBeCastBy(EntityLiving npc, boolean override) { return true; }

	@Override
	public boolean canBeCastBy(TileEntityDispenser dispenser) { return true; }

	@Override
	public boolean applicableForItem(Item item) {
		return item == AncientSpellcraftItems.ancient_spellcraft_spell_book || item == AncientSpellcraftItems.ancient_spellcraft_scroll;
	}
}
