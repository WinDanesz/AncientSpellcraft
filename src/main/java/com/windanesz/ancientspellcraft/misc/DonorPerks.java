package com.windanesz.ancientspellcraft.misc;

import com.google.common.collect.ImmutableSet;
import net.minecraft.entity.player.EntityPlayer;

import java.util.UUID;

public class DonorPerks {

	private static final ImmutableSet<UUID> DONOR_UUID_MAP = ImmutableSet.<UUID>builder()
			.add(UUID.fromString("4b29263e-007b-48ef-b3e6-ce86cca989e9"))
			.build();

	public static boolean isDonor(EntityPlayer player){
		return DONOR_UUID_MAP.contains(player.getUniqueID());
	}

	public static boolean isDonor(UUID uuid){
		return DONOR_UUID_MAP.contains(uuid);
	}

}
