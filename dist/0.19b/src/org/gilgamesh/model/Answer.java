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

/**
 * Class that represents an answer.<br/><br/>
 * This class is usually used to store a fact with its respective force. 
 * @author Eduardo Alevi
 * @param <Type> The atoms type.
 */
public class Answer<Type> implements Comparable<Answer<Type>>
{
	public final Type value;
	private double force;
	private long time;	
	private String description = "";
	
	public Answer(Type value, double force)
	{
		this.force = force;
		this.value = value;
	}
	
	public Answer(Type value, double force, long time)
	{
		this.force = force;
		this.value = value;
		this.time  = time;
	}
	
	public Answer(Type value, double force, String description)
	{
		this.force = force;
		this.value = value;
		this.description = description;
	}
	
	public void sumForce(double force)
	{
		this.force += force;
	}
	
	public double getForce()
	{
		return force;
	}
	
	public void increment()
	{
		force+=1;
	}
	
	public void setForce(double force)
	{
		this.force = force;
	}
	
	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}
	
	public long getTime()
	{
		return time;
	}

	public void setTime(long time)
	{
		this.time = time;
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean equals(Object obj)
	{
		if(obj instanceof Answer)
			return ((Answer<Type>) obj).value.equals(value);
		else
			return false;
	}

	@Override
	public int compareTo(Answer<Type> answer)
	{
		if(answer == null || force > answer.force)
			return 1;
		if(force < answer.force)
			return -1;
		else
		{
			if(time > answer.time)
				return 1;
			else if(time < answer.time)
				return -1;
			else
				return 0;
		}
	}

	@Override
	public String toString()
	{
		return String.format("%s:%.2f", value, force);
	}

	
}
