package com.windanesz.ancientspellcraft.registry;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import electroblob.wizardry.Wizardry;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootEntryTable;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Arrays;
import java.util.List;

/**
 * Class responsible for registering Ancient Spellcraft's loot tables. Also handles loot injection.
 * <p>
 * Based on Electroblob's TFSpellPack and Wizardry Loot registry classes
 *
 * @author WinDanesz
 */
@Mod.EventBusSubscriber
public class AncientSpellcraftLoot {

	private static LootTable UNCOMMON_ARTEFACTS;
	private static LootTable RARE_ARTEFACTS;
	private static LootTable EPIC_ARTEFACTS;
	private static LootTable ANCIENTSPELLCRAFT_BOOKS_AND_SCROLLS;
	private static LootTable LIBRARY_RUINS_BOOKSHELF;
	private static LootTable OBELISK;
	private static LootTable SHRINE;
	private static LootTable WIZARD_TOWER;
	private static LootTable WAND_UPGRADES;

	private AncientSpellcraftLoot() {}

	/**
	 * Called from the preInit method in the main mod class to register the custom dungeon loot.
	 */

	public static void preInit() {
		// chest
		LootTableList.register(new ResourceLocation(AncientSpellcraft.MODID, "chests/dungeon_additions"));

		// subsets
		LootTableList.register(new ResourceLocation(AncientSpellcraft.MODID, "subsets/uncommon_artefacts"));
		LootTableList.register(new ResourceLocation(AncientSpellcraft.MODID, "subsets/rare_artefacts"));
		LootTableList.register(new ResourceLocation(AncientSpellcraft.MODID, "subsets/epic_artefacts"));
		LootTableList.register(new ResourceLocation(AncientSpellcraft.MODID, "subsets/ritual_books"));
		LootTableList.register(new ResourceLocation(AncientSpellcraft.MODID, "subsets/wand_upgrades"));

		LootTableList.register(new ResourceLocation(AncientSpellcraft.MODID, "subsets/ancientspellcraft_books_and_scrolls"));


		LootTableList.register(new ResourceLocation(AncientSpellcraft.MODID, "inject/as_library_ruins_bookshelf"));
		LootTableList.register(new ResourceLocation(AncientSpellcraft.MODID, "inject/as_obelisk"));
		LootTableList.register(new ResourceLocation(AncientSpellcraft.MODID, "inject/as_shrine"));
		LootTableList.register(new ResourceLocation(AncientSpellcraft.MODID, "inject/as_wizard_tower"));

		// entities
		LootTableList.register(new ResourceLocation(AncientSpellcraft.MODID, "entities/void_creeper"));
		LootTableList.register(new ResourceLocation(AncientSpellcraft.MODID, "entities/evil_battlemage"));
	}

