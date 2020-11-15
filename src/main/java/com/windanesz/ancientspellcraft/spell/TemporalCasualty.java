package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftItems;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftPotions;
import electroblob.wizardry.data.IStoredVariable;
import electroblob.wizardry.data.Persistence;
import electroblob.wizardry.data.WizardData;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.spell.SpellRay;
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
import net.minecraft.world.World;

import javax.annotation.Nullable;

import static com.windanesz.ancientspellcraft.spell.TimeKnot.*;

public class TemporalCasualty extends SpellRay {

	public static final IStoredVariable<Long> CURSE_TEMPORAL_CASUALTY_LOOP_START = IStoredVariable.StoredVariable.ofLong("curse_temporal_casualty_loop_start", Persistence.ALWAYS).setSynced().withTicker(TemporalCasualty::update);
	public static final int LOOP_DURATION = 24000;

	public TemporalCasualty() {
		super(AncientSpellcraft.MODID, "curse_temporal_casualty", SpellActions.POINT, false);
		WizardData.registerStoredVariables(CURSE_TEMPORAL_CASUALTY_LOOP_START);

	}

	@Override
	protected boolean onEntityHit(World world, Entity target, Vec3d hit,
			@Nullable EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {
		if (target instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) target;

			WizardData data = WizardData.get(player);
			if (data != null) {

				NBTTagCompound compound = storeCurrentPlayerData(player);
				data.setVariable(TIME_KNOT_DATA, compound);

				data.setVariable(CURSE_TEMPORAL_CASUALTY_LOOP_START, player.world.getTotalWorldTime() + LOOP_DURATION);
				data.sync();

			} else {
				return false;
			}

			if (player.isPotionActive(AncientSpellcraftPotions.time_knot)) {
				player.removePotionEffect(AncientSpellcraftPotions.time_knot);
			}

			player.addPotionEffect(new PotionEffect(AncientSpellcraftPotions.curse_temporal_casualty, Integer.MAX_VALUE, 0));
			//			player.addPotionEffect(new PotionEffect(AncientSpellcraftPotions.curse_temporal_casualty, Integer.MAX_VALUE, 0));
			return true;
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
	protected void spawnParticle(World world, double x, double y, double z, double vx, double vy, double vz) {
		ParticleBuilder.create(ParticleBuilder.Type.DARK_MAGIC).pos(x, y, z).clr(0x12db00).spawn(world);
		ParticleBuilder.create(ParticleBuilder.Type.DARK_MAGIC).pos(x, y, z).clr(0x084d02).spawn(world);
		ParticleBuilder.create(ParticleBuilder.Type.SPARKLE).pos(x, y, z).time(12 + world.rand.nextInt(8)).clr(0x0b4b40).spawn(world);
	}

	private static long update(EntityPlayer player, Long nextLoopStart) {
		if (nextLoopStart == null)
			return 0;
		if (player.world.getTotalWorldTime() == nextLoopStart && player.isPotionActive(AncientSpellcraftPotions.curse_temporal_casualty) && player.isEntityAlive()) {
			loopPlayer(player);
			long l = player.world.getTotalWorldTime() + LOOP_DURATION;
			player.addPotionEffect(new PotionEffect(AncientSpellcraftPotions.curse_temporal_casualty, Integer.MAX_VALUE, 0));

			return l;
		}

		return nextLoopStart;
	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == AncientSpellcraftItems.ancient_spellcraft_spell_book || item == AncientSpellcraftItems.ancient_spellcraft_scroll;
	}
}
