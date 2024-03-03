package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.registry.ASItems;
import com.windanesz.ancientspellcraft.util.ASUtils;
import com.windanesz.ancientspellcraft.util.WizardArmourUtils;
import electroblob.wizardry.client.DrawingUtils;
import electroblob.wizardry.constants.Element;
import electroblob.wizardry.data.IStoredVariable;
import electroblob.wizardry.data.Persistence;
import electroblob.wizardry.data.WizardData;
import electroblob.wizardry.item.ItemArtefact;
import electroblob.wizardry.item.ItemCrystal;
import electroblob.wizardry.item.ItemWizardArmour;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.registry.WizardryBlocks;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

import java.util.Optional;
import java.util.Random;

public class AbsorbCrystal extends Spell {

	public static final IStoredVariable<Integer> ELEMENT = IStoredVariable.StoredVariable.ofInt("AbsorbCrystalElement", Persistence.ALWAYS);

	public AbsorbCrystal() {
		super(AncientSpellcraft.MODID, "absorb_crystal", SpellActions.SUMMON, true);
		WizardData.registerStoredVariables(ELEMENT);
	}

	@Override
	protected SoundEvent[] createSounds() {
		return createContinuousSpellSounds();
	}

	@Override
	public boolean cast(World world, EntityPlayer caster, EnumHand hand, int ticksInUse, SpellModifiers modifiers) {
		Optional<Element> elementOptional = WizardArmourUtils.getFullSetElementForClassOptional(caster, ItemWizardArmour.ArmourClass.WARLOCK);
		ItemStack crystal = caster.getHeldItemOffhand();
		boolean isBlock = crystal.getItem() == Item.getItemFromBlock(WizardryBlocks.crystal_block);
		if ((!(crystal.getItem() instanceof ItemCrystal) || (isBlock && !ItemArtefact.isArtefactActive(caster, ASItems.ring_absorb_crystal))) || crystal.getItemDamage() == 0) {
			ASUtils.sendMessage(caster, "You must hold an elemental crystal in your offhand", true);
			return false;
		}
		if (elementOptional.isPresent()) {
			Element element = elementOptional.get();

			Random rand = caster.world.rand;
			double posX = caster.posX;
			double posY = caster.posY;
			double posZ = caster.posZ;

			if (world.isRemote) {

				if (world.getTotalWorldTime() % 3 == 0) {
					ParticleBuilder.create(WarlockSpellVisuals.ELEMENTAL_PARTICLES.get(element), rand, posX + rand.nextDouble() * 0.5d * (rand.nextBoolean() ? 1 : -1), posY,
									posZ + rand.nextDouble() * 0.5d * (rand.nextBoolean() ? 1 : -1), 0.03, true).vel(0, 0.3, 0).clr(WarlockSpellVisuals.PARTICLE_COLOURS.get(element)[0])
							.time(20 + rand.nextInt(50)).spawn(world);
					ParticleBuilder.create(WarlockSpellVisuals.ELEMENTAL_PARTICLES.get(element), rand, posX + rand.nextDouble() * 0.5d * (rand.nextBoolean() ? 1 : -1), posY,
									posZ + rand.nextDouble() * 0.5d * (rand.nextBoolean() ? 1 : -1), 0.03, true).vel(0, 0.3, 0).clr(WarlockSpellVisuals.PARTICLE_COLOURS.get(element)[1])
							.time(20 + rand.nextInt(50)).spawn(world);

					ParticleBuilder.create(WarlockSpellVisuals.ELEMENTAL_PARTICLES.get(element), rand, posX + rand.nextDouble() * 0.2d * (rand.nextBoolean() ? 1 : -1), posY,
									posZ + rand.nextDouble() * 0.2d * (rand.nextBoolean() ? 1 : -1), 0.03, true).spin(0.7, 0.05).vel(0, 0.3, 0).clr(WarlockSpellVisuals.PARTICLE_COLOURS.get(element)[2])
							.time(20 + rand.nextInt(50)).spawn(world);
				}

				// horizontal particle on the floor, always visible
				ParticleBuilder.create(ParticleBuilder.Type.FLASH)
						.pos(caster.posX, caster.posY + 0.101, caster.posZ)
						.face(EnumFacing.UP)
						.clr(DrawingUtils.mix(WarlockSpellVisuals.PARTICLE_COLOURS.get(element)[1], WarlockSpellVisuals.PARTICLE_COLOURS.get(element)[2], 0.5f))
						.collide(false)
						.scale(2.3F)
						.time(10)
						.spawn(world);
			} else if (ticksInUse == 60) {
				WizardData data = WizardData.get(caster);
				data.setVariable(ELEMENT, isBlock ? caster.getHeldItemOffhand().getItemDamage() + 7 : caster.getHeldItemOffhand().getItemDamage());
				caster.stopActiveHand();
				ASUtils.sendMessage(caster, "Absorbed " + caster.getHeldItemOffhand().getDisplayName(), true);
				caster.getHeldItemOffhand().shrink(1);
				return true;
			}
		}
		return false;
	}

	@Override
	protected void playSound(World world, EntityLivingBase entity, int ticksInUse, int duration, SpellModifiers modifiers, String... sounds) {
		this.playSoundLoop(world, entity, ticksInUse);
	}

	@Override
	protected void playSound(World world, double x, double y, double z, int ticksInUse, int duration, SpellModifiers modifiers, String... sounds) {
		this.playSoundLoop(world, x, y, z, ticksInUse, duration);
	}

	@Override
	public boolean canBeCastBy(EntityLiving npc, boolean override) {
		return false;
	}

	@Override
	public boolean canBeCastBy(TileEntityDispenser dispenser) {
		return false;
	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == ASItems.ancient_spell_book || item == ASItems.ancient_spellcraft_scroll;
	}

	public static Optional<Element> getElement(WizardData data) {
		if (data.getVariable(ELEMENT) == null) {
			return Optional.empty();
		} else {
			return Optional.of(Element.values()[(int) data.getVariable(ELEMENT) > 7 ? (int) data.getVariable(ELEMENT) -7 : (int) data.getVariable(ELEMENT)]);
		}

	}

	public static boolean isBlock(WizardData data) {
		if (data.getVariable(ELEMENT) == null) {
			return false;
		} else {
			return data.getVariable(ELEMENT) > 7;
		}

	}



}