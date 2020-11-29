package com.windanesz.ancientspellcraft.item;

import com.windanesz.ancientspellcraft.integration.baubles.ASBaublesIntegration;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftTabs;
import electroblob.wizardry.Wizardry;
import electroblob.wizardry.integration.baubles.WizardryBaublesIntegration;
import electroblob.wizardry.util.InventoryUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * New artefact class type to add support for additional Bauble types (e.g. Belt, Head).
 * Because there is no API to add new wizardry bauble types unfortunately this means many classes needs to be copied from ItemBaubles
 * Author: WinDanesz
 * <p>
 * Most classes are based on {@link electroblob.wizardry.item.ItemArtefact} - Author: Electroblob
 */
public class ItemNewArtefact extends Item {

	private final EnumRarity rarity;
	private final AdditionalType type;

	/**
	 * False if this artefact has been disabled in the config, true otherwise.
	 */
	private boolean enabled = true;

	public ItemNewArtefact(EnumRarity rarity, AdditionalType type) {
		super();
		setMaxStackSize(1);
		setCreativeTab(AncientSpellcraftTabs.ANCIENTSPELLCRAFT_GEAR);
		this.rarity = rarity;
		this.type = type;
	}

	public enum AdditionalType {

		/** An artefact that fits into the Baubles BELT slot. One of these can be active at any one time. */ BELT(1),
		/** An artefact that fits into the Baubles HEAD slot. One of these can be active at any one time. */ HEAD(1);

		public final int maxAtOnce;

		AdditionalType(int maxAtOnce) {
			this.maxAtOnce = maxAtOnce;
		}
	}

	@Nullable
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt){
		return ASBaublesIntegration.enabled() ? new ASBaublesIntegration.ArtefactBaubleProvider(type) : null;
	}

	/**
	 * Sets whether this artefact is enabled or not.
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	public EnumRarity getRarity(ItemStack stack){
		return rarity;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack stack) {
		return rarity == EnumRarity.EPIC;
	}

	public AdditionalType getType() {
		return type;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, net.minecraft.client.util.ITooltipFlag advanced) {
		Wizardry.proxy.addMultiLineDescription(tooltip, "item." + this.getRegistryName() + ".desc");
		if (!enabled)
			tooltip.add(Wizardry.proxy.translate("item." + Wizardry.MODID + ":generic.disabled", new Style().setColor(TextFormatting.RED)));
	}

	/**
	 * THIS METHOD IS ONLY FOR THE NEW ARTEFACT TYPES!
	 * Returns whether the given artefact is active for the given player. If Baubles is loaded, an artefact is active
	 * when it is equipped in an appropriate bauble slot. If Baubles is not loaded, an artefact is active if it is one
	 * of the first n of its type on the player's hands/hotbar, where n is the number of bauble slots of that type.
	 * <p></p>
	 * {@link ItemNewArtefact#getActiveNewArtefacts(EntityPlayer, ItemNewArtefact.AdditionalType...)} cannot be used to query the new types.
	 *
	 * @param player   The player whose inventory is to be checked.
	 * @param artefact The artefact to check for.
	 * @return True if the player has the artefact and it is active, false if not. Always returns false if the given
	 * item is not an instance of {@code ItemNewArtefact}.
	 * @throws IllegalArgumentException If the given item is not an artefact.
	 */
	// It's cleaner to cast to ItemNewArtefact here than wherever it is used - items can't be stored as ItemWhatever objects
	public static boolean isNewArtefactActive(EntityPlayer player, Item artefact) {

		if (!(artefact instanceof ItemNewArtefact))
			throw new IllegalArgumentException("Not an ItemNewArtefact!");

		if (!((ItemNewArtefact) artefact).enabled)
			return false; // Disabled in the config

		if (WizardryBaublesIntegration.enabled()) {
			return WizardryBaublesIntegration.isBaubleEquipped(player, artefact);
		} else {
			// To find out if the given artefact is one of the first n on the player's hotbar (where n is the maximum
			// number of that kind of artefact that can be active at once):
			return InventoryUtils.getPrioritisedHotbarAndOffhand(player).stream() // Retrieve the stacks in question
					// Filter out all except artefacts of the same type as the given one (preserving order)
					.filter(s -> s.getItem() instanceof ItemNewArtefact && ((ItemNewArtefact) s.getItem()).type == ((ItemNewArtefact) artefact).type)
					.limit(((ItemNewArtefact) artefact).type.maxAtOnce)    // Ignore all but the first n
					.anyMatch(s -> s.getItem() == artefact); // Check if the remaining stacks contain the artefact
			// Note that streaming a list DOES retain the order (unless you call unordered(), obviously)
		}
	}


	/**
	 * Returns the currently active artefacts for the given player. If Baubles is loaded, an artefact is active
	 * when it is equipped in an appropriate bauble slot. If Baubles is not loaded, an artefact is active if it is one
	 * of the first n of its type on the player's hands/hotbar, where n is the number of bauble slots of that type.
	 * <p></p>
	 * This method is more efficient for processing multiple artefact behaviours at once.
	 *
	 * @param player The player whose inventory is to be checked.
	 * @param types The artefact types to check for. If omitted, all artefact types will be checked.
	 * @return True if the player has the artefact and it is active, false if not. Always returns false if the given
	 * item is not an instance of {@code ItemNewArtefact}.
	 */
	public static List<ItemNewArtefact> getActiveNewArtefacts(EntityPlayer player, ItemNewArtefact.AdditionalType... types){

		if(types.length == 0) types = ItemNewArtefact.AdditionalType.values();

		if(WizardryBaublesIntegration.enabled()){
			List<ItemNewArtefact> artefacts = ASBaublesIntegration.getEquippedArtefacts(player, types);
//			artefacts.removeIf(i -> !i.enabled); // Remove artefacts that are disabled in the config
			return artefacts;
		}else{

			List<ItemNewArtefact> artefacts = new ArrayList<>();

			for(ItemNewArtefact.AdditionalType type : types){
				artefacts.addAll(InventoryUtils.getPrioritisedHotbarAndOffhand(player).stream()
						.filter(s -> s.getItem() instanceof ItemNewArtefact)
						.map(s -> (ItemNewArtefact)s.getItem())
						.filter(i -> type == i.type && i.enabled)
						.limit(type.maxAtOnce)
						.collect(Collectors.toList()));
			}

			return artefacts;
		}
	}

}
