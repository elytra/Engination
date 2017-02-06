package io.github.elytra.engination.block;

import java.util.List;

import io.github.elytra.engination.Engination;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLog;
import net.minecraft.block.BlockRotatedPillar;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;

public class BlockCosmeticPillar extends BlockRotatedPillar {
	public static PropertyInteger VARIATION = PropertyInteger.create("variant", 0, 3);
	//public static PropertyEnum<BlockLog.EnumAxis> AXIS = PropertyEnum.create("axis", BlockLog.EnumAxis.class);
	private boolean showTip = false;
	
	public BlockCosmeticPillar(String blockName, Material material, MapColor color) {
		super(material, color);
		this.setUnlocalizedName("engination."+blockName);
		this.setRegistryName("cosmetic."+blockName);
		
		this.blockHardness = 1.0f;
		this.blockResistance = 15.0f;
		this.setHarvestLevel("pickaxe", 1);
		
		this.setCreativeTab(Engination.TAB_COSMETIC);
	}

	@Override
	public void getSubBlocks(Item itemBlock, CreativeTabs tab, List<ItemStack> list) {
		for(int i=0; i<4; i++) {
			list.add(new ItemStack(itemBlock, 1, i));
		}
	}
	
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, VARIATION, AXIS);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		int variation = meta & 0x3;
		
		return super.getStateFromMeta(meta >> 2)
				.withProperty(VARIATION, variation);
		
		
		//state = state.withProperty(VARIATION, variation);
		
		//int axis = meta >> 2;
		//if (axis>BlockLog.EnumAxis.values().length) axis = 0;
		
		//return this.blockState.getBaseState()
		//		.withProperty(VARIATION, variation)
		//		.withProperty(AXIS, EnumFacing.Axis.values()[axis]);
		
		
	}
	
	@Override
	public int getMetaFromState(IBlockState state) {
		return super.getMetaFromState(state) | (state.getValue(VARIATION) << 2);
	}
	
	@Override
	public int damageDropped(IBlockState state) {
        return state.getValue(VARIATION); //Just the variation
    }
	
	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float x, float y, float z, int something, EntityLivingBase placer, ItemStack item) {
		return super.getStateForPlacement(world, pos, facing, x, y, z, something, placer, item)
				.withProperty(VARIATION, item.getItemDamage());
	}
	
	public BlockCosmeticPillar setTip() { this.showTip=true; return this; }
	
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
		if (showTip) tooltip.add(I18n.translateToLocal(this.getUnlocalizedName()+".tip"));
	}
}
