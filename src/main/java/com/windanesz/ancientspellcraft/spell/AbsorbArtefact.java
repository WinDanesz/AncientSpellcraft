package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.Settings;
import com.windanesz.ancientspellcraft.registry.ASItems;
import com.windanesz.ancientspellcraft.util.ASUtils;
import com.windanesz.ancientspellcraft.util.WizardArmourUtils;
import electroblob.wizardry.Wizardry;
import electroblob.wizardry.client.DrawingUtils;
import electroblob.wizardry.constants.Element;
import electroblob.wizardry.data.IStoredVariable;
import electroblob.wizardry.data.Persistence;
import electroblob.wizardry.data.WizardData;
import electroblob.wizardry.item.ItemArtefact;
import electroblob.wizardry.item.ItemWizardArmour;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class AbsorbArtefact extends Spell implements IClassSpell {

	public static final IStoredVariable<String> ARTEFACT = IStoredVariable.StoredVariable.ofString("AbsorbArtefact", Persistence.ALWAYS);
	public static final IStoredVariable<Integer> POWER_GEM_COUNT = IStoredVariable.StoredVariable.ofInt("PowerGemCount", Persistence.ALWAYS);
	public static final String TIER_LIMIT = "tier_limit";

	public static enum Rarities {
		UNCOMMON,
		RARE,
		EPIC;

		static boolean isKnownRarity(String rarity) {
			for (Rarities r : Rarities.values()) {
				if (r.name().equalsIgnoreCase(rarity)) {
					return true;
				}
			}
			return false;
		}
	}

	public AbsorbArtefact() {
		super(AncientSpellcraft.MODID, "absorb_artefact", SpellActions.SUMMON, true);
		WizardData.registerStoredVariables(ARTEFACT, POWER_GEM_COUNT);
		addProperties(TIER_LIMIT);
	}

	@Override
	protected SoundEvent[] createSounds() {
		return createContinuousSpellSounds();
	}

	@Override
	public boolean cast(World world, EntityPlayer caster, EnumHand hand, int ticksInUse, SpellModifiers modifiers) {
		Element element = getElementOrMagicElement(caster);
		ItemStack artefact = caster.getHeldItemOffhand();
		if (!(artefact.getItem() instanceof ItemArtefact)) {
			ASUtils.sendMessage(caster, "You must hold an artefact in your offhand", true);
			return false;
		}
		String rarity = artefact.getItem().getForgeRarity(artefact).getName();
		if (Rarities.isKnownRarity(rarity) && Rarities.valueOf(rarity.toUpperCase()).ordinal() > getProperty(TIER_LIMIT).intValue()) {
			ASUtils.sendMessage(caster, "This artefact is too powerful to be absorbed", true);
			return false;
		}
		List<String> blacklist = new ArrayList<>(Arrays.asList(Settings.generalSettings.absorb_artefact_blacklist));

		if (blacklist.contains(artefact.getItem().getRegistryName().toString())) {
			ASUtils.sendMessage(caster, "This artefact cannot be absorbed", true);
			return false;
		}

		Random rand = caster.world.rand;
		double posX = caster.posX;
		double posY = caster.posY;
		double posZ = caster.posZ;

		if (world.isRemote) {
			if (world.getTotalWorldTime() % 3 == 0) {
				ParticleBuilder.create(WarlockElementalSpellEffects.getElementalParticle(element), rand, posX + rand.nextDouble() * 0.5d * (rand.nextBoolean() ? 1 : -1), posY,
								posZ + rand.nextDouble() * 0.5d * (rand.nextBoolean() ? 1 : -1), 0.03, true).vel(0, 0.3, 0).clr(WarlockElementalSpellEffects.PARTICLE_COLOURS.get(element)[0])
						.time(20 + rand.nextInt(50)).spawn(world);
				ParticleBuilder.create(WarlockElementalSpellEffects.getElementalParticle(element), rand, posX + rand.nextDouble() * 0.5d * (rand.nextBoolean() ? 1 : -1), posY,
								posZ + rand.nextDouble() * 0.5d * (rand.nextBoolean() ? 1 : -1), 0.03, true).vel(0, 0.3, 0).clr(WarlockElementalSpellEffects.PARTICLE_COLOURS.get(element)[0])
						.time(20 + rand.nextInt(50)).spawn(world);

				ParticleBuilder.create(WarlockElementalSpellEffects.getElementalParticle(element), rand, posX + rand.nextDouble() * 0.2d * (rand.nextBoolean() ? 1 : -1), posY,
								posZ + rand.nextDouble() * 0.2d * (rand.nextBoolean() ? 1 : -1), 0.03, true).spin(0.7, 0.05).vel(0, 0.3, 0).clr(WarlockElementalSpellEffects.PARTICLE_COLOURS.get(element)[0])
						.time(20 + rand.nextInt(50)).spawn(world);
			}

			// horizontal particle on the floor, always visible
			ParticleBuilder.create(ParticleBuilder.Type.FLASH)
					.pos(caster.posX, caster.posY + 0.101, caster.posZ)
					.face(EnumFacing.UP)
					.clr(WarlockElementalSpellEffects.PARTICLE_COLOURS.get(element)[1])
					.collide(false)
					.scale(2.3F)
					.time(10)
					.spawn(world);
		}
		if (ticksInUse == 60) {
			WizardData data = WizardData.get(caster);
			if (caster.getHeldItemOffhand().getItem() == ASItems.body_power_gem) {
				addToPowerGemCount(caster);
			} else {
				data.setVariable(ARTEFACT, caster.getHeldItemOffhand().getItem().getRegistryName().toString());
			}
			Wizardry.proxy.shakeScreen(caster, 10);
			caster.stopActiveHand();
			ASUtils.sendMessage(caster, "Absorbed " + caster.getHeldItemOffhand().getDisplayName(), true);
			spawnParticleEffect(world, 5, caster, modifiers);
			caster.getHeldItemOffhand().shrink(1);
		}
		return true;
	}

	@Override
	protected void playSound(World world, EntityLivingBase entity, int ticksInUse, int duration, SpellModifiers modifiers, String... sounds) {
		this.playSoundLoop(world, entity, ticksInUse);
	}

	@Override
	protected void playSound(World world, double x, double y, double z, int ticksInUse, int duration, SpellModifiers modifiers, String... sounds) {
		this.playSoundLoop(world, x, y, z, ticksInUse, duration);
	}

	public static int getPowerGemCount(EntityPlayer player) {
		WizardData data = WizardData.get(player);
		if (data.getVariable(POWER_GEM_COUNT) != null) {
			return data.getVariable(POWER_GEM_COUNT);
		}

		return 0;
	}

	public static int getPowerGemCount(WizardData data) {
		if (data.getVariable(POWER_GEM_COUNT) != null) {
			return data.getVariable(POWER_GEM_COUNT);
		}
		return 0;
	}

	public static void addToPowerGemCount(EntityPlayer player) {
		WizardData data = WizardData.get(player);
		int count = 0;
		if (data.getVariable(POWER_GEM_COUNT) != null) {
			count = data.getVariable(POWER_GEM_COUNT);
		}
		count++;
		data.setVariable(POWER_GEM_COUNT, count);
		data.sync();
	}

	@Override
	public boolean canBeCastBy(TileEntityDispenser dispenser) {
		return false;
	}

	public static Optional<Item> getArtefact(WizardData data) {
		return data.getVariable(ARTEFACT) == null ? Optional.empty() : Optional.of(ForgeRegistries.ITEMS.getValue(new ResourceLocation(data.getVariable(ARTEFACT).toString())));
	}

	public static boolean isArtefactActive(EntityPlayer player, Item artefact) {
		WizardData data = WizardData.get(player);
		if (data != null && data.getVariable(ARTEFACT) != null) {
			return (ForgeRegistries.ITEMS.getValue(new ResourceLocation(data.getVariable(ARTEFACT))) != null && ForgeRegistries.ITEMS.getValue(new ResourceLocation(data.getVariable(ARTEFACT))) == artefact);
		}
		return false;
	}

	protected void spawnParticleEffect(World world, double radius, EntityLivingBase caster, SpellModifiers modifiers) {
		if (!world.isRemote) {return;}

		double particleX, particleZ;
		Vec3d origin = caster.getPositionVector();
		for (int i = 0; i < 40 * modifiers.get(WizardryItems.blast_upgrade); i++) {

			particleX = origin.x - 1.0d + 2 * world.rand.nextDouble();
			particleZ = origin.z - 1.0d + 2 * world.rand.nextDouble();
			ParticleBuilder.create(ParticleBuilder.Type.DARK_MAGIC).pos(particleX, origin.y, particleZ)
					.vel(particleX - origin.x, 0, particleZ - origin.z).clr(0.1f, 0, 0).spawn(world);

			particleX = origin.x - 1.0d + 2 * world.rand.nextDouble();
			particleZ = origin.z - 1.0d + 2 * world.rand.nextDouble();
			ParticleBuilder.create(ParticleBuilder.Type.SPARKLE).pos(particleX, origin.y, particleZ)
					.vel(particleX - origin.x, 0, particleZ - origin.z).time(30).clr(0.1f, 0, 0.05f).spawn(world);

			particleX = origin.x - 1.0d + 2 * world.rand.nextDouble();
			particleZ = origin.z - 1.0d + 2 * world.rand.nextDouble();

			ParticleBuilder.create(ParticleBuilder.Type.SCORCH)
					.pos(caster.posX, caster.posY + 0.101, caster.posZ)
					.face(EnumFacing.UP)
					.clr(0.1f, 0, 0.05f)
					.collide(false)
					.scale(4.3F)
					.time(100)
					.spawn(world);

			IBlockState block = world.getBlockState(new BlockPos(origin.x, origin.y - 0.5, origin.z));

			if (block != null) {
				world.spawnParticle(EnumParticleTypes.BLOCK_DUST, particleX, origin.y,
						particleZ, particleX - origin.x, 0, particleZ - origin.z, Block.getStateId(block));
			}
		}

		ParticleBuilder.create(ParticleBuilder.Type.SPHERE).pos(origin.add(0, 0.1, 0)).scale((float) radius * 0.8f).clr(0.8f, 0, 0.05f).spawn(world);
	}

	@Override
	public ItemWizardArmour.ArmourClass getArmourClass() {
		return ItemWizardArmour.ArmourClass.WARLOCK;
	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == ASItems.forbidden_tome;
	}

	@Override
	public boolean canBeCastBy(EntityLiving npc, boolean override) {
		return canBeCastByClassNPC(npc);
	}
}