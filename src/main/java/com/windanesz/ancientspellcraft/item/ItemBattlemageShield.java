package com.windanesz.ancientspellcraft.item;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.Settings;
import com.windanesz.ancientspellcraft.client.gui.GuiHandlerAS;
import com.windanesz.ancientspellcraft.registry.ASItems;
import com.windanesz.ancientspellcraft.registry.ASTabs;
import com.windanesz.ancientspellcraft.util.ASUtils;
import com.windanesz.ancientspellcraft.util.WizardArmourUtils;
import electroblob.wizardry.Wizardry;
import electroblob.wizardry.constants.Constants;
import electroblob.wizardry.constants.Element;
import electroblob.wizardry.constants.Tier;
import electroblob.wizardry.data.WizardData;
import electroblob.wizardry.event.SpellCastEvent;
import electroblob.wizardry.item.IManaStoringItem;
import electroblob.wizardry.item.ISpellCastingItem;
import electroblob.wizardry.item.IWorkbenchItem;
import electroblob.wizardry.item.ItemArtefact;
import electroblob.wizardry.item.ItemWizardArmour;
import electroblob.wizardry.packet.PacketCastSpell;
import electroblob.wizardry.packet.WizardryPacketHandler;
import electroblob.wizardry.registry.Spells;
import electroblob.wizardry.registry.WizardryAdvancementTriggers;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.registry.WizardryRecipes;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.SpellModifiers;
import electroblob.wizardry.util.WandHelper;
import net.minecraft.block.BlockDispenser;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.Slot;
import net.minecraft.item.EnumAction;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ItemBattlemageShield extends Item implements ISpellCastingItem, IManaStoringItem, IWorkbenchItem, IItemWithSlots {

	private static final UUID SHIELD_ARMOR_BONUS = UUID.fromString("2b5b1f50-b6b5-473a-a402-e45a24d83c13");
	private static final UUID SHIELD_ARMOR_PERCENT_BONUS = UUID.fromString("3c1acb30-dafb-4c27-a142-680f12ed9c36");
	private static final UUID SHIELD_TOUGHNESS_BONUS = UUID.fromString("84d9a7e1-5346-4073-98e9-0e04d6d28da5");


	public ItemBattlemageShield() {
		setFull3D();
		this.maxStackSize = 1;
		this.setCreativeTab(ASTabs.ANCIENTSPELLCRAFT_GEAR);
		this.setMaxDamage(1000);
		this.addPropertyOverride(new ResourceLocation("blocking"), new IItemPropertyGetter() {
			@SideOnly(Side.CLIENT)
			public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
				return entityIn != null && entityIn.isHandActive() && entityIn.getActiveItemStack() == stack ? 1.0F : 0.0F;
			}
		});
		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(this, ItemArmor.DISPENSER_BEHAVIOR);
		WizardryRecipes.addToManaFlaskCharging(this);
	}

	@Override
	public int getSpellSlotCount(ItemStack stack) {
		return 0;
	}

	@Override
	public boolean onApplyButtonPressed(EntityPlayer player, Slot centre, Slot crystals, Slot upgrade, Slot[] spellBooks) {
		boolean changed = false;

		if (upgrade.getHasStack()) {
			ItemStack original = centre.getStack().copy();
			centre.putStack(this.applyUpgrade(player, centre.getStack(), upgrade.getStack()));
			changed = !ItemStack.areItemStacksEqual(centre.getStack(), original);
		}


		// Charges the shield by appropriate amount. Taken mostly from ItemWizardArmour, thanks to Electroblob
		if (crystals.getStack() != ItemStack.EMPTY && !this.isManaFull(centre.getStack())) {

			int chargeDepleted = this.getManaCapacity(centre.getStack()) - this.getMana(centre.getStack());

			if (crystals.getStack().getCount() * Constants.MANA_PER_CRYSTAL < chargeDepleted) {
				// If there aren't enough crystals to fully charge the shield
				this.rechargeMana(centre.getStack(), crystals.getStack().getCount() * Constants.MANA_PER_CRYSTAL);
				crystals.decrStackSize(crystals.getStack().getCount());

			} else {
				// If there are excess crystals (or just enough)
				this.setMana(centre.getStack(), this.getManaCapacity(centre.getStack()));
				crystals.decrStackSize((int) Math.ceil(((double) chargeDepleted) / Constants.MANA_PER_CRYSTAL));
			}

			changed = true;
		}

		return changed;
	}

	public int getMaxDamage(ItemStack stack) {
		return (int)((float)super.getMaxDamage(stack) * (1.0F + 0.15F * (float)WandHelper.getUpgradeLevel(stack, WizardryItems.storage_upgrade)) + 0.5F);
	}

	public ItemStack applyUpgrade(@Nullable EntityPlayer player, ItemStack shield, ItemStack upgrade) {
		if (WandHelper.isWandUpgrade(upgrade.getItem())) {
			Item specialUpgrade = upgrade.getItem();

			if (specialUpgrade == WizardryItems.storage_upgrade || specialUpgrade == WizardryItems.siphon_upgrade || specialUpgrade == WizardryItems.condenser_upgrade) {

				int maxUpgrades = 9;
				if (WandHelper.getTotalUpgrades(shield) < maxUpgrades && WandHelper.getUpgradeLevel(shield, specialUpgrade) < 3) {
					int prevMana = this.getMana(shield);
					WandHelper.applyUpgrade(shield, specialUpgrade);
					if (specialUpgrade == WizardryItems.storage_upgrade) {
						this.setMana(shield, prevMana);
					}

					upgrade.shrink(1);
					if (player != null) {
						WizardryAdvancementTriggers.special_upgrade.triggerFor(player);
					}
				}
			}

		}
		return shield;
	}

	@Override
	public boolean showTooltip(ItemStack stack) {
		return true;
	}

	@Override
	public int getMana(ItemStack stack) {
		return getManaCapacity(stack) - getDamage(stack);
	}

	@Override
	public void setDamage(ItemStack stack, int damage) {
		// Overridden to do nothing to stop repair things from 'repairing' the mana in a wand
	}

	@Override
	public void setMana(ItemStack stack, int mana) {
		// Using super (which can only be done from in here) bypasses the above override
		super.setDamage(stack, getManaCapacity(stack) - mana);
	}

	@Override
	public int getManaCapacity(ItemStack stack) {
		return this.getMaxDamage(stack);
	}

	/**
	 * allows items to add custom lines of information to the mouseover description
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, net.minecraft.client.util.ITooltipFlag advanced) {
		Wizardry.proxy.addMultiLineDescription(tooltip, "item." + this.getRegistryName() + ".desc");

		if (advanced.isAdvanced()) {
			// Advanced tooltips for debugging
			// current mana
			tooltip.add("\u00A79" + net.minecraft.client.resources.I18n.format("item." + Wizardry.MODID + ":wand.mana",
					this.getMana(stack), this.getManaCapacity(stack)));

		}
	}

	/**
	 * returns the action that specifies what animation to play when the items is being used
	 */
	public EnumAction getItemUseAction(ItemStack stack) {
		return EnumAction.BLOCK;
	}

	/**
	 * How long it takes to use or consume an item
	 */
	public int getMaxItemUseDuration(ItemStack stack) {
		return 20000;
	}

	/**
	 * Called when the equipped item is right-clicked.
	 */
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer player, EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);

		if (!isBattlemage(player)) {
			ASUtils.sendMessage(player, "Only a battlemage can use this", false);
			return new ActionResult<>(EnumActionResult.FAIL, stack);
		}

		if (hand == EnumHand.MAIN_HAND && player.isSneaking()) {
			// open hud
			player.openGui(AncientSpellcraft.instance, GuiHandlerAS.BATTLEMAGE_SHIELD, player.world, 0, 0, 0);
			return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
		}

		if (isManaEmpty(stack)) {
			return new ActionResult<>(EnumActionResult.FAIL, stack);
		}

		//		Spell spell = Spells.shield;
		//		SpellModifiers modifiers = new SpellModifiers();
		//		modifiers.set("potency", 2.f, false);
		//
		//		cast(stack, spell, player, hand, 0, modifiers);
		//		if (!player.isHandActive()) {
		player.setActiveHand(hand);
		return new ActionResult<>(EnumActionResult.SUCCESS, stack);
		//		}

		//return new ActionResult<>(EnumActionResult.FAIL, stack);
	}

	@Override
	public boolean canContinueUsing(ItemStack oldStack, ItemStack newStack) {
		// Ignore durability changes
		if (ItemStack.areItemsEqualIgnoreDurability(oldStack, newStack)) {return true;}
		return super.canContinueUsing(oldStack, newStack);
	}

	@Override
	public void onUsingTick(ItemStack stack, EntityLivingBase user, int count) {
		if (user instanceof EntityPlayer) {
			if (count % 20 == 0) { // every sec
				consumeMana(stack, 3, user);
				if (isManaEmpty(stack)) {
					user.stopActiveHand();
				}
			}

		}
		super.onUsingTick(stack, user, count);
	}

	public boolean isBattlemage(EntityPlayer player) {
		return WizardArmourUtils.isWearingFullSet(player, null, ItemWizardArmour.ArmourClass.BATTLEMAGE);
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		return super.getItemStackDisplayName(stack);
	}

	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		if (!oldStack.isEmpty() || !newStack.isEmpty()) {
			// We only care about the situation where we specifically want the animation NOT to play.
			if (oldStack.getItem() == newStack.getItem() && !slotChanged) {
				return false;
			}
		}

		return super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged);
	}

	@Override
	public boolean cast(ItemStack stack, Spell spell, EntityPlayer caster, EnumHand hand, int castingTick, SpellModifiers modifiers) {

		World world = caster.world;

		if (world.isRemote && !spell.isContinuous && spell.requiresPacket()) {return false;}

		if (spell.cast(world, caster, hand, castingTick, modifiers)) {

			if (castingTick == 0) {MinecraftForge.EVENT_BUS.post(new SpellCastEvent.Post(SpellCastEvent.Source.OTHER, spell, caster, modifiers));}

			if (!world.isRemote) {

				// Continuous spells never require packets so don't rely on the requiresPacket method to specify it
				if (!spell.isContinuous && spell.requiresPacket()) {
					// Sends a packet to all players in dimension to tell them to spawn particles.
					IMessage msg = new PacketCastSpell.Message(caster.getEntityId(), hand, spell, modifiers);
					WizardryPacketHandler.net.sendToDimension(msg, world.provider.getDimension());
				}
			}
			return true;
		}
		return false;
	}

	@Nonnull
	@Override
	public Spell getCurrentSpell(ItemStack stack) {
		return Spells.shield;
	}

	@Override
	public boolean showSpellHUD(EntityPlayer player, ItemStack stack) {
		return false;
	}

	@Override
	public boolean canCast(ItemStack stack, Spell spell, EntityPlayer caster, EnumHand hand, int castingTick, SpellModifiers modifiers) {
		//unused
		return true;
	}

	@Override
	public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot slot)     {
		Multimap<String, AttributeModifier> multimap = super.getItemAttributeModifiers(slot);
		if (slot == EntityEquipmentSlot.OFFHAND)
		{
			multimap.put(SharedMonsterAttributes.ARMOR.getName(), new AttributeModifier(SHIELD_ARMOR_BONUS, "Armor modifier",  Settings.generalSettings.runic_shield_armor, 0));
			//multimap.put(SharedMonsterAttributes.ARMOR.getName(), new AttributeModifier(SHIELD_ARMOR_PERCENT_BONUS, "Armor modifier", 0.2, 1));
			multimap.put(SharedMonsterAttributes.ARMOR_TOUGHNESS.getName(), new AttributeModifier(SHIELD_TOUGHNESS_BONUS, "Armor toughness", Settings.generalSettings.runic_shield_armor_toughness, 0));
			//multimap.put(SharedMonsterAttributes.ARMOR_TOUGHNESS.getName(), new AttributeModifier(ARMOR_MODIFIERS[equipmentSlot.getIndex()], "Armor toughness", (double)this.toughness, 0));
		}

		return multimap;
	}


	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
		super.onUpdate(stack, world, entity, itemSlot, isSelected);

		if (entity instanceof EntityPlayer && isBattlemage((EntityPlayer) entity)) {

			EntityPlayer player = (EntityPlayer) entity;

			if (!ItemStack.areItemStacksEqual(player.getHeldItemOffhand(), stack)) {
				return;
			}

			if (stack.getItem() instanceof ItemBattlemageShield && stack.hasTagCompound()) {
				NBTTagCompound nbt = stack.getTagCompound();
				if (nbt.hasKey("Items")) {
					int count = stack.getTagCompound().getTagList("Items", 10).tagCount();

					for (int i = 0; i < count; i++) {
						tickArtefact(new ItemStack(stack.getTagCompound().getTagList("Items", 10).getCompoundTagAt(i)), player);
					}
				}
			}

			if (!world.isRemote && !this.isManaFull(stack) && world.getTotalWorldTime() % 50L == 0L) {
				this.rechargeMana(stack, WandHelper.getUpgradeLevel(stack, WizardryItems.condenser_upgrade));
			}

			if (!world.isRemote && player.getCooldownTracker().hasCooldown(stack.getItem()) && ItemArtefact.isArtefactActive(player, ASItems.charm_glyph_shield_disable)) {
				player.getCooldownTracker().removeCooldown(this);
			}
		}

	}

	public static List<ItemStack> getArtefacts(ItemStack shield) {
		List<ItemStack> artefacts = new ArrayList<>();
		if (shield.getItem() instanceof ItemBattlemageShield && shield.hasTagCompound()) {
			NBTTagCompound nbt = shield.getTagCompound();
			if (nbt.hasKey("Items")) {
				int count = shield.getTagCompound().getTagList("Items", 10).tagCount();

				for (int i = 0; i < count; i++) {
					artefacts.add(new ItemStack(shield.getTagCompound().getTagList("Items", 10).getCompoundTagAt(i)));
				}
			}
		}
		return artefacts;
	}

	public void tickArtefact(ItemStack stack, EntityPlayer player) {
		if (stack.getItem() instanceof ITickableArtefact) {
			((ITickableArtefact) stack.getItem()).onWornTick(stack, player);
		}
	}

	@Override
	public boolean isEnchantable(ItemStack stack) {
		return false;
	}

	public int getItemEnchantability() {
		return 0;
	}

	@Override
	public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
		return false;
	}

	/**
	 * Return whether this item is repairable in an anvil.
	 */
	public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
		return false;
	}

	@Override
	public boolean isShield(ItemStack stack, @Nullable EntityLivingBase entity) {
		return true;
	}

	@Override
	public int getSlotCount() {
		return 3;
	}

	@Override
	public boolean hasGUI() {
		return true;
	}

	@Override
	public int getRowCount() {
		return 1;
	}

	@Override
	public int getColumnCount() {
		return 3;
	}

	@Override
	public boolean isItemValid(Item item) {
		return false;
	}
}
