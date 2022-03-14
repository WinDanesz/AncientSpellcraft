package com.windanesz.ancientspellcraft.data;

import com.google.common.collect.Maps;
import com.windanesz.ancientspellcraft.registry.ASSpells;
import electroblob.wizardry.constants.Tier;
import electroblob.wizardry.registry.WizardryBlocks;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.spell.Spell;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * All ancient element spells are craftable, this enum lists their crafting components
 */
public enum SpellComponentList {

	ANTIMAGIC_FIELD(ASSpells.antimagic_field, new ItemStack(Items.DIAMOND), new ItemStack(WizardryItems.grand_crystal)),
	ARCANE_AEGIS(ASSpells.arcane_aegis, new ItemStack(WizardryItems.astral_diamond), new ItemStack(WizardryItems.grand_crystal), new ItemStack(Items.GOLDEN_APPLE)),
	ARCANE_AUGMENTATION(ASSpells.arcane_augmentation, new ItemStack(WizardryItems.grand_crystal), new ItemStack(WizardryItems.blast_upgrade)),
	ARCANE_BEAM(ASSpells.arcane_beam, new ItemStack(WizardryItems.grand_crystal), new ItemStack(Items.QUARTZ)),
	ARCANE_MAGNETISM(ASSpells.arcane_magnetism, new ItemStack(Items.ENDER_EYE), new ItemStack(Item.getItemFromBlock(Blocks.REDSTONE_TORCH))),
	ASPECT_HUNTER(ASSpells.aspect_hunter, new ItemStack(Items.BOW), new ItemStack(Items.LEATHER_HELMET), PotionUtils.addPotionToItemStack(new ItemStack(Items.SPLASH_POTION), PotionTypes.STRENGTH)),
	MIGHT_AND_MAGIC(ASSpells.might_and_magic, new ItemStack(WizardryItems.medium_mana_flask), PotionUtils.addPotionToItemStack(new ItemStack(Items.SPLASH_POTION), PotionTypes.HEALING)),
	BLOCKWEAVING(ASSpells.blockweaving, new ItemStack(WizardryBlocks.crystal_block, 1, 0), new ItemStack(Items.GOLDEN_PICKAXE)),
	BUBBLE_HEAD(ASSpells.bubble_head, new ItemStack(Item.getItemFromBlock(Blocks.GLASS)), new ItemStack(Items.WATER_BUCKET), new ItemStack(Items.GLOWSTONE_DUST)),
	BULWARK(ASSpells.bulwark, new ItemStack(WizardryItems.magic_crystal, 1, 5), new ItemStack(Items.SHIELD), new ItemStack(WizardryItems.grand_crystal)),
	CHANNEL_POWER(ASSpells.channel_power, new ItemStack(WizardryItems.astral_diamond), new ItemStack(WizardryItems.astral_diamond), new ItemStack(WizardryItems.astral_diamond)),
	CHANNEL_EFFECT(ASSpells.channel_effect, new ItemStack(WizardryItems.grand_crystal)),
	CONTINUITY_CHANT(ASSpells.continuity_charm, new ItemStack(WizardryItems.grand_crystal), new ItemStack(WizardryItems.duration_upgrade)),
	CRYSTAL_MINE(ASSpells.crystal_mine, new ItemStack(Item.getItemFromBlock(WizardryBlocks.crystal_block)), new ItemStack(Item.getItemFromBlock(Blocks.TNT))),
	CONDUIT(ASSpells.conduit, new ItemStack(WizardryItems.large_mana_flask), new ItemStack(Item.getItemFromBlock(WizardryBlocks.receptacle)), new ItemStack(WizardryItems.grand_crystal)),
	COVENANT(ASSpells.covenant, new ItemStack(WizardryItems.master_wand), new ItemStack(Item.getItemFromBlock(Blocks.DIAMOND_BLOCK)), new ItemStack(WizardryItems.astral_diamond)),
	DISPEL_LESSER_MAGIC(ASSpells.dispel_lesser_magic, new ItemStack(Items.GLASS_BOTTLE), new ItemStack(WizardryItems.grand_crystal), new ItemStack(WizardryItems.siphon_upgrade)),
	DISPEL_GREATER_MAGIC(ASSpells.dispel_greater_magic, new ItemStack(Items.GLASS_BOTTLE), new ItemStack(WizardryItems.astral_diamond), new ItemStack(WizardryItems.siphon_upgrade)),
	EAGLE_EYE(ASSpells.eagle_eye, new ItemStack(Items.ENDER_EYE), new ItemStack(WizardryItems.magic_crystal), new ItemStack(Items.FEATHER)),
	FARSIGHT(ASSpells.farsight, new ItemStack(Items.ENDER_EYE), new ItemStack(WizardryItems.magic_crystal), new ItemStack(Item.getItemFromBlock(Blocks.GLASS_PANE))),
	FORCEFEND(ASSpells.forcefend, new ItemStack(WizardryItems.astral_diamond), new ItemStack(Items.GOLDEN_APPLE), new ItemStack(Items.SHIELD)),
	INTENSIFYING_FOCUS(ASSpells.intensifying_focus, new ItemStack(WizardryItems.grand_crystal), new ItemStack(WizardryItems.attunement_upgrade)),
	MAGELIGHT(ASSpells.magelight, new ItemStack(Item.getItemFromBlock(Blocks.GLOWSTONE)), new ItemStack(Item.getItemFromBlock(Blocks.TORCH)), new ItemStack(Items.FIRE_CHARGE)),
	MANA_FLARE(ASSpells.mana_flare, new ItemStack(WizardryItems.magic_wand), new ItemStack(WizardryItems.grand_crystal)),
	MANA_VORTEX(ASSpells.mana_vortex, new ItemStack(Item.getItemFromBlock(WizardryBlocks.receptacle)), new ItemStack(WizardryItems.grand_crystal), new ItemStack(Items.BLAZE_POWDER)),
	PROJECTILE_WARD(ASSpells.projectile_ward, new ItemStack(Items.LEATHER_CHESTPLATE), new ItemStack(Items.SHIELD), new ItemStack(Items.ARROW)),
	PRISMATIC_SPRAY(ASSpells.prismatic_spray, new ItemStack(Item.getItemFromBlock(Blocks.GLASS_PANE)), new ItemStack(WizardryItems.grand_crystal), new ItemStack(WizardryItems.astral_diamond)),
	SILENCING_SIGIL(ASSpells.silencing_sigil, new ItemStack(Blocks.NOTEBLOCK), new ItemStack(Item.getItemFromBlock(Blocks.REDSTONE_BLOCK))),
	SKULL_SENTINEL(ASSpells.skull_sentinel, new ItemStack(Items.SKULL), new ItemStack(WizardryItems.grand_crystal)),
	WATER_WALKING(ASSpells.water_walking, new ItemStack(Items.WATER_BUCKET), new ItemStack(Items.DIAMOND_BOOTS), new ItemStack(WizardryItems.grand_crystal)),
	ESSENCE_EXTRACTION(ASSpells.essence_extraction, new ItemStack(WizardryItems.large_mana_flask), new ItemStack(Item.getItemFromBlock(WizardryBlocks.receptacle)), new ItemStack(WizardryItems.grand_crystal)),

