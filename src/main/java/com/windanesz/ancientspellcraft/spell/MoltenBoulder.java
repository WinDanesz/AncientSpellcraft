package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.entity.construct.EntityMoltenBoulder;
import com.windanesz.ancientspellcraft.entity.living.EntityEvilClassWizard;
import com.windanesz.ancientspellcraft.registry.ASItems;
import electroblob.wizardry.item.ItemWizardArmour;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.spell.SpellConstruct;
import electroblob.wizardry.util.GeometryUtils;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;

public class MoltenBoulder extends SpellConstruct<EntityMoltenBoulder> implements IClassSpell {

	public static final String SPEED = "speed";
	public static final String KNOCKBACK_STRENGTH = "knockback_strength";

	public MoltenBoulder(){
		super(AncientSpellcraft.MODID, "molten_boulder", SpellActions.SUMMON, EntityMoltenBoulder::new, false);
		addProperties(SPEED, DAMAGE, KNOCKBACK_STRENGTH);
	}

	@Override
	protected void addConstructExtras(EntityMoltenBoulder construct, EnumFacing side, EntityLivingBase caster, SpellModifiers modifiers){
		float speed = getProperty(SPEED).floatValue();
		// Unlike tornado, boulder always has the same speed
		Vec3d direction = caster == null ? new Vec3d(side.getDirectionVec()) : GeometryUtils.horizontalise(caster.getLookVec());
		construct.setHorizontalVelocity(direction.x * speed, direction.z * speed);
		construct.rotationYaw = caster == null ? side.getHorizontalAngle() : caster.rotationYaw;
		double yOffset = caster == null ? 0 : 1.6;
		construct.setPosition(construct.posX + direction.x, construct.posY + yOffset, construct.posZ + direction.z);
	}

	@Override
	public boolean canBeCastBy(EntityLiving npc, boolean override) {
		return npc instanceof EntityEvilClassWizard && ((EntityEvilClassWizard) npc).getArmourClass() == ItemWizardArmour.ArmourClass.SAGE;
	}

	@Override
	public ItemWizardArmour.ArmourClass getArmourClass() { return ItemWizardArmour.ArmourClass.SAGE; }

	@Override
	public boolean applicableForItem(Item item) {
		return item == ASItems.mystic_spell_book;
	}

}
