package com.windanesz.ancientspellcraft.handler;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.Settings;
import com.windanesz.ancientspellcraft.data.RitualDiscoveryData;
import com.windanesz.ancientspellcraft.entity.ai.EntitySummonAIFollowOwner;
import com.windanesz.ancientspellcraft.entity.projectile.EntityContingencyProjectile;
import com.windanesz.ancientspellcraft.entity.projectile.EntityMetamagicProjectile;
import com.windanesz.ancientspellcraft.integration.artemislib.ASArtemisLibIntegration;
import com.windanesz.ancientspellcraft.integration.baubles.ASBaublesIntegration;
import com.windanesz.ancientspellcraft.item.ItemBeltScrollHolder;
import com.windanesz.ancientspellcraft.item.ItemManaArtefact;
import com.windanesz.ancientspellcraft.item.ItemNewArtefact;
import com.windanesz.ancientspellcraft.item.ItemRitualBook;
import com.windanesz.ancientspellcraft.item.ItemSoulboundWandUpgrade;
import com.windanesz.ancientspellcraft.potion.PotionMetamagicEffect;
import com.windanesz.ancientspellcraft.registry.ASBlocks;
import com.windanesz.ancientspellcraft.registry.ASEnchantments;
import com.windanesz.ancientspellcraft.registry.ASItems;
import com.windanesz.ancientspellcraft.registry.ASPotions;
import com.windanesz.ancientspellcraft.registry.ASSpells;
import com.windanesz.ancientspellcraft.ritual.Ritual;
import com.windanesz.ancientspellcraft.spell.Contingency;
import com.windanesz.ancientspellcraft.spell.DimensionalAnchor;
import com.windanesz.ancientspellcraft.spell.Martyr;
import com.windanesz.ancientspellcraft.spell.MetaSpellBuff;
import com.windanesz.ancientspellcraft.spell.MetamagicProjectile;
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
import electroblob.wizardry.entity.construct.EntityBubble;
import electroblob.wizardry.entity.living.EntitySkeletonMinion;
import electroblob.wizardry.entity.living.ISummonedCreature;
import electroblob.wizardry.entity.projectile.EntityMagicProjectile;
import electroblob.wizardry.event.SpellBindEvent;
import electroblob.wizardry.event.SpellCastEvent;
import electroblob.wizardry.integration.DamageSafetyChecker;
import electroblob.wizardry.item.IManaStoringItem;
import electroblob.wizardry.item.ItemArtefact;
import electroblob.wizardry.item.ItemScroll;
import electroblob.wizardry.item.ItemWand;
import electroblob.wizardry.item.ItemWandUpgrade;
import electroblob.wizardry.potion.Curse;
import electroblob.wizardry.registry.Spells;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.registry.WizardrySounds;
import electroblob.wizardry.spell.ImbueWeapon;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.spell.SpellBuff;
import electroblob.wizardry.spell.SpellMinion;
import electroblob.wizardry.spell.SpellProjectile;
import electroblob.wizardry.spell.SpellRay;
import electroblob.wizardry.util.AllyDesignationSystem;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.IElementalDamage;
import electroblob.wizardry.util.InventoryUtils;
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
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.lang.reflect.Field;
import java.util.ArrayList;
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

	public static final float COST_REDUCTION_PER_ARMOUR = 0.15f;
	public static final IStoredVariable<Integer> SPELL_ID = IStoredVariable.StoredVariable.ofInt("artefactEternitySpell", Persistence.ALWAYS);
	public static final IStoredVariable<Integer> COUNTDOWN_KEY = IStoredVariable.StoredVariable.ofInt("artefactEternityCountdown", Persistence.NEVER).withTicker(ASEventHandler::update);

	static {
		WizardData.registerStoredVariables(COUNTDOWN_KEY, SPELL_ID);
	}

	private ASEventHandler() {} // No instances!

	@SubscribeEvent
	public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
		if (event.getItemStack().getItem() == WizardryItems.identification_scroll && event.getEntityLiving() instanceof EntityPlayer) {
			EnumHand otherHand = event.getHand() == EnumHand.MAIN_HAND ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;
			ItemStack otherItemStack = event.getEntityLiving().getHeldItem(otherHand);
			if (otherItemStack.getItem() instanceof ItemRitualBook) {
				EntityPlayer player = (EntityPlayer) event.getEntityLiving();
				Ritual ritual = ItemRitualBook.getRitual(otherItemStack);
				if (!RitualDiscoveryData.hasRitualBeenDiscovered(player, ritual)) {
					RitualDiscoveryData.addKnownRitual(player, ritual);
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
			float chance =  0;
			for (ItemArtefact ring : ItemArtefact.getActiveArtefacts(player, ItemArtefact.Type.RING)) {
				if (ring == ASItems.ring_metamagic_preserve) {
					chance += 0.33f;
				}
			}
			if (chance > 0 && player.world.rand.nextFloat() < chance) {
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
		if (countdown == null) { return 0; }

		if (!player.world.isRemote) {

			WizardData data = WizardData.get(player);

			Integer spellId = data.getVariable(SPELL_ID);

			if (spellId == null) { return 0; }

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

			if (player.isPotionActive(ASPotions.wizard_shield)) {
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

				if (artefact == ASItems.charm_cryostasis) {
					if ((player.getHealth() <= 6 || (player.getHealth() - event.getAmount() <= 6)) && player.world.rand.nextFloat() < 0.25f) {
						ASSpells.cryostasis.cast(player.world, player, player.getActiveHand(), 0, new SpellModifiers());
					}
				} else if (artefact == ASItems.ring_protector) {
					if ((player.getHealth() <= 8 || (player.getHealth() - event.getAmount() <= 6)) && player.world.rand.nextFloat() < 0.5f) {
						boolean shouldContinue = true;
						for (ItemStack wand : ASUtils.getAllHotbarWands(player)) {
							if (!shouldContinue) { break; }
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
					if (!player.world.isRemote && (player.getHealth() <= 6 || (player.getHealth() - event.getAmount() <= 6))) {

						if (!player.isPotionActive(MobEffects.STRENGTH)) {
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
						player.addPotionEffect(new PotionEffect(ASPotions.wizard_shield, 100, 15));
						player.getCooldownTracker().setCooldown(ASItems.amulet_shield, 3600); // 3 mins cd
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

		{
			if (!event.getEntity().world.isRemote && event.getEntityLiving().isPotionActive(ASPotions.martyr_beneficial) && event.getEntityLiving() instanceof EntityPlayer
					&& !event.getSource().isUnblockable() && !(event.getSource() instanceof IElementalDamage
					&& ((IElementalDamage) event.getSource()).isRetaliatory())) {

				EntityPlayer player = (EntityPlayer) event.getEntityLiving(); // the beneficial who is attacked
				WizardData data = WizardData.get(player);

				if (data != null) {

					for (Iterator<UUID> iterator = Martyr.getMartyrBoundEntities(data).iterator(); iterator.hasNext(); ) {

						Entity entity = EntityUtils.getEntityByUUID(player.world, iterator.next()); // the target who will take the damage instead

						if (entity == null) { iterator.remove(); }

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

	}

	@SubscribeEvent
	public static void onPotionApplicableEvent(PotionEvent.PotionApplicableEvent event) {
		if (event.getPotionEffect().getPotion() == ASPotions.eagle_eye && !(event.getEntityLiving() instanceof EntityPlayer)) {
			event.setResult(Event.Result.DENY);
		}

		if (event.getPotionEffect().getPotion() == ASPotions.shrinkage || event.getPotionEffect().getPotion() == ASPotions.growth) {
			if (!ASArtemisLibIntegration.enabled()) {
				// ArtemisLib-dependent potions are not applicable if ArtemisLib is not loaded
				event.setResult(Event.Result.DENY);
			}
		}

		if (event.getEntity() instanceof EntityPlayer) {

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
				if (event.getCaster()  instanceof EntityLivingBase && event.getCaster().getDistanceSq(player) < 20 && ItemArtefact.isArtefactActive(player, ASItems.charm_suppression_orb)) {
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

			if (wand.getItem() instanceof ItemWand) {

				int wandMana = ((ItemWand) wand.getItem()).getMana(wand);
				if (wandMana < cost) {

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
								modifiers.set(WizardryItems.range_upgrade, blast + level * Constants.RANGE_INCREASE_PER_LEVEL, true);
								modifiers.set(WizardryItems.blast_upgrade, range + level * Constants.BLAST_RADIUS_INCREASE_PER_LEVEL, true);
							}
							onMetaMagicFinished(player, ASSpells.arcane_augmentation, ASPotions.arcane_augmentation);

						} else if (potion.equals(ASPotions.intensifying_focus)) {
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
			if (ItemNewArtefact.isNewArtefactActive(player, ASItems.belt_enchanter)) {
				if (event.getSpell().getType() == SpellType.BUFF) {
					modifiers.set(WizardryItems.duration_upgrade, modifiers.get(WizardryItems.duration_upgrade) * 1.2f, false);
				}
			}

			/// custom artefact types
			for (ItemNewArtefact artefact : getActiveNewArtefacts(player)) {

				if (artefact == ASItems.head_curse) {
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
				} else if (artefact == ASItems.belt_scroll_holder && ASBaublesIntegration.enabled()) {
					ItemStack holder = ASBaublesIntegration.getBeltSlotItemStack(player);
					if (holder.getItem() instanceof ItemBeltScrollHolder) {
						ItemStack scroll = ItemBeltScrollHolder.getScroll(holder);
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
				} else if (artefact == ASItems.head_lightning && ASBaublesIntegration.enabled()) {

					if (event.getSpell().getElement() == Element.LIGHTNING && player.world.getBlockState(player.getPosition()).getBlock() == ASBlocks.lightning_block) {
						modifiers.set(WizardryItems.blast_upgrade, modifiers.get(WizardryItems.blast_upgrade) + 0.25F, true);
						modifiers.set(WizardryItems.range_upgrade, modifiers.get(WizardryItems.range_upgrade) + 0.25F, true);
						modifiers.set(WizardryItems.duration_upgrade, modifiers.get(WizardryItems.duration_upgrade) + 0.25F, true);
					}
				}
			}

			for (ItemArtefact artefact : getActiveArtefacts(player)) {

				float potency = modifiers.get(SpellModifiers.POTENCY);
				float cost = modifiers.get(SpellModifiers.COST);

				if (artefact == ASItems.charm_mana_orb) {
					modifiers.set(SpellModifiers.COST, 0.85f * cost, false);

				} else if (artefact == ASItems.amulet_mana) {
					modifiers.set(SpellModifiers.COST, 0.90f * cost, false);

				} else if (artefact == ASItems.ring_blast) {
					modifiers.set(SpellModifiers.COST, 1.25f * cost, false);
					event.getModifiers().set(WizardryItems.blast_upgrade, event.getModifiers().get(WizardryItems.blast_upgrade) + 0.25F, true);

				} else if (artefact == ASItems.ring_range) {
					modifiers.set(SpellModifiers.COST, 1.25f * cost, false);
					event.getModifiers().set(WizardryItems.range_upgrade, event.getModifiers().get(WizardryItems.range_upgrade) + 0.25F, true);

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
				}

			}
			if (jewelsSetCount > 1) {
				float potency = modifiers.get(SpellModifiers.POTENCY);
				float potencyBonus = ((jewelsSetCount - 1) * 5f) / 100; // +5% per set piece
				modifiers.set(SpellModifiers.POTENCY, potencyBonus + potency, false);

			}

			WizardData data = WizardData.get(player);

			if (data != null && !(event.getSpell() instanceof Contingency)) {

				// casting the spell as a metamagic projectile
				if (data.getVariable(MetamagicProjectile.METAMAGIC_PROJECTILE) != null && data.getVariable(MetamagicProjectile.METAMAGIC_PROJECTILE).booleanValue()) {

					if (event.getSpell() instanceof MetaSpellBuff || event.getSpell() instanceof SpellRay || event.getSpell() instanceof SpellProjectile ||
							event.getSpell() instanceof MetamagicProjectile) {
						return;
					}

					EntityMetamagicProjectile projectile = new EntityMetamagicProjectile(player.world);
					projectile.setCaster(player);
					projectile.setStoredSpell(event.getSpell());
					projectile.aim(player, calculateVelocity(projectile, modifiers, player.getEyeHeight() - (float) EntityMagicProjectile.LAUNCH_Y_OFFSET));
					projectile.damageMultiplier = modifiers.get(SpellModifiers.POTENCY);

					// Spawns the projectile in the world
					if (!player.world.isRemote) { player.world.spawnEntity(projectile); }
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
						if (!event.getWorld().isRemote) { player.world.spawnEntity(projectile); }
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
					if (event.getWorld().isRemote) { Contingency.spawnParticles(event.getWorld(), player, Contingency.Type.fromName(spellTag)); }
					Contingency.playSound(event.getWorld(), player.getPosition());

					data.setVariable(Contingency.ACTIVE_CONTINGENCY_LISTENER, null);
					data.setVariable(Contingency.ACTIVE_CONTINGENCIES, activeContingencies);
					data.sync();
					event.setCanceled(true);
				}
			}
		}

		// Dimensional Anchor check
		if (DimensionalAnchor.shouldPreventSpell(event.getCaster(), event.getWorld(), event.getSpell())) {
			event.setCanceled(true);
		}

	}

	@SubscribeEvent
	public static void onSpellCastPostEvent(SpellCastEvent.Post event) {
		if (event.getCaster() instanceof EntityPlayer) {

			EntityPlayer player = (EntityPlayer) event.getCaster();

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

				if (artefact == ASItems.ring_prismarine) {

					if (player.isBurning()) {
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
		}

		// tick artefacts
		if (event.phase == TickEvent.Phase.END) {
			EntityPlayer player = event.player;
			if (ASBaublesIntegration.enabled()) {
				ASBaublesIntegration.tickWornArtefacts(player);
			}
		}

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

		// We have no way of checking if it's a spawner in getCanSpawnHere() so this has to be done here instead
		if (event.getEntity() instanceof ISummonedCreature) {
			if (event.getEntity() instanceof EntityCreature && ((ISummonedCreature) event.getEntity()).getOwner() != null) {
				Entity owner = ((ISummonedCreature) event.getEntity()).getOwner();

				if (owner instanceof EntityPlayer && ItemNewArtefact.isNewArtefactActive(((EntityPlayer) owner), ASItems.head_minions)) {
					if (ItemNewArtefact.isNewArtefactActive(((EntityPlayer) owner), ASItems.head_minions)) {
						EntityCreature creature = (EntityCreature) event.getEntity();
						EntitySummonAIFollowOwner task = new EntitySummonAIFollowOwner(creature, 1.0D, 10.0F, 2.0F);
						creature.tasks.addTask(5, task);
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
					if (!ImbueWeapon.isBow(bow)) { return; }
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

	public static float calculateVelocity(EntityMagicProjectile projectile, SpellModifiers modifiers, float launchHeight) {
		// The required range
		float range = 20 * modifiers.get(WizardryItems.range_upgrade);

		if (projectile.hasNoGravity()) {
			// No sensible spell will do this - range is meaningless if the projectile has no gravity or lifetime
			if (projectile.getLifetime() <= 0) { return 1.5f; }
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
