package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.entity.construct.EntityTransportationPortal;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftItems;
import electroblob.wizardry.data.WizardData;
import electroblob.wizardry.item.ItemArtefact;
import electroblob.wizardry.registry.WizardryBlocks;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.spell.SpellConstruct;
import electroblob.wizardry.spell.SpellConstructRanged;
import electroblob.wizardry.spell.Transportation;
import electroblob.wizardry.util.GeometryUtils;
import electroblob.wizardry.util.Location;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * based on electroblob\wizardry\spell\Transportation.java author: Electroblob
 */
public class TransportationPortal extends SpellConstructRanged<EntityTransportationPortal> {

	public TransportationPortal(String modID, String name, EnumAction action, boolean isContinuous) {
		super(modID, name, EntityTransportationPortal::new, false);
		this.soundValues(1, 1.1f, 0.1f);
		this.floor(true);
		this.overlap(false);
		addProperties(EFFECT_DURATION);
	}

	@Override
	public boolean requiresPacket() {
		return false;
	}

	@Override
	public boolean canBeCastBy(TileEntityDispenser dispenser) { return false; }

	@Override
	public boolean cast(World world, EntityPlayer caster, EnumHand hand, int ticksInUse, SpellModifiers modifiers) {
		if (!ItemArtefact.isArtefactActive(caster, WizardryItems.charm_transportation)) {
			return super.cast(world, caster, hand, ticksInUse, modifiers);
		}

		if (!hasTargetDestination(caster, modifiers)) {
			return false;
		}

		double range = getProperty(RANGE).doubleValue() * modifiers.get(WizardryItems.range_upgrade);
		if (!world.isRemote) {
			Vec3d look = caster.getLookVec();

			double x = caster.posX + look.x * range;
			double y = caster.getEntityBoundingBox().minY;
			double z = caster.posZ + look.z * range;
			if (!world.isAirBlock(new BlockPos(x, y, z)) || !world.isAirBlock(new BlockPos(caster.posX + look.x * range / 2, y, caster.posZ + look.z * range / 2))) {
				x = caster.posX;
				z = caster.posZ;
			}

			if (!spawnConstruct(world, x, y, z, EnumFacing.UP, caster, modifiers))
				return false;
		}

		caster.swingArm(hand);
		this.playSound(world, caster, ticksInUse, -1, modifiers);
		return true;
	}

	@Override
	public boolean cast(World world, EntityLiving caster, EnumHand hand, int ticksInUse, EntityLivingBase target, SpellModifiers modifiers) {
		return false;
	}

	@Override
	public boolean cast(World world, double x, double y, double z, EnumFacing direction, int ticksInUse, int duration, SpellModifiers modifiers) {
		//		dispenser casting or command?
		return false;
	}

	//		private boolean hasTargetDestination(EntityLivingBase entityLivingBaseCaster, SpellModifiers modifiers) {
	//			if (entityLivingBaseCaster instanceof EntityPlayer) {
	//				EntityPlayer caster = (EntityPlayer) entityLivingBaseCaster;
	//				WizardData data = WizardData.get(caster);
	//
	//				// Only works when the caster is in the same dimension.
	//				if (data != null) {
	//
	//					List<Location> locations = data.getVariable(Transportation.LOCATION_KEY);
	//
	//					if (locations != null && locations.isEmpty()) {
	//						if (!caster.world.isRemote)
	//							caster.sendStatusMessage(new TextComponentTranslation("spell.ebwizardry:transportation.undefined"), true);
	//						return false;
	//					}
	//
	//					Location destination = locations.get(locations.size() - 1); // The most recent one, or the only one
	//
	//					if (!ItemArtefact.isArtefactActive(caster, AncientSpellcraftItems.charm_rift_bottle) && destination.dimension != caster.dimension) {
	//						caster.sendStatusMessage(new TextComponentTranslation("spell.ebwizardry:transportation.wrongdimension"), true);
	//						return false;
	//					}
	//
	//					if (!testForCircleInDim(caster, destination)) {
	//						if (!caster.world.isRemote)
	//							caster.sendStatusMessage(new TextComponentTranslation("spell.ebwizardry:transportation.missing"), true);
	//						return false;
	//					}
	//					return true;
	//				}
	//			}
	//			return false;
	//
	//		}

