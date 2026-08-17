package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.entity.construct.EntityMysticSigil;
import com.windanesz.ancientspellcraft.registry.ASItems;
import electroblob.wizardry.item.ItemWizardArmour;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.spell.SpellConstructRanged;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.RayTracer;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MysticSigil extends SpellConstructRanged<EntityMysticSigil> implements IClassSpell {

	public MysticSigil() {
		super(AncientSpellcraft.MODID, "mystic_sigil", EntityMysticSigil::new, true);
		this.soundValues(1, 1.1f, 0.1f);
		this.floor(true);
		this.overlap(false);
	}

	@Override
	public boolean cast(World world, EntityPlayer caster, EnumHand hand, int ticksInUse, SpellModifiers modifiers) {
		double range = getProperty(RANGE).doubleValue() * modifiers.get(WizardryItems.range_upgrade);
		RayTraceResult rayTrace = RayTracer.standardBlockRayTrace(world, caster, range, hitLiquids, ignoreUncollidables, false);
		if (rayTrace != null && rayTrace.typeOfHit == RayTraceResult.Type.BLOCK && (rayTrace.sideHit == EnumFacing.UP || !requiresFloor)) {
			if (!world.isRemote) {
				double x = rayTrace.hitVec.x;
				double y = rayTrace.hitVec.y;
				double z = rayTrace.hitVec.z;

				if (!EntityUtils.getEntitiesWithinRadius(1, x, y, z, world, EntityMysticSigil.class).isEmpty()) {
					EntityUtils.getEntitiesWithinRadius(1, x, y, z, world, EntityMysticSigil.class).get(0).setDead();
					return true;
				}

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
	public ItemWizardArmour.ArmourClass getArmourClass() {
		return ItemWizardArmour.ArmourClass.SAGE;
	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == ASItems.mystic_spell_book || item == ASItems.ancient_spellcraft_scroll;
	}

	@SideOnly(Side.CLIENT)
	public String getDisplayNameWithFormatting() {
		return TextFormatting.GOLD + net.minecraft.client.resources.I18n.format(getTranslationKey());
	}
}
