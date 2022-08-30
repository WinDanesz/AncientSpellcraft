package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.potion.PotionMetamagicEffect;
import com.windanesz.ancientspellcraft.registry.ASItems;
import electroblob.wizardry.item.ItemArtefact;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Arrays;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class MetaSpellBuff extends Spell {

	/**
	 * Author: WinDanesz,
	 * Electroblob: of the methods taken from SpellBuffapplyEffects
	 */
	/**
	 * An array of factories for the status effects that this spell applies to its caster. The effect factory
	 * avoids the issue of the potions being registered after the spell.
	 */
	protected final Supplier<Potion>[] effects;
	/**
	 * A set of all the different potions (status effects) that this spell applies to its caster. Loaded during
	 * init().
	 */
	protected Set<Potion> potionSet;
	/**
	 * The RGB colour values of the particles spawned when this spell is cast.
	 */
	protected final float r, g, b;

	/**
	 * The number of sparkle particles spawned when this spell is cast. Defaults to 10.
	 */
	protected float particleCount = 10;

	public final String CHARGEUP_COOLDOWN = "chargeup_cooldown";
	//
	//	public MetaSpellBuff(String name, float r, float g, float b, Supplier<Potion>... effects) {
	//		super(AncientSpellcraft.MODID, name, r, g, b, effects);
	//	}

	@SafeVarargs
	public MetaSpellBuff(String name, float r, float g, float b, Supplier<Potion>... effects) {
		super(AncientSpellcraft.MODID, name, EnumAction.BOW, false);
		this.effects = effects;
		this.r = r;
		this.g = g;
		this.b = b;
		this.npcSelector((e, o) -> true);
		//		addProperties(CHARGEUP_COOLDOWN);
	}

	@Override
	public void init() {
		// Loads the potion set
		this.potionSet = Arrays.stream(effects).map(Supplier::get).collect(Collectors.toSet());

		for (Potion potion : potionSet) {
			if (!potion.isInstant())
				addProperties(getDurationKey(potion));
		}
	}


	@SideOnly(Side.CLIENT)
	public String getDisplayNameWithFormatting(){
		return TextFormatting.GOLD +  net.minecraft.client.resources.I18n.format(getTranslationKey());
	}

	@Override
	public boolean cast(World world, EntityPlayer caster, EnumHand hand, int ticksInUse, SpellModifiers modifiers) {

		if (!world.isRemote) {

			Set<Potion> activePotions = caster.getActivePotionMap().keySet();

			for (Potion activePotion : activePotions) {
				for (Potion potion : potionSet) {
					if (activePotion instanceof PotionMetamagicEffect && potion != activePotion) {
						caster.removePotionEffect(activePotion);
					}
				}
			}

			for (Potion potion : potionSet) {
				boolean shouldAmplify = false;
				int currentAmplifier = 0;
				boolean active = caster.isPotionActive(potion);

				if (active) {
					currentAmplifier = caster.getActivePotionEffect(potion).getAmplifier();

				}

				int bonusAmplifier = active ? currentAmplifier + 1 : ItemArtefact.isArtefactActive(caster, ASItems.charm_metamagic_amplifier) ? 1 : 0;

				if (bonusAmplifier > 2) {
					return false;
				} else {
					shouldAmplify = true;
				}

				caster.removePotionEffect(potion);

				caster.addPotionEffect(new PotionEffect(potion, (int) (getProperty(getDurationKey(potion)).floatValue() * modifiers.get(WizardryItems.duration_upgrade)),
						bonusAmplifier, false, true));

				//				if (shouldAmplify) {
				if (currentAmplifier == 2) {

					float cooldown = modifiers.get(WizardryItems.cooldown_upgrade);
					modifiers.set(WizardryItems.cooldown_upgrade, cooldown * 0.1F, true);
				}

				//				}
			}
			this.playSound(world, caster, ticksInUse, -1, modifiers);

			return true;
		} else {
			for (Potion potion : potionSet) {
				boolean active = caster.isPotionActive(potion);
				if (active) {
					this.playSound(world, caster, ticksInUse, -1, modifiers);
					return true;
				}
			}
			return false;
		}
	}

	/**
	 * Spawns buff particles around the caster. Override to add a custom particle effect. Only called client-side.
	 */
	protected void spawnParticles(World world, EntityLivingBase caster, SpellModifiers modifiers) {

		for (int i = 0; i < particleCount; i++) {
			double x = caster.posX + world.rand.nextDouble() * 2 - 1;
			double y = caster.getEntityBoundingBox().minY + caster.getEyeHeight() - 0.5 + world.rand.nextDouble();
			double z = caster.posZ + world.rand.nextDouble() * 2 - 1;
			ParticleBuilder.create(ParticleBuilder.Type.SPARKLE).pos(x, y, z).vel(0, 0.1, 0).clr(r, g, b).spawn(world);
		}

		ParticleBuilder.create(ParticleBuilder.Type.BUFF).entity(caster).clr(r, g, b).spawn(world);
	}

	protected static String getDurationKey(Potion potion) {
		return potion.getRegistryName().getPath() + "_duration";
	}

	public boolean requiresPacket() {
		return true;
	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == ASItems.ancient_spell_book || item == ASItems.ancient_spellcraft_scroll;
	}
}
