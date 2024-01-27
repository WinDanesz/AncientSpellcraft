package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.registry.ASItems;
import electroblob.wizardry.entity.EntityMeteor;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.spell.SpellAreaEffect;
import electroblob.wizardry.util.AllyDesignationSystem;
import electroblob.wizardry.util.BlockUtils;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

public class Armageddon extends SpellAreaEffect {

	public static final String BURN_DURATION = "burn_duration";
	public static final String MAX_DAMAGE = "max_damage";

	public Armageddon() {
		super(AncientSpellcraft.MODID, "armageddon", SpellActions.SUMMON, true);
		addProperties(BURN_DURATION, MAX_DAMAGE);
	}

	@Override
	public boolean cast(World world, EntityPlayer caster, EnumHand hand, int ticksInUse, SpellModifiers modifiers) {
		boolean f = super.cast(world, caster, hand, ticksInUse, modifiers);

		if (!world.isRemote) {
			float radius = getProperty(EFFECT_RADIUS).floatValue() * modifiers.get(WizardryItems.blast_upgrade);
			List<BlockPos> sphere = BlockUtils.getBlockSphere(caster.getPosition().up(6), radius);

			int yPos = caster.getPosition().getY() + 16;
			List<BlockPos> validSpawnLocations = sphere.stream()
					.filter(p -> p.getY() == yPos && p.distanceSq(new BlockPos(caster.posX, p.getY(), caster.posZ)) > 9) // 3 block radius squared
					.collect(Collectors.toList());

			for (int i = 0; i < 1 && !validSpawnLocations.isEmpty(); i++) {
				BlockPos currPos = validSpawnLocations.remove(world.rand.nextInt(validSpawnLocations.size()));
				System.out.println("distance: " + currPos.distanceSq(caster.getPosition()));

				EntityMeteor meteor = new EntityMeteor(world, currPos.getX() + 0.5, currPos.getY(), currPos.getZ() + 0.5, 0.5f, true);

				meteor.motionX = 0;
				meteor.motionY = -1;
				meteor.motionZ = 0;

				world.spawnEntity(meteor);
			}
		} else {
			ParticleBuilder.create(ParticleBuilder.Type.MAGIC_FIRE).clr(255, 255, 247).vel(0, 0.1, 0).fade(1f, 1f, 1f).spin(0.8f, 0.03f).time(40).entity(caster).scale(1.2f).spawn(world);
			ParticleBuilder.create(ParticleBuilder.Type.MAGIC_FIRE).clr(255, 255, 247).vel(0, 0.1, 0).fade(1f, 1f, 1f).spin(0.8f, -0.03f).time(40).entity(caster).scale(1.2f).spawn(world);
		}

		this.playSound(world, caster, ticksInUse, -1, modifiers);
		return true;
	}


	@Override
	protected boolean affectEntity(World world, Vec3d origin,
			@Nullable EntityLivingBase caster, EntityLivingBase target, int targetCount, int ticksInUse, SpellModifiers modifiers) {
		if (caster instanceof EntityPlayer && AllyDesignationSystem.isAllied((EntityPlayer) caster, target))
			return false;

		return true;
	}

	@Override
	protected SoundEvent[] createSounds() {
		return this.createContinuousSpellSounds();
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
	public boolean applicableForItem(Item item) {
		return item == ASItems.ancient_spellcraft_spell_book || item == ASItems.ancient_spellcraft_scroll;
	}
}
