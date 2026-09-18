package com.windanesz.ancientspellcraft.mixin.ebwizardry;

import com.windanesz.ancientspellcraft.integration.baubles.ASBaublesIntegration;
import com.windanesz.ancientspellcraft.item.ItemVesselOfTheWitheredOath;
import com.windanesz.ancientspellcraft.registry.ASItems;
import electroblob.wizardry.item.ItemArtefact;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.spell.PlagueOfDarkness;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.spell.SpellBuff;
import electroblob.wizardry.util.MagicDamage;
import electroblob.wizardry.util.MagicDamage.DamageType;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import javax.annotation.Nullable;

/**
 * Adds Vessel of the Withered Oath ({@link ASItems#charm_vessel_of_the_withered_oath}) compatibility to the Plague
 * of Darkness spell: applies the artefact's stored curse to affected targets. Previously this lived in a
 * {@code PlagueOfDarknessAS extends SpellAreaEffect} class that got re-registered under the same name as the base
 * spell, with the networkID hazard that came with that (see {@link MixinMine}'s javadoc for the full story). See
 * {@link MixinSpellAreaEffect} for the other half of this (the {@code cast} hook that removes the stored curse once
 * spent).
 */
@Mixin(PlagueOfDarkness.class)
public class MixinPlagueOfDarkness {

	/**
	 * @author WinDanesz
	 * @reason Add Vessel of the Withered Oath curse application.
	 */
	@Overwrite(remap = false)
	protected boolean affectEntity(World world, Vec3d origin, @Nullable EntityLivingBase caster, EntityLivingBase target, int targetCount, int ticksInUse, SpellModifiers modifiers){

		if(!world.isRemote){

			if(caster instanceof EntityPlayer && ItemArtefact.isArtefactActive((EntityPlayer)caster, ASItems.charm_vessel_of_the_withered_oath)){
				ItemStack itemstack = ASBaublesIntegration.getEquippedArtefactStacks((EntityPlayer)caster, ItemArtefact.Type.CHARM).get(0);
				if(itemstack.getItem() == ASItems.charm_vessel_of_the_withered_oath){
					Potion curse = ((ItemVesselOfTheWitheredOath)itemstack.getItem()).getStoredCurse(itemstack);
					if(curse != null && target != null){
						target.addPotionEffect(new PotionEffect(curse, Integer.MAX_VALUE));
					}
				}
			}

			if(target != null && !MagicDamage.isEntityImmune(DamageType.WITHER, target)){
				target.attackEntityFrom(MagicDamage.causeDirectMagicDamage(caster, DamageType.WITHER),
						((PlagueOfDarkness)(Object)this).getProperty(Spell.DAMAGE).floatValue() * modifiers.get(SpellModifiers.POTENCY));
				target.addPotionEffect(new PotionEffect(MobEffects.WITHER,
						(int)(((PlagueOfDarkness)(Object)this).getProperty(Spell.EFFECT_DURATION).floatValue() * modifiers.get(WizardryItems.duration_upgrade)),
						((PlagueOfDarkness)(Object)this).getProperty(Spell.EFFECT_STRENGTH).intValue() + SpellBuff.getStandardBonusAmplifier(modifiers.get(SpellModifiers.POTENCY))));
			}
		}

		return true;
	}
}
