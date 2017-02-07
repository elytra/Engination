package io.github.elytra.engination.item;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockCosmeticPillar extends ItemBlock {

	public ItemBlockCosmeticPillar(Block block) {
		super(block);
	}
	
	@Override
	public boolean getHasSubtypes() {
		return true;
	}
	
	@Override
	public int getMetadata(int damage) {
		return damage;
	}
	
	@Override
	public int getDamage(ItemStack stack) {
		return super.getDamage(stack);
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
        return this.block.getUnlocalizedName()+"."+(stack.getItemDamage()&0x3);
    }
}
