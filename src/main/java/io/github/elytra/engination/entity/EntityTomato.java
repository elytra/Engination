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
package io.github.elytra.engination.entity;

import io.github.elytra.engination.Engination;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Blocks;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EntityTomato extends EntityThrowable {
	
    public EntityTomato(World world) {
        super(world);
    }

    public EntityTomato(World world, EntityLivingBase thrower) {
        super(world, thrower);
    }

    public EntityTomato(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    protected void onImpact(RayTraceResult result) {
        if (result.entityHit != null) {
            result.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, this.getThrower()), 0.0f);
        }

        for (int i = 0; i < 128; ++i) {
            this.world.spawnParticle(EnumParticleTypes.BLOCK_DUST, this.posX, this.posY, this.posZ, this.rand.nextGaussian()*0.3 + (this.motionX/2), this.rand.nextDouble()*0.3 + (this.motionY/2), this.rand.nextGaussian()*0.3 + (this.motionZ/2), new int[] { Block.getIdFromBlock(Blocks.REDSTONE_BLOCK) });
        }
        
        this.world.playSound(null, this.posX, this.posY, this.posZ, Engination.SOUND_TOMATO, SoundCategory.PLAYERS, 0.4f, 0.9f + rand.nextFloat()*0.2f);
        
        if (!this.world.isRemote) {
            this.setDead();
        }
    }
}