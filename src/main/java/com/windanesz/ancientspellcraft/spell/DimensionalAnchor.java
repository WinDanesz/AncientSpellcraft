package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.potion.PotionDimensionalAnchor;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftItems;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftPotions;
import electroblob.wizardry.data.IStoredVariable;
import electroblob.wizardry.data.Persistence;
import electroblob.wizardry.data.WizardData;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.spell.SpellRay;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.Location;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

@Mod.EventBusSubscriber
public class DimensionalAnchor extends SpellRay {

	private static final String LOCATION_TAG = "location_data";

	public static final IStoredVariable<NBTTagCompound> DIMENSION_ANCHOR_DATA = IStoredVariable.StoredVariable.ofNBT("dimension_anchor_data", Persistence.DIMENSION_CHANGE);

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
		WizardData.registerStoredVariables(DIMENSION_ANCHOR_DATA);
	}

	@Override
	protected boolean onEntityHit(World world, Entity target, Vec3d hit, EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {
		if (EntityUtils.isLiving(target)) {
			((EntityLivingBase) target).addPotionEffect(new PotionEffect(AncientSpellcraftPotions.dimensional_anchor, getProperty(EFFECT_DURATION).intValue(), 0));
		}
		return true;
	}

	@Override
	protected boolean onBlockHit(World world, BlockPos pos, EnumFacing side, Vec3d hit, EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) { return true; }

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
		if (entity != null && world != null && entity.isPotionActive(AncientSpellcraftPotions.dimensional_anchor)) {
			//noinspection ConstantConditions
			preventCast = teleportationSpellList.contains(spell.getRegistryName().toString());

			if (preventCast && entity instanceof EntityPlayer && !world.isRemote) {
				((EntityPlayer) entity).sendStatusMessage(new TextComponentTranslation("spell.ancientspellcraft:dimensional_anchor.prevent_spell"), false);
			}
		}

		return preventCast;
	}

	/**
	 * Called by {@link PotionDimensionalAnchor#performEffect(net.minecraft.entity.EntityLivingBase, int) when the potion is active to keep this data updated
	 */
	public static void storePlayerLocationData(EntityPlayer player) {
		WizardData data = WizardData.get(player);
		if (data != null) {

			NBTTagCompound compound = new NBTTagCompound();
			compound.setTag(LOCATION_TAG, (new Location(player.getPosition(), player.dimension)).toNBT());

			data.setVariable(DIMENSION_ANCHOR_DATA, compound);
			data.sync();
		}
	}

	public static void purgeLocationData(EntityPlayer player) {
		WizardData data = WizardData.get(player);
		if (data != null) {
			data.setVariable(DIMENSION_ANCHOR_DATA, null);
			data.sync();
		}
	}

	@Nullable
	public static Location getPlayerLocationData(EntityPlayer player) {
		WizardData data = WizardData.get(player);
		if (data != null) {
			NBTTagCompound compound = data.getVariable(DIMENSION_ANCHOR_DATA);
			if (compound != null) {
				if (compound.hasKey(LOCATION_TAG)) {
					return Location.fromNBT((NBTTagCompound) compound.getTag(LOCATION_TAG));
				}
			}
		}
		return null;
	}

	@SubscribeEvent
	public static void onEnderTeleportEvent(EnderTeleportEvent event) {
		if (event.getEntityLiving().isPotionActive(AncientSpellcraftPotions.dimensional_anchor)) {
			if (event.isCancelable()) {
				if (event.getEntityLiving() instanceof EntityPlayer && !event.getEntityLiving().world.isRemote) {
					((EntityPlayer) event.getEntityLiving()).sendStatusMessage(new TextComponentTranslation("spell.ancientspellcraft:dimensional_anchor.prevent_ender_pearl"), false);
				}
				event.setCanceled(true);
			}
		}
	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == AncientSpellcraftItems.ancient_spell_book || item == AncientSpellcraftItems.ancient_spellcraft_scroll;
	}
}