	private boolean hasTargetDestination(EntityLivingBase entityLivingBaseCaster, SpellModifiers modifiers) {
		if (entityLivingBaseCaster instanceof EntityPlayer) {
			EntityPlayer caster = (EntityPlayer) entityLivingBaseCaster;
			WizardData data = WizardData.get(caster);

			// Only works when the caster is in the same dimension.
			if (data != null) {
				List<Location> locations = data.getVariable(Transportation.LOCATIONS_KEY);

				if (locations != null && locations.isEmpty()) {
					if (!caster.world.isRemote)
						caster.sendStatusMessage(new TextComponentTranslation("spell.ebwizardry:transportation.undefined"), true);
					return false;
				}

				if (ItemArtefact.isArtefactActive(caster, WizardryItems.charm_transportation)) {

					if (!ItemArtefact.isArtefactActive(caster, AncientSpellcraftItems.charm_rift_bottle)) {
						List<Location> locationsInDimension = locations.stream().filter(l -> l.dimension == caster.dimension).collect(Collectors.toList());
						if (locationsInDimension.isEmpty()) {
							if (!caster.world.isRemote)
								caster.sendStatusMessage(new TextComponentTranslation("spell." + this.getUnlocalisedName() + ".wrongdimension"), true);
							return false;
						}
					}

					Location destination = getLocationAimedAt(caster, locations, 1);

					if (destination == null)
						return false;
				} else {
					Location destination = locations.get(locations.size() - 1); // The most recent one, or the only one

					if (!ItemArtefact.isArtefactActive(caster, AncientSpellcraftItems.charm_rift_bottle) && destination.dimension != caster.dimension) {
						caster.sendStatusMessage(new TextComponentTranslation("spell.ebwizardry:transportation.wrongdimension"), true);
						return false;
					}

					if (!testForCircleInDim(caster, destination)) {
						if (!caster.world.isRemote)
							caster.sendStatusMessage(new TextComponentTranslation("spell.ebwizardry:transportation.missing"), true);
						return false;
					}
					return true;
				}
			}
		}
		return false;

	}

	// Author: Electroblob
	// The following four methods centralise and neaten up the code
	// Since the deviation angle is also used by the UI renderer, this also ensures they use the same calculation

	/**
	 * Returns the location from the given list that the give player is aiming at, or null if they are not aiming
	 * at any of them.
	 */
	public static Location getLocationAimedAt(EntityPlayer player, List<Location> locations, float partialTicks) {
		return locations.stream()
				.filter(l -> isLocationAimedAt(player, l.pos, partialTicks))
				.min(Comparator.comparingDouble(l -> getLookDeviationAngle(player, l.pos, partialTicks)))
				.orElse(null);
	}

	public static boolean isLocationAimedAt(EntityPlayer player, BlockPos pos, float partialTicks) {
		Vec3d origin = player.getPositionEyes(partialTicks);
		Vec3d centre = GeometryUtils.getCentre(pos);
		Vec3d direction = centre.subtract(origin);
		double distance = direction.length();

		return getLookDeviationAngle(player, pos, partialTicks) < getIconSize(distance);
	}

	public static double getLookDeviationAngle(EntityPlayer player, BlockPos pos, float partialTicks) {

		Vec3d origin = player.getPositionEyes(partialTicks);
		Vec3d look = player.getLook(partialTicks);
		Vec3d centre = GeometryUtils.getCentre(pos);
		Vec3d direction = centre.subtract(origin);
		double distance = direction.length();

		return Math.acos(direction.dotProduct(look) / distance); // Angle between a and b = acos((a.b) / (|a|*|b|))
	}

