package com.windanesz.ancientspellcraft.spell;

import com.google.common.base.Predicate;
import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.entity.ai.EntityAIWizardFollowPlayer;
import com.windanesz.ancientspellcraft.registry.ASItems;
import electroblob.wizardry.Wizardry;
import electroblob.wizardry.data.IStoredVariable;
import electroblob.wizardry.data.Persistence;
import electroblob.wizardry.data.WizardData;
import electroblob.wizardry.entity.living.EntityWizard;
import electroblob.wizardry.entity.living.ISummonedCreature;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.spell.SpellRay;
import electroblob.wizardry.util.AllyDesignationSystem;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.UUID;

@Mod.EventBusSubscriber
public class Covenant extends SpellRay {

	public static final IStoredVariable<UUID> ALLIED_WIZARD_UUID_KEY = IStoredVariable.StoredVariable.ofUUID("alliedWizardUUID", Persistence.ALWAYS);

	public Covenant() {
		super(AncientSpellcraft.MODID, "covenant", SpellActions.SUMMON, false);
		WizardData.registerStoredVariables(ALLIED_WIZARD_UUID_KEY);

	}

	// only work if you are wearing wizard robes !!!

	@Override
	protected boolean onEntityHit(World world, Entity target, Vec3d hit,
			@Nullable EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {
		if (caster instanceof EntityPlayer) {

			if (target instanceof EntityWizard) {

				EntityPlayer player = (EntityPlayer) caster;
				EntityWizard wizard = (EntityWizard) target;
				WizardData data = WizardData.get(player);
				boolean changeStatus = true;

				if (!world.isRemote) {
					Entity oldWizard = EntityUtils.getEntityByUUID(world, data.getVariable(ALLIED_WIZARD_UUID_KEY));
					boolean flag1 = oldWizard != null && oldWizard != wizard;
					boolean flag2 = wizard == oldWizard && (isAlreadyFollowing(wizard));
					if (flag1 || flag2) {

						if (flag2) {
							changeStatus = false;
						}

						endAlliance((EntityWizard) oldWizard);
						player.sendStatusMessage(new TextComponentTranslation("spell.ancientspellcraft:covenant.no_longer_following", oldWizard.getDisplayName()), false);
					}

					if (changeStatus && !isAlreadyFollowing(wizard)) {
						allyWithWizard(player, wizard);
						player.sendStatusMessage(new TextComponentTranslation("spell.ancientspellcraft:covenant.following", wizard.getDisplayName()), true);

						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	protected boolean onBlockHit(World world, BlockPos pos, EnumFacing side, Vec3d hit,
			@Nullable EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {
		return false;
	}

	@Override
	protected boolean onMiss(World world, @Nullable EntityLivingBase caster, Vec3d origin, Vec3d direction, int ticksInUse, SpellModifiers modifiers) {
		return false;
	}

	@Override
	public boolean canBeCastBy(TileEntityDispenser dispenser) {
		return false;
	}

	@Override
	public boolean canBeCastBy(EntityLiving npc, boolean override) {
		return false;
	}

	// FIXME: wizards follow the player again after world reload
	private static void endAlliance(EntityWizard wizard) {

		for (EntityAITasks.EntityAITaskEntry entityaitasks$entityaitaskentry : wizard.tasks.taskEntries) {
			EntityAIBase entityAIBase = entityaitasks$entityaitaskentry.action;
			if (entityAIBase instanceof EntityAIWizardFollowPlayer) {
				wizard.tasks.removeTask(entityAIBase);
			}
		}
	}

	private static void allyWithWizard(EntityPlayer player, EntityWizard wizard) {

		EntityAIWizardFollowPlayer task = new EntityAIWizardFollowPlayer(wizard, 1f, 6, 10, player);
		wizard.tasks.addTask(2, task);

		EntityAINearestAttackableTarget targetTask = null;
		EntityAIHurtByTarget hurtByTask = null;

		boolean foundTargetTask = false;
		boolean foundHurtByTask = false;

		for (EntityAITasks.EntityAITaskEntry entityaitasks$entityaitaskentry : wizard.targetTasks.taskEntries) {
			EntityAIBase entityaibase = entityaitasks$entityaitaskentry.action;
			if (entityaibase instanceof EntityAINearestAttackableTarget) {
				targetTask = (EntityAINearestAttackableTarget) entityaibase;
				foundTargetTask = true;
			}

			if (entityaibase instanceof EntityAIHurtByTarget) {
				hurtByTask = (EntityAIHurtByTarget) entityaibase;
				foundHurtByTask = true;
			}
		}

		if (foundTargetTask) {
			wizard.targetTasks.removeTask(targetTask);
			EntityAINearestAttackableTarget newTargetTask = new EntityAINearestAttackableTarget<>(wizard, EntityLiving.class, 0, false,
					true, getNewTargetSelector(wizard, player));
			wizard.targetTasks.addTask(0, newTargetTask);
		}

		if (foundHurtByTask) {
			wizard.targetTasks.removeTask(hurtByTask);
		}
		WizardData data = WizardData.get(player);
		data.setVariable(ALLIED_WIZARD_UUID_KEY, wizard.getUniqueID());
	}

	private static Predicate<Entity> getNewTargetSelector(EntityWizard wizard, EntityPlayer thePlayer) {
		return entity -> {

			// If the target is valid and not invisible...
			if (entity != null && !entity.isInvisible()
					&& AllyDesignationSystem.isValidTarget(wizard, entity)) {

				// and the new check..
				if (entity != thePlayer) {
					// ... and is a mob, a summoned creature ...
					if ((entity instanceof IMob || entity instanceof ISummonedCreature
							// ... or in the whitelist ...
							|| Arrays.asList(Wizardry.settings.summonedCreatureTargetsWhitelist)
							.contains(EntityList.getKey(entity.getClass())))
							// ... and isn't in the blacklist ...
							&& !Arrays.asList(Wizardry.settings.summonedCreatureTargetsBlacklist)
							.contains(EntityList.getKey(entity.getClass()))) {
						// ... it can be attacked.
						return true;
					}
				}
			}

			return false;
		};
	}

	private static boolean isAlreadyFollowing(EntityWizard wizard) {
		for (EntityAITasks.EntityAITaskEntry entityaitasks$entityaitaskentry : wizard.tasks.taskEntries) {
			EntityAIBase entityAIBase = entityaitasks$entityaitaskentry.action;
			if (entityAIBase instanceof EntityAIWizardFollowPlayer) {
				return true;
			}
		}
		return false;
	}

	@SubscribeEvent
	public static void onCheckSpawnEvent(EntityJoinWorldEvent event) {

		// We have no way of checking if it's a spawner in getCanSpawnHere() so this has to be done here instead
		if (event.getEntity() instanceof EntityPlayer) {
			if (!event.getWorld().isRemote) {

				EntityPlayer player = (EntityPlayer) event.getEntity();

				WizardData data = WizardData.get(player);
				Entity storedWizard = EntityUtils.getEntityByUUID(event.getWorld(), data.getVariable(ALLIED_WIZARD_UUID_KEY));
				if (storedWizard != null && !isAlreadyFollowing((EntityWizard) storedWizard)) {
					allyWithWizard(player, (EntityWizard) storedWizard);
				}
			}
		}
	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == ASItems.ancient_spell_book || item == ASItems.ancient_spellcraft_scroll;
	}
}
