package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.entity.construct.EntityTransportationPortal;
import com.windanesz.ancientspellcraft.registry.ASItems;
import electroblob.wizardry.block.BlockTransportationStone;
import electroblob.wizardry.data.WizardData;
import electroblob.wizardry.item.ItemArtefact;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.spell.SpellConstructRanged;
import electroblob.wizardry.spell.Transportation;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.Location;
import electroblob.wizardry.util.RayTracer;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Based on {@link electroblob.wizardry.spell.Transportation} - author: Electroblob
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

	@SuppressWarnings("Duplicates")
	@Override
	public boolean cast(World world, EntityPlayer caster, EnumHand hand, int ticksInUse, SpellModifiers modifiers) {

		double range = getProperty(RANGE).doubleValue() * modifiers.get(WizardryItems.range_upgrade);
		RayTraceResult rayTrace = RayTracer.standardBlockRayTrace(world, caster, range, hitLiquids, ignoreUncollidables, false);

		if(rayTrace != null &&  (rayTrace.sideHit == EnumFacing.UP )){
			if(!world.isRemote){
				double x = rayTrace.hitVec.x;
				double y = rayTrace.hitVec.y;
				double z = rayTrace.hitVec.z;
				List<EntityTransportationPortal> portals = EntityUtils.getEntitiesWithinRadius(1.5d, x, y, z, world, EntityTransportationPortal.class);

				if (!portals.isEmpty()) {
					for (EntityTransportationPortal portal : portals) {
						if (portal.getCaster() == caster) {
							portal.setDead();
							return true;
						}
					}
				}
			}
		}

		WizardData data = WizardData.get(caster);
		// Fixes the sound not playing in first person.
		//		if(world.isRemote) this.playSound(world, caster, ticksInUse, -1, modifiers);

		// Only works when the caster is in the same dimension.
		if (data != null) {

			List<Location> locations = data.getVariable(Transportation.LOCATIONS_KEY);

			if (locations == null)
				data.setVariable(Transportation.LOCATIONS_KEY, locations = new ArrayList<>(Transportation.MAX_REMEMBERED_LOCATIONS));

			if (locations.isEmpty()) {
				if (!world.isRemote)
					caster.sendStatusMessage(new TextComponentTranslation("spell.ebwizardry:transportation.undefined"), true);
				return false;
			}

			if (!ItemArtefact.isArtefactActive(caster, ASItems.charm_rift_bottle)) {
			// no Rift Bottle - current dim only

				if (ItemArtefact.isArtefactActive(caster, WizardryItems.charm_transportation)) {
					// with Ancient Compass - render overlay to pick location

					List<Location> locationsInDimension = locations.stream().filter(l -> l.dimension == caster.dimension).collect(Collectors.toList());

					if (locationsInDimension.isEmpty()) {
						if (!world.isRemote)
							caster.sendStatusMessage(new TextComponentTranslation("spell.ebwizardry:transportation.wrongdimension"), true);
						return false;
					}

					Location destination = Transportation.getLocationAimedAt(caster, locationsInDimension, 1);

					if (destination == null)
						return false; // None of them were aimed at

					return spawnPortal(world, caster.posX, caster.posY, caster.posZ, EnumFacing.UP, caster, modifiers, destination);

				} else {
					// no Ancient Compass - teleport to last circle only
					Location destination = locations.get(locations.size() - 1); // The most recent one, or the only one

					if (destination.dimension == caster.dimension) {
						return spawnPortal(world, caster.posX, caster.posY, caster.posZ, EnumFacing.UP, caster, modifiers, destination);

					} else {
						if (!world.isRemote)
							caster.sendStatusMessage(new TextComponentTranslation("spell.ebwizardry:transportation.wrongdimension"), true);
					}
				}
			} else {
				// with Rift Bottle - any dim

				if (ItemArtefact.isArtefactActive(caster, WizardryItems.charm_transportation)) {
					// with Ancient Compass - render overlay to pick location

					List<Location> locationsInDimension = new ArrayList<>(locations);
					Location destination = Transportation.getLocationAimedAt(caster, locationsInDimension, 1);

					if (destination == null)
						return false; // None of them were aimed at

					return spawnPortal(world, caster.posX, caster.posY, caster.posZ, EnumFacing.UP, caster, modifiers, destination);
				} else {
					// no Ancient Compass - teleport to last circle only
					Location destination = locations.get(locations.size() - 1); // The most recent one, or the only one

					return spawnPortal(world, caster.posX, caster.posY, caster.posZ, EnumFacing.UP, caster, modifiers, destination);
				}

			}
			return false;
		}

		return false;
	}

	@Override
	public boolean cast(World world, EntityLiving caster, EnumHand hand, int ticksInUse, EntityLivingBase target, SpellModifiers modifiers) {
		return false;
	}

	@Override
	public boolean cast(World world, double x, double y, double z, EnumFacing direction, int ticksInUse, int duration, SpellModifiers modifiers) {
		return false;
	}

	public boolean spawnPortal(World world, double x, double y, double z,
			@Nullable EnumFacing side, @Nullable EntityLivingBase caster, SpellModifiers modifiers, Location location) {

		if (location.pos.distanceSq(x, y, z) < 5) {
			if (!world.isRemote && caster instanceof EntityPlayer)
				((EntityPlayer) caster).sendStatusMessage(new TextComponentTranslation("spell.ancientspellcraft:transportation_portal.too_close"), true);
			return false;
		}

		// only checks for stone circle in the current dimension
		if (caster instanceof EntityPlayer && (!ItemArtefact.isArtefactActive((EntityPlayer) caster, ASItems.charm_rift_bottle) || caster.dimension == location.dimension)) {
			if (!BlockTransportationStone.testForCircle(world, location.pos)) {
				if (!world.isRemote)
					((EntityPlayer) caster).sendStatusMessage(new TextComponentTranslation("spell.ebwizardry:transportation.missing"), true);
				return false;
			}
		}

		// based on electroblob.wizardry.spell.SpellConstruct.spawnConstruct
		if (!world.isRemote) {
			// Creates a new construct using the supplied factory
			EntityTransportationPortal construct = constructFactory.apply(world);
			// Sets the position of the construct (and initialises its bounding box)
			construct.setPosition(x, y, z);
			// Sets the various parameters
			construct.setCaster(caster);

			construct.lifetime = (caster instanceof EntityPlayer && ItemArtefact.isArtefactActive((EntityPlayer) caster, ASItems.head_riftstone) ? -1 :
					(int) (getProperty(DURATION).floatValue() * modifiers.get(WizardryItems.duration_upgrade)));

			construct.setTargetDim(location.dimension);
			construct.setTargetPos(location.pos);
			construct.setParticleColors();

			// Prevents overlapping of multiple constructs of the same type. Since we have an instance here this is
			// very simple. The trade-off is that we have to create the entity before the spell fails, but unless
			// world.spawnEntity(...) is called, its scope is limited to this method so it should be fine.
			// Needs to be last in case addConstructExtras modifies the bounding box
			if (!world.getEntitiesWithinAABB(construct.getClass(), construct.getEntityBoundingBox()).isEmpty())
				return false;
			// Spawns the construct in the world
			world.spawnEntity(construct);
			this.playSound(world, caster, 0, -1, modifiers);
		}

		return true;
	}



	@Override
	public boolean applicableForItem(Item item) {
		return item == ASItems.ancient_spellcraft_spell_book || item == ASItems.ancient_spellcraft_scroll;
	}
}




