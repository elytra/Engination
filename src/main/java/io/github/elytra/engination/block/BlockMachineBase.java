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

import io.github.elytra.engination.Engination;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockMachineBase extends Block {
	public static PropertyDirection PROPERTY_FACING = PropertyDirection.create("facing");
	public static PropertyBool      PROPERTY_ON     = PropertyBool.create("on");
	
	protected int guiId = 0;
	
	
	
	public BlockMachineBase(String machineName) {
		super(Material.IRON);
		
		this.blockHardness = 1.0f;
		this.blockResistance = 15.0f;
		this.setRegistryName(Engination.MODID, "machine."+machineName);
		this.setUnlocalizedName("machine."+machineName);
		
		this.setCreativeTab(Engination.TAB_ENGINATION);
	}

	@Override
	public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		return this.blockState.getBaseState().withProperty(PROPERTY_FACING, placer.getAdjustedHorizontalFacing().getOpposite());
	}
	
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, ItemStack held, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (world.isRemote) {
			return true;
		} else {
			player.openGui(Engination.instance(), guiId, world, pos.getX(), pos.getY(), pos.getZ()); //TODO: Machine-specific GUI numbers
			return true;
		}
	}
	
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
        super.breakBlock(world, pos, state);
        world.removeTileEntity(pos);
    }
	
	@Deprecated
	public boolean eventReceived(IBlockState state, World worldIn, BlockPos pos, int id, int param) {
        super.eventReceived(state, worldIn, pos, id, param);
        TileEntity tileentity = worldIn.getTileEntity(pos);
        return tileentity == null ? false : tileentity.receiveClientEvent(id, param);
    }
	
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, PROPERTY_FACING, PROPERTY_ON);
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta) {
		boolean on = (meta & 0x08) != 0;
		
		switch(meta & 0x03) {
		case 0:
			return this.blockState.getBaseState().withProperty(PROPERTY_FACING, EnumFacing.NORTH).withProperty(PROPERTY_ON, on);
		case 1:
			return this.blockState.getBaseState().withProperty(PROPERTY_FACING, EnumFacing.EAST).withProperty(PROPERTY_ON, on);
		case 2:
			return this.blockState.getBaseState().withProperty(PROPERTY_FACING, EnumFacing.SOUTH).withProperty(PROPERTY_ON, on);
		case 3:
			return this.blockState.getBaseState().withProperty(PROPERTY_FACING, EnumFacing.WEST).withProperty(PROPERTY_ON, on);
		default:
			return this.blockState.getBaseState().withProperty(PROPERTY_FACING, EnumFacing.NORTH).withProperty(PROPERTY_ON, on);
		}
	}
	
	@Override
	public int getMetaFromState(IBlockState state) {
		int on = state.getValue(PROPERTY_ON) ? 8 : 0;
		
		switch(state.getValue(PROPERTY_FACING)) {
		case NORTH:
			return 0 | on;
		case EAST:
			return 1 | on;
		case SOUTH:
			return 2 | on;
		case WEST:
			return 3 | on;
		default:
			return 0;
		}
	}
	
	public void setOn(World world, BlockPos pos, boolean on) {
		IBlockState state = world.getBlockState(pos);
		if (state.getValue(PROPERTY_ON)==on) return;
		world.setBlockState(pos, state.withProperty(PROPERTY_ON, on));
	}
}
