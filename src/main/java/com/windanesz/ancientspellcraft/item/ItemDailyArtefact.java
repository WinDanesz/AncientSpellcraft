package com.windanesz.ancientspellcraft.item;

import com.windanesz.ancientspellcraft.registry.AncientSpellcraftTabs;
import electroblob.wizardry.Wizardry;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public abstract class ItemDailyArtefact extends Item {
	private static final String LAST_OPEN_TIME_TAG = "last_open_time";

	private final EnumRarity rarity;

	public ItemDailyArtefact(EnumRarity rarity) {
		setMaxStackSize(1);
		this.rarity = rarity;
		setCreativeTab(AncientSpellcraftTabs.ANCIENTSPELLCRAFT_GEAR);
	}

	public void addReadinessPropertyOverride() {
		this.addPropertyOverride(new ResourceLocation("ready"), new IItemPropertyGetter() {
			@SideOnly(Side.CLIENT)
			public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
				if (worldIn == null && entityIn != null) {
					return isReady(entityIn.getEntityWorld(), stack) ? 0f : 1f;
				} else {
					return isReady(worldIn, stack) ? 0f : 1f;
				}
			}
		});
	}

	@Override
	public EnumRarity getRarity(ItemStack stack) {
		return rarity;
	}

	@Override
	public boolean hasEffect(ItemStack stack) {
		return rarity == EnumRarity.EPIC;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, net.minecraft.client.util.ITooltipFlag advanced) {
		Wizardry.proxy.addMultiLineDescription(tooltip, "item." + this.getRegistryName() + ".desc");
		Wizardry.proxy.addMultiLineDescription(tooltip, "tooltip.ancientspellcraft:artefact_use.usage", new Style().setItalic(true));
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);
		long currentWorldTime = player.world.getTotalWorldTime();

		if (stack.hasTagCompound() && stack.getTagCompound().hasKey(LAST_OPEN_TIME_TAG)) {
			long lastAccess = stack.getTagCompound().getLong(LAST_OPEN_TIME_TAG);
			if (isFullDayBetween(lastAccess, currentWorldTime)) {
				performAction(player);
				setLastOpenTimeCurrent(stack, currentWorldTime);
				return new ActionResult<>(EnumActionResult.SUCCESS, stack);
			} else {
				if (!player.world.isRemote) { player.sendStatusMessage(new TextComponentTranslation("item." + this.getRegistryName() + ".empty"), true); }
				return new ActionResult<>(EnumActionResult.FAIL, stack);
			}
		} else {
			performAction(player);
			setLastOpenTimeCurrent(stack, currentWorldTime);
			return new ActionResult<>(EnumActionResult.SUCCESS, stack);
		}
	}

	public static boolean isReady(World world, ItemStack stack) {
		if (world != null && !stack.isEmpty() && stack.hasTagCompound() && stack.getTagCompound().hasKey(LAST_OPEN_TIME_TAG)) {
			long currentWorldTime = world.getTotalWorldTime();
			long lastAccess = stack.getTagCompound().getLong(LAST_OPEN_TIME_TAG);
			return isFullDayBetween(lastAccess, currentWorldTime);
		}
		return true;
	}

	public static boolean isFullDayBetween(long startTime, long endTime) {
		long fullDay = 24000;
		return (endTime - startTime) >= fullDay;
	}

	public abstract void performAction(EntityPlayer player);

	public static void setLastOpenTimeCurrent(ItemStack stack, long currentTime) {
		if (stack.hasTagCompound()) {
			NBTTagCompound nbt = stack.getTagCompound();
			nbt.setLong(LAST_OPEN_TIME_TAG, currentTime);
			stack.setTagCompound(nbt);
		} else {
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setLong(LAST_OPEN_TIME_TAG, currentTime);
			stack.setTagCompound(nbt);
		}

	}
}
