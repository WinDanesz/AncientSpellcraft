package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.Settings;
import com.windanesz.ancientspellcraft.registry.ASBlocks;
import com.windanesz.ancientspellcraft.registry.ASItems;
import com.windanesz.ancientspellcraft.tileentity.TileEntityLightningBlock;
import electroblob.wizardry.Wizardry;
import electroblob.wizardry.data.IVariable;
import electroblob.wizardry.data.Persistence;
import electroblob.wizardry.data.WizardData;
import electroblob.wizardry.item.ItemArtefact;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.registry.Spells;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.MagicDamage;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.ParticleBuilder.Type;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber
public class ChargeAS extends Spell implements IOverrideSpell {

	public static final IVariable<Integer> CHARGE_TIME = new IVariable.Variable<Integer>(Persistence.NEVER).withTicker(ChargeAS::update);
	public static final IVariable<SpellModifiers> CHARGE_MODIFIERS = new IVariable.Variable<>(Persistence.NEVER);

	public static final String CHARGE_SPEED = "charge_speed";
	public static final String KNOCKBACK_STRENGTH = "knockback_strength";

	private static final double EXTRA_HIT_MARGIN = 1;

	public ChargeAS(){
		super(Wizardry.MODID,"charge", SpellActions.POINT, false);
		addProperties(CHARGE_SPEED, DURATION, DAMAGE, KNOCKBACK_STRENGTH);
		this.soundValues(0.6f, 1, 0);

		// must use the original networkID of the base spell
		handleNetworkIDChange(this, Settings.spellCompatSettings.chargeSpellNetworkID);
	}

	@Override
	public boolean cast(World world, EntityPlayer caster, EnumHand hand, int ticksInUse, SpellModifiers modifiers){

		WizardData.get(caster).setVariable(CHARGE_TIME, (int)(getProperty(DURATION).floatValue()
				* modifiers.get(WizardryItems.duration_upgrade)));

		WizardData.get(caster).setVariable(CHARGE_MODIFIERS, modifiers);

		if(world.isRemote) world.spawnParticle(EnumParticleTypes.EXPLOSION_LARGE, caster.posX, caster.posY + caster.height/2, caster.posZ, 0, 0, 0);

		this.playSound(world, caster, ticksInUse, -1, modifiers);

		return true;
	}

	private static int update(EntityPlayer player, Integer chargeTime){

		if(chargeTime == null) chargeTime = 0;

		if(chargeTime > 0){

			if (!player.world.isRemote && ItemArtefact.isArtefactActive(player, ASItems.ring_charge)) {
				if (player.world.isAirBlock(player.getPosition())) {
					player.world.setBlockState(player.getPosition(), ASBlocks.lightning_block.getDefaultState());
					TileEntity tile = player.world.getTileEntity(player.getPosition());
					if (tile instanceof TileEntityLightningBlock) {
						((TileEntityLightningBlock) tile).setLifetime(120);
						((TileEntityLightningBlock) tile).setCaster(player);
					}
				}
			}

			SpellModifiers modifiers = WizardData.get(player).getVariable(CHARGE_MODIFIERS);
			if(modifiers == null) modifiers = new SpellModifiers();

			Vec3d look = player.getLookVec();

			float speed = Spells.charge.getProperty(ChargeAS.CHARGE_SPEED).floatValue() * modifiers.get(WizardryItems.range_upgrade);

			player.motionX = look.x * speed;
			player.motionZ = look.z * speed;

			if(player.world.isRemote){
				for(int i = 0; i < 5; i++){
					ParticleBuilder.create(Type.SPARK, player).spawn(player.world);
				}
			}

			List<EntityLivingBase> collided = player.world.getEntitiesWithinAABB(EntityLivingBase.class, player.getEntityBoundingBox().grow(EXTRA_HIT_MARGIN));

			collided.remove(player);

			float damage = Spells.charge.getProperty(DAMAGE).floatValue() * modifiers.get(SpellModifiers.POTENCY);
			float knockback = Spells.charge.getProperty(KNOCKBACK_STRENGTH).floatValue();

			collided.forEach(e -> e.attackEntityFrom(MagicDamage.causeDirectMagicDamage(player, MagicDamage.DamageType.SHOCK), damage));
			collided.forEach(e -> e.addVelocity(player.motionX * knockback, player.motionY * knockback + 0.3f, player.motionZ * knockback));

			if(player.world.isRemote) player.world.spawnParticle(EnumParticleTypes.EXPLOSION_LARGE,
					player.posX + player.motionX, player.posY + player.height/2, player.posZ + player.motionZ, 0, 0, 0);

			if(collided.isEmpty()) chargeTime--;
			else{
				EntityUtils.playSoundAtPlayer(player, SoundEvents.ENTITY_GENERIC_HURT, 1, 1);
				chargeTime = 0;
			}
		}

		return chargeTime;
	}
}
