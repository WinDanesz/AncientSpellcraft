package com.windanesz.ancientspellcraft.item;

import com.windanesz.ancientspellcraft.registry.AncientSpellcraftItems;
import electroblob.wizardry.Wizardry;
import electroblob.wizardry.entity.construct.EntityEarthquake;
import electroblob.wizardry.util.BlockUtils;
import electroblob.wizardry.util.EntityUtils;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemAdvancedStoneFist extends ItemStoneFist {

	public ItemAdvancedStoneFist() {
		super();
		this.setMaxDamage(30);
		this.setNoRepair();
		this.setCreativeTab(null);
	}

	/**
	 * Called when a Block is right-clicked with this Item
	 */
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (pos.distanceSq(player.posX, player.posY, player.posZ) < 9) {
			if (facing != EnumFacing.UP) {

				if (!world.isRemote) {
					world.createExplosion(player, pos.getX(), pos.getY(), pos.getZ(), 1.8f, true);
				}
				if (world.isRemote) {
					EntityUtils.getEntitiesWithinRadius(15, pos.getX(), pos.getY(), pos.getZ(), world, EntityPlayer.class)
							.forEach(p -> Wizardry.proxy.shakeScreen(p, 12));
				}
				player.getCooldownTracker().setCooldown(this, 80);
				player.getHeldItem(hand).damageItem(4, player);
				player.swingArm(hand);
				return EnumActionResult.PASS;
			} else if (ItemNewArtefact.isNewArtefactActive(player, AncientSpellcraftItems.belt_stone)) {
				if (!world.isRemote) {
					EntityEarthquake earthquake = new EntityEarthquake(world);
					earthquake.setCaster(player);
					earthquake.setPosition(player.posX, player.posY, player.posZ);
					earthquake.lifetime = (int) (2f);
					world.spawnEntity(earthquake);
					player.getCooldownTracker().setCooldown(this, 100);
					player.getHeldItem(hand).damageItem(8, player);
				} else {
					EntityUtils.getEntitiesWithinRadius(15, pos.getX(), pos.getY(), pos.getZ(), world, EntityPlayer.class)
							.forEach(p -> Wizardry.proxy.shakeScreen(p, 12));

					double particleX, particleZ;

					for (int i = 0; i < 40; i++) {

						particleX = player.posX - 1.0d + 2 * world.rand.nextDouble();
						particleZ = player.posZ - 1.0d + 2 * world.rand.nextDouble();

						IBlockState block = BlockUtils.getBlockEntityIsStandingOn(player);
						world.spawnParticle(EnumParticleTypes.BLOCK_DUST, particleX, player.posY,
								particleZ, particleX - player.posX, 0, particleZ - player.posZ, Block.getStateId(block));
					}
				}
				player.swingArm(hand);
			}
		}
		return EnumActionResult.PASS;
	}
}






































