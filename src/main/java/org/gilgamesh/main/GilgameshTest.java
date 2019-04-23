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


package org.gilgamesh.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

import org.gilgamesh.model.Answer;
import org.gilgamesh.model.core.Atom;
import org.gilgamesh.model.core.Core;
import org.gilgamesh.model.core.Fact;

public class GilgameshTest
{
	private static int counter;
	private static int THREADS = 1000;
	
	public static void main(String args[]) throws Exception
	{
		System.out.println("########################################################################################################################");
		System.out.println("Gilgamesh Core Test");
		System.out.println("########################################################################################################################");

		Core<String> core = new Core<String>();

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

		String atoms[][] = {{"cat", "eats", "ration", "tasty"}, {"dog", "eats", "food", "tasty"}, {"rat", "eats", "food", "tasty"}};
		double forces[] = {5, 2, 1};
		List<Answer<Fact<String>>> answers = core.getAnswers(false, false, "eats", "tasty");

		check(true, answers, atoms, forces);

		atoms = new String[][] {{"dog", "eats", "food", "tasty"}, {"cat", "eats", "ration", "tasty"}, {"dog", "eats", "food", "bad"},
				{"rat", "eats", "food", "tasty"}, {"dog"}, {"tasty"}};
		forces = new double[]{2.0, 1.25, 0.25, 0.25};
		answers = core.getAnswers(true, false, "tasty", "dog");
		check(true, answers, atoms, forces);

		atoms = new String[][]{{"rat", "bad"}, {"dog", "tasty"}, {"rat", "tasty"}, {"dog", "bad"}};
		forces = new double[]{3.0, 2.0, 1.0, 1.0};
		answers = core.getAnswers(false, true, "eats", "food");
		check(true, answers, atoms, forces);

		atoms = new String[][] {{"rat", "bad"}, {"cat", "ration", "tasty"}, {"dog", "tasty"}, {"rat", "tasty"}, {"dog", "bad"}};
		forces = new double[]{1.0, 0.83, 0.67, 0.33, 0.33};
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

		atoms = new String[][] {{"dog", "eats", "food", "tasty"}, {"dog", "eats", "food", "bad"},
				{"rat", "eats", "food", "tasty"}, {"cat", "eats", "ration", "tasty"}};
		forces = new double[]{1, 0.25, 0, -0.75};
		answers = core.getAnswers(true, false, Arrays.asList("tasty", "dog"));
		check(true, answers, atoms, forces);

		core = new Core<String>();
		atoms = new String[][] {{"P+", "V-", "U+", "FV+", "FU+"}, {"P+", "V-", "U+"}, {"P+", "V-", "U+", "FP-"}};
		forces = new double[]{0.11, 0, -0.11};
		core.fact(0.0, new String[] {"P+", "V-", "U+"});
		core.reinforce(new String[] {"P+", "V-", "U+", "FV+", "FU+"});
		core.punish(new String[] {"P+", "V-", "U+", "FP-"});
		answers = core.getAnswers(true, false, Arrays.asList("P-", "V+", "U+"));
		check(true, answers, atoms, forces);

		atoms = new String[][] {{"P-", "V-", "U-", "FP+", "FU+"}, {"P+", "V-", "U+", "FV+", "FU+"},	{"P-", "V-", "U-"}, {"P+", "V-", "U+"},
				{"P-", "V-", "U-", "FV-"}, {"P+", "V-", "U+", "FP-"}};
		forces = new double[]{0.33, 0.33, 0, 0, -0.33, -0.33};
		core.fact(0.0, new String[] {"P-", "V-", "U-"});
		core.reinforce(new String[] {"P-", "V-", "U-", "FP+", "FU+"});
		core.punish(new String[] {"P-", "V-", "U-", "FV-"});
		answers = core.getAnswers(true, false, Arrays.asList("P-", "V-", "U+"));
		check(true, answers, atoms, forces);

		atoms = new String[][] {{"P-", "V-", "U+", "FU+"}, {"P-", "V-", "U-", "FP+", "FU+"}, {"P+", "V-", "U+", "FV+", "FU+"},
				{"P-", "V-", "U+"},{"P-", "V-", "U-"}, {"P+", "V-", "U+"}, {"P-", "V-", "U-", "FV-"}, {"P+", "V-", "U+", "FP-"},
				{"P-", "V-", "U+", "FP-", "FV-"}};
		forces = new double[]{0.33, 0.11, 0.11, 0, 0, 0, -0.11, -0.11, -0.33};
		core.fact(0.0, new String[] {"P-", "V-", "U+"});
		core.reinforce(new String[] {"P-", "V-", "U+", "FU+"});
		core.punish(new String[] {"P-", "V-", "U+", "FP-", "FV-"});
		answers = core.getAnswers(true, false, Arrays.asList("P-", "V+", "U+"));
		check(true, answers, atoms, forces);

		atoms = new String[][] {{"P-", "V-", "U-", "FP+", "FU+"}, {"P-", "V-", "U+", "FU+"}, {"P+", "V-", "U+", "FV+", "FU+"},
				{"P-", "V-", "U+"}, {"P-", "V-", "U-"}, {"P+", "V-", "U+"}, {"P+", "V-", "U+", "FP-"}, {"P-", "V-", "U+", "FP-", "FV-"}, {"P-", "V-", "U-", "FV-"}};
		forces = new double[]{1.0, 0.33, 0.11, 0, 0, 0, -0.11, -0.33, -1.0};
		core.fact(0.0, new String[] {"P+", "V+", "U+"});
		core.reinforce(new String[] {"P+", "V+", "U+", "FP+"});
		core.punish(new String[] {"P+", "V+", "U+", "FV-", "FU-"});
		answers = core.getAnswers(true, false, Arrays.asList("P-", "V-", "U-"));
		check(true, answers, atoms, forces);

		core = new Core<String>();
		atoms = new String[][] {{"A", "B", "C"}, {"X", "Y", "B"}, {"H", "B", "F"}, {"B", "W", "Z"}, {"T", "V", "B"}};
		forces = new double[] {1, 1, 1, 1, 1};

		for(String value[] : atoms)
			core.reinforce(value);

		answers = core.getAnswers(false, false, "B");
		Collections.reverse(answers);

		check(true, answers, atoms, forces);

		threads1();
		threads2();
		threads3();

		System.out.println("\n\n\nTest ended successfully.");
		System.exit(0);
	}

