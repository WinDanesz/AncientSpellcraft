package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.registry.ASItems;
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

	private static final float DEFAULT_RADIUS = 5.0f;

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
		boolean hasActionOccurred = false;

		float radius = getProperty(EFFECT_RADIUS).floatValue() * modifiers.get(WizardryItems.blast_upgrade);
		List<BlockPos> sphere = BlockUtils.getBlockSphere(caster.getPosition(), radius);
		Map<BlockPos, Block> replantList = new HashMap<>();

		// Process the blocks in the spell's radius
		for (BlockPos pos : sphere) {
			IBlockState state = world.getBlockState(pos);
			Block block = state.getBlock();

			if (block instanceof IGrowable && !((IGrowable) block).canGrow(world, pos, state, world.isRemote)) {
				handleHarvest(world, pos, replantList);
				hasActionOccurred = true;
			} else if (block instanceof IPlantable && isPlantableBlock(state, world, pos)) {
				handleHarvest(world, pos, replantList);
				hasActionOccurred = true;
			}
		}

		// Replant harvested crops if necessary
		if (hasActionOccurred && !replantList.isEmpty() && ItemArtefact.isArtefactActive(caster, ASItems.charm_seed_bag)) {
			replantCrops(world, caster, replantList);
		}

		// Play the sound effect if any action occurred
		if (hasActionOccurred) {
			this.playSound(world, caster, ticksInUse, -1, modifiers);
		}

		return hasActionOccurred;
	}

	private void handleHarvest(World world, BlockPos pos, Map<BlockPos, Block> replantList) {
		if (!world.isRemote) {
			replantList.put(pos, world.getBlockState(pos).getBlock());
			world.destroyBlock(pos, true);
		}
	}

	private boolean isPlantableBlock(IBlockState state, World world, BlockPos pos) {
		return world.getBlockState(pos.offset(EnumFacing.DOWN)).getBlock() == state.getBlock();
	}

	private void replantCrops(World world, EntityPlayer caster, Map<BlockPos, Block> replantList) {
		for (Map.Entry<BlockPos, Block> entry : replantList.entrySet()) {
			Item seedItem = getSeedItem(entry.getValue());

			if (seedItem != null) {
				for (int i = 0; i < caster.inventory.getSizeInventory(); ++i) {
					ItemStack itemStack = caster.inventory.getStackInSlot(i);
					if (itemStack.getItem() == seedItem) {
						world.setBlockState(entry.getKey(), entry.getValue().getDefaultState());
						caster.inventory.decrStackSize(i, 1);
						break;
					}
				}
			}
		}
	}

	private Item getSeedItem(Block crop) {
		if (crop == Blocks.WHEAT) return Items.WHEAT_SEEDS;
		if (crop == Blocks.CARROTS) return Items.CARROT;
		if (crop == Blocks.BEETROOTS) return Items.BEETROOT_SEEDS;
		if (crop == Blocks.MELON_BLOCK) return Items.MELON_SEEDS;
		if (crop == Blocks.POTATOES) return Items.POTATO;
		if (crop == Blocks.PUMPKIN) return Items.PUMPKIN_SEEDS;
		if (crop == Blocks.REEDS) return Items.REEDS;
		if (crop == Blocks.NETHER_WART) return Items.NETHER_WART;
		return null;
	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == ASItems.ancient_spellcraft_spell_book || item == ASItems.ancient_spellcraft_scroll;
	}
}
