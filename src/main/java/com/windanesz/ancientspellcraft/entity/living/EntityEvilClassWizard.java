package com.windanesz.ancientspellcraft.entity.living;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.entity.ICustomCooldown;
import com.windanesz.ancientspellcraft.entity.ai.EntityAIAttackSpellImproved;
import com.windanesz.ancientspellcraft.entity.ai.EntityAIBattlemageMelee;
import com.windanesz.ancientspellcraft.entity.ai.EntityAIBattlemageSpellcasting;
import com.windanesz.ancientspellcraft.item.ItemBattlemageSword;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftItems;
import electroblob.wizardry.constants.Element;
import electroblob.wizardry.constants.Tier;
import electroblob.wizardry.entity.living.EntityAIAttackSpell;
import electroblob.wizardry.entity.living.EntityEvilWizard;
import electroblob.wizardry.item.ItemWizardArmour;
import electroblob.wizardry.registry.Spells;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.InventoryUtils;
import electroblob.wizardry.util.NBTExtras;
import electroblob.wizardry.util.SpellModifiers;
import electroblob.wizardry.util.WandHelper;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import java.util.ArrayList;
import java.util.List;

public class EntityEvilClassWizard extends EntityEvilWizard implements ICustomCooldown, IArmourClassWizard {

	/**
	 * Decremented each tick while greater than 0. When a spell is cast, this is set to that spell's cooldown plus the
	 * base cooldown.
	 */

	/**
	 * The resource location for the evil wizard's loot table.
	 */
	private static final ResourceLocation BATTLEMAGE_LOOT_TABLE = new ResourceLocation(AncientSpellcraft.MODID, "entities/evil_battlemage");

	protected int cooldown;

	/**
	 * Data parameter for the wizard's armour class.
	 */
	private static final DataParameter<Integer> EVIL_WIZARD_ARMOUR_CLASS = EntityDataManager.createKey(EntityEvilClassWizard.class, DataSerializers.VARINT);

	// Field implementations
	private List<Spell> spells = new ArrayList<Spell>(4);

	private EntityAIAttackSpellImproved<EntityEvilClassWizard> spellCastingAIImproved = new EntityAIAttackSpellImproved<>(this, 0.5D, 14.0F, 30, 50);
	private final EntityAIBattlemageMelee entityAIBattlemageMelee = new EntityAIBattlemageMelee(this, 0.6D, false);
	private final EntityAIBattlemageSpellcasting entityAIBattlemageSpellcasting = new EntityAIBattlemageSpellcasting(this, 0.6D, 14.0F, 30, 50);

	public int getCooldown() { return cooldown; }

	public void setCooldown(int cooldown) { this.cooldown = cooldown; }

	@Override
	public int incrementCooldown() { return cooldown++; }

	@Override
	public int decrementCooldown() { return cooldown--; }

	public EntityEvilClassWizard(World world) {
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
		ResourceLocation loot = getArmourClass() == ItemWizardArmour.ArmourClass.BATTLEMAGE ? BATTLEMAGE_LOOT_TABLE : super.getLootTable();
		return loot;
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		this.dataManager.register(EVIL_WIZARD_ARMOUR_CLASS, 0);
	}

	@Override
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {

		livingdata = super.onInitialSpawn(difficulty, livingdata);

		if (rand.nextInt(10) > 2) {
			this.setElement(Element.values()[rand.nextInt(Element.values().length - 1) + 1]);
		} else {
			this.setElement(Element.MAGIC);
		}

		// FIXME: ugly hack which will break when we'll add more types!
		if (hasStructure) {
			this.setArmourClass(ItemWizardArmour.ArmourClass.BATTLEMAGE);
		} else {
			this.setArmourClass(ItemWizardArmour.ArmourClass.values()[rand.nextInt(ItemWizardArmour.ArmourClass.values().length - 1) + 1]);
		}

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
				spellCount = 9;
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
			ItemStack sword = new ItemStack(AncientSpellcraftItems.battlemage_sword_master);
			NBTTagCompound nbt = sword.getTagCompound();
			if (nbt == null) {
				nbt = new NBTTagCompound();
			}
			nbt.setString(ItemBattlemageSword.ELEMENT_TAG, element.name());
			sword.setTagCompound(nbt);

			this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, sword);
			this.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, wand);
			setAITask(ItemWizardArmour.ArmourClass.BATTLEMAGE);
		} else {
			this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, wand);
			setAITask(ItemWizardArmour.ArmourClass.WIZARD);
		}

		// Default chance is 0.085f, for reference.
		for (EntityEquipmentSlot slot : EntityEquipmentSlot.values()) { this.setDropChance(slot, 0.0f); }

		return livingdata;
	}

	private void setAITask(ItemWizardArmour.ArmourClass armourClass) {
		if (armourClass == ItemWizardArmour.ArmourClass.BATTLEMAGE) {
			this.tasks.taskEntries.removeIf(t -> t.action == spellCastingAIImproved);

		} else {
			this.tasks.taskEntries.removeIf(t -> t.action == entityAIBattlemageSpellcasting);
			this.tasks.taskEntries.removeIf(t -> t.action == entityAIBattlemageMelee);
		}
	}

	public ItemWizardArmour.ArmourClass getArmourClass() {
		return ItemWizardArmour.ArmourClass.values()[this.dataManager.get(EVIL_WIZARD_ARMOUR_CLASS)];
	}

	public void setArmourClass(ItemWizardArmour.ArmourClass armourClass) {
		this.dataManager.set(EVIL_WIZARD_ARMOUR_CLASS, armourClass.ordinal());
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
	public SpellModifiers getModifiers() {
		return this.getArmourClass() == ItemWizardArmour.ArmourClass.WARLOCK ? this.getWarlockSpellModifiers() : super.getModifiers();
	}

	private SpellModifiers getWarlockSpellModifiers() {
		SpellModifiers modifiers = new SpellModifiers();
		modifiers.set(SpellModifiers.POTENCY, 1.4f, false);
		return modifiers;
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
		ItemWizardArmour.ArmourClass armourClass = ItemWizardArmour.ArmourClass.values()[nbt.getInteger("armour_class")];
		this.setArmourClass(armourClass);
		setAITask(armourClass);

		// no wandering for wizards with a tent, or they'll never go back to it
		if (hasStructure && getArmourClass() == ItemWizardArmour.ArmourClass.BATTLEMAGE) {
			this.tasks.taskEntries.removeIf(t -> t.action instanceof EntityAIWander);
		}
	}
}
