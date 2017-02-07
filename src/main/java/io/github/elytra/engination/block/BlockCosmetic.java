package io.github.elytra.engination.block;

import java.util.List;

import io.github.elytra.engination.Engination;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;

public class BlockCosmetic extends Block {
	public static PropertyInteger VARIATION = PropertyInteger.create("variant", 0, 15);
	private boolean showTip = false;
	
	public BlockCosmetic(String blockName, Material material, MapColor color) {
		super(material, color);
		this.setUnlocalizedName("engination."+blockName);
		this.setRegistryName("cosmetic."+blockName);
		
		this.blockHardness = 1.0f;
		this.blockResistance = 15.0f;
		this.setHarvestLevel("pickaxe", 1);
		
		this.setCreativeTab(Engination.TAB_COSMETIC);
		
	}

	@Override
	public void getSubBlocks(Item itemBlock, CreativeTabs tab, NonNullList<ItemStack> list) {
		for(int i=0; i<16; i++) {
			list.add(new ItemStack(itemBlock, 1, i));
		}
	}
	
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, VARIATION);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.blockState.getBaseState().withProperty(VARIATION, meta);
	}
	
	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(VARIATION);
	}
	
	@Override
	public int damageDropped(IBlockState state) {
        return state.getValue(VARIATION);
    }
	
	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		world.setBlockState(pos, this.getStateFromMeta(stack.getMetadata()));
	}
	
	public BlockCosmetic setTip() { this.showTip = true; return this; }
	
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
		if (showTip) tooltip.add(I18n.translateToLocal(this.getUnlocalizedName()+".tip"));
	}
	
}
