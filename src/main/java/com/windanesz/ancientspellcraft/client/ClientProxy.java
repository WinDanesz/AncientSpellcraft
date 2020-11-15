package com.windanesz.ancientspellcraft.client;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.CommonProxy;
import com.windanesz.ancientspellcraft.block.BlockCrystalLeaves;
import com.windanesz.ancientspellcraft.client.particle.ParticleSoulChain;
import com.windanesz.ancientspellcraft.client.particle.ParticleTimeKnot;
import com.windanesz.ancientspellcraft.client.renderer.entity.RenderEntityMageLight;
import com.windanesz.ancientspellcraft.client.renderer.entity.RenderFireSpider;
import com.windanesz.ancientspellcraft.client.renderer.entity.RenderSkeletonMage;
import com.windanesz.ancientspellcraft.client.renderer.entity.RenderSpiritBear;
import com.windanesz.ancientspellcraft.client.renderer.entity.RenderVoidCreeper;
import com.windanesz.ancientspellcraft.client.renderer.entity.RenderVolcano;
import com.windanesz.ancientspellcraft.client.renderer.entity.RenderWisp;
import com.windanesz.ancientspellcraft.client.renderer.entity.RenderWolfMinion;
import com.windanesz.ancientspellcraft.client.renderer.tileentity.RenderSkullWatch;
import com.windanesz.ancientspellcraft.client.renderer.tileentity.RenderTileSphereCognizance;
import com.windanesz.ancientspellcraft.entity.EntityMageLight;
import com.windanesz.ancientspellcraft.entity.EntityVolcano;
import com.windanesz.ancientspellcraft.entity.EntityWisp;
import com.windanesz.ancientspellcraft.entity.construct.EntityAntiMagicField;
import com.windanesz.ancientspellcraft.entity.construct.EntitySilencingSigil;
import com.windanesz.ancientspellcraft.entity.construct.EntitySpiritWard;
import com.windanesz.ancientspellcraft.entity.construct.EntityTransportationPortal;
import com.windanesz.ancientspellcraft.entity.living.EntityFireAnt;
import com.windanesz.ancientspellcraft.entity.living.EntitySkeletonMageMinion;
import com.windanesz.ancientspellcraft.entity.living.EntitySpiritBear;
import com.windanesz.ancientspellcraft.entity.living.EntityVoidCreeper;
import com.windanesz.ancientspellcraft.entity.living.EntityWolfMinion;
import com.windanesz.ancientspellcraft.entity.projectile.EntityDispelMagic;
import com.windanesz.ancientspellcraft.entity.projectile.EntityHeart;
import com.windanesz.ancientspellcraft.tileentity.TileSkullWatch;
import com.windanesz.ancientspellcraft.tileentity.TileSphereCognizance;
import com.windanesz.ancientspellcraft.util.ASParticles;
import electroblob.wizardry.client.particle.ParticleWizardry;
import electroblob.wizardry.client.renderer.entity.RenderBlank;
import electroblob.wizardry.client.renderer.entity.RenderProjectile;
import electroblob.wizardry.client.renderer.entity.RenderSigil;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

public class ClientProxy extends CommonProxy {

	public void registerRenderers() {

		// CREATURES
		RenderingRegistry.registerEntityRenderingHandler(EntityWisp.class,
				manager -> new RenderWisp(manager, 0.7f, new ResourceLocation(AncientSpellcraft.MODID, "textures/entity/wisp.png"), true));

		RenderingRegistry.registerEntityRenderingHandler(EntityMageLight.class,
				manager -> new RenderEntityMageLight(manager, 0.7f, new ResourceLocation(AncientSpellcraft.MODID, "textures/entity/wisp.png"), true));

		RenderingRegistry.registerEntityRenderingHandler(EntityVoidCreeper.class,
				RenderVoidCreeper::new);

		RenderingRegistry.registerEntityRenderingHandler(EntitySkeletonMageMinion.class, RenderSkeletonMage::new);

		RenderingRegistry.registerEntityRenderingHandler(EntityWolfMinion.class, RenderWolfMinion::new);

		RenderingRegistry.registerEntityRenderingHandler(EntitySpiritBear.class, RenderSpiritBear::new);

		RenderingRegistry.registerEntityRenderingHandler(EntityFireAnt.class, RenderFireSpider::new);


		//		RenderingRegistry.registerEntityRenderingHandler(EntitySpectralFishHook.class, RenderSpectralFishHook::new);

		RenderingRegistry.registerEntityRenderingHandler(EntityDispelMagic.class,
				manager -> new RenderProjectile(manager, 0.7f, new ResourceLocation(AncientSpellcraft.MODID, "textures/entity/dispel_magic.png"), false));


		//throwables
		RenderingRegistry.registerEntityRenderingHandler(EntityHeart.class,
				manager -> new RenderProjectile(manager, 0.7f, new ResourceLocation(AncientSpellcraft.MODID, "textures/entity/healing_heart.png"), false));

		// TESRs
		ClientRegistry.bindTileEntitySpecialRenderer(TileSphereCognizance.class, new RenderTileSphereCognizance());
		ClientRegistry.bindTileEntitySpecialRenderer(TileSkullWatch.class, new RenderSkullWatch());

		// Runes on ground
		RenderingRegistry.registerEntityRenderingHandler(EntityTransportationPortal.class,
				manager -> new RenderSigil(manager, new ResourceLocation(AncientSpellcraft.MODID, "textures/entity/transportation_portal.png"), 0.0f, false));

		RenderingRegistry.registerEntityRenderingHandler(EntitySpiritWard.class,
				manager -> new RenderSigil(manager, new ResourceLocation(AncientSpellcraft.MODID, "textures/entity/spirit_ward.png"), 0.0f, false));

		RenderingRegistry.registerEntityRenderingHandler(EntitySilencingSigil.class,
				manager -> new RenderSigil(manager, new ResourceLocation(AncientSpellcraft.MODID, "textures/entity/silencing_sigil.png"), 0.0f, false));

		RenderingRegistry.registerEntityRenderingHandler(EntityVolcano.class, RenderVolcano::new);

//		RenderingRegistry.registerEntityRenderingHandler(EntitySpellBook.class, RenderSpellBook::new);


		// NO render
		RenderingRegistry.registerEntityRenderingHandler(EntityAntiMagicField.class, RenderBlank::new);

	}

	@Override
	public void registerParticles() {
		ParticleWizardry.registerParticle(ASParticles.SOUL_CHAIN, ParticleSoulChain::new);
		ParticleWizardry.registerParticle(ASParticles.TIME_KNOT, ParticleTimeKnot::new);
	}

	@Override
	public void setGraphicsLevel(BlockCrystalLeaves block, boolean fancyEnabled) {
		block.setGraphicsLevel(fancyEnabled);
	}

}

