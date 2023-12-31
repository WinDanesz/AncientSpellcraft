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

public class RunesmithingImprove extends RunesmithingSpellBase implements IRunicHammerSpell, IClassSpell {

	public RunesmithingImprove(String name, EnumAction action, boolean isContinuous) {
		super(name, action, isContinuous);
	}

	@Override
	public boolean doAnvilEffect(World world, BlockPos pos, EnumFacing side, Vec3d hit,
			@Nullable EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {

		int[] colours = BlockReceptacle.PARTICLE_COLOURS.get(Element.MAGIC);

		if (world.isRemote) {
			for (int i = 0; i < (ticksInUse * 0.1 * 2); i++) {
				ParticleBuilder.create(ParticleBuilder.Type.DUST).pos(pos.getX() + world.rand.nextFloat(), pos.getY() + 1, pos.getZ() + world.rand.nextFloat())
						.vel(0, 0.05 + (world.rand.nextFloat() * 0.1), 0).clr(colours[1]).fade(colours[2]).time(40).shaded(false).spawn(world);
			}
		}

		ItemStack stack = BlockArcaneAnvil.getItemOnLeftSlot(world, pos);

		if (stack.getItem() instanceof ItemTool) {

			TileEntity tile = world.getTileEntity(pos);

			if (tile instanceof TileArcaneAnvil) {
				ItemStack copy = stack.copy();

				boolean flag = false;
				if (stack.getItemDamage() != 0) {
					float percentToRestore = stack.getMaxDamage() * 0.05f;
					stack.setItemDamage((int) (stack.getItemDamage() - percentToRestore));
					flag = true;
				}

				int level = 2;
				Enchantment enchantment = Enchantments.EFFICIENCY;
				if (EnchantmentHelper.getEnchantmentLevel(enchantment, copy) < level) {
					if (EnchantmentHelper.getEnchantments(copy).containsKey(enchantment)) {
						Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(copy);
						enchantments.remove(enchantment);
						EnchantmentHelper.setEnchantments(enchantments, copy);
					}
					copy.addEnchantment(enchantment, 2);
					((TileArcaneAnvil) tile).setInventorySlotContents(TileSageLectern.BOOK_SLOT, copy);
					flag = true;
				}

				return flag;
			}
		}
		return false;
	}
}
