package com.windanesz.ancientspellcraft.registry;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.item.ItemASArtefact;
import com.windanesz.ancientspellcraft.item.ItemAncientHat;
import com.windanesz.ancientspellcraft.item.ItemAncientSpellcraftSpellBook;
import com.windanesz.ancientspellcraft.item.ItemCoalBucket;
import com.windanesz.ancientspellcraft.item.ItemEnchantedNameTag;
import com.windanesz.ancientspellcraft.item.ItemEternityPendant;
import com.windanesz.ancientspellcraft.item.ItemEverfullManaFlask;
import com.windanesz.ancientspellcraft.item.ItemEvergrowingCrystal;
import com.windanesz.ancientspellcraft.item.ItemGoldBag;
import com.windanesz.ancientspellcraft.item.ItemHorn;
import com.windanesz.ancientspellcraft.item.ItemIceShield;
import com.windanesz.ancientspellcraft.item.ItemKnowledgeOrb;
import com.windanesz.ancientspellcraft.item.ItemMagicShield;
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
	public static final Item charm_bucket_coal = placeholder();
	public static final Item charm_gold_bag = placeholder();
	public static final Item charm_evergrowing_crystal = placeholder();
	public static final Item charm_shadow_blade = placeholder();
	public static final Item charm_magic_light = placeholder();
	public static final Item charm_war_horn = placeholder();
	public static final Item charm_elemental_grimoire = placeholder();
	public static final Item charm_wand_upgrade = placeholder();
	public static final Item charm_enchanted_needle = placeholder();
	public static final Item charm_seed_bag = placeholder();

	/// misc
	public static final Item magic_shield = placeholder();

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
		registerItemBlock(registry, AncientSpellcraftBlocks.CRYSTAL_BALL_COGNIZANCE, new ItemBlock(AncientSpellcraftBlocks.CRYSTAL_BALL_COGNIZANCE) {
			@Override
			public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
				Wizardry.proxy.addMultiLineDescription(tooltip, "item.ancientspellcraft:sphere_congizance.desc");
			}
		});

		registerItem(registry, "magic_shield", new ItemMagicShield(EnumRarity.EPIC));
		registerItem(registry, "spectral_shield", new ItemSpectralShield());
		registerItem(registry, "ancient_spellcraft_spell_book", new ItemAncientSpellcraftSpellBook());
		registerItem(registry, "ancient_spellcraft_scroll", new ItemScroll());
		registerItem(registry, "enchanted_name_tag", new ItemEnchantedNameTag());

		registerItem(registry, "wizard_hat_ancient", new ItemAncientHat(WizardryItems.Materials.SILK, 1, EntityEquipmentSlot.HEAD, Element.MAGIC));

		// ====================== Artefacts ======================

		/// ring
		registerItem(registry, "ring_power", new ItemSetArtefact(EnumRarity.UNCOMMON, ItemArtefact.Type.RING, "jewels_of_power", 4, new ArrayList<String>(Arrays.asList("ring_power", "amulet_power", "charm_power_orb"))));
		registerItem(registry, "ring_poison_arrow", new ItemASArtefact(EnumRarity.UNCOMMON, ItemArtefact.Type.RING));
		registerItem(registry, "ring_lodestone", new ItemASArtefact(EnumRarity.RARE, ItemArtefact.Type.RING));
		registerItem(registry, "ring_berserker", new ItemASArtefact(EnumRarity.RARE, ItemArtefact.Type.RING));
		registerItem(registry, "ring_blast", new ItemASArtefact(EnumRarity.RARE, ItemArtefact.Type.RING) {
			@Override
			public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
				tooltip.add(net.minecraft.client.resources.I18n.format("item." + this.getRegistryName() + ".desc"));
				tooltip.add("\u00A7c" + net.minecraft.client.resources.I18n.format("item." + this.getRegistryName() + ".desc2"));
			}
		});

		registerItem(registry, "ring_range", new ItemASArtefact(EnumRarity.RARE, ItemArtefact.Type.RING) {
			@Override
			public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
				tooltip.add(net.minecraft.client.resources.I18n.format("item." + this.getRegistryName() + ".desc"));
				tooltip.add("\u00A7c" + net.minecraft.client.resources.I18n.format("item." + this.getRegistryName() + ".desc2"));
			}
		});
		registerItem(registry, "ring_focus_crystal", new ItemASArtefact(EnumRarity.RARE, ItemArtefact.Type.RING));

		/// amulet
		registerItem(registry, "amulet_mana", new ItemASArtefact(EnumRarity.UNCOMMON, ItemArtefact.Type.AMULET));
		registerItem(registry, "amulet_rabbit", new ItemASArtefact(EnumRarity.RARE, ItemArtefact.Type.AMULET));
		registerItem(registry, "amulet_poison_resistance", new ItemASArtefact(EnumRarity.UNCOMMON, ItemArtefact.Type.AMULET));
		registerItem(registry, "amulet_power", new ItemSetArtefact(EnumRarity.RARE, ItemArtefact.Type.AMULET, "jewels_of_power", 4, new ArrayList<String>(Arrays.asList("ring_power", "amulet_power", "charm_power_orb"))));
		registerItem(registry, "amulet_curse_ward", new ItemASArtefact(EnumRarity.EPIC, ItemArtefact.Type.AMULET));
		registerItem(registry, "amulet_pendant_of_eternity", new ItemEternityPendant(EnumRarity.EPIC, ItemArtefact.Type.AMULET));
		registerItem(registry, "amulet_time_knot", new ItemASArtefact(EnumRarity.EPIC, ItemArtefact.Type.AMULET));

		/// charm
		registerItem(registry, "charm_bucket_coal", new ItemCoalBucket(EnumRarity.UNCOMMON, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_mana_flask", new ItemEverfullManaFlask(EnumRarity.RARE, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_mana_orb", new ItemASArtefact(EnumRarity.RARE, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_knowledge_orb", new ItemKnowledgeOrb(EnumRarity.EPIC, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_power_orb", new ItemSetArtefact(EnumRarity.EPIC, ItemArtefact.Type.CHARM, "jewels_of_power", 4, new ArrayList<String>(Arrays.asList("ring_power", "amulet_power", "charm_power_orb"))));
		registerItem(registry, "charm_rift_bottle", new ItemASArtefact(EnumRarity.EPIC, ItemArtefact.Type.AMULET));
		registerItem(registry, "charm_reanimation", new ItemASArtefact(EnumRarity.EPIC, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_gold_bag", new ItemGoldBag(EnumRarity.RARE, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_evergrowing_crystal", new ItemEvergrowingCrystal(EnumRarity.EPIC, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_shadow_blade", new ItemASArtefact(EnumRarity.EPIC, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_magic_light", new ItemASArtefact(EnumRarity.RARE, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_elemental_grimoire", new ItemASArtefact(EnumRarity.EPIC, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_war_horn", new ItemHorn(EnumRarity.EPIC, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_wand_upgrade", new ItemASArtefact(EnumRarity.EPIC, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_cryostasis", new ItemASArtefact(EnumRarity.EPIC, ItemArtefact.Type.CHARM));

		/// misc
		//		registerItem(registry, "artefact_pensive", new ItemNonBaubleArtefact(EnumRarity.EPIC));

		/// handheld
		registerItem(registry, "shadow_blade", new ItemShadowBlade());
		registerItem(registry, "spectral_fishing_rod", new ItemSpectralFishingRod());
		registerItem(registry, "ice_shield", new ItemIceShield());
		registerItem(registry, "charm_enchanted_needle", new ItemASArtefact(EnumRarity.RARE, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_seed_bag", new ItemASArtefact(EnumRarity.UNCOMMON, ItemArtefact.Type.CHARM));

	}

	public static void registerDispenseBehaviours() {
		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(ancient_spellcraft_scroll, new BehaviourSpellDispense());
	}
}