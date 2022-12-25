package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.Settings;
import com.windanesz.ancientspellcraft.registry.ASItems;
import electroblob.wizardry.item.ItemWizardArmour;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.spell.SpellRay;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Extension extends SpellRay implements IClassSpell {

	public static final String MAX_BONUS_DURATION = "max_bonus_duration";

	public Extension() {
		super(AncientSpellcraft.MODID, "extension", SpellActions.POINT, false);
		addProperties(MAX_BONUS_DURATION);
	}

	public boolean affectEntity(EntityLivingBase target, World world) {
		if (!world.isRemote) {
			List<PotionEffect> potions = new ArrayList<>(target.getActivePotionEffects());
			List<String> blacklist = new ArrayList<>(Arrays.asList(Settings.generalSettings.extension_spell_blacklist));
			if (!potions.isEmpty()) {
				potions.removeIf(p -> blacklist.contains(p.getPotion().getRegistryName().toString()));
			}

			if (!potions.isEmpty()) {
				PotionEffect oldEffect = potions.get(world.rand.nextInt(potions.size()));
				int oldDuration = oldEffect.getDuration();
				int newDuration = Math.min(oldDuration * 2, oldDuration + getProperty(MAX_BONUS_DURATION).intValue());
				PotionEffect newEffect = new PotionEffect(oldEffect.getPotion(), oldEffect.getAmplifier(), newDuration);
				target.removePotionEffect(oldEffect.getPotion());
				target.addPotionEffect(newEffect);
				return true;
			}

		}
		return false;
	}

	@Override
	protected boolean onEntityHit(World world, Entity target, Vec3d hit, EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {
		if (caster != null && caster.isSneaking()) {
			return affectEntity(caster, world);
		}

		if (EntityUtils.isLiving(target)) {
			return affectEntity((EntityLivingBase) target, world);
		}

		return true;
	}

	@Override
	protected boolean onBlockHit(World world, BlockPos pos, EnumFacing side, Vec3d hit, EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {
		if (caster != null && caster.isSneaking()) {
			return affectEntity(caster, world);
		}
		return false;
	}

	@Override
	protected boolean onMiss(World world, EntityLivingBase caster, Vec3d origin, Vec3d direction, int ticksInUse, SpellModifiers modifiers) {
		if (caster != null && caster.isSneaking()) {
			return affectEntity(caster, world);
		}
		return false;
	}

	@Override
	protected void spawnParticle(World world, double x, double y, double z, double vx, double vy, double vz) {
		ParticleBuilder.create(ParticleBuilder.Type.DARK_MAGIC).pos(x, y, z).clr(0x571e65).spawn(world);
		ParticleBuilder.create(ParticleBuilder.Type.DARK_MAGIC).pos(x, y, z).clr(0x251609).spawn(world);
		ParticleBuilder.create(ParticleBuilder.Type.SPARKLE).pos(x, y, z).time(12 + world.rand.nextInt(8)).clr(0x0b4b40).spawn(world);
	}

	@Override
	public ItemWizardArmour.ArmourClass getArmourClass() { return ItemWizardArmour.ArmourClass.SAGE; }

	@Override
	public boolean applicableForItem(Item item) {
		return item == ASItems.mystic_spell_book;
	}
}
