package com.windanesz.ancientspellcraft.util;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.registry.ASBlocks;
import com.windanesz.ancientspellcraft.registry.ASItems;
import com.windanesz.ancientspellcraft.registry.ASPotions;
import electroblob.wizardry.constants.Constants;
import electroblob.wizardry.constants.Element;
import electroblob.wizardry.constants.Tier;
import electroblob.wizardry.item.IManaStoringItem;
import electroblob.wizardry.item.ISpellCastingItem;
import electroblob.wizardry.item.IWorkbenchItem;
import electroblob.wizardry.item.ItemArtefact;
import electroblob.wizardry.item.ItemCrystal;
import electroblob.wizardry.item.ItemSpellBook;
import electroblob.wizardry.item.ItemWand;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.registry.WizardryPotions;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.BlockUtils;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.potion.Potion;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import static electroblob.wizardry.util.InventoryUtils.getHotbar;

/**
 * A class for common utilities used in the mod
 *
 * @autor Dan (WinDanesz)
 **/
public final class ASUtils {

	/*
	public static List<Item> getRegisteredSpellScrolls() {
		List<Item> scrolls = ForgeRegistries.ITEMS.getValuesCollection().stream().filter(i -> i instanceof ItemScroll).distinct().collect(Collectors.toList());
		Map<Item, Boolean> scrollsToCheck = new HashMap<>();
		for (Item scroll : scrolls) {
			scrollsToCheck.put(scroll, false);
		}

		List<Item> validScrolls = new ArrayList<>();

		for (Spell spell : Spell.getAllSpells()) {

			for (Item scroll : scrollsToCheck.keySet()) {
				if (spell.applicableForItem(scroll)) {
					validScrolls.add(scroll);
					scrollsToCheck.remove(scroll);
				}
			}

			if (scrollsToCheck.isEmpty()) {
				break;
			}
		}
		return validScrolls;
	}
	*/

	/**
	 * Get a Map of all biomes with their Ids
	 *
	 * @return A Map with all registered biome names with their corresponding biome Ids.
	 **/
	public static List<String> getAllBiomes() {
		return ForgeRegistries.BIOMES.getValuesCollection().stream().map(Biome::getBiomeName).collect(Collectors.toList());
	}

	/**
	 * Check if a biome name is registered
	 *
	 * @return A Map with all registered biome names with their corresponding biome Ids.
	 **/
	public static boolean isBiomeNameRegistered(String biomeName) {
		List<String> biomes = getAllBiomes();
		return biomes.contains(biomeName);
	}

	public static ResourceLocation getBiomeRegistryNameFromName(String biomeName) {
		Optional<Map.Entry<ResourceLocation, Biome>> optional = ForgeRegistries.BIOMES.getEntries().stream()
				.filter(p -> p.getValue().getBiomeName().equals(biomeName)).findFirst();
		return optional.map(Map.Entry::getKey).orElse(null);
	}

	public static ItemStack pickRandomStackFromItemStackList(List<ItemStack> stackList) {
		return stackList.get(new Random().nextInt(stackList.size()));
	}

	public static Element getCrystalElementFromStack(ItemStack crystal) {
		int metadata = crystal.getMetadata();
		return Element.values()[metadata];
	}

	public static Potion getRandomPowerPotion() {
		List<Potion> potionList = Arrays.asList(
				ASPotions.mana_regeneration,
				ASPotions.spell_blast,
				ASPotions.spell_siphon,
				ASPotions.spell_cooldown,
				ASPotions.spell_range,
				ASPotions.spell_duration
		);

		return potionList.get(AncientSpellcraft.rand.nextInt(potionList.size()));
	}

	/**
	 * @param min the minimum float value
	 * @param max the maximum float value
	 * @return returns a random float number between min-max
	 */

	public static float randFloatBetween(float min, float max) {
		return min + AncientSpellcraft.rand.nextFloat() * (max - min);
	}

	public static int randIntBetween(int min, int max) {
		return AncientSpellcraft.rand.nextInt((max - min) + 1) + min;
	}

