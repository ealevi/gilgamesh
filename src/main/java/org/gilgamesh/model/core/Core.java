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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.TreeSet;

import org.gilgamesh.model.Answer;




/**
 * The Gilgamesh memory.
 * This is the main class to manage AI data. You should create one of it for each context you have.
 * @author Eduardo Alevi
 * @param <Type> The generic type you want to work on.
 */
public class Core<Type extends Comparable<Type>> implements Serializable
{
	public static final long serialVersionUID = 20L;

	private transient Object lock = new Object();

	private HashMap<Type, Atom<Type>> atoms = new HashMap<Type, Atom<Type>>();




	private synchronized Object getLock()
	{
		if(lock == null)
			lock = new Object();
		return lock;
	}

	/**
	 * Reinforce a fact, with +1 to force.
	 * @param values The atoms which will make a fact.
	 */
	@SuppressWarnings("unchecked")
	public void reinforce(Type ... values)
	{
		fact(1.0, convert(values));
	}

	/**
	 * Punish a fact, with -1 to force.
	 * @param values The atoms which will make a fact.
	 */
	@SuppressWarnings("unchecked")
	public void punish(Type ... values)
	{
		fact(-1.0, convert(values));
	}

	/**
	 * Reinforce a fact, with +1 to force.
	 * @param values List of Type atoms to make a fact.
	 */
	public void reinforce(List<Type> values)
	{
		fact(1.0, convert(values));
	}

	/**
	 * Punish a fact, with -1 to force.
	 * @param values List of Type atoms to make a fact.
	 */
	public void punish(List<Type> values)
	{
		fact(-1.0, convert(values));
	}

	private Atom<Type>[] convert(List<Type> values)
	{
		@SuppressWarnings("unchecked")
		Atom<Type> array[] = new Atom[values.size()];

		for(int i=0; i<values.size(); i++)
			array[i] = new Atom<Type>(values.get(i));

		return array;
	}

	@SuppressWarnings("unchecked")
	private Atom<Type>[] convert(Type ... values)
	{
		Atom<Type> array[] = new Atom[values.length];

		for(int i=0; i<values.length; i++)
			array[i] = new Atom<Type>(values[i]);

		return array;
	}

	/**
	 * Make a explicit fact, with a defined force.
	 * @param force The force to be defined to the fact (or summed if the fact exists).
	 * @param values The atoms used to make that fact.
	 */
	@SuppressWarnings("unchecked")
	public void fact(double force, Type ...values)
	{
		fact(force, convert(values));
	}

	/**
	 * Make a explicit fact, with a defined force.
	 * @param force The force to be defined to the fact (or summed if the fact exists).
	 * @param values List with atoms used to make that fact.
	 */
	public void fact(double force, List<Type> values)
	{
		fact(force, convert(values));
	}

	@SuppressWarnings("unchecked")
	private void fact(double force, Atom<Type> ... values)
	{
		synchronized(getLock())
		{
			Fact<Type> fact = new Fact<Type>(values);

			for(Atom<Type> atom : values)
			{
				if(!atoms.keySet().contains(atom.value))
					atoms.put(atom.value, atom);

				atoms.get(atom.value).add(fact, force);
			}
		}
	}

	/**
	 * Delete a fact.
	 * If the atoms involved have connections to other facts, they will be not deleted.
	 * @return true if the fact was removed or false otherwise.
	 * @param values The atoms used to make that fact.
	 */
	@SuppressWarnings("unchecked")
	public boolean remove(Type ...values)
	{
		return remove(convert(values));
	}

	/**
	 * Delete fact.
	 * If the atoms involved have connections to other facts, they will be not deleted.
	 * @param values The atoms list used to make that fact.
	 */
	public boolean remove(List<Type> values)
	{
		return remove(convert(values));
	}

	@SuppressWarnings("unchecked")
	private boolean remove(Atom<Type> ... values)
	{
		synchronized(getLock())
		{
			for(Atom<Type> atom : values)
				atoms.remove(atom.value);
			return true;
		}
	}

	/**
	 * Returns the Atom object from a respective block of information.
	 * @param atom The atom of information
	 * @return The Atom object
	 */
	public Atom<Type> getAtom(Type atom)
	{
		if(atom != null && atoms.keySet().contains(atom))
			return atoms.get(atom);
		else
			return null;
	}

	/**
	 * Return an single and most probable answer.
	 * @param values The question atoms you want to ask.
	 * @return The answer atoms.
	 */
	@SuppressWarnings("unchecked")
	public Type[] answer(Type ... values)
	{
		Answer<Fact<Type>> answer = answer(Arrays.asList(values));
		if(answer != null)
			return answer.value.values();

		return null;
	}

