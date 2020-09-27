package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftItems;
import com.windanesz.ancientspellcraft.util.ASUtils;
import electroblob.wizardry.item.IManaStoringItem;
import electroblob.wizardry.spell.SpellRay;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class ManaFlare extends SpellRay {

	public ManaFlare() {
		super(AncientSpellcraft.MODID, "mana_flare", true, EnumAction.BLOCK);
		this.particleVelocity(-0.5);
		this.particleSpacing(0.4);
	}

	@Override
	protected boolean onEntityHit(World world, Entity target, Vec3d hit,
			@Nullable EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {
		if (target instanceof EntityLivingBase) {
			if (ASUtils.isManaStoringCastingItem(((EntityLivingBase) target).getHeldItemMainhand().getItem())) {

				return drainMana(((EntityLivingBase) target).getHeldItemMainhand(), modifiers);

			} else if (ASUtils.isManaStoringCastingItem(((EntityLivingBase) target).getHeldItemOffhand().getItem())) {
				return drainMana(((EntityLivingBase) target).getHeldItemOffhand(), modifiers);
			}
		}

		return false;
	}

	private boolean drainMana(ItemStack target, SpellModifiers modifiers) {
		IManaStoringItem targetItem = (IManaStoringItem) target.getItem();

		if (!targetItem.isManaEmpty(target)) {
			consumeMana(target, (int) (modifiers.get(SpellModifiers.COST)) * 2);

			return true;
		} else {
			return false;
		}
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

	private void consumeMana(ItemStack stack, int mana) {
		((IManaStoringItem) stack.getItem()).setMana(stack, Math.max(((IManaStoringItem) stack.getItem()).getMana(stack) - mana, 0));
	}

	@Override
	protected void spawnParticle(World world, double x, double y, double z, double vx, double vy, double vz) {
		if (world.rand.nextInt(5) == 0)
			ParticleBuilder.create(ParticleBuilder.Type.DARK_MAGIC).pos(x, y, z).clr(1, 1, 0.65f).fade(0.7f, 0, 1).spawn(world);
		// This used to multiply the velocity by the distance from the caster
		ParticleBuilder.create(ParticleBuilder.Type.SPARKLE).pos(x, y, z).vel(vx, vy, vz).time(8 + world.rand.nextInt(6))
				.clr(1, 1, 0.65f).fade(0.7f, 0, 1).spawn(world);
	}

	@Override
	public boolean canBeCastBy(EntityLiving npc, boolean override) {
		return true;
	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == AncientSpellcraftItems.ancient_spellcraft_spell_book;  // No scroll!
	}
}
