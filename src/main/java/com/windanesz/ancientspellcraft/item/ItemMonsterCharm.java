package com.windanesz.ancientspellcraft.item;

import com.windanesz.ancientspellcraft.util.ASUtils;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ItemMonsterCharm extends ItemASArtefact {
    private static final String ATTUNED_MOB_TAG = "attuned_mob";

	public ItemMonsterCharm(EnumRarity rarity, Type type) {
        super(rarity, type);
    }

    @Override
    public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer player, EntityLivingBase target, EnumHand hand) {
        if (!player.world.isRemote && player.isSneaking()) {
            ResourceLocation mobId = EntityList.getKey(target);
            
            if (mobId != null) {
                // Create a new ItemStack to ensure changes persist
                ItemStack newStack = stack.copy();
                
                // Ensure the new stack has an NBT compound
                if (!newStack.hasTagCompound()) {
                    newStack.setTagCompound(new NBTTagCompound());
                }
                
                // Set the attuned mob data
                NBTTagCompound tag = newStack.getTagCompound();
                tag.setString(ATTUNED_MOB_TAG, mobId.toString());
                newStack.setTagCompound(tag);
                
                // Update the player's hand with the new stack
                if (hand == EnumHand.MAIN_HAND) {
                    player.setHeldItem(EnumHand.MAIN_HAND, newStack);
                } else {
                    player.setHeldItem(EnumHand.OFF_HAND, newStack);
                }
                
                // Send success message
                ASUtils.sendMessage(player, "item.ancientspellcraft:monster_charm.attuned", false, target.getDisplayName());
                return true;
            }
        }
        return false;
    }

    public static String getAttunedMob(ItemStack stack) {
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey(ATTUNED_MOB_TAG)) {
            return stack.getTagCompound().getString(ATTUNED_MOB_TAG);
        }
        return null;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable net.minecraft.world.World world, List<String> tooltip, net.minecraft.client.util.ITooltipFlag advanced) {
        super.addInformation(stack, world, tooltip, advanced);
        String mob = getAttunedMob(stack);
        if (mob != null) {
            tooltip.add("Attuned to: " + mob);
        } else {
            tooltip.add("Sneak-right-click a mob to attune");
        }
    }
} 