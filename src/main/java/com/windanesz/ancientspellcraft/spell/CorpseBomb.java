package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftItems;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.spell.SpellRay;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class CorpseBomb extends SpellRay {

	public CorpseBomb() {
		super(AncientSpellcraft.MODID, "corpse_bomb", SpellActions.POINT, false);
		soundValues(1, 1.1f, 0.2f);
		addProperties(EFFECT_RADIUS, DAMAGE);
	}

	@Override
	protected boolean onEntityHit(World world, Entity target, Vec3d hit, EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {

		if (target instanceof IEntityOwnable && target instanceof EntityLivingBase) {

			if (((IEntityOwnable) target).getOwner() != null && ((IEntityOwnable) target).getOwner() == caster) {
				double radius = getProperty(EFFECT_RADIUS).floatValue() * modifiers.get(WizardryItems.blast_upgrade);

				if (world.isRemote) {

					double particleX, particleZ;

					for (int i = 0; i < 40 * modifiers.get(WizardryItems.blast_upgrade); i++) {

						particleX = origin.x - 1.0d + world.rand.nextDouble();
						particleZ = origin.z - 1.0d + world.rand.nextDouble();
						ParticleBuilder.create(ParticleBuilder.Type.DARK_MAGIC).pos(particleX, origin.y, particleZ)
								.vel(particleX - origin.x, 0, particleZ - origin.z).clr(0.1f, 0, 0).spawn(world);

						particleX = origin.x - 1.0d + 2 * world.rand.nextDouble();
						particleZ = origin.z - 1.0d + 2 * world.rand.nextDouble();
						ParticleBuilder.create(ParticleBuilder.Type.SPARKLE).pos(particleX, origin.y, particleZ)
								.vel(particleX - origin.x, 0, particleZ - origin.z).time(30).clr(0.1f, 0, 0.05f).spawn(world);

						particleX = origin.x - 1.0d + 2 * world.rand.nextDouble();
						particleZ = origin.z - 1.0d + 2 * world.rand.nextDouble();

						IBlockState block = world.getBlockState(new BlockPos(origin.x, origin.y - 0.5, origin.z));

						if (block != null) {
							world.spawnParticle(EnumParticleTypes.BLOCK_DUST, particleX, origin.y,
									particleZ, particleX - origin.x, 0, particleZ - origin.z, Block.getStateId(block));
						}
					}

					ParticleBuilder.create(ParticleBuilder.Type.SPHERE).pos(target.posX,target.posY + target.getEyeHeight() ,target.posZ).scale(1f).clr(0.8f, 0, 0.05f).spawn(world);
					ParticleBuilder.create(ParticleBuilder.Type.SCORCH)
							.pos(target.posX + world.rand.nextFloat() * 0.5,target.posY + target.getEyeHeight() + world.rand.nextFloat() * 0.5 ,target.posZ + world.rand.nextFloat() * 0.5)
							.time(15).scale(0.5f).clr(0.8f, 0, 0.05f).spawn(world);

				}

				world.createExplosion(caster, target.posX, target.posY+ target.getEyeHeight(), target.posZ, (float) (radius * 0.7), false);
				world.createExplosion(caster, target.posX, target.posY+ target.getEyeHeight(), target.posZ, (float) (radius * 0.2), true);


				((EntityLivingBase) target).setHealth(0f);
				return true;
			}
		}

		return false;
	}

	@Override
	protected boolean onBlockHit(World world, BlockPos pos, EnumFacing side, Vec3d hit, EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {
		return false;
	}

	@Override
	protected boolean onMiss(World world, EntityLivingBase caster, Vec3d origin, Vec3d direction, int ticksInUse, SpellModifiers modifiers) {
		return false;
	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == AncientSpellcraftItems.ancient_spellcraft_spell_book || item == AncientSpellcraftItems.ancient_spellcraft_scroll;
	}
}
