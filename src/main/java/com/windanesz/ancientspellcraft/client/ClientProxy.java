package com.windanesz.ancientspellcraft.client;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.CommonProxy;
import com.windanesz.ancientspellcraft.block.BlockCrystalLeaves;
import com.windanesz.ancientspellcraft.client.model.ItemColorizer;
import com.windanesz.ancientspellcraft.client.particle.ParticleConstantBeam;
import com.windanesz.ancientspellcraft.client.particle.ParticleSoulChain;
import com.windanesz.ancientspellcraft.client.particle.ParticleTimeKnot;
import com.windanesz.ancientspellcraft.client.renderer.RenderArcaneBarrierEventBased;
import com.windanesz.ancientspellcraft.client.renderer.RenderMerchantWizard;
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
import com.windanesz.ancientspellcraft.client.renderer.tileentity.RenderRune;
import com.windanesz.ancientspellcraft.client.renderer.tileentity.RenderSkullWatch;
import com.windanesz.ancientspellcraft.client.renderer.tileentity.RenderTileSentinel;
import com.windanesz.ancientspellcraft.client.renderer.tileentity.RenderTileSphereCognizance;
import com.windanesz.ancientspellcraft.data.RitualDiscoveryData;
import com.windanesz.ancientspellcraft.entity.EntityAOEProjectile;
import com.windanesz.ancientspellcraft.entity.EntityArcaneBarrier;
import com.windanesz.ancientspellcraft.entity.EntityMageLight;
import com.windanesz.ancientspellcraft.entity.EntityVolcano;
import com.windanesz.ancientspellcraft.entity.EntityWisp;
import com.windanesz.ancientspellcraft.entity.EntityWizardMerchant;
import com.windanesz.ancientspellcraft.entity.construct.EntityAntiMagicField;
import com.windanesz.ancientspellcraft.entity.construct.EntitySilencingSigil;
import com.windanesz.ancientspellcraft.entity.construct.EntitySpiritWard;
import com.windanesz.ancientspellcraft.entity.construct.EntityTransportationPortal;
import com.windanesz.ancientspellcraft.entity.living.EntityFireAnt;
import com.windanesz.ancientspellcraft.entity.living.EntitySkeletonHorseMinion;
import com.windanesz.ancientspellcraft.entity.living.EntitySkeletonMageMinion;
import com.windanesz.ancientspellcraft.entity.living.EntitySpellCaster;
import com.windanesz.ancientspellcraft.entity.living.EntitySpiritBear;
import com.windanesz.ancientspellcraft.entity.living.EntityVoidCreeper;
import com.windanesz.ancientspellcraft.entity.living.EntityWolfMinion;
import com.windanesz.ancientspellcraft.entity.projectile.EntityContingencyProjectile;
import com.windanesz.ancientspellcraft.entity.projectile.EntityDevoritiumArrow;
import com.windanesz.ancientspellcraft.entity.projectile.EntityDevoritiumBomb;
import com.windanesz.ancientspellcraft.entity.projectile.EntityDispelGreaterMagic;
import com.windanesz.ancientspellcraft.entity.projectile.EntityDispelMagic;
import com.windanesz.ancientspellcraft.entity.projectile.EntityFlint;
import com.windanesz.ancientspellcraft.entity.projectile.EntityHeart;
import com.windanesz.ancientspellcraft.entity.projectile.EntityManaVortex;
import com.windanesz.ancientspellcraft.entity.projectile.EntityMetamagicProjectile;
import com.windanesz.ancientspellcraft.packet.PacketContinuousRitual;
import com.windanesz.ancientspellcraft.packet.PacketStartRitual;
import com.windanesz.ancientspellcraft.ritual.Ritual;
import com.windanesz.ancientspellcraft.tileentity.EntityBarter;
import com.windanesz.ancientspellcraft.tileentity.TileRune;
import com.windanesz.ancientspellcraft.tileentity.TileSentinel;
import com.windanesz.ancientspellcraft.tileentity.TileSkullWatch;
import com.windanesz.ancientspellcraft.tileentity.TileSphereCognizance;
import com.windanesz.ancientspellcraft.util.ASParticles;
import electroblob.wizardry.Wizardry;
import electroblob.wizardry.client.particle.ParticleWizardry;
import electroblob.wizardry.client.renderer.entity.RenderBlank;
import electroblob.wizardry.client.renderer.entity.RenderMagicArrow;
import electroblob.wizardry.client.renderer.entity.RenderProjectile;
import electroblob.wizardry.client.renderer.entity.RenderSigil;
import electroblob.wizardry.client.renderer.entity.layers.LayerTiledOverlay;
import electroblob.wizardry.data.WizardData;
import electroblob.wizardry.item.ISpellCastingItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.Style;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.input.Keyboard;

