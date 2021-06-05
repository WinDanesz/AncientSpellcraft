package com.windanesz.ancientspellcraft.client;

import com.google.common.collect.Lists;
import com.windanesz.ancientspellcraft.Settings;
import com.windanesz.ancientspellcraft.packet.ASPacketHandler;
import com.windanesz.ancientspellcraft.packet.PacketSelectRadialItemSpell;
import electroblob.wizardry.client.DrawingUtils;
import electroblob.wizardry.item.ISpellCastingItem;
import electroblob.wizardry.registry.Spells;
import electroblob.wizardry.spell.Spell;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.util.Arrays;
import java.util.List;

@Mod.EventBusSubscriber(Side.CLIENT)
public class GuiRadialMenu extends GuiScreen {

	private boolean closing;
	private boolean doneClosing;
	private double startAnimation;

	private int selectedItem = -1;

	private final ISpellCastingItem spellCastingItem;
	private int spellCount;
	private List<Spell> spells;
	private int currentIndex = 0;

	GuiRadialMenu(ItemStack stack) {
		if (!(stack.getItem() instanceof ISpellCastingItem)) {
			throw new IllegalArgumentException("Attempted to open Radial spell menu with an item not implementing ISpellCastingItem!");
		}

		spellCastingItem = ((ISpellCastingItem) stack.getItem());
		spellCount = spellCastingItem.getSpells(stack).length;
		spells = Arrays.asList((spellCastingItem.getSpells(stack)));
		startAnimation = Minecraft.getMinecraft().world.getTotalWorldTime() + (double) Minecraft.getMinecraft().getRenderPartialTicks();
	}

	@SubscribeEvent
	public static void overlayEvent(RenderGameOverlayEvent.Pre event) {
		if (event.getType() != RenderGameOverlayEvent.ElementType.CROSSHAIRS)
			return;

		if (Minecraft.getMinecraft().currentScreen instanceof GuiRadialMenu) {
			event.setCanceled(true);
		}
	}

	@Override
	public void updateScreen() {
		super.updateScreen();

		if (closing) {
			if (doneClosing) {
				Minecraft.getMinecraft().displayGuiScreen(null);

				ClientProxy.wipeOpen();
			}

			return;
		}

		if (!GameSettings.isKeyDown(ClientProxy.keyOpenToolMenu)) {
			if (Settings.clientSettings.release_to_swap) {
				processClick();
			} else {
				animateClose();
			}
		}
	}

	@Override
	protected void mouseReleased(int mouseX, int mouseY, int state) {
		super.mouseReleased(mouseX, mouseY, state);
		processClick();
	}

	private void processClick() {
		if (closing)
			return;

		IMessage msg = new PacketSelectRadialItemSpell.Message(currentIndex);
		ASPacketHandler.net.sendToServer(msg);

		List<Integer> items = Lists.newArrayList();
		for (int i = 0; i < 10; i++) {
			items.add(i);
		}

		int numItems = items.size();
		numItems++;
		if (numItems <= 0)
			return;

		animateClose();
	}

	private void animateClose() {
		closing = true;
		doneClosing = false;
		startAnimation = Minecraft.getMinecraft().world.getTotalWorldTime() + (double) Minecraft.getMinecraft().getRenderPartialTicks();
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);

		boolean hasAddButton = false;
		int numItems = spellCount;
		if (numItems <= 0) {
			if (closing)
				doneClosing = true;
			return;
		}

		final float OPEN_ANIMATION_LENGTH = 2.5f;

		float openAnimation = closing
				? (float) (1 - ((Minecraft.getMinecraft().world.getTotalWorldTime() + partialTicks - startAnimation) / OPEN_ANIMATION_LENGTH))
				: (float) ((Minecraft.getMinecraft().world.getTotalWorldTime() + partialTicks - startAnimation) / OPEN_ANIMATION_LENGTH);

		if (closing && openAnimation <= 0)
			doneClosing = true;

