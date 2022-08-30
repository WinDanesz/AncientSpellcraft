package com.windanesz.ancientspellcraft.client.renderer.tileentity;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.block.BlockArcaneWall;
import com.windanesz.ancientspellcraft.tileentity.TileArcaneWall;
import electroblob.wizardry.util.GeometryUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderArcaneWall extends TileEntitySpecialRenderer<TileArcaneWall> {

	private static final ResourceLocation[] TEXTURES = new ResourceLocation[8];

	static {
		for (int i = 0; i < TEXTURES.length; i++) {
			TEXTURES[i] = new ResourceLocation(AncientSpellcraft.MODID, "textures/blocks/arcane_wall_" + i + ".png");
		}
	}

	private static void drawFace(BufferBuilder buffer, Vec3d topLeft, Vec3d topRight, Vec3d bottomLeft, Vec3d bottomRight, float u1, float v1, float u2, float v2) {
		buffer.pos(topLeft.x, topLeft.y, topLeft.z).tex(u1, v1).endVertex();
		buffer.pos(topRight.x, topRight.y, topRight.z).tex(u2, v1).endVertex();
		buffer.pos(bottomRight.x, bottomRight.y, bottomRight.z).tex(u2, v2).endVertex();
		buffer.pos(bottomLeft.x, bottomLeft.y, bottomLeft.z).tex(u1, v2).endVertex();
	}

	public void render(TileArcaneWall te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		EntityPlayer player = Minecraft.getMinecraft().player;
		World world = Minecraft.getMinecraft().world;
		Vec3d origin = player.getPositionEyes(partialTicks);

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();
		boolean lighting = false;
		boolean flag = false;

		if (!flag) {
			flag = true;
			GlStateManager.pushMatrix();
			GlStateManager.enableBlend();
			lighting = GL11.glIsEnabled(GL11.GL_LIGHTING);
			GlStateManager.disableLighting();
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240, 240);
			GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			//GlStateManager.translate((float) x + 0.5F, (float) y + 1, (float) z + 0.5F);
			GlStateManager.translate(-origin.x, -origin.y + player.getEyeHeight(), -origin.z);

			GlStateManager.color(1, 1, 1, 1);

			Minecraft.getMinecraft().renderEngine.bindTexture(TEXTURES[(player.ticksExisted % (TEXTURES.length * 2)) / 2]);

			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		}

		Vec3d[] vertices = GeometryUtils.getVertices(world.getBlockState(te.getPos()).getBoundingBox(world, te.getPos()).offset(te.getPos()));
		drawFace(buffer, vertices[0], vertices[1], vertices[3], vertices[2], 0, 0, 1, 1); // Bottom

		for (EnumFacing value : EnumFacing.values()) {
			switch (value) {
				case DOWN:
					if (!(te.getWorld().getBlockState(te.getPos().offset(value)).getBlock() instanceof BlockArcaneWall)) {
						drawFace(buffer, vertices[0], vertices[1], vertices[3], vertices[2], 0, 0, 1, 1); // Bottom
					}
					break;
				case UP:
					if (!(te.getWorld().getBlockState(te.getPos().offset(value)).getBlock() instanceof BlockArcaneWall)) {
						drawFace(buffer, vertices[5], vertices[4], vertices[6], vertices[7], 0, 0, 1, 1); // Top
					}
					break;
				case NORTH:
					if (!(te.getWorld().getBlockState(te.getPos().offset(value)).getBlock() instanceof BlockArcaneWall)) {
						drawFace(buffer, vertices[4], vertices[5], vertices[0], vertices[1], 0, 0, 1, 1); // North
					}
					break;
				case SOUTH:
					if (!(te.getWorld().getBlockState(te.getPos().offset(value)).getBlock() instanceof BlockArcaneWall)) {
						drawFace(buffer, vertices[6], vertices[7], vertices[2], vertices[3], 0, 0, 1, 1); // South
					}
					break;
				case WEST:
					if (!(te.getWorld().getBlockState(te.getPos().offset(value)).getBlock() instanceof BlockArcaneWall)) {
						drawFace(buffer, vertices[7], vertices[4], vertices[3], vertices[0], 0, 0, 1, 1); // West
					}
					break;
				case EAST:
					if (!(te.getWorld().getBlockState(te.getPos().offset(value)).getBlock() instanceof BlockArcaneWall)) {
						drawFace(buffer, vertices[5], vertices[6], vertices[1], vertices[2], 0, 0, 1, 1); // East
					}
					break;
			}
		}
		if (flag) {
			tessellator.draw();

			GlStateManager.disableBlend();
			GlStateManager.enableTexture2D();
			if (lighting) {
				GlStateManager.enableLighting();
			}
			GlStateManager.disableRescaleNormal();
			GlStateManager.popMatrix();
		}
	}
}
