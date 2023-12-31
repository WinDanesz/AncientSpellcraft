package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.block.BlockArcaneAnvil;
import com.windanesz.ancientspellcraft.registry.ASItems;
import com.windanesz.ancientspellcraft.tileentity.TileArcaneAnvil;
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
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Map;

public abstract class RunesmithingSpellBase extends SpellRay implements IRunicHammerSpell, IClassSpell {

	public RunesmithingSpellBase(String name, EnumAction action, boolean isContinuous) {
		super(AncientSpellcraft.MODID, name, action, isContinuous);
	}

	@Override
	protected boolean onEntityHit(World world, Entity target, Vec3d hit,
			@Nullable EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {
		ASUtils.sendMessage(caster, "generic.ancientspellcraft:runic_hammer_spell.no_anvil", true);
		return false;
	}

	@Override
	protected boolean onBlockHit(World world, BlockPos pos, EnumFacing side, Vec3d hit,
			@Nullable EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {

		if (caster instanceof EntityPlayer) {

			EntityPlayer player = (EntityPlayer) caster;

			if (isAnvilBlock(world, pos)) {

				return doAnvilEffect(world, pos, side, hit, caster,  origin, ticksInUse, modifiers);

				} else {
					// the lectern doesn't have a valid book
					ASUtils.sendMessage(player, "generic.ancientspellcraft:runic_hammer_spell.no_anvil", true);
					return false;
				}
			}
		return true;
	}

	public abstract boolean doAnvilEffect(World world, BlockPos pos, EnumFacing side, Vec3d hit,
			@Nullable EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers);

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
			if (isAnvilBlock(world, rayTrace.getBlockPos())) {
				return false;
			}
		}
		if (caster instanceof EntityPlayer && !world.isRemote) {
			((EntityPlayer) caster).sendStatusMessage(new TextComponentTranslation("generic.ancientspellcraft:runic_hammer_spell.no_anvil"), true);
		}
		return true;
	}



	@Override
	public ItemWizardArmour.ArmourClass getArmourClass() { return ItemWizardArmour.ArmourClass.BATTLEMAGE; }

	@Override
	public boolean applicableForItem(Item item) {
		return item == ASItems.runic_plate;
	}

	public static boolean isAnvilBlock(World world, BlockPos pos) {
		return world.getBlockState(pos).getBlock() instanceof BlockArcaneAnvil;
	}



}
