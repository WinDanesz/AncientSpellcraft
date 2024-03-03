package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.registry.ASBlocks;
import com.windanesz.ancientspellcraft.registry.ASDimensions;
import com.windanesz.ancientspellcraft.registry.ASItems;
import com.windanesz.ancientspellcraft.util.SpellTeleporter;
import electroblob.wizardry.data.IStoredVariable;
import electroblob.wizardry.data.Persistence;
import electroblob.wizardry.data.WizardData;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.Location;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.ParticleBuilder.Type;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import java.util.Random;

public class PocketDimension extends Spell {

	public static final IStoredVariable<NBTTagCompound> POCKET_DIM_LOCATION = IStoredVariable.StoredVariable.ofNBT("pocket_dim_location", Persistence.ALWAYS);
	public static final IStoredVariable<NBTTagCompound> POCKET_DIM_PREVIOUS_LOCATION = IStoredVariable.StoredVariable.ofNBT("pocket_dim_previous_location", Persistence.ALWAYS);

	public PocketDimension() {
		super(AncientSpellcraft.MODID, "pocket_dimension", EnumAction.BLOCK, true);
		soundValues(0.8f, 1.2f, 0.2f);
		WizardData.registerStoredVariables(POCKET_DIM_LOCATION, POCKET_DIM_PREVIOUS_LOCATION);
	}

	@Override
	public boolean cast(World world, EntityPlayer caster, EnumHand hand, int ticksInUse, SpellModifiers modifiers) {
		// Only return on the server side or the client probably won't spawn particles
		if (ticksInUse == 0) {
			this.playSound(world, caster, ticksInUse, -1, modifiers);
			if (!world.isRemote) {
				WorldServer destinationWorld = caster.getServer().getWorld(ASDimensions.POCKET_DIM_ID);

				checkOrInitPocketData(caster, destinationWorld);
				return true;
			}
		}

		if (world.isRemote)
			this.spawnParticles(world, caster, modifiers);

		if (ticksInUse == 60 && !world.isRemote) {
			return teleportPlayer(caster);
		}

		//this.playSound(world, caster, ticksInUse, -1, modifiers);
		return true;
	}

	public static boolean teleportPlayer(EntityLivingBase caster) {
		if (caster instanceof EntityPlayer && !caster.world.isRemote) {

			EntityPlayer player = (EntityPlayer) caster;
			WizardData data = WizardData.get(player);

			if (data != null) {

				// teleport player back from the pocket
				if (((EntityPlayer) caster).dimension == ASDimensions.POCKET_DIM_ID) {
					if (data.getVariable(POCKET_DIM_PREVIOUS_LOCATION) != null) {

						Location previousPos = Location.fromNBT(data.getVariable(POCKET_DIM_PREVIOUS_LOCATION));

						SpellTeleporter.teleportEntity(previousPos.dimension, previousPos.pos.getX(), previousPos.pos.getY() + 1, previousPos.pos.getZ(), true, player);
						return true;
					}
				}

				// teleport player to pocket and store old location
				data.setVariable(POCKET_DIM_PREVIOUS_LOCATION, new Location(player.getPosition(), player.dimension).toNBT());
				data.sync();

				NBTTagCompound location = data.getVariable(POCKET_DIM_LOCATION);
				if (location != null) {
					BlockPos pocketPos = NBTUtil.getPosFromTag(location);
					SpellTeleporter.teleportEntity(ASDimensions.POCKET_DIM_ID, pocketPos.getX(), pocketPos.getY(), pocketPos.getZ(), true, player);
					return true;
				}

			}
		}
		return false;
	}

	/**
	 * Spawns buff particles around the caster. Override to add a custom particle effect. Only called client-side.
	 */
	protected void spawnParticles(World world, EntityLivingBase caster, SpellModifiers modifiers) {

		for (int i = 0; i < 10; i++) {
			double dx = (world.rand.nextDouble() * (world.rand.nextBoolean() ? 1 : -1)) * 0.1;
			double dy = (world.rand.nextDouble() * (world.rand.nextBoolean() ? 1 : -1)) * 0.1;
			double dz = (world.rand.nextDouble() * (world.rand.nextBoolean() ? 1 : -1)) * 0.1;

			ParticleBuilder.create(Type.SPARKLE)
					.entity(caster)
					.clr(50, 168, 72)
					.pos(0, caster.height / 2, 0)
					.vel(dx, dy, dz)
					.spawn(world);
		}
	}

	public static void checkOrInitPocketData(EntityPlayer player, World pocketWorld) {

		WizardData data = WizardData.get(player);
		if (data != null) {

			NBTTagCompound compound = data.getVariable(POCKET_DIM_LOCATION);
			if (compound == null) {

				BlockPos pocketLocation = findSuitablePocketPos(pocketWorld);

				createPocket(pocketLocation, pocketWorld);
				setPocketDimLocation(data, pocketLocation);

			}
		}
	}

