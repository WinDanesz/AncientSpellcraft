package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.block.BlockSageLectern;
import com.windanesz.ancientspellcraft.registry.ASItems;
import com.windanesz.ancientspellcraft.tileentity.TileSageLectern;
import com.windanesz.ancientspellcraft.util.ASUtils;
import electroblob.wizardry.block.BlockReceptacle;
import electroblob.wizardry.constants.Element;
import electroblob.wizardry.item.ItemWizardArmour;
import electroblob.wizardry.spell.SpellRay;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.RayTracer;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public abstract class SpellLecternInteract extends SpellRay implements IClassSpell {

	public static final String MAX_DISTANCE_FROM_LECTERN = "max_distance_from_lectern";

	public SpellLecternInteract(String name, EnumAction action, boolean isContinuous) {
		super(AncientSpellcraft.MODID, name, action, isContinuous);
		this.soundValues(1, 1.1f, 0.2f);
	}

	protected void spawnLecternParticles(World world, int ticksInUse, BlockPos pos, int minTicks) {
		int[] colours = BlockReceptacle.PARTICLE_COLOURS.get(Element.MAGIC);
		if (world.isRemote && ticksInUse < minTicks) {
			for (int i = 0; i < (ticksInUse * 0.1 * 2); i++) {
				ParticleBuilder.create(ParticleBuilder.Type.DUST).pos(pos.getX() + world.rand.nextFloat(), pos.getY() + 1, pos.getZ() + world.rand.nextFloat())
						.vel(0, 0.05 + (world.rand.nextFloat() * 0.1), 0).clr(colours[1]).fade(colours[2]).time(40).shaded(false).spawn(world);
			}
		}
	}

	@Override
	protected void spawnParticle(World world, double x, double y, double z, double vx, double vy, double vz) {
		//ParticleBuilder.create(ParticleBuilder.Type.DUST).pos(x, y, z).clr(0x571e65).spawn(world);
		//ParticleBuilder.create(ParticleBuilder.Type.DUST).pos(x, y, z).clr(0x251609).spawn(world);
		//ParticleBuilder.create(ParticleBuilder.Type.DUST).pos(x, y, z).time(12 + world.rand.nextInt(8)).clr(0x0b4b40).spawn(world);
	}

	@Override
	protected boolean onEntityHit(World world, Entity target, Vec3d hit,
			@Nullable EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {
		ASUtils.sendMessage(caster, "generic.ancientspellcraft:spell_lectern_interact.no_lectern", true);
		return false;
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
			((EntityPlayer) caster).sendStatusMessage(new TextComponentTranslation("generic.ancientspellcraft:spell_lectern_interact.no_lectern"), true);
		}
		return true;
	}

	@Override
	public ItemWizardArmour.ArmourClass getArmourClass() { return ItemWizardArmour.ArmourClass.SAGE; }

	@Override
	public boolean applicableForItem(Item item) {
		return item == ASItems.mystic_spell_book;
	}

	public static boolean isLecternBlock(World world, BlockPos pos) {
		return world.getBlockState(pos).getBlock() instanceof BlockSageLectern;
	}

	public boolean persistsOnBookRemoval() { return false; }

	public void persistentEffectOnLecternClick(TileSageLectern lectern, EntityPlayer player) {}
}
