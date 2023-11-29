package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.item.ItemSageTome;
import com.windanesz.ancientspellcraft.registry.ASItems;
import com.windanesz.ancientspellcraft.util.ASUtils;
import electroblob.wizardry.data.IStoredVariable;
import electroblob.wizardry.data.Persistence;
import electroblob.wizardry.data.WizardData;
import electroblob.wizardry.item.ItemWizardArmour;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.misc.Forfeit;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.lang.reflect.Field;

import static electroblob.wizardry.util.WandHelper.SELECTED_SPELL_KEY;

public class PerfectTheorySpell extends Spell implements IClassSpell {

	private static final IStoredVariable<NBTTagCompound> PERFECT_THEORY = IStoredVariable.StoredVariable.ofNBT("perfectTheoryData", Persistence.ALWAYS).setSynced();

	public PerfectTheorySpell() {
		super(AncientSpellcraft.MODID, "perfect_theory_spell", SpellActions.SUMMON, false);
		WizardData.registerStoredVariables(PERFECT_THEORY);
	}

	private static NBTTagCompound getData(EntityPlayer player) {
		WizardData data = WizardData.get(player);
		if (data != null) {
			NBTTagCompound nbt = data.getVariable(PERFECT_THEORY);
			if (nbt != null) {
				return nbt;
			}
		}
		return new NBTTagCompound();
	}

	@Override
	public boolean cast(World world, EntityPlayer caster, EnumHand hand, int ticksInUse, SpellModifiers modifiers) {
		return castTheory(world, caster, hand, ticksInUse, modifiers);
	}

	public boolean castTheory(World world, EntityPlayer caster, EnumHand hand, int ticksInUse, SpellModifiers modifiers) {
		ItemStack tome = caster.getHeldItem(hand);

		if (tome.getItem() instanceof ItemSageTome && tome.getTagCompound().hasKey("perfectTheoryData")) {
			int selectedSpell = tome.getTagCompound().getInteger(SELECTED_SPELL_KEY);
			NBTTagCompound theories = tome.getTagCompound().getCompoundTag("perfectTheoryData");
			if (theories.hasKey(String.valueOf(selectedSpell))) {

				NBTTagCompound data = theories.getCompoundTag(String.valueOf(selectedSpell));

				System.out.println("normal cast"); // TODO: REMOVE DEBUG
				//NBTTagCompound data = Experiment.getLastExperiment(caster);
				String effectType = data.getString("effectType");
				String effect = data.getString("effect");

				if (effectType.equals(Experiment.FORFEIT_WEIGHT)) {
					// FIXME: get rid of reflection
					for (Forfeit forfeit : Forfeit.getForfeits()) {

						if (forfeit != null && !world.isRemote) {
							try {
								Field nameField = null;
								nameField = ASUtils.ReflectionUtil.getField(forfeit.getClass(), "name");
								ASUtils.ReflectionUtil.makeAccessible(nameField);
								String name = ((ResourceLocation) nameField.get(forfeit)).toString();

								if (name.equals(effect)) {
									forfeit.apply(world, caster);
									return true;
								}
							}
							catch (Exception e) {}
						}
					}
				} else if (effectType.equals(Experiment.DEBUFF_WEIGHT) || effectType.equals(Experiment.BUFF_WEIGHT)) {
					String potionName = effect.split(",")[0];
					int duration = Integer.parseInt(effect.split(",")[1]);
					Potion potion = ForgeRegistries.POTIONS.getValue(new ResourceLocation(potionName));
					if (potion != null) {
						caster.addPotionEffect(new PotionEffect(potion, duration));
						return true;
					}
				}
				return true;
			}
		}
		return true;
	}

	@Override
	public ItemWizardArmour.ArmourClass getArmourClass() { return ItemWizardArmour.ArmourClass.SAGE; }

	@Override
	public boolean applicableForItem(Item item) {
		return item == ASItems.mystic_spell_book || item == ASItems.mystic_scroll;
	}

	@Override
	public String getDisplayName() {
		return super.getDisplayName();
	}
}
