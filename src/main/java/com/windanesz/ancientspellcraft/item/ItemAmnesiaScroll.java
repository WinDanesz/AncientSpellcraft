package com.windanesz.ancientspellcraft.item;

import com.windanesz.ancientspellcraft.Settings;
import com.windanesz.ancientspellcraft.registry.ASItems;
import electroblob.wizardry.entity.living.EntityWizard;
import electroblob.wizardry.item.ItemArtefact;
import electroblob.wizardry.item.ItemScroll;
import electroblob.wizardry.misc.WildcardTradeList;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.util.NBTExtras;
import electroblob.wizardry.util.ParticleBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber
public class ItemAmnesiaScroll extends ItemRareScroll {

	public static final String TRADES_TAG = "trades";

	public ItemAmnesiaScroll() {
		super();
	}

	@SubscribeEvent
	public static void onEntityInteractEvent(PlayerInteractEvent.EntityInteract event) {
		if (event.getTarget() instanceof EntityWizard) {
			if (event.getItemStack().getItem() == ASItems.amnesia_scroll) {
				handleAmnesiaScroll(event);
			} else if (ItemArtefact.isArtefactActive(event.getEntityPlayer(), ASItems.head_merchant)) {
				handleCrown(event);
			}
		}
	}

	private static void handleAmnesiaScroll(PlayerInteractEvent.EntityInteract event) {

		World world = event.getWorld();
		NBTTagCompound entityInNbt = event.getTarget().serializeNBT();
		boolean foundTrades = entityInNbt.hasKey(TRADES_TAG);

		if (event.getWorld().isRemote) {
			Vec3d origin = event.getTarget().getPositionEyes(1);
			for (int i = 0; i < 30; i++) {
				double x = origin.x - 1 + world.rand.nextDouble() * 2;
				double y = origin.y - 0.25 + world.rand.nextDouble() * 0.5;
				double z = origin.z - 1 + world.rand.nextDouble() * 2;
				if (world.rand.nextBoolean()) {
					ParticleBuilder.create(ParticleBuilder.Type.SPARKLE).pos(x, y, z)
							.vel(0, 0.1, 0).fade(0, 0, 0).spin(0.3f, 0.03f)
							.clr(140, 140, 140).spawn(world);
				} else {
					ParticleBuilder.create(ParticleBuilder.Type.SPARKLE).pos(x, y, z)
							.vel(0, 0.1, 0).fade(0, 0, 0).spin(0.3f, 0.03f)
							.clr(99, 1, 110).spawn(world);
				}
			}
		}

		if (foundTrades) {
			entityInNbt.removeTag(TRADES_TAG);
			spawnWizard(event, entityInNbt);
		}
	}

	private static void handleCrown(PlayerInteractEvent.EntityInteract event) {
		NBTTagCompound entityInNbt = event.getTarget().serializeNBT();
		boolean foundTrades = entityInNbt.hasKey(TRADES_TAG);
		if (foundTrades) {
			if (entityInNbt.hasKey(TRADES_TAG)) {
				NBTTagCompound tagCompound = entityInNbt.getCompoundTag(TRADES_TAG);
				MerchantRecipeList trades = new WildcardTradeList(tagCompound);
				boolean scrolls = false;
				for (MerchantRecipe recipe : trades) {
					if (recipe.getItemToBuy().getItem() instanceof ItemScroll) {
						scrolls = true;
						break;
					}
				}
				if (!scrolls) {
					List<Item> itemList = new ArrayList<>();
					for (String itemName : Settings.generalSettings.scroll_items_for_crown_of_the_merchant_king) {
						if (ForgeRegistries.ITEMS.containsKey(new ResourceLocation(itemName))) {
							itemList.add(ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemName)));
						}
					}

					for (Item scroll : itemList) {
						ItemStack scrollStack = new ItemStack(scroll, 1, OreDictionary.WILDCARD_VALUE);
						ItemStack crystalStack1 = new ItemStack(WizardryItems.magic_crystal, 1);
						MerchantRecipe rec = new MerchantRecipe(scrollStack, crystalStack1);

						boolean matches = false;
						for (MerchantRecipe r : trades) {
							if (r.getItemToBuy().getItem() == rec.getItemToBuy().getItem()) {
								matches = true;
							}
						}

						if (!matches) {
							trades.add(rec);
						}
					}
					entityInNbt.removeTag(TRADES_TAG);
					NBTExtras.storeTagSafely(entityInNbt, TRADES_TAG, trades.getRecipiesAsTags());
					spawnWizard(event, entityInNbt);
				}
			}
		}
	}

	private static void spawnWizard(PlayerInteractEvent.EntityInteract event, NBTTagCompound entityInNbt) {
		entityInNbt.setUniqueId("UUID", MathHelper.getRandomUUID(event.getWorld().rand));
		Entity mob = EntityList.createEntityFromNBT(entityInNbt, event.getWorld());

		if (mob != null) {
			if (!event.getWorld().isRemote) {
				event.getWorld().removeEntity(event.getTarget());
				event.getEntityPlayer().setHeldItem(event.getHand(), ItemStack.EMPTY);
				event.getWorld().spawnEntity(mob);
				event.setCanceled(true);
				consumeScroll(event.getEntityPlayer(), event.getHand());
			}
		}
	}
}
