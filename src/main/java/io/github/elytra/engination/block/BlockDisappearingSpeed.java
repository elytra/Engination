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

import java.util.List;

import com.google.common.primitives.Doubles;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockDisappearingSpeed extends BlockDisappearing {
	public static ChainReactionType CHAINTYPE_SPEED = new ChainReactionType();
	
	public BlockDisappearingSpeed(String blockName) {
		super(blockName);
	}

	@Override
	public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB bounds, List<AxisAlignedBB> list, Entity entity, boolean something) {
		if (!world.isRemote) return; //Sadly, the really important behavior here is clientside
		if (state.getValue(DISAPPEARED)) {
			//Don't collide with it if disappear'd!
		} else {
			
			if (entity instanceof EntityPlayer) {
				double speed = entitySpeed(entity);
				if (speed<getSpeedThreshold(state)) {
					super.addCollisionBoxToList(state, world, pos, bounds, list, entity, something);
				} else {
					//Move aside, let the man go through. Let the man go through.
				}
			} else {
				super.addCollisionBoxToList(state, world, pos, bounds, list, entity, something);
			}
		}
	}
	
	@Override
	public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity) {
		//if (!world.isRemote) return; //If we skip serverside collision breaks, the "restore" action never triggers.
		
		if (entity instanceof EntityPlayer) {
			if (!state.getValue(DISAPPEARED)) {
				this.disappearChainReaction(world, new BlockPos(pos));
			}
		}
	}
	
	@Override
	public void onFallenUpon(World world, BlockPos pos, Entity entity, float maybeFallDistance) {
		if (!world.isRemote) return;
		if (meetsThreshold(entity, world.getBlockState(pos))) considerBreaking(world, pos, entity);
	}
	
	@Override
	public void onEntityWalk(World world, BlockPos pos, Entity entity) {
		if (!world.isRemote) return;
		if (meetsThreshold(entity, world.getBlockState(pos))) considerBreaking(world, pos, entity);
	}
	
	@Override
	public boolean isPassable(IBlockAccess world, BlockPos pos) {
		return true;
	}
	
	public void considerBreaking(World world, BlockPos pos, Entity entity) {
		if (entity instanceof EntityPlayer) {
			if (!world.getBlockState(pos).getValue(DISAPPEARED)) {
				this.disappearChainReaction(world, new BlockPos(pos));
			}
		}
	}
	
	public boolean meetsThreshold(Entity entity, IBlockState state) {
		if (!(entity instanceof EntityPlayer)) return false;
		return entitySpeed(entity) >= getSpeedThreshold(state);
	}
	
	private static float getSpeedThreshold(IBlockState state) {
		return 0.23f + (0.1f * state.getValue(BlockDisappearing.VARIANT));
	}
	
	private static double entitySpeed(Entity entity) {
		//Used to check Y, but then you could easily *fall* through blocks, and that's just not good.
		return Doubles.max(Math.abs(entity.motionX), Math.abs(entity.motionZ));
	}
	
	@Override
	public ChainReactionType getChainReactionType() {
		return CHAINTYPE_SPEED;
	}
}
