package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftItems;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftPotions;
import electroblob.wizardry.spell.SpellBuff;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class EagleEye extends SpellBuff {

	public EagleEye() {
		super(AncientSpellcraft.MODID, "eagle_eye", 255, 200, 0, () -> AncientSpellcraftPotions.eagle_eye);
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
		return item == AncientSpellcraftItems.ancient_spell_book || item == AncientSpellcraftItems.ancient_spellcraft_scroll;
	}
}
