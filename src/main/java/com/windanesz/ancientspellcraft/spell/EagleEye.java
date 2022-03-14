package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.registry.ASItems;
import com.windanesz.ancientspellcraft.registry.ASPotions;
import electroblob.wizardry.spell.SpellBuff;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class EagleEye extends SpellBuff {

	public EagleEye() {
		super(AncientSpellcraft.MODID, "eagle_eye", 255, 200, 0, () -> ASPotions.eagle_eye);
		soundValues(0.7f, 1.1f, 0.1f);
	}

	@Override
	public boolean cast(World world, EntityPlayer caster, EnumHand hand, int ticksInUse, SpellModifiers modifiers) {

		if (!world.canSeeSky(caster.getPosition())) {
			if (!world.isRemote)
				caster.sendStatusMessage(new TextComponentTranslation("spell.ancientspellcraft\\:eagle_eye.indoor"), true);
			return false;
		}

		if (caster.isSneaking()) {
			return false;
		}

		return super.cast(world, caster, hand, ticksInUse, modifiers);

	}

	@Override
	public boolean cast(World world, EntityLiving caster, EnumHand hand, int ticksInUse, EntityLivingBase target, SpellModifiers modifiers) {
		return false;
	}

	@Override
	public boolean cast(World world, double x, double y, double z, EnumFacing direction, int ticksInUse, int duration, SpellModifiers modifiers) {
		return false;
	}

	@Override
	public boolean canBeCastBy(TileEntityDispenser dispenser) {
		return false;
	}

	/**
	 * Returns the number to be added to the potion amplifier(s) based on the given potency modifier. Override
	 * to define custom modifier handling. Delegates to {@link SpellBuff#getStandardBonusAmplifier(float)} by
	 * default.
	 * <b>Maximum level of this buff is 0</>
	 */
	protected int getBonusAmplifier(float potencyModifier) {
		return 0;
	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == ASItems.ancient_spell_book || item == ASItems.ancient_spellcraft_scroll;
	}
}
