package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.entity.projectile.EntitySafeIceShard;
import com.windanesz.ancientspellcraft.registry.ASItems;
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

			List<BlockPos> list = sphere.stream().filter(p -> p.getY() == yPos && p.distanceSq(new BlockPos(caster.posX, p.getY(), caster.posZ)) > 12)
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
			ParticleBuilder.create(ParticleBuilder.Type.SNOW).vel(0, 0.1, 0).fade(1f, 1f, 1f).spin(0.8f, 0.03f).time(40).entity(caster).scale(1.2f).spawn(world);
			ParticleBuilder.create(ParticleBuilder.Type.SNOW).vel(0, 0.1, 0).fade(1f, 1f, 1f).spin(0.8f, -0.03f).time(40).entity(caster).scale(1.2f).spawn(world);
			
			for(int i=0; i<6; i++){
				double speed = (caster.world.rand.nextBoolean() ? 1 : -1) * (0.1 + 0.05 * caster.world.rand.nextDouble());
				ParticleBuilder.create(ParticleBuilder.Type.SNOW).pos(caster.posX, caster.posY + caster.world.rand.nextDouble() * 18, caster.posZ).vel(0, 0, 0)
						.time(100).scale(2).spin(caster.world.rand.nextDouble() * (18 - 0.5) + 0.5, speed * 0.1).shaded(true).spawn(world);
			}
		}

		this.playSound(world, caster, ticksInUse, -1, modifiers);
		return true;
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
		return item == ASItems.ancient_spellcraft_spell_book || item == ASItems.ancient_spellcraft_scroll;
	}
}
