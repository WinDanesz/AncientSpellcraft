package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.item.ItemBattlemageSword;
import com.windanesz.ancientspellcraft.registry.ASItems;
import electroblob.wizardry.item.ItemArtefact;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.util.InventoryUtils;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.HashMap;

public class RunewordImbue extends Runeword {
	public static final String POTION_TAG = "Potion";

	public RunewordImbue() {
		super("runeword_imbue", SpellActions.POINT_UP, false);
		addProperties(EFFECT_DURATION, CHARGES);
	}

	@Override
	public boolean onAboutToHitEntity(World world, EntityLivingBase caster, EntityLivingBase target, EnumHand hand, ItemStack sword, SpellModifiers modifiers, boolean charged) {
		HashMap<Runeword, NBTTagCompound> data = ItemBattlemageSword.getTemporaryRunewordData(sword);
		if (data.containsKey(this) ) {
			NBTTagCompound imbuement = data.get(this);
			if (imbuement.hasKey(POTION_TAG)) {
				PotionEffect effect = new PotionEffect(ForgeRegistries.POTIONS.getValue(new ResourceLocation(imbuement.getString(POTION_TAG))),
						(int) (getProperty(EFFECT_DURATION).intValue() * modifiers.get(WizardryItems.duration_upgrade)));
				target.addPotionEffect(effect);
				spendCharge(sword);
			}
		}
		return true;
	}

	@Override
	public boolean cast(World world, EntityPlayer caster, EnumHand hand, int ticksInUse, SpellModifiers modifiers) {
		if (!world.isRemote) {

			for (ItemStack stack : InventoryUtils.getHotbar(caster)) {
				if (stack.getItem() instanceof ItemPotion) {
					for (PotionEffect potioneffect : PotionUtils.getEffectsFromStack(stack)) {
						if (!potioneffect.getPotion().isInstant()) {
							int charges = getProperty(CHARGES).intValue();
							if ((ItemArtefact.isArtefactActive(caster, ASItems.charm_glyph_imbuement))) {
								charges *= 6;
							}
							ItemBattlemageSword.setActiveRuneword(caster.getHeldItem(hand), this, charges);
							NBTTagCompound imbuement = new NBTTagCompound();
							imbuement.setString(POTION_TAG, potioneffect.getPotion().getRegistryName().toString());
							ItemBattlemageSword.setTemporaryRuneWordData(caster.getHeldItem(hand), this, imbuement);
							stack.shrink(1);
							return true;
						}
					}
				}
			}

		}
		return false;
	}

	@Override
	public boolean cast(World world, EntityLiving caster, EnumHand hand, int ticksInUse, EntityLivingBase target, SpellModifiers modifiers) {
		return super.cast(world, caster, hand, ticksInUse, target, modifiers);
	}

	@Override
	public boolean canBeCastBy(TileEntityDispenser dispenser) {
		return false;
	}

}
