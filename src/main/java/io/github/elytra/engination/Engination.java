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

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.github.elytra.engination.block.BlockConveyor;
import io.github.elytra.engination.block.BlockCosmetic;
import io.github.elytra.engination.block.BlockCosmeticPillar;
import io.github.elytra.engination.block.BlockDisappearing;
import io.github.elytra.engination.block.BlockDisappearingMelee;
import io.github.elytra.engination.block.BlockDisappearingSpeed;
import io.github.elytra.engination.block.BlockDisappearingSword;
import io.github.elytra.engination.block.BlockFallThrough;
import io.github.elytra.engination.block.BlockGravityField;
import io.github.elytra.engination.block.BlockLandingPad;
import io.github.elytra.engination.block.BlockLauncher;
import io.github.elytra.engination.block.EnginationBlocks;
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
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;

@Mod(modid=Engination.MODID, name="Engination", version="@VERSION@")
public class Engination {
	public static final String MODID = "engination";
	public static Logger LOG;
	public static Configuration CONFIG;
	
	public static CreativeTabs TAB_ENGINATION = new CreativeTabs("engination") {
		@Override
		public ItemStack getTabIconItem() {
			return new ItemStack(EnginationBlocks.CONVEYOR);
		}
	};
	public static CreativeTabs TAB_COSMETIC = new CreativeTabs("engination.cosmetic") {
		@Override
		public ItemStack getTabIconItem() {
			return new ItemStack(EnginationBlocks.COSMETIC_TOURIAN);
		}
	};
	
	private static boolean ENABLE_RECIPES_COSMETIC = false;
	
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
		
		BlockDisappearing.DELAY_REAPPEAR = CONFIG.getInt(
				"delay-reappear", "block.disappearing",
				BlockDisappearing.DELAY_REAPPEAR, 1, 900,
				"The time it takes for a disappearing block to reappear");
		
		BlockDisappearing.DISAPPEAR_CHAIN_MAX = CONFIG.getInt(
				"chain-max", "block.disappearing",
				BlockDisappearing.DISAPPEAR_CHAIN_MAX, 1, 900,
				"The maximum number of blocks that will disappear together in a group");
		
		ENABLE_RECIPES_COSMETIC = CONFIG.getBoolean("enable-cosmetic", "recipe", ENABLE_RECIPES_COSMETIC, "If enabled, this registers recipes for cosmetic blocks");
		
		CONFIG.save();
		
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
		
		registerCosmeticBlock(new BlockCosmetic("scrapmetal",  Material.IRON, MapColor.BROWN     ).setTip());
		registerCosmeticBlock(new BlockCosmetic("oneup",       Material.IRON, MapColor.IRON      ).setTip());
		registerCosmeticBlock(new BlockCosmetic("loosestone",  Material.ROCK, MapColor.GRAY      ).setTip());
		registerCosmeticBlock(new BlockCosmetic("wood",        Material.WOOD, MapColor.WOOD      ).setTip());
		registerCosmeticBlock(new BlockCosmetic("sanic",       Material.ROCK, MapColor.LIGHT_BLUE).setTip());
		registerCosmeticBlock(new BlockCosmetic("wingfortress",Material.IRON, MapColor.GRAY      ).setTip());
		registerCosmeticBlock(new BlockCosmetic("tourian",     Material.IRON, MapColor.GRAY      ).setTip());
		registerCosmeticBlock(new BlockCosmetic("dolomite",    Material.ROCK, MapColor.STONE     ).setTip());
		registerCosmeticBlock(new BlockCosmetic("celestite",   Material.ROCK, MapColor.CYAN      ).setTip());
		registerCosmeticBlock(new BlockCosmetic("baroque",     Material.ROCK, MapColor.QUARTZ    ).setTip()); //This is a joke. Please don't try to explain history to me.
		registerCosmeticBlock(new BlockCosmetic("presidential",Material.ROCK, MapColor.QUARTZ    ).setTip()); //#NotMyPresident #DealWithIt
		registerCosmeticBlock(new BlockCosmetic("peridot",     Material.GLASS,MapColor.GREEN     ).setTip());
		
