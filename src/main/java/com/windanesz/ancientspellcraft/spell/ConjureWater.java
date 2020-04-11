package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.registry.AncientSpellcraftItems;
import electroblob.wizardry.spell.SpellRay;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.SpellModifiers;
import electroblob.wizardry.util.WizardryUtilities;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGlassBottle;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionUtils;
import net.minecraft.stats.StatList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Random;

public class ConjureWater extends SpellRay {
	private Random rand = new Random();

	public ConjureWater(String modID, String name, EnumAction action, boolean isContinuous) {
		super(modID, name, isContinuous, action);
		this.soundValues(0.5f, 1.1f, 0.2f);
	}

	@Override
	protected boolean onEntityHit(World world, Entity entity, Vec3d vec3d,
			@Nullable EntityLivingBase entityLivingBase, Vec3d vec3d1, int i, SpellModifiers spellModifiers) {
		return false;
	}

	@Override
	protected boolean onBlockHit(World world, BlockPos pos, EnumFacing side, Vec3d hit, EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {
		if (!WizardryUtilities.canDamageBlocks(caster, world))
			return false;

		pos = pos.offset(side);
		IBlockState iblockstate = world.getBlockState(pos);
		Material material = iblockstate.getMaterial();
		boolean flag = !material.isSolid();
		boolean flag1 = iblockstate.getBlock().isReplaceable(world, pos);

		if (!world.isAirBlock(pos) && !flag && !flag1) {
			return false;
		} else {
			if (!world.isRemote && (flag || flag1) && !material.isLiquid()) {
				world.destroyBlock(pos, true);
//			}
//			if (!world.isRemote) {
//				world.destroyBlock(pos, true);
				world.setBlockState(pos, Blocks.WATER.getDefaultState());
				world.getBlockState(pos).getBlock().neighborChanged(world.getBlockState(pos), world, pos, Blocks.WATER, pos);
			}
			world.playSound((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D, SoundEvents.BLOCK_WATER_AMBIENT, SoundCategory.BLOCKS, 0.5F, rand.nextFloat() * 0.4F + 0.8F, false);

			return true;
		}
	}

	@Override
	protected boolean onMiss(World world, @Nullable EntityLivingBase caster, Vec3d origin, Vec3d direction, int ticksInUse, SpellModifiers modifiers) {
		if (caster instanceof EntityPlayer) {
			EntityPlayer playerIn = (EntityPlayer) caster;
			ItemStack stack = caster.getHeldItemOffhand();
			if (stack.getItem() instanceof ItemGlassBottle) {
				stack.shrink(1);
				ItemStack potionStack = PotionUtils.addPotionToItemStack(new ItemStack(Items.POTIONITEM), PotionTypes.WATER);
				world.playSound(playerIn, playerIn.posX, playerIn.posY, playerIn.posZ, SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.NEUTRAL, 1.0F, 1.0F);

				playerIn.addStat(StatList.getObjectUseStats(Items.GLASS_BOTTLE));
				if (stack.isEmpty()) {
					playerIn.setHeldItem(EnumHand.OFF_HAND, potionStack);
				} else {
					if (!playerIn.inventory.addItemStackToInventory(potionStack)) {
						playerIn.dropItem(potionStack, false);
					}
				}
				return true;
			} else {
				//	if (stack.getItem() instanceof ItemBucket) {
				//	ItemBucket bucket = (ItemBucket) stack.getItem();
				// TODO: fill water buckets in hand
				//				}
			}
		}
		return false;
	}

	@Override
	protected void spawnParticle(World world, double x, double y, double z, double vx, double vy, double vz) {
		float brightness = world.rand.nextFloat();
		ParticleBuilder.create(ParticleBuilder.Type.MAGIC_BUBBLE).pos(x, y, z).vel(vx, vy, vz).time(8 + world.rand.nextInt(12))
				.clr(0.4f + 0.6f * brightness, 0.6f + 0.4f * brightness, 1).collide(true).spawn(world);
		ParticleBuilder.create(ParticleBuilder.Type.MAGIC_BUBBLE).pos(x, y, z).vel(vx, vy, vz).time(8 + world.rand.nextInt(12)).collide(true).spawn(world);
	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == AncientSpellcraftItems.ancient_spellcraft_spell_book || item == AncientSpellcraftItems.ancient_spellcraft_scroll;
	}
}
