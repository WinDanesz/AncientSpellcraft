package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.item.ItemWhisperingVeil;
import com.windanesz.ancientspellcraft.registry.ASItems;
import electroblob.wizardry.data.IStoredVariable;
import electroblob.wizardry.data.Persistence;
import electroblob.wizardry.data.WizardData;
import electroblob.wizardry.entity.living.EntityWizard;
import electroblob.wizardry.item.ItemArtefact;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.registry.WizardryPotions;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.World;

import java.util.UUID;

public class Compulsion extends SpellRayAS {

	public static final IStoredVariable<UUID> CONTROLLED_ENTITY = IStoredVariable.StoredVariable.ofUUID("controlled_entity", Persistence.ALWAYS);

	public Compulsion() {
		super("compulsion", SpellActions.POINT, false);
		WizardData.registerStoredVariables(CONTROLLED_ENTITY);
	}

	@Override
	protected boolean onEntityHit(World world, Entity target, Vec3d hit, EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {
		
		if (!(caster instanceof EntityPlayer)) return false;
		
		EntityPlayer player = (EntityPlayer) caster;
		WizardData data = WizardData.get(player);
		
		if (data == null) return false;
		
		UUID controlledEntityUUID = data.getVariable(CONTROLLED_ENTITY);
		boolean hasBlackTongueAmulet = ItemArtefact.isArtefactActive(player, ASItems.amulet_black_tongue);
		boolean hasWhisperingVeil = ItemArtefact.isArtefactActive(player, ASItems.head_whispering_veil);
		boolean isSneaking = player.isSneaking();
		
		// Sneak-cast to select a creature as controlled entity
		if (isSneaking) {
			if (target instanceof EntityLiving) {
				EntityLiving livingEntity = (EntityLiving) target;
				data.setVariable(CONTROLLED_ENTITY, target.getUniqueID());
				livingEntity.setRevengeTarget(null);
				livingEntity.setAttackTarget(null);

				((EntityLivingBase)target).addPotionEffect(new PotionEffect(WizardryPotions.mind_trick, 200, 0));
				if (hasWhisperingVeil) {
					player.sendStatusMessage(new TextComponentTranslation("spell.ancientspellcraft:compulsion.target_selected_whispering_veil", target.getName()), true);
				} else {
					player.sendStatusMessage(new TextComponentTranslation("spell.ancientspellcraft:compulsion.target_selected", target.getName()), true);
				}
				return true;
			}
			return false;
		}
		
		// Normal cast to command the controlled entity
		if (controlledEntityUUID == null) {
			player.sendStatusMessage(new TextComponentTranslation("spell.ancientspellcraft:compulsion.no_controlled_entity"), true);
			return false;
		}
		
		// Handle Whispering Veil special behavior
		if (hasWhisperingVeil && target instanceof EntityVillager) {
			EntityVillager villager = (EntityVillager) target;
			
			// Check if this villager has already been used today
			if (ItemWhisperingVeil.hasUsedVillagerToday(player, villager.getUniqueID())) {
				player.sendStatusMessage(new TextComponentTranslation("spell.ancientspellcraft:compulsion.villager_already_used_today"), true);
				return false;
			}
			
			// Ensure villager trades are initialized by setting the customer first
			villager.setCustomer(player);
			MerchantRecipeList recipes = villager.getRecipes(player);
			
			if (recipes != null && !recipes.isEmpty()) {
				// Use the original simple approach but with better initialization
				MerchantRecipe randomRecipe = recipes.get(world.rand.nextInt(recipes.size()));
				ItemStack freeItem = randomRecipe.getItemToSell().copy();
				
				// Only proceed if we actually got a valid item
				if (!freeItem.isEmpty()) {
					if (!player.addItemStackToInventory(freeItem)) {
						player.dropItem(freeItem, false);
					}
					
					// Mark this villager as used today
					ItemWhisperingVeil.markVillagerAsUsedToday(player, villager.getUniqueID());
					
					player.sendStatusMessage(new TextComponentTranslation("spell.ancientspellcraft:compulsion.villager_forced_trade", villager.getName(), freeItem.getDisplayName()), true);
					return true;
				} else {
					player.sendStatusMessage(new TextComponentTranslation("spell.ancientspellcraft:compulsion.villager_no_valid_trades"), true);
					return false;
				}
			} else {
				player.sendStatusMessage(new TextComponentTranslation("spell.ancientspellcraft:compulsion.villager_no_trades"), true);
				return false;
			}
		} else if (hasWhisperingVeil && target instanceof EntityWizard) {
			EntityWizard wizard = (EntityWizard) target;
			
			// Check if this wizard has already been used today
			if (ItemWhisperingVeil.hasUsedWizardToday(player, wizard.getUniqueID())) {
				player.sendStatusMessage(new TextComponentTranslation("spell.ancientspellcraft:compulsion.wizard_already_used_today"), true);
				return false;
			}
			
			// Ensure wizard trades are initialized by setting the customer first
			wizard.setCustomer(player);
			MerchantRecipeList recipes = wizard.getRecipes(player);
			
			if (recipes != null && !recipes.isEmpty()) {
				// Use the original simple approach but with better initialization
				MerchantRecipe randomRecipe = recipes.get(world.rand.nextInt(recipes.size()));
				ItemStack freeItem = randomRecipe.getItemToSell().copy();
				
				// Only proceed if we actually got a valid item
				if (!freeItem.isEmpty()) {
					if (!player.addItemStackToInventory(freeItem)) {
						player.dropItem(freeItem, false);
					}
					
					// Mark this wizard as used today
					ItemWhisperingVeil.markWizardAsUsedToday(player, wizard.getUniqueID());
					
					player.sendStatusMessage(new TextComponentTranslation("spell.ancientspellcraft:compulsion.wizard_forced_trade", wizard.getName(), freeItem.getDisplayName()), true);
					return true;
				} else {
					player.sendStatusMessage(new TextComponentTranslation("spell.ancientspellcraft:compulsion.wizard_no_valid_trades"), true);
					return false;
				}
			} else {
				player.sendStatusMessage(new TextComponentTranslation("spell.ancientspellcraft:compulsion.wizard_no_trades"), true);
				return false;
			}
		}
		
		// We have a controlled entity, now execute the command
		Entity controlledEntity = EntityUtils.getEntityByUUID(world, controlledEntityUUID);
		
		if (controlledEntity == null) {
			player.sendStatusMessage(new TextComponentTranslation("spell.ancientspellcraft:compulsion.target_lost"), true);
			data.setVariable(CONTROLLED_ENTITY, null);
			return false;
		}
		
		if (controlledEntity.getDistance(player) > getProperty(RANGE).floatValue() * modifiers.get(WizardryItems.range_upgrade)) {
			player.sendStatusMessage(new TextComponentTranslation("spell.ancientspellcraft:compulsion.target_too_far"), true);
			return false;
		}
		
		if (hasBlackTongueAmulet && !world.isRemote) {
			// Black Tongue Amulet: Make controlled entity attack the target
			if (controlledEntity instanceof EntityLivingBase && target instanceof EntityLivingBase) {
				// Make the controlled entity attack the target
				if (controlledEntity instanceof net.minecraft.entity.EntityLiving) {
					net.minecraft.entity.EntityLiving livingEntity = (net.minecraft.entity.EntityLiving) controlledEntity;
					
					// Clear any existing targets first
					livingEntity.setRevengeTarget(null);
					livingEntity.setAttackTarget(null);
					
					// Set the new attack target
					livingEntity.setAttackTarget((EntityLivingBase) target);
					livingEntity.setRevengeTarget((EntityLivingBase) target);
					
					// Force the entity to look at the target
					livingEntity.getLookHelper().setLookPositionWithEntity((EntityLivingBase) target, 30.0F, 30.0F);
					
					// Clear any existing pathfinding to ensure the entity focuses on attacking
					livingEntity.getNavigator().clearPath();
					
					// Force the entity to move towards the target and attack
					double distance = livingEntity.getDistance((EntityLivingBase) target);
					if (distance > 2.0) {
						// If target is far, move towards it
						livingEntity.getNavigator().tryMoveToEntityLiving((EntityLivingBase) target, 1.0D);
					} else {
						// If target is close, attack it directly
						livingEntity.attackEntityAsMob((EntityLivingBase) target);
						livingEntity.swingArm(EnumHand.MAIN_HAND);
					}
					
					// Debug: Check if targets are actually set
					player.sendStatusMessage(new TextComponentTranslation("spell.ancientspellcraft:compulsion.debug_targets", 
						livingEntity.getAttackTarget() != null ? livingEntity.getAttackTarget().getName() : "null",
						livingEntity.getRevengeTarget() != null ? livingEntity.getRevengeTarget().getName() : "null"), true);
				}
				player.sendStatusMessage(new TextComponentTranslation("spell.ancientspellcraft:compulsion.target_attacking", controlledEntity.getName(), target.getName()), true);
				
				// Keep the controlled entity selected for multiple uses
				return true;
			}
		} else {
			// Normal behavior: Move controlled entity to target location
			if (controlledEntity instanceof EntityLiving) {
				EntityLiving livingEntity = (EntityLiving) controlledEntity;
				livingEntity.setRevengeTarget(null);
				livingEntity.setAttackTarget(null);

			//	((EntityLivingBase)target).addPotionEffect(new PotionEffect(WizardryPotions.mind_trick, 200, 0));

				// Move the entity to the target location
				if (livingEntity instanceof net.minecraft.entity.EntityLiving) {
					((net.minecraft.entity.EntityLiving) livingEntity).getNavigator().clearPath();
					((net.minecraft.entity.EntityLiving) livingEntity).getNavigator().tryMoveToXYZ(target.posX, target.posY, target.posZ, 1.0f);
				}
				
				// Spawn the directional line showing the path
				if (world.isRemote) {
					spawnDirectionalLine(world, livingEntity, new BlockPos(target.posX, target.posY, target.posZ));
				}
				
				player.sendStatusMessage(new TextComponentTranslation("spell.ancientspellcraft:compulsion.target_moved", controlledEntity.getName()), true);
				
				// Keep the controlled entity selected for multiple uses
				return true;
			}
		}
		
		return false;
	}

	@Override
	protected boolean onBlockHit(World world, BlockPos pos, EnumFacing side, Vec3d hit, EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {
		
		if (!(caster instanceof EntityPlayer)) return false;
		
		EntityPlayer player = (EntityPlayer) caster;
		WizardData data = WizardData.get(player);
		
		if (data == null) return false;
		
		UUID controlledEntityUUID = data.getVariable(CONTROLLED_ENTITY);
		boolean hasBlackTongueAmulet = ItemArtefact.isArtefactActive(player, ASItems.amulet_black_tongue);
		boolean hasWhisperingVeil = ItemArtefact.isArtefactActive(player, ASItems.head_whispering_veil);
		boolean isSneaking = player.isSneaking();
		
		// Sneak-cast on block - show message that we need to target an entity
		if (isSneaking) {
			if (!world.isRemote) {
				player.sendStatusMessage(new TextComponentTranslation("spell.ancientspellcraft:compulsion.select_entity_to_control"), true);
			}
			return false;
		}
		
		// Normal cast to command the controlled entity
		if (controlledEntityUUID == null) {
			if (!world.isRemote) {
				player.sendStatusMessage(new TextComponentTranslation("spell.ancientspellcraft:compulsion.no_controlled_entity"), true);
			}
			return false;
		}
		
		// We have a controlled entity, now execute the command on the block
		Entity controlledEntity = EntityUtils.getEntityByUUID(world, controlledEntityUUID);
		
		if (controlledEntity == null) {
			player.sendStatusMessage(new TextComponentTranslation("spell.ancientspellcraft:compulsion.target_lost"), true);
			data.setVariable(CONTROLLED_ENTITY, null);
			return false;
		}
		
		if (controlledEntity.getDistance(player) > getProperty(RANGE).floatValue() * modifiers.get(WizardryItems.range_upgrade)) {
			player.sendStatusMessage(new TextComponentTranslation("spell.ancientspellcraft:compulsion.target_too_far"), true);
			return false;
		}
		
		// Normal behavior: Move controlled entity to block location
		if (controlledEntity instanceof EntityLivingBase) {
			EntityLivingBase livingEntity = (EntityLivingBase) controlledEntity;
			// Move the entity to the block location
			if (livingEntity instanceof net.minecraft.entity.EntityLiving) {
				((net.minecraft.entity.EntityLiving) livingEntity).getNavigator().clearPath();
				((net.minecraft.entity.EntityLiving) livingEntity).getNavigator().tryMoveToXYZ(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, 1.0f);
			}

			// Spawn the directional line showing the path
			if (world.isRemote) {
				spawnDirectionalLine(world, livingEntity, pos);
			}

			player.sendStatusMessage(new TextComponentTranslation("spell.ancientspellcraft:compulsion.target_moved", controlledEntity.getName()), true);

			// Keep the controlled entity selected for multiple uses
			return true;
		}

		return false;
	}

	@Override
	protected boolean onMiss(World world, EntityLivingBase caster, Vec3d origin, Vec3d direction, int ticksInUse, SpellModifiers modifiers) {
		
		if (!(caster instanceof EntityPlayer)) return false;
		
		EntityPlayer player = (EntityPlayer) caster;
		WizardData data = WizardData.get(player);
		
		if (data == null) return false;
		
		UUID controlledEntityUUID = data.getVariable(CONTROLLED_ENTITY);
		boolean hasBlackTongueAmulet = ItemArtefact.isArtefactActive(player, ASItems.amulet_black_tongue);
		boolean hasWhisperingVeil = ItemArtefact.isArtefactActive(player, ASItems.head_whispering_veil);
		boolean isSneaking = player.isSneaking();
		
		// Sneak-cast but missed - show message that we need to target an entity
		if (isSneaking) {
			if (!world.isRemote) {
				player.sendStatusMessage(new TextComponentTranslation("spell.ancientspellcraft:compulsion.select_entity_to_control"), true);
			}
			return false;
		}
		
		// Normal cast but missed
		if (controlledEntityUUID == null) {
			if (!world.isRemote) {
				player.sendStatusMessage(new TextComponentTranslation("spell.ancientspellcraft:compulsion.no_controlled_entity"), true);
			}
		} else {
			// We have a controlled entity but missed - show message
			if (!world.isRemote) {
				if (hasBlackTongueAmulet) {
					player.sendStatusMessage(new TextComponentTranslation("spell.ancientspellcraft:compulsion.select_target_entity"), true);
				} else {
					player.sendStatusMessage(new TextComponentTranslation("spell.ancientspellcraft:compulsion.select_target_location"), true);
				}
			}
		}
		
		return false;
	}

	@Override
	protected void spawnParticle(World world, double x, double y, double z, double vx, double vy, double vz) {
		// Beautiful purple line trail for the Compulsion spell
		ParticleBuilder.create(ParticleBuilder.Type.MAGIC_FIRE)
				.pos(x, y, z)
				.vel(vx * 0.1, vy * 0.1, vz * 0.1) // Slower movement for trail effect
				.clr(0.8f, 0.2f, 0.8f) // Deep purple
				.time(15 + world.rand.nextInt(10)) // Longer lasting particles
				.scale(1.2f + world.rand.nextFloat() * 0.6f)
				.spawn(world);
		
		// Additional smaller particles for the line effect
		ParticleBuilder.create(ParticleBuilder.Type.SPARKLE)
				.pos(x, y, z)
				.vel(vx * 0.05, vy * 0.05, vz * 0.05)
				.clr(0.9f, 0.3f, 0.9f) // Lighter purple
				.time(12 + world.rand.nextInt(8))
				.scale(0.8f)
				.spawn(world);
		
		// Ground-level particles for the floor line effect
		if (world.rand.nextInt(2) == 0) {
			ParticleBuilder.create(ParticleBuilder.Type.MAGIC_FIRE)
					.pos(x, y + 0.1, z) // Slightly above ground
					.vel(0, 0.02, 0) // Gentle upward drift
					.clr(0.7f, 0.1f, 0.7f) // Darker purple for ground effect
					.time(20 + world.rand.nextInt(15))
					.scale(0.6f + world.rand.nextFloat() * 0.4f)
					.spawn(world);
		}
	}
	
	/**
	 * Spawns a directional line showing the path the controlled entity will take
	 */
	private void spawnDirectionalLine(World world, EntityLivingBase controlledEntity, BlockPos targetPos) {
		if (controlledEntity == null || targetPos == null) return;
		
		Vec3d start = controlledEntity.getPositionVector().add(0, 0.1, 0); // Slightly above ground
		Vec3d end = new Vec3d(targetPos.getX() + 0.5, targetPos.getY() + 0.1, targetPos.getZ() + 0.5);
		Vec3d direction = end.subtract(start).normalize();
		double distance = start.distanceTo(end);
		
		// Create particles along the path
		int particleCount = (int) (distance * 4); // 4 particles per block
		for (int i = 0; i < particleCount; i++) {
			double progress = (double) i / particleCount;
			Vec3d pos = start.add(direction.scale(distance * progress));
			
			// Main path particles
			ParticleBuilder.create(ParticleBuilder.Type.MAGIC_FIRE)
					.pos(pos.x, pos.y, pos.z)
					.vel(0, 0.01, 0) // Gentle upward drift
					.clr(0.6f, 0.1f, 0.8f) // Purple path color
					.time(30 + world.rand.nextInt(20)) // Long lasting
					.scale(0.8f + world.rand.nextFloat() * 0.4f)
					.spawn(world);
			
			// Occasional sparkle particles along the path
			if (world.rand.nextInt(3) == 0) {
				ParticleBuilder.create(ParticleBuilder.Type.SPARKLE)
						.pos(pos.x, pos.y + 0.1, pos.z)
						.vel(0, 0.02, 0)
						.clr(0.8f, 0.3f, 1.0f) // Bright purple sparkles
						.time(20 + world.rand.nextInt(10))
						.scale(0.6f)
						.spawn(world);
			}
		}
	}
} 