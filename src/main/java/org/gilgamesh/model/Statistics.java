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
 * Hold some statistic data.
 * 
 * @author Eduardo Alevi
 */
public class Statistics {

	public final int counter;
	public final double average;
	public final double variance;
	public final double sigma;


	public Statistics(int counter, double average, double variance, double sigma) {

		this.counter = counter;
		this.average = average;
		this.variance = variance;
		this.sigma = sigma;
	}

	@Override
	public String toString() {

		return String.format("Counter:[%d], Average:[%.2f], Variance:[%.2f], Sigma:[%.2f]", counter, average, variance, sigma);
	}
}