		float animProgress = MathHelper.clamp(openAnimation, 0, 1);
		float radiusIn = Math.max(0.1f, 30 * animProgress);
		float radiusOut = radiusIn * 2;
		float itemRadius = (radiusIn + radiusOut) * 0.5f;
		float animTop = (1 - animProgress) * height / 2.0f;

		int x = width / 2;
		int y = height / 2;

		double a = Math.toDegrees(Math.atan2(mouseY - y, mouseX - x));
		double d = Math.sqrt(Math.pow(mouseX - x, 2) + Math.pow(mouseY - y, 2));
		float s0 = (((0 - 0.5f) / (float) numItems) + 0.25f) * 360;
		if (a < s0)
			a += 360;

		GlStateManager.pushMatrix();
		GlStateManager.disableAlpha();
		GlStateManager.enableBlend();
		GlStateManager.disableTexture2D();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

		GlStateManager.translate(0, animTop, 0);

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();
		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
		boolean hasMouseOver = false;
		ItemStack itemMouseOver = ItemStack.EMPTY;
		Spell spellOver = Spells.none;

		if (!closing) {
			selectedItem = -1;
			for (int i = 0; i < numItems; i++) {
				float s = (((i - 0.5f) / (float) numItems) + 0.25f) * 360;
				float e = (((i + 0.5f) / (float) numItems) + 0.25f) * 360;
				if (a >= s && a < e && d >= radiusIn && d < radiusOut) {
					selectedItem = i;
					break;
				}
			}
		}

		for (int i = 0; i < numItems; i++) {
			float s = (((i - 0.5f) / (float) numItems) + 0.25f) * 360;
			float e = (((i + 0.5f) / (float) numItems) + 0.25f) * 360;
			if (selectedItem == i) {
				drawPieArc(buffer, x, y, zLevel, radiusIn, radiusOut, s, e, 255, 255, 255, 64);

				if (i > 0 || !hasAddButton) {
					hasMouseOver = true;

					ItemStack inSlot = ItemStack.EMPTY;

					itemMouseOver = inSlot;
					spellOver = spells.get(i);
					currentIndex = i;
				}
			} else {
				drawPieArc(buffer, x, y, zLevel, radiusIn, radiusOut, s, e, 0, 0, 0, 64);
			}
		}

		double scaledX = Mouse.getX() - (mc.displayWidth / 2.0f);
		double scaledY = Mouse.getY() - (mc.displayHeight / 2.0f);

		double distance = Math.sqrt(scaledX * scaledX + scaledY * scaledY);
		double radius = 60.0 * (mc.displayWidth / width);

		if (distance > radius) {
			double fixedX = scaledX * radius / distance;
			double fixedY = scaledY * radius / distance;

			Mouse.setCursorPosition((int) (mc.displayWidth / 2 + fixedX), (int) (mc.displayHeight / 2 + fixedY));
		}

		tessellator.draw();
		GlStateManager.enableTexture2D();

		RenderHelper.enableGUIStandardItemLighting();
		for (int index = 0; index < spellCount; index++) {
			float angle1 = ((index / (float) spellCount) + 0.25f) * 2 * (float) Math.PI;
			float posX = x - 8 + itemRadius * (float) Math.cos(angle1);
			float posY = y - 8 + itemRadius * (float) Math.sin(angle1);

			GlStateManager.pushMatrix();
			GlStateManager.scale(0.5, 0.5, 0.5);
			Spell spell = spells.get(index);
			Minecraft.getMinecraft().renderEngine.bindTexture(spell.getIcon());
			DrawingUtils.drawTexturedRect(((int) posX) * 2, ((int) posY) * 2, 0, 0, 32, 32, 32, 32);
			GlStateManager.popMatrix();
		}

		if (hasMouseOver) {
			if (spellOver != null && spellOver != Spells.none) {
				GlStateManager.pushMatrix();
				drawCenteredString(fontRenderer, spellOver.getDisplayNameWithFormatting(), width / 2, (height - fontRenderer.FONT_HEIGHT) / 2, 0xFFFFFFFF);
				// reset colors otherwise everything will be colored
				GlStateManager.color(1.0f, 1.0f, 1.0f);
				GlStateManager.popMatrix();
			}
		}
		//		RenderHelper.disableStandardItemLighting();