	private static void check(boolean check, List<Answer<Fact<String>>> answers, String atoms[][], double forces[])
	{
		assert answers != null && answers.size() > 0 : "Invalid answers";

		System.out.printf("Answers %d:\n", ++counter);
		for(int i=0; i<answers.size(); i++)
		{
			Answer<Fact<String>> answer = answers.get(i);
			System.out.println(answer.toString());
			
			for(int a=0; a<answer.value.atoms.length; a++)
			{
				Atom<String> atom = answer.value.atoms[a];
				if(check)
					assert atom.toString().equals(atoms[i][a]);
			}
			
			if(check)
			{
				assert round(answer.getForce(), 2) == forces[i];
			}
		}
		System.out.println();
	}
	
	private static double round(double value, int precision)
	{
		value = value * Math.pow(10, precision);
		value = Math.round(value);
		return value / Math.pow(10, precision);
	}
	
	private static void threads1() throws InterruptedException
	{
		final Core<String> core = new Core<String>();
		HashMap<String, Integer> counter = new HashMap<String, Integer>();
		
		ExecutorService executor = Executors.newFixedThreadPool(THREADS);
		List<Callable<Object>> todo = new ArrayList<Callable<Object>>();

		for(int i=0; i<1000; i++)
		{
			final String string = "" + new Random().nextInt(50);
			
			if(!counter.keySet().contains(string))
				counter.put(string, 0);
			else
				counter.put(string, counter.get(string) + 1);
			
			todo.add(Executors.callable(new Runnable() {
				public void run()
				{
					try
					{
						int time = (int) (Math.random() * 1000);
						Thread.currentThread().setPriority(time % 2 == 0? Thread.MAX_PRIORITY : Thread.MIN_PRIORITY);
						Thread.sleep(time);
						core.reinforce(string);
					}
					catch(InterruptedException e)
					{
						System.err.print(e);
					}
				}
			}));
		}

		List<Future<Object>> answers = executor.invokeAll(todo);

		for(String key : counter.keySet())
		{
			double value = core.getAnswers(false, false, key).get(0).getForce();
			System.out.printf("[%s]:[%d] - Core: [%.0f]\n", key, counter.get(key), value-1);
			assert counter.get(key) == value -1 : "Counter: " + counter.get(key) + " / Core: " + value;
		}
	}
	
