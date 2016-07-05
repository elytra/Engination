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

import cofh.api.energy.IEnergyConnection;
import io.github.elytra.engination.CapabilityRegistryBlocks;
import io.github.elytra.engination.energy.EnergyStorage;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;

public class TileEntityMachineBase extends TileEntity implements IEnergyConnection, ITickable {

	public static int TICKS_BETWEEN_NETWORK_UPDATES = 20;
	private boolean isNetworkDirty = false;
	private int ticksUntilNetworkUpdate = TICKS_BETWEEN_NETWORK_UPDATES + (int)(Math.random()*20); //scatter out tile updates over an extra second or so to mitigate network heartbeat lags
	
	protected CapabilityRegistryBlocks capabilityRegistry = new CapabilityRegistryBlocks();
	
	protected final EnergyStorage energy = new EnergyStorage(50000, 30, 30)
			.listen((it)->{
				this.markDirty();
				isNetworkDirty = true;
			});
	
	@Override
	public boolean canConnectEnergy(EnumFacing from) {
		return true;
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		
		tag.setLong("rf", energy.getEnergy());
		
		return tag;
	}
	
	/**
	 * @deprecated This is for Waila and preserving stored Dank when broken
	 */
	@Deprecated
	public long getEnergyInternal() {
		return energy.getEnergy();
	}
	
	/**
	 * @deprecated This is for Waila and preserving stored Dank when broken
	 */
	@Deprecated
	public long getEnergyCapacityInternal() {
		return energy.getCapacity();
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		
		energy.setEnergy(tag.getLong("rf"));
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
		 NBTTagCompound tag = packet.getNbtCompound();
		 this.energy.setEnergy(tag.getLong("rf"));
	}
	 
	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		//NBTTagCompound tag = new NBTTagCompound();
		
		//writeToNBT(tag);
		
		return new SPacketUpdateTileEntity(pos, getBlockMetadata(), getUpdateTag());
		
	}
	
	public NBTTagCompound getUpdateTag() {
		NBTTagCompound tag = super.getUpdateTag();
		writeToNBT(tag);
		return tag;
	}

	@Override
	public void update() {
		if (worldObj.isRemote) return;
		
		if (isNetworkDirty) {
			ticksUntilNetworkUpdate--;
			if (ticksUntilNetworkUpdate<=0) {
				IBlockState curState = worldObj.getBlockState(pos);
				worldObj.notifyBlockUpdate(pos, curState, curState, 3);
				isNetworkDirty = false;
				ticksUntilNetworkUpdate = TICKS_BETWEEN_NETWORK_UPDATES;
			}
		}
	}
	
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing side) {
		return capabilityRegistry.hasCapability(capability, side);
	}
	
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing side) {
		return capabilityRegistry.getCapability(capability, side);
	}
}
