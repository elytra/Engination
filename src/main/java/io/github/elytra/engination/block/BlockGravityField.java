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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import io.github.elytra.engination.Engination;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLog;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockGravityField extends Block {
	public static PropertyDirection PROPERTY_FACING = PropertyDirection.create("facing");
	
	public static AxisAlignedBB SELECTION_BOX = new AxisAlignedBB(0,0,0,0,0,0);
	
	public BlockGravityField() {
		super(Material.IRON);
		this.fullBlock = false;

		this.lightOpacity = 0;
		this.lightValue = 0x0;
		this.useNeighborBrightness = false;
		
		this.setHardness(99999);
		this.setResistance(99999);
		
		this.setCreativeTab(Engination.TAB_ENGINATION);
		
		this.setRegistryName("field.gravity");
		this.setUnlocalizedName("field.gravity");
		
		
	}
	
	//@Override
	//public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
	//	
	//	EnumFacing dir = BlockPistonBase.getFacingFromEntity(pos, placer).getOpposite();
	//	
	//	return this.blockState.getBaseState().withProperty(PROPERTY_FACING, dir);
	//}
	
	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		EnumFacing facing = BlockPistonBase.getFacingFromEntity(pos, placer).getOpposite();
		world.setBlockState(pos,
				this.getStateFromMeta(stack.getMetadata())
				    .withProperty(PROPERTY_FACING, facing));
	}
	
	
	public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity) {
		entity.fallDistance = 0;
		
		if (!world.isRemote && entity instanceof EntityPlayer) return;
		//if (world.isRemote && !(entity instanceof EntityPlayer)) return;
			
		//Don't adjust player velocities if they're sneaking or creative flying
		//.......do if they're elytra flying.
		if (entity.isSneaking()) return;
		if (entity instanceof EntityPlayer) {
			if (((EntityPlayer)entity).capabilities.isFlying) return;
		}
		
		EnumFacing facing = state.getValue(PROPERTY_FACING);
		adjustMotion(entity, facing);
		
		//entity.motionX += facing.getFrontOffsetX();
		//entity.motionY += facing.getFrontOffsetY();
		//entity.motionZ += facing.getFrontOffsetZ();
	}
	
	private boolean adjustMotion(Entity entity, EnumFacing vector) {
		double step = 0.2d;
		double cap = 0.8d;
		double outX = adjustScalar(entity.motionX, vector.getFrontOffsetX()*cap, Math.signum(vector.getFrontOffsetX())*step);
		double outY = adjustScalar(entity.motionY, vector.getFrontOffsetY()*cap, Math.signum(vector.getFrontOffsetY())*step);
		double outZ = adjustScalar(entity.motionZ, vector.getFrontOffsetZ()*cap, Math.signum(vector.getFrontOffsetZ())*step);
		
		if (entity.motionX!=outX || entity.motionY!=outY || entity.motionZ!=outZ) {
			
			entity.motionX = outX;
			entity.motionY = outY;
			entity.motionZ = outZ;
			return true;
		} else {
			return false;
		}
	}
	
	private double adjustScalar(double in, double cap, double step) {
		if (cap==0) return 0;
		
		double tmp = in + step;
		if (cap<0) {
			if (tmp<cap) tmp=cap;
		} else {
			if (tmp>cap) tmp=cap;
		}
		
		return tmp;
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
		case 4:
			return this.blockState.getBaseState().withProperty(PROPERTY_FACING, EnumFacing.UP);
		case 5:
			return this.blockState.getBaseState().withProperty(PROPERTY_FACING, EnumFacing.DOWN);
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
		case UP:
			return 4;
		case DOWN:
			return 5;
		default:
			return 0;
		}
	}
	
	@Override
	public boolean canCollideCheck(IBlockState state, boolean hitIfLiquid) {
        return false;
    }
	
	
	public static void addCollisionBoxToList(BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, AxisAlignedBB blockBox) {}
	
	@Override
	public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World world, BlockPos pos) { return SELECTION_BOX; }
	@Override
	public boolean isSideSolid(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) { return false; }
	@Override
	public boolean isOpaqueCube(IBlockState state) { return false; }
	@Override
    public boolean isFullCube(IBlockState state) { return false; }
	@Override
	public boolean isBlockSolid(IBlockAccess world, BlockPos pos, EnumFacing side) { return false; }
	@Override
	public boolean isAir(IBlockState state, IBlockAccess world, BlockPos pos) { return true; }
	@Override
	public boolean canBeReplacedByLeaves(IBlockState state, IBlockAccess world, BlockPos pos) { return true; }
	@Override
	public boolean isReplaceable(IBlockAccess world, BlockPos pos) { return true; }
	@Override
	public boolean canDropFromExplosion(Explosion explosion) { return false; }
	@Override
	public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune) {}
	@Override
	public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, Entity entityIn) {}
	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState state, World world, BlockPos pos) { return Block.NULL_AABB; }
	@Override
	public Item getItemDropped(IBlockState state, Random random, int fortuneLevel) { return null; }
	@Override
	public int quantityDropped(Random random) { return 0; }
	@Override
	public ArrayList<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		return new ArrayList<ItemStack>();
	}
	
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.CUTOUT_MIPPED;
	}
}
