package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.registry.ASBlocks;
import com.windanesz.ancientspellcraft.registry.ASItems;
import com.windanesz.ancientspellcraft.ritual.WarlockAttunement;
import com.windanesz.ancientspellcraft.util.ASUtils;
import com.windanesz.ancientspellcraft.util.WizardArmourUtils;
import electroblob.wizardry.Wizardry;
import electroblob.wizardry.client.DrawingUtils;
import electroblob.wizardry.constants.Element;
import electroblob.wizardry.constants.Tier;
import electroblob.wizardry.data.IStoredVariable;
import electroblob.wizardry.data.Persistence;
import electroblob.wizardry.data.WizardData;
import electroblob.wizardry.item.ItemSpellBook;
import electroblob.wizardry.item.ItemWizardArmour;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.registry.Spells;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import java.util.Optional;
import java.util.Random;

import static com.windanesz.ancientspellcraft.client.ClientProxy.KEY_ACTIVATE_CHARM_BAUBLE;

public class AbsorbSpell extends Spell implements IClassSpell {

	public static final IStoredVariable<String> SPELL = IStoredVariable.StoredVariable.ofString("AbsorbSpell", Persistence.ALWAYS);
	public static final String TIER_LIMIT = "tier_limit";

	public AbsorbSpell() {
		super(AncientSpellcraft.MODID, "absorb_spell", SpellActions.SUMMON, true);
		WizardData.registerStoredVariables(SPELL);
		addProperties(TIER_LIMIT);
	}

	@Override
	protected SoundEvent[] createSounds() {
		return createContinuousSpellSounds();
	}

	@Override
	public boolean cast(World world, EntityPlayer caster, EnumHand hand, int ticksInUse, SpellModifiers modifiers) {
		Optional<Element> elementOptional = WizardArmourUtils.getFullSetElementForClassOptional(caster, ItemWizardArmour.ArmourClass.WARLOCK);
		ItemStack book = caster.getHeldItemOffhand();
		if (!(book.getItem() instanceof ItemSpellBook) || Spell.byMetadata(book.getMetadata()) instanceof IClassSpell) {
			ASUtils.sendMessage(caster, "You must hold a regular spell book in your offhand", true);
			return false;
		}
		Spell spell = Spell.byMetadata(book.getMetadata());
		String rarity = book.getItem().getForgeRarity(book).getName();
		if (spell.getTier().ordinal() > getProperty(TIER_LIMIT).intValue()) {
			ASUtils.sendMessage(caster, "This spell is too powerful to be absorbed", true);
			return false;
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

				// horizontal particle on the floor, always visible
				ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(caster.posX, caster.posY + 0.101, caster.posZ).face(EnumFacing.UP).clr(DrawingUtils.mix(WarlockElementalSpellEffects.PARTICLE_COLOURS.get(element)[1], WarlockElementalSpellEffects.PARTICLE_COLOURS.get(element)[2], 0.5f)).collide(false).scale(2.3F).time(10).spawn(world);
			}
			if (ticksInUse == 60) {
				WizardData data = WizardData.get(caster);
				if (isSpellAllowed(spell)) {
					data.setVariable(SPELL, spell.getRegistryName().toString());
					Wizardry.proxy.shakeScreen(caster, 10);
					caster.stopActiveHand();
					ASUtils.sendMessage(caster, "Absorbed " + caster.getHeldItemOffhand().getDisplayName(), true);
					spawnParticleEffect(world, 5, caster, modifiers);
					caster.getHeldItemOffhand().shrink(1);
				} else {
				}
				return true;
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

	public static Optional<Spell> getSpell(WizardData data) {
		return data.getVariable(SPELL) == null ? Optional.empty() : Optional.of(Spell.get((data.getVariable(SPELL))));
	}

	public static Optional<Spell> getSpell(EntityPlayer player) {
		WizardData data = WizardData.get(player);
		return data.getVariable(SPELL) == null ? Optional.empty() : Optional.of(Spell.get((data.getVariable(SPELL))));
	}

	protected void spawnParticleEffect(World world, double radius, EntityLivingBase caster, SpellModifiers modifiers) {
		if (!world.isRemote) {return;}

		double particleX, particleZ;
		Vec3d origin = caster.getPositionVector();
		for (int i = 0; i < 40 * modifiers.get(WizardryItems.blast_upgrade); i++) {

			particleX = origin.x - 1.0d + 2 * world.rand.nextDouble();
			particleZ = origin.z - 1.0d + 2 * world.rand.nextDouble();
			ParticleBuilder.create(ParticleBuilder.Type.DARK_MAGIC).pos(particleX, origin.y, particleZ).vel(particleX - origin.x, 0, particleZ - origin.z).clr(0.1f, 0, 0).spawn(world);

			particleX = origin.x - 1.0d + 2 * world.rand.nextDouble();
			particleZ = origin.z - 1.0d + 2 * world.rand.nextDouble();
			ParticleBuilder.create(ParticleBuilder.Type.SPARKLE).pos(particleX, origin.y, particleZ).vel(particleX - origin.x, 0, particleZ - origin.z).time(30).clr(0.1f, 0, 0.05f).spawn(world);

			particleX = origin.x - 1.0d + 2 * world.rand.nextDouble();
			particleZ = origin.z - 1.0d + 2 * world.rand.nextDouble();

			IBlockState block = world.getBlockState(new BlockPos(origin.x, origin.y - 0.5, origin.z));

			if (block != null) {
				world.spawnParticle(EnumParticleTypes.BLOCK_DUST, particleX, origin.y, particleZ, particleX - origin.x, 0, particleZ - origin.z, Block.getStateId(block));
			}
		}

		ParticleBuilder.create(ParticleBuilder.Type.SPHERE).pos(origin.add(0, 0.1, 0)).scale((float) radius * 0.8f).clr(0.8f, 0, 0.05f).spawn(world);
	}

	private boolean isSpellAllowed(Spell spell) {
		if (Spells.satiety == spell || Spells.replenish_hunger == spell) {
			return false;
		}
		return true;
	}

	public static boolean canCast(EntityPlayer caster) {
		if (!caster.getCooldownTracker().hasCooldown(ItemSpellBook.getItemFromBlock(ASBlocks.DIMENSION_FOCUS))) {
			if (WarlockAttunement.isWarlockAttuned(caster) || WizardArmourUtils.isWearingFullSet(caster, null, ItemWizardArmour.ArmourClass.WARLOCK)) {
				return true;
			}
		}
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getDescription() {
		if (Minecraft.getMinecraft().player != null) {
			String descTransKey = super.getDescriptionTranslationKey();
			return Wizardry.proxy.translate(descTransKey, Keyboard.getKeyName(KEY_ACTIVATE_CHARM_BAUBLE.getKeyCode()), Tier.values()[getProperty(TIER_LIMIT).intValue()].getDisplayName());
		}
		return super.getDescription();
	}

	@Override
	public ItemWizardArmour.ArmourClass getArmourClass() {
		return ItemWizardArmour.ArmourClass.WARLOCK;
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