package com.windanesz.ancientspellcraft.integration.artemislib;

import com.artemis.artemislib.util.attributes.ArtemisLibAttributes;
import com.windanesz.ancientspellcraft.Settings;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraftforge.fml.common.Loader;

import javax.annotation.Nullable;

public final class ASArtemisLibIntegration {

	private ASArtemisLibIntegration() {} // no instances

	public static final String ARTEMISLIB_MOD_ID = "artemislib";

	private static boolean artemisLibLoaded;

	public static void init() {

		artemisLibLoaded = Loader.isModLoaded(ARTEMISLIB_MOD_ID);

		if (!enabled())
			return;
	}

	public static boolean enabled() {
		return Settings.generalSettings.artemislib_integration && artemisLibLoaded;
	}

	@Nullable
	public static IAttribute getHeightAttribute() {
		return enabled() ? ArtemisLibAttributes.ENTITY_HEIGHT : null;
	}

	@Nullable
	public static IAttribute getWidthAttribute() {
		return enabled() ? ArtemisLibAttributes.ENTITY_WIDTH : null;
	}

}
