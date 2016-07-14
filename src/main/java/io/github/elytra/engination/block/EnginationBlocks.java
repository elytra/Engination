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

package io.github.elytra.engination.block;

import io.github.elytra.engination.Engination;
import net.minecraft.block.Block;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;

@ObjectHolder(Engination.MODID)
public class EnginationBlocks {
	@ObjectHolder("machine.conveyor.2")
	public static final BlockConveyor CONVEYOR = null;
	@ObjectHolder("machine.conveyor.4")
	public static final BlockConveyor CONVEYOR_FAST = null;
	@ObjectHolder("machine.conveyor.8")
	public static final BlockConveyor CONVEYOR_ULTRAFAST = null;
	@ObjectHolder("machine.launcher.2")
	public static final BlockLauncher LAUNCHER = null;
	@ObjectHolder("machine.launcher.3")
	public static final BlockLauncher LAUNCHER_FORCEFUL = null;
	@ObjectHolder("machine.launcher.5")
	public static final BlockLauncher LAUNCHER_ULTRAFORCEFUL = null;
	@ObjectHolder("machine.landingpad")
	public static final BlockLandingPad LANDINGPAD = null;
	@ObjectHolder("machine.generator")
	public static final BlockGenerator GENERATOR = null;
	@ObjectHolder("machine.battery")
	public static final BlockBattery BATTERY = null;
	@ObjectHolder("cosmetic.tourian")
	public static final BlockCosmetic COSMETIC_TOURIAN = null;
}
