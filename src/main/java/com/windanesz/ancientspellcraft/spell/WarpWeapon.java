package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.registry.ASEnchantments;
import electroblob.wizardry.constants.Constants;
import electroblob.wizardry.data.WizardData;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.registry.WizardryEnchantments;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.registry.WizardryPotions;
import electroblob.wizardry.spell.ImbueWeapon;
import electroblob.wizardry.spell.SpellRay;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.ParticleBuilder.Type;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * TODO: this can only be completed with electroblob.wizardry.enchantment.Imbuement#removeImbuements(net.minecraft.item.ItemStack)
 */
public class WarpWeapon extends SpellRay {

	public WarpWeapon() {
		super(AncientSpellcraft.MODID, "warp_weapon", SpellActions.IMBUE, false);
		addProperties(EFFECT_DURATION);
	}

	@Override
	protected boolean onEntityHit(World world, Entity target, Vec3d hit,
			@Nullable EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {

		if (EntityUtils.isLiving(target)) {
			if (!world.isRemote) {
				if (target instanceof EntityLivingBase) {
					EntityLivingBase livingTarget = (EntityLivingBase) target;

					if (livingTarget.isPotionActive(WizardryPotions.ward)) {
						if (caster instanceof EntityPlayer) {
							((EntityPlayer) caster).sendStatusMessage(new TextComponentTranslation("spell.resist", target.getName(),
									this.getNameForTranslationFormatted()), true);
							livingTarget.removePotionEffect(WizardryPotions.ward);
							return true;
						}
					}

					ItemStack stack;
					if (!livingTarget.getHeldItemMainhand().isEmpty() && (ImbueWeapon.isSword(livingTarget.getHeldItemMainhand()) || ImbueWeapon.isBow(livingTarget.getHeldItemMainhand()))) {
						stack = livingTarget.getHeldItemMainhand();
					} else if (!livingTarget.getHeldItemOffhand().isEmpty() && (ImbueWeapon.isSword(livingTarget.getHeldItemOffhand()) || ImbueWeapon.isBow(livingTarget.getHeldItemOffhand()))) {
						stack = livingTarget.getHeldItemOffhand();
					} else {
						return false;
					}

					EnumDegradedItem type = ImbueWeapon.isSword(stack) ? EnumDegradedItem.SWORD : EnumDegradedItem.BOW;

					// degrading the weapon removes its imbuement enchantment
					//if (EnchantmentHelper.getEnchantments(stack).containsKey(type.beneficialEnchant)) {
					//EnchantmentHelper.getEnchantments(stack).remove(type.beneficialEnchant);
					//return true;
					//	} else {
					// The enchantment level as determined by the damage multiplier. The + 0.5f is so that
					// weird float processing doesn't incorrectly round it down.
					if (livingTarget instanceof EntityPlayer) {
						int duration = (int)(600 * modifiers.get(WizardryItems.duration_upgrade));
						System.out.println(duration);
						WizardData.get((EntityPlayer) livingTarget).setImbuementDuration(ASEnchantments.degrade_sword, duration);
//						WizardData.get((EntityPlayer) livingTarget).setImbuementDuration(WizardryEnchantments.magic_sword,
//								(int) (600 * modifiers.get(WizardryItems.duration_upgrade)));
					}
					int level = modifiers.get(SpellModifiers.POTENCY) == 1.0f
							? 1
							: (int) ((modifiers.get(SpellModifiers.POTENCY) - 1.0f) / Constants.POTENCY_INCREASE_PER_TIER
							+ 0.5f);
					System.out.println("level: " + level);
					stack.addEnchantment(ASEnchantments.degrade_sword, level);
					//	}

				}

				this.playSound(world, caster, ticksInUse, -1, modifiers);
				return true;
			}

			if (world.isRemote) {
				for (int i = 0; i < 10; i++) {
					double x = target.posX + world.rand.nextDouble() * 2 - 1;
					double y = target.posY + target.getEyeHeight() - 0.5 + world.rand.nextDouble();
					double z = target.posZ + world.rand.nextDouble() * 2 - 1;
					ParticleBuilder.create(Type.SPARKLE).pos(x, y, z).vel(0, 0.1, 0).clr(0.9f, 0.7f, 1).spawn(world);
				}
			}
		}
		return true;
	}

	private enum EnumDegradedItem {
		BOW(WizardryEnchantments.magic_bow, ASEnchantments.degrade_bow),
		SWORD(WizardryEnchantments.magic_sword, ASEnchantments.degrade_sword);

		Enchantment beneficialEnchant;
		Enchantment debuff;

		EnumDegradedItem(Enchantment beneficialEnchant, Enchantment debuff) {
			this.beneficialEnchant = beneficialEnchant;
			this.debuff = debuff;
		}

	}

	@Override
	protected boolean onBlockHit(World world, BlockPos pos, EnumFacing side, Vec3d hit,
			@Nullable EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {
		return true;
	}

	@Override
	protected boolean onMiss(World world, @Nullable EntityLivingBase caster, Vec3d origin, Vec3d direction, int ticksInUse, SpellModifiers modifiers) {
		return true;
	}
}
