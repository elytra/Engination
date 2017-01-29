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

package io.github.elytra.engination.client.gui;

import java.util.List;

import io.github.elytra.engination.block.BlockMachineBase;
import io.github.elytra.engination.block.te.TileEntityMachineBase;
//import mcp.mobius.waila.api.IWailaConfigHandler;
//import mcp.mobius.waila.api.IWailaDataAccessor;
//import mcp.mobius.waila.api.IWailaDataProvider;
//import mcp.mobius.waila.api.IWailaRegistrar;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;

@Optional.Interface(iface = "mcp.mobius.waila.api.IWailaDataProvider", modid = "Waila")
public class EnergyWailaDataProvider /*implements IWailaDataProvider*/ {

	//@Optional.Method(modid = "Waila")
	//public static void callbackRegister(final IWailaRegistrar registry) {
	//	EnergyWailaDataProvider instance = new EnergyWailaDataProvider();

	//	registry.registerBodyProvider(instance, BlockMachineBase.class);
	//}
	/*
	//@Override
	public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound nbt, World world, BlockPos pos) {
		return nbt;
	}

	@SuppressWarnings("deprecation")
	@Override
	public List<String> getWailaBody(ItemStack item, List<String> body, IWailaDataAccessor data, IWailaConfigHandler config) {
		
		TileEntity te = data.getTileEntity();
		if (te instanceof TileEntityMachineBase) {
			String danks = "Danks: "+((TileEntityMachineBase)te).getEnergyInternal()+" / "+((TileEntityMachineBase)te).getEnergyCapacityInternal();
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
	}*/
	
}
