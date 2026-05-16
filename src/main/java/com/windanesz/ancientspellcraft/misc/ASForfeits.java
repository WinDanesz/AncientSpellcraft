package com.windanesz.ancientspellcraft.misc;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.entity.living.EntityFireAnt;
import com.windanesz.ancientspellcraft.entity.living.EntitySkeletonMageMinion;
import com.windanesz.ancientspellcraft.entity.living.EntityWolfMinion;
import com.windanesz.ancientspellcraft.registry.ASBlocks;
import com.windanesz.ancientspellcraft.registry.ASItems;
import com.windanesz.ancientspellcraft.registry.ASPotions;
import com.windanesz.ancientspellcraft.registry.ASSpells;
import com.windanesz.ancientspellcraft.spell.AttireAlteration;
import com.windanesz.ancientspellcraft.spell.CurseArmor;
import com.windanesz.ancientspellcraft.spell.FairyRing;
import com.windanesz.ancientspellcraft.spell.QuicksandRing;
import com.windanesz.ancientspellcraft.spell.ShockZone;
import com.windanesz.ancientspellcraft.spell.TemporalCasualty;
import com.windanesz.ancientspellcraft.util.ASUtils;
import electroblob.wizardry.constants.Element;
import electroblob.wizardry.constants.Tier;
import electroblob.wizardry.data.WizardData;
import electroblob.wizardry.entity.construct.EntityBubble;
import electroblob.wizardry.entity.living.EntitySpectralGolem;
import electroblob.wizardry.item.ItemArtefact;
import electroblob.wizardry.misc.Forfeit;
import electroblob.wizardry.registry.WizardryBlocks;
import electroblob.wizardry.registry.WizardryPotions;
import electroblob.wizardry.tileentity.TileEntityTimer;
import electroblob.wizardry.util.BlockUtils;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class ASForfeits {

    // Safe duration value for semi-permanent/infinite effects (approx. 14 hours) to prevent packet overflows
    private static final int INF_DURATION = 1000000;

    public static <T> void spawnMobs(Function<World, T> minionFactory, int count, World world, EntityPlayer player) {
        if (!world.isRemote) {
            for (int i = 0; i < count; i++) {
                BlockPos pos = BlockUtils.findNearbyFloorSpace(player, 4, 2);
                if (pos == null) { break; }

                T minion = minionFactory.apply(world);
                ((EntityLivingBase) minion).setPosition(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
                ((EntityLivingBase) minion).setRevengeTarget(player);
                world.spawnEntity(((EntityLivingBase) minion));
            }
        }
    }

    public static void add(Tier tier, Element element, Forfeit forfeit) {
        Forfeit.add(tier, element, forfeit);
    }

    private static Forfeit create(String name, BiConsumer<World, EntityPlayer> effect) {
        return Forfeit.create(new ResourceLocation(AncientSpellcraft.MODID, name), effect);
    }

    public static void register() {

        // Enforce server side checks inside lambdas modifying blocks or entity statuses globally
        add(Tier.NOVICE, Element.LIGHTNING, create("self_glow", (w, p) -> {
            if (!w.isRemote) p.addPotionEffect(new PotionEffect(MobEffects.GLOWING, 4000));
        }));

        add(Tier.ADVANCED, Element.LIGHTNING, create("area_shock", (w, p) -> {
            if (!w.isRemote) {
                ShockZone.makeZone(w, null, p.getPosition(), new SpellModifiers());
            }
        }));

        add(Tier.NOVICE, Element.FIRE, create("burn_area", (w, p) -> {
            if (!w.isRemote) {
                for (int i = 0; i < 4; i++) {
                    BlockPos pos = BlockUtils.findNearbyFloorSpace(p, 2, 2);
                    if (pos != null) { w.setBlockState(pos, Blocks.FIRE.getDefaultState()); }
                }
            }
        }));

        add(Tier.NOVICE, Element.FIRE, create("fire_ant", (w, p) -> spawnMobs(EntityFireAnt::new, 2, w, p)));
        add(Tier.ADVANCED, Element.FIRE, create("creeper", (w, p) -> spawnMobs(EntityCreeper::new, 1, w, p)));
        add(Tier.ADVANCED, Element.FIRE, create("fire_ant_swarm", (w, p) -> spawnMobs(EntityFireAnt::new, 12, w, p)));

        add(Tier.MASTER, Element.FIRE, create("nether_portals", (w, p) -> {
            if (!w.isRemote) {
                for (int i = 0; i < 8; i++) {
                    BlockPos pos = BlockUtils.findNearbyFloorSpace(p, 2, 3);
                    if (pos != null) { w.setBlockState(pos, ASBlocks.NETHER_FIRE.getDefaultState()); }
                }
            }
        }));

        add(Tier.NOVICE, Element.EARTH, create("spider", (w, p) -> spawnMobs(EntitySpider::new, 1, w, p)));

        add(Tier.ADVANCED, Element.EARTH, create("wolf_swarm", (w, p) -> {
            if (!w.isRemote) {
                for (int i = 0; i < 4; i++) {
                    BlockPos pos = BlockUtils.findNearbyFloorSpace(p, 4, 2);
                    if (pos == null) break;

                    EntityWolfMinion mob = new EntityWolfMinion(w);
                    mob.setPosition(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);
                    mob.setAttackTarget(p);
                    w.spawnEntity(mob);
                }
            }
        }));

        add(Tier.APPRENTICE, Element.EARTH, create("quicksand_ring", (w, p) -> {
            if (!w.isRemote) {
                QuicksandRing.summonQuickSandRing(w, null, p.getPosition(), new SpellModifiers());
            }
        }));

        add(Tier.ADVANCED, Element.EARTH, create("mushroom_ring", (w, p) -> {
            if (!w.isRemote) {
                FairyRing.summonMushroomRing(w, null, p.getPosition(), new SpellModifiers());
            }
        }));

        add(Tier.ADVANCED, Element.EARTH, create("trapped_in_bubble", (w, p) -> {
            if (!w.isRemote) {
                EntityBubble bubble = new EntityBubble(w);
                bubble.setPosition(p.posX, p.posY, p.posZ);
                bubble.lifetime = (160);
                bubble.isDarkOrb = false;
                w.spawnEntity(bubble);
                p.startRiding(bubble);
            }
        }));

        add(Tier.ADVANCED, Element.NECROMANCY, create("curse_armour", (w, p) -> {
            if (!w.isRemote && !ItemArtefact.isArtefactActive(p, ASItems.amulet_curse_ward) && !p.isPotionActive(ASPotions.curse_ward)) {
                CurseArmor.curseRandomArmourPiece(p, w);
            }
        }));

        // Swapped out unsafe Integer.MAX_VALUE duration variables to block networking overflow desyncs
        add(Tier.MASTER, Element.NECROMANCY, create("unluck", (w, p) -> {
            if (!w.isRemote) p.addPotionEffect(new PotionEffect(MobEffects.UNLUCK, INF_DURATION));
        }));

        add(Tier.MASTER, Element.NECROMANCY, create("self_curse_of_enfeeblement", (w, p) -> {
            if (!w.isRemote) p.addPotionEffect(new PotionEffect(WizardryPotions.curse_of_enfeeblement, INF_DURATION));
        }));

        add(Tier.MASTER, Element.NECROMANCY, create("self_curse_of_gills", (w, p) -> {
            if (!w.isRemote) p.addPotionEffect(new PotionEffect(ASPotions.curse_of_gills, INF_DURATION));
        }));

        add(Tier.MASTER, Element.NECROMANCY, create("self_curse_of_umbra", (w, p) -> {
            if (!w.isRemote) p.addPotionEffect(new PotionEffect(ASPotions.curse_of_umbra, INF_DURATION));
        }));

        add(Tier.ADVANCED, Element.NECROMANCY, create("skeleton_mage_swarm", (w, p) -> {
            if (!w.isRemote) {
                for (int i = 0; i < 4; i++) {
                    BlockPos pos = BlockUtils.findNearbyFloorSpace(p, 4, 2);
                    if (pos == null) break;

                    Element element = (Element.values()[w.rand.nextInt(Element.values().length - 1) + 1]);
                    EntitySkeletonMageMinion mob = new EntitySkeletonMageMinion(w, element);
                    mob.setItemStackToSlot(EntityEquipmentSlot.HEAD, new ItemStack(ASItems.wizard_hat_ancient));
                    mob.setPosition(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);
                    w.spawnEntity(mob);
                }
            }
        }));

        add(Tier.MASTER, Element.NECROMANCY, create("self_curse_of_ender", (w, p) -> {
            if (!w.isRemote) p.addPotionEffect(new PotionEffect(ASPotions.curse_of_ender, INF_DURATION));
        }));

        add(Tier.MASTER, Element.NECROMANCY, create("self_curse_of_death", (w, p) -> {
            if (!w.isRemote) p.addPotionEffect(new PotionEffect(ASPotions.curse_of_death, 168000));
        }));

        add(Tier.MASTER, Element.SORCERY, create("spectral_golem", (w, p) -> spawnMobs(EntitySpectralGolem::new, 1, w, p)));

        add(Tier.MASTER, Element.SORCERY, create("warp_armour", (w, p) -> {
            if (!w.isRemote && ASSpells.attire_alteration.isEnabled() && WizardData.get(p) != null && WizardData.get(p).hasSpellBeenDiscovered(ASSpells.attire_alteration)){
                AttireAlteration.swapArmour(p, w, new SpellModifiers(), 0);
            }
        }));

        add(Tier.MASTER, Element.SORCERY, create("contained_in_sphere", (w, p) -> {
            if (!w.isRemote) {
                for (BlockPos currPos : ASUtils.getHollowSphere(p, new SpellModifiers(), 4)) {
                    if (BlockUtils.canBlockBeReplaced(w, currPos)){
                        w.setBlockState(currPos, WizardryBlocks.spectral_block.getDefaultState());
                        if (w.getTileEntity(currPos) instanceof TileEntityTimer){
                            ((TileEntityTimer)w.getTileEntity(currPos)).setLifetime(1200);
                        }
                    }
                }
            }
        }));

        add(Tier.MASTER, Element.SORCERY, create("summon_tnt", (w, p) -> {
            if (!w.isRemote) {
                EntityTNTPrimed tnt = new EntityTNTPrimed(w, p.posX, p.posY, p.posZ, null);
                tnt.setFuse(60);
                w.spawnEntity(tnt);
                w.playSound(null, tnt.posX, tnt.posY, tnt.posZ, SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 1.0F, 1.0F);
            }
        }));

        add(Tier.MASTER, Element.SORCERY, create("self_temporal_curse", (w, p) -> {
            if (!w.isRemote) TemporalCasualty.cursePlayer(p);
        }));

        add(Tier.ADVANCED, Element.HEALING, create("self_transience", (w, p) -> {
            if (!w.isRemote) p.addPotionEffect(new PotionEffect(WizardryPotions.transience, 900));
        }));
    }
}