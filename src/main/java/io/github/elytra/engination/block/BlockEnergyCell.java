package io.github.elytra.engination.block;

import io.github.elytra.engination.block.te.TileEntityEnergyCell;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockEnergyCell extends BlockMachineBase implements ITileEntityProvider {
	public BlockEnergyCell() {
		super("energyCell");
	}

	@Override
	public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		return this.blockState.getBaseState();
	}
	
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this);
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.blockState.getBaseState();
	}
	
	@Override
	public int getMetaFromState(IBlockState state) {
		return 0;
	}
	
	public void setOn(World world, BlockPos pos, boolean on) {
		//Energy cells don't really activate like other machines do.
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityEnergyCell();
	}
	
	
}
