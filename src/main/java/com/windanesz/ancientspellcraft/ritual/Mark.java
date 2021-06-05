package com.windanesz.ancientspellcraft.ritual;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.tileentity.TileRune;
import electroblob.wizardry.data.IStoredVariable;
import electroblob.wizardry.data.Persistence;
import electroblob.wizardry.data.WizardData;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.util.ParticleBuilder;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class Mark extends Ritual {

	public static final IStoredVariable<BlockPos> RITUAL_MARK_LOCATION = IStoredVariable.StoredVariable.ofBlockPos("ritual_mark_location", Persistence.ALWAYS).setSynced();

	public Mark() {
		super(AncientSpellcraft.MODID, "mark", SpellActions.SUMMON, false);
		WizardData.registerStoredVariables(RITUAL_MARK_LOCATION);
	}

	@Override
	public void initialEffect(World world, EntityPlayer caster, TileRune centerPiece) {
		ruinNonCenterPieceRunes(centerPiece, world);

		if (!world.isRemote) {
			if (caster != null) {
				WizardData data = WizardData.get(caster);
				if (data != null) {
					data.setVariable(RITUAL_MARK_LOCATION, centerPiece.getPos());
					data.sync();
				}
			}
		}
	}

	@Override
	public void effect(World world, EntityPlayer caster, TileRune centerPiece) {
		super.effect(world, caster, centerPiece);
		if (world.isRemote && world.rand.nextInt(5) == 0) {
			float f = Math.max(world.rand.nextInt(3), 1) * world.rand.nextFloat();
			ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(centerPiece.getPos().getX(), centerPiece.getPos().offset(EnumFacing.UP).getY(), centerPiece.getPos().getZ()).scale(f).spawn(world);
		}

	}
}
