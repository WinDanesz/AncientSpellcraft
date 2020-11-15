package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftItems;
import electroblob.wizardry.item.ItemArtefact;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.BlockUtils;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Harvest extends Spell {

	public Harvest() {
		super(AncientSpellcraft.MODID, "harvest", EnumAction.NONE, false);
		addProperties(EFFECT_RADIUS);
		soundValues(0.7f, 1.2f, 0.2f);
	}

	@Override
	public boolean requiresPacket() {
		return false;
	}

	@Override
	public boolean canBeCastBy(EntityLiving npc, boolean override) {
		return false;
	}

	@Override
	public boolean cast(World world, EntityPlayer caster, EnumHand hand, int ticksInUse, SpellModifiers modifiers) {

		boolean flag = false;

		List<BlockPos> sphere = BlockUtils.getBlockSphere(caster.getPosition(),
				getProperty(EFFECT_RADIUS).floatValue() * modifiers.get(WizardryItems.blast_upgrade));

		HashMap<BlockPos, Block> replantList = new HashMap<>();

		for (BlockPos pos : sphere) {

			IBlockState state = world.getBlockState(pos);

			if (state.getBlock() instanceof IGrowable) {

				IGrowable plant = (IGrowable) state.getBlock();

				if (!plant.canGrow(world, pos, state, world.isRemote)) {
					if (!world.isRemote) {
						replantList.put(new BlockPos(pos.getX(), pos.getY(), pos.getZ()), world.getBlockState(pos).getBlock());

						world.destroyBlock(pos, true);
						flag = true;

					}
				}

			} else if (state.getBlock() instanceof IPlantable) {
				if (world.getBlockState(pos.offset(EnumFacing.DOWN)).getBlock() == state.getBlock()) {
					if (!world.isRemote) {

						world.destroyBlock(pos, true);
						flag = true;

					}
				}
			}
		}
		if (flag & !replantList.isEmpty() && ItemArtefact.isArtefactActive(caster, AncientSpellcraftItems.charm_seed_bag)) {

			for (Map.Entry<BlockPos, Block> currentEntry : replantList.entrySet()) {
				Item seedItem = null;

				Block currCrop = currentEntry.getValue();
				if (currCrop == Blocks.WHEAT) {
					seedItem = Items.WHEAT_SEEDS;
				} else if (currCrop == Blocks.CARROTS) {
					seedItem = Items.CARROT;
				} else if (currCrop == Blocks.BEETROOTS) {
					seedItem = Items.BEETROOT_SEEDS;
				} else if (currCrop == Blocks.MELON_BLOCK) {
					seedItem = Items.MELON_SEEDS;
				} else if (currCrop == Blocks.POTATOES) {
					seedItem = Items.POTATO;
				} else if (currCrop == Blocks.PUMPKIN) {
					seedItem = Items.PUMPKIN_SEEDS;
				} else if (currCrop == Blocks.REEDS) {
					seedItem = Items.REEDS;
				} else if (currCrop == Blocks.NETHER_WART) {
					seedItem = Items.NETHER_WART;
				}

				for (int i = 0; i < caster.inventory.getSizeInventory(); ++i) {
					ItemStack itemstack = caster.inventory.getStackInSlot(i);

					if (seedItem != null && itemstack.getItem() == seedItem) {
						world.setBlockState(currentEntry.getKey(), currentEntry.getValue().getDefaultState());
						caster.inventory.decrStackSize(i, 1);
					}
				}
				flag = true;
			}

		}
		if (flag) {
			this.playSound(world, caster, ticksInUse, -1, modifiers);
		}

		return flag;
	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == AncientSpellcraftItems.ancient_spellcraft_spell_book || item == AncientSpellcraftItems.ancient_spellcraft_scroll;
	}

}
