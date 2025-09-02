package com.windanesz.ancientspellcraft.mixin.modrefs;

import com.windanesz.ancientspellcraft.Settings;
import com.windanesz.ancientspellcraft.registry.ASItems;
import electroblob.wizardry.item.ItemArtefact;
import electroblob.wizardry.util.AllyDesignationSystem;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.ParticleBuilder;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

public class HandleVanillaPotionMixins {

	public static void diffuser(ItemStack stack, World world, EntityLivingBase entityLiving) {
		if (entityLiving instanceof EntityPlayer) {

			EntityPlayer player = (EntityPlayer) entityLiving;
			if (ItemArtefact.isArtefactActive(player, ASItems.charm_arcane_diffuser)) {
				List<EntityLivingBase> entities = EntityUtils.getEntitiesWithinRadius(7, player.posX, player.posY, player.posZ, world, EntityLivingBase.class);
				entities.removeIf(e -> !AllyDesignationSystem.isAllied(player, e));
				entities.removeIf(e -> e == player);

				for (PotionEffect potioneffect : PotionUtils.getEffectsFromStack(stack)) {
					for (EntityLivingBase entity : entities) {
						if (potioneffect.getPotion().isInstant()) {
							potioneffect.getPotion().affectEntity(entity, player, entity, potioneffect.getAmplifier(), 0.3D);
						} else {
							entity.addPotionEffect(new PotionEffect(potioneffect.getPotion(), (int) (potioneffect.getDuration() * 0.3f), potioneffect.getAmplifier()));
						}
					}
				}

				if (world.isRemote) {
					Vec3d origin = player.getPositionVector();
					for (int i = 0; (float) i < 40.0F; ++i) {
						double particleX = origin.x - 1.0 + 2.0 * world.rand.nextDouble();
						double particleZ = origin.z - 1.0 + 2.0 * world.rand.nextDouble();
						ParticleBuilder.create(ParticleBuilder.Type.DARK_MAGIC).pos(particleX, origin.y + 0.5, particleZ).vel(particleX - origin.x, 0.0, particleZ - origin.z).clr(0xad73bd).spawn(world);
						particleX = origin.x - 1.0 + 2.0 * world.rand.nextDouble();
						particleZ = origin.z - 1.0 + 2.0 * world.rand.nextDouble();
						ParticleBuilder.create(ParticleBuilder.Type.SPARKLE).pos(particleX, origin.y + 0.5, particleZ).vel(particleX - origin.x, 0.0, particleZ - origin.z).time(30).clr(0xa522c9).spawn(world);
					}
				}
			}
		}
	}

	public static void mixinAurelianCup(World world, EntityLivingBase entityLiving) {
		// Aurelian Cup: Apply Absorption II for configurable duration when drinking potions
		if (entityLiving instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) entityLiving;
			if (ItemArtefact.isArtefactActive(player, ASItems.charm_aurelian_cup)) {
				if (!world.isRemote) {
					int durationTicks = Settings.generalSettings.aurelian_cup_absorption_duration * 20; // Convert seconds to ticks
					player.addPotionEffect(new PotionEffect(MobEffects.ABSORPTION, durationTicks, 1)); // Absorption II
				}
			}
		}
	}
}