	private static void threads2() throws InterruptedException
	{
		System.out.println("\nTesting and matching forces - can take a while...");
		final Core<String> core = new Core<String>();
		final ArrayList<String[]> original = new ArrayList<String[]>();
		final ConcurrentHashMap<String, String[]> facts = new ConcurrentHashMap<String, String[]>();
		final ConcurrentHashMap<String, Double> forces = new ConcurrentHashMap<String, Double>();


		for(int i=0; i<1000; i++)
		{
			int size = (int) (100 * Math.random());
			ArrayList<String> fact = new ArrayList<String>();
			
			for(int x=0; x<size; x++)
				fact.add("" + (char) ((int) 100 * Math.random()));
			
			core.fact(Math.random(), fact.toArray(new String[0]));
			original.add(fact.toArray(new String[0]));
		}

		ExecutorService executor = Executors.newFixedThreadPool(THREADS);
		List<Callable<Object>> todo = new ArrayList<Callable<Object>>();

		for(int i=0; i<1000; i++)
			todo.add(Executors.callable(new Runnable() {
				public void run()
				{
					try
					{
						int time = (int) (Math.random() * 1000);
						Thread.currentThread().setPriority(time % 2 == 0? Thread.MAX_PRIORITY : Thread.MIN_PRIORITY);
						Thread.sleep(time);
						String fact[] = original.get((int) (original.size() * Math.random()));

						if(fact == null)
							return;

						String key = createKey(fact);
						String partial[] = createPartialFact(fact);

						Answer<Fact<String>> answer = core.getAnswers(true, false, partial).get(0);

						if(answer != null)
						{
							facts.put(key, partial);
							forces.put(key, answer.getForce());
						}
					}
					catch(InterruptedException e)
					{
						System.err.print(e);
					}
				}
			}));

		List<Future<Object>> answers = executor.invokeAll(todo);

		todo.clear();
		for(final String key : facts.keySet())
			todo.add(Executors.callable(new Runnable() {
				public void run()
				{
					try
					{
						int time = (int) (Math.random() * 1000);
						Thread.currentThread().setPriority(time % 2 == 0? Thread.MAX_PRIORITY : Thread.MIN_PRIORITY);
						Thread.sleep(time);
						String partial[] = facts.get(key);
						Answer<Fact<String>> answer = core.getAnswers(true, false, partial).get(0);

						if(answer != null)
							assert answer.getForce() == forces.get(key) : "Error on expected force";
					}
					catch(InterruptedException e)
					{
						System.err.print(e);
					}
				}
			}));

		answers = executor.invokeAll(todo);

	}

	private static String createKey(String fact[])
	{
		String key = "";
		for(int i=0; i<fact.length; i++)
			key += fact[i];
		return key;
	}

	private static String[] createPartialFact(String fact[])
	{
		ArrayList<String> partial = new ArrayList<String>();
		Random random = new Random();

		for(int i=0; i<fact.length; i++)
			if(random.nextInt() % 2 == 0)
				partial.add(fact[i]);

		return partial.toArray(new String[0]);
	}


	private static void threads3() throws InterruptedException
	{
		final Core<String> core1 = new Core<String>();
		final Core<String> core2 = new Core<String>();
		final ArrayList<String[]> facts = new ArrayList<String[]>();
		
		for(int i=0; i<1000; i++)
		{
			int size = new Random().nextInt(10);
			ArrayList<String> fact = new ArrayList<String>();
			
			for(int x=0; x<size; x++)
				fact.add(String.valueOf(new Random().nextInt(size)));
			
			facts.add(fact.toArray(new String[0]));
			core1.reinforce(fact.toArray(new String[0]));
		}


		ExecutorService executor = Executors.newFixedThreadPool(THREADS);
		List<Callable<Object>> todo = new ArrayList<Callable<Object>>();
		
		Collections.reverse(facts);
		
		for(final String[] fact : facts)
			todo.add(Executors.callable(new Runnable() {
				public void run()
				{
					try
					{
						int time = (int) (Math.random() * 1000);
						Thread.currentThread().setPriority(time % 2 == 0? Thread.MAX_PRIORITY : Thread.MIN_PRIORITY);
						Thread.sleep(time);
						core2.reinforce(fact);
					}
					catch(InterruptedException e)
					{
						System.err.print(e);
					}
				}
			}));

		List<Future<Object>> answers = executor.invokeAll(todo);
		
		for(String fact[] : facts)
		{
			List<Answer<Fact<String>>> list1 = core1.getAnswers(false, false, fact);
			List<Answer<Fact<String>>> list2 = core1.getAnswers(false, false, fact);
			
			assert(list1.size() == list2.size());
			
			for(int i=0; i<list1.size(); i++)
				assert(list1.get(i).equals(list2.get(i)));
		}
		
	}

	

}


































