package com.windanesz.ancientspellcraft.handler;

import com.windanesz.ancientspellcraft.Settings;
import com.windanesz.ancientspellcraft.entity.ai.EntitySummonAIFollowOwner;
import com.windanesz.ancientspellcraft.item.ItemNewArtefact;
import com.windanesz.ancientspellcraft.potion.PotionMetamagicEffect;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftEnchantments;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftItems;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftPotions;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftSpells;
import com.windanesz.ancientspellcraft.spell.Martyr;
import com.windanesz.ancientspellcraft.spell.MetaSpellBuff;
import com.windanesz.ancientspellcraft.spell.TimeKnot;
import com.windanesz.ancientspellcraft.util.ASUtils;
import electroblob.wizardry.Wizardry;
import electroblob.wizardry.constants.Constants;
import electroblob.wizardry.constants.Element;
import electroblob.wizardry.constants.SpellType;
import electroblob.wizardry.constants.Tier;
import electroblob.wizardry.data.IStoredVariable;
import electroblob.wizardry.data.Persistence;
import electroblob.wizardry.data.WizardData;
import electroblob.wizardry.entity.living.EntitySkeletonMinion;
import electroblob.wizardry.entity.living.ISummonedCreature;
import electroblob.wizardry.event.SpellBindEvent;
import electroblob.wizardry.event.SpellCastEvent;
import electroblob.wizardry.integration.DamageSafetyChecker;
import electroblob.wizardry.item.ItemArtefact;
import electroblob.wizardry.item.ItemWand;
import electroblob.wizardry.potion.Curse;
import electroblob.wizardry.registry.Spells;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.registry.WizardrySounds;
import electroblob.wizardry.spell.ImbueWeapon;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.spell.SpellBuff;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.IElementalDamage;
import electroblob.wizardry.util.MagicDamage;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.SpellModifiers;
import electroblob.wizardry.util.WandHelper;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static com.windanesz.ancientspellcraft.item.ItemNewArtefact.getActiveNewArtefacts;
import static electroblob.wizardry.constants.Constants.*;
import static electroblob.wizardry.item.ItemArtefact.getActiveArtefacts;
import static electroblob.wizardry.item.ItemArtefact.isArtefactActive;

@Mod.EventBusSubscriber
public class ASEventHandler {

	private ASEventHandler() {} // No instances!

