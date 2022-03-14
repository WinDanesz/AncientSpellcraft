package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.registry.ASItems;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.spell.SpellRay;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.PotionTypes;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemGlassBottle;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Random;

import static electroblob.wizardry.util.EntityUtils.canDamageBlocks;

public class ConjureWater extends SpellRay {
	private Random rand = new Random();

	public ConjureWater(String modID, String name, EnumAction action, boolean isContinuous) {
		super(modID, name, SpellActions.POINT, false);
		this.soundValues(0.5f, 1.1f, 0.2f);
		addProperties(BLAST_RADIUS);
	}

	@Override
	protected boolean onEntityHit(World world, Entity target, Vec3d hit, EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {
		if (target instanceof EntityLivingBase) {
			EntityLivingBase entityLivingTarget = (EntityLivingBase) target;

			if (!entityLivingTarget.canBreatheUnderwater() && !entityLivingTarget.isPotionActive(MobEffects.WATER_BREATHING)) {
				// deal drowning damage, double for water sensitive entities
				entityLivingTarget.attackEntityFrom(DamageSource.DROWN, isWaterSensitiveEntity(entityLivingTarget) ? 2.0F : 1.0F);
				return true;
			}
		}

		return false;
	}

	@Override
	protected boolean onBlockHit(World world, BlockPos pos, EnumFacing side, Vec3d hit, EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {
		if (!canDamageBlocks(caster, world))
			return false;

		Item offhandItemItem = caster.getHeldItemOffhand().getItem();
		if (offhandItemItem instanceof ItemBucket || offhandItemItem instanceof ItemGlassBottle) {
			return onMiss(world, caster, origin, hit, ticksInUse, modifiers);
		}

		// check if caster is in the Nether
		if (caster.world.provider.getDimension() == -1)
			return false;

		int waterRadius = (int) (getProperty(BLAST_RADIUS).intValue() * modifiers.get(WizardryItems.blast_upgrade));

		boolean createdWater = false;
		for (BlockPos currPos : BlockPos.getAllInBox(pos.add(-waterRadius, -waterRadius, -waterRadius), pos.add(waterRadius, waterRadius, waterRadius))) {
			if (currPos.getY() > pos.getY() || world.getBlockState(currPos).getBlock() == Blocks.WATER) {
				continue;
			}
			currPos = currPos.offset(side);
			IBlockState iblockstate = world.getBlockState(currPos);
			Material material = iblockstate.getMaterial();
			boolean flag = !material.isSolid();
			boolean flag1 = iblockstate.getBlock().isReplaceable(world, currPos);

			if (!world.isAirBlock(currPos) && !flag && !flag1) {
				continue;
			}
			if (!world.isRemote && (flag || flag1) && (!material.isLiquid() || world.getBlockState(currPos).getBlock() == Blocks.WATER)) {
				world.destroyBlock(currPos, true);
				world.setBlockState(currPos, Blocks.WATER.getDefaultState());
				world.getBlockState(currPos).getBlock().neighborChanged(world.getBlockState(currPos), world, currPos, Blocks.WATER, currPos);
				createdWater = true;
			}
			world.playSound((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D, SoundEvents.BLOCK_WATER_AMBIENT, SoundCategory.BLOCKS, 0.5F, rand.nextFloat() * 0.4F + 0.8F, false);
		}
		return createdWater;
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

				if (stack.isEmpty()) {
					playerIn.setHeldItem(EnumHand.OFF_HAND, potionStack);
				} else {
					if (!playerIn.inventory.addItemStackToInventory(potionStack)) {
						playerIn.dropItem(potionStack, false);
					}
				}
				return true;
			}
			if (stack.getItem() instanceof ItemBucket) {
				stack.shrink(1);
				ItemStack waterBucket = new ItemStack(Items.WATER_BUCKET);
				world.playSound(playerIn, playerIn.posX, playerIn.posY, playerIn.posZ, SoundEvents.ITEM_BUCKET_FILL, SoundCategory.NEUTRAL, 1.0F, 1.0F);

				if (stack.isEmpty()) {
					playerIn.setHeldItem(EnumHand.OFF_HAND, waterBucket);
				} else {
					if (!playerIn.inventory.addItemStackToInventory(waterBucket)) {
						playerIn.dropItem(waterBucket, false);
					}
				}
				return true;
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
		return item == ASItems.ancient_spellcraft_spell_book || item == ASItems.ancient_spellcraft_scroll;
	}

	private static boolean isWaterSensitiveEntity(EntityLivingBase entity) {
		return entity instanceof EntityEnderman || entity instanceof EntityBlaze;
	}
}
