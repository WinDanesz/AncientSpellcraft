package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.entity.projectile.EntityManaVortex;
import com.windanesz.ancientspellcraft.registry.ASItems;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.spell.SpellConstruct;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;

public class ManaVortex extends SpellConstruct<EntityManaVortex> {

	public static final String SPEED = "speed";

	public ManaVortex(){
		super(AncientSpellcraft.MODID, "mana_vortex", SpellActions.POINT, EntityManaVortex::new, false);
		addProperties(EFFECT_RADIUS, SPEED, DAMAGE);
	}

	@Override
	protected void addConstructExtras(EntityManaVortex construct, EnumFacing side, EntityLivingBase caster, SpellModifiers modifiers){
		float speed = getProperty(SPEED).floatValue();
		Vec3d direction = caster == null ? new Vec3d(side.getDirectionVec()) : caster.getLookVec();
		construct.setHorizontalVelocity(direction.x * speed, direction.z * speed);
		construct.lifetime = 40;
	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == ASItems.ancient_spell_book || item == ASItems.ancient_spellcraft_scroll;
	}

}