		GlStateManager.popMatrix();

		if (itemMouseOver.getCount() > 0)
			renderToolTip(itemMouseOver, mouseX, mouseY);
	}

	private static final float PRECISION = 5;

	private void drawPieArc(BufferBuilder buffer, float x, float y, float z, float radiusIn, float radiusOut, float startAngle, float endAngle, int r, int g, int b, int a) {
		float angle = endAngle - startAngle;
		int sections = Math.max(1, MathHelper.ceil(angle / PRECISION));

		startAngle = (float) Math.toRadians(startAngle);
		endAngle = (float) Math.toRadians(endAngle);
		angle = endAngle - startAngle;

		for (int i = 0; i < sections; i++) {
			float angle1 = startAngle + (i / (float) sections) * angle;
			float angle2 = startAngle + ((i + 1) / (float) sections) * angle;

			float pos1InX = x + radiusIn * (float) Math.cos(angle1);
			float pos1InY = y + radiusIn * (float) Math.sin(angle1);
			float pos1OutX = x + radiusOut * (float) Math.cos(angle1);
			float pos1OutY = y + radiusOut * (float) Math.sin(angle1);
			float pos2OutX = x + radiusOut * (float) Math.cos(angle2);
			float pos2OutY = y + radiusOut * (float) Math.sin(angle2);
			float pos2InX = x + radiusIn * (float) Math.cos(angle2);
			float pos2InY = y + radiusIn * (float) Math.sin(angle2);

			buffer.pos(pos1OutX, pos1OutY, z).color(r, g, b, a).endVertex();
			buffer.pos(pos1InX, pos1InY, z).color(r, g, b, a).endVertex();
			buffer.pos(pos2InX, pos2InY, z).color(r, g, b, a).endVertex();
			buffer.pos(pos2OutX, pos2OutY, z).color(r, g, b, a).endVertex();
		}
	}

	@Override
	public boolean doesGuiPauseGame() {
		return Settings.clientSettings.gui_pause_game;
	}
}

/*
The code above is a modified version of David Quintana <gigaherz@gmail.com>'s solution.

Copyright (c) 2015, David Quintana <gigaherz@gmail.com>
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of the author nor the
      names of the contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
* */


