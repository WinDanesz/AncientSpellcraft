package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.block.BlockMagicMushroom;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftItems;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.spell.SpellRay;
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

public class SporelingsAid extends SpellRay {

	private static final String BLOCK_LIFETIME = "block_lifetime";

	public SporelingsAid() {
		super(AncientSpellcraft.MODID, "sporelings_aid", SpellActions.POINT, false);
		this.ignoreLivingEntities(true);
		addProperties(BLOCK_LIFETIME);
	}

	@Override
	protected boolean onEntityHit(World world, Entity target, Vec3d hit, EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {
		return false;
	}

	@Override
	protected boolean onBlockHit(World world, BlockPos pos, EnumFacing side, Vec3d hit, EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {

		pos = pos.offset(side);

		if (world.isRemote) {
			ParticleBuilder.create(Type.FLASH).pos(pos.getX() + 0.5, pos.getY() + 0.1, pos.getZ() + 0.5).scale(2).time(20).face(EnumFacing.UP).clr(78, 168, 50).spawn(world);
		} else {
			if (caster.isSneaking()) {
				BlockMagicMushroom mushroom = BlockMagicMushroom.getRandomMushroom(0.05f, 0.03f, BlockMagicMushroom.MushroomType.BUFF);
				return BlockMagicMushroom.tryPlaceMushroom(world, pos, caster, mushroom, getProperty(BLOCK_LIFETIME).intValue());
			} else {
				BlockMagicMushroom mushroom = BlockMagicMushroom.getRandomMushroom(0.05f, 0.03f, BlockMagicMushroom.MushroomType.COMBAT);
				return BlockMagicMushroom.tryPlaceMushroom(world, pos, caster, mushroom, getProperty(BLOCK_LIFETIME).intValue());
			}
		}

		return false;
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
