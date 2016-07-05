package io.github.elytra.engination.block.te;

import cofh.api.energy.IEnergyConnection;
import io.github.elytra.engination.energy.EnergyStorage;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;

public class TileEntityMachineBase extends TileEntity implements IEnergyConnection, ITickable {

	public static int TICKS_BETWEEN_NETWORK_UPDATES = 20;
	private boolean isNetworkDirty = false;
	private int ticksUntilNetworkUpdate = TICKS_BETWEEN_NETWORK_UPDATES + (int)(Math.random()*20); //scatter out tile updates over an extra second or so to mitigate network heartbeat lags
	
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
	
	public long getEnergyForWaila() {
		return energy.getEnergy();
	}
	
	public long getEnergyCapacityForWaila() {
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
	
	
}
