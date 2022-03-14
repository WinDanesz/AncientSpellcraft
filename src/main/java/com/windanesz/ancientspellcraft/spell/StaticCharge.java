package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.registry.ASEnchantments;
import com.windanesz.ancientspellcraft.registry.ASItems;
import electroblob.wizardry.constants.Constants;
import electroblob.wizardry.data.WizardData;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.spell.ImbueWeapon;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.InventoryUtils;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class StaticCharge extends Spell {

	/**
	 * Author: WinDanesz, Electroblob
	 * Weapon effect : {@link com.windanesz.ancientspellcraft.handler.ASEventHandler#onLivingHurtEvent}
	 * On-hit particle: {@link com.windanesz.ancientspellcraft.handler.ASEventHandler#onLivingAttackEvent}
	 */

	public StaticCharge() {
		super(AncientSpellcraft.MODID, "static_charge", SpellActions.IMBUE, false);
		addProperties(EFFECT_DURATION);
	}

	@Override
	public boolean cast(World world, EntityPlayer caster, EnumHand hand, int ticksInUse, SpellModifiers modifiers) {

		// Won't work if the weapon already has the enchantment
		if (WizardData.get(caster) != null
				&& WizardData.get(caster).getImbuementDuration(ASEnchantments.static_charge) <= 0) {

			for (ItemStack stack : InventoryUtils.getPrioritisedHotbarAndOffhand(caster)) {

				if ((ImbueWeapon.isSword(stack))
						&& !EnchantmentHelper.getEnchantments(stack).containsKey(ASEnchantments.static_charge)) {
					// The enchantment level as determined by the damage multiplier. The + 0.5f is so that
					// weird float processing doesn't incorrectly round it down.
					stack.addEnchantment(ASEnchantments.static_charge,
							modifiers.get(SpellModifiers.POTENCY) == 1.0f ? 1
									: (int) ((modifiers.get(SpellModifiers.POTENCY) - 1.0f)
									/ Constants.POTENCY_INCREASE_PER_TIER + 0.5f));

					WizardData.get(caster).setImbuementDuration(ASEnchantments.static_charge,
							(int) (getProperty(EFFECT_DURATION).floatValue() * modifiers.get(WizardryItems.duration_upgrade)));

					if (world.isRemote) {
						for (int i = 0; i < 10; i++) {
							double x = caster.posX + world.rand.nextDouble() * 2 - 1;
							double y = caster.getEntityBoundingBox().minY + caster.getEyeHeight() - 0.5 + world.rand.nextDouble();
							double z = caster.posZ + world.rand.nextDouble() * 2 - 1;
							ParticleBuilder.create(ParticleBuilder.Type.SPARKLE).pos(x, y, z).vel(0, 0.1, 0).clr(0.9f, 0.7f, 1).spawn(world);
						}
					}

					this.playSound(world, caster, ticksInUse, -1, modifiers);
					return true;

				}
			}
		}
		return false;
	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == ASItems.ancient_spellcraft_spell_book || item == ASItems.ancient_spellcraft_scroll;
	}

}
