package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.registry.ASItems;
import com.windanesz.ancientspellcraft.util.ASUtils;
import electroblob.wizardry.item.ItemWizardArmour;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketSetExperience;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PreserveExperience extends Spell implements IClassSpell {

	public static final String XP_COST_PER_BOTTLE = "xp_cost_per_bottle";

	public PreserveExperience() {
		super(AncientSpellcraft.MODID, "preserve_experience", SpellActions.IMBUE, true);
		addProperties(XP_COST_PER_BOTTLE);
	}

	@Override
	public boolean cast(World world, EntityPlayer caster, EnumHand hand, int ticksInUse, SpellModifiers modifiers) {
		ItemStack offhand = caster.getHeldItemOffhand();

		if (offhand.isEmpty() || offhand.getItem() != Items.GLASS_BOTTLE) {
			if (ticksInUse == 1 && !world.isRemote) {
				ASUtils.sendMessage(caster, "You must hold glass bottles in your offhand", true);
			}
			return false;
		}

		if (ticksInUse % 40 == 0) {
			this.playSound(world, caster, ticksInUse, -1, modifiers);
		}

		if (world.isRemote) {
			if (ticksInUse % 2 == 0) {
				ParticleBuilder.create(ParticleBuilder.Type.SPARKLE, world.rand,
						caster.posX + (world.rand.nextDouble() - 0.5) * 0.8,
						caster.posY + 0.5 + world.rand.nextDouble() * 0.6,
						caster.posZ + (world.rand.nextDouble() - 0.5) * 0.8,
						0.02, true).clr(0.3f, 0.85f, 0.4f).spawn(world);
			}
			return true;
		}

		// Converting 64 bottles in ~20 seconds (400 ticks) means 1 bottle every 6 ticks (384 ticks total)
		if (ticksInUse % 6 == 0) {
			int cost = getProperty(XP_COST_PER_BOTTLE).intValue();
			int currentXP = getTotalExperience(caster);

			if (!caster.isCreative() && currentXP < cost) {
				if (ticksInUse == 6) {
					ASUtils.sendMessage(caster, "You do not have enough experience", true);
				}
				return false;
			}

			if (!caster.isCreative()) {
				setTotalExperience(caster, currentXP - cost);
				offhand.shrink(1);
			} else if (!offhand.isEmpty()) {
				offhand.shrink(1);
			}

			ItemStack expBottle = new ItemStack(Items.EXPERIENCE_BOTTLE);
			ASUtils.giveStackToPlayer(caster, expBottle);

			world.playSound(null, caster.posX, caster.posY, caster.posZ, SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP,
					SoundCategory.PLAYERS, 0.5f, 1.0f + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2f);
		}

		return true;
	}

	@Override
	public boolean cast(World world, EntityLiving caster, EnumHand hand, int ticksInUse, EntityLivingBase target, SpellModifiers modifiers) {
		return super.cast(world, caster, hand, ticksInUse, target, modifiers);
	}

	public static int getTotalExperience(EntityPlayer player) {
		int total = 0;
		for (int i = 0; i < player.experienceLevel; i++) {
			total += getXpBarCap(i);
		}
		total += Math.round(player.experience * player.xpBarCap());
		return total;
	}

	public static int getXpBarCap(int level) {
		if (level >= 30) {
			return 112 + (level - 30) * 9;
		} else if (level >= 15) {
			return 37 + (level - 15) * 5;
		} else {
			return 7 + level * 2;
		}
	}

	public static void setTotalExperience(EntityPlayer player, int xp) {
		player.experienceTotal = xp;
		player.experienceLevel = 0;
		player.experience = 0.0f;
		while (true) {
			int cap = player.xpBarCap();
			if (xp < cap) {
				player.experience = (float) xp / (float) cap;
				break;
			}
			xp -= cap;
			player.experienceLevel++;
		}
		if (player instanceof EntityPlayerMP) {
			((EntityPlayerMP) player).connection.sendPacket(new SPacketSetExperience(player.experience, player.experienceTotal, player.experienceLevel));
		}
	}

	@Override
	public ItemWizardArmour.ArmourClass getArmourClass() {
		return ItemWizardArmour.ArmourClass.SAGE;
	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == ASItems.mystic_spell_book || item == ASItems.ancient_spellcraft_scroll;
	}

	@SideOnly(Side.CLIENT)
	public String getDisplayNameWithFormatting() {
		return TextFormatting.GOLD + net.minecraft.client.resources.I18n.format(getTranslationKey());
	}
}
