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

package io.github.elytra.engination.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerSimple extends Container {

	InventoryPlayer playerInventory;
	IInventory blockInventory;
	
	public ContainerSimple(InventoryPlayer playerInventory, IInventory blockInventory, EntityPlayer player) {
        this.blockInventory = blockInventory;
        blockInventory.openInventory(player);
        this.playerInventory = playerInventory;
        int slotSize = 18;
        int fiftyOne = 51;
        

        //**Measured: 44 is the left coordinate of the slot grid, with 18 horizontal distance (16 of which is itemstack space)
        //**Measured: 20 is the top coordinate of the slot grid, with 18 vertical distance (16 of which is itemstack space)
        
        
        //44 is the start (left edge) of the inventory slot grid??
        //20 is the start (top edge) of the grid??
        for (int i = 0; i < blockInventory.getSizeInventory(); ++i) {
            this.addSlotToContainer(new SlotValidated(blockInventory, i, 44 + i * 18, 20));
        }

        //player bag
        for (int y = 0; y < 3; ++y) {
            for (int x = 0; x < 9; ++x) {
            	//grid here starts at (9, 51+8)
                this.addSlotToContainer(new Slot(playerInventory, x + y * 9 + 9, 8 + x * slotSize, y * slotSize + fiftyOne));
            }
        }
        //player hotbar
        for (int i = 0; i < 9; ++i) {
        	//grid here starts at (8, 51+58)
            this.addSlotToContainer(new Slot(playerInventory, i, 8 + i * slotSize, 58 + fiftyOne));
        }
        
        //System.out.println("Container Open: "+this.getSlot(0).getStack());
    }
	
	
	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return true; //TODO: Prevent long-distance access
	}
	
	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int index) {
		//System.out.println("TransferStackInSlot");
		ItemStack itemstack = null;
		Slot slot = (Slot) this.inventorySlots.get(index);

		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();

			int minPlayerSlot = inventorySlots.size() - playerInventory.mainInventory.length;
			if (index < minPlayerSlot) {
				if (!this.mergeItemStack(itemstack1, minPlayerSlot, this.inventorySlots.size(), true)) {
					return null;
				}
			} else if (!this.mergeItemStack(itemstack1, 0, minPlayerSlot, false)) {
				return null;
			}

			if (itemstack1.stackSize == 0) {
				slot.putStack((ItemStack) null);
			} else {
				slot.onSlotChanged();
			}
		}

		return itemstack;
	}
	
	public static boolean canAddItemToSlot(Slot slotIn, ItemStack stack, boolean stackSizeMatters) {
		//System.out.println("CanAddItem");
		//Diagnostics suggest this method is basically never called?
		if (slotIn==null || !slotIn.getHasStack()) {
			if (slotIn==null) return true;
			else return slotIn.isItemValid(stack);
		} else {
	        boolean flag = slotIn == null || !slotIn.getHasStack();
	
	        if (slotIn != null && slotIn.getHasStack() && stack != null && stack.isItemEqual(slotIn.getStack()) && ItemStack.areItemStackTagsEqual(slotIn.getStack(), stack))
	        {
	            flag |= slotIn.getStack().stackSize + (stackSizeMatters ? 0 : stack.stackSize) <= stack.getMaxStackSize();
	        }
	
	        return flag;
		}
    }
	
	protected boolean mergeItemStack(ItemStack stack, int startIndex, int endIndex, boolean reverseDirection) {
		//System.out.println("MergeItemStack");
		return super.mergeItemStack(stack, startIndex, endIndex, reverseDirection);
	}
	
	public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player) {
		//System.out.println("SlotClick dragtype:"+dragType+" clicktype:"+clickTypeIn+" slotId:"+slotId);
		
		if (clickTypeIn == ClickType.PICKUP && dragType == 0) {
			Slot slot = inventorySlots.get(slotId);
			ItemStack held = playerInventory.getItemStack();
			
			if (slot==null) return super.slotClick(slotId, dragType, clickTypeIn, player);
			
			if (held!=null && !slot.getHasStack()) {
				if (!slot.isItemValid(held)) {
					playerInventory.setItemStack(held);
					slot.putStack(null);
					
					return held;
				}
			}
		}
		
		
		
		return super.slotClick(slotId, dragType, clickTypeIn, player);
	}
	
	@Override
	public void onContainerClosed(EntityPlayer player) {
		super.onContainerClosed(player);
		//System.out.println("Container closed! Remaining: ["+this.getSlot(0).getStack()+"]");
		this.blockInventory.markDirty();
		
	}
	
	/*
	//Mostly for shiftclick, but also useful for automation
	protected boolean mergeItemStack(ItemStack stack, int startIndex, int endIndex, boolean reverseDirection) {
		boolean merged = false;
		int i = startIndex;

		if (reverseDirection) {
			i = endIndex - 1;
		}

		if (stack.isStackable()) {
			//Try to stack into a slot already containing that item
			while (stack.stackSize > 0 && (!reverseDirection && i < endIndex || reverseDirection && i >= startIndex)) {
				Slot slot = (Slot) this.inventorySlots.get(i);
				
				ItemStack itemstack = slot.getStack();

				if (itemstack != null && areItemStacksEqual(stack, itemstack)) {
					int combinedSize = itemstack.stackSize + stack.stackSize;

					if (combinedSize <= stack.getMaxStackSize()) {
						stack.stackSize = 0;
						itemstack.stackSize = combinedSize;
						slot.onSlotChanged();
						merged = true;
					} else if (itemstack.stackSize < stack.getMaxStackSize()) {
						stack.stackSize -= stack.getMaxStackSize() - itemstack.stackSize;
						itemstack.stackSize = stack.getMaxStackSize();
						slot.onSlotChanged();
						merged = true;
					}
				}

				if (reverseDirection) {
					--i;
				} else {
					++i;
				}
			}
		}

		if (stack.stackSize > 0) {
			if (reverseDirection) {
				i = endIndex - 1;
			} else {
				i = startIndex;
			}

			//Place in the first free slot
			while (!reverseDirection && i < endIndex || reverseDirection && i >= startIndex) {
				Slot slot = (Slot) this.inventorySlots.get(i);
				ItemStack itemstack = slot.getStack();

				if (itemstack == null && slot.isItemValid(stack)) {
					slot.putStack(stack.copy());
					slot.onSlotChanged();
					stack.stackSize = 0;
					merged = true;
					break;
				}

				if (reverseDirection) {
					--i;
				} else {
					++i;
				}
			}
		}

		return merged;
	}

	
	private static boolean areItemStacksEqual(ItemStack stackA, ItemStack stackB) {
        return stackB.getItem() == stackA.getItem() && (!stackA.getHasSubtypes() || stackA.getMetadata() == stackB.getMetadata()) && ItemStack.areItemStackTagsEqual(stackA, stackB);
    }*/
}
