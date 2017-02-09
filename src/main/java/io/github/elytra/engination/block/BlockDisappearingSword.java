/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2017 Isaac Ellingson (Falkreon)
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
package io.github.elytra.engination.block;

import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockDisappearingSword extends BlockDisappearing {
	public static final ChainReactionType CHAINTYPE_WEAPON = new ChainReactionType();
	
	public BlockDisappearingSword(String blockName) {
		super("sword");
	}

	@Override
	public void onBlockClicked(World world, BlockPos pos, EntityPlayer player) {
		if (world.isRemote) return;
		ItemStack heldItem = player.getHeldItemMainhand();
		
		int variation = world.getBlockState(pos).getValue(BlockDisappearing.VARIANT);
		
		switch(variation) {
		case 0:
			System.out.println(heldItem.getItem().getRegistryName());
			if (heldItem.getItem().equals(Items.WOODEN_SWORD)) {
				this.disappearChainReaction(world, pos);
			}
			break;
		case 1:
			if (heldItem.getItem().equals(Items.STONE_SWORD)) {
				this.disappearChainReaction(world, pos);
			}
			break;
		case 2:
			if (heldItem.getItem().equals(Items.IRON_SWORD)) {
				this.disappearChainReaction(world, pos);
			}
			break;
		case 3:
			if (heldItem.getItem().equals(Items.GOLDEN_SWORD)) {
				this.disappearChainReaction(world, pos);
			}
			break;
		case 4:
			if (heldItem.getItem().equals(Items.DIAMOND_SWORD)) {
				this.disappearChainReaction(world, pos);
			}
			break;
		case 5:
			//RESERVED FOR MOD INTEGRATION
			break;
		case 6:
			//RESERVED FOR MOD INTEGRATION
			break;
		case 7:
			//RESERVED FOR MOD INTEGRATION
			break;
		default:
			//nothing is a fine thing to do for impossible variants.
		}
		
		if (variation==0 && heldItem.getItem().equals(Items.WOODEN_SWORD)) {
			this.disappearChainReaction(world, pos);
		}
	}
	
	@Override
	public ChainReactionType getChainReactionType() {
		return CHAINTYPE_WEAPON;
	}
	
}
