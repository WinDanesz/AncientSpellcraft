package com.windanesz.ancientspellcraft.ritual;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.packet.ASPacketHandler;
import com.windanesz.ancientspellcraft.packet.PacketContinuousRitual;
import com.windanesz.ancientspellcraft.packet.PacketStartRitual;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftBlocks;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftItems;
import com.windanesz.ancientspellcraft.registry.Rituals;
import com.windanesz.ancientspellcraft.tileentity.TileRune;
import com.windanesz.ancientspellcraft.util.RitualProperties;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.BlockUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Generic Ritual class for all rituals, implementing a forge registry. Based on {@link electroblob.wizardry.registry.Spells}.
 *
 * @author WinDanesz; most of the helper methods are Eletroblob's work
 * @since AncientSpellcraft 1.2.0
 */
public abstract class Ritual extends IForgeRegistryEntry.Impl<Ritual> {

	public static IForgeRegistry<Ritual> registry;

	/**
	 * A reference to the global ritual properties for this ritual, so they are only loaded once.
	 */
	private RitualProperties globalProperties;

	/** List of items for which this spell is applicable (used by default behaviour of {@link Spell#applicableForItem(Item)}). */
	protected Item[] applicableItems;

	/**
	 * Used in initialisation.
	 */
	private Set<String> propertyKeys = new HashSet<>();

	/**
	 * The unlocalised name of the ritual.
	 */
	private final String unlocalisedName;

	public RitualProperties getProperties() {
		return properties;
	}

	/**
	 * This ritual's associated RitualProperties object.
	 */
	private RitualProperties properties;

	/**
	 * False if the ritual has been disabled in the AS config file, true otherwise.
	 */
	private boolean enabled = true;

	private static int nextRitualId = 0;
	/**
	 * The ritual's integer ID, mainly used for networking.
	 */
	private final int id;

	public Ritual(String modID, String name, EnumAction action, boolean isContinuous) {
		this.setRegistryName(modID, name);
		this.unlocalisedName = this.getRegistryName().toString();
		this.id = nextRitualId++;
		this.items(AncientSpellcraftItems.ritual_book);
	}

	/**
	 * Returns a list containing all rituals matching the given {@link Predicate}. The returned list is separate from the
	 * internal rituals list; any changes you make to the returned list will have no effect on wizardry since the
	 * returned list is local to this method. Never includes the None ritual.
	 *
	 * @param filter A <code>Predicate&ltSpell&gt</code> that the returned rituals must satisfy.
	 * @return A <b>local, modifiable</b> list of rituals matching the given predicate. <i>Note that this list may be
	 * empty.</i>
	 */
	public static List<Ritual> getRituals(Predicate<Ritual> filter) {
		return registry.getValuesCollection().stream().filter(filter.and(s -> s != Rituals.none)).collect(Collectors.toList());
	}

	public static String getRegistryNameString(Ritual ritual) {
		return ritual.getRegistryName().toString();
	}

	public String getDescription(){
		return AncientSpellcraft.proxy.translate(getDescriptionTranslationKey());
	}

	/** Returns the translation key for this spell's description. */
	protected String getDescriptionTranslationKey(){
		return "ritual." + unlocalisedName + ".desc";
	}

	// ============================================ Ritual specific methods ==============================================

	/**
	 * Called only for the first tick of this ritual
	 */
	public void initialEffect(World world, EntityPlayer caster, TileRune centerPiece) {}

	/**
	 * Called continuously after the initial effect
	 */
	public void effect(World world, EntityPlayer caster, TileRune centerPiece) {
		if (!world.isRemote) {
			if (isFirstTick(centerPiece)) {
				initialEffect(world, caster, centerPiece);
				IMessage msg = new PacketStartRitual.Message(caster.getEntityId(), this, centerPiece.getX(), centerPiece.getY(), centerPiece.getZ());
				ASPacketHandler.net.sendToDimension(msg, caster.world.provider.getDimension());
				return;
			} else if (caster != null) {
				IMessage msg = new PacketContinuousRitual.Message(caster.getEntityId(), this, centerPiece.getX(), centerPiece.getY(), centerPiece.getZ());
				ASPacketHandler.net.sendToDimension(msg, caster.world.provider.getDimension());
			}
			if (BlockUtils.canBlockBeReplaced(world, centerPiece.getPos().up())) {
				world.setBlockState(centerPiece.getPos().up(), AncientSpellcraftBlocks.MAGELIGHT.getDefaultState());
			}
		}
	}

