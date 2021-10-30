package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftItems;
import electroblob.wizardry.Wizardry;
import electroblob.wizardry.item.ItemArtefact;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.spell.SpellRay;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import java.util.List;

public class FistOfWind extends SpellRay {

	public static final String REPULSION_VELOCITY = "repulsion_velocity";

	public FistOfWind() {
		super(AncientSpellcraft.MODID, "fist_of_wind", SpellActions.POINT, false);
		this.soundValues(0.8f, 0.7f, 0.2f);
		addProperties(REPULSION_VELOCITY, BLAST_RADIUS);
	}

	@Override
	protected boolean onEntityHit(World world, Entity originalTarget, Vec3d hit, EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {

		boolean result = false;
		float radius = (float) Math.min(1.0, modifiers.get(BLAST_RADIUS) * 0.7) * getProperty(BLAST_RADIUS).intValue();
		List<EntityLivingBase> targets = EntityUtils.getEntitiesWithinRadius(radius, originalTarget.posX, originalTarget.posY, originalTarget.posZ, world, EntityLivingBase.class);

		for (EntityLivingBase target : targets) {

			if (target == caster) {continue;}
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

					float velocity = getProperty(REPULSION_VELOCITY).floatValue() * modifiers.get(SpellModifiers.POTENCY);

					target.motionX = vec.x * velocity;
					target.motionY = vec.y * velocity + 1;
					target.motionZ = vec.z * velocity;

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

				result = true;
			}
		}
		return result;
	}

	@Override
	protected boolean onBlockHit(World world, BlockPos pos, EnumFacing side, Vec3d hit, EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {
		return false;
	}

	@Override
	protected boolean onMiss(World world, EntityLivingBase caster, Vec3d origin, Vec3d direction, int ticksInUse, SpellModifiers modifiers) {
		return false;
	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == AncientSpellcraftItems.ancient_spellcraft_spell_book || item == AncientSpellcraftItems.ancient_spellcraft_scroll;
	}
}
