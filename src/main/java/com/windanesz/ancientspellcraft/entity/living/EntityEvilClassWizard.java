package com.windanesz.ancientspellcraft.entity.living;

import com.windanesz.ancientspellcraft.entity.ai.EntityAIAttackSpellImproved;
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
import electroblob.wizardry.util.SpellProperties;
import electroblob.wizardry.util.WandHelper;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IEntityLivingData;
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
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EntityEvilClassWizard extends EntityEvilWizard {

	/**
	 * Data parameter for the wizard's armour class.
	 */
	private static final DataParameter<Integer> ARMOUR_CLASS = EntityDataManager.createKey(EntityClassWizard.class, DataSerializers.VARINT);

	// Field implementations
	private List<Spell> spells = new ArrayList<Spell>(4);

	private EntityAIAttackSpellImproved<EntityEvilClassWizard> spellCastingAIImproved = new EntityAIAttackSpellImproved<>(this, 0.5D, 14.0F, 30, 50);

	public EntityEvilClassWizard(World world) {
		super(world);
		this.tasks.taskEntries.removeIf(t -> t.action instanceof EntityAIAttackSpell);
		this.tasks.addTask(3, this.spellCastingAIImproved);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		this.dataManager.register(ARMOUR_CLASS, ItemWizardArmour.ArmourClass.WIZARD.ordinal());
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

		Tier maxTier = populateSpells(this, spells, element, this.getArmourClass() == ItemWizardArmour.ArmourClass.SAGE, spellCount, rand);

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

	private ItemWizardArmour.ArmourClass getArmourClass() {
		return ItemWizardArmour.ArmourClass.values()[this.dataManager.get(ARMOUR_CLASS)];
	}

	private void setArmourClass(ItemWizardArmour.ArmourClass armourClass) {
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

		ITextComponent wizardName = new TextComponentTranslation("class_element." + getElement().getName() + ".wizard");
		ITextComponent className = getArmourClassNameFor(this.getArmourClass());

		return new TextComponentTranslation("entity." + this.getEntityString() + "_combined.name", wizardName, className);
	}

	public static ITextComponent getArmourClassNameFor(ItemWizardArmour.ArmourClass armourClass) {
		return new TextComponentTranslation("wizard_armour_class." + armourClass.name().toLowerCase());
	}

	/**
	 * This method was package private so we had to duplicate it
	 * <p>
	 * Adds n random spells to the given list. The spells will be of the given element if possible. Extracted as a
	 * separate function since it was the same in both EntityWizard and EntityEvilWizard.
	 *
	 * @param wizard The wizard whose spells are to be populated.
	 * @param spells The spell list to be populated.
	 * @param e      The element that the spells should belong to, or {@link Element#MAGIC} for a random element each time.
	 * @param master Whether to include master spells.
	 * @param n      The number of spells to add.
	 * @param random A random number generator to use.
	 * @return The tier of the highest-tier spell that was added to the list.
	 * @author: Electroblob
	 */
	static Tier populateSpells(final EntityLiving wizard, List<Spell> spells, Element e, boolean master, int n, Random random) {

		// This is the tier of the highest tier spell added.
		Tier maxTier = Tier.NOVICE;

		List<Spell> npcSpells = Spell.getSpells(s -> s.canBeCastBy(wizard, false));
		npcSpells.removeIf(s -> !s.applicableForItem(WizardryItems.spell_book));

		for (int i = 0; i < n; i++) {

			Tier tier;
			// If the wizard has no element, it picks a random one each time.
			Element element = e == Element.MAGIC ? Element.values()[random.nextInt(Element.values().length)] : e;

			int randomiser = random.nextInt(20);

			// Uses its own special weighting
			if (randomiser < 10) {
				tier = Tier.NOVICE;
			} else if (randomiser < 16) {
				tier = Tier.APPRENTICE;
			} else if (randomiser < 19 || !master) {
				tier = Tier.ADVANCED;
			} else {
				tier = Tier.MASTER;
			}

			if (tier.ordinal() > maxTier.ordinal()) { maxTier = tier; }

			// Finds all the spells of the chosen tier and element
			List<Spell> list = Spell.getSpells(new Spell.TierElementFilter(tier, element, SpellProperties.Context.NPCS));
			// Keeps only spells which can be cast by NPCs
			list.retainAll(npcSpells);
			// Removes spells that the wizard already has
			list.removeAll(spells);

			// Ensures the tier chosen actually has spells in it. (isEmpty() is exactly the same as size() == 0)
			if (list.isEmpty()) {
				// If there are no spells applicable, tier and element restrictions are removed to give maximum
				// possibility of there being an applicable spell.
				list = npcSpells;
				// Removes spells that the wizard already has
				list.removeAll(spells);
			}

			// If the list is still empty now, there must be less than 3 enabled spells that can be cast by wizards
			// (excluding magic missile). In this case, having empty slots seems reasonable.
			if (!list.isEmpty()) { spells.add(list.get(random.nextInt(list.size()))); }

		}

		return maxTier;
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
