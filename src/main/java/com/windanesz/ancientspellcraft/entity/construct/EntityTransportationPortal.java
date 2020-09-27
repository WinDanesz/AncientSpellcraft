package com.windanesz.ancientspellcraft.entity.construct;

import com.google.common.base.Optional;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftSounds;
import com.windanesz.ancientspellcraft.util.SpellTeleporter;
import electroblob.wizardry.entity.construct.EntityMagicConstruct;
import electroblob.wizardry.packet.PacketTransportation;
import electroblob.wizardry.packet.WizardryPacketHandler;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.ParticleBuilder.Type;
import electroblob.wizardry.util.WizardryUtilities;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import javax.annotation.Nullable;
import java.util.List;

public class EntityTransportationPortal extends EntityMagicConstruct {

	private static final DataParameter<Integer> TARGET_DIM = EntityDataManager.createKey(EntityTransportationPortal.class, DataSerializers.VARINT);
	private static final DataParameter<Optional<BlockPos>> ATTACHED_BLOCK_POS = EntityDataManager.<Optional<BlockPos>>createKey(EntityTransportationPortal.class, DataSerializers.OPTIONAL_BLOCK_POS);
	//	private int TARGET_DIM;
	//	private BlockPos ATTACHED_BLOCK_POS;

	//	private Location destinationLocation;
	private int entityTicker = 0;
	// the particle colors, based on target dim;
	private int r = 255;
	private int g = 255;
	private int b = 255;
	private boolean hasEntityInside = false;

	public EntityTransportationPortal(World world) {
		super(world);
		this.height = 1.0f;
		//		this.width = 3.0f;
		this.width = 2.0f;
		//		setParticleColors();
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		this.dataManager.register(TARGET_DIM, Integer.valueOf(0));
		this.dataManager.register(ATTACHED_BLOCK_POS, Optional.absent());
	}

	public void setTargetDim(int dim) {
		this.dataManager.set(TARGET_DIM, Integer.valueOf(dim));
	}

	public int getTargetDim() {
		return ((Integer) this.dataManager.get(TARGET_DIM)).intValue();
	}

	@Nullable
	public BlockPos getTargetPos() {
		return (BlockPos) ((Optional) this.dataManager.get(ATTACHED_BLOCK_POS)).orNull();
	}

	public void setTargetPos(@Nullable BlockPos pos) {
		this.dataManager.set(ATTACHED_BLOCK_POS, Optional.fromNullable(pos));
	}

	private void setParticleColors() {
		// default (89, 238, 155)
//		System.out.println("targetdim:" + getTargetDim());
		switch (getTargetDim()) {
			case -1: // Nether
				r = 162;
				g = 54;
				b = 37;
				break;
			case 1: // End
				r = 9;
				g = 7;
				b = 13;
				break;

			// Now with mod support!
			case 7: // Twilight Forest
				r = 66;
				g = 24;
				b = 141;
				break;
			case 67: // Erebus 227, 206, 139
				r = 227;
				g = 206;
				b = 139;
				break;
			case 0: // Overworld
			default:
				r = 89;
				g = 238;
				b = 155;
		}
//		System.out.println("particles: r g b: " + r + " " + g + " " + b);
	}

