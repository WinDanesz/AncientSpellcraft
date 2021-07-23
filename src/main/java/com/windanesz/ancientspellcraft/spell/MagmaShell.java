package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.entity.construct.EntityBuilder;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftBlocks;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftItems;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.registry.WizardryPotions;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.spell.SpellBuff;
import electroblob.wizardry.util.AllyDesignationSystem;
import electroblob.wizardry.util.BlockUtils;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.MagicDamage;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static electroblob.wizardry.util.BlockUtils.canBlockBeReplaced;

@Mod.EventBusSubscriber
public class MagmaShell extends Spell {

	public MagmaShell() {
		super(AncientSpellcraft.MODID, "magma_shell", SpellActions.SUMMON, false);
		soundValues(1.0f, 1.2f, 0.2f);
		addProperties(BLAST_RADIUS, EFFECT_DURATION);
	}

	@Override
	public boolean canBeCastBy(EntityLiving npc, boolean override) {
		return true;
	}

	@Override
	public boolean canBeCastBy(TileEntityDispenser dispenser) {
		return false;
	}

	@Override
	public boolean cast(World world, double x, double y, double z, EnumFacing direction, int ticksInUse, int duration, SpellModifiers modifiers) {
		return false;
	}

	@Override
	public boolean cast(World world, EntityLiving caster, EnumHand hand, int ticksInUse, EntityLivingBase target, SpellModifiers modifiers) {
		return createShell(world, caster, hand, ticksInUse, modifiers);
	}

	@Override
	public boolean cast(World world, EntityPlayer caster, EnumHand hand, int ticksInUse, SpellModifiers modifiers) {
		return createShell(world, caster, hand, ticksInUse, modifiers);
	}

	private boolean createShell(World world, EntityLivingBase caster, EnumHand hand, int ticksInUse, SpellModifiers modifiers) {
		if (caster.onGround) {
			if (!world.isRemote) {

				// knock away nearby hostiles
				for (EntityLivingBase currentTarget : EntityUtils.getEntitiesWithinRadius(4, caster.posX, caster.posY, caster.posZ, caster.world, EntityLivingBase.class)) {
					if (AllyDesignationSystem.isValidTarget(caster, currentTarget) && !MagicDamage.isEntityImmune(MagicDamage.DamageType.FROST, currentTarget)) {
						EntityUtils.applyStandardKnockback(caster, currentTarget);
					}
				}

				EntityBuilder builder = new EntityBuilder(world);
				builder.setPosition(caster.getPosition().getX(), caster.getPosition().getY(), caster.getPosition().getZ());
				builder.setCaster(caster);
				builder.lifetime = 600;
				builder.blockLifetime = (int) ((getProperty(EFFECT_DURATION).floatValue() * modifiers.get(WizardryItems.duration_upgrade)));
				builder.buildTickRate = 1;
				builder.batchSize = (int)  (2  * (modifiers.get(SpellModifiers.POTENCY))) + (int) (3 * modifiers.get(WizardryItems.blast_upgrade));
				List<BlockPos> list = getShellBlocks(caster, modifiers);
				list.sort(Comparator.comparingInt(Vec3i::getY));
				builder.setBuildList(list);
				builder.setBlockToBuild(AncientSpellcraftBlocks.CONJURED_MAGMA.getDefaultState());
				world.spawnEntity(builder);
			}

			return true;
		}
		return false;
	}

	private List<BlockPos> getShellBlocks(EntityLivingBase caster, SpellModifiers modifiers) {
		List<BlockPos> largeFilledSphere = BlockUtils.getBlockSphere(caster.getPosition().up(), getProperty(BLAST_RADIUS).intValue() * modifiers.get(WizardryItems.blast_upgrade));
		List<BlockPos> smallFilledSphere = BlockUtils.getBlockSphere(caster.getPosition().up(), (getProperty(BLAST_RADIUS).intValue() - 1) * modifiers.get(WizardryItems.blast_upgrade));

		List<BlockPos> hollowSphere = new ArrayList<>();
		for (BlockPos pos : largeFilledSphere) {
			if (!smallFilledSphere.contains(pos)) {
				hollowSphere.add(pos);
			}
		}
		return hollowSphere;
	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == AncientSpellcraftItems.ancient_spellcraft_spell_book || item == AncientSpellcraftItems.ancient_spellcraft_scroll;
	}

}
