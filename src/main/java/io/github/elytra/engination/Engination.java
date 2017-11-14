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
import java.util.ArrayList;
import java.util.List;

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
import io.github.elytra.engination.item.EnginationItems;
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
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.registries.IForgeRegistry;

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
	
	public List<Block> pendingBlocks = new ArrayList<>();
	public List<Item> pendingItems = new ArrayList<>();
	
	@SubscribeEvent
	public void onRegisterSounds(RegistryEvent.Register<SoundEvent> e) {
		SOUND_TOMATO = createSound(e.getRegistry(), "tomato");
		SOUND_THROW = createSound(e.getRegistry(), "tomato.throw");
		SOUND_LAUNCH = createSound(e.getRegistry(), "machine.launcher");
	}
	
	public SoundEvent createSound(IForgeRegistry<SoundEvent> registry, String loc) {
		ResourceLocation rsrc = new ResourceLocation(Engination.MODID, loc);
		SoundEvent sound = new SoundEvent(rsrc);
		sound.setRegistryName(rsrc.getResourcePath());
		registry.register(sound);
		return sound;
	}
	
	@SubscribeEvent
	public void onRegisterBlocks(RegistryEvent.Register<Block> e) {
		IForgeRegistry<Block> r = e.getRegistry();
		
		EnginationBlocks.CONVEYOR               = block(r, new BlockConveyor(2));
		EnginationBlocks.CONVEYOR_FAST          = block(r, new BlockConveyor(4));
		EnginationBlocks.CONVEYOR_ULTRAFAST     = block(r, new BlockConveyor(8));
		
		EnginationBlocks.LAUNCHER               = block(r, new BlockLauncher(2));
		EnginationBlocks.LAUNCHER_FORCEFUL      = block(r, new BlockLauncher(3));
		EnginationBlocks.LAUNCHER_ULTRAFORCEFUL = block(r, new BlockLauncher(5));
		
		EnginationBlocks.LANDINGPAD             = block(r, new BlockLandingPad());
		
		EnginationBlocks.COSMETIC_SCRAPMETAL    = block(r, new BlockCosmetic("scrapmetal",  Material.IRON, MapColor.BROWN     ));
		EnginationBlocks.COSMETIC_ONEUP         = block(r, new BlockCosmetic("oneup",       Material.IRON, MapColor.IRON      ));
		EnginationBlocks.COSMETIC_LOOSESTONE    = block(r, new BlockCosmetic("loosestone",  Material.ROCK, MapColor.GRAY      ));
		EnginationBlocks.COSMETIC_WOOD          = block(r, new BlockCosmetic("wood",        Material.WOOD, MapColor.WOOD      ));
		EnginationBlocks.COSMETIC_SANIC         = block(r, new BlockCosmetic("sanic",       Material.ROCK, MapColor.LIGHT_BLUE));
		EnginationBlocks.COSMETIC_WINGFORTRESS  = block(r, new BlockCosmetic("wingfortress",Material.IRON, MapColor.GRAY      ));
		EnginationBlocks.COSMETIC_TOURIAN       = block(r, new BlockCosmetic("tourian",     Material.IRON, MapColor.GRAY      ));
		EnginationBlocks.COSMETIC_DOLOMITE      = block(r, new BlockCosmetic("dolomite",    Material.ROCK, MapColor.STONE     ));
		EnginationBlocks.COSMETIC_CELESTITE     = block(r, new BlockCosmetic("celestite",   Material.ROCK, MapColor.CYAN      ));
		EnginationBlocks.COSMETIC_BAROQUE       = block(r, new BlockCosmetic("baroque",     Material.ROCK, MapColor.QUARTZ    ));
		EnginationBlocks.COSMETIC_PRESIDENTIAL  = block(r, new BlockCosmetic("presidential",Material.ROCK, MapColor.QUARTZ    )); //#NotMyPresident #DealWithIt
		EnginationBlocks.COSMETIC_PERIDOT       = block(r, new BlockCosmetic("peridot",     Material.GLASS,MapColor.GREEN     )); //I love feedback - Thanks u/Barhandar! ^_^
		
		EnginationBlocks.COSMETIC_PILLAR_SCRAPMETAL = block(r, new BlockCosmeticPillar("scrapmetal.column", Material.IRON, MapColor.BROWN));
		EnginationBlocks.COSMETIC_PILLAR_BAROQUE    = block(r, new BlockCosmeticPillar("baroque.column",    Material.IRON, MapColor.GREEN));
		
		EnginationBlocks.COSMETIC_LAMP = block(r, new BlockCosmetic("lamp", Material.ROCK, MapColor.ICE));
		EnginationBlocks.COSMETIC_LAMP.setLightLevel(1.0f);
		
		EnginationBlocks.GRAVITY_FIELD          = block(r, new BlockGravityField());
		
		EnginationBlocks.DISAPPEARING_FALLTHROUGH = block(r, new BlockFallThrough("fallthrough"));
		EnginationBlocks.DISAPPEARING_MELEE       = block(r, new BlockDisappearingMelee("melee"));
		EnginationBlocks.DISAPPEARING_SWORD       = block(r, new BlockDisappearingSword("sword"));
		EnginationBlocks.DISAPPEARING_SPEED       = block(r, new BlockDisappearingSpeed("speed"));
	}
	
	public <T extends Block> T block(IForgeRegistry<Block> registry, T t) { //You forced my hand, Lex
		registry.register(t);
		pendingBlocks.add(t);
		return t;
	}
	
	@SubscribeEvent
	public void onRegisterItems(RegistryEvent.Register<Item> e) {
		IForgeRegistry<Item> r = e.getRegistry();
		
		//Create appropriate item variants of all the blocks we created earlier
		for(Block b : pendingBlocks) {
			if (b instanceof BlockCosmeticPillar) {
				ItemBlockCosmeticPillar item = new ItemBlockCosmeticPillar(b);
				item.setRegistryName(b.getRegistryName());
				r.register(item);
				pendingItems.add(item);
			} else if (b instanceof BlockCosmetic) {
				ItemBlockCosmetic item = new ItemBlockCosmetic(b);
				if (b==EnginationBlocks.COSMETIC_PRESIDENTIAL) item.setMaxStackSize(45); //Important special case
				item.setRegistryName(b.getRegistryName());
				r.register(item);
				pendingItems.add(item);
			} else {
				ItemBlock item = new ItemBlock(b);
				item.setRegistryName(b.getRegistryName());
				r.register(item);
				pendingItems.add(item);
			}
		}
		
		EnginationItems.TOMATO       = item(r, new ItemTomato());
		EnginationItems.WAND_RELIGHT = item(r, new ItemWandRelight());
		EnginationItems.CELERY       = food(r, "food.celery", 0, 0, false);
		
		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(EnginationItems.TOMATO, new ItemTomato.BehaviorTomatoDispense());
	}
	
	public <T extends Item> T item(IForgeRegistry<Item> registry, T t) {
		registry.register(t);
		pendingItems.add(t);
		return t;
	}
	
	public ItemFood food(IForgeRegistry<Item> registry, String name, int amount, float saturation, boolean forWolves) {
		ItemFood food = new ItemFood(amount, saturation, forWolves);
		food.setRegistryName(name);
		food.setUnlocalizedName(name);
		food.setCreativeTab(Engination.TAB_ENGINATION);
		registry.register(food);
		pendingItems.add(food);
		
		return food;
	}
	
	@SubscribeEvent
	public void onRegisterRecipes(RegistryEvent.Register<IRecipe> e) {
		IForgeRegistry<IRecipe> r = e.getRegistry();
		
		if (ENABLE_RECIPES_COSMETIC) {
			ResourceLocation groupCosmetic = new ResourceLocation("engination:cosmetic");
			
			shapedOreRecipe(r, groupCosmetic,
			//r.register(new ShapedOreRecipe( groupCosmetic,
					new ItemStack(EnginationBlocks.COSMETIC_SCRAPMETAL, 32),
					"SiS", "i i", "SiS",
					'S', "stone",
					'i', "ingotIron"
					);//.setRegistryName(EnginationBlocks.COSMETIC_SCRAPMETAL.getRegistryName()));
			
			shapedOreRecipe(r, groupCosmetic,
			//r.register(new ShapedOreRecipe( groupCosmetic,
					new ItemStack(EnginationBlocks.COSMETIC_SANIC, 32),
					"SiS", "ili", "SiS",
					'S', "stone",
					'i', "ingotIron",
					'l', "gemLapis"
					);
			
			shapedOreRecipe(r, groupCosmetic,
			//r.register(new ShapedOreRecipe( groupCosmetic,
					new ItemStack(EnginationBlocks.COSMETIC_WINGFORTRESS, 32),
					"SiS", "iii", "SiS",
					'S', "stone",
					'i', "ingotIron"
					);
			
			shapedOreRecipe(r, groupCosmetic,
			//r.register(new ShapedOreRecipe( groupCosmetic,
					new ItemStack(EnginationBlocks.COSMETIC_TOURIAN, 32),
					"SiS", "ici", "SiS",
					'S', "stone",
					'i', "ingotIron",
					'c', new ItemStack(Items.COAL, 1, OreDictionary.WILDCARD_VALUE)
					);
			
			//Unrealistically expensive to account for the fact that it's really quite big.
			//And you know what they say, big block, ...
			shapedOreRecipe(r, groupCosmetic,
			//r.register(new ShapedOreRecipe( groupCosmetic,
					new ItemStack(EnginationBlocks.COSMETIC_PRESIDENTIAL, 1),
					"GgG", "geg", "GgG",
					'G', "blockGold",
					'g', "ingotGold",
					'e', "gemEmerald"
					);
			
			shapedOreRecipe(r, groupCosmetic,
			//r.register(new ShapedOreRecipe( groupCosmetic,
					new ItemStack(EnginationBlocks.COSMETIC_WOOD, 32),
					"WWW", "WsW", "WWW",
					'W', "plankWood",
					's', "stickWood"
					);
			
			shapedOreRecipe(r, groupCosmetic,
			//r.register(new ShapedOreRecipe( groupCosmetic,
					new ItemStack(EnginationBlocks.COSMETIC_LOOSESTONE, 32),
					"SCS", "C C", "SCS",
					'S', "stone",
					'C', "cobblestone"
					);
			
			shapedOreRecipe(r, groupCosmetic,
			//r.register(new ShapedOreRecipe( groupCosmetic,
					new ItemStack(EnginationBlocks.COSMETIC_LAMP, 1),
					"SgS", "g g", "SgS",
					'S', "stone",
					'g', "dustGlowstone"
					);
			
			shapedOreRecipe(r, groupCosmetic,
			//r.register(new ShapedOreRecipe( groupCosmetic,
					new ItemStack(EnginationBlocks.COSMETIC_CELESTITE, 32),
					"GcG", "c c", "GcG",
					'G', "blockGlass",
					'c', "dyeCyan"
					);
			
			shapedOreRecipe(r, groupCosmetic,
			//r.register(new ShapedOreRecipe( groupCosmetic,
					new ItemStack(EnginationBlocks.COSMETIC_DOLOMITE, 32),
					"SDS", "DGD", "SDS",
					'S', "stone",
					'D', "stoneDiorite",
					'G', "stoneGranite"
					);
			
			//No listAllMushrooms entry exists :/
			shapedOreRecipe(r, groupCosmetic,
			//r.register(new ShapedOreRecipe( groupCosmetic,
					new ItemStack(EnginationBlocks.COSMETIC_ONEUP, 32),
					"SSS", "SMS", "SSS",
					'S', "stone",
					'M', new ItemStack(Blocks.RED_MUSHROOM)
					);
			
			shapedOreRecipe(r, groupCosmetic,
			//r.register(new ShapedOreRecipe( groupCosmetic,
					new ItemStack(EnginationBlocks.COSMETIC_ONEUP, 32),
					"SSS", "SMS", "SSS",
					'S', "stone",
					'M', new ItemStack(Blocks.BROWN_MUSHROOM)
					);
			
			if (OreDictionary.doesOreNameExist("gemPeridot")) {
				shapedOreRecipe(r, groupCosmetic,
				//r.register(new ShapedOreRecipe( groupCosmetic,
						new ItemStack(EnginationBlocks.COSMETIC_PERIDOT, 32),
						"GGG", "GpG", "GGG",
						'G', "blockGlass",
						'p', "gemPeridot"
						);
			} else {
				shapedOreRecipe(r, groupCosmetic,
				//r.register(new ShapedOreRecipe( groupCosmetic,
						new ItemStack(EnginationBlocks.COSMETIC_PERIDOT, 16),
						"SGS", "GlG", "SGS",
						'S', "sand",
						'G', "blockGlass",
						'l', "dyeLime"
						);
			}
			
			shapedOreRecipe(r, groupCosmetic,
			//r.register(new ShapedOreRecipe( groupCosmetic,
					new ItemStack(EnginationBlocks.COSMETIC_BAROQUE, 32),
					"SSS", "SES", "SSS",
					'S', "stone",
					'E', new ItemStack(Blocks.SOUL_SAND)
					);
			
			
			//Circular crafting for varieties
			registerCraftingCircle(r, EnginationBlocks.COSMETIC_BAROQUE);
			registerCraftingCircle(r, EnginationBlocks.COSMETIC_CELESTITE);
			registerCraftingCircle(r, EnginationBlocks.COSMETIC_DOLOMITE);
			registerCraftingCircle(r, EnginationBlocks.COSMETIC_LAMP);
			registerCraftingCircle(r, EnginationBlocks.COSMETIC_LOOSESTONE);
			registerCraftingCircle(r, EnginationBlocks.COSMETIC_ONEUP);
			registerCraftingCircle(r, EnginationBlocks.COSMETIC_PERIDOT);
			registerCraftingCircle(r, EnginationBlocks.COSMETIC_PRESIDENTIAL);
			registerCraftingCircle(r, EnginationBlocks.COSMETIC_SANIC);
			registerCraftingCircle(r, EnginationBlocks.COSMETIC_SCRAPMETAL);
			registerCraftingCircle(r, EnginationBlocks.COSMETIC_TOURIAN);
			registerCraftingCircle(r, EnginationBlocks.COSMETIC_WINGFORTRESS);
			registerCraftingCircle(r, EnginationBlocks.COSMETIC_WOOD);
		}
	}
	
	public ShapedOreRecipe shapedOreRecipe(IForgeRegistry<IRecipe> registry, ResourceLocation category, ItemStack out, Object... recipe) {
		ShapedOreRecipe result = new ShapedOreRecipe(category, out, recipe);
		result.setRegistryName(out.getItem().getRegistryName()+"_"+out.getItemDamage());
		registry.register(result);
		return result;
	}
	
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
		
		EntityRegistry.registerModEntity(new ResourceLocation("engination", "tomato"), EntityTomato.class, "tomato", 0, this, 80, 3, true);
		
		MinecraftForge.EVENT_BUS.register(this);
		proxy.preInit();
	}
	
	
	public void registerCraftingCircle(IForgeRegistry<IRecipe> registry, BlockCosmetic block) {
		NonNullList<ItemStack> list = NonNullList.create();
		block.getVarieties(Item.getItemFromBlock(block), list);
		if (list.size()<2) return;
		ItemStack first = list.remove(0);
		ItemStack previous = first;
		int i = 0;
		for(ItemStack item : list) {
			registry.register(new ShapelessRecipes("engination:chisel", item, NonNullList.from(Ingredient.fromStacks(previous)))
					.setRegistryName(block.getRegistryName()+"_"+i));
			previous = item;
			i++;
		}
		registry.register(new ShapelessRecipes("engination:chisel", first, NonNullList.from(Ingredient.fromStacks(list.get(list.size()-1))))
				.setRegistryName(block.getRegistryName()+"_"+i));
	}
	
	public static Engination instance() {
		return instance;
	}
}
