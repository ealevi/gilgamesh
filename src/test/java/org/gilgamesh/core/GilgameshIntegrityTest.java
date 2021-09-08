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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ Gilgamesh.class })
@SuppressWarnings("unchecked")
public class GilgameshIntegrityTest {

	private int counter;
	private static final double PRECISION = 0.0001;


	@Test
	public void testIntegrity() throws Exception {

		Gilgamesh<String> core = new Gilgamesh<String>();
		
		Statistics statistics = core.statistics();
		assertEquals(0.0, statistics.average, 0.0001);
		assertEquals(0.0, statistics.counter, 0.0001);
		assertEquals(0.0, statistics.sigma, 0.0001);
		assertEquals(0.0, statistics.variance, 0.0001);

		core.reinforce("cat", "eats", "ration", "tasty");
		core.reinforce("cat", "eats", "ration", "tasty");
		core.reinforce("cat", "eats", "ration", "tasty");
		core.reinforce("cat", "eats", "ration", "tasty");
		core.reinforce("cat", "eats", "ration", "tasty");
		core.reinforce("rat", "eats", "food", "tasty");
		core.reinforce("rat", "eats", "food", "bad");
		core.reinforce("rat", "eats", "food", "bad");
		core.reinforce("rat", "eats", "food", "bad");
		core.reinforce("dog", "eats", "food", "bad");
		core.reinforce("dog", "eats", "food", "tasty");
		core.reinforce("dog", "eats", "food", "tasty");
		core.reinforce("cat", "died");

		String atoms[][] = {
				{"cat", "eats", "ration", "tasty"}, 
				{"dog", "eats", "food", "tasty"}, 
				{"rat", "eats", "food", "tasty"}};
		
		double forces[] = {10, 4, 2};
		Set<Fact<String>> answers = core.getAnswers(false, false, "eats", "tasty");

		check(true, answers, atoms, forces);

		atoms = new String[][] {
			{"cat", "eats", "ration", "tasty"}, 
			{"dog", "eats", "food", "tasty"}, 
			{"dog", "eats", "food", "bad"},
			{"rat", "eats", "food", "tasty"}};
			
		forces = new double[]{5, 4, 1, 1};
		answers = core.getAnswers(true, false, "tasty", "dog");
		check(true, answers, atoms, forces);

		atoms = new String[][]{{"rat", "bad"}, {"dog", "tasty"}, {"dog", "bad"}, {"rat", "tasty"}};
		forces = new double[]{6, 4, 2, 2};
		answers = core.getAnswers(false, true, "eats", "food");
		check(true, answers, atoms, forces);

		atoms = new String[][] {{"rat", "bad"}, {"cat", "ration", "tasty"}, {"dog", "tasty"}, {"dog", "bad"}, {"rat", "tasty"}};
		forces = new double[]{6, 5, 4, 2, 2};
		answers = core.getAnswers(true, true, "eats", "food");
		check(true, answers, atoms, forces);

		core.punish("cat", "eats", "ration", "tasty");
		core.punish("cat", "eats", "ration", "tasty");
		core.punish("cat", "eats", "ration", "tasty");
		core.punish("cat", "eats", "ration", "tasty");
		core.punish("cat", "eats", "ration", "tasty");
		core.punish("cat", "eats", "ration", "tasty");
		core.punish("cat", "eats", "ration", "tasty");
		core.punish("cat", "eats", "ration", "tasty");
		core.punish("rat", "eats", "food", "tasty");
		core.punish("rat", "eats", "food", "bad");
		core.punish("rat", "eats", "food", "bad");
		core.punish("dog", "eats", "food", "tasty");
		core.punish("cat", "died");

		atoms = new String[][] {
			{"dog", "eats", "food", "tasty"}, 
			{"dog", "eats", "food", "bad"},
			{"rat", "eats", "food", "tasty"}, 
			{"cat", "eats", "ration", "tasty"}};
			
		forces = new double[]{2, 1, 0, -3};
		answers = core.getAnswers(true, false, "tasty", "dog");
		check(true, answers, atoms, forces);

		core = new Gilgamesh<String>();
		atoms = new String[][] {{"P+", "V-", "U+", "FV+", "FU+"}, {"P+", "V-", "U+"}, {"P+", "V-", "U+", "FP-"}};
		forces = new double[]{1, 0, -1};
		core.fact(0.0, new String[] {"P+", "V-", "U+"});
		core.reinforce(new String[] {"P+", "V-", "U+", "FV+", "FU+"});
		core.punish(new String[] {"P+", "V-", "U+", "FP-"});
		answers = core.getAnswers(true, false, "P-", "V+", "U+");
		check(true, answers, atoms, forces);

		atoms = new String[][] {{"P-", "V-", "U-", "FP+", "FU+"}, {"P+", "V-", "U+", "FV+", "FU+"},	{"P-", "V-", "U-"}, {"P+", "V-", "U+"},
				{"P-", "V-", "U-", "FV-"}, {"P+", "V-", "U+", "FP-"}};
		forces = new double[]{2, 2, 0, 0, -2, -2};
		core.fact(0.0, new String[] {"P-", "V-", "U-"});
		core.reinforce(new String[] {"P-", "V-", "U-", "FP+", "FU+"});
		core.punish(new String[] {"P-", "V-", "U-", "FV-"});
		answers = core.getAnswers(true, false, "P-", "V-", "U+");
		check(true, answers, atoms, forces);

		statistics = core.statistics();
		assertEquals(0.08333333, statistics.average, 0.0001);
		assertEquals(24.0000000, statistics.counter, 0.0001);
		assertEquals(0.8620067027323833, statistics.sigma, 0.000000001);
		assertEquals(0.7430555555555555, statistics.variance, 0.0001);
		
		atoms = new String[][] {{"P-", "V-", "U+", "FU+"}, {"P-", "V-", "U-", "FP+", "FU+"}, {"P+", "V-", "U+", "FV+", "FU+"},
				{"P-", "V-", "U+"},{"P-", "V-", "U-"}, {"P+", "V-", "U+"}, {"P-", "V-", "U-", "FV-"}, {"P+", "V-", "U+", "FP-"},
				{"P-", "V-", "U+", "FP-", "FV-"}};
		forces = new double[]{2, 1, 1, 0, 0, 0, -1, -1, -2};
		core.fact(0.0, new String[] {"P-", "V-", "U+"});
		core.reinforce(new String[] {"P-", "V-", "U+", "FU+"});
		core.punish(new String[] {"P-", "V-", "U+", "FP-", "FV-"});
		answers = core.getAnswers(true, false, "P-", "V+", "U+");
		check(true, answers, atoms, forces);

		atoms = new String[][] {{"P-", "V-", "U-", "FP+", "FU+"}, {"P-", "V-", "U+", "FU+"}, {"P+", "V-", "U+", "FV+", "FU+"},
				{"P-", "V-", "U+"}, {"P-", "V-", "U-"}, {"P+", "V-", "U+"}, {"P+", "V-", "U+", "FP-"}, {"P-", "V-", "U+", "FP-", "FV-"}, {"P-", "V-", "U-", "FV-"}};
		forces = new double[]{3, 2, 1, 0, 0, 0, -1, -2, -3};
		core.fact(0.0, new String[] {"P+", "V+", "U+"});
		core.reinforce(new String[] {"P+", "V+", "U+", "FP+"});
		core.punish(new String[] {"P+", "V+", "U+", "FV-", "FU-"});
		answers = core.getAnswers(true, false, "P-", "V-", "U-");
		check(true, answers, atoms, forces);

		core = new Gilgamesh<String>();
		atoms = new String[][] {{"A", "B", "C"}, {"X", "Y", "B"}, {"H", "B", "F"}, {"B", "W", "Z"}, {"T", "V", "B"}};
		forces = new double[] {1, 1, 1, 1, 1};

		for(String value[] : atoms)
			core.reinforce(value);

		answers = new TreeSet<Fact<String>>();
		answers.addAll(core.getAnswers(false, false, "B"));
		
		check(true, answers, atoms, forces);
	}

