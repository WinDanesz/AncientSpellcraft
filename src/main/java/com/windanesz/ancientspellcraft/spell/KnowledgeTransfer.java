package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftItems;
import electroblob.wizardry.item.ItemWand;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.SpellModifiers;
import electroblob.wizardry.util.WandHelper;
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
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import javax.annotation.Nullable;

import static electroblob.wizardry.util.WandHelper.PROGRESSION_KEY;

public class KnowledgeTransfer extends Spell {

	private static int TRANSFER_AMOUNT = 2;

	public KnowledgeTransfer() {
		super(AncientSpellcraft.MODID, "knowledge_transfer", EnumAction.BLOCK, true);
		addProperties(DURATION);
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

			if (player.getHeldItemOffhand().getItem() instanceof ItemWand && (player.getHeldItemMainhand().getItem() instanceof ItemWand)) {
				// source wand is in the main hand, target is offhand
				ItemStack sourceWandStack = player.getHeldItemMainhand();
				ItemStack targetWandStack = player.getHeldItemOffhand();
				ItemWand sourceWand = (ItemWand) sourceWandStack.getItem();
				ItemWand targetWand = (ItemWand) targetWandStack.getItem();
				if (targetWand.getMana(targetWandStack) < targetWand.getManaCapacity(targetWandStack)) {

				}
				//TODO
//				if (WandHelper.getProgression(sourceWandStack) > TRANSFER_AMOUNT) {
//					removeProgression(sourceWandStack, TRANSFER_AMOUNT);
//					WandHelper.addProgression(targetWandStack, TRANSFER_AMOUNT);
//				}

			} else if (!player.world.isRemote)
				player.sendStatusMessage(new TextComponentTranslation("spell.ancientspellcraft:knowledge_transfer.no_wand"), true);

		}
	}

	public static int getProgression(ItemStack wand) {

		if (wand.getTagCompound() == null)
			return 0;

		return wand.getTagCompound().getInteger(PROGRESSION_KEY);
	}

	private static void removeProgression(ItemStack wand, int amount) {
		int current = WandHelper.getProgression(wand);
		WandHelper.setProgression(wand, current - amount);
	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == AncientSpellcraftItems.ancient_spellcraft_spell_book || item == AncientSpellcraftItems.ancient_spellcraft_scroll;
	}
}