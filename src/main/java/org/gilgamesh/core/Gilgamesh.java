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

package org.gilgamesh.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Main factory class. It creates instances of Gilgamesh Core (memory) for use.
 * 
 * @author Eduardo Alevi
 */
@SuppressWarnings("unchecked")
public class Gilgamesh<T extends Serializable> implements Serializable {

	
	public static final long serialVersionUID = 100L;

	private ConcurrentHashMap<T, Atom<T>> atoms = new ConcurrentHashMap<T, Atom<T>>();
	private ConcurrentSkipListMap<Long, Fact<T>> facts = new ConcurrentSkipListMap<Long, Fact<T>>();
	
	
	
	
	
	
	
	
	

	private Atom<T> refresh(Atom<T> atom) {

		if(atoms.containsKey(atom.value))
			return atoms.get(atom.value);
		
		atoms.put(atom.value, atom);
		return atom;
	}
	
	private Fact<T> refresh(Fact<T> fact) {

		if(facts.containsValue(fact)) {

			Iterator<Fact<T>> iterator = facts.values().iterator();
			
			while(iterator.hasNext()) {
				
				Fact<T> current = iterator.next();
				
				if(current.equals(fact))
					return current;
			}
		}
		
		long id = 1 + (facts.size() > 0? facts.descendingKeySet().iterator().next() : 0l);
		fact = new Fact<T>(id, 0.0, fact.time, fact.atoms);
		facts.put(id, fact);
		
		return fact;
	}
	
	
	/**
	 * Saves the Gilgamesh Core memory into a file.
	 * 
	 * @param file The file to be saved. If exists, it will be overridden.
	 * @param core The Gilgamesh Core object to be saved.
	 */
	public synchronized static <T extends Serializable> boolean save(File file, Gilgamesh<T> gilgamesh) {

		try {
			
			if (file == null || file.isDirectory())
				return false;

			ObjectOutputStream output = null;

			output = new ObjectOutputStream(new FileOutputStream(file));
			output.writeObject(gilgamesh);
			output.close();

			Logger.getGlobal().log(Level.FINER, "Gilgamesh Core saved in " + file.getAbsolutePath());
			return true;
		}
		catch (Exception e) {
			e.printStackTrace();
			Logger.getGlobal().log(Level.SEVERE, "Cannot save Gilgamesh in file " + file);
			return false;
		}
	}

	/**
	 * Loads a Gilgamesh Core memory binary file.
	 * 
	 * @param <Type> the core type to be created.
	 * @param file The file to be read.
	 * @return The Gilgamesh Core object.
	 */
	public synchronized static <T extends Serializable> Gilgamesh<T> load(File file) {

		try {
			
			if (file == null || !file.exists() || file.isDirectory())
				return null;

			ObjectInput input = null;

			input = new ObjectInputStream(new FileInputStream(file));
			Gilgamesh<T> core = (Gilgamesh<T>) input.readObject();
			input.close();
			Logger.getGlobal().log(Level.FINER, "Gilgamesh Core loaded from " + file.getAbsolutePath());
			
			return core;
		}
		catch (Exception e) {
			e.printStackTrace();
			Logger.getGlobal().log(Level.SEVERE, "Cannot load Gilgamesh Core memory from file " + file);
			
			return null;
		}
	}

	/**
	 * Return the software version.
	 * 
	 * @return The current version.
	 */
	public static String getVersion() {

		return String.format("%.2f", serialVersionUID / 100.0);
	}



	/**
	 * Reinforce a fact, with +1 to force.
	 * 
	 * @param values List of Type atoms to make a fact.
	 */
	public void reinforce(T ... values) {

		fact(1.0, Atom.convert(values));
	}

	/**
	 * Reinforce a fact, with +1 to force.
	 * 
	 * @param values List of Type atoms to make a fact.
	 */
	public void reinforce(List<Atom<T>> values) {

		fact(1.0, Atom.convert(values));
	}

	/**
	 * Reinforce a fact, with +1 to force.
	 * 
	 * @param values The atoms which will make a fact.
	 */
	public void reinforce(Atom<T> ... values) {

		fact(1.0, values);
	}