	public static double getIconSize(double distance) {
		return 0.05 + 2 / (distance + 5);
	}

	private Location getStoneCircleLocation(EntityPlayer caster) {
		WizardData data = WizardData.get(caster);
		List<Location> locations = data.getVariable(Transportation.LOCATIONS_KEY);

		if (ItemArtefact.isArtefactActive(caster, WizardryItems.charm_transportation)) {
			if (ItemArtefact.isArtefactActive(caster, AncientSpellcraftItems.charm_rift_bottle)) {
				Location destination = getLocationAimedAt(caster, locations, 1);
				return destination;
			} else {
				List<Location> locationsInDimension = locations.stream().filter(l -> l.dimension == caster.dimension).collect(Collectors.toList());
				Location destination = getLocationAimedAt(caster, locationsInDimension, 1);
				return destination;
			}
		} else {

			Location destination = locations.get(locations.size() - 1); // The most recent one, or the only one
			return destination;
		}
	}

	protected boolean spawnConstruct(World world, double x, double y, double z,
			@Nullable EnumFacing side, @Nullable EntityLivingBase caster, SpellModifiers modifiers) {
		try {
			if (getStoneCircleLocation((EntityPlayer) caster).pos == null) {
				return false;
			}
		}
		catch (Exception e) {
			return false;
		}
		Location loc = getStoneCircleLocation((EntityPlayer) caster);
		if (loc.dimension != caster.dimension && !ItemArtefact.isArtefactActive(((EntityPlayer) caster), AncientSpellcraftItems.charm_rift_bottle)) {
			if (!world.isRemote)
				((EntityPlayer) caster).sendStatusMessage(new TextComponentTranslation("spell.ebwizardry:transportation.wrongdimension"), true);
			return false;
		}

		return super.spawnConstruct(world, x, y, z, side, caster, modifiers);
	}

	/**
	 * Called just before each construct is spawned. Does nothing by default, but is provided to allow subclasses to call
	 * extra methods on the spawned entity. This method is only called server-side so cannot be used to spawn particles
	 * directly.
	 *
	 * @param construct The entity being spawned.
	 * @param side      The side of a block that was hit, or null if the construct is being spawned in mid-air (only happens
	 *                  if {@link SpellConstruct#requiresFloor} is true).
	 * @param caster    The caster of this spell, or null if it was cast by a dispenser.
	 * @param modifiers The modifiers this spell was cast with.
	 */
	@Override
	protected void addConstructExtras(EntityTransportationPortal construct, EnumFacing side,
			@Nullable EntityLivingBase caster, SpellModifiers modifiers) {
		try {
			if (getStoneCircleLocation((EntityPlayer) caster).pos != null) {
				construct.setTargetDim(getStoneCircleLocation((EntityPlayer) caster).dimension);
				construct.setTargetPos(getStoneCircleLocation((EntityPlayer) caster).pos);
			}
		}
		catch (Exception e) {
			AncientSpellcraft.logger.error("An error occurred during setting the target location of the Transportation Portal:");
			e.printStackTrace();
		}
	}

	/**
	 * Returns whether the specified location at a given dimension is surrounded by a complete cicle of 8 transportation stones.
	 */
	public static boolean testForCircleInDim(EntityLivingBase caster, Location location) {

		MinecraftServer server = caster.getEntityWorld().getMinecraftServer();
		if (server != null) {
			WorldServer worldServer = server.getWorld(location.dimension);

			if (worldServer.getBlockState(location.pos).getMaterial().blocksMovement()) {
				return false;
			}

			for (int x = -1; x <= 1; x++) {
				for (int z = -1; z <= 1; z++) {
					if (x == 0 && z == 0)
						continue;
					if (worldServer.getBlockState(location.pos.add(x, 0, z)).getBlock() != WizardryBlocks.transportation_stone) {
						return false;
					}
				}
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == AncientSpellcraftItems.ancient_spellcraft_spell_book || item == AncientSpellcraftItems.ancient_spellcraft_scroll;
	}
}




