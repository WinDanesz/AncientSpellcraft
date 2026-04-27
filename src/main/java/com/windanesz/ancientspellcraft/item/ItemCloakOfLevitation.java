package com.windanesz.ancientspellcraft.item;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import com.windanesz.ancientspellcraft.Settings;
import com.windanesz.ancientspellcraft.integration.baubles.ASBaublesIntegration;
import electroblob.wizardry.item.ItemArtefact;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ItemCloakOfLevitation extends ItemASArtefact implements IBauble {

	/** Maximum levitation charges — read from config. */
	public static int getMaxCharges() {
		return Settings.generalSettings.cloak_of_levitation_max_charges;
	}

	/** Fly speed granted by this cloak - slower than default (0.05F) but usable */
	public static final float FLY_SPEED = 0.03F;

	/** Default fly speed to restore when deactivating */
	private static final float DEFAULT_FLY_SPEED = 0.05F;

	/** Game ticks before one charge unit is consumed (20 ticks = 1 second) */
	private static final int CONSUME_TICKS_PER_UNIT = 20;

	/** Game ticks before one charge unit is regenerated — read from config. */
	private static int getRegenTicksPerUnit() {
		return Settings.generalSettings.cloak_of_levitation_recharge_ticks;
	}

	/** Grace period in ticks after activation before ground-check deactivates */
	private static final int ACTIVATION_GRACE_TICKS = 10;

	private static final String NBT_CHARGES = "levCharges";
	private static final String NBT_ACTIVE = "levActive";
	private static final String NBT_ACTIVATE_TICK = "levActivateTick";

	public ItemCloakOfLevitation(EnumRarity rarity) {
		super(rarity, ItemArtefact.Type.BODY);
	}

	@Override
	public BaubleType getBaubleType(ItemStack itemstack) {
		return BaubleType.BODY;
	}

	@Override
	public void onWornTick(ItemStack stack, EntityLivingBase entity) {
		if (!(entity instanceof EntityPlayer) || entity.world.isRemote) return;
		if (!Settings.isArtefactEnabled(this)) return;

		EntityPlayer player = (EntityPlayer) entity;
		boolean active = isActive(stack);

		if (active) {
			// Ensure flight capabilities are set each tick in case they got cleared
			if (!player.capabilities.allowFlying) {
				player.capabilities.allowFlying = true;
				player.capabilities.setFlySpeed(FLY_SPEED);
				if (player instanceof EntityPlayerMP) {
					((EntityPlayerMP) player).sendPlayerAbilities();
				}
			}

			// Deactivate if player is on the ground, with a grace period after activation
			int activateTick = getActivateTick(stack);
			boolean gracePeriodOver = (entity.ticksExisted - activateTick) > ACTIVATION_GRACE_TICKS;
			if (gracePeriodOver && player.onGround) {
				deactivate(stack, player);
				return;
			}

			// Consume 1 charge unit per CONSUME_TICKS_PER_UNIT ticks
			if (entity.ticksExisted % CONSUME_TICKS_PER_UNIT == 0) {
				int charges = getCharges(stack);
				charges--;
				if (charges <= 0) {
					setCharges(stack, 0);
					deactivate(stack, player);
				} else {
					setCharges(stack, charges);
				}
			}

		} else {
			// Revoke flight if we granted it (don't touch creative/spectator players)
			if (!player.isCreative() && player.capabilities.allowFlying) {
				player.capabilities.allowFlying = false;
				player.capabilities.isFlying = false;
				player.capabilities.setFlySpeed(DEFAULT_FLY_SPEED);
				if (player instanceof EntityPlayerMP) {
					((EntityPlayerMP) player).sendPlayerAbilities();
				}
			}

			// Recharge 1 unit per getRegenTicksPerUnit() ticks, capped at getMaxCharges()
			if (entity.ticksExisted % getRegenTicksPerUnit() == 0) {
				int charges = getCharges(stack);
				if (charges < getMaxCharges()) {
					setCharges(stack, charges + 1);
				}
			}
		}
	}

	@Override
	public void onUnequipped(ItemStack stack, EntityLivingBase entity) {
		if (!(entity instanceof EntityPlayer)) return;
		EntityPlayer player = (EntityPlayer) entity;
		setActive(stack, false);
		if (!player.isCreative()) {
			player.capabilities.allowFlying = false;
			player.capabilities.isFlying = false;
			player.capabilities.setFlySpeed(DEFAULT_FLY_SPEED);
			if (player instanceof EntityPlayerMP) {
				((EntityPlayerMP) player).sendPlayerAbilities();
			}
		}
	}

	/**
	 * Toggles levitation on/off for the player. Called server-side from the packet handler.
	 */
	public static void toggleLevitation(EntityPlayer player) {
		List<ItemStack> stacks = ASBaublesIntegration.getEquippedArtefactStacks(player, ItemArtefact.Type.BODY);
		for (ItemStack stack : stacks) {
			if (stack.getItem() instanceof ItemCloakOfLevitation) {
				boolean wasActive = isActive(stack);
				if (!wasActive && getCharges(stack) <= 0) {
					// No charges left, cannot activate
					return;
				}
				setActive(stack, !wasActive);
				if (!wasActive) {
					// Activating: enable slow flight immediately
					setActivateTick(stack, player.ticksExisted);
					player.capabilities.allowFlying = true;
					player.capabilities.isFlying = true;
					player.capabilities.setFlySpeed(FLY_SPEED);
				} else {
					// Deactivating manually
					if (!player.isCreative()) {
						player.capabilities.allowFlying = false;
						player.capabilities.isFlying = false;
						player.capabilities.setFlySpeed(DEFAULT_FLY_SPEED);
					}
				}
				if (player instanceof EntityPlayerMP) {
					((EntityPlayerMP) player).sendPlayerAbilities();
				}
				return;
			}
		}
	}

	private static void deactivate(ItemStack stack, EntityPlayer player) {
		setActive(stack, false);
		if (!player.isCreative()) {
			player.capabilities.allowFlying = false;
			player.capabilities.isFlying = false;
			player.capabilities.setFlySpeed(DEFAULT_FLY_SPEED);
			if (player instanceof EntityPlayerMP) {
				((EntityPlayerMP) player).sendPlayerAbilities();
			}
		}
	}

	public static boolean isActive(ItemStack stack) {
		return stack.hasTagCompound() && stack.getTagCompound().getBoolean(NBT_ACTIVE);
	}

	public static void setActive(ItemStack stack, boolean active) {
		if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
		stack.getTagCompound().setBoolean(NBT_ACTIVE, active);
	}

	public static int getCharges(ItemStack stack) {
		if (!stack.hasTagCompound() || !stack.getTagCompound().hasKey(NBT_CHARGES)) {
			return getMaxCharges();
		}
		return stack.getTagCompound().getInteger(NBT_CHARGES);
	}

	public static void setCharges(ItemStack stack, int charges) {
		if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
		stack.getTagCompound().setInteger(NBT_CHARGES, Math.max(0, Math.min(getMaxCharges(), charges)));
	}

	private static int getActivateTick(ItemStack stack) {
		if (!stack.hasTagCompound()) return 0;
		return stack.getTagCompound().getInteger(NBT_ACTIVATE_TICK);
	}

	private static void setActivateTick(ItemStack stack, int tick) {
		if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
		stack.getTagCompound().setInteger(NBT_ACTIVATE_TICK, tick);
	}

	@Override
	public boolean canEquip(ItemStack itemstack, EntityLivingBase player) {
		return true;
	}

	@Override
	public boolean canUnequip(ItemStack itemstack, EntityLivingBase player) {
		return true;
	}

	@Override
	public boolean willAutoSync(ItemStack itemstack, EntityLivingBase player) {
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		tooltip.add(I18n.format("item.ancientspellcraft:body_cloak_of_levitation.desc"));
		int charges = getCharges(stack);
		tooltip.add(I18n.format("item.ancientspellcraft:body_cloak_of_levitation.charges", charges, getMaxCharges()));
		if (isActive(stack)) {
			tooltip.add("\u00A7aActive");
		}
	}
}