import javax.annotation.Nullable;
import java.util.List;

@Mod.EventBusSubscriber(Side.CLIENT)
public class ClientProxy extends CommonProxy {
	private static final int TOOLTIP_WRAP_WIDTH = 140;

	public static KeyBinding KEY_ACTIVATE_CHARM_BAUBLE;
	public static KeyBinding KEY_ACTIVATE_RING1_BAUBLE;
	public static KeyBinding KEY_ACTIVATE_RING2_BAUBLE;
	public static KeyBinding keyOpenToolMenu;

	public static KeyBinding yt;
	public static KeyBinding yt2;
	public static KeyBinding zt;
	public static KeyBinding zt2;
	public static KeyBinding xt;
	public static KeyBinding xt2;
	public static KeyBinding reset;
	public static KeyBinding switc;

	@Override
	public void initialiseLayers() {
		LayerTiledOverlay.initialiseLayers(LayerFire::new);
	}

	public void init() {

		ClientRegistry.registerKeyBinding(keyOpenToolMenu =
				new KeyBinding("key.ancientspellcraft.open", Keyboard.KEY_R, "key.ancientspellcraft.category"));
		//keyOpenToolMenu.

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

	@SubscribeEvent
	public static void handleKeys(InputEvent ev) // Not a mistake, I want both kb & mouse events handled.
	{
		Minecraft mc = Minecraft.getMinecraft();

		while (keyOpenToolMenu.isPressed()) {
			if (mc.currentScreen == null) {
				ItemStack inHand = mc.player.getHeldItemMainhand();
				if (inHand.getItem() instanceof ISpellCastingItem) {
					mc.displayGuiScreen(new GuiRadialMenu(inHand));
				}
			}
		}
	}

	static void wipeOpen() {
		while (keyOpenToolMenu.isPressed()) {
		}
	}

	public void registerRenderers() {

		// CREATURES
		RenderingRegistry.registerEntityRenderingHandler(EntityWisp.class, manager -> new RenderWisp(manager, 0.7f, new ResourceLocation(AncientSpellcraft.MODID, "textures/entity/wisp.png"), true));
		RenderingRegistry.registerEntityRenderingHandler(EntityMageLight.class, manager -> new RenderEntityMageLight(manager, 0.7f, new ResourceLocation(AncientSpellcraft.MODID, "textures/entity/wisp.png"), true));
		RenderingRegistry.registerEntityRenderingHandler(EntityVoidCreeper.class, RenderVoidCreeper::new);
		RenderingRegistry.registerEntityRenderingHandler(EntitySkeletonMageMinion.class, RenderSkeletonMage::new);
		RenderingRegistry.registerEntityRenderingHandler(EntitySkeletonHorseMinion.class, RenderSkeletonHorseMinion::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityWolfMinion.class, RenderWolfMinion::new);
		RenderingRegistry.registerEntityRenderingHandler(EntitySpiritBear.class, RenderSpiritBear::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityFireAnt.class, RenderFireSpider::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityVolcano.class, RenderVolcano::new);

		// Throwables and projectiles
		RenderingRegistry.registerEntityRenderingHandler(EntityDispelMagic.class, manager -> new RenderProjectile(manager, 0.4f, new ResourceLocation(AncientSpellcraft.MODID, "textures/entity/dispel_magic.png"), false));
		RenderingRegistry.registerEntityRenderingHandler(EntityDispelGreaterMagic.class, manager -> new RenderProjectile(manager, 0.7f, new ResourceLocation(AncientSpellcraft.MODID, "textures/entity/dispel_magic.png"), false));
		RenderingRegistry.registerEntityRenderingHandler(EntityContingencyProjectile.class, manager -> new RenderProjectile(manager, 0.7f, new ResourceLocation(AncientSpellcraft.MODID, "textures/entity/contingency_projectile.png"), true));
		RenderingRegistry.registerEntityRenderingHandler(EntityMetamagicProjectile.class, manager -> new RenderProjectile(manager, 0.7f, new ResourceLocation(AncientSpellcraft.MODID, "textures/entity/dispel_magic.png"), false));
		RenderingRegistry.registerEntityRenderingHandler(EntityHeart.class, manager -> new RenderProjectile(manager, 0.7f, new ResourceLocation(AncientSpellcraft.MODID, "textures/entity/healing_heart.png"), false));
		RenderingRegistry.registerEntityRenderingHandler(EntityDevoritiumBomb.class, manager -> new RenderProjectile(manager, 0.6f, new ResourceLocation(AncientSpellcraft.MODID, "textures/items/devoritium_bomb.png"), false));
		RenderingRegistry.registerEntityRenderingHandler(EntityFlint.class, manager -> new RenderMagicArrow(manager, new ResourceLocation(AncientSpellcraft.MODID, "textures/entity/flint_shard.png"), false, 6.0, 1.0, 9, 8, true));
		RenderingRegistry.registerEntityRenderingHandler(EntityAOEProjectile.class,
				manager -> new RenderProjectile(manager, 0.7f, new ResourceLocation(Wizardry.MODID, "textures/entity/fireball.png"), false));

		// TESRs
		ClientRegistry.bindTileEntitySpecialRenderer(TileSphereCognizance.class, new RenderTileSphereCognizance());
		ClientRegistry.bindTileEntitySpecialRenderer(TileSkullWatch.class, new RenderSkullWatch());
		ClientRegistry.bindTileEntitySpecialRenderer(TileSentinel.class, new RenderTileSentinel());
		ClientRegistry.bindTileEntitySpecialRenderer(TileRune.class, new RenderRune());

		// Runes on ground
		RenderingRegistry.registerEntityRenderingHandler(EntityTransportationPortal.class, manager -> new RenderSigil(manager, new ResourceLocation(AncientSpellcraft.MODID, "textures/entity/transportation_portal.png"), 0.0f, false));
		RenderingRegistry.registerEntityRenderingHandler(EntitySpiritWard.class, manager -> new RenderSigil(manager, new ResourceLocation(AncientSpellcraft.MODID, "textures/entity/spirit_ward.png"), 0.0f, false));
		RenderingRegistry.registerEntityRenderingHandler(EntitySilencingSigil.class, manager -> new RenderSigil(manager, new ResourceLocation(AncientSpellcraft.MODID, "textures/entity/silencing_sigil.png"), 0.0f, false));

		// NO render
		RenderingRegistry.registerEntityRenderingHandler(EntityAntiMagicField.class, RenderBlank::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityManaVortex.class, RenderBlank::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityDevoritiumArrow.class, RenderDevoritiumArrow::new);
		RenderingRegistry.registerEntityRenderingHandler(EntitySpellCaster.class, RenderBlank::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityWizardMerchant.class, RenderMerchantWizard::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityBarter.class, RenderBlank::new);

		// other constructs
		RenderingRegistry.registerEntityRenderingHandler(EntityArcaneBarrier.class, RenderBlank::new);
//		RenderingRegistry.registerEntityRenderingHandler(EntityArcaneBarrierProxy.class, RenderArcaneBarrierProxy::new);

	}

	@Override
	public void registerParticles() {
		ParticleWizardry.registerParticle(ASParticles.SOUL_CHAIN, ParticleSoulChain::new);
		ParticleWizardry.registerParticle(ASParticles.TIME_KNOT, ParticleTimeKnot::new);
		ParticleWizardry.registerParticle(ASParticles.CONSTANT_BEAM, ParticleConstantBeam::new);
	}

	@Override
	public void setGraphicsLevel(BlockCrystalLeaves block, boolean fancyEnabled) {
		block.setGraphicsLevel(fancyEnabled);
	}

	@Override
	public void handleRitualStartPacket(PacketStartRitual.Message message) {

		World world = Minecraft.getMinecraft().world;
		Entity caster = world.getEntityByID(message.casterID);
		Ritual ritual = Ritual.byNetworkID(message.ritualID);
		// Should always be true
		if (caster instanceof EntityPlayer) {

			BlockPos centerPos = new BlockPos(message.x, message.y, message.z);
			if (world.getTileEntity(centerPos) instanceof TileRune) {
				TileRune centerPiece = (TileRune) world.getTileEntity(centerPos);
				ritual.initialEffect(world, (EntityPlayer) caster, centerPiece);
			}

		} else {
			Wizardry.logger.warn("Receieved a PacketStartRitual, but the caster ID was not the ID of a player");
		}
	}

	@Override
	public void handleContinuousRitualPacket(PacketContinuousRitual.Message message) {
		World world = Minecraft.getMinecraft().world;
		Entity caster = world.getEntityByID(message.casterID);
		Ritual ritual = Ritual.byNetworkID(message.ritualID);
		// Should always be true
		if (caster instanceof EntityPlayer) {

			BlockPos centerPos = new BlockPos(message.x, message.y, message.z);
			if (world.getTileEntity(centerPos) instanceof TileRune) {
				TileRune centerPiece = (TileRune) world.getTileEntity(centerPos);
				ritual.effect(world, (EntityPlayer) caster, centerPiece);
			}

		} else {
			Wizardry.logger.warn("Receieved a PacketStartRitual, but the caster ID was not the ID of a player");
		}
	}

	@Override
	public boolean shouldDisplayDiscovered(Ritual ritual, @Nullable ItemStack stack) {

		EntityPlayerSP player = Minecraft.getMinecraft().player;

		if (player == null)
			return false;

		if (player.isCreative())
			return true;
		if (WizardData.get(player) != null && RitualDiscoveryData.hasRitualBeenDiscovered(player, ritual))
			return true;

		return false;
	}

	@Override
	public String translate(String key, Style style, Object... args) {
		return style.getFormattingCode() + I18n.format(key, args);
	}

	@Override
	public void handleRemoveBarrier(EntityArcaneBarrier entityArcaneBarrier) {
		RenderArcaneBarrierEventBased.barrierList.remove(entityArcaneBarrier);
	}

	@Override
	public void handleAddBarrier(EntityArcaneBarrier entityArcaneBarrier) {
		if (!RenderArcaneBarrierEventBased.barrierList.contains(entityArcaneBarrier) && Minecraft.getMinecraft().player != null && Minecraft.getMinecraft().player.dimension == entityArcaneBarrier.dimension) {
			RenderArcaneBarrierEventBased.barrierList.add(entityArcaneBarrier);
		}
	}

	@Override
	public void addMultiLineDescription(List<String> tooltip, String key, Style style, Object... args) {
		tooltip.addAll(Minecraft.getMinecraft().fontRenderer.listFormattedStringToWidth(translate(key, style, args), TOOLTIP_WRAP_WIDTH));
	}

}