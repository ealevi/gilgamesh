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

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@SuppressWarnings("unchecked")
public class Atom<T extends Serializable> implements Serializable {

	private static final long serialVersionUID = 1L;
	public final T value;

	private HashSet<Long> facts = new HashSet<Long>();

	
	

	
	
	public Atom(T value) {
		
		if (value == null)
			throw new NullPointerException("Atom value cannot be null");
		
		this.value = value;
	}
	
	public void add(long factID) {

		facts.add(factID);
	}

	public HashSet<Long> getFacts() {

		return facts;
	}

	
	/**
	 * Returns a generic atom from a value.
	 * @param <T> The type of value
	 * @param value the value to create an Atom from.
	 * @return
	 */
	public static <T extends Serializable> Atom<T> of(T value) {

		return new Atom<T>(value);
	}

	public static <T extends Serializable> T[] convert(Atom<T> ... values) {

		T array[] = (T[]) Array.newInstance(values[0].value.getClass(), values.length);
		
		for(int i=0; i<values.length; i++)
			array[i] = values[i].value;
		
		return array;
	}
	
	/**
	 * Converts a list of values in an Atom array.
	 * @param values to be converted
	 * @return an array of Atoms
	 */
	public static <T extends Serializable> T[] convert(List<T> values) {

		T array[] = (T[]) Array.newInstance(values.get(0).getClass(), values.size());
		
		for(int i=0; i<values.size(); i++)
			array[i] = values.get(i);
		
		return array;
	}

	/**
	 * Converts a array of values in an Atom array.
	 * @param values to be converted
	 * @return an array of Atoms
	 */
	public static <T extends Serializable> List<Atom<T>> convert(T ... values) {

		ArrayList<Atom<T>> array = new ArrayList<Atom<T>>();

		for (int i=0; i<values.length; i++)
			array.add(of(values[i]));

		return array;
	}
	
	public double equality(Atom<T> another) {
		return equals(another)? 1.0 : 0.0;
	}

	public int hashCode() {

		return value.hashCode();
	}

	@SuppressWarnings("rawtypes")
	public boolean equals(Object obj) {

		if(!(obj instanceof Atom))
			return false;
		
		return value.equals(((Atom) obj).value);
	}

	@Override
	public String toString() {

		return value.toString();
	}
	
	/*

	@Override
	public int compareTo(Atom atom) {

		int h1 = hashCode();
		int h2 = atom.hashCode();
		
		if(h1 > h2)
			return 1;
		else if(h1 < h2)
			return -1;
		else
			return 0;
	}

	*/
}


