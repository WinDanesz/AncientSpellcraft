package com.windanesz.ancientspellcraft.registry;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootEntryTable;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Arrays;

/**
 * Class responsible for registering Ancient Spellcraft's loot tables. Also handles loot injection.
 * <p>
 * Based on Electroblob's TFSpellPack and Wizardry Loot registry classes
 *
 * @author WinDanesz
 */
@Mod.EventBusSubscriber
public class AncientSpellcraftLoot {

	private AncientSpellcraftLoot() {}

	/**
	 * Called from the preInit method in the main mod class to register the custom dungeon loot.
	 */

	public static void register() {
		// chest
		LootTableList.register(new ResourceLocation(AncientSpellcraft.MODID, "chests/dungeon_additions"));
		//		LootTableList.register(new ResourceLocation(AncientSpellcraft.MODID, "chests/obelisk_additions"));
		//		LootTableList.register(new ResourceLocation(AncientSpellcraft.MODID, "chests/shrine_additions"));
		//		LootTableList.register(new ResourceLocation(AncientSpellcraft.MODID, "chests/wizard_tower_additions"));
		//		LootTableList.register(new ResourceLocation(AncientSpellcraft.MODID, "subsets/ancientspellcraft_books"));

		//		 wizadry patching
		//		LootTableList.register(new ResourceLocation(Wizardry.MODID, "subsets/epic_artefacts"));

		// subsets
		LootTableList.register(new ResourceLocation(AncientSpellcraft.MODID, "subsets/epic_artefacts"));

		// entities
		LootTableList.register(new ResourceLocation(AncientSpellcraft.MODID, "entities/void_creeper"));
	}

	//	Artefact Loot entries
	//	LootEntryItem(Item itemIn, int weightIn, int qualityIn, LootFunction[] functionsIn, LootCondition[] conditionsIn, java.lang.String entryName)

	//	private static LootEntry magicShield = new LootEntryItem(AncientSpellcraftItems.magic_shield, 30, 1, new LootFunction[0], new LootCondition[0], AncientSpellcraft.MODID + ":magic_shield");

	@SubscribeEvent
	public static void onLootTableLoadEvent(LootTableLoadEvent event) {
		if (Arrays.asList(AncientSpellcraft.settings.lootInjectionLocations).contains(event.getName())) {
			event.getTable().addPool(getAdditive(AncientSpellcraft.MODID + ":chests/dungeon_additions", AncientSpellcraft.MODID + "_ancientspellcraft_dungeon_additions"));
		}

		if (Arrays.asList(AncientSpellcraft.settings.artefactInjectionLocations).contains(event.getName())) {
			event.getTable().addPool(getAdditive(AncientSpellcraft.MODID + ":subsets/epic_artefacts", AncientSpellcraft.MODID + "_ancientspellcraft_epic_artefacts"));

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
