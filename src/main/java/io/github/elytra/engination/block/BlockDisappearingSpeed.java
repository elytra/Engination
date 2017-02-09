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
import net.minecraft.world.World;

public class BlockDisappearingSpeed extends BlockDisappearing {

	public BlockDisappearingSpeed(String blockName) {
		super(blockName);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB bounds, List<AxisAlignedBB> list, Entity entity, boolean something) {
		if (state.getValue(DISAPPEARED)) {
			//Don't collide with it if disappear'd!
		} else {
			if (entity instanceof EntityPlayer) {
				double speed = entitySpeed(entity);
				System.out.println("SPEED: "+speed);
				switch(state.getValue(BlockDisappearing.VARIANT)) {
				case 0:
				case 1:
				case 2:
				case 3:
				case 4:
				case 5:
				case 6:
				case 7:
				default:
					super.addCollisionBoxToList(state, world, pos, bounds, list, entity, something);
					break;
				}
			} else {
				super.addCollisionBoxToList(state, world, pos, bounds, list, entity, something);
			}
		}
	}
	
	private static double entitySpeed(Entity entity) {
		return Doubles.max(Math.abs(entity.motionX), Math.abs(entity.motionY), Math.abs(entity.motionZ));
	}
}
