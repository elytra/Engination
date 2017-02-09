/**
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
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

public class BlockConveyor extends Block {
	public static PropertyDirection PROPERTY_FACING = PropertyDirection.create("facing");
	
	private double scale;
	
	public BlockConveyor(int scale) {
		super(Material.IRON);
		
		this.scale = scale;
		
		this.blockHardness = 1.0f;
		this.blockResistance = 15.0f;
		this.setRegistryName(Engination.MODID, "machine.conveyor."+scale);
		this.setUnlocalizedName("machine.conveyor."+scale);
		
		this.setCreativeTab(Engination.TAB_ENGINATION);
	}
	
	@Override
	public void onEntityWalk(World world, BlockPos pos, Entity ent) {
		if (!world.isRemote) return;
		shootPlayer(world, pos, ent);
	}
	
	@Override
	public void onFallenUpon(World world, BlockPos pos, Entity ent, float fallDistance) {
		if (!world.isRemote) return;
		shootPlayer(world, pos, ent);
	}
	
	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		world.setBlockState( pos,
				this.blockState.getBaseState().withProperty(PROPERTY_FACING, placer.getAdjustedHorizontalFacing())
				);
	}
	
	private void shootPlayer(World world, BlockPos pos, Entity ent) {
		if (!(ent instanceof EntityPlayer)) return;
		
		IBlockState state = world.getBlockState(pos);
		EnumFacing facing = state.getValue(PROPERTY_FACING);
		Vec3i vec = facing.getDirectionVec();
		//double scale = 1.2d;
		Vec3d motion = new Vec3d(vec.getX()*scale, vec.getY()*scale, vec.getZ()*scale);
		
		ent.motionX = adjustScalar(ent.motionX, motion.xCoord);
		ent.motionY = adjustScalar(ent.motionY, motion.yCoord);
		ent.motionZ = adjustScalar(ent.motionZ, motion.zCoord);
	}
	
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, PROPERTY_FACING);
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta) {
		switch(meta) {
		case 0:
			return this.blockState.getBaseState().withProperty(PROPERTY_FACING, EnumFacing.NORTH);
		case 1:
			return this.blockState.getBaseState().withProperty(PROPERTY_FACING, EnumFacing.EAST);
		case 2:
			return this.blockState.getBaseState().withProperty(PROPERTY_FACING, EnumFacing.SOUTH);
		case 3:
			return this.blockState.getBaseState().withProperty(PROPERTY_FACING, EnumFacing.WEST);
		default:
			return this.blockState.getBaseState().withProperty(PROPERTY_FACING, EnumFacing.NORTH);
		}
	}
	
	@Override
	public int getMetaFromState(IBlockState state) {
		switch(state.getValue(PROPERTY_FACING)) {
		case NORTH:
			return 0;
		case EAST:
			return 1;
		case SOUTH:
			return 2;
		case WEST:
			return 3;
		default:
			return 0;
		}
	}
	
	private double adjustScalar(double in, double floor) {
		if (floor<0) {
			return (floor < in) ? floor : in;
		} else if (floor>0) {
			return (floor > in) ? floor : in;
		}
		
		return in;
	}
}
