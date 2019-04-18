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


package org.gilgamesh.model;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;

import org.gilgamesh.model.core.Core;


/**
 * Main factory class. It creates instances of Gilgamesh Core (memory) for use.
 * @author Eduardo Alevi
 */
public class Gilgamesh
{
	private static final double version = 0.17;
	

	/**
	 * Creates a usable Core (memory) of Gilgamesh.<\br><\br>
	 * This method is called when you need a memory and you want it working only with strings.
	 * @return The generic Core for strings.
	 */
	public static Core<String> createStringCore()
	{
		return new Core<String>();
	}
	
	/**
	 * Creates a usable Core (memory) of Gilgamesh.<\br><\br>
	 * This method creates a instance of Core for generic classes. Notice this method is
	 * used when you want to use a specific class for this context.
	 * @return The generic Core for a specific class.
	 */
	public static <Type extends Comparable<Type>> Core<Type> createGenericCore()
	{
		return new Core<Type>();
	}
	
	/**
	 * Saves the Gilgamesh Core memory into a file.
	 * @param file The file to be saved. If exists, it will be overridden.
	 * @param core The Gilgamesh Core object to be saved.
	 */
	public static void save(File file, Core<?> core)
	{
		try
		{
			if(file == null || file.isDirectory())
				return;
			
			ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(file));
			output.writeObject(core);
			output.flush();
			output.close();
			
			System.out.println("Gilgamesh Core saved in " + file.getAbsolutePath());
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("Cannot save Gilgamesh Core memory to the file " + file);
		}
	}

	/**
	 * Loads a Gilgamesh Core memory binary file.
	 * @param file The file to be read.
	 * @return The Gilgamesh Core object.
	 */
	@SuppressWarnings({"resource", "unchecked"})
	public static <Type extends Comparable<Type>> Core<Type> load(File file)
	{
		try
		{
			if(file == null || !file.exists() || file.isDirectory())
				return null;
			
			ObjectInputStream input = new ObjectInputStream(new BufferedInputStream(new FileInputStream(new RandomAccessFile(file, "rw").getFD())));
			System.out.println("Gilgamesh Core loaded from " + file.getAbsolutePath());
			
			Core<Type> core = (Core<Type>) input.readObject();
			input.close();
			
			return core;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("Cannot load Gilgamesh Core memory from file " + file);
			return null;
		}
	}

	
	/**
	 * Return the software version.
	 * @return The current version.
	 */
	public static double getVersion()
	{
		return version;
	}
	
}



