	/**
	 * Punish a fact, with -1 to force.
	 * 
	 * @param values List of Type atoms to make a fact.
	 */
	public void punish(List<Atom<T>> values) {

		fact(-1.0, Atom.convert(values));
	}

	/**
	 * Punish a fact, with -1 to force.
	 * 
	 * @param values
	 *            List of Type atoms to make a fact.
	 */
	public void punish(Atom<T> ... values) {

		fact(-1.0, values);
	}

	/**
	 * Punish a fact, with -1 to force.
	 * 
	 * @param values
	 *            List of Type atoms to make a fact.
	 */
	public void punish(T ... values) {

		fact(-1.0, Atom.convert(values));
	}

	/**
	 * Make a explicit fact, with a defined force.
	 * 
	 * @param force The force to be defined to the fact (or summed if the fact exists).
	 * @param values List with atoms used to make that fact.
	 */
	public void fact(double force, List<Atom<T>> values) {

		fact(force, Atom.convert(values));
	}

	/**
	 * Make a explicit fact, with a defined force.
	 * 
	 * @param force The force to be defined to the fact (or summed if the fact exists).
	 * @param values Atoms to be used to make that fact.
	 */
	public void fact(double force, Atom<T> ... values) {
		
		fact(new Fact<T>(force, Atom.convert(values)));
	}
	
	/**
	 * Make a explicit fact, with a defined force.
	 * 
	 * @param force The force to be defined to the fact (or summed if the fact exists).
	 * @param values Atoms to be used to make that fact.
	 */
	public void fact(double force, T ... values) {
		
		fact(new Fact<T>(force, values));
	}
	
	/**
	 * Include a fact.
	 * 
	 * @param fact The fact to be included.
	 */
	private synchronized void fact(Fact<T> fact) {

		Fact<T> current = refresh(fact);
		
		current.force += fact.force;
		
		for (Atom<T> atom : current.toAtoms())
			refresh(atom).add(current.id);
	}

	/**
	 * Delete a fact.
	 * 
	 * @return true if the fact was removed. false otherwise.
	 * @param values The atoms list of the fact.
	 */
	public boolean remove(List<Atom<T>> values) {

		return remove(Atom.convert(values));
	}

	/**
	 * Delete a fact.
	 * 
	 * @return true if the fact was removed. false otherwise.
	 * @param values The atoms of the fact.
	 */
	public boolean remove(Atom<T> ... values) {

		for (Atom<T> atom : values)
			atoms.remove(atom.value);
		return true;
	}

	/**
	 * Return an single and most probable answer.
	 * 
	 * @param values The question atoms you want to ask.
	 * @return The answer atoms.
	 */
	public Fact<T> answer(T ... values) {

		TreeSet<Fact<T>> list = getAnswers(false, false, values);
		
		if (list.size() > 0)
			return list.iterator().next();

		list = getAnswers(true, false, values);
		
		if (list.size() > 0) {
			Fact<T> fact = list.iterator().next();
			return new Fact<T>(0, fact.force / (double) values.length, fact.time, fact.atoms);
		}

		return null;
	}

	/**
	 * Return an single and most probable answer.
	 * 
	 * @param values A list of question atoms.
	 * @return The answer atoms, in the form of Answer class which contains the forces.
	 */
	public Fact<T> answer(List<T> values) {
		
		Fact<T> answer = answer(Atom.convert(values));
		
		if (answer != null)
			return answer;

		return null;
	}

	/**
	 * Search for a deep answer, using the first answer as question to the next question.
	 * @param matchAny true if the answers shoud consider any match
	 * @param values question values
	 * @return the most probable answer
	 */
	public Fact<T> answerDeep(boolean matchAny, List<T> values) {
		
		return answerDeep(matchAny, Atom.convert(values));
	}
	
	/**
	 * Search for a deep answer, using the first answer as question to the next question.
	 * @param matchAny true if the answers shoud consider any match
	 * @param values question values
	 * @return the most probable answer
	 */
	public Fact<T> answerDeep(boolean matchAny, T ... values) {
		
		LinkedHashSet<Fact<T>> memory = new LinkedHashSet<Fact<T>>();
		Fact<T> fact = new Fact<T>(values);
		
		while(!memory.contains(fact)) {
			
			memory.add(fact);
			
			TreeSet<Fact<T>> answers = getAnswers(matchAny, true, fact.atoms);
			
			if(answers.size() <= 0)
				break;
			
			Fact<T> next = answers.iterator().next();
			
			if(memory.contains(next))
				break;
			
			fact = next;
		}

		return fact;
	}
	
