package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftItems;
import electroblob.wizardry.spell.SpellConjuration;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.world.World;

public class ConjureShadowBlade extends SpellConjuration {
	public static final String WITHER_DURATION = "wither_duration";

	public ConjureShadowBlade() {
		super(AncientSpellcraft.MODID, "conjure_shadow_blade", AncientSpellcraftItems.shadow_blade);
		addProperties(DAMAGE, WITHER_DURATION);
	}

	@Override
	protected void spawnParticles(World world, EntityLivingBase caster, SpellModifiers modifiers) {

		for (int i = 0; i < 10; i++) {
			double x = caster.posX + world.rand.nextDouble() * 2 - 1;
			double y = caster.getEntityBoundingBox().minY + caster.getEyeHeight() - 0.5 + world.rand.nextDouble();
			double z = caster.posZ + world.rand.nextDouble() * 2 - 1;

			ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(x, y, z).time(6).vel(caster.world.rand.nextGaussian() / 40, caster.world.rand.nextDouble() / 40,
					caster.world.rand.nextGaussian() / 40).clr(0, 0, 0).collide(false).scale(0.5F).spawn(caster.world);
		}
	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == AncientSpellcraftItems.ancient_spellcraft_spell_book || item == AncientSpellcraftItems.ancient_spellcraft_scroll;
	}
}
