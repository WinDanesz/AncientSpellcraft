package com.windanesz.ancientspellcraft.item;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.packet.ASPacketHandler;
import com.windanesz.ancientspellcraft.packet.PacketContinuousRitual;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftItems;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftSounds;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftTabs;
import electroblob.wizardry.Wizardry;
import electroblob.wizardry.block.BlockCrystal;
import electroblob.wizardry.client.DrawingUtils;
import electroblob.wizardry.constants.Element;
import electroblob.wizardry.item.ItemArmourUpgrade;
import electroblob.wizardry.item.ItemArtefact;
import electroblob.wizardry.item.ItemCrystal;
import electroblob.wizardry.item.ItemScroll;
import electroblob.wizardry.item.ItemSpellBook;
import electroblob.wizardry.item.ItemWand;
import electroblob.wizardry.item.ItemWandUpgrade;
import electroblob.wizardry.item.ItemWizardArmour;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.registry.Spells;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.registry.WizardrySounds;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.SpellProperties;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber
public class ItemTransmutationScroll extends ItemRareScroll {

	public ItemTransmutationScroll() {
		super();
	}

	@Override
	public void onUsingTick(ItemStack stack, EntityLivingBase entityLiving, int count) {
		if (!entityLiving.world.isRemote)
			return;

		World world = entityLiving.world;
		Random rand = entityLiving.world.rand;
		double posX = entityLiving.posX;
		double posY = entityLiving.posY;
		double posZ = entityLiving.posZ;

		if (world.getTotalWorldTime() % 3 == 0) {
			ParticleBuilder.create(ParticleBuilder.Type.SPARKLE, rand, posX + rand.nextDouble() * 0.5d * (rand.nextBoolean() ? 1 : -1), posY,
					posZ + rand.nextDouble() * 0.5d * (rand.nextBoolean() ? 1 : -1), 0.03, true).vel(0, 0.3, 0).clr(245, 188, 66)
					.time(20 + rand.nextInt(50)).spawn(world);

			ParticleBuilder.create(ParticleBuilder.Type.SPARKLE, rand, posX + rand.nextDouble() * 0.5d * (rand.nextBoolean() ? 1 : -1), posY,
					posZ + rand.nextDouble() * 0.5d * (rand.nextBoolean() ? 1 : -1), 0.03, true).vel(0, 0.3, 0).clr(0.3f, 0.3f,0.3f)
					.time(20 + rand.nextInt(50)).spawn(world);

			ParticleBuilder.create(ParticleBuilder.Type.SPARKLE, rand, posX + rand.nextDouble() * 0.2d * (rand.nextBoolean() ? 1 : -1), posY,
					posZ + rand.nextDouble() * 0.2d * (rand.nextBoolean() ? 1 : -1), 0.03, true).spin(0.7, 0.05).vel(0, 0.3, 0).clr(0.4f, 0.4f,0.4f)
					.time(20 + rand.nextInt(50)).spawn(world);
		}

		// horizontal particle on the floor, always visible
		ParticleBuilder.create(ParticleBuilder.Type.FLASH)
				.pos(entityLiving.posX, entityLiving.posY + 0.101, entityLiving.posZ)
				.face(EnumFacing.UP)
				.clr(DrawingUtils.mix(0xf5bc42, 0x6b6a69, 0.5f))
				.collide(false)
				.scale(2.3F)
				.time(10)
				.spawn(world);

	}

	/**
	 * Called when the player finishes using this Item (E.g. finishes eating.). Not called when the player stops using
	 * the Item before the action is complete.
	 */
	@Override
	public ItemStack onItemUseFinish(ItemStack scrollStack, World world, EntityLivingBase entityLiving) {
		return onTransmutationFinish(scrollStack, world, entityLiving);
	}

