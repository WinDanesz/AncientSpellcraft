package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.item.ItemBattlemageShield;
import com.windanesz.ancientspellcraft.item.ItemBattlemageSword;
import com.windanesz.ancientspellcraft.item.WizardClassWeaponHelper;
import com.windanesz.ancientspellcraft.registry.ASItems;
import electroblob.wizardry.block.BlockReceptacle;
import electroblob.wizardry.constants.Element;
import electroblob.wizardry.item.IManaStoringItem;
import electroblob.wizardry.item.ItemArtefact;
import electroblob.wizardry.item.ItemWizardArmour;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class RunewordMeditate extends Runeword {

	public RunewordMeditate() {
		super("runeword_meditate", SpellActions.IMBUE, true);
		addProperties();
	}

	@Override
	public boolean cast(World world, EntityPlayer caster, EnumHand hand, int ticksInUse, SpellModifiers modifiers) {
		if (!caster.world.isRemote && caster.ticksExisted % 10 == 0) {
			if (caster.getHeldItemOffhand().getItem() instanceof ItemBattlemageShield) {
				((IManaStoringItem) caster.getHeldItemOffhand().getItem()).rechargeMana(caster.getHeldItemOffhand(), 3);

				if (ItemArtefact.isArtefactActive(caster, ASItems.charm_glyph_fortification)) {
					for (ItemStack stack : caster.getArmorInventoryList()) {

						if (stack.getItem() instanceof ItemWizardArmour && ((ItemWizardArmour) stack.getItem()).armourClass == ItemWizardArmour.ArmourClass.BATTLEMAGE) {
							((IManaStoringItem) stack.getItem()).rechargeMana(stack, 6);
						}
					}
				}
			}
		}
		if (world.isRemote) {

			if (caster.getHeldItem(hand).getItem() instanceof ItemBattlemageSword) {
				Element element = WizardClassWeaponHelper.getElement(caster.getHeldItem(hand));
				int[] colours = BlockReceptacle.PARTICLE_COLOURS.get(element);

				if (element != Element.MAGIC) {
					for (int i = 0; i < 2; i++) {
						ParticleBuilder.create(ParticleBuilder.Type.DUST).pos(caster.posX -0.5f +  caster.world.rand.nextFloat(), caster.posY, caster.posZ -0.5f + caster.world.rand.nextFloat())
								.vel(0, 0.05 + (caster.world.rand.nextFloat() * 0.1), 0).clr(colours[1]).fade(colours[2]).time(40).shaded(false).spawn(world);
					}
				}
			}
		}
		return true;
	}
}
