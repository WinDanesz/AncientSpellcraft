package com.windanesz.ancientspellcraft.item;

import com.google.common.collect.Lists;
import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.data.SpellComponentList;
import com.windanesz.ancientspellcraft.registry.ASPotions;
import com.windanesz.ancientspellcraft.registry.ASSounds;
import com.windanesz.ancientspellcraft.registry.ASTabs;
import com.windanesz.ancientspellcraft.util.ASUtils;
import electroblob.wizardry.Wizardry;
import electroblob.wizardry.constants.Element;
import electroblob.wizardry.constants.Tier;
import electroblob.wizardry.data.WizardData;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.registry.WizardryPotions;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.ParticleBuilder;
import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.StringUtils;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.IRarity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static com.windanesz.ancientspellcraft.data.SpellComponentList.lookupBySpell;

public class ItemRelic extends Item {

	private static final String SPELL = "spell";
	private static final String INCANTATION = "incantation";
	private static final String ENCHANTMENT = "enchantment";
	private static final String POWER = "power";

	private final EnumRarity rarity;
	private static List<Potion> ALL_POTIONS = new ArrayList<>();
	private static List<Potion> ALL_POWERS = new ArrayList<>();

	public static void initEffects() {

		ALL_POTIONS.add(MobEffects.SPEED);
		ALL_POTIONS.add(MobEffects.SLOWNESS);
		ALL_POTIONS.add(MobEffects.HASTE);
		ALL_POTIONS.add(MobEffects.MINING_FATIGUE);
		ALL_POTIONS.add(MobEffects.STRENGTH);
		ALL_POTIONS.add(MobEffects.JUMP_BOOST);
		ALL_POTIONS.add(MobEffects.NAUSEA);
		ALL_POTIONS.add(MobEffects.REGENERATION);
		ALL_POTIONS.add(MobEffects.RESISTANCE);
		ALL_POTIONS.add(MobEffects.FIRE_RESISTANCE);
		ALL_POTIONS.add(MobEffects.WATER_BREATHING);
		ALL_POTIONS.add(MobEffects.INVISIBILITY);
		ALL_POTIONS.add(MobEffects.BLINDNESS);
		ALL_POTIONS.add(MobEffects.NIGHT_VISION);
		ALL_POTIONS.add(MobEffects.HUNGER);
		ALL_POTIONS.add(MobEffects.WEAKNESS);
		ALL_POTIONS.add(MobEffects.POISON);
		ALL_POTIONS.add(MobEffects.WITHER);
		ALL_POTIONS.add(MobEffects.HEALTH_BOOST);
		ALL_POTIONS.add(MobEffects.ABSORPTION);
		ALL_POTIONS.add(MobEffects.GLOWING);
		ALL_POTIONS.add(MobEffects.LEVITATION);

		// ALL_POTIONS.add(MobEffects.UNLUCK;
		// ALL_POTIONS.add(MobEffects.LUCK;
		// ALL_POTIONS.add(MobEffects.INSTANT_DAMAGE;
		// ALL_POTIONS.add(MobEffects.INSTANT_HEALTH;
		// ALL_POTIONS.add(MobEffects.SATURATION;

		ALL_POTIONS.add(ASPotions.candlelight);
		ALL_POTIONS.add(ASPotions.curse_ward);
		ALL_POTIONS.add(ASPotions.magelight);
		ALL_POTIONS.add(ASPotions.arcane_aegis);
		ALL_POTIONS.add(ASPotions.bubble_head);
		ALL_POTIONS.add(ASPotions.bulwark);
		ALL_POTIONS.add(ASPotions.water_walking);
		ALL_POTIONS.add(ASPotions.fortified_archery);
		ALL_POTIONS.add(ASPotions.lava_vision);
		ALL_POTIONS.add(ASPotions.feather_fall);
		ALL_POTIONS.add(ASPotions.projectile_ward);
		ALL_POTIONS.add(ASPotions.aquatic_agility);
		ALL_POTIONS.add(ASPotions.magma_strider);

		ALL_POTIONS.add(WizardryPotions.muffle);
		ALL_POTIONS.add(WizardryPotions.transience);
		ALL_POTIONS.add(WizardryPotions.sixth_sense);
		ALL_POTIONS.add(WizardryPotions.arcane_jammer);
		// ALL_POTIONS.add(WizardryPotions.paralysis);
		ALL_POTIONS.add(WizardryPotions.fireskin);
		ALL_POTIONS.add(WizardryPotions.frost);
		ALL_POTIONS.add(WizardryPotions.ice_shroud);
		ALL_POTIONS.add(WizardryPotions.containment);
		ALL_POTIONS.add(WizardryPotions.curse_of_enfeeblement);
		ALL_POTIONS.add(WizardryPotions.curse_of_undeath);
		ALL_POTIONS.add(WizardryPotions.ward);
		ALL_POTIONS.add(WizardryPotions.static_aura);
		ALL_POTIONS.add(WizardryPotions.frost_step);
		ALL_POTIONS.add(WizardryPotions.font_of_mana);

		ALL_POWERS.add(WizardryPotions.empowerment);
		ALL_POWERS.add(ASPotions.spell_blast);
		ALL_POWERS.add(ASPotions.spell_range);
		ALL_POWERS.add(ASPotions.spell_duration);
		ALL_POWERS.add(ASPotions.spell_cooldown);
		ALL_POWERS.add(ASPotions.spell_siphon);
		ALL_POWERS.add(ASPotions.mana_regeneration);
	}

