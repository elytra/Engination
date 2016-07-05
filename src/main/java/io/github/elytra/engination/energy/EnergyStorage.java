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
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;

import io.github.elytra.engination.Listener;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.Optional;

public class EnergyStorage {
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

	public void markDirty() {
		for(Listener<EnergyStorage> l : listeners) {
			l.changed(this);
		}
	}
	
	public EnergyStorage listen(Listener<EnergyStorage> l) {
		listeners.add(l);
		return this;
	}
	
	@Optional.Method(modid = "CapabilityCore")
	public gigaherz.capabilities.api.energy.IEnergyHandler getCapabilityCoreWrapper() {
		return new CapabilityCoreWrapper(this);
	}
	
	@Optional.Method(modid = "CapabilityCore")
	public gigaherz.capabilities.api.energy.IEnergyHandler getCapabilityCoreProviderWrapper() {
		return new CapabilityCoreProviderWrapper(this);
	}
	
	@Optional.Method(modid = "CapabilityCore")
	public gigaherz.capabilities.api.energy.IEnergyHandler getCapabilityCoreConsumerWrapper() {
		return new CapabilityCoreConsumerWrapper(this);
	}
	
	public cofh.api.energy.IEnergyHandler getRedstoneFluxWrapper() {
		return new RedstoneFluxWrapper(this);
	}
	
	@Optional.Method(modid = "Tesla")
	public net.darkhax.tesla.api.ITeslaHolder getTeslaWrapper() {
		return new TeslaWrapper(this);
	}
	
	@Optional.Method(modid = "Tesla")
	public net.darkhax.tesla.api.ITeslaProducer getTeslaProducerWrapper() {
		return new TeslaProducerWrapper(this);
	}
	
	@Optional.Method(modid = "Tesla")
	public net.darkhax.tesla.api.ITeslaConsumer getTeslaConsumerWrapper() {
		return new TeslaConsumerWrapper(this);
	}
	
	@Optional.Method(modid = "Tesla")
	public net.darkhax.tesla.api.ITeslaHolder getTeslaHolderWrapper() {
		return new TeslaHolderWrapper(this);
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
		long toExtract = Longs.min(maxExtract, rf, outPerTick);
		if (toExtract<=0) return 0;
		
		if (!simulate) {
			rf -= toExtract;
			markDirty();
		}
		
		return toExtract;
	}

