package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.registry.AncientSpellcraftItems;
import electroblob.wizardry.item.ItemArtefact;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IceCream extends SpellConjurationAS {

	// These are being reused for the itemStack nbt as well
	public static final String HEALING_AMOUNT = "healing_amount";
	public static final String HUNGER_RESTORE_AMOUNT = "hunger_restore_amount";
	public static final String SATURATION_AMOUNT = "saturation_amount";

	public IceCream() {
		super("ice_cream", AncientSpellcraftItems.ice_cream);
		addProperties(HEALING_AMOUNT, HUNGER_RESTORE_AMOUNT, SATURATION_AMOUNT);
	}

	@Override
	protected void addItemExtras(EntityPlayer caster, ItemStack stack, SpellModifiers modifiers) {
		if (!stack.hasTagCompound())
			stack.setTagCompound(new NBTTagCompound());

		float heal = getProperty(HEALING_AMOUNT).floatValue() * modifiers.get(SpellModifiers.POTENCY);

		if (ItemArtefact.isArtefactActive(caster, AncientSpellcraftItems.charm_ice_cream)) {
			Potion[] potions = {MobEffects.HEALTH_BOOST, MobEffects.REGENERATION, MobEffects.SPEED,
					MobEffects.HEALTH_BOOST, MobEffects.ABSORPTION, MobEffects.STRENGTH,
					MobEffects.FIRE_RESISTANCE, MobEffects.INVISIBILITY, MobEffects.HASTE, MobEffects.NIGHT_VISION, MobEffects.LUCK};

			@SuppressWarnings("unchecked")
			List<Potion> effects = new ArrayList<>(Arrays.asList(potions));
			String potion = effects.get(caster.world.rand.nextInt(effects.size())).getRegistryName().getPath();
			stack.getTagCompound().setString("potion", potion);

			heal += 0.5f;
		}

		stack.getTagCompound().setFloat(HEALING_AMOUNT, heal);
		stack.getTagCompound().setInteger(HUNGER_RESTORE_AMOUNT, getProperty(HUNGER_RESTORE_AMOUNT).intValue());
		stack.getTagCompound().setInteger(SATURATION_AMOUNT, getProperty(SATURATION_AMOUNT).intValue());
	}
}
