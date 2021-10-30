package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftBlocks;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftItems;
import com.windanesz.ancientspellcraft.tileentity.TileSentinel;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.spell.SpellRay;
import electroblob.wizardry.util.BlockUtils;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class ConjureSentry extends SpellRay {

	private boolean greater;

	public ConjureSentry(String name, boolean greater) {
		super(AncientSpellcraft.MODID, name, SpellActions.POINT_UP, false);
		this.greater = greater;
		addProperties(DURATION);
	}

	@Override
	protected boolean onEntityHit(World world, Entity target, Vec3d hit,
			@Nullable EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {
		return false;
	}

	@Override
	protected boolean onMiss(World world, @Nullable EntityLivingBase caster, Vec3d origin, Vec3d direction, int ticksInUse, SpellModifiers modifiers) {
		return false;
	}

	@Override
	protected boolean onBlockHit(World world, BlockPos pos, EnumFacing side, Vec3d hit,
			@Nullable EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {

		if (caster instanceof EntityPlayer) {
			pos = pos.offset(side);

			if (BlockUtils.canBlockBeReplaced(world, pos)) {

				if (!world.isRemote) {
					Block sentinelBlock = greater ? AncientSpellcraftBlocks.SENTINEL_BLOCK_LARGE_GOLD : AncientSpellcraftBlocks.SENTINEL_BLOCK_GOLD;

					world.setBlockState(pos, sentinelBlock.getDefaultState());
					TileEntity tile = world.getTileEntity(pos);
					if (tile instanceof TileSentinel) {
						((TileSentinel) tile).setLifeTime(getProperty(DURATION).intValue());
						((TileSentinel) tile).getSpellCasterEntity().setOwnerId(caster.getUniqueID());
					}
				}
				return true;
			}
		}

		return false;

	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == AncientSpellcraftItems.ancient_spell_book || item == AncientSpellcraftItems.ancient_spellcraft_scroll;
	}

}