	public long insertEnergy(long maxReceive, boolean simulate) {
		if (maxReceive<=0) return 0;
		long freeSpace = max-rf;
		if (freeSpace<=0) return 0;
		
		long toInsert = Longs.min(maxReceive, freeSpace, inPerTick);
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
	
	@Optional.InterfaceList ({
			@Optional.Interface(iface = "net.darkhax.tesla.api.ITeslaHolder", modid = "Tesla"),
			@Optional.Interface(iface = "net.darkhax.tesla.api.ITeslaProducer", modid = "Tesla"),
			@Optional.Interface(iface = "net.darkhax.tesla.api.ITeslaConsumer", modid = "Tesla")
	})
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
	
	@Optional.Interface(iface = "net.darkhax.tesla.api.ITeslaProducer", modid = "Tesla")
	private static class TeslaProducerWrapper implements net.darkhax.tesla.api.ITeslaProducer {
		private final EnergyStorage delegate;
		
		public TeslaProducerWrapper(EnergyStorage delegate) {
			this.delegate = delegate;
		}
	
		@Override
		public long takePower(long power, boolean simulated) {
			return delegate.extractEnergy(power, simulated);	
		}
	}
	
	@Optional.Interface(iface = "net.darkhax.tesla.api.ITeslaHolder", modid = "Tesla")
	private static class TeslaHolderWrapper implements net.darkhax.tesla.api.ITeslaHolder {
		private final EnergyStorage delegate;
		
		public TeslaHolderWrapper(EnergyStorage delegate) {
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
	}
	

	@Optional.Interface(iface = "net.darkhax.tesla.api.ITeslaConsumer", modid = "Tesla")
	private static class TeslaConsumerWrapper implements net.darkhax.tesla.api.ITeslaConsumer {
		private final EnergyStorage delegate;
		
		public TeslaConsumerWrapper(EnergyStorage delegate) {
			this.delegate = delegate;
		}
	
		@Override
		public long givePower(long power, boolean simulated) {
			return delegate.insertEnergy(power, simulated);
		}
	}
	
	//@Optional.Interface(iface = "cofh.api.energy.IEnergyHandler", modid = "CoFHAPI")
	//^ This probably won't work
	private static class RedstoneFluxWrapper implements cofh.api.energy.IEnergyHandler {
		private final EnergyStorage delegate;
		
		public RedstoneFluxWrapper(EnergyStorage delegate) {
			this.delegate = delegate;
		}
		
		@Override
		public int extractEnergy(EnumFacing from, int maxExtract, boolean simulate) {
			return Ints.saturatedCast(delegate.extractEnergy(maxExtract, simulate));
		}

		@Override
		public boolean canConnectEnergy(EnumFacing from) {
			return true;
		}

		@Override
		public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
			return Ints.saturatedCast(delegate.insertEnergy(maxReceive, simulate));
		}

		@Override
		public int getEnergyStored(EnumFacing from) {
			return Ints.saturatedCast(delegate.getEnergy());
		}

		@Override
		public int getMaxEnergyStored(EnumFacing from) {
			return Ints.saturatedCast(delegate.getCapacity());
		}
		
	}
	
	@Optional.Interface(iface = "gigaherz.capabilities.api.energy.IEnergyHandler", modid = "CapabilityCore")
	//TODO: This mod would be fairly friendly to link against more weakly, if I linked against it via gradle instead.
	private static class CapabilityCoreWrapper implements gigaherz.capabilities.api.energy.IEnergyHandler {
		private final EnergyStorage delegate;
		
		public CapabilityCoreWrapper(EnergyStorage delegate) {
			this.delegate = delegate;
		}
		
		@Override
		public int getCapacity() {
			return Ints.saturatedCast(delegate.getCapacity());
		}

		@Override
		public int getEnergy() {
			return Ints.saturatedCast(delegate.getEnergy());
		}

		@Override
		public int extractEnergy(int maxExtract, boolean simulate) {
			return Ints.saturatedCast(delegate.extractEnergy(maxExtract, simulate));
		}

		@Override
		public int insertEnergy(int maxReceive, boolean simulate) {
			return Ints.saturatedCast(delegate.insertEnergy(maxReceive, simulate));
		}
		
	}
	
	@Optional.Interface(iface = "gigaherz.capabilities.api.energy.IEnergyHandler", modid = "CapabilityCore")
	private static class CapabilityCoreProviderWrapper implements gigaherz.capabilities.api.energy.IEnergyHandler {
		private final EnergyStorage delegate;
		
		public CapabilityCoreProviderWrapper(EnergyStorage delegate) {
			this.delegate = delegate;
		}
		
		@Override
		public int getCapacity() {
			return Ints.saturatedCast(delegate.getCapacity());
		}

		@Override
		public int getEnergy() {
			return Ints.saturatedCast(delegate.getEnergy());
		}

		@Override
		public int extractEnergy(int maxExtract, boolean simulate) {
			return Ints.saturatedCast(delegate.extractEnergy(maxExtract, simulate));
		}

		@Override
		public int insertEnergy(int maxReceive, boolean simulate) {
			return 0;
		}
	}
	
	@Optional.Interface(iface = "gigaherz.capabilities.api.energy.IEnergyHandler", modid = "CapabilityCore")
	private static class CapabilityCoreConsumerWrapper implements gigaherz.capabilities.api.energy.IEnergyHandler {
		private final EnergyStorage delegate;
		
		public CapabilityCoreConsumerWrapper(EnergyStorage delegate) {
			this.delegate = delegate;
		}
		
		@Override
		public int getCapacity() {
			return Ints.saturatedCast(delegate.getCapacity());
		}

		@Override
		public int getEnergy() {
			return Ints.saturatedCast(delegate.getEnergy());
		}

		@Override
		public int extractEnergy(int maxExtract, boolean simulate) {
			return 0;
		}

		@Override
		public int insertEnergy(int maxReceive, boolean simulate) {
			return Ints.saturatedCast(delegate.insertEnergy(maxReceive, simulate));
		}
	}
}
