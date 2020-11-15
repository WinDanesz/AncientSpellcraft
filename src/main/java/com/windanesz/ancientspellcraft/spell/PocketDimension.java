package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftDimensions;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftItems;
import com.windanesz.ancientspellcraft.util.SpellTeleporter;
import electroblob.wizardry.data.IStoredVariable;
import electroblob.wizardry.data.Persistence;
import electroblob.wizardry.data.WizardData;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.ParticleBuilder.Type;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PocketDimension extends Spell {

	protected float particleCount = 10;

	public static final IStoredVariable<NBTTagCompound> POCKET_DIM_LOCATION = IStoredVariable.StoredVariable.ofNBT("pocket_dim_location", Persistence.ALWAYS).setSynced();
	public static final IStoredVariable<NBTTagCompound> POCKET_DIM_PREVIOUS_LOCATION = IStoredVariable.StoredVariable.ofNBT("pocket_dim_previous_location", Persistence.ALWAYS).setSynced();

	public PocketDimension() {
		super(AncientSpellcraft.MODID, "pocket_dimension", EnumAction.BLOCK, true);
		soundValues(1.0f, 1.2f, 0.2f);
		WizardData.registerStoredVariables(POCKET_DIM_LOCATION, POCKET_DIM_PREVIOUS_LOCATION);
	}

	@Override
	public boolean cast(World world, EntityPlayer caster, EnumHand hand, int ticksInUse, SpellModifiers modifiers) {
		// Only return on the server side or the client probably won't spawn particles
		if (ticksInUse == 0) {
			this.playSound(world, caster, ticksInUse, -1, modifiers);
		}

		if (ticksInUse < 60) {
			//			System.out.println("ticksinuse: " + ticksInUse);
			if (world.isRemote)
				this.spawnParticles(world, caster, modifiers);
			return true;
		}

		if (ticksInUse > 60) {
			return false;
		}

		//		System.out.println("should teleport now - " + ticksInUse);

		if (!teleportPlayer(caster) && !world.isRemote)
			return false;
		this.playSound(world, caster, ticksInUse, -1, modifiers);
		return true;
	}

	public static boolean teleportPlayer(EntityLivingBase caster) {
		if (caster instanceof EntityPlayer) {

			EntityPlayer player = (EntityPlayer) caster;
			WizardData data = WizardData.get(player);

			// TODO: Move player to pocket dim location if it exists

			if (data != null) {

				if (((EntityPlayer) caster).dimension == AncientSpellcraftDimensions.POCKET_DIM_ID) {
					if (data.getVariable(POCKET_DIM_PREVIOUS_LOCATION) != null) {

						BlockPos oldpos = NBTUtil.getPosFromTag(data.getVariable(POCKET_DIM_PREVIOUS_LOCATION));
						SpellTeleporter.teleportEntity(0, oldpos.getX(), oldpos.getY() + 1, oldpos.getZ(), true, player);
						return true;
					}
				}

				NBTTagCompound nbt = NBTUtil.createPosTag(player.getPosition());
				data.setVariable(POCKET_DIM_PREVIOUS_LOCATION, nbt);

				if (!player.world.isRemote)
					data.sync();

				SpellTeleporter.teleportEntity(AncientSpellcraftDimensions.POCKET_DIM_ID, 0, 3, 0, true, player);

				return true;
				//				}
			}
		}
		return false;
	}
	//
	//	protected void spawnParticles(World world, EntityLivingBase caster, SpellModifiers modifiers) {
	//
	//		super.spawnParticles(world, caster, modifiers);
	//
	//		for (int i = 0; i < particleCount * 2; i++) {
	//			double x = caster.posX + world.rand.nextDouble() * 2 - 1;
	//			double y = caster.getEntityBoundingBox().minY + caster.getEyeHeight() - 0.5 + world.rand.nextDouble();
	//			double z = caster.posZ + world.rand.nextDouble() * 2 - 1;
	//			ParticleBuilder.create(Type.SPARKLE).pos(x, y, z).vel(0, 0.14, 0).clr(0x0f001b)
	//					.time(20 + world.rand.nextInt(12)).spawn(world);
	//			ParticleBuilder.create(Type.DARK_MAGIC).pos(x, y, z).clr(0x0f001b).spawn(world);
	//		}
	//	}

	/**
	 * Spawns buff particles around the caster. Override to add a custom particle effect. Only called client-side.
	 */
	protected void spawnParticles(World world, EntityLivingBase caster, SpellModifiers modifiers) {

		for (int i = 0; i < 10; i++) {
			double dx = (world.rand.nextDouble() * (world.rand.nextBoolean() ? 1 : -1)) * 0.1;
			double dy = (world.rand.nextDouble() * (world.rand.nextBoolean() ? 1 : -1)) * 0.1;
			double dz = (world.rand.nextDouble() * (world.rand.nextBoolean() ? 1 : -1)) * 0.1;

			ParticleBuilder.create(Type.SPARKLE)
					.entity(caster)
					.clr(50, 168, 72)
					.pos(0, caster.height / 2, 0)
					.vel(dx, dy, dz)
					.spawn(world);

		}

		//		for (int i = 0; i < particleCount; i++) {
		//			double x = caster.posX + world.rand.nextDouble() * 2 - 1;
		//			double y = caster.getEntityBoundingBox().minY + caster.getEyeHeight() - 0.5 + world.rand.nextDouble();
		//			double z = caster.posZ + world.rand.nextDouble() * 2 - 1;
		//			ParticleBuilder.create(Type.SPARKLE).pos(x, y, z).vel(0, 0.1, 0).clr(1, 1, 1).spawn(world);
		//		}
		//
		//		ParticleBuilder.create(Type.BUFF).entity(caster).clr(1, 1, 1).spawn(world);
	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == AncientSpellcraftItems.ancient_spellcraft_spell_book || item == AncientSpellcraftItems.ancient_spellcraft_scroll;
	}
}
