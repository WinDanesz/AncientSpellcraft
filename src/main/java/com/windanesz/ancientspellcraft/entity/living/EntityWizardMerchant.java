package com.windanesz.ancientspellcraft.entity.living;

import com.windanesz.ancientspellcraft.registry.AncientSpellcraftItems;
import electroblob.wizardry.Wizardry;
import electroblob.wizardry.block.BlockReceptacle;
import electroblob.wizardry.client.ClientProxy;
import electroblob.wizardry.constants.Element;
import electroblob.wizardry.constants.Tier;
import electroblob.wizardry.entity.living.EntityWizard;
import electroblob.wizardry.event.SpellCastEvent;
import electroblob.wizardry.item.ItemSpellBook;
import electroblob.wizardry.misc.WildcardTradeList;
import electroblob.wizardry.packet.PacketNPCCastSpell;
import electroblob.wizardry.packet.WizardryPacketHandler;
import electroblob.wizardry.registry.Spells;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.registry.WizardrySounds;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.InventoryUtils;
import electroblob.wizardry.util.NBTExtras;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.SpellModifiers;
import electroblob.wizardry.util.SpellProperties;
import electroblob.wizardry.util.WandHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EntityWizardMerchant extends EntityWizard {

	int lifetime = 24000; // a full MC day

	/**
	 * The wizard's trades.
	 */
	private MerchantRecipeList trades;

	public EntityWizardMerchant(World world) {
		super(world);
	}

	@Override
	public ITextComponent getDisplayName() {
		ITextComponent tex = super.getDisplayName();
		if (!hasCustomName()) { return (new TextComponentTranslation("entity.ancientspellcraft:travelling.prefix")).appendText(" ").appendSibling(tex); }
		return super.getDisplayName();
	}

	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();

		if (lifetime >= 0) {

			if (ticksExisted == 1) {
				this.onSpawn(false);
			}

			if (ticksExisted == 40) {
				EntityPlayer closestPlayer = world.getClosestPlayerToEntity(this, 120);
				if (closestPlayer != null) {
					this.getNavigator().clearPath();
					this.getNavigator().tryMoveToEntityLiving(closestPlayer, 0.7f);
					this.faceEntity(closestPlayer, 30F, 30F);
				}
			}

			lifetime--;

			// these cowards will teleport away if you try to beat them
			if (lifetime <= 0) {
				this.onDespawn(false);
			} else if (isNearCriticalHealth()) {
				this.onDespawn(true);
			}
		}
	}

	@Override
	protected void updateAITasks() {
		// noop!
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {

		super.writeEntityToNBT(nbt);

		if (this.trades != null) {
			NBTExtras.storeTagSafely(nbt, "trades", this.trades.getRecipiesAsTags());
		}

		nbt.setInteger("lifetime", this.lifetime);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);

		if (nbt.hasKey("trades")) {
			NBTTagCompound nbttagcompound1 = nbt.getCompoundTag("trades");
			this.trades = new WildcardTradeList(nbttagcompound1);
		}

		Element element = this.getElement();
		nbt.setInteger("element", element == null ? 0 : element.ordinal());
		nbt.setInteger("skin", this.textureIndex);

		if (nbt.hasKey("lifetime")) {
			this.lifetime = nbt.getInteger("lifetime");
		}
	}

	/**
	 * Had to copy this to alter as it was more feasible than using reflection for all the private fields and methods
	 *
	 * @Author Electroblob, WinDanesz
	 */
	// This is called from the gui in order to display the recipes (no surprise there), and this is actually where
	// the initialisation is done, i.e. the trades don't actually exist until some player goes to trade with the
	// villager, at which point the first is added.
	@Override
	public MerchantRecipeList getRecipes(EntityPlayer par1EntityPlayer) {

		if (this.trades == null) {

			this.trades = new WildcardTradeList();
			// buy

			// travellers are only interested in one specific book type, but this supports addon books as well!
			ItemStack crystalStack = new ItemStack(WizardryItems.magic_crystal, 5);

			List<Item> bookTypeList = new ArrayList<>(ForgeRegistries.ITEMS.getValuesCollection().stream().filter(i -> i instanceof ItemSpellBook).distinct().collect(Collectors.toList()));
			if (!bookTypeList.isEmpty()) {
				MerchantRecipe bookRecipe = new MerchantRecipe(new ItemStack(bookTypeList.get(rand.nextInt(bookTypeList.size())), 1, OreDictionary.WILDCARD_VALUE), crystalStack);
				this.trades.add(bookRecipe);
			}

			this.addRandomRecipes(Math.max(7, rand.nextInt(10)));

			// can sell a stone tablet occassionally
			if (rand.nextBoolean()) {
				List<Item> tablets = new ArrayList<>(Arrays.asList(AncientSpellcraftItems.stone_tablet_small, AncientSpellcraftItems.stone_tablet,
						AncientSpellcraftItems.stone_tablet_large, AncientSpellcraftItems.stone_tablet_grand));
				Item tablet = tablets.get(rand.nextInt(tablets.size()));

				if (tablet == AncientSpellcraftItems.stone_tablet_small) {
					ItemStack secondItemToBuy = new ItemStack(WizardryItems.magic_crystal, Tier.NOVICE.ordinal() * 3 + 1 + rand.nextInt(4));
					this.trades.add(new MerchantRecipe(this.getRandomPrice(Tier.NOVICE), secondItemToBuy, new ItemStack(tablet), 0, 1));
				} else if (tablet == AncientSpellcraftItems.stone_tablet) {
					ItemStack secondItemToBuy = new ItemStack(WizardryItems.magic_crystal, Tier.APPRENTICE.ordinal() * 3 + 1 + rand.nextInt(4));
					this.trades.add(new MerchantRecipe(this.getRandomPrice(Tier.APPRENTICE), secondItemToBuy, new ItemStack(tablet), 0, 1));
				} else {
					ItemStack secondItemToBuy = new ItemStack(WizardryItems.magic_crystal, Tier.ADVANCED.ordinal() * 3 + 1 + rand.nextInt(8));
					this.trades.add(new MerchantRecipe(this.getRandomPrice(Tier.ADVANCED), secondItemToBuy, new ItemStack(tablet), 0, 1));

				}
			}

			String rarity = "uncommon";
			for (int j = 0; j < 2; j++) {
				LootTable table = world.getLootTableManager().getLootTableFromLocation(new ResourceLocation(Wizardry.MODID, "subsets/" + rarity + "_artefacts"));
				LootContext context = new LootContext.Builder((WorldServer) world).withLuck(0).build();
				List<ItemStack> stacks = table.generateLootForPools(world.rand, context);
				if (!stacks.isEmpty()) {
					ItemStack secondItemToBuy = rarity.equals("uncommon") ? new ItemStack(WizardryItems.astral_diamond) : rarity.equals("rare")
							? new ItemStack(WizardryItems.astral_diamond, 2) : new ItemStack(WizardryItems.astral_diamond, 3);

					this.trades.add(new MerchantRecipe(getRandomPrice(Tier.MASTER), secondItemToBuy, stacks.get(0), 0, 1));

				}
				rarity = rand.nextInt(5) == 0 ? "epic" : "rare";
				if (rand.nextBoolean()) {
					j++;
				}
			}

		}

		return this.trades;
	}

	/**
	 * Had to copy this to alter as it was more feasible than using reflection for all the private fields and methods
	 *
	 * @Author Electroblob, WinDanesz
	 */
	private void addRandomRecipes(int numberOfItemsToAdd) {

		MerchantRecipeList merchantrecipelist;
		merchantrecipelist = new MerchantRecipeList();

		for (int i = 0; i < numberOfItemsToAdd; i++) {

			ItemStack itemToSell = ItemStack.EMPTY;

			boolean itemAlreadySold = true;

			Tier tier = Tier.NOVICE;

			while (itemAlreadySold) {

				itemAlreadySold = false;

				/* New way of getting random item, by giving a chance to increase the tier which depends on how much the
				 * player has already traded with the wizard. The more the player has traded with the wizard, the more
				 * likely they are to get items of a higher tier. The -4 is to ignore the original 4 trades. For
				 * reference, the chances are as follows: Trades done Basic Apprentice Advanced Master 0 50% 25% 18% 8%
				 * 1 46% 25% 20% 9% 2 42% 24% 22% 12% 3 38% 24% 24% 14% 4 34% 22% 26% 17% 5 30% 21% 28% 21% 6 26% 19%
				 * 30% 24% 7 22% 17% 32% 28% 8 18% 15% 34% 33% */

				double tierIncreaseChance = 0.5 + 0.04 * (Math.max(this.trades.size(), 0));

				tier = Tier.APPRENTICE;

				if (rand.nextDouble() < tierIncreaseChance) {
					tier = Tier.ADVANCED;
					if (rand.nextDouble() < tierIncreaseChance * 0.6) {
						tier = Tier.MASTER;
					}
				}

				itemToSell = this.getRandomItemOfTier(tier);

				for (Object recipe : merchantrecipelist) {
					if (ItemStack.areItemStacksEqual(((MerchantRecipe) recipe).getItemToSell(), itemToSell)) { itemAlreadySold = true; }
				}

				if (this.trades != null) {
					for (Object recipe : this.trades) {
						if (ItemStack.areItemStacksEqual(((MerchantRecipe) recipe).getItemToSell(), itemToSell)) { itemAlreadySold = true; }
					}
				}

			}

			// Don't know how it can ever be empty here, but it's a failsafe.
			if (itemToSell.isEmpty()) { return; }

			ItemStack secondItemToBuy = tier == Tier.MASTER ? new ItemStack(WizardryItems.astral_diamond)
					: new ItemStack(WizardryItems.magic_crystal, tier.ordinal() * 3 + 1 + rand.nextInt(4));

			merchantrecipelist.add(new MerchantRecipe(this.getRandomPrice(tier), secondItemToBuy, itemToSell, 0, 1));
		}

		Collections.shuffle(merchantrecipelist);

		if (this.trades == null) {
			this.trades = new WildcardTradeList();
		}

		this.trades.addAll(merchantrecipelist);
	}

	/**
	 * Had to copy this to alter as it was more feasible than using reflection for all the private fields and methods
	 *
	 * @Author Electroblob, WinDanesz
	 */
	@SuppressWarnings("unchecked")
	private ItemStack getRandomPrice(Tier tier) {

		Map<Pair<ResourceLocation, Short>, Integer> map = Wizardry.settings.currencyItems;
		// This isn't that efficient but it's not called very often really so it doesn't matter
		Pair<ResourceLocation, Short> itemName = map.keySet().toArray(new Pair[0])[rand.nextInt(map.size())];
		Item item = Item.REGISTRY.getObject(itemName.getLeft());
		short meta = itemName.getRight();
		int value;

		if (item == null) {
			Wizardry.logger.warn("Invalid item in currency items: {}", itemName);
			item = Items.EMERALD; // Fallback item
			value = 6;
		} else {
			value = map.get(itemName);
		}

		// ((tier.ordinal() + 1) * 16 + rand.nextInt(6)) gives a 'value' for the item being bought
		// This is then divided by the value of the currency item to give a price
		// The absolute maximum stack size that can result from this calculation (with value = 1) is 64.
		return new ItemStack(item, MathHelper.clamp((8 + tier.ordinal() * 16 + rand.nextInt(9)) / value, 1, 64), meta);
	}

	/**
	 * Had to copy this to alter as it was more feasible than using reflection for all the private fields and methods
	 *
	 * @Author Electroblob, WinDanesz
	 */
	private ItemStack getRandomItemOfTier(Tier tier) {

		int randomiser;

		// All enabled spells of the given tier
		List<Spell> spells = Spell.getSpells(new Spell.TierElementFilter(tier, null, SpellProperties.Context.TRADES));
		// All enabled spells of the given tier that match this wizard's element
		List<Spell> specialismSpells = Spell.getSpells(new Spell.TierElementFilter(tier, this.getElement(), SpellProperties.Context.TRADES));

		// Wizards don't sell scrolls
		spells.removeIf(s -> !s.isEnabled(SpellProperties.Context.BOOK));
		specialismSpells.removeIf(s -> !s.isEnabled(SpellProperties.Context.BOOK));

		// This code is sooooooo much neater with the new filter system!
		switch (tier) {

			case NOVICE:
				randomiser = rand.nextInt(5);
				if (randomiser < 4 && !spells.isEmpty()) {
					if (this.getElement() != Element.MAGIC && rand.nextInt(4) > 0 && !specialismSpells.isEmpty()) {
						// This means it is more likely for spell books sold to be of the same element as the wizard if the
						// wizard has an element.
						return new ItemStack(WizardryItems.spell_book, 1,
								specialismSpells.get(rand.nextInt(specialismSpells.size())).metadata());
					} else {
						return new ItemStack(WizardryItems.spell_book, 1, spells.get(rand.nextInt(spells.size())).metadata());
					}
				} else {
					if (this.getElement() != Element.MAGIC && rand.nextInt(4) > 0) {
						// This means it is more likely for wands sold to be of the same element as the wizard if the wizard
						// has an element.
						return new ItemStack(WizardryItems.getWand(tier, this.getElement()));
					} else {
						return new ItemStack(
								WizardryItems.getWand(tier, Element.values()[rand.nextInt(Element.values().length)]));
					}
				}

			case APPRENTICE:
				randomiser = rand.nextInt(Wizardry.settings.discoveryMode ? 12 : 10);
				if (randomiser < 5 && !spells.isEmpty()) {
					if (this.getElement() != Element.MAGIC && rand.nextInt(4) > 0 && !specialismSpells.isEmpty()) {
						// This means it is more likely for spell books sold to be of the same element as the wizard if the
						// wizard has an element.
						return new ItemStack(WizardryItems.spell_book, 1,
								specialismSpells.get(rand.nextInt(specialismSpells.size())).metadata());
					} else {
						return new ItemStack(WizardryItems.spell_book, 1, spells.get(rand.nextInt(spells.size())).metadata());
					}
				} else if (randomiser < 6) {
					if (this.getElement() != Element.MAGIC && rand.nextInt(4) > 0) {
						// This means it is more likely for wands sold to be of the same element as the wizard if the wizard
						// has an element.
						return new ItemStack(WizardryItems.getWand(tier, this.getElement()));
					} else {
						return new ItemStack(
								WizardryItems.getWand(tier, Element.values()[rand.nextInt(Element.values().length)]));
					}
				} else if (randomiser < 8) {
					return new ItemStack(WizardryItems.arcane_tome, 1, 1);
				} else if (randomiser < 10) {
					EntityEquipmentSlot slot = InventoryUtils.ARMOUR_SLOTS[rand.nextInt(InventoryUtils.ARMOUR_SLOTS.length)];
					if (this.getElement() != Element.MAGIC && rand.nextInt(4) > 0) {
						// This means it is more likely for armour sold to be of the same element as the wizard if the
						// wizard has an element.
						return new ItemStack(WizardryItems.getArmour(this.getElement(), slot));
					} else {
						return new ItemStack(
								WizardryItems.getArmour(Element.values()[rand.nextInt(Element.values().length)], slot));
					}
				} else {
					// Don't need to check for discovery mode here since it is done above
					return new ItemStack(WizardryItems.identification_scroll);
				}

			case ADVANCED:
				randomiser = rand.nextInt(18);
				if (randomiser < 5 && !spells.isEmpty()) {
					if (this.getElement() != Element.MAGIC && rand.nextInt(4) > 0 && !specialismSpells.isEmpty()) {
						// This means it is more likely for spell books sold to be of the same element as the wizard if the
						// wizard has an element.
						return new ItemStack(WizardryItems.spell_book, 1,
								specialismSpells.get(rand.nextInt(specialismSpells.size())).metadata());
					} else {
						return new ItemStack(WizardryItems.spell_book, 1, spells.get(rand.nextInt(spells.size())).metadata());
					}
				} else if (randomiser < 6) {
					if (this.getElement() != Element.MAGIC && rand.nextInt(4) > 0) {
						// This means it is more likely for wands sold to be of the same element as the wizard if the wizard
						// has an element.
						return new ItemStack(WizardryItems.getWand(tier, this.getElement()));
					} else {
						return new ItemStack(
								WizardryItems.getWand(tier, Element.values()[rand.nextInt(Element.values().length)]));
					}
				} else if (randomiser < 8) {
					return new ItemStack(WizardryItems.arcane_tome, 1, 2);

				} else if (randomiser < 10) {
					return new ItemStack(AncientSpellcraftItems.transmutation_scroll, 1);
				} else if (randomiser < 12) {
					return new ItemStack(WizardryItems.grand_crystal, 1);
				} else if (randomiser < 14) {
					List<Item> upgrades = new ArrayList<>(Arrays.asList(WizardryItems.resplendent_thread, WizardryItems.crystal_silver_plating, WizardryItems.ethereal_crystalweave));
					return new ItemStack(upgrades.get(rand.nextInt(upgrades.size())), 1);
				} else {
					List<Item> upgrades = new ArrayList<Item>(WandHelper.getSpecialUpgrades());
					randomiser = rand.nextInt(upgrades.size());
					return new ItemStack(upgrades.get(randomiser));
				}

			case MASTER:
				// If a regular wizard rolls a master trade, it can only be a simple master wand or a tome of arcana
				randomiser = this.getElement() != Element.MAGIC ? rand.nextInt(8) : 5 + rand.nextInt(3);

				if (randomiser < 5 && this.getElement() != Element.MAGIC && !specialismSpells.isEmpty()) {
					// Master spells can only be sold by a specialist in that element.
					return new ItemStack(WizardryItems.spell_book, 1,
							specialismSpells.get(rand.nextInt(specialismSpells.size())).metadata());

				} else if (randomiser < 6) {
					if (this.getElement() != Element.MAGIC && rand.nextInt(4) > 0) {
						// Master elemental wands can only be sold by a specialist in that element.
						return new ItemStack(WizardryItems.getWand(tier, this.getElement()));
					} else {
						return new ItemStack(WizardryItems.master_wand);
					}
				} else {
					return new ItemStack(WizardryItems.arcane_tome, 1, 3);
				}
		}

		return new ItemStack(Blocks.STONE);
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float damage) {

		if (isNearCriticalHealth(damage)) {
			this.onDespawn(true);
			return false;
		}

		return super.attackEntityFrom(source, damage);
	}

	private boolean isNearCriticalHealth(float damage) {
		return (this.getMaxHealth() - damage) <= this.getMaxHealth() * 0.3f;
	}

	private boolean isNearCriticalHealth() {
		return this.getHealth() <= this.getMaxHealth() * 0.3f;
	}

	private void onSpawn(boolean castBlind) {
		if (world.isRemote) {
			spawnParticles();
		} else if (castBlind) {
			castBlind();
		}
	}

	private void onDespawn(boolean castBlind) {
		if (world.isRemote) {
			spawnParticles();
		} else if (castBlind) {
			castBlind();
		}
		this.setDead();
	}

	private void castBlind() {
		Spells.blinding_flash.cast(this.world, posX, posY, posZ, EnumFacing.UP, 0, 0, new SpellModifiers());

		// If anything stops the spell working at this point, nothing else happens.
		if (MinecraftForge.EVENT_BUS.post(new SpellCastEvent.Pre(SpellCastEvent.Source.NPC, Spells.blinding_flash, this, new SpellModifiers()))) {
			return;
		}

		// This is only called when spell casting starts so ticksInUse is always zero
		if (Spells.blinding_flash.cast(world, this, EnumHand.MAIN_HAND, 0, this, new SpellModifiers())) {

			MinecraftForge.EVENT_BUS.post(new SpellCastEvent.Post(SpellCastEvent.Source.NPC, Spells.blinding_flash, this, new SpellModifiers()));

			if (Spells.blinding_flash.requiresPacket()) {
				// Sends a packet to all players in dimension to tell them to spawn particles.
				IMessage msg = new PacketNPCCastSpell.Message(this.getEntityId(), this.getEntityId(),
						EnumHand.MAIN_HAND, Spells.blinding_flash, new SpellModifiers());
				WizardryPacketHandler.net.sendToDimension(msg, this.world.provider.getDimension());
			}
		}
	}

	/**
	 * Taken from {@link ClientProxy#handleTransportationPacket(electroblob.wizardry.packet.PacketTransportation.Message)} for consistency
	 */
	private void spawnParticles() {
		BlockPos pos = this.getPos();
		// Moved from when the packet is sent to when it is received; fixes the sound not playing in first person.
		// Changed to a position to avoid syncing issues
		world.playSound(pos.getX(), pos.getY(), pos.getZ(), WizardrySounds.SPELL_TRANSPORTATION_TRAVEL, WizardrySounds.SPELLS, 1, 1, false);

		int[] colours = BlockReceptacle.PARTICLE_COLOURS.get(this.getElement());

		for (int i = 0; i < 20; i++) {
			double radius = 1;
			float angle = world.rand.nextFloat() * (float) Math.PI * 2;
			double x = pos.getX() + 0.5 + radius * MathHelper.cos(angle);
			double y = pos.getY() + world.rand.nextDouble() * 2;
			double z = pos.getZ() + 0.5 + radius * MathHelper.sin(angle);
			ParticleBuilder.create(ParticleBuilder.Type.SPARKLE).pos(x, y, z).vel(0, 0.02, 0).clr(colours[0])
					.fade(colours[1]).time(80 + world.rand.nextInt(10)).spawn(world);
		}
		for (int i = 0; i < 20; i++) {
			double radius = 1;
			float angle = world.rand.nextFloat() * (float) Math.PI * 2;
			double x = pos.getX() + 0.5 + radius * MathHelper.cos(angle);
			double y = pos.getY() + world.rand.nextDouble() * 2;
			double z = pos.getZ() + 0.5 + radius * MathHelper.sin(angle);
			world.spawnParticle(EnumParticleTypes.VILLAGER_HAPPY, x, y, z, 0, 0.02, 0);
		}
		for (int i = 0; i < 20; i++) {
			double radius = 1;
			float angle = world.rand.nextFloat() * (float) Math.PI * 2;
			double x = pos.getX() + 0.5 + radius * MathHelper.cos(angle);
			double y = pos.getY() + world.rand.nextDouble() * 2;
			double z = pos.getZ() + 0.5 + radius * MathHelper.sin(angle);
			world.spawnParticle(EnumParticleTypes.ENCHANTMENT_TABLE, x, y, z, 0, 0.02, 0);
		}
	}
}
