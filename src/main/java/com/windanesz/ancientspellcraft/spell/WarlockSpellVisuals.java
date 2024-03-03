package com.windanesz.ancientspellcraft.spell;

import com.google.common.collect.ImmutableMap;
import electroblob.wizardry.constants.Element;
import electroblob.wizardry.util.ParticleBuilder;
import net.minecraft.util.ResourceLocation;

import java.util.Map;

public class WarlockSpellVisuals {

	public static final Map<Element, int[]> PARTICLE_COLOURS = ImmutableMap.<Element, int[]>builder()
			.put(Element.MAGIC, new int[]{0xfc0303, 0x400101, 0x9d2cf3})
			.put(Element.FIRE, new int[]{0xff9600, 0xfffe67, 0xd02700})
			.put(Element.ICE, new int[]{0xa3e8f4, 0xe9f9fc, 0x138397})
			.put(Element.LIGHTNING, new int[]{0x409ee1, 0xf5f0ff, 0x225474})
			.put(Element.NECROMANCY, new int[]{0xa811ce, 0xf575f5, 0x382366})
			.put(Element.EARTH, new int[]{0xa8f408, 0xc8ffb2, 0x795c28})
			.put(Element.SORCERY, new int[]{0x56e8e3, 0xe8fcfc, 0x16a64d})
			.put(Element.HEALING, new int[]{0xfff69e, 0xfffff6, 0xa18200})
			.build();

	public static final ImmutableMap<Element, ResourceLocation> ELEMENTAL_PARTICLES = ImmutableMap.<Element, ResourceLocation>builder()
			.put(Element.MAGIC, ParticleBuilder.Type.SPARKLE)
			.put(Element.FIRE, ParticleBuilder.Type.MAGIC_FIRE)
			.put(Element.ICE, ParticleBuilder.Type.SNOW)
			.put(Element.LIGHTNING, ParticleBuilder.Type.LIGHTNING)
			.put(Element.NECROMANCY, ParticleBuilder.Type.DARK_MAGIC)
			.put(Element.EARTH, ParticleBuilder.Type.LEAF)
			.put(Element.SORCERY, ParticleBuilder.Type.FLASH)
			.put(Element.HEALING, ParticleBuilder.Type.SPARKLE)
			.build();
}
