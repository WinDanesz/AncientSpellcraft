package com.windanesz.ancientspellcraft.client;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.CommonProxy;
import com.windanesz.ancientspellcraft.block.BlockCrystalLeaves;
import com.windanesz.ancientspellcraft.client.model.ItemColorizer;
import com.windanesz.ancientspellcraft.client.particle.ParticleSoulChain;
import com.windanesz.ancientspellcraft.client.particle.ParticleTimeKnot;
import com.windanesz.ancientspellcraft.client.renderer.entity.RenderDevoritiumArrow;
import com.windanesz.ancientspellcraft.client.renderer.entity.RenderEntityMageLight;
import com.windanesz.ancientspellcraft.client.renderer.entity.RenderFireSpider;
import com.windanesz.ancientspellcraft.client.renderer.entity.RenderSkeletonHorseMinion;
import com.windanesz.ancientspellcraft.client.renderer.entity.RenderSkeletonMage;
import com.windanesz.ancientspellcraft.client.renderer.entity.RenderSpiritBear;
import com.windanesz.ancientspellcraft.client.renderer.entity.RenderVoidCreeper;
import com.windanesz.ancientspellcraft.client.renderer.entity.RenderVolcano;
import com.windanesz.ancientspellcraft.client.renderer.entity.RenderWisp;
import com.windanesz.ancientspellcraft.client.renderer.entity.RenderWolfMinion;
import com.windanesz.ancientspellcraft.client.renderer.entity.layers.LayerFire;
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
import com.windanesz.ancientspellcraft.entity.living.EntitySkeletonHorseMinion;
import com.windanesz.ancientspellcraft.entity.living.EntitySkeletonMageMinion;
import com.windanesz.ancientspellcraft.entity.living.EntitySpiritBear;
import com.windanesz.ancientspellcraft.entity.living.EntityVoidCreeper;
import com.windanesz.ancientspellcraft.entity.living.EntityWolfMinion;
import com.windanesz.ancientspellcraft.entity.projectile.EntityDevoritiumArrow;
import com.windanesz.ancientspellcraft.entity.projectile.EntityDevoritiumBomb;
import com.windanesz.ancientspellcraft.entity.projectile.EntityDispelMagic;
import com.windanesz.ancientspellcraft.entity.projectile.EntityFlint;
import com.windanesz.ancientspellcraft.entity.projectile.EntityHeart;
import com.windanesz.ancientspellcraft.entity.projectile.EntityManaVortex;
import com.windanesz.ancientspellcraft.tileentity.TileSkullWatch;
import com.windanesz.ancientspellcraft.tileentity.TileSphereCognizance;
import com.windanesz.ancientspellcraft.util.ASParticles;
import electroblob.wizardry.client.particle.ParticleWizardry;
import electroblob.wizardry.client.renderer.entity.RenderBlank;
import electroblob.wizardry.client.renderer.entity.RenderMagicArrow;
import electroblob.wizardry.client.renderer.entity.RenderProjectile;
import electroblob.wizardry.client.renderer.entity.RenderSigil;
import electroblob.wizardry.client.renderer.entity.layers.LayerTiledOverlay;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.Style;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.input.Keyboard;

@Mod.EventBusSubscriber(Side.CLIENT)
public class ClientProxy extends CommonProxy {

	public static KeyBinding KEY_OpenToolboxMenu;
	public static KeyBinding KEY_ACTIVATE_CHARM_BAUBLE;
	public static KeyBinding KEY_ACTIVATE_RING1_BAUBLE;
	public static KeyBinding KEY_ACTIVATE_RING2_BAUBLE;

	public static KeyBinding yt;
	public static KeyBinding yt2;
	public static KeyBinding zt;
	public static KeyBinding zt2;
	public static KeyBinding xt;
	public static KeyBinding xt2;
	public static KeyBinding reset;
	public static KeyBinding switc;


	@Override
	public void initialiseLayers(){
		LayerTiledOverlay.initialiseLayers(LayerFire::new);
	}


