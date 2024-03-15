package com.windanesz.ancientspellcraft.item;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.client.gui.GuiHandlerAS;
import com.windanesz.ancientspellcraft.registry.ASBlocks;
import com.windanesz.ancientspellcraft.registry.ASItems;
import com.windanesz.ancientspellcraft.registry.ASTabs;
import com.windanesz.ancientspellcraft.registry.Rituals;
import com.windanesz.ancientspellcraft.ritual.Ritual;
import com.windanesz.ancientspellcraft.util.LangUtils;
import electroblob.wizardry.Wizardry;
import electroblob.wizardry.data.WizardData;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class ItemRitualBook extends Item {

	public ItemRitualBook() {
		super();
		setHasSubtypes(true);
		setMaxStackSize(16);
		setCreativeTab(ASTabs.ANCIENTSPELLCRAFT_RITUALS);
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> list) {

		if (tab == ASTabs.ANCIENTSPELLCRAFT_RITUALS) {

			List<Ritual> rituals = Ritual.getAllRituals();
			rituals.removeIf(r -> !r.applicableForItem(this));

			for (Ritual ritual : rituals) {
				ItemStack stack = new ItemStack(this);
				setRitual(stack, ritual);
				list.add(stack);
			}
		}
	}

	public static Ritual getRitual(ItemStack stack) {
		if (stack.getItem() instanceof ItemRitualBook) {
			if (stack.hasTagCompound() && stack.getTagCompound().hasKey("ritual")) {
				String ritual = stack.getTagCompound().getString("ritual");
				Ritual rit = Ritual.registry.getValue(new ResourceLocation(ritual));
				if (rit != null) {
					return rit;
				}
			}
		}
		return Rituals.none;
	}

	public static void setRitual(ItemStack stack, Ritual ritual) {
		if (stack.getItem() instanceof ItemRitualBook) {
			NBTTagCompound compound;
			if (stack.hasTagCompound()) {
				compound = stack.getTagCompound();
			} else {
				compound = new NBTTagCompound();
			}
			//noinspection ConstantConditions
			compound.setString("ritual", ritual.getRegistryName().toString());
			stack.setTagCompound(compound);
		}
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);
		Ritual ritual = getRitual(stack);
		if (ritual != Rituals.none && !player.isSneaking()) {
			player.openGui(AncientSpellcraft.instance, GuiHandlerAS.RITUAL_BOOK, world, 0, 0, 0);
		} else if (!world.isRemote && ritual != Rituals.none) {
			NonNullList<Ingredient> pattern = ritual.getRitualProperties().pattern;
			int width = ritual.getProperties().width;
			int height = ritual.getProperties().height;
			int middleIndex = width * height / 2;
			BlockPos playerPos = player.getPosition();
			EnumFacing playerFacing = player.getHorizontalFacing();

			// Calculate starting position of the ritual pattern
			BlockPos startingPos = playerPos.offset(playerFacing, width / 2).offset(playerFacing.rotateY(), height / 2);

			int counter = 0;
			for (int j = 0; j < height; j++) {
				for (int k = 0; k < width; k++) {
					Ingredient ingredient = pattern.get(counter);
					// Calculate current block position
					BlockPos currentBlockPos = startingPos.offset(playerFacing, -k).offset(playerFacing.rotateY(), -j);
					if (middleIndex != counter && ingredient.getMatchingStacks().length == 1 && ingredient.getMatchingStacks()[0].getItem() instanceof ItemRune) {
						ItemStack matchingStack = ingredient.getMatchingStacks()[0];
						// Place the block in the world
						((ItemRune) ASItems.rune_algiz).placeBlockAt(matchingStack, player, world,
								currentBlockPos, playerFacing.rotateYCCW(), 0, 0, 0, ASBlocks.PLACED_RUNE.getDefaultState());
					} else {
						// Clear the block in the world
						world.setBlockToAir(currentBlockPos);
					}
					counter++;
				}
			}
			// Place the middle rune separately (if applicable)
			if (middleIndex < pattern.size()) {
				BlockPos middleBlockPos = startingPos.offset(playerFacing, -width / 2).offset(playerFacing.rotateY(), -height / 2);
				((ItemRune) ASItems.rune_algiz).placeBlockAt(pattern.get(middleIndex).getMatchingStacks()[0], player, world,
						middleBlockPos, playerFacing, 0, 0, 0, ASBlocks.PLACED_RUNE.getDefaultState());
			}
		}
		return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
	}



	// This is accessed during loading (before we even get to the main menu) for search tree population
	// Obviously the world is always null at that point, because no world objects exist! However, outside of a world
	// there are no guarantees as to spell metadata order so we just have to give up (and we can't account for discovery)
	// TODO: Search trees seem to get reloaded when the mappings change so in theory this should work ok, why doesn't it?
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack itemstack, World world, List<String> tooltip, net.minecraft.client.util.ITooltipFlag advanced) {
		super.addInformation(itemstack, world, tooltip, advanced);
		if (world == null)
			world = Wizardry.proxy.getTheWorld(); // But... I need the world!

		// Tooltip is left blank for wizards buying generic spell books.
		if (world != null && itemstack.hasTagCompound()) {
			if (getRitual(itemstack) != Rituals.none) {
				Ritual ritual = getRitual(itemstack);
				EntityPlayer player = Wizardry.proxy.getThePlayer();

				boolean discovered = AncientSpellcraft.proxy.shouldDisplayDiscovered(ritual, itemstack);

				// If the ritual should *appear* discovered but isn't *actually* discovered, show a 'new spell' message
				// A bit annoying to check this again but it's the easiest way
				if (!ritual.isEnabled()) {
					tooltip.add(AncientSpellcraft.proxy.translate("item.ancientspellcraft:ritual_book.desc_disabled"));
				} else if (!player.isCreative() && WizardData.get(player) != null && !discovered) {
					tooltip.add(LangUtils.toElderFuthark(ritual.getDisplayName()));
					tooltip.add(AncientSpellcraft.proxy.translate("item.ancientspellcraft:ritual_book.desc"));
				} else {
					tooltip.add(ritual.getDisplayName());
				}
			}

		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public net.minecraft.client.gui.FontRenderer getFontRenderer(ItemStack stack) {
		return Wizardry.proxy.getFontRenderer(stack);
	}
}
