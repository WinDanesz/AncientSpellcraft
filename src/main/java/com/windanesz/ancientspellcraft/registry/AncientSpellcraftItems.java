package com.windanesz.ancientspellcraft.registry;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.item.ItemASArtefact;
import com.windanesz.ancientspellcraft.item.ItemAncientHat;
import com.windanesz.ancientspellcraft.item.ItemAncientSpellcraftSpellBook;
import com.windanesz.ancientspellcraft.item.ItemChargedAstralDiamond;
import com.windanesz.ancientspellcraft.item.ItemCoalBucket;
import com.windanesz.ancientspellcraft.item.ItemCornucopia;
import com.windanesz.ancientspellcraft.item.ItemElementalOrb;
import com.windanesz.ancientspellcraft.item.ItemEnchantedNameTag;
import com.windanesz.ancientspellcraft.item.ItemEternityPendant;
import com.windanesz.ancientspellcraft.item.ItemEverfullManaFlask;
import com.windanesz.ancientspellcraft.item.ItemEvergrowingCrystal;
import com.windanesz.ancientspellcraft.item.ItemGoldBag;
import com.windanesz.ancientspellcraft.item.ItemHorn;
import com.windanesz.ancientspellcraft.item.ItemIceShield;
import com.windanesz.ancientspellcraft.item.ItemKnowledgeOrb;
import com.windanesz.ancientspellcraft.item.ItemMagicShield;
import com.windanesz.ancientspellcraft.item.ItemNewArtefact;
import com.windanesz.ancientspellcraft.item.ItemOmnicron;
import com.windanesz.ancientspellcraft.item.ItemRelic;
import com.windanesz.ancientspellcraft.item.ItemSetArtefact;
import com.windanesz.ancientspellcraft.item.ItemShadowBlade;
import com.windanesz.ancientspellcraft.item.ItemSpectralFishingRod;
import com.windanesz.ancientspellcraft.item.ItemSpectralShield;
import electroblob.wizardry.Wizardry;
import electroblob.wizardry.constants.Element;
import electroblob.wizardry.item.ItemArtefact;
import electroblob.wizardry.item.ItemScroll;
import electroblob.wizardry.misc.BehaviourSpellDispense;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.registry.WizardryTabs;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.client.util.ITooltipFlag;
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

	/// amulet
	public static final Item charm_cryostasis = placeholder();
	public static final Item amulet_poison_resistance = placeholder();
	public static final Item amulet_power = placeholder();
	public static final Item amulet_mana = placeholder();
	public static final Item amulet_rabbit = placeholder();
	public static final Item amulet_pendant_of_eternity = placeholder();
	public static final Item amulet_curse_ward = placeholder();
	public static final Item amulet_time_knot = placeholder();

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

	/// now with Belts!

	public static final Item belt_enchanter = placeholder();


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


	/// handheld
	public static final Item shadow_blade = placeholder();
	public static final Item spectral_fishing_rod = placeholder();
	public static final Item ice_shield = placeholder();

	// ====================== Misc ======================
	public static final Item enchanted_name_tag = placeholder();

	// below registry methods are courtesy of EB
	public static void registerItem(IForgeRegistry<Item> registry, String name, Item item) {
		registerItem(registry, name, item, false);
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


		registerItemBlock(registry, AncientSpellcraftBlocks.crystal_ore_fire, new ItemBlock(AncientSpellcraftBlocks.crystal_ore_fire));
		registerItemBlock(registry, AncientSpellcraftBlocks.crystal_ore_earth, new ItemBlock(AncientSpellcraftBlocks.crystal_ore_earth));
		registerItemBlock(registry, AncientSpellcraftBlocks.crystal_ore_healing, new ItemBlock(AncientSpellcraftBlocks.crystal_ore_healing));
		registerItemBlock(registry, AncientSpellcraftBlocks.crystal_ore_ice, new ItemBlock(AncientSpellcraftBlocks.crystal_ore_ice));
		registerItemBlock(registry, AncientSpellcraftBlocks.crystal_ore_lightning, new ItemBlock(AncientSpellcraftBlocks.crystal_ore_lightning));
		registerItemBlock(registry, AncientSpellcraftBlocks.crystal_ore_necromancy, new ItemBlock(AncientSpellcraftBlocks.crystal_ore_necromancy));
		registerItemBlock(registry, AncientSpellcraftBlocks.crystal_ore_sorcery, new ItemBlock(AncientSpellcraftBlocks.crystal_ore_sorcery));


		registerItemBlock(registry, AncientSpellcraftBlocks.log_crystal_tree, new ItemBlock(AncientSpellcraftBlocks.log_crystal_tree));
//		registerItemBlock(registry, AncientSpellcraftBlocks.log_crystal_tree2, new ItemBlock(AncientSpellcraftBlocks.log_crystal_tree2));
		registerItemBlock(registry, AncientSpellcraftBlocks.leaves_crystal_tree, new ItemBlock(AncientSpellcraftBlocks.leaves_crystal_tree));


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

		/// amulet
		registerItem(registry, "amulet_mana", new ItemASArtefact(EnumRarity.UNCOMMON, ItemArtefact.Type.AMULET));
		registerItem(registry, "amulet_rabbit", new ItemASArtefact(EnumRarity.UNCOMMON, ItemArtefact.Type.AMULET));
		registerItem(registry, "amulet_poison_resistance", new ItemASArtefact(EnumRarity.UNCOMMON, ItemArtefact.Type.AMULET));
		registerItem(registry, "amulet_power", new ItemSetArtefact(EnumRarity.RARE, ItemArtefact.Type.AMULET, "jewels_of_power", 4, new ArrayList<String>(Arrays.asList("ring_power", "amulet_power", "charm_power_orb"))));
		registerItem(registry, "amulet_curse_ward", new ItemASArtefact(EnumRarity.EPIC, ItemArtefact.Type.AMULET));
		registerItem(registry, "amulet_pendant_of_eternity", new ItemEternityPendant(EnumRarity.EPIC, ItemArtefact.Type.AMULET));
		registerItem(registry, "amulet_time_knot", new ItemASArtefact(EnumRarity.EPIC, ItemArtefact.Type.AMULET));

		/// charm
		registerItem(registry, "charm_bucket_coal", new ItemCoalBucket(EnumRarity.UNCOMMON, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_seed_bag", new ItemASArtefact(EnumRarity.UNCOMMON, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_mana_flask", new ItemEverfullManaFlask(EnumRarity.RARE, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_enchanted_needle", new ItemASArtefact(EnumRarity.RARE, ItemArtefact.Type.CHARM));
		registerItem(registry, "cornucopia", new ItemCornucopia(EnumRarity.RARE, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_gold_bag", new ItemGoldBag(EnumRarity.RARE, ItemArtefact.Type.CHARM));
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
		registerItem(registry, "charm_evergrowing_crystal", new ItemEvergrowingCrystal(EnumRarity.EPIC, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_shadow_blade", new ItemASArtefact(EnumRarity.EPIC, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_elemental_grimoire", new ItemASArtefact(EnumRarity.EPIC, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_war_horn", new ItemHorn(EnumRarity.EPIC, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_wand_upgrade", new ItemASArtefact(EnumRarity.EPIC, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_cryostasis", new ItemASArtefact(EnumRarity.EPIC, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_omnicron", new ItemOmnicron(EnumRarity.EPIC, ItemArtefact.Type.CHARM));

		registerItem(registry, "belt_enchanter", new ItemNewArtefact(EnumRarity.RARE, ItemNewArtefact.AdditionalType.BELT));

		/// misc

		registerItem(registry, "stone_tablet_small", new ItemRelic("stone_tablet_small", EnumRarity.COMMON));
		registerItem(registry, "stone_tablet", new ItemRelic("stone_tablet", EnumRarity.UNCOMMON));
		registerItem(registry, "stone_tablet_large", new ItemRelic("stone_tablet_large", EnumRarity.RARE));
		registerItem(registry, "stone_tablet_grand", new ItemRelic("stone_tablet_grand", EnumRarity.EPIC));
		registerItem(registry, "ancient_mana_flask", new ItemRelic("ancient_mana_flask", EnumRarity.UNCOMMON));
		registerItem(registry, "ancient_bound_stone", new ItemRelic("ancient_bound_stone", EnumRarity.EPIC));

		registerItem(registry, "shadow_blade", new ItemShadowBlade());
		registerItem(registry, "spectral_fishing_rod", new ItemSpectralFishingRod());
		registerItem(registry, "ice_shield", new ItemIceShield());


		registerItem(registry, "astral_diamond_shard", new Item().setMaxDamage(0).setCreativeTab(AncientSpellcraftTabs.ANCIENTSPELLCRAFT));
		registerItem(registry, "astral_diamond_charged", new ItemChargedAstralDiamond());

		registerItem(registry, "crystal_shard_fire", new Item().setMaxDamage(0).setCreativeTab(AncientSpellcraftTabs.ANCIENTSPELLCRAFT));
		registerItem(registry, "crystal_shard_earth", new Item().setMaxDamage(0).setCreativeTab(AncientSpellcraftTabs.ANCIENTSPELLCRAFT));
		registerItem(registry, "crystal_shard_healing", new Item().setMaxDamage(0).setCreativeTab(AncientSpellcraftTabs.ANCIENTSPELLCRAFT));
		registerItem(registry, "crystal_shard_ice", new Item().setMaxDamage(0).setCreativeTab(AncientSpellcraftTabs.ANCIENTSPELLCRAFT));
		registerItem(registry, "crystal_shard_lightning", new Item().setMaxDamage(0).setCreativeTab(AncientSpellcraftTabs.ANCIENTSPELLCRAFT));
		registerItem(registry, "crystal_shard_necromancy", new Item().setMaxDamage(0).setCreativeTab(AncientSpellcraftTabs.ANCIENTSPELLCRAFT));
		registerItem(registry, "crystal_shard_sorcery", new Item().setMaxDamage(0).setCreativeTab(AncientSpellcraftTabs.ANCIENTSPELLCRAFT));

	}

	public static void registerDispenseBehaviours() {
		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(ancient_spellcraft_scroll, new BehaviourSpellDispense());
	}
}