package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.registry.AncientSpellcraftPotions;

public class IntensifyingFocus extends MetaSpellBuff {

	public IntensifyingFocus() {
		super("intensifying_focus", 255, 255, 255, () -> AncientSpellcraftPotions.intensifying_focus);
	}

//	@Override
//		public boolean cast(World world, EntityPlayer caster, EnumHand hand, int ticksInUse, SpellModifiers modifiers) {
//			// TODO: Once we've found a way of detecting if amplifiers actually affect the potion type, implement it here.
//			//		int level = 0;
//			if (!world.isRemote) {
//				int currentAmplifier = 0;
//				boolean active = caster.isPotionActive(AncientSpellcraftPotions.intensifying_focus);
//
//				if (active) {
//					currentAmplifier = caster.getActivePotionEffect(AncientSpellcraftPotions.intensifying_focus).getAmplifier();
//
//					System.out.println("currentAmplifier: " + currentAmplifier);
//					//			if (currentAmplifier == 0) {
//					//				System.out.println("level was 0, adding one to it");
//					//				level += currentAmplifier + 1;
//					//			}
//
//					//			System.out.println("level");
//					//			if (level > 2) {
//					//				return false;
//					//			}
//
//				}
//				int bonusAmplifier = active ? currentAmplifier + 1 : 0;
//
//				if (bonusAmplifier > 2) {
//					return false;
//				}
//
//				for (Potion potion : potionSet) {
//					caster.addPotionEffect(new PotionEffect(potion, (int) (getProperty(getDurationKey(potion)).floatValue() * modifiers.get(WizardryItems.duration_upgrade)),
//							bonusAmplifier,
//							false, true));
//				}
//
//				return true;
//			}
//			return true;
//		}
}
