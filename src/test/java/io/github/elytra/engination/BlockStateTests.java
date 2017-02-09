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
package io.github.elytra.engination;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.Set;
import java.util.stream.StreamSupport;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.minecraftforge.client.model.BlockStateLoader;
import net.minecraftforge.client.model.ForgeBlockStateV1;
import net.minecraftforge.client.model.ForgeBlockStateV1.Variant;
import net.minecraftforge.common.model.TRSRTransformation;

public class BlockStateTests {
	@Test
	public void triageJson() throws IOException {
		Gson gson = (new GsonBuilder())
	            .registerTypeAdapter(ForgeBlockStateV1.class, new ForgeBlockStateV1.Deserializer())
	            .registerTypeAdapter(ForgeBlockStateV1.Variant.class, new ForgeBlockStateV1.Variant.Deserializer())
	            .registerTypeAdapter(TRSRTransformation.class, ForgeBlockStateV1.TRSRDeserializer.INSTANCE)
	            .create();
		
		File blockstates = new File("src/main/resources/assets/engination/blockstates");
		//System.out.println("Root triage dir: "+ blockstates.getAbsolutePath());
		
		exploreForTriage(blockstates, gson);
	}
	
	public void exploreForTriage(File f, Gson parser) throws IOException {
		if (!f.exists()) return;
		if (f.isDirectory()) {
			for(File sub : f.listFiles()) {
				exploreForTriage(sub, parser);
			}
		} else if (f.isFile()) {
			submitForTriage(f, parser);
		}
	}
	
	public void submitForTriage(File f, Gson parser) throws IOException {
		//System.out.println("Triage on "+f.getAbsolutePath());
		InputStreamReader reader = new InputStreamReader(new FileInputStream(f));
		//ModelBlockDefinition modelDef = BlockStateLoader.load(reader, parser);
		String contents = IOUtils.toString(reader);
		
		BlockStateLoader.Marker marker = parser.fromJson(contents, BlockStateLoader.Marker.class);
		if (marker.forge_marker==1) {
			//System.out.println("  Forge BlockState");
			
			ForgeBlockStateV1 v1 = parser.fromJson(contents, ForgeBlockStateV1.class);
			Multimap<String, ForgeBlockStateV1.Variant> variants = HashMultimap.create();
			try {
				Field field = v1.getClass().getDeclaredField("variants");
				field.setAccessible(true);
				variants = (Multimap<String, Variant>) field.get(v1);
				
				
			} catch (Throwable t) {
				//System.out.println("Variants field inaccessible.");
				//t.printStackTrace();
			}
			//System.out.println("BlockState File: "+f.getName());
			for(String key : variants.asMap().keySet()) {
				//System.out.println("  ["+key+"] ("+variants.get(key).size()+")");
			}
			
			if (variants.asMap().keySet().size()==1 && variants.asMap().keySet().contains("variant")) {
				throw new AssertionError(f.getAbsolutePath()+" has a broken variant set. Chances are, this is due json not matching the BlockStateV1 format.");
				
			}
		} else {
			//System.out.println("  Vanilla BlockState");
		}
	}
}
