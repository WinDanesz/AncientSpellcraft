package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.item.ItemBattlemageSword;
import com.windanesz.ancientspellcraft.item.WizardClassWeaponHelper;
import com.windanesz.ancientspellcraft.packet.ASPacketHandler;
import com.windanesz.ancientspellcraft.packet.PacketExtendedSwordReach;
import com.windanesz.ancientspellcraft.registry.ASSpells;
import electroblob.wizardry.Wizardry;
import electroblob.wizardry.block.BlockReceptacle;
import electroblob.wizardry.constants.Element;
import electroblob.wizardry.util.GeometryUtils;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.RayTracer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Map;

@Mod.EventBusSubscriber
public class RunewordReach extends Runeword {

	private Effect effect = Effect.NONE;

	public RunewordReach(String name, EnumAction action, boolean isContinuous) {
		super(name, action, isContinuous);
		addProperties(CHARGES);
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void LeftClick(PlayerInteractEvent.LeftClickEmpty event) {
		EntityPlayerSP player = Minecraft.getMinecraft().player;

		if (player != null && player.getHeldItemMainhand().getItem() instanceof ItemBattlemageSword) {
			ItemStack stack = player.getHeldItemMainhand();

			for (Map.Entry<Runeword, Integer> entry : ItemBattlemageSword.getActiveRunewords(stack).entrySet()) {

				if (entry.getKey() instanceof RunewordReach) {

					Element element = WizardClassWeaponHelper.getElement(stack);

					Vec3d direction = player.getLookVec();
					Vec3d origin = new Vec3d(player.posX, player.posY + player.getEyeHeight() - 0.25f, player.posZ);
					if (!Wizardry.proxy.isFirstPerson(player)) {
						origin = origin.add(direction.scale(1.2));
					}

					Vec3d endpoint = origin.add(direction.scale(9.0));

					RayTraceResult result = RayTracer.rayTrace(player.world, origin, endpoint, 0.3f, false,
							true, false, EntityLivingBase.class, RayTracer.ignoreEntityFilter(player));

					if (result != null && result.entityHit != null && result.entityHit.getDistance(player) > 3.5f) {
						Effect effect = ((RunewordReach) entry.getKey()).getEffect();

						IMessage msg = new PacketExtendedSwordReach.Message(player, result.entityHit, effect);
						ASPacketHandler.net.sendToServer(msg);

						int[] colours = BlockReceptacle.PARTICLE_COLOURS.get(element);

						ParticleBuilder.create(ParticleBuilder.Type.BEAM).entity(player).clr(colours[1]).time(5)
								.pos(result.entityHit != null ? origin.subtract(player.getPositionVector()) : origin).target(result.entityHit).spawn(player.world);

					}

					break;
				}
			}

		}
	}

	public Effect getEffect() {
		return this.effect;
	}

	public RunewordReach setEffect(RunewordReach.Effect effect) {
		this.effect = effect;
		return this;
	}

	public enum Effect {

		NONE((Runeword) ASSpells.runeword_reach) {
			@Override
			public void apply(EntityPlayer player, Entity target) {
				;
			}
		},
		PULL((Runeword) ASSpells.runeword_pull) {
			@Override
			public void apply(EntityPlayer player, Entity target) {
				{
					Vec3d origin = new Vec3d(player.posX, player.posY, player.posZ);
					Vec3d target2 = GeometryUtils.getCentre(target);
					Vec3d vec = target2.subtract(origin).normalize();
					Vec3d velocity = vec.scale(1.0f);
					if (target instanceof EntityLivingBase) {
								double ax1 = (-velocity.x - target.motionX) * 4.4;
								double ay1 = (-velocity.y - target.motionY) * 4.4;
								double az1 = (-velocity.z - target.motionZ) * 4.4;
								target.addVelocity(ax1, ay1, az1);
								// Player motion is handled on that player's client so needs packets
								if (target instanceof EntityPlayerMP) {
									((EntityPlayerMP) target).connection.sendPacket(new SPacketEntityVelocity(target));
								}
						}}}

		},
		AOE((Runeword) ASSpells.runeword_reach) {
			@Override
			public void apply(EntityPlayer player, Entity target) {
			}
		},
		PUSH((Runeword) ASSpells.runeword_push) {
			@Override
			public void apply(EntityPlayer player, Entity target) {
				Vec3d origin = new Vec3d(player.posX, player.posY + player.getEyeHeight() -  0.25, player.posZ);
				Vec3d vec = target.getPositionEyes(1).subtract(origin).normalize();
				float velocity = 1.5f;
				target.motionX = vec.x * (velocity * 3.5f) ;
				target.motionY = vec.y * velocity + 0.3;
				target.motionZ = vec.z * (velocity * 3.5f);

				// Player motion is handled on that player's client so needs packets
				if (target instanceof EntityPlayerMP) {
					((EntityPlayerMP) target).connection.sendPacket(new SPacketEntityVelocity(target));
				}
			}
		};

		private Runeword runeword;

		Effect(Runeword runeword) {
			this.runeword = runeword;
		}

		public Runeword getRuneword() {
			switch (this) {
				case NONE:
				case AOE:
					return (Runeword) ASSpells.runeword_reach;
				case PULL:
					return (Runeword) ASSpells.runeword_pull;
				case PUSH:
					return (Runeword) ASSpells.runeword_push;
				default:
					return (Runeword) ASSpells.runeword_reach;
			}

		}

		public abstract void apply(EntityPlayer player, Entity target);
	}
}
