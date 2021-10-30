package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.spell.SpellRay;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.ParticleBuilder.Type;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.block.BlockFurnace;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

// TODO
public class HeatFurnace extends SpellRay {

	public static final String BURN_TICKS_INCREASE = "burn_ticks_increase";

	public HeatFurnace() {
		super(AncientSpellcraft.MODID, "heat_furnace", SpellActions.POINT, true);
		this.particleVelocity(1);
		this.particleSpacing(0.5);
		addProperties(BURN_TICKS_INCREASE);
		this.soundValues(2.5f, 1, 0);
	}

	// The following three methods serve as a good example of how to implement continuous spell sounds (hint: it's easy)

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
	protected boolean onEntityHit(World world, Entity target, Vec3d hit, EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {
		return false;
	}

	@Override
	protected boolean onBlockHit(World world, BlockPos pos, EnumFacing side, Vec3d hit, EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {
		if (world.getBlockState(pos).getBlock() instanceof BlockFurnace) {

			TileEntity te = world.getTileEntity(pos);

			if (te instanceof TileEntityFurnace) {
				TileEntityFurnace furnace = (TileEntityFurnace) te;
				int furnaceBurnTime = furnace.getField(0);

				// lava burn time
				if (furnaceBurnTime < 20000) {

					//f (!world.isRemote) {
					//if (ticksInUse % 20 == 0) {
						furnace.setField(0, furnaceBurnTime + getProperty(BURN_TICKS_INCREASE).intValue());
					//}

					//BlockFurnace.setState(true, world, pos);
					//}
					return true;
				}
			}
		}

		return false;
	}

	@Override
	protected boolean onMiss(World world, EntityLivingBase caster, Vec3d origin, Vec3d direction, int ticksInUse, SpellModifiers modifiers) {
		return true;
	}

	@Override
	protected void spawnParticle(World world, double x, double y, double z, double vx, double vy, double vz) {
		//ParticleBuilder.create(Type.MAGIC_FIRE).pos(x, y, z).vel(vx, vy, vz).collide(true).spawn(world);
		ParticleBuilder.create(Type.MAGIC_FIRE).pos(x, y, z).vel(vx, vy, vz).collide(true).spawn(world);
	}

}
