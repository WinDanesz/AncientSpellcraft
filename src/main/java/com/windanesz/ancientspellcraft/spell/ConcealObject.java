package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.block.BlockConcealedBlock;
import com.windanesz.ancientspellcraft.registry.ASBlocks;
import com.windanesz.ancientspellcraft.registry.ASItems;
import com.windanesz.ancientspellcraft.tileentity.TileConcealedBlock;
import electroblob.wizardry.item.ItemWizardArmour;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.spell.SpellRay;
import electroblob.wizardry.util.BlockUtils;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ConcealObject extends SpellRay implements IClassSpell {


	public ConcealObject() {
		super(AncientSpellcraft.MODID, "conceal_object", SpellActions.POINT, false);
		this.soundValues(1, 1.1f, 0.2f);
	}

	@Override
	protected boolean onEntityHit(World world, Entity target, Vec3d hit, EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {
		return false;
	}

	@Override
	protected boolean onBlockHit(World world, BlockPos pos, EnumFacing side, Vec3d hit, EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {
		if (!world.isRemote && BlockUtils.canBreakBlock(caster, world, pos)) {

			if (world.getBlockState(pos).getBlock() instanceof BlockConcealedBlock) {
				TileEntity tile = world.getTileEntity(pos);

				if (tile instanceof TileConcealedBlock) {
					((TileConcealedBlock) tile).revert();
					return true;
				}
			}

			NBTTagCompound nbt = new NBTTagCompound();
			TileEntity tile = world.getTileEntity(pos);
			nbt.setTag("old_state", NBTUtil.writeBlockState(new NBTTagCompound(), world.getBlockState(pos)));
			if (tile != null) {
				nbt.setTag("tile_data", tile.writeToNBT(new NBTTagCompound()));
				world.removeTileEntity(pos);
			}

			world.setBlockState(pos, ASBlocks.concealed_block.getDefaultState());
			TileConcealedBlock concealedTile = new TileConcealedBlock();
			world.setTileEntity(pos, concealedTile);
			concealedTile.store(nbt);
			concealedTile.setCaster(caster);
			world.setTileEntity(pos, tile);
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
