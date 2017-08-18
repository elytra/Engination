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
package io.github.elytra.engination.item;

import java.util.List;

import io.github.elytra.engination.Engination;
import io.github.elytra.engination.entity.EntityTomato;
import net.minecraft.dispenser.BehaviorProjectileDispense;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

public class ItemTomato extends Item {
	public ItemTomato() {
		this.setCreativeTab(Engination.TAB_ENGINATION);
		this.setUnlocalizedName("tomato");
		this.setRegistryName("tomato");
		this.maxStackSize = 16;
	}
	
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
		tooltip.add("Throw me!");
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack item = player.getHeldItem(hand);
		
        if (!player.capabilities.isCreativeMode) {
        	item.shrink(1);
        	player.setHeldItem(hand, item);
            //--stack.stackSize;
        }
        world.playSound((EntityPlayer)null, player.posX, player.posY, player.posZ, Engination.SOUND_THROW, SoundCategory.PLAYERS, 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
        
        if (!world.isRemote) {
            EntityTomato tomato = new EntityTomato(world, player);
            tomato.setHeadingFromThrower(player, player.rotationPitch, player.rotationYaw, 0.0F, 1.5F, 1.0F);
            //Adjustments are because newly-aggressive clientside collisions were causing premature tomato-face
            tomato.posX += player.getLookVec().x*2f;
            tomato.posY += player.getLookVec().y*1.5f;
            tomato.posZ += player.getLookVec().z*2f;
            tomato.posY -= 0.2f; //Felt high as it was.
            world.spawnEntity(tomato);
        }

        player.addStat(StatList.getObjectUseStats(this));
        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, item);
    }
	
	public static class BehaviorTomatoDispense extends BehaviorProjectileDispense {

		@Override
		protected IProjectile getProjectileEntity(World world, IPosition position, ItemStack stackIn) {
			return new EntityTomato(world, position.getX(), position.getY(), position.getZ());
		}
		
	}
}
