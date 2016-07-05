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

package io.github.elytra.engination;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

import com.google.common.collect.Lists;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

/**
 * Provides Capabilities on behalf of a TileEntity. The tile can register *providers* conditionally when the corresponding
 * capabilities are available, and the Provider's results will be automatically memoized and shared between faces that
 * support it. This eases the burden on providing complex patterns of side-sensitive wrapper interfaces.
 */
public class CapabilityRegistryBlocks {
	private List<RegistryEntry<?>> entries = Lists.newArrayList();
	
	public <T> void registerProviderForAllSides(Capability<T> capability, Callable<T> provider) {
		entries.add(new RegistryEntry<T>(capability, provider, EnumFacing.values()));
	}
	
	public <T> void registerProviderForSides(Capability<T> capability, Callable<T> provider, EnumFacing... applicableSides) {
		entries.add(new RegistryEntry<T>(capability, provider, applicableSides));
	}
	
	public boolean hasCapability(Capability<?> capability, EnumFacing side) {
		if (capability==null) return false;
		for(RegistryEntry<?> entry : entries) {
			if (entry.provides(capability, side)) return true;
		}
		
		return false;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getCapability(Capability<T> capability, EnumFacing side) {
		if (capability==null) return null;
		for(RegistryEntry<?> entry : entries) {
			//TODO: The following is a really shitty way to narrow from ? down to T.
			//It'll work, but not in the strong-type-safety way that would give me
			//confidence in data integrity down the road.
			if (entry.provides(capability, side)) return ((RegistryEntry<T>)entry).provide(capability, side);
		}

		return null;
	}
	
	public static class RegistryEntry<T> {
		private Capability<T> capability;
		private Callable<T> provider;
		private T providedObject = null;
		private List<EnumFacing> validSides;
		
		public RegistryEntry(Capability<T> capability, Callable<T> provider, EnumFacing... validSides) {
			this.capability = capability;
			this.provider = provider;
			this.validSides = Arrays.asList(validSides);
		}
		
		public T provide(Capability<T> theCapability, EnumFacing side) {
			if (theCapability!=capability) return null;
			
			try {
				if (providedObject==null) providedObject = provider.call();
				
				if (side==null || validSides.contains(side)) {
					return providedObject;
				} else {
					return null;
				}
			} catch (Exception ex) {
				return null;
			}
		}
		
		public boolean provides(Capability<?> capability, EnumFacing side) {
			return (capability==this.capability) &&
					validSides.contains(side);
		}
	}
}
