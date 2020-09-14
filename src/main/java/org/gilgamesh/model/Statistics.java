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
