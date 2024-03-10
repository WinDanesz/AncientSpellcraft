package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.registry.ASItems;
import electroblob.wizardry.constants.Element;
import electroblob.wizardry.item.ItemWizardArmour;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.AllyDesignationSystem;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.List;

public class ChaosVortex extends Spell implements IClassSpell {


	public ChaosVortex() {
		super(AncientSpellcraft.MODID, "chaos_vortex", SpellActions.POINT, true);
		this.soundValues(1, 1, 0.4f);
		addProperties(DAMAGE, EFFECT_RADIUS);
	}

	@Override
	public boolean cast(World world, EntityLiving caster, EnumHand hand, int ticksInUse, EntityLivingBase target, SpellModifiers modifiers) {
		return doSpellTick(world, caster, hand, ticksInUse, modifiers);
	}

	@Override
	public boolean cast(World world, EntityPlayer caster, EnumHand hand, int ticksInUse, SpellModifiers modifiers) {
		return doSpellTick(world, caster, hand, ticksInUse, modifiers);
	}

	private boolean doSpellTick(World world, EntityLivingBase caster, EnumHand hand, int ticksInUse,
			SpellModifiers modifiers) {
		Element element =  getElementOrMagicElement(caster);
		float radius = Math.min(5.0f, ticksInUse * 0.2f) * 2 * modifiers.get(WizardryItems.blast_upgrade);

		if (world.isRemote && caster.ticksExisted % 1 == 0) {
			for (int i = 0; i < 10; i++) {
				ParticleBuilder.create(ParticleBuilder.Type.FLASH)
						.entity(caster)
						.spin(radius * world.rand.nextFloat() + 1f, 0.006f)
						.scale(2)
						.clr(WarlockElementalSpellEffects.PARTICLE_COLOURS.get(element)[world.rand.nextInt(2)])
						.pos(0, world.rand.nextInt(10) + 0.5f, 0).time(40).spawn(world);

				ParticleBuilder.create(WarlockElementalSpellEffects.getElementalParticle(element))
						.entity(caster)
						.spin(radius * world.rand.nextFloat() + 1f, 0.026f)
						.scale(world.rand.nextFloat() * 2)
						.clr(WarlockElementalSpellEffects.PARTICLE_COLOURS.get(element)[element == Element.LIGHTNING ? 3 : world.rand.nextInt(2) ])
						.pos(0, world.rand.nextInt(10) + 0.5f, 0)
						.time(40)
						.spawn(world);
			}
		}
		if (caster.ticksExisted % 10 == 0) {
			radius = getProperty(EFFECT_RADIUS).floatValue() * modifiers.get(WizardryItems.blast_upgrade) ;
			List<EntityLivingBase> targets = EntityUtils.getLivingWithinRadius(radius * 0.5f, caster.posX, caster.posY, caster.posZ, world);
			targets.removeIf(target -> !AllyDesignationSystem.isValidTarget(caster, target));
			Vec3d origin = new Vec3d(caster.posX, caster.posY, caster.posZ);
			// Sort by distance from the origin for consistency in ordering for spells with a limit
			targets.sort(Comparator.comparingDouble(e -> e.getDistanceSq(origin.x, origin.y, origin.z)));
			for (EntityLivingBase target2 : targets) {
				affectEntity(caster, target2, modifiers, element);
			}
		}
		return true;
	}

	boolean affectEntity(@Nullable EntityLivingBase caster, EntityLivingBase target, SpellModifiers modifiers, Element element) {
		WarlockElementalSpellEffects.affectEntity(target, element, caster, modifiers, true);
		return true;
	}

	@Override
	public ItemWizardArmour.ArmourClass getArmourClass() {
		return ItemWizardArmour.ArmourClass.WARLOCK;
	}

	public boolean applicableForItem(Item item) {
		return item == ASItems.forbidden_tome;
	}

	@Override
	public boolean canBeCastBy(TileEntityDispenser dispenser) {
		return false;
	}

	@Override
	public boolean canBeCastBy(EntityLiving npc, boolean override) {
		return canBeCastByClassNPC(npc);
	}

}
