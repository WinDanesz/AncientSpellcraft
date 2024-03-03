package com.windanesz.ancientspellcraft;

import com.google.common.collect.Lists;
import zone.rong.mixinbooter.ILateMixinLoader;

import java.util.List;

@SuppressWarnings("unused")
public class LateMixinLoader implements ILateMixinLoader {

	@Override
	public List<String> getMixinConfigs() {
		return Lists.newArrayList("ancientspellcraft.mixins.json");
	}

	@Override
	public boolean shouldMixinConfigQueue(String mixinConfig) {
		return mixinConfig.equals("ancientspellcraft.mixins.json");
	}
}
