package com.windanesz.ancientspellcraft.entity.living;

import com.windanesz.ancientspellcraft.entity.ai.EntityAIAttackSpellImproved;
import electroblob.wizardry.Wizardry;
import electroblob.wizardry.constants.Element;
import electroblob.wizardry.constants.Tier;
import electroblob.wizardry.entity.living.EntityAIAttackSpell;
import electroblob.wizardry.item.ItemSpellBook;
import electroblob.wizardry.item.ItemWizardArmour;
import electroblob.wizardry.registry.Spells;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.InventoryUtils;
import electroblob.wizardry.util.NBTExtras;
import electroblob.wizardry.util.WandHelper;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import java.util.ArrayList;
import java.util.List;

public class EntityClassWizard extends EntityWizardMerchant implements IArmourClassWizard {

	/**
	 * Data parameter for the wizard's element.
	 */
	private static final DataParameter<Integer> ARMOUR_CLASS = EntityDataManager.createKey(EntityClassWizard.class, DataSerializers.VARINT);

	// Field implementations
	private List<Spell> spells = new ArrayList<Spell>(4);

	private final EntityAIAttackSpellImproved<EntityClassWizard> spellCastingAIImproved = new EntityAIAttackSpellImproved<>(this, 0.5D, 14.0F, 20, 50);

	public EntityClassWizard(World world) {
		super(world);
		this.tasks.taskEntries.removeIf(t -> t.action instanceof EntityAIAttackSpell);
		this.tasks.addTask(3, this.spellCastingAIImproved);

		// prevents despawning
		lifetime = -1;
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		List<EntityDataManager.DataEntry<?>> list = this.dataManager.getAll();
		this.dataManager.register(ARMOUR_CLASS, 0);
	}

	@Override
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {

		livingdata = super.onInitialSpawn(difficulty, livingdata);

		if (rand.nextInt(10) > 2) {
			this.setElement(Element.values()[rand.nextInt(Element.values().length - 1) + 1]);
		} else {
			this.setElement(Element.MAGIC);
		}

		this.setArmourClass(ItemWizardArmour.ArmourClass.values()[rand.nextInt(ItemWizardArmour.ArmourClass.values().length - 1) + 1]);

		Element element = this.getElement();

		// Adds armour.
		for (EntityEquipmentSlot slot : InventoryUtils.ARMOUR_SLOTS) {
			this.setItemStackToSlot(slot, new ItemStack(ItemWizardArmour.getArmour(element, this.getArmourClass(), slot)));
		}

		// Default chance is 0.085f, for reference.
		for (EntityEquipmentSlot slot : EntityEquipmentSlot.values()) { this.setDropChance(slot, 0.0f); }

		// All wizards know magic missile, even if it is disabled.

		spells.add(Spells.magic_missile);

		int spellCount;
		switch (this.getArmourClass()) {
			case SAGE:
				spellCount = 8;
			case WARLOCK:
				spellCount = 6;
			default: // BATTLEMAGE
				spellCount = 4;
		}

		Tier maxTier = IArmourClassWizard.populateSpells(this, spells, element, this.getArmourClass() == ItemWizardArmour.ArmourClass.SAGE, spellCount, rand);

		// Now done after the spells so it can take the tier into account.
		ItemStack wand = new ItemStack(WizardryItems.getWand(maxTier, element));
		ArrayList<Spell> list = new ArrayList<>(spells);
		list.add(Spells.heal);
		WandHelper.setSpells(wand, list.toArray(new Spell[5]));

		if (getArmourClass() == ItemWizardArmour.ArmourClass.BATTLEMAGE) {
			ItemStack sword = new ItemStack(Items.GOLDEN_SWORD);
			sword.addEnchantment(Enchantments.SHARPNESS, 1);
			this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, sword);
			this.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, wand);
		} else {
			this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, wand);
		}
		return livingdata;
	}

	public ItemWizardArmour.ArmourClass getArmourClass() {
		return ItemWizardArmour.ArmourClass.values()[this.dataManager.get(ARMOUR_CLASS)];
	}

	public void setArmourClass(ItemWizardArmour.ArmourClass armourClass) {
		this.dataManager.set(ARMOUR_CLASS, armourClass.ordinal());
	}

	@Override
	public List<Spell> getSpells() { return spells; }

	@Override
	public ITextComponent getDisplayName() {
		if (this.hasCustomName()) {
			TextComponentString textcomponentstring = new TextComponentString(ScorePlayerTeam.formatPlayerName(this.getTeam(), this.getName()));
			textcomponentstring.getStyle().setHoverEvent(this.getHoverEvent());
			textcomponentstring.getStyle().setInsertion(this.getCachedUniqueIdString());
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
	public boolean processInteract(EntityPlayer player, EnumHand hand) {

		ItemStack stack = player.getHeldItem(hand);

		// Debugging
		// player.addChatComponentMessage(new TextComponentTranslation("wizard.debug",
		// Spell.get(spells[1]).getDisplayName(), Spell.get(spells[2]).getDisplayName(),
		// Spell.get(spells[3]).getDisplayName()));

		// When right-clicked with a spell book in creative, sets one of the spells to that spell
		if (player.isCreative() && stack.getItem() instanceof ItemSpellBook) {
			Spell spell = Spell.byMetadata(stack.getItemDamage());
			if (this.spells.size() >= 4 && spell.canBeCastBy(this, true)) {
				// The set(...) method returns the element that was replaced - neat!
				player.sendMessage(new TextComponentTranslation("item." + Wizardry.MODID + ":spell_book.apply_to_wizard",
						this.getDisplayName(), this.spells.set(rand.nextInt(3) + 1, spell).getNameForTranslationFormatted(),
						spell.getNameForTranslationFormatted()));
				return true;
			}
		}

		// Won't trade with a player that has attacked them.
		if (this.isEntityAlive() && !this.isTrading() && !this.isChild() && !player.isSneaking()
				&& this.getAttackTarget() != player) {
			if (!this.world.isRemote) {
				this.setCustomer(player);
				player.displayVillagerTradeGui(this);
				// player.displayGUIMerchant(this, this.getElement().getWizardName());
			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {

		super.writeEntityToNBT(nbt);
		NBTExtras.storeTagSafely(nbt, "spells", NBTExtras.listToNBT(spells, spell -> new NBTTagInt(spell.metadata())));
		nbt.setInteger("armour_class", this.getArmourClass().ordinal());
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {

		super.readEntityFromNBT(nbt);
		this.spells = (List<Spell>) NBTExtras.NBTToList(nbt.getTagList("spells", Constants.NBT.TAG_INT),
				(NBTTagInt tag) -> Spell.byMetadata(tag.getInt()));
		this.setArmourClass(ItemWizardArmour.ArmourClass.values()[nbt.getInteger("armour_class")]);
	}

}
