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
package io.github.elytra.engination.energy;

import javax.annotation.Nonnull;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.energy.IEnergyStorage;

public class RedstoneFlux {
	public static final IEnergyStorage NULL_ACCESS = new NullWrapper();
	public static Capability<IEnergyStorage> FORGE_ACCESS = null;
	
	@CapabilityInject(net.minecraftforge.energy.IEnergyStorage.class)
	public static void forgeStorageCallback(Capability<net.minecraftforge.energy.IEnergyStorage> cap) {
		FORGE_ACCESS = cap;
	}
	
	@Nonnull
	public static IEnergyStorage getAccess(IBlockAccess world, BlockPos pos, EnumFacing side) {
		TileEntity te = world.getTileEntity(pos);
		if (te==null) return NULL_ACCESS;
		
		if (FORGE_ACCESS!=null) {
			if (te.hasCapability(FORGE_ACCESS, side)) {
				return te.getCapability(FORGE_ACCESS, side);
			}
		}
		
		return NULL_ACCESS;
	}
	
	public int getEnergy(IBlockAccess world, BlockPos pos) {
		return getEnergy(world, pos, null); //Null is valid for the side argument!
	}
	
	public int getEnergy(IBlockAccess world, BlockPos pos, EnumFacing side) {
		return getAccess(world, pos, side).getEnergyStored();
	}
	
	
	public static class NullWrapper implements IEnergyStorage {
		@Override
		public boolean canExtract() {
			return false;
		}

		@Override
		public boolean canReceive() {
			return false;
		}

		@Override
		public int extractEnergy(int amount, boolean simulate) {
			return 0;
		}

		@Override
		public int getEnergyStored() {
			return 0;
		}

		@Override
		public int getMaxEnergyStored() {
			return 0;
		}

		@Override
		public int receiveEnergy(int amount, boolean simulate) {
			return 0;
		}
	}
}
