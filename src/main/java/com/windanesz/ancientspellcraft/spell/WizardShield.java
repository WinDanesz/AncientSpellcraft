package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.handler.ASEventHandler;
import com.windanesz.ancientspellcraft.registry.ASItems;
import com.windanesz.ancientspellcraft.registry.ASPotions;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

/**
 * Also see {@link ASEventHandler#onLivingHurtEvent(net.minecraftforge.event.entity.living.LivingHurtEvent)}
 */
public class WizardShield extends Spell {

	public WizardShield() {

		super(AncientSpellcraft.MODID, "wizard_shield", SpellActions.POINT_UP, true);
	}

	@Override
	public boolean cast(World world, EntityPlayer caster, EnumHand hand, int ticksInUse, SpellModifiers modifiers) {
		int amplifier = 0;

		if (caster.isPotionActive(ASPotions.wizard_shield)) {
			PotionEffect effect = caster.getActivePotionEffect(ASPotions.wizard_shield);
			if (effect != null) {
				amplifier = effect.getAmplifier();
			}
		}

		if (ticksInUse <= 80 && ticksInUse % 2 == 0) {
			caster.addPotionEffect(new PotionEffect(ASPotions.wizard_shield, 80, amplifier + 1));
		} else if (ticksInUse % 40 == 0 && amplifier <= 15) {
			caster.addPotionEffect(new PotionEffect(ASPotions.wizard_shield, 80, amplifier + 1));
		}
		this.playSound(world, caster, ticksInUse, -1, modifiers);

		return true;
	}

	@Override
	protected SoundEvent[] createSounds(){
		return this.createContinuousSpellSounds();
	}

	@Override
	protected void playSound(World world, EntityLivingBase entity, int ticksInUse, int duration, SpellModifiers modifiers, String... sounds){
		this.playSoundLoop(world, entity, ticksInUse);
	}

	@Override
	protected void playSound(World world, double x, double y, double z, int ticksInUse, int duration, SpellModifiers modifiers, String... sounds){
		this.playSoundLoop(world, x, y, z, ticksInUse, duration);
	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == ASItems.ancient_spell_book || item == ASItems.ancient_spellcraft_scroll;
	}
}
