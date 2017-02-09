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
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockLauncher extends Block {
	private double scale;
	
	public BlockLauncher(int scale) {
		super(Material.IRON);
		
		this.scale = scale;
		
		this.blockHardness = 1.0f;
		this.blockResistance = 15.0f;
		this.setRegistryName(Engination.MODID, "machine.launcher."+scale);
		this.setUnlocalizedName("machine.launcher."+scale);
		
		this.setCreativeTab(Engination.TAB_ENGINATION);
	}
	
	@Override
	public void onEntityWalk(World world, BlockPos pos, Entity ent) {
		if (!world.isRemote) return;
		launchPlayer(world, pos, ent);
	}
	
	@Override
	public void onFallenUpon(World world, BlockPos pos, Entity ent, float fallDistance) {
		super.onFallenUpon(world, pos, ent, fallDistance);
		
		if (!world.isRemote) return;
		launchPlayer(world, pos, ent);
	}
	
	private void launchPlayer(World world, BlockPos pos, Entity ent) {
		if (!(ent instanceof EntityPlayer)) return;
		double newY = adjustScalar(ent.motionY, scale*0.6);
		if (newY!=ent.motionY) {
			ent.motionY = newY;
			world.playSound((EntityPlayer)ent, ent.posX, ent.posY, ent.posZ, Engination.SOUND_LAUNCH, SoundCategory.PLAYERS, 0.5F, RANDOM.nextFloat() * 0.4F + 1.0F);
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
