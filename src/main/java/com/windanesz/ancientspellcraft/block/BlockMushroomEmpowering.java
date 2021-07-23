package com.windanesz.ancientspellcraft.block;

import com.windanesz.ancientspellcraft.registry.AncientSpellcraftPotions;
import electroblob.wizardry.registry.WizardryPotions;
import electroblob.wizardry.util.ParticleBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class BlockMushroomEmpowering extends BlockMagicMushroom {

	public BlockMushroomEmpowering() {
		super();
	}

	@Override
	public boolean applyBeneficialEffect(World world,
			@Nullable Block block, BlockPos pos, DamageSource source, float damage, IBlockState state,
			@Nullable EntityLivingBase caster, EntityLivingBase target, float potency) {

		if (target != null) {

			if (!world.isRemote) {

				List<Potion> potions = new ArrayList<>();
				potions.add(AncientSpellcraftPotions.mana_regeneration);
				potions.add(AncientSpellcraftPotions.spell_siphon);
				potions.add(AncientSpellcraftPotions.spell_cooldown);
				potions.add(AncientSpellcraftPotions.spell_blast);
				potions.add(AncientSpellcraftPotions.spell_duration);
				potions.add(AncientSpellcraftPotions.spell_range);
				potions.add(WizardryPotions.empowerment);

				PotionEffect potionEffect = new PotionEffect(potions.get(world.rand.nextInt(potions.size())), POTION_DURATION * 4);
				target.addPotionEffect(potionEffect);

			} else {
				for (int i = 0; i < 10; i++) {
					double x = target.posX + world.rand.nextDouble() * 2 - 1;
					double y = target.posY + target.getEyeHeight() - 0.5 + world.rand.nextDouble();
					double z = target.posZ + world.rand.nextDouble() * 2 - 1;
					ParticleBuilder.create(ParticleBuilder.Type.SPARKLE).pos(x, y, z).vel(0, 0.1, 0).clr(121, 64, 255).spawn(world);
				}

				ParticleBuilder.create(ParticleBuilder.Type.BUFF).entity(target).clr(121, 64, 255).spawn(world);
			}

			return true;
		}
		return false;
	}

	@Override
	public boolean applyHarmfulEffect(World world,
			@Nullable Block block, BlockPos pos, DamageSource source, float damage, IBlockState state,
			@Nullable EntityLivingBase caster, EntityLivingBase target, float potency) {
		return true;
	}

}