	public void onRitualFinish(World world, EntityPlayer caster, TileRune centerPiece) {
		//		List<BlockPos> posList = centerPiece.getConnectedRunes().stream().filter(r -> r != centerPiece).map(TileRune::getPos).collect(Collectors.toList());
		if (!world.isRemote) {
			List<BlockPos> posList = centerPiece.getConnectedRunes().stream().map(TileRune::getPos).collect(Collectors.toList());
			for (BlockPos pos : posList) {
				if (world.getBlockState(pos).getBlock() == AncientSpellcraftBlocks.PLACED_RUNE) {
					world.setBlockState(pos, AncientSpellcraftBlocks.RUNE_USED.getDefaultState());
				}
			}
		}
	}

	public boolean isFirstTick(TileRune centerPiece) {
		return centerPiece.getRitualCurrentLifeTime() == 1;
	}

	// ============================================ Getter methods ==============================================

	public final NonNullList<Ingredient> getPattern() { return properties.pattern; }

	public final RitualProperties getRitualProperties() { return properties; }

	public final int getMaxLifeTime() { return properties.lifetime; }

	// ========================================= Initialisation methods ===========================================

	/**
	 * Called from {@code init()} in the main mod class. Used to initialise ritual fields and properties that depend on
	 * other things being registered (e.g. potions). <i>Always initialise things in the constructor wherever possible.</i>
	 */
	public void init() {}

	/**
	 * Returns true if this ritual's properties have been initialised, false if not. Check this if you're attempting
	 * to access them from code that could be called before wizardry's {@code init()} method (e.g. item attributes).
	 */
	public final boolean arePropertiesInitialised() {
		return properties != null;
	}

	/**
	 * Internal, do not use.
	 */
	public final String[] getPropertyKeys() {
		return propertyKeys.toArray(new String[0]);
	}

	/**
	 * Sets this ritual's properties to the given {@link RitualProperties} object, but only if it doesn't already
	 * have one. This prevents ritual properties from being changed after initialisation.
	 */
	public void setProperties(@Nonnull RitualProperties properties) {

		if (!arePropertiesInitialised()) {
			this.properties = properties;
			if (this.globalProperties == null)
				this.globalProperties = properties;
		} else {
			AncientSpellcraft.logger.info("A mod attempted to set a ritual's properties, but they were already initialised.");
		}
	}

	// ============================================ Static methods ==============================================

	/**
	 * Returns all registered spells, excluding the none ritual.
	 */
	public static List<Ritual> getAllRituals() {
		return getRituals(s -> true);
	}

	/**
	 * Gets a ritual instance from its network ID, or the {@link com.windanesz.ancientspellcraft.ritual.None} ritual if no such spell exists.
	 */
	public static Ritual byNetworkID(int id) {
		if (id < 0 || id >= registry.getValuesCollection().size()) {
			return Rituals.none;
		}
		return registry.getValuesCollection().stream().filter(s -> s.id == id).findAny().orElse(Rituals.none);
	}

	public final int networkID() {
		return id;
	}

	// misc

	protected void ruinNonCenterPieceRunes(TileRune centerPiece, World world) {
		if (!world.isRemote) {
			List<BlockPos> alist = centerPiece.getRitualRunes().stream().filter(Objects::nonNull).filter(r -> r != centerPiece).map(TileRune::getPos).collect(Collectors.toList());

			for (BlockPos pos : alist) {
				if (world.getBlockState(pos).getBlock() == AncientSpellcraftBlocks.PLACED_RUNE) {
					world.removeTileEntity(pos);
					world.setBlockState(pos, AncientSpellcraftBlocks.RUNE_USED.getDefaultState());
				}
			}
		}
	}

	/** Returns true if the given item has a variant for this ritual. By default, returns true if the given item is
	 * in this spell's {@link Ritual#applicableItems} list (set using {@link Ritual#items(Item...)}). Override to do
	 * something more complex. */
	public boolean applicableForItem(Item item){
		return Arrays.asList(applicableItems).contains(item);
	}

	/**
	 * Sets which items this ritual can appear on (these default to the regular ritual book).
	 * @param applicableItems The items this ritual should naturally appear on (or no items at all).
	 * @return The spell instance, allowing this method to be chained onto the constructor. Note that since this method
	 * only returns a {@code Ritual}, if you are chaining multiple methods onto the constructor this should be called last.
	 */
	public Ritual items(Item... applicableItems){
		this.applicableItems = applicableItems;
		return this;
	}

	public NBTTagCompound getNBT() {
		NBTTagCompound compound = new NBTTagCompound();
		compound.setString("ritual", this.getRegistryName().toString());
		return compound;
	}

	/**
	 * Returns the translated display name of the spell, without formatting (i.e. not coloured). <b>Client-side
	 * only!</b> On the server side, use {@link TextComponentTranslation} (see {@link Spell#getNameForTranslation()}).
	 */
	public String getDisplayName(){
		return AncientSpellcraft.proxy.translate(getTranslationKey());
	}

	/** Returns the translation key for this spell. */
	protected String getTranslationKey(){
		return "ritual." + unlocalisedName;
	}

}
