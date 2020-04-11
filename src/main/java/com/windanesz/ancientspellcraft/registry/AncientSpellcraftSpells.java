package com.windanesz.ancientspellcraft.registry;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.spell.ConjureWater;
import com.windanesz.ancientspellcraft.spell.CurseOfEnder;
import com.windanesz.ancientspellcraft.spell.Drought;
import com.windanesz.ancientspellcraft.spell.Extinguish;
import com.windanesz.ancientspellcraft.spell.HellGate;
import com.windanesz.ancientspellcraft.spell.NaturesSprout;
import com.windanesz.ancientspellcraft.spell.TameAnimal;
import com.windanesz.ancientspellcraft.spell.WillOWisp;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.spell.SpellConjuration;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nonnull;

@ObjectHolder(AncientSpellcraft.MODID)
@EventBusSubscriber
public final class AncientSpellcraftSpells {

	private AncientSpellcraftSpells() {} // no instances

	private static final String modId = AncientSpellcraft.MODID;

	@Nonnull
	@SuppressWarnings("ConstantConditions")
	private static <T> T placeholder() { return null; }

	public static final Spell hellgate = placeholder();
	public static final Spell tameanimal = placeholder();
//	public static final Spell summonlichelord = placeholder();
	public static final Spell extinguish = placeholder();
	public static final Spell curse_of_ender = placeholder();
	public static final Spell conjure_water = placeholder();
	public static final Spell conjure_shield = placeholder();
	public static final Spell will_o_wisp = placeholder();
	public static final Spell natures_sprout = placeholder();

	@SubscribeEvent
	public static void register(RegistryEvent.Register<Spell> event) {

		IForgeRegistry<Spell> registry = event.getRegistry();

		Item[] asSpellItems = {AncientSpellcraftItems.ancient_spellcraft_spell_book, AncientSpellcraftItems.ancient_spellcraft_scroll};

		registry.register(new HellGate(modId, "hellgate", EnumAction.BLOCK, false));
		registry.register(new TameAnimal(modId, "tameanimal", false, EnumAction.BLOCK));
//		registry.register(new SummonLicheLord(modId, "summonlichelord", EntitySkeletonMinion::new)); // unused
		registry.register(new Extinguish(modId, "extinguish"));
		registry.register(new CurseOfEnder(modId, "curse_of_ender", EnumAction.NONE, false));
		registry.register(new ConjureWater(modId, "conjure_water", EnumAction.BLOCK, false));
		registry.register(new SpellConjuration(modId, "conjure_shield", AncientSpellcraftItems.spectral_shield) {
			@Override
			public boolean applicableForItem(Item item) {
				return item == AncientSpellcraftItems.ancient_spellcraft_spell_book || item == AncientSpellcraftItems.ancient_spellcraft_scroll;
			}
		});
		registry.register(new Drought(modId, "drought", EnumAction.BLOCK, false));
		registry.register(new WillOWisp(modId, "will_o_wisp"));
		registry.register(new NaturesSprout(modId, "natures_sprout", EnumAction.BLOCK, false));

	}

}
