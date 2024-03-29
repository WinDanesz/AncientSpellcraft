package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.registry.ASItems;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.spell.SpellRay;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityZombieVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

public class CureZombie extends SpellRay {

	private static final Method startConverting;

	static {
		startConverting = ObfuscationReflectionHelper.findMethod(EntityZombieVillager.class, "func_191991_a", void.class, UUID.class, int.class);
	}

	public CureZombie() {
		super(AncientSpellcraft.MODID, "cure_zombie", SpellActions.POINT_UP, false);
	}

	// The following three methods serve as a good example of how to implement continuous spell sounds (hint: it's easy)

	@Override
	protected boolean onEntityHit(World world, Entity target, Vec3d hit, EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {
		if (target instanceof EntityZombieVillager && caster instanceof EntityPlayer) {
			if (!world.isRemote) {
				StartConverting((EntityZombieVillager) target, caster.getUniqueID(), AncientSpellcraft.rand.nextInt(2401) + 3600);
				return true;
			}
		}
		return false;
	}

	@Override
	protected boolean onBlockHit(World world, BlockPos pos, EnumFacing side, Vec3d hit, EntityLivingBase caster, Vec3d origin,
			int ticksInUse, SpellModifiers modifiers) {
		return false;
	}

	@Override
	protected boolean onMiss(World world, EntityLivingBase caster, Vec3d origin, Vec3d direction, int ticksInUse, SpellModifiers modifiers) {
		return true;
	}

	@Override
	protected void spawnParticle(World world, double x, double y, double z, double vx, double vy, double vz) {
		ParticleBuilder.create(ParticleBuilder.Type.DARK_MAGIC).pos(x, y, z).clr(0x571e65).spawn(world);
		ParticleBuilder.create(ParticleBuilder.Type.DARK_MAGIC).pos(x, y, z).clr(0x251609).spawn(world);
		ParticleBuilder.create(ParticleBuilder.Type.SPARKLE).pos(x, y, z).time(12 + world.rand.nextInt(8)).clr(0x0b4b40).spawn(world);
	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == ASItems.ancient_spellcraft_spell_book || item == ASItems.ancient_spellcraft_scroll;
	}

	public static void StartConverting(EntityZombieVillager zombieVillager, UUID conversionStarterIn, int conversionTimeIn) {
		try {
			startConverting.invoke(zombieVillager, conversionStarterIn, conversionTimeIn);

		}
		catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			AncientSpellcraft.logger.error("Error while reflectively calling EntityZombieVillager#startConverting");
			e.printStackTrace();
		}
	}
}
