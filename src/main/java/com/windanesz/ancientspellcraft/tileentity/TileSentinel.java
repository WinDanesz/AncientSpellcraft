package com.windanesz.ancientspellcraft.tileentity;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.block.BlockSentinel;
import com.windanesz.ancientspellcraft.entity.living.EntitySpellCaster;
import com.windanesz.ancientspellcraft.registry.ASSounds;
import electroblob.wizardry.constants.Element;
import electroblob.wizardry.event.SpellCastEvent;
import electroblob.wizardry.packet.PacketCastSpell;
import electroblob.wizardry.packet.WizardryPacketHandler;
import electroblob.wizardry.registry.Spells;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.AllyDesignationSystem;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("Duplicates")
public class TileSentinel extends TileEntity implements ITickable {

	private final double maxAttackDistance = 15D;
	public float crystalRotation;
	public float crystalRotationPrev;
	public float tRot;
	private Spell spell = Spells.magic_missile;
	private int spellCastFrequency = 60;
	private int ticksUntilNextSpell = 60;
	private SpellModifiers modifiers = new SpellModifiers();

	private int lifetime = -1;

	private EntitySpellCaster spellCaster = null;

	private float spellCasterHealth = 0;
	private UUID ownerUUID;

	private boolean large = false;

	private Element crystalElement;

	public TileSentinel() {}

	public Spell getSpell() {
		return spell;
	}

	public float getSpellCasterHealth() {
		if (spellCaster != null) {
			return spellCaster.getHealth();
		} else {
			return spellCasterHealth;
		}
	}

	public void setSpellCasterHealth(float spellCasterHealth) {
		this.spellCasterHealth = spellCasterHealth;
	}

	public boolean isLarge() {
		return large;
	}

	public void setLarge(boolean large) {
		this.large = large;
	}

	@Nullable
	public UUID getOwnerId() {
		return ownerUUID;
	}

	@Nullable
	public EntityLivingBase getOwner() {

		Entity entity = EntityUtils.getEntityByUUID(world, getOwnerId());

		if (entity != null && !(entity instanceof EntityLivingBase)) { // Should never happen
			AncientSpellcraft.logger.warn("{} has a non-living owner!", this);
			entity = null;
		}

		return (EntityLivingBase) entity;
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);

		ticksUntilNextSpell = compound.getInteger("ticks_until_next_spell");
		spellCastFrequency = compound.getInteger("spell_cast_frequency");
		spellCasterHealth = compound.getFloat("spellcaster_health");
		if (compound.hasKey("owner_id")) {
			ownerUUID = UUID.fromString(compound.getString("owner_id"));
		}
		Spell storedSpell = Spell.registry.getValue(new ResourceLocation(compound.getString("spell")));
		spell = storedSpell == null ? Spells.none : storedSpell;
		if (compound.hasKey("lifetime")) {
			this.lifetime = compound.getInteger("lifetime");
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);

		compound.setInteger("ticks_until_next_spell", ticksUntilNextSpell);
		compound.setInteger("spell_cast_frequency", spellCastFrequency);
		compound.setFloat("spellcaster_health", spellCasterHealth);
		if (ownerUUID != null) {
			compound.setString("owner_id", ownerUUID.toString());
		}
		compound.setString("spell", spell.getRegistryName().toString());
		if (spellCaster != null) {
			compound.setInteger("lifetime", spellCaster.getLifetime());
		} else {
			compound.setInteger("lifetime", lifetime);
		}

