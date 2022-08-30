package com.windanesz.ancientspellcraft.entity.construct;

import com.windanesz.ancientspellcraft.Settings;
import com.windanesz.ancientspellcraft.registry.ASItems;
import com.windanesz.ancientspellcraft.registry.ASPotions;
import com.windanesz.ancientspellcraft.registry.ASSounds;
import com.windanesz.ancientspellcraft.util.SpellTeleporter;
import electroblob.wizardry.entity.construct.EntityMagicConstruct;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.ParticleBuilder.Type;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class EntityTransportationPortal extends EntityMagicConstruct {

	private int targetDim;
	private BlockPos targetPos;

	private int entityTicker = 0;
	// the particle colors, based on target dim;
	private int r = 255;
	private int g = 255;
	private int b = 255;

	public EntityTransportationPortal(World world) {
		super(world);
		this.height = 1.0f;
		this.width = 2.0f;
	}

	public void setTargetDim(int dim) { targetDim = dim;}

	public int getTargetDim() { return targetDim; }

	public void setTargetPos(@Nullable BlockPos pos) { this.targetPos = pos; }

	public BlockPos getTargetPos() { return targetPos; }

	public void setParticleColors() {
		// default (89, 238, 155)
		switch (targetDim) {
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
	}

	private BlockPos getRandomBlockPos(int x, int z, double radius) {
		double ang = Math.random() * 2 * Math.PI,
				hyp = Math.sqrt(Math.random()) * radius,
				adj = Math.cos(ang) * hyp,
				opp = Math.sin(ang) * hyp;
		int xPos = (int) (x + adj);
		int zPos = (int) (z + opp);
		int yPos = world.getTopSolidOrLiquidBlock(new BlockPos(xPos, 0, zPos)).getY();
		return new BlockPos(xPos, yPos, zPos);
	}

	@Override
	public void onUpdate() {
				if (this.ticksExisted == 3) {
					setParticleColors();
				}

		if ((this.ticksExisted == 20 || this.ticksExisted % 160 == 0)) {
			this.playSound(ASSounds.ENTITY_TRANSPORTATION_PORTAL_AMBIENT, 0.4f, 1.0f);
		}

		List<EntityItem> items = EntityUtils.getEntitiesWithinRadius(width, posX, posY, posZ, world, EntityItem.class);

		for (EntityItem entityItem : items) {
			if (entityItem != null & entityItem.getItem().getItem() == ASItems.charm_wild_catalyst) {
				entityItem.setItem(ItemStack.EMPTY);
				BlockPos newPos = getRandomBlockPos(this.getPosition().getX(), this.getPosition().getZ(), Settings.generalSettings.wild_catalyst_max_distance);
				setTargetPos(newPos);
			}
		}

		List<EntityLivingBase> targets = EntityUtils.getEntitiesWithinRadius(width, posX, posY, posZ, world, EntityLivingBase.class);
		boolean hasEntityInside = !targets.isEmpty();

		if (this.world.isRemote) {
			double speed = (rand.nextBoolean() ? 1 : -1) * 0.1;
			double radius = rand.nextDouble() * 2.0;
			float angle = rand.nextFloat() * (float) Math.PI * 2;

			// horizontal particle on the floor, always visible
			ParticleBuilder.create(Type.FLASH)
					.pos(this.posX, this.posY + 0.1, this.posZ)
					.face(EnumFacing.UP)
					.clr(r, g, b)
					.collide(false)
					.scale(4.3F)
					.time(10)
					.spawn(world);
			if (!(r == 255 && g == 255 && b == 255)) {
				// spinning particles when an entity is present
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
			if (hasEntityInside) {
				if (entityTicker == 50) {
					this.playSound(ASSounds.TRANSPORTATION_PORTAL_TELEPORTS, 0.6f, 1.0f);
				}
				if (entityTicker == 90) {
					EntityLivingBase target = targets.get(0);

					if (targetPos == null || (targetPos.getX() == 0 && targetPos.getY() == 0 && targetPos.getZ() == 0)) {
						this.setDead();
						return;
					}

					if (target instanceof EntityPlayer) {
						if (!target.isPotionActive(ASPotions.dimensional_anchor)) {
							teleportPlayer((EntityPlayer) target, targetDim, targetPos);
						}

						// non players
					} else {
						if (Settings.generalSettings.transportation_portal_teleports_any_entites && !target.isPotionActive(ASPotions.dimensional_anchor) && target.dimension == getTargetDim()) {
							teleportEntityLiving(target, targetDim, targetPos);
							this.playSound(ASSounds.TRANSPORTATION_PORTAL_TELEPORTS, 0.6f, 1.0f);
						} else {EntityUtils.applyStandardKnockback(this, targets.get(0));}
					}
				} else {
					entityTicker++;
				}
			} else {
				entityTicker = 0;
			}
		}

		super.onUpdate();
	}

	public void teleportPlayer(EntityPlayer player, int targetDim, BlockPos targetPos) {
		if (player != null) {
			SpellTeleporter.teleportEntity(targetDim, targetPos.getX(), targetPos.getY(), targetPos.getZ(), true, player);
		}
	}

	public void teleportEntityLiving(EntityLivingBase entity, int targetDim, BlockPos targetPos) {
		if (entity != null && !entity.world.isRemote) {
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
		nbt.setInteger("r", r);
		nbt.setInteger("g", g);
		nbt.setInteger("b", b);
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		if (nbt.hasKey("target_dim")) {
			this.targetDim = nbt.getInteger("target_dim");
		}
		if (nbt.hasKey("target_pos")) {
			this.targetPos = NBTUtil.getPosFromTag(nbt.getCompoundTag("target_pos"));
		}
		if (nbt.hasKey("r")) {
			this.r = nbt.getInteger("r");
		}
		if (nbt.hasKey("g")) {
			this.g = nbt.getInteger("g");
		}
		if (nbt.hasKey("b")) {
			this.b = nbt.getInteger("b");
		}

	}

	@Override
	public boolean canRenderOnFire() { return false; }

}

