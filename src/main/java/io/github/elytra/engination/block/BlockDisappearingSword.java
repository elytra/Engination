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
		super("sword", Material.CIRCUITS, Material.ROCK.getMaterialMapColor());
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
