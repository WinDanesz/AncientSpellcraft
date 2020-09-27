package com.windanesz.ancientspellcraft.client.renderer.entity;

import com.windanesz.ancientspellcraft.client.model.ModelSpellBook;
import com.windanesz.ancientspellcraft.entity.living.EntitySpellBook;
import net.minecraft.client.model.ModelBook;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

@SideOnly(Side.CLIENT)
public class RenderSpellBook extends RenderLivingBase<EntitySpellBook> {
	/** The texture for the book above the book */
	private static final ResourceLocation TEXTURE_BOOK = new ResourceLocation("textures/entity/enchanting_table_book.png");
	private final ModelBook modelBook = new ModelBook();

	public RenderSpellBook(RenderManager renderManager) {
		super(renderManager, new ModelSpellBook(), 0.5F);
	}

	@Nullable
	@Override
	protected ResourceLocation getEntityTexture(EntitySpellBook entity) {
		return TEXTURE_BOOK;
	}

}
