/*
 * MIT License
 *
 * Copyright (c) 2016 Isaac Ellingson (Falkreon)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package io.github.elytra.engination.client.render;

import org.lwjgl.opengl.GL11;

import io.github.elytra.engination.block.te.TileEntityBattery;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

public class RenderEnergyStorage extends TileEntitySpecialRenderer<TileEntityBattery> {
	
	@Override
	public void renderTileEntityAt(TileEntityBattery te, double x, double y, double z, float partialTicks, int destroyStage) {
		TextureAtlasSprite icon = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite("minecraft:blocks/redstone_block");
		
		
		Tessellator tess = Tessellator.getInstance();
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);
		
		RenderHelper.disableStandardItemLighting();
        GlStateManager.disableCull();
        this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        
        
		VertexBuffer buf = tess.getBuffer();
		buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		
		float voxel = 1.0f/16.0f;
		float barLeft  = 0.5f - (voxel*2); //0.35f;
		float barRight = 0.5f + (voxel*2); //0.65f;
		float barTop = te.getStoragePercent();
		float texWidth = icon.getMaxU()-icon.getMinU();
		//float texHeight = icon.getMaxV()-icon.getMinV();
		float texel = texWidth/16;
		float texelMid = icon.getMinU() + (texWidth/2);
		
		float texLeft  = texelMid - (texel*2);
		float texRight = texelMid + (texel*2);
		//float texTop   = icon.getMaxV() - (texel * te.getStoragePercent());
		
		buf.pos( barLeft,  0.0,    voxel*0.8).tex(texLeft,  icon.getMaxV()).endVertex();
		buf.pos( barRight, 0.0,    voxel*0.8).tex(texRight, icon.getMaxV()).endVertex();
		buf.pos( barRight, barTop, voxel*0.8).tex(texRight, icon.getMinV()).endVertex();
		buf.pos( barLeft,  barTop, voxel*0.8).tex(texLeft,  icon.getMinV()).endVertex();

		buf.pos( barLeft,  0.0,    1- (voxel*0.8)).tex(texLeft,  icon.getMaxV()).endVertex();
		buf.pos( barRight, 0.0,    1- (voxel*0.8)).tex(texRight, icon.getMaxV()).endVertex();
		buf.pos( barRight, barTop, 1- (voxel*0.8)).tex(texRight, icon.getMinV()).endVertex();
		buf.pos( barLeft,  barTop, 1- (voxel*0.8)).tex(texLeft,  icon.getMinV()).endVertex();
		
		buf.pos( voxel*0.8, 0.0,    barLeft).tex(texLeft,  icon.getMaxV()).endVertex();
		buf.pos( voxel*0.8, 0.0,    barRight).tex(texRight, icon.getMaxV()).endVertex();
		buf.pos( voxel*0.8, barTop, barRight).tex(texRight, icon.getMinV()).endVertex();
		buf.pos( voxel*0.8, barTop, barLeft).tex(texLeft,  icon.getMinV()).endVertex();
		
		buf.pos( 1- (voxel*0.8), 0.0,    barLeft).tex(texLeft,  icon.getMaxV()).endVertex();
		buf.pos( 1- (voxel*0.8), 0.0,    barRight).tex(texRight, icon.getMaxV()).endVertex();
		buf.pos( 1- (voxel*0.8), barTop, barRight).tex(texRight, icon.getMinV()).endVertex();
		buf.pos( 1- (voxel*0.8), barTop, barLeft).tex(texLeft,  icon.getMinV()).endVertex();
		
		tess.draw();
		GlStateManager.popMatrix();
		RenderHelper.enableStandardItemLighting();
		
	}
	
}
