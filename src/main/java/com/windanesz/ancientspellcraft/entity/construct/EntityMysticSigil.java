package com.windanesz.ancientspellcraft.entity.construct;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.entity.living.EntitySpellCaster;
import com.windanesz.ancientspellcraft.registry.ASSounds;
import com.windanesz.ancientspellcraft.registry.ASSpells;
import electroblob.wizardry.constants.SpellType;
import electroblob.wizardry.entity.construct.EntityMagicConstruct;
import electroblob.wizardry.event.SpellCastEvent;
import electroblob.wizardry.packet.PacketCastSpell;
import electroblob.wizardry.packet.WizardryPacketHandler;
import electroblob.wizardry.registry.WizardrySounds;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.spell.SpellBuff;
import electroblob.wizardry.util.AllyDesignationSystem;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;

@Mod.EventBusSubscriber
public class EntityMysticSigil extends EntityMagicConstruct {

	private static final DataParameter<String> ABSORBED_SPELL_NAME = EntityDataManager.createKey(EntityMysticSigil.class, DataSerializers.STRING);

	private Spell absorbedSpell = null;
	private SpellModifiers modifiers = new SpellModifiers();
	private int cooldownTicks = 0;

	public EntityMysticSigil(World world) {
		super(world);
		this.height = 0.2f;
		this.width = 1.0f;
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		this.dataManager.register(ABSORBED_SPELL_NAME, "");
	}

	public Spell getAbsorbedSpell() {
		return absorbedSpell;
	}

	public void absorbSpell(Spell spell, SpellModifiers modifiers) {
		this.absorbedSpell = spell;
		this.modifiers = modifiers != null ? modifiers : new SpellModifiers();
		this.cooldownTicks = 60;
		this.dataManager.set(ABSORBED_SPELL_NAME, spell.getRegistryName().toString());
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		if (this.world.isRemote) {
			String spellName = this.dataManager.get(ABSORBED_SPELL_NAME);
			if (spellName == null || spellName.isEmpty()) {
				if (this.rand.nextInt(2) == 0) {
					double swirlRadius = 0.55 + rand.nextDouble() * 0.3;
					ParticleBuilder.create(ParticleBuilder.Type.SPARKLE, rand, this.posX, this.posY + 0.05, this.posZ, 0.01, false)
							.pos(this.posX, this.posY + 0.05 + rand.nextDouble() * 0.2, this.posZ)
							.spin(swirlRadius, 0.08 + rand.nextDouble() * 0.04)
							.vel(0, 0.015 + rand.nextDouble() * 0.01, 0)
							.time(20 + rand.nextInt(15))
							.clr(0.3f, 0.85f + rand.nextFloat() * 0.15f, 0.7f + rand.nextFloat() * 0.3f)
							.spawn(world);
				}
			} else {
				if (this.rand.nextInt(10) == 0) {
					double radius = 0.5 + rand.nextDouble() * 0.3;
					float angle = rand.nextFloat() * (float) Math.PI * 2;
					ParticleBuilder.create(ParticleBuilder.Type.SPARKLE, rand, this.posX + radius * MathHelper.cos(angle), this.posY + 0.1,
							this.posZ + radius * MathHelper.sin(angle), 0.05, true).clr(0.3f, 0.9f, 1.0f).spawn(world);
				}
			}
			return;
		}

		if (this.absorbedSpell == null) {
			String spellName = this.dataManager.get(ABSORBED_SPELL_NAME);
			if (spellName != null && !spellName.isEmpty()) {
				Spell s = Spell.registry.getValue(new ResourceLocation(spellName));
				if (s != null) {
					this.absorbedSpell = s;
				}
			}
		}

		if (this.cooldownTicks > 0) {
			this.cooldownTicks--;
		}

		if (this.absorbedSpell != null && this.cooldownTicks <= 0) {
			List<EntityLivingBase> targets = EntityUtils.getLivingWithinRadius(width / 2.0f, this.posX, this.posY, this.posZ, this.world);
			for (EntityLivingBase target : targets) {
				if (target.isDead || target.getHealth() <= 0) continue;

				boolean isAlly = (this.getCaster() != null && (this.getCaster() == target || AllyDesignationSystem.isAllied(this.getCaster(), target)));
				boolean beneficial = isBeneficialSpell(this.absorbedSpell);

				if ((beneficial && isAlly) || (!beneficial && !isAlly)) {
					triggerSigil(target);
					break;
				}
			}
		}
	}

	public static boolean isBeneficialSpell(Spell spell) {
		if (spell == null) return false;
		if (spell instanceof SpellBuff || spell.getType() == SpellType.BUFF || spell.getType() == SpellType.DEFENCE) {
			return true;
		}
		String name = spell.getRegistryName() != null ? spell.getRegistryName().getPath() : "";
		return name.contains("heal") || name.contains("regrowth") || name.contains("ward") || name.contains("cure");
	}

