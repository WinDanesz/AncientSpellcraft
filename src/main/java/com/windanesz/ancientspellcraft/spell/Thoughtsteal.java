package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.item.ItemSageTome;
import com.windanesz.ancientspellcraft.registry.ASItems;
import electroblob.wizardry.entity.living.EntityWizard;
import electroblob.wizardry.item.ISpellCastingItem;
import electroblob.wizardry.item.ItemWizardArmour;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.spell.SpellRay;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.SpellModifiers;
import electroblob.wizardry.util.WandHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import java.util.Random;

import static electroblob.wizardry.util.WandHelper.SELECTED_SPELL_KEY;

public class Thoughtsteal extends SpellRay implements IClassSpell {

	public static final String MAX_TIER = "max_tier";

	public Thoughtsteal() {
		super(AncientSpellcraft.MODID, "thoughtsteal", SpellActions.POINT, false);
		this.soundValues(1, 1.1f, 0.2f);
		addProperties(MAX_TIER);
	}

	@Override
	protected boolean onEntityHit(World world, Entity target, Vec3d hit, EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {

		if (!world.isRemote && EntityUtils.isLiving(target)) {
			EntityLivingBase livingTarget = (EntityLivingBase) target;
			// Wizards are not actually setting their wand's current spell into the wand, so they need to be handled separately

			if (target instanceof EntityWizard) {
				return tryCopySpell(livingTarget.getHeldItemMainhand(), caster, (EntityWizard) livingTarget);
			}
			if (livingTarget.getHeldItemMainhand().getItem() instanceof ISpellCastingItem) {
				return tryCopySpell(livingTarget.getHeldItemMainhand(), caster, null);
			} else if (livingTarget.getHeldItemOffhand().getItem() instanceof ISpellCastingItem) {
				return tryCopySpell(livingTarget.getHeldItemOffhand(), caster, null);
			}

		}

		return true;
	}

	private boolean tryCopySpell(ItemStack fromStack, EntityLivingBase caster, EntityWizard wizard) {

		Spell spell = ((ISpellCastingItem) fromStack.getItem()).getCurrentSpell(fromStack);
		if (wizard != null) {
			// wizards can only ever grant you one spell
			Random wizzyRand = new Random(Math.abs(wizard.getUniqueID().hashCode()));
			spell = wizard.getSpells().get(Math.min(1, wizzyRand.nextInt(wizard.getSpells().size())));
		}

		if (spell.getTier().ordinal() <= getProperty(MAX_TIER).intValue()) {

			if (!(spell instanceof IClassSpell)) {
				ItemStack newTome = caster.getHeldItem(caster.getActiveHand()).copy();
				if (newTome.getItem() instanceof ItemSageTome) {
					if (newTome.hasTagCompound()) {

						if (newTome.getTagCompound().hasKey(SELECTED_SPELL_KEY)) {
							Spell[] spells = WandHelper.getSpells(newTome);
							int selectedSpell = newTome.getTagCompound().getInteger(SELECTED_SPELL_KEY);
							spells[selectedSpell] = spell;
							WandHelper.setSpells(newTome, spells);
							caster.setHeldItem(caster.getActiveHand(), newTome);
							return true;
						}
					}
				}
			}
		} else if (caster instanceof EntityPlayer) {
			((EntityPlayer) caster).sendStatusMessage(new TextComponentTranslation("spell." + this.getUnlocalisedName() + ".too_strong_spell"), true);
		}

		return false;
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
		return item == ASItems.mystic_spell_book || item == WizardryItems.scroll;
	}
}
