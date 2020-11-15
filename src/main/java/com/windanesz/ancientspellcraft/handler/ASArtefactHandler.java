package com.windanesz.ancientspellcraft.handler;

import com.windanesz.ancientspellcraft.item.ItemKnowledgeOrb;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftItems;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftPotions;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftSpells;
import com.windanesz.ancientspellcraft.spell.TimeKnot;
import com.windanesz.ancientspellcraft.util.ASUtils;
import electroblob.wizardry.Wizardry;
import electroblob.wizardry.constants.Constants;
import electroblob.wizardry.constants.Element;
import electroblob.wizardry.constants.Tier;
import electroblob.wizardry.data.IStoredVariable;
import electroblob.wizardry.data.Persistence;
import electroblob.wizardry.data.WizardData;
import electroblob.wizardry.entity.living.EntitySkeletonMinion;
import electroblob.wizardry.event.SpellBindEvent;
import electroblob.wizardry.event.SpellCastEvent;
import electroblob.wizardry.item.ItemArtefact;
import electroblob.wizardry.item.ItemWand;
import electroblob.wizardry.potion.Curse;
import electroblob.wizardry.registry.Spells;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.registry.WizardrySounds;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.spell.SpellBuff;
import electroblob.wizardry.util.SpellModifiers;
import electroblob.wizardry.util.WandHelper;
import net.minecraft.client.Minecraft;
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
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static electroblob.wizardry.item.ItemArtefact.getActiveArtefacts;
import static electroblob.wizardry.item.ItemArtefact.isArtefactActive;

/**
 * A class for handling Ancient Spellcraft artefact events.
 * Artefacts should be registered as {@link ItemArtefact)
 * Author: Dan
 */

@Mod.EventBusSubscriber
public final class ASArtefactHandler {
	private ASArtefactHandler() {} // no instances

	public static final IStoredVariable<Integer> COUNTDOWN_KEY = IStoredVariable.StoredVariable.ofInt("artefactEternityCountdown", Persistence.NEVER).withTicker(ASArtefactHandler::update);
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
					System.out.println("casted the thing!");
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
							System.out.println("we are here!! its a buff");
							try {
								// FIXME: get rid of reflection :(
								// once the access is public/has getter

								SpellBuff obj = (SpellBuff) spell;
								Field field = ASUtils.ReflectionUtil.getField(obj.getClass(), "potionSet");
								ASUtils.ReflectionUtil.makeAccessible(field);
								Set<Potion> potionset = (Set<Potion>) field.get(obj);
								System.out.println("potionset:" + potionset);

								if (potionset.contains(event.getPotionEffect().getPotion())) {
									System.out.println("its a potion effect from the spell!!");

									WizardData data = WizardData.get(player);
									data.setVariable(COUNTDOWN_KEY, 20);
									data.setVariable(SPELL_ID, spell.metadata());

									//									if (!player.world.isRemote) {
									//										if (ItemArtefact.findMatchingWandAndCast(player, Spells.water_breathing)) {
									//											System.out.println("casted the thing!");
									//
									//										}
								}
								//										if (ItemArtefact.findMatchingWandAndCast(player, spell)) {
								//											System.out.println("casted the thing!");
								//										}

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

			for (ItemArtefact artefact : getActiveArtefacts(player)) {

				if (artefact == AncientSpellcraftItems.charm_reanimation) {
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

			EntityPlayer player = (EntityPlayer) event.getCaster();
			SpellModifiers modifiers = event.getModifiers();

			int jewelsSetCount = 0;
			for (ItemArtefact artefact : getActiveArtefacts(player)) {

				float potency = modifiers.get(SpellModifiers.POTENCY);
				float cost = modifiers.get(SpellModifiers.COST);

				if (artefact == AncientSpellcraftItems.charm_mana_orb) {
					modifiers.set(SpellModifiers.COST, 0.85f * cost, false);

				} else if (artefact == AncientSpellcraftItems.amulet_mana) {
					modifiers.set(SpellModifiers.COST, 0.90f * cost, false);

				} else if (artefact == AncientSpellcraftItems.ring_blast) {
					modifiers.set(SpellModifiers.COST, 1.25f * cost, false);
					//					System.out.println("blast before: " + event.getModifiers().get(WizardryItems.blast_upgrade));
					event.getModifiers().set(WizardryItems.blast_upgrade, event.getModifiers().get(WizardryItems.blast_upgrade) + 0.25F, true);
					//					System.out.println("blast after: " + event.getModifiers().get(WizardryItems.blast_upgrade));

				} else if (artefact == AncientSpellcraftItems.ring_range) {
					modifiers.set(SpellModifiers.COST, 1.25f * cost, false);
					//					System.out.println("range_upgrade before: " + event.getModifiers().get(WizardryItems.range_upgrade));
					event.getModifiers().set(WizardryItems.range_upgrade, event.getModifiers().get(WizardryItems.range_upgrade) + 0.25F, true);
					//					System.out.println("range_upgrade after: " + event.getModifiers().get(WizardryItems.range_upgrade));

				} else if (artefact == AncientSpellcraftItems.charm_elemental_grimoire) {
					if (event.getSpell().getElement() == Element.FIRE || event.getSpell().getElement() == Element.ICE || event.getSpell().getElement() == Element.LIGHTNING) {
						//						System.out.println("before:" + modifiers.get(SpellModifiers.POTENCY));
						modifiers.set(SpellModifiers.POTENCY, 0.1f + potency, false);
						//						System.out.println("after:" + modifiers.get(SpellModifiers.POTENCY));
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
				System.out.println("potency before: " + modifiers.get(SpellModifiers.POTENCY));
				modifiers.set(SpellModifiers.POTENCY, potencyBonus + potency, false);
				System.out.println("potencyBOuns: " + potencyBonus);
				System.out.println("potency after: " + modifiers.get(SpellModifiers.POTENCY));

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

						// play orb swirl animation for extra fanciness
						ItemStack artefact = getArtefactItemStack(player, (ItemArtefact) AncientSpellcraftItems.charm_knowledge_orb);
						if (!(((ItemKnowledgeOrb) artefact.getItem()).isSwirlAnimationInProgess(artefact))) {
							((ItemKnowledgeOrb) artefact.getItem()).startSwirlAnimation(artefact);
						}

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
					System.out.println("(highest) centre slot: " + centre);
					System.out.println("(highest) upgrade slot: " + upgrade);
				}
			}
		}
	}

	private static ItemStack getArtefactItemStack(EntityPlayer player, ItemArtefact artefact) {
		return ASUtils.getItemStackFromInventoryHotbar(player, artefact);
	}

	private static Spell getCurrentSpellFromSpellBearingArtefact(Item item, ItemStack stack) {
		System.out.println("current spell: " + Spell.byMetadata(stack.getItemDamage()).getRegistryName());
		return Spell.byMetadata(stack.getItemDamage());
	}

	@SubscribeEvent
	public static void onPlayerTickEvent(TickEvent.PlayerTickEvent event) {

		if (event.phase == TickEvent.Phase.START) {

			EntityPlayer player = event.player;

			for (ItemArtefact artefact : getActiveArtefacts(player)) {

				if (artefact == AncientSpellcraftItems.ring_prismarine) {

					if (player.isBurning()) {
						float i = player.getCooldownTracker().getCooldown(AncientSpellcraftItems.ring_prismarine, Minecraft.getMinecraft().getRenderPartialTicks());
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
}
