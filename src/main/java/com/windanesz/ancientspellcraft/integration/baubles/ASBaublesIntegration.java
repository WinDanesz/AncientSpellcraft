package com.windanesz.ancientspellcraft.integration.baubles;

import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import baubles.api.IBauble;
import baubles.api.cap.BaublesCapabilities;
import com.windanesz.ancientspellcraft.Settings;
import com.windanesz.ancientspellcraft.item.ItemNewArtefact;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.common.Loader;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * This class is a replica of Wizardry's Baubles integration {@link electroblob.wizardry.integration.baubles},
 * its necessary to add support to new bauble types till an API exists for {@link electroblob.wizardry.item.ItemArtefact.Type}.
 *
 * @author Electroblob, WinDanesz
 */
public final class ASBaublesIntegration {

	public static final String BAUBLES_MOD_ID = "baubles";

	private static final Map<ItemNewArtefact.AdditionalType, BaubleType> ARTEFACT_TYPE_MAP = new EnumMap<>(ItemNewArtefact.AdditionalType.class);

	private static boolean baublesLoaded;

	public static void init(){

		baublesLoaded = Loader.isModLoaded(BAUBLES_MOD_ID);

		if(!enabled()) return;

		ARTEFACT_TYPE_MAP.put(ItemNewArtefact.AdditionalType.BELT, BaubleType.BELT);
		ARTEFACT_TYPE_MAP.put(ItemNewArtefact.AdditionalType.HEAD, BaubleType.HEAD);
	}

	public static boolean enabled(){
		return Settings.generalSettings.baubles_integration && baublesLoaded;
	}

	// Wrappers for BaublesApi methods

	/**
	 * Returns a list of artefact stacks equipped of the given types. <i>This method does not check whether artefacts
	 * have been disabled in the config! {ItemNewArtefact#getActiveArtefacts(EntityPlayer, ItemNewArtefact.AdditionalType...)}
	 * should be used instead of this method in nearly all cases.</i>
	 * @param player The player whose inventory is to be checked.
	 * @param types Zero or more artefact types to check for. If omitted, searches for all types.
	 * @return A list of equipped artefact {@code ItemStacks}.
	 */
	// This could return all ItemStacks, but if an artefact type is given this doesn't really make sense.
	public static List<ItemNewArtefact> getEquippedArtefacts(EntityPlayer player, ItemNewArtefact.AdditionalType... types){

		List<ItemNewArtefact> artefacts = new ArrayList<>();

		for(ItemNewArtefact.AdditionalType type : types){
			for(int slot : ARTEFACT_TYPE_MAP.get(type).getValidSlots()){
				ItemStack stack = BaublesApi.getBaublesHandler(player).getStackInSlot(slot);
				if(stack.getItem() instanceof ItemNewArtefact) artefacts.add((ItemNewArtefact)stack.getItem());
			}
		}

		return artefacts;
	}

	// Shamelessly copied from The Twilight Forest, with a few modifications
	@SuppressWarnings("unchecked")
	public static final class ArtefactBaubleProvider implements ICapabilityProvider {

		private BaubleType type;

		public ArtefactBaubleProvider(ItemNewArtefact.AdditionalType type){
			this.type = ARTEFACT_TYPE_MAP.get(type);
		}

		@Override
		public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing){
			return capability == BaublesCapabilities.CAPABILITY_ITEM_BAUBLE;
		}

		@Override
		public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing){
			// This lambda expression is an implementation of the entire IBauble interface
			return capability == BaublesCapabilities.CAPABILITY_ITEM_BAUBLE ? (T)(IBauble)itemStack -> type : null;
		}
	}

}
