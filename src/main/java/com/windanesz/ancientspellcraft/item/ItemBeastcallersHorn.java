package com.windanesz.ancientspellcraft.item;

import com.windanesz.ancientspellcraft.Settings;
import com.windanesz.ancientspellcraft.entity.living.EntityOrdinarySpiderMinion;
import com.windanesz.ancientspellcraft.entity.living.EntityWolfMinion;
import electroblob.wizardry.Wizardry;
import electroblob.wizardry.entity.living.EntitySilverfishMinion;
import electroblob.wizardry.util.BlockUtils;
import electroblob.wizardry.util.ParticleBuilder;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ItemBeastcallersHorn extends ItemASArtefact {

    private static final int CHANNEL_TIME = 40; // 2 seconds in ticks
    private static final int MINION_LIFETIME = 600; // 30 seconds in ticks

    public ItemBeastcallersHorn(EnumRarity rarity, Type type) {
        super(rarity, type);
        setMaxStackSize(1);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack itemstack = player.getHeldItem(hand);

        // Check if on cooldown
        if (player.getCooldownTracker().hasCooldown(this)) {
            return new ActionResult<>(EnumActionResult.FAIL, itemstack);
        }

        // Start channeling
        player.setActiveHand(hand);
        return new ActionResult<>(EnumActionResult.SUCCESS, itemstack);
    }

    @Override
    public void onUsingTick(ItemStack stack, EntityLivingBase player, int count) {
        if (!(player instanceof EntityPlayer)) return;

        EntityPlayer entityPlayer = (EntityPlayer) player;
        int useTime = getMaxItemUseDuration(stack) - count;

        // Check if channeling is complete (2 seconds)
        if (useTime >= CHANNEL_TIME) {
            summonBeasts(entityPlayer.world, entityPlayer);
            // Set cooldown (configurable)
            entityPlayer.getCooldownTracker().setCooldown(this, Settings.generalSettings.beastcallers_flute_cooldown * 20);
            entityPlayer.stopActiveHand();
        }
    }

    private void summonBeasts(World world, EntityPlayer player) {
        if (world.isRemote) return;

        // Summon 2 EntityOrdinarySpider
        for (int i = 0; i < 2; i++) {
            BlockPos spawnPos = BlockUtils.findNearbyFloorSpace(player, 2,2);
            if (spawnPos != null) {
                EntityOrdinarySpiderMinion spider = new EntityOrdinarySpiderMinion(world);
                spider.setPosition(spawnPos.getX() + 0.5f, spawnPos.getY(), spawnPos.getZ() + 0.5f);
                spider.setLifetime(MINION_LIFETIME);
                spider.setCaster(player);
                world.spawnEntity(spider);
            }
        }

        // Summon 2 EntityWolfMinion
        for (int i = 0; i < 2; i++) {
            BlockPos spawnPos = BlockUtils.findNearbyFloorSpace(player, 2,2);
            if (spawnPos != null) {
                EntityWolfMinion wolf = new EntityWolfMinion(world);
                wolf.setPosition(spawnPos.getX() + 0.5f, spawnPos.getY(), spawnPos.getZ() + 0.5f);
                wolf.setLifetime(MINION_LIFETIME);
                wolf.setCaster(player);
                world.spawnEntity(wolf);
            }
        }

        // Summon 2 EntitySilverfishMinion
        for (int i = 0; i < 2; i++) {
            BlockPos spawnPos = BlockUtils.findNearbyFloorSpace(player, 2,2);
            if (spawnPos != null) {
                EntitySilverfishMinion silverfish = new EntitySilverfishMinion(world);
                silverfish.setPosition(spawnPos.getX() + 0.5f, spawnPos.getY(), spawnPos.getZ() + 0.5f);
                silverfish.setLifetime(MINION_LIFETIME);
                silverfish.setCaster(player);
                world.spawnEntity(silverfish);
            }
        }

        // Visual effects
        if (world.isRemote) {
            for (int i = 0; i < 20; i++) {
                double x = player.posX + world.rand.nextDouble() * 2 - 1;
                double y = player.posY + world.rand.nextDouble() * 2;
                double z = player.posZ + world.rand.nextDouble() * 2 - 1;
                ParticleBuilder.create(ParticleBuilder.Type.SPARKLE).pos(x, y, z).clr(0x4CAF50).spawn(world);
            }
        }
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 72000; // Arbitrary large number for channeling
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag advanced) {
        super.addInformation(stack, world, tooltip, advanced);

        // Add usage and cooldown info
        int cooldownMinutes = Settings.generalSettings.beastcallers_flute_cooldown / 60;
        String usageText = I18n.format("item.ancientspellcraft:charm_beastcallers_flute.tooltip", cooldownMinutes);
        tooltip.add(usageText);

        if (!Settings.isArtefactEnabled(this)) {
            tooltip.add(Wizardry.proxy.translate("item." + Wizardry.MODID + ":generic.disabled"));
        }
    }
}
