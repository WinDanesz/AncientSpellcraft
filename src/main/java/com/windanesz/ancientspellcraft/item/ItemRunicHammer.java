package com.windanesz.ancientspellcraft.item;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.Settings;
import com.windanesz.ancientspellcraft.registry.ASItems;
import com.windanesz.ancientspellcraft.registry.ASSpells;
import com.windanesz.ancientspellcraft.registry.ASTabs;
import com.windanesz.ancientspellcraft.spell.IRunicHammerSpell;
import electroblob.wizardry.Wizardry;
import electroblob.wizardry.constants.Constants;
import electroblob.wizardry.data.IStoredVariable;
import electroblob.wizardry.data.Persistence;
import electroblob.wizardry.data.SpellGlyphData;
import electroblob.wizardry.data.WizardData;
import electroblob.wizardry.event.SpellCastEvent;
import electroblob.wizardry.item.IManaStoringItem;
import electroblob.wizardry.item.ISpellCastingItem;
import electroblob.wizardry.item.IWorkbenchItem;
import electroblob.wizardry.item.ItemWand;
import electroblob.wizardry.packet.PacketCastSpell;
import electroblob.wizardry.packet.WizardryPacketHandler;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.registry.WizardryRecipes;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.SpellModifiers;
import electroblob.wizardry.util.WandHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

import static com.windanesz.ancientspellcraft.item.WizardClassWeaponHelper.getDistributedCost;

@Mod.EventBusSubscriber
public class ItemRunicHammer extends Item implements ISpellCastingItem, IWorkbenchItem, IWizardClassWeapon, IManaStoringItem {

	/**
	 * The number of spell slots a tome has with no attunement upgrades applied.
	 */
	public static final int TOME_BASE_SPELL_SLOTS = 5;
	public static final IStoredVariable<BlockPos> LAST_POS = IStoredVariable.StoredVariable.ofBlockPos("lastPlayerPos", Persistence.NEVER);
	private static final String MANA_AVAILABLE_TAG = "mana_available";

	public ItemRunicHammer() {
		super();
		setCreativeTab(ASTabs.ANCIENTSPELLCRAFT_GEAR);
		setMaxDamage(800);
		setMaxStackSize(1);
		// TODO: expose to settings
		WizardryRecipes.addToManaFlaskCharging(this);
	}

	private static EnumHand getOtherHandForSword(ItemStack stack, EntityLivingBase entity) {
		return entity.getHeldItemMainhand().getItem() instanceof ItemRunicHammer ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;
	}

