package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.block.BlockSageLectern;
import com.windanesz.ancientspellcraft.item.ItemEmptyMysticSpellBook;
import com.windanesz.ancientspellcraft.registry.ASItems;
import com.windanesz.ancientspellcraft.registry.ASSpells;
import com.windanesz.ancientspellcraft.tileentity.TileSageLectern;
import com.windanesz.ancientspellcraft.util.ASUtils;
import electroblob.wizardry.Wizardry;
import electroblob.wizardry.block.BlockReceptacle;
import electroblob.wizardry.constants.Element;
import electroblob.wizardry.data.WizardData;
import electroblob.wizardry.item.ItemArtefact;
import electroblob.wizardry.item.ItemWizardArmour;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.SpellModifiers;
import electroblob.wizardry.util.SpellProperties;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class Scribe extends SpellLecternInteract {

	public Scribe() {
		super("scribe", SpellActions.POINT, true);
	}

	public static List<Spell> getAllSageSpells() {
		return Spell.getSpells(s -> s instanceof IClassSpell && ((IClassSpell) s).getArmourClass() == ItemWizardArmour.ArmourClass.SAGE);
	}

	@Override
	protected boolean onBlockHit(World world, BlockPos pos, EnumFacing side, Vec3d hit,
			@Nullable EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {

		if (caster instanceof EntityPlayer) {

			if (!isLecternBlock(world, pos)) { return false; }
			int minTicks = 100;
			spawnLecternParticles(world, ticksInUse, pos, minTicks);
			if (ticksInUse < minTicks) { return true; }

			EntityPlayer player = (EntityPlayer) caster;

			if (isLecternBlock(world, pos)) {
				int[] colours = BlockReceptacle.PARTICLE_COLOURS.get(Element.MAGIC);

				if (world.isRemote) {
					for (int i = 0; i < (ticksInUse * 0.1 * 2); i++) {
						ParticleBuilder.create(ParticleBuilder.Type.DUST).pos(pos.getX() + world.rand.nextFloat(), pos.getY() + 1, pos.getZ() + world.rand.nextFloat())
								.vel(0, 0.05 + (world.rand.nextFloat() * 0.1), 0).clr(colours[1]).fade(colours[2]).time(40).shaded(false).spawn(world);
					}
				}

				ItemStack stack = BlockSageLectern.getItemOnLectern(world, pos);

				if (stack.getItem() instanceof ItemEmptyMysticSpellBook) {
					WizardData data = WizardData.get(player);

					int theoryPoints = Experiment.getTheoryPoints(player);

					if (theoryPoints < 1) {
						ASUtils.sendMessage(player, "generic.ancientspellcraft:spell_lectern_interact.no_theory_points", true);
						return false;
					}

					List<Spell> spells;
					if (ItemArtefact.isArtefactActive(player, ASItems.charm_elemental_alkahest) && world.rand.nextFloat() < 0.2f) {
						spells = Spell.getSpells(s -> !(s instanceof IClassSpell) && s.isEnabled(SpellProperties.Context.BOOK) && s.isEnabled(SpellProperties.Context.LOOTING));
						spells.removeIf(data::hasSpellBeenDiscovered);
					} else {
						spells = getAllSageSpells();
						spells.removeIf(data::hasSpellBeenDiscovered);
						spells.removeIf(s -> s == ASSpells.perfect_theory_spell);
					}

					if (!world.isRemote && !spells.isEmpty()) {
						Spell randomSpell = spells.get(world.rand.nextInt(spells.size()));
						TileEntity tile = world.getTileEntity(pos);
						if (tile instanceof TileSageLectern) {
							((TileSageLectern) tile).setInventorySlotContents(TileSageLectern.BOOK_SLOT, ASUtils.getSpellBookForSpell(randomSpell));
						}
						Experiment.consumeTheoryPoint(player, 1);
						if (player.isHandActive()) {
							player.getCooldownTracker().setCooldown(player.getHeldItem(player.getActiveHand()).getItem(), 10);
							player.stopActiveHand();
						}
						return true;
					}
					return false;
				} else {
					// the lectern doesn't have a valid book
					ASUtils.sendMessage(player,"generic.ancientspellcraft:spell_lectern_interact.needs_empty_book", true);
					return false;
				}
			} else {
				// wasn't cast on a lectern block
				ASUtils.sendMessage(player, "generic.ancientspellcraft:spell_lectern_interact.no_lectern", true);
			}
		}
		return true;
	}


	@Override
	@SideOnly(Side.CLIENT)
	public String getDescription() {
		if (Minecraft.getMinecraft().player != null) {
			EntityPlayer player = Minecraft.getMinecraft().player;
			return Wizardry.proxy.translate(getDescriptionTranslationKey(), Experiment.getTheoryPoints(player));
		}
		return super.getDescription();
	}
}