	/**
	 * Looks for the given ItemStack in the player's inventory and shrinks the first
	 * found one by the specified amount. Order to check: main inv -> offhand -> others (mainhand, armorinv)
	 *
	 * @param player        the player who's inventory will be checked
	 * @param stackToShrink the specific item stack which will be checked
	 * @return True if found stack(s) to shrink and shrinked it successfully, False if not.
	 */
	public static boolean shrinkInventoryStackByOne(EntityPlayer player, ItemStack stackToShrink) {
		if (player.inventory.hasItemStack(stackToShrink)) {

			int j = -1;
			for (int i = 0; i < player.inventory.mainInventory.size(); ++i) {
				if (!(player.inventory.mainInventory.get(i).isEmpty()) && stackEqualExact(player.inventory.mainInventory.get(i), stackToShrink)) {
					j = i;
					break;
				}
			}

			if (j != -1) {
				player.inventory.getStackInSlot(j).shrink(1);
				return true;
			} else {
				// check offhand only after main inv
				if (stackEqualExact(player.getHeldItemOffhand(), stackToShrink)) {
					player.getHeldItemOffhand().shrink(1);
					return true;
				}
			}
		}
		return false;
	}

	@Nullable
	public static ItemStack getItemStackFromInventoryHotbar(EntityPlayer player, Item item) {
		for (int i = 0; i < 9; ++i) {

			if (!player.inventory.mainInventory.get(i).isEmpty()) {
				ItemStack currItemStack = player.inventory.mainInventory.get(i);
				if (currItemStack.getItem().getRegistryName() == item.getRegistryName()) {
					return currItemStack;
				}
			}
		}
		return null;
	}

	/**
	 * Compares if two given stacks are equal by metadata. Order doesn't matters
	 *
	 * @param stack1 first stack to compare
	 * @param stack2 second stack to compare
	 * @return True if the stacks are equal, false otherwise.
	 */
	public static boolean stackEqualExact(ItemStack stack1, ItemStack stack2) {
		return stack1.getItem() == stack2.getItem() && (!stack1.getHasSubtypes() || stack1.getMetadata() == stack2.getMetadata()) && ItemStack.areItemStackTagsEqual(stack1, stack2);
	}

	public static List<EntityItem> getEntityItemsWithinRadius(double radius, double x, double y, double z, World world) {
		AxisAlignedBB aabb = new AxisAlignedBB(x - radius, y - radius, z - radius, x + radius, y + radius, z + radius);
		List<EntityItem> entityItemList = world.getEntitiesWithinAABB(EntityItem.class, aabb);
		for (int i = 0; i < entityItemList.size(); i++) {
			if (entityItemList.get(i).getDistance(x, y, z) > radius) {
				entityItemList.remove(i);
				break;
			}
		}
		return entityItemList;
	}

	/**
	 * Checks if an entity has a biped model
	 *
	 * @param entity The entity to check against
	 * @return true or false whether the entity's model is a biped or a not biped model.
	 */
	@SideOnly(Side.CLIENT)
	public static boolean isEntityBiped(Entity entity) {
		return Minecraft.getMinecraft().getRenderManager().getEntityClassRenderObject(entity.getClass()) instanceof RenderBiped;
	}

	/**
	 * Returns all wands from the player's hotbar (possibly including main and offhand wands)
	 *
	 * @param player to check
	 * @return List of wand itemstacks
	 */
	public static List<ItemStack> getAllHotbarWands(EntityPlayer player) {
		return getAllHotbarWands(player, Tier.MASTER);
	}

	public static List<ItemStack> getAllHotbarWands(EntityPlayer player, Tier maxTier) {
		List<ItemStack> wands = new ArrayList<>();
		for (ItemStack stack : getHotbar(player)) {
			if (stack.getItem() instanceof ItemWand && ((ItemWand) stack.getItem()).tier.level <= maxTier.level) {
				wands.add(stack);
			}
		}
		return wands;
	}

	/**
	 * Attempts to drain mana from the mainhand or offhand wand, prioritizes mainhand.
	 *
	 * @param entity who holds the wand (possibly)
	 * @param amount to drain
	 * @return true if successfully drained mana, false if not
	 */
	public static boolean attemptConsumeManaFromHand(EntityLivingBase entity, int amount) {
		if (!entity.getHeldItemMainhand().isEmpty() && entity.getHeldItemMainhand().getItem() instanceof ItemWand) {
			if (attemptConsumeManaFromStack(entity, entity.getHeldItemMainhand(), amount)) {
				return true;
			} else if (!entity.getHeldItemOffhand().isEmpty() && entity.getHeldItemOffhand().getItem() instanceof ItemWand) {
				return attemptConsumeManaFromStack(entity, entity.getHeldItemOffhand(), amount);
			}
		} else if (!entity.getHeldItemOffhand().isEmpty() && entity.getHeldItemOffhand().getItem() instanceof ItemWand) {
			return attemptConsumeManaFromStack(entity, entity.getHeldItemOffhand(), amount);
		}
		return false;
	}

