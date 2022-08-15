package com.windanesz.ancientspellcraft.entity.construct;

import com.windanesz.ancientspellcraft.block.ITemporaryBlock;
import com.windanesz.ancientspellcraft.registry.ASBlocks;
import electroblob.wizardry.entity.construct.EntityBoulder;
import electroblob.wizardry.entity.projectile.EntityEmber;
import electroblob.wizardry.registry.WizardrySounds;
import electroblob.wizardry.util.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;
import java.util.stream.Collectors;

public class EntityMoltenBoulder extends EntityBoulder {
	public EntityMoltenBoulder(World world) {
		super(world);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		if (!world.isRemote && this.ticksExisted % 15 == 0) {

			int width1 = (int) this.width;
			List<BlockPos> list = BlockUtils.getBlockSphere(this.getPosition().down(), (int) this.width).stream().filter(i -> !world.isAirBlock(i)).filter(i -> i.getY() == this.getPosition().down().getY()).collect(Collectors.toList());

			for (int i = 0; i < list.size(); i++) {

				BlockPos currPos = list.get(i);
				if (world.rand.nextInt(10) == 0) {
					if (world.isAirBlock(currPos.offset(EnumFacing.UP))) {
						world.setBlockState(currPos.offset(EnumFacing.UP), Blocks.FIRE.getDefaultState());
					}
				}
				ITemporaryBlock.placeTemporaryBlock(this.getCaster(), world, ASBlocks.CONJURED_MAGMA, currPos, 600);

				if (world.rand.nextInt(10) == 0) {
					if (world.isAirBlock(currPos.offset(EnumFacing.UP))) {
						world.setBlockState(currPos.offset(EnumFacing.UP), Blocks.FIRE.getDefaultState());
					}
				}
			}
		}
		// Entity damage
		List<EntityLivingBase> collided = world.getEntitiesWithinAABB(EntityLivingBase.class, this.getEntityBoundingBox());

		for(EntityLivingBase entity : collided){

			if(!isValidTarget(entity)) break;
			entity.setFire(6);
		}

	}

	@Override
	public void despawn() {

		if(world.isRemote){

			for(int i = 0; i < 200; i++){
				double x = posX + (rand.nextDouble() - 0.5) * width;
				double y = posY + rand.nextDouble() * height;
				double z = posZ + (rand.nextDouble() - 0.5) * width;
				world.spawnParticle(EnumParticleTypes.BLOCK_DUST, x, y, z, (x - posX) * 0.1,
						(y - posY + height / 2) * 0.1, (z - posZ) * 0.1, Block.getStateId(Blocks.MAGMA.getDefaultState()));
			}

			world.playSound(posX, posY, posZ, WizardrySounds.ENTITY_BOULDER_BREAK_BLOCK, SoundCategory.BLOCKS, 1, 1, false);
		}

		for(int i = 0; i < (int) (10 + lifetime * 0.1); i++){
			EntityEmber ember = new EntityEmber(world, this.getCaster());
			double x = (world.rand.nextDouble() - 0.5) * this.width;
			double y = world.rand.nextDouble() * this.height;
			double z = (world.rand.nextDouble() - 0.5) * this.width;
			ember.setPosition(this.posX + x, this.posY + y, this.posZ + z);
			ember.ticksExisted = world.rand.nextInt(20);
			float speed = 0.2f;
			ember.motionX = x * speed;
			ember.motionY = y * 0.5f * speed;
			ember.motionZ = z * speed;
			world.spawnEntity(ember);
		}
		super.despawn();
	}
}
