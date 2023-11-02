package com.windanesz.ancientspellcraft.client;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.CommonProxy;
import com.windanesz.ancientspellcraft.Settings;
import com.windanesz.ancientspellcraft.block.BlockCrystalLeaves;
import com.windanesz.ancientspellcraft.block.BlockMagicMushroom;
import com.windanesz.ancientspellcraft.client.model.ItemColorizer;
import com.windanesz.ancientspellcraft.client.particle.ParticleConstantBeam;
import com.windanesz.ancientspellcraft.client.particle.ParticleSoulChain;
import com.windanesz.ancientspellcraft.client.particle.ParticleTimeKnot;
import com.windanesz.ancientspellcraft.client.renderer.RenderArcaneBarrierEventBased;
import com.windanesz.ancientspellcraft.client.renderer.RenderMerchantWizard;
import com.windanesz.ancientspellcraft.client.renderer.entity.RenderAnimatedItem;
import com.windanesz.ancientspellcraft.client.renderer.entity.RenderDevoritiumArrow;
import com.windanesz.ancientspellcraft.client.renderer.entity.RenderEntityMageLight;
import com.windanesz.ancientspellcraft.client.renderer.entity.RenderEvilClassWizard;
import com.windanesz.ancientspellcraft.client.renderer.entity.RenderFireSpider;
import com.windanesz.ancientspellcraft.client.renderer.entity.RenderMoltenBoulder;
import com.windanesz.ancientspellcraft.client.renderer.entity.RenderSkeletonHorseMinion;
import com.windanesz.ancientspellcraft.client.renderer.entity.RenderSkeletonMage;
import com.windanesz.ancientspellcraft.client.renderer.entity.RenderSpiritBear;
import com.windanesz.ancientspellcraft.client.renderer.entity.RenderStoneGuardian;
import com.windanesz.ancientspellcraft.client.renderer.entity.RenderVoidCreeper;
import com.windanesz.ancientspellcraft.client.renderer.entity.RenderVolcano;
import com.windanesz.ancientspellcraft.client.renderer.entity.RenderWisp;
import com.windanesz.ancientspellcraft.client.renderer.entity.RenderWolfMinion;
import com.windanesz.ancientspellcraft.client.renderer.entity.layers.LayerFire;
import com.windanesz.ancientspellcraft.client.renderer.tileentity.RenderArcaneWall;
import com.windanesz.ancientspellcraft.client.renderer.tileentity.RenderRune;
import com.windanesz.ancientspellcraft.client.renderer.tileentity.RenderSageLectern;
import com.windanesz.ancientspellcraft.client.renderer.tileentity.RenderSkullWatch;
import com.windanesz.ancientspellcraft.client.renderer.tileentity.RenderTileSentinel;
import com.windanesz.ancientspellcraft.client.renderer.tileentity.RenderTileSphereCognizance;
import com.windanesz.ancientspellcraft.data.RitualDiscoveryData;
import com.windanesz.ancientspellcraft.entity.EntityMageLight;
import com.windanesz.ancientspellcraft.entity.EntityWisp;
import com.windanesz.ancientspellcraft.entity.construct.EntityAntiMagicField;
import com.windanesz.ancientspellcraft.entity.construct.EntityArcaneBarrier;
import com.windanesz.ancientspellcraft.entity.construct.EntityBarterConstruct;
import com.windanesz.ancientspellcraft.entity.construct.EntityBuilder;
import com.windanesz.ancientspellcraft.entity.construct.EntityHealingSigil;
import com.windanesz.ancientspellcraft.entity.construct.EntityMoltenBoulder;
import com.windanesz.ancientspellcraft.entity.construct.EntitySilencingSigil;
import com.windanesz.ancientspellcraft.entity.construct.EntitySpellTicker;
import com.windanesz.ancientspellcraft.entity.construct.EntitySpiritWard;
import com.windanesz.ancientspellcraft.entity.construct.EntityTransportationPortal;
import com.windanesz.ancientspellcraft.entity.living.EntityAnimatedItem;
import com.windanesz.ancientspellcraft.entity.living.EntityCreeperMinion;
import com.windanesz.ancientspellcraft.entity.living.EntityEvilClassWizard;
import com.windanesz.ancientspellcraft.entity.living.EntityFireAnt;
import com.windanesz.ancientspellcraft.entity.living.EntityPigZombieMinion;
import com.windanesz.ancientspellcraft.entity.living.EntitySkeletonHorseMinion;
import com.windanesz.ancientspellcraft.entity.living.EntitySkeletonMage;
import com.windanesz.ancientspellcraft.entity.living.EntitySkeletonMageMinion;
import com.windanesz.ancientspellcraft.entity.living.EntitySpellCaster;
import com.windanesz.ancientspellcraft.entity.living.EntitySpiritBear;
import com.windanesz.ancientspellcraft.entity.living.EntityStoneGuardian;
import com.windanesz.ancientspellcraft.entity.living.EntityVoidCreeper;
import com.windanesz.ancientspellcraft.entity.living.EntityVolcano;
import com.windanesz.ancientspellcraft.entity.living.EntityWizardMerchant;
import com.windanesz.ancientspellcraft.entity.living.EntityWolfMinion;
import com.windanesz.ancientspellcraft.entity.projectile.EntityAOEProjectile;
import com.windanesz.ancientspellcraft.entity.projectile.EntityContingencyProjectile;
import com.windanesz.ancientspellcraft.entity.projectile.EntityDevoritiumArrow;
import com.windanesz.ancientspellcraft.entity.projectile.EntityDevoritiumBomb;
import com.windanesz.ancientspellcraft.entity.projectile.EntityDispelGreaterMagic;
import com.windanesz.ancientspellcraft.entity.projectile.EntityDispelMagic;
import com.windanesz.ancientspellcraft.entity.projectile.EntityFlint;
import com.windanesz.ancientspellcraft.entity.projectile.EntityHeart;
import com.windanesz.ancientspellcraft.entity.projectile.EntityManaVortex;
import com.windanesz.ancientspellcraft.entity.projectile.EntityMasterBolt;
import com.windanesz.ancientspellcraft.entity.projectile.EntityMetamagicProjectile;
import com.windanesz.ancientspellcraft.entity.projectile.EntitySafeIceShard;
import com.windanesz.ancientspellcraft.entity.projectile.EntityStoneGuardianShard;
import com.windanesz.ancientspellcraft.integration.antiqueatlas.ASAntiqueAtlasIntegration;
import com.windanesz.ancientspellcraft.item.ItemNewArtefact;
import com.windanesz.ancientspellcraft.packet.PacketContinuousRitual;
import com.windanesz.ancientspellcraft.packet.PacketMushroomActivation;
import com.windanesz.ancientspellcraft.packet.PacketStartRitual;
import com.windanesz.ancientspellcraft.ritual.Ritual;
import com.windanesz.ancientspellcraft.tileentity.TileArcaneWall;
import com.windanesz.ancientspellcraft.tileentity.TileRune;
import com.windanesz.ancientspellcraft.tileentity.TileSageLectern;
import com.windanesz.ancientspellcraft.tileentity.TileSentinel;
import com.windanesz.ancientspellcraft.tileentity.TileSkullWatch;
import com.windanesz.ancientspellcraft.tileentity.TileSphereCognizance;
import com.windanesz.ancientspellcraft.util.ASParticles;
import electroblob.wizardry.Wizardry;
import electroblob.wizardry.client.gui.handbook.GuiWizardHandbook;
import electroblob.wizardry.client.particle.ParticleWizardry;
import electroblob.wizardry.client.renderer.entity.RenderBlank;
import electroblob.wizardry.client.renderer.entity.RenderMagicArrow;
import electroblob.wizardry.client.renderer.entity.RenderProjectile;
import electroblob.wizardry.client.renderer.entity.RenderSigil;
import electroblob.wizardry.client.renderer.entity.layers.LayerTiledOverlay;
import electroblob.wizardry.data.WizardData;
import electroblob.wizardry.item.ISpellCastingItem;
import electroblob.wizardry.item.ItemArtefact;
import electroblob.wizardry.spell.Spell;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreenBook;
import net.minecraft.client.renderer.entity.RenderCreeper;
import net.minecraft.client.renderer.entity.RenderPigZombie;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.Style;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.input.Keyboard;

