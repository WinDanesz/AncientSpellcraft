package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftBlocks;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftItems;
import com.windanesz.ancientspellcraft.util.ASUtils;
import electroblob.wizardry.data.IStoredVariable;
import electroblob.wizardry.data.Persistence;
import electroblob.wizardry.data.WizardData;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.InventoryUtils;
import electroblob.wizardry.util.Location;
import electroblob.wizardry.util.NBTExtras;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class MasterBolt extends Spell {

	public static final IStoredVariable<List<Location>> MASTER_BOLT_LOCATIONS_KEY = new IStoredVariable.StoredVariable<List<Location>, NBTTagList>("masterBoltLocationsKey",
			s -> NBTExtras.listToNBT(s, Location::toNBT), t -> new ArrayList<>(NBTExtras.NBTToList(t, Location::fromNBT)), Persistence.RESPAWN).setSynced();

	public MasterBolt() {
		super(AncientSpellcraft.MODID, "master_bolt", SpellActions.POINT_UP, false);
		WizardData.registerStoredVariables(MASTER_BOLT_LOCATIONS_KEY);
		addProperties(DAMAGE);
	}

	public static void storeLocation(World world, EntityPlayer player, BlockPos pos) {
		WizardData data = WizardData.get(player);

		Location here = new Location(pos, player.dimension);

		List<Location> locations = data.getVariable(MASTER_BOLT_LOCATIONS_KEY);
		if (locations == null) { data.setVariable(MASTER_BOLT_LOCATIONS_KEY, locations = new ArrayList<>(3)); }


			if (!locations.contains(here)) {
				locations.add(here);
				if (!world.isRemote) {
					player.sendStatusMessage(new TextComponentTranslation("spell." + AncientSpellcraft.MODID + ":master_bolt.remember"), true);
				}

				if (locations.size() > 3) {
					Location removed = locations.remove(0);
					if (!world.isRemote) {
						player.sendStatusMessage(new TextComponentTranslation("spell." + AncientSpellcraft.MODID + ":master_bolt.forget"), true);
					}
				}

			} else {
				if (locations.isEmpty()) { locations.add(here); } else {
					locations.remove(here); // Prevents duplicates
					if (locations.isEmpty()) { locations.add(here); } else { locations.set(Math.max(locations.size() - 1, 0), here); }
				}
				if (!world.isRemote) {
					player.sendStatusMessage(new TextComponentTranslation("spell." + AncientSpellcraft.MODID + ":master_bolt.confirm"), true);
				}
			}
	}

	@Override
	public boolean cast(World world, EntityPlayer caster, EnumHand hand, int ticksInUse, SpellModifiers modifiers) {
		if (caster != null) {

			WizardData data = WizardData.get(caster);

			if (data != null) {

				List<Location> locations = data.getVariable(MASTER_BOLT_LOCATIONS_KEY);
				if (locations == null || locations.isEmpty()) {
					if(InventoryUtils.doesPlayerHaveItem(caster, AncientSpellcraftItems.master_bolt)) return false;

					ASUtils.giveStackToPlayer(caster, new ItemStack(AncientSpellcraftItems.master_bolt));
					return true;
				}

				Location destination = locations.get(locations.size() - 1);
				if (destination.dimension == caster.dimension) {
					if (world.getBlockState(destination.pos).getBlock() == AncientSpellcraftBlocks.master_bolt) {
						if (!world.isRemote) {
							world.setBlockToAir(destination.pos);
						}
						caster.setPositionAndUpdate(destination.pos.getX() + 0.5, destination.pos.getY(), destination.pos.getZ() + 0.5);
					}
					locations.remove(destination);
					data.setVariable(MASTER_BOLT_LOCATIONS_KEY, locations);
					data.sync();
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == AncientSpellcraftItems.ancient_spellcraft_spell_book || item == AncientSpellcraftItems.ancient_spellcraft_scroll;
	}
}
