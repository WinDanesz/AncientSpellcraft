package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftItems;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftPotions;
import com.windanesz.ancientspellcraft.util.SpellTeleporter;
import electroblob.wizardry.data.IStoredVariable;
import electroblob.wizardry.data.Persistence;
import electroblob.wizardry.data.WizardData;
import electroblob.wizardry.packet.PacketTransportation;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.spell.SpellBuff;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber
public class TimeKnot extends SpellBuff {

	/**
	 * Stores: HEALTH, POTION EFFECTS, LOCATION (BLOCKPOS, DIM), ACTIVE POTION EFFECTS
	 * Resets: motion, fall distance, current potion effects
	 */

	public static final IStoredVariable<NBTTagCompound> TIME_KNOT_DATA = IStoredVariable.StoredVariable.ofNBT("time_knot_data", Persistence.ALWAYS).setSynced();

	// location: electroblob.wizardry.util.Location.fromNBT

	public TimeKnot() {
		super(AncientSpellcraft.MODID, "time_knot", 42, 206, 88, () -> AncientSpellcraftPotions.time_knot);
		WizardData.registerStoredVariables(TIME_KNOT_DATA);
	}

	/**
	 * <b>Overriding as we don't want to spawn particles.</b>
	 */
	protected boolean applyEffects(EntityLivingBase caster, SpellModifiers modifiers) {

		float baseDur = (getProperty(getDurationKey(AncientSpellcraftPotions.time_knot)).floatValue());
		float durationUpgrade = (modifiers.get(WizardryItems.duration_upgrade));
		float potency = (modifiers.get(SpellModifiers.POTENCY));
		if (potency > 1.0f) {
			potency = (potency - 1) * 8 + 1;
		}

		if (durationUpgrade > 1.0f) {
			durationUpgrade = (durationUpgrade - 1) * 10 + 1;
		}

		int duration = (int) (baseDur * potency * durationUpgrade);

		for (Potion potion : potionSet) {
			caster.addPotionEffect(new PotionEffect(potion, potion.isInstant() ? 1 : duration, (int) getProperty(getStrengthKey(potion)).floatValue(), false, false));
		}
		//		for (Potion potion : potionSet) {
		//			caster.addPotionEffect(new PotionEffect(potion, potion.isInstant() ? 1 :
		//					(int) (getProperty(getDurationKey(potion)).floatValue() * modifiers.get(WizardryItems.duration_upgrade)),
		//					(int) getProperty(getStrengthKey(potion)).floatValue(),
		//					false, false));
		//		}
		return true;
	}

	@Override
	protected int getBonusAmplifier(float potencyModifier) {
		return getStandardBonusAmplifier(potencyModifier);
	}

	@Override
	public boolean cast(World world, EntityPlayer player, EnumHand hand, int ticksInUse, SpellModifiers modifiers) {
		if (world.isRemote)
			this.spawnParticles(world, player, modifiers);

		if (!player.isPotionActive(AncientSpellcraftPotions.time_knot)) {

			if (player.isPotionActive(AncientSpellcraftPotions.curse_temporal_casualty)) {
				player.removePotionEffect(AncientSpellcraftPotions.curse_temporal_casualty);
			}
			WizardData data = WizardData.get(player);
			if (data != null) {

				NBTTagCompound compound = storeCurrentPlayerData(player);
				data.setVariable(TIME_KNOT_DATA, compound);
				data.sync();

			}
			return super.cast(world, player, hand, ticksInUse, modifiers);
		} else {
			return false;
		}
	}

	public static NBTTagCompound storeCurrentPlayerData(EntityPlayer caster) {
		NBTTagCompound compound = new NBTTagCompound();

		// location info
		compound.setDouble("PosX", caster.posX);
		compound.setDouble("PosY", caster.posY);
		compound.setDouble("PosZ", caster.posZ);

		compound.setInteger("Dimension", caster.dimension);

		// properties
		compound.setFloat("Health", caster.getHealth());
		compound.setFloat("AbsorptionAmount", caster.getAbsorptionAmount());
		compound.setFloat("SaturationLevel", caster.getFoodStats().getSaturationLevel());

		compound.setInteger("AirLevel", caster.getAir()); // current air level (in lungs)
		compound.setInteger("FoodLevel", caster.getFoodStats().getFoodLevel());

		compound.setBoolean("Burning", caster.isBurning());

		// potions
		if (!caster.getActivePotionMap().isEmpty()) {
			NBTTagList potionTagList = new NBTTagList();

			for (PotionEffect potioneffect : caster.getActivePotionMap().values()) {
				// TODO: artefact ?

				// skip time knot potion effect
				if (potioneffect.getPotion() == AncientSpellcraftPotions.time_knot) {
					continue;
				}
				potionTagList.appendTag(potioneffect.writeCustomPotionEffectToNBT(new NBTTagCompound()));
			}

			compound.setTag("ActiveEffects", potionTagList);

		}

		return compound;
	}

