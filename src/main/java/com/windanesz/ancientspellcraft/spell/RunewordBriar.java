package com.windanesz.ancientspellcraft.spell;

import electroblob.wizardry.block.BlockThorns;
import electroblob.wizardry.item.IManaStoringItem;
import electroblob.wizardry.registry.WizardryBlocks;
import electroblob.wizardry.tileentity.TileEntityThorns;
import electroblob.wizardry.util.BlockUtils;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class RunewordBriar extends Runeword {

	public RunewordBriar() {
		super("runeword_briar", EnumAction.NONE, false);
		addProperties(EFFECT_TRIGGER_CHANCE, EFFECT_DURATION);
		setPassive();
	}

	@Override
	public boolean onAboutToHitEntity(World world, EntityLivingBase caster, EntityLivingBase target, EnumHand hand, ItemStack sword, SpellModifiers modifiers, boolean charged) {

		// chance to grow a thorns
		if (!caster.world.isRemote && world.rand.nextDouble() <= getProperty(EFFECT_TRIGGER_CHANCE).floatValue()) {

			BlockPos pos = BlockUtils.findNearbyFloorSpace(target, 3, 3);

			if (pos != null) {

				//if (castSuccessful(this, world, caster, hand, sword, modifiers)) {

					if (BlockUtils.canBlockBeReplaced(world, pos) && BlockUtils.canBlockBeReplaced(world, pos.up())) {

						((BlockThorns) WizardryBlocks.thorns).placeAt(world, pos, 3);

						TileEntity tileentity = world.getTileEntity(pos);

						if (tileentity instanceof TileEntityThorns) {

							((TileEntityThorns) tileentity).setLifetime((int) (60));

							if (caster != null) { ((TileEntityThorns) tileentity).setCaster(caster); }
							((TileEntityThorns) tileentity).damageMultiplier = 1f;

							((TileEntityThorns) tileentity).sync();
							((IManaStoringItem) sword.getItem()).consumeMana(sword, this.getCost(), caster);
						}
					}
				//}
			}
		}
		return false;
	}

}
