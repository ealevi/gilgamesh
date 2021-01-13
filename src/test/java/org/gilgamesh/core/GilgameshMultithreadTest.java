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
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ Gilgamesh.class })
public class GilgameshMultithreadTest {

	private final int THREADS = 10;
	private final double PRECISION = 0.00001;
	private final Random random = new Random();
	private char array[] = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 
			'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'Y', 'X', 'W', 'Z'};

	@Test
	public void testThreads1() throws InterruptedException {

		final Gilgamesh<String> core = new Gilgamesh<String>();
		HashMap<String, Integer> counter = new HashMap<String, Integer>();

		ExecutorService executor = Executors.newFixedThreadPool(THREADS);
		List<Callable<Object>> todo = new ArrayList<Callable<Object>>();

		for (int i = 0; i < THREADS; i++) {
			final String string = "" + array[random.nextInt(array.length)];

			if (!counter.keySet().contains(string))
				counter.put(string, 0);
			else
				counter.put(string, counter.get(string) + 1);

			todo.add(Executors.callable(new Runnable() {

				public void run() {

					try {
						int time = (int) (Math.random() * 100) + 100;
						Thread.currentThread().setPriority(time % 2 == 0 ? Thread.MAX_PRIORITY : Thread.MIN_PRIORITY);
						Thread.sleep(time);
						core.reinforce(string);
					}
					catch (InterruptedException e) {
						fail();
					}
				}
			}));
		}

		executor.invokeAll(todo);

		for (String key : counter.keySet()) {
			double value = core.getAnswers(false, false, key).iterator().next().force;
			System.out.printf("[%s]:[%d] - Core: [%.0f]\n", key, counter.get(key), value - 1);
			assertTrue(counter.get(key) == value - 1);
		}
	}

	@Test
	public void testThreads2() throws InterruptedException {

		System.out.println("\nTesting and matching forces - can take a while...");
		final Gilgamesh<String> core = new Gilgamesh<String>();
		final ArrayList<String[]> original = new ArrayList<String[]>();
		final ConcurrentHashMap<String, String[]> facts = new ConcurrentHashMap<String, String[]>();
		final ConcurrentHashMap<String, Double> forces = new ConcurrentHashMap<String, Double>();

		for (int i = 0; i < THREADS; i++) {
			int size = (int) (100 * Math.random()) + 100;
			ArrayList<String> fact = new ArrayList<String>();

			for (int x = 0; x < size; x++)
				fact.add("" + (char) ((int) 10 * Math.random()));

			core.fact(Math.random(), fact.toArray(new String[0]));
			original.add(fact.toArray(new String[0]));
		}

		ExecutorService executor = Executors.newFixedThreadPool(THREADS);
		List<Callable<Object>> todo = new ArrayList<Callable<Object>>();

		for (int i = 0; i < THREADS; i++)
			todo.add(Executors.callable(new Runnable() {

				public void run() {

					try {
						int time = (int) (Math.random() * 100) + 100;
						Thread.currentThread().setPriority(time % 2 == 0 ? Thread.MAX_PRIORITY : Thread.MIN_PRIORITY);
						Thread.sleep(time);
						String fact[] = original.get((int) (original.size() * Math.random()));

						if (fact == null)
							return;

						String key = createKey(fact);
						String partial[] = createPartialFact(fact);

						Fact<String> answer = core.getAnswers(true, false, partial).iterator().next();

						if (answer != null) {
							facts.put(key, partial);
							forces.put(key, answer.force);
						}
					}
					catch (InterruptedException e) {
						fail();
					}
				}
			}));

		executor.invokeAll(todo);

		todo.clear();
		for (final String key : facts.keySet())
			todo.add(Executors.callable(new Runnable() {

				public void run() {

					try {
						int time = (int) (Math.random() * 100) + 100;
						Thread.currentThread().setPriority(time % 2 == 0 ? Thread.MAX_PRIORITY : Thread.MIN_PRIORITY);
						Thread.sleep(time);
						String partial[] = facts.get(key);
						Fact<String> answer = core.getAnswers(true, false, partial).iterator().next();

						if (answer != null)
							assertEquals(answer.force, forces.get(key), PRECISION);
					}
					catch (InterruptedException e) {
						fail();
					}
				}
			}));

		executor.invokeAll(todo);

	}

	private String createKey(String fact[]) {

		String key = "";
		for (int i = 0; i < fact.length; i++)
			key += fact[i];
		return key;
	}

	private String[] createPartialFact(String fact[]) {

		ArrayList<String> partial = new ArrayList<String>();

		for (int i = 0; i < fact.length; i++)
			if (random.nextInt() % 2 == 0)
				partial.add(fact[i]);

		return partial.toArray(new String[0]);
	}

	@Test
	public void testThreads3() throws InterruptedException {

		final Gilgamesh<String> core1 = new Gilgamesh<String>();
		final Gilgamesh<String> core2 = new Gilgamesh<String>();
		final ArrayList<String[]> facts = new ArrayList<String[]>();

		for (int i = 0; i < THREADS; i++) {
			int size = random.nextInt(10) + 10;
			ArrayList<String> fact = new ArrayList<String>();

			for (int x = 0; x < size; x++)
				fact.add(String.valueOf(array[random.nextInt(array.length)]));

			facts.add(fact.toArray(new String[0]));
			core1.reinforce(fact.toArray(new String[0]));
		}

		ExecutorService executor = Executors.newFixedThreadPool(THREADS);
		List<Callable<Object>> todo = new ArrayList<Callable<Object>>();

		Collections.reverse(facts);

		for (final String[] fact : facts)
			todo.add(Executors.callable(new Runnable() {

				public void run() {

					try {
						int time = (int) (Math.random() * 100) + 100;
						Thread.currentThread().setPriority(time % 2 == 0 ? Thread.MAX_PRIORITY : Thread.MIN_PRIORITY);
						Thread.sleep(time);
						core2.reinforce(fact);
					}
					catch (InterruptedException e) {
						fail();
					}
				}
			}));

		executor.invokeAll(todo);

		for (String fact[] : facts) {
			Set<Fact<String>> list1 = core1.getAnswers(false, false, fact);
			Set<Fact<String>> list2 = core1.getAnswers(false, false, fact);

			assertTrue(list1.size() == list2.size());

			Iterator<Fact<String>> i1 = list1.iterator();
			Iterator<Fact<String>> i2 = list2.iterator();
			
			while(i1.hasNext())
				assertEquals(i1.next(), i2.next());
		}

	}

}