		registerCosmeticBlock(new BlockCosmeticPillar("scrapmetal.column", Material.IRON, MapColor.BROWN).setTip());
		registerCosmeticBlock(new BlockCosmeticPillar("baroque.column", Material.IRON, MapColor.GREEN).setTip());
		
		BlockCosmetic lamps = new BlockCosmetic("lamp", Material.ROCK, MapColor.ICE).setTip();
		lamps.setLightLevel(1.0f); //15 light level == 1.0f ... why? MOJANG LOVES CONSISTENCY :(
		registerCosmeticBlock(lamps);
		
		registerBlock(new BlockGravityField());
		registerCosmeticBlock(new BlockFallThrough("fallthrough"));
		registerCosmeticBlock(new BlockDisappearingMelee("melee"));
		registerCosmeticBlock(new BlockDisappearingSword("sword"));
		registerCosmeticBlock(new BlockDisappearingSpeed("speed"));
		
		ItemTomato tomato = new ItemTomato();
		registerItem(tomato);
		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(tomato, new ItemTomato.BehaviorTomatoDispense());
		
		registerItem(new ItemWandRelight());
		
		registerFood("food.celery", 0, 0, false);
		
		
		EntityRegistry.registerModEntity(new ResourceLocation("engination", "tomato"), EntityTomato.class, "tomato", 0, this, 80, 3, true);
		
