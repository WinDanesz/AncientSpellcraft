package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.item.ItemBattlemageSword;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.util.AllyDesignationSystem;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.GeometryUtils;
import electroblob.wizardry.util.MagicDamage;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class RunewordImplode extends Runeword {

	protected static final double Y_OFFSET = 0.25;

	public RunewordImplode() {
		super("runeword_implode", SpellActions.POINT_UP, false);
		addProperties(EFFECT_RADIUS, DAMAGE);
	}

	@Override
	public boolean cast(World world, EntityPlayer caster, EnumHand hand, int ticksInUse, SpellModifiers modifiers) {
		if (caster.getHeldItem(hand).getItem() instanceof ItemBattlemageSword) {
			float radius = getProperty(EFFECT_RADIUS).floatValue() * modifiers.get(WizardryItems.blast_upgrade);

			for (EntityLivingBase target : EntityUtils.getEntitiesWithinRadius(radius, caster.posX, caster.posY, caster.posZ, world, EntityLivingBase.class)) {
				if (target == caster || target instanceof EntityLivingBase && AllyDesignationSystem.isAllied(caster, (EntityLivingBase) target)) {
					continue;
				}
				Vec3d origin = new Vec3d(caster.posX, caster.posY, caster.posZ);
				Vec3d target2 = GeometryUtils.getCentre(target);
				Vec3d vec = target2.subtract(origin).normalize();
				Vec3d velocity = vec.scale(0.5f);
				if (target instanceof EntityLivingBase) {
					double ax1 = (-velocity.x - target.motionX) * 4.4;
					double ay1 = (-velocity.y - target.motionY) * 4.4;
					double az1 = (-velocity.z - target.motionZ) * 4.4;
					target.addVelocity(ax1, ay1, az1);
					// Player motion is handled on that player's client so needs packets
					if (target instanceof EntityPlayerMP) {
						((EntityPlayerMP) target).connection.sendPacket(new SPacketEntityVelocity(target));
					}
				}
				if (!world.isRemote) {
					EntityUtils.attackEntityWithoutKnockback(target,
							MagicDamage.causeDirectMagicDamage(caster, MagicDamage.DamageType.MAGIC), getProperty(DAMAGE).floatValue() * modifiers.get(SpellModifiers.POTENCY));
				} else {
					for (int i = 0; i < 20; i++) {
						ParticleBuilder.create(ParticleBuilder.Type.DUST).scale(0.3f).pos(target.posX - 0.5 + world.rand.nextFloat(), target.posY + target.getEyeHeight()  - 0.5 + world.rand.nextFloat(), target.posZ - 0.5 + world.rand.nextFloat()).
						vel(velocity.x * -3, velocity.y * -3, velocity.z * -3).scale(1.02f).time(15).spawn(world);
					}
				}
			}

			if (world.isRemote) {
				ParticleBuilder.create(ParticleBuilder.Type.SPHERE).entity(caster).pos(0, caster.getEyeHeight() + 0.3f, 0).scale(2f).spawn(world);
			}
			return true;
		}

		return false;
	}

}