package com.windanesz.ancientspellcraft.registry;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.item.ItemAncientSpellcraftSpellBook;
import com.windanesz.ancientspellcraft.item.ItemEnchantedNameTag;
import com.windanesz.ancientspellcraft.item.ItemMagicShield;
import com.windanesz.ancientspellcraft.item.ItemSpectralShield;
import electroblob.wizardry.item.ItemScroll;
import electroblob.wizardry.misc.BehaviourSpellDispense;
import electroblob.wizardry.registry.WizardryTabs;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nonnull;

@ObjectHolder(AncientSpellcraft.MODID)
@Mod.EventBusSubscriber
public final class AncientSpellcraftItems {

	private AncientSpellcraftItems() {} // No instances!

	@Nonnull
	@SuppressWarnings("ConstantConditions")
	private static <T> T placeholder() { return null; }

	public static final Item ancient_spellcraft_spell_book = placeholder();
	public static final Item ancient_spellcraft_scroll = placeholder();

	public static final Item magic_shield = placeholder();
	public static final Item spectral_shield = placeholder();
	public static final Item enchanted_name_tag = placeholder();


	// below registry methods are courtesy of EB
	public static void registerItem(IForgeRegistry<Item> registry, String name, Item item){
		registerItem(registry, name, item, false);
	}

	public static void registerItem(IForgeRegistry<Item> registry, String name, Item item, boolean setTabIcon){
		item.setRegistryName(AncientSpellcraft.MODID, name);
		item.setTranslationKey(item.getRegistryName().toString());
		registry.register(item);

		if(setTabIcon && item.getCreativeTab() instanceof WizardryTabs.CreativeTabSorted){
			((WizardryTabs.CreativeTabSorted)item.getCreativeTab()).setIconItem(new ItemStack(item));
		}

		if(item.getCreativeTab() instanceof WizardryTabs.CreativeTabListed){
			((WizardryTabs.CreativeTabListed)item.getCreativeTab()).order.add(item);
		}
	}

	private static void registerItemBlock(IForgeRegistry<Item> registry, Block block){
		Item itemblock = new ItemBlock(block).setRegistryName(block.getRegistryName());
		registry.register(itemblock);

		if(block.getCreativeTab() instanceof WizardryTabs.CreativeTabListed){
			((WizardryTabs.CreativeTabListed)block.getCreativeTab()).order.add(itemblock);
		}
	}



	@SubscribeEvent
	public static void register(RegistryEvent.Register<Item> event){

		IForgeRegistry<Item> registry = event.getRegistry();

		registerItem(registry, "magic_shield", new ItemMagicShield(EnumRarity.EPIC));
		registerItem(registry, "spectral_shield", new ItemSpectralShield());
		registerItem(registry, "ancient_spellcraft_spell_book", new ItemAncientSpellcraftSpellBook());
		registerItem(registry, "ancient_spellcraft_scroll", new ItemScroll());
		registerItem(registry, "enchanted_name_tag", new ItemEnchantedNameTag());
	}

	public static void registerDispenseBehaviours() {
		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(ancient_spellcraft_scroll, new BehaviourSpellDispense());
	}
}