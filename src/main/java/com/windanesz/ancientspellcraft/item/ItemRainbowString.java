package com.windanesz.ancientspellcraft.item;

import com.windanesz.ancientspellcraft.integration.baubles.ASBaublesIntegration;
import com.windanesz.ancientspellcraft.registry.ASPotions;
import com.windanesz.ancientspellcraft.ritual.ElementalAttunement;
import electroblob.wizardry.constants.Element;
import electroblob.wizardry.item.ItemArtefact;
import electroblob.wizardry.registry.WizardryPotions;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.init.MobEffects;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;
import java.util.Optional;

@Mod.EventBusSubscriber
public class ItemRainbowString extends ItemASArtefact {

    private static final String CURRENT_ELEMENT_TAG = "CurrentElement";

    public ItemRainbowString(EnumRarity rarity, Type type) {
        super(rarity, type);
    }

    @SubscribeEvent
    public static void onArrowLoose(ArrowLooseEvent event) {
        if (event.getEntityPlayer() != null && isHoldingBow(event.getEntityPlayer())) {
            List<ItemStack> charmStacks = ASBaublesIntegration.getEquippedArtefactStacks(event.getEntityPlayer(), ItemArtefact.Type.CHARM);
            if (!charmStacks.isEmpty() && charmStacks.get(0).getItem() instanceof ItemRainbowString) {
                rotateElement(charmStacks.get(0));
            }
        }
    }

    private static boolean isHoldingBow(EntityPlayer player) {
        return player.getHeldItemMainhand().getItem() instanceof net.minecraft.item.ItemBow ||
                player.getHeldItemOffhand().getItem() instanceof net.minecraft.item.ItemBow;
    }

    private static void rotateElement(ItemStack stack) {
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt == null) {
            nbt = new NBTTagCompound();
            stack.setTagCompound(nbt);
        }
        String currentElementName = nbt.getString(CURRENT_ELEMENT_TAG);
        Element currentElement = Element.fromName(currentElementName, Element.FIRE); // Fallback to FIRE if not found
        Element[] elements = Element.values();
        Element newElement = elements[(currentElement.ordinal() + 1) % elements.length];
        nbt.setString(CURRENT_ELEMENT_TAG, newElement.getName());
    }

    private static Element getElement(ItemStack stack, EntityPlayer player) {
        Optional<Element> element = ElementalAttunement.getElement(player);
        if (element.isPresent()) {
            return element.get();
        }

        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt != null) {
            String currentElementName = nbt.getString(CURRENT_ELEMENT_TAG);
            return Element.fromName(currentElementName, Element.FIRE); // Fallback to FIRE if not found
        }
        return Element.FIRE;
    }

    private static PotionEffect getEffect(Element element) {
        switch (element) {
            case NECROMANCY:
                return new PotionEffect(MobEffects.WITHER, 40, 1); // 2s of Wither II
            case HEALING:
                return new PotionEffect(MobEffects.BLINDNESS, 60, 0); // 3s of Blindness
            case ICE:
                return new PotionEffect(WizardryPotions.frost, 60, 0); // 3s of Frost
            case EARTH:
                return new PotionEffect(MobEffects.POISON, 80, 0); // 4s of Poison
            case SORCERY:
                return new PotionEffect(MobEffects.LEVITATION, 35, 0); // 1.75s of Levitation
            case LIGHTNING:
                return new PotionEffect(WizardryPotions.paralysis, 40, 0); // 2s of Paralysis
            case FIRE:
            default:
                return new PotionEffect(ASPotions.soul_scorch, 80, 0); // 4s of Soul Scorch
        }
    }

    private static void createOrModifyTippedArrow(EntityArrow arrow, PotionEffect effect) {
        if (!(arrow instanceof EntityTippedArrow)) {
            EntityTippedArrow tippedArrow = new EntityTippedArrow(arrow.world, (EntityLivingBase) arrow.shootingEntity);
            tippedArrow.setPosition(arrow.posX, arrow.posY, arrow.posZ);
            tippedArrow.setVelocity(arrow.motionX, arrow.motionY, arrow.motionZ);
            tippedArrow.addEffect(effect);
            arrow.world.spawnEntity(tippedArrow);
            arrow.setDead();
        } else {
            ((EntityTippedArrow) arrow).addEffect(effect);
        }
    }

    @SubscribeEvent
    public static void onArrowSpawn(EntityJoinWorldEvent event) {
        if (event.getEntity() instanceof EntityArrow) {
            EntityArrow arrow = (EntityArrow) event.getEntity();
            if (arrow.shootingEntity instanceof EntityPlayer) {
                EntityPlayer shooter = (EntityPlayer) arrow.shootingEntity;
                List<ItemStack> charmStacks = ASBaublesIntegration.getEquippedArtefactStacks(shooter, ItemArtefact.Type.CHARM);
                if (!charmStacks.isEmpty() && charmStacks.get(0).getItem() instanceof ItemRainbowString) {
                    Element currentElement = getElement(charmStacks.get(0), shooter);
                    PotionEffect effect = getEffect(currentElement);
                    createOrModifyTippedArrow(arrow, effect);
                }
            }
        }
    }

}
