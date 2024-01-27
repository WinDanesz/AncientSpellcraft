package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.registry.ASItems;
import com.windanesz.ancientspellcraft.registry.ASPotions;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.spell.SpellRay;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Arrays;
import java.util.List;

@Mod.EventBusSubscriber
public class DimensionalAnchor extends SpellRay {

	public static List<String> teleportationSpellList = Arrays.asList(
			"ebwizardry:phase_step",
			"ebwizardry:blink",
			"ebwizardry:transportation",
			"ebwizardry:transience",
			"ancientspellcraft:transportation_portal",
			"ancientspellcraft:pocket_dimension",
			"ancientspellcraft:hellgate");

	public DimensionalAnchor() {
		super(AncientSpellcraft.MODID, "dimensional_anchor", SpellActions.POINT, false);
		this.soundValues(1, 1.1f, 0.2f);
		this.addProperties(EFFECT_DURATION);
	}

	@Override
	protected boolean onEntityHit(World world, Entity target, Vec3d hit, EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {
		if (EntityUtils.isLiving(target)) {
			((EntityLivingBase) target).addPotionEffect(new PotionEffect(ASPotions.dimensional_anchor, getProperty(EFFECT_DURATION).intValue(), 0));
		}
		return true;
	}

	@Override
	protected boolean onBlockHit(World world, BlockPos pos, EnumFacing side, Vec3d hit, EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {return true;}

	@Override
	protected boolean onMiss(World world, EntityLivingBase caster, Vec3d origin, Vec3d direction, int ticksInUse, SpellModifiers modifiers) {
		return true;
	}

	@Override
	protected void spawnParticle(World world, double x, double y, double z, double vx, double vy, double vz) {
		ParticleBuilder.create(ParticleBuilder.Type.DARK_MAGIC).pos(x, y, z).clr(0x12db00).spawn(world);
		ParticleBuilder.create(ParticleBuilder.Type.DARK_MAGIC).pos(x, y, z).clr(0x084d02).spawn(world);
		ParticleBuilder.create(ParticleBuilder.Type.SPARKLE).pos(x, y, z).time(12 + world.rand.nextInt(8)).clr(0x0b4b40).spawn(world);
	}

	// called by Event Subscription
	public static boolean shouldPreventSpell(EntityLivingBase entity, World world, Spell spell) {
		boolean preventCast = false;
		if (entity != null && world != null && entity.isPotionActive(ASPotions.dimensional_anchor)) {
			//noinspection ConstantConditions
			preventCast = teleportationSpellList.contains(spell.getRegistryName().toString());

			if (preventCast && entity instanceof EntityPlayer && !world.isRemote) {
				((EntityPlayer) entity).sendStatusMessage(new TextComponentTranslation("spell.ancientspellcraft:dimensional_anchor.prevent_spell"), false);
			}
		}

		return preventCast;
	}

	@SubscribeEvent
	public static void onEnderTeleportEvent(EnderTeleportEvent event) {
		if (event.getEntityLiving().isPotionActive(ASPotions.dimensional_anchor)) {
			if (event.isCancelable()) {
				if (event.getEntityLiving() instanceof EntityPlayer && !event.getEntityLiving().world.isRemote) {
					((EntityPlayer) event.getEntityLiving()).sendStatusMessage(new TextComponentTranslation("spell.ancientspellcraft:dimensional_anchor.prevent_ender_pearl"), false);
				}
				event.setCanceled(true);
			}
		}
	}

	@SubscribeEvent
	public static void onEntityTravelToDimension(EntityTravelToDimensionEvent event) {
		if (event.getEntity() instanceof EntityLivingBase && ((EntityLivingBase) event.getEntity()).isPotionActive(ASPotions.dimensional_anchor)) {
			if (event.getEntity() instanceof EntityPlayer && !event.getEntity().world.isRemote) {
				((EntityPlayer) event.getEntity()).sendStatusMessage(new TextComponentTranslation("spell.ancientspellcraft:dimensional_anchor.prevent_ender_pearl"), false);
			}
			event.setCanceled(true);
		}
	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == ASItems.ancient_spell_book || item == ASItems.ancient_spellcraft_scroll;
	}
}
