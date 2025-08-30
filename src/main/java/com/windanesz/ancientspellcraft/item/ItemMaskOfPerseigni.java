package com.windanesz.ancientspellcraft.item;

import com.windanesz.ancientspellcraft.util.ASUtils;
import electroblob.wizardry.item.ItemWand;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.MagicDamage;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.RayTracer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

public class ItemMaskOfPerseigni extends ItemASArtefact implements ITickableArtefact {
	
	private static final int CHECK_INTERVAL = 20; // Check every second
	private static final double RANGE = 10.0; // Same range as Pyrokinesis spell
	private static final int MANA_COST = 15; // Same cost as Pyrokinesis spell
	
	public ItemMaskOfPerseigni(EnumRarity rarity, Type type) {
		super(rarity, type);
	}

	@Override
	public void onWornTick(ItemStack itemstack, EntityLivingBase player) {
		// Only run on server side and at the specified interval
		if (!player.world.isRemote && player.ticksExisted % CHECK_INTERVAL == 0) {
			if (player instanceof EntityPlayer) {
				EntityPlayer entityPlayer = (EntityPlayer) player;
				
				// Check if player has a wand with Pyrokinesis spell and sufficient mana
				ItemStack wandStack = findWandWithPyrokinesis(entityPlayer);
				if (wandStack != null && hasSufficientMana(wandStack)) {
					
					// Perform ray trace to find entity player is looking at
					Entity target = getEntityInSight(entityPlayer);
					if (target instanceof EntityLivingBase && target != entityPlayer) {
						
						// Apply Pyrokinesis effects
						applyPyrokinesisEffects(entityPlayer, (EntityLivingBase) target, wandStack);
					}
				}
			}
		}
	}
	
	private ItemStack findWandWithPyrokinesis(EntityPlayer player) {
		// Check hotbar for wands with Pyrokinesis spell
		List<ItemStack> hotbarWands = ASUtils.getAllHotbarWands(player);
		
		for (ItemStack wandStack : hotbarWands) {
			if (wandStack.getItem() instanceof ItemWand) {
				ItemWand wand = (ItemWand) wandStack.getItem();
				for (Spell spell : wand.getSpells(wandStack)) {
					if (spell.getRegistryName().toString().equals("ancientspellcraft:pyrokinesis")) {
						return wandStack;
					}
				}
			}
		}
		
		return null;
	}
	
	private boolean hasSufficientMana(ItemStack wandStack) {
		if (wandStack.getItem() instanceof ItemWand) {
			ItemWand wand = (ItemWand) wandStack.getItem();
			return wand.getMana(wandStack) >= MANA_COST;
		}
		return false;
	}
	
	private Entity getEntityInSight(EntityPlayer player) {
		Vec3d look = player.getLookVec();
		Vec3d origin = new Vec3d(player.posX, player.posY + player.getEyeHeight() - 0.25f, player.posZ);
		
		// Adjust origin for third person view
		if (player.world.isRemote && !electroblob.wizardry.Wizardry.proxy.isFirstPerson(player)) {
			origin = origin.add(look.scale(1.2));
		}
		
		Vec3d endpoint = origin.add(look.scale(RANGE));
		
		// Ray trace to find entity
		RayTraceResult rayTrace = RayTracer.rayTrace(player.world, origin, endpoint, 0.4f, false,
				true, false, Entity.class, RayTracer.ignoreEntityFilter(player));
		
		if (rayTrace != null && rayTrace.typeOfHit == RayTraceResult.Type.ENTITY) {
			return rayTrace.entityHit;
		}
		
		return null;
	}
	
	private void applyPyrokinesisEffects(EntityPlayer caster, EntityLivingBase target, ItemStack wandStack) {
		World world = caster.world;
		
		// Check if target is immune to fire damage
		if (MagicDamage.isEntityImmune(MagicDamage.DamageType.FIRE, target)) {
			return;
		}
		
		// Apply fire damage and effects (same as Pyrokinesis spell)
		target.setFire(10); // burn_duration from spell properties
		
		EntityUtils.attackEntityWithoutKnockback(target,
				MagicDamage.causeDirectMagicDamage(caster, MagicDamage.DamageType.FIRE),
				3.0f); // damage from spell properties
		
		target.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 30, 1)); // slow_duration from spell properties
		
		// Spawn particles on client side
		spawnParticlesAroundTarget(target);
		
		// Consume mana from the wand
		if (wandStack.getItem() instanceof ItemWand) {
			ItemWand wand = (ItemWand) wandStack.getItem();
			wand.consumeMana(wandStack, MANA_COST, caster);
		}
	}
	
	private void spawnParticlesAroundTarget(EntityLivingBase target) {
		// Spawn particles on the client side
		if (target.world.isRemote) {
			for (int i = 0; i < 10; i++) {
				double dx = (target.world.rand.nextDouble() * (target.world.rand.nextBoolean() ? 1 : -1)) * 0.1;
				double dy = (target.world.rand.nextDouble() * (target.world.rand.nextBoolean() ? 1 : -1)) * 0.1;
				double dz = (target.world.rand.nextDouble() * (target.world.rand.nextBoolean() ? 1 : -1)) * 0.1;
				
				ParticleBuilder.create(ParticleBuilder.Type.MAGIC_FIRE)
						.entity(target)
						.pos(0, target.height / 2, 0)
						.vel(dx, dy, dz)
						.spawn(target.world);
			}
		}
	}
}
