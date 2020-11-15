package com.windanesz.ancientspellcraft.data;

import com.google.common.collect.Maps;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftSpells;
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

public enum SpellComponentList {

	ANTIMAGIC_FIELD(AncientSpellcraftSpells.antimagic_field, new ItemStack(Items.DIAMOND), new ItemStack(WizardryItems.grand_crystal)),
	ARCANE_AEGIS(AncientSpellcraftSpells.arcane_aegis, new ItemStack(WizardryItems.astral_diamond), new ItemStack(WizardryItems.grand_crystal), new ItemStack(Items.GOLDEN_APPLE)),
	ARCANE_AUGMENTATION(AncientSpellcraftSpells.arcane_augmentation, new ItemStack(WizardryItems.grand_crystal), new ItemStack(WizardryItems.blast_upgrade)),
	ARCANE_BEAM(AncientSpellcraftSpells.arcane_beam, new ItemStack(WizardryItems.grand_crystal), new ItemStack(Items.QUARTZ)),
	ARCANE_MAGNETISM(AncientSpellcraftSpells.arcane_magnetism, new ItemStack(Items.ENDER_EYE), new ItemStack(Item.getItemFromBlock(Blocks.REDSTONE_TORCH))),
	ASPECT_HUNTER(AncientSpellcraftSpells.aspect_hunter, new ItemStack(Items.BOW), new ItemStack(Items.LEATHER_HELMET), PotionUtils.addPotionToItemStack(new ItemStack(Items.SPLASH_POTION), PotionTypes.STRENGTH)),
	BLOCKWEAVING(AncientSpellcraftSpells.blockweaving, new ItemStack(WizardryBlocks.crystal_block, 1, 0), new ItemStack(Items.GOLDEN_PICKAXE)),
	BULWARK(AncientSpellcraftSpells.bulwark, new ItemStack(WizardryItems.magic_crystal, 1, 5), new ItemStack(Items.SHIELD), new ItemStack(WizardryItems.grand_crystal)),
	CHANNEL_POWER(AncientSpellcraftSpells.channel_power, new ItemStack(WizardryItems.astral_diamond), new ItemStack(WizardryItems.astral_diamond), new ItemStack(WizardryItems.astral_diamond)),
	CONTINUITY_CHANT(AncientSpellcraftSpells.continuity_charm, new ItemStack(WizardryItems.grand_crystal), new ItemStack(WizardryItems.duration_upgrade)),
	DISPEL_LESSER_MAGIC(AncientSpellcraftSpells.dispel_lesser_magic, new ItemStack(Items.GLASS_BOTTLE), new ItemStack(WizardryItems.grand_crystal), new ItemStack(WizardryItems.siphon_upgrade)),
	DISPEL_GREATER_MAGIC(AncientSpellcraftSpells.dispel_greater_magic, new ItemStack(Items.GLASS_BOTTLE), new ItemStack(WizardryItems.astral_diamond), new ItemStack(WizardryItems.siphon_upgrade)),
	FORCEFEND(AncientSpellcraftSpells.forcefend, new ItemStack(WizardryItems.astral_diamond), new ItemStack(Items.GOLDEN_APPLE,0,0), new ItemStack(Items.SHIELD)),
	INTENSIFYING_FOCUS(AncientSpellcraftSpells.intensifying_focus, new ItemStack(WizardryItems.grand_crystal), new ItemStack(WizardryItems.attunement_upgrade)),
	MAGELIGHT(AncientSpellcraftSpells.magelight, new ItemStack(Item.getItemFromBlock(Blocks.GLOWSTONE)), new ItemStack(Item.getItemFromBlock(Blocks.TORCH)), new ItemStack(Items.FIRE_CHARGE)),
	MANA_FLARE(AncientSpellcraftSpells.mana_flare, new ItemStack(WizardryItems.magic_wand), new ItemStack(WizardryItems.grand_crystal)),
	PROJECTILE_WARD(AncientSpellcraftSpells.projectile_ward, new ItemStack(Items.LEATHER_CHESTPLATE), new ItemStack(Items.SHIELD), new ItemStack(Items.ARROW)),
	SILENCING_SIGIL(AncientSpellcraftSpells.silencing_sigil, new ItemStack(Blocks.NOTEBLOCK), new ItemStack(Item.getItemFromBlock(Blocks.REDSTONE_BLOCK))),
	SKULL_SENTINEL(AncientSpellcraftSpells.skull_sentinel, new ItemStack(Items.SKULL), new ItemStack(WizardryItems.grand_crystal)),
	WATER_WALKING(AncientSpellcraftSpells.water_walking, new ItemStack(Items.WATER_BUCKET), new ItemStack(Items.DIAMOND_BOOTS), new ItemStack(WizardryItems.grand_crystal)),
	ESSENCE_EXTRACTION(AncientSpellcraftSpells.essence_extraction, new ItemStack(WizardryItems.large_mana_flask), new ItemStack(Item.getItemFromBlock(WizardryBlocks.receptacle)), new ItemStack(WizardryItems.grand_crystal)),
	CONDUIT(AncientSpellcraftSpells.conduit, new ItemStack(WizardryItems.large_mana_flask), new ItemStack(Item.getItemFromBlock(WizardryBlocks.receptacle)), new ItemStack(WizardryItems.grand_crystal));

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


