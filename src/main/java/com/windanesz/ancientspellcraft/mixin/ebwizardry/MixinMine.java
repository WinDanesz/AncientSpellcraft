package com.windanesz.ancientspellcraft.mixin.ebwizardry;

import com.windanesz.ancientspellcraft.Settings;
import com.windanesz.ancientspellcraft.registry.ASItems;
import electroblob.wizardry.constants.Constants;
import electroblob.wizardry.item.ISpellCastingItem;
import electroblob.wizardry.item.ItemArtefact;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.spell.Mine;
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
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

/**
 * Adds Fortune artefact ({@link ASItems#head_fortune}) compatibility to the Mine spell. Previously this lived in a
 * whole separate {@code MineAS extends Mine} spell class that got re-registered under the same registry name as the
 * base spell - which required manually patching up Wizardry's internal networkID bookkeeping to avoid corrupting
 * spell property/casting sync for every other spell (see git history if curious). Mixing directly into the one real
 * {@link Mine} instance instead avoids all of that: there's only ever one spell object, so there's nothing to
 * desync in the first place.
 */
@Mixin(Mine.class)
public abstract class MixinMine {

	@Shadow(remap = false)
	private static ItemStack getSilkTouchDrop(IBlockState state) {
		throw new UnsupportedOperationException();
	}

	/**
	 * @author WinDanesz
	 * @reason Add Fortune artefact support when breaking blocks without silk touch.
	 */
	@Overwrite(remap = false)
	protected boolean onBlockHit(World world, BlockPos pos, EnumFacing side, Vec3d hit, EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers){

		// Needs to be outside because it gets run on the client-side
		if(caster instanceof EntityPlayer){
			if(caster.getHeldItemMainhand().getItem() instanceof ISpellCastingItem){
				caster.swingArm(EnumHand.MAIN_HAND);
			}else if(caster.getHeldItemOffhand().getItem() instanceof ISpellCastingItem){
				caster.swingArm(EnumHand.OFF_HAND);
			}
		}

		if(!world.isRemote){

			if(BlockUtils.isBlockUnbreakable(world, pos)) return false;
			// Reworked to respect the rules, but since we might break multiple blocks this is left as an optimisation
			if(!EntityUtils.canDamageBlocks(caster, world)) return false;

			IBlockState state = world.getBlockState(pos);
			// The maximum harvest level as determined by the potency multiplier. The + 0.5f is so that
			// weird float processing doesn't incorrectly round it down.
			int harvestLevel = (int)((modifiers.get(SpellModifiers.POTENCY) - 1) / Constants.POTENCY_INCREASE_PER_TIER + 0.5f);

			if(harvestLevel > 0) harvestLevel--; // Shifts them all down one since normally novice wands give some potency

			// The >= 3 is to allow master earth wands to break anything.
			if(state.getBlock().getHarvestLevel(state) <= harvestLevel || harvestLevel >= 3){

				boolean flag = false;

				int blastUpgradeCount = (int)((modifiers.get(WizardryItems.blast_upgrade) - 1) / Constants.BLAST_RADIUS_INCREASE_PER_LEVEL + 0.5f);
				// Results in the following patterns:
				// 0 blast upgrades: single block
				// 1 blast upgrade: 3x3 without corners or edges
				// 2 blast upgrades: 3x3 with corners
				// 3 blast upgrades: 5x5 without corners or edges
				float radius = 0.5f + 0.73f * blastUpgradeCount;

				List<BlockPos> sphere = BlockUtils.getBlockSphere(pos, radius);

				for(BlockPos pos1 : sphere){

					if(BlockUtils.isBlockUnbreakable(world, pos1)) continue;

					IBlockState state1 = world.getBlockState(pos1);

					if(state1.getBlock().getHarvestLevel(state1) <= harvestLevel || harvestLevel >= 3){

						if(caster instanceof EntityPlayerMP){ // Everything in here is server-side only so this is fine

							boolean silkTouch = state1.getBlock().canSilkHarvest(world, pos1, state1, (EntityPlayer)caster)
									&& ItemArtefact.isArtefactActive((EntityPlayer)caster, WizardryItems.charm_silk_touch);

							boolean hasFortune = Settings.spellCompatSettings.mineSpellOverride
									&& ItemArtefact.isArtefactActive((EntityPlayer)caster, ASItems.head_fortune);

							int xp = BlockUtils.checkBlockBreakXP(caster, world, pos);

							if(xp < 0) continue; // Not allowed to break the block

							if(silkTouch){
								flag = world.destroyBlock(pos1, false);
								if(flag){
									ItemStack stack = getSilkTouchDrop(state1);
									if(stack != null) Block.spawnAsEntity(world, pos1, stack);
								}
							}else if(hasFortune){
								// Fortune level is determined by potency, max III
								state1.getBlock().dropBlockAsItem(world, pos1, state1, Math.min(harvestLevel, 3));
								flag = world.destroyBlock(pos1, false); // no longer dropping the items here!
								if(flag) state1.getBlock().dropXpOnBlockBreak(world, pos1, xp);
							}else{
								flag = world.destroyBlock(pos1, true);
								if(flag) state1.getBlock().dropXpOnBlockBreak(world, pos1, xp);
							}

						}else if(BlockUtils.canBreakBlock(caster, world, pos)){
							// NPCs can dig the block under the target's feet
							flag = world.destroyBlock(pos1, true) || flag;
						}
					}
				}

				return flag;
			}
		}else{
			return true;
		}

		return false;
	}
}
