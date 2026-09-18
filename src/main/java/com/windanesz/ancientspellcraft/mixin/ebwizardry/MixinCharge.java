package com.windanesz.ancientspellcraft.mixin.ebwizardry;

import com.windanesz.ancientspellcraft.Settings;
import com.windanesz.ancientspellcraft.registry.ASBlocks;
import com.windanesz.ancientspellcraft.registry.ASItems;
import com.windanesz.ancientspellcraft.tileentity.TileEntityLightningBlock;
import electroblob.wizardry.item.ItemArtefact;
import electroblob.wizardry.spell.Charge;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Adds Ring of Charge ({@link ASItems#ring_charge}) compatibility to the Charge spell: while charging, it places a
 * short-lived lightning block under the player. This used to live in a {@code ChargeAS extends Spell} class that
 * fully reimplemented Charge from scratch and got re-registered under the same name - which, besides the networkID
 * hazard shared with all the other overrides, also used its own separate {@code CHARGE_TIME} variable key instead of
 * {@link Charge#CHARGE_TIME}. That silently broke {@link Charge#onLivingAttackEvent}'s melee-immunity-while-charging
 * check, since it reads {@code Charge.CHARGE_TIME}, which never got set while the override was active. Injecting
 * into the original {@code update} method instead means there's only ever one {@code CHARGE_TIME} variable again,
 * so that check works correctly.
 */
@Mixin(Charge.class)
public class MixinCharge {

	@Inject(method = "update", at = @At("HEAD"), remap = false)
	private static void addRingOfChargeEffect(EntityPlayer player, Integer chargeTime, CallbackInfoReturnable<Integer> cir){

		if(!Settings.spellCompatSettings.chargeSpellOverride) return;
		if(chargeTime == null || chargeTime <= 0) return;
		if(player.world.isRemote) return;
		if(!ItemArtefact.isArtefactActive(player, ASItems.ring_charge)) return;

		if(player.world.isAirBlock(player.getPosition())){
			player.world.setBlockState(player.getPosition(), ASBlocks.lightning_block.getDefaultState());
			TileEntity tile = player.world.getTileEntity(player.getPosition());
			if(tile instanceof TileEntityLightningBlock){
				((TileEntityLightningBlock)tile).setLifetime(120);
				((TileEntityLightningBlock)tile).setCaster(player);
			}
		}
	}
}
