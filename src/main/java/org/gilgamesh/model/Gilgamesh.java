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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;



/**
 * The Gilgamesh core. This is the main class to manage AI data. You should
 * create one of it for each context you have.
 * 
 * @author Eduardo Alevi
 * @param  The generic type you want to work on.
 */
public class Gilgamesh {

	private static final String serialVersionUID = "0.21b";
	private Memory memory;
	
	/**
	 * Creates a new Gilgamesh instance.
	 */
	public Gilgamesh() {
		this(new Memory());
	}
	
	/**
	 * Creates a new Gilgamesh instance using a previous existing memory.
	 * @param memory
	 */
	public Gilgamesh(Memory memory) {
		this.memory = memory;
	}
	

	/**
	 * Saves the Gilgamesh Core memory into a file.
	 * 
	 * @param file
	 *            The file to be saved. If exists, it will be overridden.
	 * @param memory
	 *            The Gilgamesh memory object to be saved.
	 */
	public void save(File file) {

		try {
			if (file == null || file.isDirectory())
				return;

			ObjectOutputStream output = null;

			synchronized(memory) {
				try {
					BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
					output = new ObjectOutputStream(bos);
					output.writeObject(memory);
				}
				finally {
					if (output != null)
						output.close();
				}
			}

			Logger.getGlobal().log(Level.FINER, "Gilgamesh Core saved in " + file.getAbsolutePath());
		}
		catch (Exception e) {
			e.printStackTrace();
			Logger.getGlobal().log(Level.SEVERE, "Cannot save Gilgamesh Core memory to the file " + file);
		}
	}

