package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.block.BlockMagicMushroom;
import com.windanesz.ancientspellcraft.registry.ASItems;
import com.windanesz.ancientspellcraft.registry.ASSpells;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.BlockUtils;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class FairyRing extends Spell {

	public FairyRing() {
		super(AncientSpellcraft.MODID, "fairy_ring", SpellActions.SUMMON, false);
		addProperties(EFFECT_RADIUS, DURATION, DAMAGE);
	}

	@Override
	public boolean requiresPacket() { return false; }

	@Override
	public boolean canBeCastBy(EntityLiving npc, boolean override) { return true; }

	@Override
	public boolean canBeCastBy(TileEntityDispenser dispenser) { return true; }

	@Override
	public boolean cast(World world, EntityPlayer caster, EnumHand hand, int ticksInUse, SpellModifiers modifiers) {
		if (!summonMushroomRing(world, caster, caster.getPosition(), modifiers))
			return false;
		this.playSound(world, caster, ticksInUse, -1, modifiers);
		return true;
	}

	@Override
	public boolean cast(World world, EntityLiving caster, EnumHand hand, int ticksInUse, EntityLivingBase target, SpellModifiers modifiers) {
		if (!summonMushroomRing(world, caster, caster.getPosition(), modifiers))
			return false;
		this.playSound(world, caster, ticksInUse, -1, modifiers);
		return true;
	}

	@Override
	public boolean cast(World world, double x, double y, double z, EnumFacing direction, int ticksInUse, int duration, SpellModifiers modifiers) {
		if (!summonMushroomRing(world, null, new BlockPos(x, y, z).offset(direction), modifiers))
			return false;
		this.playSound(world, x, y, z, ticksInUse, duration, modifiers);
		return true;
	}

	public static boolean summonMushroomRing(World world, @Nullable EntityLivingBase caster, BlockPos origin, SpellModifiers modifiers) {

		if (!world.isRemote) {

			double radius = ASSpells.fairy_ring.getProperty(EFFECT_RADIUS).doubleValue() * modifiers.get(WizardryItems.blast_upgrade);

			List<BlockPos> ring = new ArrayList<>((int) (7 * radius)); // 7 is a bit more than 2 pi

			// This is from SummonThorns, Author: Electroblob
			for (int x = -(int) radius; x <= radius; x++) {

				for (int z = -(int) radius; z <= radius; z++) {

					double distance = MathHelper.sqrt(x * x + z * z);

					if (distance > radius || distance < radius - 1.5)
						continue;

					Integer y = BlockUtils.getNearestSurface(world, origin.add(x, 0, z), EnumFacing.UP, (int) radius, true, BlockUtils.SurfaceCriteria.BUILDABLE);
					if (y != null)
						ring.add(new BlockPos(origin.getX() + x, y, origin.getZ() + z));
				}
			}

			if (ring.isEmpty())
				return false;

			boolean singleType = false;
			BlockMagicMushroom mushroom = BlockMagicMushroom.getRandomMushroom(0.05f, 0.03f);

			// 5% to summon a single type as the ring
			if (world.rand.nextFloat() < 0.05) {
				singleType = true;
			}

			int lifetime = (int) ((ASSpells.fairy_ring.getProperty(DURATION).floatValue() * modifiers.get(WizardryItems.duration_upgrade)) * 5);
			for (BlockPos pos : ring) {

				if (!singleType) // get a new random mushroom
					mushroom = BlockMagicMushroom.getRandomMushroom(0.05f, 0.03f);

				BlockMagicMushroom.tryPlaceMushroom(world, pos, caster, mushroom, lifetime);
			}
		}

		return true;
	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == ASItems.ancient_spellcraft_spell_book || item == ASItems.ancient_spellcraft_scroll;
	}
}