	@Test
	public void testTimeOrder() {
		
		// Check time order
		Gilgamesh<String> core = new Gilgamesh<String>();
		core.reinforce("cat", "eats", "ration", "tasty");
		core.reinforce("rat", "eats", "food", "tasty");
		core.reinforce("rat", "eats", "food", "bad");
		core.reinforce("dog", "eats", "food", "bad");
		core.reinforce("dog", "eats", "food", "tasty");

		String atoms[][] = new String[][] { { "dog", "eats", "food", "tasty" }, { "dog", "eats", "food", "bad" }, 
			{ "rat", "eats", "food", "bad" }, { "rat", "eats", "food", "tasty" }, { "cat", "eats", "ration", "tasty" } };

		double forces[] = { 1, 1, 1, 1, 1 };
		Set<Fact<String>> answers = core.getAnswers(true, false, "eats");
		check(true, answers, atoms, forces);

	}
	
	@Test
	@SuppressWarnings("rawtypes")
	public void testEstimated() {

		Gilgamesh<String> core = new Gilgamesh<String>();
		
		CustomAtom atoms1[] = {new CustomAtom<String>("A"), new CustomAtom<String>("B"), new CustomAtom<String>("C")};
		CustomAtom atoms2[] = {new CustomAtom<String>("B"), new CustomAtom<String>("C"), new CustomAtom<String>("A")};
		CustomAtom atoms3[] = {new CustomAtom<String>("B"), new CustomAtom<String>("C"), new CustomAtom<String>("B")};
		core.reinforce(atoms1[0], atoms1[1], atoms1[2]);
		core.reinforce(atoms2[0], atoms2[1], atoms2[2]);
		core.reinforce(atoms3[0], atoms3[1], atoms3[2]);

		Set<Fact<String>> answers = core.getAnswers(true, false, new CustomAtom<String>("B"));
		Iterator<Fact<String>> iterator = answers.iterator();
		
		Fact<String> answer = iterator.next();

		double forces[] = {2, 1, 1};
		
		
		for(int i=0; i<3; i++)
			assertEquals(answer.toAtoms()[i], atoms3[i]);
		assertEquals(answer.force, forces[0], PRECISION);
		
		answer = iterator.next();
		for(int i=0; i<3; i++)
			assertEquals(answer.toAtoms()[i], atoms2[i]);
		assertEquals(answer.force, forces[1], PRECISION);
		
		answer = iterator.next();
		for(int i=0; i<3; i++)
			assertEquals(answer.toAtoms()[i], atoms1[i]);
		assertEquals(answer.force, forces[2], PRECISION);
		
	}

