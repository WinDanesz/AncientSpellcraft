package com.windanesz.ancientspellcraft.item;

import com.windanesz.ancientspellcraft.registry.ASItems;
import com.windanesz.ancientspellcraft.registry.ASSounds;
import electroblob.wizardry.Wizardry;
import electroblob.wizardry.constants.Constants;
import electroblob.wizardry.item.IManaStoringItem;
import electroblob.wizardry.item.IWorkbenchItem;
import electroblob.wizardry.item.ItemArmourUpgrade;
import electroblob.wizardry.item.ItemArtefact;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.registry.WizardrySounds;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Slot;
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
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ItemCubeTransmutation extends AbstractItemArtefactWithSlots implements IManaStoringItem, IWorkbenchItem {
	private static final int MANA_COST = 2000;
	private static final int MANA_CAPACITY = 2000;
	private static final String LAST_OPEN_TIME_TAG = "last_open_time";

	public ItemCubeTransmutation() {
		super(EnumRarity.EPIC, Type.CHARM, 1, 1, true);
		setMaxDamage(MANA_CAPACITY);
		addReadinessPropertyOverride();
	}

	@Override
	public boolean isItemStackValid(ItemStack stack) {
		return !stack.isEmpty() && com.windanesz.ancientspellcraft.item.ItemTransmutationScroll.Transmutation.canBeTransmuted(stack);
	}

	@Override
	public boolean isItemValid(Item item) {
		return true;
	}


	// IManaStoringItem implementation (pattern from ItemManaArtefact)
	@Override
	public void setDamage(ItemStack stack, int damage) {
		// Prevent repair from restoring mana
	}

	@Override
	public void setMana(ItemStack stack, int mana) {
		super.setDamage(stack, getManaCapacity(stack) - mana);
	}

	@Override
	public int getMana(ItemStack stack) {
		return getManaCapacity(stack) - getDamage(stack);
	}

	@Override
	public int getManaCapacity(ItemStack stack) {
		return this.getMaxDamage(stack);
	}

	@Override
	public boolean showDurabilityBar(ItemStack p_showDurabilityBar_1_) {
		return super.showDurabilityBar(p_showDurabilityBar_1_);
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

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, World world, List<String> text, net.minecraft.client.util.ITooltipFlag advanced) {
		text.add(Wizardry.proxy.translate("item." + Wizardry.MODID + ":wand.mana", new Style().setColor(TextFormatting.BLUE), this.getMana(stack), this.getManaCapacity(stack)));
		super.addInformation(stack, world, text, advanced);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		if (player.isSneaking()) {
			if (!player.world.isRemote) {
				if (isReady(world, player.getHeldItem(hand))) {
					if (AbstractItemArtefactWithSlots.getItemForSlot(player.getHeldItem(hand), 0).isEmpty()) {
						//no item message
						player.sendStatusMessage(new TextComponentTranslation("item." + this.getRegistryName() + ".no_item"), true);
					} else {
						if (getMana(player.getHeldItem(hand)) >= MANA_COST) {
							if (tryTransmute(player.getHeldItem(hand), world, player)) {
								setLastOpenTimeCurrent(player.getHeldItem(hand), player.world.getTotalWorldTime());
							}
						} else {
							player.sendStatusMessage(new TextComponentTranslation("item." + this.getRegistryName() + ".not_enough_mana", MANA_COST), true);
						}
					}
				} else {
					player.sendStatusMessage(new TextComponentTranslation("item." + this.getRegistryName() + ".not_ready"), true);
				}

			}
			return new ActionResult<>(EnumActionResult.FAIL, player.getHeldItem(hand));
		} else {
			return super.onItemRightClick(world, player, hand);
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

	@Override
	public int getSpellSlotCount(ItemStack stack) {
		return 0;
	}

	@Override
	public boolean onApplyButtonPressed(EntityPlayer player, Slot centre, Slot crystals, Slot upgrade, Slot[] spellBooks) {
		boolean changed = false; // Used for advancements
		// Charges wand by appropriate amount
		if (crystals.getStack() != ItemStack.EMPTY && !this.isManaFull(centre.getStack())) {

			int chargeDepleted = this.getManaCapacity(centre.getStack()) - this.getMana(centre.getStack());

			int manaPerItem = Constants.MANA_PER_CRYSTAL;
			if (crystals.getStack().getItem() == WizardryItems.crystal_shard) {
				manaPerItem = Constants.MANA_PER_SHARD;
			}
			if (crystals.getStack().getItem() == WizardryItems.grand_crystal) {
				manaPerItem = Constants.GRAND_CRYSTAL_MANA;
			}

			if (crystals.getStack().getCount() * manaPerItem < chargeDepleted) {
				// If there aren't enough crystals to fully charge the wand
				this.rechargeMana(centre.getStack(), crystals.getStack().getCount() * manaPerItem);
				crystals.decrStackSize(crystals.getStack().getCount());

			} else {
				// If there are excess crystals (or just enough)
				this.setMana(centre.getStack(), this.getManaCapacity(centre.getStack()));
				crystals.decrStackSize((int) Math.ceil(((double) chargeDepleted) / manaPerItem));
			}

			changed = true;
		}

		return changed;
	}

	@Override
	public boolean showTooltip(ItemStack stack) {
		return false;
	}

	public boolean tryTransmute(ItemStack cubeStack, World world, EntityPlayer player) {
		ItemStack slotItem = AbstractItemArtefactWithSlots.getItemForSlot(cubeStack, 0);
		if (slotItem.isEmpty() && !world.isRemote) {
			player.sendStatusMessage(new TextComponentTranslation("item.ancientspellcraft:transmutation.no_items_to_transmute"), false);
			return false;
		}

		if (ItemTransmutationScroll.Transmutation.canBeTransmuted(slotItem)) {
			if (!world.isRemote) {
				boolean transmuted = false;
				ItemStack transmutedItem = ItemStack.EMPTY;

				// handle special case with its related artefact
				if (slotItem.getItem() instanceof ItemArmourUpgrade) {

					if (ItemArtefact.isArtefactActive(player, ASItems.charm_runic_hammer)) {
						if (slotItem.getItem() != WizardryItems.crystal_silver_plating) {
							transmuted = true;
							transmutedItem = new ItemStack(WizardryItems.crystal_silver_plating);
						} else {
							player.sendStatusMessage(new TextComponentTranslation("item.ancientspellcraft:transmutation_scroll.results_in_same_item"), false);
							return false;
						}
					} else if (ItemArtefact.isArtefactActive(player, ASItems.charm_fabrikator_toolkit)) {
						if (slotItem.getItem() != WizardryItems.resplendent_thread) {
							transmuted = true;
							transmutedItem = new ItemStack(WizardryItems.resplendent_thread);
						} else {
							player.sendStatusMessage(new TextComponentTranslation("item.ancientspellcraft:transmutation_scroll.results_in_same_item"), false);
							return false;
						}
					} else if (ItemArtefact.isArtefactActive(player, ASItems.charm_scissors)) {
						if (slotItem.getItem() != WizardryItems.ethereal_crystalweave) {
							transmuted = true;
							transmutedItem = new ItemStack(WizardryItems.ethereal_crystalweave);
						} else {
							player.sendStatusMessage(new TextComponentTranslation("item.ancientspellcraft:transmutation_scroll.results_in_same_item"), false);
							return false;
						}
					}
				}

				// another special case.. artefacts. We have to access the worldObj here to grab the loot tables
				if (ItemTransmutationScroll.Transmutation.getMatchingTransmutation(slotItem) == ItemTransmutationScroll.Transmutation.ARTEFACT) {
					String rarity = slotItem.getItem().getForgeRarity(slotItem).getName().toLowerCase();

					LootTable table = world.getLootTableManager().getLootTableFromLocation(new ResourceLocation(Wizardry.MODID, "subsets/" + rarity + "_artefacts"));
					LootContext context = new LootContext.Builder((WorldServer) world).withPlayer(player).withLuck(0).build();
					boolean isArtefactActive = false;
					ItemArtefact charm = null;

					// filtering out unexpected stuff...
					if (rarity.equals(EnumRarity.UNCOMMON.rarityName.toLowerCase()) || rarity.equals(EnumRarity.RARE.rarityName.toLowerCase()) || rarity.equals(EnumRarity.EPIC.rarityName.toLowerCase())) {

						List<ItemArtefact> charmSlot = ItemArtefact.getActiveArtefacts(player, ItemArtefact.Type.CHARM);

						if (!charmSlot.isEmpty()) {
							charm = charmSlot.get(0);
							if (charm == ASItems.charm_scissors || charm == ASItems.charm_fabrikator_toolkit || charm == ASItems.charm_runic_hammer) {
								isArtefactActive = true;
							}
						}

						// 60 tries should be more than enough
						for (int i = 0; i < 60; i++) {
							List<ItemStack> stacks = table.generateLootForPools(world.rand, context);

							if (!stacks.isEmpty()) {
								ItemStack artefactCandidate = stacks.get(0);
								Item item = artefactCandidate.getItem();
								if (item != slotItem.getItem()) {

									if (isArtefactActive) {
										if (charm == ASItems.charm_scissors) { // AMULET or BELT
											if (item instanceof ItemArtefact && ((ItemArtefact) item).getType() == ItemArtefact.Type.AMULET || item instanceof ItemArtefact && ((ItemArtefact) item).getType() == ItemArtefact.Type.BELT) {
												transmuted = true;
												transmutedItem = artefactCandidate;
												break;
											}
										} else if (charm == ASItems.charm_fabrikator_toolkit) { // CHARM or HEAD
											if (item instanceof ItemArtefact && ((ItemArtefact) item).getType() == ItemArtefact.Type.CHARM || item instanceof ItemArtefact && ((ItemArtefact) item).getType() == ItemArtefact.Type.HEAD || item instanceof ItemDailyArtefact) { // these are considered as charms
												transmuted = true;
												transmutedItem = artefactCandidate;
												break;
											}
										} else { //  runic hammer - RINGS
											if (item instanceof ItemArtefact && ((ItemArtefact) item).getType() == ItemArtefact.Type.RING) {
												transmuted = true;
												transmutedItem = artefactCandidate;
												break;
											}
										}
									} else {
										transmuted = true;
										transmutedItem = artefactCandidate;
										break;
									}

								}
							}
						}
					}
				}

				// all the other cases (including armour upgrade transmutation WITHOUT an artefact
				if (!transmuted) {

					Item relatedArtefact = ItemTransmutationScroll.Transmutation.getRelatedArtefact(slotItem);
					boolean applyArtefactEffect = false;

					if (relatedArtefact != Items.AIR) {

						if (relatedArtefact instanceof ItemArtefact) {
							applyArtefactEffect = ItemArtefact.isArtefactActive(player, relatedArtefact);
						} else if (relatedArtefact instanceof ItemArtefact) {
							applyArtefactEffect = ItemArtefact.isArtefactActive(player, relatedArtefact);
						}
					}

					transmutedItem = ItemTransmutationScroll.Transmutation.transmuteStack(slotItem, applyArtefactEffect);
				}

				if (transmutedItem != ItemStack.EMPTY) {

					if (transmutedItem.isItemEqual(slotItem)) {
						player.sendMessage(new TextComponentTranslation("Transmutation failed"));
						player.getCooldownTracker().setCooldown(this, 20);
						return false;
					} else {
						world.playSound(player.posX, player.posY, player.posZ, ASSounds.TRANSMUTATION, WizardrySounds.SPELLS, 1, 1, false);
						setMana(cubeStack, getMana(cubeStack) - MANA_COST);
						AbstractItemArtefactWithSlots.setItemForSlot(cubeStack, 0, transmutedItem);
					}
				}
			}
		} else {
			if (!world.isRemote)
				player.sendStatusMessage(new TextComponentTranslation("item.ancientspellcraft:transmutation.invalid_item"), false);
		}


		return true;
	}

}