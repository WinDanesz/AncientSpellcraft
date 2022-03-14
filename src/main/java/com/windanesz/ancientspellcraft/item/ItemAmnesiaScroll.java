package com.windanesz.ancientspellcraft.item;

import com.windanesz.ancientspellcraft.registry.AncientSpellcraftItems;
import electroblob.wizardry.entity.living.EntityWizard;
import electroblob.wizardry.util.ParticleBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class ItemAmnesiaScroll extends ItemRareScroll {

	public ItemAmnesiaScroll() {
		super();
	}

	@SubscribeEvent
	public static void onEntityInteractEvent(PlayerInteractEvent.EntityInteract event) {
		if (event.getTarget() instanceof EntityWizard && event.getItemStack().getItem() == AncientSpellcraftItems.amnesia_scroll) {
			World world = event.getWorld();
			NBTTagCompound entityInNbt = event.getTarget().serializeNBT();
			boolean foundTrades = entityInNbt.hasKey("trades");

			if (event.getWorld().isRemote) {
				Vec3d origin = event.getTarget().getPositionEyes(1);
				for (int i = 0; i < 30; i++) {
					double x = origin.x - 1 + world.rand.nextDouble() * 2;
					double y = origin.y - 0.25 + world.rand.nextDouble() * 0.5;
					double z = origin.z - 1 + world.rand.nextDouble() * 2;
					if (world.rand.nextBoolean()) {
						ParticleBuilder.create(ParticleBuilder.Type.SPARKLE).pos(x, y, z)
								.vel(0, 0.1, 0).fade(0, 0, 0).spin(0.3f, 0.03f)
								.clr(140, 140, 140).spawn(world);
					} else {
						ParticleBuilder.create(ParticleBuilder.Type.SPARKLE).pos(x, y, z)
								.vel(0, 0.1, 0).fade(0, 0, 0).spin(0.3f, 0.03f)
								.clr(99, 1, 110).spawn(world);
					}
				}
			}

			if (foundTrades) {
				entityInNbt.removeTag("trades");
				entityInNbt.setUniqueId("UUID", MathHelper.getRandomUUID(event.getWorld().rand));
				Entity mob = EntityList.createEntityFromNBT(entityInNbt, event.getWorld());

				if (mob != null) {
					if (!event.getWorld().isRemote) {
						event.getWorld().removeEntity(event.getTarget());
						event.getEntityPlayer().setHeldItem(event.getHand(), ItemStack.EMPTY);
						event.getWorld().spawnEntity(mob);
						event.setCanceled(true);
					}
				}
			}
		}
	}
}
