package com.windanesz.ancientspellcraft.entity.construct;

import com.windanesz.ancientspellcraft.item.ItemBattlemageSword;
import com.windanesz.ancientspellcraft.item.WizardClassWeaponHelper;
import com.windanesz.ancientspellcraft.registry.ASPotions;
import com.windanesz.ancientspellcraft.registry.ASSpells;
import electroblob.wizardry.block.BlockReceptacle;
import electroblob.wizardry.constants.Element;
import electroblob.wizardry.entity.construct.EntityMagicConstruct;
import electroblob.wizardry.potion.Curse;
import electroblob.wizardry.potion.PotionMagicEffect;
import electroblob.wizardry.registry.Spells;
import electroblob.wizardry.registry.WizardrySounds;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.BlockUtils;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.ParticleBuilder;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityAntiMagicField extends EntityMagicConstruct {

	public EntityAntiMagicField(World world) {
		super(world);
		this.height = 0.1f;
		this.width = 0.1f;
	}

	public void onUpdate() {

		if (this.ticksExisted % 120 == 1) {
			this.playSound(WizardrySounds.ENTITY_BLIZZARD_AMBIENT, 1.0f, 1.0f);
		}

		super.onUpdate();

		// This is a good example of why you might define a spell base property without necessarily using it in the
		// spell - in fact, blizzard doesn't even have a spell class (yet)
		double radius = ASSpells.antimagic_field.getProperty(Spell.EFFECT_RADIUS).doubleValue();

		if (!this.world.isRemote) {

			List<EntityLivingBase> targets = EntityUtils.getEntitiesWithinRadius(radius, this.posX, this.posY,
					this.posZ, this.world, EntityLivingBase.class);

			for (EntityLivingBase target : targets) {

				// All entities are slowed, even the caster (except those immune to frost effects)
				target.addPotionEffect(new PotionEffect(ASPotions.magical_exhaustion, 40, 3));

				Map<Potion, PotionEffect> activeEffects = new HashMap<>(target.getActivePotionMap());
				for (Potion potion : activeEffects.keySet()) {
					if (potion instanceof PotionMagicEffect && !(potion instanceof Curse) && potion != ASPotions.magical_exhaustion) {
						target.removePotionEffect(potion);
					}
				}
			}

		} else {
			int[] colours = BlockReceptacle.PARTICLE_COLOURS.get(Element.NECROMANCY);
			for (int i = 0; i < 20; i++) {
				double angle = 2 * Math.PI * world.rand.nextDouble(); // Generate a random angle in radians
				double distance = radius * Math.sqrt(world.rand.nextDouble()); // Generate a random distance within the radius

				double offsetX = distance * Math.cos(angle);
				double offsetZ = distance * Math.sin(angle);

				ParticleBuilder.create(ParticleBuilder.Type.DUST)
						.pos(posX + offsetX, posY + radius * world.rand.nextFloat(), posZ + offsetZ)
						.vel(0, 0.05 + (world.rand.nextFloat() * 0.1), 0)
						.clr(colours[1]).fade(colours[2]).scale(2).time(40).shaded(false).spawn(world);
			}

			int PERIMETER_PARTICLE_DENSITY = 6;

			for (int i = 0; i < PERIMETER_PARTICLE_DENSITY; i++) {

				float angle = ((float) Math.PI * 2) / PERIMETER_PARTICLE_DENSITY * (i + rand.nextFloat());

				double x = posX + radius * MathHelper.sin(angle);
				double z = posZ + radius * MathHelper.cos(angle);

				Integer y = BlockUtils.getNearestSurface(world, new BlockPos(x, posY, z), EnumFacing.UP, 5, true, BlockUtils.SurfaceCriteria.COLLIDABLE);

				if (y != null) {
					ParticleBuilder.create(ParticleBuilder.Type.DUST).pos(x, y, z).vel(0, 0.01, 0).scale(2).clr(0xf575f5).fade(0xffec90).spawn(world);
				}
			}
		}
	}
}