	public static boolean attemptConsumeManaFromStack(EntityLivingBase entity, ItemStack wandStack, int amount) {
		ItemWand wand = (ItemWand) wandStack.getItem();
		int mana = wand.getMana(wandStack);
		if (mana > amount) {
			wand.consumeMana(wandStack, amount, entity);
			return true;
		} else {
			return false;
		}
	}

	public static boolean wandHasSpell(ItemStack wand, Spell spell) {
		for (Spell currentSpell : ((ItemWand) wand.getItem()).getSpells(wand)) {
			if (currentSpell == spell) {
				return true;
			}
		}
		return false;
	}

	public static <T> T getRandomListItem(List<T> list) {
		int listSize = list.size();
		int randomIndex = AncientSpellcraft.rand.nextInt(listSize);
		return list.get(randomIndex);
	}

	public static int getRandomMapId(Map<?, ?> map) {
		int mapSize = map.keySet().toArray().length;
		return AncientSpellcraft.rand.nextInt(mapSize);
	}

	public static int getRandomNumberInRange(int min, int max) {
		Random r = new Random();
		return r.nextInt((max - min) + 1) + min;
	}

	public static Optional<TileEntity> getTile(IBlockAccess world, BlockPos pos) {
		return getTile(world, pos, TileEntity.class);
	}

	public static <T> Optional<T> getTile(@Nullable IBlockAccess world, @Nullable BlockPos pos, Class<T> teClass) {
		if (world == null || pos == null) {
			return Optional.empty();
		}

		TileEntity te = world.getTileEntity(pos);

		if (teClass.isInstance(te)) {
			return Optional.of(teClass.cast(te));
		}

		return Optional.empty();
	}

	/**
	 * Static method to give an itemstack to a player. Handles side checks and null checks, prioritizes the hands.
	 *
	 * @param player the player who receives the item
	 * @param stack  the stack to give
	 * @return false if failed to give, true if successfully gave the item
	 */
	public static boolean giveStackToPlayer(EntityPlayer player, ItemStack stack) {
		if (player != null && stack != null && !stack.isEmpty()) {

			if (!player.world.isRemote) {

				if (player.getHeldItemMainhand().isEmpty()) {
					// main hand
					player.setHeldItem(EnumHand.MAIN_HAND, stack);
				} else if (player.getHeldItemOffhand().isEmpty()) {
					// offhand
					player.setHeldItem(EnumHand.OFF_HAND, stack);
				} else {
					// any slot
					if (!player.inventory.addItemStackToInventory(stack)) {
						// or just drop the item..
						player.dropItem(stack, false);
					}
				}

				return true;
			}
		}

		return false;
	}

	public static boolean isManaStoringCastingItem(Item item) {
		return item instanceof ISpellCastingItem && item instanceof IManaStoringItem;
	}

	/**
	 * Serializes an ItemStack into an NBTTagCompound with all of the ItemStack's data.
	 *
	 * @param stack the ItemStack to serialize
	 * @return an NBTTagCompound with the ItemStack's data.
	 */
	public static NBTTagCompound convertToNBT(ItemStack stack) {
		NBTTagCompound stackCompound = new NBTTagCompound();
		return stack.writeToNBT(stackCompound);
	}

	/**
	 * Serializes an ItemStack into an NBTTagCompound with all of the ItemStack's data but only as a single stack, ignoring the actual count.
	 *
	 * @param stack the ItemStack to serialize
	 * @return an NBTTagCompound with the ItemStack's data.
	 */
	public static NBTTagCompound convertToNBTSingeCount(ItemStack stack) {
		ItemStack stack1 = stack.copy();
		stack1.setCount(1);
		return convertToNBT(stack1);
	}

	public static List<BlockPos> getBlockPosListFromTag(NBTTagCompound listCompound) {
		List<String> list = new ArrayList<>(listCompound.getKeySet());
		List<BlockPos> blockPosList = new ArrayList<>();
		for (String tag : list) {
			blockPosList.add(NBTUtil.getPosFromTag(listCompound.getCompoundTag(tag)));
		}

		return blockPosList;
	}

	public static NBTTagCompound writeBlockPosListToTag(List<BlockPos> posList) {
		NBTTagCompound posTagList = new NBTTagCompound();

		int i = 0;
		for (BlockPos pos : posList) {
			posTagList.setTag(String.valueOf(i), NBTUtil.createPosTag(pos));
			i++;
		}
		return posTagList;
	}

