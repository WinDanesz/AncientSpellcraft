package com.windanesz.ancientspellcraft.ritual;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.registry.ASItems;
import com.windanesz.ancientspellcraft.tileentity.TileRune;
import com.windanesz.ancientspellcraft.util.ASUtils;
import electroblob.wizardry.constants.Element;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.ParticleBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class Condensing extends Ritual implements IRitualIngredient, IHasItemToRender, IHasRightClickEffect {

	private ItemStack stack = ItemStack.EMPTY;
	private String STORED_ITEM_TAG = "stored_item";
	private String GROWTH_PROGRESS_TAG = "growth_progress";

	// throw in an astral diamond to make permanent?
	public Condensing() {
		super(AncientSpellcraft.MODID, "condensing", SpellActions.SUMMON, false);
	}

	@Override
	public void initialEffect(World world, EntityPlayer caster, TileRune centerPiece) {
		ruinNonCenterPieceRunes(centerPiece, world);
	}

	@Override
	public void onRitualFinish(World world, EntityPlayer caster, TileRune centerPiece) {
		if (caster != null && !world.isRemote) {

		}

		super.onRitualFinish(world, caster, centerPiece);
	}

	@Override
	public void effect(World world, EntityPlayer caster, TileRune centerPiece) {
		super.effect(world, caster, centerPiece);

		// should run once every 9 seconds
		if (!world.isRemote && world.getTotalWorldTime() % 180 == 0) {
			float progress = 0;

			if (centerPiece.getRitualData().hasKey(STORED_ITEM_TAG)) {

				stack = getStoredItem(centerPiece);

				if (centerPiece.getRitualData().hasKey(GROWTH_PROGRESS_TAG)) {
					progress = centerPiece.getRitualData().getFloat(GROWTH_PROGRESS_TAG);
					progress = incrementGrowthTimer(progress);
					if (progress >= 100f) {
						stack = growCrystal();
						progress = 0f;
					}
				}

				NBTTagCompound stackCompound = new NBTTagCompound();
				stack.writeToNBT(stackCompound);
				NBTTagCompound comp = centerPiece.getRitualData();
				comp.setFloat(GROWTH_PROGRESS_TAG, progress);
				comp.setTag(STORED_ITEM_TAG, stackCompound);
				centerPiece.setRitualData(comp);
			} else {

				List<EntityItem> entityItemList = EntityUtils.getEntitiesWithinRadius(1, centerPiece.getPos().getX(), centerPiece.getPos().getY(), centerPiece.getPos().getZ(), world, EntityItem.class);

				if (!entityItemList.isEmpty()) {
					for (EntityItem entityItem : entityItemList) {
						if ((entityItem.getItem().getItem() == WizardryItems.magic_crystal && entityItem.getItem().getMetadata() == 0) ||
								entityItem.getItem().getItem() == WizardryItems.crystal_shard || isElementalCrystalShard(entityItem.getItem().getItem())) {
							// magic crystal shard
							NBTTagCompound stackCompound = new NBTTagCompound();
							ItemStack itemStack = (new ItemStack(entityItem.getItem().getItem(), 1));
							itemStack.writeToNBT(stackCompound);
							//							stack.writeToNBT(stackCompound);
							NBTTagCompound comp = new NBTTagCompound();
							comp.setTag(STORED_ITEM_TAG, stackCompound);

							centerPiece.setRitualData(comp);
							//							NBTTagCompound comp = centerPiece.getRitualData();
							//							comp.setTag("stored_item", stackCompound);
							//							centerPiece.setRitualData(comp);
							entityItem.getItem().shrink(1);
						}
					}
				}
			}
			centerPiece.sendUpdates();
		}

		if (world.isRemote) {
			if (getStoredItem(centerPiece) != null && getStoredItem(centerPiece) != ItemStack.EMPTY) {

				ParticleBuilder.create(ParticleBuilder.Type.FLASH).clr(255, 255, 247).face(EnumFacing.SOUTH).pos(centerPiece.getXCenter(), centerPiece.getYCenter() + 0.3f, centerPiece.getZCenter()).scale(1f).spawn(world);
				ParticleBuilder.create(ParticleBuilder.Type.FLASH).clr(255, 255, 247).face(EnumFacing.WEST).pos(centerPiece.getXCenter(), centerPiece.getYCenter() + 0.3f, centerPiece.getZCenter()).scale(1f).spawn(world);
				ParticleBuilder.create(ParticleBuilder.Type.FLASH).clr(255, 255, 247).fade(0, 0, 0).spin(0.8f, 0.07f).time(40).pos(centerPiece.getXCenter(), centerPiece.getY() + 0.1f, centerPiece.getZCenter()).scale(0.2f).spawn(world);
				ParticleBuilder.create(ParticleBuilder.Type.FLASH).clr(255, 255, 247).vel(0, 0.1, 0).fade(0, 0, 0).spin(0.8f, 0.07f).time(40).pos(centerPiece.getXCenter(), centerPiece.getY() + 0.1f, centerPiece.getZCenter()).scale(0.2f).spawn(world);
			}
		}
	}

	private ItemStack growCrystal() {
		Item item = stack.getItem();
		if (item == WizardryItems.magic_crystal && stack.getMetadata() == 0) {
			return new ItemStack(WizardryItems.grand_crystal);
		}
		if (item == WizardryItems.crystal_shard) {
			return new ItemStack(WizardryItems.magic_crystal);
		}
		if (item == ASItems.crystal_shard_sorcery) {
			return new ItemStack(WizardryItems.magic_crystal, Element.SORCERY.ordinal());
		}
		if (item == ASItems.crystal_shard_necromancy) {
			return new ItemStack(WizardryItems.magic_crystal, Element.NECROMANCY.ordinal());
		}
		if (item == ASItems.crystal_shard_lightning) {
			return new ItemStack(WizardryItems.magic_crystal, Element.LIGHTNING.ordinal());
		}
		if (item == ASItems.crystal_shard_ice) {
			return new ItemStack(WizardryItems.magic_crystal, Element.ICE.ordinal());
		}
		if (item == ASItems.crystal_shard_healing) {
			return new ItemStack(WizardryItems.magic_crystal, Element.HEALING.ordinal());
		}
		if (item == ASItems.crystal_shard_fire) {
			return new ItemStack(WizardryItems.magic_crystal, Element.FIRE.ordinal());
		}
		if (item == ASItems.crystal_shard_earth) {
			return new ItemStack(WizardryItems.magic_crystal, Element.EARTH.ordinal());
		}

		return stack;
	}
	private float incrementGrowthTimer(float currentProgress) {
		Item item = stack.getItem();
		// 400 cycles to grow a magic crystal into a grand crystal (24000 ticks is a full day, 72000 equals 3 days)
		if (item == WizardryItems.magic_crystal && stack.getMetadata() == 0) {
			return currentProgress + 0.25f;
		} else if (isElementalCrystalShard(item) || item == WizardryItems.crystal_shard) {
			return currentProgress + 0.75f;
		}
		return currentProgress;
	}

	@Override
	public List<List<ItemStack>> getRequiredIngredients() {
		List<List<ItemStack>> ingredients = new ArrayList<>();
		List<ItemStack> condensingUpgrade = new ArrayList<>();

		condensingUpgrade.add(new ItemStack(WizardryItems.condenser_upgrade));

		ingredients.add(condensingUpgrade);
		return ingredients;
	}

	@Override
	public boolean onRightClick(TileRune centerPiece, @Nullable EntityLivingBase breakerEntity) {
		if (breakerEntity instanceof EntityPlayer) {
			ItemStack stack = getStoredItem(centerPiece);

			if (stack != ItemStack.EMPTY && stack.getItem() != Items.AIR) {

				if (breakerEntity.world.isRemote)
					return true;

				if (!((EntityPlayer) breakerEntity).inventory.addItemStackToInventory(stack)) {
					EntityItem entityitem = new EntityItem(centerPiece.getWorld(), centerPiece.getXCenter(), centerPiece.getYCenter(), centerPiece.getZCenter(), stack);
					entityitem.setDefaultPickupDelay();
					centerPiece.getWorld().spawnEntity(entityitem);
				}

				setStoredItem(ItemStack.EMPTY, centerPiece);
				return true;
			} else {
				// no item to retrieve, try storing the held item
				EntityPlayer player = ((EntityPlayer) breakerEntity);
				ItemStack playerStack = player.getHeldItemMainhand();

				if (!centerPiece.getWorld().isRemote && (isValidItem(playerStack))) {

					setStoredItem(player.getHeldItemMainhand(), centerPiece);
					player.getHeldItemMainhand().shrink(1);

					return true;
				}
			}
		}
		return true;
	}

	private void setStoredItem(ItemStack newItem, TileRune centerPiece) {
		NBTTagCompound ritualData = centerPiece.getRitualData();
		if (ritualData == null) {
			ritualData = new NBTTagCompound();
		}
		ritualData.setFloat(GROWTH_PROGRESS_TAG, 0);
		ritualData.setTag(STORED_ITEM_TAG, ASUtils.convertToNBTSingeCount(newItem));
		centerPiece.setRitualData(ritualData);
		centerPiece.sendUpdates();
	}

	private ItemStack getStoredItem(TileRune centerPiece) {
		if (centerPiece.getRitualData().hasKey(STORED_ITEM_TAG)) {
			return new ItemStack(centerPiece.getRitualData().getCompoundTag(STORED_ITEM_TAG));
		}
		return ItemStack.EMPTY;
	}

	private boolean isValidItem(ItemStack stack) {
		Item item = stack.getItem();
		return isElementalCrystalShard(item) || item == WizardryItems.crystal_shard && stack.getMetadata() == 0 || item == WizardryItems.magic_crystal && stack.getMetadata() == 0;
	}

	private boolean isElementalCrystalShard(Item item) {
		return item == ASItems.crystal_shard_sorcery || item == ASItems.crystal_shard_necromancy ||
				item == ASItems.crystal_shard_lightning || item == ASItems.crystal_shard_ice ||
				item == ASItems.crystal_shard_healing || item == ASItems.crystal_shard_fire ||
				item == ASItems.crystal_shard_earth;
	}

	@Override
	public boolean shouldConsumeIngredients() {
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderItem(TileRune tileRune, float partialTicks, int destroyStage, float alpha) {
		if (tileRune.getWorld().isRemote) {
			ItemStack stack = getStoredItem(tileRune);

			if (stack != ItemStack.EMPTY) {

				float t = Minecraft.getMinecraft().player.ticksExisted + partialTicks;

				if (!stack.isEmpty()) {

					GlStateManager.pushMatrix();
					GlStateManager.translate(0, 0.8f, 0);
					GlStateManager.rotate(0, 1, 0, 180);
					GlStateManager.rotate(t, 0, 1, 0);
					GlStateManager.scale(0.85F, 0.85F, 0.85F);

					Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.FIXED);

					GlStateManager.popMatrix();
				}
			}
		}
	}
}
