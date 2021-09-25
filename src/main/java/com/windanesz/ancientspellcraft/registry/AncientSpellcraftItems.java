package com.windanesz.ancientspellcraft.registry;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.entity.projectile.EntityDevoritiumBomb;
import com.windanesz.ancientspellcraft.item.ItemASArtefact;
import com.windanesz.ancientspellcraft.item.ItemASArtemisLibArtefact;
import com.windanesz.ancientspellcraft.item.ItemAdvancedStoneFist;
import com.windanesz.ancientspellcraft.item.ItemAlchemicalEssence;
import com.windanesz.ancientspellcraft.item.ItemAmberMushroomAmulet;
import com.windanesz.ancientspellcraft.item.ItemAncientHat;
import com.windanesz.ancientspellcraft.item.ItemAncientSpellcraftSpellBook;
import com.windanesz.ancientspellcraft.item.ItemBarterScroll;
import com.windanesz.ancientspellcraft.item.ItemBattlemageSword;
import com.windanesz.ancientspellcraft.item.ItemBlockDevoritiumMaterial;
import com.windanesz.ancientspellcraft.item.ItemChargedAstralDiamond;
import com.windanesz.ancientspellcraft.item.ItemCoalBucket;
import com.windanesz.ancientspellcraft.item.ItemCornucopia;
import com.windanesz.ancientspellcraft.item.ItemCubePhasing;
import com.windanesz.ancientspellcraft.item.ItemDevoritiumArrow;
import com.windanesz.ancientspellcraft.item.ItemDevoritiumAxe;
import com.windanesz.ancientspellcraft.item.ItemDevoritiumBomb;
import com.windanesz.ancientspellcraft.item.ItemDevoritiumDoor;
import com.windanesz.ancientspellcraft.item.ItemDevoritiumIngot;
import com.windanesz.ancientspellcraft.item.ItemDevoritiumScimitar;
import com.windanesz.ancientspellcraft.item.ItemDevoritiumShield;
import com.windanesz.ancientspellcraft.item.ItemDevoritiumSword;
import com.windanesz.ancientspellcraft.item.ItemElementalOrb;
import com.windanesz.ancientspellcraft.item.ItemEnchantedNameTag;
import com.windanesz.ancientspellcraft.item.ItemEternityPendant;
import com.windanesz.ancientspellcraft.item.ItemEverfullManaFlask;
import com.windanesz.ancientspellcraft.item.ItemEvergrowingCrystal;
import com.windanesz.ancientspellcraft.item.ItemGoldBag;
import com.windanesz.ancientspellcraft.item.ItemHorn;
import com.windanesz.ancientspellcraft.item.ItemIceCream;
import com.windanesz.ancientspellcraft.item.ItemIceShield;
import com.windanesz.ancientspellcraft.item.ItemKnowledgeOrb;
import com.windanesz.ancientspellcraft.item.ItemMagicShield;
import com.windanesz.ancientspellcraft.item.ItemNewArtefact;
import com.windanesz.ancientspellcraft.item.ItemOmnicron;
import com.windanesz.ancientspellcraft.item.ItemOverlordScepter;
import com.windanesz.ancientspellcraft.item.ItemPhiloshopersStone;
import com.windanesz.ancientspellcraft.item.ItemRelic;
import com.windanesz.ancientspellcraft.item.ItemRitualBook;
import com.windanesz.ancientspellcraft.item.ItemRune;
import com.windanesz.ancientspellcraft.item.ItemSacredMace;
import com.windanesz.ancientspellcraft.item.ItemSetArtefact;
import com.windanesz.ancientspellcraft.item.ItemShadowBlade;
import com.windanesz.ancientspellcraft.item.ItemSoulboundWandUpgrade;
import com.windanesz.ancientspellcraft.item.ItemSpectralFishingRod;
import com.windanesz.ancientspellcraft.item.ItemSpectralShield;
import com.windanesz.ancientspellcraft.item.ItemSpectralShovel;
import com.windanesz.ancientspellcraft.item.ItemStoneFist;
import com.windanesz.ancientspellcraft.item.ItemTransmutationScroll;
import electroblob.wizardry.Wizardry;
import electroblob.wizardry.constants.Element;
import electroblob.wizardry.constants.Tier;
import electroblob.wizardry.item.ItemArtefact;
import electroblob.wizardry.item.ItemScroll;
import electroblob.wizardry.misc.BehaviourSpellDispense;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.registry.WizardryTabs;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.dispenser.BehaviorProjectileDispense;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.IProjectile;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ObjectHolder(AncientSpellcraft.MODID)
@Mod.EventBusSubscriber
public final class AncientSpellcraftItems {

	private AncientSpellcraftItems() {} // No instances!

	@Nonnull
	@SuppressWarnings("ConstantConditions")
	private static <T> T placeholder() { return null; }

	public static final Item ancient_spellcraft_spell_book = placeholder();
	public static final Item ancient_spell_book = placeholder();
	public static final Item ancient_spellcraft_scroll = placeholder();
	public static final Item spectral_shield = placeholder();

	// wizard gear
	public static final Item wizard_hat_ancient = placeholder();

	// ====================== Artefacts ======================