	/**
	 * Return an single and most probable answer.
	 * @param values A list of question atoms.
	 * @return The answer atoms, in the form of Answer class which contains the forces.
	 */
	public Answer<Fact<Type>> answer(List<Type> values)
	{
		List<Answer<Fact<Type>>> list = getAnswers(false, false, values);
		if(list.size() > 0)
			return list.get(0);

		list = getAnswers(true, false, values);
		if(list.size() > 0)
		{
			list.get(0).setForce(list.get(0).getForce() / (double) values.size());
			return list.get(0);
		}

		return null;
	}

	/**
	 * Get a list of answers for a question.
	 * This method provides the same answer as getAnswer(false, <atoms>), except for the fact that the answer has to have all question atoms.
	 * @param suppress Indicates that answers should be combined, suppressing the atoms informed.
	 * @param values Question atoms.
	 * @return A list of Answer objects, containing the answers with their respective forces. The list is ordered from the most probable to the less probable.
	 */
	@SuppressWarnings("unchecked")
	public List<Answer<Fact<Type>>> getAnswersForAll(boolean suppress, final Type ... values)
	{
		return getAnswers(suppress, new Predicate<Type>() {
			public boolean match(Atom<Type> ... atoms)
			{
				if(values.length != atoms.length)
					return false;

				loop:
				for(Atom<Type> value : convert(values))
				{
					for(Atom<Type> atom : atoms)
						if(atom.equals(value))
							continue loop;
					return false;
				}

				return true;
			}

			@Override
			public double calc(Fact<Type> fact, double force)
			{
				return force / values.length;
			}
		}, values);
	}

	/**
	 * Get a list of answers for a question.
	 * @param suppress Indicates that answers should be combined, suppressing the atoms informed.
	 * @param matchAny true if the answer should contains any question atoms. false if the answers should contains all question atoms.
	 * @param values Question atoms.
	 * @return A list of Answer objects, containing the answers with their respective forces. The list is ordered from the most probable to the less probable.
	 */
	@SuppressWarnings("unchecked")
	public List<Answer<Fact<Type>>> getAnswers(boolean matchAny, boolean suppress, Type ... values)
	{
		if(matchAny)
			return getAnswersOR(suppress, convert(values));
		else
			return getAnswersAND(suppress, convert(values));
	}

	/**
	 * Get a list of answers for a question.
	 * @param suppress Indicates that answers should be combined, suppressing the atoms informed.
	 * @param matchAny true if the answer should contains any question atoms. false if the answers should contains all question atoms.
	 * @param values List of question atoms.
	 * @return A list of Answer objects, containing the answers with their respective forces. The list is ordered from the most probable to the less probable.
	 */
	public List<Answer<Fact<Type>>> getAnswers(boolean matchAny, boolean suppress, final List<Type> values)
	{
		if(matchAny)
			return getAnswersOR(suppress, convert(values));
		else
			return getAnswersAND(suppress, convert(values));
	}

	@SuppressWarnings("unchecked")
	private List<Answer<Fact<Type>>> getAnswersAND(boolean suppress, final Atom<Type> ... values)
	{
		return getAnswers(suppress, new Predicate<Type>() {
			public boolean match(Atom<Type> ... atoms)
			{
				loop:
				for(Atom<Type> value : values)
				{
					for(Atom<Type> atom : atoms)
						if(atom.equals(value))
							continue loop;
					return false;
				}

				return true;
			}

			@Override
			public double calc(Fact<Type> fact, double force)
			{
				return force / values.length;
			}
		}, values);
	}

	@SuppressWarnings("unchecked")
	private List<Answer<Fact<Type>>> getAnswersOR(boolean suppress, final Atom<Type> ... values)
	{
		return getAnswers(suppress, new Predicate<Type>() {
			public boolean match(Atom<Type> ... atoms)
			{
				for(Atom<Type> value : values)
				{
					for(Atom<Type> atom : atoms)
						if(atom.equals(value))
							return true;
				}

				return false;
			}

			@Override
			public double calc(Fact<Type> fact, double force)
			{
				int factor = values.length + 1;

				for(Atom<Type> atom : values)
					if(fact.contains(atom))
						factor--;

				return (force / values.length) / (double) (factor);
			}
		}, values);
	}

	/**
	 * Returns a customized list of answers.
	 * If you want to customize the logic done by Gilgamesh, you can implement the Core inner class <i>Predicate</i> in order to
	 * provide your own matching implementation.
	 * @param suppress Indicates that answers should be combined, suppressing the atoms informed.
	 * @param predicate The predicate object which will determine how to approve an answer.
	 * @param values The question atoms.
	 * @return A list of Answer objects, containing their respectives forces.
	 */
	@SuppressWarnings("unchecked")
	public List<Answer<Fact<Type>>> getAnswers(boolean suppress, final Predicate<Type> predicate, Type ... values)
	{
		return getAnswers(suppress, predicate, convert(values));
	}

