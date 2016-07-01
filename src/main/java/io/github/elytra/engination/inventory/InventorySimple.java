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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;

import io.github.elytra.engination.Listener;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.items.ItemStackHandler;

public class InventorySimple extends ItemStackHandler implements IInventory {
	
	private List<Listener<ItemStackHandler>> changeListeners;
	private HashMap<Integer, Predicate<ItemStack>> stackValidators;
	private String name = "inventory";
	
	public InventorySimple() {
		this(1);
	}
	
	public InventorySimple(int numSlots) {
		super(numSlots);
		changeListeners = new ArrayList<>();
		stackValidators = new HashMap<>();
	}
	
	public InventorySimple(int numSlots, String name) {
		this(numSlots);
		this.name = name;
	}
	
	@Override
	public void onContentsChanged(int slot) {
		markDirty();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TextComponentTranslation(name);
	}

	@Override
	public int getSizeInventory() {
		return this.getSlots();
	}

	@Override
	public ItemStack decrStackSize(int index, int count) {
		return this.extractItem(index, count, false);
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		ItemStack result = this.getStackInSlot(index);
		this.setStackInSlot(index, null);
		return result;
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		this.setStackInSlot(index, stack);
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public void markDirty() {
		for(Listener<ItemStackHandler> listener : changeListeners) {
			listener.changed(this);
		}
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return true;
	}

	@Override
	public void openInventory(EntityPlayer player) {}

	@Override
	public void closeInventory(EntityPlayer player) {}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		//Vanilla implementations ignore whether there's a stack there already. It's more about agreeing with the stack's intent.
		if (stackValidators.containsKey(index)) {
			return stackValidators.get(index).test(stack);
		} else return true;
	}

	@Override
	public int getField(int id) {
		return 0;
	}

	@Override
	public void setField(int id, int value) {}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public void clear() {
		for (int i = 0; i < this.getSlots(); ++i) {
            this.setStackInSlot(i, null);
        }
	}

	public InventorySimple listen(Listener<ItemStackHandler> callback) {
		changeListeners.add(callback);
		return this;
	}
	
	public InventorySimple setStackValidator(int slotIndex, Predicate<ItemStack> validator) {
		//TODO: Consider composing with Predicate.and() if a validator already exists for that slot
		stackValidators.put(slotIndex, validator);
		return this;
	}
}
