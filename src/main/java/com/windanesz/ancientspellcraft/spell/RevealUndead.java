package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.registry.ASItems;
import com.windanesz.ancientspellcraft.util.ASUtils;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

public class RevealUndead extends Spell {

	public RevealUndead() {
		super(AncientSpellcraft.MODID, "reveal_undead", SpellActions.POINT_UP, false);
		addProperties(EFFECT_RADIUS, EFFECT_DURATION);
	}

	@Override
	public boolean cast(World world, EntityPlayer caster, EnumHand hand, int ticksInUse, SpellModifiers modifiers) {
		double radius = getProperty(EFFECT_RADIUS).floatValue() * modifiers.get(WizardryItems.blast_upgrade);
		int count = 0;
		for (EntityLivingBase entity : EntityUtils.getEntitiesWithinRadius(radius, caster.posX, caster.posY, caster.posZ, world, EntityLivingBase.class)) {
			if (ASUtils.isEntityConsideredUndead(entity)) {
				entity.addPotionEffect(new PotionEffect(MobEffects.GLOWING, (int)(getProperty(EFFECT_DURATION).intValue() * modifiers.get(WizardryItems.duration_upgrade)), 0));
				count++;
			}
		}

		if (!world.isRemote) {
			caster.sendStatusMessage(new TextComponentTranslation("spell.ancientspellcraft:reveal_undead.count", count), false);
		} else {
			Vec3d origin = caster.getPositionEyes(1);
			for (int i = 0; i < 30; i++) {
				double x = origin.x - 1 + world.rand.nextDouble() * 2;
				double y = origin.y - 0.25 + world.rand.nextDouble() * 0.5;
				double z = origin.z - 1 + world.rand.nextDouble() * 2;
				ParticleBuilder.create(ParticleBuilder.Type.SPARKLE).pos(x, y, z)
						.vel(0, 0.1, 0).fade(0, 0, 0).spin(0.3f, 0.03f)
						.clr(250, 248, 205).spawn(world);
			}
		}
		return true;
	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == ASItems.ancient_spellcraft_spell_book || item == ASItems.ancient_spellcraft_scroll;
	}

}
