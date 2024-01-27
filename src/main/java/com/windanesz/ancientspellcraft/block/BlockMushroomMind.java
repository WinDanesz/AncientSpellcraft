package com.windanesz.ancientspellcraft.block;

import electroblob.wizardry.registry.WizardryPotions;
import electroblob.wizardry.spell.Intimidate;
import electroblob.wizardry.spell.MindControl;
import electroblob.wizardry.util.ParticleBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BlockMushroomMind extends BlockMagicMushroom {

	public BlockMushroomMind() {
		super();
	}

	@Override
	public boolean applyHarmfulEffect(World world,
			@Nullable Block block, BlockPos pos, DamageSource source, float damage, IBlockState state,
			@Nullable EntityLivingBase caster, EntityLivingBase target, float potency) {

		// affects players with nausea and weakness
		if (target instanceof EntityPlayer) {
			target.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, (int) POTION_DURATION / 2, 0));
			target.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, (int) POTION_DURATION / 2, 0));
			return true;
		} else if (target instanceof EntityLiving) {
			if (!world.isRemote) {

				EntityLiving livingTarget = (EntityLiving) target;
				float chance = world.rand.nextFloat();

				// 30% chance to mind control the target
				if (chance > 0.7f && caster != null) {
					MindControl.startControlling(livingTarget, caster, POTION_DURATION * 10);
					// 20% chance to make it run away
				} else if (chance > 0.5f && target instanceof EntityCreature && caster != null) {
					NBTTagCompound entityNBT = target.getEntityData();
					if (entityNBT != null)
						entityNBT.setUniqueId(Intimidate.NBT_KEY, caster.getUniqueID());
					livingTarget.addPotionEffect(new PotionEffect(WizardryPotions.fear, POTION_DURATION * 5, 0));
					// Mind trick otherwise (50% chance, or 100% if no caster was present)
				} else {
					livingTarget.setAttackTarget(null);
					livingTarget.addPotionEffect(new PotionEffect(WizardryPotions.mind_trick, POTION_DURATION * 5, 0));
				}
			} else {
				for(int i=0; i<10; i++){
					ParticleBuilder.create(ParticleBuilder.Type.DARK_MAGIC, world.rand, target.posX,
							target.posY + target.getEyeHeight(), target.posZ, 0.25, false)
							.clr(0.8f, 0.2f, 1.0f).spawn(world);
					ParticleBuilder.create(ParticleBuilder.Type.DARK_MAGIC, world.rand, target.posX,
							target.posY + target.getEyeHeight(), target.posZ, 0.25, false)
							.clr(0.2f, 0.04f, 0.25f).spawn(world);
				}
			}
			return true;
		}

		return false;
	}
}
