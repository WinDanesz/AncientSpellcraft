package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.Settings;
import com.windanesz.ancientspellcraft.item.ItemNewArtefact;
import com.windanesz.ancientspellcraft.registry.ASItems;
import electroblob.wizardry.Wizardry;
import electroblob.wizardry.constants.Constants;
import electroblob.wizardry.item.ISpellCastingItem;
import electroblob.wizardry.item.ItemArtefact;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.spell.Mine;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.BlockUtils;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class MineAS extends Mine {

	private static final Method getSilkTouchDrop;

	static {
		getSilkTouchDrop = ObfuscationReflectionHelper.findMethod(Block.class, "func_180643_i", ItemStack.class, IBlockState.class);
	}

	public MineAS() {
		super();

		////// Overrides //////
		AncientSpellcraft.logger.info("Overriding default Electroblobs's Wizardry spell " + this.getRegistryName() + " to apply changes by Ancient Spellcraft.");

		// must use the original networkID of the base spell
		int id = Settings.spellCompatSettings.mineSpellNetworkID;
		ObfuscationReflectionHelper.setPrivateValue(Spell.class, this, id, "id");

		// most call this or the networkIDs will be pushed, because calling super() increments the next ID again!
		int nextSpellId = ObfuscationReflectionHelper.getPrivateValue(Spell.class, this, "nextSpellId");
		//  decrement it back...
		nextSpellId--;
		ObfuscationReflectionHelper.setPrivateValue(Spell.class, this, nextSpellId, "nextSpellId");
		////// Overrides //////

	}

	/**
	 * Basically the same as {@link Mine#onBlockHit(World, BlockPos, EnumFacing, Vec3d, EntityLivingBase, Vec3d, int, SpellModifiers)}, with the addition of the AS-provided Fortune artefact
	 */
	@Override
	protected boolean onBlockHit(World world, BlockPos pos, EnumFacing side, Vec3d hit, EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {
		// Needs to be outside because it gets run on the client-side
		if (caster instanceof EntityPlayer) {
			if (caster.getHeldItemMainhand().getItem() instanceof ISpellCastingItem) {
				caster.swingArm(EnumHand.MAIN_HAND);
			} else if (caster.getHeldItemOffhand().getItem() instanceof ISpellCastingItem) {
				caster.swingArm(EnumHand.OFF_HAND);
			}
		}

		if (!world.isRemote) {

			if (BlockUtils.isBlockUnbreakable(world, pos))
				return false;
			// Reworked to respect the rules, but since we might break multiple blocks this is left as an optimisation
			if (!EntityUtils.canDamageBlocks(caster, world))
				return false;

			IBlockState state = world.getBlockState(pos);
			// The maximum harvest level as determined by the potency multiplier. The + 0.5f is so that
			// weird float processing doesn't incorrectly round it down.
			int harvestLevel = (int) ((modifiers.get(SpellModifiers.POTENCY) - 1) / Constants.POTENCY_INCREASE_PER_TIER + 0.5f);

			if (harvestLevel > 0)
				harvestLevel--; // Shifts them all down one since normally novice wands give some potency

			// The >= 3 is to allow master earth wands to break anything.
			if (state.getBlock().getHarvestLevel(state) <= harvestLevel || harvestLevel >= 3) {

				boolean flag = false;

				int blastUpgradeCount = (int) ((modifiers.get(WizardryItems.blast_upgrade) - 1) / Constants.BLAST_RADIUS_INCREASE_PER_LEVEL + 0.5f);
				// Results in the following patterns:
				// 0 blast upgrades: single block
				// 1 blast upgrade: 3x3 without corners or edges
				// 2 blast upgrades: 3x3 with corners
				// 3 blast upgrades: 5x5 without corners or edges
				float radius = 0.5f + 0.73f * blastUpgradeCount;

				List<BlockPos> sphere = BlockUtils.getBlockSphere(pos, radius);

				for (BlockPos pos1 : sphere) {

					if (BlockUtils.isBlockUnbreakable(world, pos1))
						continue;

					IBlockState state1 = world.getBlockState(pos1);

					if (state1.getBlock().getHarvestLevel(state1) <= harvestLevel || harvestLevel >= 3) {

						if (caster instanceof EntityPlayerMP) { // Everything in here is server-side only so this is fine

							boolean silkTouch = state1.getBlock().canSilkHarvest(world, pos1, state1, (EntityPlayer) caster)
									&& ItemArtefact.isArtefactActive((EntityPlayer) caster, WizardryItems.charm_silk_touch);

							boolean hasFortune = ItemNewArtefact.isNewArtefactActive((EntityPlayer) caster, ASItems.head_fortune);

							int xp = BlockUtils.checkBlockBreakXP(caster, world, pos);

							if (xp < 0)
								continue; // Not allowed to break the block

							if (silkTouch) {
								flag = world.destroyBlock(pos1, false);
								if (flag) {
									ItemStack stack = getSilkTouchDrop(state1);
									if (stack != null)
										Block.spawnAsEntity(world, pos1, stack);
								}
							} else {
								////// Changes //////

								if (hasFortune) {

									// Fortune level is determined by potency, max III
									state1.getBlock().dropBlockAsItem(world, pos1, state1, Math.min(harvestLevel, 3));
									flag = world.destroyBlock(pos1, false); // no longer dropping the items here!
								} else {
									flag = world.destroyBlock(pos1, true);
								}

								if (flag)
									state1.getBlock().dropXpOnBlockBreak(world, pos1, xp);

								////// Changes //////
							}

						} else if (BlockUtils.canBreakBlock(caster, world, pos)) {
							// NPCs can dig the block under the target's feet
							flag = world.destroyBlock(pos1, true) || flag;
						}
					}
				}

				return flag;
			}
		} else {
			return true;
		}

		return false;
	}

	private static ItemStack getSilkTouchDrop(IBlockState state) {

		try {
			return (ItemStack) getSilkTouchDrop.invoke(state.getBlock(), state);
		}
		catch (IllegalAccessException | InvocationTargetException e) {
			Wizardry.logger.error("Error while reflectively retrieving silk touch drop", e);
		}

		return null;
	}
}
