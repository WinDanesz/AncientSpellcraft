package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.registry.ASItems;
import com.windanesz.ancientspellcraft.registry.ASPotions;
import electroblob.wizardry.data.IStoredVariable;
import electroblob.wizardry.data.Persistence;
import electroblob.wizardry.data.WizardData;
import electroblob.wizardry.item.ItemArtefact;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.registry.WizardryPotions;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.UUID;

public class EmpoweringLink extends SpellRayAS {

	public static final IStoredVariable<UUID> UUID_KEY = IStoredVariable.StoredVariable.ofUUID("empoweringLinkTarget", Persistence.NEVER).withTicker(EmpoweringLink::update);
	public static final IStoredVariable<UUID> LAST_UUID_KEY = IStoredVariable.StoredVariable.ofUUID("empoweringLinkLastTarget", Persistence.ALWAYS);

	public EmpoweringLink() {
		super("empowering_link", SpellActions.POINT, true);
		WizardData.registerStoredVariables(UUID_KEY,LAST_UUID_KEY);
	}

	private static UUID update(EntityPlayer player, UUID uuid) {

		if (player != null && player.ticksExisted % 60 == 0 && uuid != null) {
			EntityPlayer empoweredPlayer = player.world.getPlayerEntityByUUID(uuid);
			if (empoweredPlayer != null) {

				if (player.dimension != empoweredPlayer.dimension) {
					return null;
				}

				empoweredPlayer.addPotionEffect(new PotionEffect(WizardryPotions.empowerment, 60, 0));
				empoweredPlayer.addPotionEffect(new PotionEffect(MobEffects.SPEED, 60, 0));
				empoweredPlayer.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 60, 0));
				player.addPotionEffect(new PotionEffect(ASPotions.magical_exhaustion, 60, 0));
				player.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 60, 0));
				player.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 60, 0));
			}
		}

		return uuid;
	}

	@Override
	protected boolean onEntityHit(World world, Entity target, Vec3d hit,
			@Nullable EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {

		if (target instanceof EntityPlayer && caster instanceof EntityPlayer) {
			WizardData data = WizardData.get((EntityPlayer) caster);

			UUID uuid = data.getVariable(UUID_KEY);

			if (uuid == target.getUniqueID()) {
				// dispelling the effect
				data.setVariable(UUID_KEY, null);
				return true;
			} else {
				data.setVariable(UUID_KEY, uuid);
				data.setVariable(LAST_UUID_KEY, uuid);
				return true;
			}

		}

		return false;
	}

	@Override
	protected boolean onBlockHit(World world, BlockPos pos, EnumFacing side, Vec3d hit,
			@Nullable EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {
		if (caster instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) caster;
			if (ItemArtefact.isArtefactActive(player, ASItems.ring_devotion)) {
				WizardData data = WizardData.get(player);
				UUID lastUUID = data.getVariable(LAST_UUID_KEY);
				UUID currentUUID = data.getVariable(UUID_KEY);

				if (lastUUID != null) {
					if (currentUUID != null && currentUUID != lastUUID) {
						data.setVariable(UUID_KEY, lastUUID);
					} else if (currentUUID != null) {
						data.setVariable(UUID_KEY, null);
					}
				}
			}
		}

		return false;

	}

	@Override
	protected boolean onMiss(World world, @Nullable EntityLivingBase caster, Vec3d origin, Vec3d direction, int ticksInUse, SpellModifiers modifiers) {
		return false;
	}

	@Override
	public boolean canBeCastBy(EntityLiving npc, boolean override) { return false; }

	@Override
	public boolean canBeCastBy(TileEntityDispenser dispenser) { return false; }

	@Override
	protected void spawnParticle(World world, double x, double y, double z, double vx, double vy, double vz) {
		world.spawnParticle(EnumParticleTypes.FLAME, x, y, z, 0, 0, 0);
	}
}