	/**
	 * Returns a customized list of answers. If you want to customize the logic
	 * done by Gilgamesh, you can implement the Core inner class
	 * <i>Predicate</i> in order to provide your own matching implementation.
	 * 
	 * @param suppress Indicates that answers should be combined, suppressing the atoms informed.
	 * @param predicate The predicate object which will determine how to approve an answer.
	 * @param questionAtoms The question atoms.
	 * @return A list of Answer objects, containing their respectives forces.
	 */
	public TreeSet<Fact<T>> getAnswers(boolean matchAny, boolean suppress, Atom<T> ... questionAtoms) {
		return getAnswers(matchAny, suppress, Atom.convert(questionAtoms));
	}
	
	/**
	 * Returns a customized list of answers. If you want to customize the logic
	 * done by Gilgamesh, you can implement the Core inner class
	 * <i>Predicate</i> in order to provide your own matching implementation.
	 * 
	 * @param suppress Indicates that answers should be combined, suppressing the atoms informed.
	 * @param predicate The predicate object which will determine how to approve an answer.
	 * @param questionValues The question atoms.
	 * @return A list of Answer objects, containing their respectives forces.
	 */
	public TreeSet<Fact<T>> getAnswers(boolean matchAny, boolean suppress, List<T> questionValues) {
		T array[] = (T[]) Array.newInstance(questionValues.get(0).getClass(), questionValues.size());
		return getAnswers(matchAny, suppress, array);
	}
	
	/**
	 * Returns a customized list of answers. If you want to customize the logic
	 * done by Gilgamesh, you can implement the Core inner class
	 * <i>Predicate</i> in order to provide your own matching implementation.
	 * 
	 * @param suppress Indicates that answers should be combined, suppressing the atoms informed.
	 * @param predicate The predicate object which will determine how to approve an answer.
	 * @param questionAtoms The question atoms.
	 * @return A list of Answer objects, containing their respectives forces.
	 */
	public TreeSet<Fact<T>> getAnswers(boolean matchAny, boolean suppress, T ... questionValues) {

		TreeSet<Fact<T>> answers = new TreeSet<Fact<T>>(Collections.reverseOrder());

		if (atoms == null || atoms.size() <= 0)
			return answers;

		LinkedHashSet<Atom<T>> questionAtoms = new LinkedHashSet<Atom<T>>();

		for (T questionValue : questionValues) {
			
			if (questionValue == null || !atoms.containsKey(questionValue))
				continue;
			
			questionAtoms.add(atoms.get(questionValue));
		}

		
		for (Atom<T> questionAtom : questionAtoms) {
			for (long factID : questionAtom.getFacts()) {

				Fact<T> memoryFact = facts.get(factID);
				
				//if(suppress && question.equals(memoryFact))
					//continue;
				
				double matches = 0.0;
				
				for (Atom<T> a1 : questionAtoms) {
					for (T v2 : memoryFact.atoms)
						matches += a1.equality(atoms.get(v2));
				}

				if(!matchAny && questionValues.length == matches || matchAny) {
					
					double force = matches * memoryFact.force;
					T[] array = suppress? memoryFact.suppress(questionValues) : memoryFact.atoms;
					
					if(array != null && array.length > 0)
						answers.add(new Fact<T>(0, force, memoryFact.time, array));
				}
			}
		}

		return answers;
	}

	public Atom<T>[] getAtoms() {

		return atoms.values().toArray(new Atom[0]);
	}

	public Fact<T>[] getFacts() {

		return facts.values().toArray(new Fact[0]);
	}

	/**
	 * Provides some statistic data from Gilgamesh Core memory.
	 * 
	 * @return A Statistics object, with some statistic information.
	 */
	public Statistics statistics() {

		final ArrayList<Double> values = new ArrayList<Double>();

		if (atoms == null || atoms.size() <= 0)
			return new Statistics(0, 0, 0, 0);

		for (Atom<T> atom : atoms.values())
			for (long factID : atom.getFacts())
				values.add(facts.get(factID).force);

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
