package io.github.elytra.engination.block.te;

import cofh.api.energy.IEnergyConnection;
import io.github.elytra.engination.energy.EnergyStorage;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

public class TileEntityMachineBase extends TileEntity implements IEnergyConnection {

	protected final EnergyStorage energy = new EnergyStorage(50000, 30, 30)
			.listen((it)->this.markDirty());
	
	@Override
	public boolean canConnectEnergy(EnumFacing from) {
		return true;
	}
	
}