import javax.annotation.Nullable;
import javax.swing.event.AncestorEvent;
import java.util.Collection;
import java.util.List;

@Mod.EventBusSubscriber(Side.CLIENT)
public class ClientProxy extends CommonProxy {
	private static final int TOOLTIP_WRAP_WIDTH = 140;

	public static KeyBinding KEY_ACTIVATE_CHARM_BAUBLE;
	public static KeyBinding KEY_ACTIVATE_RING1_BAUBLE;
	public static KeyBinding KEY_ACTIVATE_RING2_BAUBLE;
	public static KeyBinding KEY_ACTIVATE_RADIAL_SPELL_MENU;

	@SubscribeEvent
	public static void handleKeys(InputEvent ev) // Not a mistake, I want both kb & mouse events handled.
	{
		Minecraft mc = Minecraft.getMinecraft();

		if (!Settings.clientSettings.radial_menu_enabled) {
			return;
		}

		while (KEY_ACTIVATE_RADIAL_SPELL_MENU.isPressed()) {
			if (mc.currentScreen == null) {
				ItemStack inHand = mc.player.getHeldItemMainhand();
				if (inHand.getItem() instanceof ISpellCastingItem) {
					mc.displayGuiScreen(new GuiRadialMenu(inHand));
				}
			}
		}
	}