	private void check(boolean check, Set<Fact<String>> answers, String atoms[][], double forces[]) {

		assertTrue(answers != null && answers.size() > 0);

		System.out.printf("Answers %d:\n", ++counter);

		Iterator<Fact<String>> iterator = answers.iterator();

		for (int i = 0; i < answers.size(); i++) {
			Fact<String> answer = iterator.next();
			System.out.println(answer.toString());

			for (int a = 0; a < answer.atoms.length; a++) {
				Atom<String> atom = answer.toAtoms()[a];
				if (check)
					assertTrue(atom.toString().equals(atoms[i][a]));
			}

			if (check)
				assertEquals(answer.force, forces[i], PRECISION);
		}
		System.out.println();
	}

	private class CustomAtom<T extends Serializable> extends Atom<T> {

		private static final long serialVersionUID = 1L;
		
		public T value;
		
		public CustomAtom(T value) {
			super(value);
			this.value = value;
		}
		
		@Override
		public double equality(Atom<T> another) {

			double h1 = value.hashCode();
			double h2 = ((CustomAtom<T>) another).value.hashCode(); 
			
			return Math.min(h1, h2) / Math.max(h1, h2);
		}
		
		@Override
		public int hashCode() {

			return value.hashCode();
		}

		@Override
		public boolean equals(Object obj) {

			if(!(obj instanceof Atom))
				return false;
			
			try {
				Field field = obj.getClass().getField("value");
				return field != null && field.get(obj).equals(value);
			}
			catch(Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		
		@Override
		public String toString() {

			return value.toString();
		}

	}
	
	
	@Test
	public void testForces() {
		
		Gilgamesh<String> gilgamesh = new Gilgamesh<String>();
		
		gilgamesh.reinforce("A", "B", "A", "C", "A");
		gilgamesh.reinforce("A", "B", "A", "C", "A");
		gilgamesh.reinforce("A", "B", "A", "C", "A");
		gilgamesh.reinforce("A", "B", "A", "C", "A");
		gilgamesh.reinforce("A", "B", "A", "C", "A");
		
		gilgamesh.reinforce("A", "D", "A", "E", "A");
		gilgamesh.reinforce("A", "D", "A", "E", "A");
		gilgamesh.reinforce("A", "D", "A", "E", "A");
		gilgamesh.reinforce("A", "D", "A", "E", "A");
		
		gilgamesh.reinforce("A", "C", "A", "C", "A");
		gilgamesh.reinforce("A", "C", "A", "C", "A");
		gilgamesh.reinforce("A", "C", "A", "C", "A");
		
		String atoms[][] = new String[][] { { "A", "B", "A", "C", "A" }, { "A", "C", "A", "C", "A" }, { "A", "D", "A", "E", "A" } };

		double forces[] = { 20, 15, 12 };
		Set<Fact<String>> answers = gilgamesh.getAnswers(true, false, "A", "C");
		check(true, answers, atoms, forces);
		
	}
	
	@Test
	public void testDeepAnswer() {

		Fact<String> facts[] = new Fact[3];

		Gilgamesh<String> gilgamesh = new Gilgamesh<String>();
		gilgamesh.reinforce("john", "kills", "terminators", "robots");
		gilgamesh.reinforce("terminators", "robots", "living", "things");
		gilgamesh.reinforce("beautiful", "dogs", "living", "things"); // inverted!!!
		facts[0] = gilgamesh.answerDeep(true, "john", "kills");
		System.out.println("Answer Deep: " + facts[0]);

		gilgamesh = new Gilgamesh<String>();
		gilgamesh.reinforce("dog", "eats");
		gilgamesh.reinforce("eats", "meat");
		gilgamesh.reinforce("meat", "food");
		gilgamesh.reinforce("food", "animal");
		facts[1] = gilgamesh.answerDeep(true, "dog");
		System.out.println("Answer Deep: " + facts[1]);
		

		gilgamesh = new Gilgamesh<String>();
		gilgamesh.reinforce("dog", "animal");
		gilgamesh.fact(5.0, "animal", "fear");
		gilgamesh.fact(6.0, "animal", "attack");
		gilgamesh.fact(4.0, "animal", "flee");
		facts[2] = gilgamesh.answerDeep(true, "dog");
		System.out.println("Answer Deep: " + facts[2]);

		assertEquals(new Fact<String>(2, "beautiful", "dogs"), facts[0]);
		assertEquals(new Fact<String>(1, "animal"), facts[1]);
		assertEquals(new Fact<String>(6, "attack"), facts[2]);
		
	}
	
	
	
}



























