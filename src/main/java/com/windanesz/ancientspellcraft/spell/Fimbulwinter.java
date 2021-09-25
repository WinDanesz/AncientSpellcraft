package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.entity.projectile.EntitySafeIceShard;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftItems;
import electroblob.wizardry.entity.projectile.EntityIceCharge;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.spell.SpellAreaEffect;
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

public class Fimbulwinter extends SpellAreaEffect {

	public Fimbulwinter() {
		super(AncientSpellcraft.MODID, "fimbulwinter", SpellActions.SUMMON, true);
	}

	@Override
	public boolean cast(World world, EntityPlayer caster, EnumHand hand, int ticksInUse, SpellModifiers modifiers) {
		boolean f = super.cast(world, caster, hand, ticksInUse, modifiers);

		if (!world.isRemote) {

			int radius = (int) (getProperty(EFFECT_RADIUS).floatValue() * modifiers.get(WizardryItems.blast_upgrade));
			List<BlockPos> sphere = BlockUtils.getBlockSphere(caster.getPosition().up(12), radius);
			int yPos = caster.getPosition().getY() + 10;

			List<BlockPos> list = sphere.stream().filter(p -> p.getY() == yPos && p.getX() != caster.getPosition().getX() && p.getZ() != caster.getPosition().getZ())
					.collect(Collectors.toList());

			for (int i = 0; i < 3; i++) {

				BlockPos currPos = list.get(world.rand.nextInt(list.size()));

				EntitySafeIceShard iceShard = new EntitySafeIceShard(world);
				iceShard.setCaster(caster);
				iceShard.setPosition(currPos.getX() + world.rand.nextFloat(), currPos.getY() + world.rand.nextFloat(), currPos.getZ() + world.rand.nextFloat());
				iceShard.motionY = -0.5f;
				world.spawnEntity(iceShard);
			}

			BlockPos currPos = list.get(world.rand.nextInt(list.size()));

			EntityIceCharge charge = new EntityIceCharge(world);
			charge.setPosition(currPos.getX(), currPos.getY(), currPos.getZ());
			charge.ticksExisted = world.rand.nextInt(20);
			charge.motionY = world.rand.nextDouble() * 0.2f;
			world.spawnEntity(charge);

		} else {
			ParticleBuilder.create(ParticleBuilder.Type.ICE).vel(0, 0.1, 0).fade(1f, 1f, 1f).spin(0.8f, 0.03f).time(40).entity(caster).scale(1.2f).spawn(world);
			ParticleBuilder.create(ParticleBuilder.Type.ICE).vel(0, 0.1, 0).fade(1f, 1f, 1f).spin(0.8f, -0.03f).time(40).entity(caster).scale(1.2f).spawn(world);
		}

		this.playSound(world, caster, ticksInUse, -1, modifiers);
		return f;
	}

	@Override
	protected boolean affectEntity(World world, Vec3d origin,
			@Nullable EntityLivingBase caster, EntityLivingBase target, int targetCount, int ticksInUse, SpellModifiers modifiers) {
		return false;
	}

	@Override
	protected SoundEvent[] createSounds(){
		return this.createContinuousSpellSounds();
	}

	@Override
	protected void playSound(World world, EntityLivingBase entity, int ticksInUse, int duration, SpellModifiers modifiers, String... sounds){
		this.playSoundLoop(world, entity, ticksInUse);
	}

	@Override
	protected void playSound(World world, double x, double y, double z, int ticksInUse, int duration, SpellModifiers modifiers, String... sounds){
		this.playSoundLoop(world, x, y, z, ticksInUse, duration);
	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == AncientSpellcraftItems.ancient_spellcraft_spell_book || item == AncientSpellcraftItems.ancient_spellcraft_scroll;
	}
}