	static void wipeOpen() {
		while (KEY_ACTIVATE_RADIAL_SPELL_MENU.isPressed()) {
		}
	}

	@Override
	public void initialiseLayers() {
		LayerTiledOverlay.initialiseLayers(LayerFire::new);
	}

	public void init() {
		registerKeybindings();

		// init colored items
		ItemColorizer.init();
	}

	private void registerKeybindings() {
		// Initializing
		ClientRegistry.registerKeyBinding(KEY_ACTIVATE_RADIAL_SPELL_MENU = new KeyBinding("key.ancientspellcraft.open_radial_spell_menu", Keyboard.KEY_R, "key.ancientspellcraft.category"));
		//keyOpenToolMenu.

		KEY_ACTIVATE_CHARM_BAUBLE = new KeyBinding("key.ancientspellcraft.charm_bauble_activate", Keyboard.KEY_K, "key.ancientspellcraft.category");
		ClientRegistry.registerKeyBinding(KEY_ACTIVATE_CHARM_BAUBLE);

		KEY_ACTIVATE_RING1_BAUBLE = new KeyBinding("key.ancientspellcraft.charm_ring_1_activate", Keyboard.KEY_U, "key.ancientspellcraft.category");
		ClientRegistry.registerKeyBinding(KEY_ACTIVATE_RING1_BAUBLE);

		KEY_ACTIVATE_RING2_BAUBLE = new KeyBinding("key.ancientspellcraft.charm_ring_2_activate", Keyboard.KEY_J, "key.ancientspellcraft.category");
		ClientRegistry.registerKeyBinding(KEY_ACTIVATE_RING2_BAUBLE);
	}