	public static void setPocketDimLocation(WizardData data, BlockPos pocketLocation) {
		NBTTagCompound nbt = NBTUtil.createPosTag(pocketLocation.offset(EnumFacing.UP));
		data.setVariable(POCKET_DIM_LOCATION, nbt);
		data.sync();
	}

	public static void createPocket(BlockPos pos, World pocketWorld) {
		int pocketSize = 6;
		int pocketHeight = 11;
		createPlatform(pos, pocketWorld, pocketHeight);
		createWalls(pos, pocketWorld, pocketSize, pocketHeight);
		createPlatform(pos.offset(EnumFacing.UP, pocketHeight + 1), pocketWorld, pocketSize); // roof
		pocketWorld.setBlockState(pos, ASBlocks.DIMENSION_FOCUS.getDefaultState());
	}

	public static BlockPos findSuitablePocketPos(World pocketWorld) {
		BlockPos currPos = new BlockPos(0, 1, 0);

		while (!(isSuitablePosition(currPos, pocketWorld))) {
			currPos = getRandomLocationWithOffset(currPos);
		}
		return currPos;
	}

	private static boolean isSuitablePosition(BlockPos pos, World pocketWorld) {
		boolean isFree = true;
		for (BlockPos currPos : BlockPos.getAllInBox(
				pos.offset(EnumFacing.WEST, 250).offset(EnumFacing.SOUTH, 250).offset(EnumFacing.DOWN),
				pos.offset(EnumFacing.EAST, 250).offset(EnumFacing.NORTH, 250).offset(EnumFacing.UP))) {

			if (!(pocketWorld.isAirBlock(currPos))) {
				isFree = false;
				break;
			}
		}
		return isFree;
	}

	private static BlockPos getRandomLocationWithOffset(BlockPos origPos) {
		Random rand = new Random();

		int i = rand.nextBoolean() ? 1 : -1;

		EnumFacing facing = rand.nextBoolean() ? EnumFacing.NORTH : rand.nextBoolean() ? EnumFacing.SOUTH : rand.nextBoolean() ? EnumFacing.EAST : EnumFacing.WEST;
		return origPos.offset(facing, 300 * i);
	}

	private static void createPlatform(BlockPos pos, World pocketWorld, int size) {
		for (BlockPos currPos : BlockPos.getAllInBox(
				pos.offset(EnumFacing.SOUTH, size).offset(EnumFacing.WEST, size),
				pos.offset(EnumFacing.NORTH, size).offset(EnumFacing.EAST, size))) {
			pocketWorld.setBlockState(currPos, ASBlocks.DIMENSION_BOUNDARY.getDefaultState());
		}

	}

	private static void createWalls(BlockPos center, World pocketWorld, int width, int height) {
		for (BlockPos currPos : BlockPos.getAllInBox(
				center.offset(EnumFacing.SOUTH, width).offset(EnumFacing.WEST, width),
				center.offset(EnumFacing.NORTH, width).offset(EnumFacing.WEST, width).offset(EnumFacing.UP, height))) {
			pocketWorld.setBlockState(currPos, ASBlocks.DIMENSION_BOUNDARY.getDefaultState());
		}

		for (BlockPos currPos : BlockPos.getAllInBox(
				center.offset(EnumFacing.SOUTH, width).offset(EnumFacing.EAST, width),
				center.offset(EnumFacing.NORTH, width).offset(EnumFacing.EAST, width).offset(EnumFacing.UP, height))) {
			pocketWorld.setBlockState(currPos, ASBlocks.DIMENSION_BOUNDARY.getDefaultState());
		}

		for (BlockPos currPos : BlockPos.getAllInBox(
				center.offset(EnumFacing.SOUTH, width).offset(EnumFacing.EAST, -width),
				center.offset(EnumFacing.SOUTH, width).offset(EnumFacing.EAST, width).offset(EnumFacing.UP, height))) {
			pocketWorld.setBlockState(currPos, ASBlocks.DIMENSION_BOUNDARY.getDefaultState());
		}

		for (BlockPos currPos : BlockPos.getAllInBox(
				center.offset(EnumFacing.NORTH, width).offset(EnumFacing.WEST, -width),
				center.offset(EnumFacing.NORTH, width).offset(EnumFacing.WEST, width).offset(EnumFacing.UP, height))) {
			pocketWorld.setBlockState(currPos, ASBlocks.DIMENSION_BOUNDARY.getDefaultState());
		}
	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == ASItems.ancient_spellcraft_spell_book || item == ASItems.ancient_spellcraft_scroll;
	}
}
