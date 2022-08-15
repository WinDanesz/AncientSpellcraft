package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.block.BlockSageLectern;
import com.windanesz.ancientspellcraft.util.ASUtils;
import electroblob.wizardry.block.BlockReceptacle;
import electroblob.wizardry.constants.Element;
import electroblob.wizardry.data.WizardData;
import electroblob.wizardry.item.ItemSpellBook;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.RayTracer;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class Identify extends SpellLecternInteract {

	public static final String SUCCESS_CHANCE = "success_chance";

	public Identify() {
		super("identify", SpellActions.POINT, true);
		addProperties(SUCCESS_CHANCE);
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

				ItemStack stack = BlockSageLectern.getItemOnLectern(world, pos);
				if (stack.getItem() instanceof ItemSpellBook && !(stack.getItem() instanceof IClassSpell)) {
					Spell spell = Spell.byMetadata(stack.getMetadata());
					WizardData data = WizardData.get(player);

					colours = BlockReceptacle.PARTICLE_COLOURS.get(spell.getElement());

					if (world.isRemote) {
						for (int i = 0; i < 2; i++) {
							ParticleBuilder.create(ParticleBuilder.Type.DUST).pos(pos.getX() + world.rand.nextFloat(), pos.getY() + 1, pos.getZ() + world.rand.nextFloat())
									.vel(0, 0.05 + (world.rand.nextFloat() * 0.1), 0).clr(colours[1]).fade(colours[2]).time(40).shaded(false).spawn(world);
						}
					}

					if (!data.hasSpellBeenDiscovered(spell)) {
						// unknown spell
						if (world.rand.nextFloat() <= getProperty(SUCCESS_CHANCE).floatValue() && data.discoverSpell(spell)) {
							data.sync();
							if (!world.isRemote) {
								player.sendMessage(new TextComponentTranslation("spell.discover",
										spell.getNameForTranslationFormatted()));
							}
							return true;
						}
						//else if (!world.isRemote) {
//							// failed attempt to identify spell
//							player.sendStatusMessage(new TextComponentTranslation("spell." + this.getUnlocalisedName() + ".attempt_failed"), true);
//							WizardData.get(player).stopCastingContinuousSpell();
//							return false;
						//}
					} else {
						// known spell
						if (!world.isRemote) {
							player.sendStatusMessage(new TextComponentTranslation("spell." + this.getUnlocalisedName() + ".known_spell"), true);
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
	protected boolean onMiss(World world, @Nullable EntityLivingBase caster, Vec3d origin, Vec3d direction, int ticksInUse, SpellModifiers modifiers) {
		// Had to add this as a failed onBlockHit/onEntityHit cascades to onMiss

		double range = getRange(world, origin, direction, caster, ticksInUse, modifiers);
		Vec3d endpoint = origin.add(direction.scale(range));

		// Change the filter depending on whether living entities are ignored or not
		RayTraceResult rayTrace = RayTracer.rayTrace(world, origin, endpoint, aimAssist, hitLiquids,
				ignoreUncollidables, false, Entity.class, ignoreLivingEntities ? EntityUtils::isLiving
						: RayTracer.ignoreEntityFilter(caster));

		if (rayTrace != null && rayTrace.typeOfHit == RayTraceResult.Type.BLOCK) {
			if (isLecternBlock(world, rayTrace.getBlockPos())) {
				return false;
			}
		}
		if (caster instanceof EntityPlayer && !world.isRemote) {
			((EntityPlayer) caster).sendStatusMessage(new TextComponentTranslation("spell." + this.getUnlocalisedName() + ".no_lectern"), true);
		}
		return true;
	}

	@Override
	protected void spawnParticle(World world, double x, double y, double z, double vx, double vy, double vz){
//		if(world.rand.nextInt(5) == 0) ParticleBuilder.create(ParticleBuilder.Type.DARK_MAGIC).pos(x, y, z).clr(0.1f, 0, 0).spawn(world);
//		// This used to multiply the velocity by the distance from the caster
//		ParticleBuilder.create(ParticleBuilder.Type.SPARKLE).pos(x, y, z).vel(vx, vy, vz).time(8 + world.rand.nextInt(6))
//				.clr(0.5f, 0, 0).spawn(world);
	}
}
