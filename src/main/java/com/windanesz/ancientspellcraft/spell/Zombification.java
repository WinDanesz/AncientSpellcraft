package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.registry.AncientSpellcraftItems;
import electroblob.wizardry.spell.SpellRay;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityZombieVillager;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class Zombification extends SpellRay {

	/**
	 * The number by which this spell's damage is multiplied for undead entities.
	 */
	public Zombification(String modID, String name, EnumAction action, boolean isContinuous) {
		super(modID, name, isContinuous, action);
	}

	// The following three methods serve as a good example of h||ow to implement continuous spell sounds (hint: it's easy)

	@Override
	protected boolean onEntityHit(World world, Entity target, Vec3d hit, EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {
		this.playSound(world, target.getPosition().getX(), target.getPosition().getY(), target.getPosition().getZ(), ticksInUse, ticksInUse, modifiers);

		if (!world.isRemote && target instanceof EntityVillager) {
			EntityVillager entityvillager = (EntityVillager) target;
			EntityZombieVillager entityzombievillager = new EntityZombieVillager(world);
			entityzombievillager.copyLocationAndAnglesFrom(entityvillager);
			world.removeEntity(entityvillager);
			//				entityzombievillager.onInitialSpawn(world.getDifficultyForLocation(new BlockPos(entityzombievillager)), new EntityZombie.GroupData(false));
			entityzombievillager.setProfession(entityvillager.getProfession());
			entityzombievillager.setChild(entityvillager.isChild());
			entityzombievillager.setNoAI(entityvillager.isAIDisabled());

			if (entityvillager.hasCustomName()) {
				entityzombievillager.setCustomNameTag(entityvillager.getCustomNameTag());
				entityzombievillager.setAlwaysRenderNameTag(entityvillager.getAlwaysRenderNameTag());
			}

			world.spawnEntity(entityzombievillager);
			world.playEvent((EntityPlayer) null, 1026, new BlockPos(target), 0);
			return true;
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
		return item == AncientSpellcraftItems.ancient_spellcraft_spell_book || item == AncientSpellcraftItems.ancient_spellcraft_scroll;
	}

}
