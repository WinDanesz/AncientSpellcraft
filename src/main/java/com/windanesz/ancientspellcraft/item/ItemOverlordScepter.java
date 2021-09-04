package com.windanesz.ancientspellcraft.item;

import com.windanesz.ancientspellcraft.entity.ai.EntityAIWizardFollowPlayer;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftTabs;
import electroblob.wizardry.Wizardry;
import electroblob.wizardry.registry.Spells;
import electroblob.wizardry.registry.WizardryPotions;
import electroblob.wizardry.registry.WizardrySounds;
import electroblob.wizardry.spell.MindControl;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.ParticleBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class ItemOverlordScepter extends Item {

	private static final String CONTROLLED_ENTITY = "last_controlled_entity_uuid";

	@SuppressWarnings("ConstantConditions")
	public ItemOverlordScepter() {
		super();
		setCreativeTab(AncientSpellcraftTabs.ANCIENTSPELLCRAFT_GEAR);
		setMaxDamage(0);
	}

	@Override
	public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
		World world = attacker.getEntityWorld();
		Entity lastEntity = getLastControlledEntity(world, stack);
		if (lastEntity == target) {
			// do nothing as we already control this entity
		} else {
			controlEntity(target, attacker, world, stack);
			if (lastEntity instanceof EntityLivingBase) {
				// Lose control of the previous entity
				stopEntityControl((EntityLivingBase) lastEntity, world, attacker);
			}
		}
		return true;
	}

	@Override
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.EPIC;
	}

	@Override
	public boolean isEnchantable(ItemStack stack) { return false; }

	@Override
	public int getItemEnchantability() { return 0; }

	@Override
	public void setDamage(ItemStack stack, int damage) {}

	@Override
	public boolean isRepairable() { return false; }

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, net.minecraft.client.util.ITooltipFlag advanced) {
		Wizardry.proxy.addMultiLineDescription(tooltip, "item." + this.getRegistryName() + ".desc");
	}

	@Nullable
	private static Entity getLastControlledEntity(World world, ItemStack stack) {
		if (stack.hasTagCompound() && stack.getTagCompound().hasUniqueId(CONTROLLED_ENTITY)) {
			return EntityUtils.getEntityByUUID(world, stack.getTagCompound().getUniqueId(CONTROLLED_ENTITY));
		}
		return null;

	}

	// this is basically a modified version of electroblob.wizardry.spell.MindControl.onEntityHit
	private static void controlEntity(EntityLivingBase target, EntityLivingBase caster, World world, ItemStack stack) {
		if (EntityUtils.isLiving(target)) {
			//			if (!MindControl.canControl(target)) {
			if (target instanceof EntityLiving && target.isNonBoss() &&
					Arrays.asList(Wizardry.settings.mindControlTargetsBlacklist).contains(EntityList.getKey(target.getClass()))) {
				if (!world.isRemote) {
					if (caster instanceof EntityPlayer) {
						// TODO: // Adds a message saying that the player/boss entity/wizard resisted mind control
						((EntityPlayer) caster).sendStatusMessage(new TextComponentTranslation("spell.resist", target.getName(),
								Spells.mind_control.getNameForTranslationFormatted()), true);
					}
				}

			} else if (target instanceof EntityLiving) {

				if (!world.isRemote) {
					if (!MindControl.findMindControlTarget((EntityLiving) target, caster, world)) {
						// If no valid target was found, this just acts like mind trick.
						((EntityLiving) target).setAttackTarget(null);
					}
				}

				if (target instanceof EntitySheep && ((EntitySheep) target).getFleeceColor() == EnumDyeColor.BLUE
						&& EntityUtils.canDamageBlocks(caster, world)) {
					if (!world.isRemote) {
						((EntitySheep) target).setFleeceColor(EnumDyeColor.RED); // Wololo!
					}
					world.playSound(caster.posX, caster.posY, caster.posZ, SoundEvents.EVOCATION_ILLAGER_PREPARE_WOLOLO, WizardrySounds.SPELLS, 1, 1, false);
				}

				if (!world.isRemote) {
					MindControl.startControlling((EntityLiving) target, caster, Integer.MAX_VALUE);

					// make them follow
					if (target instanceof EntityCreature) {
						EntityCreature creature = (EntityCreature) target;
						// fixme
//						EntitySummonAIFollowOwner task = new EntitySummonAIFollowOwner(creature, 1.0D, 10.0F, 2.0F);
//						creature.tasks.addTask(5, task);
					}
					storeCurrentlyControlledEntityUUID(stack, target);
				}

			}

			if (world.isRemote) {

				for (int i = 0; i < 10; i++) {
					ParticleBuilder.create(ParticleBuilder.Type.DARK_MAGIC, world.rand, target.posX,
							target.posY + target.getEyeHeight(), target.posZ, 0.25, false)
							.clr(0.8f, 0.2f, 1.0f).spawn(world);
					ParticleBuilder.create(ParticleBuilder.Type.DARK_MAGIC, world.rand, target.posX,
							target.posY + target.getEyeHeight(), target.posZ, 0.25, false)
							.clr(0.2f, 0.04f, 0.25f).spawn(world);
				}
			}
		}
	}

	private static void storeCurrentlyControlledEntityUUID(ItemStack stack, EntityLivingBase entityToControl) {
		NBTTagCompound nbt = stack.getTagCompound() != null ? stack.getTagCompound() : new NBTTagCompound();
		nbt.setUniqueId(CONTROLLED_ENTITY, entityToControl.getUniqueID());
		stack.setTagCompound(nbt);
	}

	private static void stopEntityControl(EntityLivingBase controlledEntity, World world, EntityLivingBase attacker) {
		if (controlledEntity.isPotionActive(WizardryPotions.mind_control)) {
			NBTTagCompound entityNBT = controlledEntity.getEntityData();

			Entity caster = EntityUtils.getEntityByUUID(world, entityNBT.getUniqueId(MindControl.NBT_KEY));

			// Only remove if the current player was the one controlling this entity, otherwise we'd interrupt the mind control
			// effect of someone else
			if (caster == attacker) {
				// The MindControl.onPotionExpiryEvent() method will pick this up and remove the entity nbt data tag
				controlledEntity.removePotionEffect(WizardryPotions.mind_control);

				if (controlledEntity instanceof EntityLiving) {

					for (EntityAITasks.EntityAITaskEntry entityaitasks$entityaitaskentry : ((EntityLiving) controlledEntity).tasks.taskEntries) {
						EntityAIBase entityAIBase = entityaitasks$entityaitaskentry.action;
						if (entityAIBase instanceof EntityAIWizardFollowPlayer) {
							((EntityLiving) controlledEntity).tasks.removeTask(entityAIBase);
						}
					}
				}

			}
		}
	}
}





