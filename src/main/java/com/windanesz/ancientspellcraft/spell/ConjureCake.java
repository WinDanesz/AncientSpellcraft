package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.spell.SpellRay;
import electroblob.wizardry.util.BlockUtils;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.ParticleBuilder.Type;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ConjureCake extends SpellRay {

	public ConjureCake(){
		super(AncientSpellcraft.MODID, "conjure_cake", SpellActions.POINT, false);
		this.ignoreLivingEntities(true);
	}

	@Override
	protected boolean onEntityHit(World world, Entity target, Vec3d hit, EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers){
		return false;
	}

	@Override
	protected boolean onBlockHit(World world, BlockPos pos, EnumFacing side, Vec3d hit, EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers){
		
		pos = pos.offset(side);
		
		if(world.isRemote){
			ParticleBuilder.create(Type.FLASH).pos(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5).scale(3)
			.clr(0.75f, 1, 0.85f).spawn(world);
		}
		
		if(BlockUtils.canBlockBeReplaced(world, pos)){
			if(!world.isRemote){
				world.setBlockState(pos, Blocks.CAKE.getDefaultState());
			}
			return true;
		}
		
		return false;
	}

	@Override
	protected boolean onMiss(World world, EntityLivingBase caster, Vec3d origin, Vec3d direction, int ticksInUse, SpellModifiers modifiers){
		return false;
	}

}
