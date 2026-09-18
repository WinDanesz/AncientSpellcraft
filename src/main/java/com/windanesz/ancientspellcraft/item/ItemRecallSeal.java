package com.windanesz.ancientspellcraft.item;

import com.windanesz.ancientspellcraft.Settings;
import com.windanesz.ancientspellcraft.util.ASUtils;
import electroblob.wizardry.item.ItemArtefact;
import electroblob.wizardry.util.Location;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nullable;
import java.util.List;

public class ItemRecallSeal extends ItemASArtefact {

	// Seal (tool) NBT keys - these describe the seal's own current configuration.
	private static final String NBT_BOUND_LOCATION = "boundLocation";
	private static final String NBT_DELAY_SECONDS = "delaySeconds";
	private static final String NBT_EXPIRE_MODE = "expireMode";

	// Stamp NBT keys - nested under NBT_STAMP on whatever arbitrary item gets stamped.
	private static final String NBT_STAMP = "recallSealBinding";
	private static final String NBT_STAMP_LOCATION = "location";
	private static final String NBT_STAMP_DELAY_TICKS = "delayTicks";
	private static final String NBT_STAMP_EXPIRE = "expire";
	private static final String NBT_STAMP_START_TICK = "startTick";
	private static final String NBT_STAMP_LAST_SEEN_TICK = "lastSeenTick";

	private static final int DEFAULT_DELAY_SECONDS = 60;
	// How often (in ticks) player inventories are scanned for stamped items
	private static final int SCAN_INTERVAL_TICKS = 92;

	public ItemRecallSeal() {
		super(EnumRarity.EPIC, ItemArtefact.Type.CHARM);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {

		ItemStack stack = player.getHeldItem(hand);

		if (hand != EnumHand.MAIN_HAND || !Settings.isArtefactEnabled(this)) {
			return new ActionResult<>(EnumActionResult.PASS, stack);
		}

		RayTraceResult trace = this.rayTrace(world, player, false);
		BlockPos targetPos = trace != null && trace.typeOfHit == RayTraceResult.Type.BLOCK ? trace.getBlockPos() : null;
		boolean targetingContainer = targetPos != null && isValidContainer(world, targetPos);

		if (player.isSneaking()) {

			if (targetingContainer) {
				bindLocation(player, stack, targetPos);
				return new ActionResult<>(EnumActionResult.SUCCESS, stack);
			}

			ItemStack offhand = player.getHeldItem(EnumHand.OFF_HAND);

			if (!offhand.isEmpty()) {
				stampItem(player, stack, offhand);
			} else {
				cycleDelay(player, stack);
			}

			return new ActionResult<>(EnumActionResult.SUCCESS, stack);
		}

		if (!targetingContainer) {
			toggleExpireMode(player, stack);
			return new ActionResult<>(EnumActionResult.SUCCESS, stack);
		}

		// Not sneaking and looking at a chest: let the chest open normally.
		return new ActionResult<>(EnumActionResult.PASS, stack);
	}

	private static boolean isValidContainer(World world, BlockPos pos) {
		TileEntity te = world.getTileEntity(pos);
		if (te == null) return false;
		return te.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null) || te instanceof IInventory;
	}

	private void bindLocation(EntityPlayer player, ItemStack stack, BlockPos pos) {
		setBoundLocation(stack, new Location(pos, player.dimension));
		ASUtils.sendMessage(player, "item.ancientspellcraft:charm_recall_seal.bound", true, pos.getX(), pos.getY(), pos.getZ());
	}

	private void stampItem(EntityPlayer player, ItemStack seal, ItemStack target) {

		Location location = getBoundLocation(seal);

		if (location == null) {
			ASUtils.sendMessage(player, "item.ancientspellcraft:charm_recall_seal.not_bound", true);
			return;
		}

		NBTTagCompound stamp = new NBTTagCompound();
		stamp.setTag(NBT_STAMP_LOCATION, location.toNBT());
		stamp.setInteger(NBT_STAMP_DELAY_TICKS, getDelaySeconds(seal) * 20);
		stamp.setBoolean(NBT_STAMP_EXPIRE, isExpireMode(seal));

		if (!target.hasTagCompound()) target.setTagCompound(new NBTTagCompound());
		target.getTagCompound().setTag(NBT_STAMP, stamp);

		ASUtils.sendMessage(player, "item.ancientspellcraft:charm_recall_seal.stamped", true, target.getDisplayName());
	}

	private void cycleDelay(EntityPlayer player, ItemStack stack) {

		int[] presets = Settings.generalSettings.recall_seal_delay_presets_seconds;
		if (presets == null || presets.length == 0) return;

		int current = getDelaySeconds(stack);
		int index = 0;

		for (int i = 0; i < presets.length; i++) {
			if (presets[i] == current) {
				index = i;
				break;
			}
		}

		int next = presets[(index + 1) % presets.length];
		setDelaySeconds(stack, next);

		ASUtils.sendMessage(player, "item.ancientspellcraft:charm_recall_seal.delay_set", true, formatDuration(next));
	}

