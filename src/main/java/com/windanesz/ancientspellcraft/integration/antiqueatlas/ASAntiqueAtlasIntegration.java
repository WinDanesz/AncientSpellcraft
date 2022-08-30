package com.windanesz.ancientspellcraft.integration.antiqueatlas;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.Settings;
import electroblob.wizardry.integration.antiqueatlas.WizardryAntiqueAtlasIntegration;
import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.api.AtlasAPI;
import hunternif.mc.atlas.marker.GlobalMarkersData;
import hunternif.mc.atlas.marker.Marker;
import hunternif.mc.atlas.registry.MarkerType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * This class is pretty much the same as {@link WizardryAntiqueAtlasIntegration}, adapted for Ancient Spellcraft
 * This class handles all of Ancient Spellcraft's integration with the <i>Antique Atlas</i> mod. This class contains only the code
 * that requires Antique Atlas to be loaded in order to run. Conversely, all code that requires Antique Atlas to be
 * loaded is located within this class or another class in the package {@code com.windanesz.ancientspellcraft.integration.antiqueatlas}.
 *
 * @since AS 1.3.4
 * @author Electroblob, WinDanesz
 */
@Mod.EventBusSubscriber
public class ASAntiqueAtlasIntegration {

	public static final String ANTIQUE_ATLAS_MOD_ID = "antiqueatlas";

	private static final ResourceLocation BATTLEMAGE_CAMP_MARKER = new ResourceLocation(AncientSpellcraft.MODID, "battlemage_camp");
	private static final ResourceLocation BATTLEMAGE_KEEP_MARKER = new ResourceLocation(AncientSpellcraft.MODID, "battlemage_keep");
	private static final ResourceLocation SAGE_HILL_MARKER = new ResourceLocation(AncientSpellcraft.MODID, "sage_hill");
	private static final ResourceLocation ANCIENT_VAULT_MARKER = new ResourceLocation(AncientSpellcraft.MODID, "ancient_vault");

	private static boolean antiqueAtlasLoaded;

	public static void init(){
		antiqueAtlasLoaded = Loader.isModLoaded(ANTIQUE_ATLAS_MOD_ID);
		AncientSpellcraft.proxy.registerAtlasMarkers(); // Needs routing through the proxies to make sure it's only client-side
	}

	public static boolean enabled(){
		return Settings.generalSettings.antique_atlas_integration && antiqueAtlasLoaded;
	}

	/** Places a global battlemage camp marker in all antique atlases at the given coordinates in the given world if
	 * {@link electroblob.wizardry.Settings#autoTowerMarkers} is enabled. Server side only! */
	public static void markBattlemageCamp(World world, int x, int z){
		if(enabled() && Settings.generalSettings.auto_battlemage_camp_markers){
			AtlasAPI.getMarkerAPI().putGlobalMarker(world, false, BATTLEMAGE_CAMP_MARKER.toString(), "integration.antiqueatlas.marker." + BATTLEMAGE_CAMP_MARKER.toString().replace(':', '.'), x, z);
		}
	}

	/** Places a global battlemage camp marker in all antique atlases at the given coordinates in the given world if
	 * {@link electroblob.wizardry.Settings#autoTowerMarkers} is enabled. Server side only! */
	public static void markBattlemageKeep(World world, int x, int z){
		if(enabled() && Settings.generalSettings.auto_battlemage_keep_markers){
			AtlasAPI.getMarkerAPI().putGlobalMarker(world, false, BATTLEMAGE_KEEP_MARKER.toString(), "integration.antiqueatlas.marker." + BATTLEMAGE_KEEP_MARKER.toString().replace(':', '.'), x, z);
		}
	}

	/** Places a global sage hill marker in all antique atlases at the given coordinates in the given world if
	 * {@link electroblob.wizardry.Settings#autoTowerMarkers} is enabled. Server side only! */
	public static void markSageHill(World world, int x, int z){
		if(enabled() && Settings.generalSettings.auto_battlemage_keep_markers){
			AtlasAPI.getMarkerAPI().putGlobalMarker(world, false, SAGE_HILL_MARKER.toString(), "integration.antiqueatlas.marker." + SAGE_HILL_MARKER.toString().replace(':', '.'), x, z);
		}
	}

	public static void markMysteryStructure(World world, int x, int z){
		if(enabled() && Settings.generalSettings.ancient_vault_markers){
			AtlasAPI.getMarkerAPI().putGlobalMarker(world, false, ANCIENT_VAULT_MARKER.toString(), "integration.antiqueatlas.marker." + ANCIENT_VAULT_MARKER.toString().replace(':', '.'), x, z);
		}
	}

	/** Registers the marker icons with Antique Atlas. Client side only! */
	public static void registerMarkers(){

		if(!enabled()) return;

		AtlasAPI.getMarkerAPI().registerMarker(new MarkerType(BATTLEMAGE_CAMP_MARKER, new ResourceLocation(AncientSpellcraft.MODID, "textures/integration/antiqueatlas/battlemage_camp.png")));
		AtlasAPI.getMarkerAPI().registerMarker(new MarkerType(BATTLEMAGE_KEEP_MARKER, new ResourceLocation(AncientSpellcraft.MODID, "textures/integration/antiqueatlas/battlemage_keep.png")));
		AtlasAPI.getMarkerAPI().registerMarker(new MarkerType(SAGE_HILL_MARKER, new ResourceLocation(AncientSpellcraft.MODID, "textures/integration/antiqueatlas/sage_hill.png")));
		AtlasAPI.getMarkerAPI().registerMarker(new MarkerType(ANCIENT_VAULT_MARKER, new ResourceLocation(AncientSpellcraft.MODID, "textures/integration/antiqueatlas/ancient_vault.png")));

	}

	@SubscribeEvent
	public static void onWorldLoadEvent(WorldEvent.Load event){

		if(!enabled()) return;

		// Backwards compatibility for existing markers using the old translation key format (with colons)
		GlobalMarkersData data = AntiqueAtlasMod.globalMarkersData.getData();
		for(Marker marker : data.getMarkersInDimension(event.getWorld().provider.getDimension())){
			if(marker.getLabel().contains(":")){
				// Remove old-format markers and replace them with new ones
				data.removeMarker(marker.getId());
				AtlasAPI.getMarkerAPI().putGlobalMarker(event.getWorld(), marker.isVisibleAhead(), marker.getType(),
						marker.getLabel().replace(':', '.'), marker.getX(), marker.getZ());
			}
		}
	}

}
