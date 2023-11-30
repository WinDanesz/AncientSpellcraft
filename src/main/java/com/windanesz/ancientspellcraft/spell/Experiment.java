package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.Settings;
import com.windanesz.ancientspellcraft.misc.WeightedRandom;
import com.windanesz.ancientspellcraft.registry.ASItems;
import com.windanesz.ancientspellcraft.registry.ASSpells;
import com.windanesz.ancientspellcraft.util.ASUtils;
import electroblob.wizardry.Wizardry;
import electroblob.wizardry.block.BlockReceptacle;
import electroblob.wizardry.constants.Element;
import electroblob.wizardry.constants.Tier;
import electroblob.wizardry.data.IStoredVariable;
import electroblob.wizardry.data.Persistence;
import electroblob.wizardry.data.WizardData;
import electroblob.wizardry.item.ItemArtefact;
import electroblob.wizardry.item.ItemWizardArmour;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.misc.Forfeit;
import electroblob.wizardry.potion.Curse;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.NBTExtras;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.ParticleBuilder.Type;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Experiment extends Spell implements IClassSpell {

	public static final String EXPERIMENTED_SPELLS = "experimented_spells";
	public static final String BASE_SUCCESS_CHANCE = "base_success_chance";
	public static final String FIRST_CAST = "first_cast";
	public static final String THEORY_POINT_COUNT = "theory_point_count";
	public static final String FORFEIT_WEIGHT = "forfeit_weight";
	public static final String BUFF_WEIGHT = "buff_weight";
	public static final String DEBUFF_WEIGHT = "debuff_weight";
	public static final String NO_EFFECT_WEIGHT = "no_effect_weight";
	public static final String LAST_EFFECT = "last_effect";

	private static final IStoredVariable<NBTTagCompound> EXPERIMENT_DATA_NBT = IStoredVariable.StoredVariable.ofNBT("experimentData", Persistence.ALWAYS).setSynced();

	public Experiment() {
		super(AncientSpellcraft.MODID, "experiment", SpellActions.SUMMON, false);
		WizardData.registerStoredVariables(EXPERIMENT_DATA_NBT);
		addProperties(BASE_SUCCESS_CHANCE, FORFEIT_WEIGHT, BUFF_WEIGHT, DEBUFF_WEIGHT, NO_EFFECT_WEIGHT);
	}

	public static float getSuccessChance(EntityPlayer player) {
		float chance = ASSpells.experiment.getProperty(BASE_SUCCESS_CHANCE).floatValue();

		List<Spell> usableSpells = new ArrayList<>(WizardData.get(player).spellsDiscovered);
		List<Spell> experimentedSpells = (List<Spell>) NBTExtras.NBTToList(getData(player).getTagList(EXPERIMENTED_SPELLS, Constants.NBT.TAG_STRING), (NBTTagString tag) -> Spell.get(tag.getString()));
		usableSpells.removeAll(experimentedSpells);

		if (!usableSpells.isEmpty()) {
			List<Element> distinctElements = usableSpells.stream().map(Spell::getElement).collect(Collectors.toList());
			chance = (float) (chance + Math.pow(distinctElements.size(), 2));
		}

		return Math.min(chance, 100f);
	}

	public static List<Element> getResearchedElements(EntityPlayer player) {
		List<Spell> usableSpells = new ArrayList<>(WizardData.get(player).spellsDiscovered);
		List<Spell> experimentedSpells = (List<Spell>) NBTExtras.NBTToList(getData(player).getTagList(EXPERIMENTED_SPELLS, Constants.NBT.TAG_STRING), (NBTTagString tag) -> Spell.get(tag.getString()));
		usableSpells.removeAll(experimentedSpells);

		if (!usableSpells.isEmpty()) {
			return usableSpells.stream().map(Spell::getElement).collect(Collectors.toList());
		}

		return new ArrayList<Element>();
	}

	public static boolean isFirstCast(NBTTagCompound experimentData) {
		return !experimentData.hasKey(FIRST_CAST) || experimentData.hasKey(FIRST_CAST) && experimentData.getBoolean(FIRST_CAST);
	}

	public static int getDiscoveredSpellCount(EntityPlayer player) {
		WizardData data = WizardData.get(player);
		return data != null ? data.spellsDiscovered.size() : 0;
	}

	public static NBTTagCompound getData(EntityPlayer player) {
		WizardData data = WizardData.get(player);
		if (data != null) {
			NBTTagCompound experimentData = data.getVariable(EXPERIMENT_DATA_NBT);

			if (experimentData != null) {
				return experimentData;
			}

		}
		return new NBTTagCompound();
	}

	public static int getTheoryPoints(EntityPlayer player) {
		NBTTagCompound data = getData(player);
		if (data != null && data.hasKey(THEORY_POINT_COUNT)) {
			return data.getInteger(THEORY_POINT_COUNT);
		}
		return 0;
	}

	public static void addTheoryPoint(EntityPlayer player, int amount) {
		NBTTagCompound experimentData = getData(player);
		experimentData = addTheoryPoint(experimentData, amount);
		WizardData data = WizardData.get(player);
		if (data != null) {
			data.setVariable(EXPERIMENT_DATA_NBT, experimentData);
			data.sync();
		}
	}

	public static void consumeTheoryPoint(EntityPlayer player, int amount) {
		NBTTagCompound experimentData = getData(player);
		experimentData = consumeTheoryPoint(experimentData, amount);
		WizardData data = WizardData.get(player);
		if (data != null) {
			data.setVariable(EXPERIMENT_DATA_NBT, experimentData);
			data.sync();
		}
	}

	private static NBTTagCompound addTheoryPoint(NBTTagCompound experimentData, int amount) {
		int points = amount;
		if (experimentData.hasKey(THEORY_POINT_COUNT)) {
			points += experimentData.getInteger(THEORY_POINT_COUNT);
		}
		experimentData.setInteger(THEORY_POINT_COUNT, points);
		return experimentData;
	}

	private static NBTTagCompound consumeTheoryPoint(NBTTagCompound experimentData, int amount) {
		int points = 0;
		if (experimentData.hasKey(THEORY_POINT_COUNT)) {
			points = experimentData.getInteger(THEORY_POINT_COUNT) - amount;
		}
		experimentData.setInteger(THEORY_POINT_COUNT, points);
		return experimentData;
	}

	public static int getMinKnownSpellAmount() {
		return Math.round((float) (3 * Math.sqrt(Spell.registry.getKeys().size())));
	}

	@Override
	public boolean cast(World world, EntityPlayer caster, EnumHand hand, int ticksInUse, SpellModifiers modifiers) {

		NBTTagCompound experimentData = getData(caster);
		WizardData data = WizardData.get(caster);

		float chance = getProperty(BASE_SUCCESS_CHANCE).floatValue();

		//	Get +2% chance for a theory with the amulet artefact
		if (ItemArtefact.isArtefactActive(caster, ASItems.amulet_inspiration)) {
			chance += 0.02f;
		}

		if (data != null) {

			if (!isFirstCast(experimentData)) {

				List<Spell> discoveredSpells = new ArrayList<>(data.spellsDiscovered);

				// should always be true if this is not the first cast
				if (!experimentData.hasKey(EXPERIMENTED_SPELLS)) {
					NBTExtras.storeTagSafely(experimentData, EXPERIMENTED_SPELLS, NBTExtras.listToNBT(discoveredSpells, spell -> new NBTTagString(spell.getRegistryName().toString())));
				}
				List<Spell> experimentedSpells = (List<Spell>) NBTExtras.NBTToList(experimentData.getTagList(EXPERIMENTED_SPELLS, Constants.NBT.TAG_STRING), (NBTTagString tag) -> Spell.get(tag.getString()));

				List<Spell> usableSpells = new ArrayList<>(discoveredSpells);
				usableSpells.removeAll(experimentedSpells);

				if (!usableSpells.isEmpty()) { }

				List<Element> distinctElements = usableSpells.stream().map(Spell::getElement).distinct().collect(Collectors.toList());

				if (!distinctElements.isEmpty()) {
					chance = (float) (chance + (Math.pow(distinctElements.size(), 2) * 0.01f));
				}

				// Time to experiment
				boolean success = data.synchronisedRandom.nextFloat() <= chance;

				if (success) {
					// success logic

					if (!world.isRemote) {
						// use up "spell points"
						experimentedSpells.addAll(usableSpells);
						NBTExtras.storeTagSafely(experimentData, EXPERIMENTED_SPELLS, NBTExtras.listToNBT(experimentedSpells, spell -> new NBTTagString(spell.getRegistryName().toString())));

						// add a theory point
						addTheoryPoint(experimentData, 1);
						data.setVariable(EXPERIMENT_DATA_NBT, experimentData);
						data.sync();
						ASUtils.sendMessage(caster, "spell." + this.getUnlocalisedName() + ".received_point", true, 1);
					}
					if (world.isRemote) {
						spawnParticles(world, caster, new SpellModifiers(), data.synchronisedRandom);
					}
					return true;
				} else {
					WeightedRandom<String> effects = new WeightedRandom<>(data.synchronisedRandom);
					effects.addEntry(FORFEIT_WEIGHT, getProperty(FORFEIT_WEIGHT).floatValue());
					effects.addEntry(NO_EFFECT_WEIGHT, getProperty(NO_EFFECT_WEIGHT).floatValue());
					effects.addEntry(BUFF_WEIGHT, getProperty(BUFF_WEIGHT).floatValue());
					effects.addEntry(DEBUFF_WEIGHT, getProperty(DEBUFF_WEIGHT).floatValue());

					String effectType = effects.getRandom();
					String effect = "";

					switch (effectType) {
						case FORFEIT_WEIGHT: {// tier: the more bonus points the player had, the less adverse the effects can be
							System.out.println(FORFEIT_WEIGHT);
							// TODO: artefact to reduce max tier by one
							int tier = data.synchronisedRandom.nextInt(Math.max(0, Math.round(3 - (distinctElements.size() / 3f))));
							Element element = Element.values()[Math.max(1, data.synchronisedRandom.nextInt(Element.values().length))];
							Forfeit forfeit = Forfeit.getRandomForfeit(data.synchronisedRandom, Tier.values()[tier], element);
							if (forfeit != null && !world.isRemote) {
								forfeit.apply(world, caster);
								effect = forfeit.getMessageForScroll().getUnformattedText();

								try {
									Field field = null;
									field = ASUtils.ReflectionUtil.getField(forfeit.getClass(), "name");
									ASUtils.ReflectionUtil.makeAccessible(field);
									effect = ((ResourceLocation) field.get(forfeit)).toString();
								}
								catch (Exception e) {}
								caster.sendMessage(forfeit.getMessage(new TextComponentTranslation("item." + AncientSpellcraft.MODID + ":tome.generic")));
							}
							//							if (!world.isRemote) {
							//								caster.sendStatusMessage(new TextComponentTranslation("spell." + this.getUnlocalisedName() + ".fail"), false);
							//							}
							break;
						}
						case NO_EFFECT_WEIGHT:
							break;
						case BUFF_WEIGHT: {
							List<Potion> potions = ForgeRegistries.POTIONS.getValuesCollection().stream().filter(p -> !p.isBadEffect()).collect(Collectors.toList());
							for (int i = 0; i < 10; i++) {
								Potion randomPotion = potions.get(data.synchronisedRandom.nextInt(potions.size()));
								if (!Arrays.asList(Settings.generalSettings.experiment_buff_blacklist).contains(randomPotion.getRegistryName().toString())) {
									// todo?: artefact
									int duration = data.synchronisedRandom.nextInt(1000);
									effect = randomPotion.getRegistryName().toString() + "," + duration;
									if (!world.isRemote) { caster.addPotionEffect(new PotionEffect(randomPotion, duration)); }
									ASUtils.sendMessage(caster, "spell." + this.getUnlocalisedName() + ".buff_effect", false, new TextComponentTranslation(randomPotion.getName()));
									break;
								}
							}
						}
						break;
						case DEBUFF_WEIGHT: {
							List<Potion> potions = ForgeRegistries.POTIONS.getValuesCollection().stream().filter(p -> !(p instanceof Curse) && p.isBadEffect()).collect(Collectors.toList());
							for (int i = 0; i < 10; i++) {
								Potion randomPotion = potions.get(data.synchronisedRandom.nextInt(potions.size()));
								if (!Arrays.asList(Settings.generalSettings.experiment_buff_blacklist).contains(randomPotion.getRegistryName().toString())) {
									// todo?: artefact
									int duration = data.synchronisedRandom.nextInt(1000);
									effect = randomPotion.getRegistryName().toString() + "," + duration;
									if (!world.isRemote) { caster.addPotionEffect(new PotionEffect(randomPotion, duration)); }
									ASUtils.sendMessage(caster, "spell." + this.getUnlocalisedName() + ".buff_effect", false, new TextComponentTranslation(randomPotion.getName()));
									break;
								}
							}
						}
						break;
					}
					//						else {
					//							EntityMetamagicProjectile projectile = new EntityMetamagicProjectile(caster.world);
					//							projectile.setCaster(null);
					//							Spell spellToCast = discoveredSpells.get(data.synchronisedRandom.nextInt(discoveredSpells.size()));
					//							if (spellToCast.canBeCastBy(new TileEntityDispenser())) {
					//								projectile.setStoredSpell(spellToCast);
					//								projectile.aim(caster, ASEventHandler.calculateVelocity(projectile, modifiers, caster.getEyeHeight() - (float) EntityMagicProjectile.LAUNCH_Y_OFFSET));
					//								projectile.damageMultiplier = modifiers.get(SpellModifiers.POTENCY);
					//								if (!world.isRemote) { world.spawnEntity(projectile); }
					//							} else {
					//								if (!world.isRemote) {
					//									SpellcastUtils.tryCastSpellAsPlayer(caster, spellToCast, EnumHand.MAIN_HAND, AncientSpellcraft.SAGE_ITEM, new SpellModifiers(), 40);
					//								}
					//							}
					//						}
					if (world.isRemote) {
						spawnParticles(world, caster, new SpellModifiers(), data.synchronisedRandom);
					}

					// store this in case the Perfect Theory spell reuses it
					if (!effect.isEmpty()) {
						NBTTagCompound nbt = new NBTTagCompound();
						nbt.setString("effectType", effectType);
						nbt.setString("effect", effect);
						storeLastEffect(caster, nbt);
					}

					return true;
				}

			} else {
				// first cast logic, should only ever be used once for each player

				int knownSpellAmount = getDiscoveredSpellCount(caster);
				int minimumKnownSpellAmount = getMinKnownSpellAmount();

				if (ItemArtefact.isArtefactActive(caster, ASItems.charm_sage_diary)) {
					minimumKnownSpellAmount = (int) (minimumKnownSpellAmount * 0.6f);
				}

				if (knownSpellAmount < minimumKnownSpellAmount) {
					// need to identify more spells
					if (!world.isRemote) {
						caster.sendStatusMessage(new TextComponentTranslation("spell." + this.getUnlocalisedName() + ".not_enough_known_spells",
								knownSpellAmount, minimumKnownSpellAmount, minimumKnownSpellAmount - knownSpellAmount), false);
					}
					return false;
				} else {
					// ready for first cast
					int theoryPoints = 0;

					int extraKnowledge = minimumKnownSpellAmount - knownSpellAmount;
					if (extraKnowledge > 0) {
						// give some bonus..
						// we have 8 elements (including Ancient)
						int rolls = extraKnowledge / 8;
						for (int i = 0; i < rolls; i++) {
							if (data.synchronisedRandom.nextFloat() < 0.4f) {
								theoryPoints++;
							}
						}

						ASUtils.sendMessage(caster, "spell." + this.getUnlocalisedName() + ".first_cast_with_bonus", false, theoryPoints);
						ASUtils.sendMessage(caster, "spell." + this.getUnlocalisedName() + ".current_amount_of_theory_points", false, getTheoryPoints(caster));
					}
					// add a theory point
					addTheoryPoint(experimentData, theoryPoints);
					experimentData.setBoolean(FIRST_CAST, false);
					data.setVariable(EXPERIMENT_DATA_NBT, experimentData);

					data.sync();
					if (world.isRemote) {
						spawnParticles(world, caster, new SpellModifiers(), data.synchronisedRandom);
					}
					return true;
				}
			}
		}
		if (world.isRemote) {
			spawnParticles(world, caster, new SpellModifiers(), data.synchronisedRandom);
		}
		return true;
	}

	@Override
	public boolean cast(World world, EntityLiving caster, EnumHand hand, int ticksInUse, EntityLivingBase target, SpellModifiers modifiers) {
		return false;
	}

	@Override
	public ItemWizardArmour.ArmourClass getArmourClass() { return ItemWizardArmour.ArmourClass.SAGE; }

	public void spawnParticle() {

	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == ASItems.mystic_spell_book || item == WizardryItems.scroll;
	}

	public static void spawnParticle(World world, EntityLivingBase caster, SpellModifiers modifiers, Random rand) {

		int effect = rand.nextInt(13);
		//effect = 12;
		float r = world.rand.nextFloat();
		float g = world.rand.nextFloat();
		float b = world.rand.nextFloat();

		switch (effect) {
			case 0:
				for (int i = 0; i < 10; i++) {
					double x = caster.posX + world.rand.nextDouble() * 2 - 1;
					double y = caster.posY + caster.getEyeHeight() - 0.5 + world.rand.nextDouble();
					double z = caster.posZ + world.rand.nextDouble() * 2 - 1;
					ParticleBuilder.create(Type.SPARKLE).pos(x, y, z).vel(0, 0.1, 0).clr(r, g, b).spawn(world);
				}
				break;
			case 1:
				ParticleBuilder.create(Type.BUFF).entity(caster).clr(r, g, b).spawn(world);

				for (int i = 0; i < 20; i++) {
					double x = caster.posX + world.rand.nextDouble() * 2 - 1;
					double y = caster.posY + caster.getEyeHeight() - 0.5 + world.rand.nextDouble();
					double z = caster.posZ + world.rand.nextDouble() * 2 - 1;
					ParticleBuilder.create(Type.SPARKLE).pos(x, y, z).vel(0, 0.14, 0).clr(r, g, b)
							.time(20 + world.rand.nextInt(12)).spawn(world);
					ParticleBuilder.create(Type.DARK_MAGIC).pos(x, y, z).clr(r, g, b).spawn(world);
				}
				break;
			case 2:
				double particleX, particleZ;

				for (int i = 0; i < 40 * modifiers.get(WizardryItems.blast_upgrade); i++) {

					particleX = caster.getPositionVector().x - 1.0d + 2 * world.rand.nextDouble();
					particleZ = caster.getPositionVector().z - 1.0d + 2 * world.rand.nextDouble();
					ParticleBuilder.create(Type.DARK_MAGIC).pos(particleX, caster.getPositionVector().y, particleZ)
							.vel(particleX - caster.getPositionVector().x, 0, particleZ - caster.getPositionVector().z).clr(r, g, b).spawn(world);

					particleX = caster.getPositionVector().x - 1.0d + 2 * world.rand.nextDouble();
					particleZ = caster.getPositionVector().z - 1.0d + 2 * world.rand.nextDouble();
					ParticleBuilder.create(Type.SPARKLE).pos(particleX, caster.getPositionVector().y, particleZ)
							.vel(particleX - caster.getPositionVector().x, 0, particleZ - caster.getPositionVector().z).time(30).clr(r, g, b).spawn(world);

					particleX = caster.getPositionVector().x - 1.0d + 2 * world.rand.nextDouble();
					particleZ = caster.getPositionVector().z - 1.0d + 2 * world.rand.nextDouble();

					IBlockState block = world.getBlockState(new BlockPos(caster.getPositionVector().x, caster.getPositionVector().y - 0.5, caster.getPositionVector().z));

					if (block != null) {
						world.spawnParticle(EnumParticleTypes.BLOCK_DUST, particleX, caster.getPositionVector().y,
								particleZ, particleX - caster.getPositionVector().x, 0, particleZ - caster.getPositionVector().z, Block.getStateId(block));
					}
				}
				break;
			case 3:
				ParticleBuilder.create(Type.SPHERE).pos(caster.getPositionVector().add(0, 0.5, 0)).scale(2 * ((float) rand.nextInt(2) + 1)).clr(r, g, b).spawn(world);
				break;
			case 4:
				for (int i = 0; i < 2 * rand.nextInt(5); i++) {
					ParticleBuilder.create(Type.CLOUD, caster).pos(rand.nextFloat(), rand.nextFloat(), rand.nextFloat())
							.clr(r, g, b).shaded(true).spawn(world);
				}
				break;
			case 5:
				for (int i = 0; i < 5; ++i) {
					world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, caster.posX + (world.rand.nextDouble() - 0.5D) * (double) caster.width,
							caster.posY + world.rand.nextDouble() * (double) caster.height + 0.3f, caster.posZ + (world.rand.nextDouble() - 0.5D) *
									(double) caster.width, 0.0D, 0.0D, 0.0D);
				}
				break;
			case 6:
				for (int i = 0; i < 20 + rand.nextInt(20); i++) {
					double dx = (world.rand.nextDouble() * (world.rand.nextBoolean() ? 1 : -1)) * 0.1;
					double dy = (world.rand.nextDouble() * (world.rand.nextBoolean() ? 1 : -1)) * 0.1;
					double dz = (world.rand.nextDouble() * (world.rand.nextBoolean() ? 1 : -1)) * 0.1;

					ParticleBuilder.create(Type.SPARKLE)
							.entity(caster)
							.clr(r, g, b)
							.pos(0, caster.height / 2, 0)
							.vel(dx, dy, dz)
							.spawn(world);
				}
				break;
			case 7:
				for (int i = 0; i < 20 + rand.nextInt(20); i++) {
					ParticleBuilder.create(rand.nextBoolean() ? Type.SPARKLE : Type.DUST)
							//.clr(r,g,b)
							.fade(0, 0, 0)
							.spin(1.2, rand.nextFloat() * 0.1f)
							.time(rand.nextInt(20) * 3)
							.entity(caster)
							.pos(0, 1, 0)
							.scale(1.2f)
							.spawn(world);
				}
				break;
			case 8:
				for (int i = 0; i < 20 + rand.nextInt(20); i++) {
					ParticleBuilder.create(Type.SPARKLE).pos(r, g, b).vel(0, 0.03, 0).time(50)
							.clr(r, g, b).spawn(world);
				}
				break;
			case 9:
				ParticleBuilder.create(Type.FLASH).face(EnumFacing.UP).entity(caster).vel(0, 0.1, 0).time(60)
						.clr(r, g, b).scale(5).spawn(world);
				break;
			case 10:
				List<ResourceLocation> list = Arrays.asList(Type.MAGIC_FIRE, Type.DARK_MAGIC, Type.SNOW, Type.LEAF, Type.LIGHTNING);
				ResourceLocation type = list.get(rand.nextInt(list.size()));
				for (int i = 0; i < 10 + rand.nextInt(20); i++) {
					ParticleBuilder.create(type).vel(0, 0.1, 0)
							.spin(0.8f, 0.03f + rand.nextFloat() / 2).time(40).entity(caster).scale(1.2f).spawn(world);

				}
				break;
			case 11:
				int[] colours = BlockReceptacle.PARTICLE_COLOURS.get(Element.values()[rand.nextInt(Element.values().length)]);
				for (int i = 0; i < 60 + rand.nextInt(20); i++) {
					ParticleBuilder.create(ParticleBuilder.Type.DUST).entity(caster).pos(world.rand.nextBoolean() ? world.rand.nextFloat() : -world.rand.nextFloat(),
							0.3, world.rand.nextBoolean() ? world.rand.nextFloat() : -world.rand.nextFloat())
							.vel(0, 0.05 + (world.rand.nextFloat() * 0.1), 0).clr(colours[1]).fade(colours[2]).time(40).shaded(false).spawn(world);
				}
				for (int i = 0; i < 60 + rand.nextInt(20); i++) {
					ParticleBuilder.create(ParticleBuilder.Type.DUST).entity(caster).pos(world.rand.nextBoolean() ? world.rand.nextFloat() : -world.rand.nextFloat(),
							0.3, world.rand.nextBoolean() ? world.rand.nextFloat() : -world.rand.nextFloat()).spin(world.rand.nextFloat(), world.rand.nextFloat() * 0.1)
							.vel(0, 0.05 + (world.rand.nextFloat() * 0.1), 0).clr(colours[1]).fade(colours[2]).time(40).shaded(false).spawn(world);
				}
				break;
			case 12:
				float radius = 2 + rand.nextFloat() * 5;
				int particleCount = (int) Math.round(0.64f * Math.PI * radius * radius);

				for (int i = 0; i < particleCount; i++) {

					double m = (1 + world.rand.nextDouble() * (radius - 1));
					float angle = world.rand.nextFloat() * (float) Math.PI * 2f;

					ParticleBuilder.create(Type.SPARKLE).vel(0, 0.1, 0).clr(r, g, b).pos(caster.posX + m * MathHelper.cos(angle), caster.posY + 0.1f, caster.posZ + m * MathHelper.sin(angle)).time(40).spawn(world);
				}
				break;
		}
	}


	/**
	 * Spawns buff particles around the caster. Override to add a custom particle effect. Only called client-side.
	 */
	protected void spawnParticles(World world, EntityLivingBase caster, SpellModifiers modifiers, Random rand) {
		spawnParticle(world, caster, modifiers, rand);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getDescription() {
		if (Minecraft.getMinecraft().player != null) {
			EntityPlayer player = Minecraft.getMinecraft().player;
			NBTTagCompound nbt = getData(player);
			if (isFirstCast(nbt)) {
				int minKnownSpells = getMinKnownSpellAmount();
				int knownSpells = getDiscoveredSpellCount(player);
				String descTransKey = "spell." + this.getUnlocalisedName() + ".desc_before_first_cast";
				return Wizardry.proxy.translate(descTransKey, minKnownSpells, knownSpells);
			} else {
				List<Element> list = getResearchedElements(player);
				List<String> test = list.stream().map(e -> e.getDisplayName() + " ").map(f -> f.equals("None ") ? "Ancient" : f).distinct().collect(Collectors.toList());
				return Wizardry.proxy.translate(getDescriptionTranslationKey(), getSuccessChance(player), getTheoryPoints(player), test);
			}
		}
		return super.getDescription();
	}

	private void storeLastEffect(EntityPlayer player, NBTTagCompound nbt) {
		NBTTagCompound experimentData = getData(player);
		experimentData.setTag("lastExperiment", nbt);
		saveData(player, experimentData);
	}

	private void saveData(EntityPlayer player, NBTTagCompound experimentData) {
		WizardData data = WizardData.get(player);
		data.setVariable(EXPERIMENT_DATA_NBT, experimentData);
		data.sync();
	}

	public static NBTTagCompound getLastExperiment(EntityPlayer player) {
		NBTTagCompound nbt = getData(player);
		if (nbt.hasKey("lastExperiment")) {
			return nbt.getCompoundTag("lastExperiment");
		}
		return new NBTTagCompound();
	}
}