package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftItems;
import electroblob.wizardry.Wizardry;
import electroblob.wizardry.constants.Tier;
import electroblob.wizardry.item.ItemArtefact;
import electroblob.wizardry.item.ItemWand;
import electroblob.wizardry.registry.WizardryAdvancementTriggers;
import electroblob.wizardry.registry.WizardrySounds;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.RayTracer;
import electroblob.wizardry.util.SpellModifiers;
import electroblob.wizardry.util.WandHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class Conduit extends Spell {

	private static int TRANSFER_AMOUNT = 2;
	private static String TRANSFER_EFFICIENCY = "transfer_efficiency";
	private static String TRANSFER_AMOUNT_PER_TICK = "transfer_amount_per_tick";

	public Conduit() {
		super(AncientSpellcraft.MODID, "conduit", EnumAction.BLOCK, true);
		addProperties(DURATION, TRANSFER_EFFICIENCY, TRANSFER_AMOUNT_PER_TICK);
	}

	@Override
	public boolean canBeCastBy(EntityLiving npc, boolean override) {
		return false;
	}

	@Override
	public boolean canBeCastBy(TileEntityDispenser dispenser) {
		return true;
	}

	@Override
	protected SoundEvent[] createSounds() {
		return createContinuousSpellSounds();
	}

	@Override
	protected void playSound(World world, EntityLivingBase entity, int ticksInUse, int duration, SpellModifiers modifiers, String... sounds) {
		this.playSoundLoop(world, entity, ticksInUse);
	}

	@Override
	protected void playSound(World world, double x, double y, double z, int ticksInUse, int duration, SpellModifiers modifiers, String... sounds) {
		this.playSoundLoop(world, x, y, z, ticksInUse, duration);
	}

	@Override
	public boolean cast(World world, EntityPlayer caster, EnumHand hand, int ticksInUse, SpellModifiers modifiers) {
		if (!(caster instanceof EntityPlayer))
			return false;
		performEffect(world, caster.getPositionEyes(0).subtract(0, 0.1, 0), caster, modifiers, ticksInUse);
		this.playSound(world, caster, ticksInUse, -1, modifiers);
		return true;
	}

	@Override
	public boolean cast(World world, EntityLiving caster, EnumHand hand, int ticksInUse, EntityLivingBase target, SpellModifiers modifiers) {
		return false;
	}

	@Override
	public boolean cast(World world, double x, double y, double z, EnumFacing direction, int ticksInUse, int duration, SpellModifiers modifiers) {
		return false;
	}

	private void performEffect(World world, Vec3d centre, @Nullable EntityLivingBase caster, SpellModifiers modifiers, int ticksInUse) {
		if (caster instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) caster;

			if (player.getHeldItemMainhand().getItem() instanceof ItemWand && (player.getHeldItemOffhand().getItem() instanceof ItemWand || ItemArtefact.isArtefactActive(player, AncientSpellcraftItems.ring_mana_transfer))) {
				// source wand is in the main hand, target is offhand
				ItemStack sourceWandStack = player.getHeldItemMainhand();
				ItemStack targetWandStack = player.getHeldItemOffhand();
				ItemWand sourceWand = (ItemWand) sourceWandStack.getItem();
				ItemWand targetWand = (ItemWand) targetWandStack.getItem();

				if (!Wizardry.settings.legacyWandLevelling && ItemArtefact.isArtefactActive(player, AncientSpellcraftItems.charm_progression_orb)) {

					if (targetWand.tier.level < Tier.MASTER.level) {
						int sourceProgression = WandHelper.getProgression(sourceWandStack);
						int targetProgression = WandHelper.getProgression(targetWandStack);
						int amount = 10;

						if (sourceProgression > 0) {

							// If the wand just gained enough progression to be upgraded...
							Tier nextTier = targetWand.tier.next();
							int excess = WandHelper.getProgression(targetWandStack) - nextTier.getProgression();
							if (excess >= 0 && excess < amount) {
								// ...display a message above the player's hotbar
								caster.playSound(WizardrySounds.ITEM_WAND_LEVELUP, 1.25f, 1);
								WizardryAdvancementTriggers.wand_levelup.triggerFor(player);
								if (!world.isRemote)
									caster.sendMessage(new TextComponentTranslation("item." + Wizardry.MODID + ":wand.levelup",
											targetWand.getItemStackDisplayName(targetWandStack), nextTier.getNameForTranslationFormatted()));
							} else {
								if (sourceProgression > 10 && sourceWand.getMana(sourceWandStack) > 10) {
									WandHelper.setProgression(targetWandStack, targetProgression + 10);
									WandHelper.setProgression(sourceWandStack, sourceProgression - 10);
									sourceWand.consumeMana(sourceWandStack, 10, player);
									if (world.isRemote) {
										spawnParticle(player, true);
									}
								}
							}
						}
					}

				} else if (ItemArtefact.isArtefactActive(player, AncientSpellcraftItems.ring_mana_transfer)) {
					Vec3d look = caster.getLookVec();
					double Y_OFFSET = 0.25;

					Vec3d origin = new Vec3d(caster.posX, caster.posY + caster.getEyeHeight() - Y_OFFSET, caster.posZ);
					shootSpell(world, origin, look, player, ticksInUse, modifiers);

				} else if (!sourceWand.isManaEmpty(sourceWandStack) && !targetWand.isManaFull(targetWandStack)) {

					int amount = getProperty(TRANSFER_AMOUNT_PER_TICK).intValue();
					sourceWand.consumeMana(sourceWandStack, amount, player);
					int newAmount = (int) (amount * getProperty(TRANSFER_EFFICIENCY).floatValue());
					sourceWand.rechargeMana(targetWandStack, newAmount);
					if (world.isRemote) {
						spawnParticle(player, true);
					}
				}

			} else if (!player.world.isRemote)
				player.sendStatusMessage(new TextComponentTranslation("spell.ancientspellcraft:conduit.no_wand"), true);

		}
	}

	private static void spawnParticle(EntityPlayer player, boolean progression) {
		double speed = (player.world.rand.nextBoolean() ? 1 : -1) * 0.1;// + 0.01 * rand.nextDouble();
		double radius = player.world.rand.nextDouble() * 2.0;
		float angle = player.world.rand.nextFloat() * (float) Math.PI * 2;
		ParticleBuilder.create(ParticleBuilder.Type.FLASH)
				.pos(player.posX + radius / 3 * MathHelper.cos(angle), player.posY, player.posZ + radius / 3 * MathHelper.sin(angle))
				.vel(0, 0.05, 0)
				.scale(0.7F)
				.time(48 + player.world.rand.nextInt(12))
				.spin(1.5, speed)
				.clr(121, 219, 9)
				.spawn(player.world);
	}

	/**
	 * Takes care of the shared stuff for the three casting methods. This is mainly for internal use.
	 */
	protected void shootSpell(World world, Vec3d origin, Vec3d direction, @Nullable EntityPlayer caster, int ticksInUse, SpellModifiers modifiers) {

		double range = 10;
		Vec3d endpoint = origin.add(direction.scale(range));
		boolean ignoreLivingEntities = false;

		// Change the filter depending on whether living entities are ignored or not
		RayTraceResult rayTrace = RayTracer.rayTrace(world, origin, endpoint, 0, false,
				true, false, Entity.class, ignoreLivingEntities ? EntityUtils::isLiving
						: RayTracer.ignoreEntityFilter(caster));

		if (rayTrace != null) {
			// Doesn't matter which way round these are, they're mutually exclusive
			if (rayTrace.typeOfHit == RayTraceResult.Type.ENTITY) {
				// Do whatever the spell does when it hits an entity
				onEntityHit(world, rayTrace.entityHit, rayTrace.hitVec, caster, origin, ticksInUse, modifiers);
			}
		}
	}

	protected void onEntityHit(World world, Entity target, Vec3d hit,
			@Nullable EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {
		if (target instanceof EntityPlayer) {
			EntityPlayer targetPlayer = (EntityPlayer) target;
			if (!targetPlayer.getHeldItemMainhand().isEmpty() && targetPlayer.getHeldItemMainhand().getItem() instanceof ItemWand) {
				if (caster.getHeldItemMainhand().getItem() instanceof ItemWand) {

					ItemWand targetWand = (ItemWand) targetPlayer.getHeldItemMainhand().getItem();
					ItemStack targetWandStack = targetPlayer.getHeldItemMainhand();

					ItemWand sourceWand = (ItemWand) caster.getHeldItemMainhand().getItem();
					ItemStack sourceWandStack = caster.getHeldItemMainhand();

					if (!sourceWand.isManaEmpty(sourceWandStack) && !targetWand.isManaFull(targetWandStack)) {
						int amount = getProperty(TRANSFER_AMOUNT_PER_TICK).intValue();
						sourceWand.consumeMana(sourceWandStack, amount, caster);
						int newAmount = (int) (amount * getProperty(TRANSFER_EFFICIENCY).floatValue());
						sourceWand.rechargeMana(targetWandStack, newAmount);
						if (world.isRemote) {
							spawnParticle(targetPlayer, true);
						}
					}
				}

			}

		}
	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == AncientSpellcraftItems.ancient_spell_book || item == AncientSpellcraftItems.ancient_spellcraft_scroll;
	}
}