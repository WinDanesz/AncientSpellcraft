package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftItems;
import electroblob.wizardry.client.DrawingUtils;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;

public class Orbiter extends Spell {

	public Orbiter() {
		super(AncientSpellcraft.MODID, "orbiter", SpellActions.SUMMON, false);
		addProperties(DAMAGE);
	}

	@Override
	public boolean cast(World world, EntityPlayer caster, EnumHand hand, int ticksInUse, SpellModifiers modifiers) {
		double speed = 0.02;
		double radius = 1.8f;
		if (world.isRemote) {

			for (int i = 0; i < 3; i++) {


		ParticleBuilder.create(ParticleBuilder.Type.SCORCH)
				.entity(caster)
				.pos(0, 1, 0)
				.clr(DrawingUtils.mix(0xbb57cd, 0x4a005c, 0.7f))
				.spin(radius, speed)
				.scale(0.2f)
				.time(100)
				.spawn(world);
			}

		}

		return true;
	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == AncientSpellcraftItems.ancient_spell_book || item == AncientSpellcraftItems.ancient_spellcraft_scroll;
	}
}
