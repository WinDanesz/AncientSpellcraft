package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftItems;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.spell.SpellBuff;
import electroblob.wizardry.util.AllyDesignationSystem;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.List;

public class Celerity extends SpellBuff {
	public Celerity() {
		super(AncientSpellcraft.MODID, "celerity", 157, 168, 249, () -> MobEffects.SPEED);
		soundValues(0.7f, 1.2f, 0.4f);
		addProperties(EFFECT_RADIUS);
	}

	@Override
	public boolean cast(World world, EntityPlayer caster, EnumHand hand, int ticksInUse, SpellModifiers modifiers) {
		List<EntityLivingBase> targets = EntityUtils.getEntitiesWithinRadius(
				getProperty(EFFECT_RADIUS).floatValue() * modifiers.get(WizardryItems.blast_upgrade),
				caster.posX, caster.posY, caster.posZ, world, EntityLivingBase.class);

		for (EntityLivingBase target : targets) {
			if (AllyDesignationSystem.isAllied(caster, target) || target == caster ||
					(target instanceof IEntityOwnable && ((IEntityOwnable) target).getOwnerId() == caster.getUniqueID())) {

				System.out.println("current entity:" + target.getDisplayName());
				this.applyEffects(target, modifiers);
				if (world.isRemote)
					this.spawnParticles(world, caster, modifiers);
			}
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

		playSound(world, caster, ticksInUse, -1, modifiers);
		return true;
	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == AncientSpellcraftItems.ancient_spellcraft_spell_book || item == AncientSpellcraftItems.ancient_spellcraft_scroll;
	}
}
