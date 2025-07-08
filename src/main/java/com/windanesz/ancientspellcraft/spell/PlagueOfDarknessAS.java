package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.Settings;
import com.windanesz.ancientspellcraft.integration.baubles.ASBaublesIntegration;
import com.windanesz.ancientspellcraft.item.ItemVesselOfTheWitheredOath;
import com.windanesz.ancientspellcraft.registry.ASItems;
import electroblob.wizardry.item.ItemArtefact;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.spell.SpellAreaEffect;
import electroblob.wizardry.spell.SpellBuff;
import electroblob.wizardry.util.MagicDamage;
import electroblob.wizardry.util.MagicDamage.DamageType;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.ParticleBuilder.Type;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class PlagueOfDarknessAS extends SpellAreaEffect implements IOverrideSpell {

	public PlagueOfDarknessAS() {
		super("plague_of_darkness", SpellActions.POINT_DOWN, false);
		this.alwaysSucceed(true);
		addProperties(DAMAGE, EFFECT_DURATION, EFFECT_STRENGTH);
		soundValues(1, 1.1f, 0.2f);

		// must use the original networkID of the base spell
		handleNetworkIDChange(this, Settings.spellCompatSettings.plagueOfDarknessSpellNetworkID);
	}

	@Override
	public boolean cast(World world, EntityPlayer caster, EnumHand hand, int ticksInUse, SpellModifiers modifiers) {

		boolean result = findAndAffectEntities(world, caster.getPositionVector(), caster, ticksInUse, modifiers);
		if (result) {
			this.playSound(world, caster, ticksInUse, -1, modifiers);
			if (ItemArtefact.isArtefactActive(caster, ASItems.charm_vessel_of_the_withered_oath)) {
				ItemStack itemstack = ASBaublesIntegration.getEquippedArtefactStacks(caster, ItemArtefact.Type.CHARM).get(0);
				if (itemstack.getItem() == ASItems.charm_vessel_of_the_withered_oath) {
					((ItemVesselOfTheWitheredOath) itemstack.getItem()).removeStoredCurse(itemstack);
					ASBaublesIntegration.setArtefactToSlot(caster, itemstack, ItemArtefact.Type.CHARM);
				}
			}
		}
		return result;
	}

	@Override
	protected boolean affectEntity(World world, Vec3d origin, @Nullable EntityLivingBase caster, EntityLivingBase target, int targetCount, int ticksInUse, SpellModifiers modifiers) {
		if (caster instanceof EntityPlayer && ItemArtefact.isArtefactActive((EntityPlayer) caster, ASItems.charm_vessel_of_the_withered_oath)) {
			ItemStack itemstack = ASBaublesIntegration.getEquippedArtefactStacks((EntityPlayer) caster, ItemArtefact.Type.CHARM).get(0);
			if (itemstack.getItem() == ASItems.charm_vessel_of_the_withered_oath) {
				Potion curse = ((ItemVesselOfTheWitheredOath) itemstack.getItem()).getStoredCurse(itemstack);
				if (curse != null && target != null) {
					target.addPotionEffect(new PotionEffect(curse, Integer.MAX_VALUE));
				}
			}
		}

		if (!MagicDamage.isEntityImmune(DamageType.WITHER, target)) {
			target.attackEntityFrom(MagicDamage.causeDirectMagicDamage(caster, DamageType.WITHER), getProperty(DAMAGE).floatValue() * modifiers.get(SpellModifiers.POTENCY));
			target.addPotionEffect(new PotionEffect(MobEffects.WITHER, (int) (getProperty(EFFECT_DURATION).floatValue() * modifiers.get(WizardryItems.duration_upgrade)), getProperty(EFFECT_STRENGTH).intValue() + SpellBuff.getStandardBonusAmplifier(modifiers.get(SpellModifiers.POTENCY))));
		}

		return true;
	}

	@Override
	protected void spawnParticleEffect(World world, Vec3d origin, double radius, @Nullable EntityLivingBase caster, SpellModifiers modifiers) {

		double particleX, particleZ;

		for (int i = 0; i < 40 * modifiers.get(WizardryItems.blast_upgrade); i++) {

			particleX = origin.x - 1.0d + 2 * world.rand.nextDouble();
			particleZ = origin.z - 1.0d + 2 * world.rand.nextDouble();
			ParticleBuilder.create(Type.DARK_MAGIC).pos(particleX, origin.y, particleZ).vel(particleX - origin.x, 0, particleZ - origin.z).clr(0.1f, 0, 0).spawn(world);

			particleX = origin.x - 1.0d + 2 * world.rand.nextDouble();
			particleZ = origin.z - 1.0d + 2 * world.rand.nextDouble();
			ParticleBuilder.create(Type.SPARKLE).pos(particleX, origin.y, particleZ).vel(particleX - origin.x, 0, particleZ - origin.z).time(30).clr(0.1f, 0, 0.05f).spawn(world);

			particleX = origin.x - 1.0d + 2 * world.rand.nextDouble();
			particleZ = origin.z - 1.0d + 2 * world.rand.nextDouble();

			IBlockState block = world.getBlockState(new BlockPos(origin.x, origin.y - 0.5, origin.z));

			if (block != null) {
				world.spawnParticle(EnumParticleTypes.BLOCK_DUST, particleX, origin.y, particleZ, particleX - origin.x, 0, particleZ - origin.z, Block.getStateId(block));
			}
		}

		ParticleBuilder.create(Type.SPHERE).pos(origin.add(0, 0.1, 0)).scale((float) radius * 0.8f).clr(0.8f, 0, 0.05f).spawn(world);
	}

}
