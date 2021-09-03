package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.entity.EntityWisp;
import com.windanesz.ancientspellcraft.entity.construct.EntitySpellTicker;
import com.windanesz.ancientspellcraft.item.ItemEnchantedNameTag;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftItems;
import com.windanesz.ancientspellcraft.util.ASUtils;
import com.windanesz.ancientspellcraft.util.BiomeLocator;
import electroblob.wizardry.data.WizardData;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.spell.SpellConstruct;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import javax.annotation.Nullable;

public class WillOWisp extends SpellConstruct<EntitySpellTicker> implements ISpellTickerConstruct {

	public static String BIOME_POS_TAG = "biomePos";
	public static String BIOME_TAG = "biome";

	public WillOWisp(String modID, String name) {
		super(modID, name, EnumAction.BLOCK, EntitySpellTicker::new, false);
		addProperties(RANGE);
		soundValues(1.0f, 1.2f, 0.3f);
	}

	@Override
	public boolean cast(World world, EntityPlayer caster, EnumHand hand, int ticksInUse, SpellModifiers modifiers) {
		WizardData data = WizardData.get(caster);

		ItemStack offhand = caster.getHeldItemOffhand();

		if (caster.getEntityWorld().provider.getDimension() == 0) {
			if (!offhand.isEmpty() && offhand.getItem() instanceof ItemEnchantedNameTag) {
				if (!offhand.hasTagCompound()) {
					if (!world.isRemote) {
						caster.sendStatusMessage(new TextComponentTranslation("spell.ancientspellcraft:will_o_wisp.tag_has_no_name"), false);
					}
					return false;
				}
				String nameTag;
				try {
					nameTag = caster.getHeldItemOffhand().getTagCompound().getCompoundTag("display").getString("Name");
				}
				catch (Exception e) {
					if (!world.isRemote) {
						caster.sendStatusMessage(new TextComponentTranslation("spell.ancientspellcraft:will_o_wisp.tag_has_no_name"), false);
					}
					return false;
				}

				if (ASUtils.isBiomeNameRegistered(nameTag)) { // biome id found

					boolean res = super.cast(world, caster, hand, ticksInUse, modifiers);
					caster.setHeldItem(EnumHand.OFF_HAND, ItemStack.EMPTY);
					return res;

				} else {
					if (!world.isRemote) {
						caster.sendStatusMessage(new TextComponentTranslation("spell.ancientspellcraft:will_o_wisp.invalid_biome_name_in_tag"), false);
						caster.sendStatusMessage(new TextComponentTranslation("spell.ancientspellcraft:will_o_wisp.try_listbiomes"), false);
					}

					return false;
				}

			} else {
				if (!world.isRemote) {
					caster.sendStatusMessage(new TextComponentTranslation("spell.ancientspellcraft:will_o_wisp.no_name_tag"), false);
				}
			}
		} else {
			if (!world.isRemote) {
				caster.sendStatusMessage(new TextComponentTranslation("spell.ancientspellcraft:will_o_wisp.not_in_overworld"), false);
			}
		}
		return false;
	}

	@Override
	protected void addConstructExtras(EntitySpellTicker construct, EnumFacing side, @Nullable EntityLivingBase caster, SpellModifiers modifiers) {
		NBTTagCompound extraData = new NBTTagCompound();

		// range update base values are 1.25 1.5 1.75
		// ranges: 800,	1562, 2700, 4287
		double maxRange = getProperty(RANGE).floatValue() * Math.pow(modifiers.get(WizardryItems.range_upgrade), 3);

		//noinspection ConstantConditions
		String biome = caster.getHeldItemOffhand().getTagCompound().getCompoundTag("display").getString("Name");
		NBTTagCompound nameTagCompund = caster.getHeldItemOffhand().getTagCompound();
		extraData.setDouble("maxRange", maxRange);
		extraData.setString("biome", biome);
		extraData.setTag("nametagCompound", nameTagCompund);
		construct.setExtraData(extraData);
		construct.setSpell(this);
		construct.lifetime = 200;
		if (caster != null && !caster.world.isRemote) {
			caster.world.spawnEntity(construct);
		}
	}

	/**
	 * Called when the equipped item is right clicked.
	 */
	public void createWisp(World world, EntityPlayer player, BlockPos targetPos, NBTTagCompound nameTagStackTag) {

		if (!world.isRemote) {
			if (targetPos != null) {
				EntityWisp entitywisp = new EntityWisp(world, player.posX, player.posY + (double) (player.height / 2.0F), player.posZ);
				entitywisp.moveTowards(targetPos, player);
				entitywisp.setEnchantedNameTag(nameTagStackTag, 0);
				world.spawnEntity(entitywisp);
			}
		}

	}

