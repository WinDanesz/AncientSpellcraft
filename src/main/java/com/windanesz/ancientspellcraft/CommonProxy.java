package com.windanesz.ancientspellcraft;

import com.windanesz.ancientspellcraft.block.BlockCrystalLeaves;
import net.minecraft.util.text.Style;

public class CommonProxy {

	/**
	 * Called from init() in the main mod class to initialise the particle factories.
	 */
	public void registerParticles() {}

	/**
	 * Called from preInit() in the main mod class to initialise the renderers.
	 */
	public void registerRenderers() {}

	public void setGraphicsLevel(BlockCrystalLeaves block, boolean fancyEnabled) {}

	public void init() {}

	public void initialiseLayers(){}

	/**
	 * Translates the given key with no specified style. Client-side only; on the server this simply returns
	 * the given translation key. Useful whenever translation from common classes is required, e.g. item tooltips.
	 * @param key The unlocalised name to be translated.
	 * @param args The format arguments to pass into the translation, if any.
	 * @return The resulting translated text.
	 * author: Electroblob
	 */
	public String translate(String key, Object... args){
		return translate(key, new Style(), args);
	}

	/**
	 * Translates the given key and formats it with the given style. Client-side only; on the server this simply returns
	 * the given translation key. Useful whenever translation from common classes is required, e.g. item tooltips.
	 * @param key The unlocalised name to be translated.
	 * @param style The {@link Style} to use for the displayed text.
	 * @param args The format arguments to pass into the translation, if any.
	 * @return The resulting translated text.
     * author: Electroblob
	 */
	public String translate(String key, Style style, Object... args){
		return key;
	}

}
