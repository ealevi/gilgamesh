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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("unchecked")
public class Fact<T extends Serializable> implements Comparable<Fact<T>>, Serializable {

	private static final long serialVersionUID = 1L;

	protected long id;
	protected double force;
	
	public final T atoms[];
	public final int hash;
	public final long time;
	


	
	
	protected Fact(T ... atoms) {
		this(0, 0.0, System.nanoTime(), atoms);
	}
	
	protected Fact(double force, T ... atoms) {
		this(0, force, System.nanoTime(), atoms);
	}

	protected Fact(long id, double force, long time, T ... atoms) {

		this.id = id;
		
		if(atoms.length <= 0)
			throw new NullPointerException("A fact must have one atom at least.");
		
		this.atoms = atoms;
		this.force = force;
		this.time = time;

		int value = 0;

		for (T atom : atoms)
			value += atom.hashCode();
		
		hash = value;
	}

	public Fact<T> suppress(T ... values)
	{
		List<T> remains = new ArrayList<T>(Arrays.asList(atoms));
		
		for(T current : values)
			remains.remove(current);
		
		T array[] = (T[]) Array.newInstance(values[0].getClass(), remains.size());
		
		for(int i=0; i<remains.size(); i++)
			array[i] = remains.get(i);
		
		return new Fact<T>(0, force, time, array);
	}

	public Atom<T>[] toAtoms() {
		
		return Arrays.asList(atoms).stream().<Atom<T>>map(v -> new Atom<T>(v)).collect(Collectors.toList()).toArray(new Atom[0]);
	}
	
	@Override
	public String toString() {

		String string = "";

		for (int i = 0; i < atoms.length; i++)
			string += atoms[i] + " ";

		return String.format("[ %d: %.8f = %s]", time, force, string);
	}

	public double getForce() {
		return force;
	}

	@Override
	public int hashCode() {

		return hash;
	}

	@Override
	public boolean equals(Object object) {

		if (!(object instanceof Fact) || ((Fact<T>) object).atoms.length != atoms.length)
			return false;

		for (int i = 0; i < atoms.length; i++)
			if (!atoms[i].equals(((Fact<T>) object).atoms[i]))
				return false;

		return true;
	}
	
	@Override
	public int compareTo(Fact<T> fact) {

		if(force > fact.force)
			return 1;
		if(force < fact.force)
			return -1;
		else
		{
			if(time > fact.time)
				return 1;
			else if(time < fact.time)
				return -1;
			else
				return 0;
		}
	}
	
}


















