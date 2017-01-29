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

package io.github.elytra.engination.block.te;

import java.util.HashMap;

//import cofh.api.energy.IEnergyProvider;
import io.github.elytra.engination.block.BlockGenerator;
import io.github.elytra.engination.block.EnginationBlocks;
import io.github.elytra.engination.energy.RedstoneFlux;
import io.github.elytra.engination.energy.RedstoneFluxAccess;
import io.github.elytra.engination.inventory.InventorySimple;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;

public class TileEntityGenerator extends TileEntityMachineBase implements ITickable {
	//public static final int CONVERSION_TICKS_TO_RF = 30; //1,600 ticks of coal -> 48,000 RF == 30 RF/t
	public static final int CONVERSION_TICKS_TO_RF = 1000; //For testing
	
	private final InventorySimple inventory = new InventorySimple(1, "tile.machine.generator.name")
			.listen((it)->this.markDirty())
			.setStackValidator(0, TileEntityFurnace::isItemFuel);
	
	private int fuelTicks = 0;
	
	private HashMap<EnumFacing, TileEntity> verification = new HashMap<>();
	private HashMap<EnumFacing, RedstoneFluxAccess> localAccess = new HashMap<>();
	
	public TileEntityGenerator() {
		this.capabilityRegistry.registerProviderForAllSides(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, ()->inventory);
		
		//energy Capabilities
		if (RedstoneFlux.CAPABILITY_CORE_ENERGY!=null) {
			capabilityRegistry.registerProviderForAllSides(RedstoneFlux.CAPABILITY_CORE_ENERGY, energy::getCapabilityCoreProviderWrapper);
		}
		if (RedstoneFlux.TESLA_ENERGY_PRODUCER!=null) {
			capabilityRegistry.registerProviderForAllSides(RedstoneFlux.TESLA_ENERGY_PRODUCER, energy::getTeslaProducerWrapper);
		}
		if (RedstoneFlux.TESLA_ENERGY_STORAGE!=null) {
			capabilityRegistry.registerProviderForAllSides(RedstoneFlux.TESLA_ENERGY_STORAGE, energy::getTeslaHolderWrapper);
		}
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		
		tag.setTag("inventory", inventory.serializeNBT());
		//tag.setLong("rf", energy.getEnergy());
		tag.setInteger("fuelTicks", fuelTicks);
		
		return tag;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		
		inventory.deserializeNBT(tag.getCompoundTag("inventory"));
		
		//energy.setEnergy(tag.getLong("rf"));
		fuelTicks = tag.getInteger("fuelTicks");
	}
	
	
	
	//@Override
	//public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		/*if (capability==null) return false;
		
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ||
			capability == RedstoneFlux.CAPABILITY_CORE_ENERGY ||
			capability == RedstoneFlux.TESLA_ENERGY_PRODUCER) {
			return true;
		}*/
		
		//return super.hasCapability(capability, facing);
	//}
	
	//@SuppressWarnings("unchecked")
	//@Override
	//public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		/*
		if (capability==null) return null;
		
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return (T) inventory;
		}
		if (capability == RedstoneFlux.CAPABILITY_CORE_ENERGY) {
			return (T) energy.getCapabilityCoreWrapper();
		}
		
		if (capability == RedstoneFlux.TESLA_ENERGY_PRODUCER) {
			return (T) energy.getTeslaWrapper();
		}
		*/
		//return super.getCapability(capability, facing);
	//}
	
	
	public boolean isFuel(ItemStack stack) {
		return true;
	}
	
	public InventorySimple getInventory() {
		return inventory;
	}

	private void sendUpdatePacket() {
		IBlockState curState = world.getBlockState(pos);
		world.notifyBlockUpdate(pos, curState, curState, 6);
	}
	
	@Override
	public void update() {
		super.update();
		
		if (this.world==null || this.world.isRemote) return;
		
		long startEnergy = energy.getEnergy();
		for(EnumFacing side : EnumFacing.values()) {
			if (energy.getEnergy()<=0) break;
			pushEnergy(side);
		}
		if (energy.getEnergy()!=startEnergy) sendUpdatePacket();
		
		if (fuelTicks>0) {
			if (energy.getEnergy()+CONVERSION_TICKS_TO_RF < energy.getCapacity()) {
				//Keep calm and burn the universe down until there's nothing left but choking clouds of ash.
				setOn(true);
				fuelTicks--;
				energy.insertEnergy(CONVERSION_TICKS_TO_RF, false);
				this.markDirty();
				//sendUpdatePacket();
				return;
			} else {
				//Machine is full.
				setOn(false);
				return;
			}
		} else {
			//Load a single fuel item into the fuelTicks buffer
			ItemStack oneFuel = inventory.extractItem(0, 1, false); //Pull one fuel item from the zeroth slot
			
			if (oneFuel==null || oneFuel.stackSize<=0) {
				//TODO: Adding the fuel back into the itemslot has the slight chance to dupe or overstack.
				//Because of this, we're instead eating the fuel for no gain. It should be impossible to
				//arrive here, but nonetheless we need to keep considering the consequences of destroying
				//the item and search for better alternatives.
				
				setOn(false);
				return;
			} else {
				int ticksForOneFuel = TileEntityFurnace.getItemBurnTime(oneFuel);
				fuelTicks += ticksForOneFuel;
				setOn(true);
				this.markDirty();
				
				return;
			}
			
		}
	}
	
	public void pushEnergy(EnumFacing side) {
		BlockPos neighbor = pos.offset(side);
		TileEntity te = world.getTileEntity(neighbor);
		if (te==null) return;
		if (te!=verification.get(side)) {
			verification.put(side, te);
			localAccess.put(side, RedstoneFlux.getAccess(world, neighbor, side.getOpposite()));
		}
		
		RedstoneFluxAccess access = localAccess.get(side);
		if (access==RedstoneFlux.NULL_ACCESS) return;
		
		long simulatedEnergyRemoved = access.insertEnergy(energy.getEnergy(), true);
		if (simulatedEnergyRemoved<=0) return;
		long pulledFromReserves = energy.extractEnergy(simulatedEnergyRemoved, false);
		long actualEnergyRemoved = access.insertEnergy(pulledFromReserves, false);
		//this.markDirty(); not really necessary since EnergyStorage does that for us via callback
		if (actualEnergyRemoved!=pulledFromReserves) {
			//TODO: Ideally, mark this tile visually as malfunctioning via IBlockState.
			//There's not much I could do if this turned out to be dropping RF, but it'd be nice to know.
		}
	}
	
	public void setOn(boolean on) {
		EnginationBlocks.GENERATOR.setOn(world, pos, on);
	}
	
	public boolean isOn() {
		return world.getBlockState(pos).getValue(BlockGenerator.PROPERTY_ON);
	}
	
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
		if (oldState.getBlock().equals(EnginationBlocks.GENERATOR) && newState.getBlock().equals(EnginationBlocks.GENERATOR)) return false;
		return super.shouldRefresh(world, pos, oldState, newState);
	}
}
