package com.windanesz.ancientspellcraft.item;

import com.windanesz.ancientspellcraft.packet.ASPacketHandler;
import com.windanesz.ancientspellcraft.packet.PacketVinekeeperTarget;
import electroblob.wizardry.data.WizardData;
import electroblob.wizardry.registry.Spells;
import electroblob.wizardry.spell.Grapple;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ItemRingVinekeeper extends ItemASArtefact implements ITickableArtefact {

    private static final int CAST_DURATION = 90; // 4.5 seconds
    private static final int COOLDOWN_DURATION = 100; // 5 seconds
    private static final int SEARCH_HEIGHT = 8;
    private static final int SEARCH_RADIUS = 3;
    private static final double MINIMUM_ANCHOR_DISTANCE = 4.0;

    public ItemRingVinekeeper(EnumRarity rarity, Type type) {
        super(rarity, type);
    }

    @Override
    public void onWornTick(ItemStack itemstack, EntityLivingBase player) {
        if (!(player instanceof EntityPlayer)) return;
        EntityPlayer entityPlayer = (EntityPlayer) player;
        World world = entityPlayer.world;
        if (world.isRemote) return;

        WizardData data = WizardData.get(entityPlayer);
        if (data == null) return;

        if (entityPlayer.fallDistance > 4.0f && !data.isCasting() && !entityPlayer.getCooldownTracker().hasCooldown(this)) {

            RayTraceResult hit = findNearestSurface(world, entityPlayer);

            if (hit != null) {

                data.setVariable(Grapple.TARGET_KEY, hit);
                ASPacketHandler.net.sendToDimension(new PacketVinekeeperTarget.Message(entityPlayer, hit), world.provider.getDimension());
                data.startCastingContinuousSpell(Spells.grapple, new SpellModifiers(), CAST_DURATION);

                entityPlayer.getCooldownTracker().setCooldown(this, CAST_DURATION + COOLDOWN_DURATION);
            }
        }
    }

    private static RayTraceResult findNearestSurface(World world, EntityPlayer player) {
        BlockPos playerPos = player.getPosition();
        Vec3d eyes = player.getPositionEyes(1.0F);
        RayTraceResult closest = null;
        double closestDistSq = Double.MAX_VALUE;

        for (int y = 1; y <= SEARCH_HEIGHT; y++) {
            for (int x = -SEARCH_RADIUS; x <= SEARCH_RADIUS; x++) {
                for (int z = -SEARCH_RADIUS; z <= SEARCH_RADIUS; z++) {
                    BlockPos pos = playerPos.add(x, y, z);
                    if (!world.getBlockState(pos).getMaterial().isSolid()) continue;

                    for (EnumFacing face : EnumFacing.values()) {
                        if (world.getBlockState(pos.offset(face)).getMaterial().isSolid()) continue;

                        Vec3d hitVec = new Vec3d(pos).add(0.5 + face.getXOffset() * 0.5, 0.5 + face.getYOffset() * 0.5, 0.5 + face.getZOffset() * 0.5);
                        RayTraceResult lineOfSight = world.rayTraceBlocks(eyes, hitVec, false, true, false);
                        if (lineOfSight == null || !pos.equals(lineOfSight.getBlockPos())) continue;

                        double distSq = eyes.squareDistanceTo(hitVec);
                        if (distSq < MINIMUM_ANCHOR_DISTANCE * MINIMUM_ANCHOR_DISTANCE) continue;
                        if (distSq < closestDistSq) {
                            closestDistSq = distSq;
                            closest = new RayTraceResult(RayTraceResult.Type.BLOCK, hitVec, face, pos);
                        }
                    }
                }
            }
        }

        return closest;
    }
}
