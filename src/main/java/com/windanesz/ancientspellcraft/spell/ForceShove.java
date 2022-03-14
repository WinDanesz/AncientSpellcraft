package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftItems;
import electroblob.wizardry.Wizardry;
import electroblob.wizardry.block.BlockReceptacle;
import electroblob.wizardry.constants.Element;
import electroblob.wizardry.item.ItemArtefact;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.spell.SpellRay;
import electroblob.wizardry.util.AllyDesignationSystem;
import electroblob.wizardry.util.MagicDamage;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

public class ForceShove extends SpellRay {

	public static final String REPULSION_VELOCITY = "repulsion_velocity";

	public ForceShove(){
		super(AncientSpellcraft.MODID,"force_shove", SpellActions.POINT, false);
		this.soundValues(0.8f, 0.7f, 0.2f);
		addProperties(REPULSION_VELOCITY, DAMAGE);
	}

	@Override
	protected boolean onEntityHit(World world, Entity target, Vec3d hit, EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers){

		if(target instanceof EntityPlayer && ((caster instanceof EntityPlayer && !Wizardry.settings.playersMoveEachOther)
				|| ItemArtefact.isArtefactActive((EntityPlayer)target, WizardryItems.amulet_anchoring))){

			if(!world.isRemote && caster instanceof EntityPlayer) ((EntityPlayer)caster).sendStatusMessage(
					new TextComponentTranslation("spell.resist", target.getName(), this.getNameForTranslationFormatted()), true);
			return false;
		}

		// Left as EntityLivingBase because why not be able to move armour stands around?
		if(target instanceof EntityLivingBase && !AllyDesignationSystem.isAllied(caster, (EntityLivingBase) target)){
			
			Vec3d vec = target.getPositionEyes(1).subtract(origin).normalize();

			if(!world.isRemote){

				float velocity = getProperty(REPULSION_VELOCITY).floatValue() * modifiers.get(SpellModifiers.POTENCY);

				target.motionX = vec.x * velocity;
				target.motionY = vec.y * velocity + 0.4;
				target.motionZ = vec.z * velocity;

				// Player motion is handled on that player's client so needs packets
				if(target instanceof EntityPlayerMP){
					((EntityPlayerMP)target).connection.sendPacket(new SPacketEntityVelocity(target));
				}

				double motionX = target.motionX;
				double motionY = target.motionY;
				double motionZ = target.motionZ;
				target.attackEntityFrom(MagicDamage.causeDirectMagicDamage(caster, MagicDamage.DamageType.FORCE),
						getProperty(DAMAGE).floatValue() * modifiers.get(SpellModifiers.POTENCY));
				target.motionX = motionX;
				target.motionY = motionY;
				target.motionZ = motionZ;
			}

			if(world.isRemote){
				
				double distance = target.getDistance(origin.x, origin.y, origin.z);
				
				int[] colours = BlockReceptacle.PARTICLE_COLOURS.get(Element.SORCERY);
				for(int i = 0; i < 25; i++){
					double x = origin.x + world.rand.nextDouble() - 0.3 + vec.x * distance * 0.3;
					double y = origin.y + world.rand.nextDouble() - 0.3 + vec.y * distance * 0.3;
					double z = origin.z + world.rand.nextDouble() - 0.3 + vec.z * distance * 0.3;

					ParticleBuilder.create(ParticleBuilder.Type.DUST, world.rand,x, y, z, 1, false).scale(world.rand.nextFloat() * 4)
							.clr(colours[1]).fade(colours[2]).time(10).vel(vec.x * 0.8, vec.y * 0.8,vec.z * 0.8).spawn(world);

					//world.spawnParticle(EnumParticleTypes.CLOUD, x, y, z, vec.x, vec.y, vec.z);
				}
			}
			
			return true;
		}
		
		return false;
	}

	@Override
	protected boolean onBlockHit(World world, BlockPos pos, EnumFacing side, Vec3d hit, EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers){
		return false;
	}

	@Override
	protected boolean onMiss(World world, EntityLivingBase caster, Vec3d origin, Vec3d direction, int ticksInUse, SpellModifiers modifiers){
		return false;
	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == AncientSpellcraftItems.ancient_spellcraft_spell_book || item == AncientSpellcraftItems.ancient_spellcraft_scroll;
	}
}
