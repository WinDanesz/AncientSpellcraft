package com.windanesz.ancientspellcraft.worldgen;

import com.windanesz.ancientspellcraft.entity.living.EntityClassWizard;
import com.windanesz.ancientspellcraft.entity.living.EntityEvilClassWizard;
import com.windanesz.ancientspellcraft.entity.living.EntitySkeletonMage;
import com.windanesz.ancientspellcraft.entity.living.EntityStoneGuardian;
import electroblob.wizardry.Wizardry;
import electroblob.wizardry.constants.Element;
import electroblob.wizardry.item.ItemWizardArmour;
import electroblob.wizardry.registry.Spells;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Optional;
import java.util.Set;

public class WorldGenUtils {

	private static final String WIZARD_DATA_BLOCK_TAG = "wizard";
	private static final String EVIL_WIZARD_DATA_BLOCK_TAG = "evil_wizard";
	private static final String HORSE_DATA_BLOCK_TAG = "horse";
	private static final String STONE_GUARDIAN = "stone_guardian";
	private static final String SKELETON_MAGE_DATA_BLOCK_TAG = "skeleton_mage";
	private static final String SKELETON_MAGE_GHOST_DATA_BLOCK_TAG = "skeleton_mage_ghost";
	private static final String EVIL_WARLOCK_TAG = "warlock";

	public static void spawnEntityByType(World world, String entityType, ItemWizardArmour.ArmourClass armourClass, BlockPos origin, Vec3d vec, Set<BlockPos> towerBlocks, Element element, boolean isSkeletonGhost) {
        if (world.isRemote) {
            return;
        }
        switch (entityType) {
			case WIZARD_DATA_BLOCK_TAG:
			case EVIL_WARLOCK_TAG:
				spawnClassWizard(world, armourClass, origin, vec, towerBlocks);
				break;
			case EVIL_WIZARD_DATA_BLOCK_TAG:
				spawnEvilClassWizard(world, armourClass, origin, vec);
				break;
			case HORSE_DATA_BLOCK_TAG:
				spawnHorse(world, vec);
				break;
			case STONE_GUARDIAN:
				spawnGuardian(world, origin, vec);
				break;
			case SKELETON_MAGE_DATA_BLOCK_TAG:
			case SKELETON_MAGE_GHOST_DATA_BLOCK_TAG:
				spawnSkeletonMage(world, origin, vec, element, isSkeletonGhost);
				break;
			default:
				// This probably shouldn't happen...
				Wizardry.logger.info("Unrecognised data block value {} in structure", entityType);
				break;
		}
	}

	public static void spawnClassWizard(World world, ItemWizardArmour.ArmourClass armourClass, BlockPos origin, Vec3d vec, Set<BlockPos> towerBlocks) {
		EntityClassWizard wizard = new EntityClassWizard(world);
		wizard.setLocationAndAngles(vec.x, vec.y, vec.z, 0, 0);
		wizard.setArmourClass(armourClass);
		wizard.onInitialSpawn(world.getDifficultyForLocation(origin), null);
		wizard.setTowerBlocks(towerBlocks);
		world.spawnEntity(wizard);
	}

	public static void spawnEvilClassWizard(World world, ItemWizardArmour.ArmourClass armourClass, BlockPos origin, Vec3d vec) {
		EntityEvilClassWizard wizard = new EntityEvilClassWizard(world);
		wizard.setLocationAndAngles(vec.x, vec.y, vec.z, 0, 0);
		wizard.hasStructure = true; // Stops it despawning
		wizard.setArmourClass(armourClass);
		wizard.onInitialSpawn(world.getDifficultyForLocation(origin), null);
		world.spawnEntity(wizard);
	}

	public static void spawnHorse(World world, Vec3d vec) {
		EntityHorse horse = new EntityHorse(world);
		horse.setLocationAndAngles(vec.x, vec.y, vec.z, 0, 0);
		world.spawnEntity(horse);
	}

	public static void spawnGuardian(World world, BlockPos origin, Vec3d vec) {
		EntityStoneGuardian guardian = new EntityStoneGuardian(world);
		guardian.setLocationAndAngles(vec.x, vec.y, vec.z, 0, 0);
		guardian.onInitialSpawn(world.getDifficultyForLocation(origin), null);
		guardian.setSeal(Optional.of(new BlockPos(vec.x, vec.y - 1, vec.z)));
		guardian.addPotionEffect(new PotionEffect(MobEffects.INVISIBILITY, 40));
		world.spawnEntity(guardian);
	}

	public static void spawnSkeletonMage(World world, BlockPos origin, Vec3d vec, Element element, boolean ghost) {
		EntitySkeletonMage skeleton = new EntitySkeletonMage(world);
		skeleton.setElement(element);
		skeleton.setRare(true);
		skeleton.setLocationAndAngles(vec.x, vec.y, vec.z, 0, 0);
		skeleton.onInitialSpawn(world.getDifficultyForLocation(origin), null);
		if (element == Element.HEALING) {
			skeleton.populateSpellList(Element.HEALING, Spells.ray_of_purification);
		}
		world.spawnEntity(skeleton);
	}

}
