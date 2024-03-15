package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.registry.ASBlocks;
import com.windanesz.ancientspellcraft.registry.ASItems;
import com.windanesz.ancientspellcraft.tileentity.TileArcaneWall;
import electroblob.wizardry.item.ItemWizardArmour;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.spell.SpellRay;
import electroblob.wizardry.util.BlockUtils;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ArcaneWall extends SpellRay implements IClassSpell {

	public ArcaneWall() {
		super(AncientSpellcraft.MODID, "arcane_wall", SpellActions.POINT, false);
		this.soundValues(1, 1.1f, 0.2f);
	}

	@Override
	protected boolean onEntityHit(World world, Entity target, Vec3d hit, EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {
		return false;
	}

	@Override
	protected boolean onBlockHit(World world, BlockPos pos, EnumFacing side, Vec3d hit, EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {

		if (caster != null && caster.isSneaking() && world.getBlockState(pos).getBlock() == ASBlocks.arcane_wall) {

			if (!world.isRemote) {
				// Dispelling of blocks
				world.removeTileEntity(pos);
				world.setBlockToAir(pos);

			} else {
				ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5).scale(3)
						.clr(0.68f, 0.17f, 0.78f).spawn(world);
			}

			return true;
		}

		pos = pos.offset(side);

		if (world.isRemote) {
			ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5).scale(3)
					.clr(0.68f, 0.17f, 0.78f).spawn(world);
		}

		if (BlockUtils.canBlockBeReplaced(world, pos)) {

			if (!world.isRemote) {

				world.setBlockState(pos, ASBlocks.arcane_wall.getDefaultState());

				if (world.getTileEntity(pos) instanceof TileArcaneWall) {
					((TileArcaneWall) world.getTileEntity(pos)).setCaster(caster);

					if (caster instanceof EntityPlayer && caster.isSneaking() && ((EntityPlayer) caster).isCreative()) {
						((TileArcaneWall) world.getTileEntity(pos)).setGenerated(true);
						((TileArcaneWall) world.getTileEntity(pos)).setCaster(null);
					}
				}
			}

			return true;
		}

		return true;
	}

	@Override
	protected boolean onMiss(World world, EntityLivingBase caster, Vec3d origin, Vec3d direction, int ticksInUse, SpellModifiers modifiers) {
		return true;
	}

	@Override
	protected void spawnParticle(World world, double x, double y, double z, double vx, double vy, double vz) {
		ParticleBuilder.create(ParticleBuilder.Type.DARK_MAGIC).pos(x, y, z).clr(0x571e65).spawn(world);
		ParticleBuilder.create(ParticleBuilder.Type.DARK_MAGIC).pos(x, y, z).clr(0xde14b9).spawn(world);
		ParticleBuilder.create(ParticleBuilder.Type.SPARKLE).pos(x, y, z).time(12 + world.rand.nextInt(8)).clr(0xb310e0).spawn(world);
	}

	@Override
	public ItemWizardArmour.ArmourClass getArmourClass() { return ItemWizardArmour.ArmourClass.SAGE; }

	@Override
	public boolean applicableForItem(Item item) {
		return item == ASItems.mystic_spell_book;
	}
}
