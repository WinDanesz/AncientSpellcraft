package com.windanesz.ancientspellcraft.entity.ai;

import com.windanesz.ancientspellcraft.item.ItemSageTome;
import com.windanesz.ancientspellcraft.registry.ASItems;
import com.windanesz.ancientspellcraft.util.SpellcastUtils;
import electroblob.wizardry.constants.Constants;
import electroblob.wizardry.constants.SpellType;
import electroblob.wizardry.entity.living.ISpellCaster;
import electroblob.wizardry.entity.living.ISummonedCreature;
import electroblob.wizardry.event.SpellCastEvent;
import electroblob.wizardry.event.SpellCastEvent.Source;
import electroblob.wizardry.item.ItemScroll;
import electroblob.wizardry.item.ItemWand;
import electroblob.wizardry.packet.PacketNPCCastSpell;
import electroblob.wizardry.packet.WizardryPacketHandler;
import electroblob.wizardry.registry.Spells;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.spell.SpellBuff;
import electroblob.wizardry.spell.SpellMinion;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.SpellModifiers;
import electroblob.wizardry.util.WandHelper;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import java.util.ArrayList;
import java.util.List;

import static electroblob.wizardry.spell.SpellMinion.SUMMON_RADIUS;

public class EntityAIAttackSpellWithCost<T extends EntityLiving & ISpellCaster> extends EntityAIBase {

	/**
	 * The entity the AI instance has been applied to. Thanks to type parameters, methods from both EntityLiving and
	 * ISummonedCreature may be invoked on this field.
	 */
	private final T attacker;
	private EntityLivingBase target;
	/**
	 * Decremented each tick while greater than 0. When a spell is cast, this is set to that spell's cooldown plus the
	 * base cooldown.
	 */
	private int cooldown;
	/**
	 * The number of ticks between the entity finding a new target and when it first starts attacking, and also the
	 * amount that is added to the spell's cooldown between casting spells.
	 */
	private final int baseCooldown;
	/**
	 * Decremented each tick while greater than 0. When a continuous spell is first cast, this is set to the value of
	 * {@link EntityAIAttackSpellWithCost#continuousSpellDuration}.
	 */
	// I think that in this case this is only necessary on the server side. If any inconsistent behaviour
	// occurs, look into syncing this as well.
	private int continuousSpellTimer;
	/**
	 * If true, buff spells will be casted on the owner (if present), instead of this entity.
	 */
	private boolean proxyBuffs;
	/**
	 * The number of ticks that continuous spells will be cast for before cooling down.
	 */
	private final int continuousSpellDuration;
	/**
	 * The speed that the entity should move when attacking. Only used when passed into the navigator.
	 */
	private final double speed;
	private int seeTime;
	private final float maxAttackDistance;

	/**
	 * Creates a new spell attack AI with the given parameters.
	 *
	 * @param attacker                The entity that that uses this AI.
	 * @param speed                   The speed that the entity should move when attacking. Only used when passed into the navigator.
	 * @param maxDistance             The maximum distance the entity should be from its target.
	 * @param baseCooldown            The number of ticks between the entity finding a new target and when it first starts
	 *                                attacking, and also the amount that is added to the cooldown of the spell that has just been cast.
	 * @param continuousSpellDuration The number of ticks that continuous spells will be cast for before cooling down.
	 */
	public EntityAIAttackSpellWithCost(T attacker, double speed, float maxDistance, int baseCooldown, int continuousSpellDuration, boolean proxyBuffs) {
		this.cooldown = -1;
		this.attacker = attacker;
		this.baseCooldown = baseCooldown;
		this.continuousSpellDuration = continuousSpellDuration;
		this.speed = speed;
		this.maxAttackDistance = maxDistance * maxDistance;
		this.proxyBuffs = proxyBuffs;
		this.setMutexBits(3);
	}

	@Override
	public boolean shouldExecute() {

		EntityLivingBase entitylivingbase = this.attacker.getAttackTarget();

		if (entitylivingbase == null) {
			return false;
		} else {
			this.target = entitylivingbase;
			return true;
		}
	}

	@Override
	public boolean shouldContinueExecuting() {
		return this.shouldExecute() || !this.attacker.getNavigator().noPath();
	}