	/**
	 * Returns a customized list of answers.
	 * If you want to customize the logic done by Gilgamesh, you can implement the Core inner class <i>Predicate</i> in order to
	 * provide your own matching implementation.
	 * @param suppress Indicates that answers should be combined, suppressing the atoms informed.
	 * @param predicate The predicate object which will determine how to approve an answer.
	 * @param values List of question atoms.
	 * @return A list of Answer objects, containing their respectives forces.
	 */
	public List<Answer<Fact<Type>>> getAnswers(boolean suppress, final Predicate<Type> predicate, List<Type> values)
	{
		return getAnswers(suppress, predicate, convert(values));
	}

	@SuppressWarnings("unchecked")
	private List<Answer<Fact<Type>>> getAnswers(final boolean suppress, final Predicate<Type> predicate, final Atom<Type> ... values)
	{
		synchronized(getLock())
		{
			HashMap<Fact<Type>, Double> results = new HashMap<Fact<Type>, Double>();
			HashSet<Atom<Type>> set = suppress? new HashSet<Atom<Type>>(Arrays.asList(values)) : null;

			if(atoms == null)
				return new ArrayList<Answer<Fact<Type>>>();

			for(Atom<Type> current : values)
			{
				if(current.value == null || !atoms.keySet().contains(current.value))
					continue;

				Atom<Type> atom = atoms.get(current.value);

				for(Fact<Type> fact : atom.getFacts())
					if(predicate.match(fact.atoms))
					{
						Fact<Type> newFact = suppress? fact.suppress(set) : fact;
						double force = results.get(newFact) == null? atom.getForce(fact) : results.get(newFact) + atom.getForce(fact);

						if(newFact.atoms != null && newFact.atoms.length > 0)
							results.put(newFact, force);
					}
			}


			TreeSet<Answer<Fact<Type>>> answers = new TreeSet<Answer<Fact<Type>>>();

			for(Fact<Type> fact : results.keySet())
				answers.add(new Answer<Fact<Type>>(fact, predicate.calc(fact, results.get(fact)), fact.time));

			return new ArrayList<Answer<Fact<Type>>>(answers.descendingSet());
		}
	}
	
	@SuppressWarnings("unchecked")
	public Atom<Type>[] getAtoms()
	{
		return atoms.values().toArray(new Atom[0]);
	}
	
	@SuppressWarnings("unchecked")
	public Fact<Type>[] getFacts()
	{
		HashSet<Fact<Type>> facts = new HashSet<Fact<Type>>();

		for(Atom<Type> atom : atoms.values())
			facts.addAll(atom.getFacts());

		return facts.toArray(new Fact[0]);
	}
	
	/**
	 * Provides some statistic data from Gilgamesh Core memory.
	 * @return A Statistics object, with some statistic information.
	 */
    public Statistics statistics()
    {
    	final ArrayList<Double> values = new ArrayList<Double>();
    	
    	if(atoms == null)
    		return new Statistics(0, 0, 0, 0);

    	for(Atom<Type> atom : atoms.values())
    		for(Fact<Type> fact : atom.getFacts())
				values.add(atom.getForce(fact));

    	double average = 0.0;
    	double sigma = 0.0;
    	double variance = 0.0;
    	
    	for(double value : values)
    		average += value;
    	
    	average /= values.size();
    	
    	for(double value : values)
    		variance += Math.pow(average - value, 2);
    	
    	variance /= values.size();
    	sigma = Math.sqrt(variance);
    	sigma = (sigma!=sigma)? 0.0 : sigma; // NaN
    	
    	return new Statistics(values.size(), average, variance, sigma);
    };
    
	
    /**
     * Class to validate an answer.
     * This class is used to validate when a fact matches to the question. Can be customized to receive other validations.
     * @author Eduardo Alevi
     * @param <Type> The atom type.
     */
	@SuppressWarnings("unchecked")
	public static abstract class Predicate<Type extends Comparable<Type>>
	{
		public abstract boolean match(Atom<Type> ... answer);
		public abstract double calc(Fact<Type> fact, double force);
	}
	
	/**
	 * Hold some statistic data.
	 * @author Eduardo Alevi
	 */
	public static class Statistics
	{
		public final int counter;
		public final double average;
		public final double variance;
		public final double sigma;
		
		public Statistics(int counter, double average, double variance, double sigma)
		{
			this.counter = counter;
			this.average = average;
			this.variance = variance;
			this.sigma = sigma;
		}
		
		@Override
		public String toString()
		{
			return String.format("Counter:[%d], Average:[%.2f], Variance:[%.2f], Sigma:[%.2f]", counter, average, variance, sigma);
		}
	}
}