	@SubscribeEvent(priority = EventPriority.LOW) // Low priority in case the event gets cancelled at default priority
	public static void onLivingAttackEvent(LivingAttackEvent event) {

		if (event.getSource() != null && event.getSource().getTrueSource() instanceof EntityLivingBase) {

			EntityLivingBase attacker = (EntityLivingBase) event.getSource().getTrueSource();
			World world = event.getEntityLiving().world;

			if (!attacker.getHeldItemMainhand().isEmpty() && ImbueWeapon.isSword(attacker.getHeldItemMainhand())) {

				int level = EnchantmentHelper.getEnchantmentLevel(AncientSpellcraftEnchantments.static_charge,
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
		player.removePotionEffect(effect);
	}

	private static void setCooldown(EntityPlayer player, Spell spell) {

		List<ItemStack> wands = ASUtils.getAllHotbarWands(player);
		if (wands != null && !wands.isEmpty()) {

			for (ItemStack wand : wands) {
				int index = 0;

				for (Spell currentSpell : WandHelper.getSpells(wand)) {

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
			}
		}
	}

	/////////////////////////////// ARTEFACT EVENTS ///////////////////////////////

	public static final IStoredVariable<Integer> COUNTDOWN_KEY = IStoredVariable.StoredVariable.ofInt("artefactEternityCountdown", Persistence.NEVER).withTicker(ASEventHandler::update);
	public static final IStoredVariable<Integer> SPELL_ID = IStoredVariable.StoredVariable.ofInt("artefactEternitySpell", Persistence.ALWAYS);

	static {
		WizardData.registerStoredVariables(COUNTDOWN_KEY, SPELL_ID);
	}

	private static int update(EntityPlayer player, Integer countdown) {
		if (countdown == null)
			return 0;

		if (!player.world.isRemote) {

			WizardData data = WizardData.get(player);

			Integer spellId = data.getVariable(SPELL_ID);

			if (spellId == null)
				return 0;

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

			for (ItemArtefact artefact : getActiveArtefacts(player)) {

				if (artefact == AncientSpellcraftItems.amulet_time_knot) {
					if (player.isPotionActive(AncientSpellcraftPotions.time_knot) && (player.getHealth() - event.getAmount() <= 0F)) {
						TimeKnot.loopPlayer(player);
						player.extinguish();
						if (!player.world.isRemote) {
							Iterator<PotionEffect> iterator = player.getActivePotionMap().values().iterator();

							//							while (iterator.hasNext()) {
							//								try {
							//									iterator.remove();
							//								}
							//								catch (Exception e) {}
							//							}
						}
					}
				}

				if (artefact == AncientSpellcraftItems.charm_cryostasis) {
					if ((player.getHealth() <= 6 || (player.getHealth() - event.getAmount() <= 6)) && player.world.rand.nextFloat() < 0.25f) {
						AncientSpellcraftSpells.cryostasis.cast(player.world, player, player.getActiveHand(), 0, new SpellModifiers());
					}
				} else if (artefact == AncientSpellcraftItems.ring_berserker) {
					if (!player.world.isRemote && (player.getHealth() <= 6 || (player.getHealth() - event.getAmount() <= 6))) {

						if (!player.isPotionActive(MobEffects.STRENGTH)) {
							player.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 300)); // 15 seconds of strength
						}
					}
				} else if (artefact == AncientSpellcraftItems.amulet_rabbit) {
					if (!player.world.isRemote && player.world.rand.nextFloat() < 0.25f) {
						if (!player.isPotionActive(MobEffects.SPEED)) {
							player.addPotionEffect(new PotionEffect(MobEffects.SPEED, 200)); // 10 seconds of speed
						}
						if (!player.isPotionActive(MobEffects.WEAKNESS)) {
							player.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 200, 1)); // 10 seconds of weakness
						}
						//						if (!player.isPotionActive(WizardryPotions.fear)) { // TODO, add magical weakness
						//							player.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 200)); // 10 seconds of speed
						//						}
					}
				}
			}
		}

		{
			if (!event.getEntity().world.isRemote && event.getEntityLiving().isPotionActive(AncientSpellcraftPotions.martyr_beneficial) && event.getEntityLiving() instanceof EntityPlayer
					&& !event.getSource().isUnblockable() && !(event.getSource() instanceof IElementalDamage
					&& ((IElementalDamage) event.getSource()).isRetaliatory())) {

				EntityPlayer player = (EntityPlayer) event.getEntityLiving(); // the beneficial who is attacked
				WizardData data = WizardData.get(player);

				if (data != null) {

					for (Iterator<UUID> iterator = Martyr.getMartyrBoundEntities(data).iterator(); iterator.hasNext(); ) {

						Entity entity = EntityUtils.getEntityByUUID(player.world, iterator.next()); // the target who will take the damage instead

						if (entity == null)
							iterator.remove();

						if (entity instanceof EntityPlayer && ((EntityPlayer) entity).isPotionActive(AncientSpellcraftPotions.martyr)) {
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

					int level = EnchantmentHelper.getEnchantmentLevel(AncientSpellcraftEnchantments.static_charge,
							attacker.getHeldItemMainhand());

					if (level > 0 && !MagicDamage.isEntityImmune(MagicDamage.DamageType.SHOCK, event.getEntityLiving())) {
						event.setAmount(event.getAmount() + level * 2);
					}
				}
			}
		}

	}

	@SubscribeEvent
	public static void onPotionApplicableEvent(PotionEvent.PotionApplicableEvent event) {

		if (event.getEntity() instanceof EntityPlayer) {

			EntityPlayer player = (EntityPlayer) event.getEntity();

			for (ItemArtefact artefact : getActiveArtefacts(player)) {

				if (artefact == AncientSpellcraftItems.amulet_poison_resistance) {

					if (event.getPotionEffect().getPotion() == MobEffects.POISON) {
						if (player.world.rand.nextFloat() < 0.5f) {
							event.setResult(Event.Result.DENY);
						}
					}
				} else if (artefact == AncientSpellcraftItems.amulet_curse_ward) {
					if (event.getPotionEffect().getPotion() instanceof Curse) {
						event.setResult(Event.Result.DENY);
					}
				}
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onPotionRemoveEvent(PotionEvent.PotionRemoveEvent event) {

		if (event.getPotion() == AncientSpellcraftPotions.magical_exhaustion) {
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public static void onPotionExpiryEvent(PotionEvent.PotionExpiryEvent event) {

		if (event.getEntity() instanceof EntityPlayer) {

			EntityPlayer player = (EntityPlayer) event.getEntity();

			for (ItemArtefact artefact : getActiveArtefacts(player)) {

				if (artefact == AncientSpellcraftItems.amulet_pendant_of_eternity) {
					if (getArtefactItemStack(player, (ItemArtefact) AncientSpellcraftItems.amulet_pendant_of_eternity) != null) {
						ItemStack pendant = getArtefactItemStack(player, (ItemArtefact) AncientSpellcraftItems.amulet_pendant_of_eternity);
						Spell spell = getCurrentSpellFromSpellBearingArtefact(AncientSpellcraftItems.amulet_pendant_of_eternity, pendant);
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

			if (ItemArtefact.isArtefactActive(player, AncientSpellcraftItems.charm_reanimation)) {
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

		}
	}

	@SubscribeEvent
	public static void onProjectileImpactArrowEvent(ProjectileImpactEvent.Arrow event) {

		if (!event.getArrow().world.isRemote) {

			if (event.getArrow().shootingEntity != null && event.getArrow().shootingEntity instanceof EntityPlayer) {

				EntityPlayer player = (EntityPlayer) event.getArrow().shootingEntity;

				for (ItemArtefact artefact : getActiveArtefacts(player)) {

					if (artefact == AncientSpellcraftItems.ring_poison_arrow) {
						//						if (player.world.rand.nextFloat() < 0.2f) {
						if (player.world.rand.nextFloat() < 0.2f) {

							if (event.getRayTraceResult().entityHit != null && event.getRayTraceResult().entityHit instanceof EntityLivingBase) {
								EntityLivingBase target = (EntityLivingBase) event.getRayTraceResult().entityHit;
								if (!target.isPotionActive(MobEffects.POISON)) {
									target.addPotionEffect(new PotionEffect(MobEffects.POISON, 100)); // 5 seconds of poisoning

								}
							}
						}
					}
				}
			}

		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST) // processing after electroblob.wizardry.item.ItemArtefact.onSpellCastPreEvent (EventPriority.LOW)
	public static void onSpellCastPreEvent(SpellCastEvent.Pre event) {
		if (event.getCaster() instanceof EntityPlayer) {

			if (!(event.getSpell() instanceof MetaSpellBuff)) {

				EntityPlayer player = (EntityPlayer) event.getCaster();
				Map<Potion, PotionEffect> potionEffectMap = new HashMap();
				Map<Potion, PotionEffect> potionEffects = player.getActivePotionMap();
				potionEffectMap.putAll(potionEffects);

				for (Map.Entry<Potion, PotionEffect> entry : potionEffectMap.entrySet()) {
					Potion potion = entry.getKey();

					if (potion instanceof PotionMetamagicEffect) {

						if (potion.equals(AncientSpellcraftPotions.arcane_augmentation)) {
							PotionEffect effect = entry.getValue();
							SpellModifiers modifiers = event.getModifiers();

							float range = modifiers.get(WizardryItems.range_upgrade);
							float blast = modifiers.get(WizardryItems.blast_upgrade);

							int level = effect.getAmplifier() + 1;

							if (level > 0) {
								modifiers.set(WizardryItems.range_upgrade, blast + level * Constants.RANGE_INCREASE_PER_LEVEL, true);
								modifiers.set(WizardryItems.blast_upgrade, range + level * Constants.BLAST_RADIUS_INCREASE_PER_LEVEL, true);
							}
							onMetaMagicFinished(player, AncientSpellcraftSpells.arcane_augmentation, AncientSpellcraftPotions.arcane_augmentation);

						} else if (potion.equals(AncientSpellcraftPotions.intensifying_focus)) {
							PotionEffect effect = entry.getValue();
							SpellModifiers modifiers = event.getModifiers();

							float potency = modifiers.get(SpellModifiers.POTENCY);
							float range = modifiers.get(WizardryItems.range_upgrade);
							float blast = modifiers.get(WizardryItems.range_upgrade);

							int level = effect.getAmplifier() + 1;
							if (level > 0) {

								modifiers.set(SpellModifiers.POTENCY, potency + level * POTENCY_INCREASE_PER_TIER, true);
								modifiers.set(WizardryItems.range_upgrade, blast - level * Constants.RANGE_INCREASE_PER_LEVEL, true);
								modifiers.set(WizardryItems.blast_upgrade, range - level * Constants.BLAST_RADIUS_INCREASE_PER_LEVEL, true);
							}
							setCooldown(player, AncientSpellcraftSpells.intensifying_focus);
							player.removePotionEffect(AncientSpellcraftPotions.intensifying_focus);
							onMetaMagicFinished(player, AncientSpellcraftSpells.intensifying_focus, AncientSpellcraftPotions.intensifying_focus);

						} else if (potion.equals(AncientSpellcraftPotions.continuity_charm)) {
							PotionEffect effect = entry.getValue();
							SpellModifiers modifiers = event.getModifiers();

							float duration = modifiers.get(WizardryItems.duration_upgrade);
							float cost = modifiers.get(SpellModifiers.COST);
							int level = effect.getAmplifier() + 1;
							if (level > 0) {

								modifiers.set(WizardryItems.duration_upgrade, duration + level * DURATION_INCREASE_PER_LEVEL, true);
								modifiers.set(SpellModifiers.COST, cost + level * COST_REDUCTION_PER_ARMOUR, true);
							}
							onMetaMagicFinished(player, AncientSpellcraftSpells.continuity_charm, AncientSpellcraftPotions.continuity_charm);
						}

					}

					if (potion == AncientSpellcraftPotions.spell_blast) {
						SpellModifiers modifiers = event.getModifiers();
						float blast = modifiers.get(WizardryItems.blast_upgrade);
						modifiers.set(WizardryItems.blast_upgrade, blast + BLAST_RADIUS_INCREASE_PER_LEVEL, true);
					}
					if (potion == AncientSpellcraftPotions.spell_range) {
						SpellModifiers modifiers = event.getModifiers();
						float range = modifiers.get(WizardryItems.range_upgrade);
						modifiers.set(WizardryItems.range_upgrade, range + Constants.RANGE_INCREASE_PER_LEVEL, true);

					}
					if (potion == AncientSpellcraftPotions.spell_cooldown) {
						SpellModifiers modifiers = event.getModifiers();
						float cooldown = modifiers.get(WizardryItems.cooldown_upgrade);
						modifiers.set(WizardryItems.cooldown_upgrade, cooldown - Constants.COOLDOWN_REDUCTION_PER_LEVEL, true);
					}
					if (potion == AncientSpellcraftPotions.spell_duration) {
						SpellModifiers modifiers = event.getModifiers();
						float duration = modifiers.get(WizardryItems.duration_upgrade);
						modifiers.set(WizardryItems.duration_upgrade, duration + DURATION_INCREASE_PER_LEVEL, false);
					}

				}

			}

			EntityPlayer player = (EntityPlayer) event.getCaster();
			SpellModifiers modifiers = event.getModifiers();

			int jewelsSetCount = 0;

			/// custom artefact types
			if (ItemNewArtefact.isNewArtefactActive(player, AncientSpellcraftItems.belt_enchanter)) {
				if (event.getSpell().getType() == SpellType.BUFF) {
					modifiers.set(WizardryItems.duration_upgrade, modifiers.get(WizardryItems.duration_upgrade) * 1.2f, false);
				}
			}

			/// custom artefact types

			for (ItemNewArtefact artefact : getActiveNewArtefacts(player)) {

				if (artefact == AncientSpellcraftItems.head_curse) {
					float potency = modifiers.get(SpellModifiers.POTENCY);
					int modifier = 0;
					for (Potion potion : player.getActivePotionMap().keySet()) {
						if (potion instanceof Curse) {
							modifier += 0.1;
						}
					}

					if (modifier >= 0) {
						modifiers.set(SpellModifiers.POTENCY, (1 + modifier) * potency, false);
					}
				}

			}

			for (ItemArtefact artefact : getActiveArtefacts(player)) {

				float potency = modifiers.get(SpellModifiers.POTENCY);
				float cost = modifiers.get(SpellModifiers.COST);

				if (artefact == AncientSpellcraftItems.charm_mana_orb) {
					modifiers.set(SpellModifiers.COST, 0.85f * cost, false);

				} else if (artefact == AncientSpellcraftItems.amulet_mana) {
					modifiers.set(SpellModifiers.COST, 0.90f * cost, false);

				} else if (artefact == AncientSpellcraftItems.ring_blast) {
					modifiers.set(SpellModifiers.COST, 1.25f * cost, false);
					event.getModifiers().set(WizardryItems.blast_upgrade, event.getModifiers().get(WizardryItems.blast_upgrade) + 0.25F, true);

				} else if (artefact == AncientSpellcraftItems.ring_range) {
					modifiers.set(SpellModifiers.COST, 1.25f * cost, false);
					event.getModifiers().set(WizardryItems.range_upgrade, event.getModifiers().get(WizardryItems.range_upgrade) + 0.25F, true);

				} else if (artefact == AncientSpellcraftItems.charm_elemental_grimoire) {
					if (event.getSpell().getElement() == Element.FIRE || event.getSpell().getElement() == Element.ICE || event.getSpell().getElement() == Element.LIGHTNING) {
						modifiers.set(SpellModifiers.POTENCY, 0.1f + potency, false);
					}
				} else if (artefact == AncientSpellcraftItems.charm_earth_orb) {
					if (event.getSpell().getElement() == Element.EARTH) {
						modifiers.set(SpellModifiers.POTENCY, 0.01f * Settings.generalSettings.orb_artefact_potency_bonus + potency, false);
					} else {
						modifiers.set(SpellModifiers.POTENCY, -0.5f + potency, false);
					}
				} else if (artefact == AncientSpellcraftItems.charm_healing_orb) {
					if (event.getSpell().getElement() == Element.HEALING) {
						modifiers.set(SpellModifiers.POTENCY, 0.01f * Settings.generalSettings.orb_artefact_potency_bonus + potency, false);
					} else {
						modifiers.set(SpellModifiers.POTENCY, -0.5f + potency, false);
					}
				} else if (artefact == AncientSpellcraftItems.charm_lightning_orb) {
					if (event.getSpell().getElement() == Element.LIGHTNING) {
						modifiers.set(SpellModifiers.POTENCY, 0.01f * Settings.generalSettings.orb_artefact_potency_bonus + potency, false);
					} else {
						modifiers.set(SpellModifiers.POTENCY, -0.5f + potency, false);
					}
				} else if (artefact == AncientSpellcraftItems.charm_fire_orb) {
					if (event.getSpell().getElement() == Element.FIRE) {
						modifiers.set(SpellModifiers.POTENCY, 0.01f * Settings.generalSettings.orb_artefact_potency_bonus + potency, false);
					} else {
						modifiers.set(SpellModifiers.POTENCY, -0.5f + potency, false);
					}
				} else if (artefact == AncientSpellcraftItems.charm_ice_orb) {
					if (event.getSpell().getElement() == Element.ICE) {
						modifiers.set(SpellModifiers.POTENCY, 0.01f * Settings.generalSettings.orb_artefact_potency_bonus + potency, false);
					} else {
						modifiers.set(SpellModifiers.POTENCY, -0.5f + potency, false);
					}
				} else if (artefact == AncientSpellcraftItems.charm_necromancy_orb) {
					if (event.getSpell().getElement() == Element.NECROMANCY) {
						modifiers.set(SpellModifiers.POTENCY, 0.01f * Settings.generalSettings.orb_artefact_potency_bonus + potency, false);
					} else {
						modifiers.set(SpellModifiers.POTENCY, -0.5f + potency, false);
					}
				} else if (artefact == AncientSpellcraftItems.charm_sorcery_orb) {
					if (event.getSpell().getElement() == Element.SORCERY) {
						modifiers.set(SpellModifiers.POTENCY, 0.01f * Settings.generalSettings.orb_artefact_potency_bonus + potency, false);
					} else {
						modifiers.set(SpellModifiers.POTENCY, -0.5f + potency, false);
					}
				}

				if (artefact == AncientSpellcraftItems.ring_power) {
					jewelsSetCount++;

					modifiers.set(SpellModifiers.POTENCY, 0.05f + potency, false);
					modifiers.set(SpellModifiers.COST, 0.05f + cost, false);

				} else if (artefact == AncientSpellcraftItems.amulet_power) {
					jewelsSetCount++;

					modifiers.set(SpellModifiers.POTENCY, 0.10f + potency, false);
					modifiers.set(SpellModifiers.COST, 0.10f + cost, false);

				} else if (artefact == AncientSpellcraftItems.charm_power_orb) {
					jewelsSetCount++;

					modifiers.set(SpellModifiers.POTENCY, 0.20f + potency, false);
					modifiers.set(SpellModifiers.COST, 0.20f + cost, false);
				}

			}
			if (jewelsSetCount > 1) {
				float potency = modifiers.get(SpellModifiers.POTENCY);
				float potencyBonus = ((jewelsSetCount - 1) * 5f) / 100; // +5% per set piece
				modifiers.set(SpellModifiers.POTENCY, potencyBonus + potency, false);

			}
		}

	}

	@SubscribeEvent
	public static void onSpellCastPostEvent(SpellCastEvent.Post event) {
		if (event.getCaster() instanceof EntityPlayer) {

			EntityPlayer player = (EntityPlayer) event.getCaster();

			if (isArtefactActive(player, AncientSpellcraftItems.charm_knowledge_orb)) {

				boolean flag = false;

				if ((!player.getHeldItemMainhand().isEmpty() && player.getHeldItemMainhand().getItem() instanceof ItemWand)) {
					if (((ItemWand) player.getHeldItemMainhand().getItem()).tier.level == Tier.MASTER.level) {
						flag = true;
					}
				} else if ((!player.getHeldItemOffhand().isEmpty() && player.getHeldItemOffhand().getItem() instanceof ItemWand))
					if (((ItemWand) player.getHeldItemOffhand().getItem()).tier.level == Tier.MASTER.level) {
						flag = true;
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
							if (!player.world.isRemote)
								player.sendMessage(new TextComponentTranslation("item." + Wizardry.MODID + ":wand.levelup",
										targetWand.getItem().getItemStackDisplayName(targetWand), nextTier.getNameForTranslationFormatted()));
						}
					}
				}
			}
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

				if (artefact == AncientSpellcraftItems.charm_wand_upgrade) {

					if (player.world.rand.nextFloat() < 0.2f) {
						// check if its a wand and a wand upgrade item
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

	private static ItemStack getArtefactItemStack(EntityPlayer player, ItemArtefact artefact) {
		return ASUtils.getItemStackFromInventoryHotbar(player, artefact);
	}

	private static Spell getCurrentSpellFromSpellBearingArtefact(Item item, ItemStack stack) {
		return Spell.byMetadata(stack.getItemDamage());
	}

	@SubscribeEvent
	public static void onPlayerTickEvent(TickEvent.PlayerTickEvent event) {

		if (event.phase == TickEvent.Phase.START) {

			EntityPlayer player = event.player;

			for (ItemArtefact artefact : getActiveArtefacts(player)) {

				if (artefact == AncientSpellcraftItems.ring_prismarine) {

					if (player.isBurning()) {
						float i = player.getCooldownTracker().getCooldown(AncientSpellcraftItems.ring_prismarine, 0.0F);
						if (i == 0) {
							player.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, 120));

							SpellModifiers modifiers = new SpellModifiers();
							if (AncientSpellcraftSpells.extinguish.cast(player.world, player, EnumHand.MAIN_HAND, 0, modifiers)) {

								MinecraftForge.EVENT_BUS.post(new SpellCastEvent.Post(SpellCastEvent.Source.SCROLL, AncientSpellcraftSpells.extinguish, player, modifiers));
								player.getCooldownTracker().setCooldown(AncientSpellcraftItems.ring_prismarine, 1200);
							}
						}
					}
				}
			}
		}
	}

	@SubscribeEvent
	public static void onCheckSpawnEvent(EntityJoinWorldEvent event) {

		// We have no way of checking if it's a spawner in getCanSpawnHere() so this has to be done here instead
		if (event.getEntity() instanceof ISummonedCreature) {
			if (event.getEntity() instanceof EntityCreature && ((ISummonedCreature) event.getEntity()).getOwner() != null) {
				Entity owner = ((ISummonedCreature) event.getEntity()).getOwner();

				if (owner instanceof EntityPlayer && ItemNewArtefact.isNewArtefactActive(((EntityPlayer) owner), AncientSpellcraftItems.head_minions)) {
					if (ItemNewArtefact.isNewArtefactActive(((EntityPlayer) owner), AncientSpellcraftItems.head_minions)) {
						EntityCreature creature = (EntityCreature) event.getEntity();
						EntitySummonAIFollowOwner task = new EntitySummonAIFollowOwner(creature, 1.0D, 10.0F, 2.0F);
						creature.tasks.addTask(5, task);
					}
				}
			}
		}
	}

}
