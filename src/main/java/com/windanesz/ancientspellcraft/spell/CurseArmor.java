package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.registry.ASItems;
import com.windanesz.ancientspellcraft.registry.ASPotions;
import electroblob.wizardry.item.ItemArtefact;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.spell.SpellRay;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.windanesz.ancientspellcraft.util.ASUtils.pickRandomStackFromItemStackList;

public class CurseArmor extends SpellRay {

	public CurseArmor(String modID, String name, EnumAction action, boolean isContinuous) {
		super(modID, name, SpellActions.THRUST, false);
		this.soundValues(1, 1.1f, 0.2f);
	}

	public static boolean curseRandomArmourPiece(EntityLivingBase target, World world) {
		if (EntityUtils.isLiving(target)) {
			if (!world.isRemote) {
				if (target instanceof EntityLivingBase) {

					if (target.isPotionActive(ASPotions.curse_ward) ||
							(target instanceof EntityPlayer && ItemArtefact.isArtefactActive((EntityPlayer) target, ASItems.amulet_curse_ward))) {
						return false;
					}

					List<ItemStack> itemStackList = new ArrayList<>();
					for (ItemStack stack : target.getArmorInventoryList()) {
						if (stack.getItem() instanceof ItemArmor) {
							itemStackList.add(stack);
						}
					}
					if (!itemStackList.isEmpty()) {
						ItemStack itemToCurse = pickRandomStackFromItemStackList(itemStackList);
						return attemptCurseItemStack(itemToCurse);
					}
					return false;
					// TODO: add play sound
				}
			}
		}
		return false;
	}

	public static boolean attemptCurseItemStack(ItemStack stack) {
		List<Enchantment> validCurseList = new ArrayList<>();
		if (!EnchantmentHelper.hasVanishingCurse(stack)) { validCurseList.add(Enchantments.VANISHING_CURSE); }
		if (!EnchantmentHelper.hasBindingCurse(stack)) { validCurseList.add(Enchantments.BINDING_CURSE); }
		if (!validCurseList.isEmpty()) {
			Random rand = new Random();
			try {
				stack.addEnchantment(validCurseList.get(rand.nextInt(validCurseList.size())), 1);
				return true;
			}
			catch (Exception e) {
				return false;
			}
		}
		return false;
	}

	@Override
	protected boolean onEntityHit(World world, Entity target, Vec3d hit, EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {
		if (EntityUtils.isLiving(target)) {
			return curseRandomArmourPiece((EntityLivingBase) target, world);
		}
		return false;
	}

	@Override
	protected boolean onBlockHit(World world, BlockPos pos, EnumFacing side, Vec3d hit, EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {
		return false;
	}

	@Override
	protected boolean onMiss(World world, EntityLivingBase caster, Vec3d origin, Vec3d direction, int ticksInUse, SpellModifiers modifiers) {
		return true;
	}

	@Override
	protected void spawnParticle(World world, double x, double y, double z, double vx, double vy, double vz) {
		ParticleBuilder.create(ParticleBuilder.Type.DARK_MAGIC).pos(x, y, z).clr(0x571e65).spawn(world);
		ParticleBuilder.create(ParticleBuilder.Type.SCORCH).pos(x, y, z).clr(0x251609).spawn(world);
		ParticleBuilder.create(ParticleBuilder.Type.SPARKLE).pos(x, y, z).time(12 + world.rand.nextInt(8)).clr(0x0b4b40).spawn(world);
	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == ASItems.ancient_spellcraft_spell_book || item == ASItems.ancient_spellcraft_scroll;
	}
}