	/// ring
	public static final Item ring_lodestone = placeholder();
	public static final Item ring_poison_arrow = placeholder();
	public static final Item ring_power = placeholder();
	public static final Item ring_berserker = placeholder();
	public static final Item ring_prismarine = placeholder();
	public static final Item ring_mana_transfer = placeholder();
	public static final Item ring_blast = placeholder();
	public static final Item ring_range = placeholder();
	public static final Item ring_focus_crystal = placeholder();
	public static final Item ring_permanent_shrinkage = placeholder();
	public static final Item ring_permanent_growth = placeholder();
	public static final Item ring_unbinding = placeholder();
	public static final Item ring_disenchanter = placeholder();
	public static final Item ring_quicksand = placeholder();

	/// amulet
	public static final Item charm_cryostasis = placeholder();
	public static final Item amulet_poison_resistance = placeholder();
	public static final Item amulet_power = placeholder();
	public static final Item amulet_mana = placeholder();
	public static final Item amulet_rabbit = placeholder();
	public static final Item amulet_pendant_of_eternity = placeholder();
	public static final Item amulet_curse_ward = placeholder();
	public static final Item amulet_time_knot = placeholder();
	public static final Item amulet_arcane_catalyst = placeholder();
	public static final Item amulet_persistence = placeholder();
	public static final Item amulet_healing_mushroom = placeholder();
	public static final Item amulet_shield = placeholder();

	/// charm
	public static final Item charm_rift_bottle = placeholder();
	public static final Item charm_mana_flask = placeholder();
	public static final Item charm_reanimation = placeholder();
	public static final Item charm_knowledge_orb = placeholder();
	public static final Item charm_power_orb = placeholder();
	public static final Item charm_mana_orb = placeholder();
	public static final Item charm_progression_orb = placeholder();
	public static final Item charm_lightning_orb = placeholder();
	public static final Item charm_ice_orb = placeholder();
	public static final Item charm_earth_orb = placeholder();
	public static final Item charm_healing_orb = placeholder();
	public static final Item charm_necromancy_orb = placeholder();
	public static final Item charm_fire_orb = placeholder();
	public static final Item charm_sorcery_orb = placeholder();

	public static final Item charm_bucket_coal = placeholder();
	public static final Item cornucopia = placeholder();
	public static final Item charm_gold_bag = placeholder();
	public static final Item charm_evergrowing_crystal = placeholder();
	public static final Item charm_shadow_blade = placeholder();
	public static final Item charm_magic_light = placeholder();
	public static final Item charm_war_horn = placeholder();
	public static final Item charm_elemental_grimoire = placeholder();
	public static final Item charm_wand_upgrade = placeholder();
	public static final Item charm_enchanted_needle = placeholder();
	public static final Item charm_seed_bag = placeholder();
	public static final Item charm_omnicron = placeholder();
	public static final Item charm_burrow = placeholder();
	public static final Item charm_quicksand_walker = placeholder();
	//	public static final Item charm_book_death = placeholder();
	//	public static final Item charm_book_life = placeholder();
	public static final Item charm_prismatic_spray = placeholder();
	public static final Item charm_ice_cream = placeholder();
	public static final Item charm_runic_hammer = placeholder();
	public static final Item charm_fabrikator_toolkit = placeholder();
	public static final Item charm_scissors = placeholder();
	public static final Item charm_philosophers_stone = placeholder();
	public static final Item charm_cube_phasing = placeholder();

	public static final Item alchemical_essence = placeholder();
	public static final Item wand_channeling = placeholder();

	/// now with Belts!

	public static final Item belt_enchanter = placeholder();
	public static final Item belt_stone = placeholder();
	//	public static final Item belt_druid = placeholder();
	public static final Item belt_vortex = placeholder();
	public static final Item belt_horse = placeholder();

	/// and head slot artefacts!
	public static final Item head_curse = placeholder();
	public static final Item head_minions = placeholder();
	public static final Item head_riftstone = placeholder();
	public static final Item head_fortune = placeholder();
	public static final Item head_merchant = placeholder();
	public static final Item head_shield = placeholder();
	public static final Item head_magelight = placeholder();

	/// misc
	public static final Item magic_shield = placeholder();
	public static final Item stone_tablet_small = placeholder();
	public static final Item stone_tablet = placeholder();
	public static final Item stone_tablet_large = placeholder();
	public static final Item stone_tablet_grand = placeholder();
	public static final Item ancient_mana_flask = placeholder();
	public static final Item ancient_bound_stone = placeholder();

	public static final Item astral_diamond_shard = placeholder();
	public static final Item astral_diamond_charged = placeholder();
	public static final Item crystal_shard_fire = placeholder();
	public static final Item crystal_shard_earth = placeholder();
	public static final Item crystal_shard_healing = placeholder();
	public static final Item crystal_shard_ice = placeholder();
	public static final Item crystal_shard_lightning = placeholder();
	public static final Item crystal_shard_necromancy = placeholder();
	public static final Item crystal_shard_sorcery = placeholder();

	public static final Item devoritium_ingot = placeholder();
	public static final Item devoritium_sword = placeholder();
	public static final Item devoritium_axe = placeholder();
	public static final Item devoritium_arrow = placeholder();
	public static final Item devoritium_bomb = placeholder();
	public static final Item devoritium_door = placeholder();
	public static final Item devoritium_scimitar = placeholder();
	public static final Item devoritium_shield = placeholder();
	public static final Item transmutation_scroll = placeholder();
	public static final Item bartering_scroll = placeholder();