	public ItemRelic(String name, EnumRarity rarity) {
		this.maxStackSize = 1;
		this.setCreativeTab(ASTabs.ANCIENTSPELLCRAFT);
		this.rarity = rarity;

		this.addPropertyOverride(new ResourceLocation("activating"), new IItemPropertyGetter() {

			@SideOnly(Side.CLIENT)
			public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
				if (entityIn == null) {
					return 0.0F;
				}
				return !(entityIn.getActiveItemStack() == stack && entityIn.getActiveItemStack().getItem() instanceof ItemRelic) ? 0.0F : entityIn.getItemInUseCount() > 0 ? 1.0F : 0F;
			}
		});

	}

	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		return 120;
	}

	@Override
	public EnumAction getItemUseAction(ItemStack stack) {
		return SpellActions.IMBUE;
		// : SpellActions.POINT_UP;
		//		return !hasRelicType(stack) ? SpellActions.POINT_UP : getRelicType(stack) == RelicType.ENCHANTMENT ? SpellActions.IMBUE : SpellActions.POINT_UP;
	}

	/**
	 * Called once in each side when the equipped item is right clicked <b>immediately</b>, not when the item use duration is finished.
	 * {@link ItemRelic#onItemUseFinish(net.minecraft.item.ItemStack, net.minecraft.world.World, net.minecraft.entity.EntityLivingBase)} gets called when item usage is finished.
	 */
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		ItemStack itemStack = playerIn.getHeldItem(handIn);
		RelicType relicType = getRelicType(itemStack);

		if (relicType == RelicType.INCANTATION || relicType == RelicType.POWER || relicType == RelicType.ENCHANTMENT) {
			playerIn.setActiveHand(handIn);
			return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemStack);
		} else {
			return new ActionResult<ItemStack>(EnumActionResult.FAIL, itemStack);
		}
	}

	/**
	 * Called each tick while using an item.
	 *
	 * @param itemStack  The Item being used
	 * @param player     The Player using the item
	 * @param usingTicks The amount of time in tick the item has been used for continuously
	 */
	@Override
	public void onUsingTick(ItemStack itemStack, EntityLivingBase player, int usingTicks) {

		if (usingTicks % 25 == 0 || usingTicks == 0) {
			RelicType relicType = getRelicType(itemStack);

			if (relicType == RelicType.INCANTATION || relicType == RelicType.ENCHANTMENT || relicType == RelicType.POWER) {
				player.world.playSound(null, player.posX, player.posY, player.posZ, ASSounds.RELIC_USE_LOOP, SoundCategory.NEUTRAL, 1.1F, 1F);
			}
		}

		if (player.world.isRemote && usingTicks % 10 == 0) {
			RelicType relicType = getRelicType(itemStack);

			if (relicType == RelicType.INCANTATION || relicType == RelicType.ENCHANTMENT || relicType == RelicType.POWER) {

				int color = relicType == RelicType.INCANTATION ? getIncantationFromRelic(itemStack).getLiquidColor() : relicType == RelicType.ENCHANTMENT ? 0x5c1461 : 0x10c7c1;

				if (relicType == RelicType.ENCHANTMENT) {
					if (player.world.isRemote) {
						World world = player.world;
						for (int i = 0; i < 10; i++) {
							double dx = (world.rand.nextDouble() * 2 - 1) * 3;
							double dy = (world.rand.nextDouble() * 2 - 1) * 3;
							double dz = (world.rand.nextDouble() * 2 - 1) * 3;
							// These particles use the velocity args differently; they behave more like portal particles
							world.spawnParticle(EnumParticleTypes.ENCHANTMENT_TABLE, player.posX, player.posY + 1.5, player.posZ, dx, dy, dz);
						}
					}
				}
				ParticleBuilder.create(ParticleBuilder.Type.FLASH)
						.entity(player)
						.pos(0, 0.1, 0)
						.face(EnumFacing.UP)
						.clr(color)
						//					.clr(89, 238, 155)
						.collide(false)
						.scale(4.3F)
						.time(30)
						.spawn(player.world);

				ParticleBuilder.create(ParticleBuilder.Type.FLASH)
						.entity(player)
						.pos(0, 0.1, 0)
						.face(EnumFacing.UP)
						.clr(0x303030)
						.collide(false)
						.scale(2.3F)
						.time(40)
						.spawn(player.world);

				ParticleBuilder.create(ParticleBuilder.Type.FLASH)
						.entity(player)
						.vel(0, 0.05, 0)
						.scale(0.7F)
						.time(48 + player.world.rand.nextInt(12))
						.spin(1, (120 - usingTicks) * 0.001)
						.clr(color)
						.spawn(player.world);
			}
		}

	}

	/**
	 * Called when the player finishes using this Item (E.g. finishes eating.). Not called when the player stops using
	 * the Item before the action is complete.
	 */
	@SuppressWarnings("Duplicates")
	@Override
	public ItemStack onItemUseFinish(ItemStack itemStack, World worldIn, EntityLivingBase entityLiving) {
		if (entityLiving instanceof EntityPlayer) {
			((EntityPlayer) entityLiving).getCooldownTracker().setCooldown(this, 60);
			RelicType relicType = getRelicType(itemStack);
			if (relicType == RelicType.INCANTATION) {
				Potion potion = getIncantationFromRelic(itemStack);
				int duration = itemStack.getTagCompound().getInteger("duration");
				entityLiving.addPotionEffect(new PotionEffect(potion, duration, 0));
				worldIn.playSound(null, entityLiving.posX, entityLiving.posY, entityLiving.posZ, ASSounds.RELIC_ACTIVATE_2, SoundCategory.NEUTRAL, 0.9F, 1F);
				return ItemStack.EMPTY;

			} else if (relicType == RelicType.POWER) {
				Potion potion = getPowerFromRelic(itemStack);
				int duration = itemStack.getTagCompound().getInteger("duration");
				entityLiving.addPotionEffect(new PotionEffect(potion, duration, 0));
				worldIn.playSound(null, entityLiving.posX, entityLiving.posY, entityLiving.posZ, ASSounds.RELIC_ACTIVATE_2, SoundCategory.NEUTRAL, 0.9F, 1F);
				return ItemStack.EMPTY;

			} else if (relicType == RelicType.ENCHANTMENT) {
				if (!entityLiving.getHeldItemOffhand().isEmpty()) {
					ItemStack offHandStack = entityLiving.getHeldItemOffhand();

					if (!offHandStack.getItem().isEnchantable(offHandStack)) {
						return itemStack;
					}

					Map<Enchantment, Integer> stoneEnchantments = getStoredEnchantments(itemStack);

					List<EnchantmentData> stoneData = new ArrayList<>();
					List<EnchantmentData> tempStoneData = new ArrayList<>();

					for (Map.Entry<Enchantment, Integer> enchantment : stoneEnchantments.entrySet()) {
						EnchantmentData data = new EnchantmentData(enchantment.getKey(), enchantment.getValue());
						stoneData.add(data);
					}

					boolean enchantedSomething = false;
					boolean isItemEnchanted = offHandStack.isItemEnchanted();

					if (isItemEnchanted) {
						// build list of current enchantments
						Map<Enchantment, Integer> currentEnchantments = EnchantmentHelper.getEnchantments(offHandStack);
						List<EnchantmentData> currentData = new ArrayList<>();

						for (Map.Entry<Enchantment, Integer> enchantment : currentEnchantments.entrySet()) {
							EnchantmentData data = new EnchantmentData(enchantment.getKey(), enchantment.getValue());
							currentData.add(data);
						}

						// remove incompatible enchants from the planned list
						for (EnchantmentData data : currentData) {
							EnchantmentHelper.removeIncompatible(stoneData, data);
						}
					}

					tempStoneData.addAll(0, stoneData);

					for (EnchantmentData data : tempStoneData) {
						stoneData.remove(data);
						EnchantmentHelper.removeIncompatible(stoneData, data);
						stoneData.add(data);
					}

					stoneEnchantments = new HashMap<>();

					for (EnchantmentData data : stoneData) {
						if (offHandStack.getItem().canApplyAtEnchantingTable(offHandStack, data.enchantment)) {
							stoneEnchantments.put(data.enchantment, data.enchantmentLevel);

							offHandStack.addEnchantment(data.enchantment, data.enchantmentLevel);
							enchantedSomething = true;
						}
					}

					if (enchantedSomething) {
						worldIn.playSound(null, entityLiving.posX, entityLiving.posY, entityLiving.posZ, ASSounds.RELIC_ACTIVATE, SoundCategory.NEUTRAL, 0.9F, 1F);

						return ItemStack.EMPTY;
					} else {
						return itemStack;
					}

				}
			}
		}

		return itemStack;
	}

	public static void removeAllEnchantments(ItemStack stack) {
		if (stack.isItemEnchanted()) {
			if (stack.isItemEnchanted()) {
				NBTTagCompound nbt = stack.getTagCompound();
				nbt.removeTag("ench");
				stack.setTagCompound(nbt);
			}
		}
	}

	@Override
	public EnumRarity getRarity(ItemStack stack) {
		return rarity;
	}

	private static Tier getTier(ItemStack stack) {

		switch (stack.getItem().getRarity(stack)) {
			case COMMON:
				return Tier.NOVICE;
			case UNCOMMON:
				return Tier.APPRENTICE;
			case RARE:
				return Tier.ADVANCED;
			case EPIC:
				return Tier.MASTER;
			default:
				return Tier.NOVICE;
		}
	}

	@Override
	public IRarity getForgeRarity(ItemStack stack) {
		return rarity;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, net.minecraft.client.util.ITooltipFlag advanced) {
		boolean researched = isResearched(stack);

		if (advanced.isAdvanced() && !researched) {
//			tooltip.add(TextFormatting.GOLD + "Ancient Relic");
			tooltip.add(Wizardry.proxy.translate("tooltip.ancientspellcraft:ancient_relic_tooltip"));
			tooltip.add(Wizardry.proxy.translate("tooltip.ancientspellcraft:ancient_relic_research"));
		}

		if (researched) {

			NBTTagCompound nbt = stack.getTagCompound();

			if (nbt.hasKey("relicType")) {
				RelicType type = RelicType.fromName(nbt.getString("relicType").toLowerCase());
				tooltip.add(AncientSpellcraft.proxy.translate(type.getUnlocalisedName(), type.colour.setItalic(true)));

				if (type == RelicType.SPELL && nbt.hasKey("spell")) {
					Spell spell = getSpell(stack);
					tooltip.add(" " + spell.getDisplayName());
					tooltip.add(TextFormatting.GOLD + I18n.format("relic_type.spell.required_components"));

					for (ItemStack stack1 : getSpellComponentItems(stack)) {
						tooltip.add(" - " + stack1.getDisplayName());
					}

					tooltip.add(I18n.format("relic_type.spell.instruction"));

				} else if (type == RelicType.INCANTATION && nbt.hasKey("incantation")) {
					String potionName = nbt.getString("incantation");
					String displayName = net.minecraft.client.resources.I18n.format(Potion.getPotionFromResourceLocation(potionName).getName());
					int duration = nbt.getInteger("duration");
					tooltip.add(" " + displayName + " (" + StringUtils.ticksToElapsedTime(duration) + ")");

					tooltip.add(I18n.format("relic_type.incantation.instruction"));

				} else if (type == RelicType.POWER) {

					if (stack.getTagCompound().hasKey("power")) {
						String potionName = nbt.getString("power");
						String displayName = net.minecraft.client.resources.I18n.format(Potion.getPotionFromResourceLocation(potionName).getName());
						int duration = nbt.getInteger("duration");
						tooltip.add(" " + displayName + " (" + StringUtils.ticksToElapsedTime(duration) + ")");

						tooltip.add(I18n.format("relic_type.power.instruction"));
					}

					//					String potionName = nbt.getString("power");
					//					String displayName = net.minecraft.client.resources.I18n.format(Potion.getPotionFromResourceLocation(potionName).getName());
					//					int duration = nbt.getInteger("duration");
					//					tooltip.add(" " + displayName + " (" + StringUtils.ticksToElapsedTime(duration) + ")");
					//
					//					tooltip.add(I18n.format("relic_type.power.instruction"));

				} else if (type == RelicType.ENCHANTMENT) {
					Map<Enchantment, Integer> list = getStoredEnchantments(stack);
					if (list != null || !list.isEmpty()) {
						for (Map.Entry<Enchantment, Integer> entry : getStoredEnchantments(stack).entrySet()) {
							tooltip.add(" " + entry.getKey().getTranslatedName(entry.getValue()));
						}
						tooltip.add(I18n.format("relic_type.enchantment.instruction"));
					}
				}
			}

		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack stack) {
		return rarity == EnumRarity.EPIC;
	}

	public boolean hasRelicType(ItemStack stack) {
		return stack.hasTagCompound() && stack.getTagCompound().hasKey("getRelicType");
	}

	@Override
	public boolean isEnchantable(ItemStack stack) {
		return false;
	}

	private static Map<Enchantment, Integer> getStoredEnchantments(ItemStack stack) {
		Map<Enchantment, Integer> list = new HashMap<>();
		if (isResearched(stack)) {
			NBTTagList taglist = stack.getTagCompound().getTagList("StoredEnchantments", 10);

			for (int i = 0; i < taglist.tagCount(); ++i) {
				NBTTagCompound nbttagcompound = taglist.getCompoundTagAt(i);
				Enchantment enchantment = Enchantment.getEnchantmentByID(nbttagcompound.getShort("id"));
				int level = nbttagcompound.getShort("lvl");
				list.put(enchantment, level);
			}
			return list;
		}
		return null;
	}

	public static void setRandomContentType(ItemStack stack, EntityPlayer player, @Nullable RelicType type) {
		if (stack.getTagCompound() != null && isResearched(stack))
			return;

		if (type != null) {
			setRelicType(stack, player, type);
			switch (type) {
				case SPELL:
					setRandomSpell(player, stack);
					break;
				case INCANTATION:
					setRandomIncantation(stack);
					break;
				case ENCHANTMENT:
					setRandomEnchantment(stack, player);
					break;
				default:
					setRandomPower(stack, player);
			}
			setRandomSpell(player, stack);
		} else {
			float f = player.world.rand.nextFloat();

			if (f <= 0.4f) { // SPELL
				setRelicType(stack, player, RelicType.SPELL);
				setRandomSpell(player, stack);

			} else if (0.4f < f && f <= 0.55f) { // INCANTATION
				setRelicType(stack, player, RelicType.INCANTATION);
				setRandomIncantation(stack);

			} else if (0.55f < f && f <= 0.8f) { // ENCHANTMENT
				setRelicType(stack, player, RelicType.ENCHANTMENT);
				setRandomEnchantment(stack, player);

			} else if (0.8f < f && f <= 1f) { // POWER
				setRelicType(stack, player, RelicType.POWER);
				setRandomPower(stack, player);
			}
		}

		setResearchedTag(stack);
	}

	public static void setResearchedTag(ItemStack stack) {
		if (stack.getTagCompound() != null && stack.getTagCompound().hasKey("researched") && stack.getTagCompound().getBoolean("researched"))
			return;

		NBTTagCompound nbt = getTagCompound(stack);
		nbt.setBoolean("researched", true);
		stack.setTagCompound(nbt);
	}

	public static boolean isResearched(ItemStack stack) {
		if (stack.getTagCompound() != null && stack.getTagCompound().hasKey("researched")) {
			return stack.getTagCompound().getBoolean("researched");
		}
		return false;
	}

	public static void setRelicType(ItemStack stack, EntityPlayer player, RelicType type) {
		if (stack.getTagCompound() != null && stack.getTagCompound().hasKey("researched") && stack.getTagCompound().getBoolean(type.unlocalisedName))
			return;

		NBTTagCompound nbt = getTagCompound(stack);
		nbt.setString("relicType", type.unlocalisedName);
		stack.setTagCompound(nbt);
	}

	private static NBTTagCompound getTagCompound(ItemStack stack) {
		if (stack.hasTagCompound()) {
			return stack.getTagCompound();
		} else {
			return new NBTTagCompound();
		}
	}

	@Nullable
	public static List<ItemStack> getSpellComponentItems(ItemStack stack) {
		if (stack.getItem() instanceof ItemRelic) {
			if ((stack.getTagCompound() != null && stack.getTagCompound().hasKey(SPELL))) {
				Spell spell = Spell.get(stack.getTagCompound().getString(SPELL));
				if (spell != null && SpellComponentList.containsSpell(spell)) {
					SpellComponentList componentList = lookupBySpell(spell);
					return Arrays.asList(componentList.getComponents());
				}
			}
		}
		return null;
	}

	@Nullable
	private static void setRandomSpell(EntityPlayer player, ItemStack stack) {
		if (player != null) {
			WizardData data = WizardData.get(player);
			if (data != null) {
				Tier tier = getTier(stack);
				List<Spell> list = SpellComponentList.getSpellListByTier(tier);
				list.removeIf(spell -> !spell.isEnabled());

				// would only occur if all spells are disabled
				if (list.isEmpty())
					return;

				Spell spell = ASUtils.getRandomListItem(list);

				NBTTagCompound nbt;
				if (stack.hasTagCompound()) {
					nbt = stack.getTagCompound();
				} else {
					nbt = new NBTTagCompound();
				}
				nbt.setString(RelicType.SPELL.unlocalisedName, spell.getUnlocalisedName());
				stack.setTagCompound(nbt);
			}
		}
	}

	private static void setRandomIncantation(ItemStack stack) {
		Potion potion = getRandomPotion();
		Tier tier = getTier(stack);
		int duration = 0;
		switch (tier) {
			case NOVICE:
				duration = 10 * (60 * 20);
				break;
			case APPRENTICE:
				duration = 16 * (60 * 20);
				break;
			case ADVANCED:
				duration = 30 * (60 * 20);
				break;
			case MASTER:
				duration = 60 * (60 * 20);
				break;
		}

		NBTTagCompound nbt = getTagCompound(stack);

		nbt.setString(RelicType.INCANTATION.unlocalisedName, potion.getRegistryName().toString());
		nbt.setInteger("duration", duration);
		stack.setTagCompound(nbt);
	}

	private static void setRandomEnchantment(ItemStack stack, EntityPlayer player) {
		Enchantment enchantment;
		List<Enchantment> list = Lists.newArrayList();

		for (Enchantment enchantment1 : Enchantment.REGISTRY) {
			if (!enchantment1.isCurse() &&
					!enchantment1.getRegistryName().toString().equals("ebwizardry:magic_sword") &&
					!enchantment1.getRegistryName().toString().equals("ebwizardry:flaming_weapon") &&
					!enchantment1.getRegistryName().toString().equals("ebwizardry:freezing_weapon") &&
					!enchantment1.getRegistryName().toString().equals("ebwizardry:shocking_weapon") &&
					!enchantment1.getRegistryName().toString().equals("ebwizardry:magic_bow")
			) {

				list.add(enchantment1);
			}
		}

		int count = getTier(stack).level + 1;
		for (int j = 0; j < count; j++) {
			enchantment = list.get(player.world.rand.nextInt(list.size()));
			if (enchantment != null) {

				// filter out the weird ones
				for (int i = 0; i < 5; i++) {
					if (enchantment.type == null || !(enchantment.type.canEnchantItem(Items.DIAMOND_HELMET) || enchantment.type.canEnchantItem(Items.DIAMOND_CHESTPLATE)
							|| enchantment.type.canEnchantItem(Items.DIAMOND_LEGGINGS) || enchantment.type.canEnchantItem(Items.DIAMOND_BOOTS) || enchantment.type.canEnchantItem(Items.DIAMOND_SWORD) ||
							enchantment.type.canEnchantItem(Items.BOW) || enchantment.type.canEnchantItem(Items.DIAMOND_HOE) || enchantment.type.canEnchantItem(Items.DIAMOND_PICKAXE))) {
						enchantment = list.get(player.world.rand.nextInt(list.size()));
					} else {
						break;
					}
				}

				int i = MathHelper.getInt(player.world.rand, Math.min(enchantment.getMinLevel() + (count / 2), enchantment.getMaxLevel()), enchantment.getMaxLevel());
				ItemEnchantedBook.addEnchantment(stack, new EnchantmentData(enchantment, i));
			}
		}
	}

	private static void setRandomPower(ItemStack stack, EntityPlayer player) {
		Potion potion = getRandomPower();
		Tier tier = getTier(stack);
		int duration = 0;
		switch (tier) {
			case NOVICE:
				duration = 5 * (60 * 20);
				break;
			case APPRENTICE:
				duration = 10 * (60 * 20);
				break;
			case ADVANCED:
				duration = 15 * (60 * 20);
				break;
			case MASTER:
				duration = 20 * (60 * 20);
				break;
		}

		NBTTagCompound nbt = getTagCompound(stack);

		nbt.setString(RelicType.POWER.unlocalisedName, potion.getRegistryName().toString());
		nbt.setInteger("duration", duration);
		stack.setTagCompound(nbt);
	}

	public static Element getRandomElement() {
		Element[] elements = Element.values();
		Random rand = new Random();
		int i = 0;
		while (i == 0) {
			i = rand.nextInt(elements.length);
		}
		return elements[i];
	}

	private static Potion getRandomPotion() {
		Random rand = new Random();
		return ALL_POTIONS.get(rand.nextInt(ALL_POTIONS.size()));
	}

	private static Potion getRandomPower() {
		Random rand = new Random();
		return ALL_POWERS.get(rand.nextInt(ALL_POWERS.size()));
	}

	@Nullable
	public static RelicType getRelicType(ItemStack stack) {
		if (stack.getItem() instanceof ItemRelic) {
			if ((stack.getTagCompound() != null && stack.getTagCompound().hasKey("relicType"))) {
				return RelicType.fromName(stack.getTagCompound().getString("relicType").toLowerCase());
			}
		}
		return null;
	}

	@Nullable
	public static Spell getSpell(ItemStack stack) {
		if (stack.getItem() instanceof ItemRelic) {
			if ((stack.getTagCompound() != null && stack.getTagCompound().hasKey(SPELL))) {
				return Spell.get(stack.getTagCompound().getString(SPELL));
			}
		}
		return null;
	}

	public static Potion getIncantationFromRelic(ItemStack stack) {
		String potionName = stack.getTagCompound().getString("incantation");
		return Potion.getPotionFromResourceLocation(potionName);
	}

	public static Potion getPowerFromRelic(ItemStack stack) {
		String potionName = stack.getTagCompound().getString("power");

		Potion potion = Potion.getPotionFromResourceLocation(potionName);

		if (potion == null) {
			AncientSpellcraft.logger.error("Couldn't find potion " + potionName + " in the registry, defaulting to minecraft:swiftness");
			potion = MobEffects.SPEED;
		}

		return potion;
	}

	public static int getColorFromPotion(ItemStack stack, int layer) {
		if (layer == 0) {
			if (getRelicType(stack) == RelicType.INCANTATION) {
				return getIncantationFromRelic(stack).getLiquidColor();
			}
		}
		if (layer == 1) {
			RelicType type = getRelicType(stack);
			if (type == RelicType.INCANTATION) {
				return getIncantationFromRelic(stack).getLiquidColor();
			} else if (type == RelicType.ENCHANTMENT) {
				return 0x5c1461;
			} else if (type == RelicType.POWER) {
				return 0x10c7c1;
			}
		}
		return 0;
	}

	public enum RelicType implements IStringSerializable {
		SPELL(new Style().setColor(TextFormatting.GOLD), ItemRelic.SPELL),
		INCANTATION(new Style().setColor(TextFormatting.BLUE), ItemRelic.INCANTATION),
		ENCHANTMENT(new Style().setColor(TextFormatting.DARK_PURPLE), ItemRelic.ENCHANTMENT),
		POWER(new Style().setColor(TextFormatting.DARK_AQUA), ItemRelic.POWER);

		private final Style colour;
		private final String unlocalisedName;

		RelicType(Style colour, String name) {
			this.colour = colour;
			this.unlocalisedName = name;
		}

		public static RelicType fromName(String name) {
			for (RelicType relicType : values()) {
				if (relicType.unlocalisedName.equals(name))
					return relicType;
			}
			throw new IllegalArgumentException("No such relicType with unlocalised name: " + name);
		}

		/**
		 * Returns the translated display name of this relicType, without formatting.
		 */
		public String getDisplayName() {
			return AncientSpellcraft.proxy.translate("relic_type." + getName());
		}

		/**
		 * Returns this relicType's unlocalised name. Also used as the serialised string in block properties.
		 */
		@Override
		public String getName() {
			return unlocalisedName;
		}

		public String getUnlocalisedName() {
			return "relic_type." + unlocalisedName;
		}

		/**
		 * Returns the {@link Style} object representing the colour of this relicType.
		 */
		public Style getColour() {
			return colour;
		}
	}

}