	@SubscribeEvent
	public static void onLootTableLoadEvent(LootTableLoadEvent event) {
		if (Arrays.asList(AncientSpellcraft.settings.lootInjectionLocations).contains(event.getName())) {
			event.getTable().addPool(getAdditive(AncientSpellcraft.MODID + ":chests/dungeon_additions", AncientSpellcraft.MODID + "_ancientspellcraft_dungeon_additions"));
		}

		// Fortunately the loot tables of Ancient Spellcraft load before wizardry so we can make a static reference to them and reuse it
		if (event.getName().toString().equals(AncientSpellcraft.MODID + ":subsets/uncommon_artefacts")) {
			UNCOMMON_ARTEFACTS = event.getTable();
		} else if (event.getName().toString().equals(AncientSpellcraft.MODID + ":subsets/rare_artefacts")) {
			RARE_ARTEFACTS = event.getTable();
		} else if (event.getName().toString().equals(AncientSpellcraft.MODID + ":subsets/epic_artefacts")) {
			EPIC_ARTEFACTS = event.getTable();
		} else if (event.getName().toString().equals(AncientSpellcraft.MODID + ":inject/as_library_ruins_bookshelf")) {
			LIBRARY_RUINS_BOOKSHELF = event.getTable();
		} else if (event.getName().toString().equals(AncientSpellcraft.MODID + ":inject/as_obelisk")) {
			OBELISK = event.getTable();
		} else if (event.getName().toString().equals(AncientSpellcraft.MODID + ":inject/as_wizard_tower")) {
			WIZARD_TOWER = event.getTable();
		} else if (event.getName().toString().equals(AncientSpellcraft.MODID + ":inject/as_shrine")) {
			SHRINE = event.getTable();
		}

		// Inject books and scrolls to ebwiz tables
		if (event.getName().toString().equals(Wizardry.MODID + ":chests/library_ruins_bookshelf") && LIBRARY_RUINS_BOOKSHELF != null) {
			LootPool targetPool = event.getTable().getPool("wizardry");
			LootPool sourcePool = LIBRARY_RUINS_BOOKSHELF.getPool("ancientspellcraft");
			injectEntries(sourcePool, targetPool);
		} else if (event.getName().toString().equals(Wizardry.MODID + ":chests/obelisk") && OBELISK != null) {
			LootPool targetPool = event.getTable().getPool("high_value");
			LootPool sourcePool = OBELISK.getPool("ancientspellcraft");
			injectEntries(sourcePool, targetPool);
		} else if (event.getName().toString().equals(Wizardry.MODID + ":chests/shrine") && SHRINE != null) {
			LootPool targetPool = event.getTable().getPool("high_value");
			LootPool sourcePool = SHRINE.getPool("ancientspellcraft");
			injectEntries(sourcePool, targetPool);
		} else if (event.getName().toString().equals(Wizardry.MODID + ":chests/wizard_tower") && WIZARD_TOWER != null) {
			LootPool targetPool = event.getTable().getPool("wizardry");
			LootPool sourcePool = WIZARD_TOWER.getPool("ancientspellcraft");
			injectEntries(sourcePool, targetPool);
		} else if (event.getName().toString().equals(Wizardry.MODID + ":subsets/wand_upgrades") && WAND_UPGRADES != null) {
			LootPool targetPool = event.getTable().getPool("upgrades");
			LootPool sourcePool = WAND_UPGRADES.getPool("ancientspellcraft");
			injectEntries(sourcePool, targetPool);
		}


		// inject artefacts to ebwiz tables
		if (Arrays.asList(AncientSpellcraft.settings.artefactInjectionLocations).contains(event.getName())) {
			if (event.getName().toString().equals(Wizardry.MODID + ":subsets/uncommon_artefacts") && UNCOMMON_ARTEFACTS != null) {
				LootPool targetPool = event.getTable().getPool("uncommon_artefacts");
				LootPool sourcePool = UNCOMMON_ARTEFACTS.getPool("main");
				injectEntries(sourcePool, targetPool);
			}
			if (event.getName().toString().equals(Wizardry.MODID + ":subsets/rare_artefacts") && RARE_ARTEFACTS != null) {
				LootPool targetPool = event.getTable().getPool("rare_artefacts");
				LootPool sourcePool = RARE_ARTEFACTS.getPool("main");

				injectEntries(sourcePool, targetPool);
			}
			if (event.getName().toString().equals(Wizardry.MODID + ":subsets/epic_artefacts") && EPIC_ARTEFACTS != null) {
				LootPool targetPool = event.getTable().getPool("epic_artefacts");
				LootPool sourcePool = EPIC_ARTEFACTS.getPool("main");
				injectEntries(sourcePool, targetPool);
			}

		}
	}

	/**
	 * Injects every element of sourcePool into targetPool
	 */
	private static void injectEntries(LootPool sourcePool, LootPool targetPool) {
		// Accessing {@link net.minecraft.world.storage.loot.LootPool.lootEntries}
		if (sourcePool != null && targetPool != null) {
			List<LootEntry> lootEntries = ObfuscationReflectionHelper.getPrivateValue(LootPool.class, sourcePool, "field_186453_a");

			for (LootEntry entry : lootEntries) {
				targetPool.addEntry(entry);
			}
		} else {
			AncientSpellcraft.logger.warn("Attempted to inject to null pool source or target.");
		}

	}

	private static LootPool getAdditive(String entryName, String poolName) {
		return new LootPool(new LootEntry[] {getAdditiveEntry(entryName, 1)}, new LootCondition[0],
				new RandomValueRange(1), new RandomValueRange(0, 1), AncientSpellcraft.MODID + "_" + poolName);
	}

	private static LootEntryTable getAdditiveEntry(String name, int weight) {
		return new LootEntryTable(new ResourceLocation(name), weight, 0, new LootCondition[0],
				AncientSpellcraft.MODID + "_additive_entry");
	}

}
