package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.registry.ASItems;
import electroblob.wizardry.block.BlockTransportationStone;
import electroblob.wizardry.data.WizardData;
import electroblob.wizardry.item.ItemArtefact;
import electroblob.wizardry.item.ItemWizardArmour;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.spell.SpellRay;
import electroblob.wizardry.spell.Transportation;
import electroblob.wizardry.util.BlockUtils;
import electroblob.wizardry.util.Location;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;

import java.util.List;

public class TeleportObject extends SpellRay implements IClassSpell {

	public TeleportObject() {
		super(AncientSpellcraft.MODID, "teleport_object", SpellActions.POINT, false);
		this.soundValues(1, 1.1f, 0.2f);
	}

	@Override
	protected boolean onEntityHit(World world, Entity target, Vec3d hit, EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {
		return false;

	}

	@Override
	protected boolean onBlockHit(World world, BlockPos blockPosToMove, EnumFacing side, Vec3d hit, EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {
		if (caster instanceof EntityPlayer && !world.isRemote) {

			WizardData data = WizardData.get((EntityPlayer) caster);

			List<Location> locations = data.getVariable(Transportation.LOCATIONS_KEY);

			if (locations == null || locations.isEmpty()) {
				((EntityPlayer) caster).sendStatusMessage(new TextComponentTranslation("spell.ebwizardry:transportation.undefined"), true);
				return false;
			} else {
				Location destination = locations.get(locations.size() - 1); // The most recent one, or the only one

				if(!BlockTransportationStone.testForCircle(world, destination.pos)){
					((EntityPlayer) caster).sendStatusMessage(new TextComponentTranslation("spell.ebwizardry:transportation.missing"), true);
					return false;
				}


				if (!world.isAreaLoaded(blockPosToMove, 1)) {
					ForgeChunkManager.Ticket tk = ForgeChunkManager.requestTicket(AncientSpellcraft.MODID, world, ForgeChunkManager.Type.NORMAL);
					ForgeChunkManager.forceChunk(tk, new ChunkPos(destination.pos));
				}

				BlockPos targetPos = new BlockPos(destination.pos);

				if (ItemArtefact.isArtefactActive((EntityPlayer) caster, ASItems.charm_hoarders_orb)) {
					BlockPos randomPos;
					boolean foundSpot = false;

					for (int i = 0; i < 20; i++) {
						randomPos = BlockUtils.findNearbyFloorSpace(world, destination.pos, 4, 1, false);

						// prefer not to override the centerpiece
						if (randomPos != null && !randomPos.equals(destination.pos) && !randomPos.equals(destination.pos.down()) &&
								!(world.getBlockState(randomPos.down()).getBlock() instanceof BlockTransportationStone)) {
							targetPos = randomPos;
							foundSpot = true;
							break;
						}
					}
					if (!foundSpot) {
						((EntityPlayer) caster).sendStatusMessage(new TextComponentTranslation("spell.ancientspellcraft:teleport_object.no_free_pos"), true);
						return false;
					}
				}

				if (BlockUtils.canBlockBeReplaced(world, targetPos)) {
					IBlockState state = world.getBlockState(blockPosToMove);
					TileEntity tileEntity = world.getTileEntity(blockPosToMove);
					if (!BlockUtils.isBlockUnbreakable(world, blockPosToMove) && BlockUtils.canBreakBlock(caster, world, blockPosToMove)) {
						world.setBlockToAir(targetPos);
						world.setBlockState(targetPos, state);
						if (tileEntity != null) {
							NBTTagCompound tileData = tileEntity.serializeNBT();
							world.setTileEntity(targetPos, TileEntity.create(world, tileData));
							//TileEntity tileCopy = world.getTileEntity(destination.pos);
							//tileCopy.deserializeNBT(tileData);
							world.removeTileEntity(blockPosToMove);
						}

						world.setBlockToAir(blockPosToMove);

					}
					// don't override blocks

				} else {
					((EntityPlayer) caster).sendStatusMessage(new TextComponentTranslation("spell.ancientspellcraft:teleport_object.obstructed"), true);
				}

			}

		}

		return true;
	}

	@Override
	protected boolean onMiss(World world, EntityLivingBase caster, Vec3d origin, Vec3d direction, int ticksInUse, SpellModifiers modifiers) {
		return true;
	}

	@Override
	protected void spawnParticle(World world, double x, double y, double z, double vx, double vy, double vz) {
		ParticleBuilder.create(ParticleBuilder.Type.DARK_MAGIC).pos(x, y, z).clr(0x571e65).spawn(world);
		ParticleBuilder.create(ParticleBuilder.Type.DARK_MAGIC).pos(x, y, z).clr(0x251609).spawn(world);
		ParticleBuilder.create(ParticleBuilder.Type.SPARKLE).pos(x, y, z).time(12 + world.rand.nextInt(8)).clr(0x0b4b40).spawn(world);
	}

	@Override
	public ItemWizardArmour.ArmourClass getArmourClass() { return ItemWizardArmour.ArmourClass.SAGE; }

	@Override
	public boolean applicableForItem(Item item) {
		return item == ASItems.mystic_spell_book;
	}
}