	@SubscribeEvent
	public static void onPotionExpiryEvent(PotionEvent.PotionExpiryEvent event) {

		if (event.getEntity() instanceof EntityPlayer) {

			EntityPlayer player = (EntityPlayer) event.getEntity();

			if (event.getPotionEffect().getPotion() == AncientSpellcraftPotions.time_knot && player.isEntityAlive() && player.getHealth() != 0F) {
				loopPlayer(player);
			}
		}

	}

	public static void loopPlayer(EntityPlayer player) {

		WizardData data = WizardData.get(player);
		if (data != null) {
			NBTTagCompound compound = data.getVariable(TIME_KNOT_DATA);
			if (compound != null) {
				try {
					if (player.world.isRemote)
						ParticleBuilder.create(ParticleBuilder.Type.BUFF).entity(player).clr(42, 206, 88).spawn(player.world);

					if (player.isRiding()) {
						player.dismountRidingEntity();
					}

					updatePlayerDataFromNBT(player, compound);

				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void updatePlayerDataFromNBT(EntityPlayer player, NBTTagCompound compound) {
		if (compound != null) {
			// reset location

			int storedDim = compound.getInteger("Dimension");
			double posX = compound.getDouble("PosX");
			double posY = compound.getDouble("PosY");
			double posZ = compound.getDouble("PosZ");

			if (player.dimension == storedDim) {
				player.setPositionAndUpdate(posX, posY, posZ);
				IMessage msg = new PacketTransportation.Message();
//				WizardryPacketHandler.net.sendToDimension(msg, player.world.provider.getDimension());
			} else {
				SpellTeleporter.teleportEntity(storedDim, posX, posY, posZ, false, player);
			}
			//			}

			// reset properties
			player.setHealth(compound.getFloat("Health"));
			player.setAbsorptionAmount(compound.getFloat("AbsorptionAmount"));

			player.getFoodStats().setFoodSaturationLevel(compound.getFloat("SaturationLevel"));
			player.getFoodStats().setFoodLevel(compound.getInteger("FoodLevel"));

			player.setAir(compound.getInteger("AirLevel"));

			boolean burning = compound.getBoolean("Burning");
//			if (burning) {
//				player.setFire(3);
//			} else {
//			}
			player.extinguish();

			List<Potion> activePotions = new ArrayList<>();

			for (Potion potion : player.getActivePotionMap().keySet()) {
				activePotions.add(potion);
			}

			if (!activePotions.isEmpty()) {
				for (Potion activePotion : activePotions) {
					player.removePotionEffect(activePotion);
				}
			}
		}

		// re-apply saved potions
		if (compound.hasKey("ActiveEffects", 9)) {
			NBTTagList nbttaglist = compound.getTagList("ActiveEffects", 10);

			for (int i = 0; i < nbttaglist.tagCount(); ++i) {
				NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i);
				PotionEffect potioneffect = PotionEffect.readCustomPotionEffectFromNBT(nbttagcompound);

				if (potioneffect != null) {
					player.addPotionEffect(potioneffect);
				}
			}
		}

	}

	//TODO maybe do a fancy effect by freezing the player in the air for a moment, applying a green overlay on it, before it disappears. set it to not affected by gravity...

	// TODO: reset position
	// TODO: clear active potion effects
	// TODO: reset spell cooldowns
	// TODO: reset motion - otherwise you can e.g. die from your acceleration, maybe store it?
	// TODO: reset fall distance

	@Override
	public boolean canBeCastBy(TileEntityDispenser dispenser) {
		return false;
	}

	@Override
	public boolean canBeCastBy(EntityLiving npc, boolean override) {
		return false;
	}

	/**
	 * Spawns buff particles around the caster. Override to add a custom particle effect. Only called client-side.
	 */
	@Override
	protected void spawnParticles(World world, EntityLivingBase caster, SpellModifiers modifiers) {

		for (int i = 0; i < particleCount; i++) {
			double x = caster.posX + world.rand.nextDouble() * 2 - 1;
			double y = caster.getEntityBoundingBox().minY + caster.getEyeHeight() - 0.5 + world.rand.nextDouble();
			double z = caster.posZ + world.rand.nextDouble() * 2 - 1;
			ParticleBuilder.create(ParticleBuilder.Type.SPARKLE).pos(x, y, z).vel(0, 0.1, 0).clr(42, 206, 88).spawn(world);
		}

		ParticleBuilder.create(ParticleBuilder.Type.BUFF).entity(caster).clr(42, 206, 88).spawn(world);
	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == AncientSpellcraftItems.ancient_spellcraft_spell_book || item == AncientSpellcraftItems.ancient_spellcraft_scroll;
	}
}
