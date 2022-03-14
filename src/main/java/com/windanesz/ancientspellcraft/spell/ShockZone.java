package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.entity.construct.EntityBuilder;
import com.windanesz.ancientspellcraft.registry.ASBlocks;
import com.windanesz.ancientspellcraft.registry.ASItems;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.spell.SpellRay;
import electroblob.wizardry.util.BlockUtils;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ShockZone extends SpellRay {

	public ShockZone() {
		super(AncientSpellcraft.MODID, "shock_zone", SpellActions.POINT_DOWN, false);
		this.ignoreLivingEntities(true);
		addProperties(EFFECT_DURATION, EFFECT_RADIUS, DAMAGE);
	}

	@Override
	protected boolean onEntityHit(World world, Entity target, Vec3d hit, EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {
		return false;
	}

	@Override
	protected boolean onBlockHit(World world, BlockPos pos, EnumFacing side, Vec3d hit, EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {

		//pos = pos.offset(EnumFacing.DOWN);
		pos = pos.up();

		if (!world.isRemote) {

			//center piece
			//ITemporaryBlock.placeTemporaryBlock(caster, world, AncientSpellcraftBlocks.CONJURED_MAGMA, pos, 600);

			BlockPos finalPos = pos;
			List<BlockPos> listb = BlockUtils.getBlockSphere(pos, getProperty(EFFECT_RADIUS).intValue() * modifiers.get(WizardryItems.blast_upgrade)).stream().filter(world::isAirBlock).filter(i -> i.getY() == finalPos.getY()).collect(Collectors.toList());

			EntityBuilder builder = new EntityBuilder(world);
			builder.setPosition(caster.getPosition().getX(), caster.getPosition().getY(), caster.getPosition().getZ());
			builder.setCaster(caster);
			builder.blockLifetime = (int) ((getProperty(EFFECT_DURATION).floatValue() * modifiers.get(WizardryItems.duration_upgrade)));
			builder.buildTickRate = 1;
			builder.batchSize = (int) (2 * (modifiers.get(SpellModifiers.POTENCY))) + (int) (3 * modifiers.get(WizardryItems.blast_upgrade));
			listb.sort(Comparator.comparingInt(Vec3i::getY));
			builder.setBuildList(listb);
			builder.setBlockToBuild(ASBlocks.lightning_block.getDefaultState());
			builder.damageMultiplier = getProperty(DAMAGE).floatValue() * modifiers.get(SpellModifiers.POTENCY);
			world.spawnEntity(builder);
		}

		return true;
	}

	@Override
	protected boolean onMiss(World world, EntityLivingBase caster, Vec3d origin, Vec3d direction, int ticksInUse, SpellModifiers modifiers) {
		return false;
	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == ASItems.ancient_spellcraft_spell_book || item == ASItems.ancient_spellcraft_scroll;
	}
}