	// 1.2.0 spells

	// 1.2 spells
	CONTINGENCY_FIRE(ASSpells.contingency_fire, new ItemStack(WizardryItems.blank_scroll), PotionUtils.addPotionToItemStack(new ItemStack(Items.SPLASH_POTION), PotionTypes.FIRE_RESISTANCE), new ItemStack(Item.getItemFromBlock(WizardryBlocks.crystal_block))),
	CONTINGENCY_FALL(ASSpells.contingency_fire, new ItemStack(WizardryItems.blank_scroll), PotionUtils.addPotionToItemStack(new ItemStack(Items.SPLASH_POTION), PotionTypes.LONG_SWIFTNESS), new ItemStack(Item.getItemFromBlock(WizardryBlocks.crystal_block))),
	CONTINGENCY_DAMAGE(ASSpells.contingency_damage, new ItemStack(WizardryItems.blank_scroll), PotionUtils.addPotionToItemStack(new ItemStack(Items.SPLASH_POTION), PotionTypes.HARMING), new ItemStack(Item.getItemFromBlock(WizardryBlocks.crystal_block))),
	CONTINGENCY_CRITICAL_HEALTH(ASSpells.contingency_critical_health, new ItemStack(WizardryItems.blank_scroll), PotionUtils.addPotionToItemStack(new ItemStack(Items.SPLASH_POTION), PotionTypes.STRONG_HEALING), new ItemStack(Item.getItemFromBlock(WizardryBlocks.crystal_block))),
	CONTINGENCY_DEATH(ASSpells.contingency_death, new ItemStack(WizardryItems.blank_scroll), PotionUtils.addPotionToItemStack(new ItemStack(Items.SPLASH_POTION), PotionTypes.STRONG_HARMING), new ItemStack(Item.getItemFromBlock(WizardryBlocks.crystal_block))),
	CONTINGENCY_DROWNING(ASSpells.contingency_drowning, new ItemStack(WizardryItems.blank_scroll), PotionUtils.addPotionToItemStack(new ItemStack(Items.SPLASH_POTION), PotionTypes.WATER_BREATHING), new ItemStack(Item.getItemFromBlock(WizardryBlocks.crystal_block))),
	CONTINGENCY_HOSTILE_SPELLCAST(ASSpells.contingency_hostile_spellcast, new ItemStack(WizardryItems.blank_scroll), new ItemStack(WizardryItems.wizard_hat), new ItemStack(Item.getItemFromBlock(WizardryBlocks.crystal_block))),
	CONTINGENCY_IMMOBILITY(ASSpells.contingency_immobility, new ItemStack(WizardryItems.blank_scroll), PotionUtils.addPotionToItemStack(new ItemStack(Items.SPLASH_POTION), PotionTypes.SLOWNESS), new ItemStack(Item.getItemFromBlock(WizardryBlocks.crystal_block))),
	METAMAGIC_PROJECTILE(ASSpells.metamagic_projectile, new ItemStack(WizardryItems.magic_wand), new ItemStack(Item.getItemFromBlock(WizardryBlocks.crystal_block))),
	WIZARD_SHIELD(ASSpells.wizard_shield, new ItemStack(WizardryItems.magic_wand), new ItemStack(Item.getItemFromBlock(WizardryBlocks.crystal_block))),
	SHRINK_SELF(ASSpells.shrink_self, new ItemStack(Items.SPECKLED_MELON), new ItemStack(Items.GOLDEN_APPLE)),
	GROW_SELF(ASSpells.grow_self, new ItemStack(Items.GOLDEN_CARROT), new ItemStack(Items.GOLDEN_APPLE)),
	MASS_SHRINK(ASSpells.mass_shrink, new ItemStack(Items.SPECKLED_MELON), new ItemStack(Items.GOLDEN_APPLE), new ItemStack(WizardryItems.grand_crystal)),
	MASS_GROWTH(ASSpells.mass_growth, new ItemStack(Items.GOLDEN_CARROT), new ItemStack(Items.GOLDEN_APPLE), new ItemStack(WizardryItems.grand_crystal)),
	PERMASHRINK(ASSpells.permashrink, new ItemStack(Items.SPECKLED_MELON), new ItemStack(Items.GOLDEN_APPLE), new ItemStack(WizardryItems.astral_diamond)),
	PERMAGROWTH(ASSpells.permagrowth, new ItemStack(Items.GOLDEN_CARROT), new ItemStack(Items.GOLDEN_APPLE), new ItemStack(WizardryItems.astral_diamond)),
	WORDS_OF_UNBINDING(ASSpells.words_of_unbinding, new ItemStack(Item.getItemFromBlock(Blocks.ENCHANTING_TABLE)), new ItemStack(WizardryItems.grand_crystal)),
	ASTRAL_PROJECTION(ASSpells.astral_projection, new ItemStack(Items.GHAST_TEAR), new ItemStack(WizardryItems.grand_crystal), new ItemStack(Items.GOLDEN_APPLE)),
	DIMENSIONAL_ANCHOR(ASSpells.dimensional_anchor, PotionUtils.addPotionToItemStack(new ItemStack(Items.SPLASH_POTION), PotionTypes.WEAKNESS)),
	CONJURE_LESSER_SENTRY(ASSpells.conjure_lesser_sentry, new ItemStack(Item.getItemFromBlock(Blocks.GOLD_BLOCK)), new ItemStack(WizardryItems.magic_crystal, 1,0)),
	CONJURE_GREATER_SENTRY(ASSpells.conjure_greater_sentry, new ItemStack(Item.getItemFromBlock(Blocks.DIAMOND_BLOCK)), new ItemStack(WizardryItems.grand_crystal));


	public static final Map<Spell, SpellComponentList> nameIndex =
			Maps.newHashMapWithExpectedSize(SpellComponentList.values().length);

	static {
		for (SpellComponentList spellComponent : SpellComponentList.values()) {
			nameIndex.put(spellComponent.spell, spellComponent);
		}
	}

	public static SpellComponentList lookupBySpell(Spell name) {
		return nameIndex.get(name);
	}

	public static boolean containsSpell(Spell spell) {
		return nameIndex.keySet().contains(spell);
	}

	public static List<Spell> getSpellListByTier(Tier tier) {
		List<Spell> spells = new ArrayList<>();

		for (Spell spell : nameIndex.keySet()) {
			if (spell.getTier() == tier) {
				spells.add(spell);
			}
		}
		return spells;
	}

	SpellComponentList(Spell spell, ItemStack... components) {
		this.spell = spell;
		this.components = components;
	}

	private final Spell spell;
	private final ItemStack[] components;

	public Spell getSpell() {
		return spell;
	}

	public ItemStack[] getComponents() {
		return components;
	}

	}


