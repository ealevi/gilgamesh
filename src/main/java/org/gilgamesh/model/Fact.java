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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Fact implements Comparable<Fact>, Serializable {

	private static final long serialVersionUID = 1L;

	public final Atom<?> atoms[];
	public final int hash;
	public final long time;
	public final double force;

	
	
	
	public Fact(Atom<?> ... atoms) {
		this(0.0, System.nanoTime(), atoms);
	}
	
	public Fact(double force, Atom<?> ... atoms) {
		this(force, System.nanoTime(), atoms);
	}
	
	public Fact(double force, long time, Atom<?> ... atoms) {

		if(atoms.length <= 0)
			throw new NullPointerException("A fact must have one atom at least.");
		
		this.atoms = atoms;
		this.force = force;
		this.time = time;

		int value = 0;

		for (Atom<?> atom : atoms)
			value += atom.hashCode();
		
		hash = value;
	}

	protected int matches(Atom<?> values[]) {
		
		int total = 0;
		
		for (Atom<?> atom : values)
			total += count(atom);
		
		return total;
	}
	
	protected int count(Atom<?> atom) {

		int total = 0;
		
		for (Atom<?> current : atoms)
			if (current.equals(atom))
				total++;

		return total;
	}

	public Fact suppress(Atom<?> ... values)
	{
		List<Atom<?>> remains = new ArrayList<Atom<?>>(Arrays.asList(atoms));
		
		for(Atom<?> current : values)
			remains.remove(current);
		
		return new Fact(force, time, remains.toArray(new Atom[0]));
	}

	public long getTime() {
		return time;
	}
	
	public Atom<?>[] getAtoms() {
		return atoms;
	}
	
	@Override
	public String toString() {

		String string = "";

		for (int i = 0; i < atoms.length; i++)
			string += atoms[i] + " ";

		return String.format("[ %d: %.8f = %s]", time, force, string);
	}

	@Override
	public int hashCode() {

		return hash;
	}

	@Override
	public boolean equals(Object object) {

		if (!(object instanceof Fact) || ((Fact) object).atoms.length != atoms.length)
			return false;

		for (int i = 0; i < atoms.length; i++)
			if (!atoms[i].equals(((Fact) object).atoms[i]))
				return false;

		return true;
	}
	
	public int compareTo(Fact fact) {

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


















