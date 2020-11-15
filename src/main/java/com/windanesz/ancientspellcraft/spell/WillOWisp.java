package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.entity.EntityWisp;
import com.windanesz.ancientspellcraft.item.ItemEnchantedNameTag;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftItems;
import com.windanesz.ancientspellcraft.util.ASUtils;
import com.windanesz.ancientspellcraft.util.BiomeLocator;
import electroblob.wizardry.data.WizardData;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.Random;

public class WillOWisp extends Spell {

	public WillOWisp(String modID, String name) {
		super(modID, name, EnumAction.BLOCK, false);
		addProperties(RANGE);
		soundValues(1.0f, 1.2f, 0.3f);
	}

	@Override
	public boolean cast(World world, EntityPlayer caster, EnumHand hand, int ticksInUse, SpellModifiers modifiers) {
		WizardData data = WizardData.get(caster);
		// range update base values are 1.25 1.5 1.75
		// ranges: 800,	1562, 2700, 4287
		double maxRange = getProperty(RANGE).floatValue() * Math.pow(modifiers.get(WizardryItems.range_upgrade), 3);

		ItemStack offhand = caster.getHeldItemOffhand();

		if (caster.getEntityWorld().provider.getDimension() == 0) {
			if (!offhand.isEmpty() && offhand.getItem() instanceof ItemEnchantedNameTag) {
				if (!offhand.hasTagCompound()) {
					if (!world.isRemote)
						caster.sendStatusMessage(new TextComponentTranslation("spell.ancientspellcraft:will_o_wisp.tag_has_no_name"), true);
					return false;
				}
				String nameTag;
				try {
					nameTag = caster.getHeldItemOffhand().getTagCompound().getCompoundTag("display").getString("Name");
				}
				catch (Exception e) {
					if (!world.isRemote)
						caster.sendStatusMessage(new TextComponentTranslation("spell.ancientspellcraft:will_o_wisp.tag_has_no_name"), true);
					return false;
				}
				if (offhand.getItem().getMaxDamage(offhand) - offhand.getItem().getDamage(offhand) < 50) {
					if (!world.isRemote) {
						caster.sendStatusMessage(new TextComponentTranslation("The Enchanted Name Tag doesn't have enough charge"), true);
					}
					return false;
				}
				if (ASUtils.isBiomeNameRegistered(nameTag)) { // biome id found
					// get biome res name
					if (!world.isRemote)
						caster.sendStatusMessage(new TextComponentTranslation("Searching for biome ..."), true);
					ResourceLocation biomeResourceLocation = ASUtils.getBiomeRegistryNameFromName(nameTag);
					// get biome pos
					BlockPos biomePos = BiomeLocator.spiralOutwardsLookingForBiome(world, ForgeRegistries.BIOMES.getValue(biomeResourceLocation), caster.posX, caster.posZ); // find biome pos
					// calculate distance from current pos to biome
					if (!world.isRemote) {

						double distFromCaster = Math.sqrt(biomePos.distanceSq(caster.posX, caster.posY, caster.posZ));
						if (distFromCaster > maxRange) {
							caster.sendStatusMessage(new TextComponentTranslation("Biome is too far away, wand range upgrades can increase the search radius"), true);
							return false;
						}
					}

					// consume mana from the name tag
					((ItemEnchantedNameTag) caster.getHeldItemOffhand().getItem()).consumeMana(caster.getHeldItemOffhand(), 50, caster);

					// create wisp with data
					createWisp(world, caster, hand, biomePos, offhand.getTagCompound(), offhand.getItemDamage()); // send Wisp
					playSound(world, caster.posX, caster.posY, caster.posZ, 0, 0, modifiers);
					if (!world.isRemote)
						caster.sendStatusMessage(new TextComponentTranslation("spell.ancientspellcraft:will_o_wisp.sent_to_biome", nameTag), true);
					caster.setHeldItem(EnumHand.OFF_HAND, ItemStack.EMPTY);
					return true;
				} else {
					if (!world.isRemote && data.hasSpellBeenDiscovered(this))
						caster.sendStatusMessage(new TextComponentTranslation("spell.ancientspellcraft:will_o_wisp.invalid_biome_name_in_tag"), true);
					caster.sendMessage(new TextComponentTranslation("spell.ancientspellcraft:will_o_wisp.try_listbiomes"));

					return false;
				}

			}
			if (!world.isRemote && data.hasSpellBeenDiscovered(this))
				caster.sendStatusMessage(new TextComponentTranslation("spell.ancientspellcraft:will_o_wisp.no_name_tag"), true);
		}
		return false;
	}

	/**
	 * Called when the equipped item is right clicked.
	 */
	public void createWisp(World world, EntityPlayer player, EnumHand hand, BlockPos targetPos, NBTTagCompound nameTagStackTag, int nameTagDamage) {
		Random rand = new Random();
		if (!world.isRemote) {
			if (targetPos != null) {
				EntityWisp entitywisp = new EntityWisp(world, player.posX, player.posY + (double) (player.height / 2.0F), player.posZ);
				entitywisp.moveTowards(targetPos, player);
				entitywisp.setEnchantedNameTag(nameTagStackTag, nameTagDamage);
				world.spawnEntity(entitywisp);
				//				world.playSound((EntityPlayer) null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ENDEREYE_LAUNCH, SoundCategory.NEUTRAL, 0.5F, 0.4F / (rand.nextFloat() * 0.4F + 0.8F));
			}
		}

	}


	public boolean hasCustomName(ItemStack nameTag) {
		NBTTagCompound tag = nameTag.getTagCompound();
		if (tag.getString("name").isEmpty()) {
			return false;
		} else
			return true;
	}

	public boolean hasEnoughMana(ItemStack nameTag) {
		return (nameTag.getMaxDamage() - nameTag.getItemDamage() >= 100);
	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == AncientSpellcraftItems.ancient_spellcraft_spell_book || item == AncientSpellcraftItems.ancient_spellcraft_scroll;
	}
}
