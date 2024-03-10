package com.windanesz.ancientspellcraft.ritual;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.tileentity.TileRune;
import electroblob.wizardry.data.IStoredVariable;
import electroblob.wizardry.data.Persistence;
import electroblob.wizardry.data.WizardData;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.util.ParticleBuilder;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

public class WarlockAttunement extends Ritual {

	public static final IStoredVariable<Boolean> WARLOCK_ATTUNED = IStoredVariable.StoredVariable.ofBoolean("WarlockAttuned", Persistence.ALWAYS);

	// throw in an astral diamond to make permanent?
	public WarlockAttunement() {
		super(AncientSpellcraft.MODID, "warlock_attunement", SpellActions.SUMMON, false);
		WizardData.registerStoredVariables(WARLOCK_ATTUNED);
	}

	@Override
	public void initialEffect(World world, EntityPlayer caster, TileRune centerPiece) {
		ruinNonCenterPieceRunes(centerPiece, world);

	}

	public static boolean isWarlockAttuned(EntityPlayer player) {
		WizardData data = WizardData.get(player);
		return data.getVariable(WARLOCK_ATTUNED) != null && data.getVariable(WARLOCK_ATTUNED).booleanValue();
	}

	@Override
	public void onRitualFinish(World world, EntityPlayer caster, TileRune centerPiece) {

		// attune
		WizardData data = WizardData.get(caster);
		if (data.getVariable(WARLOCK_ATTUNED) == null) {
			data.setVariable(WARLOCK_ATTUNED, true);
		} else {
			data.setVariable(WARLOCK_ATTUNED, !data.getVariable(WARLOCK_ATTUNED).booleanValue());
		}
		data.sync();

		if (caster != null && !world.isRemote) {
			caster.sendMessage(new TextComponentTranslation("ritual.ancientspellcraft:warlock_attunement.attuned", centerPiece.getX(), centerPiece.getY(), centerPiece.getZ()));
		}

		super.onRitualFinish(world, caster, centerPiece);
	}

	@Override
	public void effect(World world, EntityPlayer caster, TileRune centerPiece) {
		super.effect(world, caster, centerPiece);

		if (world.isRemote) {
			Vec3d target = new Vec3d(centerPiece.getXCenter(), 256, centerPiece.getZCenter());
			if (world.getTotalWorldTime() % 7 == 0) {
				ParticleBuilder.create(ParticleBuilder.Type.BEAM).clr(252, 252, 159)
						.pos(centerPiece.getXCenter(), centerPiece.getYCenter() - 0.5f, centerPiece.getZCenter()).scale(2f).time(10).target(target).spawn(world);
			}
			ParticleBuilder.create(ParticleBuilder.Type.BEAM).clr(252, 252, 159)
					.pos(centerPiece.getXCenter(), centerPiece.getYCenter() - 0.5f, centerPiece.getZCenter()).scale(1f).time(5).target(target).spawn(world);

			ParticleBuilder.create(ParticleBuilder.Type.FLASH).clr(252, 252, 159).pos(centerPiece.getXCenter(), centerPiece.getYCenter(), centerPiece.getZCenter()).scale(2f).spawn(world);
			ParticleBuilder.create(ParticleBuilder.Type.FLASH).clr(255, 255, 247).pos(centerPiece.getXCenter(), centerPiece.getYCenter() + 0.5f, centerPiece.getZCenter()).scale(0.9f).spawn(world);
		}
	}
}
