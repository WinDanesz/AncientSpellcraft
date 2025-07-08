package com.windanesz.ancientspellcraft.item;

import com.windanesz.ancientspellcraft.util.ASUtils;
import electroblob.wizardry.registry.WizardryPotions;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ItemSpecterLightTorch extends ItemASArtefact implements ITickableArtefact {

    private static final int RADIUS = 10;
    private static final int GLOW_DURATION = 100; // 5 seconds (20 ticks per second)

    public ItemSpecterLightTorch() {
        super(EnumRarity.UNCOMMON, Type.CHARM);
    }

    public ItemSpecterLightTorch(EnumRarity rarity, Type type) {
        super(rarity, type);
    }

    @Override
    public void onWornTick(ItemStack itemstack, EntityLivingBase player) {
        if (player.world.isRemote) return;
        if (player.ticksExisted % 20 != 0) return;

        World world = player.world;
        double x = player.posX;
        double y = player.posY;
        double z = player.posZ;

        // Reveal invisible entities
        List<EntityLivingBase> entities = world.getEntitiesWithinAABB(EntityLivingBase.class,
                new AxisAlignedBB(x - RADIUS, y - RADIUS, z - RADIUS, x + RADIUS, y + RADIUS, z + RADIUS),
                e -> e != player && e.isEntityAlive() && e.isInvisible());
        for (EntityLivingBase entity : entities) {
            // Remove invisibility and similar effects
            for (PotionEffect effect : entity.getActivePotionEffects()) {
                Potion potion = effect.getPotion();
                if (potion == MobEffects.INVISIBILITY || (WizardryPotions.mirage != null && potion == WizardryPotions.mirage) || (WizardryPotions.muffle != null && potion == WizardryPotions.muffle)) {
                    entity.removePotionEffect(potion);
                }
            }
        }

        // Mark undead with Glowing
        List<EntityLivingBase> undead = world.getEntitiesWithinAABB(EntityLivingBase.class,
                new AxisAlignedBB(x - RADIUS, y - RADIUS, z - RADIUS, x + RADIUS, y + RADIUS, z + RADIUS),
                e -> e != player && ASUtils.isEntityConsideredUndead(e));
        for (EntityLivingBase entity : undead) {
            entity.addPotionEffect(new PotionEffect(MobEffects.GLOWING, GLOW_DURATION, 0));
        }

        // Mark mob spawners with a ring of particles (visual marker)
        BlockPos center = new BlockPos(x, y, z);
        Iterable<BlockPos> positions = BlockPos.getAllInBox(center.add(-RADIUS, -RADIUS, -RADIUS), center.add(RADIUS, RADIUS, RADIUS));
        for (BlockPos pos : positions) {
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof TileEntityMobSpawner) {
                if (world.isRemote) {
                    // Spawn a ring of particles around the spawner
                    double cx = pos.getX() + 0.5;
                    double cy = pos.getY() + 1.0;
                    double cz = pos.getZ() + 0.5;
                    int count = 10;
                    double radius = 0.7;
                    for (int i = 0; i < count; i++) {
                        double angle = 2 * Math.PI * i / count;
                        double px = cx + radius * Math.cos(angle);
                        double pz = cz + radius * Math.sin(angle);
                        net.minecraft.util.EnumParticleTypes type = net.minecraft.util.EnumParticleTypes.SPELL_MOB;
                        // Use ParticleBuilder for mod particles
                        electroblob.wizardry.util.ParticleBuilder.create(electroblob.wizardry.util.ParticleBuilder.Type.SPARKLE)
                                .pos(px, cy, pz)
                                .clr(180, 255, 255)
                                .scale(0.3f)
                                .time(20)
                                .spawn(world);
                    }
                }
            }
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (world.isRemote) {
            BlockPos center = player.getPosition();
            int radius = 10;
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dy = -radius; dy <= radius; dy++) {
                    for (int dz = -radius; dz <= radius; dz++) {
                        BlockPos pos = center.add(dx, dy, dz);
                        if (world.isAirBlock(pos)) {
                            BlockPos below = pos.down();
                            // Use only block light for mob spawn check
                            if (world.getBlockState(below).isTopSolid() && world.getLightFor(EnumSkyBlock.BLOCK, pos) <= 7) {
                                for (int i = 0; i < 3; i++) {
                                    electroblob.wizardry.util.ParticleBuilder.create(electroblob.wizardry.util.ParticleBuilder.Type.SPARKLE)
                                        .pos(pos.getX() + 0.5 + world.rand.nextGaussian() * 0.2, pos.getY() + 0.1, pos.getZ() + 0.5 + world.rand.nextGaussian() * 0.2)
                                        .scale(0.3f + world.rand.nextFloat() * 0.2f)
                                        .clr(180, 255, 255)
                                        .spawn(world);
                                }
                            }
                        }
                    }
                }
            }
        }
        player.swingArm(hand);
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(I18n.format("item.ancientspellcraft:charm_specterlight_torch.desc"));
    }
} 