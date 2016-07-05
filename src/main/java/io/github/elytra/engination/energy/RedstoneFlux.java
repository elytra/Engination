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

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class RedstoneFlux {
	public static final RedstoneFluxAccess NULL_ACCESS = new RedstoneFluxAccess.NullWrapper();
	
	public static Capability<gigaherz.capabilities.api.energy.IEnergyHandler> CAPABILITY_CORE_ENERGY = null;
	public static Capability<net.darkhax.tesla.api.ITeslaHolder> TESLA_ENERGY_STORAGE = null;
	public static Capability<net.darkhax.tesla.api.ITeslaProducer> TESLA_ENERGY_PRODUCER = null;
	public static Capability<net.darkhax.tesla.api.ITeslaConsumer> TESLA_ENERGY_CONSUMER = null;
	
	@CapabilityInject(gigaherz.capabilities.api.energy.IEnergyHandler.class)
	public static void capabilityCoreStorageCallback(Capability<gigaherz.capabilities.api.energy.IEnergyHandler> cap) {
		CAPABILITY_CORE_ENERGY = cap;
	}
	
	@CapabilityInject(net.darkhax.tesla.api.ITeslaHolder.class)
	public static void teslaStorageCallback(Capability<net.darkhax.tesla.api.ITeslaHolder> cap) {
		TESLA_ENERGY_STORAGE = cap;
	}
	
	@CapabilityInject(net.darkhax.tesla.api.ITeslaProducer.class)
	public static void teslaProducerCallback(Capability<net.darkhax.tesla.api.ITeslaProducer> cap) {
		TESLA_ENERGY_PRODUCER = cap;
	}
	
	@CapabilityInject(net.darkhax.tesla.api.ITeslaConsumer.class)
	public static void teslaConsumerCallback(Capability<net.darkhax.tesla.api.ITeslaConsumer> cap) {
		TESLA_ENERGY_CONSUMER = cap;
	}
	
	public static RedstoneFluxAccess getAccess(IBlockAccess world, BlockPos pos, EnumFacing side) {
		TileEntity te = world.getTileEntity(pos);
		if (te==null) return NULL_ACCESS;
		
		//Prefer tesla because of datatype concerns
		if (TESLA_ENERGY_STORAGE!=null) {
			boolean hasStorage = te.hasCapability(TESLA_ENERGY_STORAGE,  side);
			boolean hasProducer = te.hasCapability(TESLA_ENERGY_PRODUCER, side);
			boolean hasConsumer = te.hasCapability(TESLA_ENERGY_CONSUMER, side);
			if (hasStorage || hasProducer || hasConsumer) {
				
				return new RedstoneFluxAccess.TeslaWrapper(te, side);
			}
		}
		
		//Closest to native access
		if (CAPABILITY_CORE_ENERGY!=null) {
			if (te.hasCapability(CAPABILITY_CORE_ENERGY, side)) {
				return new RedstoneFluxAccess.CapabilityWrapper(
						te.getCapability(CAPABILITY_CORE_ENERGY, side)
						);
			}
		}
		
		if (te instanceof cofh.api.energy.IEnergyConnection) {
			return new RedstoneFluxAccess.FluxWrapper(te, side);
		}
		
		
		return NULL_ACCESS;
	}
	
	
	public long getEnergy(IBlockAccess world, BlockPos pos) {
		return getEnergy(world, pos, null); //Null is valid for the side argument!
	}
	
	public long getEnergy(IBlockAccess world, BlockPos pos, EnumFacing side) {
		TileEntity te = world.getTileEntity(pos);
		
		//Prefer tesla because of datatype concerns
		if (TESLA_ENERGY_STORAGE!=null) {
			if (te.hasCapability(TESLA_ENERGY_STORAGE, side)) {
				return te.getCapability(TESLA_ENERGY_STORAGE, side).getStoredPower();
			}
		}
		
		if (CAPABILITY_CORE_ENERGY!=null) {
			if (te.hasCapability(CAPABILITY_CORE_ENERGY, side)) {
				return te.getCapability(CAPABILITY_CORE_ENERGY, side).getEnergy();
			}
		}
		
		
		
		return 0;
	}
}
