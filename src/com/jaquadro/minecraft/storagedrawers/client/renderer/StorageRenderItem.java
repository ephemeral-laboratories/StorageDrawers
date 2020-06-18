package com.jaquadro.minecraft.storagedrawers.client.renderer;

import java.text.NumberFormat;

import com.jaquadro.minecraft.storagedrawers.inventory.ItemStackHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ModelManager;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;

@SideOnly(Side.CLIENT)
public class StorageRenderItem extends RenderItem
{
    private RenderItem parent;

    @Nonnull
    public ItemStack overrideStack;

    public StorageRenderItem (TextureManager texManager, ModelManager modelManager, ItemColors colors) {
        super(texManager, modelManager, colors);
        parent = Minecraft.getMinecraft().getRenderItem();
        overrideStack = ItemStack.EMPTY;
    }

    @Override
    public ItemModelMesher getItemModelMesher () {
        return parent.getItemModelMesher();
    }

    @Override
    public void renderItem (@Nonnull ItemStack stack, IBakedModel model) {
        parent.renderItem(stack, model);
    }

    @Override
    public boolean shouldRenderItemIn3D (@Nonnull ItemStack stack) {
        return parent.shouldRenderItemIn3D(stack);
    }

    @Override
    public void renderItem (@Nonnull ItemStack stack, ItemCameraTransforms.TransformType transformType) {
        parent.renderItem(stack, transformType);
    }

    @Override
    public void renderItem (@Nonnull ItemStack stack, EntityLivingBase entity, ItemCameraTransforms.TransformType transform, boolean flag) {
        parent.renderItem(stack, entity, transform, flag);
    }

    @Override
    public IBakedModel getItemModelWithOverrides (@Nonnull ItemStack stack, World world, EntityLivingBase entity) {
        return parent.getItemModelWithOverrides(stack, world, entity);
    }

    @Override
    public void renderItemIntoGUI (@Nonnull ItemStack stack, int x, int y) {
        parent.renderItemIntoGUI(stack, x, y);
    }

    @Override
    public void renderItemAndEffectIntoGUI (@Nonnull ItemStack stack, int xPosition, int yPosition) {
        parent.renderItemAndEffectIntoGUI(stack, xPosition, yPosition);
    }

    @Override
    public void renderItemOverlays (FontRenderer fr, @Nonnull ItemStack stack, int xPosition, int yPosition) {
        parent.renderItemOverlays(fr, stack, xPosition, yPosition);
    }

    @Override
    public void renderItemOverlayIntoGUI (FontRenderer font, @Nonnull ItemStack item, int x, int y, String text)
    {
        if (item != overrideStack) {
            super.renderItemOverlayIntoGUI(font, item, x, y, text);
            return;
        }

        item = ItemStackHelper.decodeItemStack(item);

        if (!item.isEmpty())
        {
            float scale = .5f;
            float xoff = 0;
            if (font.getUnicodeFlag()) {
                scale = 1f;
                xoff = 1;
            }

            int stackSize = item.getCount();
            if (ItemStackHelper.isStackEncoded(item))
                stackSize = 0;

            if (stackSize >= 0 || text != null)
            {
                if (text == null)
                {
                    text = CountFormatter.formatQuantity(font, text, stackSize);
                }

                int textX = (int)((x + 16 + xoff - font.getStringWidth(text) * scale) / scale) - 1;
                int textY = (int)((y + 16 - 7 * scale) / scale) - 1;

                GlStateManager.disableLighting();
                GlStateManager.disableDepth();
                GlStateManager.disableBlend();
                GlStateManager.pushMatrix();
                GlStateManager.scale(scale, scale, scale);

                if (stackSize > 0)
                    font.drawStringWithShadow(text, textX, textY, 16777215);
                else
                    font.drawStringWithShadow(text, textX, textY, (255 << 16) | (96 << 8) | (96));

                GlStateManager.popMatrix();
                GlStateManager.enableLighting();
                GlStateManager.enableDepth();
            }

            if (item.getItem().showDurabilityBar(item))
            {
                double health = item.getItem().getDurabilityForDisplay(item);
                int j1 = (int)Math.round(13.0D - health * 13.0D);
                int k = (int)Math.round(255.0D - health * 255.0D);
                GlStateManager.disableLighting();
                GlStateManager.disableDepth();
                GlStateManager.disableTexture2D();
                GlStateManager.disableAlpha();
                GlStateManager.disableBlend();
                Tessellator tessellator = Tessellator.getInstance();
                BufferBuilder worldrenderer = tessellator.getBuffer();
                int l = 255 - k << 16 | k << 8;
                int i1 = (255 - k) / 4 << 16 | 16128;
                this.renderQuad(worldrenderer, x + 2, y + 13, 13, 2, 0, 0, 0, 255);
                this.renderQuad(worldrenderer, x + 2, y + 13, 12, 1, (255 - k) / 4, 64, 0, 255);
                this.renderQuad(worldrenderer, x + 2, y + 13, j1, 1, 255 - k, k, 0, 255);
                //GL11.glEnable(GL11.GL_BLEND); // Forge: Disable Bled because it screws with a lot of things down the line.
                GlStateManager.enableAlpha();
                GlStateManager.enableTexture2D();
                GlStateManager.enableLighting();
                GlStateManager.enableDepth();
            }
        }
    }

    private void renderQuad (BufferBuilder tessellator, int x, int y, int w, int h, int r, int g, int b, int a)
    {
        tessellator.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        tessellator.pos(x + 0, y + 0, 0).color(r, g, b, a).endVertex();
        tessellator.pos(x + 0, y + h, 0).color(r, g, b, a).endVertex();
        tessellator.pos(x + w, y + h, 0).color(r, g, b, a).endVertex();
        tessellator.pos(x + w, y + 0, 0).color(r, g, b, a).endVertex();
        Tessellator.getInstance().draw();
    }
}
