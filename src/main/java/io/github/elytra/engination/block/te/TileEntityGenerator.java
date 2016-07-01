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

import cofh.api.energy.IEnergyProvider;
import io.github.elytra.engination.Engination;
import io.github.elytra.engination.block.BlockGenerator;
import io.github.elytra.engination.block.EnginationBlocks;
import io.github.elytra.engination.energy.EnergyStorage;
import io.github.elytra.engination.inventory.InventorySimple;
import net.minecraft.block.state.IBlockState;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;

public class TileEntityGenerator extends TileEntity implements IEnergyProvider, ITickable {
	public static final int CONVERSION_TICKS_TO_RF = 30; //1,600 ticks of coal -> 48,000 RF == 30 RF/t
	
	final InventorySimple inventory = new InventorySimple(1, "tile.machine.generator.name")
			.listen((it)->this.markDirty())
			.setStackValidator(0, TileEntityFurnace::isItemFuel); //TODO: May explode
	
	final EnergyStorage energy = new EnergyStorage(50000, 30)
			.listen((it)->this.markDirty());
	
	//private int maxRf = 50000;
	//private int rf = 0;
	private int fuelTicks = 0;
	
	public TileEntityGenerator() {}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		
		tag.setTag("inventory", inventory.serializeNBT());
		tag.setInteger("rf", energy.getEnergy());
		tag.setInteger("fuelTicks", fuelTicks);
		
		return tag;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		
		inventory.deserializeNBT(tag.getCompoundTag("inventory"));
		
		energy.setEnergy(tag.getInteger("rf"));
		fuelTicks = tag.getInteger("fuelTicks");
	}
	
	
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return true;
		}
		return super.hasCapability(capability, facing);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return (T) inventory;
		}
		return super.getCapability(capability, facing);
	}
	
	
	public boolean isFuel(ItemStack stack) {
		return true;
	}

	@Override
	public boolean canConnectEnergy(EnumFacing from) {
		return true; //Accept cables from all sides
	}

	@Override
	public int extractEnergy(EnumFacing from, int maxExtract, boolean simulate) {
		return energy.extractEnergy(from, maxExtract, simulate);
		/*
		int toExtract = maxExtract;
		if (toExtract>energy.getEnergy()) toExtract = rf;
		if (simulate) return toExtract;
		
		rf -= toExtract;
		return toExtract;*/
	}

	@Override
	public int getEnergyStored(EnumFacing from) {
		return energy.getEnergyStored(from);
		//return rf;
	}

	@Override
	public int getMaxEnergyStored(EnumFacing from) {
		return energy.getMaxEnergyStored(from);
		//return maxRf;
	}
	
	public IInventory getInventory() {
		return inventory;
	}

	@Override
	public void update() {
		if (this.worldObj==null || this.worldObj.isRemote) return;
		
		if (fuelTicks>0) {
			if (energy.getEnergy()+CONVERSION_TICKS_TO_RF < energy.getCapacity()) {
				//Keep calm and burn the universe down until there's nothing left but choking clouds of ash.
				setOn(true);
				fuelTicks--;
				energy.insertEnergy(CONVERSION_TICKS_TO_RF, false);
				//Engination.LOG.info("BurnTick. New RF: "+rf);
				this.markDirty();
				return;
			} else {
				//Machine is full. Indicate stop to user.
				setOn(false);
				//Engination.LOG.info("Machine is full. Stopping.");
				return;
			}
		} else {
			//Load a single fuel item into the fuelTicks buffer
			
			//ItemStack fuelSlot = inventory.getStackInSlot(0);
			//ItemStack slotStack = inventory.getStackInSlot(0);
			//if (slotStack!=null) Engination.LOG.info("Pulling one item from ["+slotStack.getDisplayName()+" x"+slotStack.stackSize+"]");
			ItemStack oneFuel = inventory.extractItem(0, 1, false); //Pull one fuel item from the zeroth slot
			
			
			
			if (oneFuel==null || oneFuel.stackSize<=0) {
				//TODO: Adding the fuel back into the itemslot has the slight chance to dupe or overstack.
				//Because of this, we're instead eating the fuel for no gain. It should be impossible to
				//arrive here, but nonetheless we need to keep considering the consequences of destroying
				//the item and search for better alternatives.
				//if (isOn()) {
					//System.out.println("Ran out of fuel! Stopping.");
				//}
				
				
				setOn(false);
				return;
			} else {
				//slotStack = inventory.getStackInSlot(0);
				//if (slotStack==null) Engination.LOG.info("None left.");
				//else Engination.LOG.info("Remaining ["+slotStack.getDisplayName()+" x"+slotStack.stackSize+"]");
				
				int ticksForOneFuel = TileEntityFurnace.getItemBurnTime(oneFuel);
				
				//Engination.LOG.info("Burning "+oneFuel.getDisplayName()+" for "+ticksForOneFuel+" ticks.");
				
				fuelTicks += ticksForOneFuel;
				setOn(true);
				//inventory.markDirty();
				this.markDirty();
				
				return;
			}
			
		}
		
	}
	
	public void setOn(boolean on) {
		EnginationBlocks.GENERATOR.setOn(worldObj, pos, on);
	}
	
	public boolean isOn() {
		return worldObj.getBlockState(pos).getValue(BlockGenerator.PROPERTY_ON);
	}
	
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
		if (oldState.getBlock().equals(EnginationBlocks.GENERATOR) && newState.getBlock().equals(EnginationBlocks.GENERATOR)) return false;
		return super.shouldRefresh(world, pos, oldState, newState);
	}
}
