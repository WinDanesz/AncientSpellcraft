package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.registry.ASItems;
import electroblob.wizardry.item.ItemWizardArmour;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.spell.SpellRay;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.MagicDamage;
import electroblob.wizardry.util.MagicDamage.DamageType;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.ParticleBuilder.Type;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

public class PoisonSpray extends SpellRay implements IClassSpell {

	public PoisonSpray(){
		super(AncientSpellcraft.MODID, "poison_spray", SpellActions.POINT, true);
		this.particleVelocity(1);
		this.particleSpacing(0.5);
		addProperties(DAMAGE, EFFECT_DURATION, EFFECT_STRENGTH);
		this.soundValues(2.5f, 1, 0);
	}

	// The following three methods serve as a good example of how to implement continuous spell sounds (hint: it's easy)

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
	protected boolean onEntityHit(World world, Entity target, Vec3d hit, EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers){
		// Fire can damage armour stands
		if(target instanceof EntityLivingBase){

			if(MagicDamage.isEntityImmune(DamageType.POISON, target)){
				if(!world.isRemote && ticksInUse == 1 && caster instanceof EntityPlayer) ((EntityPlayer)caster)
				.sendStatusMessage(new TextComponentTranslation("spell.resist", target.getName(),
						this.getNameForTranslationFormatted()), true);
			// This now only damages in line with the maxHurtResistantTime. Some mods don't play nicely and fiddle
			// with this mechanic for their own purposes, so this line makes sure that doesn't affect wizardry.
			}else if(ticksInUse % ((EntityLivingBase)target).maxHurtResistantTime == 1){
				((EntityLivingBase)target).addPotionEffect(new PotionEffect(MobEffects.POISON,
						(int)(getProperty(EFFECT_DURATION).floatValue() * modifiers.get(WizardryItems.duration_upgrade)),
						getProperty(EFFECT_STRENGTH).intValue()));				EntityUtils.attackEntityWithoutKnockback(target,
						MagicDamage.causeDirectMagicDamage(caster, DamageType.POISON),
						getProperty(DAMAGE).floatValue() * modifiers.get(SpellModifiers.POTENCY));
			}
		}
		
		return true;
	}

	@Override
	protected boolean onBlockHit(World world, BlockPos pos, EnumFacing side, Vec3d hit, EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers){
		return false;
	}

	@Override
	protected boolean onMiss(World world, EntityLivingBase caster, Vec3d origin, Vec3d direction, int ticksInUse, SpellModifiers modifiers){
		return true;
	}
	
	@Override
	protected void spawnParticle(World world, double x, double y, double z, double vx, double vy, double vz){
		float brightness = world.rand.nextFloat();
		ParticleBuilder.create(Type.SPARKLE).pos(x, y, z).vel(vx, vy, vz).time(8 + world.rand.nextInt(12))
				.clr(0.1f + brightness, 0.2f + brightness, 0).collide(true).spawn(world);
	}

	@Override
	public ItemWizardArmour.ArmourClass getArmourClass() { return ItemWizardArmour.ArmourClass.SAGE; }

	@Override
	public boolean applicableForItem(Item item) { return item == ASItems.mystic_spell_book; }
}
