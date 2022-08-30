package com.windanesz.ancientspellcraft.capability;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.util.ASUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nullable;
import java.util.UUID;

@Mod.EventBusSubscriber
public class DeathMarkCapability implements INBTSerializable<NBTTagCompound> {

	public static final String CASTER_UUID_TAG = "casterUUID";
	/**
	 * Static instance of what I like to refer to as the capability key. Private because, well, it's internal!
	 * This annotation does some crazy Forge magic behind the scenes and assigns this field a value.
	 */
	@CapabilityInject(DeathMarkCapability.class)
	private static final Capability<DeathMarkCapability> DEATH_MARK_CAPABILITY = null;
	/**
	 * The entity this capability instance belongs to.
	 */
	private final EntityLivingBase entity;
	protected UUID casterUUID;

	public DeathMarkCapability() {
		this(null); // Nullary constructor for the registration method factory parameter
	}

	public DeathMarkCapability(EntityLivingBase entity) {
		this.entity = entity;
	}

	/**
	 * Called from preInit in the main mod class to register the capability.
	 */
	public static void register() {

		// Yes - by the looks of it, having an interface is completely unnecessary in this case.
		CapabilityManager.INSTANCE.register(DeathMarkCapability.class, new IStorage<DeathMarkCapability>() {
			// These methods are only called by Capability.writeNBT() or Capability.readNBT(), which in turn are
			// NEVER CALLED. Unless I'm missing some reflective invocation, that means this entire class serves only
			// to allow capabilities to be saved and loaded manually. What that would be useful for I don't know.
			// (If an API forces most users to write redundant code for no reason, it's not user friendly, is it?)
			// ... well, that's my rant for today!
			@Override
			public NBTBase writeNBT(Capability<DeathMarkCapability> capability, DeathMarkCapability instance, EnumFacing side) {
				return null;
			}

			@Override
			public void readNBT(Capability<DeathMarkCapability> capability, DeathMarkCapability instance, EnumFacing side, NBTBase nbt) {}

		}, DeathMarkCapability::new);
	}

	/**
	 * Returns the capability instance for the specified entity.
	 *
	 * @return
	 */
	public static DeathMarkCapability get(EntityLivingBase entity) {
		return entity.getCapability(DEATH_MARK_CAPABILITY, null);
	}

	// ============================================== Event Handlers ==============================================

	@SubscribeEvent
	public static void attachCapability(AttachCapabilitiesEvent<Entity> event) {
		if (event.getObject() instanceof EntityCreature) {
			event.addCapability(new ResourceLocation(AncientSpellcraft.MODID, "DeathMarkCapability"),
					new Provider((EntityLivingBase) event.getObject()));
		}
	}

	@SubscribeEvent
	public static void onLivingDeathEvent(LivingDeathEvent event) {
		if (event.getEntity() instanceof EntityLivingBase && event.getEntity().hasCapability(DEATH_MARK_CAPABILITY, null)
				&& get(event.getEntityLiving()).casterUUID != null) {
			UUID casterUUID = get(event.getEntityLiving()).casterUUID;
			if (event.getEntity().world.getPlayerEntityByUUID(casterUUID) != null) {
				ASUtils.sendMessage(event.getEntity().world.getPlayerEntityByUUID(casterUUID), "spell.ancientspellcraft:death_mark.entity_death_message",
						 false, event.getEntity().getDisplayName());
			}
		}

	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound nbt = new NBTTagCompound();

		if (this.casterUUID != null) {
			NBTTagCompound casterUUID = new NBTTagCompound();

			casterUUID.setUniqueId(CASTER_UUID_TAG, this.getCasterId());
			nbt.setTag(CASTER_UUID_TAG, casterUUID);

		}

		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		if (nbt.hasKey(CASTER_UUID_TAG)) {
			this.setCasterId(nbt.getCompoundTag(CASTER_UUID_TAG).getUniqueId(CASTER_UUID_TAG));
		}
	}

	/**
	 * Sets the caster of the death mark spell.
	 *
	 * @param caster the caster of the death mark spell on this entity.
	 */
	public void setCaster(@Nullable EntityLivingBase caster) {
		setCasterId(caster == null ? null : caster.getUniqueID());
	}

	public UUID getCasterId() {
		return casterUUID;
	}

	public void setCasterId(UUID uuid) {
		this.casterUUID = uuid;
	}

	// ========================================== Capability Boilerplate ==========================================

	public static class Provider implements ICapabilitySerializable<NBTTagCompound> {

		private final DeathMarkCapability capability;

		public Provider(EntityLivingBase entity) {
			capability = new DeathMarkCapability(entity);
		}

		@Override
		public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
			return capability == DEATH_MARK_CAPABILITY;
		}

		@Override
		public <T> T getCapability(Capability<T> capability, EnumFacing facing) {

			if (capability == DEATH_MARK_CAPABILITY) {
				return DEATH_MARK_CAPABILITY.cast(this.capability);
			}

			return null;
		}

		@Override
		public NBTTagCompound serializeNBT() {
			return capability.serializeNBT();
		}

		@Override
		public void deserializeNBT(NBTTagCompound nbt) {
			capability.deserializeNBT(nbt);
		}

	}

}
