package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftItems;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.spell.SpellRay;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class FireWall extends SpellRay {
	public static final String LENGTH = "length";

	public FireWall() {
		super(AncientSpellcraft.MODID, "firewall", SpellActions.POINT, false);
		this.soundValues(0.5f, 1.1f, 0.2f);
		addProperties(LENGTH);

	}

	@Override
	public boolean requiresPacket() {
		return false;
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

		pos = pos.offset(EnumFacing.UP);
		if (world.getBlockState(pos).getBlock().isReplaceable(world, pos)) {

			EnumFacing facing = caster.getHorizontalFacing().rotateY();

			List<BlockPos> blockPosList = new ArrayList<>();
			blockPosList.add(pos);
			blockPosList.add(pos.offset(facing));
			blockPosList.add(pos.offset(facing.getOpposite()));

			for (BlockPos currPos : blockPosList) {
				if (world.getBlockState(currPos).getBlock().isReplaceable(world, currPos)) {
					world.setBlockState(currPos, Blocks.FIRE.getDefaultState());
				}
			}
			return true;

		}
		return false;
	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == AncientSpellcraftItems.ancient_spellcraft_spell_book || item == AncientSpellcraftItems.ancient_spellcraft_scroll;
	}
}