	private void toggleExpireMode(EntityPlayer player, ItemStack stack) {
		boolean expire = !isExpireMode(stack);
		setExpireMode(stack, expire);
		ASUtils.sendMessage(player, expire ? "item.ancientspellcraft:charm_recall_seal.mode_expire" : "item.ancientspellcraft:charm_recall_seal.mode_permanent", true);
	}

	// Seal NBT helpers

	@Nullable
	private static Location getBoundLocation(ItemStack stack) {
		if (!stack.hasTagCompound() || !stack.getTagCompound().hasKey(NBT_BOUND_LOCATION)) return null;
		return Location.fromNBT(stack.getTagCompound().getCompoundTag(NBT_BOUND_LOCATION));
	}

	private static void setBoundLocation(ItemStack stack, Location location) {
		if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
		stack.getTagCompound().setTag(NBT_BOUND_LOCATION, location.toNBT());
	}

	private static int getDelaySeconds(ItemStack stack) {
		if (!stack.hasTagCompound() || !stack.getTagCompound().hasKey(NBT_DELAY_SECONDS)) return DEFAULT_DELAY_SECONDS;
		return stack.getTagCompound().getInteger(NBT_DELAY_SECONDS);
	}

	private static void setDelaySeconds(ItemStack stack, int seconds) {
		if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
		stack.getTagCompound().setInteger(NBT_DELAY_SECONDS, seconds);
	}

	private static boolean isExpireMode(ItemStack stack) {
		return stack.hasTagCompound() && stack.getTagCompound().getBoolean(NBT_EXPIRE_MODE);
	}

	private static void setExpireMode(ItemStack stack, boolean expire) {
		if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
		stack.getTagCompound().setBoolean(NBT_EXPIRE_MODE, expire);
	}

	private static String formatDuration(int seconds) {
		if (seconds < 60) return seconds + "s";
		if (seconds < 3600) return seconds / 60 + "m" + (seconds % 60 == 0 ? "" : " " + seconds % 60 + "s");
		return seconds / 3600 + "h" + (seconds % 3600 == 0 ? "" : " " + formatDuration(seconds % 3600));
	}

	@SideOnly(Side.CLIENT)
	private static String getRemainingRecallTime(ItemStack stack) {
		NBTTagCompound stamp = stack.getTagCompound().getCompoundTag(NBT_STAMP);
		int delayTicks = stamp.getInteger(NBT_STAMP_DELAY_TICKS);

		// A newly stamped item has not necessarily been picked up by the server-side inventory
		// scanner yet. In that short interval, its full configured delay is still the best estimate.
		if (!stamp.hasKey(NBT_STAMP_START_TICK) || Minecraft.getMinecraft().world == null) {
			return formatDuration((delayTicks + 19) / 20);
		}

		long elapsedTicks = Minecraft.getMinecraft().world.getTotalWorldTime() - stamp.getLong(NBT_STAMP_START_TICK);
		long remainingTicks = Math.max(0, (long) delayTicks - elapsedTicks);
		return formatDuration((int) Math.min(Integer.MAX_VALUE, (remainingTicks + 19) / 20));
	}

	/** Adds recall timing to every stamped item, including items owned by other mods. */
	@Mod.EventBusSubscriber(value = Side.CLIENT)
	public static class RecallTooltipHandler {

