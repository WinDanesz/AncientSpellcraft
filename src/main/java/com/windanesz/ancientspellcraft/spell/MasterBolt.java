package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.Settings;
import com.windanesz.ancientspellcraft.block.BlockLightning;
import com.windanesz.ancientspellcraft.entity.projectile.EntityMasterBolt;
import com.windanesz.ancientspellcraft.registry.ASBlocks;
import com.windanesz.ancientspellcraft.registry.ASItems;
import com.windanesz.ancientspellcraft.util.ASUtils;
import electroblob.wizardry.Wizardry;
import electroblob.wizardry.data.IStoredVariable;
import electroblob.wizardry.data.Persistence;
import electroblob.wizardry.data.WizardData;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.registry.WizardryPotions;
import electroblob.wizardry.registry.WizardrySounds;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.BlockUtils;
import electroblob.wizardry.util.InventoryUtils;
import electroblob.wizardry.util.Location;
import electroblob.wizardry.util.NBTExtras;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class MasterBolt extends Spell {

	public static final IStoredVariable<List<Location>> MASTER_BOLT_LOCATIONS_KEY = new IStoredVariable.StoredVariable<List<Location>, NBTTagList>("masterBoltLocationsKey",
			s -> NBTExtras.listToNBT(s, Location::toNBT), t -> new ArrayList<>(NBTExtras.NBTToList(t, Location::fromNBT)), Persistence.RESPAWN).setSynced();
	public static final IStoredVariable<Integer> COUNTDOWN_KEY = IStoredVariable.StoredVariable.ofInt("masterBoltTpCountdown", Persistence.NEVER).withTicker(MasterBolt::update);

	public MasterBolt() {
		super(AncientSpellcraft.MODID, "master_bolt", SpellActions.POINT_UP, false);
		WizardData.registerStoredVariables(MASTER_BOLT_LOCATIONS_KEY, COUNTDOWN_KEY);
		addProperties(DAMAGE);
	}

	public static void storeLocation(World world, EntityPlayer player, BlockPos pos) {
		WizardData data = WizardData.get(player);

		Location here = new Location(pos, player.dimension);

		List<Location> locations = data.getVariable(MASTER_BOLT_LOCATIONS_KEY);

		if (locations != null && !locations.isEmpty()) {
			BlockPos posI = locations.get(0).pos;
			if (world.getBlockState(posI).getBlock() == ASBlocks.master_bolt) {
				world.setBlockToAir(posI);
			}
		}

		locations = new ArrayList<>();
		locations.add(here);
		data.setVariable(MASTER_BOLT_LOCATIONS_KEY, locations);
		data.sync();
		if (!world.isRemote) {
			player.sendStatusMessage(new TextComponentTranslation("spell." + AncientSpellcraft.MODID + ":master_bolt.remember"), true);
		}
	}

	@Override
	public boolean cast(World world, EntityPlayer caster, EnumHand hand, int ticksInUse, SpellModifiers modifiers) {
		if (caster != null) {

			WizardData data = WizardData.get(caster);

			if (data != null) {

				List<Location> locations = data.getVariable(MASTER_BOLT_LOCATIONS_KEY);
				if (!caster.isSneaking()) {
					boolean removed = false;
					if (InventoryUtils.doesPlayerHaveItem(caster, ASItems.master_bolt)) {
						for (ItemStack stack : caster.inventory.mainInventory) {
							if (stack.getItem() == ASItems.master_bolt) {
								stack.shrink(1);
								removed = true;
							}
						}

						for (ItemStack stack : caster.inventory.armorInventory) {
							if (stack.getItem() == ASItems.master_bolt) {
								stack.shrink(1);
								removed = true;
							}
						}

						for (ItemStack stack : caster.inventory.offHandInventory) {
							if (stack.getItem() == ASItems.master_bolt) {
								stack.shrink(1);
								removed = true;
							}
						}
						if (removed) {
							if (!world.isRemote) {
								float speed = 1.5f;
								if (!caster.onGround) {
									speed *= 3f;
								}
								EntityMasterBolt masterBolt = new EntityMasterBolt(world);
								masterBolt.aim(caster, speed);
								masterBolt.setCaster(caster);
								world.spawnEntity(masterBolt);
							}
						}
						return false;
					}

					caster.playSound(WizardrySounds.ENTITY_SPARK_BOMB_THROW, 0.5F, 0.4F / (caster.world.rand.nextFloat() * 0.4F + 0.8F));

					caster.getCooldownTracker().setCooldown(ASItems.master_bolt, 20);

					if (!world.isRemote) {
						float speed = 1.5f;
						if (!caster.onGround) {
							speed *= 3f;
						}
						EntityMasterBolt masterBolt = new EntityMasterBolt(world);
						masterBolt.aim(caster, speed);
						masterBolt.setCaster(caster);
						world.spawnEntity(masterBolt);
					}


					return true;
				}

				if (caster.isSneaking() && locations != null && locations.isEmpty()) {
					ASUtils.sendMessage(caster, "First, you must cast the spell without sneaking to shoot a bolt.", true);
					return false;
				}
				Location destination = null;
				if (locations != null && !locations.isEmpty()) {
					destination = locations.get(locations.size() - 1);
					data.setVariable(COUNTDOWN_KEY, 20);
					if (true) {
						return true;
					}
				} else {
					return false;
				}
				if (caster.isSneaking() && destination.dimension == caster.dimension) {
					if (world.isRemote) {
						// Rather neatly, the entity can be set here and if it's null nothing will happen.
						//	ParticleBuilder.spawnShockParticles(world, caster.posX, caster.posY + caster.height/2, caster.posZ);
						//	ParticleBuilder.spawnShockParticles(world, destination.pos.getX(), destination.pos.getY(), destination.pos.getZ());
					}
					if (world.getBlockState(destination.pos).getBlock() == ASBlocks.master_bolt) {
						if (!world.isRemote) {
							world.setBlockToAir(destination.pos);
							if (!InventoryUtils.doesPlayerHaveItem(caster, ASItems.master_bolt)) {
								ASUtils.giveStackToPlayer(caster, new ItemStack(ASItems.master_bolt));
							}
						}
					}

					return true;
				}
			}
		}
		return false;
	}


	private static Integer update(EntityPlayer player, Integer countdown) {

		if (countdown == null) {return 0;}

		if (!player.world.isRemote) {

			WizardData data = WizardData.get(player);

			List<Location> locations = data.getVariable(MASTER_BOLT_LOCATIONS_KEY);
			if (locations == null || locations.isEmpty()) {return 0;}

			Location destination = locations.get(0);
			if (countdown > 1 && player.getDistanceSq(destination.pos) > 2f) {
				player.hurtTime = 20;
				player.addPotionEffect(new PotionEffect(WizardryPotions.static_aura, 40));
				player.world.setBlockToAir(destination.pos);
				// Calculate the distance between current position and destination
				double distanceX = destination.pos.getX() + 0.5 - player.posX;
				double distanceY = destination.pos.getY() - player.posY;
				double distanceZ = destination.pos.getZ() + 0.5 - player.posZ;

				// Calculate the increment for each step
				double stepX = distanceX / countdown;
				double stepY = distanceY / countdown;
				double stepZ = distanceZ / countdown;

				// Teleport the player in 10 steps
				for (int i = 0; i < 10; i++) {
					double newX = player.posX + stepX;
					double newY = player.posY + stepY;
					double newZ = player.posZ + stepZ;
					BlockPos pos = new BlockPos(newX, newY, newZ);
					player.setPositionAndUpdate(newX, newY, newZ);
					if (player.world.isAirBlock(pos)) {
						player.world.setBlockState(pos, ASBlocks.lightning_block.getDefaultState());
						BlockLightning.setProperties(player.world, pos, player, 60,3);
					}

					if (pos == player.getPosition() || player.getDistanceSq(destination.pos) < 2f) {
						player.world.createExplosion(player, player.posX, player.posY, player.posZ, Math.min(2.0f, player.fallDistance * 0.05f),
								Settings.generalSettings.master_bolt_impact_deals_block_damage &&
								BlockUtils.canBreakBlock(player, player.world, destination.pos));
						locations.remove(destination);
						data.setVariable(MASTER_BOLT_LOCATIONS_KEY, null);
						data.sync();
						float radius = Math.max(1f, Math.min(3, player.fallDistance * 0.2f));
						for (BlockPos pos1 : BlockUtils.getBlockSphere(player.getPosition(), radius)) {
							if (player.world.isAirBlock(pos1)) {
								player.world.setBlockState(pos1, ASBlocks.lightning_block.getDefaultState());
								BlockLightning.setProperties(player.world, pos1, player, 90, 5);
							}
						}
						ASUtils.giveStackToPlayer(player, new ItemStack(ASItems.master_bolt));
						player.fallDistance = 0;
						return 0;
					}
				}
			}
			if (countdown == 1) {
				locations.remove(destination);
				data.setVariable(MASTER_BOLT_LOCATIONS_KEY, null);
				data.sync();
			}
			if (countdown > 0) {
				countdown--;
			}
		} else if (countdown == 1) {
			Wizardry.proxy.playBlinkEffect(player);
		}

		return countdown;
	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == ASItems.ancient_spellcraft_spell_book || item == ASItems.ancient_spellcraft_scroll;
	}
}
