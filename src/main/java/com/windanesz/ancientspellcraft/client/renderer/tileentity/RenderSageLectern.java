package com.windanesz.ancientspellcraft.client.renderer.tileentity;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.item.ItemSageTome;
import com.windanesz.ancientspellcraft.tileentity.TileSageLectern;
import electroblob.wizardry.constants.Tier;
import electroblob.wizardry.item.ItemSpellBook;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.client.model.ModelBook;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

import java.util.HashMap;

public class RenderSageLectern extends TileEntitySpecialRenderer<TileSageLectern> {

	private static final ResourceLocation GENERIC_BOOK = new ResourceLocation(AncientSpellcraft.MODID, "textures/entity/lectern_book_empty.png");

	private static ResourceLocation[] BOOK_TEXTURES = new ResourceLocation[] {
			new ResourceLocation(AncientSpellcraft.MODID, "textures/entity/lectern_book_generic.png"),
			new ResourceLocation(AncientSpellcraft.MODID, "textures/entity/lectern_book_fire.png"),
			new ResourceLocation(AncientSpellcraft.MODID, "textures/entity/lectern_book_ice.png"),
			new ResourceLocation(AncientSpellcraft.MODID, "textures/entity/lectern_book_lightning.png"),
			new ResourceLocation(AncientSpellcraft.MODID, "textures/entity/lectern_book_necromancy.png"),
			new ResourceLocation(AncientSpellcraft.MODID, "textures/entity/lectern_book_earth.png"),
			new ResourceLocation(AncientSpellcraft.MODID, "textures/entity/lectern_book_sorcery.png"),
			new ResourceLocation(AncientSpellcraft.MODID, "textures/entity/lectern_book_healing.png")
	};

	/**
	 * Addons and other stuff
	 */
	private static HashMap<String, ResourceLocation> EXTRA_BOOK_TEXTURES = new HashMap<String, ResourceLocation>() {{
		put("ancientspellcraft:ancient_spell_book", new ResourceLocation(AncientSpellcraft.MODID, "textures/entity/lectern_book_ancient_spell_book.png"));
		put("ancientspellcraft:ancient_spellcraft_spell_book", new ResourceLocation(AncientSpellcraft.MODID, "textures/entity/lectern_book_ancient_spellcraft_spell_book.png"));
		put("ancientspellcraft:mystic_spell_book", new ResourceLocation(AncientSpellcraft.MODID, "textures/entity/lectern_mystic_spell_book.png"));
		put("ancientspellcraft:empty_mystic_spell_book", new ResourceLocation(AncientSpellcraft.MODID, "textures/entity/lectern_mystic_spell_book.png"));
		put("ancientspellcraft:ritual_book", new ResourceLocation(AncientSpellcraft.MODID, "textures/entity/lectern_book_ritual_book.png"));
		put("minecraft:book", new ResourceLocation(AncientSpellcraft.MODID, "textures/entity/lectern_book_empty.png"));
		put("minecraft:writable_book", new ResourceLocation(AncientSpellcraft.MODID, "textures/entity/lectern_book_empty.png"));
		put("minecraft:written_book", new ResourceLocation(AncientSpellcraft.MODID, "textures/entity/lectern_book_empty.png"));
		put("minecraft:book_and_quill", new ResourceLocation(AncientSpellcraft.MODID, "textures/entity/lectern_book_empty.png"));
		put("minecraft:enchanted_book", new ResourceLocation(AncientSpellcraft.MODID, "textures/entity/lectern_book_enchanted.png"));

	}};

	private final ModelBook modelBook = new ModelBook();

