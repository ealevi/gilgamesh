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
import java.util.Set;
import java.util.TreeMap;


public class Atom<Type extends Comparable<Type>> implements Comparable<Atom<Type>>, Serializable
{
	private static final long serialVersionUID = 1L;
	
	public final Type value;
	private TreeMap<Fact<Type>, Double> facts = new TreeMap<Fact<Type>, Double>();
	
	
	public Atom(Type value)
	{
		this.value = value;
	}
	
	protected void add(Fact<Type> fact, double force)
	{
		if(!facts.containsKey(fact))
			facts.put(fact, 0.0);
			
		facts.put(fact, facts.get(fact) + force);
	}
	
	public double getForce(Fact<Type> fact)
	{
		if(facts.containsKey(fact))
			return facts.get(fact);
		else
			return 0.0;
	}
	
	public Set<Fact<Type>> getFacts()
	{
		return facts.keySet();
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public boolean equals(Object obj)
	{
		if(obj instanceof Atom)
			return ((Atom<Type>) obj).value.equals(value);
		else
			return false;
	}

	@Override
	public int compareTo(Atom<Type> atom)
	{
		return value.compareTo(atom.value);
	}

	@Override
	public String toString()
	{
		return value == null? "" : value.toString();
	}
}































