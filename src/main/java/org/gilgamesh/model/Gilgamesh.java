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

import org.gilgamesh.model.core.Core;

import java.io.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Main factory class. It creates instances of Gilgamesh Core (memory) for use.
 * @author Eduardo Alevi
 */
public class Gilgamesh
{
	private static ReadWriteLock lock = new ReentrantReadWriteLock();

	/**
	 * Creates a usable Core (memory) of Gilgamesh.
	 * This method is called when you need a memory and you want it working only with strings.
	 * @return The generic Core for strings.
	 */
	public static Core<String> createStringCore()
	{
		return new Core<String>();
	}
	
	/**
	 * Creates a usable Core (memory) of Gilgamesh.
	 * This method creates a instance of Core for generic classes. Notice this method is
	 * used when you want to use a specific class for this context.
	 * @param Type the core type to be created. 
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

			ObjectOutputStream output = null;

			try
			{
				lock.writeLock().lock();
				BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
				output = new ObjectOutputStream(bos);
				output.writeObject(core);
			}
			finally
			{
				lock.writeLock().unlock();
				if(output != null)
					output.close();
			}

			Logger.getGlobal().log(Level.FINER, "Gilgamesh Core saved in " + file.getAbsolutePath());
		}
		catch(Exception e)
		{
			e.printStackTrace();
			Logger.getGlobal().log(Level.SEVERE, "Cannot save Gilgamesh Core memory to the file " + file);
		}
	}

	/**
	 * Loads a Gilgamesh Core memory binary file.
	 * @param Type the core type to be created. 
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

			ObjectInput input = null;

			try
			{
				lock.readLock().lock();
				BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
				input = new ObjectInputStream(bis);
				Core<Type> core = (Core<Type>) input.readObject();
				Logger.getGlobal().log(Level.FINER, "Gilgamesh Core loaded from " + file.getAbsolutePath());
				return core;
			}
			finally
			{
				lock.readLock().unlock();
				if(input != null)
					input.close();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			Logger.getGlobal().log(Level.SEVERE, "Cannot load Gilgamesh Core memory from file " + file);
			return null;
		}
	}

	
	/**
	 * Return the software version.
	 * @return The current version.
	 */
	public static String getVersion()
	{
		return String.format("0.%db", Core.serialVersionUID);
	}
	
}



