	@Override
	public void render(TileSageLectern te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {

		if (te.hasItem()) {

			GlStateManager.pushMatrix();

			GlStateManager.translate((float) x + 0.5F, (float) y + 1, (float) z + 0.5F);
			GlStateManager.rotate(90 - te.getWorld().getBlockState(te.getPos()).getValue(BlockHorizontal.FACING).getHorizontalAngle(), 0, 1, 0);

			//			GlStateManager.pushMatrix();
			//
			//			GlStateManager.translate(0, 0.2, 0);
			//
			//			float t = Minecraft.getMinecraft().player.ticksExisted + partialTicks;
			//			//		GlStateManager.translate(0, 0.05f * MathHelper.sin(t/15), 0);
			//			this.renderItem(t);
			//			GlStateManager.popMatrix();

			float time = (float) te.ticksExisted + partialTicks;

			float spread = te.bookSpreadPrev + (te.bookSpread - te.bookSpreadPrev) * partialTicks;

			GlStateManager.translate(0, 0.12, 0);

			if (spread > 0.3) { GlStateManager.translate(0, MathHelper.sin(time * 0.1F) * 0.01F, 0); }

			GlStateManager.rotate(112.5F, 0, 0, 1);

			GlStateManager.translate(0, 0.04 + (1 - spread) * 0.09, (1 - spread) * -0.1875);

			GlStateManager.rotate((1 - spread) * -90, 0, 1, 0);

			// Choose book texture
			Item item = te.getBookSlotItem().getItem();
			// Default brown book
			ResourceLocation texture = GENERIC_BOOK;

			if (item instanceof ItemSpellBook) {
				texture = BOOK_TEXTURES[0];
			}

			String name = item.getRegistryName().toString();
			if (item instanceof ItemSageTome) {
				// Default - sage tome textures and default brown book texture
				texture = BOOK_TEXTURES[((ItemSageTome) item).element.ordinal()];
			} else if (item.getRegistryName() != null && EXTRA_BOOK_TEXTURES.containsKey(name)) {
				texture = EXTRA_BOOK_TEXTURES.get(item.getRegistryName().toString());
			}
			this.bindTexture(texture);

			GlStateManager.pushMatrix();

			if (te.getBookSlotItem().getItem() instanceof ItemSageTome && spread < 0.1) {
				Tier tier = ((ItemSageTome) te.getBookSlotItem().getItem()).tier;

				float thickness = tier.level * 0.1f;

				switch (tier) {
					case NOVICE:
						GlStateManager.translate(0, -0.0, 0.03);
						break;
					case APPRENTICE:
						GlStateManager.translate(0, -0.0, 0.02);
						break;
					case ADVANCED:
						GlStateManager.translate(0, -0.0, 0.00);
						break;
					case MASTER:
						GlStateManager.translate(0, -0.0, -0.015);
						break;
				}

				GlStateManager.scale(
						1.0, 1.0, 0.5 + thickness * 2.5);
			}

			float f3 = te.pageFlipPrev + (te.pageFlip - te.pageFlipPrev) * partialTicks + 0.25F;
			float f4 = te.pageFlipPrev + (te.pageFlip - te.pageFlipPrev) * partialTicks + 0.75F;
			f3 = (f3 - (float) MathHelper.fastFloor(f3)) * 1.6F - 0.3F;
			f4 = (f4 - (float) MathHelper.fastFloor(f4)) * 1.6F - 0.3F;

			f3 = MathHelper.clamp(f3, 0, 1);
			f4 = MathHelper.clamp(f4, 0, 1);

			GlStateManager.enableCull();

			this.modelBook.render(null, time, f3, f4, spread, 0.0F, 0.0625F);
			GlStateManager.popMatrix();

			GlStateManager.popMatrix();
		}

	}

	//	private void renderItem(float t) {
	//
	//		ItemStack stack =  new ItemStack(Items.FEATHER);
	//		if (!stack.isEmpty()) {
	//			GlStateManager.pushMatrix();
	//
	//			GlStateManager.rotate(180, 0, 180, -180);
	//			GlStateManager.scale(0.65F, 0.65F, 0.65F);
	//
	//			GlStateManager.color(1, 1, 1, 1);
	//			Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.FIXED);
	//			GlStateManager.popMatrix();
	//		}
	//	}

}
