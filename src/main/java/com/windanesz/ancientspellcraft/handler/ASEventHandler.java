package com.windanesz.ancientspellcraft.handler;

import com.windanesz.ancientspellcraft.registry.AncientSpellcraftEnchantments;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftPotions;
import com.windanesz.ancientspellcraft.spell.Martyr;
import electroblob.wizardry.data.WizardData;
import electroblob.wizardry.integration.DamageSafetyChecker;
import electroblob.wizardry.registry.WizardrySounds;
import electroblob.wizardry.spell.ImbueWeapon;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.IElementalDamage;
import electroblob.wizardry.util.MagicDamage;
import electroblob.wizardry.util.ParticleBuilder;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Iterator;
import java.util.UUID;

@Mod.EventBusSubscriber
public class ASEventHandler {

	private ASEventHandler() {} // No instances!

	@SubscribeEvent(priority = EventPriority.LOW) // Low priority in case the event gets cancelled at default priority
	public static void onLivingAttackEvent(LivingAttackEvent event) {

		if (event.getSource() != null && event.getSource().getTrueSource() instanceof EntityLivingBase) {

			EntityLivingBase attacker = (EntityLivingBase) event.getSource().getTrueSource();
			World world = event.getEntityLiving().world;

			if (!attacker.getHeldItemMainhand().isEmpty() && ImbueWeapon.isSword(attacker.getHeldItemMainhand())) {

				int level = EnchantmentHelper.getEnchantmentLevel(AncientSpellcraftEnchantments.static_charge,
						attacker.getHeldItemMainhand());
				if (level > 0 && event.getEntityLiving().world.isRemote) {
					// Particle effect
					for (int i = 0; i < 5; i++) {
						ParticleBuilder.create(ParticleBuilder.Type.SPARK, event.getEntityLiving()).spawn(event.getEntityLiving().world);
					}
				}
			}
		}

	}

	@SubscribeEvent
	public static void onLivingHurtEvent(LivingHurtEvent event) {
		if (!event.getEntity().world.isRemote && event.getEntityLiving().isPotionActive(AncientSpellcraftPotions.martyr_beneficial) && event.getEntityLiving() instanceof EntityPlayer
				&& !event.getSource().isUnblockable() && !(event.getSource() instanceof IElementalDamage
				&& ((IElementalDamage) event.getSource()).isRetaliatory())) {

			EntityPlayer player = (EntityPlayer) event.getEntityLiving(); // the beneficial who is attacked
			WizardData data = WizardData.get(player);

			if (data != null) {

				for (Iterator<UUID> iterator = Martyr.getMartyrBoundEntities(data).iterator(); iterator.hasNext(); ) {

					Entity entity = EntityUtils.getEntityByUUID(player.world, iterator.next()); // the target who will take the damage instead

					if (entity == null)
						iterator.remove();

					if (entity instanceof EntityPlayer && ((EntityPlayer) entity).isPotionActive(AncientSpellcraftPotions.martyr)) {
						// Retaliatory effect
						if (DamageSafetyChecker.attackEntitySafely(entity, MagicDamage.causeDirectMagicDamage(player,
								MagicDamage.DamageType.MAGIC, true), event.getAmount(), event.getSource().getDamageType(),
								DamageSource.MAGIC, false)) {
							// Sound only plays if the damage succeeds
							entity.playSound(WizardrySounds.SPELL_CURSE_OF_SOULBINDING_RETALIATE, 1.0F, player.world.rand.nextFloat() * 0.2F + 1.0F);
						}
						// cancel the damage
						event.setCanceled(true);
					}
				}

			}
		}

		// Static weapon
		if (event.getSource().getTrueSource() instanceof EntityLivingBase) {

			EntityLivingBase attacker = (EntityLivingBase) event.getSource().getTrueSource();

			// Players can only ever attack with their main hand, so this is the right method to use here.
			if (!attacker.getHeldItemMainhand().isEmpty() && ImbueWeapon.isSword(attacker.getHeldItemMainhand())) {

				int level = EnchantmentHelper.getEnchantmentLevel(AncientSpellcraftEnchantments.static_charge,
						attacker.getHeldItemMainhand());

				if (level > 0 && !MagicDamage.isEntityImmune(MagicDamage.DamageType.SHOCK, event.getEntityLiving())) {
					event.setAmount(event.getAmount() + level * 2);
				}
			}
		}

	}

}
