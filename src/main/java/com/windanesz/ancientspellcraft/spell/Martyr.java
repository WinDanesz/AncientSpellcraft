package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.registry.ASItems;
import com.windanesz.ancientspellcraft.registry.ASPotions;
import electroblob.wizardry.data.IStoredVariable;
import electroblob.wizardry.data.Persistence;
import electroblob.wizardry.data.WizardData;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.spell.SpellRay;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.NBTExtras;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.ParticleBuilder.Type;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Mod.EventBusSubscriber
public class Martyr extends SpellRay {

	public static final IStoredVariable<Set<UUID>> MARTYR_BOUND_CREATURES = new IStoredVariable.StoredVariable<>("martyrBoundCreatures",
			s -> NBTExtras.listToNBT(s, NBTUtil::createUUIDTag),
			// For some reason gradle screams at me unless I explicitly declare the type of t here, despite IntelliJ being fine without it
			(NBTTagList t) -> new HashSet<>(NBTExtras.NBTToList(t, NBTUtil::getUUIDFromTag)),
			// Martyr is lifted when the caster dies, but not when they switch dimensions.
			Persistence.DIMENSION_CHANGE);

	public Martyr() {
		super(AncientSpellcraft.MODID, "martyr", SpellActions.POINT, false);
		this.soundValues(1, 1.1f, 0.2f);
		WizardData.registerStoredVariables(MARTYR_BOUND_CREATURES);
	}

	@Override
	public boolean canBeCastBy(EntityLiving npc, boolean override) { return false; }

	// You can't damage a dispenser so this would be nonsense!
	@Override
	public boolean canBeCastBy(TileEntityDispenser dispenser) { return false; }

	@Override
	protected boolean onEntityHit(World world, Entity target, Vec3d hit, EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {

		if (EntityUtils.isLiving(target) && caster instanceof EntityPlayer) {
			WizardData data = WizardData.get((EntityPlayer) caster);
			if (data != null) {
				// Return false if soulbinding failed (e.g. if the target is already soulbound)
				if (getMartyrBoundEntities(data).add(target.getUniqueID())) {
					// This will actually run out in the end, but only if you leave Minecraft running for 3.4 years
					((EntityLivingBase) target).addPotionEffect(new PotionEffect(ASPotions.martyr_beneficial, 1200));
					caster.addPotionEffect(new PotionEffect(ASPotions.martyr, 1200));
				} else {
					return false;
				}
			}
		}

		return true;
	}

	@Override
	protected boolean onBlockHit(World world, BlockPos pos, EnumFacing side, Vec3d hit, EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {
		return false;
	}

	@Override
	protected boolean onMiss(World world, EntityLivingBase caster, Vec3d origin, Vec3d direction, int ticksInUse, SpellModifiers modifiers) {
		return true;
	}

	@Override
	protected void spawnParticle(World world, double x, double y, double z, double vx, double vy, double vz) {
		ParticleBuilder.create(Type.DARK_MAGIC).pos(x, y, z).clr(0.4f, 0, 0).spawn(world);
		ParticleBuilder.create(Type.DARK_MAGIC).pos(x, y, z).clr(0.1f, 0, 0).spawn(world);
		ParticleBuilder.create(Type.SPARKLE).pos(x, y, z).time(12 + world.rand.nextInt(8)).clr(1, 0.8f, 1).spawn(world);
	}

	public static Set<UUID> getMartyrBoundEntities(WizardData data) {

		if (data.getVariable(MARTYR_BOUND_CREATURES) == null) {
			Set<UUID> result = new HashSet<>();
			data.setVariable(MARTYR_BOUND_CREATURES, result);
			return result;

		} else
			return data.getVariable(MARTYR_BOUND_CREATURES);
	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == ASItems.ancient_spellcraft_spell_book || item == ASItems.ancient_spellcraft_scroll;
	}

}
