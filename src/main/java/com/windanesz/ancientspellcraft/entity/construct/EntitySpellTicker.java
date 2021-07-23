package com.windanesz.ancientspellcraft.entity.construct;

import com.windanesz.ancientspellcraft.spell.ISpellTickerConstruct;
import electroblob.wizardry.entity.construct.EntityScaledConstruct;
import electroblob.wizardry.registry.Spells;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class EntitySpellTicker extends EntityScaledConstruct {

	public int getDuration() {
		return duration;
	}

	/**
	 * This is not the duration of the EntityMushroomForest itself, but the duration of each individual mushroom!
	 */
	protected int duration = 200;

	private SpellModifiers modifiers;

	private int updateRate = 5;

	private Spell spell;

	private NBTTagCompound extraData;

	public EntitySpellTicker(World world) {
		super(world);
		setSize(5, 1);
	}

	@Override
	protected boolean shouldScaleHeight() {
		return false;
	}

	public void onUpdate() {
		super.onUpdate();

		if (!this.world.isRemote && world.getTotalWorldTime() % updateRate == 0 && spell instanceof ISpellTickerConstruct) {
			((ISpellTickerConstruct) spell).onUpdate(world, this);
		}
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		duration = (nbt.getInteger("duration"));

		if (nbt.hasKey("spellModifiers")) {
			modifiers = SpellModifiers.fromNBT(nbt.getCompoundTag("spellModifiers"));
		} else {
			modifiers = new SpellModifiers();
		}

		Spell spell = Spell.get(nbt.getString("spell"));
		this.spell = spell != null ? spell : Spells.none;
		updateRate = nbt.getInteger("updateRate");
		if (nbt.hasKey("extraData")) {
			extraData = nbt.getCompoundTag("extraData");
		}
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		nbt.setInteger("duration", duration);

		if (modifiers == null) {
			modifiers = new SpellModifiers();
		}
		nbt.setTag("spellModifiers", modifiers.toNBT());

		if (spell != null) {
			nbt.setString("spell", spell.getRegistryName().toString());
		} else {
			nbt.setString("spell", Spells.none.getRegistryName().toString());
		}
		nbt.setInteger("updateRate", updateRate);

		if (extraData != null) {
			nbt.setTag("extraData", extraData);
		}
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public SpellModifiers getModifiers() {
		return modifiers;
	}

	public void setModifiers(SpellModifiers spellModifiers) {
		this.modifiers = spellModifiers;
	}

	public Spell getSpell() {
		return spell;
	}

	public void setSpell(Spell spell) {
		this.spell = spell;
	}

	public int getUpdateRate() {
		return updateRate;
	}

	public void setUpdateRate(int updateRate) {
		this.updateRate = updateRate;
	}

	public void setExtraData(NBTTagCompound compound) {
		extraData = compound;
	}

	public NBTTagCompound getExtraData() {
		return extraData == null ? new NBTTagCompound() : extraData;
	}
}