	private void triggerSigil(EntityLivingBase target) {
		EntitySpellCaster spellCaster = new EntitySpellCaster(world);
		spellCaster.setPosition(this.posX, this.posY + 0.1, this.posZ);
		if (this.getCaster() != null) {
			spellCaster.setOwnerId(this.getCaster().getUniqueID());
		}
		double dx = target.posX - this.posX;
		double dy = (target.posY + target.getEyeHeight() / 2.0) - (this.posY + 0.1);
		double dz = target.posZ - this.posZ;
		double dist = MathHelper.sqrt(dx * dx + dz * dz);
		float yaw = (float)(MathHelper.atan2(dz, dx) * (180D / Math.PI)) - 90F;
		float pitch = (float)-(MathHelper.atan2(dy, dist) * (180D / Math.PI));
		spellCaster.setLocationAndAngles(this.posX, this.posY + 0.1, this.posZ, yaw, pitch);
		spellCaster.rotationYawHead = yaw;
		spellCaster.setLifetime(20);
		world.spawnEntity(spellCaster);

		if (!MinecraftForge.EVENT_BUS.post(new SpellCastEvent.Pre(SpellCastEvent.Source.OTHER, this.absorbedSpell, spellCaster, this.modifiers))) {
			if (this.absorbedSpell.cast(world, spellCaster, EnumHand.MAIN_HAND, 0, target, this.modifiers)) {
				MinecraftForge.EVENT_BUS.post(new SpellCastEvent.Post(SpellCastEvent.Source.OTHER, this.absorbedSpell, world, (int)this.posX, (int)this.posY, (int)this.posZ, EnumFacing.UP, this.modifiers));
				if (this.absorbedSpell.requiresPacket()) {
					WizardryPacketHandler.net.sendToDimension(new PacketCastSpell.Message(spellCaster.getEntityId(), EnumHand.MAIN_HAND, this.absorbedSpell, this.modifiers), world.provider.getDimension());
				}
			}
		}

		world.playSound(null, this.posX, this.posY, this.posZ, ASSounds.DISPEL_ENTITY, SoundCategory.NEUTRAL, 1.0f, 1.0f);
		this.setDead();
	}

	@Override
	public boolean canRenderOnFire() {
		return false;
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);
		compound.setInteger("cooldown_ticks", this.cooldownTicks);
		if (this.absorbedSpell != null && this.absorbedSpell.getRegistryName() != null) {
			compound.setString("absorbed_spell", this.absorbedSpell.getRegistryName().toString());
		}
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound compound) {
		super.readEntityFromNBT(compound);
		this.cooldownTicks = compound.getInteger("cooldown_ticks");
		if (compound.hasKey("absorbed_spell")) {
			Spell s = Spell.registry.getValue(new ResourceLocation(compound.getString("absorbed_spell")));
			if (s != null) {
				this.absorbedSpell = s;
				this.dataManager.set(ABSORBED_SPELL_NAME, compound.getString("absorbed_spell"));
			}
		}
	}

	@SubscribeEvent
	public static void onSpellCast(SpellCastEvent.Pre event) {
		if (event.getWorld() == null || event.getWorld().isRemote) return;
		Spell spell = event.getSpell();
		if (spell == null || spell == ASSpells.mystic_sigil) return;

		EntityLivingBase caster = event.getCaster();
		if (caster == null) return;

		List<EntityMysticSigil> sigils = EntityUtils.getEntitiesWithinRadius(4.0, caster.posX, caster.posY, caster.posZ, event.getWorld(), EntityMysticSigil.class);
		for (EntityMysticSigil sigil : sigils) {
			if (sigil.isEntityAlive() && sigil.getAbsorbedSpell() == null) {
				if (sigil.getCaster() == null || sigil.getCaster() == caster || AllyDesignationSystem.isAllied(sigil.getCaster(), caster)) {
					boolean close = sigil.getDistance(caster) <= 3.5f;
					if (!close) {
						Vec3d eyePos = caster.getPositionEyes(1.0f);
						Vec3d lookVec = caster.getLook(1.0f);
						AxisAlignedBB box = sigil.getEntityBoundingBox().grow(1.0, 1.0, 1.0);
						if (box.calculateIntercept(eyePos, eyePos.add(lookVec.scale(12.0))) != null) {
							close = true;
						}
					}
					if (close) {
						sigil.absorbSpell(spell, event.getModifiers());
						event.setCanceled(true);
						sigil.world.playSound(null, sigil.posX, sigil.posY, sigil.posZ, ASSounds.DISPEL_ENTITY, SoundCategory.NEUTRAL, 1.0f, 1.2f);
						break;
					}
				}
			}
		}
	}
}
