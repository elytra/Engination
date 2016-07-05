package io.github.elytra.engination.client.gui;

import java.util.List;

import io.github.elytra.engination.block.BlockMachineBase;
import io.github.elytra.engination.block.te.TileEntityMachineBase;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;

@Optional.Interface(iface = "mcp.mobius.waila.api.IWailaDataProvider", modid = "Waila")
public class EnergyWailaDataProvider implements IWailaDataProvider {

	@Optional.Method(modid = "Waila")
	public static void callbackRegister(final IWailaRegistrar registry) {
		EnergyWailaDataProvider instance = new EnergyWailaDataProvider();

		registry.registerBodyProvider(instance, BlockMachineBase.class);
	}
	
	@Override
	public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound nbt, World world, BlockPos pos) {
		return nbt;
	}

	@Override
	public List<String> getWailaBody(ItemStack item, List<String> body, IWailaDataAccessor data, IWailaConfigHandler config) {
		
		TileEntity te = data.getTileEntity();
		if (te instanceof TileEntityMachineBase) {
			String danks = "Danks: "+((TileEntityMachineBase)te).getEnergyForWaila()+" / "+((TileEntityMachineBase)te).getEnergyCapacityForWaila();
			body.add(danks);
		}
		
		return body;
	}

	@Override
	public List<String> getWailaHead(ItemStack stack, List<String> head, IWailaDataAccessor data, IWailaConfigHandler config) {
		return head;
	}

	@Override
	public ItemStack getWailaStack(IWailaDataAccessor data, IWailaConfigHandler arg1) {
		return null;
	}

	@Override
	public List<String> getWailaTail(ItemStack arg0, List<String> tail, IWailaDataAccessor arg2, IWailaConfigHandler arg3) {
		return tail;
	}
	
}