		return compound;
	}

	/**
	 * Creates a tag containing the TileEntity information, used by vanilla to transmit from server to client
	 * Warning - although our getUpdatePacket() uses this method, vanilla also calls it directly, so don't remove it.
	 */
	@Override
	public final NBTTagCompound getUpdateTag() {
		return this.writeToNBT(new NBTTagCompound());
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		return new SPacketUpdateTileEntity(pos, getBlockMetadata(), this.getUpdateTag());
	}

	public EntitySpellCaster getSpellCasterEntity() {
		return spellCaster;
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		readFromNBT(pkt.getNbtCompound());
	}

	@SideOnly(Side.CLIENT)
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		AxisAlignedBB bb;
		Block type = getBlockType();
		if (type instanceof BlockSentinel) {
			bb = new AxisAlignedBB(pos, pos.add(4, 4, 4));
		} else {
			bb = this.getWorld().getBlockState(pos).getBoundingBox(world, pos);
		}
		return bb;
	}

	public void update() {
		doRotation();

		if (world.getTotalWorldTime() % 32 == 0) {
			world.playSound(null, this.pos.getX(), this.pos.getY(), this.pos.getZ(), ASSounds.SENTINEL_AMBIENT, SoundCategory.NEUTRAL, 1.1F, 1F);
		}

		if (this.ownerUUID == null) {
			EntityPlayer player = world.getClosestPlayer(pos.getX(), pos.getY(), pos.getZ(), 20, false);
			if (player != null) {
				this.ownerUUID = player.getUniqueID();
			}
		}

		if (spellCaster == null) {
			initSpellcaster();
		} else {
			this.spellCasterHealth = spellCaster.getHealth();
		}

		decrementSpellTimer();

		// saves some work by only looking for enemies when it's able to target them
		if (ticksUntilNextSpell == 0) {

			List<EntityLivingBase> entities = EntityUtils.getEntitiesWithinRadius(maxAttackDistance, this.pos.getX() + 0.5F, this.pos.getY() + 0.5F, this.pos.getZ() + 0.5F, world, EntityLivingBase.class);

			if (!entities.isEmpty()) {

				for (EntityLivingBase target : entities) {

					if (!(target instanceof EntityArmorStand) && isValidTarget(target) && target.isEntityAlive()) {

						double distanceSq = target.getDistanceSq(this.pos.getX(), this.pos.getY(), this.pos.getZ());

						if (Math.sqrt(distanceSq) <= maxAttackDistance) { // && isVisibleTarget(target)) {
							if (tryCastSpell(target)) {
								resetSpellTimer();

								// only attack the first enemy
								break;
							}
						}
					}

				}
			}

		}

		// terminating
		if (getSpellCasterHealth() == 0.0f) {
			world.setBlockToAir(pos);
		}

		if (world.isRemote && world.rand.nextBoolean() && world.rand.nextBoolean()) {

			double d = (pos.getY() + 0.5);
			float f = this.crystalRotation;
			d = d + MathHelper.sin(f * 2) * 0.12f;

			ParticleBuilder.create(ParticleBuilder.Type.FLASH)
					.pos(pos.getX() + 0.5, d, pos.getZ() + 0.5f).vel(0, 0.01, 0)
					.time(20 + world.rand.nextInt(10)).clr(0.5f + (world.rand.nextFloat() / 5), 0.5f + (world.rand.nextFloat() / 5),
					0.5f + (world.rand.nextFloat() / 2)).scale(0.5f).spawn(world);
			double r = 0.12;
			//
			//			double x = r * (world.rand.nextDouble() * 2 - 1);
			//			double y = r * (world.rand.nextDouble() * 2 - 1);
			//			double z = r * (world.rand.nextDouble() * 2 - 1);
			//			ParticleBuilder.create(ParticleBuilder.Type.DUST).pos(this.pos.getX() + 0.5, this.pos.getY()  + 0.5,this.pos.getZ()  + 0.5)
			//					.vel(x-0.03, 0.02, z-0.03).time(24 + world.rand.nextInt(8)).clr(0xe4c7cd, 0xfeffbe, 0x9d2cf3).fade(0xe4c7cd, 0xfeffbe, 0x9d2cf3).spawn(world);

//			for (int i = 0; i < 10; i++) {
//				double dx = (world.rand.nextDouble() * 2 - 1) * 3;
//				double dy = (world.rand.nextDouble() * 2 - 1) * 3;
//				double dz = (world.rand.nextDouble() * 2 - 1) * 3;
//				// These particles use the velocity args differently; they behave more like portal particles
//				world.spawnParticle(EnumParticleTypes.ENCHANTMENT_TABLE, pos.getX() + 0.5, pos.getY() + 0.1, pos.getZ() + 0.5, dx, dy, dz);
//			}

		}
	}

	// returns true if the entity provided in the argument can be seen. (Raytrace)
	private boolean isVisibleTarget(EntityLivingBase entity) {
		if (entity.isInvisible())
			return false;
		// TODO: fix me, returns false when it shouldn't
		return world.rayTraceBlocks(new Vec3d(this.pos.getX() + 0.5, this.pos.getY() + 0.5, this.pos.getZ() + 0.5), new Vec3d(entity.posX,
						entity.posY + (double) entity.getEyeHeight(), entity.posZ),
				false, true, false) == null;
	}

	private void decrementSpellTimer() {
		if (ticksUntilNextSpell > 0) {
			ticksUntilNextSpell--;
		}
	}

	public Element getCrystalElement() {
		return crystalElement;
	}

	public void setCrystalElement(Element crystalElement) {
		this.crystalElement = crystalElement;
	}

	private void resetSpellTimer() {
		ticksUntilNextSpell = (int) (spellCastFrequency * (large ? 0.6 : 1));
	}

	public boolean isValidTarget(Entity target) {
		return target != spellCaster && AllyDesignationSystem.isValidTarget(getOwner(), target);
		//				&& !(target instanceof EntitySpellCaster && ((EntitySpellCaster) target).getOwnerId() != null
		//				&& spellCaster.getOwnerId() != null && spellCaster.getOwnerId() != ((EntitySpellCaster) target).getOwnerId());
	}

	// animation stuff
	private void doRotation() {
		this.crystalRotationPrev = this.crystalRotation;

		this.tRot += 0.02F;

		while (this.crystalRotation >= (float) Math.PI) {
			this.crystalRotation -= ((float) Math.PI * 2F);
		}

		while (this.crystalRotation < -(float) Math.PI) {
			this.crystalRotation += ((float) Math.PI * 2F);
		}

		while (this.tRot >= (float) Math.PI) {
			this.tRot -= ((float) Math.PI * 2F);
		}

		while (this.tRot < -(float) Math.PI) {
			this.tRot += ((float) Math.PI * 2F);
		}

		float f2;

		for (f2 = this.tRot - this.crystalRotation; f2 >= (float) Math.PI; f2 -= ((float) Math.PI * 2F)) {
		}

		while (f2 < -(float) Math.PI) {
			f2 += ((float) Math.PI * 2F);
		}

		this.crystalRotation += f2 * 0.4F;
	}

	public boolean tryCastSpell(EntityLivingBase target) {
		if (spell == null || spellCaster == null)
			return false;

		if (spell.isContinuous)
			return false;

		if (!spell.canBeCastBy(spellCaster, true)) {
			//			spellCaster.setDead();
			return false;
		}

		// if true, the spell cast fails
		if (MinecraftForge.EVENT_BUS.post(new SpellCastEvent.Pre(SpellCastEvent.Source.NPC, spell, spellCaster, modifiers))) {
			//			spellCaster.setDead();
			return false;
		}

		if (spell.cast(world, spellCaster, EnumHand.MAIN_HAND, 0, target, modifiers)) {

			MinecraftForge.EVENT_BUS.post(new SpellCastEvent.Post(SpellCastEvent.Source.OTHER, spell, world, pos.getX(), pos.getY(), pos.getZ(), EnumFacing.UP, modifiers));

			if (spell.requiresPacket()) {
				// Sends a packet to all players in dimension to tell them to spawn particles.
				// Only sent if the spell succeeded, because if the spell failed, you wouldn't
				// need to spawn any particles!
				IMessage msg = new PacketCastSpell.Message(spellCaster.getEntityId(), EnumHand.MAIN_HAND, spell, modifiers);
				WizardryPacketHandler.net.sendToDimension(msg, world.provider.getDimension());
			}
		}

		if (spellCaster.isEntityAlive()) {
			//			spellCaster.setDead();
		}

		return true;
	}

	public void setLifeTime(int lifetime) {
		this.lifetime = lifetime;
		if (spellCaster == null) {
			initSpellcaster();
		}
		if (spellCaster != null) {
			spellCaster.setLifetime(lifetime);
		}
	}

	private void initSpellcaster() {
		EntitySpellCaster spellCaster = new EntitySpellCaster(world);
		spellCaster.setPosition(this.pos.getX() + 0.5f, this.pos.getY(), this.pos.getZ() + 0.5f);
		spellCaster.setContinuousSpell(spell);
		spellCaster.setModifiers(modifiers);
		spellCaster.setOwnerId(this.ownerUUID);
		spellCaster.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(getSpellCasterHealth());
		spellCaster.setHealth(getSpellCasterHealth());
		spellCaster.setLifetime(lifetime);
		if (!world.isRemote)
			world.spawnEntity(spellCaster);
		this.spellCaster = spellCaster;
	}
}
