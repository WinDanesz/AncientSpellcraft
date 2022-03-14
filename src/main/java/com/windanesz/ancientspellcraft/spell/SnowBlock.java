package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.registry.ASItems;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.spell.SpellRay;
import electroblob.wizardry.util.BlockUtils;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class SnowBlock extends SpellRay {
	public SnowBlock() {
		super(AncientSpellcraft.MODID, "snow_block", SpellActions.POINT, false);
		this.soundValues(0.5f, 1.1f, 0.2f);
	}

	@Override
	protected boolean onEntityHit(World world, Entity target, Vec3d hit, EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {
		return false;
	}

	@Override
	protected boolean onBlockHit(World world, BlockPos pos, EnumFacing side, Vec3d hit, EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {

		pos = pos.offset(side);
		if (!world.isRemote) {
			if (BlockUtils.canPlaceBlock(caster, world, pos)) {
				world.setBlockState(pos, Blocks.SNOW.getDefaultState());
			}
		} else {
			for (int i = 1; i < 12; i++) {
				double speed = (world.rand.nextBoolean() ? 1 : -1) * 0.1 + 0.05 * world.rand.nextDouble();
				ParticleBuilder.create(ParticleBuilder.Type.SNOW).pos(pos.getX() + 0.5, pos.getY() + world.rand.nextDouble() * 2, pos.getZ() + 0.5).vel(0, 0, 0)
						.time(20).scale(1).spin(world.rand.nextDouble() * +1.1, speed).spawn(world);
			}
		}
		return true;
	}

	@Override
	protected boolean onMiss(World world, EntityLivingBase caster, Vec3d origin, Vec3d direction, int ticksInUse, SpellModifiers modifiers) {
		return false;
	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == ASItems.ancient_spellcraft_spell_book || item == ASItems.ancient_spellcraft_scroll;
	}
}