	public void init() {
		KEY_OpenToolboxMenu =
				new KeyBinding(
						"key.immersiveradialmenu.opentoolbox",
						Keyboard.KEY_R,
						"key.immersiveradialmenu.category"
				);
		ClientRegistry.registerKeyBinding(KEY_OpenToolboxMenu);

		KEY_ACTIVATE_CHARM_BAUBLE = new KeyBinding("key.ancientspellcraft.charm_bauble_activate", Keyboard.KEY_K, "key.ancientspellcraft.category");
		ClientRegistry.registerKeyBinding(KEY_ACTIVATE_CHARM_BAUBLE);

		KEY_ACTIVATE_RING1_BAUBLE = new KeyBinding("key.ancientspellcraft.charm_ring_1_activate", Keyboard.KEY_U, "key.ancientspellcraft.category");
		ClientRegistry.registerKeyBinding(KEY_ACTIVATE_RING1_BAUBLE);

		KEY_ACTIVATE_RING2_BAUBLE = new KeyBinding("key.ancientspellcraft.charm_ring_2_activate", Keyboard.KEY_J, "key.ancientspellcraft.category");
		ClientRegistry.registerKeyBinding(KEY_ACTIVATE_RING2_BAUBLE);

		switc = new KeyBinding("Enable / Disable", Keyboard.KEY_V, "FreeCam");
		ClientRegistry.registerKeyBinding(switc);
		reset = new KeyBinding("Reset", Keyboard.KEY_O, "FreeCam");
		ClientRegistry.registerKeyBinding(reset);
		yt = new KeyBinding("Up", Keyboard.KEY_SPACE, "FreeCam");
		ClientRegistry.registerKeyBinding(yt);
		yt2 = new KeyBinding("Down", Keyboard.KEY_RSHIFT, "FreeCam");
		ClientRegistry.registerKeyBinding(yt2);
		zt = new KeyBinding("Right", Keyboard.KEY_RIGHT, "FreeCam");
		ClientRegistry.registerKeyBinding(zt);
		zt2 = new KeyBinding("Left", Keyboard.KEY_LEFT, "FreeCam");
		ClientRegistry.registerKeyBinding(zt2);
		xt = new KeyBinding("Forward", Keyboard.KEY_UP, "FreeCam");
		ClientRegistry.registerKeyBinding(xt);
		xt2 = new KeyBinding("Backward", Keyboard.KEY_DOWN, "FreeCam");
		ClientRegistry.registerKeyBinding(xt2);

		// init colored items
		ItemColorizer.init();
	}

	public static void wipeOpen() {
		while (KEY_OpenToolboxMenu.isPressed()) {

		}
	}

	public void registerRenderers() {

		// CREATURES
		RenderingRegistry.registerEntityRenderingHandler(EntityWisp.class,
				manager -> new RenderWisp(manager, 0.7f, new ResourceLocation(AncientSpellcraft.MODID, "textures/entity/wisp.png"), true));

		RenderingRegistry.registerEntityRenderingHandler(EntityMageLight.class,
				manager -> new RenderEntityMageLight(manager, 0.7f, new ResourceLocation(AncientSpellcraft.MODID, "textures/entity/wisp.png"), true));

		RenderingRegistry.registerEntityRenderingHandler(EntityVoidCreeper.class,
				RenderVoidCreeper::new);

		RenderingRegistry.registerEntityRenderingHandler(EntitySkeletonMageMinion.class, RenderSkeletonMage::new);
		RenderingRegistry.registerEntityRenderingHandler(EntitySkeletonHorseMinion.class, RenderSkeletonHorseMinion::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityWolfMinion.class, RenderWolfMinion::new);
		RenderingRegistry.registerEntityRenderingHandler(EntitySpiritBear.class, RenderSpiritBear::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityFireAnt.class, RenderFireSpider::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityDispelMagic.class,
				manager -> new RenderProjectile(manager, 0.7f, new ResourceLocation(AncientSpellcraft.MODID, "textures/entity/dispel_magic.png"), false));

		// throwables
		RenderingRegistry.registerEntityRenderingHandler(EntityHeart.class,
				manager -> new RenderProjectile(manager, 0.7f, new ResourceLocation(AncientSpellcraft.MODID, "textures/entity/healing_heart.png"), false));

		RenderingRegistry.registerEntityRenderingHandler(EntityDevoritiumBomb.class,
				manager -> new RenderProjectile(manager, 0.6f, new ResourceLocation(AncientSpellcraft.MODID, "textures/items/devoritium_bomb.png"), false));

		RenderingRegistry.registerEntityRenderingHandler(EntityFlint.class, manager -> new RenderMagicArrow(manager,
				new ResourceLocation(AncientSpellcraft.MODID, "textures/entity/flint_shard.png"), false, 6.0, 1.0, 9, 8, true));

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

		// NO render
		RenderingRegistry.registerEntityRenderingHandler(EntityAntiMagicField.class, RenderBlank::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityManaVortex.class, RenderBlank::new);

		RenderingRegistry.registerEntityRenderingHandler(EntityDevoritiumArrow.class, RenderDevoritiumArrow::new);

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

	@Override
	public String translate(String key, Style style, Object... args) {
		return style.getFormattingCode() + I18n.format(key, args);
	}
}