	/**
	 * Loads a Gilgamesh Core memory binary file.
	 * 
	 * @param <Type>
	 *            the core type to be created.
	 * @param file
	 *            The file to be read.
	 * @return The Gilgamesh Core object.
	 */
	public void load(File file) {

		try {
			if (file == null || !file.exists() || file.isDirectory())
				throw new FileNotFoundException("File [" + file + "] not found");

			ObjectInput input = null;

			synchronized(memory) {
				try {

					BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
					input = new ObjectInputStream(bis);
					memory = (Memory) input.readObject();
					Logger.getGlobal().log(Level.FINER, "Gilgamesh memory loaded from " + file.getAbsolutePath());
				}
				finally {

					if (input != null)
						input.close();
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			Logger.getGlobal().log(Level.SEVERE, "Cannot load Gilgamesh memory from file " + file);
		}
	}

	/**
	 * Return the software version.
	 * 
	 * @return The current version.
	 */
	public static String getVersion() {

		return String.format("0.%db", serialVersionUID);
	}

	/**
	 * Learn a fact, with no force applied.
	 * 
	 * @param values
	 *            List of Type atoms to make a fact.
	 */
	@SuppressWarnings("unchecked")
	public <T> void learn(T ... values) {

		fact(0.0, convert(values));
	}

	/**
	 * Learn a fact, with no force applied
	 * 
	 * @param values
	 *            List of Type atoms to make a fact.
	 */
	public void learn(List<Atom<?>> values) {

		fact(0.0, convert(values));
	}

	/**
	 * Learn a fact, with no force applied
	 * 
	 * @param values
	 *            The atoms which will make a fact.
	 */
	public void learn(Atom<?> ... values) {

		fact(0.0, values);
	}

	/**
	 * Reinforce a fact, with +1 to force.
	 * 
	 * @param values
	 *            List of Type atoms to make a fact.
	 */
	@SuppressWarnings("unchecked")
	public <T> void reinforce(T ... values) {

		fact(1.0, convert(values));
	}

	/**
	 * Reinforce a fact, with +1 to force.
	 * 
	 * @param values
	 *            List of Type atoms to make a fact.
	 */
	public void reinforce(List<Atom<?>> values) {

		fact(1.0, convert(values));
	}

	/**
	 * Reinforce a fact, with +1 to force.
	 * 
	 * @param values
	 *            The atoms which will make a fact.
	 */
	public void reinforce(Atom<?> ... values) {

		fact(1.0, values);
	}

	/**
	 * Punish a fact, with -1 to force.
	 * 
	 * @param values
	 *            List of Type atoms to make a fact.
	 */
	public void punish(List<Atom<?>> values) {

		fact(-1.0, convert(values));
	}

	/**
	 * Punish a fact, with -1 to force.
	 * 
	 * @param values
	 *            List of Type atoms to make a fact.
	 */
	public void punish(Atom<?> ... values) {

		fact(-1.0, values);
	}

	/**
	 * Punish a fact, with -1 to force.
	 * 
	 * @param values
	 *            List of Type atoms to make a fact.
	 */
	@SuppressWarnings("unchecked")
	public <Type> void punish(Type ... values) {

		fact(-1.0, convert(values));
	}

	/**
	 * Make a explicit fact, with a defined force.
	 * 
	 * @param force The force to be defined to the fact (or summed if the fact exists).
	 * @param values List with atoms used to make that fact.
	 */
	public void fact(double force, List<Atom<?>> values) {

		fact(force, convert(values));
	}

	/**
	 * Make a explicit fact, with a defined force.
	 * 
	 * @param force The force to be defined to the fact (or summed if the fact exists).
	 * @param values Atoms to be used to make that fact.
	 */
	public void fact(double force, Atom<?> ... values) {
		
		fact(new Fact(force, values));
	}
	
	/**
	 * Make a explicit fact, with a defined force.
	 * 
	 * @param force The force to be defined to the fact (or summed if the fact exists).
	 * @param values Atoms to be used to make that fact.
	 */
	@SuppressWarnings("unchecked")
	public <Type> void fact(double force, Type ... values) {
		
		fact(new Fact(force, convert(values)));
	}
	
	/**
	 * Include a fact.
	 * 
	 * @param fact The fact to be included.
	 */
	public void fact(Fact fact) {

		synchronized (this) {
			for (Atom<?> atom : fact.getAtoms())				
				memory.link(atom, fact);
		}
	}

	/**
	 * Unlearn and delete a fact.
	 * 
	 * @return true if the fact was removed. false otherwise.
	 * @param values The atoms list of the fact.
	 */
	@SuppressWarnings("unchecked")
	public <Type> boolean forget(Type ... values) {

		return forget(convert(values));
	}

	/**
	 * Unlearn and delete a fact.
	 * 
	 * @return true if the fact was removed. false otherwise.
	 * @param values The atoms list of the fact.
	 */
	public boolean forget(List<Atom<?>> values) {

		return forget(convert(values));
	}

	/**
	 * Unlearn and delete a fact.
	 * 
	 * @return true if the fact was removed. false otherwise.
	 * @param values The atoms of the fact.
	 */
	public boolean forget(Atom<?> ... values) {

		synchronized (this) {
			for (Atom<?> atom : values)
				memory.remove(atom);
			return true;
		}
	}

	/**
	 * Return an single and most probable answer.
	 * 
	 * @param values
	 *            The question atoms you want to ask.
	 * @return The answer atoms.
	 */
	public Fact answer(Atom<?> ... values) {

		Fact answer = answer(Arrays.asList(values));
		
		if (answer != null)
			return answer;

		return null;
	}

	/**
	 * Return an single and most probable answer.
	 * 
	 * @param values
	 *            The question atoms you want to ask.
	 * @return The answer atoms.
	 */
	@SuppressWarnings("unchecked")
	public <Type> Fact answer(Type ... values) {

		Fact answer = answer(convert(values));
		
		if (answer != null)
			return answer;

		return null;
	}

	/**
	 * Return an single and most probable answer.
	 * 
	 * @param values
	 *            A list of question atoms.
	 * @return The answer atoms, in the form of Answer class which contains the
	 *         forces.
	 */
	public Fact answer(List<Atom<?>> values) {

		TreeSet<Fact> list = getAnswers(false, false, values);
		
		if (list.size() > 0)
			return list.iterator().next();

		list = getAnswers(true, false, values);
		
		if (list.size() > 0) {
			Fact fact = list.iterator().next();
			return new Fact(fact.force / (double) values.size(), fact.getTime(), fact.getAtoms());
		}

		return null;
	}

	/**
	 * Get a list of answers for a question.
	 * 
	 * @param suppress Indicates that answers should suppress question atoms.
	 * @param matchAny
	 *            true if the answer should contains any question atoms. false
	 *            if the answers should contains all question atoms.
	 * @param values
	 *            Question atoms.
	 * @return A list of Answer objects, containing the answers with their
	 *         respective forces. The list is ordered from the most probable to
	 *         the less probable.
	 */
	@SuppressWarnings("unchecked")
	public <Type> TreeSet<Fact> getAnswers(boolean matchAny, boolean suppress, Type ... values) {
		return getAnswers(matchAny, suppress, convert(values));
	}
	
	/**
	 * Get a list of answers for a question.
	 * 
	 * @param suppress Indicates that answers should suppress question atoms.
	 * @param matchAny
	 *            true if the answer should contains any question atoms. false
	 *            if the answers should contains all question atoms.
	 * @param values
	 *            Question atoms.
	 * @return A list of Answer objects, containing the answers with their
	 *         respective forces. The list is ordered from the most probable to
	 *         the less probable.
	 */
	public TreeSet<Fact> getAnswers(boolean matchAny, boolean suppress, Atom<?> ... values) {

		if (matchAny)
			return getAnswersOR(suppress, values);
		else
			return getAnswersAND(suppress, values);
	}

	/**
	 * Returns a customized list of answers. If you want to customize the logic
	 * done by Gilgamesh, you can implement the Core inner class
	 * <i>Predicate</i> in order to provide your own matching implementation.
	 * 
	 * @param suppress
	 *            Indicates that answers should be combined, suppressing the
	 *            atoms informed.
	 * @param predicate
	 *            The predicate object which will determine how to approve an
	 *            answer.
	 * @param values
	 *            List of question atoms.
	 * @return A list of Answer objects, containing their respectives forces.
	 */
	public TreeSet<Fact> getAnswers(final Predicate predicate, List<Atom<?>> values) {

		return getAnswers(predicate, convert(values));
	}

	/**
	 * Get a list of answers for a question.
	 * 
	 * @param suppress
	 *            Indicates that answers should be combined, suppressing the
	 *            atoms informed.
	 * @param matchAny
	 *            true if the answer should contains any question atoms. false
	 *            if the answers should contains all question atoms.
	 * @param values
	 *            List of question atoms.
	 * @return A list of Answer objects, containing the answers with their
	 *         respective forces. The list is ordered from the most probable to
	 *         the less probable.
	 */
	public TreeSet<Fact> getAnswers(boolean matchAny, boolean suppress, final List<Atom<?>> values) {

		if (matchAny)
			return getAnswersOR(suppress, convert(values));
		else
			return getAnswersAND(suppress, convert(values));
	}

	/**
	 * Get a list of answers for a question. This method provides the same
	 * answer as getAnswer(false, atoms), except for the fact that the answer
	 * has to have all question atoms.
	 * 
	 * @param suppress
	 *            Indicates that answers should be combined, suppressing the
	 *            atoms informed.
	 * @param values
	 *            Question atoms.
	 * @return A list of Answer objects, containing the answers with their
	 *         respective forces. The list is ordered from the most probable to
	 *         the less probable.
	 */
	public TreeSet<Fact> getAnswersFixed(final boolean suppress, final Atom<?> ... values) {

		return getAnswers(new Predicate() {

			public double match(Atom<?> ... atoms) {

				if (values.length != atoms.length)
					return 0.0;

				loop:
				for (Atom<?> value : values) {
					for (Atom<?> atom : atoms)
						if (atom.equals(value))
							continue loop;
					return 0.0;
				}

				return 1.0;
			}

			@Override
			public Fact getAnswer(Fact fact, double force) {
				
				Fact answer = new Fact(force / values.length, fact.getTime(), fact.getAtoms());
				return suppress? answer.suppress(values) : answer;
			}
			
		}, values);
	}

	private TreeSet<Fact> getAnswersAND(final boolean suppress, final Atom<?> ... values) {

		return getAnswers(new Predicate() {

			public double match(Atom<?> ... atoms) {

				loop:
				for (Atom<?> value : values) {
					for (Atom<?> atom : atoms)
						if (atom.equals(value))
							continue loop;
					return 0.0;
				}

				return 1.0;
			}

			@Override
			public Fact getAnswer(Fact fact, double force) {
			
				Fact answer = new Fact(force / values.length, fact.getTime(), fact.getAtoms());
				return suppress? answer.suppress(values) : answer;
			}
			
			
		}, values);
	}

	private TreeSet<Fact> getAnswersOR(final boolean suppress, final Atom<?> ... values) {

		return getAnswers(new Predicate() {

			public double match(Atom<?> ... atoms) {

				for (Atom<?> value : values) {
					for (Atom<?> atom : atoms)
						if (atom.equals(value))
							return 1.0;
				}

				return 0.0;
			}

			@Override
			public Fact getAnswer(Fact fact, double force) {
			
				double matches = fact.matches(values);
				matches /= (double) fact.getAtoms().length;
				Fact answer = new Fact(force * matches, fact.getTime(), fact.getAtoms());
				return suppress? answer.suppress(values) : answer;
			}
			
		}, values);
	}

	/**
	 * Get a list of answers for a question, using an estimation according Atom.equality() method.<br/><br/>
	 * To have a estimated match between 0.0 and 1.0, all facts have to be informed using an custom Atom<?> class, 
	 * overriding the equality() method.
	 * @param suppress Indicates that answers should suppress question atoms.
	 * @param values
	 * @return the list of best estimated answers
	 */
	@SuppressWarnings("unchecked")
	public <Type> TreeSet<Fact> getAnswersEstimated(boolean suppress, Type ... values) {
		return getAnswersEstimated(suppress, convert(values));
	}
	
	/**
	 * Get a list of answers for a question, using an estimation according Atom.equality() method.<br/><br/>
	 * To have a estimated match between 0.0 and 1.0, all facts have to be informed using an custom Atom<?> class, 
	 * overriding the equality() method.
	 * @param suppress Indicates that answers should suppress question atoms.
	 * @param values
	 * @return the list of best estimated answers
	 */
	public TreeSet<Fact> getAnswersEstimated(boolean suppress, List<Atom<?>> values) {
		return getAnswersEstimated(suppress, convert(values));
	}
	
	/**
	 * Get a list of answers for a question, using an estimation according Atom.equality() method.<br/><br/>
	 * To have a estimated match between 0.0 and 1.0, all facts have to be informed using an custom Atom<?> class, 
	 * overriding the equality() method.
	 * @param suppress Indicates that answers should suppress question atoms.
	 * @param values
	 * @return the list of best estimated answers
	 */
	public TreeSet<Fact> getAnswersEstimated(final boolean suppress, final Atom<?> ... values) {
		
		return getAnswers(new Predicate() {

			public double match(Atom<?> ... atoms) {

				double total = 0.0;
				
				for (Atom<?> atom : atoms) {
					
					double partial = 0.0;
					
					for (Atom<?> value : values)
						partial += atom.equality(value);
					
					total += partial / atoms.length;
				}

				return total;
			}

			@Override
			public Fact getAnswer(Fact fact, double force) {
				
				Fact answer = new Fact(force / values.length, fact.getTime(), fact.getAtoms());
				return suppress? answer.suppress(values) : answer;
			}
			
		}, values);
	}
	

	/**
	 * Returns a customized list of answers. If you want to customize the logic
	 * done by Gilgamesh, you can implement the Core inner class
	 * <i>Predicate</i> in order to provide your own matching implementation.
	 * 
	 * @param suppress
	 *            Indicates that answers should be combined, suppressing the
	 *            atoms informed.
	 * @param predicate
	 *            The predicate object which will determine how to approve an
	 *            answer.
	 * @param values
	 *            The question atoms.
	 * @return A list of Answer objects, containing their respectives forces.
	 */
	private TreeSet<Fact> getAnswers(final Predicate predicate, final Atom<?> ... values) {

		TreeSet<Fact> answers = new TreeSet<Fact>(Collections.reverseOrder());

		if (memory.getTotalAtoms() <= 0)
			return answers;

		synchronized (this) {
			
			HashSet<Fact> results = new HashSet<Fact>();
			
			for (Atom<?> current : values) {
				
				if (current == null || !memory.contains(current))
					continue;

				Atom<?> atom = memory.load(current);
				
				if(atom == null)
					continue;

				for (Fact fact : memory.getFacts(atom)) {
				
					double match = predicate.match(fact.getAtoms());
					
					if (match > 0.0)
						results.add(predicate.getAnswer(fact, atom.getForce(fact) * match));
				}
			}

			answers.addAll(results);
			
			return answers;
		}
	}

	/**
	 * Converts a list of values in an Atom array.
	 * @param values to be converted
	 * @return an array of Atoms
	 */
	public static Atom<?>[] convert(List<Atom<?>> values) {

		Atom<?> array[] = new Atom[values.size()];

		for (int i = 0; i < values.size(); i++)
			array[i] = values.get(i);

		return array;
	}

	/**
	 * Converts a array of values in an Atom array.
	 * @param values to be converted
	 * @return an array of Atoms
	 */
	@SuppressWarnings("unchecked")
	public static <Type> Atom<Type>[] convert(Type ... values) {

		Atom<Type> array[] = new Atom[values.length];

		for (int i=0; i<values.length; i++)
			array[i] = new Atom<Type>(values[i]);

		return array;
	}
	
	public Memory getMemory() {
		
		return memory;
	}

	/**
	 * Provides some statistic data from Gilgamesh Core memory.
	 * 
	 * @return A Statistics object, with some statistic information.
	 */
	public Statistics statistics() {

		final ArrayList<Double> values = new ArrayList<Double>();

		Collection<Atom<?>> atoms = memory.getAtoms();
		
		if (atoms == null || atoms.size() <= 0)
			return new Statistics(0, 0, 0, 0);

		for (Atom<?> atom : atoms)
			for (Fact fact : memory.getFacts(atom))
				values.add(fact.force);

		double average = 0.0;
		double sigma = 0.0;
		double variance = 0.0;

		for (double value : values)
			average += value;

		average /= values.size();

		for (double value : values)
			variance += Math.pow(average - value, 2);

		variance /= values.size();
		sigma = Math.sqrt(variance);
		sigma = (sigma != sigma) ? 0.0 : sigma; // NaN

		return new Statistics(values.size(), average, variance, sigma);
	};


}
