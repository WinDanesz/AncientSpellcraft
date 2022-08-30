package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.registry.ASItems;
import electroblob.wizardry.Wizardry;
import electroblob.wizardry.item.ItemWizardArmour;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.spell.SpellRay;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class Transplace extends SpellRay implements IClassSpell {

	public Transplace() {
		super(AncientSpellcraft.MODID, "transplace", SpellActions.POINT, false);
		this.soundValues(1, 1.1f, 0.2f);
	}

	@Override
	protected boolean onEntityHit(World world, Entity target, Vec3d hit, EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {
		boolean res = swapPlace(world, target, caster);
		playSound(world, caster, 0, -1, new SpellModifiers());
		return res;
	}

	public static boolean swapPlace(World world, Entity target, EntityLivingBase caster) {
		if (EntityUtils.isLiving(target)) {

			if (world.isRemote) {
				for (int i = 0; i < 10; i++) {
					double dx1 = caster.posX;
					double dy1 = caster.posY + caster.height * world.rand.nextFloat();
					double dz1 = caster.posZ;
					world.spawnParticle(EnumParticleTypes.PORTAL, dx1, dy1, dz1, world.rand.nextDouble() - 0.5,
							world.rand.nextDouble() - 0.5, world.rand.nextDouble() - 0.5);
				}

				for (int i = 0; i < 10; i++) {
					double dx1 = target.posX;
					double dy1 = target.posY + caster.height * world.rand.nextFloat();
					double dz1 = target.posZ;
					world.spawnParticle(EnumParticleTypes.PORTAL, dx1, dy1, dz1, world.rand.nextDouble() - 0.5,
							world.rand.nextDouble() - 0.5, world.rand.nextDouble() - 0.5);
				}

				if (caster instanceof EntityPlayer) { Wizardry.proxy.playBlinkEffect((EntityPlayer) caster); }
			}

			double posX = caster.posX;
			double posY = caster.posY;
			double posZ = caster.posZ;
			caster.setPositionAndUpdate(target.posX, target.posY, target.posZ);
			target.setPositionAndUpdate(posX, posY, posZ);

		}

		return true;
	}

	@Override
	protected boolean onBlockHit(World world, BlockPos pos, EnumFacing side, Vec3d hit, EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {
		return false;
	}

	@Override
	protected boolean onMiss(World world, EntityLivingBase caster, Vec3d origin, Vec3d direction, int ticksInUse, SpellModifiers modifiers) {
		return true;
	}

	@Override
	protected void spawnParticle(World world, double x, double y, double z, double vx, double vy, double vz) {
		ParticleBuilder.create(ParticleBuilder.Type.DARK_MAGIC).pos(x, y, z).clr(0x571e65).spawn(world);
		ParticleBuilder.create(ParticleBuilder.Type.DARK_MAGIC).pos(x, y, z).clr(0x251609).spawn(world);
		ParticleBuilder.create(ParticleBuilder.Type.SPARKLE).pos(x, y, z).time(12 + world.rand.nextInt(8)).clr(0x0b4b40).spawn(world);
	}

	@Override
	public ItemWizardArmour.ArmourClass getArmourClass() { return ItemWizardArmour.ArmourClass.SAGE; }

	@Override
	public boolean applicableForItem(Item item) {
		return item == ASItems.mystic_spell_book;
	}
}
