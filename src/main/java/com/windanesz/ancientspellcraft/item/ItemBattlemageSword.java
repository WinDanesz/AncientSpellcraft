package com.windanesz.ancientspellcraft.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.Settings;
import com.windanesz.ancientspellcraft.data.ClassWeaponData;
import com.windanesz.ancientspellcraft.entity.living.EntityEvilClassWizard;
import com.windanesz.ancientspellcraft.registry.ASItems;
import com.windanesz.ancientspellcraft.registry.ASSpells;
import com.windanesz.ancientspellcraft.registry.ASTabs;
import com.windanesz.ancientspellcraft.spell.Runeword;
import com.windanesz.ancientspellcraft.spell.RunewordFury;
import com.windanesz.ancientspellcraft.util.WizardArmourUtils;
import electroblob.wizardry.Wizardry;
import electroblob.wizardry.constants.Constants;
import electroblob.wizardry.constants.Element;
import electroblob.wizardry.constants.Tier;
import electroblob.wizardry.data.IStoredVariable;
import electroblob.wizardry.data.Persistence;
import electroblob.wizardry.data.SpellGlyphData;
import electroblob.wizardry.data.WizardData;
import electroblob.wizardry.event.SpellCastEvent;
import electroblob.wizardry.item.IManaStoringItem;
import electroblob.wizardry.item.ISpellCastingItem;
import electroblob.wizardry.item.IWorkbenchItem;
import electroblob.wizardry.item.ItemWand;
import electroblob.wizardry.item.ItemWizardArmour;
import electroblob.wizardry.packet.PacketCastSpell;
import electroblob.wizardry.packet.WizardryPacketHandler;
import electroblob.wizardry.registry.Spells;
import electroblob.wizardry.registry.WizardryAdvancementTriggers;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.registry.WizardrySounds;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.AllyDesignationSystem;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.SpellModifiers;
import electroblob.wizardry.util.WandHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.Slot;
import net.minecraft.item.EnumAction;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemShield;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

/**
 * Monster-sized class that holds all the Spell Blade related code. Brace yourselves.
 *
 * Author WinDanesez
 */
@Mod.EventBusSubscriber
public class ItemBattlemageSword extends ItemSword implements ISpellCastingItem, IWorkbenchItem, IWizardClassWeapon, IManaStoringItem {

	private static final String MANA_AVAILABLE_TAG = "mana_available";

	private static final String TEMPORARY_RUNEWORD_DATA_TAG = "temporary_runeword_data";

	/**
	 * The number of ticks between each time a continuous spell is added to the player's recently-cast spells.
	 */
	private static final int CONTINUOUS_TRACKING_INTERVAL = 20;
	/**
	 * The increase in progression for casting spells of the matching element.
	 */
	private static final float ELEMENTAL_PROGRESSION_MODIFIER = 1.2f;
	/**
	 * The increase in progression for casting an undiscovered spell (can only happen once per spell for each player).
	 */
	private static final float DISCOVERY_PROGRESSION_MODIFIER = 5f;
	/**
	 * The increase in progression for tiers that the player has already reached.
	 */
	private static final float SECOND_TIME_PROGRESSION_MODIFIER = 1.5f;
	/**
	 * The fraction of progression lost when all recently-cast spells are the same as the one being cast.
	 */
	private static final float MAX_PROGRESSION_REDUCTION = 0.75f;

	/**
	 * The number of spell slots a sword has with no attunement upgrades applied.
	 */
	public static final int SWORD_BASE_SPELL_SLOTS = 3;
	public static final String ACTIVE_RUNEWORDS = "active_runewords";

	/**
	 * The maximum number of upgrades that can be applied to a wand of this tier.
	 */
	public final int upgradeLimit;

	private final int attackDamage;

	public final Tier tier;

	public static final IStoredVariable<BlockPos> LAST_POS = IStoredVariable.StoredVariable.ofBlockPos("lastPlayerPos", Persistence.NEVER);

