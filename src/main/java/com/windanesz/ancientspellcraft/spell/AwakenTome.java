package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.entity.living.EntityAnimatedItem;
import com.windanesz.ancientspellcraft.item.ItemSageTome;
import com.windanesz.ancientspellcraft.registry.ASItems;
import com.windanesz.ancientspellcraft.util.ASUtils;
import electroblob.wizardry.data.IStoredVariable;
import electroblob.wizardry.data.Persistence;
import electroblob.wizardry.data.WizardData;
import electroblob.wizardry.item.ItemWizardArmour;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.UUID;

public class AwakenTome extends Animate implements IClassSpell {

	public static final IStoredVariable<UUID> UUID_KEY = IStoredVariable.StoredVariable.ofUUID("awakenedTomeUUID", Persistence.ALWAYS).setSynced();

	public AwakenTome() {
		super(AncientSpellcraft.MODID, "awaken_tome");
		WizardData.registerStoredVariables(UUID_KEY);
	}

	public static void equipWithTome(EntityAnimatedItem minion, @Nullable EntityLivingBase caster) {
		if (caster != null && (caster.getHeldItemMainhand().getItem() instanceof ItemSageTome)) {
			ItemStack stack = caster.getHeldItemMainhand().copy();
			stack.setCount(1);

			// remove from caster
			caster.getHeldItemMainhand().shrink(1);

			// give to minion
			minion.setHeldItem(EnumHand.MAIN_HAND, stack);
			minion.setItemType(stack.getItem().getRegistryName().toString());
			minion.setHasArmour(false);

			// remember tome
			setTome((EntityPlayer) caster, minion);
			
			// give token to player
			caster.setHeldItem(EnumHand.MAIN_HAND, new ItemStack(ASItems.tome_controller));
		}
	}

	@Override
	public boolean cast(World world, EntityPlayer caster, EnumHand hand, int ticksInUse, SpellModifiers modifiers) {

		if (!(caster.getHeldItem(hand).getItem() instanceof ItemSageTome)) {
			return false;
		}

		EntityAnimatedItem oldTome = getTome(caster);
		if (oldTome != null) {
			recallTome(oldTome);
			this.playSound(world, caster, ticksInUse, -1, modifiers);
			return true;
		}

		if (!this.spawnMinions(world, caster, modifiers, true)) { return false; }
		this.playSound(world, caster, ticksInUse, -1, modifiers);
		return true;
	}

	public boolean applicableForItem(Item item) {
		return item == ASItems.mystic_spell_book;
	}

	@Override
	public ItemWizardArmour.ArmourClass getArmourClass() { return ItemWizardArmour.ArmourClass.SAGE; }

	@Override
	public boolean canBeCastBy(EntityLiving npc, boolean override) {
		return false;
	}

	@Override
	public boolean canBeCastBy(TileEntityDispenser dispenser) {
		return false;
	}

	@Override
	protected void addMinionExtras(EntityAnimatedItem minion, BlockPos pos, @Nullable EntityLivingBase caster, SpellModifiers modifiers, int alreadySpawned) {
		super.addMinionExtras(minion, pos, caster, modifiers, alreadySpawned);
		equipWithTome(minion, caster);
		setTome((EntityPlayer) caster, minion);
	}

	@Nullable
	public static EntityAnimatedItem getTome(EntityPlayer caster) {
		if (caster != null) {
			WizardData data = WizardData.get(caster);

			if (data != null) {
				Entity oldTome = EntityUtils.getEntityByUUID(caster.world, data.getVariable(UUID_KEY));
				if (oldTome instanceof EntityAnimatedItem) {
					return (EntityAnimatedItem) oldTome;
				}
			}
		}

		return null;
	}

	public static void setTome(EntityPlayer caster, EntityAnimatedItem tome) {
		if (caster != null) {
			WizardData data = WizardData.get(caster);

			if (data != null) {
				data.setVariable(UUID_KEY, tome.getUniqueID());
				data.sync();
			}
		}
	}

	public static void recallTome(EntityAnimatedItem tome) {
		if (tome != null) {
			tome.onDespawn();
			tome.setDead();
		}
	}

	public static void removeController(EntityPlayer player) {
		ASUtils.shrinkInventoryStackByOne(player, new ItemStack(ASItems.tome_controller));
	}
}
