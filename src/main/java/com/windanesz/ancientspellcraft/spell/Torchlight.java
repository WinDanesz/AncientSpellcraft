package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.registry.ASItems;
import com.windanesz.ancientspellcraft.util.ASUtils;
import electroblob.wizardry.item.ItemWizardArmour;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.spell.SpellRay;
import electroblob.wizardry.util.BlockUtils;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.block.BlockTorch;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class Torchlight extends SpellRay implements IClassSpell {

	public Torchlight() {
		super(AncientSpellcraft.MODID, "torchlight", SpellActions.POINT, false);
	}

	@Override
	public boolean requiresPacket() { return false; }

	@Override
	protected boolean onEntityHit(World world, Entity target, Vec3d hit, EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {
		return false;
	}

	@Override
	protected boolean onBlockHit(World world, BlockPos pos, EnumFacing side, Vec3d hit, EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {

		if (caster instanceof EntityPlayer) {

			if (!((EntityPlayer) caster).inventory.hasItemStack(new ItemStack(Item.getItemFromBlock(Blocks.TORCH)))) {
				ASUtils.sendMessage(caster, "spell.ancientspellcraft:torchlight.no_torch", true);
				return false;
			}

			pos = pos.offset(side);

			if (world.isRemote) {
				ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5).scale(3)
						.clr(227, 154, 27).spawn(world);
			}

			if (!world.isRemote) {
				if (BlockUtils.canBlockBeReplaced(world, pos) && (side != EnumFacing.DOWN) &&
						world.setBlockState(pos, Blocks.TORCH.getDefaultState().withProperty(BlockTorch.FACING, side))) {
					ASUtils.shrinkInventoryStackByOne((EntityPlayer) caster, new ItemStack(Item.getItemFromBlock(Blocks.TORCH)));
					return true;
				}
			}
		}

		return false;
	}

	@Override
	protected boolean onMiss(World world, EntityLivingBase caster, Vec3d origin, Vec3d direction, int ticksInUse, SpellModifiers modifiers) {
		return false;
	}

	@Override
	public ItemWizardArmour.ArmourClass getArmourClass() { return ItemWizardArmour.ArmourClass.SAGE; }

	@Override
	public boolean applicableForItem(Item item) {
		return item == ASItems.mystic_spell_book;
	}

}
