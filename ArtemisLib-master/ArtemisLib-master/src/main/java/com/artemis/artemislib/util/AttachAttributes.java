package com.artemis.artemislib.util;

import com.artemis.artemislib.compatibilities.sizeCap.ISizeCap;
import com.artemis.artemislib.compatibilities.sizeCap.SizeCapPro;
import com.artemis.artemislib.util.attributes.ArtemisLibAttributes;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class AttachAttributes
{
	@SubscribeEvent
	public void attachAttributes(EntityEvent.EntityConstructing event)
	{
		if(event.getEntity() instanceof EntityLivingBase)
		{
			final EntityLivingBase entity = (EntityLivingBase) event.getEntity();
			final AbstractAttributeMap map = entity.getAttributeMap();

			map.registerAttribute(ArtemisLibAttributes.ENTITY_HEIGHT);
			map.registerAttribute(ArtemisLibAttributes.ENTITY_WIDTH);
		}
	}

	@SubscribeEvent
	public void onPlayerTick(TickEvent.PlayerTickEvent event)
	{
		final EntityPlayer player = event.player;
		if(player.hasCapability(SizeCapPro.sizeCapability, null))
		{
			final ISizeCap cap = player.getCapability(SizeCapPro.sizeCapability, null);

			final boolean hasHeightModifier = player.getAttributeMap().getAttributeInstance(ArtemisLibAttributes.ENTITY_HEIGHT).getModifiers().isEmpty();
			final boolean hasWidthModifier = player.getAttributeMap().getAttributeInstance(ArtemisLibAttributes.ENTITY_WIDTH).getModifiers().isEmpty();

			final double heightAttribute = player.getAttributeMap().getAttributeInstance(ArtemisLibAttributes.ENTITY_HEIGHT).getAttributeValue();
			final double widthAttribute = player.getAttributeMap().getAttributeInstance(ArtemisLibAttributes.ENTITY_WIDTH).getAttributeValue();
			float height = (float) (cap.getDefaultHeight() * heightAttribute);
			float width = (float) (cap.getDefaultWidth() * widthAttribute);

			/* Makes Sure to only Run the Code IF the Entity Has Modifiers */
			if(hasHeightModifier != true || hasWidthModifier != true)
			{
				/* If the Entity Does have a Modifier get it's size before changing it's size */
				if(cap.getTrans() != true)
				{
					cap.setDefaultHeight(1.8f);
					cap.setDefaultWidth(0.6f);
					cap.setTrans(true);
				}
				/* Handles Resizing while true */
				if(cap.getTrans() == true)
				{
					float eyeHeight = (float) (player.getDefaultEyeHeight() * heightAttribute);
					if (player.isSneaking())
					{
						height *= 0.91666666666f;
						eyeHeight *= 0.9382716f;
					}
					if (player.isElytraFlying())
					{
						height *= 0.33f;
					}
					if (player.isPlayerSleeping())
					{
						width = 0.2F;
						height = 0.2F;
					}
					if (player.isRiding())
					{
						//eyeHeight = (float) (player.getDefaultEyeHeight() * heightAttribute)*1.4f;
						//height = height*1.4f;
					}
					
					width = MathHelper.clamp(width, 0.15F, width);
					height = MathHelper.clamp(height, 0.25F, height);
					if(height >= 1.6F) player.eyeHeight = eyeHeight;
					else player.eyeHeight = (eyeHeight * 0.9876542F);
					player.height = height;
					player.width = width;

					final double d0 = width / 2.0D;
					final AxisAlignedBB aabb = player.getEntityBoundingBox();
					player.setEntityBoundingBox(new AxisAlignedBB(player.posX - d0, aabb.minY, player.posZ - d0,
							player.posX + d0, aabb.minY + player.height, player.posZ + d0));
				}
			}
			else /* If the Entity Does not have any Modifiers */
			{
				/* Returned the Entities Size Back to Normal */
				if(cap.getTrans() == true)
				{
					player.height = height;
					player.width = width;
					final double d0 = width / 2.0D;
					final AxisAlignedBB aabb = player.getEntityBoundingBox();
					player.setEntityBoundingBox(new AxisAlignedBB(player.posX - d0, aabb.minY, player.posZ - d0,
							player.posX + d0, aabb.minY + height, player.posZ + d0));
					player.eyeHeight = player.getDefaultEyeHeight();
					cap.setTrans(false);
				}
			}
		}
	}

	@SubscribeEvent
	public void onLivingUpdate(LivingUpdateEvent event)
	{

		final EntityLivingBase entity = event.getEntityLiving();
		if(!(entity instanceof EntityPlayer))
		{
			if(entity.hasCapability(SizeCapPro.sizeCapability, null))
			{
				final ISizeCap cap = entity.getCapability(SizeCapPro.sizeCapability, null);

				final boolean hasHeightModifier = entity.getAttributeMap().getAttributeInstance(ArtemisLibAttributes.ENTITY_HEIGHT).getModifiers().isEmpty();
				final boolean hasWidthModifier = entity.getAttributeMap().getAttributeInstance(ArtemisLibAttributes.ENTITY_WIDTH).getModifiers().isEmpty();
				final double heightAttribute = entity.getAttributeMap().getAttributeInstance(ArtemisLibAttributes.ENTITY_HEIGHT).getAttributeValue();
				final double widthAttribute = entity.getAttributeMap().getAttributeInstance(ArtemisLibAttributes.ENTITY_WIDTH).getAttributeValue();
				float height = (float) (cap.getDefaultHeight() * heightAttribute);
				float width = (float) (cap.getDefaultWidth() * widthAttribute);

				/* Makes Sure to only Run the Code IF the Entity Has Modifiers */
				if(hasHeightModifier != true || hasWidthModifier != true)
				{
					/* If the Entity Does have a Modifier get it's size before changing it's size */
					if(cap.getTrans() != true)
					{
						cap.setDefaultHeight(entity.height);
						cap.setDefaultWidth(entity.width);
						cap.setTrans(true);
					}

					/* Handles Resizing while true */
					if(cap.getTrans() == true)
					{
						width = MathHelper.clamp(width, 0.04F, width);
						height = MathHelper.clamp(height, 0.08F, height);
						entity.height = height;
						entity.width = width;

						final double d0 = width / 2.0D;
						final AxisAlignedBB aabb = entity.getEntityBoundingBox();
						entity.setEntityBoundingBox(new AxisAlignedBB(entity.posX - d0, aabb.minY, entity.posZ - d0,
								entity.posX + d0, aabb.minY + entity.height, entity.posZ + d0));
					}
				}
				else /* If the Entity Does not have any Modifiers */
				{
					/* Returned the Entities Size Back to Normal */
					if(cap.getTrans() == true)
					{
						entity.height = height;
						entity.width = width;
						final double d0 = width / 2.0D;
						final AxisAlignedBB aabb = entity.getEntityBoundingBox();
						entity.setEntityBoundingBox(new AxisAlignedBB(entity.posX - d0, aabb.minY, entity.posZ - d0,
								entity.posX + d0, aabb.minY + height, entity.posZ + d0));
						cap.setTrans(false);
					}
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onEntityRenderPre(RenderLivingEvent.Pre event)
	{
		final EntityLivingBase entity = event.getEntity();
		
		if(entity.hasCapability(SizeCapPro.sizeCapability, null))
		{
			final ISizeCap cap = entity.getCapability(SizeCapPro.sizeCapability, null);
			
			if(cap.getTrans() == true)
			{
				float scaleHeight = entity.height / cap.getDefaultHeight();
				float scaleWidth = entity.width / cap.getDefaultWidth();
				
				GlStateManager.pushMatrix();
				GlStateManager.scale(scaleWidth, scaleHeight, scaleWidth);
				GlStateManager.translate(event.getX() / scaleWidth - event.getX(),
						event.getY() / scaleHeight - event.getY(), event.getZ() / scaleWidth - event.getZ());
			}
		}

		if(entity instanceof EntityPlayer)
		{
			final EntityPlayer player = (EntityPlayer) entity;
			
			if(player.getRidingEntity() instanceof AbstractHorse)
			{
				//GlStateManager.translate(0F, (1.7F-scaleHeight)*scaleHeight, 0F);
				//GlStateManager.translate(0, scaleHeight * 2, 0);
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onLivingRenderPost(RenderLivingEvent.Post event)
	{
		final EntityLivingBase entity = event.getEntity();
		
		if(entity.hasCapability(SizeCapPro.sizeCapability, null))
		{
			final ISizeCap cap = entity.getCapability(SizeCapPro.sizeCapability, null);
			
			if(cap.getTrans() == true)
			{
				GlStateManager.popMatrix();
			}
		}
	}
}
