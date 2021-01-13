/*
	Gilgamesh Artificial Intelligence Project
    Copyright (C) 2014  Eduardo Alevi

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
    
*/

package org.gilgamesh.core;

import static org.junit.Assert.fail;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ Gilgamesh.class })
public class GilgameshIntegrityPersistenceTest {

	private final int THREADS = 10;
	private static final int QUANTITY = 100;
	
	@Test
	public void testIntegrity() throws Exception {

		ArrayList<File> files = new ArrayList<File>();
		ArrayList<Gilgamesh<String>> cores = new ArrayList<Gilgamesh<String>>();
		String array = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		Random random = new Random();
		
		for(int f=0; f<QUANTITY; f++) {
			
			File file = File.createTempFile("gilgamesh_", ".dat");
			Gilgamesh<String> gilgamesh = new Gilgamesh<String>();
			
			for(int i=0; i<QUANTITY; i++)
				gilgamesh.reinforce("" + array.charAt(random.nextInt(array.length())));
			
			files.add(file);
			cores.add(gilgamesh);
		}

		ExecutorService executor = Executors.newFixedThreadPool(THREADS);
		List<Callable<Object>> todo = new ArrayList<Callable<Object>>();
		
		// Save
		for(int i=0; i<files.size(); i++) {
			
			final int pos = i;
			
			todo.add(Executors.callable(() -> {

				File file = files.get(pos);
				Gilgamesh<String> gilgamesh = cores.get(pos);
				
				try {
					
					if(!Gilgamesh.<String>save(file, gilgamesh) || file.length() <= 0)
						fail();
				}
				catch (Exception e) {
					fail();
				}

			}));
		}
			
		executor.invokeAll(todo);
		executor.shutdown();
		executor = Executors.newFixedThreadPool(THREADS);
		
		// Load
		for(int i=0; i<QUANTITY; i++)
			todo.add(Executors.callable(() -> {

				File file = files.get(random.nextInt(files.size()));

				Gilgamesh<String> gilgamesh = Gilgamesh.<String>load(file);
				
				if(gilgamesh == null) {
					fail("Fail on load Gilgamesh file");
					return;
				}
				
				System.out.println(gilgamesh.getFacts()[0].toString());
				
			}));
			
		executor.invokeAll(todo);
		executor.shutdown();
		
		
		
		for(File file : files)
			file.delete();
		
		
	}
	
}





