	/// handheld
	public static final Item shadow_blade = placeholder();
	public static final Item spectral_fishing_rod = placeholder();
	public static final Item ice_shield = placeholder();
	public static final Item battlemage_sword_novice = placeholder();
	public static final Item battlemage_sword_apprentice = placeholder();
	public static final Item battlemage_sword_advanced = placeholder();
	public static final Item battlemage_sword_master = placeholder();

	public static final Item sacred_mace = placeholder();
	public static final Item stone_fist = placeholder();
	public static final Item advanced_stone_fist = placeholder();
	public static final Item scepter_mind_control = placeholder();

	// ====================== Misc ======================
	public static final Item enchanted_name_tag = placeholder();

	public static final Item blank_rune = placeholder();
	public static final Item rune_feoh = placeholder();
	public static final Item rune_uruz = placeholder();
	public static final Item rune_thurisaz = placeholder();
	public static final Item rune_ansuz = placeholder();
	public static final Item rune_raido = placeholder();
	public static final Item rune_kaunan = placeholder();
	public static final Item rune_gyfu = placeholder();
	public static final Item rune_wynn = placeholder();
	public static final Item rune_haglaz = placeholder();
	public static final Item rune_naudiz = placeholder();
	public static final Item rune_isaz = placeholder();
	public static final Item rune_jera = placeholder();
	public static final Item rune_ihwaz = placeholder();
	public static final Item rune_peorth = placeholder();
	public static final Item rune_algiz = placeholder();
	public static final Item rune_sowilo = placeholder();
	public static final Item rune_tiwaz = placeholder();
	public static final Item rune_berkanan = placeholder();
	public static final Item rune_ehwaz = placeholder();
	public static final Item rune_mannaz = placeholder();
	public static final Item rune_laguz = placeholder();
	public static final Item rune_yngvi = placeholder();
	public static final Item rune_odal = placeholder();
	public static final Item rune_dagaz = placeholder();
	public static final Item ritual_book = placeholder();
	public static final Item arcane_compound = placeholder();
	public static final Item ice_cream = placeholder();

	public static final Item soulbound_upgrade = placeholder();
	public static final Item spectral_shovel = placeholder();

	// below registry methods are courtesy of EB
	public static void registerItem(IForgeRegistry<Item> registry, String name, Item item) {
		registerItem(registry, name, item, false);
	}

	// below registry methods are courtesy of EB
	public static void registerItem(IForgeRegistry<Item> registry, String name, String modid, Item item) {
		registerItem(registry, name, modid, item, false);
	}

	public static void registerItem(IForgeRegistry<Item> registry, String name, Item item, boolean setTabIcon) {
		item.setRegistryName(AncientSpellcraft.MODID, name);
		item.setTranslationKey(item.getRegistryName().toString());
		registry.register(item);

		if (setTabIcon && item.getCreativeTab() instanceof WizardryTabs.CreativeTabSorted) {
			((WizardryTabs.CreativeTabSorted) item.getCreativeTab()).setIconItem(new ItemStack(item));
		}

		if (item.getCreativeTab() instanceof WizardryTabs.CreativeTabListed) {
			((WizardryTabs.CreativeTabListed) item.getCreativeTab()).order.add(item);
		}
	}

	public static void registerItem(IForgeRegistry<Item> registry, String modid, String name, Item item, boolean setTabIcon) {
		item.setRegistryName(modid, name);
		item.setTranslationKey(item.getRegistryName().toString());
		registry.register(item);

		if (setTabIcon && item.getCreativeTab() instanceof WizardryTabs.CreativeTabSorted) {
			((WizardryTabs.CreativeTabSorted) item.getCreativeTab()).setIconItem(new ItemStack(item));
		}

		if (item.getCreativeTab() instanceof WizardryTabs.CreativeTabListed) {
			((WizardryTabs.CreativeTabListed) item.getCreativeTab()).order.add(item);
		}
	}

	private static void registerItemBlock(IForgeRegistry<Item> registry, Block block) {
		Item itemblock = new ItemBlock(block).setRegistryName(block.getRegistryName());
		registry.register(itemblock);
	}

	private static void registerItemBlock(IForgeRegistry<Item> registry, Block block, Item itemblock) {
		itemblock.setRegistryName(block.getRegistryName());
		registry.register(itemblock);
	}

	@SubscribeEvent
	public static void register(RegistryEvent.Register<Item> event) {

		IForgeRegistry<Item> registry = event.getRegistry();

		//		registerItemBlock(registry, AncientSpellcraftBlocks.SNOW_SLAB);
		//		registerItemBlock(registry, AncientSpellcraftBlocks.ICE_CRAFTING_TABLE);
		registerItemBlock(registry, AncientSpellcraftBlocks.ARTEFACT_PENSIVE, new ItemBlock(AncientSpellcraftBlocks.ARTEFACT_PENSIVE) {
			@Override
			public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
				Wizardry.proxy.addMultiLineDescription(tooltip, "item.ancientspellcraft:pensive.desc");
			}

			@Override
			public EnumRarity getRarity(ItemStack stack) {
				return EnumRarity.EPIC;
			}
		});

