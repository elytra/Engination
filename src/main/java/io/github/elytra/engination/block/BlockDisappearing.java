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
package io.github.elytra.engination.block;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import io.github.elytra.engination.Engination;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockDisappearing extends BlockCosmetic {
	public static PropertyBool DISAPPEARED = PropertyBool.create("disappeared");
	public static PropertyInteger  VARIANT     = PropertyInteger.create("variant", 0, 7);
	
	public static int DELAY_REAPPEAR = 20 * 5;
	public static int DISAPPEAR_CHAIN_MAX = 16;
	public static final ChainReactionType CHAINTYPE_NONE = new ChainReactionType();
	
	public BlockDisappearing(String blockName) {
		super(Material.CIRCUITS, Material.ROCK.getMaterialMapColor());
		this.setUnlocalizedName("engination.disappearing."+blockName);
		this.setRegistryName("disappearing."+blockName);
		this.setCreativeTab(Engination.TAB_ENGINATION);
		this.needsRandomTick = false;
		
		this.setDefaultState(blockState.getBaseState().withProperty(DISAPPEARED, false).withProperty(VARIANT, 0));
	}

	@Override
	public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
		if (tab.equals(CreativeTabs.SEARCH) || getCreativeTabToDisplayOn().equals(tab)) {
			for(int i=0; i<7; i++) {
				list.add(new ItemStack(ItemBlock.getItemFromBlock(this), 1, i));
			}
		}
	}
	
	@Override
	public BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, DISAPPEARED, VARIANT);
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta) {
		return blockState.getBaseState()
				.withProperty(DISAPPEARED, (meta&0x8)!=0)
				.withProperty(VARIANT, meta & 0x7);
	}
	
	@Override
	public int damageDropped(IBlockState state) {
        return state.getValue(VARIANT);
    }
	
	@Override
	public int getMetaFromState(IBlockState state) {
		if (state.getValue(DISAPPEARED)) {
			return 0x8 | state.getValue(VARIANT);
		} else {
			return 0x0 | state.getValue(VARIANT);
		}
	}
	
	/**
	 * Determines whether entity collision/raycasting hits this block or passes through
	 */
	@Override
	public boolean canCollideCheck(IBlockState state, boolean hitIfLiquid) {
        return !state.getValue(DISAPPEARED);
    }
	
	@Override
	public boolean isPassable(IBlockAccess world, BlockPos pos) {
		return world.getBlockState(pos).getValue(DISAPPEARED);
	}

	@Override
	public int tickRate(World world) {
		return DELAY_REAPPEAR;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB bounds, List<AxisAlignedBB> list, Entity entity, boolean something) {
		if (state.getValue(DISAPPEARED)) {
			//Don't collide with it if disappear'd!
		} else {
			super.addCollisionBoxToList(state, world, pos, bounds, list, entity, something);
		}
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
		if (state.getValue(DISAPPEARED)) {
			return Block.NULL_AABB;
		} else {
			return super.getCollisionBoundingBox(state, world, pos);
		}
	}
	
	@Override
	public boolean isSideSolid(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
		return !state.getValue(DISAPPEARED);
	}
	
	/** Returns true if this block should trigger hidden surface removal in neighbors. **/
	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return !state.getValue(DISAPPEARED);
	}
	
	/** Returns true if light opacity should be 100% regardless of values set on this block */
	@Override
	public boolean isFullCube(IBlockState state) {
		return !state.getValue(DISAPPEARED);
	}
	
	/** Returns true if a block needs updates immediately after world generation; in this case, very no. */
	@Override
	public boolean requiresUpdates() {
		return false;
	}
	
	@Override
	public boolean isAir(IBlockState state, IBlockAccess world, BlockPos pos) {
		return state.getValue(DISAPPEARED);
	}
	
	@Override
	//TODO: TEMPORARY. MAYBE. Really helps for clearing out "dead" bugged disappeared blocks
	public boolean isReplaceable(IBlockAccess world, BlockPos pos) { return world.getBlockState(pos).getValue(DISAPPEARED); }
	
	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
		if (world.isRemote) return;
		if (state.getValue(DISAPPEARED)) {
			world.setBlockState(pos, state.withProperty(DISAPPEARED, false));
		}
	}
	
	public ChainReactionType getChainReactionType() {
		return CHAINTYPE_NONE;
	}
	
	public void disappear(World world, BlockPos pos) {
		IBlockState cur = world.getBlockState(pos);
		if (!cur.getValue(DISAPPEARED)) {
			world.setBlockState(pos, world.getBlockState(pos).withProperty(DISAPPEARED, true));
			world.scheduleUpdate(pos, this, this.tickRate(world));
		}
	}
	
	public void disappearChainHorizontal(World world, BlockPos pos) {
		if (getChainReactionType()==CHAINTYPE_NONE) {
			disappear(world,pos);
			return;
		}
		
		HashSet<BlockPos> flood = new HashSet<>();
		chainToHorizontal(world, pos, getChainReactionType(), 0, flood);
		
		for(BlockPos cur : flood) {
			triggerDisappearance(world, cur);
		}
	}
	
	public void disappearChainReaction(World world, BlockPos pos) {
		if (getChainReactionType()==CHAINTYPE_NONE) {
			disappear(world,pos);
			return;
		}
		
		HashSet<BlockPos> flood = new HashSet<>();
		chainToFull(world, pos, getChainReactionType(), 0, flood);
		
		for(BlockPos cur : flood) {
			triggerDisappearance(world, cur);
		}
	}

	private static void chainToHorizontal(World world, BlockPos pos, ChainReactionType sourceChainType, int depth, Set<BlockPos> set) {
		if (set.contains(pos)) return;
		IBlockState targetBlockState = world.getBlockState(pos);
		if (targetBlockState.getBlock() instanceof BlockDisappearing) {
			ChainReactionType targetChainType = ((BlockDisappearing)targetBlockState.getBlock()).getChainReactionType();
			if (sourceChainType==targetChainType) set.add(pos);
			if (depth<DISAPPEAR_CHAIN_MAX) {
				chainToFull(world, pos.north(), sourceChainType, depth+1, set);
				chainToFull(world, pos.east(), sourceChainType, depth+1, set);
				chainToFull(world, pos.south(), sourceChainType, depth+1, set);
				chainToFull(world, pos.west(), sourceChainType, depth+1, set);
			}
		}
	}
	
	private static void chainToFull(World world, BlockPos pos, ChainReactionType sourceChainType, int depth, Set<BlockPos> set) {
		if (set.contains(pos)) return;
		IBlockState targetBlockState = world.getBlockState(pos);
		if (targetBlockState.getBlock() instanceof BlockDisappearing) {
			ChainReactionType targetChainType = ((BlockDisappearing)targetBlockState.getBlock()).getChainReactionType();
			if (sourceChainType==targetChainType) set.add(pos);
			if (depth<DISAPPEAR_CHAIN_MAX) {
				chainToFull(world, pos.north(), sourceChainType, depth+1, set);
				chainToFull(world, pos.east(), sourceChainType, depth+1, set);
				chainToFull(world, pos.south(), sourceChainType, depth+1, set);
				chainToFull(world, pos.west(), sourceChainType, depth+1, set);
				if (pos.getY()<world.getHeight()-1) chainToFull(world, pos.up(), sourceChainType, depth+1, set);
				if (pos.getY()>0) chainToFull(world, pos.down(), sourceChainType, depth+1, set);
			}
		}
	}
	
	private static void triggerDisappearance(World world, BlockPos pos) {
		IBlockState state = world.getBlockState(pos);
		if (state.getBlock() instanceof BlockDisappearing) {
			((BlockDisappearing)state.getBlock()).disappear(world, pos);
		}
	}
	
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.CUTOUT_MIPPED;
	}
	
	/**
	 * Tagging class for chain reaction IDs. For blocks to chain-react together, their chaintypes must be equal (==).
	 * 
	 * Before this system, I was using obnoxious magic numbers like 0x57ABB3D and 0xC0DE_404. Don't do that, it's bad.
	 *
	 */
	public static class ChainReactionType {}
}
