package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.item.ItemNewArtefact;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftItems;
import com.windanesz.ancientspellcraft.util.SpellcastUtils;
import electroblob.wizardry.event.SpellCastEvent;
import electroblob.wizardry.item.ItemArtefact;
import electroblob.wizardry.registry.Spells;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class SpringCharge extends Spell {

	public static final String HORIZONTAL_SPEED = "horizontal_speed";
	public static final String VERTICAL_SPEED = "vertical_speed";

	public SpringCharge() {
		super(AncientSpellcraft.MODID, "spring_charge", EnumAction.NONE, true);
		addProperties(HORIZONTAL_SPEED, VERTICAL_SPEED);
		soundValues(0.5f, 1, 0);
	}

	@Override
	public boolean cast(World world, EntityPlayer caster, EnumHand hand, int ticksInUse, SpellModifiers modifiers) {
		if (ticksInUse > 3 && !caster.onGround) {
			if (caster.isHandActive()) { caster.stopActiveHand(); }
		}

		if (ticksInUse > 40 && caster.isHandActive()) { caster.stopActiveHand(); }

		if (world.isRemote) {

		ParticleBuilder.create(ParticleBuilder.Type.SPARK).pos(
				caster.posX + ((world.rand.nextDouble() / 5) * (ticksInUse / 10F) * (world.rand.nextBoolean() ? -1 : 1)),
				caster.posY + +(0.3 + (world.rand.nextDouble() / 5) * (ticksInUse / 10F) * (world.rand.nextBoolean() ? -1 : 1)),
				caster.posZ + (world.rand.nextDouble() / 5) * (ticksInUse / 10F) * (world.rand.nextBoolean() ? -1 : 1)).time(4).spawn(world);
		}

		return caster.onGround;
	}

	@Override
	public void finishCasting(World world,
			@Nullable EntityLivingBase caster, double x, double y, double z, @Nullable EnumFacing direction, int ticksInUse, SpellModifiers modifiers) {
		super.finishCasting(world, caster, x, y, z, direction, ticksInUse, modifiers);

		float reduct = 1.0f;
		if (caster instanceof EntityPlayer) {
			if (!caster.onGround && !ItemArtefact.isArtefactActive((EntityPlayer) caster, AncientSpellcraftItems.ring_cloudwalker)) {
				return;
			} else if (!caster.onGround) {
				ticksInUse = 2;
				reduct = 0.5f;
			}
		}

		ticksInUse = Math.min(40, ticksInUse);

		if (caster != null) {
			caster.motionY = getProperty(VERTICAL_SPEED).floatValue() * modifiers.get(SpellModifiers.POTENCY) * ticksInUse * reduct;
			double horizontalSpeed = getProperty(HORIZONTAL_SPEED).floatValue();
			caster.addVelocity(caster.getLookVec().x * horizontalSpeed * ticksInUse, 0, caster.getLookVec().z * horizontalSpeed * ticksInUse);

			if (world.isRemote) {
				for (int i = 0; i < 10; i++) {
					double xa = caster.posX + world.rand.nextFloat() - 0.5F;
					double ya = caster.posY;
					double za = caster.posZ + world.rand.nextFloat() - 0.5F;
					world.spawnParticle(EnumParticleTypes.CLOUD, xa, ya, za, 0, 0, 0);
				}
			}
		}

		if (ticksInUse > 5 && caster instanceof EntityPlayer && caster.onGround && ItemNewArtefact.isNewArtefactActive((EntityPlayer) caster, AncientSpellcraftItems.belt_spring_charge)) {
			SpellModifiers modifiers1 = new SpellModifiers();
			modifiers1.set(WizardryItems.blast_upgrade, ticksInUse * 0.1f, true);
			modifiers1.set(SpellModifiers.POTENCY, 0.5f, true);
			SpellcastUtils.tryCastSpellAsPlayer((EntityPlayer) caster, Spells.lightning_pulse, EnumHand.MAIN_HAND, SpellCastEvent.Source.WAND, modifiers1, 0);
		}
	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == AncientSpellcraftItems.ancient_spellcraft_spell_book || item == AncientSpellcraftItems.ancient_spellcraft_scroll;
	}
}
