package com.windanesz.ancientspellcraft.entity.living;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.entity.ai.EntityAIAttackSpellImproved;
import com.windanesz.ancientspellcraft.entity.ai.EntityAIBattlemageMelee;
import com.windanesz.ancientspellcraft.entity.ai.EntityAIBattlemageSpellcasting;
import com.windanesz.ancientspellcraft.entity.ai.EntityAIBlockWithShield;
import com.windanesz.ancientspellcraft.entity.ai.IShieldUser;
import com.windanesz.ancientspellcraft.item.ItemRareScroll;
import com.windanesz.ancientspellcraft.item.ItemWarlockOrb;
import com.windanesz.ancientspellcraft.item.WizardClassWeaponHelper;
import com.windanesz.ancientspellcraft.registry.ASItems;
import com.windanesz.ancientspellcraft.registry.ASSpells;
import com.windanesz.ancientspellcraft.spell.Covenant;
import electroblob.wizardry.Wizardry;
import electroblob.wizardry.constants.Element;
import electroblob.wizardry.constants.Tier;
import electroblob.wizardry.data.WizardData;
import electroblob.wizardry.entity.living.EntityAIAttackSpell;
import electroblob.wizardry.entity.living.EntityWizard;
import electroblob.wizardry.entity.living.ISummonedCreature;
import electroblob.wizardry.event.DiscoverSpellEvent;
import electroblob.wizardry.item.ItemArtefact;
import electroblob.wizardry.item.ItemSpellBook;
import electroblob.wizardry.item.ItemWizardArmour;
import electroblob.wizardry.misc.WildcardTradeList;
import electroblob.wizardry.registry.Spells;
import electroblob.wizardry.registry.WizardryAdvancementTriggers;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.registry.WizardrySounds;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.AllyDesignationSystem;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.InventoryUtils;
import electroblob.wizardry.util.NBTExtras;
import electroblob.wizardry.util.SpellModifiers;
import electroblob.wizardry.util.SpellProperties;
import electroblob.wizardry.util.WandHelper;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIOpenDoor;
import net.minecraft.entity.ai.EntityAIRestrictOpenDoor;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityAIWatchClosest2;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EntityClassWizard extends EntityWizard implements ICustomCooldown, IArmourClassWizard, IShieldUser {

	private static final DataParameter<Integer> SHIELD_DISABLED_TICK = EntityDataManager.createKey(EntityClassWizard.class, DataSerializers.VARINT);

	@Nullable
	private EntityPlayer customer;
	private static final ResourceLocation BATTLEMAGE_LOOT_TABLE = new ResourceLocation(AncientSpellcraft.MODID, "entities/evil_battlemage");
	private static final ResourceLocation SAGE_LOOT_TABLE = new ResourceLocation(AncientSpellcraft.MODID, "entities/evil_sage");
	private static final ResourceLocation WARLOCK_LOOT_TABLE = new ResourceLocation(AncientSpellcraft.MODID, "entities/evil_warlock");

	/**
	 * True if this evil wizard was spawned as part of a structure (tower or shrine), false if it spawned naturally.
	 */
	public ItemWizardArmour.ArmourClass armourClass = ItemWizardArmour.ArmourClass.BATTLEMAGE;
	private MerchantRecipeList trades;
	private int timeUntilReset;

	/** addDefaultEquipmentAndRecipies is called if this is true */
	private boolean updateRecipes;
	protected int cooldown;

	private int  battlemageMercenaryRemainingDuration;

	/**
	 * Data parameter for the wizard's armour class.
	 */
	private static final DataParameter<Integer> EVIL_WIZARD_ARMOUR_CLASS = EntityDataManager.createKey(EntityClassWizard.class, DataSerializers.VARINT);

	// Field implementations
	private List<Spell> spells = new ArrayList<Spell>(4);

	private EntityAIAttackSpellImproved<EntityClassWizard> spellCastingAIImproved = new EntityAIAttackSpellImproved<>(this, 0.5D, 14.0F, 30, 80);
	private final EntityAIBattlemageMelee entityAIBattlemageMelee = new EntityAIBattlemageMelee(this, 0.6D, false);
	private final EntityAIBattlemageSpellcasting entityAIBattlemageSpellcasting = new EntityAIBattlemageSpellcasting(this, 0.6D, 14.0F, 30, 50);

	public int getCooldown() {return cooldown;}

	public void setCooldown(int cooldown) {this.cooldown = cooldown;}

	@Override
	public int incrementCooldown() {return cooldown++;}

	@Override
	public int decrementCooldown() {return cooldown--;}

	public EntityClassWizard(World world) {
		super(world);
		// discard the old AI
		this.tasks.taskEntries.removeIf(t -> t.action instanceof EntityAIAttackSpell);

		// add the new AI tasks
		this.tasks.addTask(3, this.spellCastingAIImproved);
		this.tasks.addTask(3, this.entityAIBattlemageMelee);
		this.tasks.addTask(3, this.entityAIBattlemageSpellcasting);
	}

	@Override
	protected ResourceLocation getLootTable() {
		// TODO debug
		ResourceLocation loot = super.getLootTable();

		if (getArmourClass() == ItemWizardArmour.ArmourClass.BATTLEMAGE) {
			loot = BATTLEMAGE_LOOT_TABLE;
		} else if (getArmourClass() == ItemWizardArmour.ArmourClass.SAGE) {
			loot = SAGE_LOOT_TABLE;
		} else if (getArmourClass() == ItemWizardArmour.ArmourClass.WARLOCK) {
			loot = WARLOCK_LOOT_TABLE;
		}

		return loot;
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		this.dataManager.register(EVIL_WIZARD_ARMOUR_CLASS, 0);
		dataManager.register(SHIELD_DISABLED_TICK, 0);
	}



	@Override
	protected void initEntityAI(){

		this.tasks.addTask(0, new EntityAISwimming(this));
		// Why would you go to the effort of making the IMerchant interface and then have the AI classes only accept
		// EntityVillager?
		this.tasks.addTask(1, new EntityAITradePlayer(this));
		this.tasks.addTask(1, new EntityAILookAtTradePlayer(this));
		this.tasks.addTask(4, new EntityAIRestrictOpenDoor(this));
		this.tasks.addTask(5, new EntityAIOpenDoor(this, true));
		this.tasks.addTask(6, new EntityAIMoveTowardsRestriction(this, 0.6D));
		this.tasks.addTask(7, new EntityAIWatchClosest2(this, EntityPlayer.class, 3.0F, 1.0F));
		this.tasks.addTask(7, new EntityAIWatchClosest2(this, EntityWizard.class, 5.0F, 0.02F));
		this.tasks.addTask(7, new EntityAIWander(this, 0.6D));
		this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityLiving.class, 8.0F));
		this.tasks.addTask(2, new EntityAIBlockWithShield(this));

		this.targetSelector = entity -> {

			// If the target is valid and not invisible...
			if(entity != this && entity != null && !entity.isInvisible() && !(entity instanceof EntityWizard)
					&& AllyDesignationSystem.isValidTarget(EntityClassWizard.this, entity)){

				// ... and is a mob, a summoned creature ...
				if((entity instanceof IMob || entity instanceof ISummonedCreature
						// ... or in the whitelist ...
						|| Arrays.asList(Wizardry.settings.summonedCreatureTargetsWhitelist)
						.contains(EntityList.getKey(entity.getClass())))
						// ... and isn't in the blacklist ...
						&& !Arrays.asList(Wizardry.settings.summonedCreatureTargetsBlacklist)
						.contains(EntityList.getKey(entity.getClass()))){
					// ... it can be attacked.
					return true;
				}
			}

			return false;
		};

		this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
		// By default, wizards don't attack players unless the player has attacked them.
		this.targetTasks.addTask(0, new EntityAINearestAttackableTarget<EntityLiving>(this, EntityLiving.class, 0,
				false, true, this.targetSelector));
	}


	@Override
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {

		livingdata = super.onInitialSpawn(difficulty, livingdata);

		if (getElement() == null) {
			if (rand.nextInt(10) > 2) {
				this.setElement(Element.values()[rand.nextInt(Element.values().length - 1) + 1]);
			} else {
				this.setElement(Element.MAGIC);
			}
		}

		// FIXME: ugly hack which will break when we'll add more types!
		if (getArmourClass() == null || getArmourClass() == ItemWizardArmour.ArmourClass.WIZARD) {
			this.setArmourClass(ItemWizardArmour.ArmourClass.values()[world.rand.nextInt(3) + 1]);
		}

		Element element = this.getElement();

		// Adds armour.
		for (EntityEquipmentSlot slot : InventoryUtils.ARMOUR_SLOTS) {
			this.setItemStackToSlot(slot, new ItemStack(ItemWizardArmour.getArmour(element, this.getArmourClass(), slot)));
		}

		// All wizards know magic missile, even if it is disabled.

		int spellCount;
		switch (this.getArmourClass()) {
			case SAGE:
				spellCount = 9;
			case WARLOCK:
				spellCount = 6;
			default: // BATTLEMAGE
				spellCount = 4;
		}

		Tier maxTier = IArmourClassWizard.populateSpells(this, spells, element, this.getArmourClass() == ItemWizardArmour.ArmourClass.SAGE
				|| this.getArmourClass() == ItemWizardArmour.ArmourClass.WARLOCK, spellCount, rand);

		if (this.getArmourClass() == ItemWizardArmour.ArmourClass.WARLOCK) {
			spells.remove(Spells.magic_missile);
			if (rand.nextBoolean()) {
				spells.add(ASSpells.chaos_blast);
			}
			//spells.add(ASSpells.chaos_orb);
			spells.add(ASSpells.chaos_orb);
		}

		// Now done after the spells so it can take the tier into account.
		ItemStack wand = new ItemStack(WizardryItems.getWand(maxTier, element));
		ArrayList<Spell> list = new ArrayList<>(spells);
		list.add(Spells.heal);
		WandHelper.setSpells(wand, list.toArray(new Spell[5]));

		if (getArmourClass() == ItemWizardArmour.ArmourClass.BATTLEMAGE) {
			ItemStack sword = new ItemStack(ASItems.battlemage_sword_master);
			NBTTagCompound nbt = sword.getTagCompound();
			if (nbt == null) {
				nbt = new NBTTagCompound();
			}
			nbt.setString(WizardClassWeaponHelper.ELEMENT_TAG, element.name());
			sword.setTagCompound(nbt);

			this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, sword);
			this.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, new ItemStack(ASItems.battlemage_shield));
			setAITask(ItemWizardArmour.ArmourClass.BATTLEMAGE);
		} else if (getArmourClass() == ItemWizardArmour.ArmourClass.WARLOCK) {
				wand = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(AncientSpellcraft.MODID, "warlock_orb_" + Tier.values()[rand.nextInt(4)].toString().toLowerCase() + "_" + element.name().toLowerCase())));
				NBTTagCompound nbt = wand.getTagCompound();
				if (nbt == null) {
					nbt = new NBTTagCompound();
				}
				wand.setTagCompound(nbt);
			this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, wand);
			setAITask(ItemWizardArmour.ArmourClass.WIZARD);
		} else {
			wand = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(AncientSpellcraft.MODID, "sage_tome_" + Tier.values()[rand.nextInt(4)].toString().toLowerCase() + "_" + element.name().toLowerCase())));
			NBTTagCompound nbt = wand.getTagCompound();
			if (nbt == null) {
				nbt = new NBTTagCompound();
			}
			wand.setTagCompound(nbt);
			this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, wand);
			setAITask(ItemWizardArmour.ArmourClass.WIZARD);
		}


		// Default chance is 0.085f, for reference.
		for (EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {this.setDropChance(slot, 0.0f);}

		return livingdata;
	}

	private void setAITask(ItemWizardArmour.ArmourClass armourClass) {
		if (armourClass == ItemWizardArmour.ArmourClass.BATTLEMAGE) {
			this.tasks.taskEntries.removeIf(t -> t.action == spellCastingAIImproved);
		//	ToDO
		} else {
			this.tasks.taskEntries.removeIf(t -> t.action == entityAIBattlemageSpellcasting);
			this.tasks.taskEntries.removeIf(t -> t.action == entityAIBattlemageMelee);
		}
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if (getShieldDisabledTick() > 0) {
			decrementShieldDisabledTick();
		}
		if (battlemageMercenaryRemainingDuration > 0) {
			battlemageMercenaryRemainingDuration--;
		} else {
			Covenant.endAlliance(this);
		}
	}

	public ItemWizardArmour.ArmourClass getArmourClass() {
		return ItemWizardArmour.ArmourClass.values()[this.dataManager.get(EVIL_WIZARD_ARMOUR_CLASS)];
	}

	public void setArmourClass(ItemWizardArmour.ArmourClass armourClass) {
		this.dataManager.set(EVIL_WIZARD_ARMOUR_CLASS, armourClass.ordinal());
	}

	@Override
	public List<Spell> getSpells() {return spells;}

	@Override
	public ITextComponent getDisplayName() {
		if (this.hasCustomName()) {
			TextComponentString textcomponentstring = new TextComponentString(ScorePlayerTeam.formatPlayerName(this.getTeam(), this.getName()));
			textcomponentstring.getStyle().setHoverEvent(this.getHoverEvent());
			textcomponentstring.getStyle().setInsertion(this.getCachedUniqueIdString());
			// don't see why this could happen but it might fix #81
			if (textcomponentstring == null) {
				ITextComponent wizardName = new TextComponentTranslation("class_element." + getElement().getName() + ".wizard");
				ITextComponent className = getArmourClassNameFor(this.getArmourClass());
				new TextComponentTranslation("entity." + this.getEntityString() + "_combined.name", wizardName, className);
			}
			return textcomponentstring;
		}

		// no-element wizards should only display the class name
		if (this.getElement() == Element.MAGIC) {
			return getArmourClassNameFor(this.getArmourClass());
		}

		ITextComponent wizardName = new TextComponentTranslation("class_element." + getElement().getName() + ".wizard");
		ITextComponent className = getArmourClassNameFor(this.getArmourClass());

		return new TextComponentTranslation("entity." + this.getEntityString() + "_combined.name", wizardName, className);
	}

	@Override
	public SpellModifiers getModifiers() {
		return this.getArmourClass() == ItemWizardArmour.ArmourClass.WARLOCK ? this.getWarlockSpellModifiers() : super.getModifiers();
	}

	private SpellModifiers getWarlockSpellModifiers() {
		SpellModifiers modifiers = new SpellModifiers();
		if (this.getHeldItemMainhand().getItem() instanceof ItemWarlockOrb) {
			float potency = (float) (1 + ((((ItemWarlockOrb) this.getHeldItemMainhand().getItem()).tier.ordinal() + 1) * 0.15));
			modifiers.set(SpellModifiers.POTENCY, potency, false);
		}
		return modifiers;
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {

		super.writeEntityToNBT(nbt);
		NBTExtras.storeTagSafely(nbt, "spells", NBTExtras.listToNBT(spells, spell -> new NBTTagInt(spell.metadata())));
		nbt.setInteger("armour_class", this.getArmourClass().ordinal());
		nbt.setInteger("battlemage_mercenary_remaining_duration", battlemageMercenaryRemainingDuration);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {

		super.readEntityFromNBT(nbt);
		this.spells = (List<Spell>) NBTExtras.NBTToList(nbt.getTagList("spells", Constants.NBT.TAG_INT),
				(NBTTagInt tag) -> Spell.byMetadata(tag.getInt()));
		ItemWizardArmour.ArmourClass armourClass = ItemWizardArmour.ArmourClass.values()[nbt.getInteger("armour_class")];
		this.setArmourClass(armourClass);
		setAITask(armourClass);
		if (nbt.hasKey("battlemage_mercenary_remaining_duration")) {
			this.battlemageMercenaryRemainingDuration = nbt.getInteger("battlemage_mercenary_remaining_duration");
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

			this.addRandomRecipes(3);

			// can sell a stone tablet occasionally
			if (rand.nextBoolean()) {
				if (this.getArmourClass() == ItemWizardArmour.ArmourClass.WARLOCK) {
					ItemStack secondItemToBuy = new ItemStack(WizardryItems.magic_crystal, Tier.APPRENTICE.ordinal() * 3 + 1 + rand.nextInt(8));
					this.trades.add(new MerchantRecipe(this.getRandomPrice(Tier.APPRENTICE), secondItemToBuy, new ItemStack(ASItems.blank_rune), 0, 5));

				} else {
					List<Item> tablets = new ArrayList<>(Arrays.asList(ASItems.stone_tablet_small, ASItems.stone_tablet,
							ASItems.stone_tablet_large, ASItems.stone_tablet_grand));
					Item tablet = tablets.get(rand.nextInt(tablets.size()));

					if (tablet == ASItems.stone_tablet_small) {
						ItemStack secondItemToBuy = new ItemStack(WizardryItems.magic_crystal, Tier.NOVICE.ordinal() * 3 + 1 + rand.nextInt(4));
						this.trades.add(new MerchantRecipe(this.getRandomPrice(Tier.NOVICE), secondItemToBuy, new ItemStack(tablet), 0, 2));
					} else if (tablet == ASItems.stone_tablet) {
						ItemStack secondItemToBuy = new ItemStack(WizardryItems.magic_crystal, Tier.APPRENTICE.ordinal() * 3 + 1 + rand.nextInt(4));
						this.trades.add(new MerchantRecipe(this.getRandomPrice(Tier.APPRENTICE), secondItemToBuy, new ItemStack(tablet), 0, 1));
					} else {
						ItemStack secondItemToBuy = new ItemStack(WizardryItems.magic_crystal, Tier.ADVANCED.ordinal() * 3 + 1 + rand.nextInt(8));
						this.trades.add(new MerchantRecipe(this.getRandomPrice(Tier.ADVANCED), secondItemToBuy, new ItemStack(tablet), 0, 1));

					}
				}
			}

			String rarity = "uncommon";
			LootTable table = world.getLootTableManager().getLootTableFromLocation(new ResourceLocation(Wizardry.MODID, "subsets/" + rarity + "_artefacts"));
			LootContext context = new LootContext.Builder((WorldServer) world).withLuck(0).build();
			List<ItemStack> stacks = table.generateLootForPools(world.rand, context);
			if (!stacks.isEmpty()) {
				ItemStack secondItemToBuy = new ItemStack(WizardryItems.astral_diamond);
				this.trades.add(new MerchantRecipe(getRandomPrice(Tier.MASTER), secondItemToBuy, stacks.get(0), 0, 1));

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



	@Override
	protected void updateAITasks(){

		if(!this.isTrading() && this.timeUntilReset > 0){

			--this.timeUntilReset;

			if(this.timeUntilReset <= 0){

				if(this.updateRecipes){

					for(MerchantRecipe merchantrecipe : this.trades){

						if(merchantrecipe.isRecipeDisabled() && !(merchantrecipe.getItemToSell().getItem() instanceof ItemArtefact)){
							// Increases the number of allowed uses of a disabled recipe by a random number.
							if (!(merchantrecipe.getItemToSell().getItem() instanceof ItemRareScroll) || rand.nextFloat() < 0.15f) {
								merchantrecipe.increaseMaxTradeUses(1);
							}
						}
					}

					if(this.trades.size() < 12){
						this.addRandomRecipes(1);
					}

					this.updateRecipes = false;
				}

                if (!this.world.isRemote) {
                    this.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 200, 0));
                }
			}
		}

		super.updateAITasks(); // This actually does nothing
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
				randomiser = rand.nextInt(Wizardry.settings.discoveryMode ? 16 : 10);
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
				} else if (randomiser < 14) {
					Item upgrade = this.getArmourClass() == ItemWizardArmour.ArmourClass.BATTLEMAGE ? ASItems.crystal_silver_nugget : this.getArmourClass() ==
							ItemWizardArmour.ArmourClass.SAGE ? ASItems.enchanted_filament : ASItems.ethereal_essence;
					return new ItemStack(upgrade, 3);
				}else {
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
					return new ItemStack(ASItems.transmutation_scroll, 1);
				} else if (randomiser < 12) {
					return new ItemStack(WizardryItems.grand_crystal, 1);
				} else {
					List<Item> upgrades = new ArrayList<Item>(WandHelper.getSpecialUpgrades());
					randomiser = rand.nextInt(upgrades.size());
					return new ItemStack(upgrades.get(randomiser));
				}

			case MASTER:
				// If a regular wizard rolls a master trade, it can only be a simple master wand or a tome of arcana
				randomiser = this.getElement() != Element.MAGIC ? rand.nextInt(10) : 5 + rand.nextInt(3);

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
				} else if (randomiser < 8) {
					Item upgrade = this.getArmourClass() == ItemWizardArmour.ArmourClass.BATTLEMAGE ? WizardryItems.crystal_silver_plating : this.getArmourClass() ==
							 ItemWizardArmour.ArmourClass.SAGE ? WizardryItems.resplendent_thread : WizardryItems.ethereal_crystalweave;
					return new ItemStack(upgrade, 1);
				} else {
					return new ItemStack(WizardryItems.arcane_tome, 1, 3);
				}
		}

		return new ItemStack(Blocks.STONE);
	}

	@Override
	public void useRecipe(MerchantRecipe merchantrecipe) {

		merchantrecipe.incrementToolUses();
		this.livingSoundTime = -this.getTalkInterval();
		this.playSound(WizardrySounds.ENTITY_WIZARD_YES, this.getSoundVolume(), this.getSoundPitch());

		if(this.getCustomer() != null){
			// Achievements
			WizardryAdvancementTriggers.wizard_trade.triggerFor(this.getCustomer());

			if(merchantrecipe.getItemToSell().getItem() instanceof ItemSpellBook){

				Spell spell = Spell.byMetadata(merchantrecipe.getItemToSell().getItemDamage());

				if(spell.getTier() == Tier.MASTER) WizardryAdvancementTriggers.buy_master_spell.triggerFor(this.getCustomer());

				// Spell discovery (a lot of this is the same as in the event handler)
				WizardData data = WizardData.get(this.getCustomer());

				if(data != null){

					if(!MinecraftForge.EVENT_BUS.post(new DiscoverSpellEvent(this.getCustomer(), spell,
							DiscoverSpellEvent.Source.PURCHASE)) && data.discoverSpell(spell)){

						data.sync();

						if(!world.isRemote && !this.getCustomer().isCreative() && Wizardry.settings.discoveryMode){
							// Sound and text only happen server-side, in survival, with discovery mode on
							EntityUtils.playSoundAtPlayer(this.getCustomer(), WizardrySounds.MISC_DISCOVER_SPELL, 1.25f, 1);
							this.getCustomer().sendMessage(new TextComponentTranslation("spell.discover",
									spell.getNameForTranslationFormatted()));
						}
					}
				}
			}
		}

		// Changed to a 4 in 5 chance of unlocking a new recipe.
		if(customer != null && (this.rand.nextInt(5) > 0 || ItemArtefact.isArtefactActive(customer, WizardryItems.charm_haggler))){
			this.timeUntilReset = 40;
			this.updateRecipes = true;

			if(this.getCustomer() != null){
				this.getCustomer().getName();
			}else{
			}
		}
	}

	protected void applyEntityAttributes()
	{
		super.applyEntityAttributes();
		this.getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
	}


	public boolean attackEntityAsMob(Entity entityIn)
	{
		float f = (float)this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
		int i = 0;

		if (entityIn instanceof EntityLivingBase)
		{
			f += EnchantmentHelper.getModifierForCreature(this.getHeldItemMainhand(), ((EntityLivingBase)entityIn).getCreatureAttribute());
			i += EnchantmentHelper.getKnockbackModifier(this);
		}

		boolean flag = entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), f);

		if (flag)
		{
			if (i > 0 && entityIn instanceof EntityLivingBase)
			{
				((EntityLivingBase)entityIn).knockBack(this, (float)i * 0.5F, (double)MathHelper.sin(this.rotationYaw * 0.017453292F), (double)(-MathHelper.cos(this.rotationYaw * 0.017453292F)));
				this.motionX *= 0.6D;
				this.motionZ *= 0.6D;
			}

			int j = EnchantmentHelper.getFireAspectModifier(this);

			if (j > 0)
			{
				entityIn.setFire(j * 4);
			}

			if (entityIn instanceof EntityPlayer)
			{
				EntityPlayer entityplayer = (EntityPlayer)entityIn;
				ItemStack itemstack = this.getHeldItemMainhand();
				ItemStack itemstack1 = entityplayer.isHandActive() ? entityplayer.getActiveItemStack() : ItemStack.EMPTY;

				if (!itemstack.isEmpty() && !itemstack1.isEmpty() && itemstack.getItem().canDisableShield(itemstack, itemstack1, entityplayer, this) && itemstack1.getItem().isShield(itemstack1, entityplayer))
				{
					float f1 = 0.25F + (float)EnchantmentHelper.getEfficiencyModifier(this) * 0.05F;

					if (this.rand.nextFloat() < f1)
					{
						entityplayer.getCooldownTracker().setCooldown(itemstack1.getItem(), 100);
						this.world.setEntityState(entityplayer, (byte)30);
					}
				}
			}

			this.applyEnchantments(this, entityIn);
		}

		return flag;
	}

	@Override
	protected void blockUsingShield(EntityLivingBase attacker) {
		attacker.knockBack(this, 0.1F, posX - attacker.posX, posZ - attacker.posZ);

		if (attacker.getHeldItemMainhand().getItem().canDisableShield(attacker.getHeldItemMainhand(), getActiveItemStack(), this, attacker)) {
			disableShield();
		}
	}

	private void disableShield() {
		float f = 1F + (float) EnchantmentHelper.getEfficiencyModifier(this) * 0.05F;

		if (rand.nextFloat() < f) {
			setShieldDisabledTick(80);
			resetActiveHand();
			world.setEntityState(this, (byte) 30);
		}
	}

	@Override
	protected void damageShield(float damage) {
		if (damage >= 3.0F && activeItemStack.getItem().isShield(activeItemStack, this)) {
			int i = 1 + MathHelper.floor(damage);
			getShieldStack().damageItem(i, this);
			setActiveHand(EnumHand.OFF_HAND);

			if (getShieldStack().isEmpty()) {  //shield breaks
				setShieldStack(ItemStack.EMPTY);
				playSound(SoundEvents.ITEM_SHIELD_BREAK, 0.8F, 0.8F + world.rand.nextFloat() * 0.4F);
			}
		}
	}

	// vanilla method with a check for shield items to ignore canContinueUsing
	@Override
	protected void updateActiveHand() {
		if (isHandActive()) {
			ItemStack itemstack = getHeldItem(getActiveHand());
			if (net.minecraftforge.common.ForgeHooks.canContinueUsing(activeItemStack, itemstack) || itemstack.getItem().isShield(itemstack, this)) {
				activeItemStack = itemstack;
			}

			if (itemstack == activeItemStack) {
				if (!activeItemStack.isEmpty()) {
					activeItemStackUseCount = net.minecraftforge.event.ForgeEventFactory.onItemUseTick(this, activeItemStack, activeItemStackUseCount);
					if (activeItemStackUseCount > 0) { activeItemStack.getItem().onUsingTick(activeItemStack, this, activeItemStackUseCount); }
				}

				if (getItemInUseCount() <= 25 && getItemInUseCount() % 4 == 0) {
					updateItemUse(activeItemStack, 5);
				}

				if ((--activeItemStackUseCount <= 0 || 20000 - activeItemStackUseCount > Math.max(25, rand.nextInt(100))) && !world.isRemote) {
					onItemUseFinish();
				}
			} else {
				resetActiveHand();
			}
		}
	}


	private ItemStack getShieldStack() {
		return getHeldItem(EnumHand.OFF_HAND);
	}
	public void setShieldStack(ItemStack stack) {
		setHeldItem(EnumHand.OFF_HAND, stack);
	}

	public void setShieldDisabledTick(int count) {
		dataManager.set(SHIELD_DISABLED_TICK, count);
	}

	public int getShieldDisabledTick() {
		return dataManager.get(SHIELD_DISABLED_TICK);
	}

	public void decrementShieldDisabledTick() {
		dataManager.set(SHIELD_DISABLED_TICK, (dataManager.get(SHIELD_DISABLED_TICK)) - 1);
	}

	public int getBattlemageMercenaryRemainingDuration() {
		return battlemageMercenaryRemainingDuration;
	}

	public void setBattlemageMercenaryRemainingDuration(int battlemageMercenaryRemainingDuration) {
		this.battlemageMercenaryRemainingDuration = battlemageMercenaryRemainingDuration;
	}

	@Override
	public boolean processInteract(EntityPlayer player, EnumHand hand){

		ItemStack stack = player.getHeldItem(hand);

		// Debugging
		// player.addChatComponentMessage(new TextComponentTranslation("wizard.debug",
		// Spell.get(spells[1]).getDisplayName(), Spell.get(spells[2]).getDisplayName(),
		// Spell.get(spells[3]).getDisplayName()));

		// When right-clicked with a spell book in creative, sets one of the spells to that spell
		if(player.isCreative() && stack.getItem() instanceof ItemSpellBook){
			Spell spell = Spell.byMetadata(stack.getItemDamage());
			if(this.spells.size() >= 4 && spell.canBeCastBy(this, true)){
				// The set(...) method returns the element that was replaced - neat!
				player.sendMessage(new TextComponentTranslation("item." + Wizardry.MODID + ":spell_book.apply_to_wizard",
						this.getDisplayName(), this.spells.set(rand.nextInt(3) + 1, spell).getNameForTranslationFormatted(),
						spell.getNameForTranslationFormatted()));
				return true;
			}
		}

		// Won't trade with a player that has attacked them.
		if(this.isEntityAlive() && !this.isTrading() && !this.isChild() && !player.isSneaking()
				&& this.getAttackTarget() != player){
			if(!this.world.isRemote){
				this.setCustomer(player);
				player.displayVillagerTradeGui(this);
				// player.displayGUIMerchant(this, this.getElement().getWizardName());
			}

			return true;
		}else{
			return false;
		}
	}
}
