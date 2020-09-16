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
 * Class to validate an answer. This class is used to validate when a fact
 * matches to the question. Can be customized to receive other validations.
 * 
 * @author Eduardo Alevi
 */
public abstract class Predicate {

	public abstract double match(Atom<?> ... answer);
	public abstract Fact getAnswer(Fact fact, double force);
}
