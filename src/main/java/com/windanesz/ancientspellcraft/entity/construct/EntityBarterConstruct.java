package com.windanesz.ancientspellcraft.entity.construct;

import com.windanesz.ancientspellcraft.entity.living.EntityWizardMerchant;
import com.windanesz.ancientspellcraft.registry.ASItems;
import com.windanesz.ancientspellcraft.util.ASParticles;
import electroblob.wizardry.entity.construct.EntityMagicConstruct;
import electroblob.wizardry.util.BlockUtils;
import electroblob.wizardry.util.ParticleBuilder;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

public class EntityBarterConstruct extends EntityMagicConstruct {

	public EntityBarterConstruct(World world) {
		super(world);
		this.lifetime = 6000;
	}

	@Override
	public void onUpdate() {
		if (world.isRemote) {
			spawnParticles();
		}

		if (!this.world.isRemote) {

			if (this.world.getTotalWorldTime() % 20L == 0L) {
				this.updateBarter();

			}
			this.lifetime--;

		}
	}

	private void updateBarter() {
		if (ticksExisted < 10) {
			return;
		}

		int i = world.rand.nextInt(Math.max(0, (int) (lifetime * 0.03) - 1));

		if (lifetime <= 0 || i <= 0) {
			if (summonMerchant()) {
				this.setDead();
			} else {
				if (getCaster() != null && !world.isRemote && getCaster() instanceof EntityPlayer) {
					getCaster().sendMessage(new TextComponentTranslation("item.ancientspellcraft:bartering_scroll.will_not_summon"));
				}
				InventoryHelper.spawnItemStack(world, posX, posY, posZ, new ItemStack(ASItems.bartering_scroll));
				this.setDead();
			}
		}
	}

	private boolean summonMerchant() {
		BlockPos spawnPos = BlockUtils.findNearbyFloorSpace(world, this.getPosition(), 15, 5);
		if (spawnPos == null) {
			return false;
		}

		EntityWizardMerchant merchant = new EntityWizardMerchant(world);
		merchant.setPosition(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ());
		merchant.onInitialSpawn(world.getDifficultyForLocation(BlockPos.ORIGIN), null);
		boolean res = world.spawnEntity(merchant);
		if (getCaster() != null && getCaster().getDistance(merchant) < 120) {
			merchant.getNavigator().clearPath();
			merchant.getNavigator().tryMoveToEntityLiving(getCaster(), merchant.getAIMoveSpeed());
		}
		return res;
	}

	private void spawnParticles() {
		Vec3d target = new Vec3d(this.posX, 256, posZ);

		if (world.getTotalWorldTime() % 3 == 0) {
			ParticleBuilder.create(ParticleBuilder.Type.SPARKLE, rand, posX + rand.nextDouble() * 0.5d * (rand.nextBoolean() ? 1 : -1), posY,
					posZ + rand.nextDouble() * 0.5d * (rand.nextBoolean() ? 1 : -1), 0.03, true).vel(0, 0.3, 0).clr(252, 252, 159)
					.time(20 + rand.nextInt(50)).spawn(world);

			ParticleBuilder.create(ParticleBuilder.Type.SPARKLE, rand, posX + rand.nextDouble() * 0.2d * (rand.nextBoolean() ? 1 : -1), posY,
					posZ + rand.nextDouble() * 0.2d * (rand.nextBoolean() ? 1 : -1), 0.03, true).spin(0.7, 0.05).vel(0, 0.3, 0).clr(252, 252, 159)
					.time(20 + rand.nextInt(50)).spawn(world);
		}

		ParticleBuilder.create(ParticleBuilder.Type.FLASH).clr(252, 252, 159).face(EnumFacing.UP).pos(posX, posY + 0.01f, posZ).scale(2f).spawn(world);

		ParticleBuilder.create(ParticleBuilder.Type.FLASH).clr(255, 255, 247).pos(posX, posY + 0.01f, posZ).scale(0.9f).spawn(world);

		ParticleBuilder.create(ASParticles.CONSTANT_BEAM).clr(252, 252, 159).pos(posX, posY, posZ).scale(1.5f).shaded(true).time(1).target(target).spawn(world);

	}
}