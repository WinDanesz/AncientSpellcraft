package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftItems;
import electroblob.wizardry.entity.projectile.EntityMagicFireball;
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
			int yPos = caster.getPosition().getY() + 6;
			List<BlockPos> list = sphere.stream().filter(p -> p.getY() == yPos)
					.collect(Collectors.toList());

			for (int i = 0; i < 4; i++) {
				BlockPos currPos = list.get(world.rand.nextInt(list.size()));
				EntityMagicFireball fireball = new EntityMagicFireball(world);
				fireball.setCaster(caster);
				fireball.setLifetime(80);
				fireball.setPosition(currPos.getX() + world.rand.nextFloat(), currPos.getY() + world.rand.nextFloat() * (world.rand.nextBoolean() ? 1 : -1)
						, currPos.getZ() + world.rand.nextFloat());
				fireball.motionY = -0.5f;
				world.spawnEntity(fireball);
			}

		} else {
			ParticleBuilder.create(ParticleBuilder.Type.MAGIC_FIRE).clr(255, 255, 247).vel(0, 0.1, 0).fade(1f, 1f, 1f).spin(0.8f, 0.03f).time(40).entity(caster).scale(1.2f).spawn(world);
			ParticleBuilder.create(ParticleBuilder.Type.MAGIC_FIRE).clr(255, 255, 247).vel(0, 0.1, 0).fade(1f, 1f, 1f).spin(0.8f, -0.03f).time(40).entity(caster).scale(1.2f).spawn(world);
		}

		this.playSound(world, caster, ticksInUse, -1, modifiers);
		return f;
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
		return item == AncientSpellcraftItems.ancient_spellcraft_spell_book || item == AncientSpellcraftItems.ancient_spellcraft_scroll;
	}
}
