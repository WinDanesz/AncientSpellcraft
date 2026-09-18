package com.windanesz.ancientspellcraft.mixin.ebwizardry;

import com.windanesz.ancientspellcraft.integration.baubles.ASBaublesIntegration;
import com.windanesz.ancientspellcraft.item.ItemVesselOfTheWitheredOath;
import com.windanesz.ancientspellcraft.registry.ASItems;
import electroblob.wizardry.item.ItemArtefact;
import electroblob.wizardry.registry.Spells;
import electroblob.wizardry.spell.SpellAreaEffect;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

/**
 * The other half of {@link MixinPlagueOfDarkness}'s Vessel of the Withered Oath support: once a successful cast
 * consumes the vessel's stored curse (applied per-target in {@code affectEntity}), this removes it from the
 * artefact. This has to hook {@code cast} rather than {@code affectEntity}, because {@code plague_of_darkness} sets
 * {@code alwaysSucceed(true)}, so the curse should be spent once per successful cast regardless of how many targets
 * (if any) were actually hit - not once per affected target.
 * <p></p>
 * {@code cast(World, EntityPlayer, EnumHand, int, SpellModifiers)} is declared on the shared {@link SpellAreaEffect}
 * base class rather than on {@code PlagueOfDarkness} itself (which doesn't override it), so this mixin targets
 * {@link SpellAreaEffect} directly and guards its extra behaviour to only run for the one spell instance it's
 * meant for - every other area-effect spell passes straight through unaffected.
 */
@Mixin(SpellAreaEffect.class)
public class MixinSpellAreaEffect {

	@Inject(method = "cast(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/util/EnumHand;ILelectroblob/wizardry/util/SpellModifiers;)Z",
			at = @At("RETURN"), remap = false)
	private void removeSpentWitheredOathCurse(World world, EntityPlayer caster, EnumHand hand, int ticksInUse, SpellModifiers modifiers, CallbackInfoReturnable<Boolean> cir){

		if((SpellAreaEffect)(Object)this != Spells.plague_of_darkness) return;
		if(!cir.getReturnValueZ()) return;
		if(!ItemArtefact.isArtefactActive(caster, ASItems.charm_vessel_of_the_withered_oath)) return;

		List<ItemStack> stacks = ASBaublesIntegration.getEquippedArtefactStacks(caster, ItemArtefact.Type.CHARM);
		if(stacks.isEmpty() || stacks.get(0).getItem() != ASItems.charm_vessel_of_the_withered_oath) return;

		ItemStack itemstack = stacks.get(0);
		((ItemVesselOfTheWitheredOath)itemstack.getItem()).removeStoredCurse(itemstack);
		ASBaublesIntegration.setArtefactToSlot(caster, itemstack, ItemArtefact.Type.CHARM);
	}
}