/*
 package com.windanesz.ancientspellcraft.client;

 import com.google.common.collect.Lists;
 import com.windanesz.ancientspellcraft.AncientSpellcraft;
 import com.windanesz.ancientspellcraft.Settings;
 import com.windanesz.ancientspellcraft.packet.ASPacketHandler;
 import com.windanesz.ancientspellcraft.packet.PacketControlInput;
 import com.windanesz.ancientspellcraft.packet.PacketSelectRadialItemSpell;
 import com.windanesz.ancientspellcraft.registry.AncientSpellcraftSpells;
 import electroblob.wizardry.client.DrawingUtils;
 import electroblob.wizardry.item.ISpellCastingItem;
 import electroblob.wizardry.registry.Spells;
 import electroblob.wizardry.spell.Spell;
 import net.minecraft.client.Minecraft;
 import net.minecraft.client.gui.GuiScreen;
 import net.minecraft.client.renderer.BufferBuilder;
 import net.minecraft.client.renderer.GlStateManager;
 import net.minecraft.client.renderer.RenderHelper;
 import net.minecraft.client.renderer.Tessellator;
 import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
 import net.minecraft.client.resources.I18n;
 import net.minecraft.client.settings.GameSettings;
 import net.minecraft.item.ItemStack;
 import net.minecraft.util.math.MathHelper;
 import net.minecraftforge.client.event.RenderGameOverlayEvent;
 import net.minecraftforge.fml.common.Mod;
 import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
 import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
 import net.minecraftforge.fml.relauncher.Side;
 import org.lwjgl.input.Mouse;
 import org.lwjgl.opengl.GL11;

 import java.util.Arrays;
 import java.util.List;

 import static org.lwjgl.opengl.Pbuffer.DEPTH_BUFFER;

 @Mod.EventBusSubscriber(Side.CLIENT)
 public class GuiRadialMenu extends GuiScreen {
 //    private IItemHandler inventory;
 private List<ItemStack> cachedStacks = null;

 private boolean closing;
 private boolean doneClosing;
 private double startAnimation;

 private int selectedItem = -1;
 private boolean keyCycleBeforeL = false;
 private boolean keyCycleBeforeR = false;

 private final ItemStack stack;
 private final ISpellCastingItem spellCastingItem;
 private int spellCount;
 private List<Spell> spells;
 private Spell currentSpell = Spells.none;
 private int currentIndex = 0;

 GuiRadialMenu(ItemStack stack) {
 this.stack = stack;
 if (!(stack.getItem() instanceof ISpellCastingItem)) {
 throw new IllegalArgumentException("Attempted to open Radial spell menu with an item not implementing ISpellCastingItem!");
 }
 spellCastingItem = ((ISpellCastingItem) stack.getItem());
 spellCount = spellCastingItem.getSpells(stack).length;
 spells = Arrays.asList((spellCastingItem.getSpells(stack)));
 startAnimation = Minecraft.getMinecraft().world.getTotalWorldTime() + (double) Minecraft.getMinecraft().getRenderPartialTicks();
 }

 /*
 TODO
 GuiRadialMenu(EntityPlayer player, WizardData data) {

 }


@SubscribeEvent
public static void overlayEvent(RenderGameOverlayEvent.Pre event) {
	if (event.getType() != RenderGameOverlayEvent.ElementType.CROSSHAIRS)
		return;

	if (Minecraft.getMinecraft().currentScreen instanceof GuiRadialMenu) {
		event.setCanceled(true);
	}
}

	@Override
	public void updateScreen() {
		super.updateScreen();

		if (closing) {
			if (doneClosing) {
				Minecraft.getMinecraft().displayGuiScreen(null);

				ClientProxy.wipeOpen();
			}

			return;
		}

		if (!GameSettings.isKeyDown(ClientProxy.keyOpenToolMenu)) {
			//            if (Config.releaseToSwap) TODO: / com.windanesz.ancientspellcraft.Settings.clientSettings#releaseToSwap
			if (Settings.clientSettings.releaseToSwap) {
				processClick(false);
			} else {
				animateClose();
			}
		}
	}

	@Override
	protected void mouseReleased(int mouseX, int mouseY, int state) {
		super.mouseReleased(mouseX, mouseY, state);
		processClick(true);
	}

	private void processClick(boolean triggeredByMouse) {
		if (closing)
			return;

		IMessage msg = new PacketSelectRadialItemSpell.Message(currentIndex);
		ASPacketHandler.net.sendToServer(msg);

		List<Integer> items = Lists.newArrayList();
		for (int i = 0; i < 10; i++) {
			items.add(i);
		}

		boolean hasAddButton = false;
		int numItems = items.size();
		//        if (!Config.displayEmptySlots && (numItems < inventory.getSlots() && inHand.getCount() > 0))
		//        {
		hasAddButton = true;
		numItems++;
		//        }
		if (numItems <= 0)
			return;

		if (selectedItem >= 0) {
			int swapWith;
			if (hasAddButton)
				swapWith = selectedItem == 0 ? -1 : items.get(selectedItem - 1);
			else
				swapWith = items.get(selectedItem);

			//            if (inHand.getCount() <= 0 && inventory.getStackInSlot(swapWith).getCount() <= 0)
			//            {
			//                if (triggeredByMouse)
			//                {
			//                    // ignore click
			//                    return;
			//                }
			//            }
			//            else
			//            {
			//                SwapItems.swapItem(swapWith, mc.player);
			//                ToolBelt.channel.sendToServer(new SwapItems(swapWith));
			//            }
		}

		animateClose();
	}

	private void animateClose() {
		closing = true;
		doneClosing = false;
		startAnimation = Minecraft.getMinecraft().world.getTotalWorldTime() + (double) Minecraft.getMinecraft().getRenderPartialTicks();
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);

		//        if (inventory == null)
		//            return;

		//		List<ItemStack> items = cachedStacks;
		//		if (items == null) {
		//			items = Lists.newArrayList();
		//			for (int i = 0; i < spellCount; i++) {
		//				ItemStack inSlot = ItemStack.EMPTY;
		//				//                if (inSlot.getCount() > 0 || Config.displayEmptySlots)
		//				if (inSlot.getCount() > 0 || true)
		//					items.add(inSlot);
		//			}
		//			cachedStacks = items;
		//		}
		ItemStack inHand = mc.player.getHeldItemMainhand();
		//        if (!Config.isItemStackAllowed(inHand))
		//            return;

		boolean hasAddButton = false;
		int numItems = spellCount;
		//        if (!Config.displayEmptySlots && (numItems < 8 && inHand.getCount() > 0))
		//		if (!true && (numItems < spellCount)) {
		//			hasAddButton = true;
		//			numItems++;
		//		}
		if (numItems <= 0) {
			//            drawCenteredString(fontRenderer, I18n.format("gui.ancientspellcraft:empty_slot"), width / 2, (height - fontRenderer.FONT_HEIGHT) / 2, 0xFFFFFFFF);
			if (closing)
				doneClosing = true;
			return;
		}

		if (hasAddButton) {
			drawCenteredString(fontRenderer, I18n.format("text.toolbelt.insert"), width / 2, height / 2 + 45 - fontRenderer.FONT_HEIGHT / 2, 0xFFFFFFFF);
		}

		final float OPEN_ANIMATION_LENGTH = 2.5f;

		float openAnimation = closing
				? (float) (1 - ((Minecraft.getMinecraft().world.getTotalWorldTime() + partialTicks - startAnimation) / OPEN_ANIMATION_LENGTH))
				: (float) ((Minecraft.getMinecraft().world.getTotalWorldTime() + partialTicks - startAnimation) / OPEN_ANIMATION_LENGTH);

		if (closing && openAnimation <= 0)
			doneClosing = true;

		float animProgress = MathHelper.clamp(openAnimation, 0, 1);
		float radiusIn = Math.max(0.1f, 30 * animProgress);
		float radiusOut = radiusIn * 2;
		float itemRadius = (radiusIn + radiusOut) * 0.5f;
		float animTop = (1 - animProgress) * height / 2.0f;

		int x = width / 2;
		int y = height / 2;
		if (!closing) {
			//            if (GameSettings.isKeyDown(ClientProxy.keyCycleToolMenuL))
			//            {
			//                if (!keyCycleBeforeL)
			//                {
			//                    selectedItem--;
			//                    if (selectedItem < 0)
			//                        selectedItem = numItems - 1;
			//                    setMousePosition(
			//                            x + itemRadius * Math.cos(-0.5 * Math.PI - selectedItem * 2 * Math.PI / numItems),
			//                            y + itemRadius * Math.sin(-0.5 * Math.PI + selectedItem * 2 * Math.PI / numItems)
			//                    );
			//                }
			//                keyCycleBeforeL = true;
			//            }
			//            else
			//            {
			keyCycleBeforeL = false;
			//            }

			//            if (GameSettings.isKeyDown(ClientProxy.keyCycleToolMenuR))
			//            {
			//                if (!keyCycleBeforeR)
			//                {
			//                    if (selectedItem < 0)
			//                        selectedItem = 0;
			//                    else
			//                    {
			//                        selectedItem++;
			//                        if (selectedItem >= numItems)
			//                            selectedItem = 0;
			//                    }
			//                    setMousePosition(
			//                            x + itemRadius * Math.cos(-0.5 * Math.PI - selectedItem * 2 * Math.PI / numItems),
			//                            y + itemRadius * Math.sin(-0.5 * Math.PI + selectedItem * 2 * Math.PI / numItems)
			//                    );
			//                }
			//                keyCycleBeforeR = true;
			//            }
			//            else
			//            {
			keyCycleBeforeR = false;
			//            }
		}

		double a = Math.toDegrees(Math.atan2(mouseY - y, mouseX - x));
		double d = Math.sqrt(Math.pow(mouseX - x, 2) + Math.pow(mouseY - y, 2));
		float s0 = (((0 - 0.5f) / (float) numItems) + 0.25f) * 360;
		if (a < s0)
			a += 360;

		GlStateManager.pushMatrix();
		GlStateManager.disableAlpha();
		GlStateManager.enableBlend();
		GlStateManager.disableTexture2D();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

		GlStateManager.translate(0, animTop, 0);

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();
		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
		boolean hasMouseOver = false;
		ItemStack itemMouseOver = ItemStack.EMPTY;
		Spell spellOver = Spells.none;

		if (!closing) {
			selectedItem = -1;
			for (int i = 0; i < numItems; i++) {
				float s = (((i - 0.5f) / (float) numItems) + 0.25f) * 360;
				float e = (((i + 0.5f) / (float) numItems) + 0.25f) * 360;
				if (a >= s && a < e && d >= radiusIn && d < radiusOut)

					//                  TODO:   com.windanesz.ancientspellcraft.Settings.clientSettings#allowClickOutsideBounds
					//                    if (a >= s && a < e && d >= radiusIn && (d < radiusOut || Config.clipMouseToCircle || Config.allowClickOutsideBounds))
					if (a >= s && a < e && d >= radiusIn && (d < radiusOut)) {
						selectedItem = i;
						break;
					}
			}
		}

		for (int i = 0; i < numItems; i++) {
			float s = (((i - 0.5f) / (float) numItems) + 0.25f) * 360;
			float e = (((i + 0.5f) / (float) numItems) + 0.25f) * 360;
			if (selectedItem == i) {
				drawPieArc(buffer, x, y, zLevel, radiusIn, radiusOut, s, e, 255, 255, 255, 64);

				if (i > 0 || !hasAddButton) {
					hasMouseOver = true;

					ItemStack inSlot = ItemStack.EMPTY;

					itemMouseOver = inSlot;
					spellOver = spells.get(i);
					currentSpell = spellOver;
					currentIndex = i;
				}
			} else {
				drawPieArc(buffer, x, y, zLevel, radiusIn, radiusOut, s, e, 0, 0, 0, 64);
			}
		}

		//        if (Config.clipMouseToCircle) TODO: if : com.windanesz.ancientspellcraft.Settings.clientSettings#clipMouseToCircle
		//        {
		double scaledX = Mouse.getX() - (mc.displayWidth / 2.0f);
		double scaledY = Mouse.getY() - (mc.displayHeight / 2.0f);

		double distance = Math.sqrt(scaledX * scaledX + scaledY * scaledY);
		double radius = 60.0 * (mc.displayWidth / width);

		if (distance > radius) {
			double fixedX = scaledX * radius / distance;
			double fixedY = scaledY * radius / distance;

			Mouse.setCursorPosition((int) (mc.displayWidth / 2 + fixedX), (int) (mc.displayHeight / 2 + fixedY));
		}
		//        }

		tessellator.draw();
		GlStateManager.enableTexture2D();

		boolean hasItemInHand = inHand.getCount() > 0;


		//        if (Config. display empty spell slotzzz )
		//        {
		for (int i = 0; i < spellCount; i++) {
			ItemStack inSlot = ItemStack.EMPTY;
			if (inSlot.getCount() <= 0) {
				float angle1 = ((i / (float) numItems) + 0.25f) * 2 * (float) Math.PI;
				float posX = x + itemRadius * (float) Math.cos(angle1);
				float posY = y + itemRadius * (float) Math.sin(angle1);
				//                drawCenteredString(fontRenderer, I18n.format("text.toolbelt.empty"), (int)posX, (int)posY - fontRenderer.FONT_HEIGHT / 2, 0x7FFFFFFF);
			}
		}
		//        }

		RenderHelper.enableGUIStandardItemLighting();
		for (int index = 0; index < spellCount; index++) {
			float angle1 = ((index / (float) spellCount) + 0.25f) * 2 * (float) Math.PI;
			float posX = x - 8 + itemRadius * (float) Math.cos(angle1);
			float posY = y - 8 + itemRadius * (float) Math.sin(angle1);
			ItemStack inSlot = ItemStack.EMPTY;
			if (hasAddButton) {
				if (index > 0) {
					//					inSlot = items.get(index - 1);
				}
			} else {
				//				inSlot = items.get(index);
			}

			//            this.itemRender.renderItemAndEffectIntoGUI(new ItemStack(WizardryItems.blast_upgrade), (int) posX, (int) posY);
			//            this.itemRender.renderItemOverlayIntoGUI(this.fontRenderer, new ItemStack(WizardryItems.blast_upgrade), (int) posX, (int) posY, "");e
			GlStateManager.pushMatrix();
			GlStateManager.scale(0.5, 0.5, 0.5);
			Spell spell = spells.get(index);
			Minecraft.getMinecraft().renderEngine.bindTexture(spell.getIcon());
			DrawingUtils.drawTexturedRect(((int) posX) * 2, ((int) posY) * 2, 0, 0, 32, 32, 32, 32);
			GlStateManager.popMatrix();
			//			if (inSlot.getCount() > 0) {
			//				this.itemRender.renderItemAndEffectIntoGUI(inSlot, (int) posX, (int) posY);
			//				this.itemRender.renderItemOverlayIntoGUI(this.fontRenderer, inSlot, (int) posX, (int) posY, "");
			//			}
		}

		if (hasMouseOver) {
			if (spellOver != null && spellOver != Spells.none) {
				GlStateManager.pushMatrix();
				drawCenteredString(fontRenderer, spellOver.getDisplayNameWithFormatting(), width / 2, (height - fontRenderer.FONT_HEIGHT) / 2, 0xFFFFFFFF);
				// reset colors otherwise everything will be colored
				GlStateManager.color(1.0f, 1.0f, 1.0f);
				GlStateManager.popMatrix();
			}
		}
		//		RenderHelper.disableStandardItemLighting();

		GlStateManager.popMatrix();

		if (itemMouseOver.getCount() > 0)
			renderToolTip(itemMouseOver, mouseX, mouseY);
	}

	private void setMousePosition(double x, double y) {
		Mouse.setCursorPosition((int) (x * mc.displayWidth / width), (int) (y * mc.displayHeight / height));
	}

	private static final float PRECISION = 5;

	private void drawPieArc(BufferBuilder buffer, float x, float y, float z, float radiusIn, float radiusOut, float startAngle, float endAngle, int r, int g, int b, int a) {
		float angle = endAngle - startAngle;
		int sections = Math.max(1, MathHelper.ceil(angle / PRECISION));

		startAngle = (float) Math.toRadians(startAngle);
		endAngle = (float) Math.toRadians(endAngle);
		angle = endAngle - startAngle;

		for (int i = 0; i < sections; i++) {
			float angle1 = startAngle + (i / (float) sections) * angle;
			float angle2 = startAngle + ((i + 1) / (float) sections) * angle;

			float pos1InX = x + radiusIn * (float) Math.cos(angle1);
			float pos1InY = y + radiusIn * (float) Math.sin(angle1);
			float pos1OutX = x + radiusOut * (float) Math.cos(angle1);
			float pos1OutY = y + radiusOut * (float) Math.sin(angle1);
			float pos2OutX = x + radiusOut * (float) Math.cos(angle2);
			float pos2OutY = y + radiusOut * (float) Math.sin(angle2);
			float pos2InX = x + radiusIn * (float) Math.cos(angle2);
			float pos2InY = y + radiusIn * (float) Math.sin(angle2);

			buffer.pos(pos1OutX, pos1OutY, z).color(r, g, b, a).endVertex();
			buffer.pos(pos1InX, pos1InY, z).color(r, g, b, a).endVertex();
			buffer.pos(pos2InX, pos2InY, z).color(r, g, b, a).endVertex();
			buffer.pos(pos2OutX, pos2OutY, z).color(r, g, b, a).endVertex();
		}
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}
}

** */