	@Override
	public void resetTask() {
		this.target = null;
		this.seeTime = 0;
		this.cooldown = -1;
		this.setContinuousSpellAndNotify(Spells.none, new SpellModifiers());
		this.continuousSpellTimer = 0;
	}

	private void setContinuousSpellAndNotify(Spell spell, SpellModifiers modifiers) {
		attacker.setContinuousSpell(spell);
		WizardryPacketHandler.net.sendToAllAround(
				new PacketNPCCastSpell.Message(attacker.getEntityId(), target == null ? -1 : target.getEntityId(),
						EnumHand.MAIN_HAND, spell, modifiers),
				// Particles are usually only visible from 16 blocks away, so 128 is more than far enough.
				// TODO: Why is this one a 128 block radius, whilst the other one is all in dimension?
				new TargetPoint(attacker.dimension, attacker.posX, attacker.posY, attacker.posZ, 128));
	}

	@Override
	public void updateTask() {

		// Only executed server side.

		double distanceSq = this.attacker.getDistanceSq(this.target.posX, this.target.posY,
				this.target.posZ);
		boolean targetIsVisible = this.attacker.getEntitySenses().canSee(this.target);

		if (targetIsVisible) {
			++this.seeTime;
		} else {
			this.seeTime = 0;
		}

		if (distanceSq <= (double) this.maxAttackDistance && this.seeTime >= 20) {
			this.attacker.getNavigator().clearPath();
		} else {
			this.attacker.getNavigator().tryMoveToEntityLiving(this.target, this.speed);
		}

		this.attacker.getLookHelper().setLookPositionWithEntity(this.target, 30.0F, 30.0F);

		if (this.continuousSpellTimer > 0) {

			if (this.target == null || this.target.isDead) {
				resetTask();
			}

			this.continuousSpellTimer--;

			// If the target goes out of range or out of sight...
			if (distanceSq > (double) this.maxAttackDistance || !targetIsVisible
					// ...or the spell is cancelled via events...
					|| MinecraftForge.EVENT_BUS
					.post(new SpellCastEvent.Tick(Source.NPC, attacker.getContinuousSpell(), attacker,
							attacker.getModifiers(), this.continuousSpellDuration - this.continuousSpellTimer))
					// ...or the spell no longer succeeds...
					|| !attacker.getContinuousSpell().cast(attacker.world, attacker, EnumHand.MAIN_HAND,
					this.continuousSpellDuration - this.continuousSpellTimer, target, attacker.getModifiers())
					// ...or the time has elapsed...
					|| this.continuousSpellTimer == 0) {

				// ...reset the continuous spell timer and start the cooldown.
				this.continuousSpellTimer = 0;
				this.cooldown = attacker.getContinuousSpell().getCooldown() + this.baseCooldown;
				setContinuousSpellAndNotify(Spells.none, new SpellModifiers());
				return;

			} else if (this.continuousSpellDuration - this.continuousSpellTimer == 1) {
				// On the first tick, if the spell did succeed, fire SpellCastEvent.Post.
				MinecraftForge.EVENT_BUS.post(new SpellCastEvent.Post(Source.NPC, attacker.getContinuousSpell(),
						attacker, attacker.getModifiers()));
			}

		} else if (--this.cooldown == 0) {

			if (distanceSq > (double) this.maxAttackDistance || !targetIsVisible) {
				return;
			}

			double dx = target.posX - attacker.posX;
			double dz = target.posZ - attacker.posZ;

			List<Spell> spells = new ArrayList<Spell>(attacker.getSpells());
			spells.removeIf(spell -> spell == Spells.none);

			if (spells.size() > 0) {

				if (!attacker.world.isRemote) {

					// New way of choosing a spell; keeps trying until one works or all have been tried

					Spell spell;

					while (!spells.isEmpty()) {

						spell = spells.get(attacker.world.rand.nextInt(spells.size()));

						if (this.attacker.getHeldItemMainhand().getItem() instanceof ItemWand) {
							ItemStack wandStack = this.attacker.getHeldItemMainhand();

							int requiredMana = spell.getCost();
							int currentMana = ((ItemWand) wandStack.getItem()).getMana(wandStack);
							if (currentMana < requiredMana) {
								// not enough mana to cast this
								spells.remove(spell);
							}
						}

						// execute buffs as the owner
						//noinspection ConstantConditions
						if (proxyBuffs && (spell instanceof SpellBuff || spell.getType() == SpellType.BUFF || spell.getType() == SpellType.DEFENCE )
								&& (attacker instanceof ISummonedCreature && ((ISummonedCreature) attacker).getCaster() != null
								&& attacker.getDistance(((ISummonedCreature) attacker).getCaster()) < 20)) {

							if (((ISummonedCreature) attacker).getCaster() instanceof EntityPlayer) {
								EntityPlayer player = (EntityPlayer) ((ISummonedCreature) attacker).getCaster();
								if (SpellcastUtils.tryCastSpellAsPlayer(player, spell, EnumHand.MAIN_HAND, Source.WAND, new SpellModifiers(), 60)) {
									this.cooldown = this.baseCooldown + spell.getCooldown() * 2;
								}
							} else if (((ISummonedCreature) attacker).getCaster() instanceof EntityLiving) {
								EntityLiving living = (EntityLiving) ((ISummonedCreature) attacker).getCaster();
								if (SpellcastUtils.tryCastSpellAsMob(living, spell, null)) {
									this.cooldown = this.baseCooldown + spell.getCooldown() * 2;
								}
							}
						}

						SpellModifiers modifiers = attacker.getModifiers();

						if (spell != null && attemptCastSpell(spell, modifiers)) {
							// The spell worked, so we're done!
							attacker.rotationYaw = (float) (Math.atan2(dz, dx) * 180.0D / Math.PI) - 90.0F;

							// fixing ownership inheritance
							if (spell instanceof SpellMinion && ((ISummonedCreature) this.attacker).getCaster() != null) {
								double radius = spell.getProperty(SUMMON_RADIUS).floatValue() + 1;
								for (EntityLivingBase entity : EntityUtils.getEntitiesWithinRadius(radius, this.attacker.posX, this.attacker.posY,
										this.attacker.posZ, this.attacker.world, EntityLivingBase.class)) {
									if (entity instanceof ISummonedCreature && ((ISummonedCreature) entity).getOwner() == this.attacker) {
										((ISummonedCreature ) entity).setCaster(((ISummonedCreature) this.attacker).getCaster());
									}
								}

							}
							return;
						} else {
							spells.remove(spell);
						}
					}
				}
			}

		} else if (this.cooldown < 0) {
			// This should only be reached when the entity first starts attacking. Stops it attacking instantly.
			this.cooldown = this.baseCooldown;
		}
	}

