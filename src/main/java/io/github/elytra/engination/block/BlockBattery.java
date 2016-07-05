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

package io.github.elytra.engination.block;

import java.util.List;

import com.google.common.collect.Lists;

import io.github.elytra.engination.block.te.TileEntityBattery;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFlowerPot;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockBattery extends BlockMachineBase implements ITileEntityProvider {
	public BlockBattery() {
		super("battery");
	}

	@Override
	public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		return this.blockState.getBaseState();
	}
	
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this);
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.blockState.getBaseState();
	}
	
	@Override
	public int getMetaFromState(IBlockState state) {
		return 0;
	}
	
	public void setOn(World world, BlockPos pos, boolean on) {
		//Energy cells don't really activate like other machines do.
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityBattery();
	}
	
	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}
	
	@Override
	public boolean hasComparatorInputOverride(IBlockState state) {
		return true;
	}
	
	@Override
	public int getComparatorInputOverride(IBlockState state, World world, BlockPos pos) {
		TileEntity te = world.getTileEntity(pos);
		if (te instanceof TileEntityBattery) {
			return (int)(((TileEntityBattery)te).getStoragePercent() * 15);
			
		}
		
		return 0;
	}
	
	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		ItemStack result = new ItemStack(ItemBlock.getItemFromBlock(this),1);
		
		TileEntity te = world.getTileEntity(pos);
		if (te!=null && te instanceof TileEntityBattery) {
			long capacity = ((TileEntityBattery) te).getEnergyCapacityInternal();
			
			NBTTagCompound tag = result.getTagCompound();
			if (tag==null) tag = new NBTTagCompound();
			
			NBTTagCompound teTag = te.serializeNBT();
			teTag.setLong("capacity", capacity);
			tag.setTag("BlockEntityTag", teTag);
			
			result.setTagCompound(tag);
		}
		
		List<ItemStack> resultList = Lists.newArrayList();
		resultList.add(result);
		return resultList;
	}

	@Override
	public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
		if (willHarvest) return true; // If it will harvest, delay deletion of the block
		                              // until after getDrops
		return super.removedByPlayer(state, world, pos, player, willHarvest);
	}

	@Override
	public void harvestBlock(World world, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity te, ItemStack tool) {
		super.harvestBlock(world, player, pos, state, te, tool);
		world.setBlockToAir(pos);
	}
	
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
		NBTTagCompound tag = stack.getTagCompound();
		if (tag==null) {
			tooltip.add("0 / 0 Dank");
			return;
		} else {
			NBTTagCompound teTag = tag.getCompoundTag("BlockEntityTag");
			
			tooltip.add(""+teTag.getLong("rf")+" / "+teTag.getLong("capacity")+" Dank");
		}
	}
}
