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

import io.github.elytra.engination.Engination;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockLandingPad extends Block {
	
	public BlockLandingPad() {
		super(Material.IRON);
		
		this.blockHardness = 1.0f;
		this.blockResistance = 15.0f;
		this.setRegistryName(Engination.MODID, "machine.landingpad");
		this.setUnlocalizedName("machine.landingpad");
		
		this.setCreativeTab(Engination.TAB_ENGINATION);
	}
	
	@Override
	public void onFallenUpon(World world, BlockPos pos, Entity ent, float fallDistance) {
		ent.fallDistance = 0;
		if (!world.isRemote) return;
	}
}