		proxy.init();
	}
	
	@EventHandler
	public void onInit(FMLInitializationEvent e) {
		if (ENABLE_RECIPES_COSMETIC) {
			GameRegistry.addRecipe(new ShapedOreRecipe(
					new ItemStack(EnginationBlocks.COSMETIC_SCRAPMETAL, 32),
					"SiS", "i i", "SiS",
					'S', "stone",
					'i', "ingotIron"
					));
			
			GameRegistry.addRecipe(new ShapedOreRecipe(
					new ItemStack(EnginationBlocks.COSMETIC_SANIC, 32),
					"SiS", "ili", "SiS",
					'S', "stone",
					'i', "ingotIron",
					'l', "gemLapis"
					));
			
			GameRegistry.addRecipe(new ShapedOreRecipe(
					new ItemStack(EnginationBlocks.COSMETIC_WINGFORTRESS, 32),
					"SiS", "iii", "SiS",
					'S', "stone",
					'i', "ingotIron"
					));
			
			GameRegistry.addRecipe(new ShapedOreRecipe(
					new ItemStack(EnginationBlocks.COSMETIC_TOURIAN, 32),
					"SiS", "ici", "SiS",
					'S', "stone",
					'i', "ingotIron",
					'c', new ItemStack(Items.COAL, 1, OreDictionary.WILDCARD_VALUE)
					));
			
			//Unrealistically expensive to account for the fact that it's really quite big.
			//And you know what they say, big block, ...
			GameRegistry.addRecipe(new ShapedOreRecipe(
					new ItemStack(EnginationBlocks.COSMETIC_PRESIDENTIAL, 1),
					"GgG", "geg", "GgG",
					'G', "blockGold",
					'g', "ingotGold",
					'e', "gemEmerald"
					));
			
			GameRegistry.addRecipe(new ShapedOreRecipe(
					new ItemStack(EnginationBlocks.COSMETIC_WOOD, 32),
					"WWW", "WsW", "WWW",
					'W', "plankWood",
					's', "stickWood"
					));
			
			GameRegistry.addRecipe(new ShapedOreRecipe(
					new ItemStack(EnginationBlocks.COSMETIC_LOOSESTONE, 32),
					"SCS", "C C", "SCS",
					'S', "stone",
					'C', "cobblestone"
					));
			
			GameRegistry.addRecipe(new ShapedOreRecipe(
					new ItemStack(EnginationBlocks.COSMETIC_LAMP, 1),
					"SgS", "g g", "SgS",
					'S', "stone",
					'g', "dustGlowstone"
					));
			
			GameRegistry.addRecipe(new ShapedOreRecipe(
					new ItemStack(EnginationBlocks.COSMETIC_CELESTITE, 32),
					"GcG", "c c", "GcG",
					'G', "blockGlass",
					'c', "dyeCyan"
					));
			
			GameRegistry.addRecipe(new ShapedOreRecipe(
					new ItemStack(EnginationBlocks.COSMETIC_DOLOMITE, 32),
					"SDS", "DGD", "SDS",
					'S', "stone",
					'D', "stoneDiorite",
					'G', "stoneGranite"
					));
			
			//No listAllMushrooms entry exists :/
			GameRegistry.addRecipe(new ShapedOreRecipe(
					new ItemStack(EnginationBlocks.COSMETIC_ONEUP, 32),
					"SSS", "SMS", "SSS",
					'S', "stone",
					'M', new ItemStack(Blocks.RED_MUSHROOM)
					));
			GameRegistry.addRecipe(new ShapedOreRecipe(
					new ItemStack(EnginationBlocks.COSMETIC_ONEUP, 32),
					"SSS", "SMS", "SSS",
					'S', "stone",
					'M', new ItemStack(Blocks.BROWN_MUSHROOM)
					));
			
			if (OreDictionary.doesOreNameExist("gemPeridot")) {
				GameRegistry.addRecipe(new ShapedOreRecipe(
						new ItemStack(EnginationBlocks.COSMETIC_PERIDOT, 32),
						"GGG", "GpG", "GGG",
						'G', "blockGlass",
						'p', "gemPeridot"
						));
			} else {
				GameRegistry.addRecipe(new ShapedOreRecipe(
						new ItemStack(EnginationBlocks.COSMETIC_PERIDOT, 16),
						"SGS", "GlG", "SGS",
						'G', "blockGlass",
						'l', "dyeLime"
						));
			}
			
			GameRegistry.addRecipe(new ShapedOreRecipe(
					new ItemStack(EnginationBlocks.COSMETIC_BAROQUE, 32),
					"SSS", "SES", "SSS",
					'S', "stone",
					'E', new ItemStack(Blocks.SOUL_SAND)
					));
			
			
			//Circular crafting for varieties
			registerCraftingCircle(EnginationBlocks.COSMETIC_BAROQUE);
			registerCraftingCircle(EnginationBlocks.COSMETIC_CELESTITE);
			registerCraftingCircle(EnginationBlocks.COSMETIC_DOLOMITE);
			registerCraftingCircle(EnginationBlocks.COSMETIC_LAMP);
			registerCraftingCircle(EnginationBlocks.COSMETIC_LOOSESTONE);
			registerCraftingCircle(EnginationBlocks.COSMETIC_ONEUP);
			registerCraftingCircle(EnginationBlocks.COSMETIC_PERIDOT);
			registerCraftingCircle(EnginationBlocks.COSMETIC_PRESIDENTIAL);
			registerCraftingCircle(EnginationBlocks.COSMETIC_SANIC);
			registerCraftingCircle(EnginationBlocks.COSMETIC_SCRAPMETAL);
			registerCraftingCircle(EnginationBlocks.COSMETIC_TOURIAN);
			registerCraftingCircle(EnginationBlocks.COSMETIC_WINGFORTRESS);
			registerCraftingCircle(EnginationBlocks.COSMETIC_WOOD);
		}
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
	
	public void registerCraftingCircle(BlockCosmetic block) {
		NonNullList<ItemStack> list = NonNullList.create();
		block.getVarieties(Item.getItemFromBlock(block), list);
		if (list.size()<2) return;
		ItemStack first = list.remove(0);
		ItemStack previous = first;
		for(ItemStack item : list) {
			GameRegistry.addShapelessRecipe(item, previous);
			previous = item;
		}
		GameRegistry.addShapelessRecipe(first, list.get(list.size()-1));
	}
	
	public static Engination instance() {
		return instance;
	}
}
