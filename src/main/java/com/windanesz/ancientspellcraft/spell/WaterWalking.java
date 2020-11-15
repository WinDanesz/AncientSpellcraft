package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.registry.AncientSpellcraftItems;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftPotions;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.List;

public class WaterWalking extends SpellBuffAS {

	public WaterWalking() {
		super("water_walking", 58, 147, 254, () -> AncientSpellcraftPotions.water_walking);
		addProperties(EFFECT_RADIUS);
	}

	@Override
	public boolean cast(World world, EntityPlayer caster, EnumHand hand, int ticksInUse, SpellModifiers modifiers) {

		List<EntityLivingBase> targets = EntityUtils.getEntitiesWithinRadius(
				getProperty(EFFECT_RADIUS).floatValue() * modifiers.get(WizardryItems.blast_upgrade),
				caster.posX, caster.posY, caster.posZ, world, EntityLivingBase.class);

		for (EntityLivingBase target : targets) {
			this.applyEffects(target, modifiers);
			if (world.isRemote)
				this.spawnParticles(world, caster, modifiers);
		}

		if (world.isRemote) {

			for (int i = 0; i < 50 * modifiers.get(WizardryItems.blast_upgrade); i++) {

				double radius = (1 + world.rand.nextDouble() * 4) * modifiers.get(WizardryItems.blast_upgrade);
				float angle = world.rand.nextFloat() * (float) Math.PI * 2;
				;

				double x = caster.posX + radius * MathHelper.cos(angle);
				double y = caster.getEntityBoundingBox().minY;
				double z = caster.posZ + radius * MathHelper.sin(angle);

				ParticleBuilder.create(ParticleBuilder.Type.SPARKLE).pos(x, y, z).vel(0, 0.03, 0).time(50).clr(157, 168, 249).spawn(world);

			}
		}

		return super.cast(world, caster, hand, ticksInUse, modifiers);
	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == AncientSpellcraftItems.ancient_spell_book || item == AncientSpellcraftItems.ancient_spellcraft_scroll;
	}
}
