/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2017 Isaac Ellingson (Falkreon)
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
package io.github.elytra.engination;

import io.github.elytra.engination.block.BlockDisappearing;
import io.github.elytra.engination.entity.EntityTomato;
import io.github.elytra.engination.item.EnginationItems;
import io.github.elytra.engination.item.ItemBlockCosmetic;
import io.github.elytra.engination.item.ItemBlockCosmeticPillar;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ClientProxy extends Proxy {
	private void registerItemModel(Item item) {
		ResourceLocation loc = Item.REGISTRY.getNameForObject(item);
		NonNullList<ItemStack> variantList = NonNullList.create();
		item.getSubItems(item.getCreativeTab(), variantList);
		if (variantList.size()==1) {
			ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(loc, "inventory"));
		} else {
			if (item instanceof ItemBlockCosmeticPillar) {
				for(ItemStack subItem : variantList) {
					ModelLoader.setCustomModelResourceLocation(item, subItem.getItemDamage(), new ModelResourceLocation(loc, "axis=y,variant="+subItem.getItemDamage()));
				}
			} else {
				if ((item instanceof ItemBlockCosmetic) && (((ItemBlockCosmetic)item).getBlock() instanceof BlockDisappearing)) {
					for(ItemStack subItem : variantList) {
						ModelLoader.setCustomModelResourceLocation(item, subItem.getItemDamage(), new ModelResourceLocation(loc, "disappeared=false,variant="+subItem.getItemDamage()));
					}
				} else {
					for(ItemStack subItem : variantList) {
						ModelLoader.setCustomModelResourceLocation(item, subItem.getItemDamage(), new ModelResourceLocation(loc, "variant="+subItem.getItemDamage()));
					}
				}
			}
		}
	}
	
	
	
	@Override
	public void preInit() {
		MinecraftForge.EVENT_BUS.register(this);
		
		RenderingRegistry.registerEntityRenderingHandler(EntityTomato.class,
				(RenderManager m) -> new RenderSnowball<EntityTomato>(m, EnginationItems.TOMATO, Minecraft.getMinecraft().getRenderItem()));
	}
	
	@SubscribeEvent
	@Override
	public void onModelRegister(ModelRegistryEvent e) {
		for(Item item : Engination.instance().pendingItems) {
			registerItemModel(item);
		}
	}
}
