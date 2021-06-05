package com.windanesz.ancientspellcraft.ritual;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.tileentity.TileRune;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.ParticleBuilder;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

public class Rejuvenation extends Ritual {

	// throw in an astral diamond to make permanent?
	public Rejuvenation() {
		super(AncientSpellcraft.MODID, "rejuvenation", SpellActions.SUMMON, false);
	}

	@Override
	public void initialEffect(World world, EntityPlayer caster, TileRune centerPiece) {
		ruinNonCenterPieceRunes(centerPiece, world);

		if (world.isRemote) {
			Random rand = world.rand;
			for (int i = 1; i < 3; i++) {
				float brightness = 0.5f + (rand.nextFloat() * 0.5f);
				double radius = rand.nextDouble() * (2);
				float angle = rand.nextFloat() * (float) Math.PI * 2;
				ParticleBuilder.create(ParticleBuilder.Type.SPARKLE)
						.pos(centerPiece.getX() + radius * MathHelper.cos(angle), centerPiece.getY(), centerPiece.getZ() + radius * MathHelper.sin(angle))
						.vel(0, 0.05, 0)
						.time(48 + rand.nextInt(12))
						.clr(1.0f, 1.0f, brightness)
						.spawn(world);
			}
		}
	}

	@Override
	public void effect(World world, EntityPlayer caster, TileRune centerPiece) {
		super.effect(world, caster, centerPiece);
		List<EntityLivingBase> entities = EntityUtils.getEntitiesWithinRadius(1.5f, centerPiece.getX(), centerPiece.getY(), centerPiece.getPos().getZ(), world, EntityLivingBase.class);
		for (EntityLivingBase entityLivingBase : entities) {
			if (!entityLivingBase.isPotionActive(MobEffects.REGENERATION)) {
				entityLivingBase.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 60, 1));
			}
			if (entityLivingBase.isPotionActive(MobEffects.WITHER)) {
				entityLivingBase.removePotionEffect(MobEffects.WITHER);
			}
			if (entityLivingBase.isPotionActive(MobEffects.POISON)) {
				entityLivingBase.removePotionEffect(MobEffects.POISON);
			}
			if (entityLivingBase.isPotionActive(MobEffects.NAUSEA)) {
				entityLivingBase.removePotionEffect(MobEffects.NAUSEA);
			}
		}
		if (world.isRemote) {
			if (world.getTotalWorldTime() % 5 == 0) {
				Random rand = world.rand;
				for (int i = 1; i < 3; i++) {
					float brightness = 0.5f + (rand.nextFloat() * 0.5f);
					double radius = rand.nextDouble() * (2);
					float angle = rand.nextFloat() * (float) Math.PI * 2;
					ParticleBuilder.create(ParticleBuilder.Type.SPARKLE)
							.pos(centerPiece.getX() + 0.5f + radius * MathHelper.cos(angle), centerPiece.getY(), centerPiece.getZ() + 0.5f + radius * MathHelper.sin(angle))
							.vel(0, 0.05, 0)
							.time(48 + rand.nextInt(12))
							.clr(1.0f, 1.0f, brightness)
							.spawn(world);
				}
			}

			ParticleBuilder.create(ParticleBuilder.Type.FLASH).clr(252, 252, 159).pos(centerPiece.getXCenter(), centerPiece.getYCenter() + 0.5f, centerPiece.getZCenter()).scale(2f).spawn(world);
			ParticleBuilder.create(ParticleBuilder.Type.FLASH).clr(255, 255, 247).pos(centerPiece.getXCenter(), centerPiece.getYCenter() + 0.5f, centerPiece.getZCenter()).scale(0.9f).spawn(world);
		}
	}
}
