package com.windanesz.ancientspellcraft.potion;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftSpells;
import com.windanesz.ancientspellcraft.spell.Burrow;
import electroblob.wizardry.potion.PotionMagicEffect;
import electroblob.wizardry.util.BlockUtils;
import electroblob.wizardry.util.EntityUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class PotionBurrow extends PotionMagicEffect {

	public PotionBurrow() {
		super(false, 0xe65aff, new ResourceLocation(AncientSpellcraft.MODID, "textures/gui/potion_icon_burrow.png"));
	}

	@Override
	public boolean isReady(int duration, int amplifier) {
		return duration == 5;
	}

	@Override
	public void performEffect(EntityLivingBase entitylivingbase, int strength) {
		makeAirPocketIfPossible(entitylivingbase);
	}

	private void makeAirPocketIfPossible(EntityLivingBase caster) {
		if (!caster.world.isRemote && caster instanceof EntityPlayer && !caster.isAirBorne) {

			if (!EntityUtils.isCasting(caster, AncientSpellcraftSpells.burrow)) {

				World world = caster.world;

				caster.noClip = false;
				double newX = 0.5 + caster.posX - (caster.posX - Math.floor(caster.posX));
				double newZ = 0.5 + caster.posZ - (caster.posZ - Math.floor(caster.posZ));

				caster.setPositionAndUpdate(newX, caster.getPosition().getY(), newZ);

				IBlockState state0 = world.getBlockState(caster.getPosition().up());
				if (!world.isRemote) {
					if (Burrow.isDiggable(state0, (EntityPlayer) caster) && BlockUtils.canBreakBlock(caster, world, caster.getPosition().up())) {
						world.setBlockToAir(caster.getPosition().up());
					}
				}

				IBlockState state1 = world.getBlockState(caster.getPosition());
				if (!world.isRemote) {
					if (Burrow.isDiggable(state1,(EntityPlayer) caster) && BlockUtils.canBreakBlock(caster, world, caster.getPosition())) {
						world.setBlockToAir(caster.getPosition());
					}
				}

				IBlockState state2 = world.getBlockState(caster.getPosition().down());
				if (!world.isRemote) {
					if (Burrow.isDiggable(state2,(EntityPlayer) caster) && BlockUtils.canBreakBlock(caster, world, caster.getPosition().down())) {
						world.setBlockToAir(caster.getPosition().down());
					}
				}
			}
		}
	}
}
