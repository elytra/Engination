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

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.github.elytra.engination.block.BlockConveyor;
import io.github.elytra.engination.block.BlockCosmetic;
import io.github.elytra.engination.block.BlockCosmeticPillar;
import io.github.elytra.engination.block.BlockBattery;
import io.github.elytra.engination.block.BlockGenerator;
import io.github.elytra.engination.block.BlockGravityField;
import io.github.elytra.engination.block.BlockLandingPad;
import io.github.elytra.engination.block.BlockLauncher;
import io.github.elytra.engination.block.EnginationBlocks;
import io.github.elytra.engination.block.te.TileEntityBattery;
import io.github.elytra.engination.block.te.TileEntityGenerator;
import io.github.elytra.engination.block.te.TileEntityMachineBase;
import io.github.elytra.engination.client.gui.EnginationGuiHandler;
import io.github.elytra.engination.entity.EntityTomato;
import io.github.elytra.engination.item.ItemBlockCosmetic;
import io.github.elytra.engination.item.ItemBlockCosmeticPillar;
import io.github.elytra.engination.item.ItemTomato;
import io.github.elytra.engination.item.ItemWandRelight;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemFood;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod(modid=Engination.MODID, name="Engination", version="@VERSION@")
public class Engination {
	public static final String MODID = "engination";
	public static Logger LOG;
	public static Configuration CONFIG;
	
	public static CreativeTabs TAB_ENGINATION = new CreativeTabs("engination") {
		@Override
		public Item getTabIconItem() {
			return ItemBlock.getItemFromBlock(EnginationBlocks.CONVEYOR);
		}
	};
	public static CreativeTabs TAB_COSMETIC = new CreativeTabs("engination.cosmetic") {
		@Override
		public Item getTabIconItem() {
			return ItemBlock.getItemFromBlock(EnginationBlocks.COSMETIC_TOURIAN);
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
		File config = e.getSuggestedConfigurationFile();
		CONFIG = new Configuration(config);
		int updateFrequency = CONFIG.getInt("energyUpdateFrequency", "network", 10, 4, 100, "Affects how frequently energy storage updates are sent to the client.", "config.key.energyUpdateFrequency");
		TileEntityMachineBase.TICKS_BETWEEN_NETWORK_UPDATES = updateFrequency;
		
		CONFIG.save();
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
		
		registerBlock(new BlockBattery());
		GameRegistry.registerTileEntity(TileEntityBattery.class, "machine.battery");
		
		registerCosmeticBlock(new BlockCosmetic("scrapMetal",  Material.IRON, MapColor.BROWN     ).setTip());
		registerCosmeticBlock(new BlockCosmetic("oneUp",       Material.IRON, MapColor.IRON      ).setTip());
		registerCosmeticBlock(new BlockCosmetic("looseStone",  Material.ROCK, MapColor.GRAY      ).setTip());
		registerCosmeticBlock(new BlockCosmetic("wood",        Material.WOOD, MapColor.WOOD      ).setTip());
		registerCosmeticBlock(new BlockCosmetic("sanic",       Material.ROCK, MapColor.LIGHT_BLUE).setTip());
		registerCosmeticBlock(new BlockCosmetic("wingFortress",Material.IRON, MapColor.GRAY      ).setTip());
		registerCosmeticBlock(new BlockCosmetic("tourian",     Material.IRON, MapColor.GRAY      ).setTip());
		registerCosmeticBlock(new BlockCosmetic("dolomite",    Material.ROCK, MapColor.STONE     ).setTip());
		registerCosmeticBlock(new BlockCosmetic("celestite",   Material.ROCK, MapColor.CYAN      ).setTip());
		registerCosmeticBlock(new BlockCosmetic("baroque",     Material.ROCK, MapColor.QUARTZ    ).setTip()); //This is a joke. Please don't try to explain history to me.
		registerCosmeticBlock(new BlockCosmetic("presidential",Material.ROCK, MapColor.QUARTZ    ).setTip()); //#NotMyPresident #DealWithIt
		
		
		registerCosmeticBlock(new BlockCosmeticPillar("scrapMetal.column", Material.IRON, MapColor.BROWN).setTip());
		registerCosmeticBlock(new BlockCosmeticPillar("baroque.column", Material.IRON, MapColor.GREEN).setTip());
		
		BlockCosmetic lamps = new BlockCosmetic("lamp", Material.ROCK, MapColor.ICE).setTip();
		lamps.setLightLevel(1.0f); //15 light level == 1.0f ... why? MOJANG LOVES CONSISTENCY :(
		registerCosmeticBlock(lamps);
		
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
	
	public void registerBlock(Block block) {
		ItemBlock item = new ItemBlock(block);
		item.setRegistryName(block.getRegistryName());
		GameRegistry.register(item);
		GameRegistry.register(block);
		proxy.registerItemModel(item);
	}
	
	public void registerCosmeticBlock(Block block) {
		ItemBlock item = null;
		if (block instanceof BlockCosmeticPillar) item = new ItemBlockCosmeticPillar(block);
		else item = new ItemBlockCosmetic(block);
		item.setRegistryName(block.getRegistryName());
		GameRegistry.register(item);
		GameRegistry.register(block);
		proxy.registerItemModel(item);
	}

	
	public void registerItem(Item item) {
		GameRegistry.register(item);
		proxy.registerItemModel(item);
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
