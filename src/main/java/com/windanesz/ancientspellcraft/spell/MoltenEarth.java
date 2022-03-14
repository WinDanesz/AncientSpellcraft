package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.block.ITemporaryBlock;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftBlocks;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftItems;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.spell.SpellRay;
import electroblob.wizardry.util.BlockUtils;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.ParticleBuilder.Type;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;
import java.util.stream.Collectors;

public class MoltenEarth extends SpellRay {

	private static final String BLOCK_LIFETIME = "block_lifetime";

	public MoltenEarth() {
		super(AncientSpellcraft.MODID, "molten_earth", SpellActions.POINT, false);
		this.ignoreLivingEntities(true);
		addProperties(BLOCK_LIFETIME, EFFECT_RADIUS);
	}

	@Override
	protected boolean onEntityHit(World world, Entity target, Vec3d hit, EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {
		return false;
	}

	@Override
	protected boolean onBlockHit(World world, BlockPos pos, EnumFacing side, Vec3d hit, EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {

		pos = pos.offset(EnumFacing.DOWN);
		pos = pos.offset(side);

		if (world.isRemote) {
			ParticleBuilder.create(Type.FLASH).pos(pos.getX() + 0.5, pos.getY() + 1.1, pos.getZ() + 0.5).scale(6).time(20).face(EnumFacing.UP).clr(0.55f, 0.29f, 0.04f).spawn(world);
		} else {

			//center piece
			ITemporaryBlock.placeTemporaryBlock(caster, world, AncientSpellcraftBlocks.CONJURED_MAGMA, pos, 600);

			BlockPos finalPos = pos;
			List<BlockPos> list = BlockUtils.getBlockSphere(pos, getProperty(EFFECT_RADIUS).intValue()).stream().filter(i -> !world.isAirBlock(i)).filter(i -> i.getY() == finalPos.getY()).collect(Collectors.toList());
			int blockLifetime = getProperty(BLOCK_LIFETIME).intValue();

			for (int i = 0; i < list.size(); i++) {
				BlockPos currPos = list.get(i);
				ITemporaryBlock.placeTemporaryBlock(caster, world, AncientSpellcraftBlocks.CONJURED_MAGMA, currPos, 600);

			}
		}
		return true;
	}

	@Override
	protected boolean onMiss(World world, EntityLivingBase caster, Vec3d origin, Vec3d direction, int ticksInUse, SpellModifiers modifiers) {
		return false;
	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == AncientSpellcraftItems.ancient_spellcraft_spell_book || item == AncientSpellcraftItems.ancient_spellcraft_scroll;
	}
}