	/**
	 * Attempts to cast the given spell (including event firing) and returns true if it succeeded.
	 */
	private boolean attemptCastSpell(Spell spell, SpellModifiers modifiers) {

		// If anything stops the spell working at this point, nothing else happens.
		if (MinecraftForge.EVENT_BUS.post(new SpellCastEvent.Pre(Source.NPC, spell, attacker, modifiers))) {
			return false;
		}

		// This is only called when spell casting starts so ticksInUse is always zero
		if (spell.cast(attacker.world, attacker, EnumHand.MAIN_HAND, 0, target, modifiers)) {

			if (spell.isContinuous) {
				// -1 because the spell has been cast once already!
				this.continuousSpellTimer = this.continuousSpellDuration - 1;
				setContinuousSpellAndNotify(spell, modifiers);

			} else {

				MinecraftForge.EVENT_BUS.post(new SpellCastEvent.Post(Source.NPC, spell, attacker, modifiers));

				// For now, the cooldown is just added to the constant base cooldown. I think this
				// is a reasonable way of doing things; it's certainly better than before.
				this.cooldown = this.baseCooldown + spell.getCooldown() * 2;

				if (spell.requiresPacket()) {
					// Sends a packet to all players in dimension to tell them to spawn particles.
					IMessage msg = new PacketNPCCastSpell.Message(attacker.getEntityId(), target.getEntityId(),
							EnumHand.MAIN_HAND, spell, modifiers);
					WizardryPacketHandler.net.sendToDimension(msg, attacker.world.provider.getDimension());
				}
				if (!attacker.world.isRemote) {
					if (attacker.getHeldItemMainhand().getItem() instanceof ItemScroll) {
						attacker.getHeldItemMainhand().shrink(1);
					}
				}
			}

			return true;
		}

		return false;
	}
}