	public static boolean isInjured(EntityLivingBase entityLivingBase) {
		return entityLivingBase.getMaxHealth() > entityLivingBase.getHealth();
	}
	public static boolean isEntityConsideredUndead(Entity entity) {
		if (!(entity instanceof EntityLivingBase)) {
			return false;
		}
		
		EntityLivingBase livingEntity = (EntityLivingBase) entity;
		
		// Skip if entity is still being constructed
		if (livingEntity.world == null || livingEntity.ticksExisted == 0) {
			return false;
		}
		
		try {
			boolean isUndead = livingEntity.isEntityUndead();
			boolean hasCurse = false;
			
			// Only check for curse if the entity is fully initialized
			if (WizardryPotions.curse_of_undeath != null) {
				try {
					hasCurse = livingEntity.isPotionActive(WizardryPotions.curse_of_undeath);
				} catch (NullPointerException e) {
					// Safely handle null pointer if potion map isn't initialized yet
				}
			}
			
			return isUndead || hasCurse;
		} catch (Exception e) {
			// Return false if any exception occurs during undead status check
			return false;
		}
	}

	public static boolean isQuicksandBlock(@Nullable IBlockState state) {
		if (state == null) {
			return false;
		}

		if (state.getBlock() == ASBlocks.QUICKSAND) {
			return true;
		}

		ResourceLocation registryName = state.getBlock().getRegistryName();
		if (registryName == null) {
			return false;
		}

		String path = registryName.getPath();
		if ("quicksand".equals(path) || path.contains("quicksand")) {
			return true;
		}

		return "biomesoplenty".equals(registryName.getNamespace()) && ("sand".equals(path) || "sand_fluid".equals(path));
	}

	public static boolean isDruidBootsActive(@Nullable EntityPlayer player) {
		if (player == null) {
			return false;
		}

		String[] druidBootsNames = {"wizard_boots_earth", "sage_boots_earth", "warlock_boots_earth", "druid_boots"};
		for (String name : druidBootsNames) {
			Item druidBoots = ForgeRegistries.ITEMS.getValue(new ResourceLocation("ebwizardry", name));
			if (druidBoots == null) {
				druidBoots = ForgeRegistries.ITEMS.getValue(new ResourceLocation("wizardry", name));
			}
			if (druidBoots != null && druidBoots instanceof ItemArtefact && ItemArtefact.isArtefactActive(player, druidBoots)) {
				return true;
			}
			if (druidBoots != null && com.windanesz.ancientspellcraft.integration.baubles.ASBaublesIntegration.enabled()) {
				for (ItemStack stack : com.windanesz.ancientspellcraft.integration.baubles.ASBaublesIntegration.getEquippedArtefactStacks(player, ItemArtefact.Type.CHARM, ItemArtefact.Type.BELT, ItemArtefact.Type.AMULET, ItemArtefact.Type.RING)) {
					if (!stack.isEmpty() && stack.getItem() == druidBoots) {
						return true;
					}
				}
			}
		}

		ItemStack boots = player.getItemStackFromSlot(EntityEquipmentSlot.FEET);
		if (!boots.isEmpty()) {
			ResourceLocation bootsRegistryName = boots.getItem().getRegistryName();
			if (bootsRegistryName != null) {
				String path = bootsRegistryName.getPath();
				if ("druid_boots".equals(path) || "wizard_boots_earth".equals(path) || "sage_boots_earth".equals(path) || "warlock_boots_earth".equals(path) || path.contains("quicksand") || path.contains("druid")) {
					return true;
				}
			}
		}

		return false;
	}

	public static boolean hasQuicksandImmunity(@Nullable EntityPlayer player) {
		if (player == null || !(ASItems.charm_quicksand_walker instanceof ItemArtefact)) {
			return false;
		}
        return ItemArtefact.isArtefactActive(player, ASItems.charm_quicksand_walker);
    }

	public static List<BlockPos> getHollowSphere(EntityLivingBase caster, SpellModifiers modifiers, float radius) {
		List<BlockPos> largeFilledSphere = BlockUtils.getBlockSphere(caster.getPosition().up(), radius * modifiers.get(WizardryItems.blast_upgrade));
		List<BlockPos> smallFilledSphere = BlockUtils.getBlockSphere(caster.getPosition().up(), (radius - 1) * modifiers.get(WizardryItems.blast_upgrade));

		List<BlockPos> hollowSphere = new ArrayList<>();
		for (BlockPos pos : largeFilledSphere) {
			if (!smallFilledSphere.contains(pos)) {
				hollowSphere.add(pos);
			}
		}
		return hollowSphere;
	}

	public static class ReflectionUtil {
		public static Field getField(Class clazz, String fieldName) throws NoSuchFieldException {
			try {
				return clazz.getDeclaredField(fieldName);
			}
			catch (NoSuchFieldException e) {
				Class superClass = clazz.getSuperclass();
				if (superClass == null) {
					throw e;
				} else {
					return getField(superClass, fieldName);
				}
			}
		}

