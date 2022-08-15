package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.block.BlockSageLectern;
import com.windanesz.ancientspellcraft.item.ItemEmptyMysticSpellBook;
import com.windanesz.ancientspellcraft.registry.ASItems;
import com.windanesz.ancientspellcraft.registry.ASSpells;
import com.windanesz.ancientspellcraft.tileentity.TileSageLectern;
import com.windanesz.ancientspellcraft.util.ASUtils;
import electroblob.wizardry.data.IStoredVariable;
import electroblob.wizardry.data.Persistence;
import electroblob.wizardry.data.WizardData;
import electroblob.wizardry.item.ItemWizardArmour;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class PerfectTheory extends SpellLecternInteract implements IClassSpell {

	private static final IStoredVariable<NBTTagCompound> PERFECT_THEORY = IStoredVariable.StoredVariable.ofNBT("perfectTheoryData", Persistence.ALWAYS).setSynced();

	public PerfectTheory() {
		super("perfect_theory", SpellActions.SUMMON, true);
		WizardData.registerStoredVariables(PERFECT_THEORY);
	}

	private static NBTTagCompound getData(EntityPlayer player) {
		WizardData data = WizardData.get(player);
		if (data != null) {
			NBTTagCompound nbt = data.getVariable(PERFECT_THEORY);
			if (nbt != null) {
				return nbt;
			}
		}
		return new NBTTagCompound();
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

				ItemStack stack = BlockSageLectern.getItemOnLectern(world, pos);

				if (stack.getItem() instanceof ItemEmptyMysticSpellBook) {
					WizardData data = WizardData.get(player);

					int theoryPoints = Experiment.getTheoryPoints(player);

					if (theoryPoints < 1) {
						ASUtils.sendMessage(player, "generic.ancientspellcraft:spell_lectern_interact.no_theory_points", true);
						return false;
					}

					if (!world.isRemote) {
						Spell spell = ASSpells.perfect_theory_spell;
						TileEntity tile = world.getTileEntity(pos);
						if (tile instanceof TileSageLectern) {
							ItemStack book = new ItemStack(ASItems.mystic_spell_book, 1, spell.metadata());
							NBTTagCompound theory = Experiment.getLastExperiment(player);
							book.setTagCompound(theory);
							((TileSageLectern) tile).setInventorySlotContents(TileSageLectern.BOOK_SLOT, book);
						}
						Experiment.consumeTheoryPoint(player, 1);
						return true;
					}
					return false;
				} else {
					// the lectern doesn't have a valid book
					ASUtils.sendMessage(player,"spell." + this.getUnlocalisedName() + ".no_valid_book", true);
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
	public ItemWizardArmour.ArmourClass getArmourClass() { return ItemWizardArmour.ArmourClass.SAGE; }

	@Override
	public boolean applicableForItem(Item item) {
		return item == ASItems.mystic_spell_book;
	}
}
