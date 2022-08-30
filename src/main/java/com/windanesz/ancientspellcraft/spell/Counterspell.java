package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.registry.ASItems;
import com.windanesz.ancientspellcraft.registry.ASPotions;
import electroblob.wizardry.entity.living.ISpellCaster;
import electroblob.wizardry.item.ISpellCastingItem;
import electroblob.wizardry.item.ItemWizardArmour;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.spell.SpellRay;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.SpellModifiers;
import electroblob.wizardry.util.WandHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class Counterspell extends SpellRay implements IClassSpell {

	public Counterspell() {
		super(AncientSpellcraft.MODID, "counterspell", SpellActions.POINT, false);
		this.soundValues(1, 1.1f, 0.2f);
	}

	@Override
	protected boolean onEntityHit(World world, Entity target, Vec3d hit, EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {

		if (!world.isRemote) {

			if (target instanceof EntityLivingBase && ((EntityLivingBase) target).isHandActive()) {
				((EntityLivingBase) target).stopActiveHand();
			}

			if (target instanceof EntityPlayer) {
				EntityPlayer targetPlayer = (EntityPlayer) target;
				EnumHand hand = EnumHand.MAIN_HAND;
				boolean flag = false;
				if (targetPlayer.getHeldItemMainhand().getItem() instanceof ISpellCastingItem) {
					hand = EnumHand.MAIN_HAND;
					flag = true;
				} else if (targetPlayer.getHeldItemOffhand().getItem() instanceof ISpellCastingItem) {
					hand = EnumHand.OFF_HAND;
					flag = true;
				}

				if (flag) {
					ItemStack stack = targetPlayer.getHeldItem(hand).copy();
					if (stack.getTagCompound() != null && stack.getTagCompound().hasKey(WandHelper.SELECTED_SPELL_KEY)) {
						WandHelper.setCurrentCooldown(stack, WandHelper.getCurrentSpell(stack).getCooldown());
						targetPlayer.setHeldItem(hand, stack);
					}
				}
			} else if ((target instanceof EntityLivingBase && target instanceof ISpellCaster) && !world.isRemote) {
				((EntityLivingBase) target).addPotionEffect(new PotionEffect(ASPotions.magical_exhaustion, 80, 4));
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
		ParticleBuilder.create(ParticleBuilder.Type.DARK_MAGIC).pos(x, y, z).clr(0x571e65).spawn(world);
		ParticleBuilder.create(ParticleBuilder.Type.DARK_MAGIC).pos(x, y, z).clr(0x251609).spawn(world);
		ParticleBuilder.create(ParticleBuilder.Type.SPARKLE).pos(x, y, z).time(12 + world.rand.nextInt(8)).clr(0x0b4b40).spawn(world);
	}

	@Override
	public ItemWizardArmour.ArmourClass getArmourClass() { return ItemWizardArmour.ArmourClass.SAGE; }

	@Override
	public boolean applicableForItem(Item item) {
		return item == ASItems.mystic_spell_book;
	}

	@Override
	public boolean canBeCastBy(EntityLiving npc, boolean override) {
		return true;
	}
}
