package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.registry.ASItems;
import com.windanesz.ancientspellcraft.registry.ASSpells;
import com.windanesz.ancientspellcraft.util.ASUtils;
import com.windanesz.ancientspellcraft.util.WizardArmourUtils;
import electroblob.wizardry.client.DrawingUtils;
import electroblob.wizardry.constants.Element;
import electroblob.wizardry.data.IStoredVariable;
import electroblob.wizardry.data.Persistence;
import electroblob.wizardry.data.WizardData;
import electroblob.wizardry.item.ItemWizardArmour;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.AllyDesignationSystem;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.Optional;
import java.util.Random;

public class AbsorbPotion extends Spell implements IClassSpell {

	public static final IStoredVariable<String> EFFECT = IStoredVariable.StoredVariable.ofString("AbsorbPotionEffect", Persistence.ALWAYS).setSynced();
	public static final IStoredVariable<Integer> DURATION = IStoredVariable.StoredVariable.ofInt("AbsorbPotionDuration", Persistence.ALWAYS).withTicker(AbsorbPotion::update);

	public AbsorbPotion() {
		super(AncientSpellcraft.MODID, "absorb_potion", SpellActions.SUMMON, true);
		WizardData.registerStoredVariables(EFFECT);
		addProperties(EFFECT_RADIUS);
	}

	@Override
	protected SoundEvent[] createSounds() {
		return createContinuousSpellSounds();
	}

	@Override
	public boolean cast(World world, EntityPlayer caster, EnumHand hand, int ticksInUse, SpellModifiers modifiers) {
		Optional<Element> elementOptional = WizardArmourUtils.getFullSetElementForClassOptional(caster, ItemWizardArmour.ArmourClass.WARLOCK);
		ItemStack potion = caster.getHeldItemOffhand();
		if (!(potion.getItem() instanceof ItemPotion)) {
			ASUtils.sendMessage(caster, "You must hold a potion in your offhand", true);
		}
		if (elementOptional.isPresent()) {
			Element element = elementOptional.get();

			Random rand = caster.world.rand;
			double posX = caster.posX;
			double posY = caster.posY;
			double posZ = caster.posZ;

			if (world.isRemote) {
				if (world.getTotalWorldTime() % 3 == 0) {
					ParticleBuilder.create(WarlockElementalSpellEffects.getElementalParticle(element), rand, posX + rand.nextDouble() * 0.5d * (rand.nextBoolean() ? 1 : -1), posY, posZ + rand.nextDouble() * 0.5d * (rand.nextBoolean() ? 1 : -1), 0.03, true).vel(0, 0.3, 0).clr(WarlockElementalSpellEffects.PARTICLE_COLOURS.get(element)[0]).time(20 + rand.nextInt(50)).spawn(world);
					ParticleBuilder.create(WarlockElementalSpellEffects.getElementalParticle(element), rand, posX + rand.nextDouble() * 0.5d * (rand.nextBoolean() ? 1 : -1), posY, posZ + rand.nextDouble() * 0.5d * (rand.nextBoolean() ? 1 : -1), 0.03, true).vel(0, 0.3, 0).clr(WarlockElementalSpellEffects.PARTICLE_COLOURS.get(element)[1]).time(20 + rand.nextInt(50)).spawn(world);

					ParticleBuilder.create(WarlockElementalSpellEffects.getElementalParticle(element), rand, posX + rand.nextDouble() * 0.2d * (rand.nextBoolean() ? 1 : -1), posY, posZ + rand.nextDouble() * 0.2d * (rand.nextBoolean() ? 1 : -1), 0.03, true).spin(0.7, 0.05).vel(0, 0.3, 0).clr(WarlockElementalSpellEffects.PARTICLE_COLOURS.get(element)[2]).time(20 + rand.nextInt(50)).spawn(world);
				}

				// horizontal particle on the floor
				ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(caster.posX, caster.posY + 0.101, caster.posZ).face(EnumFacing.UP).clr(DrawingUtils.mix(WarlockElementalSpellEffects.PARTICLE_COLOURS.get(element)[1], WarlockElementalSpellEffects.PARTICLE_COLOURS.get(element)[2], 0.5f)).collide(false).scale(2.3F).time(10).spawn(world);
			}
			if (ticksInUse == 60 && !world.isRemote) {

				WizardData data = WizardData.get(caster);
				for (PotionEffect potioneffect : PotionUtils.getEffectsFromStack(caster.getHeldItemOffhand())) {
					if (!potioneffect.getPotion().isInstant()) {
						ASUtils.sendMessage(caster, "Absorbed " + caster.getHeldItemOffhand().getDisplayName(), true);
						data.setVariable(EFFECT, potioneffect.getPotion().getRegistryName().toString());
						data.setVariable(DURATION, (int) (potioneffect.getDuration() * 0.7f));
						caster.getHeldItemOffhand().shrink(1);
						caster.getCooldownTracker().setCooldown(caster.getHeldItemMainhand().getItem(), 20);
						data.sync();
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	protected void playSound(World world, EntityLivingBase entity, int ticksInUse, int duration, SpellModifiers modifiers, String... sounds) {
		this.playSoundLoop(world, entity, ticksInUse);
	}

	@Override
	protected void playSound(World world, double x, double y, double z, int ticksInUse, int duration, SpellModifiers modifiers, String... sounds) {
		this.playSoundLoop(world, x, y, z, ticksInUse, duration);
	}

	@Override
	public ItemWizardArmour.ArmourClass getArmourClass() {
		return ItemWizardArmour.ArmourClass.WARLOCK;
	}

	private static int update(EntityPlayer player, Integer duration) {

		if (duration == null) {return 0;}

 		if (duration > 0 && duration % 10 == 0 && !player.world.isRemote) {

			WizardData data = WizardData.get(player);

			String potionName = data.getVariable(EFFECT);

			if (potionName == null) {
				WizardData.get(player).setVariable(AbsorbPotion.EFFECT, null);
				WizardData.get(player).sync();
				return 0;
			}

			Potion potion = ForgeRegistries.POTIONS.getValue(new ResourceLocation(potionName));
			if (potion != null) {
				boolean isAllyEffect = !potion.isBadEffect();
				for (EntityLivingBase target : EntityUtils.getEntitiesWithinRadius(ASSpells.absorb_potion.getProperty(EFFECT_RADIUS).floatValue(), player.posX, player.posY, player.posZ, player.world, EntityLivingBase.class)) {
					boolean isAllied = AllyDesignationSystem.isAllied(player, target);
					if (target != player && ((!isAllyEffect && !isAllied)) || (target.ticksExisted > 20 && isAllyEffect && isAllied)) {
						target.addPotionEffect(new PotionEffect(potion, 50, 0));
					}
				}
			}
		}

		if (duration > 1) {
			duration--;
		} else if (duration == 1) {
			duration--;
			WizardData.get(player).setVariable(EFFECT, "none");
			WizardData.get(player).sync();
		}

		return duration;
	}

	public boolean applicableForItem(Item item) {
		return item == ASItems.forbidden_tome;
	}

	@Override
	public boolean canBeCastBy(EntityLiving npc, boolean override) {
		return false;
	}

	@Override
	public boolean canBeCastBy(TileEntityDispenser dispenser) {
		return false;
	}
}