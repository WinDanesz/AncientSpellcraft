package com.windanesz.ancientspellcraft.data;

import com.google.common.collect.EvictingQueue;
import electroblob.wizardry.data.IStoredVariable;
import electroblob.wizardry.data.Persistence;
import electroblob.wizardry.data.WizardData;
import electroblob.wizardry.util.NBTExtras;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.UUID;

public class ClassWeaponData {

	public static final int MAX_RECENT_ENEMIES = 10;

	public static final String RECENT_ENEMIES_TAG = "recent_enemies";

	public static final IStoredVariable<List<UUID>> RECENT_ENEMIES = new IStoredVariable.StoredVariable<>(RECENT_ENEMIES_TAG,
			s -> NBTExtras.listToNBT(s, NBTUtil::createUUIDTag),
			// For some reason gradle screams at me unless I explicitly declare the type of t here, despite IntelliJ being fine without it
			(NBTTagList t) -> new ArrayList<>(NBTExtras.NBTToList(t, NBTUtil::getUUIDFromTag)),
			// cleared when the caster dies, but not when they switch dimensions.
			Persistence.DIMENSION_CHANGE);

	private ClassWeaponData() {}

	public static void init() {
		WizardData.registerStoredVariables(RECENT_ENEMIES);
	}

	/**
	 * Adds the given entity to this player's recently-hit entities. Entities can (and will) be added multiple times, and
	 * will be automatically removed when enough entity hits are added after them.
	 *
	 * @param entity The entity to be tracked.
	 */
	public static void trackRecentEnemy(EntityPlayer player, EntityLivingBase entity) {
		if (entity != null) {
			WizardData data = WizardData.get(player);
			Queue<UUID> recentEnemies = getRecentEnemies(data);
			recentEnemies.add(entity.getUniqueID());
			List<UUID> enemyList = new ArrayList<>(recentEnemies);
			data.setVariable(RECENT_ENEMIES, enemyList);

		}
	}

	public static int getRecentHitCount(EntityPlayer player, EntityLivingBase entity) {
		WizardData data = WizardData.get(player);
		Queue<UUID> recentEnemies = getRecentEnemies(data);

		return (int) recentEnemies.stream().filter(e -> e == entity.getUniqueID()).count(); // We know this can't be more than 10
	}

	public static Queue<UUID> getRecentEnemies(WizardData data) {
		if (data.getVariable(RECENT_ENEMIES) == null) {
			// store empty list
			List<UUID> resultSet = new ArrayList<>();
			data.setVariable(RECENT_ENEMIES, resultSet);
			// return an empty queue
			return EvictingQueue.create(MAX_RECENT_ENEMIES);

		} else {
			Queue<UUID> result = EvictingQueue.create(MAX_RECENT_ENEMIES);
			List<UUID> resultList = data.getVariable(RECENT_ENEMIES);

			if (!resultList.isEmpty()) {
				result.addAll(resultList);
			}

			return result;
		}
	}
}
