package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.entity.construct.EntitySilencingSigil;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftItems;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.spell.SpellConstructRanged;
import electroblob.wizardry.util.RayTracer;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class SilencingSigil extends SpellConstructRanged<EntitySilencingSigil> {

	public SilencingSigil() {
		super(AncientSpellcraft.MODID, "silencing_sigil", EntitySilencingSigil::new, true);
		this.soundValues(1, 1.1f, 0.1f);
		this.floor(true);
		this.overlap(false);
	}

	@Override
	public boolean cast(World world, EntityPlayer caster, EnumHand hand, int ticksInUse, SpellModifiers modifiers) {

		double range = getProperty(RANGE).doubleValue() * modifiers.get(WizardryItems.range_upgrade);
		RayTraceResult rayTrace = RayTracer.standardBlockRayTrace(world, caster, range, hitLiquids, ignoreUncollidables, false);
		if (rayTrace != null && rayTrace.typeOfHit == RayTraceResult.Type.ENTITY) {
			if (rayTrace.entityHit instanceof EntitySilencingSigil) {
				rayTrace.entityHit.setDead();
				return true;
			}
		}
		if (rayTrace != null && rayTrace.typeOfHit == RayTraceResult.Type.BLOCK && (rayTrace.sideHit == EnumFacing.UP || !requiresFloor)) {

			if (!world.isRemote) {

				double x = rayTrace.hitVec.x;
				double y = rayTrace.hitVec.y;
				double z = rayTrace.hitVec.z;

				if (!spawnConstruct(world, x, y, z, rayTrace.sideHit, caster, modifiers))
					return false;
			}

		} else if (!requiresFloor) {

			if (!world.isRemote) {

				Vec3d look = caster.getLookVec();

				double x = caster.posX + look.x * range;
				double y = caster.getEntityBoundingBox().minY + caster.getEyeHeight() + look.y * range;
				double z = caster.posZ + look.z * range;

				if (!spawnConstruct(world, x, y, z, null, caster, modifiers))
					return false;
			}

		} else {
			return false;
		}

		caster.swingArm(hand);
		this.playSound(world, caster, ticksInUse, -1, modifiers);
		return true;
	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == AncientSpellcraftItems.ancient_spell_book || item == AncientSpellcraftItems.ancient_spellcraft_scroll;
	}
}
