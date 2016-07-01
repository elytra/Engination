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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.github.elytra.engination.block.BlockConveyor;
import io.github.elytra.engination.block.BlockGenerator;
import io.github.elytra.engination.block.BlockGravityField;
import io.github.elytra.engination.block.BlockLandingPad;
import io.github.elytra.engination.block.BlockLauncher;
import io.github.elytra.engination.block.EnginationBlocks;
import io.github.elytra.engination.block.te.TileEntityGenerator;
import io.github.elytra.engination.client.gui.EnginationGuiHandler;
import io.github.elytra.engination.entity.EntityTomato;
import io.github.elytra.engination.item.ItemTomato;
import io.github.elytra.engination.item.ItemWandRelight;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemFood;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLMissingMappingsEvent;
import net.minecraftforge.fml.common.event.FMLMissingMappingsEvent.MissingMapping;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod(modid=Engination.MODID, name="Engination", version="@VERSION@")
public class Engination {
	public static final String MODID = "engination";
	public static Logger LOG;
	
	public static CreativeTabs TAB_ENGINATION = new CreativeTabs("engination") {
		@Override
		public Item getTabIconItem() {
			return ItemBlock.getItemFromBlock(EnginationBlocks.CONVEYOR);
		}
	};
	
	@Instance(MODID)
	private static Engination instance;
	
	@SidedProxy(clientSide="io.github.elytra.engination.ClientProxy", serverSide="io.github.elytra.engination.Proxy")
	public static Proxy proxy;
	
	
	public static SoundEvent SOUND_TOMATO;
	public static SoundEvent SOUND_THROW;
	public static SoundEvent SOUND_LAUNCH;
	
	
	
	@EventHandler
	public void onPreInit(FMLPreInitializationEvent e) {
		LOG = LogManager.getLogger(Engination.MODID);
		
		//LOG.info("");
		
		SOUND_TOMATO = createSound("tomato");
		SOUND_THROW = createSound("tomato.throw");
		SOUND_LAUNCH = createSound("machine.launcher");
		
		registerBlock(new BlockConveyor(2));
		registerBlock(new BlockConveyor(4));
		registerBlock(new BlockConveyor(8));
		
		registerBlock(new BlockLauncher(2));
		registerBlock(new BlockLauncher(3));
		registerBlock(new BlockLauncher(5));
		
		registerBlock(new BlockLandingPad());
		
		registerBlock(new BlockGenerator());
		GameRegistry.registerTileEntity(TileEntityGenerator.class, "machine.generator");
		
		
		registerBlock(new BlockGravityField());
		
		ItemTomato tomato = new ItemTomato();
		registerItem(tomato);
		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(tomato, new ItemTomato.BehaviorTomatoDispense());
		
		registerItem(new ItemWandRelight());
		
		registerFood("food.celery", 0, 0, false);
		
		
		EntityRegistry.registerModEntity(EntityTomato.class, "tomato", 0, this, 80, 3, true);
		
		proxy.init();
	}
	
	@EventHandler
	public void onInit(FMLInitializationEvent e) {
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new EnginationGuiHandler());
		
	}
	
	@EventHandler
	public void onBlockRemap(FMLMissingMappingsEvent e) {
		for(MissingMapping mapping : e.getAll()) {
			//System.out.println("Remap issue:"+mapping.resourceLocation);
			String modid = mapping.resourceLocation.getResourceDomain();
			String name = mapping.resourceLocation.getResourcePath();
			
			if (modid.equals("dendrology")) {
				
				
				//System.out.println("Trying to remap dendrology item: "+name+" of registry type "+mapping.type);
				
				if (mapping.type==GameRegistry.Type.BLOCK) {
					Block remapTo = Block.getBlockFromName(Engination.MODID+":"+name);
					if (remapTo==null) {
						//System.out.println("Cannot find a remap.");
					} else {
						//System.out.println("Attempting to remap to "+remapTo.getUnlocalizedName());
						mapping.remap(remapTo);
					}
				} else {
					Item remapTo = Item.getByNameOrId(Engination.MODID+":"+name);
					if (remapTo==null) {
						//System.out.println("Cannot find a remap.");
					} else {
						//System.out.println("Attempting to remap to "+remapTo.getUnlocalizedName());
						mapping.remap(remapTo);
					}
				}
			}
			
		}
	}
	
	public void registerBlock(Block block) {
		ItemBlock item = new ItemBlock(block);
		item.setRegistryName(block.getRegistryName());
		GameRegistry.register(item);
		GameRegistry.register(block);
		proxy.registerItemModel(item,0);
	}
	
	public void registerItem(Item item) {
		GameRegistry.register(item);
		proxy.registerItemModel(item, 0);
	}
	
	public void registerFood(String name, int amount, float saturation, boolean forWolves) {
		ItemFood food = new ItemFood(amount, saturation, forWolves);
		food.setRegistryName(name);
		food.setUnlocalizedName(name);
		food.setCreativeTab(Engination.TAB_ENGINATION);
		registerItem(food);
	}
	
	public SoundEvent createSound(String loc) {
		ResourceLocation rsrc = new ResourceLocation(Engination.MODID, loc);
		SoundEvent sound = new SoundEvent(rsrc);
		GameRegistry.register(sound, rsrc);
		return sound;
	}
	
	public static Engination instance() {
		return instance;
	}
}
