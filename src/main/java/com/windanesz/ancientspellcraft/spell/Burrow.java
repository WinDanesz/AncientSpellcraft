package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftItems;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftPotions;
import electroblob.wizardry.item.ItemArtefact;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber
public class Burrow extends Spell {

	public Burrow() {
		super(AncientSpellcraft.MODID, "burrow", SpellActions.POINT_DOWN, true);
		soundValues(1.0f, 1.2f, 0.2f);
	}

	@Override
	public boolean canBeCastBy(EntityLiving npc, boolean override) {
		return false;
	}

	@Override
	public boolean canBeCastBy(TileEntityDispenser dispenser) {
		return false;
	}

	@Override
	public boolean cast(World world, EntityLiving caster, EnumHand hand, int ticksInUse, EntityLivingBase target, SpellModifiers modifiers) {
		return false;
	}

	@Override
	public boolean cast(World world, double x, double y, double z, EnumFacing direction, int ticksInUse, int duration, SpellModifiers modifiers) {
		return false;
	}

	@Override
	public boolean cast(World world, EntityPlayer caster, EnumHand hand, int ticksInUse, SpellModifiers modifiers) {

		if (caster.onGround) {

			if (ticksInUse % 20 == 0) {
				this.playSound(world, caster, ticksInUse, -1, modifiers);
			}

			if (caster.posY < 15) {
				if (!world.isRemote)
					if (ticksInUse % 20 == 0)
						caster.sendStatusMessage(new TextComponentTranslation("spell.ancientspellcraft:burrow.y_limit"), false);
				return false;
			}

			if (!isDiggable(world.getBlockState(caster.getPosition().down()), caster) || !isDiggable(world.getBlockState(caster.getPosition()), caster)) {
				if (ticksInUse % 20 == 0) {
					caster.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 41, 2));
					caster.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, 81));
					caster.addPotionEffect(new PotionEffect(AncientSpellcraftPotions.burrow, 20));
					if (!world.isRemote)
						caster.sendStatusMessage(new TextComponentTranslation("spell.ancientspellcraft:burrow.hard_material"), false);
				}
				return false;
			}

			caster.addPotionEffect(new PotionEffect(AncientSpellcraftPotions.burrow, 20));

			if (ticksInUse % 20 == 0 && !world.isRemote) {
				caster.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 41));
				caster.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 41, 2));
				caster.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, 81));
			}

			if (!world.isAirBlock(caster.getPosition().down())) {
				caster.noClip = true;
			}

			if (caster.motionY < 0) {
				caster.motionY *= 0.5;
			}

			return true;
		}

		return false;
	}

	@Override
	public void finishCasting(World world,
			@Nullable EntityLivingBase caster, double x, double y, double z, @Nullable EnumFacing direction, int duration, SpellModifiers modifiers) {
		super.finishCasting(world, caster, x, y, z, direction, duration, modifiers);
	}

	public static boolean isDiggable(IBlockState state, EntityPlayer player) {
		List<Material> diggable = new ArrayList<>();
		diggable.add(Material.VINE);
		diggable.add(Material.CLAY);
		diggable.add(Material.AIR);
		diggable.add(Material.WATER);
		diggable.add(Material.SAND);
		diggable.add(Material.GRASS);
		diggable.add(Material.SNOW);
		diggable.add(Material.PLANTS);
		diggable.add(Material.GROUND);
		diggable.add(Material.LEAVES);

		if (ItemArtefact.isArtefactActive(player, AncientSpellcraftItems.charm_burrow)) {
			diggable.add(Material.ROCK);
		}
		return diggable.contains(state.getMaterial());
	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == AncientSpellcraftItems.ancient_spellcraft_spell_book || item == AncientSpellcraftItems.ancient_spellcraft_scroll;
	}

}
