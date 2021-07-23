package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.block.BlockQuickSand;
import com.windanesz.ancientspellcraft.block.ITemporaryBlock;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftBlocks;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftItems;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.registry.WizardryBlocks;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.spell.SpellRay;
import electroblob.wizardry.tileentity.TileEntityTimer;
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
import scala.actors.threadpool.Arrays;

import java.util.ArrayList;
import java.util.List;

public class SummonQuicksand extends SpellRay {

	private static final String BLOCK_LIFETIME = "block_lifetime";

	public SummonQuicksand() {
		super(AncientSpellcraft.MODID, "summon_quicksand", SpellActions.POINT, false);
		this.ignoreLivingEntities(true);
		addProperties(BLOCK_LIFETIME);
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
			ITemporaryBlock.placeTemporaryBlock(caster, world, AncientSpellcraftBlocks.QUICKSAND, pos, 600);

			BlockPos origPos = pos;
			// random bonus
			for (int i = 0; i < 4; i++) {
				int length = EnumFacing.HORIZONTALS.length;

				//noinspection unchecked
				List<EnumFacing> horizontals = new ArrayList<EnumFacing>(Arrays.asList(EnumFacing.HORIZONTALS));
				EnumFacing offset = horizontals.get(world.rand.nextInt(length));
				pos = pos.offset(offset, 1);

				ITemporaryBlock.placeTemporaryBlock(caster, world, AncientSpellcraftBlocks.QUICKSAND, pos, 600);
				pos = origPos;
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
