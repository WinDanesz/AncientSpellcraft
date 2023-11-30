package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.registry.ASItems;
import electroblob.wizardry.constants.Constants;
import electroblob.wizardry.item.ItemWizardArmour;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.registry.WizardryBlocks;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.tileentity.TileEntityTimer;
import electroblob.wizardry.util.BlockUtils;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.ParticleBuilder.Type;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;
import java.util.stream.Collectors;

public class SpectralFloor extends Spell implements IClassSpell {

	private static final String BLOCK_LIFETIME = "block_lifetime";

	public SpectralFloor() {
		super(AncientSpellcraft.MODID, "spectral_floor", SpellActions.POINT, false);
		addProperties(BLOCK_LIFETIME, EFFECT_RADIUS);
	}

	@Override
	public boolean cast(World world, EntityPlayer caster, EnumHand hand, int ticksInUse, SpellModifiers modifiers) {
		BlockPos pos = caster.getPosition();
		pos = pos.offset(EnumFacing.DOWN);
		if (caster.motionY < 0) {
			pos = pos.offset(EnumFacing.DOWN);
		}
		if (world.isRemote) {
			ParticleBuilder.create(Type.FLASH).pos(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5).scale(3).clr(0.75f, 1, 0.85f).spawn(world);
		} else {

			//center piece
			if(BlockUtils.canBlockBeReplaced(world, pos)){

				if(!world.isRemote){

					world.setBlockState(pos, WizardryBlocks.spectral_block.getDefaultState());

					if(world.getTileEntity(pos) instanceof TileEntityTimer){
						((TileEntityTimer)world.getTileEntity(pos)).setLifetime((int)(getProperty(BLOCK_LIFETIME).floatValue()
								* modifiers.get(WizardryItems.duration_upgrade)));
					}
				}

			}

			BlockPos finalPos = pos;
			int blastUpgradeCount = (int) ((modifiers.get(WizardryItems.blast_upgrade) - 1) / Constants.BLAST_RADIUS_INCREASE_PER_LEVEL + 0.5f);
			List<BlockPos> list = BlockUtils.getBlockSphere(pos, getProperty(EFFECT_RADIUS).intValue() -1 + blastUpgradeCount).stream().filter(world::isAirBlock).filter(i -> i.getY() == finalPos.getY()).collect(Collectors.toList());

			for (int i = 0; i < list.size(); i++) {
				BlockPos currPos = list.get(i);
				if(BlockUtils.canBlockBeReplaced(world, currPos)){

					if(!world.isRemote){

						world.setBlockState(currPos, WizardryBlocks.spectral_block.getDefaultState());

						if(world.getTileEntity(currPos) instanceof TileEntityTimer){
							((TileEntityTimer)world.getTileEntity(currPos)).setLifetime((int)(getProperty(BLOCK_LIFETIME).floatValue()
									* modifiers.get(WizardryItems.duration_upgrade)));
						}
					}
				}
			}
		}
		return true;
	}

	@Override
	public ItemWizardArmour.ArmourClass getArmourClass() { return ItemWizardArmour.ArmourClass.SAGE; }

	@Override
	public boolean applicableForItem(Item item) {
		return item == ASItems.mystic_spell_book || item == WizardryItems.scroll;
	}

}