	public ItemBattlemageSword(Tier tier, int upgradeLimit) {
		super(ToolMaterial.IRON);
		setCreativeTab(ASTabs.ANCIENTSPELLCRAFT_GEAR);
		this.tier = tier;
		this.upgradeLimit = upgradeLimit;
		int bonus = tier.level == 0 ? 2 : tier.level == 1 ? 1 : 0;
		this.attackDamage = Settings.generalSettings.spellblade_base_damage + tier.level * Settings.generalSettings.spellblade_damage_increase_per_tier + bonus;
		WizardData.registerStoredVariables(LAST_POS);

		this.addPropertyOverride(new ResourceLocation(WizardClassWeaponHelper.ELEMENT_TAG), new IItemPropertyGetter() {
			@SideOnly(Side.CLIENT)
			public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
				return (WizardClassWeaponHelper.getElement(stack).ordinal() * 0.1f);
			}
		});
	}

	@Override
	public boolean hasEffect(ItemStack stack) {
		return WizardClassWeaponHelper.isChargeFull(stack);
	}

	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean isHeld) {

		WandHelper.decrementCooldowns(stack);

		if (!world.isRemote) {
			boolean hasManaStorage = hasManaStorage(stack);
			boolean powered = false;

			// Decrements wand damage (increases mana) every 1.5 seconds if it has a condenser upgrade
			if (hasManaStorage && !this.isManaFull(stack) && world.getTotalWorldTime() % Constants.CONDENSER_TICK_INTERVAL == 0) {
				// If the upgrade level is 0, this does nothing anyway.
				this.rechargeMana(stack, WandHelper.getUpgradeLevel(stack, WizardryItems.condenser_upgrade));
			}

			NBTTagCompound compound = stack.getTagCompound();
			if (compound == null) {
				compound = new NBTTagCompound();
			}

			if (hasManaStorage && !((ItemBattlemageSword) stack.getItem()).isManaEmpty(stack)) {
				powered = true;
			}

			Element element = Element.MAGIC;

			// look for mana storage
			// this is used for damage calculation as we don't have access to the other hand's held item in getAttributeModifiers
			if (isHeld && entity instanceof EntityLivingBase) {
				EntityLivingBase entityLiving = (EntityLivingBase) entity;

				ItemStack offhand = entityLiving.getHeldItemOffhand();

				if (offhand.getItem() instanceof ItemWand && !((ItemWand) offhand.getItem()).isManaEmpty(offhand)) {
					powered = true;
				}

				element = WizardArmourUtils.getFullSetElementForClass(entityLiving, ItemWizardArmour.ArmourClass.BATTLEMAGE);

				if (element == null)
					element = Element.MAGIC;
				// Triggering the elemental effect of this sword each tick
				EnumElementalSwordEffect.onUpdate(element, stack, world, entityLiving, slot, true);
			}

			if (element == null)
				element = Element.MAGIC;

			// setting the current element type, each tick
			if (element != null) {
				compound.setString(WizardClassWeaponHelper.ELEMENT_TAG, element.getName().toLowerCase());
			}
			compound.setBoolean(MANA_AVAILABLE_TAG, powered);

			// Ticks runewords
			if (entity instanceof EntityLivingBase) {

				for (Spell spell : getSpells(stack)) {
					if (spell instanceof Runeword && ((Runeword) spell).isTicking()) {
						((Runeword) spell).tick((EntityLivingBase) entity, stack);
					}
				}
			}

			// update glyph data
			if (entity.ticksExisted % 15 == 0 && entity instanceof EntityPlayer) {
				List<String> glyphs = new ArrayList<>();

				for (Item item : ItemGlyphArtefact.getActiveArtefacts((EntityPlayer) entity)) {
					if (item instanceof ItemGlyphArtefact) {
						String name = item.getRegistryName().toString();
						if (!glyphs.contains(name)) {
							glyphs.add(name);

							// custom logic
							if (item == ASItems.charm_glyph_antigravity && ((EntityPlayer) entity).getHeldItemMainhand().getItem() instanceof ItemBattlemageSword) {
								((EntityPlayer) entity).addPotionEffect(new PotionEffect(MobEffects.JUMP_BOOST, 40,0));
								((EntityPlayer) entity).addPotionEffect(new PotionEffect(MobEffects.SPEED, 40,0));
							}
						}
					}
				}

				if (!glyphs.isEmpty()) {
					NBTTagList list = new NBTTagList();
					for (String s : glyphs) {
						list.appendTag(new NBTTagString(s));
					}
					compound.setTag("active_glyphs", list);
				}
			}

			stack.setTagCompound(compound);
		}
	}

	// Max damage is modifiable with upgrades.
	@Override
	public int getMaxDamage(ItemStack stack) {
		if (hasManaStorage(stack)) {
			// + 0.5f corrects small float errors rounding down
			return (int) (super.getMaxDamage(stack) * (1.0f + Constants.STORAGE_INCREASE_PER_LEVEL
					* WandHelper.getUpgradeLevel(stack, WizardryItems.storage_upgrade)) + 0.5f);
		}
		return 0;
	}

	//	/** Does nothing, use {@link ItemWand#setMana(ItemStack, int)} to modify wand mana. */
	@Override
	public void setDamage(ItemStack stack, int damage) {
		super.setDamage(stack, damage);
		// Overridden to do nothing to stop repair things from 'repairing' the mana in a wand
	}

	@Override
	public boolean getIsRepairable(ItemStack stack, ItemStack material) {
		return false;
	}

	@Override
	public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
		Multimap<String, AttributeModifier> multimap = HashMultimap.create();

		double damage = this.attackDamage;

		if (slot == EntityEquipmentSlot.MAINHAND) {
			int level = WandHelper.getUpgradeLevel(stack, WizardryItems.melee_upgrade);
			// This check doesn't affect the damage output, but it does stop a blank line from appearing in the tooltip.
			// Only adding this attribute modifier if this sword has innate mana storage and enough mana!
			// Otherwise, see onUpdate()

			// This is sourced from the sword itself
			boolean innateManaAvailable = hasManaStorage(stack) && !this.isManaEmpty(stack);

			// mana sourced from a wand or from innate mana, checked each tick in onUpdate()
			// we only care about this if the sword has no innate mana currently
			boolean anyManaAvailable = stack.hasTagCompound() && stack.getTagCompound().hasKey(MANA_AVAILABLE_TAG) && stack.getTagCompound().getBoolean(MANA_AVAILABLE_TAG);

			if (level > 0 && innateManaAvailable) {
				damage = this.attackDamage + level;
			} else if (level > 0 && anyManaAvailable) {
				damage = (double) this.attackDamage + level;
			} else if (!innateManaAvailable && !anyManaAvailable && stack.hasTagCompound()) {
				// stack.hasTagCompound() is only false if a wizard holds this item as for some reason the onUpdate method is not called for them. this condition makes sure wizards won't deal only half as much damage
				// no melee updates and no mana from any source, the sword will deal minimal damage - no charge damage
				damage = (double) this.attackDamage * (1f / 2f);
			} else {
				// no melee upgrades, but we have some mana from any source - regular charged damage / or a wizard is holding this sword
				damage = this.attackDamage;
			}

			// spell effect
			if (anyManaAvailable) {

				Spell[] spells = getSpells(stack);

				//				Arrays.stream(spells).(s -> (s instanceof Runeword && ((Runeword) s).isAffectingAttributes()));
				for (Spell spell : spells) {

					if (spell instanceof Runeword && ((Runeword) spell).isAffectingAttributes()) {

						HashMap<Runeword, NBTTagCompound> data = getTemporaryRunewordData(stack);

						if (spell == ASSpells.runeword_fury) {
							if (data.containsKey((Runeword) ASSpells.runeword_fury)) {
								int charges = data.get(ASSpells.runeword_fury).getInteger(RunewordFury.CHARGES_TAG);
								float modifier = 1 + ASSpells.runeword_fury.getProperty(RunewordFury.DMG_PERCENT_INCREASE_PER_HIT).floatValue() * charges;
								damage = damage * modifier;
							}
						} else if (spell == ASSpells.runeword_sacrifice) {

						}
					}
				}
			}

			if (stack.hasTagCompound() && stack.getTagCompound().hasKey("active_glyphs")) {
				Iterator<NBTBase> iterator = stack.getTagCompound().getTagList("active_glyphs", net.minecraftforge.common.util.Constants.NBT.TAG_STRING).iterator();
				while (iterator.hasNext()) {
					String glyph = ((NBTTagString) iterator.next()).getString();
					if (glyph.equals("ancientspellcraft:charm_glyph_antigravity")) {
						damage *= 0.7;
//						multimap.put(SharedMonsterAttributes.MOVEMENT_SPEED.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier",
//								0.15f, net.minecraftforge.common.util.Constants.AttributeModifierOperation.ADD_MULTIPLE));
					}
				}

			}

			multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", damage,
					net.minecraftforge.common.util.Constants.AttributeModifierOperation.ADD));
			multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", -2.4000000953674316D,
					net.minecraftforge.common.util.Constants.AttributeModifierOperation.ADD));
		}

		return multimap;
	}

	// Client side effects (particle and sound) are added by electroblob.wizardry.item.ItemWand.onAttackEntityEvent ONLY if the attacker item
	// is IManaStoringItem WITH enough mana, so that won't trigger for the no storage scenario
	@Override
	public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase wielder) {
		EnumHand otherHand = getOtherHandForSword(stack, wielder);
		ItemStack otherStack = wielder.getHeldItem(otherHand);

		// base cost, this is consumed for each hit
		int cost = Settings.generalSettings.spellblade_base_mana_cost * (this.tier.level + 1);

		// cost increase per melee upgrade
		int level = WandHelper.getUpgradeLevel(stack, WizardryItems.melee_upgrade);

		// lightning costs 50% more
		if (WizardClassWeaponHelper.getElement(stack) == Element.LIGHTNING) {
			cost = (int) (cost * 1.5);
		}
		cost = cost + level * 6;

		if (this.getMana(stack) > 0) {
			// consume mana from the sword
			this.consumeMana(stack, cost, wielder);
		} else if (otherStack.getItem() instanceof ItemWand && !((ItemWand) otherStack.getItem()).isManaEmpty(otherStack)) {
			// consume mana from the wand
			((ItemWand) otherStack.getItem()).consumeMana(otherStack, cost, wielder);
		}

		if (hasManaAvailable(stack) || wielder instanceof EntityEvilClassWizard) {
			Element element = wielder instanceof EntityEvilClassWizard ? ((EntityEvilClassWizard) wielder).getElement() : WizardClassWeaponHelper.getElement(stack);
			EnumElementalSwordEffect.hitEntity(element, stack, target, wielder);
		}

		// Charge Progression
		WizardClassWeaponHelper.addChargeProgress(stack, Settings.generalSettings.spellblade_charge_gain_per_spellcast);

		// Progression
		if (this.tier.level < Tier.MASTER.level && wielder instanceof EntityPlayer) {

			EntityPlayer player = (EntityPlayer) wielder;

			int recentHits = ClassWeaponData.getRecentHitCount(player, target);

			// only get progression if this entity was hit less than 2 times + tier level
			if (recentHits <= 2 + this.tier.level) {
				WandHelper.addProgression(stack, 1);
			}

			// If the sword just gained enough progression to be upgraded...
			Tier nextTier = tier.next();
			int excess = WandHelper.getProgression(stack) - nextTier.getProgression();
			if (excess >= 0) {
				// ...display a message above the player's hotbar
				player.playSound(WizardrySounds.ITEM_WAND_LEVELUP, 1.25f, 1);
				//	TODO: advancement
				//	WizardryAdvancementTriggers.wand_levelup.triggerFor(caster);
				if (!player.world.isRemote) {
					player.sendMessage(new TextComponentTranslation("item." + Wizardry.MODID + ":wand.levelup",
							this.getItemStackDisplayName(stack), nextTier.getNameForTranslationFormatted()));
				}
			}

			ClassWeaponData.trackRecentEnemy((player), target);
		}

		return true;
	}

	@Override
	public boolean canContinueUsing(ItemStack oldStack, ItemStack newStack) {
		// Ignore durability changes
		if (ItemStack.areItemsEqualIgnoreDurability(oldStack, newStack)) { return true; }
		return super.canContinueUsing(oldStack, newStack);
	}

	@Override
	public boolean shouldCauseBlockBreakReset(ItemStack oldStack, ItemStack newStack) {
		// Ignore durability changes
		if (ItemStack.areItemsEqualIgnoreDurability(oldStack, newStack)) { return false; }
		return super.shouldCauseBlockBreakReset(oldStack, newStack);
	}

	@Override
	// Only called client-side
	// This method is always called on the item in oldStack, meaning that oldStack.getItem() == this
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {

		// This method does some VERY strange things! Despite its name, it also seems to affect the updating of NBT...

		// We only care about the situation where we specifically want the animation NOT to play.
		if (!oldStack.isEmpty() || !newStack.isEmpty() && oldStack.getItem() == newStack.getItem() && !slotChanged && oldStack.getItem() instanceof ItemBattlemageSword
				&& newStack.getItem() instanceof ItemBattlemageSword) { return false; }

		return super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, World world, List<String> text, net.minecraft.client.util.ITooltipFlag advanced) {

		EntityPlayer player = net.minecraft.client.Minecraft.getMinecraft().player;
		if (player == null) { return; }

		text.add(Wizardry.proxy.translate("item.ancientspellcraft:battlemage_sword_armour_requirements_tooltip"));

		Element element = WizardArmourUtils.getFullSetElementForClass(player, ItemWizardArmour.ArmourClass.BATTLEMAGE);

		if (element != null && element != Element.MAGIC) {
			// +0.5f is necessary due to the error in the way floats are calculated.
			text.add(Wizardry.proxy.translate("item." + Wizardry.MODID + ":wand.buff",
					new Style().setColor(TextFormatting.DARK_GRAY),
					(int) ((tier.level + 1) * (Constants.POTENCY_INCREASE_PER_TIER / 3) * 100 + 0.5f), element.getDisplayName()));
		}

		Spell spell = WandHelper.getCurrentSpell(stack);

		boolean discovered = !Wizardry.settings.discoveryMode || player.isCreative() || WizardData.get(player) == null
				|| WizardData.get(player).hasSpellBeenDiscovered(spell);

		text.add(Wizardry.proxy.translate("item." + Wizardry.MODID + ":wand.spell", new Style().setColor(TextFormatting.GRAY),
				discovered ? spell.getDisplayNameWithFormatting() : "#" + TextFormatting.BLUE + SpellGlyphData.getGlyphName(spell, player.world)));

		if (advanced.isAdvanced()) {

			// show innate mana
			if (hasManaStorage(stack)) {
				text.add(Wizardry.proxy.translate("item." + Wizardry.MODID + ":wand.mana", new Style().setColor(TextFormatting.BLUE),
						this.getMana(stack), this.getManaCapacity(stack)));
			} else {
				Optional<ItemStack> otherSource = WizardClassWeaponHelper.getManaSourceFromOtherHand(stack, player);
				if (otherSource.isPresent()) {
				otherSource.ifPresent(itemStack -> text.add(Wizardry.proxy.translate("tooltip.ancientspellcraft:mana_with_eternal_source_name",
						new Style().setColor(TextFormatting.BLUE), ((IManaStoringItem) otherSource.get().getItem()).getMana(itemStack),
						((IManaStoringItem) otherSource.get().getItem()).getManaCapacity(itemStack), itemStack.getDisplayName() + new Style().setColor(TextFormatting.BLUE).getFormattingCode())));
				} else {
					text.add(Wizardry.proxy.translate("tooltip.ancientspellcraft:no_mana_source", new Style().setColor(TextFormatting.RED)));
				}
			}

		}

		if (this.tier.level < Tier.MASTER.level) {
			text.add(Wizardry.proxy.translate("item." + Wizardry.MODID + ":wand.progression", new Style().setColor(TextFormatting.GRAY),
					WandHelper.getProgression(stack), this.tier.level < Tier.MASTER.level ? tier.next().getProgression() : 0));
		}
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		//Element element = getElement(stack);
		return TextFormatting.LIGHT_PURPLE + super.getItemStackDisplayName(stack);
		//return (element != Element.MAGIC ? "" : element.getFormattingCode()) + super.getItemStackDisplayName(stack);
	}

	private ItemStack getNextTier(Tier tier) {
		switch (tier) {
			case NOVICE:
				return new ItemStack(ASItems.battlemage_sword_apprentice);
			case APPRENTICE:
				return new ItemStack(ASItems.battlemage_sword_advanced);
			default:
				return new ItemStack(ASItems.battlemage_sword_master);
		}
	}

	@Override
	public EnumAction getItemUseAction(ItemStack itemstack) {
		return WandHelper.getCurrentSpell(itemstack).action;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		return 72000;
	}

	// Continuous spells use the onUsingItemTick method instead of this one.
	/* An important thing to note about this method: it is only called on the server and the client of the player
	 * holding the item (I call this client-inconsistency). This means if you spawn particles here they will not show up
	 * on other players' screens. Instead, this must be done via packets. */
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {

		ItemStack stack = player.getHeldItem(hand);

		// Alternate right-click function; overrides spell casting.
		if (WizardClassWeaponHelper.selectMinionTarget(player, world)) { return new ActionResult<>(EnumActionResult.SUCCESS, stack); }

		Spell spell = WandHelper.getCurrentSpell(stack);
		SpellModifiers modifiers = this.calculateModifiers(stack, player, spell);

		if (canCast(stack, spell, player, hand, 0, modifiers)) {
			// Need to account for the modifier since it could be zero even if the original charge-up wasn't
			int chargeup = (int) (spell.getChargeup() * modifiers.get(SpellModifiers.CHARGEUP));

			if (spell.isContinuous || chargeup > 0) {
				// Spells that need the mouse to be held (continuous, charge-up or both)
				if (!player.isHandActive()) {
					player.setActiveHand(hand);
					// Store the modifiers for use later
					if (WizardData.get(player) != null) { WizardData.get(player).itemCastingModifiers = modifiers; }
					if (chargeup > 0 && world.isRemote) { Wizardry.proxy.playChargeupSound(player); }
					return new ActionResult<>(EnumActionResult.SUCCESS, stack);
				}
			} else {
				// All other (instant) spells
				if (cast(stack, spell, player, hand, 0, modifiers)) {
					return new ActionResult<>(EnumActionResult.SUCCESS, stack);
				}
			}
		}

		return new ActionResult<>(EnumActionResult.FAIL, stack);
	}



	// For continuous spells and spells with a charge-up time. The count argument actually decrements by 1 each tick.
	// N.B. The first time this gets called is the tick AFTER onItemRightClick is called, not the same tick
	@Override
	public void onUsingTick(ItemStack stack, EntityLivingBase user, int count){

		if(user instanceof EntityPlayer){

			EntityPlayer player = (EntityPlayer)user;

			Spell spell = WandHelper.getCurrentSpell(stack);

			SpellModifiers modifiers;

			if(WizardData.get(player) != null){
				modifiers = WizardData.get(player).itemCastingModifiers;
			}else{
				modifiers = this.calculateModifiers(stack, (EntityPlayer)user, spell); // Fallback to the old way, should never be used
			}

			int useTick = stack.getMaxItemUseDuration() - count;
			int chargeup = (int)(spell.getChargeup() * modifiers.get(SpellModifiers.CHARGEUP));

			if(spell.isContinuous){
				// Continuous spell charge-up is simple, just don't do anything until it's charged
				if(useTick >= chargeup){
					// castingTick needs to be relative to when the spell actually started
					int castingTick = useTick - chargeup;
					// Continuous spells (these must check if they can be cast each tick since the mana changes)
					// Don't call canCast when castingTick == 0 because we already did it in onItemRightClick - even
					// with charge-up times, because we don't want to trigger events twice
					if(castingTick == 0 || canCast(stack, spell, player, player.getActiveHand(), castingTick, modifiers)){
						cast(stack, spell, player, player.getActiveHand(), castingTick, modifiers);
					}else{
						// Stops the casting if it was interrupted, either by events or because the wand ran out of mana
						player.stopActiveHand();
					}
				}
			}else{
				// Non-continuous spells need to check they actually have a charge-up since ALL spells call setActiveHand
				if(chargeup > 0 && useTick == chargeup){
					// Once the spell is charged, it's exactly the same as in onItemRightClick
					cast(stack, spell, player, player.getActiveHand(), 0, modifiers);
				}
			}
		}
	}

	//--------------------------  ISpellCastingItem implementation  --------------------------//

	@Override
	public Spell getCurrentSpell(ItemStack stack) {
		return WandHelper.getCurrentSpell(stack);
	}

	@Override
	public Spell getNextSpell(ItemStack stack) {
		return WandHelper.getNextSpell(stack);
	}

	@Override
	public Spell getPreviousSpell(ItemStack stack) {
		return WandHelper.getPreviousSpell(stack);
	}

	@Override
	public Spell[] getSpells(ItemStack stack) {
		return WandHelper.getSpells(stack);
	}

	@Override
	public void selectNextSpell(ItemStack stack) {
		WandHelper.selectNextSpell(stack);
	}

	@Override
	public void selectPreviousSpell(ItemStack stack) {
		WandHelper.selectPreviousSpell(stack);
	}

	@Override
	public boolean selectSpell(ItemStack stack, int index) {
		return WandHelper.selectSpell(stack, index);
	}

	@Override
	public int getCurrentCooldown(ItemStack stack) {
		return WandHelper.getCurrentCooldown(stack);
	}

	@Override
	public int getCurrentMaxCooldown(ItemStack stack) {
		return WandHelper.getCurrentMaxCooldown(stack);
	}

	@Override
	public boolean canCast(ItemStack stack, Spell spell, EntityPlayer caster, EnumHand hand, int castingTick, SpellModifiers modifiers) {
		EnumHand otherHand = hand == EnumHand.MAIN_HAND ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;

		// Spells can only be cast if the casting events aren't cancelled...
		if (castingTick == 0) {
			if (MinecraftForge.EVENT_BUS.post(new SpellCastEvent.Pre(AncientSpellcraft.BATTLEMAGE_ITEM, spell, caster, modifiers))) { return false; }
		} else {
			if (MinecraftForge.EVENT_BUS.post(new SpellCastEvent.Tick(AncientSpellcraft.BATTLEMAGE_ITEM, spell, caster, modifiers, castingTick))) { return false; }
		}

		int cost = (int) (spell.getCost() * modifiers.get(SpellModifiers.COST) + 0.1f); // Weird floaty rounding

		// As of wizardry 4.2 mana cost is only divided over two intervals each second
		if (spell.isContinuous) { cost = getDistributedCost(cost, castingTick); }

		// ...and the wand has enough mana to cast the spell...

//		if (hasManaStorage)
		boolean hasWandFromMana = cost <= this.getMana(stack, otherHand, caster) // This comes first because it changes over time
				// ...and the wand is the same tier as the spell or higher...
				&& spell.getTier().level <= this.tier.level
				// ...and either the spell is not in cooldown or the player is in creative mode
				&& (WandHelper.getCurrentCooldown(stack) == 0 || caster.isCreative());

		if (hasWandFromMana) {
			return hasWandFromMana;
		} else {
			boolean test0 = hasManaStorage(stack);
			int mana = this.getMana(stack);
			int cooldown = WandHelper.getCurrentCooldown(stack);
			boolean test = (cost <= mana // ...and the wand is the same tier as the spell or higher...
					&& spell.getTier().level <= this.tier.level
					// ...and either the spell is not in cooldown or the player is in creative mode
					&& ( cooldown == 0 || caster.isCreative()));
			return test;
		}
	}

	@Override
	public boolean cast(ItemStack stack, Spell spell, EntityPlayer caster, EnumHand hand, int castingTick, SpellModifiers modifiers) {

		EnumHand otherHand = hand == EnumHand.MAIN_HAND ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;

		World world = caster.world;

		if (world.isRemote && !spell.isContinuous && spell.requiresPacket()) { return false; }

		if (spell.cast(world, caster, hand, castingTick, modifiers)) {

			if (castingTick == 0) { MinecraftForge.EVENT_BUS.post(new SpellCastEvent.Post(AncientSpellcraft.BATTLEMAGE_ITEM, spell, caster, modifiers)); }

			if (!world.isRemote) {

				// Continuous spells never require packets so don't rely on the requiresPacket method to specify it
				if (!spell.isContinuous && spell.requiresPacket()) {
					// Sends a packet to all players in dimension to tell them to spawn particles.
					IMessage msg = new PacketCastSpell.Message(caster.getEntityId(), hand, spell, modifiers);
					WizardryPacketHandler.net.sendToDimension(msg, world.provider.getDimension());
				}

				// Mana cost
				int cost = (int) (spell.getCost() * modifiers.get(SpellModifiers.COST) + 0.1f); // Weird floaty rounding
				// As of wizardry 4.2 mana cost is only divided over two intervals each second
				if (spell.isContinuous) { cost = getDistributedCost(cost, castingTick); }

				if (cost > 0) {
					// wand
					if (caster.getHeldItem(otherHand).getItem() instanceof IManaStoringItem) {
						int mana = ((IManaStoringItem) caster.getHeldItem(otherHand).getItem()).getMana(caster.getHeldItem(otherHand));
						if (mana >= cost) {
							this.consumeMana(stack, otherHand, caster, cost);
						}
					} else {
						consumeMana(stack, cost, caster);
					}
				}
			}

			caster.setActiveHand(hand);

			// Cooldown
			if (!spell.isContinuous && !caster.isCreative()) { // Spells only have a cooldown in survival
				WandHelper.setCurrentCooldown(stack, (int) (spell.getCooldown() * modifiers.get(WizardryItems.cooldown_upgrade)));
			}

			// Progression
			if (this.tier.level < Tier.MASTER.level && castingTick % CONTINUOUS_TRACKING_INTERVAL == 0) {

				// We don't care about cost modifiers here, otherwise players would be penalised for wearing robes!
				int progression = (int) (spell.getCost() * modifiers.get(SpellModifiers.PROGRESSION));
				WandHelper.addProgression(stack, progression);

				if (!Wizardry.settings.legacyWandLevelling) { // Don't display the message if legacy wand levelling is enabled
					// If the wand just gained enough progression to be upgraded...
					Tier nextTier = tier.next();
					int excess = WandHelper.getProgression(stack) - nextTier.getProgression();
					if (excess >= 0 && excess < progression) {
						// ...display a message above the player's hotbar
						caster.playSound(WizardrySounds.ITEM_WAND_LEVELUP, 1.25f, 1);
						WizardryAdvancementTriggers.wand_levelup.triggerFor(caster);
						if (!world.isRemote) {
							caster.sendMessage(new TextComponentTranslation("item." + Wizardry.MODID + ":wand.levelup",
									this.getItemStackDisplayName(stack), nextTier.getNameForTranslationFormatted()));
						}
					}
				}

				WizardData.get(caster).trackRecentSpell(spell);
			}

			// Charge Progression
			WizardClassWeaponHelper.addChargeProgress(stack, Settings.generalSettings.spellblade_charge_gain_per_spellcast);

			return true;
		}

		return false;
	}

	//--------------------------  Helper methods for runewords and glyphs --------------------------//
	// Methods used to store and retrieve runeword-related data for the swords

	public static HashMap<Runeword, Integer> getActiveRunewords(ItemStack stack) {
		HashMap<Runeword, Integer> map = new HashMap<>();
		if (stack.hasTagCompound() && stack.getTagCompound().hasKey(ACTIVE_RUNEWORDS)) {
			NBTTagCompound compound = stack.getTagCompound().getCompoundTag(ACTIVE_RUNEWORDS);
			for (String key : compound.getKeySet()) {
				Spell runeword = Spell.get(key);
				if (runeword instanceof Runeword && compound.getInteger(key) > 0) {
					map.put((Runeword) runeword, compound.getInteger(key));
				}
			}
		}
		return map;
	}

	public static int getRunewordCharges(ItemStack stack, Runeword runeword) {
		Integer charges = getActiveRunewords(stack).get(runeword);
		return charges != null ? charges : 0;
	}

	public static void setActiveRuneword(ItemStack stack, Runeword runeword, int charges) {
		HashMap<Runeword, Integer> map = getActiveRunewords(stack);
		map.put(runeword, charges);

		NBTTagCompound compound = stack.hasTagCompound() ? stack.getTagCompound() : new NBTTagCompound();
		NBTTagCompound runewords = new NBTTagCompound();
		for (Map.Entry<Runeword, Integer> e : map.entrySet()) {
			if (e.getValue() > 0) {
				//noinspection ConstantConditions
				runewords.setInteger(e.getKey().getRegistryName().toString(), e.getValue());
			}
		}
		//noinspection ConstantConditions
		compound.setTag(ACTIVE_RUNEWORDS, runewords);
		stack.setTagCompound(compound);
	}

	public static void spendCharge(ItemStack stack, Runeword runeword, int amount) {
		HashMap<Runeword, Integer> map = getActiveRunewords(stack);
		if (map.containsKey(runeword)) {
			setActiveRuneword(stack, runeword, map.get(runeword) - amount);
		}
	}

	public static HashMap<Runeword, NBTTagCompound> getTemporaryRunewordData(ItemStack stack) {
		HashMap<Runeword, NBTTagCompound> map = new HashMap<>();
		//noinspection ConstantConditions
		if (stack.hasTagCompound() && stack.getTagCompound().hasKey(TEMPORARY_RUNEWORD_DATA_TAG)) {
			NBTTagCompound compound = stack.getTagCompound().getCompoundTag(TEMPORARY_RUNEWORD_DATA_TAG);
			for (String key : compound.getKeySet()) {
				Spell runeword = Spell.get(key);
				if (runeword instanceof Runeword) {
					map.put((Runeword) runeword, compound.getCompoundTag(key));
				}
			}
		}
		return map;
	}


	public static void setTemporaryRuneWordData(ItemStack sword, Runeword runeword, NBTTagCompound data) {
		// get the existing stuff
		HashMap<Runeword, NBTTagCompound> map = getTemporaryRunewordData(sword);

		if (data != null) {
			// add (or overwrite with) the current runeword
			map.put(runeword, data);
		} else {
			map.remove(runeword);
		}

		NBTTagCompound compound = sword.hasTagCompound() ? sword.getTagCompound() : new NBTTagCompound();
		NBTTagCompound runewords = new NBTTagCompound();
		for (Map.Entry<Runeword, NBTTagCompound> e : map.entrySet()) {
			runewords.setTag(e.getKey().getRegistryName().toString(), e.getValue());
		}

		//noinspection ConstantConditions
		compound.setTag(TEMPORARY_RUNEWORD_DATA_TAG, runewords);
		sword.setTagCompound(compound);
	}

	// --------------------------  Runeword-specific stuff  --------------------------//
	// This must be here and cannot be outsourced to the runeword classes

	@Override
	public boolean canDisableShield(ItemStack stack, ItemStack shield, EntityLivingBase entity, EntityLivingBase attacker) {
		if (shield.getItem() instanceof ItemShield) {
			if (getRunewordCharges(stack, (Runeword) ASSpells.runeword_shatter) > 0) {
				spendCharge(stack, (Runeword) ASSpells.runeword_shatter, 1);
				return true;
			}
		}
		return false;
	}

	//--------------------------  IWorkbenchItem implementation  --------------------------//

	@Override
	public boolean showSpellHUD(EntityPlayer player, ItemStack stack) {
		return true;
	}

	@Override
	public int getSpellSlotCount(ItemStack stack) {
		return SWORD_BASE_SPELL_SLOTS + WandHelper.getUpgradeLevel(stack, WizardryItems.attunement_upgrade);
	}

	@Override
	public ItemStack applyUpgrade(@Nullable EntityPlayer player, ItemStack wand, ItemStack upgrade) {
		// Upgrades wand if necessary. Damage is copied, preserving remaining durability,
		// and also the entire NBT tag compound.
		if (WandHelper.isWandUpgrade(upgrade.getItem())) {

			// Special upgrades
			Item specialUpgrade = upgrade.getItem();

			int maxUpgrades = this.tier.upgradeLimit - 2;
			//			if(this.element == Element.MAGIC) maxUpgrades += Constants.NON_ELEMENTAL_UPGRADE_BONUS;

			if (WandHelper.getTotalUpgrades(wand) < maxUpgrades
					&& WandHelper.getUpgradeLevel(wand, specialUpgrade) < Constants.UPGRADE_STACK_LIMIT) {

				// Used to preserve existing mana when upgrading storage rather than creating free mana.
				int prevMana = this.getMana(wand);

				WandHelper.applyUpgrade(wand, specialUpgrade);

				// Special behaviours for specific upgrades
				if (specialUpgrade == WizardryItems.storage_upgrade) {

					this.setMana(wand, prevMana);

				} else if (specialUpgrade == WizardryItems.attunement_upgrade) {

					int newSlotCount = SWORD_BASE_SPELL_SLOTS + WandHelper.getUpgradeLevel(wand,
							WizardryItems.attunement_upgrade);

					Spell[] spells = WandHelper.getSpells(wand);
					Spell[] newSpells = new Spell[newSlotCount];

					for (int i = 0; i < newSpells.length; i++) {
						newSpells[i] = i < spells.length && spells[i] != null ? spells[i] : Spells.none;
					}

					WandHelper.setSpells(wand, newSpells);

					int[] cooldowns = WandHelper.getCooldowns(wand);
					int[] newCooldowns = new int[newSlotCount];

					if (cooldowns.length > 0) {
						System.arraycopy(cooldowns, 0, newCooldowns, 0, cooldowns.length);
					}

					WandHelper.setCooldowns(wand, newCooldowns);
				}

				upgrade.shrink(1);

				if (player != null) {

					WizardryAdvancementTriggers.special_upgrade.triggerFor(player);

					if (WandHelper.getTotalUpgrades(wand) == Tier.MASTER.upgradeLimit) {
						WizardryAdvancementTriggers.max_out_wand.triggerFor(player);
					}
				}

			}
		}

		return wand;
	}

	@Override
	public boolean onApplyButtonPressed(EntityPlayer player, Slot centre, Slot crystals, Slot upgrade, Slot[] spellBooks) {

		boolean changed = false; // Used for advancements

		if (upgrade.getHasStack()) {
			ItemStack original = centre.getStack().copy();
			centre.putStack(this.applyUpgrade(player, centre.getStack(), upgrade.getStack()));
			changed = !ItemStack.areItemStacksEqual(centre.getStack(), original);
		}

		// Reads NBT spell metadata array to variable, edits this, then writes it back to NBT.
		// Original spells are preserved; if a slot is left empty the existing spell binding will remain.
		// Accounts for spells which cannot be applied because they are above the sword's tier; these spells
		// will not bind but the existing spell in that slot will remain and other applicable spells will
		// be bound as normal, along with any upgrades and crystals.
		Spell[] spells = WandHelper.getSpells(centre.getStack());

		if (spells.length <= 0) {
			// Base value here because if the spell array doesn't exist, the sword can't possibly have attunement upgrades
			spells = new Spell[SWORD_BASE_SPELL_SLOTS];
		}

		for (int i = 0; i < spells.length; i++) {
			if (spellBooks[i].getStack() != ItemStack.EMPTY) {

				Spell spell = Spell.byMetadata(spellBooks[i].getStack().getItemDamage());
				// If the sword is powerful enough for the spell, it's not already bound to that slot and it's enabled for swords
				if (!(spell.getTier().level > this.tier.level) && spells[i] != spell && spell.isEnabled()) {
					spells[i] = spell;
					changed = true;
				}
			}
		}

		WandHelper.setSpells(centre.getStack(), spells);

		// Charges wand by appropriate amount
		if (hasManaStorage(centre.getStack()) && crystals.getStack() != ItemStack.EMPTY && !this.isManaFull(centre.getStack())) {

			int chargeDepleted = this.getManaCapacity(centre.getStack()) - this.getMana(centre.getStack());

			int manaPerItem = Constants.MANA_PER_CRYSTAL;
			if (crystals.getStack().getItem() == WizardryItems.crystal_shard) { manaPerItem = Constants.MANA_PER_SHARD; }
			if (crystals.getStack().getItem() == WizardryItems.grand_crystal) { manaPerItem = Constants.GRAND_CRYSTAL_MANA; }

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

	/**
	 * Returns whether the tooltip (dark grey box) should be drawn when this item is in an arcane workbench. Only
	 * called client-side.
	 *
	 * @param stack The ItemStack to query.
	 * @return True if the workbench tooltip should be shown, false if not.
	 */
	@Override
	public boolean showTooltip(ItemStack stack) { return true; }

	//--------------------------  IManaStoringItem implementation  --------------------------//

	@Override
	public int getMana(ItemStack stack) { return getManaCapacity(stack) - getDamage(stack); }

	@Override
	public void setMana(ItemStack stack, int mana) {
		// Using super (which can only be done from in here) bypasses the above override
		super.setDamage(stack, getManaCapacity(stack) - mana);
	}

	@Override
	public int getManaCapacity(ItemStack stack) {
		return this.getMaxDamage(stack);
	}

	@Override
	public boolean showManaInWorkbench(EntityPlayer player, ItemStack stack) {
		return hasManaStorage(stack);
	}

	@Override
	public void consumeMana(ItemStack stack, int mana, @Nullable EntityLivingBase wielder) {
		IManaStoringItem.super.consumeMana(stack, mana, wielder);
	}

	@Override
	public void rechargeMana(ItemStack stack, int mana) {
		IManaStoringItem.super.rechargeMana(stack, mana);
	}

	@Override
	public boolean isManaFull(ItemStack stack) {
		return IManaStoringItem.super.isManaFull(stack);
	}

	@Override
	public boolean isManaEmpty(ItemStack stack) {
		return IManaStoringItem.super.isManaEmpty(stack);
	}

	@Override
	public float getFullness(ItemStack stack) {
		return IManaStoringItem.super.getFullness(stack);
	}

	////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a SpellModifiers object with the appropriate modifiers applied for the given ItemStack and Spell.
	 * This is now public because artefacts use it
	 */
	public SpellModifiers calculateModifiers(ItemStack stack, EntityPlayer player, Spell spell) {

		EnumHand otherHand = getOtherHandForSword(stack, player);

		SpellModifiers modifiers = new SpellModifiers();

		// Now we only need to add multipliers if they are not 1.
		int level = WandHelper.getUpgradeLevel(stack, WizardryItems.range_upgrade);
		if (level > 0) { modifiers.set(WizardryItems.range_upgrade, 1.0f + level * Constants.RANGE_INCREASE_PER_LEVEL, true); }

		level = WandHelper.getUpgradeLevel(stack, WizardryItems.duration_upgrade);
		if (level > 0) { modifiers.set(WizardryItems.duration_upgrade, 1.0f + level * Constants.DURATION_INCREASE_PER_LEVEL, false); }

		level = WandHelper.getUpgradeLevel(stack, WizardryItems.blast_upgrade);
		if (level > 0) { modifiers.set(WizardryItems.blast_upgrade, 1.0f + level * Constants.BLAST_RADIUS_INCREASE_PER_LEVEL, true); }

		level = WandHelper.getUpgradeLevel(stack, WizardryItems.cooldown_upgrade);
		if (level > 0) { modifiers.set(WizardryItems.cooldown_upgrade, 1.0f - level * Constants.COOLDOWN_REDUCTION_PER_LEVEL, true); }

		////		float progressionModifier = 1.0f - ((float) WizardData.get(player).countRecentCasts(spell) / WizardData.MAX_RECENT_SPELLS)
		////				* MAX_PROGRESSION_REDUCTION;
		//
		//		Element element = getElement(stack);
		//
		//		if (element != Element.MAGIC && element == spell.getElement()) {
		//			modifiers.set(SpellModifiers.POTENCY, 1.0f + (this.tier.level + 1) * (Constants.POTENCY_INCREASE_PER_TIER / 3), true);
		//			progressionModifier *= ELEMENTAL_PROGRESSION_MODIFIER;
		//
		//		}
		//
		//		if (WizardData.get(player) != null) {
		//
		//			if (!WizardData.get(player).hasSpellBeenDiscovered(spell)) {
		//				// Casting an undiscovered spell now grants 5x progression
		//				progressionModifier *= DISCOVERY_PROGRESSION_MODIFIER;
		//			}
		//
		//			if (!WizardData.get(player).hasReachedTier(this.tier.next())) {
		//				// 1.5x progression for tiers that have already been reached
		//				progressionModifier *= SECOND_TIME_PROGRESSION_MODIFIER;
		//			}
		//		}

		//		modifiers.set(SpellModifiers.PROGRESSION, progressionModifier, false);

		return modifiers;
	}

	private static EnumHand getOtherHandForSword(ItemStack stack, EntityLivingBase entity) {
		return entity.getHeldItemMainhand().getItem() instanceof ItemBattlemageSword ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;
	}

	public static boolean hasManaStorage(ItemStack stack) {
		return WandHelper.getUpgradeLevel(stack, WizardryItems.storage_upgrade) > 0;
	}

	private static boolean hasManaAvailable(ItemStack stack) {
		return stack.hasTagCompound() && stack.getTagCompound().hasKey(MANA_AVAILABLE_TAG) && stack.getTagCompound().getBoolean(MANA_AVAILABLE_TAG);
	}

	/**
	 * Distributes the given cost (which should be the per-second cost of a continuous spell) over a second and
	 * returns the appropriate cost to be applied for the given tick. Currently the cost is distributed over 2
	 * intervals per second, meaning the returned value is 0 unless {@code castingTick} is a multiple of 10.
	 */
	protected static int getDistributedCost(int cost, int castingTick) {

		int partialCost;

		if (castingTick % 20 == 0) { // Whole number of seconds has elapsed
			partialCost = cost / 2 + cost % 2; // Make sure cost adds up to the correct value by adding the remainder here
		} else if (castingTick % 10 == 0) { // Something-and-a-half seconds has elapsed
			partialCost = cost / 2;
		} else { // Some other number of ticks has elapsed
			partialCost = 0; // Wands aren't damaged within half-seconds
		}

		return partialCost;
	}

	/**
	 * Gets the battlemage sword of the specificed tier
	 * @param tier to check
	 * @return item of the tier
	 */
	public static Item getSwordForTier(Tier tier) {
		if (tier == null) { throw new NullPointerException("The given tier cannot be null."); }

		if (tier == Tier.NOVICE) {
			return ASItems.battlemage_sword_novice;
		} else if (tier == Tier.APPRENTICE) {
			return ASItems.battlemage_sword_apprentice;
		} else if (tier == Tier.ADVANCED) {
			return ASItems.battlemage_sword_advanced;
		}
		return ASItems.battlemage_sword_master;
	}

	//--------------------------  Events  --------------------------//

	// hitEntity is only called server-side, so we'll have to use events
	// this event DOES NOT trigger when the conditions are met in electroblob.wizardry.item.ItemWand.onAttackEntityEvent
	@SubscribeEvent
	public static void onAttackEntityEvent(AttackEntityEvent event) {

		EntityPlayer player = event.getEntityPlayer();
		ItemStack stack = player.getHeldItemMainhand(); // Can't melee with offhand items

		if (stack.getItem() instanceof ItemBattlemageSword) {

			// Nobody said it had to be a wand, as long as it's got a melee upgrade it counts

			// mana hit effect
			if (hasManaAvailable(stack)) {

				Random random = player.world.rand;

				player.world.playSound(player.posX, player.posY, player.posZ, WizardrySounds.ITEM_WAND_MELEE, SoundCategory.PLAYERS, 0.75f, 1, false);

				if (player.world.isRemote) {

					Element element = WizardClassWeaponHelper.getElement(stack);

					switch (element) {
						case MAGIC: {
							Vec3d origin = new Vec3d(player.posX, player.posY + player.getEyeHeight() - 0.5, player.posZ);
							Vec3d hit = origin.add(player.getLookVec().scale(player.getDistance(event.getTarget())));
							// Generate two perpendicular vectors in the plane perpendicular to the look vec
							Vec3d vec1 = player.getLookVec().rotatePitch(90);
							Vec3d vec2 = player.getLookVec().crossProduct(vec1);

							for (int i = 0; i < 15; i++) {
								ParticleBuilder.create(ParticleBuilder.Type.SPARKLE).pos(hit)
										.vel(vec1.scale(random.nextFloat() * 0.3f - 0.15f).add(vec2.scale(random.nextFloat() * 0.3f - 0.15f)))
										.clr(1f, 1f, 1f).fade(0.3f, 0.5f, 1)
										.time(8 + random.nextInt(4)).spawn(player.world);
							}
							break;
						}
						case NECROMANCY: {
							Vec3d origin = player.getPositionEyes(1);
							Vec3d hit = origin.add(player.getLookVec().scale(player.getDistance(event.getTarget())));
							Vec3d vec1 = player.getLookVec().rotatePitch(90);
							Vec3d vec2 = player.getLookVec().crossProduct(vec1);

							for (int i = 0; i < 15; i++) {
								ParticleBuilder.create(ParticleBuilder.Type.SPARKLE).pos(hit)
										.vel(vec1.scale(random.nextFloat() * 0.3f - 0.15f).add(vec2.scale(random.nextFloat() * 0.3f - 0.15f)))
										.clr(0.5f, 0, 0)
										.time(8 + random.nextInt(4)).spawn(player.world);
							}
							break;
						}
						case LIGHTNING: {
							ParticleBuilder.spawnShockParticles(player.world, event.getTarget().posX, event.getTarget().posY + event.getTarget().height / 2, event.getTarget().posZ);
							List<EntityLivingBase> secondaryTargets = EntityUtils.getLivingWithinRadius(
									4, event.getTarget().posX, event.getTarget().posY + event.getTarget().height / 2,
									event.getTarget().posZ, player.world);

							secondaryTargets.stream()
									.filter(entity -> !entity.equals(event.getTarget()))
									.filter(EntityUtils::isLiving)
									.filter(e -> AllyDesignationSystem.isValidTarget(player, e))
									.limit(3)
									.forEach(secondaryTarget -> EnumElementalSwordEffect.electrocute(event.getTarget().world, player,
											event.getTarget().getPositionVector().add(0, event.getTarget().height / 2, 0),
											secondaryTarget,
											4,
											0));
							break;
						}
						case ICE: {
							Vec3d origin = player.getPositionEyes(1);
							Vec3d hit = origin.add(player.getLookVec().scale(player.getDistance(event.getTarget())));
							Vec3d vec1 = player.getLookVec().rotatePitch(90);
							Vec3d vec2 = player.getLookVec().crossProduct(vec1);

							for (int i = 0; i < 15; i++) {
								ParticleBuilder.create(ParticleBuilder.Type.SNOW).pos(hit)
										.vel(vec1.scale(random.nextFloat() * 0.3f - 0.15f).add(vec2.scale(random.nextFloat() * 0.3f - 0.15f)))
										.time(8 + random.nextInt(4)).spawn(player.world);
							}

							if (WizardClassWeaponHelper.isChargeFull(stack)) {

								double particleX, particleZ;

								for (int i = 0; i < 10; i++) {

									particleX = event.getTarget().posX - 1.0d + 2 * event.getTarget().world.rand.nextDouble();
									particleZ = event.getTarget().posZ - 1.0d + 2 * event.getTarget().world.rand.nextDouble();
									ParticleBuilder.create(ParticleBuilder.Type.ICE)
											.pos(particleX, event.getTarget().posY + 1, particleZ)
											.vel((particleX - event.getTarget().posX) * 0.3, 0, (particleZ - event.getTarget().posZ) * 0.3)
											.time(20)
											.spawn(event.getTarget().world);

								}

								for (int i = 0; i < 40; i++) {

									particleX = event.getTarget().posX - 1.0d + 2 * event.getTarget().world.rand.nextDouble();
									particleZ = event.getTarget().posZ - 1.0d + 2 * event.getTarget().world.rand.nextDouble();
									ParticleBuilder.create(ParticleBuilder.Type.SNOW)
											.pos(particleX, event.getTarget().posY + 1, particleZ)
											.vel((particleX - event.getTarget().posX) * 0.3, 0, (particleZ - event.getTarget().posZ) * 0.3)
											.time(20)
											.spawn(event.getTarget().world);

								}
							}
							break;
						}
					}
				}
			}
		}
	}

}
