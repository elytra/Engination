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

package io.github.elytra.engination;

import io.github.elytra.engination.block.BlockMachineBase;
import io.github.elytra.engination.client.gui.EnergyWailaDataProvider;
import io.github.elytra.engination.entity.EntityTomato;
import io.github.elytra.engination.item.EnginationItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInterModComms;

public class ClientProxy extends Proxy {
	@Override
	public void registerItemModel(Item item, int variants) {
		ResourceLocation loc = Item.REGISTRY.getNameForObject(item);
		ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(loc, "inventory"));
	}
	
	@Override
	public void init() {
		RenderingRegistry.registerEntityRenderingHandler(EntityTomato.class,
				(RenderManager m) -> new RenderSnowball<EntityTomato>(m, EnginationItems.TOMATO, Minecraft.getMinecraft().getRenderItem()));
		FMLInterModComms.sendMessage( "Waila", "register", EnergyWailaDataProvider.class.getCanonicalName() + ".callbackRegister" );
		/*
		if (Loader.isModLoaded("Waila")) {
			EnergyWailaDataProvider provider = new EnergyWailaDataProvider();
			System.out.println("Waila data provider created.");
			mcp.mobius.waila.api.impl.ModuleRegistrar registrar = null;
			System.out.println("Waila registrar local variable declared.");
			registrar = mcp.mobius.waila.api.impl.ModuleRegistrar.instance();
			System.out.println("Waila registrar acquired");
			if (registrar==null) {
				System.out.println("Waila registrar is null! Bailing on Waila integration");
				return;
			}
			System.out.println("Waila registrar acquired");
			//registrar.registerBodyProvider(provider, BlockMachineBase.class);
			//System.out.println("Waila integration complete.");
		}*/
	}
}