		public static void makeAccessible(Field field) {
			if (!Modifier.isPublic(field.getModifiers()) ||
					!Modifier.isPublic(field.getDeclaringClass().getModifiers())) {
				field.setAccessible(true);
			}
		}

		public static void removeFinalModifier(Field field) {
			try {
				Field modifiersField = Field.class.getDeclaredField("modifiers");
				modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * Shorthand method to do instance check and sideonly checks for player messages
	 */
	public static void sendMessage(Entity player, String translationKey, boolean actionBar, Object... args) {
		if (player instanceof EntityPlayer && !player.world.isRemote) {
			((EntityPlayer) player).sendStatusMessage(new TextComponentTranslation(translationKey, args), actionBar);
		}
	}

	public static ItemStack getSpellBookForSpell(Spell spell) {
		List<Item> bookTypeList = ForgeRegistries.ITEMS.getValuesCollection().stream().filter(i -> i instanceof ItemSpellBook).distinct().collect(Collectors.toList());
		Item book = null;
		for (int i = 0; i < bookTypeList.size(); i++) {
			Item currentBook = bookTypeList.get(i);
			if (spell.applicableForItem(currentBook)) {
				book = currentBook;
				break;
			}
		}

		if (book == null) {
			return ItemStack.EMPTY;
		}
		return new ItemStack(book, 1, spell.metadata());
	}

	public static boolean onApplyButtonPressedStandardManaRecharging(EntityPlayer player, Slot centre, Slot crystals, Slot upgrade, Slot[] spellBooks) {
		boolean changed = false;
		if (!(centre.getStack().getItem() instanceof IWorkbenchItem) || !(centre.getStack().getItem() instanceof IManaStoringItem)) {
			return false;
		}
		IManaStoringItem iManaStoringItem = (IManaStoringItem) centre.getStack().getItem();

		// Charges the item by appropriate amount
		if (crystals.getStack() != ItemStack.EMPTY && !iManaStoringItem.isManaFull(centre.getStack())) {

			int chargeDepleted = iManaStoringItem.getManaCapacity(centre.getStack()) - iManaStoringItem.getMana(centre.getStack());

			// Not too pretty but allows addons implementing the IManaStoringItem interface to provide their mana amount for custom crystals,
			// previously this was defaulted to the regular crystal's amount, allowing players to exploit it if a crystal was worth less mana than that.
			int manaPerItem = crystals.getStack().getItem() instanceof IManaStoringItem ?
					((IManaStoringItem) crystals.getStack().getItem()).getMana(crystals.getStack()) :
					crystals.getStack().getItem() instanceof ItemCrystal ? Constants.MANA_PER_CRYSTAL : Constants.MANA_PER_SHARD;

			if (crystals.getStack().getItem() == WizardryItems.crystal_shard) {manaPerItem = Constants.MANA_PER_SHARD;}
			if (crystals.getStack().getItem() == WizardryItems.grand_crystal) {manaPerItem = Constants.GRAND_CRYSTAL_MANA;}

			if (crystals.getStack().getCount() * manaPerItem < chargeDepleted) {
				// If there aren't enough crystals to fully charge the item
				iManaStoringItem.rechargeMana(centre.getStack(), crystals.getStack().getCount() * manaPerItem);
				crystals.decrStackSize(crystals.getStack().getCount());

			} else {
				// If there are excess crystals (or just enough)
				iManaStoringItem.setMana(centre.getStack(), iManaStoringItem.getManaCapacity(centre.getStack()));
				crystals.decrStackSize((int) Math.ceil(((double) chargeDepleted) / manaPerItem));
			}

			changed = true;
		}

		return changed;
	}

	public static Optional<Element> getDamageTypeElement(String damageType) {
		if (damageType.contains("_")) {
			String type = damageType.substring(0, damageType.indexOf('_'));
			switch (type) {
				case "magic": {
					return Optional.of(Element.MAGIC);

				}
				case "fire": {
					return Optional.of(Element.FIRE);

				}
				case "frost": {
					return Optional.of(Element.ICE);

				}
				case "shock": {
					return Optional.of(Element.LIGHTNING);

				}
				case "wither": {
					return Optional.of(Element.NECROMANCY);

				}
				case "poison": {
					return Optional.of(Element.EARTH);

				}
				case "force":
				case "blast": {
					return Optional.of(Element.SORCERY);

				}
				case "radiant": {
					return Optional.of(Element.HEALING);

				}

			}
		}
		return Optional.empty();
	}

}