	@Override
	public boolean hasEffect(ItemStack stack) {
		return false;
	}

	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean isHeld) {
		WandHelper.decrementCooldowns(stack);

		// Decrements tome damage (increases mana) every 1.5 seconds if it has a condenser upgrade
		if(!world.isRemote && !this.isManaFull(stack) && world.getTotalWorldTime() % Constants.CONDENSER_TICK_INTERVAL == 0){
			// If the upgrade level is 0, this does nothing anyway.
			this.rechargeMana(stack, WandHelper.getUpgradeLevel(stack, WizardryItems.condenser_upgrade));
		}
	}

	// Max damage is modifiable with upgrades.
	@Override
	public int getMaxDamage(ItemStack stack) {
		// + 0.5f corrects small float errors rounding down
		return (int) (super.getMaxDamage(stack) * (1.0f + Constants.STORAGE_INCREASE_PER_LEVEL
				* WandHelper.getUpgradeLevel(stack, WizardryItems.storage_upgrade)) + 0.5f);
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
	public boolean canContinueUsing(ItemStack oldStack, ItemStack newStack) {
		// Ignore durability changes
		if (ItemStack.areItemsEqualIgnoreDurability(oldStack, newStack)) { return true; }
		return super.canContinueUsing(oldStack, newStack);
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
		if (!oldStack.isEmpty() || !newStack.isEmpty() && oldStack.getItem() == newStack.getItem() && !slotChanged && oldStack.getItem() instanceof ItemRunicHammer
				&& newStack.getItem() instanceof ItemRunicHammer) { return false; }

		return super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, World world, List<String> text, net.minecraft.client.util.ITooltipFlag advanced) {

		EntityPlayer player = Minecraft.getMinecraft().player;
		if (player == null) { return; }

		//text.add(Wizardry.proxy.translate("item.ancientspellcraft:battlemage_sword_armour_requirements_tooltip"));

		//Element element = WizardArmourUtils.getFullSetElementForClass(player, ItemWizardArmour.ArmourClass.BATTLEMAGE);

		if (WandHelper.getUpgradeLevel(stack, ASItems.empowerment_upgrade) > 0) {
			// +0.5f is necessary due to the error in the way floats are calculated.
			text.add(Wizardry.proxy.translate("item." + Wizardry.MODID + ":wand.buff",
					new Style().setColor(TextFormatting.DARK_GRAY),
					(int) (WandHelper.getUpgradeLevel(stack, ASItems.empowerment_upgrade) * (Settings.generalSettings.empowerment_upgrade_potency_gain) * 100 + 0.5f),
					Wizardry.proxy.translate("item.ancientspellcraft:all_other_elements")));
		}

		Spell spell = WandHelper.getCurrentSpell(stack);

		boolean discovered = !Wizardry.settings.discoveryMode || player.isCreative() || WizardData.get(player) == null
				|| WizardData.get(player).hasSpellBeenDiscovered(spell);

		text.add(Wizardry.proxy.translate("item." + Wizardry.MODID + ":wand.spell", new Style().setColor(TextFormatting.GRAY),
				discovered ? spell.getDisplayNameWithFormatting() : "#" + TextFormatting.BLUE + SpellGlyphData.getGlyphName(spell, player.world)));

		if (GameSettings.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindSneak)) {
			AncientSpellcraft.proxy.addMultiLineDescription(text, I18n.format("tooltip.ancientspellcraft:sage_tome.more_info"));
		} else {
			text.add(I18n.format("tooltip.ancientspellcraft:more_info"));
		}

		if (advanced.isAdvanced()) {

			// show innate mana
			text.add(Wizardry.proxy.translate("item." + Wizardry.MODID + ":wand.mana", new Style().setColor(TextFormatting.BLUE),
					this.getMana(stack), this.getManaCapacity(stack)));
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
					if (spell == ASSpells.awaken_tome) {
						stack = player.getHeldItem(hand);
					}
					return new ActionResult<>(EnumActionResult.SUCCESS, stack);
				}
			}
		}

		return new ActionResult<>(EnumActionResult.FAIL, stack);
	}

	//--------------------------  ISpellCastingItem implementation  --------------------------//

	// For continuous spells and spells with a charge-up time. The count argument actually decrements by 1 each tick.
	// N.B. The first time this gets called is the tick AFTER onItemRightClick is called, not the same tick
	@Override
	public void onUsingTick(ItemStack stack, EntityLivingBase user, int count) {

		if (user instanceof EntityPlayer) {

			EntityPlayer player = (EntityPlayer) user;

			Spell spell = WandHelper.getCurrentSpell(stack);

			SpellModifiers modifiers;

			if (WizardData.get(player) != null) {
				modifiers = WizardData.get(player).itemCastingModifiers;
			} else {
				modifiers = this.calculateModifiers(stack, (EntityPlayer) user, spell); // Fallback to the old way, should never be used
			}

			int useTick = stack.getMaxItemUseDuration() - count;
			int chargeup = (int) (spell.getChargeup() * modifiers.get(SpellModifiers.CHARGEUP));

			if (spell.isContinuous) {
				// Continuous spell charge-up is simple, just don't do anything until it's charged
				if (useTick >= chargeup) {
					// castingTick needs to be relative to when the spell actually started
					int castingTick = useTick - chargeup;
					// Continuous spells (these must check if they can be cast each tick since the mana changes)
					// Don't call canCast when castingTick == 0 because we already did it in onItemRightClick - even
					// with charge-up times, because we don't want to trigger events twice
					if (castingTick == 0 || canCast(stack, spell, player, player.getActiveHand(), castingTick, modifiers)) {
						cast(stack, spell, player, player.getActiveHand(), castingTick, modifiers);
					} else {
						// Stops the casting if it was interrupted, either by events or because the wand ran out of mana
						player.stopActiveHand();
					}
				}
			} else {
				// Non-continuous spells need to check they actually have a charge-up since ALL spells call setActiveHand
				if (chargeup > 0 && useTick == chargeup) {
					// Once the spell is charged, it's exactly the same as in onItemRightClick
					cast(stack, spell, player, player.getActiveHand(), 0, modifiers);
				}
			}
		}
	}

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
		// Spells can only be cast if the casting events aren't cancelled...
		if (castingTick == 0) {
			if (MinecraftForge.EVENT_BUS.post(new SpellCastEvent.Pre(AncientSpellcraft.SAGE_ITEM, spell, caster, modifiers))) { return false; }
		} else {
			if (MinecraftForge.EVENT_BUS.post(new SpellCastEvent.Tick(AncientSpellcraft.SAGE_ITEM, spell, caster, modifiers, castingTick))) { return false; }
		}

		int cost = (int) (spell.getCost() * modifiers.get(SpellModifiers.COST) + 0.1f); // Weird floaty rounding

		// As of wizardry 4.2 mana cost is only divided over two intervals each second
		if (spell.isContinuous) { cost = getDistributedCost(cost, castingTick); }

		// ...and the wand has enough mana to cast the spell...
		return cost <= this.getMana(stack) // This comes first because it changes over time
				// ...and the wand is the same tier as the spell or higher...
				// ...and either the spell is not in cooldown or the player is in creative mode
				&& (WandHelper.getCurrentCooldown(stack) == 0 || caster.isCreative());
	}

	//--------------------------  IWorkbenchItem implementation  --------------------------//

	@Override
	public boolean cast(ItemStack stack, Spell spell, EntityPlayer caster, EnumHand hand, int castingTick, SpellModifiers modifiers) {

		EnumHand otherHand = hand == EnumHand.MAIN_HAND ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;

		World world = caster.world;

		if (world.isRemote && !spell.isContinuous && spell.requiresPacket()) { return false; }

		if (spell.cast(world, caster, hand, castingTick, modifiers)) {

			if (castingTick == 0) { MinecraftForge.EVENT_BUS.post(new SpellCastEvent.Post(AncientSpellcraft.SAGE_ITEM, spell, caster, modifiers)); }

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
			return true;
		}

		return false;
	}

	@Override
	public void onPlayerStoppedUsing(ItemStack stack, World world, EntityLivingBase user, int timeLeft) {

		if (user instanceof EntityPlayer) {

			EntityPlayer player = (EntityPlayer) user;

			Spell spell = WandHelper.getCurrentSpell(stack);

			SpellModifiers modifiers;

			if (WizardData.get(player) != null) {
				modifiers = WizardData.get(player).itemCastingModifiers;
			} else {
				modifiers = this.calculateModifiers(stack, (EntityPlayer) user, spell); // Fallback to the old way, should never be used
			}

			int castingTick = stack.getMaxItemUseDuration() - timeLeft; // Might as well include this

			int cost = getDistributedCost((int) (spell.getCost() * modifiers.get(SpellModifiers.COST) + 0.1f), castingTick);

			// Still need to check there's enough mana or the spell will finish twice, since running out of mana is
			// handled separately.
			if (spell.isContinuous && cost <= this.getMana(stack)) {

				MinecraftForge.EVENT_BUS.post(new SpellCastEvent.Finish(AncientSpellcraft.SAGE_ITEM, spell, player, modifiers, castingTick));
				spell.finishCasting(world, player, Double.NaN, Double.NaN, Double.NaN, null, castingTick, modifiers);

				if (!player.isCreative()) { // Spells only have a cooldown in survival
					WandHelper.setCurrentCooldown(stack, (int) (spell.getCooldown() * modifiers.get(WizardryItems.cooldown_upgrade)));
				}
			}
		}
	}

	@Override
	public boolean showSpellHUD(EntityPlayer player, ItemStack stack) {
		return true;
	}

	@Override
	public int getSpellSlotCount(ItemStack stack) {
		return TOME_BASE_SPELL_SLOTS + WandHelper.getUpgradeLevel(stack, WizardryItems.attunement_upgrade);
	}

	@Override
	public ItemStack applyUpgrade(@Nullable EntityPlayer player, ItemStack wand, ItemStack upgrade) {
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
		// Accounts for spells which cannot be applied because they are above the wand's tier; these spells
		// will not bind but the existing spell in that slot will remain and other applicable spells will
		// be bound as normal, along with any upgrades and crystals.
		Spell[] spells = WandHelper.getSpells(centre.getStack());

		if (spells.length <= 0) {
			// Base value here because if the spell array doesn't exist, the wand can't possibly have attunement upgrades
			spells = new Spell[ItemWand.BASE_SPELL_SLOTS];
		}

		for (int i = 0; i < spells.length; i++) {
			if (spellBooks[i].getStack() != ItemStack.EMPTY) {

				Spell spell = Spell.byMetadata(spellBooks[i].getStack().getItemDamage());
				// If the wand is powerful enough for the spell, it's not already bound to that slot and it's enabled for wands
				if (spells[i] != spell && spell instanceof IRunicHammerSpell) {
					spells[i] = spell;
					changed = true;

				}
			}
		}

		WandHelper.setSpells(centre.getStack(), spells);

		// Charges wand by appropriate amount
		if (crystals.getStack() != ItemStack.EMPTY && !this.isManaFull(centre.getStack())) {

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

	//--------------------------  IManaStoringItem implementation  --------------------------//

	/**
	 * Returns whether the tooltip (dark grey box) should be drawn when this item is in an arcane workbench. Only
	 * called client-side.
	 *
	 * @param stack The ItemStack to query.
	 * @return True if the workbench tooltip should be shown, false if not.
	 */
	@Override
	public boolean showTooltip(ItemStack stack) { return true; }

	@Override
	public int getMana(ItemStack stack) { return getManaCapacity(stack) - getDamage(stack); }

	@Override
	public void setMana(ItemStack stack, int mana) {
		// Using super (which can only be done from in here) bypasses the above override
		super.setDamage(stack, getManaCapacity(stack) - mana);
	}

	@Override
	public int getManaCapacity(ItemStack stack) { return this.getMaxDamage(stack); }

	@Override
	public boolean showManaInWorkbench(EntityPlayer player, ItemStack stack) {
		return true;
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

	//--------------------------  Helper methods  --------------------------//

	@Override
	public float getFullness(ItemStack stack) {
		return IManaStoringItem.super.getFullness(stack);
	}

	/**
	 * Returns a SpellModifiers object with the appropriate modifiers applied for the given ItemStack and Spell.
	 * This is now public because artefacts use it
	 */
	/**
	 * Returns a SpellModifiers object with the appropriate modifiers applied for the given ItemStack and Spell.
	 */
	// This is now public because artefacts use it
	public SpellModifiers calculateModifiers(ItemStack stack, EntityPlayer player, Spell spell) {

		SpellModifiers modifiers = new SpellModifiers();

		return modifiers;
	}
}

