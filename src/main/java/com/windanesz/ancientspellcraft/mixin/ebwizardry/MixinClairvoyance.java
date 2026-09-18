package com.windanesz.ancientspellcraft.mixin.ebwizardry;

import com.windanesz.ancientspellcraft.Settings;
import com.windanesz.ancientspellcraft.integration.baubles.ASBaublesIntegration;
import com.windanesz.ancientspellcraft.item.ItemMonsterCharm;
import com.windanesz.ancientspellcraft.registry.ASItems;
import electroblob.wizardry.data.WizardData;
import electroblob.wizardry.item.ItemArtefact;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.spell.Clairvoyance;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Comparator;
import java.util.List;

/**
 * Adds Monster Charm ({@link ASItems#charm_monster_charm}) compatibility to the Clairvoyance spell: if a charm
 * attuned to a mob type is active, retargets Clairvoyance at the nearest matching mob instead of the normal
 * player-marked location. Previously this lived in a {@code ClairvoyanceAS extends Spell} class that fully
 * reimplemented Clairvoyance from scratch and got re-registered under the same name, with the networkID hazard that
 * came with that (see {@link MixinMine}'s javadoc for the full story). Injecting into the original {@code cast}
 * method instead means the rest of Clairvoyance's behaviour (including its own {@code onRightClickBlockEvent}
 * handler) is untouched and always in sync with this.
 */
@Mixin(Clairvoyance.class)
public class MixinClairvoyance {

	@Inject(method = "cast", at = @At("HEAD"), cancellable = true, remap = false)
	private void retargetForMonsterCharm(World world, EntityPlayer caster, EnumHand hand, int ticksInUse, SpellModifiers modifiers, CallbackInfoReturnable<Boolean> cir){

		if(!Settings.spellCompatSettings.clairvoyanceSpellOverride) return;
		if(!ItemArtefact.isArtefactActive(caster, ASItems.charm_monster_charm)) return;

		List<ItemStack> charm = ASBaublesIntegration.getEquippedArtefactStacks(caster, ItemArtefact.Type.CHARM);
		if(charm.isEmpty() || charm.get(0).getItem() != ASItems.charm_monster_charm) return;

		String mobId = ItemMonsterCharm.getAttunedMob(charm.get(0));
		if(mobId == null) return;

		ResourceLocation mobRes = new ResourceLocation(mobId);
		Clairvoyance self = (Clairvoyance)(Object)this;
		// 625 default range of the spell, reduced to 25% for the charm
		double radius = self.getProperty(Spell.RANGE).floatValue() * 0.25 * modifiers.get(WizardryItems.range_upgrade);

		EntityLivingBase closest = world.getEntitiesWithinAABB(EntityLivingBase.class, caster.getEntityBoundingBox().grow(radius))
				.stream()
				.filter(e -> EntityList.getKey(e.getClass()) != null && EntityList.getKey(e.getClass()).equals(mobRes))
				.min(Comparator.comparingDouble(e -> e.getDistance(caster)))
				.orElse(null);

		if(closest != null){

			BlockPos targetPos = closest.getPosition();
			WizardData data = WizardData.get(caster);

			if(data != null){
				data.setVariable(Clairvoyance.LOCATION_KEY, targetPos);
				data.setVariable(Clairvoyance.DIMENSION_KEY, caster.dimension);
				if(!world.isRemote){
					caster.sendStatusMessage(new TextComponentTranslation("item.ancientspellcraft:monster_charm.found", closest.getDisplayName()), true);
				}
			}

		}else{
			if(!world.isRemote){
				caster.sendStatusMessage(new TextComponentTranslation("item.ancientspellcraft:monster_charm.not_found"), true);
			}
			cir.setReturnValue(false);
			cir.cancel();
		}
	}
}