	@Override
	public void onUpdate() {
		if (this.ticksExisted == 3) {
			setParticleColors();
//			System.out.println("getTargetDim():" + getTargetDim());
//			System.out.println("getTargetPos():" + getTargetPos());
		}

		if ((this.ticksExisted == 20 || this.ticksExisted % 160 == 0)) {
			this.playSound(AncientSpellcraftSounds.ENTITY_TRANSPORTATION_PORTAL_AMBIENT, 0.4f, 1.0f);
		}

		List<EntityLivingBase> targets = WizardryUtilities.getEntitiesWithinRadius(width, posX, posY, posZ, world);
		if (!targets.isEmpty()) {
			hasEntityInside = true;
		} else {hasEntityInside = false;}

		if (this.world.isRemote) {
			double speed = (rand.nextBoolean() ? 1 : -1) * 0.1;// + 0.01 * rand.nextDouble();
			double radius = rand.nextDouble() * 2.0;
			float angle = rand.nextFloat() * (float) Math.PI * 2;

			// horizontal particle, always visible
			ParticleBuilder.create(Type.FLASH)
					.pos(this.posX, this.posY + 0.1, this.posZ)
					.face(EnumFacing.UP)
					.clr(r, g, b)
					//					.clr(89, 238, 155)
					.collide(false)
					.scale(4.3F)
					.time(10)
					.spawn(world);
			if (!(r == 255 && g == 255 && b == 255)) {
				// spinning particles
				if (hasEntityInside) {
					ParticleBuilder.create(ParticleBuilder.Type.FLASH)
							.pos(this.posX + radius / 3 * MathHelper.cos(angle), this.posY, this.posZ + radius / 3 * MathHelper.sin(angle))
							.vel(0, 0.05, 0)
							.scale(0.7F)
							.time(48 + this.rand.nextInt(12))
							.spin(this.width / 2, speed)
							.clr(r, g, b)
							.spawn(world);
				} else {
					// center glow, only without entities
					ParticleBuilder.create(Type.FLASH)
							.pos(this.posX + radius / 3 * MathHelper.cos(angle), this.posY, this.posZ + radius / 3 * MathHelper.sin(angle))
							.vel(0, 0.05, 0)
							.time(48 + this.rand.nextInt(12))
							.clr(r, g, b)
							.spawn(world);
				}
			}

		} else {
			if (hasEntityInside && !targets.isEmpty()) {
				if (entityTicker == 50) {
						this.playSound(AncientSpellcraftSounds.TRANSPORTATION_PORTAL_TELEPORTS, 0.6f, 1.0f);
				}
				if (entityTicker == 90) {
//					System.out.println("getTargetDim():" + getTargetDim());
//					System.out.println("getTargetPos():" + getTargetPos());
					if (targets.get(0) instanceof EntityPlayer) {
						EntityPlayer targetPlayer = (EntityPlayer) targets.get(0);
						// custom teleporter implementation instead of Transportation's teleporter
						teleportEntity(targetPlayer, getTargetDim(), getTargetPos());

						// non players
					} else if (targets.get(0) instanceof EntityLivingBase) {
						if (targets.get(0).dimension == getTargetDim()) {
							teleportEntityLiving(targets.get(0), getTargetDim(), getTargetPos());
							this.playSound(AncientSpellcraftSounds.TRANSPORTATION_PORTAL_TELEPORTS, 0.6f, 1.0f);
						} else {WizardryUtilities.applyStandardKnockback(this, targets.get(0));}
					}
				} else {
					entityTicker++;
				}
			} else {
				entityTicker = 0;
			}
		}

		// can't call super because EntityMagicConstruct would despawn the entity even if an entity stands in the sigil
		super.onUpdate();
	}

	public void teleportEntity(EntityPlayer player, int targetDim, BlockPos targetPos) {
		if (player != null) {
			SpellTeleporter.teleportEntity(targetDim, targetPos.getX(), targetPos.getY(), targetPos.getZ(), true, player);
			IMessage msg = new PacketTransportation.Message(player.getEntityId());
			WizardryPacketHandler.net.sendToDimension(msg, player.world.provider.getDimension());
		}
	}

	public void teleportEntityLiving(EntityLivingBase entity, int targetDim, BlockPos targetPos) {
		if (entity != null) {
			SpellTeleporter.teleportEntity(entity, targetDim, targetPos.getX(), targetPos.getY(), targetPos.getZ());
		}
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		nbt.setInteger("target_dim", getTargetDim());
		if (this.getTargetPos() != null) {
			nbt.setTag("target_pos", NBTUtil.createPosTag(this.getTargetPos()));
		}
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		if (nbt.hasKey("target_dim")) {
			this.dataManager.set(TARGET_DIM, nbt.getInteger("target_dim"));
		}
		if (nbt.hasKey("target_pos")) {
			this.dataManager.set(ATTACHED_BLOCK_POS, Optional.of(NBTUtil.getPosFromTag(nbt)));
		}

	}

	@Override
	public boolean canRenderOnFire() {
		return false;
	}

}

