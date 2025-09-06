package com.windanesz.ancientspellcraft.item;

import com.windanesz.ancientspellcraft.entity.living.EntityRemnantMinion;
import electroblob.wizardry.constants.Element;
import electroblob.wizardry.entity.living.EntityRemnant;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.item.ItemArtefact;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ItemRemnantCage extends ItemDailyArtefact {

    private static final String HAS_REMNANT_TAG = "hasRemnant";
    private static final String STORED_ELEMENT_TAG = "storedElement";

    public ItemRemnantCage(EnumRarity rarity) {
        super(rarity);
        this.addReadinessPropertyOverride();
    }

    @Override
    public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer player, EntityLivingBase target, EnumHand hand) {
        if (!player.world.isRemote && target instanceof EntityRemnant) {
            EntityRemnant remnant = (EntityRemnant) target;

            // Check if cage is empty
            if (hasRemnant(stack)) {
                return false; // Cage already contains a remnant
            }

            // Capture the remnant
            Element element = remnant.getElement();

            // Store the element in the cage
            setStoredElement(stack, element);

            // Update the player's held item to reflect the changes
            if (hand == EnumHand.MAIN_HAND) {
                player.setHeldItem(EnumHand.MAIN_HAND, stack);
            } else {
                player.setHeldItem(EnumHand.OFF_HAND, stack);
            }

            // Remove the remnant from the world without triggering death events
            target.setDead();

            return true;
        }
        return false;
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        // Allow normal right-click behavior for daily artefact functionality
        return super.onItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);

        // Check if player is sneaking and cage has a remnant
        if (player.isSneaking() && hasRemnant(stack)) {
            if (!world.isRemote) {
                Element element = getStoredElement(stack);
                if (element != null) {
                    // Spawn EntityRemnantMinion at player's location
                    EntityRemnantMinion minion = new EntityRemnantMinion(world);
                    minion.setPosition(player.posX, player.posY, player.posZ);

                    // Set the minion's element
                    minion.setElement(element);

                    // Set the owner to the player
                    minion.setOwnerId(player.getUniqueID());

                    // Spawn the minion in the world
                    world.spawnEntity(minion);

                    // Clear the cage
                    clearRemnant(stack);

                    // Update the player's held item
                    player.setHeldItem(hand, stack);
                }
            }
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        }

        // If not sneaking or empty, use normal daily artefact behavior
        return super.onItemRightClick(world, player, hand);
    }

    @Override
    public void performAction(EntityPlayer player) {
        ItemStack stack = player.getHeldItemMainhand();

        if (hasRemnant(stack)) {
            Element element = getStoredElement(stack);
            if (element != null) {
                // Create spectral dust based on element
                ItemStack dust = new ItemStack(WizardryItems.spectral_dust, 1, element.ordinal());

                // Try to give to player inventory, drop if full
                if (!player.inventory.addItemStackToInventory(dust)) {
                    player.dropItem(dust, false);
                }

                // Clear the cage after releasing dust
                clearRemnant(stack);
                // Update the player's held item to reflect the changes
                player.setHeldItem(EnumHand.MAIN_HAND, stack);
            }
        }
    }

    private boolean hasRemnant(ItemStack stack) {
        return stack.hasTagCompound() && stack.getTagCompound().getBoolean(HAS_REMNANT_TAG);
    }

    private void setStoredElement(ItemStack stack, Element element) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        NBTTagCompound nbt = stack.getTagCompound();
        nbt.setBoolean(HAS_REMNANT_TAG, true);
        nbt.setInteger(STORED_ELEMENT_TAG, element.ordinal());
        stack.setTagCompound(nbt);
    }

    private Element getStoredElement(ItemStack stack) {
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey(STORED_ELEMENT_TAG)) {
            int elementOrdinal = stack.getTagCompound().getInteger(STORED_ELEMENT_TAG);
            return Element.values()[elementOrdinal];
        }
        return null;
    }

    private void clearRemnant(ItemStack stack) {
        if (stack.hasTagCompound()) {
            NBTTagCompound nbt = stack.getTagCompound();
            nbt.removeTag(HAS_REMNANT_TAG);
            nbt.removeTag(STORED_ELEMENT_TAG);
            stack.setTagCompound(nbt);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, net.minecraft.client.util.ITooltipFlag advanced) {
        super.addInformation(stack, world, tooltip, advanced);

        if (hasRemnant(stack)) {
            Element element = getStoredElement(stack);
            if (element != null) {
                tooltip.add("Contains a " + element.getDisplayName() + " remnant");
                tooltip.add("Sneak-right-click to release as minion");
            }
        } else {
            tooltip.add("Empty - right-click a remnant to capture it");
            tooltip.add("Sneak-right-click to release captured remnant as minion");
        }
    }
}
