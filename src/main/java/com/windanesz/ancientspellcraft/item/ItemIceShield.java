package com.windanesz.ancientspellcraft.item;

import com.windanesz.ancientspellcraft.entity.projectile.EntitySafeIceShard;
import electroblob.wizardry.item.IConjuredItem;
import electroblob.wizardry.registry.Spells;
import electroblob.wizardry.registry.WizardryPotions;
import electroblob.wizardry.registry.WizardrySounds;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.BlockUtils;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.MagicDamage;
import electroblob.wizardry.util.ParticleBuilder;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

import static electroblob.wizardry.util.BlockUtils.getNearestSurface;

@Mod.EventBusSubscriber
public class ItemIceShield extends Item implements IConjuredItem {

	private EnumRarity rarity = EnumRarity.COMMON;

	public ItemIceShield() {
		setFull3D();
		this.maxStackSize = 1;
		setMaxDamage(180);
		setNoRepair();
		this.addPropertyOverride(new ResourceLocation("blocking"), new IItemPropertyGetter() {
			@SideOnly(Side.CLIENT)
			public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
				return entityIn != null && entityIn.isHandActive() && entityIn.getActiveItemStack() == stack ? 1.0F : 0.0F;
			}
		});
		addAnimationPropertyOverrides();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		tooltip.add(I18n.format("item." + this.getRegistryName() + ".desc1"));
		tooltip.add(I18n.format("item." + this.getRegistryName() + ".desc2"));
	}

	// no animations interruptions
	@Override
	public boolean canContinueUsing(ItemStack oldStack, ItemStack newStack) {
		// Ignore durability changes
		if (ItemStack.areItemsEqualIgnoreDurability(oldStack, newStack)) { return true; }
		return super.canContinueUsing(oldStack, newStack);
	}

	public Item setRarity(EnumRarity rarity) {
		this.rarity = rarity;
		return this;
	}

	@Override
	public EnumRarity getRarity(ItemStack stack) {
		return rarity;
	}

	@Override
	public int getMaxDamage(ItemStack stack) {
		return this.getMaxDamageFromNBT(stack, Spells.conjure_sword);
	}
	//
	//	@Override
	//	// This method allows the code for the item's timer to be greatly simplified by damaging it directly from
	//	// onUpdate() and removing the workaround that involved WizardData and all sorts of crazy stuff.
	//	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
	//
	//		if (!oldStack.isEmpty() || !newStack.isEmpty()) {
	//			// We only care about the situation where we specifically want the animation NOT to play.
	//			if (oldStack.getItem() == newStack.getItem() && !slotChanged)
	//				return false;
	//		}
	//
	//		return super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged);
	//	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		oldStack = oldStack.copy();
		oldStack.setTagCompound(null);
		newStack = newStack.copy();
		newStack.setTagCompound(null);
		return !ItemStack.areItemStacksEqual(oldStack, newStack);
	}

	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean selected) {

		int damage = stack.getItemDamage();

		if (damage > stack.getMaxDamage() || damage < 0) {
			// breaks

			if (entity instanceof EntityPlayer && ((EntityPlayer) entity).inventory.getStackInSlot(slot).getItem() instanceof ItemIceShield) {
				((EntityPlayer) entity).inventory.decrStackSize(slot, 1);
			}

			if (entity instanceof EntityLivingBase) {
				explodeShield(entity.world, (EntityLivingBase) entity, stack);
			}

		} else {
			// decrease shield lifetime
			stack.setItemDamage(damage + 1);
		}
	}

	public static void explodeShield(World world, EntityLivingBase entity, ItemStack shieldStack) {
		if (!world.isRemote) {

			world.playSound(null, entity.getPosition(), WizardrySounds.ENTITY_ICE_CHARGE_SMASH, SoundCategory.PLAYERS, 1.5f, world.rand.nextFloat() * 0.4f + 0.6f);
			world.playSound(null, entity.getPosition(), WizardrySounds.ENTITY_ICE_CHARGE_ICE, SoundCategory.PLAYERS, 1.2f, world.rand.nextFloat() * 0.4f + 1.2f);

			double radius = Spells.ice_charge.getProperty(Spell.EFFECT_RADIUS).floatValue() * 1.5;

			List<EntityLivingBase> targets = EntityUtils.getLivingWithinRadius(radius, entity.posX, entity.posY,
					entity.posZ, entity.world);

			// Slows targets
			for (EntityLivingBase target : targets) {
				if (target != entity) {
					if (!MagicDamage.isEntityImmune(MagicDamage.DamageType.FROST, target)) {
						target.addPotionEffect(new PotionEffect(WizardryPotions.frost,
								Spells.ice_charge.getProperty(Spell.SPLASH_EFFECT_DURATION).intValue(),
								Spells.ice_charge.getProperty(Spell.SPLASH_EFFECT_STRENGTH).intValue()));
					}
				}
			}

			// Places snow and ice on ground.
			for (int i = -1; i < 2; i++) {
				for (int j = -1; j < 2; j++) {

					BlockPos pos = new BlockPos(entity.posX + i, entity.posY, entity.posZ + j);

					Integer y = getNearestSurface(world, pos, EnumFacing.UP, 7, true,
							BlockUtils.SurfaceCriteria.SOLID_LIQUID_TO_AIR);

					if (y != null) {

						pos = new BlockPos(pos.getX(), y, pos.getZ());

						double dist = entity.getDistance(pos.getX(), pos.getY(), pos.getZ());

						// Randomised with weighting so that the nearer the block the more likely it is to be snowed.
						if (world.rand.nextInt((int) dist * 2 + 1) < 1 && dist < 2) {
							if (world.getBlockState(pos.down()).getBlock() == Blocks.WATER) {
								world.setBlockState(pos.down(), Blocks.ICE.getDefaultState());
							} else {
								// Don't need to check whether the block at pos can be replaced since getNearestFloorLevelB
								// only ever returns floors with air above them.
								world.setBlockState(pos, Blocks.SNOW_LAYER.getDefaultState());
							}
						}
					}
				}
			}

			// Releases shards
			for (int i = 0; i < 20; i++) {
				double dx = world.rand.nextDouble() - 0.5;
				double dy = world.rand.nextDouble() - 0.5;
				double dz = world.rand.nextDouble() - 0.5;
				EntitySafeIceShard iceshard = new EntitySafeIceShard(world);
				iceshard.setPosition(entity.posX + dx, entity.posY + dy + 1.5, entity.posZ + dz);
				iceshard.motionX = dx * 1.5;
				iceshard.motionY = dy * 1.5;
				iceshard.motionZ = dz * 1.5;
				iceshard.setCaster((EntityLivingBase) entity);
				iceshard.damageMultiplier = 1.3F;
				world.spawnEntity(iceshard);
				shieldStack.shrink(1);
			}
		} else {
			// adapted from electroblob.wizardry.entity.projectile.EntityIceCharge (author: Electroblob)
			// Particle effect
			world.spawnParticle(EnumParticleTypes.EXPLOSION_LARGE, entity.posX, entity.posY + 0.5, entity.posZ, 0, 0, 0);
			for (int i = 0; i < 30 * 2; i++) {

				ParticleBuilder.create(ParticleBuilder.Type.ICE, world.rand, entity.posX, entity.posY + 0.5, entity.posZ, 2, false)
						.time(35).gravity(true).spawn(world);

				float brightness = 0.4f + world.rand.nextFloat() * 0.5f;
				ParticleBuilder.create(ParticleBuilder.Type.DARK_MAGIC, world.rand, entity.posX, entity.posY + 0.5, entity.posZ, 2, false)
						.clr(brightness, brightness + 0.1f, 1.0f).spawn(world);
			}
		}
	}

	/**
	 * returns the action that specifies what animation to play when the items is being used
	 */
	public EnumAction getItemUseAction(ItemStack stack) {
		return EnumAction.BLOCK;
	}

	/**
	 * How long it takes to use or consume an item
	 */
	public int getMaxItemUseDuration(ItemStack stack) {
		return 72000;
	}

	/**
	 * Called when the equipped item is right clicked.
	 */
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer playerIn, EnumHand handIn) {
		ItemStack itemstack = playerIn.getHeldItem(handIn);
		playerIn.setActiveHand(handIn);
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemstack);
	}

	/**
	 * Return whether this item is repairable in an anvil.
	 *
	 * @param toRepair the {@code ItemStack} being repaired
	 * @param repair   the {@code ItemStack} being used to perform the repair
	 */
	@Override
	public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
		return false;
	}

	// enchantment glint
	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack stack) {
		return true;
	}

	@Override
	public int getItemEnchantability() {
		return 0;
	}

	@Override
	public boolean isEnchantable(ItemStack stack) {
		return false;
	}

	@Override
	public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
		return false;
	}

	// Cannot be dropped
	@Override
	public boolean onDroppedByPlayer(ItemStack item, EntityPlayer player) {
		return false;
	}

	@SubscribeEvent
	public static void attackEvent(LivingAttackEvent event) {
		if (!event.getEntity().world.isRemote) {
			if (!(event.getEntityLiving() instanceof EntityPlayer)) {
				return;
			}
			EntityPlayer player = (EntityPlayer) event.getEntityLiving();
			if (((EntityPlayer) event.getEntityLiving()).getActiveItemStack() == null) {
				return;
			}
			ItemStack activeItemStack = player.getActiveItemStack();

			if (activeItemStack != null && activeItemStack.getItem() instanceof ItemIceShield) {
				if (event.getSource().getImmediateSource() instanceof EntityLivingBase) {
					if (!MagicDamage.isEntityImmune(MagicDamage.DamageType.FROST, (EntityLivingBase) event.getSource().getImmediateSource())) {
						((EntityLivingBase) event.getSource().getImmediateSource()).addPotionEffect(new PotionEffect(WizardryPotions.frost,
								40, 0));
					}
				}
			}
		}
	}

	@Override
	public boolean isShield(ItemStack stack, @Nullable EntityLivingBase entity) {
		return true;
	}
}