	public void registerRenderers() {

		////////// CREATURES //////////
		RenderingRegistry.registerEntityRenderingHandler(EntityWisp.class, manager -> new RenderWisp(manager, 0.7f, new ResourceLocation(AncientSpellcraft.MODID, "textures/entity/wisp.png"), true));
		RenderingRegistry.registerEntityRenderingHandler(EntityMageLight.class, manager -> new RenderEntityMageLight(manager, 0.7f, new ResourceLocation(AncientSpellcraft.MODID, "textures/entity/wisp.png"), true));
		RenderingRegistry.registerEntityRenderingHandler(EntityVoidCreeper.class, RenderVoidCreeper::new);
		RenderingRegistry.registerEntityRenderingHandler(EntitySkeletonMageMinion.class, RenderSkeletonMage::new);
		RenderingRegistry.registerEntityRenderingHandler(EntitySkeletonMage.class, RenderSkeletonMage::new);
		RenderingRegistry.registerEntityRenderingHandler(EntitySkeletonHorseMinion.class, RenderSkeletonHorseMinion::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityWolfMinion.class, RenderWolfMinion::new);
		RenderingRegistry.registerEntityRenderingHandler(EntitySpiritBear.class, RenderSpiritBear::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityFireAnt.class, RenderFireSpider::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityCreeperMinion.class, RenderCreeper::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityPigZombieMinion.class, RenderPigZombie::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityStoneGuardian.class, RenderStoneGuardian::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityVolcano.class, RenderVolcano::new);

		////////// Throwables and projectiles //////////
		RenderingRegistry.registerEntityRenderingHandler(EntityDispelMagic.class, manager -> new RenderProjectile(manager, 0.4f, new ResourceLocation(AncientSpellcraft.MODID, "textures/entity/dispel_magic.png"), false));
		RenderingRegistry.registerEntityRenderingHandler(EntityDispelGreaterMagic.class, manager -> new RenderProjectile(manager, 0.7f, new ResourceLocation(AncientSpellcraft.MODID, "textures/entity/dispel_magic.png"), false));
		RenderingRegistry.registerEntityRenderingHandler(EntityContingencyProjectile.class, manager -> new RenderProjectile(manager, 0.7f, new ResourceLocation(AncientSpellcraft.MODID, "textures/entity/contingency_projectile.png"), true));
		RenderingRegistry.registerEntityRenderingHandler(EntityMetamagicProjectile.class, manager -> new RenderProjectile(manager, 0.7f, new ResourceLocation(AncientSpellcraft.MODID, "textures/entity/dispel_magic.png"), false));
		RenderingRegistry.registerEntityRenderingHandler(EntityHeart.class, manager -> new RenderProjectile(manager, 0.7f, new ResourceLocation(AncientSpellcraft.MODID, "textures/entity/healing_heart.png"), false));
		RenderingRegistry.registerEntityRenderingHandler(EntityDevoritiumBomb.class, manager -> new RenderProjectile(manager, 0.6f, new ResourceLocation(AncientSpellcraft.MODID, "textures/items/devoritium_bomb.png"), false));
		RenderingRegistry.registerEntityRenderingHandler(EntityFlint.class, manager -> new RenderMagicArrow(manager, new ResourceLocation(AncientSpellcraft.MODID, "textures/entity/flint_shard.png"), false, 6.0, 1.0, 9, 8, true));
		RenderingRegistry.registerEntityRenderingHandler(EntityAOEProjectile.class, manager -> new RenderProjectile(manager, 0.7f, new ResourceLocation(AncientSpellcraft.MODID, "textures/entity/aoe_projectile.png"), false));
		RenderingRegistry.registerEntityRenderingHandler(EntitySafeIceShard.class, manager -> new RenderMagicArrow(manager,
				new ResourceLocation(Wizardry.MODID, "textures/entity/ice_shard.png"), false, 8.0, 2.0, 16, 5, false));
		RenderingRegistry.registerEntityRenderingHandler(EntityMasterBolt.class, manager -> new RenderMagicArrow(manager,
				new ResourceLocation(Wizardry.MODID, "textures/entity/lightning_arrow.png"), true, 8.0, 2.0, 16, 5, false));

		////////// TESRs //////////
		ClientRegistry.bindTileEntitySpecialRenderer(TileSphereCognizance.class, new RenderTileSphereCognizance());
		ClientRegistry.bindTileEntitySpecialRenderer(TileSkullWatch.class, new RenderSkullWatch());
		ClientRegistry.bindTileEntitySpecialRenderer(TileSentinel.class, new RenderTileSentinel());
		ClientRegistry.bindTileEntitySpecialRenderer(TileRune.class, new RenderRune());
		ClientRegistry.bindTileEntitySpecialRenderer(TileSageLectern.class, new RenderSageLectern());
		ClientRegistry.bindTileEntitySpecialRenderer(TileArcaneWall.class, new RenderArcaneWall());

		////////// Runes on ground //////////
		RenderingRegistry.registerEntityRenderingHandler(EntityTransportationPortal.class, manager -> new RenderSigil(manager, new ResourceLocation(AncientSpellcraft.MODID, "textures/entity/transportation_portal.png"), 0.0f, false));
		RenderingRegistry.registerEntityRenderingHandler(EntitySpiritWard.class, manager -> new RenderSigil(manager, new ResourceLocation(AncientSpellcraft.MODID, "textures/entity/spirit_ward.png"), 0.0f, false));
		RenderingRegistry.registerEntityRenderingHandler(EntitySilencingSigil.class, manager -> new RenderSigil(manager, new ResourceLocation(AncientSpellcraft.MODID, "textures/entity/silencing_sigil.png"), 0.0f, false));
		RenderingRegistry.registerEntityRenderingHandler(EntityHealingSigil.class, manager -> new RenderSigil(manager, new ResourceLocation(AncientSpellcraft.MODID, "textures/entity/healing_sigil.png"), 0.0f, false));

		////////// NO render //////////
		RenderingRegistry.registerEntityRenderingHandler(EntityAntiMagicField.class, RenderBlank::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityManaVortex.class, RenderBlank::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityDevoritiumArrow.class, RenderDevoritiumArrow::new);
		RenderingRegistry.registerEntityRenderingHandler(EntitySpellCaster.class, RenderBlank::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityWizardMerchant.class, RenderMerchantWizard::new);
		//RenderingRegistry.registerEntityRenderingHandler(EntityClassWizard.class, RenderWizard::new);
		//RenderingRegistry.registerEntityRenderingHandler(EntityEvilWizardAS.class, RenderEvilWizard::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityEvilClassWizard.class, RenderEvilClassWizard::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityBarterConstruct.class, RenderBlank::new);
		RenderingRegistry.registerEntityRenderingHandler(EntitySpellTicker.class, RenderBlank::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityBuilder.class, RenderBlank::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityAnimatedItem.class, RenderAnimatedItem::new);

		////////// Other constructs //////////
		RenderingRegistry.registerEntityRenderingHandler(EntityArcaneBarrier.class, RenderBlank::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityMoltenBoulder.class, RenderMoltenBoulder::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityStoneGuardianShard.class,
				manager -> new RenderProjectile(manager, 0.15f, new ResourceLocation(AncientSpellcraft.MODID, "textures/entity/stone_guardian_shard.png"), false));
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

		if (player == null) { return false; }

		if (player.isCreative()) { return true; }
		if (WizardData.get(player) != null && RitualDiscoveryData.hasRitualBeenDiscovered(player, ritual)) { return true; }

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

	@Override
	public void handleMushroomActivationPacket(PacketMushroomActivation.Message message) {
		World world = Minecraft.getMinecraft().world;
		BlockPos pos = message.pos;

		Block block = world.getBlockState(pos).getBlock();
		Entity entity = world.getEntityByID(message.activatorEntityID);

		if (block instanceof BlockMagicMushroom && (message.activatorEntityID != -1 && entity != null)) {
			((BlockMagicMushroom) block).applyEffect(world, block, pos, world.getBlockState(pos), entity);
		}
	}

	@Override
	public String getIceCreamDisplayName(ItemStack stack) {
		//noinspection ConstantConditions
		if (stack.hasTagCompound() && stack.getTagCompound().hasKey("potion")) {
			String potionString = stack.getTagCompound().getString("potion");

			Potion potion = ForgeRegistries.POTIONS.getValue(new ResourceLocation(potionString));
			if (potion != null) {
				String potionDisplayName = I18n.format(potion.getName());
				return I18n.format("item." + AncientSpellcraft.MODID + ":ice_cream.potion_name", potionDisplayName).trim();
			}
		}
		return net.minecraft.util.text.translation.I18n.translateToLocal("item.ancientspellcraft:ice_cream.name").trim();
	}

	public void registerAtlasMarkers() {
		ASAntiqueAtlasIntegration.registerMarkers();
	}

	/**
	 * Utility method to check if language keys are missing. Only runs in dev environment and the client side.
	 */
	@Override
	public void checkTranslationKeys() {

		if (!FMLLaunchHandler.isDeobfuscatedEnvironment()) {
			return;
		}

		// Checking spells for missing translation keys
		Collection<ResourceLocation> spells = Spell.getSpellNames();
		for (ResourceLocation resourceLocation : spells) {
			String registryName = resourceLocation.toString();
			String nameKey = "spell." + registryName;
			String descKey = nameKey + ".desc";

			String localizedName = net.minecraft.client.resources.I18n.format(nameKey);
			String localizedDesc = net.minecraft.client.resources.I18n.format(descKey);

			if (nameKey.equals(localizedName)) {
				missingKeyWarning("Spell", registryName, nameKey);
			}
			if (descKey.equals(localizedDesc)) {
				missingKeyWarning("Spell", registryName, descKey);
			}
		}

		// Checking items for missing translation keys
		for (Item item : Item.REGISTRY) {

			if (item.getRegistryName().getNamespace().equals(AncientSpellcraft.MODID)) {


				String registryName = item.getRegistryName().toString();
				String nameKey = "item." + registryName + ".name";

				if (item instanceof ItemBlock) {
					nameKey = "tile." + registryName + ".name";
				}

				String localizedName = net.minecraft.client.resources.I18n.format(nameKey);

				if (nameKey.equals(localizedName)) {
					missingKeyWarning("Item", registryName, nameKey);
				}

				if (item instanceof ItemArtefact || item instanceof ItemNewArtefact) {
					String descKey = "item." + registryName + ".desc";
					String localizedDesc = net.minecraft.client.resources.I18n.format(descKey);
					if (descKey.equals(localizedDesc)) {
						missingKeyWarning("Artefact", registryName, descKey);
					}
				}
			}
		}
	}

	private void missingKeyWarning(String type, String registryName, String expectedKey) {
		AncientSpellcraft.logger.warn(type + " " + registryName + " is missing a translation key: \"" + expectedKey + "\"");
	}

	public void openBookGUI(EntityPlayer player, ItemStack book) {
		Minecraft.getMinecraft().displayGuiScreen(new GuiScreenBook(Minecraft.getMinecraft().player, book, false));
	}

	@Override
	public void registerExtraHandbookContent() {
		GuiWizardHandbook.registerAddonHandbookContent(AncientSpellcraft.MODID);
	}
}