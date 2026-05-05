package com.windanesz.ancientspellcraft.handler;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.Settings;
import com.windanesz.ancientspellcraft.data.RitualDiscoveryData;
import com.windanesz.ancientspellcraft.entity.ai.EntitySummonAIFollowOwner;
import com.windanesz.ancientspellcraft.entity.projectile.EntityContingencyProjectile;
import com.windanesz.ancientspellcraft.entity.projectile.EntityMetamagicProjectile;
import com.windanesz.ancientspellcraft.integration.artemislib.ASArtemisLibIntegration;
import com.windanesz.ancientspellcraft.integration.baubles.ASBaublesIntegration;
import com.windanesz.ancientspellcraft.item.*;
import com.windanesz.ancientspellcraft.potion.PotionMetamagicEffect;
import com.windanesz.ancientspellcraft.registry.*;
import com.windanesz.ancientspellcraft.ritual.ElementalAttunement;
import com.windanesz.ancientspellcraft.ritual.Ritual;
import com.windanesz.ancientspellcraft.spell.*;
import com.windanesz.ancientspellcraft.util.ASUtils;
import electroblob.wizardry.Wizardry;
import electroblob.wizardry.constants.Constants;
import electroblob.wizardry.constants.Element;
import electroblob.wizardry.constants.SpellType;
import electroblob.wizardry.constants.Tier;
import electroblob.wizardry.data.IStoredVariable;
import electroblob.wizardry.data.Persistence;
import electroblob.wizardry.data.WizardData;
import electroblob.wizardry.entity.construct.EntityBubble;
import electroblob.wizardry.entity.living.EntityEvilWizard;
import electroblob.wizardry.entity.living.EntitySkeletonMinion;
import electroblob.wizardry.entity.living.ISpellCaster;
import electroblob.wizardry.entity.living.ISummonedCreature;
import electroblob.wizardry.entity.projectile.EntityMagicProjectile;
import electroblob.wizardry.event.ArtefactCheckEvent;
import electroblob.wizardry.event.ResurrectionEvent;
import electroblob.wizardry.event.SpellBindEvent;
import electroblob.wizardry.event.SpellCastEvent;
import electroblob.wizardry.integration.DamageSafetyChecker;
import electroblob.wizardry.item.*;
import electroblob.wizardry.potion.Curse;
import electroblob.wizardry.registry.Spells;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.registry.WizardryPotions;
import electroblob.wizardry.registry.WizardrySounds;
import electroblob.wizardry.spell.*;
import electroblob.wizardry.util.*;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.lang.reflect.Field;
import java.util.*;
import java.util.Set;

import static com.windanesz.ancientspellcraft.item.EnumElementalSwordEffect.getAngleBetweenEntities;
import static electroblob.wizardry.constants.Constants.*;
import static electroblob.wizardry.item.ItemArtefact.getActiveArtefacts;
import static electroblob.wizardry.item.ItemArtefact.isArtefactActive;

@Mod.EventBusSubscriber
public class ASEventHandler {

	public static final float COST_REDUCTION_PER_ARMOUR = 0.15f;
	public static final IStoredVariable<Integer> SPELL_ID = IStoredVariable.StoredVariable.ofInt("artefactEternitySpell", Persistence.ALWAYS);
	public static final IStoredVariable<Integer> COUNTDOWN_KEY = IStoredVariable.StoredVariable.ofInt("artefactEternityCountdown", Persistence.NEVER).withTicker(ASEventHandler::update);

	static {
		WizardData.registerStoredVariables(COUNTDOWN_KEY, SPELL_ID);
	}

	private ASEventHandler() {} // No instances!

