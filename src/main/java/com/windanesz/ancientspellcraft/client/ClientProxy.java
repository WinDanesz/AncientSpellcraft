package com.windanesz.ancientspellcraft.client;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.CommonProxy;
import com.windanesz.ancientspellcraft.client.renderer.entity.RenderWisp;
import com.windanesz.ancientspellcraft.entity.EntityWisp;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

public class ClientProxy extends CommonProxy {

	public void registerRenderers() {

		// wisp
		RenderingRegistry.registerEntityRenderingHandler(EntityWisp.class,
				manager -> new RenderWisp(manager, 0.7f, new ResourceLocation(AncientSpellcraft.MODID, "textures/entity/wisp.png"), true));

	}

}

