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

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * BlockFallThrough - blocks whose "disappearing" criterion is when a Player walks over them. Enemies can pass over them
 * just fine, but players walking over will cause them to temporarily crumble in a chain-reaction, dropping players and
 * enemies alike.
 */
public class BlockFallThrough extends BlockDisappearing {
	public static final ChainReactionType CHAINTYPE_FALLTHROUGH = new ChainReactionType();
	
	public BlockFallThrough(String blockName) {
		super(blockName);
	}

	@Override
	public void onFallenUpon(World world, BlockPos pos, Entity entity, float maybeFallDistance) {
		considerBreaking(world, pos, entity);
	}
	
	@Override
	public void onEntityWalk(World world, BlockPos pos, Entity entity) {
		considerBreaking(world, pos, entity);
	}
	
	public void considerBreaking(World world, BlockPos pos, Entity entity) {
		if (world.isRemote) return;
		
		if (entity instanceof EntityPlayer) {
			this.disappearChainHorizontal(world, pos);
		}
	}
	
	@Override
	public ChainReactionType getChainReactionType() {
		return CHAINTYPE_FALLTHROUGH;
	}
}