	@SubscribeEvent
	public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
		if (!event.getWorld().isRemote && event.getItemStack().getItem() == WizardryItems.identification_scroll
				&& event.getEntityLiving() instanceof EntityPlayer) {
			EnumHand otherHand = event.getHand() == EnumHand.MAIN_HAND ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;
			ItemStack otherItemStack = event.getEntityLiving().getHeldItem(otherHand);
			if (otherItemStack.getItem() instanceof ItemRitualBook) {
				EntityPlayer player = (EntityPlayer) event.getEntityLiving();
				Ritual ritual = ItemRitualBook.getRitual(otherItemStack);
				if (!RitualDiscoveryData.hasRitualBeenDiscovered(player, ritual)) {
					RitualDiscoveryData.addKnownRitual(player, ritual);
					if (!player.isCreative()) {event.getItemStack().shrink(1);}
					ASUtils.sendMessage(player, "ritual.discover", false, ritual.getNameForTranslationFormatted());
					event.setCanceled(true);
				}
			}
		}
	}

	@SubscribeEvent
	public static void onLivingUpdateEvent(LivingEvent.LivingUpdateEvent event) {
		if (event.getEntityLiving().isPotionActive(ASPotions.magma_strider)) {
			EntityLivingBase entity = event.getEntityLiving();
			PotionEffect effect = entity.getActivePotionEffect(ASPotions.magma_strider);
			if (entity.isInLava() && (entity instanceof EntityPlayer) && !((EntityPlayer) entity).isCreative()) {
				entity.motionX *= (1.7f + 0.03 * effect.getAmplifier());
				entity.motionZ *= (1.7f + 0.03 * effect.getAmplifier());

				if (entity.isSneaking() && entity.motionY < 0 || entity.motionY > 0) {
					entity.motionY *= (1.7f + 0.03 * effect.getAmplifier());
				}
			}
		}

		if (event.getEntityLiving().isPotionActive(ASPotions.aquatic_agility)) {
			PotionEffect effect = event.getEntityLiving().getActivePotionEffect(ASPotions.aquatic_agility);
			if (event.getEntityLiving().isInWater()) {
				event.getEntityLiving().motionX *= (1.1f + 0.025 * effect.getAmplifier());
				event.getEntityLiving().motionZ *= (1.1f + 0.025 * effect.getAmplifier());

				if (event.getEntityLiving().isSneaking() && event.getEntityLiving().motionY < 0 || event.getEntityLiving().motionY > 0) {
					event.getEntityLiving().motionY *= (1.1f + 0.025 * effect.getAmplifier());
				}
			}
		}

		if (event.getEntityLiving() instanceof EntityPlayer && !event.getEntityLiving().onGround && event.getEntityLiving().fallDistance >= 4f) {

			EntityPlayer player = (EntityPlayer) event.getEntityLiving();

			WizardData data = WizardData.get(player);
			if (data != null) {

				NBTTagCompound activeContingencies = data.getVariable(Contingency.ACTIVE_CONTINGENCIES);
				if (activeContingencies != null) {

					// Contingency - Fall
					if (activeContingencies.hasKey(Contingency.Type.FALL.spellName)) {
						Contingency.tryCastContingencySpell(player, data, Contingency.Type.FALL);
					}
				}
			}
		}

		// head_mask_of_silence: apply Magical Exhaustion I to nearby players and spell-casting NPCs
		if (!event.getEntityLiving().world.isRemote
				&& event.getEntityLiving() instanceof EntityPlayer
				&& event.getEntityLiving().ticksExisted % 20 == 0) {
			EntityPlayer wearer = (EntityPlayer) event.getEntityLiving();
			if (ItemArtefact.isArtefactActive(wearer, ASItems.head_mask_of_silence)) {
				for (EntityLivingBase nearby : EntityUtils.getEntitiesWithinRadius(8, wearer.posX, wearer.posY, wearer.posZ, wearer.world, EntityLivingBase.class)) {
					if (nearby == wearer) continue;
					if (nearby instanceof EntityPlayer || nearby instanceof ISpellCaster) {
						nearby.addPotionEffect(new PotionEffect(ASPotions.magical_exhaustion, 40, 0));
					}
				}
			}
		}
	}

	@SubscribeEvent
	public static void onLivingFall(LivingFallEvent event) {
		if (!(event.getEntityLiving() instanceof EntityPlayer)) return;

		EntityPlayer player = (EntityPlayer) event.getEntityLiving();
		NBTTagCompound data = player.getEntityData();

		if (data.getBoolean(PacketControlInput.VAULT_FALL_BUFFER_TAG)) {
			data.removeTag(PacketControlInput.VAULT_FALL_BUFFER_TAG);
			// Remove roughly the extra fall distance introduced by the vault jump.
			event.setDistance(Math.max(0.0f, event.getDistance() - 4.0f));
		}
	}

	@SubscribeEvent
	public static void onPlayerSleep(PlayerSleepInBedEvent event) {
		if (event.getEntityPlayer().isPotionActive(ASPotions.curse_of_insomnia)) {
			ASUtils.sendMessage(event.getEntityPlayer(), "message.ancientspellcraft:curse.of_insomnia.sleep_prevented", true);
			event.setResult(EntityPlayer.SleepResult.OTHER_PROBLEM);
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public static void onPotionAddedEvent(PotionEvent.PotionAddedEvent event) {
		if (event.getEntityLiving() instanceof EntityPlayer) {
			List<Potion> effects = new ArrayList<>();
			for (String entry : Settings.generalSettings.immobility_contingency_effects) {
				try {
					Potion potion = ForgeRegistries.POTIONS.getValue(new ResourceLocation(entry));
					effects.add(potion);
				}
				catch (Exception e) {
					AncientSpellcraft.logger.warn("No such potion type named as " + entry);
				}
			}
			if (event.getPotionEffect().getPotion() != null && effects.contains(event.getPotionEffect().getPotion())) {

				EntityPlayer player = (EntityPlayer) event.getEntityLiving();

				WizardData data = WizardData.get(player);
				if (data != null) {

					NBTTagCompound activeContingencies = data.getVariable(Contingency.ACTIVE_CONTINGENCIES);
					if (activeContingencies != null) {

						// Contingency - Immobility
						if (activeContingencies.hasKey(Contingency.Type.IMMOBILITY.spellName)) {
							Contingency.tryCastContingencySpell(player, data, Contingency.Type.IMMOBILITY);
						}
					}
				}
			}
		}
	}

	@SubscribeEvent
	public static void onEntityMountEvent(EntityMountEvent event) {
		if (event.getEntityBeingMounted() instanceof EntityBubble && event.getEntityMounting() instanceof EntityPlayer && event.isMounting()) {

			EntityPlayer player = (EntityPlayer) event.getEntityMounting();

			WizardData data = WizardData.get(player);
			if (data != null) {

				NBTTagCompound activeContingencies = data.getVariable(Contingency.ACTIVE_CONTINGENCIES);
				if (activeContingencies != null) {

					// Contingency - Immobility
					if (activeContingencies.hasKey(Contingency.Type.IMMOBILITY.spellName)) {

						// A little bonus for this contingency.. allowing the ebwizardry:cure_effects spell to remove these effects
						String spellName = activeContingencies.getString(Contingency.Type.IMMOBILITY.spellName);
						Contingency.tryCastContingencySpell(player, data, Contingency.Type.IMMOBILITY);
						if (spellName.equals(Spells.cure_effects.getUnlocalisedName())) {
							// TODO not casting because cure effects can only be cased if you have at leats one effect..
							event.setCanceled(true);
							event.getEntityBeingMounted().setDead();
						}
					}
				}
			}
		}
	}

	/////////////////////////////// ARTEFACT EVENTS ///////////////////////////////

	@SubscribeEvent(priority = EventPriority.LOW) // Low priority in case the event gets cancelled at default priority
	public static void onLivingAttackEvent(LivingAttackEvent event) {
		if (event.getEntity() instanceof EntityPlayer) {
			if (((EntityPlayer) event.getEntity()).isPotionActive(ASPotions.burrow)) {
				if ("inWall".equals(event.getSource().getDamageType())) {
					event.setCanceled(true);
				}
			}
		}

		if (event.getSource() != null && event.getSource().getTrueSource() instanceof EntityLivingBase) {

			EntityLivingBase attacker = (EntityLivingBase) event.getSource().getTrueSource();

			if (!attacker.getHeldItemMainhand().isEmpty() && ImbueWeapon.isSword(attacker.getHeldItemMainhand())) {

				int level = EnchantmentHelper.getEnchantmentLevel(ASEnchantments.static_charge,
						attacker.getHeldItemMainhand());
				if (level > 0 && event.getEntityLiving().world.isRemote) {
					// Particle effect
					for (int i = 0; i < 5; i++) {
						ParticleBuilder.create(ParticleBuilder.Type.SPARK, event.getEntityLiving()).spawn(event.getEntityLiving().world);
					}
				}
			}
		}

	}

	private static void onMetaMagicFinished(EntityPlayer player, Spell spell, Potion effect) {
		setCooldown(player, spell);

		if (!player.world.isRemote) {
			float chance = 0;
			for (ItemArtefact ring : ItemArtefact.getActiveArtefacts(player, ItemArtefact.Type.RING)) {
				if (ring == ASItems.ring_metamagic_preserve) {
					chance += 0.33f;
				}
			}
			if (chance == 0 || chance > 0 && player.world.rand.nextFloat() < chance) {
				player.removePotionEffect(effect);
			}
		}
	}

	private static void setCooldown(EntityPlayer player, Spell spell) {

		List<ItemStack> wands = ASUtils.getAllHotbarWands(player);
		if (wands != null && !wands.isEmpty()) {

			for (ItemStack wand : wands) {
				int index = 0;

				for (Spell currentSpell : WandHelper.getSpells(wand)) {
					try {

						int[] cooldowns = WandHelper.getCooldowns(wand);

						if (cooldowns.length == 0) {
							int count = WandHelper.getSpells(wand).length - 1;
							cooldowns = new int[count];
						}

						if (currentSpell == spell) {

							int[] maxCooldowns = WandHelper.getMaxCooldowns(wand);
							if (maxCooldowns.length == 0) {
								int count = WandHelper.getSpells(wand).length - 1;
								maxCooldowns = new int[count];
							}

							cooldowns[index] = 1200;
							maxCooldowns[index] = 1200;
							WandHelper.setCooldowns(wand, cooldowns);
							WandHelper.setMaxCooldowns(wand, maxCooldowns);
						}
						index++;
					}
					catch (Exception e) {
						AncientSpellcraft.logger.error("Failed to set a wand cooldown");
					}
				}
			}
		}
	}

	private static int update(EntityPlayer player, Integer countdown) {
		if (countdown == null) {return 0;}

		if (!player.world.isRemote) {

			WizardData data = WizardData.get(player);

			Integer spellId = data.getVariable(SPELL_ID);

			if (spellId == null) {return 0;}

			Spell spell = Spell.byMetadata(spellId);

			if (countdown == 1) {

				//DOSTUFF

				if (ItemArtefact.findMatchingWandAndCast(player, spell)) {
				}
			}
			if (countdown > 0) {
				countdown--;
			}
		}
		return countdown;
	}

	@SubscribeEvent
	public static void onLivingHurtEvent(LivingHurtEvent event) {

		if (event.getEntity() instanceof EntityPlayer) {

			EntityPlayer player = (EntityPlayer) event.getEntity();

			if (!player.world.isRemote && player.isPotionActive(ASPotions.wizard_shield)) {
				PotionEffect effect = player.getActivePotionEffect(ASPotions.wizard_shield);
				if (effect != null) {
					float oldAmount = event.getAmount();
					int oldTimer = effect.getDuration();
					int amplifier = effect.getAmplifier();

					player.setArrowCountInEntity(0);
					int usedUp = (int) (event.getAmount());
					int newAmplifier = amplifier - usedUp;
					float newAmount = (int) (event.getAmount()) - (amplifier + 1);
					if (newAmount < 0) {
						amplifier = (int) -newAmount;
						event.setCanceled(true);

					}
					if (newAmplifier > 0) {
						if (event.getSource().getImmediateSource() instanceof EntityArrow) {
							event.getSource().getImmediateSource().setDead();
						}

						player.removePotionEffect(ASPotions.wizard_shield);
						player.addPotionEffect(new PotionEffect(ASPotions.wizard_shield, oldTimer, newAmplifier));

					} else {
						player.removePotionEffect(ASPotions.wizard_shield);
					}
					event.setAmount(newAmount);
				}
			}

			for (ItemArtefact artefact : getActiveArtefacts(player)) {

				if (artefact == ASItems.amulet_time_knot) {
					if (player.isPotionActive(ASPotions.time_knot) && (player.getHealth() - event.getAmount() <= 0F)) {
						if (!player.getCooldownTracker().hasCooldown(ASItems.amulet_time_knot)) {
							TimeKnot.loopPlayer(player);
							event.setCanceled(true);
							player.extinguish();
							player.getCooldownTracker().setCooldown(ASItems.amulet_time_knot, 6000);
						}
					}
				}

				if (artefact == ASItems.amulet_time_slow && !player.getCooldownTracker().hasCooldown(ASItems.amulet_time_slow)) {
					if (!player.world.isRemote && (player.getHealth() <= 6 || (player.getHealth() - event.getAmount() <= 6))) {
						player.addPotionEffect(new PotionEffect(WizardryPotions.slow_time, 120));
						player.getCooldownTracker().setCooldown(ASItems.amulet_time_slow, 9600);
					}
				} else if (artefact == ASItems.charm_cryostasis) {
					if ((player.getHealth() <= 6 || (player.getHealth() - event.getAmount() <= 6)) && player.world.rand.nextFloat() < 0.25f) {
						ASSpells.cryostasis.cast(player.world, player, player.getActiveHand(), 0, new SpellModifiers());
					}
				} else if (artefact == ASItems.ring_protector) {
					if ((player.getHealth() <= 8 || (player.getHealth() - event.getAmount() <= 6)) && player.world.rand.nextFloat() < 0.5f) {
						boolean shouldContinue = true;
						for (ItemStack wand : ASUtils.getAllHotbarWands(player)) {
							if (!shouldContinue) {break;}
							Spell[] spells = WandHelper.getSpells(wand);
							List<Spell> minions = new ArrayList<>();
							List<Integer> indexes = new ArrayList<>();

							int index = 0;
							for (Spell spell : spells) {
								if (spell instanceof SpellMinion) {
									minions.add(spell);
									indexes.add(index);
								}
								index++;
							}

							if (!minions.isEmpty()) {
								int currIndex = indexes.size() == 1 ? 0 : player.world.rand.nextInt(indexes.size() - 1);
								Spell spell = minions.get(currIndex);

								// get modifiers
								SpellModifiers modifiers = new SpellModifiers();

								if (WizardData.get(player) != null) {
									modifiers = WizardData.get(player).itemCastingModifiers;
								} else {
									modifiers = ((ItemWand) wand.getItem()).calculateModifiers(wand, player, spell); // Fallback to the old way, should never be used
								}

								int[] cooldowns = WandHelper.getCooldowns(wand);
								if (cooldowns == null || cooldowns.length == spells.length) {
									WandHelper.selectSpell(wand, currIndex);
									shouldContinue = !(((ItemWand) wand.getItem()).cast(wand, spell, player, EnumHand.MAIN_HAND, 0, modifiers));
									if (!shouldContinue) {
										WandHelper.setCooldowns(wand, cooldowns);

									}
								}
							}
						}
					}
				} else if (artefact == ASItems.ring_berserker) {
					if ((player.getHealth() <= 6 || (player.getHealth() - event.getAmount() <= 6))) {

						if (!player.world.isRemote && !player.isPotionActive(MobEffects.STRENGTH)) {
							player.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 300)); // 15 seconds of strength
						}
					}
				} else if (artefact == ASItems.amulet_rabbit) {
					if (!player.world.isRemote && player.world.rand.nextFloat() < 0.25f) {
						if (!player.isPotionActive(MobEffects.SPEED)) {
							player.addPotionEffect(new PotionEffect(MobEffects.SPEED, 200)); // 10 seconds of speed
						}
						if (!player.isPotionActive(MobEffects.WEAKNESS)) {
							player.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 200, 1)); // 10 seconds of weakness
						}
					}
				} else if (artefact == ASItems.amulet_shield && !player.getCooldownTracker().hasCooldown(ASItems.amulet_shield)) {
					if (event.getAmount() > 1) {
                        if (!player.world.isRemote) {
                            player.addPotionEffect(new PotionEffect(ASPotions.wizard_shield, 100, 15));
                        }
						player.getCooldownTracker().setCooldown(ASItems.amulet_shield, 3600); // 3 mins cd
					}
				} else if (artefact == ASItems.belt_soul_scorch && event.getSource().getImmediateSource() instanceof EntityLivingBase) {
					if (!player.world.isRemote) {
                        ((EntityLivingBase) event.getSource().getImmediateSource()).addPotionEffect(new PotionEffect(ASPotions.soul_scorch, 60));
                    }
				} else if (artefact == ASItems.ring_undeath && !player.isPotionActive(WizardryPotions.curse_of_undeath)) {
					if (player.getHealth() - event.getAmount() <= 0 && !player.getCooldownTracker().hasCooldown(ASItems.ring_undeath)) {
                        if (!player.world.isRemote) {
                            player.addPotionEffect(new PotionEffect(WizardryPotions.curse_of_undeath, Integer.MAX_VALUE, 0));
                            ASUtils.sendMessage(player, "item.ancientspellcraft:ring_undeath.resurrect", true);
                            player.heal(player.getMaxHealth() * Settings.generalSettings.ring_of_undeath_heal_amount);
                        }
                        player.getCooldownTracker().setCooldown(ASItems.ring_undeath, 6000);
                        event.setAmount(0);
					}
				} else if (artefact == ASItems.amulet_elemental_defense) {
					List<ItemStack> amuletz = ASBaublesIntegration.getEquippedArtefactStacks(player, ItemArtefact.Type.AMULET);
					if (amuletz.size() == 1) {
						ItemStack amulet = amuletz.get(0);
						if (AbstractItemArtefactWithSlots.getItemForSlot(amulet, 0).getItem() instanceof ItemCrystal) {
							ItemStack crystal = AbstractItemArtefactWithSlots.getItemForSlot(amulet, 0);
							Optional<Element> e = ASUtils.getDamageTypeElement(event.getSource().getDamageType());
							if (e.isPresent() && Element.values()[crystal.getMetadata()] == e.get()) {
								event.setAmount(event.getAmount() * 0.7f);
							}
						}
					}
				}
			}

			// Contingency - Damage
			if (event.getAmount() >= 0) {

				WizardData data = WizardData.get(player);
				if (data != null) {

					NBTTagCompound activeContingencies = data.getVariable(Contingency.ACTIVE_CONTINGENCIES);
					if (activeContingencies != null) {

						if (Contingency.isFireDamageSource(event.getSource()) && activeContingencies.hasKey(Contingency.Type.FIRE.spellName)) {
							// Contingency - Fire
							Contingency.tryCastContingencySpell(player, data, Contingency.Type.FIRE);
						} else if (event.getSource() == DamageSource.DROWN && activeContingencies.hasKey(Contingency.Type.DROWNING.spellName)) {
							// Contingency - Drowning
							Contingency.tryCastContingencySpell(player, data, Contingency.Type.DROWNING);
						} else {
							// Contingency - Damage
							if (activeContingencies.hasKey(Contingency.Type.DAMAGE.spellName)) {
								Contingency.tryCastContingencySpell(player, data, Contingency.Type.DAMAGE);
							}
						}

						// Contingency - Critical Health
						// apply if health is below 25% or health would fall below 25% after taking the damage
						if ((player.getHealth() <= player.getMaxHealth() * 0.25 || player.getHealth() - event.getAmount() <= player.getMaxHealth() * 0.25) &&
								activeContingencies.hasKey(Contingency.Type.CRITICAL_HEALTH.spellName)) {
							Contingency.tryCastContingencySpell(player, data, Contingency.Type.CRITICAL_HEALTH);
						}
					}
				}
			}
		}

        if (!event.getEntity().world.isRemote && event.getEntityLiving().isPotionActive(ASPotions.martyr_beneficial) && event.getEntityLiving() instanceof EntityPlayer
                && !event.getSource().isUnblockable() && !(event.getSource() instanceof IElementalDamage
                && ((IElementalDamage) event.getSource()).isRetaliatory()))
        {

            EntityPlayer player = (EntityPlayer) event.getEntityLiving(); // the beneficial who is attacked
            WizardData data = WizardData.get(player);

            if (data != null) {

                for (Iterator<UUID> iterator = Martyr.getMartyrBoundEntities(data).iterator(); iterator.hasNext(); ) {

                    Entity entity = EntityUtils.getEntityByUUID(player.world, iterator.next()); // the target who will take the damage instead

                    if (entity == null) {iterator.remove();}

                    if (entity instanceof EntityPlayer && ((EntityPlayer) entity).isPotionActive(ASPotions.martyr)) {
                        // Retaliatory effect
                        if (DamageSafetyChecker.attackEntitySafely(entity, MagicDamage.causeDirectMagicDamage(player,
                                        MagicDamage.DamageType.MAGIC, true), event.getAmount(), event.getSource().getDamageType(),
                                DamageSource.MAGIC, false)) {
                            // Sound only plays if the damage succeeds
                            entity.playSound(WizardrySounds.SPELL_CURSE_OF_SOULBINDING_RETALIATE, 1.0F, player.world.rand.nextFloat() * 0.2F + 1.0F);
                        }
                        // cancel the damage
                        event.setCanceled(true);
                    }
                }

            }
        }

        // Static weapon
        if (event.getSource().getTrueSource() instanceof EntityLivingBase) {

            EntityLivingBase attacker = (EntityLivingBase) event.getSource().getTrueSource();

            // Players can only ever attack with their main hand, so this is the right method to use here.
            if (!attacker.getHeldItemMainhand().isEmpty() && ImbueWeapon.isSword(attacker.getHeldItemMainhand())) {

                int level = EnchantmentHelper.getEnchantmentLevel(ASEnchantments.static_charge,
                        attacker.getHeldItemMainhand());

                if (level > 0 && !MagicDamage.isEntityImmune(MagicDamage.DamageType.SHOCK, event.getEntityLiving())) {
                    event.setAmount(event.getAmount() + level * 2);
                }
            }
        }
	}

	@SubscribeEvent
	public static void onPotionApplicableEvent(PotionEvent.PotionApplicableEvent event) {
		if (!event.getEntityLiving().world.isRemote && event.getPotionEffect().getPotion() != ASPotions.tenacity && event.getEntityLiving().isPotionActive(ASPotions.tenacity)) {
			if (!event.getEntityLiving().getTags().contains(event.getPotionEffect().getPotion().getRegistryName().toString())) {

				int tenacityLevel = event.getEntityLiving().getActivePotionEffect(ASPotions.tenacity).getAmplifier() + 1;
				// Calculate the reduction factor based on tenacity level
				double reductionFactor = 1.0 - (0.25 * tenacityLevel); // You can adjust the reduction factor as needed

				event.getEntityLiving().addTag(event.getPotionEffect().getPotion().getRegistryName().toString());
				event.getEntityLiving().addPotionEffect(new PotionEffect(event.getPotionEffect().getPotion(), (int) (event.getPotionEffect().getDuration() * 0.5), event.getPotionEffect().getAmplifier()));
				event.setResult(Event.Result.DENY);
			} else {
				event.getEntityLiving().getTags().remove(event.getPotionEffect().getPotion().getRegistryName().toString());
				//	event.setResult(Event.Result.DENY);
			}
		}

		if ((event.getPotionEffect().getPotion() == ASPotions.astral_projection || event.getPotionEffect().getPotion() == ASPotions.eagle_eye) && !(event.getEntityLiving() instanceof EntityPlayer)) {
			event.setResult(Event.Result.DENY);
		}

		if ((event.getPotionEffect().getPotion() == MobEffects.SLOWNESS) && (event.getEntityLiving() instanceof EntityPlayer
				&& ItemArtefact.isArtefactActive((EntityPlayer) event.getEntityLiving(), ASItems.belt_temporal_anchor))) {
			event.setResult(Event.Result.DENY);
		}

		if (event.getPotionEffect().getPotion() == ASPotions.shrinkage || event.getPotionEffect().getPotion() == ASPotions.growth) {
			if (!ASArtemisLibIntegration.enabled()) {
				// ArtemisLib-dependent potions are not applicable if ArtemisLib is not loaded
				event.setResult(Event.Result.DENY);
			}
		}

		if (!event.getEntityLiving().world.isRemote && event.getEntity() instanceof EntityPlayer) {

			EntityPlayer player = (EntityPlayer) event.getEntity();

			for (ItemArtefact artefact : getActiveArtefacts(player)) {

				if (artefact == ASItems.amulet_poison_resistance) {

					if (event.getPotionEffect().getPotion() == MobEffects.POISON) {
						if (player.world.rand.nextFloat() < 0.5f) {
							event.setResult(Event.Result.DENY);
						}
					}
				} else if (artefact == ASItems.amulet_curse_ward) {
					if (event.getPotionEffect().getPotion() instanceof Curse) {
						event.setResult(Event.Result.DENY);
					}
				} else if (artefact == ASItems.amulet_persistence) {
					if (event.getPotionEffect().getPotion() == ASPotions.shrinkage || event.getPotionEffect().getPotion() == ASPotions.growth) {
						event.setResult(Event.Result.DENY);
					}
				} else if (artefact == ASItems.amulet_cursed_mirror) {
					if (event.getPotionEffect().getPotion() instanceof Curse && player.world.rand.nextBoolean()) {
						for (EntityLivingBase target : EntityUtils.getEntitiesWithinRadius(12, player.posX, player.posY, player.posZ, player.world, EntityLivingBase.class)) {
							if (target == player || AllyDesignationSystem.isAllied(player, target)) {
								continue;
							} else if (target instanceof EntityPlayer || target instanceof ISpellCaster) {
								target.addPotionEffect(event.getPotionEffect());
							}
						}
					}
				} else if (artefact == ASItems.amulet_absorb_potion && event.getPotionEffect().getPotion().isBadEffect()) {
					WizardData data = WizardData.get(player);
					String potionName = data.getVariable(AbsorbPotion.EFFECT);
					if (potionName != null && potionName.equals(event.getPotionEffect().getPotion().getRegistryName().toString())) {
						event.setResult(Event.Result.DENY);
					}
				}
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onPotionRemoveEvent(PotionEvent.PotionRemoveEvent event) {

		if (event.getPotion() == ASPotions.magical_exhaustion) {
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public static void onPotionExpiryEvent(PotionEvent.PotionExpiryEvent event) {

		if (event.getEntity() instanceof EntityPlayer) {

			EntityPlayer player = (EntityPlayer) event.getEntity();

			for (ItemArtefact artefact : getActiveArtefacts(player)) {

				if (artefact == ASItems.amulet_pendant_of_eternity) {
					List<ItemStack> amuletList = ASBaublesIntegration.enabled() ? ASBaublesIntegration.getEquippedArtefactStacks(player, ItemArtefact.Type.AMULET) : new ArrayList<>();
					if (!amuletList.isEmpty()) {
						ItemStack pendant = amuletList.get(0);

						Spell spell = getCurrentSpellFromSpellBearingArtefact(ASItems.amulet_pendant_of_eternity, pendant);
						if (spell != Spells.none && spell instanceof SpellBuff) {
							try {
								// FIXME: get rid of reflection :(
								// once the access is public/has getter

								SpellBuff obj = (SpellBuff) spell;
								Field field = ASUtils.ReflectionUtil.getField(obj.getClass(), "potionSet");
								ASUtils.ReflectionUtil.makeAccessible(field);
								Set<Potion> potionset = (Set<Potion>) field.get(obj);

								if (potionset.contains(event.getPotionEffect().getPotion())) {

									WizardData data = WizardData.get(player);
									data.setVariable(COUNTDOWN_KEY, 20);
									data.setVariable(SPELL_ID, spell.metadata());

								}

							}
							catch (Exception e) {
								e.printStackTrace();
							}

						}
					}

				}
			}
		}
	}

	@SubscribeEvent
	public static void onLivingDeathEvent(LivingDeathEvent event) {
		if (event.getSource().getTrueSource() instanceof EntityPlayer && event.getEntity() instanceof EntityLivingBase
				&& ((EntityLivingBase) event.getEntity()).isEntityUndead()) {

			EntityPlayer player = (EntityPlayer) event.getSource().getTrueSource();

			if (ItemArtefact.isArtefactActive(player, ASItems.charm_reanimation)) {
				if (!event.getEntityLiving().world.isRemote) {

					if (player.world.rand.nextFloat() < 0.15f) {
						EntityLivingBase entity = event.getEntityLiving();
						if (entity instanceof EntitySkeleton || entity instanceof EntityZombie) {

							EntitySkeletonMinion skeletonMinion = new EntitySkeletonMinion(entity.world);
							skeletonMinion.setPosition(entity.posX, entity.posY, entity.posZ);
							skeletonMinion.setCaster(player);
							skeletonMinion.setLifetime(600); // 30 seconds lifetime

							// was an archer or not
							if (entity.getHeldItemOffhand().getItem() instanceof ItemBow || entity.getHeldItemMainhand().getItem() instanceof ItemBow) {
								skeletonMinion.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
							} else {
								skeletonMinion.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(Items.WOODEN_SWORD));
							}

							skeletonMinion.setDropChance(EntityEquipmentSlot.MAINHAND, 0.0f);
							entity.world.spawnEntity(skeletonMinion);
						}
					}
				}
			}

		} else if (event.getEntityLiving() instanceof EntityPlayer) {

			EntityPlayer player = (EntityPlayer) event.getEntityLiving();
			// Contingency - Death
			WizardData data = WizardData.get(player);
			if (data != null) {
				NBTTagCompound activeContingencies = data.getVariable(Contingency.ACTIVE_CONTINGENCIES);

				if (activeContingencies != null && activeContingencies.hasKey(Contingency.Type.DEATH.spellName)) {
					Contingency.tryCastContingencySpell(player, data, Contingency.Type.DEATH);
				}
			}

			// Soulbound Wand Upgrade - store items in WizardData
			ItemSoulboundWandUpgrade.storeSoulboundWands(player);

		}

		if (event.getEntityLiving().getEntityData().hasKey(Contingency.ACTIVE_LISTENER_TAG)) {
			NBTTagCompound compound = event.getEntityLiving().getEntityData().getCompoundTag(Contingency.ACTIVE_LISTENER_TAG);
			if (compound != null && compound.hasKey(Contingency.Type.DEATH.spellName)) {
				Contingency.tryCastContingencySpellAsMob(event.getEntityLiving(), Contingency.Type.DEATH);
			}
		}

		if (event.getSource().getTrueSource() instanceof EntityLivingBase && !(event.getSource().getTrueSource() instanceof EntityPlayer)) {
			EntityLivingBase entityLivingBase = (EntityLivingBase) event.getSource().getTrueSource();
			List<ItemStack> items = new ArrayList<>(Arrays.asList(entityLivingBase.getHeldItemMainhand(), entityLivingBase.getHeldItemOffhand()));
			for (ItemStack stack : items) {

				if (stack.getItem() instanceof IManaStoringItem && !((IManaStoringItem) stack.getItem()).isManaFull(stack)
						&& WandHelper.getUpgradeLevel(stack, WizardryItems.siphon_upgrade) > 0) {

					int mana = Constants.SIPHON_MANA_PER_LEVEL
							* WandHelper.getUpgradeLevel(stack, WizardryItems.siphon_upgrade)
							+ entityLivingBase.world.rand.nextInt(Constants.SIPHON_MANA_PER_LEVEL);

					((IManaStoringItem) stack.getItem()).rechargeMana(stack, mana);
					break; // Only recharge one item per kill
				}
			}
		}

		// ring_eternal_servitude — clear stored UUID when the eternal minion is killed
		if (!event.getEntityLiving().world.isRemote && event.getEntityLiving() instanceof ISummonedCreature) {
			UUID dyingUUID = event.getEntityLiving().getUniqueID();
			for (EntityPlayer player : event.getEntityLiving().world.playerEntities) {
				WizardData data = WizardData.get(player);
				if (data != null) {
					String storedUUID = data.getVariable(ItemRingEternalServitude.ETERNAL_MINION_UUID);
					if (storedUUID != null) {
						try {
							if (UUID.fromString(storedUUID).equals(dyingUUID)) {
								data.setVariable(ItemRingEternalServitude.ETERNAL_MINION_UUID, null);
								data.sync();
								break;
							}
						} catch (IllegalArgumentException ignored) {
							data.setVariable(ItemRingEternalServitude.ETERNAL_MINION_UUID, null);
						}
					}
				}
			}
		}

	}

	@SubscribeEvent(priority = EventPriority.LOWEST) // No siphoning if the event is cancelled, that could be exploited...
	public static void onLivingDeathEventLowest(LivingDeathEvent event) {

		if (event.getSource().getTrueSource() instanceof EntityPlayer) {

			EntityPlayer player = (EntityPlayer) event.getSource().getTrueSource();

			if (player.isPotionActive(ASPotions.spell_siphon)) {

				for (ItemStack stack : InventoryUtils.getPrioritisedHotbarAndOffhand(player)) {

					if (stack.getItem() instanceof IManaStoringItem && !((IManaStoringItem) stack.getItem()).isManaFull(stack)) {

						int mana = 5 * (player.getActivePotionEffect(ASPotions.spell_siphon).getAmplifier() + 1);

						((IManaStoringItem) stack.getItem()).rechargeMana(stack, mana);

						break; // Only recharge one item per kill
					}
				}
			}

			if (!event.getEntityLiving().world.isRemote && event.getEntityLiving() instanceof EntityEvilWizard
					&& ItemArtefact.isArtefactActive(player, ASItems.charm_plunderers_mark)) {
				double d0 = event.getEntityLiving().posY - 0.30000001192092896D + (double) event.getEntityLiving().getEyeHeight();
				EntityItem entityitem = new EntityItem(event.getEntityLiving().world, event.getEntityLiving().posX, d0, event.getEntityLiving().posZ, new ItemStack(ASItems.astral_diamond_shard));
				event.getEntityLiving().world.spawnEntity(entityitem);
			}
		}
	}

	@SubscribeEvent
	public static void onProjectileImpactArrowEvent(ProjectileImpactEvent.Arrow event) {

		if (!event.getArrow().world.isRemote) {

			if (event.getArrow().shootingEntity instanceof EntityPlayer) {

				EntityPlayer player = (EntityPlayer) event.getArrow().shootingEntity;

				if (player.world.rand.nextFloat() < 0.2f && ItemArtefact.isArtefactActive(player, ASItems.ring_poison_arrow)) {
					if (event.getRayTraceResult().entityHit instanceof EntityLivingBase) {
						EntityLivingBase target = (EntityLivingBase) event.getRayTraceResult().entityHit;
						if (!target.isPotionActive(MobEffects.POISON)) {
							target.addPotionEffect(new PotionEffect(MobEffects.POISON, 100)); // 5 seconds of poisoning

						}
					}
				}

				//				if (ItemArtefact.isArtefactActive(player, ASItems.charm_ice_arrow)) {
				//					if (event.getRayTraceResult().entityHit instanceof EntityLivingBase) {
				//						EntityLivingBase target = (EntityLivingBase) event.getRayTraceResult().entityHit;
				//						target.addPotionEffect(new PotionEffect(WizardryPotions.frost, 30)); // 1,5 seconds of poisoning
				//					}
				//				}
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onSpellCastPreEvent(SpellCastEvent.Pre event) {

		if (event.getSpell().getTier().ordinal() > 1) {
			for (EntityPlayer player : event.getWorld().playerEntities) {
				if (event.getCaster() instanceof EntityLivingBase && event.getCaster().getDistanceSq(player) < 20 && ItemArtefact.isArtefactActive(player, ASItems.charm_suppression_orb)) {
					if (event.getCaster() instanceof EntityPlayer) {
						ASUtils.sendMessage(event.getCaster(), "item.ancientspellcraft:charm_suppression_orb.message", true);
					}
					event.setCanceled(true);
					return;
				}
			}
		}

		if (event.getCaster() instanceof EntityPlayer && event.getSource() == SpellCastEvent.Source.WAND) {

			EntityPlayer player = (EntityPlayer) event.getCaster();

			int cost = (int) (event.getSpell().getCost() * event.getModifiers().get(SpellModifiers.COST) + 0.1f); // Weird floaty rounding
			ItemStack wand = event.getCaster().getHeldItemMainhand().getItem() instanceof ItemWand ? event.getCaster().getHeldItemMainhand() :
					event.getCaster().getHeldItemOffhand();

			if (wand.getItem() instanceof IManaStoringItem) {

				int wandMana = ((IManaStoringItem) wand.getItem()).getMana(wand);
				if (wandMana <= cost) {

					if (ASBaublesIntegration.enabled()) {
						List<ItemStack> list = ASBaublesIntegration.getEquippedArtefactStacks(player, ItemArtefact.Type.RING);

						if (!list.isEmpty()) {
							for (ItemStack currentStack : list) {
								if (currentStack.getItem() instanceof ItemManaArtefact) {
									int currMana = ((ItemManaArtefact) currentStack.getItem()).getMana(currentStack);
									if (currMana >= cost) {

										// transfer mana
										((ItemManaArtefact) currentStack.getItem()).setMana(currentStack, currMana - cost);
										((ItemWand) wand.getItem()).setMana(wand, wandMana + cost);

										// bit of an arbitrary number, but for continuous spells it transfers 50 more mana or the cont spell would be interrupted immediately
										if (event.getSpell().isContinuous) {
											currMana = ((ItemManaArtefact) currentStack.getItem()).getMana(currentStack);
											if (currMana >= 50) {
												((ItemManaArtefact) currentStack.getItem()).setMana(currentStack, currMana - 50);
												((ItemWand) wand.getItem()).rechargeMana(wand, 50);
											}
										}
										break;
									}
								}
							}
						}
					}
				}
			}
		}

		if (event.getSpell().getType() == SpellType.ALTERATION || event.getSpell().getType() == SpellType.ATTACK || event.getSpell().getType() == SpellType.MINION
				|| event.getSpell().getType() == SpellType.PROJECTILE) {

			List<EntityPlayer> closestPlayers = new ArrayList<>();
			if (event.getCaster() != null) {
				closestPlayers = EntityUtils.getEntitiesWithinRadius(20, event.getCaster().posX, event.getCaster().posY, event.getCaster().posZ, event.getWorld(), EntityPlayer.class);
			}

			if (!closestPlayers.isEmpty()) {
				for (EntityPlayer player : closestPlayers) {

					if (event.getCaster() != player) {

						if (!AllyDesignationSystem.isAllied(player, event.getCaster())) {
							WizardData data = WizardData.get(player);
							if (data != null) {

								NBTTagCompound activeContingencies = data.getVariable(Contingency.ACTIVE_CONTINGENCIES);
								if (activeContingencies != null) {

									// Contingency - HOSTILE_SPELLCAST
									if (activeContingencies.hasKey(Contingency.Type.HOSTILE_SPELLCAST.spellName)) {
										Contingency.tryCastContingencySpell(player, data, Contingency.Type.HOSTILE_SPELLCAST);
									}
								}
							}
						}
					}
				}
			}
		}

		if (event.getCaster() instanceof EntityPlayer) {

			int gems = AbsorbArtefact.getPowerGemCount((EntityPlayer) event.getCaster());
			if (gems > 0) {
				SpellModifiers modifiers = event.getModifiers();
				modifiers.set(SpellModifiers.POTENCY, modifiers.get(SpellModifiers.POTENCY) + gems * 0.01f, false);
			}

			if (!(event.getSpell() instanceof MetaSpellBuff)) {

				EntityPlayer player = (EntityPlayer) event.getCaster();
				Map<Potion, PotionEffect> potionEffectMap = new HashMap();
				Map<Potion, PotionEffect> potionEffects = player.getActivePotionMap();
				potionEffectMap.putAll(potionEffects);

				for (Map.Entry<Potion, PotionEffect> entry : potionEffectMap.entrySet()) {
					Potion potion = entry.getKey();

					if (potion instanceof PotionMetamagicEffect) {

						if (potion.equals(ASPotions.arcane_augmentation)) {
							PotionEffect effect = entry.getValue();
							SpellModifiers modifiers = event.getModifiers();

							float range = modifiers.get(WizardryItems.range_upgrade);
							float blast = modifiers.get(WizardryItems.blast_upgrade);

							int level = effect.getAmplifier() + 1;

							if (level > 0) {
								modifiers.set(WizardryItems.range_upgrade, range + level * Constants.RANGE_INCREASE_PER_LEVEL, true);
								modifiers.set(WizardryItems.blast_upgrade, blast + level * Constants.BLAST_RADIUS_INCREASE_PER_LEVEL, true);
							}
							onMetaMagicFinished(player, ASSpells.arcane_augmentation, ASPotions.arcane_augmentation);

						} else if (potion.equals(ASPotions.intensifying_focus)) {
							PotionEffect effect = entry.getValue();
							SpellModifiers modifiers = event.getModifiers();

							float potency = modifiers.get(SpellModifiers.POTENCY);
							//	float range = modifiers.get(WizardryItems.range_upgrade);
							//	float blast = modifiers.get(WizardryItems.range_upgrade);

							int level = effect.getAmplifier() + 1;
							if (level > 0) {

								modifiers.set(SpellModifiers.POTENCY, potency + level * POTENCY_INCREASE_PER_TIER, true);
								//	modifiers.set(WizardryItems.range_upgrade, blast - level * Constants.RANGE_INCREASE_PER_LEVEL, true);
								//	modifiers.set(WizardryItems.blast_upgrade, range - level * Constants.BLAST_RADIUS_INCREASE_PER_LEVEL, true);
							}
							setCooldown(player, ASSpells.intensifying_focus);
							//player.removePotionEffect(ASPotions.intensifying_focus);
							onMetaMagicFinished(player, ASSpells.intensifying_focus, ASPotions.intensifying_focus);

						} else if (potion.equals(ASPotions.continuity_charm)) {
							PotionEffect effect = entry.getValue();
							SpellModifiers modifiers = event.getModifiers();

							float duration = modifiers.get(WizardryItems.duration_upgrade);
							float cost = modifiers.get(SpellModifiers.COST);
							int level = effect.getAmplifier() + 1;
							if (level > 0) {

								modifiers.set(WizardryItems.duration_upgrade, duration + level * DURATION_INCREASE_PER_LEVEL, true);
								modifiers.set(SpellModifiers.COST, cost + level * COST_REDUCTION_PER_ARMOUR, true);
							}
							onMetaMagicFinished(player, ASSpells.continuity_charm, ASPotions.continuity_charm);
						}

					}
					int level = entry.getValue().getAmplifier() + 1;

					if (potion == ASPotions.spell_blast) {
						SpellModifiers modifiers = event.getModifiers();
						float blast = modifiers.get(WizardryItems.blast_upgrade);
						modifiers.set(WizardryItems.blast_upgrade, blast + level * BLAST_RADIUS_INCREASE_PER_LEVEL, true);
					}
					if (potion == ASPotions.spell_range) {
						SpellModifiers modifiers = event.getModifiers();
						float range = modifiers.get(WizardryItems.range_upgrade);
						modifiers.set(WizardryItems.range_upgrade, range + level * Constants.RANGE_INCREASE_PER_LEVEL, true);

					}
					if (potion == ASPotions.spell_cooldown) {
						SpellModifiers modifiers = event.getModifiers();
						float cooldown = modifiers.get(WizardryItems.cooldown_upgrade);
						modifiers.set(WizardryItems.cooldown_upgrade, cooldown - (level * Constants.COOLDOWN_REDUCTION_PER_LEVEL), true);
					}
					if (potion == ASPotions.spell_duration) {
						SpellModifiers modifiers = event.getModifiers();
						float duration = modifiers.get(WizardryItems.duration_upgrade);
						modifiers.set(WizardryItems.duration_upgrade, duration + level * DURATION_INCREASE_PER_LEVEL, false);
					}

				}

			}

			EntityPlayer player = (EntityPlayer) event.getCaster();
			SpellModifiers modifiers = event.getModifiers();

			int jewelsSetCount = 0;

			/// custom artefact types
			if (ItemArtefact.isArtefactActive(player, ASItems.belt_enchanter)) {
				if (event.getSpell().getType() == SpellType.BUFF) {
					modifiers.set(WizardryItems.duration_upgrade, modifiers.get(WizardryItems.duration_upgrade) * 1.2f, false);
				}
			}

			/// custom artefact types
			for (ItemArtefact artefact : getActiveArtefacts(player)) {
				if (artefact == ASItems.ring_shivering && event.getSpell().getElement() == Element.ICE && !player.getCooldownTracker().hasCooldown(ASItems.ring_shivering)) {
					player.getCooldownTracker().setCooldown(ASItems.ring_shivering, 100);
                    if (!player.world.isRemote) {
                        for (EntityLivingBase currTarget : EntityUtils.getEntitiesWithinRadius(4, player.posX, player.posY, player.posZ, player.world, EntityLivingBase.class)) {

                            if (currTarget == player || AllyDesignationSystem.isAllied(player, currTarget)) {
                                continue;
                            }

                            if (!MagicDamage.isEntityImmune(MagicDamage.DamageType.FROST, currTarget)) {
                                EntityUtils.attackEntityWithoutKnockback(currTarget, MagicDamage.causeDirectMagicDamage(player, MagicDamage.DamageType.FROST), 3.5f);
                                currTarget.addPotionEffect(new PotionEffect(WizardryPotions.frost, 60, 0));
                            }

                            double angle = (getAngleBetweenEntities(player, currTarget) + 90) * Math.PI / 180;
                            double distance = player.getDistance(currTarget) - 4;
                            currTarget.motionX += Math.min(1 / (distance * distance), 1) * -1 * Math.cos(angle);
                            currTarget.motionZ += Math.min(1 / (distance * distance), 1) * -1 * Math.sin(angle);
                        }
                    } else {
						double particleX, particleZ;

						for (int i = 0; i < 10; i++) {

							particleX = player.posX - 1.0d + 2 * player.world.rand.nextDouble();
							particleZ = player.posZ - 1.0d + 2 * player.world.rand.nextDouble();
							ParticleBuilder.create(ParticleBuilder.Type.ICE)
									.pos(particleX, player.posY + 1, particleZ)
									.vel((particleX - player.posX) * 0.3, 0, (particleZ - player.posZ) * 0.3)
									.time(20)
									.spawn(player.world);

						}

						for (int i = 0; i < 40; i++) {

							particleX = player.posX - 1.0d + 2 * player.world.rand.nextDouble();
							particleZ = player.posZ - 1.0d + 2 * player.world.rand.nextDouble();
							ParticleBuilder.create(ParticleBuilder.Type.SNOW)
									.pos(particleX, player.posY + 1, particleZ)
									.vel((particleX - player.posX) * 0.3, 0, (particleZ - player.posZ) * 0.3)
									.time(20)
									.spawn(player.world);

						}
					}
				} else if (artefact == ASItems.charm_focus_stone && !player.world.isRemote && !player.getCooldownTracker().hasCooldown(ASItems.charm_focus_stone)) {
					List<ItemStack> list = ASBaublesIntegration.getEquippedArtefactStacks(player, ItemArtefact.Type.CHARM);
					if (!list.isEmpty() && list.get(0).getItem() instanceof ItemFocusStone) {
						player.getCooldownTracker().setCooldown(ASItems.charm_focus_stone, 5);
						ItemStack stack = list.get(0);
						float charge = ItemFocusStone.getCharge(stack);
						if (charge < 1f) {
							float newCharge = charge + 0.1f;
							if (newCharge >= 1f && charge < 1f) {
								ASUtils.sendMessage(player, "item.ancientspellcraft:charm_focus_stone.fully_charged", false);
							} else if (newCharge >= 0.8f) {
								ASUtils.sendMessage(player, "item.ancientspellcraft:charm_focus_stone.charged_n", false, (int) (newCharge * 100));
							}
							ItemFocusStone.addCharge(stack, 0.1f);
							ASBaublesIntegration.setArtefactToSlot(player, stack, ItemArtefact.Type.CHARM);
						} else if (charge == 1f) {
							ItemFocusStone.resetCharge(stack);
							ASBaublesIntegration.setArtefactToSlot(player, stack, ItemArtefact.Type.CHARM);
							modifiers.set(SpellModifiers.POTENCY, modifiers.get(SpellModifiers.POTENCY) + 0.3f, true);
							modifiers.set(WizardryItems.blast_upgrade, modifiers.get(WizardryItems.blast_upgrade) + 0.75f, true);
							modifiers.set(WizardryItems.range_upgrade, modifiers.get(WizardryItems.range_upgrade) + 0.75f, true);
							modifiers.set(WizardryItems.duration_upgrade, modifiers.get(WizardryItems.duration_upgrade) + 0.75f, false);
							modifiers.set(WizardryItems.siphon_upgrade, modifiers.get(WizardryItems.siphon_upgrade) + 0.75f, false);
						}
					}
				} else if (artefact == ASItems.head_curse) {
					float potency = modifiers.get(SpellModifiers.POTENCY);
					float potencyBonus = 1;
					for (Potion potion : player.getActivePotionMap().keySet()) {
						if (potion instanceof Curse) {
							potencyBonus += 0.1f;
						}
					}

					if (potencyBonus >= 1) {
						modifiers.set(SpellModifiers.POTENCY, (potencyBonus) * potency, false);
					}
				} else if (artefact == ASItems.belt_scroll_holder && ASBaublesIntegration.enabled()) {
					List<ItemStack> holder = ASBaublesIntegration.getEquippedArtefactStacks(player, ItemArtefact.Type.BELT);
					if (!holder.isEmpty()) {
						if (holder.get(0).getItem() instanceof ItemBeltScrollHolder) {
							ItemStack scroll = ItemBeltScrollHolder.getScroll(holder.get(0));
							if (scroll.getItem() instanceof ItemWandUpgrade) {
								if (scroll.getItem() == WizardryItems.blast_upgrade) {
									modifiers.set(WizardryItems.blast_upgrade, modifiers.get(WizardryItems.blast_upgrade) + 0.25F, true);
								} else if (scroll.getItem() == WizardryItems.range_upgrade) {
									modifiers.set(WizardryItems.range_upgrade, modifiers.get(WizardryItems.range_upgrade) + 0.25F, true);
								} else if (scroll.getItem() == WizardryItems.duration_upgrade) {
									modifiers.set(WizardryItems.duration_upgrade, modifiers.get(WizardryItems.duration_upgrade) + 0.25F, true);
								} else if (scroll.getItem() == WizardryItems.cooldown_upgrade) {
									modifiers.set(WizardryItems.cooldown_upgrade, modifiers.get(WizardryItems.cooldown_upgrade) - 0.15F, true);
								}
							}
						}
					}
				} else if (artefact == ASItems.head_lightning && ASBaublesIntegration.enabled()) {

					if (event.getSpell().getElement() == Element.LIGHTNING && player.world.getBlockState(player.getPosition()).getBlock() == ASBlocks.lightning_block) {
						modifiers.set(WizardryItems.blast_upgrade, modifiers.get(WizardryItems.blast_upgrade) + 0.25F, true);
						modifiers.set(WizardryItems.range_upgrade, modifiers.get(WizardryItems.range_upgrade) + 0.25F, true);
						modifiers.set(WizardryItems.duration_upgrade, modifiers.get(WizardryItems.duration_upgrade) + 0.25F, true);
					}
				}

				float potency = modifiers.get(SpellModifiers.POTENCY);
				float cost = modifiers.get(SpellModifiers.COST);

				if (artefact == ASItems.amulet_elemental_offense) {
					List<ItemStack> amuletz = ASBaublesIntegration.getEquippedArtefactStacks(player, ItemArtefact.Type.AMULET);
					if (amuletz.size() == 1) {
						ItemStack amulet = amuletz.get(0);
						if (AbstractItemArtefactWithSlots.getItemForSlot(amulet, 0).getItem() instanceof ItemCrystal) {
							ItemStack crystal = AbstractItemArtefactWithSlots.getItemForSlot(amulet, 0);
							if (Element.values()[crystal.getMetadata()] == event.getSpell().getElement()) {
								event.getModifiers().set(SpellModifiers.POTENCY, event.getModifiers().get(SpellModifiers.POTENCY) + 0.05F, true);
							}
						}
					}
				} else if (artefact == ASItems.charm_mana_orb) {
					modifiers.set(SpellModifiers.COST, 0.85f * cost, false);

				} else if (artefact == ASItems.amulet_mana) {
					modifiers.set(SpellModifiers.COST, 0.90f * cost, false);

				} else if (artefact == ASItems.ring_mana_cost) {
					modifiers.set(SpellModifiers.COST, 0.95f * cost, false);

				} else if (artefact == ASItems.ring_blast) {
					modifiers.set(SpellModifiers.COST, 1.25f * cost, false);
					event.getModifiers().set(WizardryItems.blast_upgrade, event.getModifiers().get(WizardryItems.blast_upgrade) + 0.25F, true);

				} else if (artefact == ASItems.ring_range) {
					modifiers.set(SpellModifiers.COST, 1.25f * cost, false);
					event.getModifiers().set(WizardryItems.range_upgrade, event.getModifiers().get(WizardryItems.range_upgrade) + 0.25F, true);

				} else if (artefact == ASItems.ring_duration) {
					modifiers.set(SpellModifiers.COST, 1.25f * cost, false);
					event.getModifiers().set(WizardryItems.duration_upgrade, event.getModifiers().get(WizardryItems.duration_upgrade) + 0.25F, true);

				} else if (artefact == ASItems.charm_elemental_grimoire) {
					if (event.getSpell().getElement() == Element.FIRE || event.getSpell().getElement() == Element.ICE || event.getSpell().getElement() == Element.LIGHTNING) {
						modifiers.set(SpellModifiers.POTENCY, 0.1f + potency, false);
					}
				} else if (artefact == ASItems.charm_earth_orb) {
					if (event.getSpell().getElement() == Element.EARTH) {
						modifiers.set(SpellModifiers.POTENCY, 0.01f * Settings.generalSettings.orb_artefact_potency_bonus + potency, false);
					} else {
						modifiers.set(SpellModifiers.POTENCY, -0.5f + potency, false);
					}
				} else if (artefact == ASItems.charm_healing_orb) {
					if (event.getSpell().getElement() == Element.HEALING) {
						modifiers.set(SpellModifiers.POTENCY, 0.01f * Settings.generalSettings.orb_artefact_potency_bonus + potency, false);
					} else {
						modifiers.set(SpellModifiers.POTENCY, -0.5f + potency, false);
					}
				} else if (artefact == ASItems.charm_lightning_orb) {
					if (event.getSpell().getElement() == Element.LIGHTNING) {
						modifiers.set(SpellModifiers.POTENCY, 0.01f * Settings.generalSettings.orb_artefact_potency_bonus + potency, false);
					} else {
						modifiers.set(SpellModifiers.POTENCY, -0.5f + potency, false);
					}
				} else if (artefact == ASItems.charm_fire_orb) {
					if (event.getSpell().getElement() == Element.FIRE) {
						modifiers.set(SpellModifiers.POTENCY, 0.01f * Settings.generalSettings.orb_artefact_potency_bonus + potency, false);
					} else {
						modifiers.set(SpellModifiers.POTENCY, -0.5f + potency, false);
					}
				} else if (artefact == ASItems.charm_ice_orb) {
					if (event.getSpell().getElement() == Element.ICE) {
						modifiers.set(SpellModifiers.POTENCY, 0.01f * Settings.generalSettings.orb_artefact_potency_bonus + potency, false);
					} else {
						modifiers.set(SpellModifiers.POTENCY, -0.5f + potency, false);
					}
				} else if (artefact == ASItems.charm_necromancy_orb) {
					if (event.getSpell().getElement() == Element.NECROMANCY) {
						modifiers.set(SpellModifiers.POTENCY, 0.01f * Settings.generalSettings.orb_artefact_potency_bonus + potency, false);
					} else {
						modifiers.set(SpellModifiers.POTENCY, -0.5f + potency, false);
					}
				} else if (artefact == ASItems.charm_sorcery_orb) {
					if (event.getSpell().getElement() == Element.SORCERY) {
						modifiers.set(SpellModifiers.POTENCY, 0.01f * Settings.generalSettings.orb_artefact_potency_bonus + potency, false);
					} else {
						modifiers.set(SpellModifiers.POTENCY, -0.5f + potency, false);
					}
				} else if (artefact instanceof ItemElementalCloak) {
					ItemElementalCloak cloak = (ItemElementalCloak) artefact;
					Element cloakElement = cloak.getElement();
					if (event.getSpell().getElement() == cloakElement) {
						// Configurable potency bonus for matching element
						float potencyBonus = 1.0f + (Settings.generalSettings.cloak_potency_bonus / 100.0f);
						modifiers.set(SpellModifiers.POTENCY, potency * potencyBonus, false);
					}
				} else if (artefact == ASItems.head_chaos_magic) {
					if (event.getSpell() instanceof IClassSpell && (((IClassSpell) event.getSpell()).getArmourClass() == ItemWizardArmour.ArmourClass.WARLOCK)) {
						modifiers.set(SpellModifiers.POTENCY, 1.25f * potency, false);
					}
				} else if (artefact == ASItems.charm_infernal_stone) {
					// Get the actual item stack for the infernal stone
					ItemStack stoneStack = ASBaublesIntegration.getEquippedArtefactStacks(player, ItemArtefact.Type.CHARM).get(0);
					
					if (event.getSpell().getElement() == Element.FIRE) {
						// Apply heat-based bonuses for fire spells
						if (ItemInfernalStone.isHot(stoneStack)) {
							// Reduce mana cost by 25%
							ItemInfernalStone.applyFireSpellBonuses(event.getModifiers(), stoneStack);
							// Consume heat for the spell cast
							ItemInfernalStone.consumeHeatForSpell(stoneStack);
						}
						
						// If player is burning, extinguish and grant bonuses
						if (player.isBurning()) {
							// Boost fire spell potency by 15%
							event.getModifiers().set(SpellModifiers.POTENCY,
									event.getModifiers().get(SpellModifiers.POTENCY) * 1.15f, false);
							// Extinguish the player
                            if (!player.world.isRemote) {
                                player.extinguish();
                                // Apply fire resistance for 2 seconds (40 ticks)
                                player.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, 40));
                            }
						}
					}
				}

				if (artefact == ASItems.ring_power) {
					jewelsSetCount++;

					modifiers.set(SpellModifiers.POTENCY, 0.05f + potency, false);
					modifiers.set(SpellModifiers.COST, 0.05f + cost, false);

				} else if (artefact == ASItems.amulet_power) {
					jewelsSetCount++;

					modifiers.set(SpellModifiers.POTENCY, 0.10f + potency, false);
					modifiers.set(SpellModifiers.COST, 0.10f + cost, false);

				} else if (artefact == ASItems.charm_power_orb) {
					jewelsSetCount++;

					modifiers.set(SpellModifiers.POTENCY, 0.20f + potency, false);
					modifiers.set(SpellModifiers.COST, 0.20f + cost, false);
				} else if (artefact instanceof ItemElementalBelt) {
					ItemElementalBelt cloak = (ItemElementalBelt) artefact;
					Element element = cloak.getElement();
					int mod = element == event.getSpell().getElement() ? 1 : -1;
					modifiers.set(WizardryItems.blast_upgrade, modifiers.get(WizardryItems.blast_upgrade) + BLAST_RADIUS_INCREASE_PER_LEVEL * mod, true);
					modifiers.set(WizardryItems.range_upgrade, modifiers.get(WizardryItems.range_upgrade) + Constants.RANGE_INCREASE_PER_LEVEL * mod, true);
					modifiers.set(WizardryItems.duration_upgrade, modifiers.get(WizardryItems.duration_upgrade) + DURATION_INCREASE_PER_LEVEL * mod, false);
					modifiers.set(WizardryItems.siphon_upgrade, modifiers.get(WizardryItems.siphon_upgrade) + SIPHON_MANA_PER_LEVEL * mod, false);
				}

			}
			if (jewelsSetCount > 1) {
				float potency = modifiers.get(SpellModifiers.POTENCY);
				float potencyBonus = ((jewelsSetCount - 1) * 5f) / 100; // +5% per set piece
				modifiers.set(SpellModifiers.POTENCY, potencyBonus + potency, false);

			}

			WizardData data = WizardData.get(player);

			if (data != null) {

				Optional<Element> elementOptional = AbsorbCrystal.getElement(data);
				if (elementOptional.isPresent() && event.getSpell().getElement() == elementOptional.get()) {
					modifiers.set(SpellModifiers.POTENCY, modifiers.get(SpellModifiers.POTENCY) + (AbsorbCrystal.isBlock(data) ? 0.10f : 0.05f), false);
				}

				elementOptional = ElementalAttunement.getElement(player);
				if (elementOptional.isPresent()) {
					int mod = event.getSpell().getElement() == elementOptional.get() ? 1 : -1;
					// buffs modifiers for the matching element, weakens others
					modifiers.set(WizardryItems.blast_upgrade, modifiers.get(WizardryItems.blast_upgrade) + (mod * 0.25f), false);
					modifiers.set(WizardryItems.range_upgrade, modifiers.get(WizardryItems.range_upgrade) + (mod * 0.25f), false);
					modifiers.set(WizardryItems.duration_upgrade, modifiers.get(WizardryItems.duration_upgrade) + (mod * 0.25f), false);
					modifiers.set(WizardryItems.siphon_upgrade, modifiers.get(WizardryItems.siphon_upgrade) + (mod * 0.25f), false);
				}

				if (!(event.getSpell() instanceof Contingency)) {
					// casting the spell as a metamagic projectile
					if (data.getVariable(MetamagicProjectile.METAMAGIC_PROJECTILE) != null && data.getVariable(MetamagicProjectile.METAMAGIC_PROJECTILE).booleanValue()) {
						String spellName = event.getSpell().getRegistryName().toString();
						if (event.getSpell() instanceof MetaSpellBuff || event.getSpell() instanceof SpellRay || event.getSpell() instanceof SpellProjectile ||
								event.getSpell() instanceof MetamagicProjectile || Arrays.asList(Settings.generalSettings.metamagic_projectile_incompatible_spells).contains(spellName)) {
							return;
						}

						EntityMetamagicProjectile projectile = new EntityMetamagicProjectile(player.world);
						projectile.setCaster(player);
						projectile.setStoredSpell(event.getSpell());
						projectile.aim(player, calculateVelocity(projectile, modifiers, player.getEyeHeight() - (float) EntityMagicProjectile.LAUNCH_Y_OFFSET));
						projectile.damageMultiplier = modifiers.get(SpellModifiers.POTENCY);

						// Spawns the projectile in the world
						if (!player.world.isRemote) {player.world.spawnEntity(projectile);}
						data.setVariable(MetamagicProjectile.METAMAGIC_PROJECTILE, null);
						event.setCanceled(true);

						return;
					}

					// Contingency
					NBTTagCompound activeContingencyListener = data.getVariable(Contingency.ACTIVE_CONTINGENCY_LISTENER);
					if (activeContingencyListener != null && activeContingencyListener.hasKey(Contingency.ACTIVE_LISTENER_TAG)) {

						// casting the contingency as a projectile
						if (player.isSneaking() && Contingency.Type.fromName(activeContingencyListener.getString(Contingency.ACTIVE_LISTENER_TAG)) == Contingency.Type.DEATH) {

							EntityContingencyProjectile projectile = new EntityContingencyProjectile(player.world);
							projectile.setCaster(player);
							projectile.setStoredSpell(event.getSpell());
							projectile.setContingencyType(Contingency.Type.fromName(activeContingencyListener.getString(Contingency.ACTIVE_LISTENER_TAG)));
							projectile.aim(player, calculateVelocity(projectile, modifiers, player.getEyeHeight() - (float) EntityMagicProjectile.LAUNCH_Y_OFFSET));
							projectile.damageMultiplier = modifiers.get(SpellModifiers.POTENCY);

							// Spawns the projectile in the world
							if (!event.getWorld().isRemote) {player.world.spawnEntity(projectile);}
							data.setVariable(Contingency.ACTIVE_CONTINGENCY_LISTENER, null);
							Contingency.playSound(event.getWorld(), player.getPosition());
							event.setCanceled(true);

							return;
						}

						String spellTag = activeContingencyListener.getString(Contingency.ACTIVE_LISTENER_TAG);
						Spell contingency = Spell.registry.getValue(new ResourceLocation(spellTag));

						// storing the contingency on the player
						Spell spellToStore = event.getSpell();
						NBTTagCompound activeContingencies = data.getVariable(Contingency.ACTIVE_CONTINGENCIES);
						if (activeContingencies == null) {
							activeContingencies = new NBTTagCompound();
						}
						activeContingencies.setString(contingency.getRegistryName().toString(), spellToStore.getRegistryName().toString());

						if (event.getSource() == SpellCastEvent.Source.WAND) {
							if (player.getHeldItemMainhand().getItem() instanceof ItemWand) {
								((ItemWand) player.getHeldItemMainhand().getItem()).consumeMana(player.getHeldItemMainhand(), spellToStore.getCost(), player);
								if (!spellToStore.isContinuous && !player.isCreative()) { // Spells only have a cooldown in survival
									WandHelper.setCurrentCooldown(player.getHeldItemMainhand(), (int) (spellToStore.getCooldown() * modifiers.get(WizardryItems.cooldown_upgrade)));
								}
							} else if (player.getHeldItemOffhand().getItem() instanceof ItemWand) {
								((ItemWand) player.getHeldItemOffhand().getItem()).consumeMana(player.getHeldItemOffhand(), spellToStore.getCost(), player);
								if (!spellToStore.isContinuous && !player.isCreative()) { // Spells only have a cooldown in survival
									WandHelper.setCurrentCooldown(player.getHeldItemOffhand(), (int) (spellToStore.getCooldown() * modifiers.get(WizardryItems.cooldown_upgrade)));
								}
							}
						} else if (event.getSource() == SpellCastEvent.Source.SCROLL) {
							if (player.getHeldItemMainhand().getItem() instanceof ItemScroll) {
								player.getHeldItemMainhand().shrink(1);
								player.getCooldownTracker().setCooldown(player.getHeldItemMainhand().getItem(), spellToStore.getCooldown());
							} else if (player.getHeldItemOffhand().getItem() instanceof ItemScroll) {
								player.getHeldItemOffhand().shrink(1);
								player.getCooldownTracker().setCooldown(player.getHeldItemMainhand().getItem(), spellToStore.getCooldown());
							}
						}
						if (event.getWorld().isRemote) {Contingency.spawnParticles(event.getWorld(), player, Contingency.Type.fromName(spellTag));}
						Contingency.playSound(event.getWorld(), player.getPosition());

						data.setVariable(Contingency.ACTIVE_CONTINGENCY_LISTENER, null);
						data.setVariable(Contingency.ACTIVE_CONTINGENCIES, activeContingencies);
						data.sync();
						event.setCanceled(true);
					}
				}
			}
		}

		// Dimensional Anchor check
		if (DimensionalAnchor.shouldPreventSpell(event.getCaster(), event.getWorld(), event.getSpell())) {
			event.setCanceled(true);
		}

		// Conjuration Inhibitor effect
		if (event.getSpell().getType() == SpellType.MINION && event.getCaster() instanceof EntityPlayer) {
			List<EntityPlayer> playersNearby = EntityUtils.getEntitiesWithinRadius(20, event.getCaster().posX, event.getCaster().posY, event.getCaster().posZ, event.getWorld(), EntityPlayer.class);
			for (EntityPlayer player : playersNearby) {
				if (ItemArtefact.isArtefactActive(player, ASItems.charm_conjuration_inhibitor)) {
					// Count minions in radius
					List<Entity> entities = EntityUtils.getEntitiesWithinRadius(20, event.getCaster().posX, event.getCaster().posY, event.getCaster().posZ, event.getWorld(), Entity.class);
					int minionCount = 0;
					for (Entity entity : entities) {
						if (entity instanceof ISummonedCreature) {
							minionCount++;
						}
					}
					if (minionCount >= 2) {
						ASUtils.sendMessage(event.getCaster(), "item.ancientspellcraft:charm_conjuration_inhibitor.message", true);
						event.setCanceled(true);
						return;
					}
				}
			}
		}
	}

	@SubscribeEvent
	public static void onSpellCastPostEvent(SpellCastEvent.Post event) {
		EntityLivingBase caster = event.getCaster();

		if (caster != null) {
			List<EntityPlayer> nearbyPlayers = EntityUtils.getEntitiesWithinRadius(15, caster.posX, caster.posY, caster.posZ, caster.world, EntityPlayer.class);
			for (EntityPlayer nearbyPlayer : nearbyPlayers) {
				boolean offhand = nearbyPlayer.getHeldItemOffhand().getItem() == ASItems.charm_arcane_mirror;
				if (offhand) {
					// if the player is holding the mirror in their offhand, set the current spell to the one cast by the other player, if the mirror is empty
					if (nearbyPlayer.getHeldItemOffhand().getItem() == ASItems.charm_arcane_mirror) {
						Spell spell = ((ISpellCastingItem) (nearbyPlayer.getHeldItemOffhand().getItem())).getCurrentSpell(nearbyPlayer.getHeldItemOffhand());
						if (spell == Spells.none){
							((ItemArcaneMirror) nearbyPlayer.getHeldItemOffhand().getItem()).setSpell(nearbyPlayer.getHeldItemOffhand(), event.getSpell());
						}
					}
				}
			}
		}

		if (event.getCaster() instanceof EntityPlayer) {

			EntityPlayer player = (EntityPlayer) event.getCaster();

			// Handle cloak effects
			handleCloakEffects(player, event.getSpell());

			// Handle ring of healer effect
			handleRingOfHealerEffect(player, event.getSpell(), event.getModifiers());

			if (isArtefactActive(player, ASItems.charm_knowledge_orb)) {

				boolean flag = false;

				if ((!player.getHeldItemMainhand().isEmpty() && player.getHeldItemMainhand().getItem() instanceof ItemWand)) {
					if (((ItemWand) player.getHeldItemMainhand().getItem()).tier.level == Tier.MASTER.level) {
						flag = true;
					}
				} else if ((!player.getHeldItemOffhand().isEmpty() && player.getHeldItemOffhand().getItem() instanceof ItemWand)) {
					if (((ItemWand) player.getHeldItemOffhand().getItem()).tier.level == Tier.MASTER.level) {
						flag = true;
					}
				}

				if (flag) {
					int progression = (int) (event.getSpell().getCost() * event.getModifiers().get(SpellModifiers.PROGRESSION));
					List<ItemStack> wands = ASUtils.getAllHotbarWands(player, Tier.ADVANCED);
					if (wands != null && !wands.isEmpty()) {
						ItemStack targetWand = ASUtils.getRandomListItem(wands);
						WandHelper.addProgression(targetWand, progression);

						// adapted from ItemWand, to have the same mechanic for displaying level up:
						// If the wand just gained enough progression to be upgraded...
						Tier nextTier = Tier.values()[((ItemWand) targetWand.getItem()).tier.ordinal() + 1];
						int excess = WandHelper.getProgression(targetWand) - nextTier.getProgression();
						if (excess >= 0 && excess < progression) {
							// ...display a message above the player's hotbar
							player.playSound(WizardrySounds.ITEM_WAND_LEVELUP, 1.25f, 1);
							if (!player.world.isRemote) {
								player.sendMessage(new TextComponentTranslation("item." + Wizardry.MODID + ":wand.levelup",
										targetWand.getItem().getItemStackDisplayName(targetWand), nextTier.getNameForTranslationFormatted()));
							}
						}
					}
				}
			}

			// charm_voltaic_vessel: lightning spells interact based on health threshold
			// below 75%: consume 20 mana to grant Regeneration I (3s)
			// above 75%: restore 10 mana to the vessel
			if (!player.world.isRemote
					&& event.getSpell().getElement() == Element.LIGHTNING
					&& ItemArtefact.isArtefactActive(player, ASItems.charm_voltaic_vessel)) {
				List<ItemStack> charmStacks = ASBaublesIntegration.getEquippedArtefactStacks(player, ItemArtefact.Type.CHARM);
				for (ItemStack charmStack : charmStacks) {
					if (charmStack.getItem() instanceof ItemVoltaicVessel) {
						ItemVoltaicVessel vessel = (ItemVoltaicVessel) charmStack.getItem();
						if (player.getHealth() < player.getMaxHealth() * 0.75f) {
							if (vessel.getMana(charmStack) >= 20) {
								vessel.setMana(charmStack, vessel.getMana(charmStack) - 20);
								player.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 60, 0));
							}
						} else {
							vessel.rechargeMana(charmStack, 10);
						}
						break;
					}
				}
			}
		}
	}

	/**
	 * Handles the effects of elemental cloaks when casting spells
	 */
	private static void handleCloakEffects(EntityPlayer player, Spell spell) {
		// Check for each cloak type and apply effects based on spell element
		for (ItemArtefact artefact : getActiveArtefacts(player)) {
			if (artefact instanceof ItemElementalCloak && !player.world.isRemote) {
				ItemElementalCloak cloak = (ItemElementalCloak) artefact;
				Element cloakElement = cloak.getElement();
				
				// Only apply effects if the spell matches the cloak's element
				if (spell.getElement() == cloakElement) {
					applyCloakEffect(player, cloakElement, spell);
				}
			}
		}
	}

	/**
	 * Handles the ring of healer effect - applies beneficial effects to nearby allies when casting buff spells
	 */
	private static void handleRingOfHealerEffect(EntityPlayer player, Spell spell, SpellModifiers modifiers) {
		// Check if player has the ring of healer artefact active
		if (!player.world.isRemote && ItemArtefact.isArtefactActive(player, ASItems.ring_healer)) {
			// Check if the spell is a buff spell
			if (spell.getType() == SpellType.BUFF || spell instanceof SpellBuff) {
				// Get nearby allies using AllyDesignationSystem
				List<EntityLivingBase> nearbyAllies = EntityUtils.getEntitiesWithinRadius(10, player.posX, player.posY, player.posZ, player.world, EntityLivingBase.class);
				
				for (EntityLivingBase ally : nearbyAllies) {
					// Skip the caster and non-allies
					if (ally == player || !AllyDesignationSystem.isAllied(player, ally)) {
						continue;
					}
					
					// Apply beneficial effects that the caster receives to allies for 10% of the duration
					if (spell instanceof SpellBuff) {
						SpellBuff buffSpell = (SpellBuff) spell;
						
						try {
							// Use reflection to access the potion set (same pattern as existing code)
							Field field = ASUtils.ReflectionUtil.getField(buffSpell.getClass(), "potionSet");
							ASUtils.ReflectionUtil.makeAccessible(field);
							Set<Potion> potionSet = (Set<Potion>) field.get(buffSpell);
							
							for (Potion potion : potionSet) {
								// Only apply beneficial effects
								if (!potion.isBadEffect()) {
									// Calculate 10% of the original duration
									int originalDuration = (int) (buffSpell.getProperty(potion.getRegistryName().getPath() + "_duration").floatValue() * modifiers.get(WizardryItems.duration_upgrade));
									int allyDuration = (int) Math.max(1, originalDuration * 0.2); // 20% of duration
									
									// Calculate amplifier (same as original)
									int bonusAmplifier = buffSpell.getStandardBonusAmplifier(modifiers.get(SpellModifiers.POTENCY));
									int amplifier = (int) buffSpell.getProperty(potion.getRegistryName().getPath() + "_strength").floatValue() + bonusAmplifier;
									
									// Apply the effect to the ally
									ally.addPotionEffect(new PotionEffect(potion, allyDuration, amplifier, false, true));
								}
							}
						} catch (Exception e) {
							// Log error but don't crash
							AncientSpellcraft.logger.warn("Failed to apply ring of healer effect: " + e.getMessage());
						}
					}
				}
			}
		}
	}

	/**
	 * Applies the specific effect for each cloak type
	 */
	private static void applyCloakEffect(EntityPlayer player, Element element, Spell spell) {
        if (player.world.isRemote) {
            return;
        }
		switch (element) {
			case EARTH:
				// Cloak of Verdure: +15% Earth spell potency, cleanse poison, chance to poison nearby creatures
				player.removePotionEffect(MobEffects.POISON);
				if (player.world.rand.nextFloat() < 0.3f) { // 30% chance
					List<EntityLivingBase> nearbyEntities = EntityUtils.getEntitiesWithinRadius(6, player.posX, player.posY, player.posZ, player.world, EntityLivingBase.class);
					for (EntityLivingBase entity : nearbyEntities) {
						if (entity != player && !(entity instanceof EntityPlayer)) {
							entity.addPotionEffect(new PotionEffect(MobEffects.POISON, 100, 0));
						}
					}
				}
				break;
				
			case FIRE:
				// Cloak of Scorching: +15% Fire spell potency, 3 seconds fire resistance, chance to ignite nearby creatures
				player.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, 60, 0)); // 3 seconds
				if (player.world.rand.nextFloat() < 0.4f) { // 40% chance
					List<EntityLivingBase> nearbyEntities = EntityUtils.getEntitiesWithinRadius(5, player.posX, player.posY, player.posZ, player.world, EntityLivingBase.class);
					for (EntityLivingBase entity : nearbyEntities) {
						if (entity != player && !(entity instanceof EntityPlayer)) {
							entity.setFire(3); // 3 seconds of fire
						}
					}
				}
				break;
				
			case HEALING:
				// Cloak of Restoration: +15% Healing spell potency, restore 5% max health of nearby allies, absorption for full health allies
				List<EntityLivingBase> entitiesWithinRadius = EntityUtils.getEntitiesWithinRadius(8, player.posX, player.posY, player.posZ, player.world, EntityLivingBase.class);
				for (EntityLivingBase ally : entitiesWithinRadius) {
					if (ally != player && AllyDesignationSystem.isAllied(player, ally)) {
						float healAmount = ally.getMaxHealth() * 0.05f; // 5% of max health
						ally.heal(healAmount);
						
						// If ally has full health, give absorption
						if (ally.getHealth() >= ally.getMaxHealth()) {
							ally.addPotionEffect(new PotionEffect(MobEffects.ABSORPTION, 100, 0)); // 5 seconds of absorption
						}
					}
				}
				break;
				
			case ICE:
				// Cloak of Glaciation: +15% Ice spell potency, put off fire, heal when affected by Frostbite or in snowy biome
				player.extinguish();
				
				// Check if in snowy biome
				boolean inSnowyBiome = false;
				try {
					net.minecraft.world.biome.Biome biome = player.world.getBiome(player.getPosition());
					if (biome.getRegistryName() != null) {
						String biomeName = biome.getRegistryName().toString().toLowerCase();
						inSnowyBiome = biomeName.contains("tundra") || biomeName.contains("snow") || biomeName.contains("ice") || biomeName.contains("frozen") || biomeName.contains("cold");
					}
				} catch (Exception e) {
					// Ignore biome check errors
				}
				
				// Heal if in snowy biome or has frostbite effect
				if (inSnowyBiome || player.isPotionActive(MobEffects.SLOWNESS)) {
					player.heal(2.0f); // Heal 1 heart
				}
				break;
				
			case NECROMANCY:
				// Cloak of Conjuration: +15% Necromancy spell potency, increase lifetime of nearby minions by 3 seconds
				List<EntityLivingBase> nearbyEntities = EntityUtils.getEntitiesWithinRadius(10, player.posX, player.posY, player.posZ, player.world, EntityLivingBase.class);
				for (EntityLivingBase entity : nearbyEntities) {
					if (entity instanceof ISummonedCreature) {
						ISummonedCreature minion = (ISummonedCreature) entity;
						if (minion.getCaster() == player) {
							// Extend lifetime by 3 seconds (60 ticks)
							int currentLifetime = minion.getLifetime();
							if (currentLifetime > 0) {
								minion.setLifetime(currentLifetime + 60);
							}
						}
					}
				}
				break;
				
			case SORCERY:
				// Cloak of Spellweaving: +15% Sorcery spell potency, chance to grant random beneficial potion effect for 5 seconds
				if (player.world.rand.nextFloat() < 0.25f) { // 25% chance
					List<Potion> beneficialPotions = new ArrayList<>();
					
					// Load potions from config
					for (String potionName : Settings.generalSettings.sorcery_cloak_potion_effects) {
						try {
							Potion potion = ForgeRegistries.POTIONS.getValue(new ResourceLocation(potionName));
							if (potion != null) {
								beneficialPotions.add(potion);
							} else {
								AncientSpellcraft.logger.warn("Potion not found for sorcery cloak effect: " + potionName);
							}
						} catch (Exception e) {
							AncientSpellcraft.logger.warn("Invalid potion registry name for sorcery cloak effect: " + potionName);
						}
					}
					
					// Fallback to vanilla potions if config list is empty or invalid
					if (beneficialPotions.isEmpty()) {
						beneficialPotions.addAll(Arrays.asList(
							MobEffects.SPEED,
							MobEffects.JUMP_BOOST,
							MobEffects.STRENGTH,
							MobEffects.REGENERATION,
							MobEffects.ABSORPTION,
							MobEffects.LUCK,
							MobEffects.NIGHT_VISION,
							MobEffects.WATER_BREATHING,
							MobEffects.RESISTANCE
						));
					}
					
					Potion randomPotion = beneficialPotions.get(player.world.rand.nextInt(beneficialPotions.size()));
					player.addPotionEffect(new PotionEffect(randomPotion, 100, 0)); // 5 seconds
				}
				break;
				
			case LIGHTNING:
				// Cloak of Energy: +15% Lightning spell potency, Speed II for 2 seconds, chance to slow nearby creatures
				player.addPotionEffect(new PotionEffect(MobEffects.SPEED, 60, 1)); // Speed II for 2 seconds
				if (player.world.rand.nextFloat() < 0.35f) { // 35% chance
					List<EntityLivingBase> nearbyEntities2 = EntityUtils.getEntitiesWithinRadius(6, player.posX, player.posY, player.posZ, player.world, EntityLivingBase.class);
					for (EntityLivingBase entity : nearbyEntities2) {
						if (entity != player && !(entity instanceof EntityPlayer)) {
							entity.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 80, 0)); // 4 seconds of slowness
						}
					}
				}
				break;
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void onArcaneWorkbenchApplyButtonPressed(SpellBindEvent event) {
		if (!event.getEntity().world.isRemote) {

			int centre_slot = 9;
			int upgrade_slot = 10;
			ItemStack centre = event.getContainer().getSlot(centre_slot).getStack();
			ItemStack upgrade = event.getContainer().getSlot(upgrade_slot).getStack();

			EntityPlayer player = (EntityPlayer) event.getEntityPlayer();

			for (ItemArtefact artefact : getActiveArtefacts(player)) {

				if (artefact == ASItems.charm_wand_upgrade) {

					if (player.world.rand.nextFloat() < 0.2f) {
						// check if it's a wand and a wand upgrade item
						if (WandHelper.isWandUpgrade(upgrade.getItem()) && centre.getItem() instanceof ItemWand) {
							Item specialUpgrade = upgrade.getItem();

							// check if upgrade is applicable
							if (WandHelper.getTotalUpgrades(centre) < ((ItemWand) centre.getItem()).tier.upgradeLimit
									&& WandHelper.getUpgradeLevel(centre, specialUpgrade) < Constants.UPGRADE_STACK_LIMIT) {
								player.sendStatusMessage(new TextComponentTranslation("spell.ancientspellcraft:will_o_wisp.tag_has_no_name"), true);
								player.addItemStackToInventory(new ItemStack(specialUpgrade));
							}
						}

					}
				}
			}
		}
	}

	private static Spell getCurrentSpellFromSpellBearingArtefact(Item item, ItemStack stack) {
		return Spell.byMetadata(stack.getItemDamage());
	}

	@SubscribeEvent
	public static void onPlayerTickEvent(TickEvent.PlayerTickEvent event) {

		if (event.phase == TickEvent.Phase.START) {

			EntityPlayer player = event.player;

			for (ItemArtefact artefact : getActiveArtefacts(player)) {

				if (artefact == ASItems.ring_prismarine) {

					if (!player.world.isRemote && player.isBurning()) {
						float i = player.getCooldownTracker().getCooldown(ASItems.ring_prismarine, 0.0F);
						if (i == 0) {
							player.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, 120));

							SpellModifiers modifiers = new SpellModifiers();
							if (ASSpells.extinguish.cast(player.world, player, EnumHand.MAIN_HAND, 0, modifiers)) {

								MinecraftForge.EVENT_BUS.post(new SpellCastEvent.Post(SpellCastEvent.Source.SCROLL, ASSpells.extinguish, player, modifiers));
								player.getCooldownTracker().setCooldown(ASItems.ring_prismarine, 1200);
							}
						}
					}
				}
			}

			if (!event.player.world.isRemote && event.player.ticksExisted % 6 == 0) {

				if (event.player.dimension == ASDimensions.POCKET_DIM_ID && !event.player.isPotionActive(ASPotions.dimensional_anchor)) {
					event.player.addPotionEffect(new PotionEffect(ASPotions.dimensional_anchor, 200000));
				}

				// Contingency - Immobility
				WizardData data = WizardData.get(player);
				if (data != null) {
					NBTTagCompound activeContingencies = data.getVariable(Contingency.ACTIVE_CONTINGENCIES);
					if (activeContingencies != null) {

						if (activeContingencies.hasKey(Contingency.Type.IMMOBILITY.spellName)) {

							for (BlockPos pos : Arrays.asList(player.getPosition(), player.getPosition().up())) {
								if (player.world.getBlockState(pos).getMaterial() == Material.WEB || player.world.getBlockState(pos).getBlock() == ASBlocks.QUICKSAND
										|| player.world.getBlockState(pos).getBlock().getRegistryName().toString().equals("biomesoplenty:quicksand")) {
									Contingency.tryCastContingencySpell(player, data, Contingency.Type.IMMOBILITY);
									break;
								}

							}
						}
					}
				}
			}
			if (player.world.isRemote && player.ticksExisted % 21 == 0 && WizardData.get(player) != null && WizardData.get(player).getVariable(AbsorbPotion.EFFECT) != null) {
				WizardData data = WizardData.get(player);

				String potionName = data.getVariable(AbsorbPotion.EFFECT);
				Integer duration = data.getVariable(AbsorbPotion.DURATION);

				if (potionName != null && !potionName.equals("none")) {

					Potion potion = ForgeRegistries.POTIONS.getValue(new ResourceLocation(potionName));
					if (potion != null) {
						for (int i = 0; i < 10; i++) {
							ParticleBuilder.create(ParticleBuilder.Type.SPARKLE).scale(1).pos(0, 0.2, 0).entity(player).clr(potion.getLiquidColor())
									.spin(ASSpells.absorb_potion.getProperty(Spell.EFFECT_RADIUS).intValue(), 0.02).time(60).spawn(player.world);
						}
						for (EntityLivingBase target : EntityUtils.getEntitiesWithinRadius(ASSpells.absorb_potion.getProperty(AbsorbPotion.EFFECT_RADIUS).floatValue(), player.posX, player.posY, player.posZ, player.world, EntityLivingBase.class)) {
							if (target == player || potion != MobEffects.INVISIBILITY) {

								ParticleBuilder.create(ParticleBuilder.Type.SCORCH)
										.pos(target.posX, target.posY + 0.101, target.posZ)
										.face(EnumFacing.UP)
										.clr(potion.getLiquidColor())
										.collide(false)
										.scale(2.3F)
										.time(40)
										.spawn(event.player.world);

							}
						}
					}
				}
			}
		}

		// tick artefacts
		if (event.phase == TickEvent.Phase.END) {
			EntityPlayer player = event.player;
			if (ASBaublesIntegration.enabled()) {
				ASBaublesIntegration.tickWornArtefacts(player);
			}
			

		}

	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void onArtefactCheckEvent(ArtefactCheckEvent event) {
		if (event.getPlayer() != null && event.getPlayer().getHeldItemOffhand().getItem() instanceof ItemBattlemageShield) {
			if (ItemBattlemageShield.getArtefacts(event.getPlayer().getHeldItemOffhand()).stream().anyMatch(s -> s.getItem() == event.getArtefact())) {
				event.setResult(Event.Result.ALLOW);
			}
		}
		//		if (event.getPlayer() != null && AbsorbArtefact.isArtefactActive(event.getPlayer(), event.getArtefact())) {
		//			event.setResult(Event.Result.ALLOW);
		//		}
	}

	/**
	 * This event is Cancelable. If this event is canceled, the Entity is not added to the world.
	 * This event does not have a result.
	 */
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void onCheckSpawnEvent(EntityJoinWorldEvent event) {

		//		if (event.getEntity() instanceof EntityWizard && !(event.getEntity() instanceof EntityWizardAS) && !(event.getEntity() instanceof EntityWizardMerchant) &&
		//				Settings.generalSettings.apply_wizard_entity_changes) {
		//
		//			NBTTagCompound nbt = new NBTTagCompound();
		//			event.getEntity().writeToNBT(nbt);
		//
		//			// fixes the missing id
		//			nbt.setString("id", "ancientspellcraft:wizard_as");
		//
		//			EntityWizardAS wizard = (EntityWizardAS) EntityList.createEntityFromNBT(nbt, (event.getWorld()));
		//
		//			// spawn the new entity
		//			if (!event.getWorld().isRemote && wizard != null) {
		//				event.getWorld().spawnEntity(wizard);
		//			}
		//			// prevent spawning the original entity
		//			event.setCanceled(true);
		//		}
		//
		//		if (event.getEntity() instanceof EntityEvilWizard && !(event.getEntity() instanceof EntityEvilWizardAS) && !(event.getEntity() instanceof EntityEvilClassWizard) &&
		//				Settings.generalSettings.apply_wizard_entity_changes) {
		//
		//			NBTTagCompound nbt = new NBTTagCompound();
		//			event.getEntity().writeToNBT(nbt);
		//
		//			// fixes the missing id
		//			nbt.setString("id", "ancientspellcraft:evil_wizard_as");
		//
		//			EntityEvilWizardAS wizard = (EntityEvilWizardAS) EntityList.createEntityFromNBT(nbt, (event.getWorld()));
		//
		//			// spawn the new entity
		//			if (!event.getWorld().isRemote && wizard != null) {
		//				event.getWorld().spawnEntity(wizard);
		//			}
		//			// prevent spawning the original entity
		//			event.setCanceled(true);
		//		}

		// belt_emberguard
		if (event.getEntity() instanceof EntityItem) {
			EntityItem entityItem = (EntityItem) event.getEntity();
			for (EntityPlayer player : event.getWorld().playerEntities) { // Iterate directly over playerEntities
				if (player.dimension == entityItem.dimension &&
						player.getEntityBoundingBox().intersects(entityItem.getEntityBoundingBox().grow(20.0)) &&
						ItemArtefact.isArtefactActive(player, ASItems.belt_emberguard)) {
					entityItem.setEntityInvulnerable(true);
					break;
				}
			}
		}


		// covenant
		// We have no way of checking if it's a spawner in getCanSpawnHere() so this has to be done here instead
		if (event.getEntity() instanceof ISummonedCreature) {
			if (event.getEntity() instanceof EntityCreature && ((ISummonedCreature) event.getEntity()).getOwner() != null) {
				Entity owner = ((ISummonedCreature) event.getEntity()).getOwner();

				if (owner instanceof EntityPlayer && ItemArtefact.isArtefactActive(((EntityPlayer) owner), ASItems.head_minions)) {
					if (ItemArtefact.isArtefactActive(((EntityPlayer) owner), ASItems.head_minions)) {
						EntityCreature creature = (EntityCreature) event.getEntity();
						EntitySummonAIFollowOwner task = new EntitySummonAIFollowOwner(creature, 1.0D, 10.0F, 2.0F);
						creature.tasks.addTask(5, task);
					}
				}
			}
		}

		// ring_eternal_servitude — make the next summoned creature permanent; ignore new summons while one already exists
		if (!event.getWorld().isRemote && event.getEntity() instanceof ISummonedCreature) {
			ISummonedCreature summon = (ISummonedCreature) event.getEntity();
			EntityLivingBase owner = summon.getCaster();
			if (owner instanceof EntityPlayer) {
				EntityPlayer player = (EntityPlayer) owner;
				if (ItemArtefact.isArtefactActive(player, ASItems.ring_eternal_servitude)) {
					WizardData data = WizardData.get(player);
					if (data != null) {
						String existingUUID = data.getVariable(ItemRingEternalServitude.ETERNAL_MINION_UUID);
						boolean hasLiveEternalMinion = false;
						if (existingUUID != null) {
							try {
								Entity old = EntityUtils.getEntityByUUID(player.world, UUID.fromString(existingUUID));
								hasLiveEternalMinion = old != null && !old.isDead;
							} catch (IllegalArgumentException ignored) {}
						}
						if (!hasLiveEternalMinion) {
							// No current eternal minion — claim this one
							summon.setLifetime(-1);
							data.setVariable(ItemRingEternalServitude.ETERNAL_MINION_UUID, event.getEntity().getUniqueID().toString());
							data.sync();
						}
						// If a live eternal minion already exists, leave this summon's lifetime untouched
					}
				}
			}
		}

		if (!event.getEntity().world.isRemote && event.getEntity() instanceof EntityArrow) {

			EntityArrow arrow = (EntityArrow) event.getEntity();

			if (arrow.shootingEntity instanceof EntityLivingBase) {

				EntityLivingBase archer = (EntityLivingBase) arrow.shootingEntity;

				ItemStack bow = archer.getHeldItemMainhand();

				if (!ImbueWeapon.isBow(bow)) {
					bow = archer.getHeldItemOffhand();
					if (!ImbueWeapon.isBow(bow)) {return;}
				}

				// Taken directly from ItemBow, so it works exactly the same as the power enchantment.
				int level = EnchantmentHelper.getEnchantmentLevel(ASEnchantments.degrade_bow, bow);

				if (level > 0) {
					arrow.setDamage(arrow.getDamage() * (1.5 / (level + 1)));
				}
			}
		}

	}

	@SubscribeEvent
	public static void onPlayerRespawnEvent(PlayerEvent.PlayerRespawnEvent event) {
		// Soulbound Wand Upgrade - give back stored items
		ItemSoulboundWandUpgrade.restoreStoredWandsToInventory(event.player);
	}

	@SubscribeEvent
	public static void onResurrectionEvent(ResurrectionEvent event) {
		// Soulbound Wand Upgrade - give back stored items
		ItemSoulboundWandUpgrade.restoreStoredWandsToInventory(event.getEntityPlayer());
	}

	public static float calculateVelocity(EntityMagicProjectile projectile, SpellModifiers modifiers, float launchHeight) {
		// The required range
		float range = 20 * modifiers.get(WizardryItems.range_upgrade);

		if (projectile.hasNoGravity()) {
			// No sensible spell will do this - range is meaningless if the projectile has no gravity or lifetime
			if (projectile.getLifetime() <= 0) {return 1.5f;}
			// Speed = distance/time (trivial, I know, but I've put it here for the sake of completeness)
			return range / projectile.getLifetime();
		} else {
			// It seems that in Minecraft, g is usually* 0.03 - the getter method is protected unfortunately
			// * Potions and xp bottles seem to have more gravity (because that makes sense...)
			float g = 0.03f;
			// Assume horizontal projection
			return range / MathHelper.sqrt(2 * launchHeight / g);
		}
	}

}
