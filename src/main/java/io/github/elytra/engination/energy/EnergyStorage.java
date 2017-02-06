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
import com.google.common.primitives.Longs;

import io.github.elytra.engination.Listener;
import net.minecraftforge.energy.IEnergyStorage;

public class EnergyStorage implements IEnergyStorage {
	private long rf = 0;
	private long max = 0;
	private long outPerTick = 30;
	private long inPerTick = 30;
	
	private List<Listener<EnergyStorage>> listeners = Lists.newArrayList();
	
	public EnergyStorage(int limit) {
		max = limit;
	}
	
	public EnergyStorage(int limit, int perTick) {
		max=limit;
		this.outPerTick = perTick;
		this.inPerTick = perTick;
	}
	
	public EnergyStorage(int limit, int in, int out) {
		this.max=limit;
		this.inPerTick = in;
		this.outPerTick = out;
	}

	protected void markDirty() {
		for(Listener<EnergyStorage> l : listeners) {
			l.changed(this);
		}
	}
	
	public EnergyStorage listen(Listener<EnergyStorage> l) {
		listeners.add(l);
		return this;
	}
	
	@Override
	public int getMaxEnergyStored() {
		return (int)Longs.min(max,Integer.MAX_VALUE);
	}

	@Override
	public int getEnergyStored() {
		return (int)rf;
	}

	@Override
	public boolean canExtract() {
		return outPerTick>0;
	}
	
	@Override
	public int extractEnergy(int maxExtract, boolean simulate) {
		int toExtract = (int)Longs.min(maxExtract, rf, outPerTick, Integer.MAX_VALUE);
		if (toExtract<=0) return 0;
		
		if (!simulate) {
			rf -= toExtract;
			markDirty();
		}
		
		return toExtract;
	}

	public boolean canReceive() {
		return inPerTick>0;
	}
	
	@Override
	public int receiveEnergy(int maxReceive, boolean simulate) {
		if (maxReceive<=0) return 0;
		long freeSpace = max-rf;
		if (freeSpace<=0) return 0;
		
		int toInsert = (int)Longs.min(maxReceive, freeSpace, inPerTick, Integer.MAX_VALUE);
		if (!simulate) {
			rf += toInsert;
			markDirty();
		}
		
		return toInsert;
	}
	
	/**
	 * @deprecated	This method is for internal deserialization only. Use {@link #insertEnergy} for
	 * 				automation or player interaction
	 */
	@Deprecated
	public void setEnergy(long energy) {
		rf = energy;
	}
	
	/**
	 * @deprecated	This method is for internal tile initialization only. Do not call for any reason.
	 */
	@Deprecated
	public void setCapacity(long capacity) {
		max = capacity;
		if (rf>max) rf=max;
	}
	
	/**
	 * @deprecated	This method is for internal tile initialization only. Do not call for any reason.
	 */
	@Deprecated
	public void setRateIn(long rateIn) {
		inPerTick = rateIn;
	}
	
	/**
	 * @deprecated	This method is for internal tile initialization only. Do not call for any reason.
	 */
	@Deprecated
	public void setRateOut(long rateOut) {
		outPerTick = rateOut;
	}
}
