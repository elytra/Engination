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

import io.github.elytra.engination.Listener;
import net.minecraft.util.EnumFacing;

public class EnergyStorage {
	private long rf = 0;
	private long max = 0;
	private long perTick = 30;
	
	private CapabilityCoreWrapper capabilityCoreProxy = null;
	private RedstoneFluxWrapper redstoneFluxProxy = null;
	private TeslaWrapper teslaProxy = null;
	
	private List<Listener<EnergyStorage>> listeners = Lists.newArrayList();
	
	public EnergyStorage(int limit) {
		max = limit;
	}

	public EnergyStorage(int limit, int perTick) {
		max=limit;
		this.perTick = perTick;
	}

	public static long min(long a, long b, long c) {
		if (a<b && a<c) return a;
		if (b<a && b<c) return b;
		return c;
	}
	
	public void markDirty() {
		for(Listener<EnergyStorage> l : listeners) {
			l.changed(this);
		}
	}
	
	public EnergyStorage listen(Listener<EnergyStorage> l) {
		listeners.add(l);
		return this;
	}
	
	public gigaherz.capabilities.api.energy.IEnergyHandler getCapabilityCoreWrapper() {
		if (capabilityCoreProxy==null) capabilityCoreProxy = new CapabilityCoreWrapper(this);
		return capabilityCoreProxy;
	}
	
	public cofh.api.energy.IEnergyHandler getRedstoneFluxWrapper() {
		if (redstoneFluxProxy==null) redstoneFluxProxy = new RedstoneFluxWrapper(this);
		return redstoneFluxProxy;
	}
	
	public net.darkhax.tesla.api.ITeslaHolder getTeslaWrapper() {
		if (teslaProxy==null) teslaProxy = new TeslaWrapper(this);
		return teslaProxy;
	}
	
	/*
	 * -----BEGIN direct compatibility-----
	 */
	
	public long getCapacity() {
		return max;
	}

	public long getEnergy() {
		return rf;
	}

	public long extractEnergy(long maxExtract, boolean simulate) {
		long toExtract = min(maxExtract, rf, perTick);
		if (!simulate) {
			rf -= toExtract;
		}
		
		markDirty();
		
		return toExtract;
	}

	public long insertEnergy(long maxReceive, boolean simulate) {
		long toInsert = min(maxReceive, max-rf, perTick);
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
	@Deprecated
	public void setEnergy(long energy) {
		rf = energy;
	}

	private static class TeslaWrapper implements net.darkhax.tesla.api.ITeslaHolder, net.darkhax.tesla.api.ITeslaProducer, net.darkhax.tesla.api.ITeslaConsumer {
		private final EnergyStorage delegate;
		
		public TeslaWrapper(EnergyStorage delegate) {
			this.delegate = delegate;
		}
		
		@Override
		public long getStoredPower() {
			return delegate.getEnergy();
		}

		@Override
		public long getCapacity() {
			return delegate.getCapacity();
		}

		@Override
		public long takePower(long power, boolean simulated) {
			return delegate.extractEnergy(power, simulated);	
		}

		@Override
		public long givePower(long power, boolean simulated) {
			return delegate.insertEnergy(power, simulated);
		}
		
	}
	
	private static class RedstoneFluxWrapper implements cofh.api.energy.IEnergyHandler {
		private final EnergyStorage delegate;
		
		public RedstoneFluxWrapper(EnergyStorage delegate) {
			this.delegate = delegate;
		}
		
		@Override
		public int extractEnergy(EnumFacing from, int maxExtract, boolean simulate) {
			return satcast(delegate.extractEnergy(maxExtract, simulate));
		}

		@Override
		public boolean canConnectEnergy(EnumFacing from) {
			return true;
		}

		@Override
		public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
			return satcast(delegate.insertEnergy(maxReceive, simulate));
		}

		@Override
		public int getEnergyStored(EnumFacing from) {
			return satcast(delegate.getEnergy());
		}

		@Override
		public int getMaxEnergyStored(EnumFacing from) {
			return satcast(delegate.getCapacity());
		}
		
	}
	
	private static class CapabilityCoreWrapper implements gigaherz.capabilities.api.energy.IEnergyHandler {
		private final EnergyStorage delegate;
		
		public CapabilityCoreWrapper(EnergyStorage delegate) {
			this.delegate = delegate;
		}
		
		
		@Override
		public int getCapacity() {
			return satcast(delegate.getCapacity());
		}

		@Override
		public int getEnergy() {
			return satcast(delegate.getEnergy());
		}

		@Override
		public int extractEnergy(int maxExtract, boolean simulate) {
			return satcast(delegate.extractEnergy(maxExtract, simulate));
		}

		@Override
		public int insertEnergy(int maxReceive, boolean simulate) {
			return satcast(delegate.insertEnergy(maxReceive, simulate));
		}
		
	}
	
	/**
	 * Translate as much information as possible from a long into an int. For example, if the capacity
	 * or energy transfer rate of a storage device is larger than Integer.MAX_VALUE, we want to
	 * report as much storage or transfer as the integer-limited interface can handle.
	 * 
	 * <p>The name is short for "saturate and cast to int"
	 */
	private static int satcast(long i) {
		return (int)Math.max(Math.min(i, Integer.MAX_VALUE), Integer.MIN_VALUE);
	}
}
