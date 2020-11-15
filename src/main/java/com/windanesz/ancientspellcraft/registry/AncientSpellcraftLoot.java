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

	private AncientSpellcraftLoot() {}

	/**
	 * Called from the preInit method in the main mod class to register the custom dungeon loot.
	 */

	public static void preInit() {
		// chest
		LootTableList.register(new ResourceLocation(AncientSpellcraft.MODID, "chests/dungeon_additions"));
		//		LootTableList.register(new ResourceLocation(AncientSpellcraft.MODID, "chests/obelisk_additions"));
		//		LootTableList.register(new ResourceLocation(AncientSpellcraft.MODID, "chests/shrine_additions"));
		//		LootTableList.register(new ResourceLocation(AncientSpellcraft.MODID, "chests/wizard_tower_additions"));
		//		LootTableList.register(new ResourceLocation(AncientSpellcraft.MODID, "subsets/ancientspellcraft_books"));

		//		 wizadry patching
		//		LootTableList.register(new ResourceLocation(Wizardry.MODID, "subsets/epic_artefacts"));

		// subsets
		LootTableList.register(new ResourceLocation(AncientSpellcraft.MODID, "subsets/uncommon_artefacts"));
		LootTableList.register(new ResourceLocation(AncientSpellcraft.MODID, "subsets/rare_artefacts"));
		LootTableList.register(new ResourceLocation(AncientSpellcraft.MODID, "subsets/epic_artefacts"));

		// entities
		LootTableList.register(new ResourceLocation(AncientSpellcraft.MODID, "entities/void_creeper"));
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
		}

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

	private static void injectEntries(LootPool sourcePool, LootPool targetPool) {
		// Accessing {@link net.minecraft.world.storage.loot.LootPool.lootEntries}
		List<LootEntry> lootEntries = ObfuscationReflectionHelper.getPrivateValue(LootPool.class, sourcePool, "field_186453_a");

		for (LootEntry entry : lootEntries) {
			targetPool.addEntry(entry);
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