		registerItemBlock(registry, AncientSpellcraftBlocks.SKULL_WATCH, new ItemBlock(AncientSpellcraftBlocks.SKULL_WATCH));
		registerItemBlock(registry, AncientSpellcraftBlocks.SCRIBING_DESK, new ItemBlock(AncientSpellcraftBlocks.SCRIBING_DESK));
		registerItemBlock(registry, AncientSpellcraftBlocks.IMBUEMENT_ALTAR_RUINED, new ItemBlock(AncientSpellcraftBlocks.IMBUEMENT_ALTAR_RUINED));
		registerItemBlock(registry, AncientSpellcraftBlocks.SPHERE_COGNIZANCE, new ItemBlock(AncientSpellcraftBlocks.SPHERE_COGNIZANCE));
		registerItemBlock(registry, AncientSpellcraftBlocks.SENTINEL_BLOCK_IRON, new ItemBlock(AncientSpellcraftBlocks.SENTINEL_BLOCK_IRON));
		registerItemBlock(registry, AncientSpellcraftBlocks.SENTINEL_BLOCK_GOLD, new ItemBlock(AncientSpellcraftBlocks.SENTINEL_BLOCK_GOLD));
		registerItemBlock(registry, AncientSpellcraftBlocks.SENTINEL_BLOCK_DIAMOND, new ItemBlock(AncientSpellcraftBlocks.SENTINEL_BLOCK_DIAMOND));
		registerItemBlock(registry, AncientSpellcraftBlocks.SENTINEL_BLOCK_LARGE_IRON, new ItemBlock(AncientSpellcraftBlocks.SENTINEL_BLOCK_LARGE_IRON));
		registerItemBlock(registry, AncientSpellcraftBlocks.SENTINEL_BLOCK_LARGE_GOLD, new ItemBlock(AncientSpellcraftBlocks.SENTINEL_BLOCK_LARGE_GOLD));
		registerItemBlock(registry, AncientSpellcraftBlocks.SENTINEL_BLOCK_LARGE_DIAMOND, new ItemBlock(AncientSpellcraftBlocks.SENTINEL_BLOCK_LARGE_DIAMOND));

		registerItemBlock(registry, AncientSpellcraftBlocks.CRYSTAL_ORE_FIRE, new ItemBlock(AncientSpellcraftBlocks.CRYSTAL_ORE_FIRE));
		registerItemBlock(registry, AncientSpellcraftBlocks.CRYSTAL_ORE_EARTH, new ItemBlock(AncientSpellcraftBlocks.CRYSTAL_ORE_EARTH));
		registerItemBlock(registry, AncientSpellcraftBlocks.CRYSTAL_ORE_HEALING, new ItemBlock(AncientSpellcraftBlocks.CRYSTAL_ORE_HEALING));
		registerItemBlock(registry, AncientSpellcraftBlocks.CRYSTAL_ORE_ICE, new ItemBlock(AncientSpellcraftBlocks.CRYSTAL_ORE_ICE));
		registerItemBlock(registry, AncientSpellcraftBlocks.CRYSTAL_ORE_LIGHTNING, new ItemBlock(AncientSpellcraftBlocks.CRYSTAL_ORE_LIGHTNING));
		registerItemBlock(registry, AncientSpellcraftBlocks.CRYSTAL_ORE_NECROMANCY, new ItemBlock(AncientSpellcraftBlocks.CRYSTAL_ORE_NECROMANCY));
		registerItemBlock(registry, AncientSpellcraftBlocks.CRYSTAL_ORE_SORCERY, new ItemBlock(AncientSpellcraftBlocks.CRYSTAL_ORE_SORCERY));

		registerItemBlock(registry, AncientSpellcraftBlocks.DEVORITIUM_ORE, new ItemBlockDevoritiumMaterial(AncientSpellcraftBlocks.DEVORITIUM_ORE));
		registerItemBlock(registry, AncientSpellcraftBlocks.DEVORITIUM_BLOCK, new ItemBlockDevoritiumMaterial(AncientSpellcraftBlocks.DEVORITIUM_BLOCK));
		registerItemBlock(registry, AncientSpellcraftBlocks.DEVORITIUM_BARS, new ItemBlockDevoritiumMaterial(AncientSpellcraftBlocks.DEVORITIUM_BARS));

		registerItemBlock(registry, AncientSpellcraftBlocks.LOG_CRYSTAL_TREE, new ItemBlock(AncientSpellcraftBlocks.LOG_CRYSTAL_TREE));
		//		registerItemBlock(registry, AncientSpellcraftBlocks.log_crystal_tree2, new ItemBlock(AncientSpellcraftBlocks.log_crystal_tree2));
		registerItemBlock(registry, AncientSpellcraftBlocks.LEAVES_CRYSTAL_TREE, new ItemBlock(AncientSpellcraftBlocks.LEAVES_CRYSTAL_TREE));

		registerItemBlock(registry, AncientSpellcraftBlocks.DIMENSION_BOUNDARY, new ItemBlock(AncientSpellcraftBlocks.DIMENSION_BOUNDARY));
		registerItemBlock(registry, AncientSpellcraftBlocks.DIMENSION_FOCUS, new ItemBlock(AncientSpellcraftBlocks.DIMENSION_FOCUS));
		//		registerItemBlock(registry, AncientSpellcraftBlocks.ANCIENT_DIMENSION_BOUNDARY, new ItemBlock(AncientSpellcraftBlocks.ANCIENT_DIMENSION_BOUNDARY));

