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
import java.util.HashMap;
import java.util.Map;

public class Atom<T> implements Serializable {

	private static final long serialVersionUID = 1L;

	private HashMap<Long, Double> facts = new HashMap<Long, Double>();

	public final T value;
	
	
	public Atom(T value) {
		
		if (value == null)
			throw new NullPointerException("Atom value cannot be null");
		
		this.value = value;
	}
	
	public void add(Fact fact) {

		facts.put(fact.time, (facts.containsKey(fact.time) ? facts.get(fact.time) : 0.0) + fact.force);
	}

	public double getForce(Fact fact) {

		return facts.containsKey(fact.time)? facts.get(fact.time) : 0.0;
	}

	protected Map<Long, Double> getFacts() {

		return facts;
	}

	public double equality(Atom<?> another) {
		return equals(another)? 1.0 : 0.0;
	}

	@Override
	public int hashCode() {

		return value.hashCode();
	}

	@Override
	@SuppressWarnings("rawtypes")
	public boolean equals(Object obj) {

		if(!(obj instanceof Atom))
			return false;
		
		return value.equals(((Atom) obj).value);
		
		/*
		try {
			Field field = obj.getClass().getField("value");
			return field != null && field.get(obj).equals(value);
		}
		catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		*/
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


