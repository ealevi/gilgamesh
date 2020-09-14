package org.gilgamesh.model;

/**
 * Class to validate an answer. This class is used to validate when a fact
 * matches to the question. Can be customized to receive other validations.
 * 
 * @author Eduardo Alevi
 * @param <Type>
 *            The atom type.
 */
public abstract class Predicate {

	public abstract double match(Atom<?> ... answer);
	public abstract Fact getAnswer(Fact fact, double force);
}
