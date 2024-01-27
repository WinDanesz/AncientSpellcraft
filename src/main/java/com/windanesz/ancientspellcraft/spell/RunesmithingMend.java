package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.block.BlockArcaneAnvil;
import com.windanesz.ancientspellcraft.tileentity.TileArcaneAnvil;
import electroblob.wizardry.block.BlockReceptacle;
import electroblob.wizardry.constants.Element;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class RunesmithingMend extends RunesmithingSpellBase implements IRunicHammerSpell, IClassSpell {

	public RunesmithingMend(String name, EnumAction action, boolean isContinuous) {
		super(name, action, isContinuous);
	}

	@Override
	public boolean doAnvilEffect(World world, BlockPos pos, EnumFacing side, Vec3d hit,
			@Nullable EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {

		int[] colours = BlockReceptacle.PARTICLE_COLOURS.get(Element.MAGIC);

		if (world.isRemote) {
			for (int i = 0; i < (ticksInUse * 0.1 * 2); i++) {
				ParticleBuilder.create(ParticleBuilder.Type.DUST).pos(pos.getX() + world.rand.nextFloat(), pos.getY() + 2, pos.getZ() + world.rand.nextFloat())
						.vel(0, 0.05 + (world.rand.nextFloat() * 0.1), 0).clr(colours[1]).fade(colours[2]).time(40).shaded(false).spawn(world);
			}
		}

		ItemStack stack = BlockArcaneAnvil.getItemOnLeftSlot(world, pos);

		TileEntity tile = world.getTileEntity(pos);

		if (tile instanceof TileArcaneAnvil) {
			ItemStack copy = stack.copy();

			if (copy.getItemDamage() != 0 && copy.getItem().isRepairable()) {
				float percentToRestore = copy.getMaxDamage() * 0.05f;
				copy.setItemDamage((int) (copy.getItemDamage() - percentToRestore));

				((TileArcaneAnvil) tile).setInventorySlotContents(0, copy);
				return true;
			}
		}
		return false;
	}
}
