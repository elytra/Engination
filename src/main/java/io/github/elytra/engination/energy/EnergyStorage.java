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

package io.github.elytra.engination.energy;

import java.util.List;

import com.google.common.collect.Lists;

import gigaherz.capabilities.api.energy.IEnergyHandler;
import gigaherz.capabilities.api.energy.IEnergyPersist;
import io.github.elytra.engination.Listener;
import net.minecraft.util.EnumFacing;

public class EnergyStorage implements IEnergyHandler, IEnergyPersist, cofh.api.energy.IEnergyHandler {
	private int rf = 0;
	private int max = 0;
	private int perTick = 30;
	
	private List<Listener<IEnergyHandler>> listeners = Lists.newArrayList();
	
	public EnergyStorage(int limit) {
		max = limit;
	}

	public EnergyStorage(int limit, int perTick) {
		max=limit;
		this.perTick = perTick;
	}

	public static int min(int a, int b, int c) {
		if (a<b && a<c) return a;
		if (b<a && b<c) return b;
		return c;
	}
	
	public void markDirty() {
		for(Listener<IEnergyHandler> l : listeners) {
			l.changed(this);
		}
	}
	
	public EnergyStorage listen(Listener<IEnergyHandler> l) {
		listeners.add(l);
		return this;
	}
	
	/*
	 * -----BEGIN capability IEnergyHandler-----
	 */
	
	@Override
	public int getCapacity() {
		return max;
	}

	@Override
	public int getEnergy() {
		return rf;
	}

	@Override
	public int extractEnergy(int maxExtract, boolean simulate) {
		int toExtract = min(maxExtract, rf, perTick);
		if (!simulate) {
			rf -= toExtract;
		}
		
		markDirty();
		
		return toExtract;
	}

	@Override
	public int insertEnergy(int maxReceive, boolean simulate) {
		int toInsert = min(maxReceive, max-rf, perTick);
		if (!simulate) {
			rf += toInsert;
		}
		
		markDirty();
		
		return toInsert;
	}
	
	/**
	 * @deprecated	This method is for internal deserialization only. Use {@link #insertEnergy} for
	 * 				automation or player interaction
	 */
	@Override
	@Deprecated
	public void setEnergy(int energy) {
		rf = energy;
	}
	
	/*
	 * ----- END capability IEnergyHandler -----
	 * -----   BEGIN cofh IEnergyHandler   -----
	 * forwards to capability methods for more centralized debugging
	 */

	@Override
	public int extractEnergy(EnumFacing from, int maxExtract, boolean simulate) {
		return extractEnergy(maxExtract, simulate);
	}

	@Override
	public boolean canConnectEnergy(EnumFacing from) {
		return true;
	}

	@Override
	public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
		return insertEnergy(maxReceive, simulate);
	}

	@Override
	public int getEnergyStored(EnumFacing from) {
		return getEnergy();
	}

	@Override
	public int getMaxEnergyStored(EnumFacing from) {
		return getCapacity();
	}

	
	
	
}