	public boolean hasCustomName(ItemStack nameTag) {
		NBTTagCompound tag = nameTag.getTagCompound();
		return !tag.getString("name").isEmpty();
	}

	@Override
	public void onUpdate(World world, EntitySpellTicker entitySpellTicker) {
		if (entitySpellTicker.ticksExisted > 10 && !entitySpellTicker.getExtraData().hasKey(BIOME_POS_TAG)) {
			if (!world.isRemote && entitySpellTicker.getCaster() instanceof EntityPlayer) {
				((EntityPlayer) entitySpellTicker.getCaster()).sendStatusMessage(new TextComponentTranslation("spell.ancientspellcraft:will_o_wisp.searching"), false);
			}

			NBTTagCompound compound = entitySpellTicker.getExtraData();
			if (entitySpellTicker.getExtraData().hasKey("biome")) {

				if (!compound.hasKey("searching")) {

					compound.setBoolean("searching", true);
					entitySpellTicker.setExtraData(compound);

					String biome = entitySpellTicker.getExtraData().getString("biome");
					ResourceLocation biomeResourceLocation = ASUtils.getBiomeRegistryNameFromName(biome);
					BlockPos biomePos = BiomeLocator.spiralOutwardsLookingForBiome(world, ForgeRegistries.BIOMES.getValue(biomeResourceLocation), entitySpellTicker.posX, entitySpellTicker.posZ); // find biome pos

					if (biomePos != null && biomePos != BlockPos.ORIGIN) {
						compound.setTag(BIOME_POS_TAG, NBTUtil.createPosTag(biomePos));

						entitySpellTicker.setExtraData(compound);

						// biome distance from the casting position
						double distFromTicker = Math.sqrt(biomePos.distanceSq(entitySpellTicker.posX, entitySpellTicker.posY, entitySpellTicker.posZ));

						if (distFromTicker > entitySpellTicker.getExtraData().getDouble("maxRange")) {
							// biome is too far away
							if (!world.isRemote && entitySpellTicker.getCaster() != null) {
								((EntityPlayer) entitySpellTicker.getCaster()).sendStatusMessage(new TextComponentTranslation("spell.ancientspellcraft:will_o_wisp.target_too_far"), false);
							}
							dropNameTag(entitySpellTicker);
							entitySpellTicker.setDead();
							return;
						}
					} else {
						// no target biome...
						entitySpellTicker.setDead();
						dropNameTag(entitySpellTicker);
						return;
					}

				} else {
					// no target biome...
					entitySpellTicker.setDead();
					dropNameTag(entitySpellTicker);
					return;
				}
			}
		}

		if (!world.isRemote && entitySpellTicker.ticksExisted > 80 && entitySpellTicker.getExtraData().hasKey(BIOME_POS_TAG)) {
			BlockPos pos = NBTUtil.getPosFromTag(entitySpellTicker.getExtraData().getCompoundTag(BIOME_POS_TAG));
			playSound(world, entitySpellTicker.posX, entitySpellTicker.posY, entitySpellTicker.posZ, 0, 0, entitySpellTicker.getModifiers());
			createWisp(world, (EntityPlayer) entitySpellTicker.getCaster(), pos, entitySpellTicker.getExtraData().getCompoundTag("nametagCompound"));
			String biome = entitySpellTicker.getExtraData().getString("biome");

			if (entitySpellTicker.getCaster() != null) {
				((EntityPlayer) entitySpellTicker.getCaster()).sendStatusMessage(new TextComponentTranslation("spell.ancientspellcraft:will_o_wisp.sent_to_biome", biome), false);
			}

			entitySpellTicker.setDead();
		}
	}

	private static void dropNameTag(EntitySpellTicker entitySpellTicker) {
		if (!entitySpellTicker.world.isRemote) {
			if (entitySpellTicker.getExtraData().hasKey("nametagCompound")) {
				ItemStack stack = new ItemStack(AncientSpellcraftItems.enchanted_name_tag, 1);
				stack.setTagCompound(entitySpellTicker.getExtraData().getCompoundTag("nametagCompound"));

				entitySpellTicker.world.spawnEntity(new EntityItem(entitySpellTicker.world, entitySpellTicker.posX, entitySpellTicker.posY, entitySpellTicker.posZ, stack));
			}
		}
	}

	@Override
	public IBlockState getBlock(World world, EntitySpellTicker entityMushroomForest) {
		return null;
	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == AncientSpellcraftItems.ancient_spellcraft_spell_book || item == AncientSpellcraftItems.ancient_spellcraft_scroll;
	}
}
