package com.windanesz.ancientspellcraft.item;

import com.windanesz.ancientspellcraft.registry.ASBlocks;
import com.windanesz.ancientspellcraft.tileentity.TileArcaneWall;
import electroblob.wizardry.Wizardry;
import electroblob.wizardry.util.RayTracer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;


@Mod.EventBusSubscriber
public class ItemUnsealingScroll extends ItemRareScroll {

	public ItemUnsealingScroll() {
		super();
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer caster, EnumHand hand) {
		ItemStack stack = caster.getHeldItem(hand);

		Vec3d look = caster.getLookVec();
		Vec3d origin = new Vec3d(caster.posX, caster.posY + caster.getEyeHeight() - 0.25f, caster.posZ);
		if(world.isRemote && !Wizardry.proxy.isFirstPerson(caster)){
			origin = origin.add(look.scale(1.2));
		}

		double range = 5;
		Vec3d endpoint = origin.add(look.scale(range));

		// Change the filter depending on whether living entities are ignored or not
		RayTraceResult rayTrace = RayTracer.rayTrace(world, origin, endpoint, 0, false,
				false, false, Entity.class, (java.util.function.Predicate<? super Entity>) RayTracer.ignoreEntityFilter(caster));

		if (rayTrace != null && rayTrace.typeOfHit == RayTraceResult.Type.BLOCK) {
			if (world.getBlockState(rayTrace.getBlockPos()).getBlock() == ASBlocks.arcane_wall) {
				TileEntity tile = world.getTileEntity(rayTrace.getBlockPos());
				if (tile instanceof TileArcaneWall && ((TileArcaneWall) tile).isGenerated()) {
					((TileArcaneWall) tile).setBeingDispelled(true);
					if (!caster.isCreative()) {
						consumeScroll(caster, stack);
					}
				}
			}
		}


		return super.onItemRightClick(world, caster, hand);
	}

}
