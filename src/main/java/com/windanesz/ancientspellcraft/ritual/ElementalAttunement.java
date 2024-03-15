package com.windanesz.ancientspellcraft.ritual;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.spell.WarlockElementalSpellEffects;
import com.windanesz.ancientspellcraft.tileentity.TileRune;
import com.windanesz.ancientspellcraft.util.ASUtils;
import electroblob.wizardry.constants.Element;
import electroblob.wizardry.data.IStoredVariable;
import electroblob.wizardry.data.Persistence;
import electroblob.wizardry.data.WizardData;
import electroblob.wizardry.item.ItemCrystal;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.util.ParticleBuilder;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ElementalAttunement extends Ritual implements IRitualIngredient {

	public static final IStoredVariable<Integer> ELEMENTAL_ATTUNEMENT = IStoredVariable.StoredVariable.ofInt("ElementalAttunement", Persistence.ALWAYS);

	// throw in an astral diamond to make permanent?
	public ElementalAttunement() {
		super(AncientSpellcraft.MODID, "elemental_attunement", SpellActions.SUMMON, false);
		WizardData.registerStoredVariables(ELEMENTAL_ATTUNEMENT);
		}

	@Override
	public void initialEffect(World world, EntityPlayer caster, TileRune centerPiece) {
		ruinNonCenterPieceRunes(centerPiece, world);
		List<EntityItem> crystals = getActualIngredients(world, centerPiece, 1);
		if (!crystals.isEmpty() && crystals.get(0).getItem().getItem() instanceof ItemCrystal) {
			crystals.get(0).setPickupDelay(this.getMaxLifeTime() + 10);
		}

	}

	public static Optional<Element> getElement(EntityPlayer player) {
		WizardData data = WizardData.get(player);
		if (data.getVariable(ELEMENTAL_ATTUNEMENT) == null || data.getVariable(ELEMENTAL_ATTUNEMENT).intValue() == -1) {
			return Optional.empty();
		}
		return Optional.of(Element.values()[data.getVariable(ELEMENTAL_ATTUNEMENT).intValue()]);
	}

	@Override
	public void onRitualFinish(World world, EntityPlayer caster, TileRune centerPiece) {
		// attune
		WizardData data = WizardData.get(caster);
		List<EntityItem> crystals = getActualIngredients(world, centerPiece, 1);
		if (!crystals.isEmpty() && crystals.get(0).getItem().getItem() instanceof ItemCrystal) {
			Element element = Element.values()[crystals.get(0).getItem().getMetadata()];
			if (element == Element.MAGIC) {
				data.setVariable(ELEMENTAL_ATTUNEMENT, null);
			ASUtils.sendMessage(caster, "ritual.ancientspellcraft:elemental_attunement.not_attuned", false, element.getDisplayName());
			} else {
				data.setVariable(ELEMENTAL_ATTUNEMENT, element.ordinal());
				ASUtils.sendMessage(caster, "ritual.ancientspellcraft:elemental_attunement.attuned", false, element.getDisplayName());
			}
			crystals.get(0).getItem().shrink(1);
		}


		super.onRitualFinish(world, caster, centerPiece);
	}

	@Override
	public void effect(World world, EntityPlayer caster, TileRune centerPiece) {
		super.effect(world, caster, centerPiece);

		if (world.isRemote) {
			List<EntityItem> crystals = getActualIngredients(world, centerPiece, 1);
			if (!crystals.isEmpty() && crystals.get(0).getItem().getItem() instanceof ItemCrystal) {
				Element element = Element.values()[crystals.get(0).getItem().getMetadata()];
				Vec3d target = new Vec3d(centerPiece.getXCenter(), 256, centerPiece.getZCenter());
				int clr = WarlockElementalSpellEffects.PARTICLE_COLOURS.get(element)[0];
				if (world.getTotalWorldTime() % 7 == 0) {
					ParticleBuilder.create(ParticleBuilder.Type.BEAM).clr(clr)
							.pos(centerPiece.getXCenter(), centerPiece.getYCenter() - 0.5f, centerPiece.getZCenter()).scale(2f).time(80).target(target).spawn(world);
				}
				ParticleBuilder.create(ParticleBuilder.Type.BEAM).clr(clr)
						.pos(centerPiece.getXCenter(), centerPiece.getYCenter() - 0.5f, centerPiece.getZCenter()).scale(1f).time(70).target(target).spawn(world);

				ParticleBuilder.create(ParticleBuilder.Type.FLASH).face(EnumFacing.UP).clr(clr).pos(centerPiece.getXCenter(), centerPiece.getYCenter() - 0.3f,
						centerPiece.getZCenter()).time(70).scale(2f).spawn(world);
			}

		}
	}


	@Override
	public List<List<ItemStack>> getRequiredIngredients() {
		List<List<ItemStack>> ingredients = new ArrayList<>();
		List<ItemStack> crystals = new ArrayList<>();
		Arrays.stream(Element.values()).iterator().forEachRemaining(e -> crystals.add(new ItemStack(WizardryItems.magic_crystal,1, e.ordinal())));
		ingredients.add(crystals);
		return ingredients;
	}

}
