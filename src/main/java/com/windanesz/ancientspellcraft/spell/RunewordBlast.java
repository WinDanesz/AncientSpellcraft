package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.item.ItemBattlemageSword;
import electroblob.wizardry.Wizardry;
import electroblob.wizardry.item.ItemArtefact;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

public class RunewordBlast extends Runeword {

	protected static final double Y_OFFSET = 0.25;

	public RunewordBlast() {
		super("runeword_blast", SpellActions.IMBUE, false);
		addProperties(CHARGES);
	}

	@Override
	public boolean cast(World world, EntityPlayer caster, EnumHand hand, int ticksInUse, SpellModifiers modifiers) {
		if (caster.getHeldItem(hand).getItem() instanceof ItemBattlemageSword) {
			ItemBattlemageSword.setActiveRuneword(caster.getHeldItem(hand), this, getChargeCount(caster));

			if (world.isRemote) {

				ParticleBuilder.create(ParticleBuilder.Type.BUFF).entity(caster).clr(1,1,1).spawn(world);
			}
			return true;

		}
		return false;
	}

	@Override
	public boolean onAboutToHitEntity(World world, EntityLivingBase caster, EntityLivingBase target, EnumHand hand, ItemStack sword, SpellModifiers modifiers, boolean charged) {
		Vec3d look = caster.getLookVec();
		Vec3d origin = new Vec3d(caster.posX, caster.posY + caster.getEyeHeight() - Y_OFFSET, caster.posZ);

		if (target instanceof EntityPlayer && ((caster instanceof EntityPlayer && !Wizardry.settings.playersMoveEachOther)
				|| ItemArtefact.isArtefactActive((EntityPlayer) target, WizardryItems.amulet_anchoring))) {

			if (!world.isRemote && caster instanceof EntityPlayer) {
				((EntityPlayer) caster).sendStatusMessage(
						new TextComponentTranslation("spell.resist", target.getName(), this.getNameForTranslationFormatted()), true);
			}
			return false;
		}

		// Left as EntityLivingBase because why not be able to move armour stands around?
		if (target != null) {

			Vec3d vec = target.getPositionEyes(1).subtract(origin).normalize();

			if (!world.isRemote) {

				float velocity = 1.5f;

				target.motionX = vec.x * (velocity * 3.5f) ;
				target.motionY = vec.y * velocity + 0.5;
				target.motionZ = vec.z * (velocity * 3.5f);

				// Player motion is handled on that player's client so needs packets
				if (target instanceof EntityPlayerMP) {
					((EntityPlayerMP) target).connection.sendPacket(new SPacketEntityVelocity(target));
				}
			}

			if (world.isRemote) {

				double distance = target.getDistance(origin.x, origin.y, origin.z);

				for (int i = 0; i < 10; i++) {
					double x = origin.x + world.rand.nextDouble() - 0.5 + vec.x * distance * 0.5;
					double y = origin.y + world.rand.nextDouble() - 0.5 + vec.y * distance * 0.5;
					double z = origin.z + world.rand.nextDouble() - 0.5 + vec.z * distance * 0.5;
					world.spawnParticle(EnumParticleTypes.CLOUD, x, y, z, vec.x, vec.y, vec.z);
				}
			}

			spendCharge(sword);
			return true;
		}
		return false;
	}
}
