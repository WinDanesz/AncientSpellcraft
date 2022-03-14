package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.registry.ASItems;
import com.windanesz.ancientspellcraft.registry.ASSounds;
import com.windanesz.ancientspellcraft.registry.ASSpells;
import com.windanesz.ancientspellcraft.util.SpellcastUtils;
import electroblob.wizardry.constants.SpellType;
import electroblob.wizardry.data.IStoredVariable;
import electroblob.wizardry.data.Persistence;
import electroblob.wizardry.data.WizardData;
import electroblob.wizardry.event.SpellCastEvent;
import electroblob.wizardry.registry.WizardrySounds;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class Contingency extends Spell {

	public static final String ACTIVE_LISTENER_TAG = "active_listener";
	public static final String CONTINGENCY_CASTER_TAG = "contingency_caster";
	private float r;
	private float g;
	private float b;

	public static final IStoredVariable<NBTTagCompound> ACTIVE_CONTINGENCIES = IStoredVariable.StoredVariable.ofNBT("active_contingencies", Persistence.ALWAYS).setSynced();
	public static final IStoredVariable<NBTTagCompound> ACTIVE_CONTINGENCY_LISTENER = IStoredVariable.StoredVariable.ofNBT("active_contingency_listener", Persistence.ALWAYS).setSynced();

	/** The number of sparkle particles spawned when this spell is cast. Defaults to 10. */
	protected float particleCount = 10;

	public Contingency(String name, EnumAction action, float r, float g, float b) {
		super(AncientSpellcraft.MODID, name, action, false);
		WizardData.registerStoredVariables(ACTIVE_CONTINGENCIES, ACTIVE_CONTINGENCY_LISTENER);
		this.r = r;
		this.g = g;
		this.b = b;
	}

	@Override
	public boolean cast(World world, EntityPlayer caster, EnumHand hand, int ticksInUse, SpellModifiers modifiers) {
		if (WizardData.get(caster) != null) {
			WizardData data = WizardData.get(caster);
			// Fixes the sound not playing in first person.
			if (world.isRemote)
				this.playSound(world, caster, ticksInUse, -1, modifiers);
			NBTTagCompound listener = new NBTTagCompound();

			//noinspection ConstantConditions
			listener.setString(ACTIVE_LISTENER_TAG, this.getRegistryName().toString());
			data.setVariable(ACTIVE_CONTINGENCY_LISTENER, listener);
			if (!world.isRemote)
				data.sync();
			if(world.isRemote) this.spawnParticles(world, caster, modifiers);
			this.playSound(world, caster, ticksInUse, -1, modifiers);
			return true;
		}
		return false;
	}

	@Override
	public boolean canBeCastBy(EntityLiving npc, boolean override) {
		return false;
	}

	@Override
	public boolean canBeCastBy(TileEntityDispenser dispenser) {
		return false;
	}


	@SuppressWarnings("Duplicates")
	public static boolean tryCastContingencySpell(EntityPlayer player, WizardData data, Type contingency) {
		if (data == null)
			return false;

		// purging the active contingency listener to avoid weird use cases
		data.setVariable(ACTIVE_CONTINGENCY_LISTENER, null);
		data.sync();
		NBTTagCompound activeContingencies = data.getVariable(ACTIVE_CONTINGENCIES);

		if (activeContingencies == null || !activeContingencies.hasKey(contingency.spellName))
			return false;

		String spellNameToTrigger = activeContingencies.getString(contingency.spellName);
		Spell spellToTrigger = Spell.registry.getValue(new ResourceLocation(spellNameToTrigger));
		if (spellToTrigger == null)
			return false;

		// if true, the spell cast fails

		if (SpellcastUtils.tryCastSpellAsPlayer(player, spellToTrigger, EnumHand.MAIN_HAND, SpellCastEvent.Source.WAND, new SpellModifiers(), 120)) {
			// removing this contingency
			activeContingencies.removeTag(contingency.spellName);
			data.setVariable(ACTIVE_CONTINGENCIES, activeContingencies);
			data.sync();

			player.world.playSound(player.posX, player.posY, player.posZ, ASSounds.CONTINGENCY_ACTIVATE, WizardrySounds.SPELLS, 1F, 1F, false);

			return true;
		}

		return false;
	}

	@SuppressWarnings("Duplicates")
	public static boolean tryCastContingencySpellAsMob(EntityLivingBase entityLivingBase, Type contingency) {
		if (entityLivingBase instanceof EntityPlayer) {
			return tryCastContingencySpell((EntityPlayer) entityLivingBase, WizardData.get((EntityPlayer) entityLivingBase), contingency);
		}

		if (entityLivingBase instanceof EntityLiving) {
			EntityLiving entityLiving = (EntityLiving) entityLivingBase;
			NBTTagCompound entityData = entityLiving.getEntityData();
			if (!(entityData.hasKey(ACTIVE_LISTENER_TAG) && entityData.getCompoundTag(ACTIVE_LISTENER_TAG).hasKey(contingency.spellName)))
				return false;

			Spell spellToTrigger = Spell.registry.getValue(new ResourceLocation(entityData.getCompoundTag(ACTIVE_LISTENER_TAG).getString(contingency.spellName)));

			if (spellToTrigger == null)
				return false;

			EntityLivingBase target = entityLiving;
			// Buffs are usually self targeted
			if (spellToTrigger.getType() != SpellType.BUFF && spellToTrigger.getType() != SpellType.DEFENCE && spellToTrigger.getType() != SpellType.CONSTRUCT) {
				target = entityLiving.getRevengeTarget() != null ? entityLiving.getRevengeTarget() : target;
			}

			boolean test = SpellcastUtils.tryCastSpellAsMob(entityLiving, spellToTrigger, target);

			if (test) {
				// removing this contingency
				NBTTagCompound compound = entityLiving.getEntityData().getCompoundTag(Contingency.ACTIVE_LISTENER_TAG);
				if (compound.hasKey(contingency.spellName)) {
					compound.removeTag(contingency.spellName);
					entityLiving.getEntityData().removeTag(Contingency.ACTIVE_LISTENER_TAG);
					entityLiving.getEntityData().setTag(Contingency.ACTIVE_LISTENER_TAG, compound);
				}
			}

			return true;
		}
		return false;

	}

	public static void setContingencyTag(EntityLivingBase entity, EntityLivingBase caster, Type contingency, Spell spellToStore) {
		NBTTagCompound compound = new NBTTagCompound();
		if (entity.getEntityData().hasKey(Contingency.ACTIVE_LISTENER_TAG)) {
			compound = entity.getEntityData().getCompoundTag(Contingency.ACTIVE_LISTENER_TAG);
		}
		compound.setString(contingency.spellName, spellToStore.getRegistryName().toString());
		compound.setInteger(CONTINGENCY_CASTER_TAG, caster.getEntityId());
		entity.getEntityData().setTag(Contingency.ACTIVE_LISTENER_TAG, compound);
	}

	public enum Type {

		FIRE(ASSpells.contingency_fire, "ancientspellcraft:contingency_fire"),
		FALL(ASSpells.contingency_fall, "ancientspellcraft:contingency_fall"),
		DAMAGE(ASSpells.contingency_damage, "ancientspellcraft:contingency_damage"),
		CRITICAL_HEALTH(ASSpells.contingency_critical_health, "ancientspellcraft:contingency_critical_health"),
		DEATH(ASSpells.contingency_death, "ancientspellcraft:contingency_death"),
		DROWNING(ASSpells.contingency_drowning, "ancientspellcraft:contingency_drowning"),
		HOSTILE_SPELLCAST(ASSpells.contingency_hostile_spellcast, "ancientspellcraft:contingency_hostile_spellcast"),
		IMMOBILITY(ASSpells.contingency_paralysis, "ancientspellcraft:contingency_immobility"),
		;

		public Spell spell;
		public String spellName;

		Type(Spell spell, String spellName) {
			this.spell = spell;
			this.spellName = spellName;
		}

		public static Type fromName(String name) {

			for (Type type : values()) {
				if (type.spellName.equals(name))
					return type;
			}

			throw new IllegalArgumentException("No such type with unlocalised name: " + name);
		}
	}

	public static boolean isFireDamageSource(DamageSource source) {
		return source == DamageSource.IN_FIRE || source == DamageSource.ON_FIRE || source == DamageSource.LAVA || source == DamageSource.HOT_FLOOR;
	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == ASItems.ancient_spell_book || item == ASItems.ancient_spellcraft_scroll;
	}

	/** Spawns buff particles around the caster. Override to add a custom particle effect. Only called client-side. */
	public void spawnParticles(World world, EntityLivingBase caster, SpellModifiers modifiers){

		for(int i = 0; i < particleCount; i++){
			double x = caster.posX + world.rand.nextDouble() * 2 - 1;
			double y = caster.posY + caster.getEyeHeight() - 0.5 + world.rand.nextDouble();
			double z = caster.posZ + world.rand.nextDouble() * 2 - 1;
			ParticleBuilder.create(ParticleBuilder.Type.SPARKLE).pos(x, y, z).vel(0, 0.1, 0).clr(r, g, b).spawn(world);
		}

		ParticleBuilder.create(ParticleBuilder.Type.BUFF).entity(caster).clr(r, g, b).spawn(world);
	}

	public static void spawnParticles(World world, EntityLivingBase caster, Contingency.Type type) {
		Spell spell = Spell.registry.getValue(new ResourceLocation(type.spellName));
		if (!(spell instanceof Contingency))
			return;

		Contingency contingency = (Contingency) spell;

		for(int i = 0; i < contingency.particleCount; i++){
			double x = caster.posX + world.rand.nextDouble() * 2 - 1;
			double y = caster.posY + caster.getEyeHeight() - 0.5 + world.rand.nextDouble();
			double z = caster.posZ + world.rand.nextDouble() * 2 - 1;
			ParticleBuilder.create(ParticleBuilder.Type.SPARKLE).pos(x, y, z).vel(0, 0.1, 0).clr(contingency.r, contingency.g, contingency.b).spawn(world);
		}

		ParticleBuilder.create(ParticleBuilder.Type.BUFF).entity(caster).clr(contingency.r, contingency.g, contingency.b).spawn(world);
	}

	public static void playSound(World world, BlockPos pos) {
		world.playSound((double) pos.getX(), (double) pos.getY(), (double) pos.getZ(), ASSounds.CONTINGENCY, SoundCategory.BLOCKS, 1F, 1F, false);
	}
}
