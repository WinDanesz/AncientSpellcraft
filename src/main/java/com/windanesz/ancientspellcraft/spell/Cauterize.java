package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.registry.ASItems;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.MagicDamage;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

public class Cauterize extends Spell {

	public static String BURN_DURATION = "burn_duration";
	public static String REGENERATION_DURATION = "regeneration_duration";

	public Cauterize() {
		super(AncientSpellcraft.MODID, "cauterize", SpellActions.IMBUE, false);
		addProperties(HEALTH, DAMAGE, REGENERATION_DURATION, BURN_DURATION);
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
	public boolean cast(World world, EntityPlayer caster, EnumHand hand, int ticksInUse, SpellModifiers modifiers) {

		world.playSound(null, caster.getPosition(), SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0F, world.rand.nextFloat() * 0.4F + 0.8F);
		//world.playSound(null, caster.getPosition(), SoundEvents.BLOCK_FIRE_AMBIENT, SoundCategory.PLAYERS, 1.0F, world.rand.nextFloat() * 0.4F + 0.8F);
		int burn = getProperty(BURN_DURATION).intValue() / 20;

		boolean fireImmune = MagicDamage.isEntityImmune(MagicDamage.DamageType.FIRE, caster) || caster.isPotionActive(MobEffects.FIRE_RESISTANCE);
		float efficiencyRatio = 1.0f;

		for (ItemStack slot : caster.getArmorInventoryList()) {
			int level = EnchantmentHelper.getEnchantmentLevel(Enchantments.FIRE_PROTECTION, slot);
			efficiencyRatio = (float) (Math.max(0.1, (efficiencyRatio - (level * 0.1))));
		}
		// burn the caster
		caster.setFire(burn);

		if (!fireImmune) {
			if (!world.isRemote) {

				// damage the caster
				EntityUtils.attackEntityWithoutKnockback(caster,
						MagicDamage.causeDirectMagicDamage(caster, MagicDamage.DamageType.FIRE),
						getProperty(DAMAGE).floatValue() * modifiers.get(SpellModifiers.POTENCY));
				// apply regeneration
				caster.addPotionEffect(new PotionEffect(MobEffects.REGENERATION,(int)(efficiencyRatio * getProperty(REGENERATION_DURATION).intValue()), 0));
				// heal the caster
				caster.heal(efficiencyRatio * getProperty(HEALTH).intValue() * modifiers.get(SpellModifiers.POTENCY));
			} else {
				caster.heal((float) (0.3 * (getProperty(HEALTH).intValue() * modifiers.get(SpellModifiers.POTENCY))));
			}
		}

		return true;
	}

	@Override
	public boolean cast(World world, EntityLiving caster, EnumHand hand, int ticksInUse, EntityLivingBase target, SpellModifiers modifiers) {
		return false;
	}

	@Override
	public boolean cast(World world, double x, double y, double z, EnumFacing direction, int ticksInUse, int duration, SpellModifiers modifiers) {
		return false;
	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == ASItems.ancient_spellcraft_spell_book || item == ASItems.ancient_spellcraft_scroll;
	}
}