		registerItem(registry, "magic_shield", new ItemMagicShield(EnumRarity.EPIC));
		registerItem(registry, "spectral_shield", new ItemSpectralShield());
		registerItem(registry, "ancient_spellcraft_spell_book", new ItemAncientSpellcraftSpellBook());
		registerItem(registry, "ancient_spell_book", new ItemAncientSpellcraftSpellBook());
		registerItem(registry, "ancient_spellcraft_scroll", new ItemScroll());
		registerItem(registry, "enchanted_name_tag", new ItemEnchantedNameTag());

		registerItem(registry, "wizard_hat_ancient", new ItemAncientHat(WizardryItems.Materials.SILK, 1, EntityEquipmentSlot.HEAD, Element.MAGIC));

		// ====================== Artefacts ======================

		/// ring
		registerItem(registry, "ring_poison_arrow", new ItemASArtefact(EnumRarity.UNCOMMON, ItemArtefact.Type.RING));
		registerItem(registry, "ring_lodestone", new ItemASArtefact(EnumRarity.RARE, ItemArtefact.Type.RING));
		registerItem(registry, "ring_berserker", new ItemASArtefact(EnumRarity.RARE, ItemArtefact.Type.RING));
		registerItem(registry, "ring_power", new ItemSetArtefact(EnumRarity.UNCOMMON, ItemArtefact.Type.RING, "jewels_of_power", 4, new ArrayList<String>(Arrays.asList("ring_power", "amulet_power", "charm_power_orb"))));
		registerItem(registry, "ring_prismarine", new ItemASArtefact(EnumRarity.RARE, ItemArtefact.Type.RING));
		registerItem(registry, "ring_mana_transfer", new ItemASArtefact(EnumRarity.RARE, ItemArtefact.Type.RING));
		registerItem(registry, "ring_blast", new ItemASArtefact(EnumRarity.RARE, ItemArtefact.Type.RING) {
			@Override
			@SideOnly(Side.CLIENT)
			public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
				tooltip.add(net.minecraft.client.resources.I18n.format("item." + this.getRegistryName() + ".desc"));
				tooltip.add("\u00A7c" + net.minecraft.client.resources.I18n.format("item." + this.getRegistryName() + ".desc2"));
			}
		});

		registerItem(registry, "ring_range", new ItemASArtefact(EnumRarity.RARE, ItemArtefact.Type.RING) {
			@Override
			@SideOnly(Side.CLIENT)
			public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
				tooltip.add(net.minecraft.client.resources.I18n.format("item." + this.getRegistryName() + ".desc"));
				tooltip.add("\u00A7c" + net.minecraft.client.resources.I18n.format("item." + this.getRegistryName() + ".desc2"));
			}
		});
		registerItem(registry, "ring_focus_crystal", new ItemASArtefact(EnumRarity.RARE, ItemArtefact.Type.RING));
		registerItem(registry, "ring_unbinding", new ItemASArtefact(EnumRarity.RARE, ItemArtefact.Type.RING));
		registerItem(registry, "ring_disenchanter", new ItemASArtefact(EnumRarity.RARE, ItemArtefact.Type.RING));
		registerItem(registry, "ring_quicksand", new ItemASArtefact(EnumRarity.RARE, ItemArtefact.Type.RING));
		registerItem(registry, "ring_permanent_shrinkage", new ItemASArtemisLibArtefact(EnumRarity.EPIC, ItemArtefact.Type.RING));
		registerItem(registry, "ring_permanent_growth", new ItemASArtemisLibArtefact(EnumRarity.EPIC, ItemArtefact.Type.RING));

		/// amulet
		registerItem(registry, "amulet_mana", new ItemASArtefact(EnumRarity.UNCOMMON, ItemArtefact.Type.AMULET));
		registerItem(registry, "amulet_rabbit", new ItemASArtefact(EnumRarity.UNCOMMON, ItemArtefact.Type.AMULET));
		registerItem(registry, "amulet_poison_resistance", new ItemASArtefact(EnumRarity.UNCOMMON, ItemArtefact.Type.AMULET));
		registerItem(registry, "amulet_power", new ItemSetArtefact(EnumRarity.RARE, ItemArtefact.Type.AMULET, "jewels_of_power", 4, new ArrayList<String>(Arrays.asList("ring_power", "amulet_power", "charm_power_orb"))));
		registerItem(registry, "amulet_persistence", new ItemASArtefact(EnumRarity.RARE, ItemArtefact.Type.AMULET));
		registerItem(registry, "amulet_curse_ward", new ItemASArtefact(EnumRarity.EPIC, ItemArtefact.Type.AMULET));
		registerItem(registry, "amulet_pendant_of_eternity", new ItemEternityPendant(EnumRarity.EPIC, ItemArtefact.Type.AMULET));
		registerItem(registry, "amulet_time_knot", new ItemASArtefact(EnumRarity.EPIC, ItemArtefact.Type.AMULET));
		registerItem(registry, "amulet_arcane_catalyst", new ItemASArtefact(EnumRarity.EPIC, ItemArtefact.Type.AMULET));
		registerItem(registry, "amulet_healing_mushroom", new ItemAmberMushroomAmulet(EnumRarity.RARE, ItemArtefact.Type.AMULET));
		registerItem(registry, "amulet_shield", new ItemASArtefact(EnumRarity.RARE, ItemArtefact.Type.AMULET));

		/// charm
		registerItem(registry, "charm_seed_bag", new ItemASArtefact(EnumRarity.RARE, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_mana_flask", new ItemEverfullManaFlask());
		registerItem(registry, "charm_ice_cream", new ItemASArtefact(EnumRarity.UNCOMMON, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_enchanted_needle", new ItemASArtefact(EnumRarity.RARE, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_bucket_coal", new ItemCoalBucket(EnumRarity.RARE));
		registerItem(registry, "cornucopia", new ItemCornucopia(EnumRarity.RARE));
		registerItem(registry, "charm_gold_bag", new ItemGoldBag(EnumRarity.RARE));
		registerItem(registry, "charm_quicksand_walker", new ItemASArtefact(EnumRarity.RARE, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_magic_light", new ItemASArtefact(EnumRarity.RARE, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_mana_orb", new ItemASArtefact(EnumRarity.UNCOMMON, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_knowledge_orb", new ItemKnowledgeOrb(EnumRarity.EPIC, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_power_orb", new ItemSetArtefact(EnumRarity.EPIC, ItemArtefact.Type.CHARM, "jewels_of_power", 4, new ArrayList<String>(Arrays.asList("ring_power", "amulet_power", "charm_power_orb"))));
		registerItem(registry, "charm_progression_orb", new ItemASArtefact(EnumRarity.EPIC, ItemArtefact.Type.CHARM));

		registerItem(registry, "charm_lightning_orb", new ItemElementalOrb(EnumRarity.EPIC, ItemArtefact.Type.CHARM, "ebwizardry:thunderstorm", 6000, Element.LIGHTNING));
		registerItem(registry, "charm_ice_orb", new ItemElementalOrb(EnumRarity.EPIC, ItemArtefact.Type.CHARM, "ebwizardry:ice_age", 6000, Element.ICE));
		registerItem(registry, "charm_earth_orb", new ItemElementalOrb(EnumRarity.EPIC, ItemArtefact.Type.CHARM, "ebwizardry:earthquake", 6000, Element.EARTH));
		registerItem(registry, "charm_healing_orb", new ItemElementalOrb(EnumRarity.EPIC, ItemArtefact.Type.CHARM, "ebwizardry:font_of_vitality", 6000, Element.HEALING));
		registerItem(registry, "charm_necromancy_orb", new ItemElementalOrb(EnumRarity.EPIC, ItemArtefact.Type.CHARM, "ebwizardry:plague_of_darkness", 6000, Element.NECROMANCY));
		registerItem(registry, "charm_fire_orb", new ItemElementalOrb(EnumRarity.EPIC, ItemArtefact.Type.CHARM, "ebwizardry:firestorm", 6000, Element.FIRE));
		registerItem(registry, "charm_sorcery_orb", new ItemElementalOrb(EnumRarity.EPIC, ItemArtefact.Type.CHARM, "ebwizardry:shockwave", 6000, Element.SORCERY));

		registerItem(registry, "charm_rift_bottle", new ItemASArtefact(EnumRarity.EPIC, ItemArtefact.Type.AMULET));
		registerItem(registry, "charm_reanimation", new ItemASArtefact(EnumRarity.EPIC, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_evergrowing_crystal", new ItemEvergrowingCrystal(EnumRarity.EPIC));
		registerItem(registry, "charm_shadow_blade", new ItemASArtefact(EnumRarity.EPIC, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_elemental_grimoire", new ItemASArtefact(EnumRarity.EPIC, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_war_horn", new ItemHorn(EnumRarity.EPIC, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_wand_upgrade", new ItemASArtefact(EnumRarity.EPIC, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_cryostasis", new ItemASArtefact(EnumRarity.EPIC, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_omnicron", new ItemOmnicron(EnumRarity.EPIC, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_burrow", new ItemASArtefact(EnumRarity.RARE, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_prismatic_spray", new ItemASArtefact(EnumRarity.EPIC, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_runic_hammer", new ItemASArtefact(EnumRarity.EPIC, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_fabrikator_toolkit", new ItemASArtefact(EnumRarity.EPIC, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_scissors", new ItemASArtefact(EnumRarity.EPIC, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_philosophers_stone", new ItemPhiloshopersStone(EnumRarity.EPIC));
		registerItem(registry, "charm_cube_phasing", new ItemCubePhasing(EnumRarity.EPIC, ItemArtefact.Type.CHARM));
		registerItem(registry, "scepter_mind_control", new ItemOverlordScepter());

		registerItem(registry, "belt_enchanter", new ItemNewArtefact(EnumRarity.RARE, ItemNewArtefact.AdditionalType.BELT));
		registerItem(registry, "belt_stone", new ItemNewArtefact(EnumRarity.RARE, ItemNewArtefact.AdditionalType.BELT));
		registerItem(registry, "belt_vortex", new ItemNewArtefact(EnumRarity.RARE, ItemNewArtefact.AdditionalType.BELT));
		registerItem(registry, "belt_horse", new ItemNewArtefact(EnumRarity.UNCOMMON, ItemNewArtefact.AdditionalType.BELT));

		registerItem(registry, "head_curse", new ItemNewArtefact(EnumRarity.RARE, ItemNewArtefact.AdditionalType.HEAD));
		registerItem(registry, "head_merchant", new ItemNewArtefact(EnumRarity.RARE, ItemNewArtefact.AdditionalType.HEAD));
		registerItem(registry, "head_minions", new ItemNewArtefact(EnumRarity.EPIC, ItemNewArtefact.AdditionalType.HEAD));
		registerItem(registry, "head_riftstone", new ItemNewArtefact(EnumRarity.EPIC, ItemNewArtefact.AdditionalType.HEAD));
		registerItem(registry, "head_fortune", new ItemNewArtefact(EnumRarity.EPIC, ItemNewArtefact.AdditionalType.HEAD));
		registerItem(registry, "head_shield", new ItemNewArtefact(EnumRarity.EPIC, ItemNewArtefact.AdditionalType.HEAD));
		registerItem(registry, "head_magelight", new ItemNewArtefact(EnumRarity.RARE, ItemNewArtefact.AdditionalType.HEAD));

		/// misc

		registerItem(registry, "stone_tablet_small", new ItemRelic("stone_tablet_small", EnumRarity.COMMON));
		registerItem(registry, "stone_tablet", new ItemRelic("stone_tablet", EnumRarity.UNCOMMON));
		registerItem(registry, "stone_tablet_large", new ItemRelic("stone_tablet_large", EnumRarity.RARE));
		registerItem(registry, "stone_tablet_grand", new ItemRelic("stone_tablet_grand", EnumRarity.EPIC));
		registerItem(registry, "ancient_mana_flask", new ItemRelic("ancient_mana_flask", EnumRarity.UNCOMMON).setCreativeTab(null));
		registerItem(registry, "ancient_bound_stone", new ItemRelic("ancient_bound_stone", EnumRarity.EPIC).setCreativeTab(null));

		registerItem(registry, "shadow_blade", new ItemShadowBlade());
		registerItem(registry, "spectral_fishing_rod", new ItemSpectralFishingRod());
		registerItem(registry, "ice_shield", new ItemIceShield());
		registerItem(registry, "stone_fist", new ItemStoneFist());
		registerItem(registry, "advanced_stone_fist", new ItemAdvancedStoneFist());
		registerItem(registry, "sacred_mace", new ItemSacredMace());

		registerItem(registry, "astral_diamond_shard", new Item().setMaxDamage(0).setCreativeTab(AncientSpellcraftTabs.ANCIENTSPELLCRAFT));
		registerItem(registry, "astral_diamond_charged", new ItemChargedAstralDiamond());

		registerItem(registry, "crystal_shard_fire", new Item().setMaxDamage(0).setCreativeTab(AncientSpellcraftTabs.ANCIENTSPELLCRAFT));
		registerItem(registry, "crystal_shard_earth", new Item().setMaxDamage(0).setCreativeTab(AncientSpellcraftTabs.ANCIENTSPELLCRAFT));
		registerItem(registry, "crystal_shard_healing", new Item().setMaxDamage(0).setCreativeTab(AncientSpellcraftTabs.ANCIENTSPELLCRAFT));
		registerItem(registry, "crystal_shard_ice", new Item().setMaxDamage(0).setCreativeTab(AncientSpellcraftTabs.ANCIENTSPELLCRAFT));
		registerItem(registry, "crystal_shard_lightning", new Item().setMaxDamage(0).setCreativeTab(AncientSpellcraftTabs.ANCIENTSPELLCRAFT));
		registerItem(registry, "crystal_shard_necromancy", new Item().setMaxDamage(0).setCreativeTab(AncientSpellcraftTabs.ANCIENTSPELLCRAFT));
		registerItem(registry, "crystal_shard_sorcery", new Item().setMaxDamage(0).setCreativeTab(AncientSpellcraftTabs.ANCIENTSPELLCRAFT));

		registerItem(registry, "wand_channeling", new Item().setMaxDamage(0).setCreativeTab(null));

		registerItem(registry, "devoritium_ingot", new ItemDevoritiumIngot());
		registerItem(registry, "devoritium_sword", new ItemDevoritiumSword());
		registerItem(registry, "devoritium_axe", new ItemDevoritiumAxe());
		registerItem(registry, "devoritium_arrow", new ItemDevoritiumArrow().setCreativeTab(AncientSpellcraftTabs.ANCIENTSPELLCRAFT));
		registerItem(registry, "devoritium_bomb", new ItemDevoritiumBomb());
		registerItem(registry, "devoritium_door", new ItemDevoritiumDoor(AncientSpellcraftBlocks.DEVORITIUM_DOOR));
		registerItem(registry, "devoritium_scimitar", new ItemDevoritiumScimitar());
		registerItem(registry, "devoritium_shield", new ItemDevoritiumShield());

		registerItem(registry, "transmutation_scroll", new ItemTransmutationScroll());
		registerItem(registry, "bartering_scroll", new ItemBarterScroll());

		registerItem(registry, "blank_rune", new Item().setCreativeTab(AncientSpellcraftTabs.ANCIENTSPELLCRAFT).setMaxStackSize(16));
		registerItem(registry, "rune_feoh", new ItemRune().setCreativeTab(AncientSpellcraftTabs.ANCIENTSPELLCRAFT));
		registerItem(registry, "rune_uruz", new ItemRune().setCreativeTab(AncientSpellcraftTabs.ANCIENTSPELLCRAFT));
		registerItem(registry, "rune_thurisaz", new ItemRune().setCreativeTab(AncientSpellcraftTabs.ANCIENTSPELLCRAFT));
		registerItem(registry, "rune_ansuz", new ItemRune().setCreativeTab(AncientSpellcraftTabs.ANCIENTSPELLCRAFT));
		registerItem(registry, "rune_raido", new ItemRune().setCreativeTab(AncientSpellcraftTabs.ANCIENTSPELLCRAFT));
		registerItem(registry, "rune_kaunan", new ItemRune().setCreativeTab(AncientSpellcraftTabs.ANCIENTSPELLCRAFT));
		registerItem(registry, "rune_gyfu", new ItemRune().setCreativeTab(AncientSpellcraftTabs.ANCIENTSPELLCRAFT));
		registerItem(registry, "rune_wynn", new ItemRune().setCreativeTab(AncientSpellcraftTabs.ANCIENTSPELLCRAFT));
		registerItem(registry, "rune_haglaz", new ItemRune().setCreativeTab(AncientSpellcraftTabs.ANCIENTSPELLCRAFT));
		registerItem(registry, "rune_naudiz", new ItemRune().setCreativeTab(AncientSpellcraftTabs.ANCIENTSPELLCRAFT));
		registerItem(registry, "rune_isaz", new ItemRune().setCreativeTab(AncientSpellcraftTabs.ANCIENTSPELLCRAFT));
		registerItem(registry, "rune_jera", new ItemRune().setCreativeTab(AncientSpellcraftTabs.ANCIENTSPELLCRAFT));
		registerItem(registry, "rune_ihwaz", new ItemRune().setCreativeTab(AncientSpellcraftTabs.ANCIENTSPELLCRAFT));
		registerItem(registry, "rune_peorth", new ItemRune().setCreativeTab(AncientSpellcraftTabs.ANCIENTSPELLCRAFT));
		registerItem(registry, "rune_algiz", new ItemRune().setCreativeTab(AncientSpellcraftTabs.ANCIENTSPELLCRAFT));
		registerItem(registry, "rune_sowilo", new ItemRune().setCreativeTab(AncientSpellcraftTabs.ANCIENTSPELLCRAFT));
		registerItem(registry, "rune_tiwaz", new ItemRune().setCreativeTab(AncientSpellcraftTabs.ANCIENTSPELLCRAFT));
		registerItem(registry, "rune_berkanan", new ItemRune().setCreativeTab(AncientSpellcraftTabs.ANCIENTSPELLCRAFT));
		registerItem(registry, "rune_ehwaz", new ItemRune().setCreativeTab(AncientSpellcraftTabs.ANCIENTSPELLCRAFT));
		registerItem(registry, "rune_mannaz", new ItemRune().setCreativeTab(AncientSpellcraftTabs.ANCIENTSPELLCRAFT));
		registerItem(registry, "rune_laguz", new ItemRune().setCreativeTab(AncientSpellcraftTabs.ANCIENTSPELLCRAFT));
		registerItem(registry, "rune_yngvi", new ItemRune().setCreativeTab(AncientSpellcraftTabs.ANCIENTSPELLCRAFT));
		registerItem(registry, "rune_odal", new ItemRune().setCreativeTab(AncientSpellcraftTabs.ANCIENTSPELLCRAFT));
		registerItem(registry, "rune_dagaz", new ItemRune().setCreativeTab(AncientSpellcraftTabs.ANCIENTSPELLCRAFT));

		registerItem(registry, "ritual_book", new ItemRitualBook());
		registerItem(registry, "arcane_compound", new Item().setCreativeTab(AncientSpellcraftTabs.ANCIENTSPELLCRAFT).setMaxStackSize(16));

		registerItem(registry, "soulbound_upgrade", new ItemSoulboundWandUpgrade());
		registerItem(registry, "spectral_shovel", new ItemSpectralShovel());
		registerItem(registry, "ice_cream", new ItemIceCream());
		registerItem(registry, "alchemical_essence", new ItemAlchemicalEssence());

		registerItem(registry, "battlemage_sword_novice", new ItemBattlemageSword(Tier.NOVICE, 3));
		registerItem(registry, "battlemage_sword_apprentice", new ItemBattlemageSword(Tier.APPRENTICE, 5));
		registerItem(registry, "battlemage_sword_advanced", new ItemBattlemageSword(Tier.ADVANCED, 7));
		registerItem(registry, "battlemage_sword_master", new ItemBattlemageSword(Tier.MASTER, 9));

	}

	public static void registerDispenseBehaviours() {
		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(ancient_spellcraft_scroll, new BehaviourSpellDispense());

		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(devoritium_bomb, new BehaviorProjectileDispense() {
			@Override
			protected IProjectile getProjectileEntity(World world, IPosition position, ItemStack stack) {
				EntityDevoritiumBomb entity = new EntityDevoritiumBomb(world);
				entity.setPosition(position.getX(), position.getY(), position.getZ());
				return entity;
			}
		});

	}

}