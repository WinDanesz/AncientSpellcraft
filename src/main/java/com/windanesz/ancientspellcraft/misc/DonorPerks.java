package com.windanesz.ancientspellcraft.misc;

import com.google.common.collect.ImmutableSet;
import net.minecraft.entity.player.EntityPlayer;

import java.util.UUID;

public class DonorPerks {

	private static final ImmutableSet<UUID> DONOR_UUID_MAP = ImmutableSet.<UUID>builder()
			.add(UUID.fromString("e7ced0cb-c890-4f09-b024-5b1adbf207c0"))
			.build();

	public static boolean isDonor(EntityPlayer player){
		return DONOR_UUID_MAP.contains(player.getUniqueID());
	}

	public static boolean isDonor(UUID uuid){
		return DONOR_UUID_MAP.contains(uuid);
	}

}
