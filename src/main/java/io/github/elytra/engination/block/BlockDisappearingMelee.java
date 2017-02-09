package io.github.elytra.engination.block;

import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockDisappearingMelee extends BlockDisappearing {
	public static final ChainReactionType CHAINTYPE_PUNCH = new ChainReactionType();
	
	public BlockDisappearingMelee(String blockName) {
		super("melee", Material.CIRCUITS, Material.ROCK.getMaterialMapColor());
	}

	@Override
	public void onBlockClicked(World world, BlockPos pos, EntityPlayer player) {
		if (world.isRemote) return;
		ItemStack heldItem = player.getHeldItemMainhand();
		if (heldItem.isEmpty()) {
			this.disappearChainReaction(world, pos);
		}
	}
	
	@Override
	public ChainReactionType getChainReactionType() {
		return CHAINTYPE_PUNCH;
	}
	
}
