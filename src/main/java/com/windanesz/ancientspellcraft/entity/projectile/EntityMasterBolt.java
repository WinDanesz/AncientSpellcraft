package com.windanesz.ancientspellcraft.entity.projectile;

import com.windanesz.ancientspellcraft.block.BlockMasterBolt;
import com.windanesz.ancientspellcraft.registry.ASBlocks;
import com.windanesz.ancientspellcraft.registry.ASSpells;
import com.windanesz.ancientspellcraft.spell.MasterBolt;
import electroblob.wizardry.entity.projectile.EntityMagicArrow;
import electroblob.wizardry.registry.WizardrySounds;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.BlockUtils;
import electroblob.wizardry.util.MagicDamage.DamageType;
import electroblob.wizardry.util.ParticleBuilder;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EntityMasterBolt extends EntityMagicArrow {

	/** Creates a new lightning arrow in the given world. */
	public EntityMasterBolt(World world){
		super(world);
	}

	@Override public double getDamage(){ return ASSpells.master_bolt.getProperty(Spell.DAMAGE).doubleValue(); }

	@Override public int getLifetime(){ return -1; }

	@Override public DamageType getDamageType(){ return DamageType.SHOCK; }

	@Override public boolean doGravity(){ return false; }

	@Override public boolean doDeceleration(){ return false; }

	@Override
	public boolean doOverpenetration() {
		return true;
	}

	@Override
	public void onEntityHit(EntityLivingBase entityHit){

		if(world.isRemote){
			for(int j = 0; j < 8; j++){
				ParticleBuilder.create(ParticleBuilder.Type.SPARK, rand, posX, posY + height / 2, posZ, 1, false).spawn(world);
			}
		}

		this.playSound(WizardrySounds.ENTITY_LIGHTNING_ARROW_HIT, 1.0F, 1.0F);
	}

	//	@Override
	//	public void onBlockHit(RayTraceResult hit){
	//		if(this.world.isRemote){
	//			Vec3d vec = hit.hitVec.add(new Vec3d(hit.sideHit.getDirectionVec()).scale(WizardryUtilities.ANTI_Z_FIGHTING_OFFSET));
	//			ParticleBuilder.create(Type.SCORCH).pos(vec).face(hit.sideHit).clr(0.4f, 0.8f, 1).scale(0.6f).spawn(world);
	//		}
	//	}

	@Override
	protected void onBlockHit(RayTraceResult hit) {
		BlockPos pos = hit.getBlockPos().offset(hit.sideHit);

		if (!world.isRemote && BlockUtils.canBlockBeReplaced(world, pos, true)) {
			EnumFacing facing = this.getHorizontalFacing();
			if (facing == EnumFacing.NORTH || facing == EnumFacing.SOUTH) { facing = facing.getOpposite(); }
			world.setBlockState(pos, ASBlocks.master_bolt.getDefaultState().withProperty(BlockMasterBolt.FACING, facing));
			MasterBolt.storeLocation(world, (EntityPlayer) this.getCaster(), pos);
		}
	}

	@Override
	public void tickInAir(){
		if(world.isRemote){
			ParticleBuilder.create(ParticleBuilder.Type.SPARK).pos(posX, posY, posZ).spawn(world);
		} else {
			if (getCaster() != null && getCaster().isAirBorne) {
				this.motionX *= 1.1f;
				this.motionY *= 1.1f;
				this.motionZ *= 1.1f;
			}
			if (this.posY > 255) {
				this.setDead();
			}
		}
	}

	@Override
	protected void entityInit(){}

}