		@SubscribeEvent
		public static void onItemTooltip(ItemTooltipEvent event) {
			ItemStack stack = event.getItemStack();
			if (stack.isEmpty() || !stack.hasTagCompound() || !stack.getTagCompound().hasKey(NBT_STAMP)) return;

			event.getToolTip().add(TextFormatting.LIGHT_PURPLE
					+ I18n.format("item.ancientspellcraft:charm_recall_seal.return_time", getRemainingRecallTime(stack)));
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {

		Location location = getBoundLocation(stack);

		if (location != null) {
			tooltip.add(TextFormatting.GREEN + "Bound to: " + location.pos.getX() + ", " + location.pos.getY() + ", " + location.pos.getZ());
		} else {
			tooltip.add(TextFormatting.RED + "Not bound to a container");
		}

		tooltip.add(TextFormatting.GRAY + "Recall delay: " + formatDuration(getDelaySeconds(stack)));
		tooltip.add(TextFormatting.GRAY + (isExpireMode(stack) ? "Stamps expire after recall" : "Stamps remain permanently bound"));
		tooltip.add(TextFormatting.YELLOW + "Sneak + right-click a chest to bind");
		tooltip.add(TextFormatting.YELLOW + "Sneak + right-click with an item in your other hand to stamp it");
		tooltip.add(TextFormatting.YELLOW + "Sneak + right-click (empty offhand, no target) to cycle delay");
		tooltip.add(TextFormatting.YELLOW + "Right-click (no target) to toggle expiry mode");

		if (!Settings.isArtefactEnabled(this)) {
			tooltip.add(TextFormatting.RED + "Disabled");
		}

		super.addInformation(stack, worldIn, tooltip, flagIn);
	}

	// ------------------------------------------------------------------
	// Passive recall ticking. This has to scan player inventories directly rather than relying on
	// Item#onUpdate, since stamped items are arbitrary item classes, not instances of this class.
	//
	// All state lives in the stamped item's own NBT (NBT_STAMP_START_TICK / NBT_STAMP_LAST_SEEN_TICK),
	// keyed against World#getTotalWorldTime() - a clock that's saved with the world and therefore
	// survives server restarts, unlike EntityPlayer#ticksExisted (which resets to 0 for the fresh
	// EntityPlayer instance created on every relogin). No runtime-only bookkeeping is used anywhere.
	//
	// The one thing we deliberately do NOT do is refresh that NBT on every scan for a stack that's
	// currently equipped in a hand: any NBT write to a held stack makes the client think the item
	// changed and replays the re-equip animation. A stack can't visit a chest while it's physically
	// in a player's hand, so skipping the refresh while held can never hide a real gap - the write is
	// simply deferred until the stack next shows up in a non-held slot (or until it's stamped for the
	// very first time, which is one unavoidable, expected write).
	// ------------------------------------------------------------------

	@Mod.EventBusSubscriber
	public static class RecallHandler {

		@SubscribeEvent
		public static void onPlayerTick(TickEvent.PlayerTickEvent event) {

			if (event.phase != TickEvent.Phase.END || event.player.world.isRemote) return;
			if (event.player.ticksExisted % SCAN_INTERVAL_TICKS != 0) return;

			EntityPlayer player = event.player;

			scanInventory(player, player.inventory.mainInventory);
			scanInventory(player, player.inventory.armorInventory);
			scanInventory(player, player.inventory.offHandInventory);
		}

		private static void scanInventory(EntityPlayer player, NonNullList<ItemStack> inventory) {

			ItemStack heldMain = player.getHeldItemMainhand();
			ItemStack heldOff = player.getHeldItemOffhand();

			for (ItemStack stack : inventory) {

				if (stack.isEmpty() || !stack.hasTagCompound() || !stack.getTagCompound().hasKey(NBT_STAMP)) continue;

				NBTTagCompound stamp = stack.getTagCompound().getCompoundTag(NBT_STAMP);
				long currentTime = clockTime(player);

				if (!stamp.hasKey(NBT_STAMP_START_TICK)) {
					// First time this stamp has ever been observed - unavoidable one-off write.
					stamp.setLong(NBT_STAMP_START_TICK, currentTime);
					stamp.setLong(NBT_STAMP_LAST_SEEN_TICK, currentTime);
				} else if (stack != heldMain && stack != heldOff) {
					// Safe to write: this slot isn't rendered as an equipped item.
					long lastSeen = stamp.getLong(NBT_STAMP_LAST_SEEN_TICK);

					if (currentTime - lastSeen > SCAN_INTERVAL_TICKS) {
						// Gap since we last saw this stack anywhere in the player's inventory - it must
						// have spent time in a chest (or on the ground, etc.) - restart the timer.
						stamp.setLong(NBT_STAMP_START_TICK, currentTime);
					}

					stamp.setLong(NBT_STAMP_LAST_SEEN_TICK, currentTime);
				}
				// else: currently held in a hand - skip writes entirely; a gap is impossible while
				// held, so whatever startTick was last established is still correct as-is.

				long elapsed = currentTime - stamp.getLong(NBT_STAMP_START_TICK);
				int delayTicks = stamp.getInteger(NBT_STAMP_DELAY_TICKS);

				if (elapsed < delayTicks) continue;

				attemptRecall(stack, stamp);
			}
		}

		private static long clockTime(EntityPlayer player) {
			World overworld = DimensionManager.getWorld(0);
			return overworld != null ? overworld.getTotalWorldTime() : player.world.getTotalWorldTime();
		}

		private static void attemptRecall(ItemStack stack, NBTTagCompound stamp) {

			Location location = Location.fromNBT(stamp.getCompoundTag(NBT_STAMP_LOCATION));

			World targetWorld = DimensionManager.getWorld(location.dimension);

			if (targetWorld == null || !targetWorld.isBlockLoaded(location.pos)) {
				return; // Target dimension/chunk isn't currently loaded - retry on a later scan.
			}

			TileEntity te = targetWorld.getTileEntity(location.pos);
			IItemHandler handler = te == null ? null : te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);

			if (handler == null) {
				// The container is gone or no longer a valid inventory - the binding goes inert.
				stack.getTagCompound().removeTag(NBT_STAMP);
				return;
			}

			boolean expire = stamp.getBoolean(NBT_STAMP_EXPIRE);

			ItemStack toInsert = stack.copy();
			if (expire) {
				toInsert.getTagCompound().removeTag(NBT_STAMP);
				if (toInsert.getTagCompound().isEmpty()) toInsert.setTagCompound(null);
			}

			ItemStack simulatedRemainder = ItemHandlerHelper.insertItemStacked(handler, toInsert.copy(), true);

			if (!simulatedRemainder.isEmpty()) {
				return; // No room right now - retry on a later scan.
			}

			ItemHandlerHelper.insertItemStacked(handler, toInsert, false);
			stack.setCount(0);
		}
	}
}
