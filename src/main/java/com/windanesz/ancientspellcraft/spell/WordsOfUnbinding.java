package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftItems;
import electroblob.wizardry.item.ItemArtefact;
import electroblob.wizardry.item.ItemWand;
import electroblob.wizardry.item.ItemWandUpgrade;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.NBTExtras;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.SpellModifiers;
import electroblob.wizardry.util.WandHelper;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class WordsOfUnbinding extends Spell {

	public WordsOfUnbinding() {

		super(AncientSpellcraft.MODID, "words_of_unbinding", SpellActions.IMBUE, false);
	}

	@Override
	public boolean cast(World world, EntityPlayer caster, EnumHand hand, int ticksInUse, SpellModifiers modifiers) {
		if (ItemArtefact.isArtefactActive(caster, AncientSpellcraftItems.ring_disenchanter)) {
			if (caster.getHeldItemOffhand().isItemEnchanted()) {
				if (!world.isRemote) {

					NBTTagCompound compound = caster.getHeldItemOffhand().getTagCompound();
					if (compound != null && compound.hasKey("ench")) {
						compound.removeTag("ench");
						this.playSound(world, caster, ticksInUse, -1, modifiers);

						caster.getHeldItemOffhand().setTagCompound(compound);
						return true;
					}
				}
				if (world.isRemote)
					spawnParticles(world, caster);
				return true;
			} else {
				if (!world.isRemote) {
					caster.sendStatusMessage(new TextComponentTranslation("You should hold an enchanted item in your offhand to disenchant it"), false);
				}
			}
			return false;
		}

		if (!(caster.getHeldItemMainhand().getItem() instanceof ItemWand || caster.getHeldItemOffhand().getItem() instanceof ItemWand) ||
				(!(caster.getHeldItemMainhand().getItem() instanceof ItemWand) && caster.getHeldItemOffhand().getItem() instanceof ItemWand)) {
			if (!world.isRemote)
				caster.sendStatusMessage(new TextComponentTranslation("You must cast this spell with a Wand."), false);
			return false;
		}

		if (caster.isHandActive()) {
			ItemStack stack = caster.getHeldItem(caster.getActiveHand());
			if (stack.getItem() instanceof ItemWand) {
				if (WandHelper.getTotalUpgrades(stack) > 0) {
					if (!world.isRemote) {

						if (!caster.getHeldItemOffhand().isEmpty() && caster.getHeldItemOffhand().getItem() instanceof ItemWandUpgrade) {
							boolean action = removeUpgrade(caster, stack, caster.getHeldItemOffhand().getItem());
							if (action) {
								this.playSound(world, caster, ticksInUse, -1, modifiers);
							}
							return action;
						} else {
							boolean action = removeUpgrade(caster, stack, null);
							if (action) {
								this.playSound(world, caster, ticksInUse, -1, modifiers);
							}
							return action;
						}
					}

				}

			} else {
				if (!world.isRemote)
					caster.sendStatusMessage(new TextComponentTranslation("You must cast this spell with a Wand which has at least one upgrade."), false);
				return false;

			}
			System.out.println(caster.getActiveHand());
			;

		} else {
			if (!world.isRemote)
				caster.sendStatusMessage(new TextComponentTranslation("You must cast this spell with a Wand."), false);
			return false;
		}

		if (world.isRemote)
			spawnParticles(world, caster);

		return true;
	}

	public static boolean removeUpgrade(EntityPlayer player, ItemStack wand, @Nullable Item upgrade) {
		boolean returnItem = player.world.rand.nextDouble() < 0.2 && ItemArtefact.isArtefactActive(player, AncientSpellcraftItems.ring_unbinding);

		if (wand.getTagCompound() == null)
			wand.setTagCompound((new NBTTagCompound()));

		if (!wand.getTagCompound().hasKey(WandHelper.UPGRADES_KEY)) {
			return false;
		}

		NBTTagCompound upgrades = wand.getTagCompound().getCompoundTag(WandHelper.UPGRADES_KEY);

		if (upgrade != null) {

			String upgradeString = upgrade.getRegistryName().getPath().replace("_upgrade", "");

			if (upgrades.hasKey(upgradeString)) {
				int counter = upgrades.getInteger(upgradeString);
				counter--;
				if (counter >= 1) {
					upgrades.setInteger(upgradeString, counter);
				} else {
					upgrades.removeTag(upgradeString);
				}

				NBTExtras.storeTagSafely(wand.getTagCompound(), WandHelper.UPGRADES_KEY, upgrades);
				if (returnItem) {
					dropUpgrade(player, upgradeString);
				}

				return true;

			} else {
				removeRandomUpgrade(player, wand, returnItem);

			}
		} else {
			removeRandomUpgrade(player, wand, returnItem);
		}

		return true;
	}

	private static void dropUpgrade(EntityPlayer player, String upgrade) {
		List<ResourceLocation> upgradeItems = ForgeRegistries.ITEMS.getKeys().stream().filter(i -> i.getPath().equals(upgrade + "_upgrade")).collect(Collectors.toList());
		if (!upgradeItems.isEmpty()) {
			Item item = ForgeRegistries.ITEMS.getValue(upgradeItems.get(0));
			//noinspection ConstantConditions
			if (!player.addItemStackToInventory(new ItemStack(item))) {
				player.dropItem(new ItemStack(item), false);
			}
		}
	}

	private static void removeRandomUpgrade(EntityPlayer player, ItemStack wand, boolean returnItem) {

		NBTTagCompound upgrades = wand.getTagCompound().getCompoundTag(WandHelper.UPGRADES_KEY);

		if (!upgrades.isEmpty()) {
			List<String> appliedUpgrades = new ArrayList<>(upgrades.getKeySet());
			Random rand = new Random();
			String upgradeToDecrease = appliedUpgrades.get(rand.nextInt(appliedUpgrades.size()));

			if (upgrades.hasKey(upgradeToDecrease)) {
				int counter = upgrades.getInteger(upgradeToDecrease);
				counter--;
				if (counter >= 1) {
					upgrades.setInteger(upgradeToDecrease, counter);
				} else {
					upgrades.removeTag(upgradeToDecrease);
				}

				NBTExtras.storeTagSafely(wand.getTagCompound(), WandHelper.UPGRADES_KEY, upgrades);
				if (returnItem) {
					dropUpgrade(player, upgradeToDecrease);
				}
			}
		}
	}

	/**
	 * Spawns buff particles around the caster. Override to add a custom particle effect. Only called client-side.
	 */
	protected static void spawnParticles(World world, EntityLivingBase caster) {

		for (int i = 0; i < 10; i++) {
			double x = caster.posX + world.rand.nextDouble() * 2 - 1;
			double y = caster.posY + caster.getEyeHeight() - 0.5 + world.rand.nextDouble();
			double z = caster.posZ + world.rand.nextDouble() * 2 - 1;
			ParticleBuilder.create(ParticleBuilder.Type.SPARKLE).pos(x, y, z).vel(0, 0.1, 0).clr(218, 186, 219).spawn(world);
		}

		ParticleBuilder.create(ParticleBuilder.Type.BUFF).entity(caster).clr(218, 186, 219).spawn(world);
	}

	@Override
	public boolean canBeCastBy(EntityLiving npc, boolean override) {
		return false;
	}

	@Override
	public boolean canBeCastBy(TileEntityDispenser dispenser) {
		return false;
	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == AncientSpellcraftItems.ancient_spell_book || item == AncientSpellcraftItems.ancient_spellcraft_scroll;
	}
}
