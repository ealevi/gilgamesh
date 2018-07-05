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


package org.gilgamesh.model.core;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.*;


public class Fact<Type extends Comparable<Type>> implements Comparable<Fact<Type>>, Serializable
{
	private static final long serialVersionUID = 1L;

	public final Atom<Type> atoms[];
	public final String hash;
	public final long time;
	
	@SuppressWarnings("unchecked")
	public Fact(Atom<Type> ... fact)
	{
		this.atoms = fact;
		this.time = System.nanoTime();

		String value = "";

		for(Atom<Type> atom : atoms)
			value += atom.value.toString();

		hash = value;
	}
	
	protected boolean contains(Atom<Type> atom)
	{
		for(Atom<Type> current : atoms)
			if(current.equals(atom))
				return true;

		return false;
	}

	protected boolean containsAll(Atom<Type> atoms[])
	{
		boolean contains = true;

		loop:
		for(Atom<Type> atom : atoms)
			for(Atom<Type> current : this.atoms)
			{
				if(current.equals(atom))
					continue loop;
				contains = false;
			}

		return contains;
	}
	
	public Type[] values()
	{
		if(atoms.length <= 0)
			return null;

		@SuppressWarnings("unchecked")
		Type[] result = (Type[]) Array.newInstance(atoms[0].value.getClass(), atoms.length);

		for(int i=0; i<atoms.length; i++)
			result[i] = atoms[i].value;

	    return result;
	}
	
	@SuppressWarnings("unchecked")
	public Fact<Type> suppress(Set<Atom<Type>> values)
	{
		ArrayDeque<Atom<Type>> remain = new ArrayDeque<Atom<Type>>();

		for(Atom<Type> atom : atoms)
			if(!values.contains(atom))
				remain.add(atom);
		
		return new Fact<Type>(remain.toArray(new Atom[0]));
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public boolean equals(Object object)
	{
		if(!(object instanceof Fact) || ((Fact<Type>) object).atoms.length != atoms.length)
			return false;

		for(int i=0; i<atoms.length; i++)
			if(!atoms[i].equals(((Fact<Type>) object).atoms[i]))
				return false;

		return true;
	}

	@Override
	public String toString()
	{
		String string = "[";

		for(int i=0; i<atoms.length; i++)
			string += atoms[i] + (i==atoms.length-1?"]": ", ");
		
		return String.format("%s", string);
	}
	
	@Override
	public int hashCode()
	{
		return hash.hashCode();
	}

	@Override
	public int compareTo(Fact<Type> fact)
	{
		return hash.compareTo(fact.hash);
	}
}

































