package com.windanesz.ancientspellcraft.potion;

import com.windanesz.ancientspellcraft.spell.DimensionalAnchor;
import com.windanesz.ancientspellcraft.util.SpellTeleporter;
import electroblob.wizardry.util.BlockUtils;
import electroblob.wizardry.util.Location;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

public class PotionDimensionalAnchor extends PotionMagicEffectAS {
	public PotionDimensionalAnchor(String name, boolean isBadEffect, int liquidColour, ResourceLocation texture) {
		super(name, isBadEffect, liquidColour, texture);
	}

	@Override
	public boolean isReady(int duration, int amplifier) {
		return true;
	}

	@Override
	public void performEffect(EntityLivingBase entitylivingbase, int strength) {

		if (entitylivingbase instanceof EntityPlayer) {
			EntityPlayer player = ((EntityPlayer) entitylivingbase);

			// constantly resetting the nether portal timer prevents using nether portals
			player.timeUntilPortal = 40;

			// store the player location data every 2 seconds
			if (player.ticksExisted % 40 == 0) {
				Location previousLocation = DimensionalAnchor.getPlayerLocationData(player);

				if (previousLocation != null) {

					if (previousLocation.dimension != player.dimension) {
						// player moved dimension, forcing back..

						World oldWorld = DimensionManager.getWorld(previousLocation.dimension);

						if (oldWorld != null || previousLocation.dimension == -1) {
							BlockPos pos = null;

							if (previousLocation.dimension != -1) {
								// Find a random spot to teleport the player to. Can't reuse the exact old position as that might cause a teleportation loop e.g. if we TP the player back into an end portal!
								pos = BlockUtils.findNearbyFloorSpace(oldWorld, previousLocation.pos, 10, 10);

								// Worth a try.. this could happen if the player switched dimension while e.g. in water or midair.
								if (pos == null && oldWorld.isAirBlock(previousLocation.pos)) {
									pos = previousLocation.pos;
								}
							} else {
								pos = previousLocation.pos;
							}

							if (pos != null) {
								SpellTeleporter.teleportEntity(previousLocation.dimension, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, true, player);
								player.sendStatusMessage(new TextComponentTranslation("spell.ancientspellcraft:dimensional_anchor.forced_back_to_dimension"), false);
								return;
							}
						}
					}
				}

				DimensionalAnchor.storePlayerLocationData(player);
			}
		}
	}

	@Override
	public void removeAttributesModifiersFromEntity(EntityLivingBase entityLivingBaseIn, AbstractAttributeMap attributeMapIn, int amplifier) {
		if (entityLivingBaseIn instanceof EntityPlayer) {
			DimensionalAnchor.purgeLocationData((EntityPlayer) entityLivingBaseIn);
		}

		super.removeAttributesModifiersFromEntity(entityLivingBaseIn, attributeMapIn, amplifier);
	}
}