	public ItemStack onTransmutationFinish(ItemStack scrollStack, World world, EntityLivingBase entityLiving) {
		if (entityLiving instanceof EntityPlayer) {

			EntityPlayer player = (EntityPlayer) entityLiving;
			ItemStack offhandStack = player.getHeldItemOffhand();

			if (offhandStack.isEmpty() && !world.isRemote) {
				player.sendStatusMessage(new TextComponentTranslation("item.ancientspellcraft:transmutation.no_items_to_transmute"), false);
				return scrollStack;
			}

			if (Transmutation.canBeTransmuted(offhandStack)) {
				if (!world.isRemote) {
					boolean transmuted = false;
					ItemStack transmutedItem = ItemStack.EMPTY;

					// handle special case with its related artefact
					if (offhandStack.getItem() instanceof ItemArmourUpgrade) {

						if (ItemArtefact.isArtefactActive(player, AncientSpellcraftItems.charm_runic_hammer)) {
							if (offhandStack.getItem() != WizardryItems.crystal_silver_plating) {
								transmuted = true;
								transmutedItem = new ItemStack(WizardryItems.crystal_silver_plating);
							} else {
								player.sendStatusMessage(new TextComponentTranslation("item.ancientspellcraft:transmutation_scroll.results_in_same_item"), false);
								return scrollStack;
							}
						} else if (ItemArtefact.isArtefactActive(player, AncientSpellcraftItems.charm_fabrikator_toolkit)) {
							if (offhandStack.getItem() != WizardryItems.resplendent_thread) {
								transmuted = true;
								transmutedItem = new ItemStack(WizardryItems.resplendent_thread);
							} else {
								player.sendStatusMessage(new TextComponentTranslation("item.ancientspellcraft:transmutation_scroll.results_in_same_item"), false);
								return scrollStack;
							}
						} else if (ItemArtefact.isArtefactActive(player, AncientSpellcraftItems.charm_scissors)) {
							if (offhandStack.getItem() != WizardryItems.ethereal_crystalweave) {
								transmuted = true;
								transmutedItem = new ItemStack(WizardryItems.ethereal_crystalweave);
							} else {
								player.sendStatusMessage(new TextComponentTranslation("item.ancientspellcraft:transmutation_scroll.results_in_same_item"), false);
								return scrollStack;
							}
						}
					}

					// another special case.. artefacts. We have to access the worldObj here to grab the loot tables
					if (Transmutation.getMatchingTransmutation(offhandStack) == Transmutation.ARTEFACT) {
						String rarity = offhandStack.getItem().getForgeRarity(offhandStack).getName().toLowerCase();

						LootTable table = world.getLootTableManager().getLootTableFromLocation(new ResourceLocation(Wizardry.MODID, "subsets/" + rarity + "_artefacts"));
						LootContext context = new LootContext.Builder((WorldServer) world).withPlayer(player).withLuck(0).build();
						boolean isArtefactActive = false;
						ItemArtefact charm = null;

						// filtering out unexpected stuff...
						if (rarity.equals(EnumRarity.UNCOMMON.rarityName.toLowerCase()) || rarity.equals(EnumRarity.RARE.rarityName.toLowerCase())
								|| rarity.equals(EnumRarity.EPIC.rarityName.toLowerCase())) {

							List<ItemArtefact> charmSlot = ItemArtefact.getActiveArtefacts(player, ItemArtefact.Type.CHARM);

							if (!charmSlot.isEmpty()) {
								charm = charmSlot.get(0);
								if (charm == AncientSpellcraftItems.charm_scissors || charm == AncientSpellcraftItems.charm_fabrikator_toolkit || charm == AncientSpellcraftItems.charm_runic_hammer) {
									isArtefactActive = true;
								}
							}

							// 60 tries should be more than enough
							for (int i = 0; i < 60; i++) {
								List<ItemStack> stacks = table.generateLootForPools(world.rand, context);

								if (!stacks.isEmpty()) {
									ItemStack artefactCandidate = stacks.get(0);
									Item item = artefactCandidate.getItem();
									if (item != offhandStack.getItem()) {

										if (isArtefactActive) {
											if (charm == AncientSpellcraftItems.charm_scissors) { // AMULET or BELT
												if (item instanceof ItemArtefact && ((ItemArtefact) item).getType() == ItemArtefact.Type.AMULET
														|| item instanceof ItemNewArtefact && ((ItemNewArtefact) item).getType() == ItemNewArtefact.AdditionalType.BELT) {
													transmuted = true;
													transmutedItem = artefactCandidate;
													break;
												}
											} else if (charm == AncientSpellcraftItems.charm_fabrikator_toolkit) { // CHARM or HEAD
												if (item instanceof ItemArtefact && ((ItemArtefact) item).getType() == ItemArtefact.Type.CHARM
														|| item instanceof ItemNewArtefact && ((ItemNewArtefact) item).getType() == ItemNewArtefact.AdditionalType.HEAD
														|| item instanceof ItemDailyArtefact) { // these are considered as charms
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

						Item relatedArtefact = Transmutation.getRelatedArtefact(offhandStack);
						boolean applyArtefactEffect = false;

						if (relatedArtefact != Items.AIR) {

							if (relatedArtefact instanceof ItemArtefact) {
								applyArtefactEffect = ItemArtefact.isArtefactActive(player, relatedArtefact);
							} else if (relatedArtefact instanceof ItemNewArtefact) {
								applyArtefactEffect = ItemNewArtefact.isNewArtefactActive(player, relatedArtefact);
							}
						}

						transmutedItem = Transmutation.transmuteStack(offhandStack, applyArtefactEffect);
					}

					if (transmutedItem != ItemStack.EMPTY) {

						if (transmutedItem.isItemEqual(offhandStack)) {
							player.sendMessage(new TextComponentTranslation("Transmutation failed"));
							player.getCooldownTracker().setCooldown(this, 20);
							return scrollStack;
						}

						if (!player.isCreative()) {
							scrollStack.shrink(1);
						}
						offhandStack.shrink(1);

						if (player.getHeldItemOffhand().isEmpty()) {
							player.setHeldItem(EnumHand.OFF_HAND, transmutedItem);
						} else if (!player.addItemStackToInventory(transmutedItem)) {
							player.dropItem(transmutedItem, true);
						}
					}
				}
			} else {
				if (!world.isRemote)
					player.sendStatusMessage(new TextComponentTranslation("item.ancientspellcraft:transmutation.invalid_item"), false);
			}

			player.getCooldownTracker().setCooldown(this, 60);
		}

		world.playSound(entityLiving.posX, entityLiving.posY, entityLiving.posZ, AncientSpellcraftSounds.TRANSMUTATION, WizardrySounds.SPELLS, 1, 1, false);

		return scrollStack;
	}

	@Override
	public EnumAction getItemUseAction(ItemStack stack) {return SpellActions.IMBUE;}

	/**
	 * Called when the equipped item is right clicked.
	 */
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		ItemStack itemstack = playerIn.getHeldItem(handIn);

		playerIn.setActiveHand(handIn);
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemstack);
	}

	@Override
	public void onPlayerStoppedUsing(ItemStack scrollStack, World world, EntityLivingBase entityLiving, int timeLeft) {
		// this is not getting called
		if (entityLiving instanceof EntityPlayer && !world.isRemote) {
			((EntityPlayer) entityLiving).sendStatusMessage(new TextComponentTranslation("item." + this.getRegistryName() + ".interrupted"), true);
			((EntityPlayer) entityLiving).getCooldownTracker().setCooldown(this, 40);
		}

	}

	@Override
	public int getMaxItemUseDuration(ItemStack stack) {return 60;}

	public enum Transmutation {
		SPELLBOOK() {
			protected ItemStack transmute(ItemStack bookStack, boolean applyArtefactEffects) {
				if (!matches(bookStack))
					return bookStack;

				if (bookStack.getItem() == AncientSpellcraftItems.ancient_spell_book) {
					return bookStack;
				}

				Element element = applyArtefactEffects ? Spell.byMetadata(bookStack.getItemDamage()).getElement() : null;
				Item book = bookStack.getItem();
				List<Item> bookTypeList = ForgeRegistries.ITEMS.getValuesCollection().stream().filter(i -> i instanceof ItemSpellBook).collect(Collectors.toList());
				Spell newSpell = Transmutation.getRandomSpell(bookStack, element, SpellProperties.Context.BOOK);

				for (int i = 0; i < bookTypeList.size(); i++) {
					Item currentBook = bookTypeList.get(i);
					if (newSpell.applicableForItem(currentBook)) {
						book = currentBook;
					}
				}

				return new ItemStack(book, 1, newSpell.metadata());
			}

			@Override
			protected Item getRelatedArtefact(Item item) {
				return AncientSpellcraftItems.amulet_arcane_catalyst;
			}

			boolean matches(ItemStack stack) {
				return stack.getItem() instanceof ItemSpellBook;
			}
		},

		SCROLL() {
			protected ItemStack transmute(ItemStack scrollStack, boolean applyArtefactEffects) {
				if (!matches(scrollStack))
					return scrollStack;

				Element element = applyArtefactEffects ? Spell.byMetadata(scrollStack.getItemDamage()).getElement() : null;

				Spell oldSpell = Spell.byMetadata(scrollStack.getMetadata());

				if (oldSpell.getRegistryName().getNamespace().equals(AncientSpellcraft.MODID) && oldSpell.getElement() == Element.MAGIC) {
					return scrollStack;
				}

				return new ItemStack(scrollStack.getItem(), 1, Transmutation.getRandomSpell(scrollStack, element, SpellProperties.Context.SCROLL).metadata());
			}

			@Override
			protected Item getRelatedArtefact(Item item) {
				return AncientSpellcraftItems.amulet_arcane_catalyst;
			}

			boolean matches(ItemStack stack) {
				return stack.getItem() instanceof ItemScroll;
			}
		},

		WAND {
			protected ItemStack transmute(ItemStack oldItem, boolean applyArtefactEffects) {
				if (!matches(oldItem))
					return oldItem;

				Item newWand = ItemWand.getWand(((ItemWand) oldItem.getItem()).tier, Transmutation.getRandomOtherElement(((ItemWand) oldItem.getItem()).element));
				return new ItemStack(newWand);
			}

			@Override
			protected Item getRelatedArtefact(Item item) {
				return Items.AIR;
			}

			boolean matches(ItemStack stack) {
				return stack.getItem() instanceof ItemWand;
			}
		},

		ARTEFACT {
			protected ItemStack transmute(ItemStack oldItem, boolean applyArtefactEffects) {
				if (!matches(oldItem))
					return oldItem;

				return ItemStack.EMPTY;
			}

			@Override
			protected Item getRelatedArtefact(Item item) {
				/* handled elsewhere
				if (item instanceof ItemArtefact) {
					switch (((ItemArtefact) item).getType()) {
						case CHARM:
							return AncientSpellcraftItems.charm_fabrikator_toolkit;
						case RING:
							return AncientSpellcraftItems.charm_runic_hammer;
						case AMULET:
							return AncientSpellcraftItems.charm_scissor;
						default:
							return Items.AIR;
					}
				}
				if (item instanceof ItemNewArtefact) {
					switch (((ItemNewArtefact) item).getType()) {
						case HEAD:
							return AncientSpellcraftItems.charm_fabrikator_toolkit;
						case BELT:
							return AncientSpellcraftItems.charm_scissor;
						default:
							return Items.AIR;
					}
				}
				if (item instanceof ItemDailyArtefact) {
					return AncientSpellcraftItems.charm_fabrikator_toolkit;
				}
				*/
				return Items.AIR;
			}

			boolean matches(ItemStack stack) {
				return stack.getItem() instanceof ItemArtefact || stack.getItem() instanceof ItemNewArtefact || stack.getItem() instanceof ItemDailyArtefact;
			}
		},

		ARMOUR {
			protected ItemStack transmute(ItemStack oldItem, boolean applyArtefactEffects) {
				if (!matches(oldItem))
					return oldItem;
				ItemWizardArmour oldArmour = (ItemWizardArmour) oldItem.getItem();

				return new ItemStack(ItemWizardArmour.getArmour(Transmutation.getRandomOtherElement(oldArmour.element), oldArmour.armourClass, oldArmour.armorType));
			}

			@Override
			protected Item getRelatedArtefact(Item item) {
				return Items.AIR;
			}

			boolean matches(ItemStack stack) {
				return stack.getItem() instanceof ItemWizardArmour;
			}
		},

		ARMOR_UPGRADE_ITEM {
			protected ItemStack transmute(ItemStack oldItem, boolean applyArtefactEffects) {
				if (!matches(oldItem))
					return oldItem;

				if (!applyArtefactEffects) {
					List<Item> upgrades = new ArrayList<>(Arrays.asList(WizardryItems.crystal_silver_plating, WizardryItems.resplendent_thread, WizardryItems.ethereal_crystalweave));
					upgrades.remove(oldItem.getItem());
					return new ItemStack(upgrades.get(itemRand.nextInt(upgrades.size())));
				}

				return ItemStack.EMPTY;
			}

			@Override
			protected Item getRelatedArtefact(Item item) {
				// These are a bit special in that sense that there are 3 artefacts which can affect the armour upgrades and the output depends on the artefacts
				return Items.AIR;
			}

			boolean matches(ItemStack stack) {
				return stack.getItem() instanceof ItemArmourUpgrade; // TODO
			}
		},

		WAND_UPGRADE {
			protected ItemStack transmute(ItemStack oldItem, boolean applyArtefactEffects) {
				if (!matches(oldItem))
					return oldItem;

				List<Item> upgrades = new ArrayList<>(Arrays.asList((WizardryItems.attunement_upgrade), (WizardryItems.blast_upgrade), (WizardryItems.condenser_upgrade),
						(WizardryItems.cooldown_upgrade), (WizardryItems.duration_upgrade), (WizardryItems.melee_upgrade), (WizardryItems.range_upgrade),
						(WizardryItems.siphon_upgrade), (WizardryItems.storage_upgrade)));

				if (upgrades.remove(oldItem.getItem())) {

					return new ItemStack(upgrades.get(itemRand.nextInt(upgrades.size())));
				}

				return oldItem;
			}

			@Override
			protected Item getRelatedArtefact(Item item) {
				return Items.AIR;
			}

			@Override
			boolean matches(ItemStack stack) {
				return stack.getItem() instanceof ItemWandUpgrade;
			}
		},

		SPECTRAL_DUST {
			protected ItemStack transmute(ItemStack oldItem, boolean applyArtefactEffects) {
				if (!matches(oldItem))
					return oldItem;

				return Transmutation.transmuteStandardItemWithElementMetadata(oldItem);
			}

			@Override
			protected Item getRelatedArtefact(Item item) {
				return Items.AIR;
			}

			@Override
			boolean matches(ItemStack stack) {
				return false;
			}

		},

		CRYSTAL {
			protected ItemStack transmute(ItemStack oldItem, boolean applyArtefactEffects) {
				if (!matches(oldItem))
					return oldItem;

				return Transmutation.transmuteStandardItemWithElementMetadata(oldItem);
			}

			@Override
			protected Item getRelatedArtefact(Item item) {
				return Items.AIR;
			}

			boolean matches(ItemStack stack) {
				return stack.getItem() instanceof ItemCrystal;
			}
		},

		CRYSTAL_BLOCK {
			protected ItemStack transmute(ItemStack oldItem, boolean applyArtefactEffects) {
				if (!matches(oldItem))
					return oldItem;

				return Transmutation.transmuteStandardItemWithElementMetadata(oldItem);
			}

			@Override
			protected Item getRelatedArtefact(Item item) {
				return Items.AIR;
			}

			boolean matches(ItemStack stack) {
				return Block.getBlockFromItem(stack.getItem()) instanceof BlockCrystal;
			}
		};

		////////////////// public methods /////////////////////

		public static ItemStack transmuteStack(ItemStack stack, boolean applyArtefactEffects) {
			for (Transmutation t : Transmutation.values()) {
				if (t.matches(stack)) {
					return t.transmute(stack, applyArtefactEffects);
				}
			}

			return stack;
		}

		public static boolean canBeTransmuted(ItemStack stack) {
			for (Transmutation t : Transmutation.values()) {
				if (t.matches(stack)) {
					return true;
				}
			}

			return false;
		}

		public static Item getRelatedArtefact(ItemStack stack) {
			if (canBeTransmuted(stack)) {
				//noinspection ConstantConditions
				return getMatchingTransmutation(stack).getRelatedArtefact(stack.getItem());
			}
			return Items.AIR;
		}

		////////////////// public methods /////////////////////

		////////////////// enum methods /////////////////////

		abstract boolean matches(ItemStack stack);

		protected abstract ItemStack transmute(ItemStack stack, boolean applyArtefactEffects);

		abstract protected Item getRelatedArtefact(Item item);

		////////////////// enum methods /////////////////////

		////////////////// private methods /////////////////////

		private static Spell getRandomSpell(ItemStack oldStack, Element element, SpellProperties.Context context) {
			if (!(oldStack.getItem() instanceof ItemSpellBook) && !(oldStack.getItem() instanceof ItemScroll)) {
				return Spells.none;
			}

			Spell oldSpell = Spell.byMetadata(oldStack.getItemDamage());
			List<Spell> spells = Spell.getSpells(new Spell.TierElementFilter(oldSpell.getTier(), element, SpellProperties.Context.BOOK));
			spells.removeIf((new Spell.TierElementFilter(oldSpell.getTier(), element, SpellProperties.Context.LOOTING)).negate());
			//			spells.removeIf(s -> !s.applicableForItem(oldStack.getItem()));

			Spell newSpell = oldSpell;
			int remainingTries = 30;
			if (spells.size() != 0 && !spells.isEmpty()) {
				while (newSpell == oldSpell || remainingTries == 0) {
					newSpell = spells.get(itemRand.nextInt(spells.size()));
					remainingTries--;
				}
			}
			return newSpell;
		}

		private static ItemStack transmuteStandardItemWithElementMetadata(ItemStack oldStack) {
			Element newElement = Transmutation.getRandomOtherElementFromMeta(oldStack);
			return new ItemStack(oldStack.getItem(), 1, newElement.ordinal());
		}

		private static Element getRandomOtherElementFromMeta(ItemStack oldStack) {
			if (Element.values().length >= oldStack.getMetadata()) {
				Element oldElement = Element.values()[oldStack.getMetadata()];
				return getRandomOtherElement(oldElement);
			}

			return Element.MAGIC;
		}

		private static Element getRandomOtherElement(Element excluded) {
			List<Element> elements = new ArrayList<>(Arrays.asList(Element.values()));

			if (excluded != Element.MAGIC) {
				elements.remove(Element.MAGIC);
			}

			elements.remove(excluded);

			return elements.get(itemRand.nextInt(elements.size()));
		}

		public static Transmutation getMatchingTransmutation(ItemStack stack) {
			for (Transmutation t : Transmutation.values()) {
				if (t.matches(stack)) {
					return t;
				}
			}

			return null;
		}

		////////////////// private methods /////////////////////

	}

	@SubscribeEvent
	public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
		if (event.getEntity() instanceof EntityPlayer && event.getHand() == EnumHand.OFF_HAND
				&& ((EntityPlayer) event.getEntity()).getHeldItemMainhand().getItem() == AncientSpellcraftItems.transmutation_scroll) {
			event.setCanceled(true);
		}

	}
}
