package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.block.BlockSageLectern;
import com.windanesz.ancientspellcraft.tileentity.TileSageLectern;
import com.windanesz.ancientspellcraft.util.ASUtils;
import electroblob.wizardry.block.BlockReceptacle;
import electroblob.wizardry.data.WizardData;
import electroblob.wizardry.event.DiscoverSpellEvent;
import electroblob.wizardry.item.ItemSpellBook;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.registry.WizardrySounds;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

import javax.annotation.Nullable;

public class EnchantLectern extends SpellLecternInteract {

	public EnchantLectern() {
		super("enchant_lectern", SpellActions.POINT, true);
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
				if (stack.getItem() instanceof ItemSpellBook && !(stack.getItem() instanceof IClassSpell)) {
					Spell spell = Spell.byMetadata(stack.getMetadata());
					WizardData data = WizardData.get(player);

					int[] colours = BlockReceptacle.PARTICLE_COLOURS.get(spell.getElement());

					if (world.isRemote) {
						for (int i = 0; i < 2; i++) {
							ParticleBuilder.create(ParticleBuilder.Type.DUST).pos(pos.getX() + world.rand.nextFloat(), pos.getY() + 1, pos.getZ() + world.rand.nextFloat())
									.vel(0, 0.05 + (world.rand.nextFloat() * 0.1), 0).clr(colours[1]).fade(colours[2]).time(40).shaded(false).spawn(world);
						}
					}

					if (!data.hasSpellBeenDiscovered(spell)) {
						// unknown spell
						if (!world.isRemote) {
							player.sendStatusMessage(new TextComponentTranslation("spell." + this.getUnlocalisedName() + ".unknown_spell"), true);
						}
						return true;
					} else {
						// known spell
						if (!world.isRemote && !(spell instanceof IClassSpell)) {
							TileEntity lectern = world.getTileEntity(pos);
							if (lectern instanceof TileSageLectern) {
								((TileSageLectern) lectern).addSpellEffect(this);
							}

							player.sendStatusMessage(new TextComponentTranslation(
									"spell." + this.getUnlocalisedName() + ".blessing_done", spell.getNameForTranslationFormatted()), false);
							return false;
						}
					}
					return false;
				} else {
					// the lectern doesn't have a valid book
					if (!world.isRemote) {
						player.sendStatusMessage(new TextComponentTranslation("spell." + this.getUnlocalisedName() + ".no_valid_book"), true);
					}
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
	public void persistentEffectOnLecternClick(TileSageLectern lectern, EntityPlayer player) {
		if (lectern.getBookSlotItem().getItem() instanceof ItemSpellBook) {
			Spell spell = (Spell.byMetadata(lectern.getBookSlotItem().getMetadata()));
			if (!(spell instanceof IClassSpell) && !WizardData.get(player).hasSpellBeenDiscovered(spell)) {
				//if (!player.world.isRemote) {
				if (!MinecraftForge.EVENT_BUS.post(new DiscoverSpellEvent(player, spell,
						DiscoverSpellEvent.Source.IDENTIFICATION_SCROLL))) {
					// nothing happens!
					WizardData.get(player).discoverSpell(spell);
					player.playSound(WizardrySounds.MISC_DISCOVER_SPELL, 1.25f, 1);
					if (!player.world.isRemote) {
						player.sendMessage(new TextComponentTranslation("spell.discover",
								spell.getNameForTranslationFormatted()));
					}
				}
				//}
			}
		}
	}
}
